/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.EventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollDatagramChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.unix.DatagramSocketAddress;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class EpollDatagramChannel
extends AbstractEpollChannel
implements DatagramChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)true);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    private final EpollDatagramChannelConfig config = new EpollDatagramChannelConfig((EpollDatagramChannel)this);
    private volatile boolean connected;

    public EpollDatagramChannel() {
        this(null);
    }

    public EpollDatagramChannel(InternetProtocolFamily family) {
        this((LinuxSocket)(family == null ? LinuxSocket.newSocketDgram((boolean)Socket.isIPv6Preferred()) : LinuxSocket.newSocketDgram((boolean)(family == InternetProtocolFamily.IPv6))), (boolean)false);
    }

    public EpollDatagramChannel(int fd) {
        this((LinuxSocket)new LinuxSocket((int)fd), (boolean)true);
    }

    private EpollDatagramChannel(LinuxSocket fd, boolean active) {
        super(null, (LinuxSocket)fd, (boolean)active);
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public boolean isActive() {
        if (!this.socket.isOpen()) return false;
        if (this.config.getActiveOnOpen()) {
            if (this.isRegistered()) return true;
        }
        if (!this.active) return false;
        return true;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress) {
        return this.joinGroup((InetAddress)multicastAddress, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return this.joinGroup((InetAddress)multicastAddress, (NetworkInterface)NetworkInterface.getByInetAddress((InetAddress)this.localAddress().getAddress()), null, (ChannelPromise)promise);
        }
        catch (IOException e) {
            promise.setFailure((Throwable)e);
            return promise;
        }
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.joinGroup((InetSocketAddress)multicastAddress, (NetworkInterface)networkInterface, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        return this.joinGroup((InetAddress)multicastAddress.getAddress(), (NetworkInterface)networkInterface, null, (ChannelPromise)promise);
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.joinGroup((InetAddress)multicastAddress, (NetworkInterface)networkInterface, (InetAddress)source, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        if (multicastAddress == null) {
            throw new NullPointerException((String)"multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException((String)"networkInterface");
        }
        try {
            this.socket.joinGroup((InetAddress)multicastAddress, (NetworkInterface)networkInterface, (InetAddress)source);
            promise.setSuccess();
            return promise;
        }
        catch (IOException e) {
            promise.setFailure((Throwable)e);
        }
        return promise;
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress) {
        return this.leaveGroup((InetAddress)multicastAddress, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return this.leaveGroup((InetAddress)multicastAddress, (NetworkInterface)NetworkInterface.getByInetAddress((InetAddress)this.localAddress().getAddress()), null, (ChannelPromise)promise);
        }
        catch (IOException e) {
            promise.setFailure((Throwable)e);
            return promise;
        }
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.leaveGroup((InetSocketAddress)multicastAddress, (NetworkInterface)networkInterface, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        return this.leaveGroup((InetAddress)multicastAddress.getAddress(), (NetworkInterface)networkInterface, null, (ChannelPromise)promise);
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.leaveGroup((InetAddress)multicastAddress, (NetworkInterface)networkInterface, (InetAddress)source, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        if (multicastAddress == null) {
            throw new NullPointerException((String)"multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException((String)"networkInterface");
        }
        try {
            this.socket.leaveGroup((InetAddress)multicastAddress, (NetworkInterface)networkInterface, (InetAddress)source);
            promise.setSuccess();
            return promise;
        }
        catch (IOException e) {
            promise.setFailure((Throwable)e);
        }
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
        return this.block((InetAddress)multicastAddress, (NetworkInterface)networkInterface, (InetAddress)sourceToBlock, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
        if (multicastAddress == null) {
            throw new NullPointerException((String)"multicastAddress");
        }
        if (sourceToBlock == null) {
            throw new NullPointerException((String)"sourceToBlock");
        }
        if (networkInterface == null) {
            throw new NullPointerException((String)"networkInterface");
        }
        promise.setFailure((Throwable)new UnsupportedOperationException((String)"Multicast not supported"));
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
        return this.block((InetAddress)multicastAddress, (InetAddress)sourceToBlock, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
        try {
            return this.block((InetAddress)multicastAddress, (NetworkInterface)NetworkInterface.getByInetAddress((InetAddress)this.localAddress().getAddress()), (InetAddress)sourceToBlock, (ChannelPromise)promise);
        }
        catch (Throwable e) {
            promise.setFailure((Throwable)e);
            return promise;
        }
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollDatagramChannelUnsafe((EpollDatagramChannel)this);
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        InetSocketAddress socketAddress;
        if (localAddress instanceof InetSocketAddress && (socketAddress = (InetSocketAddress)localAddress).getAddress().isAnyLocalAddress() && socketAddress.getAddress() instanceof Inet4Address && Socket.isIPv6Preferred()) {
            localAddress = new InetSocketAddress((InetAddress)LinuxSocket.INET6_ANY, (int)socketAddress.getPort());
        }
        super.doBind((SocketAddress)localAddress);
        this.active = true;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        block2 : do {
            if ((msg = in.current()) == null) {
                this.clearFlag((int)Native.EPOLLOUT);
                return;
            }
            try {
                if (!Native.IS_SUPPORTING_SENDMMSG || in.size() <= 1) ** GOTO lbl-1000
                array = this.cleanDatagramPacketArray();
                array.add((ChannelOutboundBuffer)in, (boolean)this.isConnected());
                cnt = array.count();
                if (cnt < 1) lbl-1000: // 2 sources:
                {
                    done = false;
                } else {
                    offset = 0;
                    packets = array.packets();
                    do {
                        if (cnt <= 0) continue block2;
                        send = this.socket.sendmmsg((NativeDatagramPacketArray.NativeDatagramPacket[])packets, (int)offset, (int)cnt);
                        if (send == 0) {
                            this.setFlag((int)Native.EPOLLOUT);
                            return;
                        }
                        for (i = 0; i < send; ++i) {
                            in.remove();
                        }
                        cnt -= send;
                        offset += send;
                    } while (true);
                }
                for (i = this.config().getWriteSpinCount(); i > 0; --i) {
                    if (!this.doWriteMessage((Object)msg)) continue;
                    done = true;
                    break;
                }
                if (!done) {
                    this.setFlag((int)Native.EPOLLOUT);
                    return;
                }
                in.remove();
                continue;
            }
            catch (IOException e) {
                in.remove((Throwable)e);
                continue;
            }
            break;
        } while (true);
    }

    private boolean doWriteMessage(Object msg) throws Exception {
        ByteBuf data;
        InetSocketAddress remoteAddress;
        long writtenBytes;
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope envelope = (AddressedEnvelope)msg;
            data = (ByteBuf)envelope.content();
            remoteAddress = (InetSocketAddress)envelope.recipient();
        } else {
            data = (ByteBuf)msg;
            remoteAddress = null;
        }
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        if (data.hasMemoryAddress()) {
            long memoryAddress = data.memoryAddress();
            writtenBytes = remoteAddress == null ? (long)this.socket.writeAddress((long)memoryAddress, (int)data.readerIndex(), (int)data.writerIndex()) : (long)this.socket.sendToAddress((long)memoryAddress, (int)data.readerIndex(), (int)data.writerIndex(), (InetAddress)remoteAddress.getAddress(), (int)remoteAddress.getPort());
        } else if (data.nioBufferCount() > 1) {
            IovArray array = ((EpollEventLoop)this.eventLoop()).cleanIovArray();
            array.add((ByteBuf)data, (int)data.readerIndex(), (int)data.readableBytes());
            int cnt = array.count();
            assert (cnt != 0);
            writtenBytes = remoteAddress == null ? this.socket.writevAddresses((long)array.memoryAddress((int)0), (int)cnt) : (long)this.socket.sendToAddresses((long)array.memoryAddress((int)0), (int)cnt, (InetAddress)remoteAddress.getAddress(), (int)remoteAddress.getPort());
        } else {
            ByteBuffer nioData = data.internalNioBuffer((int)data.readerIndex(), (int)data.readableBytes());
            writtenBytes = remoteAddress == null ? (long)this.socket.write((ByteBuffer)nioData, (int)nioData.position(), (int)nioData.limit()) : (long)this.socket.sendTo((ByteBuffer)nioData, (int)nioData.position(), (int)nioData.limit(), (InetAddress)remoteAddress.getAddress(), (int)remoteAddress.getPort());
        }
        if (writtenBytes <= 0L) return false;
        return true;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        ByteBuf content;
        AddressedEnvelope addressedEnvelope;
        if (msg instanceof DatagramPacket) {
            Object object;
            DatagramPacket packet = (DatagramPacket)msg;
            ByteBuf content2 = (ByteBuf)packet.content();
            if (UnixChannelUtil.isBufferCopyNeededForWrite((ByteBuf)content2)) {
                object = new DatagramPacket((ByteBuf)this.newDirectBuffer((Object)packet, (ByteBuf)content2), (InetSocketAddress)((InetSocketAddress)packet.recipient()));
                return object;
            }
            object = msg;
            return object;
        }
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf;
            ByteBuf buf = (ByteBuf)msg;
            if (UnixChannelUtil.isBufferCopyNeededForWrite((ByteBuf)buf)) {
                byteBuf = this.newDirectBuffer((ByteBuf)buf);
                return byteBuf;
            }
            byteBuf = buf;
            return byteBuf;
        }
        if (!(msg instanceof AddressedEnvelope)) throw new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES));
        AddressedEnvelope e = (AddressedEnvelope)msg;
        if (!(e.content() instanceof ByteBuf)) throw new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES));
        if (e.recipient() != null) {
            if (!(e.recipient() instanceof InetSocketAddress)) throw new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES));
        }
        if (UnixChannelUtil.isBufferCopyNeededForWrite((ByteBuf)(content = (ByteBuf)e.content()))) {
            addressedEnvelope = new DefaultAddressedEnvelope<ByteBuf, InetSocketAddress>(this.newDirectBuffer((Object)e, (ByteBuf)content), (InetSocketAddress)e.recipient());
            return addressedEnvelope;
        }
        addressedEnvelope = e;
        return addressedEnvelope;
    }

    @Override
    public EpollDatagramChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.socket.disconnect();
        this.active = false;
        this.connected = false;
        this.resetCachedAddresses();
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (!super.doConnect((SocketAddress)remoteAddress, (SocketAddress)localAddress)) return false;
        this.connected = true;
        return true;
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();
        this.connected = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean connectedRead(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int maxDatagramPacketSize) throws Exception {
        try {
            int localReadAmount;
            int writable = maxDatagramPacketSize != 0 ? Math.min((int)byteBuf.writableBytes(), (int)maxDatagramPacketSize) : byteBuf.writableBytes();
            allocHandle.attemptedBytesRead((int)writable);
            int writerIndex = byteBuf.writerIndex();
            if (byteBuf.hasMemoryAddress()) {
                localReadAmount = this.socket.readAddress((long)byteBuf.memoryAddress(), (int)writerIndex, (int)(writerIndex + writable));
            } else {
                ByteBuffer buf = byteBuf.internalNioBuffer((int)writerIndex, (int)writable);
                localReadAmount = this.socket.read((ByteBuffer)buf, (int)buf.position(), (int)buf.limit());
            }
            if (localReadAmount <= 0) {
                allocHandle.lastBytesRead((int)localReadAmount);
                boolean buf = false;
                return buf;
            }
            byteBuf.writerIndex((int)(writerIndex + localReadAmount));
            allocHandle.lastBytesRead((int)(maxDatagramPacketSize <= 0 ? localReadAmount : writable));
            DatagramPacket packet = new DatagramPacket((ByteBuf)byteBuf, (InetSocketAddress)this.localAddress(), (InetSocketAddress)this.remoteAddress());
            allocHandle.incMessagesRead((int)1);
            this.pipeline().fireChannelRead((Object)packet);
            byteBuf = null;
            boolean bl = true;
            return bl;
        }
        finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
        }
    }

    private IOException translateForConnected(Errors.NativeIoException e) {
        if (e.expectedErr() != Errors.ERROR_ECONNREFUSED_NEGATIVE) return e;
        PortUnreachableException error = new PortUnreachableException((String)e.getMessage());
        error.initCause((Throwable)e);
        return error;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean scatteringRead(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int datagramSize, int numDatagram) throws IOException {
        ArrayList bufferPackets = null;
        try {
            int i;
            int offset = byteBuf.writerIndex();
            NativeDatagramPacketArray array = this.cleanDatagramPacketArray();
            for (int i2 = 0; i2 < numDatagram && array.addWritable((ByteBuf)byteBuf, (int)offset, (int)datagramSize); ++i2, offset += datagramSize) {
            }
            allocHandle.attemptedBytesRead((int)(offset - byteBuf.writerIndex()));
            NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
            int received = this.socket.recvmmsg((NativeDatagramPacketArray.NativeDatagramPacket[])packets, (int)0, (int)array.count());
            if (!received) {
                allocHandle.lastBytesRead((int)-1);
                boolean bl = false;
                return bl;
            }
            int bytesReceived = received * datagramSize;
            byteBuf.writerIndex((int)bytesReceived);
            InetSocketAddress local = this.localAddress();
            if (received) {
                DatagramPacket packet = packets[0].newDatagramPacket((ByteBuf)byteBuf, (InetSocketAddress)local);
                allocHandle.lastBytesRead((int)datagramSize);
                allocHandle.incMessagesRead((int)1);
                this.pipeline().fireChannelRead((Object)packet);
                byteBuf = null;
                boolean bl = true;
                return bl;
            }
            bufferPackets = RecyclableArrayList.newInstance();
            for (i = 0; i < received; i += 1) {
                DatagramPacket packet = packets[i].newDatagramPacket((ByteBuf)byteBuf.readRetainedSlice((int)datagramSize), (InetSocketAddress)local);
                ((RecyclableArrayList)bufferPackets).add((Object)packet);
            }
            allocHandle.lastBytesRead((int)bytesReceived);
            allocHandle.incMessagesRead((int)received);
            for (i = 0; i < received; i += 1) {
                this.pipeline().fireChannelRead((Object)((RecyclableArrayList)bufferPackets).set((int)i, (Object)Unpooled.EMPTY_BUFFER));
            }
            ((RecyclableArrayList)bufferPackets).recycle();
            bufferPackets = null;
            i = 1;
            return i != 0;
        }
        finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
            if (bufferPackets != null) {
                for (int i = 0; i < bufferPackets.size(); ++i) {
                    ReferenceCountUtil.release(bufferPackets.get((int)i));
                }
                ((RecyclableArrayList)bufferPackets).recycle();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean read(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int maxDatagramPacketSize) throws IOException {
        try {
            DatagramSocketAddress remoteAddress;
            int writable = maxDatagramPacketSize != 0 ? Math.min((int)byteBuf.writableBytes(), (int)maxDatagramPacketSize) : byteBuf.writableBytes();
            allocHandle.attemptedBytesRead((int)writable);
            int writerIndex = byteBuf.writerIndex();
            if (byteBuf.hasMemoryAddress()) {
                remoteAddress = this.socket.recvFromAddress((long)byteBuf.memoryAddress(), (int)writerIndex, (int)(writerIndex + writable));
            } else {
                ByteBuffer nioData = byteBuf.internalNioBuffer((int)writerIndex, (int)writable);
                remoteAddress = this.socket.recvFrom((ByteBuffer)nioData, (int)nioData.position(), (int)nioData.limit());
            }
            if (remoteAddress == null) {
                allocHandle.lastBytesRead((int)-1);
                boolean nioData = false;
                return nioData;
            }
            InetSocketAddress localAddress = remoteAddress.localAddress();
            if (localAddress == null) {
                localAddress = this.localAddress();
            }
            allocHandle.lastBytesRead((int)(maxDatagramPacketSize <= 0 ? remoteAddress.receivedAmount() : writable));
            byteBuf.writerIndex((int)(byteBuf.writerIndex() + allocHandle.lastBytesRead()));
            allocHandle.incMessagesRead((int)1);
            this.pipeline().fireChannelRead((Object)new DatagramPacket((ByteBuf)byteBuf, (InetSocketAddress)localAddress, (InetSocketAddress)remoteAddress));
            byteBuf = null;
            boolean bl = true;
            return bl;
        }
        finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
        }
    }

    private NativeDatagramPacketArray cleanDatagramPacketArray() {
        return ((EpollEventLoop)this.eventLoop()).cleanDatagramPacketArray();
    }

    static /* synthetic */ boolean access$000(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, ByteBuf x2, int x3) throws Exception {
        return x0.connectedRead((EpollRecvByteAllocatorHandle)x1, (ByteBuf)x2, (int)x3);
    }

    static /* synthetic */ boolean access$100(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, ByteBuf x2, int x3) throws IOException {
        return x0.read((EpollRecvByteAllocatorHandle)x1, (ByteBuf)x2, (int)x3);
    }

    static /* synthetic */ boolean access$200(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, ByteBuf x2, int x3, int x4) throws IOException {
        return x0.scatteringRead((EpollRecvByteAllocatorHandle)x1, (ByteBuf)x2, (int)x3, (int)x4);
    }

    static /* synthetic */ IOException access$300(EpollDatagramChannel x0, Errors.NativeIoException x1) {
        return x0.translateForConnected((Errors.NativeIoException)x1);
    }
}

