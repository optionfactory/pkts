/**
 * 
 */
package com.aboutsip.yajpcap.packet.sip.header.impl;

import com.aboutsip.buffer.Buffer;
import com.aboutsip.buffer.Buffers;
import com.aboutsip.yajpcap.packet.sip.SipParseException;
import com.aboutsip.yajpcap.packet.sip.header.SipHeader;
import com.aboutsip.yajpcap.packet.sip.impl.SipParser;

/**
 * @author jonas@jonasborjesson.com
 */
public class SipHeaderImpl implements SipHeader {

    private final Buffer name;

    private final Buffer value;

    /**
     * 
     */
    public SipHeaderImpl(final Buffer name, final Buffer value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Subclasses may override this one and are in fact encourage to do so
     * 
     * {@inheritDoc}
     */
    @Override
    public Buffer getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getName().toString() + ": " + getValue();
    }

    @Override
    public void verify() throws SipParseException {
        // by default, everything is assumed to be correct.
        // Subclasses should override this method and
        // check that everything is ok...

    }

    @Override
    public void getBytes(final Buffer dst) {
        this.name.getBytes(0, dst);
        dst.write(SipParser.COLON);
        dst.write(SipParser.SP);
        transferValue(dst);
    }

    /**
     * Transfer the bytes of the value into the destination. Sub-classes should
     * override this method.
     * 
     * @param dst
     */
    protected void transferValue(final Buffer dst) {
        final Buffer value = getValue();
        value.getBytes(0, dst);
    }

    @Override
    public SipHeader clone() {
        final Buffer buffer = Buffers.createBuffer(1024);
        transferValue(buffer);
        return new SipHeaderImpl(this.name.clone(), buffer);
    }

}
