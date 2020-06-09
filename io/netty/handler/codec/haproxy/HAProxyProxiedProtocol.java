/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.haproxy;

import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol;

public enum HAProxyProxiedProtocol {
    UNKNOWN((byte)0, (AddressFamily)AddressFamily.AF_UNSPEC, (TransportProtocol)TransportProtocol.UNSPEC),
    TCP4((byte)17, (AddressFamily)AddressFamily.AF_IPv4, (TransportProtocol)TransportProtocol.STREAM),
    TCP6((byte)33, (AddressFamily)AddressFamily.AF_IPv6, (TransportProtocol)TransportProtocol.STREAM),
    UDP4((byte)18, (AddressFamily)AddressFamily.AF_IPv4, (TransportProtocol)TransportProtocol.DGRAM),
    UDP6((byte)34, (AddressFamily)AddressFamily.AF_IPv6, (TransportProtocol)TransportProtocol.DGRAM),
    UNIX_STREAM((byte)49, (AddressFamily)AddressFamily.AF_UNIX, (TransportProtocol)TransportProtocol.STREAM),
    UNIX_DGRAM((byte)50, (AddressFamily)AddressFamily.AF_UNIX, (TransportProtocol)TransportProtocol.DGRAM);
    
    private final byte byteValue;
    private final AddressFamily addressFamily;
    private final TransportProtocol transportProtocol;

    private HAProxyProxiedProtocol(byte byteValue, AddressFamily addressFamily, TransportProtocol transportProtocol) {
        this.byteValue = byteValue;
        this.addressFamily = addressFamily;
        this.transportProtocol = transportProtocol;
    }

    public static HAProxyProxiedProtocol valueOf(byte tpafByte) {
        switch (tpafByte) {
            case 17: {
                return TCP4;
            }
            case 33: {
                return TCP6;
            }
            case 0: {
                return UNKNOWN;
            }
            case 18: {
                return UDP4;
            }
            case 34: {
                return UDP6;
            }
            case 49: {
                return UNIX_STREAM;
            }
            case 50: {
                return UNIX_DGRAM;
            }
        }
        throw new IllegalArgumentException((String)("unknown transport protocol + address family: " + (tpafByte & 255)));
    }

    public byte byteValue() {
        return this.byteValue;
    }

    public AddressFamily addressFamily() {
        return this.addressFamily;
    }

    public TransportProtocol transportProtocol() {
        return this.transportProtocol;
    }
}

