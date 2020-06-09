/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.Ints;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class InetAddresses {
    private static final int IPV4_PART_COUNT = 4;
    private static final int IPV6_PART_COUNT = 8;
    private static final Splitter IPV4_SPLITTER = Splitter.on((char)'.').limit((int)4);
    private static final Inet4Address LOOPBACK4 = (Inet4Address)InetAddresses.forString((String)"127.0.0.1");
    private static final Inet4Address ANY4 = (Inet4Address)InetAddresses.forString((String)"0.0.0.0");

    private InetAddresses() {
    }

    private static Inet4Address getInet4Address(byte[] bytes) {
        Preconditions.checkArgument((boolean)(bytes.length == 4), (String)"Byte array has invalid length for an IPv4 address: %s != 4.", (int)bytes.length);
        return (Inet4Address)InetAddresses.bytesToInetAddress((byte[])bytes);
    }

    public static InetAddress forString(String ipString) {
        byte[] addr = InetAddresses.ipStringToBytes((String)ipString);
        if (addr != null) return InetAddresses.bytesToInetAddress((byte[])addr);
        throw InetAddresses.formatIllegalArgumentException((String)"'%s' is not an IP string literal.", (Object[])new Object[]{ipString});
    }

    public static boolean isInetAddress(String ipString) {
        if (InetAddresses.ipStringToBytes((String)ipString) == null) return false;
        return true;
    }

    @Nullable
    private static byte[] ipStringToBytes(String ipString) {
        boolean hasColon = false;
        boolean hasDot = false;
        for (int i = 0; i < ipString.length(); ++i) {
            char c = ipString.charAt((int)i);
            if (c == '.') {
                hasDot = true;
                continue;
            }
            if (c == ':') {
                if (hasDot) {
                    return null;
                }
                hasColon = true;
                continue;
            }
            if (Character.digit((char)c, (int)16) != -1) continue;
            return null;
        }
        if (hasColon) {
            if (!hasDot) return InetAddresses.textToNumericFormatV6((String)ipString);
            if ((ipString = InetAddresses.convertDottedQuadToHex((String)ipString)) != null) return InetAddresses.textToNumericFormatV6((String)ipString);
            return null;
        }
        if (!hasDot) return null;
        return InetAddresses.textToNumericFormatV4((String)ipString);
    }

    @Nullable
    private static byte[] textToNumericFormatV4(String ipString) {
        byte[] bytes = new byte[4];
        int i = 0;
        try {
            for (String octet : IPV4_SPLITTER.split((CharSequence)ipString)) {
                bytes[i++] = InetAddresses.parseOctet((String)octet);
            }
        }
        catch (NumberFormatException ex) {
            return null;
        }
        if (i != 4) return null;
        byte[] arrby = bytes;
        return arrby;
    }

    @Nullable
    private static byte[] textToNumericFormatV6(String ipString) {
        int partsLo;
        int partsHi;
        String[] parts = ipString.split((String)":", (int)10);
        if (parts.length < 3) return null;
        if (parts.length > 9) {
            return null;
        }
        int skipIndex = -1;
        for (int i = 1; i < parts.length - 1; ++i) {
            if (parts[i].length() != 0) continue;
            if (skipIndex >= 0) {
                return null;
            }
            skipIndex = i;
        }
        if (skipIndex >= 0) {
            partsHi = skipIndex;
            partsLo = parts.length - skipIndex - 1;
            if (parts[0].length() == 0 && --partsHi != 0) {
                return null;
            }
            if (parts[parts.length - 1].length() == 0 && --partsLo != 0) {
                return null;
            }
        } else {
            partsHi = parts.length;
            partsLo = 0;
        }
        int partsSkipped = 8 - (partsHi + partsLo);
        if (skipIndex >= 0) {
            if (partsSkipped < 1) return null;
        } else if (partsSkipped != 0) {
            return null;
        }
        ByteBuffer rawBytes = ByteBuffer.allocate((int)16);
        try {
            int i;
            for (i = 0; i < partsHi; ++i) {
                rawBytes.putShort((short)InetAddresses.parseHextet((String)parts[i]));
            }
            for (i = 0; i < partsSkipped; ++i) {
                rawBytes.putShort((short)0);
            }
            i = partsLo;
            while (i > 0) {
                rawBytes.putShort((short)InetAddresses.parseHextet((String)parts[parts.length - i]));
                --i;
            }
            return rawBytes.array();
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

    @Nullable
    private static String convertDottedQuadToHex(String ipString) {
        int lastColon = ipString.lastIndexOf((int)58);
        String initialPart = ipString.substring((int)0, (int)(lastColon + 1));
        String dottedQuad = ipString.substring((int)(lastColon + 1));
        byte[] quad = InetAddresses.textToNumericFormatV4((String)dottedQuad);
        if (quad == null) {
            return null;
        }
        String penultimate = Integer.toHexString((int)((quad[0] & 255) << 8 | quad[1] & 255));
        String ultimate = Integer.toHexString((int)((quad[2] & 255) << 8 | quad[3] & 255));
        return initialPart + penultimate + ":" + ultimate;
    }

    private static byte parseOctet(String ipPart) {
        int octet = Integer.parseInt((String)ipPart);
        if (octet > 255) throw new NumberFormatException();
        if (!ipPart.startsWith((String)"0")) return (byte)octet;
        if (ipPart.length() <= 1) return (byte)octet;
        throw new NumberFormatException();
    }

    private static short parseHextet(String ipPart) {
        int hextet = Integer.parseInt((String)ipPart, (int)16);
        if (hextet <= 65535) return (short)hextet;
        throw new NumberFormatException();
    }

    private static InetAddress bytesToInetAddress(byte[] addr) {
        try {
            return InetAddress.getByAddress((byte[])addr);
        }
        catch (UnknownHostException e) {
            throw new AssertionError((Object)e);
        }
    }

    public static String toAddrString(InetAddress ip) {
        Preconditions.checkNotNull(ip);
        if (ip instanceof Inet4Address) {
            return ip.getHostAddress();
        }
        Preconditions.checkArgument((boolean)(ip instanceof Inet6Address));
        byte[] bytes = ip.getAddress();
        int[] hextets = new int[8];
        int i = 0;
        do {
            if (i >= hextets.length) {
                InetAddresses.compressLongestRunOfZeroes((int[])hextets);
                return InetAddresses.hextetsToIPv6String((int[])hextets);
            }
            hextets[i] = Ints.fromBytes((byte)0, (byte)0, (byte)bytes[2 * i], (byte)bytes[2 * i + 1]);
            ++i;
        } while (true);
    }

    private static void compressLongestRunOfZeroes(int[] hextets) {
        int bestRunStart = -1;
        int bestRunLength = -1;
        int runStart = -1;
        int i = 0;
        do {
            if (i >= hextets.length + 1) {
                if (bestRunLength < 2) return;
                Arrays.fill((int[])hextets, (int)bestRunStart, (int)(bestRunStart + bestRunLength), (int)-1);
                return;
            }
            if (i < hextets.length && hextets[i] == 0) {
                if (runStart < 0) {
                    runStart = i;
                }
            } else if (runStart >= 0) {
                int runLength = i - runStart;
                if (runLength > bestRunLength) {
                    bestRunStart = runStart;
                    bestRunLength = runLength;
                }
                runStart = -1;
            }
            ++i;
        } while (true);
    }

    private static String hextetsToIPv6String(int[] hextets) {
        StringBuilder buf = new StringBuilder((int)39);
        boolean lastWasNumber = false;
        int i = 0;
        while (i < hextets.length) {
            boolean thisIsNumber;
            boolean bl = thisIsNumber = hextets[i] >= 0;
            if (thisIsNumber) {
                if (lastWasNumber) {
                    buf.append((char)':');
                }
                buf.append((String)Integer.toHexString((int)hextets[i]));
            } else if (i == 0 || lastWasNumber) {
                buf.append((String)"::");
            }
            lastWasNumber = thisIsNumber;
            ++i;
        }
        return buf.toString();
    }

    public static String toUriString(InetAddress ip) {
        if (!(ip instanceof Inet6Address)) return InetAddresses.toAddrString((InetAddress)ip);
        return "[" + InetAddresses.toAddrString((InetAddress)ip) + "]";
    }

    public static InetAddress forUriString(String hostAddr) {
        InetAddress addr = InetAddresses.forUriStringNoThrow((String)hostAddr);
        if (addr != null) return addr;
        throw InetAddresses.formatIllegalArgumentException((String)"Not a valid URI IP literal: '%s'", (Object[])new Object[]{hostAddr});
    }

    @Nullable
    private static InetAddress forUriStringNoThrow(String hostAddr) {
        String ipString;
        int expectBytes;
        Preconditions.checkNotNull(hostAddr);
        if (hostAddr.startsWith((String)"[") && hostAddr.endsWith((String)"]")) {
            ipString = hostAddr.substring((int)1, (int)(hostAddr.length() - 1));
            expectBytes = 16;
        } else {
            ipString = hostAddr;
            expectBytes = 4;
        }
        byte[] addr = InetAddresses.ipStringToBytes((String)ipString);
        if (addr == null) return null;
        if (addr.length == expectBytes) return InetAddresses.bytesToInetAddress((byte[])addr);
        return null;
    }

    public static boolean isUriInetAddress(String ipString) {
        if (InetAddresses.forUriStringNoThrow((String)ipString) == null) return false;
        return true;
    }

    public static boolean isCompatIPv4Address(Inet6Address ip) {
        if (!ip.isIPv4CompatibleAddress()) {
            return false;
        }
        byte[] bytes = ip.getAddress();
        if (bytes[12] != 0) return true;
        if (bytes[13] != 0) return true;
        if (bytes[14] != 0) return true;
        if (bytes[15] == 0) return false;
        if (bytes[15] != 1) return true;
        return false;
    }

    public static Inet4Address getCompatIPv4Address(Inet6Address ip) {
        Preconditions.checkArgument((boolean)InetAddresses.isCompatIPv4Address((Inet6Address)ip), (String)"Address '%s' is not IPv4-compatible.", (Object)InetAddresses.toAddrString((InetAddress)ip));
        return InetAddresses.getInet4Address((byte[])Arrays.copyOfRange((byte[])ip.getAddress(), (int)12, (int)16));
    }

    public static boolean is6to4Address(Inet6Address ip) {
        byte[] bytes = ip.getAddress();
        if (bytes[0] != 32) return false;
        if (bytes[1] != 2) return false;
        return true;
    }

    public static Inet4Address get6to4IPv4Address(Inet6Address ip) {
        Preconditions.checkArgument((boolean)InetAddresses.is6to4Address((Inet6Address)ip), (String)"Address '%s' is not a 6to4 address.", (Object)InetAddresses.toAddrString((InetAddress)ip));
        return InetAddresses.getInet4Address((byte[])Arrays.copyOfRange((byte[])ip.getAddress(), (int)2, (int)6));
    }

    public static boolean isTeredoAddress(Inet6Address ip) {
        byte[] bytes = ip.getAddress();
        if (bytes[0] != 32) return false;
        if (bytes[1] != 1) return false;
        if (bytes[2] != 0) return false;
        if (bytes[3] != 0) return false;
        return true;
    }

    public static TeredoInfo getTeredoInfo(Inet6Address ip) {
        Preconditions.checkArgument((boolean)InetAddresses.isTeredoAddress((Inet6Address)ip), (String)"Address '%s' is not a Teredo address.", (Object)InetAddresses.toAddrString((InetAddress)ip));
        byte[] bytes = ip.getAddress();
        Inet4Address server = InetAddresses.getInet4Address((byte[])Arrays.copyOfRange((byte[])bytes, (int)4, (int)8));
        int flags = ByteStreams.newDataInput((byte[])bytes, (int)8).readShort() & 65535;
        int port = ~ByteStreams.newDataInput((byte[])bytes, (int)10).readShort() & 65535;
        byte[] clientBytes = Arrays.copyOfRange((byte[])bytes, (int)12, (int)16);
        int i = 0;
        do {
            if (i >= clientBytes.length) {
                Inet4Address client = InetAddresses.getInet4Address((byte[])clientBytes);
                return new TeredoInfo((Inet4Address)server, (Inet4Address)client, (int)port, (int)flags);
            }
            clientBytes[i] = (byte)(~clientBytes[i]);
            ++i;
        } while (true);
    }

    public static boolean isIsatapAddress(Inet6Address ip) {
        if (InetAddresses.isTeredoAddress((Inet6Address)ip)) {
            return false;
        }
        byte[] bytes = ip.getAddress();
        if ((bytes[8] | 3) != 3) {
            return false;
        }
        if (bytes[9] != 0) return false;
        if (bytes[10] != 94) return false;
        if (bytes[11] != -2) return false;
        return true;
    }

    public static Inet4Address getIsatapIPv4Address(Inet6Address ip) {
        Preconditions.checkArgument((boolean)InetAddresses.isIsatapAddress((Inet6Address)ip), (String)"Address '%s' is not an ISATAP address.", (Object)InetAddresses.toAddrString((InetAddress)ip));
        return InetAddresses.getInet4Address((byte[])Arrays.copyOfRange((byte[])ip.getAddress(), (int)12, (int)16));
    }

    public static boolean hasEmbeddedIPv4ClientAddress(Inet6Address ip) {
        if (InetAddresses.isCompatIPv4Address((Inet6Address)ip)) return true;
        if (InetAddresses.is6to4Address((Inet6Address)ip)) return true;
        if (InetAddresses.isTeredoAddress((Inet6Address)ip)) return true;
        return false;
    }

    public static Inet4Address getEmbeddedIPv4ClientAddress(Inet6Address ip) {
        if (InetAddresses.isCompatIPv4Address((Inet6Address)ip)) {
            return InetAddresses.getCompatIPv4Address((Inet6Address)ip);
        }
        if (InetAddresses.is6to4Address((Inet6Address)ip)) {
            return InetAddresses.get6to4IPv4Address((Inet6Address)ip);
        }
        if (!InetAddresses.isTeredoAddress((Inet6Address)ip)) throw InetAddresses.formatIllegalArgumentException((String)"'%s' has no embedded IPv4 address.", (Object[])new Object[]{InetAddresses.toAddrString((InetAddress)ip)});
        return InetAddresses.getTeredoInfo((Inet6Address)ip).getClient();
    }

    public static boolean isMappedIPv4Address(String ipString) {
        int i;
        byte[] bytes = InetAddresses.ipStringToBytes((String)ipString);
        if (bytes == null) return false;
        if (bytes.length != 16) return false;
        for (i = 0; i < 10; ++i) {
            if (bytes[i] == 0) continue;
            return false;
        }
        i = 10;
        while (i < 12) {
            if (bytes[i] != -1) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static Inet4Address getCoercedIPv4Address(InetAddress ip) {
        if (ip instanceof Inet4Address) {
            return (Inet4Address)ip;
        }
        byte[] bytes = ip.getAddress();
        boolean leadingBytesOfZero = true;
        for (int i = 0; i < 15; ++i) {
            if (bytes[i] == 0) continue;
            leadingBytesOfZero = false;
            break;
        }
        if (leadingBytesOfZero && bytes[15] == 1) {
            return LOOPBACK4;
        }
        if (leadingBytesOfZero && bytes[15] == 0) {
            return ANY4;
        }
        Inet6Address ip6 = (Inet6Address)ip;
        long addressAsLong = 0L;
        addressAsLong = InetAddresses.hasEmbeddedIPv4ClientAddress((Inet6Address)ip6) ? (long)InetAddresses.getEmbeddedIPv4ClientAddress((Inet6Address)ip6).hashCode() : ByteBuffer.wrap((byte[])ip6.getAddress(), (int)0, (int)8).getLong();
        int coercedHash = Hashing.murmur3_32().hashLong((long)addressAsLong).asInt();
        if ((coercedHash |= -536870912) != -1) return InetAddresses.getInet4Address((byte[])Ints.toByteArray((int)coercedHash));
        coercedHash = -2;
        return InetAddresses.getInet4Address((byte[])Ints.toByteArray((int)coercedHash));
    }

    public static int coerceToInteger(InetAddress ip) {
        return ByteStreams.newDataInput((byte[])InetAddresses.getCoercedIPv4Address((InetAddress)ip).getAddress()).readInt();
    }

    public static Inet4Address fromInteger(int address) {
        return InetAddresses.getInet4Address((byte[])Ints.toByteArray((int)address));
    }

    public static InetAddress fromLittleEndianByteArray(byte[] addr) throws UnknownHostException {
        byte[] reversed = new byte[addr.length];
        int i = 0;
        while (i < addr.length) {
            reversed[i] = addr[addr.length - i - 1];
            ++i;
        }
        return InetAddress.getByAddress((byte[])reversed);
    }

    public static InetAddress decrement(InetAddress address) {
        int i;
        byte[] addr = address.getAddress();
        for (i = addr.length - 1; i >= 0 && addr[i] == 0; --i) {
            addr[i] = -1;
        }
        Preconditions.checkArgument((boolean)(i >= 0), (String)"Decrementing %s would wrap.", (Object)address);
        byte[] arrby = addr;
        int n = i;
        arrby[n] = (byte)(arrby[n] - 1);
        return InetAddresses.bytesToInetAddress((byte[])addr);
    }

    public static InetAddress increment(InetAddress address) {
        int i;
        byte[] addr = address.getAddress();
        for (i = addr.length - 1; i >= 0 && addr[i] == -1; --i) {
            addr[i] = 0;
        }
        Preconditions.checkArgument((boolean)(i >= 0), (String)"Incrementing %s would wrap.", (Object)address);
        byte[] arrby = addr;
        int n = i;
        arrby[n] = (byte)(arrby[n] + 1);
        return InetAddresses.bytesToInetAddress((byte[])addr);
    }

    public static boolean isMaximum(InetAddress address) {
        byte[] addr = address.getAddress();
        int i = 0;
        while (i < addr.length) {
            if (addr[i] != -1) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private static IllegalArgumentException formatIllegalArgumentException(String format, Object ... args) {
        return new IllegalArgumentException((String)String.format((Locale)Locale.ROOT, (String)format, (Object[])args));
    }

    static /* synthetic */ Inet4Address access$000() {
        return ANY4;
    }
}

