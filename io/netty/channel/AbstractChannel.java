/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelId;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;

public abstract class AbstractChannel
extends DefaultAttributeMap
implements Channel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannel.class);
    private final Channel parent;
    private final ChannelId id;
    private final Channel.Unsafe unsafe;
    private final DefaultChannelPipeline pipeline;
    private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise((Channel)this, (boolean)false);
    private final CloseFuture closeFuture = new CloseFuture((AbstractChannel)this);
    private volatile SocketAddress localAddress;
    private volatile SocketAddress remoteAddress;
    private volatile EventLoop eventLoop;
    private volatile boolean registered;
    private boolean closeInitiated;
    private Throwable initialCloseCause;
    private boolean strValActive;
    private String strVal;

    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        this.id = this.newId();
        this.unsafe = this.newUnsafe();
        this.pipeline = this.newChannelPipeline();
    }

    protected AbstractChannel(Channel parent, ChannelId id) {
        this.parent = parent;
        this.id = id;
        this.unsafe = this.newUnsafe();
        this.pipeline = this.newChannelPipeline();
    }

    @Override
    public final ChannelId id() {
        return this.id;
    }

    protected ChannelId newId() {
        return DefaultChannelId.newInstance();
    }

    protected DefaultChannelPipeline newChannelPipeline() {
        return new DefaultChannelPipeline((Channel)this);
    }

    @Override
    public boolean isWritable() {
        ChannelOutboundBuffer buf = this.unsafe.outboundBuffer();
        if (buf == null) return false;
        if (!buf.isWritable()) return false;
        return true;
    }

    @Override
    public long bytesBeforeUnwritable() {
        ChannelOutboundBuffer buf = this.unsafe.outboundBuffer();
        if (buf == null) return 0L;
        long l = buf.bytesBeforeUnwritable();
        return l;
    }

    @Override
    public long bytesBeforeWritable() {
        ChannelOutboundBuffer buf = this.unsafe.outboundBuffer();
        if (buf == null) return Long.MAX_VALUE;
        long l = buf.bytesBeforeWritable();
        return l;
    }

    @Override
    public Channel parent() {
        return this.parent;
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.config().getAllocator();
    }

    @Override
    public EventLoop eventLoop() {
        EventLoop eventLoop = this.eventLoop;
        if (eventLoop != null) return eventLoop;
        throw new IllegalStateException((String)"channel not registered to an event loop");
    }

    @Override
    public SocketAddress localAddress() {
        SocketAddress localAddress = this.localAddress;
        if (localAddress != null) return localAddress;
        try {
            this.localAddress = localAddress = this.unsafe().localAddress();
            return localAddress;
        }
        catch (Error e) {
            throw e;
        }
        catch (Throwable t) {
            return null;
        }
    }

    @Deprecated
    protected void invalidateLocalAddress() {
        this.localAddress = null;
    }

    @Override
    public SocketAddress remoteAddress() {
        SocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress != null) return remoteAddress;
        try {
            this.remoteAddress = remoteAddress = this.unsafe().remoteAddress();
            return remoteAddress;
        }
        catch (Error e) {
            throw e;
        }
        catch (Throwable t) {
            return null;
        }
    }

    @Deprecated
    protected void invalidateRemoteAddress() {
        this.remoteAddress = null;
    }

    @Override
    public boolean isRegistered() {
        return this.registered;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return this.pipeline.bind((SocketAddress)localAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.pipeline.connect((SocketAddress)remoteAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.pipeline.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.pipeline.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.pipeline.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.pipeline.deregister();
    }

    @Override
    public Channel flush() {
        this.pipeline.flush();
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline.bind((SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.pipeline.connect((SocketAddress)remoteAddress, (ChannelPromise)promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return this.pipeline.disconnect((ChannelPromise)promise);
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return this.pipeline.close((ChannelPromise)promise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        return this.pipeline.deregister((ChannelPromise)promise);
    }

    @Override
    public Channel read() {
        this.pipeline.read();
        return this;
    }

    @Override
    public ChannelFuture write(Object msg) {
        return this.pipeline.write((Object)msg);
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.pipeline.write((Object)msg, (ChannelPromise)promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.pipeline.writeAndFlush((Object)msg);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.pipeline.writeAndFlush((Object)msg, (ChannelPromise)promise);
    }

    @Override
    public ChannelPromise newPromise() {
        return this.pipeline.newPromise();
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return this.pipeline.newProgressivePromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return this.pipeline.newSucceededFuture();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return this.pipeline.newFailedFuture((Throwable)cause);
    }

    @Override
    public ChannelFuture closeFuture() {
        return this.closeFuture;
    }

    @Override
    public Channel.Unsafe unsafe() {
        return this.unsafe;
    }

    protected abstract AbstractUnsafe newUnsafe();

    public final int hashCode() {
        return this.id.hashCode();
    }

    public final boolean equals(Object o) {
        if (this != o) return false;
        return true;
    }

    @Override
    public final int compareTo(Channel o) {
        if (this != o) return this.id().compareTo(o.id());
        return 0;
    }

    public String toString() {
        boolean active = this.isActive();
        if (this.strValActive == active && this.strVal != null) {
            return this.strVal;
        }
        SocketAddress remoteAddr = this.remoteAddress();
        SocketAddress localAddr = this.localAddress();
        if (remoteAddr != null) {
            StringBuilder buf = new StringBuilder((int)96).append((String)"[id: 0x").append((String)this.id.asShortText()).append((String)", L:").append((Object)localAddr).append((String)(active ? " - " : " ! ")).append((String)"R:").append((Object)remoteAddr).append((char)']');
            this.strVal = buf.toString();
        } else if (localAddr != null) {
            StringBuilder buf = new StringBuilder((int)64).append((String)"[id: 0x").append((String)this.id.asShortText()).append((String)", L:").append((Object)localAddr).append((char)']');
            this.strVal = buf.toString();
        } else {
            StringBuilder buf = new StringBuilder((int)16).append((String)"[id: 0x").append((String)this.id.asShortText()).append((char)']');
            this.strVal = buf.toString();
        }
        this.strValActive = active;
        return this.strVal;
    }

    @Override
    public final ChannelPromise voidPromise() {
        return this.pipeline.voidPromise();
    }

    protected abstract boolean isCompatible(EventLoop var1);

    protected abstract SocketAddress localAddress0();

    protected abstract SocketAddress remoteAddress0();

    protected void doRegister() throws Exception {
    }

    protected abstract void doBind(SocketAddress var1) throws Exception;

    protected abstract void doDisconnect() throws Exception;

    protected abstract void doClose() throws Exception;

    protected void doShutdownOutput() throws Exception {
        this.doClose();
    }

    protected void doDeregister() throws Exception {
    }

    protected abstract void doBeginRead() throws Exception;

    protected abstract void doWrite(ChannelOutboundBuffer var1) throws Exception;

    protected Object filterOutboundMessage(Object msg) throws Exception {
        return msg;
    }

    protected void validateFileRegion(DefaultFileRegion region, long position) throws IOException {
        DefaultFileRegion.validate((DefaultFileRegion)region, (long)position);
    }

    static /* synthetic */ boolean access$000(AbstractChannel x0) {
        return x0.registered;
    }

    static /* synthetic */ EventLoop access$100(AbstractChannel x0) {
        return x0.eventLoop;
    }

    static /* synthetic */ EventLoop access$102(AbstractChannel x0, EventLoop x1) {
        x0.eventLoop = x1;
        return x0.eventLoop;
    }

    static /* synthetic */ InternalLogger access$300() {
        return logger;
    }

    static /* synthetic */ CloseFuture access$400(AbstractChannel x0) {
        return x0.closeFuture;
    }

    static /* synthetic */ boolean access$002(AbstractChannel x0, boolean x1) {
        x0.registered = x1;
        return x0.registered;
    }

    static /* synthetic */ DefaultChannelPipeline access$500(AbstractChannel x0) {
        return x0.pipeline;
    }

    static /* synthetic */ SocketAddress access$602(AbstractChannel x0, SocketAddress x1) {
        x0.remoteAddress = x1;
        return x0.remoteAddress;
    }

    static /* synthetic */ SocketAddress access$702(AbstractChannel x0, SocketAddress x1) {
        x0.localAddress = x1;
        return x0.localAddress;
    }

    static /* synthetic */ boolean access$900(AbstractChannel x0) {
        return x0.closeInitiated;
    }

    static /* synthetic */ boolean access$902(AbstractChannel x0, boolean x1) {
        x0.closeInitiated = x1;
        return x0.closeInitiated;
    }

    static /* synthetic */ Throwable access$1300(AbstractChannel x0) {
        return x0.initialCloseCause;
    }

    static /* synthetic */ Throwable access$1302(AbstractChannel x0, Throwable x1) {
        x0.initialCloseCause = x1;
        return x0.initialCloseCause;
    }

    static /* synthetic */ VoidChannelPromise access$1400(AbstractChannel x0) {
        return x0.unsafeVoidPromise;
    }
}

