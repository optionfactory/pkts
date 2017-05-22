/**
 *
 */
package io.pkts.packet.impl;

import io.pkts.buffer.Buffer;
import io.pkts.frame.PcapRecordHeader;
import io.pkts.framer.EthernetFramer;
import io.pkts.framer.IPv4Framer;
import io.pkts.framer.SllFramer;
import io.pkts.packet.MACPacket;
import io.pkts.packet.PCapPacket;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: may rename this to a frame instead since this is a little different
 * than a "real" protocol packet.
 *
 * @author jonas@jonasborjesson.com
 */
public final class PCapPacketImpl extends AbstractPacket implements PCapPacket {

    private final int dataLinkType;
    private final PcapRecordHeader pcapHeader;

    private static final SllFramer sllFramer = new SllFramer();
    private static final EthernetFramer ethernetFramer = new EthernetFramer();
    private static final IPv4Framer ipv4Framer = new IPv4Framer();

    /**
     *
     */
    public PCapPacketImpl(final int dataLinkType, final PcapRecordHeader header, final Buffer payload) {
        super(Protocol.PCAP, null, payload);
        this.dataLinkType = dataLinkType;
        this.pcapHeader = header;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getArrivalTime() {
        return this.pcapHeader.getTimeStampSeconds() * 1000000 + this.pcapHeader.getTimeStampMicroSeconds();
    }

    @Override
    public long getTotalLength() {
        return this.pcapHeader.getTotalLength();
    }

    @Override
    public long getCapturedLength() {
        return this.pcapHeader.getCapturedLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify() {
        // nothing to verify for the pcap packet since that would
        // have been detected when we framed the pcap packet.
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
        final Date date = new Date(getArrivalTime() / 1000);
        sb.append("Arrival Time: ").append(formatter.format(date))
                .append(" Epoch Time: ").append(this.pcapHeader.getTimeStampSeconds()).append(".")
                .append(this.pcapHeader.getTimeStampMicroSeconds())
                .append(" Frame Length: ").append(getTotalLength())
                .append(" Capture Length: ").append(getCapturedLength());

        return sb.toString();
    }

    @Override
    public void write(final OutputStream out, final Buffer payload) throws IOException {
        final int size = payload.getReadableBytes();
        this.pcapHeader.setCapturedLength(size);
        this.pcapHeader.setTotalLength(size);
        this.pcapHeader.write(out);
        out.write(payload.getArray());
    }

    @Override
    public PCapPacket clone() {
        throw new RuntimeException("not implemente yet");
    }

    @Override
    public MACPacket getNextPacket() throws IOException {
        final Buffer payload = getPayload();
        if (payload == null) {
            return null;
        }
        switch (dataLinkType) {
            case LINKTYPE_ETHERNET:
                return ethernetFramer.frame(this, payload);
            case LINKTYPE_LINUX_SLL:
                return sllFramer.frame(this, payload);
            case LINKTYPE_IPV4:
            case LINKTYPE_RAW_IP:
                return ipv4Framer.frame(this, payload);
            default:
                throw new RuntimeException(String.format("Unsupported data link type %d", dataLinkType));
        }
    }
}
