/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import io.netty.channel.ChannelException;
import io.netty.channel.unix.DatagramSocketAddress;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.LimitsStaticallyReferencedJniMethods;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

public class Socket
extends FileDescriptor {
    public static final int UDS_SUN_PATH_SIZE = LimitsStaticallyReferencedJniMethods.udsSunPathSize();
    protected final boolean ipv6;
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean();

    public Socket(int fd) {
        super((int)fd);
        this.ipv6 = Socket.isIPv6((int)fd);
    }

    public final void shutdown() throws IOException {
        this.shutdown((boolean)true, (boolean)true);
    }

    public final void shutdown(boolean read, boolean write) throws IOException {
        int oldState;
        int newState;
        do {
            if (Socket.isClosed((int)(oldState = this.state))) {
                throw new ClosedChannelException();
            }
            newState = oldState;
            if (read && !Socket.isInputShutdown((int)newState)) {
                newState = Socket.inputShutdown((int)newState);
            }
            if (write && !Socket.isOutputShutdown((int)newState)) {
                newState = Socket.outputShutdown((int)newState);
            }
            if (newState != oldState) continue;
            return;
        } while (!this.casState((int)oldState, (int)newState));
        int res = Socket.shutdown((int)this.fd, (boolean)read, (boolean)write);
        if (res >= 0) return;
        Errors.ioResult((String)"shutdown", (int)res);
    }

    public final boolean isShutdown() {
        int state = this.state;
        if (!Socket.isInputShutdown((int)state)) return false;
        if (!Socket.isOutputShutdown((int)state)) return false;
        return true;
    }

    public final boolean isInputShutdown() {
        return Socket.isInputShutdown((int)this.state);
    }

    public final boolean isOutputShutdown() {
        return Socket.isOutputShutdown((int)this.state);
    }

    public final int sendTo(ByteBuffer buf, int pos, int limit, InetAddress addr, int port) throws IOException {
        int scopeId;
        byte[] address;
        if (addr instanceof Inet6Address) {
            address = addr.getAddress();
            scopeId = ((Inet6Address)addr).getScopeId();
        } else {
            scopeId = 0;
            address = NativeInetAddress.ipv4MappedIpv6Address((byte[])addr.getAddress());
        }
        int res = Socket.sendTo((int)this.fd, (boolean)this.ipv6, (ByteBuffer)buf, (int)pos, (int)limit, (byte[])address, (int)scopeId, (int)port);
        if (res >= 0) {
            return res;
        }
        if (res != Errors.ERROR_ECONNREFUSED_NEGATIVE) return Errors.ioResult((String)"sendTo", (int)res);
        throw new PortUnreachableException((String)"sendTo failed");
    }

    public final int sendToAddress(long memoryAddress, int pos, int limit, InetAddress addr, int port) throws IOException {
        byte[] address;
        int scopeId;
        if (addr instanceof Inet6Address) {
            address = addr.getAddress();
            scopeId = ((Inet6Address)addr).getScopeId();
        } else {
            scopeId = 0;
            address = NativeInetAddress.ipv4MappedIpv6Address((byte[])addr.getAddress());
        }
        int res = Socket.sendToAddress((int)this.fd, (boolean)this.ipv6, (long)memoryAddress, (int)pos, (int)limit, (byte[])address, (int)scopeId, (int)port);
        if (res >= 0) {
            return res;
        }
        if (res != Errors.ERROR_ECONNREFUSED_NEGATIVE) return Errors.ioResult((String)"sendToAddress", (int)res);
        throw new PortUnreachableException((String)"sendToAddress failed");
    }

    public final int sendToAddresses(long memoryAddress, int length, InetAddress addr, int port) throws IOException {
        int scopeId;
        byte[] address;
        if (addr instanceof Inet6Address) {
            address = addr.getAddress();
            scopeId = ((Inet6Address)addr).getScopeId();
        } else {
            scopeId = 0;
            address = NativeInetAddress.ipv4MappedIpv6Address((byte[])addr.getAddress());
        }
        int res = Socket.sendToAddresses((int)this.fd, (boolean)this.ipv6, (long)memoryAddress, (int)length, (byte[])address, (int)scopeId, (int)port);
        if (res >= 0) {
            return res;
        }
        if (res != Errors.ERROR_ECONNREFUSED_NEGATIVE) return Errors.ioResult((String)"sendToAddresses", (int)res);
        throw new PortUnreachableException((String)"sendToAddresses failed");
    }

    public final DatagramSocketAddress recvFrom(ByteBuffer buf, int pos, int limit) throws IOException {
        return Socket.recvFrom((int)this.fd, (ByteBuffer)buf, (int)pos, (int)limit);
    }

    public final DatagramSocketAddress recvFromAddress(long memoryAddress, int pos, int limit) throws IOException {
        return Socket.recvFromAddress((int)this.fd, (long)memoryAddress, (int)pos, (int)limit);
    }

    public final int recvFd() throws IOException {
        int res = Socket.recvFd((int)this.fd);
        if (res > 0) {
            return res;
        }
        if (res == 0) {
            return -1;
        }
        if (res == Errors.ERRNO_EAGAIN_NEGATIVE) return 0;
        if (res != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) throw Errors.newIOException((String)"recvFd", (int)res);
        return 0;
    }

    public final int sendFd(int fdToSend) throws IOException {
        int res = Socket.sendFd((int)this.fd, (int)fdToSend);
        if (res >= 0) {
            return res;
        }
        if (res == Errors.ERRNO_EAGAIN_NEGATIVE) return -1;
        if (res != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) throw Errors.newIOException((String)"sendFd", (int)res);
        return -1;
    }

    public final boolean connect(SocketAddress socketAddress) throws IOException {
        int res;
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
            NativeInetAddress address = NativeInetAddress.newInstance((InetAddress)inetSocketAddress.getAddress());
            res = Socket.connect((int)this.fd, (boolean)this.ipv6, (byte[])address.address, (int)address.scopeId, (int)inetSocketAddress.getPort());
        } else {
            if (!(socketAddress instanceof DomainSocketAddress)) throw new Error((String)("Unexpected SocketAddress implementation " + socketAddress));
            DomainSocketAddress unixDomainSocketAddress = (DomainSocketAddress)socketAddress;
            res = Socket.connectDomainSocket((int)this.fd, (byte[])unixDomainSocketAddress.path().getBytes((Charset)CharsetUtil.UTF_8));
        }
        if (res >= 0) return true;
        if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE) {
            return false;
        }
        Errors.throwConnectException((String)"connect", (int)res);
        return true;
    }

    public final boolean finishConnect() throws IOException {
        int res = Socket.finishConnect((int)this.fd);
        if (res >= 0) return true;
        if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE) {
            return false;
        }
        Errors.throwConnectException((String)"finishConnect", (int)res);
        return true;
    }

    public final void disconnect() throws IOException {
        int res = Socket.disconnect((int)this.fd, (boolean)this.ipv6);
        if (res >= 0) return;
        Errors.throwConnectException((String)"disconnect", (int)res);
    }

    public final void bind(SocketAddress socketAddress) throws IOException {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress)socketAddress;
            NativeInetAddress address = NativeInetAddress.newInstance((InetAddress)addr.getAddress());
            int res = Socket.bind((int)this.fd, (boolean)this.ipv6, (byte[])address.address, (int)address.scopeId, (int)addr.getPort());
            if (res >= 0) return;
            throw Errors.newIOException((String)"bind", (int)res);
        }
        if (!(socketAddress instanceof DomainSocketAddress)) throw new Error((String)("Unexpected SocketAddress implementation " + socketAddress));
        DomainSocketAddress addr = (DomainSocketAddress)socketAddress;
        int res = Socket.bindDomainSocket((int)this.fd, (byte[])addr.path().getBytes((Charset)CharsetUtil.UTF_8));
        if (res >= 0) return;
        throw Errors.newIOException((String)"bind", (int)res);
    }

    public final void listen(int backlog) throws IOException {
        int res = Socket.listen((int)this.fd, (int)backlog);
        if (res >= 0) return;
        throw Errors.newIOException((String)"listen", (int)res);
    }

    public final int accept(byte[] addr) throws IOException {
        int res = Socket.accept((int)this.fd, (byte[])addr);
        if (res >= 0) {
            return res;
        }
        if (res == Errors.ERRNO_EAGAIN_NEGATIVE) return -1;
        if (res != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) throw Errors.newIOException((String)"accept", (int)res);
        return -1;
    }

    public final InetSocketAddress remoteAddress() {
        byte[] addr = Socket.remoteAddress((int)this.fd);
        if (addr == null) {
            return null;
        }
        InetSocketAddress inetSocketAddress = NativeInetAddress.address((byte[])addr, (int)0, (int)addr.length);
        return inetSocketAddress;
    }

    public final InetSocketAddress localAddress() {
        byte[] addr = Socket.localAddress((int)this.fd);
        if (addr == null) {
            return null;
        }
        InetSocketAddress inetSocketAddress = NativeInetAddress.address((byte[])addr, (int)0, (int)addr.length);
        return inetSocketAddress;
    }

    public final int getReceiveBufferSize() throws IOException {
        return Socket.getReceiveBufferSize((int)this.fd);
    }

    public final int getSendBufferSize() throws IOException {
        return Socket.getSendBufferSize((int)this.fd);
    }

    public final boolean isKeepAlive() throws IOException {
        if (Socket.isKeepAlive((int)this.fd) == 0) return false;
        return true;
    }

    public final boolean isTcpNoDelay() throws IOException {
        if (Socket.isTcpNoDelay((int)this.fd) == 0) return false;
        return true;
    }

    public final boolean isReuseAddress() throws IOException {
        if (Socket.isReuseAddress((int)this.fd) == 0) return false;
        return true;
    }

    public final boolean isReusePort() throws IOException {
        if (Socket.isReusePort((int)this.fd) == 0) return false;
        return true;
    }

    public final boolean isBroadcast() throws IOException {
        if (Socket.isBroadcast((int)this.fd) == 0) return false;
        return true;
    }

    public final int getSoLinger() throws IOException {
        return Socket.getSoLinger((int)this.fd);
    }

    public final int getSoError() throws IOException {
        return Socket.getSoError((int)this.fd);
    }

    public final int getTrafficClass() throws IOException {
        return Socket.getTrafficClass((int)this.fd, (boolean)this.ipv6);
    }

    public final void setKeepAlive(boolean keepAlive) throws IOException {
        Socket.setKeepAlive((int)this.fd, (int)(keepAlive ? 1 : 0));
    }

    public final void setReceiveBufferSize(int receiveBufferSize) throws IOException {
        Socket.setReceiveBufferSize((int)this.fd, (int)receiveBufferSize);
    }

    public final void setSendBufferSize(int sendBufferSize) throws IOException {
        Socket.setSendBufferSize((int)this.fd, (int)sendBufferSize);
    }

    public final void setTcpNoDelay(boolean tcpNoDelay) throws IOException {
        Socket.setTcpNoDelay((int)this.fd, (int)(tcpNoDelay ? 1 : 0));
    }

    public final void setSoLinger(int soLinger) throws IOException {
        Socket.setSoLinger((int)this.fd, (int)soLinger);
    }

    public final void setReuseAddress(boolean reuseAddress) throws IOException {
        Socket.setReuseAddress((int)this.fd, (int)(reuseAddress ? 1 : 0));
    }

    public final void setReusePort(boolean reusePort) throws IOException {
        Socket.setReusePort((int)this.fd, (int)(reusePort ? 1 : 0));
    }

    public final void setBroadcast(boolean broadcast) throws IOException {
        Socket.setBroadcast((int)this.fd, (int)(broadcast ? 1 : 0));
    }

    public final void setTrafficClass(int trafficClass) throws IOException {
        Socket.setTrafficClass((int)this.fd, (boolean)this.ipv6, (int)trafficClass);
    }

    public static native boolean isIPv6Preferred();

    private static native boolean isIPv6(int var0);

    @Override
    public String toString() {
        return "Socket{fd=" + this.fd + '}';
    }

    public static Socket newSocketStream() {
        return new Socket((int)Socket.newSocketStream0());
    }

    public static Socket newSocketDgram() {
        return new Socket((int)Socket.newSocketDgram0());
    }

    public static Socket newSocketDomain() {
        return new Socket((int)Socket.newSocketDomain0());
    }

    public static void initialize() {
        if (!INITIALIZED.compareAndSet((boolean)false, (boolean)true)) return;
        Socket.initialize((boolean)NetUtil.isIpV4StackPreferred());
    }

    protected static int newSocketStream0() {
        return Socket.newSocketStream0((boolean)Socket.isIPv6Preferred());
    }

    protected static int newSocketStream0(boolean ipv6) {
        int res = Socket.newSocketStreamFd((boolean)ipv6);
        if (res >= 0) return res;
        throw new ChannelException((Throwable)Errors.newIOException((String)"newSocketStream", (int)res));
    }

    protected static int newSocketDgram0() {
        return Socket.newSocketDgram0((boolean)Socket.isIPv6Preferred());
    }

    protected static int newSocketDgram0(boolean ipv6) {
        int res = Socket.newSocketDgramFd((boolean)ipv6);
        if (res >= 0) return res;
        throw new ChannelException((Throwable)Errors.newIOException((String)"newSocketDgram", (int)res));
    }

    protected static int newSocketDomain0() {
        int res = Socket.newSocketDomainFd();
        if (res >= 0) return res;
        throw new ChannelException((Throwable)Errors.newIOException((String)"newSocketDomain", (int)res));
    }

    private static native int shutdown(int var0, boolean var1, boolean var2);

    private static native int connect(int var0, boolean var1, byte[] var2, int var3, int var4);

    private static native int connectDomainSocket(int var0, byte[] var1);

    private static native int finishConnect(int var0);

    private static native int disconnect(int var0, boolean var1);

    private static native int bind(int var0, boolean var1, byte[] var2, int var3, int var4);

    private static native int bindDomainSocket(int var0, byte[] var1);

    private static native int listen(int var0, int var1);

    private static native int accept(int var0, byte[] var1);

    private static native byte[] remoteAddress(int var0);

    private static native byte[] localAddress(int var0);

    private static native int sendTo(int var0, boolean var1, ByteBuffer var2, int var3, int var4, byte[] var5, int var6, int var7);

    private static native int sendToAddress(int var0, boolean var1, long var2, int var4, int var5, byte[] var6, int var7, int var8);

    private static native int sendToAddresses(int var0, boolean var1, long var2, int var4, byte[] var5, int var6, int var7);

    private static native DatagramSocketAddress recvFrom(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

    private static native DatagramSocketAddress recvFromAddress(int var0, long var1, int var3, int var4) throws IOException;

    private static native int recvFd(int var0);

    private static native int sendFd(int var0, int var1);

    private static native int newSocketStreamFd(boolean var0);

    private static native int newSocketDgramFd(boolean var0);

    private static native int newSocketDomainFd();

    private static native int isReuseAddress(int var0) throws IOException;

    private static native int isReusePort(int var0) throws IOException;

    private static native int getReceiveBufferSize(int var0) throws IOException;

    private static native int getSendBufferSize(int var0) throws IOException;

    private static native int isKeepAlive(int var0) throws IOException;

    private static native int isTcpNoDelay(int var0) throws IOException;

    private static native int isBroadcast(int var0) throws IOException;

    private static native int getSoLinger(int var0) throws IOException;

    private static native int getSoError(int var0) throws IOException;

    private static native int getTrafficClass(int var0, boolean var1) throws IOException;

    private static native void setReuseAddress(int var0, int var1) throws IOException;

    private static native void setReusePort(int var0, int var1) throws IOException;

    private static native void setKeepAlive(int var0, int var1) throws IOException;

    private static native void setReceiveBufferSize(int var0, int var1) throws IOException;

    private static native void setSendBufferSize(int var0, int var1) throws IOException;

    private static native void setTcpNoDelay(int var0, int var1) throws IOException;

    private static native void setSoLinger(int var0, int var1) throws IOException;

    private static native void setBroadcast(int var0, int var1) throws IOException;

    private static native void setTrafficClass(int var0, boolean var1, int var2) throws IOException;

    private static native void initialize(boolean var0);
}

