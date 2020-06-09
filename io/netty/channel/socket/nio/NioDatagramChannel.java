/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannelConfig;
import io.netty.channel.socket.nio.ProtocolFamilyConverter;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.MembershipKey;
import java.nio.channels.SelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class NioDatagramChannel
extends AbstractNioMessageChannel
implements DatagramChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)true);
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(SocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    private final DatagramChannelConfig config;
    private Map<InetAddress, List<MembershipKey>> memberships;

    private static java.nio.channels.DatagramChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openDatagramChannel();
        }
        catch (IOException e) {
            throw new ChannelException((String)"Failed to open a socket.", (Throwable)e);
        }
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    private static java.nio.channels.DatagramChannel newSocket(SelectorProvider provider, InternetProtocolFamily ipFamily) {
        if (ipFamily == null) {
            return NioDatagramChannel.newSocket((SelectorProvider)provider);
        }
        NioDatagramChannel.checkJavaVersion();
        try {
            return provider.openDatagramChannel((ProtocolFamily)ProtocolFamilyConverter.convert((InternetProtocolFamily)ipFamily));
        }
        catch (IOException e) {
            throw new ChannelException((String)"Failed to open a socket.", (Throwable)e);
        }
    }

    private static void checkJavaVersion() {
        if (PlatformDependent.javaVersion() >= 7) return;
        throw new UnsupportedOperationException((String)"Only supported on java 7+.");
    }

    public NioDatagramChannel() {
        this((java.nio.channels.DatagramChannel)NioDatagramChannel.newSocket((SelectorProvider)DEFAULT_SELECTOR_PROVIDER));
    }

    public NioDatagramChannel(SelectorProvider provider) {
        this((java.nio.channels.DatagramChannel)NioDatagramChannel.newSocket((SelectorProvider)provider));
    }

    public NioDatagramChannel(InternetProtocolFamily ipFamily) {
        this((java.nio.channels.DatagramChannel)NioDatagramChannel.newSocket((SelectorProvider)DEFAULT_SELECTOR_PROVIDER, (InternetProtocolFamily)ipFamily));
    }

    public NioDatagramChannel(SelectorProvider provider, InternetProtocolFamily ipFamily) {
        this((java.nio.channels.DatagramChannel)NioDatagramChannel.newSocket((SelectorProvider)provider, (InternetProtocolFamily)ipFamily));
    }

    public NioDatagramChannel(java.nio.channels.DatagramChannel socket) {
        super(null, (SelectableChannel)socket, (int)1);
        this.config = new NioDatagramChannelConfig((NioDatagramChannel)this, (java.nio.channels.DatagramChannel)socket);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public DatagramChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isActive() {
        java.nio.channels.DatagramChannel ch = this.javaChannel();
        if (!ch.isOpen()) return false;
        if (this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION).booleanValue()) {
            if (this.isRegistered()) return true;
        }
        if (!ch.socket().isBound()) return false;
        return true;
    }

    @Override
    public boolean isConnected() {
        return this.javaChannel().isConnected();
    }

    @Override
    protected java.nio.channels.DatagramChannel javaChannel() {
        return (java.nio.channels.DatagramChannel)super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.doBind0((SocketAddress)localAddress);
    }

    private void doBind0(SocketAddress localAddress) throws Exception {
        if (PlatformDependent.javaVersion() >= 7) {
            SocketUtils.bind((java.nio.channels.DatagramChannel)this.javaChannel(), (SocketAddress)localAddress);
            return;
        }
        this.javaChannel().socket().bind((SocketAddress)localAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.doBind0((SocketAddress)localAddress);
        }
        boolean success = false;
        try {
            this.javaChannel().connect((SocketAddress)remoteAddress);
            success = true;
            boolean bl = true;
            return bl;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new Error();
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.javaChannel().disconnect();
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        int pos;
        java.nio.channels.DatagramChannel ch = this.javaChannel();
        DatagramChannelConfig config = this.config();
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        ByteBuf data = allocHandle.allocate((ByteBufAllocator)config.getAllocator());
        allocHandle.attemptedBytesRead((int)data.writableBytes());
        boolean free = true;
        try {
            ByteBuffer nioData = data.internalNioBuffer((int)data.writerIndex(), (int)data.writableBytes());
            pos = nioData.position();
            InetSocketAddress remoteAddress = (InetSocketAddress)ch.receive((ByteBuffer)nioData);
            if (remoteAddress == null) {
                int n = 0;
                return n;
            }
            allocHandle.lastBytesRead((int)(nioData.position() - pos));
            buf.add((Object)new DatagramPacket((ByteBuf)data.writerIndex((int)(data.writerIndex() + allocHandle.lastBytesRead())), (InetSocketAddress)this.localAddress(), (InetSocketAddress)remoteAddress));
            free = false;
            int n = 1;
            return n;
        }
        catch (Throwable cause) {
            PlatformDependent.throwException((Throwable)cause);
            pos = -1;
            return pos;
        }
        finally {
            if (free) {
                data.release();
            }
        }
    }

    @Override
    protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
        SocketAddress remoteAddress;
        ByteBuf data;
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope envelope = (AddressedEnvelope)msg;
            remoteAddress = (SocketAddress)envelope.recipient();
            data = (ByteBuf)envelope.content();
        } else {
            data = (ByteBuf)msg;
            remoteAddress = null;
        }
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        ByteBuffer nioData = data.nioBufferCount() == 1 ? data.internalNioBuffer((int)data.readerIndex(), (int)dataLen) : data.nioBuffer((int)data.readerIndex(), (int)dataLen);
        int writtenBytes = remoteAddress != null ? this.javaChannel().send((ByteBuffer)nioData, remoteAddress) : this.javaChannel().write((ByteBuffer)nioData);
        if (writtenBytes <= 0) return false;
        return true;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (msg instanceof DatagramPacket) {
            DatagramPacket p = (DatagramPacket)msg;
            ByteBuf content = (ByteBuf)p.content();
            if (!NioDatagramChannel.isSingleDirectBuffer((ByteBuf)content)) return new DatagramPacket((ByteBuf)this.newDirectBuffer((ReferenceCounted)p, (ByteBuf)content), (InetSocketAddress)((InetSocketAddress)p.recipient()));
            return p;
        }
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (!NioDatagramChannel.isSingleDirectBuffer((ByteBuf)buf)) return this.newDirectBuffer((ByteBuf)buf);
            return buf;
        }
        if (!(msg instanceof AddressedEnvelope)) throw new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES));
        AddressedEnvelope e = (AddressedEnvelope)msg;
        if (!(e.content() instanceof ByteBuf)) throw new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES));
        ByteBuf content = (ByteBuf)e.content();
        if (!NioDatagramChannel.isSingleDirectBuffer((ByteBuf)content)) return new DefaultAddressedEnvelope<ByteBuf, A>(this.newDirectBuffer((ReferenceCounted)e, (ByteBuf)content), e.recipient());
        return e;
    }

    private static boolean isSingleDirectBuffer(ByteBuf buf) {
        if (!buf.isDirect()) return false;
        if (buf.nioBufferCount() != 1) return false;
        return true;
    }

    @Override
    protected boolean continueOnWriteError() {
        return true;
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
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
        catch (SocketException e) {
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        NioDatagramChannel.checkJavaVersion();
        if (multicastAddress == null) {
            throw new NullPointerException((String)"multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException((String)"networkInterface");
        }
        try {
            MembershipKey key = source == null ? this.javaChannel().join((InetAddress)multicastAddress, (NetworkInterface)networkInterface) : this.javaChannel().join((InetAddress)multicastAddress, (NetworkInterface)networkInterface, (InetAddress)source);
            NioDatagramChannel nioDatagramChannel = this;
            // MONITORENTER : nioDatagramChannel
            List<MembershipKey> keys = null;
            if (this.memberships == null) {
                this.memberships = new HashMap<InetAddress, List<MembershipKey>>();
            } else {
                keys = this.memberships.get((Object)multicastAddress);
            }
            if (keys == null) {
                keys = new ArrayList<MembershipKey>();
                this.memberships.put((InetAddress)multicastAddress, keys);
            }
            keys.add((MembershipKey)key);
            // MONITOREXIT : nioDatagramChannel
            promise.setSuccess();
            return promise;
        }
        catch (Throwable e) {
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
        catch (SocketException e) {
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        List<MembershipKey> keys;
        NioDatagramChannel.checkJavaVersion();
        if (multicastAddress == null) {
            throw new NullPointerException((String)"multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException((String)"networkInterface");
        }
        NioDatagramChannel nioDatagramChannel = this;
        // MONITORENTER : nioDatagramChannel
        if (this.memberships != null && (keys = this.memberships.get((Object)multicastAddress)) != null) {
            Iterator<MembershipKey> keyIt = keys.iterator();
            while (keyIt.hasNext()) {
                MembershipKey key = keyIt.next();
                if (!networkInterface.equals((Object)key.networkInterface()) || (source != null || key.sourceAddress() != null) && (source == null || !source.equals((Object)key.sourceAddress()))) continue;
                key.drop();
                keyIt.remove();
            }
            if (keys.isEmpty()) {
                this.memberships.remove((Object)multicastAddress);
            }
        }
        // MONITOREXIT : nioDatagramChannel
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
        return this.block((InetAddress)multicastAddress, (NetworkInterface)networkInterface, (InetAddress)sourceToBlock, (ChannelPromise)this.newPromise());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
        NioDatagramChannel.checkJavaVersion();
        if (multicastAddress == null) {
            throw new NullPointerException((String)"multicastAddress");
        }
        if (sourceToBlock == null) {
            throw new NullPointerException((String)"sourceToBlock");
        }
        if (networkInterface == null) {
            throw new NullPointerException((String)"networkInterface");
        }
        NioDatagramChannel nioDatagramChannel = this;
        // MONITORENTER : nioDatagramChannel
        if (this.memberships != null) {
            List<MembershipKey> keys = this.memberships.get((Object)multicastAddress);
            for (MembershipKey key : keys) {
                if (!networkInterface.equals((Object)key.networkInterface())) continue;
                try {
                    key.block((InetAddress)sourceToBlock);
                }
                catch (IOException e) {
                    promise.setFailure((Throwable)e);
                }
            }
        }
        // MONITOREXIT : nioDatagramChannel
        promise.setSuccess();
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
        catch (SocketException e) {
            promise.setFailure((Throwable)e);
            return promise;
        }
    }

    @Deprecated
    @Override
    protected void setReadPending(boolean readPending) {
        super.setReadPending((boolean)readPending);
    }

    void clearReadPending0() {
        this.clearReadPending();
    }

    @Override
    protected boolean closeOnReadError(Throwable cause) {
        if (!(cause instanceof SocketException)) return super.closeOnReadError((Throwable)cause);
        return false;
    }
}

