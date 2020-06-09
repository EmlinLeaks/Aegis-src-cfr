/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.ChannelException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.epoll.EpollTcpInfo;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

final class LinuxSocket
extends Socket {
    static final InetAddress INET6_ANY = LinuxSocket.unsafeInetAddrByName((String)"::");
    private static final InetAddress INET_ANY = LinuxSocket.unsafeInetAddrByName((String)"0.0.0.0");
    private static final long MAX_UINT32_T = 0xFFFFFFFFL;

    LinuxSocket(int fd) {
        super((int)fd);
    }

    private InternetProtocolFamily family() {
        InternetProtocolFamily internetProtocolFamily;
        if (this.ipv6) {
            internetProtocolFamily = InternetProtocolFamily.IPv6;
            return internetProtocolFamily;
        }
        internetProtocolFamily = InternetProtocolFamily.IPv4;
        return internetProtocolFamily;
    }

    int sendmmsg(NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        return Native.sendmmsg((int)this.intValue(), (boolean)this.ipv6, (NativeDatagramPacketArray.NativeDatagramPacket[])msgs, (int)offset, (int)len);
    }

    int recvmmsg(NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        return Native.recvmmsg((int)this.intValue(), (boolean)this.ipv6, (NativeDatagramPacketArray.NativeDatagramPacket[])msgs, (int)offset, (int)len);
    }

    void setTimeToLive(int ttl) throws IOException {
        LinuxSocket.setTimeToLive((int)this.intValue(), (int)ttl);
    }

    void setInterface(InetAddress address) throws IOException {
        NativeInetAddress a = NativeInetAddress.newInstance((InetAddress)address);
        LinuxSocket.setInterface((int)this.intValue(), (boolean)this.ipv6, (byte[])a.address(), (int)a.scopeId(), (int)LinuxSocket.interfaceIndex((InetAddress)address));
    }

    void setNetworkInterface(NetworkInterface netInterface) throws IOException {
        InetAddress address = LinuxSocket.deriveInetAddress((NetworkInterface)netInterface, (boolean)(this.family() == InternetProtocolFamily.IPv6));
        if (address.equals((Object)(this.family() == InternetProtocolFamily.IPv4 ? INET_ANY : INET6_ANY))) {
            throw new IOException((String)("NetworkInterface does not support " + (Object)((Object)this.family())));
        }
        NativeInetAddress nativeAddress = NativeInetAddress.newInstance((InetAddress)address);
        LinuxSocket.setInterface((int)this.intValue(), (boolean)this.ipv6, (byte[])nativeAddress.address(), (int)nativeAddress.scopeId(), (int)LinuxSocket.interfaceIndex((NetworkInterface)netInterface));
    }

    InetAddress getInterface() throws IOException {
        NetworkInterface inf = this.getNetworkInterface();
        if (inf == null) return null;
        Enumeration<InetAddress> addresses = SocketUtils.addressesFromNetworkInterface((NetworkInterface)inf);
        if (!addresses.hasMoreElements()) return null;
        return addresses.nextElement();
    }

    NetworkInterface getNetworkInterface() throws IOException {
        int ret = LinuxSocket.getInterface((int)this.intValue(), (boolean)this.ipv6);
        if (this.ipv6) {
            if (PlatformDependent.javaVersion() < 7) return null;
            NetworkInterface networkInterface = NetworkInterface.getByIndex((int)ret);
            return networkInterface;
        }
        InetAddress address = LinuxSocket.inetAddress((int)ret);
        if (address == null) return null;
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress((InetAddress)address);
        return networkInterface;
    }

    private static InetAddress inetAddress(int value) {
        byte[] var1 = new byte[]{(byte)(value >>> 24 & 255), (byte)(value >>> 16 & 255), (byte)(value >>> 8 & 255), (byte)(value & 255)};
        try {
            return InetAddress.getByAddress((byte[])var1);
        }
        catch (UnknownHostException ignore) {
            return null;
        }
    }

    void joinGroup(InetAddress group, NetworkInterface netInterface, InetAddress source) throws IOException {
        NativeInetAddress g = NativeInetAddress.newInstance((InetAddress)group);
        boolean isIpv6 = group instanceof Inet6Address;
        NativeInetAddress i = NativeInetAddress.newInstance((InetAddress)LinuxSocket.deriveInetAddress((NetworkInterface)netInterface, (boolean)isIpv6));
        if (source != null) {
            NativeInetAddress s = NativeInetAddress.newInstance((InetAddress)source);
            LinuxSocket.joinSsmGroup((int)this.intValue(), (boolean)this.ipv6, (byte[])g.address(), (byte[])i.address(), (int)g.scopeId(), (int)LinuxSocket.interfaceIndex((NetworkInterface)netInterface), (byte[])s.address());
            return;
        }
        LinuxSocket.joinGroup((int)this.intValue(), (boolean)this.ipv6, (byte[])g.address(), (byte[])i.address(), (int)g.scopeId(), (int)LinuxSocket.interfaceIndex((NetworkInterface)netInterface));
    }

    void leaveGroup(InetAddress group, NetworkInterface netInterface, InetAddress source) throws IOException {
        NativeInetAddress g = NativeInetAddress.newInstance((InetAddress)group);
        boolean isIpv6 = group instanceof Inet6Address;
        NativeInetAddress i = NativeInetAddress.newInstance((InetAddress)LinuxSocket.deriveInetAddress((NetworkInterface)netInterface, (boolean)isIpv6));
        if (source != null) {
            NativeInetAddress s = NativeInetAddress.newInstance((InetAddress)source);
            LinuxSocket.leaveSsmGroup((int)this.intValue(), (boolean)this.ipv6, (byte[])g.address(), (byte[])i.address(), (int)g.scopeId(), (int)LinuxSocket.interfaceIndex((NetworkInterface)netInterface), (byte[])s.address());
            return;
        }
        LinuxSocket.leaveGroup((int)this.intValue(), (boolean)this.ipv6, (byte[])g.address(), (byte[])i.address(), (int)g.scopeId(), (int)LinuxSocket.interfaceIndex((NetworkInterface)netInterface));
    }

    private static int interfaceIndex(NetworkInterface networkInterface) {
        if (PlatformDependent.javaVersion() < 7) return -1;
        int n = networkInterface.getIndex();
        return n;
    }

    private static int interfaceIndex(InetAddress address) throws IOException {
        if (PlatformDependent.javaVersion() < 7) return -1;
        NetworkInterface iface = NetworkInterface.getByInetAddress((InetAddress)address);
        if (iface == null) return -1;
        return iface.getIndex();
    }

    void setTcpDeferAccept(int deferAccept) throws IOException {
        LinuxSocket.setTcpDeferAccept((int)this.intValue(), (int)deferAccept);
    }

    void setTcpQuickAck(boolean quickAck) throws IOException {
        LinuxSocket.setTcpQuickAck((int)this.intValue(), (int)(quickAck ? 1 : 0));
    }

    void setTcpCork(boolean tcpCork) throws IOException {
        LinuxSocket.setTcpCork((int)this.intValue(), (int)(tcpCork ? 1 : 0));
    }

    void setSoBusyPoll(int loopMicros) throws IOException {
        LinuxSocket.setSoBusyPoll((int)this.intValue(), (int)loopMicros);
    }

    void setTcpNotSentLowAt(long tcpNotSentLowAt) throws IOException {
        if (tcpNotSentLowAt < 0L) throw new IllegalArgumentException((String)"tcpNotSentLowAt must be a uint32_t");
        if (tcpNotSentLowAt > 0xFFFFFFFFL) {
            throw new IllegalArgumentException((String)"tcpNotSentLowAt must be a uint32_t");
        }
        LinuxSocket.setTcpNotSentLowAt((int)this.intValue(), (int)((int)tcpNotSentLowAt));
    }

    void setTcpFastOpen(int tcpFastopenBacklog) throws IOException {
        LinuxSocket.setTcpFastOpen((int)this.intValue(), (int)tcpFastopenBacklog);
    }

    void setTcpFastOpenConnect(boolean tcpFastOpenConnect) throws IOException {
        LinuxSocket.setTcpFastOpenConnect((int)this.intValue(), (int)(tcpFastOpenConnect ? 1 : 0));
    }

    boolean isTcpFastOpenConnect() throws IOException {
        if (LinuxSocket.isTcpFastOpenConnect((int)this.intValue()) == 0) return false;
        return true;
    }

    void setTcpKeepIdle(int seconds) throws IOException {
        LinuxSocket.setTcpKeepIdle((int)this.intValue(), (int)seconds);
    }

    void setTcpKeepIntvl(int seconds) throws IOException {
        LinuxSocket.setTcpKeepIntvl((int)this.intValue(), (int)seconds);
    }

    void setTcpKeepCnt(int probes) throws IOException {
        LinuxSocket.setTcpKeepCnt((int)this.intValue(), (int)probes);
    }

    void setTcpUserTimeout(int milliseconds) throws IOException {
        LinuxSocket.setTcpUserTimeout((int)this.intValue(), (int)milliseconds);
    }

    void setIpFreeBind(boolean enabled) throws IOException {
        LinuxSocket.setIpFreeBind((int)this.intValue(), (int)(enabled ? 1 : 0));
    }

    void setIpTransparent(boolean enabled) throws IOException {
        LinuxSocket.setIpTransparent((int)this.intValue(), (int)(enabled ? 1 : 0));
    }

    void setIpRecvOrigDestAddr(boolean enabled) throws IOException {
        LinuxSocket.setIpRecvOrigDestAddr((int)this.intValue(), (int)(enabled ? 1 : 0));
    }

    int getTimeToLive() throws IOException {
        return LinuxSocket.getTimeToLive((int)this.intValue());
    }

    void getTcpInfo(EpollTcpInfo info) throws IOException {
        LinuxSocket.getTcpInfo((int)this.intValue(), (long[])info.info);
    }

    void setTcpMd5Sig(InetAddress address, byte[] key) throws IOException {
        NativeInetAddress a = NativeInetAddress.newInstance((InetAddress)address);
        LinuxSocket.setTcpMd5Sig((int)this.intValue(), (boolean)this.ipv6, (byte[])a.address(), (int)a.scopeId(), (byte[])key);
    }

    boolean isTcpCork() throws IOException {
        if (LinuxSocket.isTcpCork((int)this.intValue()) == 0) return false;
        return true;
    }

    int getSoBusyPoll() throws IOException {
        return LinuxSocket.getSoBusyPoll((int)this.intValue());
    }

    int getTcpDeferAccept() throws IOException {
        return LinuxSocket.getTcpDeferAccept((int)this.intValue());
    }

    boolean isTcpQuickAck() throws IOException {
        if (LinuxSocket.isTcpQuickAck((int)this.intValue()) == 0) return false;
        return true;
    }

    long getTcpNotSentLowAt() throws IOException {
        return (long)LinuxSocket.getTcpNotSentLowAt((int)this.intValue()) & 0xFFFFFFFFL;
    }

    int getTcpKeepIdle() throws IOException {
        return LinuxSocket.getTcpKeepIdle((int)this.intValue());
    }

    int getTcpKeepIntvl() throws IOException {
        return LinuxSocket.getTcpKeepIntvl((int)this.intValue());
    }

    int getTcpKeepCnt() throws IOException {
        return LinuxSocket.getTcpKeepCnt((int)this.intValue());
    }

    int getTcpUserTimeout() throws IOException {
        return LinuxSocket.getTcpUserTimeout((int)this.intValue());
    }

    boolean isIpFreeBind() throws IOException {
        if (LinuxSocket.isIpFreeBind((int)this.intValue()) == 0) return false;
        return true;
    }

    boolean isIpTransparent() throws IOException {
        if (LinuxSocket.isIpTransparent((int)this.intValue()) == 0) return false;
        return true;
    }

    boolean isIpRecvOrigDestAddr() throws IOException {
        if (LinuxSocket.isIpRecvOrigDestAddr((int)this.intValue()) == 0) return false;
        return true;
    }

    PeerCredentials getPeerCredentials() throws IOException {
        return LinuxSocket.getPeerCredentials((int)this.intValue());
    }

    boolean isLoopbackModeDisabled() throws IOException {
        if (LinuxSocket.getIpMulticastLoop((int)this.intValue(), (boolean)this.ipv6) != 0) return false;
        return true;
    }

    void setLoopbackModeDisabled(boolean loopbackModeDisabled) throws IOException {
        LinuxSocket.setIpMulticastLoop((int)this.intValue(), (boolean)this.ipv6, (int)(loopbackModeDisabled ? 0 : 1));
    }

    long sendFile(DefaultFileRegion src, long baseOffset, long offset, long length) throws IOException {
        src.open();
        long res = LinuxSocket.sendFile((int)this.intValue(), (DefaultFileRegion)src, (long)baseOffset, (long)offset, (long)length);
        if (res < 0L) return (long)Errors.ioResult((String)"sendfile", (int)((int)res));
        return res;
    }

    private static InetAddress deriveInetAddress(NetworkInterface netInterface, boolean ipv6) {
        boolean isV6;
        InetAddress ia;
        InetAddress ipAny = ipv6 ? INET6_ANY : INET_ANY;
        if (netInterface == null) return ipAny;
        Enumeration<InetAddress> ias = netInterface.getInetAddresses();
        do {
            if (!ias.hasMoreElements()) return ipAny;
        } while ((isV6 = (ia = ias.nextElement()) instanceof Inet6Address) != ipv6);
        return ia;
    }

    public static LinuxSocket newSocketStream(boolean ipv6) {
        return new LinuxSocket((int)LinuxSocket.newSocketStream0((boolean)ipv6));
    }

    public static LinuxSocket newSocketStream() {
        return LinuxSocket.newSocketStream((boolean)LinuxSocket.isIPv6Preferred());
    }

    public static LinuxSocket newSocketDgram(boolean ipv6) {
        return new LinuxSocket((int)LinuxSocket.newSocketDgram0((boolean)ipv6));
    }

    public static LinuxSocket newSocketDgram() {
        return LinuxSocket.newSocketDgram((boolean)LinuxSocket.isIPv6Preferred());
    }

    public static LinuxSocket newSocketDomain() {
        return new LinuxSocket((int)LinuxSocket.newSocketDomain0());
    }

    private static InetAddress unsafeInetAddrByName(String inetName) {
        try {
            return InetAddress.getByName((String)inetName);
        }
        catch (UnknownHostException uhe) {
            throw new ChannelException((Throwable)uhe);
        }
    }

    private static native void joinGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws IOException;

    private static native void joinSsmGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws IOException;

    private static native void leaveGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws IOException;

    private static native void leaveSsmGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws IOException;

    private static native long sendFile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

    private static native int getTcpDeferAccept(int var0) throws IOException;

    private static native int isTcpQuickAck(int var0) throws IOException;

    private static native int isTcpCork(int var0) throws IOException;

    private static native int getSoBusyPoll(int var0) throws IOException;

    private static native int getTcpNotSentLowAt(int var0) throws IOException;

    private static native int getTcpKeepIdle(int var0) throws IOException;

    private static native int getTcpKeepIntvl(int var0) throws IOException;

    private static native int getTcpKeepCnt(int var0) throws IOException;

    private static native int getTcpUserTimeout(int var0) throws IOException;

    private static native int getTimeToLive(int var0) throws IOException;

    private static native int isIpFreeBind(int var0) throws IOException;

    private static native int isIpTransparent(int var0) throws IOException;

    private static native int isIpRecvOrigDestAddr(int var0) throws IOException;

    private static native void getTcpInfo(int var0, long[] var1) throws IOException;

    private static native PeerCredentials getPeerCredentials(int var0) throws IOException;

    private static native int isTcpFastOpenConnect(int var0) throws IOException;

    private static native void setTcpDeferAccept(int var0, int var1) throws IOException;

    private static native void setTcpQuickAck(int var0, int var1) throws IOException;

    private static native void setTcpCork(int var0, int var1) throws IOException;

    private static native void setSoBusyPoll(int var0, int var1) throws IOException;

    private static native void setTcpNotSentLowAt(int var0, int var1) throws IOException;

    private static native void setTcpFastOpen(int var0, int var1) throws IOException;

    private static native void setTcpFastOpenConnect(int var0, int var1) throws IOException;

    private static native void setTcpKeepIdle(int var0, int var1) throws IOException;

    private static native void setTcpKeepIntvl(int var0, int var1) throws IOException;

    private static native void setTcpKeepCnt(int var0, int var1) throws IOException;

    private static native void setTcpUserTimeout(int var0, int var1) throws IOException;

    private static native void setIpFreeBind(int var0, int var1) throws IOException;

    private static native void setIpTransparent(int var0, int var1) throws IOException;

    private static native void setIpRecvOrigDestAddr(int var0, int var1) throws IOException;

    private static native void setTcpMd5Sig(int var0, boolean var1, byte[] var2, int var3, byte[] var4) throws IOException;

    private static native void setInterface(int var0, boolean var1, byte[] var2, int var3, int var4) throws IOException;

    private static native int getInterface(int var0, boolean var1);

    private static native int getIpMulticastLoop(int var0, boolean var1) throws IOException;

    private static native void setIpMulticastLoop(int var0, boolean var1, int var2) throws IOException;

    private static native void setTimeToLive(int var0, int var1) throws IOException;
}

