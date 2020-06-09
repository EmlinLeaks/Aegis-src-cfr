/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class DatagramSocketAddress
extends InetSocketAddress {
    private static final long serialVersionUID = 3094819287843178401L;
    private final int receivedAmount;
    private final DatagramSocketAddress localAddress;

    DatagramSocketAddress(byte[] addr, int scopeId, int port, int receivedAmount, DatagramSocketAddress local) throws UnknownHostException {
        super((InetAddress)DatagramSocketAddress.newAddress((byte[])addr, (int)scopeId), (int)port);
        this.receivedAmount = receivedAmount;
        this.localAddress = local;
    }

    public DatagramSocketAddress localAddress() {
        return this.localAddress;
    }

    public int receivedAmount() {
        return this.receivedAmount;
    }

    private static InetAddress newAddress(byte[] bytes, int scopeId) throws UnknownHostException {
        if (bytes.length != 4) return Inet6Address.getByAddress(null, (byte[])bytes, (int)scopeId);
        return InetAddress.getByAddress((byte[])bytes);
    }
}

