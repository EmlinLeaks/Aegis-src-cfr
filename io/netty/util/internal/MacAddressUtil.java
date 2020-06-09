/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.NetUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class MacAddressUtil {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MacAddressUtil.class);
    private static final int EUI64_MAC_ADDRESS_LENGTH = 8;
    private static final int EUI48_MAC_ADDRESS_LENGTH = 6;

    public static byte[] bestAvailableMac() {
        byte[] bestMacAddr = EmptyArrays.EMPTY_BYTES;
        InetAddress bestInetAddr = NetUtil.LOCALHOST4;
        LinkedHashMap<NetworkInterface, InetAddress> ifaces = new LinkedHashMap<NetworkInterface, InetAddress>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    InetAddress a;
                    NetworkInterface iface = interfaces.nextElement();
                    Enumeration<InetAddress> addrs = SocketUtils.addressesFromNetworkInterface((NetworkInterface)iface);
                    if (!addrs.hasMoreElements() || (a = addrs.nextElement()).isLoopbackAddress()) continue;
                    ifaces.put(iface, a);
                }
            }
        }
        catch (SocketException e) {
            logger.warn((String)"Failed to retrieve the list of available network interfaces", (Throwable)e);
        }
        for (Map.Entry<K, V> entry : ifaces.entrySet()) {
            byte[] macAddr;
            NetworkInterface iface = (NetworkInterface)entry.getKey();
            InetAddress inetAddr = (InetAddress)entry.getValue();
            if (iface.isVirtual()) continue;
            try {
                macAddr = SocketUtils.hardwareAddressFromNetworkInterface((NetworkInterface)iface);
            }
            catch (SocketException e) {
                logger.debug((String)"Failed to get the hardware address of a network interface: {}", (Object)iface, (Object)e);
                continue;
            }
            boolean replace = false;
            int res = MacAddressUtil.compareAddresses((byte[])bestMacAddr, (byte[])macAddr);
            if (res < 0) {
                replace = true;
            } else if (res == 0) {
                res = MacAddressUtil.compareAddresses((InetAddress)bestInetAddr, (InetAddress)inetAddr);
                if (res < 0) {
                    replace = true;
                } else if (res == 0 && bestMacAddr.length < macAddr.length) {
                    replace = true;
                }
            }
            if (!replace) continue;
            bestMacAddr = macAddr;
            bestInetAddr = inetAddr;
        }
        if (bestMacAddr == EmptyArrays.EMPTY_BYTES) {
            return null;
        }
        switch (bestMacAddr.length) {
            case 6: {
                byte[] newAddr = new byte[8];
                System.arraycopy((Object)bestMacAddr, (int)0, (Object)newAddr, (int)0, (int)3);
                newAddr[3] = -1;
                newAddr[4] = -2;
                System.arraycopy((Object)bestMacAddr, (int)3, (Object)newAddr, (int)5, (int)3);
                return newAddr;
            }
        }
        return Arrays.copyOf((byte[])bestMacAddr, (int)8);
    }

    public static byte[] defaultMachineId() {
        byte[] bestMacAddr = MacAddressUtil.bestAvailableMac();
        if (bestMacAddr != null) return bestMacAddr;
        bestMacAddr = new byte[8];
        PlatformDependent.threadLocalRandom().nextBytes((byte[])bestMacAddr);
        logger.warn((String)"Failed to find a usable hardware address from the network interfaces; using random bytes: {}", (Object)MacAddressUtil.formatAddress((byte[])bestMacAddr));
        return bestMacAddr;
    }

    /*
     * Unable to fully structure code
     */
    public static byte[] parseMAC(String value) {
        switch (value.length()) {
            case 17: {
                separator = value.charAt((int)2);
                MacAddressUtil.validateMacSeparator((char)separator);
                machineId = new byte[6];
                ** break;
            }
            case 23: {
                separator = value.charAt((int)2);
                MacAddressUtil.validateMacSeparator((char)separator);
                machineId = new byte[8];
                ** break;
            }
        }
        throw new IllegalArgumentException((String)"value is not supported [MAC-48, EUI-48, EUI-64]");
lbl13: // 2 sources:
        end = machineId.length - 1;
        j = 0;
        i = 0;
        do {
            if (i >= end) {
                machineId[end] = StringUtil.decodeHexByte((CharSequence)value, (int)j);
                return machineId;
            }
            sIndex = j + 2;
            machineId[i] = StringUtil.decodeHexByte((CharSequence)value, (int)j);
            if (value.charAt((int)sIndex) != separator) {
                throw new IllegalArgumentException((String)("expected separator '" + separator + " but got '" + value.charAt((int)sIndex) + "' at index: " + sIndex));
            }
            ++i;
            j += 3;
        } while (true);
    }

    private static void validateMacSeparator(char separator) {
        if (separator == ':') return;
        if (separator == '-') return;
        throw new IllegalArgumentException((String)("unsupported separator: " + separator + " (expected: [:-])"));
    }

    public static String formatAddress(byte[] addr) {
        StringBuilder buf = new StringBuilder((int)24);
        byte[] arrby = addr;
        int n = arrby.length;
        int n2 = 0;
        while (n2 < n) {
            byte b = arrby[n2];
            buf.append((String)String.format((String)"%02x:", (Object[])new Object[]{Integer.valueOf((int)(b & 255))}));
            ++n2;
        }
        return buf.substring((int)0, (int)(buf.length() - 1));
    }

    static int compareAddresses(byte[] current, byte[] candidate) {
        if (candidate == null) return 1;
        if (candidate.length < 6) {
            return 1;
        }
        boolean onlyZeroAndOne = true;
        for (byte b : candidate) {
            if (b == 0 || b == 1) continue;
            onlyZeroAndOne = false;
            break;
        }
        if (onlyZeroAndOne) {
            return 1;
        }
        if ((candidate[0] & 1) != 0) {
            return 1;
        }
        if ((candidate[0] & 2) == 0) {
            if (current.length == 0) return -1;
            if ((current[0] & 2) != 0) return -1;
            return 0;
        }
        if (current.length == 0) return 0;
        if ((current[0] & 2) != 0) return 0;
        return 1;
    }

    private static int compareAddresses(InetAddress current, InetAddress candidate) {
        return MacAddressUtil.scoreAddress((InetAddress)current) - MacAddressUtil.scoreAddress((InetAddress)candidate);
    }

    private static int scoreAddress(InetAddress addr) {
        if (addr.isAnyLocalAddress()) return 0;
        if (addr.isLoopbackAddress()) {
            return 0;
        }
        if (addr.isMulticastAddress()) {
            return 1;
        }
        if (addr.isLinkLocalAddress()) {
            return 2;
        }
        if (!addr.isSiteLocalAddress()) return 4;
        return 3;
    }

    private MacAddressUtil() {
    }
}

