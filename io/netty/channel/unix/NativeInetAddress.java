/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class NativeInetAddress {
    private static final byte[] IPV4_MAPPED_IPV6_PREFIX = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1};
    final byte[] address;
    final int scopeId;

    public static NativeInetAddress newInstance(InetAddress addr) {
        byte[] bytes = addr.getAddress();
        if (!(addr instanceof Inet6Address)) return new NativeInetAddress((byte[])NativeInetAddress.ipv4MappedIpv6Address((byte[])bytes));
        return new NativeInetAddress((byte[])bytes, (int)((Inet6Address)addr).getScopeId());
    }

    public NativeInetAddress(byte[] address, int scopeId) {
        this.address = address;
        this.scopeId = scopeId;
    }

    public NativeInetAddress(byte[] address) {
        this((byte[])address, (int)0);
    }

    public byte[] address() {
        return this.address;
    }

    public int scopeId() {
        return this.scopeId;
    }

    public static byte[] ipv4MappedIpv6Address(byte[] ipv4) {
        byte[] address = new byte[16];
        NativeInetAddress.copyIpv4MappedIpv6Address((byte[])ipv4, (byte[])address);
        return address;
    }

    public static void copyIpv4MappedIpv6Address(byte[] ipv4, byte[] ipv6) {
        System.arraycopy((Object)IPV4_MAPPED_IPV6_PREFIX, (int)0, (Object)ipv6, (int)0, (int)IPV4_MAPPED_IPV6_PREFIX.length);
        System.arraycopy((Object)ipv4, (int)0, (Object)ipv6, (int)12, (int)ipv4.length);
    }

    public static InetSocketAddress address(byte[] addr, int offset, int len) {
        int port = NativeInetAddress.decodeInt((byte[])addr, (int)(offset + len - 4));
        try {
            switch (len) {
                case 8: {
                    byte[] ipv4 = new byte[4];
                    System.arraycopy((Object)addr, (int)offset, (Object)ipv4, (int)0, (int)4);
                    InetAddress address = InetAddress.getByAddress((byte[])ipv4);
                    return new InetSocketAddress((InetAddress)address, (int)port);
                }
                case 24: {
                    byte[] ipv6 = new byte[16];
                    System.arraycopy((Object)addr, (int)offset, (Object)ipv6, (int)0, (int)16);
                    int scopeId = NativeInetAddress.decodeInt((byte[])addr, (int)(offset + len - 8));
                    InetAddress address = Inet6Address.getByAddress(null, (byte[])ipv6, (int)scopeId);
                    return new InetSocketAddress((InetAddress)address, (int)port);
                }
            }
            throw new Error();
        }
        catch (UnknownHostException e) {
            throw new Error((String)"Should never happen", (Throwable)e);
        }
    }

    static int decodeInt(byte[] addr, int index) {
        return (addr[index] & 255) << 24 | (addr[index + 1] & 255) << 16 | (addr[index + 2] & 255) << 8 | addr[index + 3] & 255;
    }
}

