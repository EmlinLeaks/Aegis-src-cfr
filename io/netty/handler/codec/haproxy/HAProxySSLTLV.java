/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.haproxy.HAProxyTLV;
import java.util.Collections;
import java.util.List;

public final class HAProxySSLTLV
extends HAProxyTLV {
    private final int verify;
    private final List<HAProxyTLV> tlvs;
    private final byte clientBitField;

    HAProxySSLTLV(int verify, byte clientBitField, List<HAProxyTLV> tlvs, ByteBuf rawContent) {
        super((HAProxyTLV.Type)HAProxyTLV.Type.PP2_TYPE_SSL, (byte)32, (ByteBuf)rawContent);
        this.verify = verify;
        this.tlvs = Collections.unmodifiableList(tlvs);
        this.clientBitField = clientBitField;
    }

    public boolean isPP2ClientCertConn() {
        if ((this.clientBitField & 2) == 0) return false;
        return true;
    }

    public boolean isPP2ClientSSL() {
        if ((this.clientBitField & 1) == 0) return false;
        return true;
    }

    public boolean isPP2ClientCertSess() {
        if ((this.clientBitField & 4) == 0) return false;
        return true;
    }

    public int verify() {
        return this.verify;
    }

    public List<HAProxyTLV> encapsulatedTLVs() {
        return this.tlvs;
    }
}

