/**
 *
 */
package io.pkts.packet;

/**
 * @author jonas@jonasborjesson.com
 */
public interface PCapPacket extends Packet {

    public static final int LINKTYPE_ETHERNET = 1;
    public static final int LINKTYPE_LINUX_SLL = 113;
    public static final int LINKTYPE_IPV4 = 228;

    /**
     * Get the total length of the data. Not all of that data may have been
     * captured in this one frame, which is evident if the actual captured
     * length is different from the total length
     *
     * @return
     */
    long getTotalLength();

    /**
     * Get the actual length of what is contained in this frame. Note, if the
     * captured length is different from the total length then we have a
     * fragmented packet
     *
     * @return the length in bytes
     */
    long getCapturedLength();

    @Override
    PCapPacket clone();

}
