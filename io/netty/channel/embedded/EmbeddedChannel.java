/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.embedded;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.embedded.EmbeddedChannelId;
import io.netty.channel.embedded.EmbeddedEventLoop;
import io.netty.channel.embedded.EmbeddedSocketAddress;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class EmbeddedChannel
extends AbstractChannel {
    private static final SocketAddress LOCAL_ADDRESS = new EmbeddedSocketAddress();
    private static final SocketAddress REMOTE_ADDRESS = new EmbeddedSocketAddress();
    private static final ChannelHandler[] EMPTY_HANDLERS = new ChannelHandler[0];
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EmbeddedChannel.class);
    private static final ChannelMetadata METADATA_NO_DISCONNECT = new ChannelMetadata((boolean)false);
    private static final ChannelMetadata METADATA_DISCONNECT = new ChannelMetadata((boolean)true);
    private final EmbeddedEventLoop loop = new EmbeddedEventLoop();
    private final ChannelFutureListener recordExceptionListener = new ChannelFutureListener((EmbeddedChannel)this){
        final /* synthetic */ EmbeddedChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            EmbeddedChannel.access$000((EmbeddedChannel)this.this$0, (ChannelFuture)future);
        }
    };
    private final ChannelMetadata metadata;
    private final ChannelConfig config;
    private Queue<Object> inboundMessages;
    private Queue<Object> outboundMessages;
    private Throwable lastException;
    private State state;

    public EmbeddedChannel() {
        this((ChannelHandler[])EMPTY_HANDLERS);
    }

    public EmbeddedChannel(ChannelId channelId) {
        this((ChannelId)channelId, (ChannelHandler[])EMPTY_HANDLERS);
    }

    public EmbeddedChannel(ChannelHandler ... handlers) {
        this((ChannelId)EmbeddedChannelId.INSTANCE, (ChannelHandler[])handlers);
    }

    public EmbeddedChannel(boolean hasDisconnect, ChannelHandler ... handlers) {
        this((ChannelId)EmbeddedChannelId.INSTANCE, (boolean)hasDisconnect, (ChannelHandler[])handlers);
    }

    public EmbeddedChannel(boolean register, boolean hasDisconnect, ChannelHandler ... handlers) {
        this((ChannelId)EmbeddedChannelId.INSTANCE, (boolean)register, (boolean)hasDisconnect, (ChannelHandler[])handlers);
    }

    public EmbeddedChannel(ChannelId channelId, ChannelHandler ... handlers) {
        this((ChannelId)channelId, (boolean)false, (ChannelHandler[])handlers);
    }

    public EmbeddedChannel(ChannelId channelId, boolean hasDisconnect, ChannelHandler ... handlers) {
        this((ChannelId)channelId, (boolean)true, (boolean)hasDisconnect, (ChannelHandler[])handlers);
    }

    public EmbeddedChannel(ChannelId channelId, boolean register, boolean hasDisconnect, ChannelHandler ... handlers) {
        this(null, (ChannelId)channelId, (boolean)register, (boolean)hasDisconnect, (ChannelHandler[])handlers);
    }

    public EmbeddedChannel(Channel parent, ChannelId channelId, boolean register, boolean hasDisconnect, ChannelHandler ... handlers) {
        super((Channel)parent, (ChannelId)channelId);
        this.metadata = EmbeddedChannel.metadata((boolean)hasDisconnect);
        this.config = new DefaultChannelConfig((Channel)this);
        this.setup((boolean)register, (ChannelHandler[])handlers);
    }

    public EmbeddedChannel(ChannelId channelId, boolean hasDisconnect, ChannelConfig config, ChannelHandler ... handlers) {
        super(null, (ChannelId)channelId);
        this.metadata = EmbeddedChannel.metadata((boolean)hasDisconnect);
        this.config = ObjectUtil.checkNotNull(config, (String)"config");
        this.setup((boolean)true, (ChannelHandler[])handlers);
    }

    private static ChannelMetadata metadata(boolean hasDisconnect) {
        ChannelMetadata channelMetadata;
        if (hasDisconnect) {
            channelMetadata = METADATA_DISCONNECT;
            return channelMetadata;
        }
        channelMetadata = METADATA_NO_DISCONNECT;
        return channelMetadata;
    }

    private void setup(boolean register, ChannelHandler ... handlers) {
        ObjectUtil.checkNotNull(handlers, (String)"handlers");
        ChannelPipeline p = this.pipeline();
        p.addLast((ChannelHandler[])new ChannelHandler[]{new ChannelInitializer<Channel>((EmbeddedChannel)this, (ChannelHandler[])handlers){
            final /* synthetic */ ChannelHandler[] val$handlers;
            final /* synthetic */ EmbeddedChannel this$0;
            {
                this.this$0 = this$0;
                this.val$handlers = arrchannelHandler;
            }

            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                ChannelHandler[] arrchannelHandler = this.val$handlers;
                int n = arrchannelHandler.length;
                int n2 = 0;
                while (n2 < n) {
                    ChannelHandler h = arrchannelHandler[n2];
                    if (h == null) {
                        return;
                    }
                    pipeline.addLast((ChannelHandler[])new ChannelHandler[]{h});
                    ++n2;
                }
            }
        }});
        if (!register) return;
        ChannelFuture future = this.loop.register((Channel)this);
        if ($assertionsDisabled) return;
        if (future.isDone()) return;
        throw new AssertionError();
    }

    public void register() throws Exception {
        ChannelFuture future = this.loop.register((Channel)this);
        assert (future.isDone());
        Throwable cause = future.cause();
        if (cause == null) return;
        PlatformDependent.throwException((Throwable)cause);
    }

    @Override
    protected final DefaultChannelPipeline newChannelPipeline() {
        return new EmbeddedChannelPipeline((EmbeddedChannel)this, (EmbeddedChannel)this);
    }

    @Override
    public ChannelMetadata metadata() {
        return this.metadata;
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        if (this.state == State.CLOSED) return false;
        return true;
    }

    @Override
    public boolean isActive() {
        if (this.state != State.ACTIVE) return false;
        return true;
    }

    public Queue<Object> inboundMessages() {
        if (this.inboundMessages != null) return this.inboundMessages;
        this.inboundMessages = new ArrayDeque<Object>();
        return this.inboundMessages;
    }

    @Deprecated
    public Queue<Object> lastInboundBuffer() {
        return this.inboundMessages();
    }

    public Queue<Object> outboundMessages() {
        if (this.outboundMessages != null) return this.outboundMessages;
        this.outboundMessages = new ArrayDeque<Object>();
        return this.outboundMessages;
    }

    @Deprecated
    public Queue<Object> lastOutboundBuffer() {
        return this.outboundMessages();
    }

    public <T> T readInbound() {
        Object message = EmbeddedChannel.poll(this.inboundMessages);
        if (message == null) return (T)message;
        ReferenceCountUtil.touch(message, (Object)"Caller of readInbound() will handle the message from this point");
        return (T)message;
    }

    public <T> T readOutbound() {
        Object message = EmbeddedChannel.poll(this.outboundMessages);
        if (message == null) return (T)message;
        ReferenceCountUtil.touch(message, (Object)"Caller of readOutbound() will handle the message from this point.");
        return (T)message;
    }

    public boolean writeInbound(Object ... msgs) {
        this.ensureOpen();
        if (msgs.length == 0) {
            return EmbeddedChannel.isNotEmpty(this.inboundMessages);
        }
        ChannelPipeline p = this.pipeline();
        Object[] arrobject = msgs;
        int n = arrobject.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                this.flushInbound((boolean)false, (ChannelPromise)this.voidPromise());
                return EmbeddedChannel.isNotEmpty(this.inboundMessages);
            }
            Object m = arrobject[n2];
            p.fireChannelRead((Object)m);
            ++n2;
        } while (true);
    }

    public ChannelFuture writeOneInbound(Object msg) {
        return this.writeOneInbound((Object)msg, (ChannelPromise)this.newPromise());
    }

    public ChannelFuture writeOneInbound(Object msg, ChannelPromise promise) {
        if (!this.checkOpen((boolean)true)) return this.checkException((ChannelPromise)promise);
        this.pipeline().fireChannelRead((Object)msg);
        return this.checkException((ChannelPromise)promise);
    }

    public EmbeddedChannel flushInbound() {
        this.flushInbound((boolean)true, (ChannelPromise)this.voidPromise());
        return this;
    }

    private ChannelFuture flushInbound(boolean recordException, ChannelPromise promise) {
        if (!this.checkOpen((boolean)recordException)) return this.checkException((ChannelPromise)promise);
        this.pipeline().fireChannelReadComplete();
        this.runPendingTasks();
        return this.checkException((ChannelPromise)promise);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean writeOutbound(Object ... msgs) {
        this.ensureOpen();
        if (msgs.length == 0) {
            return EmbeddedChannel.isNotEmpty(this.outboundMessages);
        }
        RecyclableArrayList futures = RecyclableArrayList.newInstance((int)msgs.length);
        try {
            int i;
            for (Object m : msgs) {
                if (m == null) break;
                futures.add((Object)this.write((Object)m));
            }
            this.flushOutbound0();
            int size = futures.size();
            for (i = 0; i < size; ++i) {
                ChannelFuture future = (ChannelFuture)futures.get((int)i);
                if (future.isDone()) {
                    this.recordException((ChannelFuture)future);
                    continue;
                }
                future.addListener((GenericFutureListener<? extends Future<? super Void>>)this.recordExceptionListener);
            }
            this.checkException();
            i = (int)(EmbeddedChannel.isNotEmpty(this.outboundMessages) ? 1 : 0);
            return i != 0;
        }
        finally {
            futures.recycle();
        }
    }

    public ChannelFuture writeOneOutbound(Object msg) {
        return this.writeOneOutbound((Object)msg, (ChannelPromise)this.newPromise());
    }

    public ChannelFuture writeOneOutbound(Object msg, ChannelPromise promise) {
        if (!this.checkOpen((boolean)true)) return this.checkException((ChannelPromise)promise);
        return this.write((Object)msg, (ChannelPromise)promise);
    }

    public EmbeddedChannel flushOutbound() {
        if (this.checkOpen((boolean)true)) {
            this.flushOutbound0();
        }
        this.checkException((ChannelPromise)this.voidPromise());
        return this;
    }

    private void flushOutbound0() {
        this.runPendingTasks();
        this.flush();
    }

    public boolean finish() {
        return this.finish((boolean)false);
    }

    public boolean finishAndReleaseAll() {
        return this.finish((boolean)true);
    }

    private boolean finish(boolean releaseAll) {
        this.close();
        try {
            this.checkException();
            boolean bl = EmbeddedChannel.isNotEmpty(this.inboundMessages) || EmbeddedChannel.isNotEmpty(this.outboundMessages);
            return bl;
        }
        finally {
            if (releaseAll) {
                EmbeddedChannel.releaseAll(this.inboundMessages);
                EmbeddedChannel.releaseAll(this.outboundMessages);
            }
        }
    }

    public boolean releaseInbound() {
        return EmbeddedChannel.releaseAll(this.inboundMessages);
    }

    public boolean releaseOutbound() {
        return EmbeddedChannel.releaseAll(this.outboundMessages);
    }

    private static boolean releaseAll(Queue<Object> queue) {
        if (!EmbeddedChannel.isNotEmpty(queue)) return false;
        Object msg;
        while ((msg = queue.poll()) != null) {
            ReferenceCountUtil.release((Object)msg);
        }
        return true;
    }

    private void finishPendingTasks(boolean cancel) {
        this.runPendingTasks();
        if (!cancel) return;
        this.loop.cancelScheduledTasks();
    }

    @Override
    public final ChannelFuture close() {
        return this.close((ChannelPromise)this.newPromise());
    }

    @Override
    public final ChannelFuture disconnect() {
        return this.disconnect((ChannelPromise)this.newPromise());
    }

    @Override
    public final ChannelFuture close(ChannelPromise promise) {
        this.runPendingTasks();
        ChannelFuture future = super.close((ChannelPromise)promise);
        this.finishPendingTasks((boolean)true);
        return future;
    }

    @Override
    public final ChannelFuture disconnect(ChannelPromise promise) {
        ChannelFuture future = super.disconnect((ChannelPromise)promise);
        this.finishPendingTasks((boolean)(!this.metadata.hasDisconnect()));
        return future;
    }

    private static boolean isNotEmpty(Queue<Object> queue) {
        if (queue == null) return false;
        if (queue.isEmpty()) return false;
        return true;
    }

    private static Object poll(Queue<Object> queue) {
        if (queue == null) return null;
        Object object = queue.poll();
        return object;
    }

    public void runPendingTasks() {
        try {
            this.loop.runTasks();
        }
        catch (Exception e) {
            this.recordException((Throwable)e);
        }
        try {
            this.loop.runScheduledTasks();
            return;
        }
        catch (Exception e) {
            this.recordException((Throwable)e);
        }
    }

    public long runScheduledPendingTasks() {
        try {
            return this.loop.runScheduledTasks();
        }
        catch (Exception e) {
            this.recordException((Throwable)e);
            return this.loop.nextScheduledTask();
        }
    }

    private void recordException(ChannelFuture future) {
        if (future.isSuccess()) return;
        this.recordException((Throwable)future.cause());
    }

    private void recordException(Throwable cause) {
        if (this.lastException == null) {
            this.lastException = cause;
            return;
        }
        logger.warn((String)"More than one exception was raised. Will report only the first one and log others.", (Throwable)cause);
    }

    private ChannelFuture checkException(ChannelPromise promise) {
        Throwable t = this.lastException;
        if (t == null) return promise.setSuccess();
        this.lastException = null;
        if (!promise.isVoid()) return promise.setFailure((Throwable)t);
        PlatformDependent.throwException((Throwable)t);
        return promise.setFailure((Throwable)t);
    }

    public void checkException() {
        this.checkException((ChannelPromise)this.voidPromise());
    }

    private boolean checkOpen(boolean recordException) {
        if (this.isOpen()) return true;
        if (!recordException) return false;
        this.recordException((Throwable)new ClosedChannelException());
        return false;
    }

    protected final void ensureOpen() {
        if (this.checkOpen((boolean)true)) return;
        this.checkException();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EmbeddedEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        if (!this.isActive()) return null;
        SocketAddress socketAddress = LOCAL_ADDRESS;
        return socketAddress;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        if (!this.isActive()) return null;
        SocketAddress socketAddress = REMOTE_ADDRESS;
        return socketAddress;
    }

    @Override
    protected void doRegister() throws Exception {
        this.state = State.ACTIVE;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
    }

    @Override
    protected void doDisconnect() throws Exception {
        if (this.metadata.hasDisconnect()) return;
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.state = State.CLOSED;
    }

    @Override
    protected void doBeginRead() throws Exception {
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new EmbeddedUnsafe((EmbeddedChannel)this, null);
    }

    @Override
    public Channel.Unsafe unsafe() {
        return ((EmbeddedUnsafe)super.unsafe()).wrapped;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        while ((msg = in.current()) != null) {
            ReferenceCountUtil.retain(msg);
            this.handleOutboundMessage((Object)msg);
            in.remove();
        }
        return;
    }

    protected void handleOutboundMessage(Object msg) {
        this.outboundMessages().add((Object)msg);
    }

    protected void handleInboundMessage(Object msg) {
        this.inboundMessages().add((Object)msg);
    }

    static /* synthetic */ void access$000(EmbeddedChannel x0, ChannelFuture x1) {
        x0.recordException((ChannelFuture)x1);
    }

    static /* synthetic */ void access$200(EmbeddedChannel x0, Throwable x1) {
        x0.recordException((Throwable)x1);
    }
}

