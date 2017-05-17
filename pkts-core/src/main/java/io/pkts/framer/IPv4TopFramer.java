/**
 *
 */
package io.pkts.framer;

import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.IPPacket;
import io.pkts.packet.PCapPacket;
import io.pkts.packet.impl.MACPacketImpl;
import io.pkts.protocol.Protocol;

import java.io.IOException;

/**
 * @author jonas@jonasborjesson.com
 *
 */
public class IPv4TopFramer implements Framer<PCapPacket> {

    public IPv4TopFramer() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Protocol getProtocol() {
        return Protocol.IPv4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPPacket frame(final PCapPacket parent, final Buffer payload) throws IOException {

        if (parent == null) {
            throw new IllegalArgumentException("The parent frame cannot be null");
        }

        final Buffer headers = Buffers.createBuffer(0);

        final MACPacketImpl macPacket = new MACPacketImpl(Protocol.ETHERNET_II, parent, headers, payload); // TODO: wtf?
        return macPacket.getNextPacket();
    }

    @Override
    public boolean accept(final Buffer buffer) throws IOException {
        buffer.markReaderIndex();
        try {
            final Buffer headers = buffer.readBytes(20);
            final byte b = headers.getByte(0);
            final int unsigned = Byte.toUnsignedInt(b);
            final int version = unsigned >> 4;
            //final int version = b >>> 5 & 0x0F;
            return version == 4;
        } catch (final IndexOutOfBoundsException e) {
            return false;
        } finally {
            buffer.resetReaderIndex();
        }
    }

}
