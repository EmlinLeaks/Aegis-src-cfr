/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.AbstractChannelHandlerContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerMask;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundInvoker;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.DefaultChannelProgressivePromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.FailedChannelFuture;
import io.netty.channel.SucceededChannelFuture;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakHint;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractChannelHandlerContext
implements ChannelHandlerContext,
ResourceLeakHint {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);
    volatile AbstractChannelHandlerContext next;
    volatile AbstractChannelHandlerContext prev;
    private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractChannelHandlerContext.class, (String)"handlerState");
    private static final int ADD_PENDING = 1;
    private static final int ADD_COMPLETE = 2;
    private static final int REMOVE_COMPLETE = 3;
    private static final int INIT = 0;
    private final DefaultChannelPipeline pipeline;
    private final String name;
    private final boolean ordered;
    private final int executionMask;
    final EventExecutor executor;
    private ChannelFuture succeededFuture;
    private Tasks invokeTasks;
    private volatile int handlerState = 0;

    AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name, Class<? extends ChannelHandler> handlerClass) {
        this.name = ObjectUtil.checkNotNull(name, (String)"name");
        this.pipeline = pipeline;
        this.executor = executor;
        this.executionMask = ChannelHandlerMask.mask(handlerClass);
        this.ordered = executor == null || executor instanceof OrderedEventExecutor;
    }

    @Override
    public Channel channel() {
        return this.pipeline.channel();
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.channel().config().getAllocator();
    }

    @Override
    public EventExecutor executor() {
        if (this.executor != null) return this.executor;
        return this.channel().eventLoop();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        AbstractChannelHandlerContext.invokeChannelRegistered((AbstractChannelHandlerContext)this.findContextInbound((int)2));
        return this;
    }

    static void invokeChannelRegistered(AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRegistered();
            return;
        }
        executor.execute((Runnable)new Runnable((AbstractChannelHandlerContext)next){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            {
                this.val$next = abstractChannelHandlerContext;
            }

            public void run() {
                AbstractChannelHandlerContext.access$000((AbstractChannelHandlerContext)this.val$next);
            }
        });
    }

    private void invokeChannelRegistered() {
        if (!this.invokeHandler()) {
            this.fireChannelRegistered();
            return;
        }
        try {
            ((ChannelInboundHandler)this.handler()).channelRegistered((ChannelHandlerContext)this);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelHandlerContext fireChannelUnregistered() {
        AbstractChannelHandlerContext.invokeChannelUnregistered((AbstractChannelHandlerContext)this.findContextInbound((int)4));
        return this;
    }

    static void invokeChannelUnregistered(AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelUnregistered();
            return;
        }
        executor.execute((Runnable)new Runnable((AbstractChannelHandlerContext)next){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            {
                this.val$next = abstractChannelHandlerContext;
            }

            public void run() {
                AbstractChannelHandlerContext.access$100((AbstractChannelHandlerContext)this.val$next);
            }
        });
    }

    private void invokeChannelUnregistered() {
        if (!this.invokeHandler()) {
            this.fireChannelUnregistered();
            return;
        }
        try {
            ((ChannelInboundHandler)this.handler()).channelUnregistered((ChannelHandlerContext)this);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelHandlerContext fireChannelActive() {
        AbstractChannelHandlerContext.invokeChannelActive((AbstractChannelHandlerContext)this.findContextInbound((int)8));
        return this;
    }

    static void invokeChannelActive(AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelActive();
            return;
        }
        executor.execute((Runnable)new Runnable((AbstractChannelHandlerContext)next){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            {
                this.val$next = abstractChannelHandlerContext;
            }

            public void run() {
                AbstractChannelHandlerContext.access$200((AbstractChannelHandlerContext)this.val$next);
            }
        });
    }

    private void invokeChannelActive() {
        if (!this.invokeHandler()) {
            this.fireChannelActive();
            return;
        }
        try {
            ((ChannelInboundHandler)this.handler()).channelActive((ChannelHandlerContext)this);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelHandlerContext fireChannelInactive() {
        AbstractChannelHandlerContext.invokeChannelInactive((AbstractChannelHandlerContext)this.findContextInbound((int)16));
        return this;
    }

    static void invokeChannelInactive(AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelInactive();
            return;
        }
        executor.execute((Runnable)new Runnable((AbstractChannelHandlerContext)next){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            {
                this.val$next = abstractChannelHandlerContext;
            }

            public void run() {
                AbstractChannelHandlerContext.access$300((AbstractChannelHandlerContext)this.val$next);
            }
        });
    }

    private void invokeChannelInactive() {
        if (!this.invokeHandler()) {
            this.fireChannelInactive();
            return;
        }
        try {
            ((ChannelInboundHandler)this.handler()).channelInactive((ChannelHandlerContext)this);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
        AbstractChannelHandlerContext.invokeExceptionCaught((AbstractChannelHandlerContext)this.findContextInbound((int)1), (Throwable)cause);
        return this;
    }

    static void invokeExceptionCaught(AbstractChannelHandlerContext next, Throwable cause) {
        ObjectUtil.checkNotNull(cause, (String)"cause");
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeExceptionCaught((Throwable)cause);
            return;
        }
        try {
            executor.execute((Runnable)new Runnable((AbstractChannelHandlerContext)next, (Throwable)cause){
                final /* synthetic */ AbstractChannelHandlerContext val$next;
                final /* synthetic */ Throwable val$cause;
                {
                    this.val$next = abstractChannelHandlerContext;
                    this.val$cause = throwable;
                }

                public void run() {
                    AbstractChannelHandlerContext.access$400((AbstractChannelHandlerContext)this.val$next, (Throwable)this.val$cause);
                }
            });
            return;
        }
        catch (Throwable t) {
            if (!logger.isWarnEnabled()) return;
            logger.warn((String)"Failed to submit an exceptionCaught() event.", (Throwable)t);
            logger.warn((String)"The exceptionCaught() event that was failed to submit was:", (Throwable)cause);
        }
    }

    private void invokeExceptionCaught(Throwable cause) {
        if (!this.invokeHandler()) {
            this.fireExceptionCaught((Throwable)cause);
            return;
        }
        try {
            this.handler().exceptionCaught((ChannelHandlerContext)this, (Throwable)cause);
            return;
        }
        catch (Throwable error) {
            if (logger.isDebugEnabled()) {
                logger.debug((String)"An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", (Object)ThrowableUtil.stackTraceToString((Throwable)error), (Object)cause);
                return;
            }
            if (!logger.isWarnEnabled()) return;
            logger.warn((String)"An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", (Object)error, (Object)cause);
            return;
        }
    }

    @Override
    public ChannelHandlerContext fireUserEventTriggered(Object event) {
        AbstractChannelHandlerContext.invokeUserEventTriggered((AbstractChannelHandlerContext)this.findContextInbound((int)128), (Object)event);
        return this;
    }

    static void invokeUserEventTriggered(AbstractChannelHandlerContext next, Object event) {
        ObjectUtil.checkNotNull(event, (String)"event");
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeUserEventTriggered((Object)event);
            return;
        }
        executor.execute((Runnable)new Runnable((AbstractChannelHandlerContext)next, (Object)event){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            final /* synthetic */ Object val$event;
            {
                this.val$next = abstractChannelHandlerContext;
                this.val$event = object;
            }

            public void run() {
                AbstractChannelHandlerContext.access$500((AbstractChannelHandlerContext)this.val$next, (Object)this.val$event);
            }
        });
    }

    private void invokeUserEventTriggered(Object event) {
        if (!this.invokeHandler()) {
            this.fireUserEventTriggered((Object)event);
            return;
        }
        try {
            ((ChannelInboundHandler)this.handler()).userEventTriggered((ChannelHandlerContext)this, (Object)event);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelHandlerContext fireChannelRead(Object msg) {
        AbstractChannelHandlerContext.invokeChannelRead((AbstractChannelHandlerContext)this.findContextInbound((int)32), (Object)msg);
        return this;
    }

    static void invokeChannelRead(AbstractChannelHandlerContext next, Object msg) {
        Object m = next.pipeline.touch((Object)ObjectUtil.checkNotNull(msg, (String)"msg"), (AbstractChannelHandlerContext)next);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRead((Object)m);
            return;
        }
        executor.execute((Runnable)new Runnable((AbstractChannelHandlerContext)next, (Object)m){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            final /* synthetic */ Object val$m;
            {
                this.val$next = abstractChannelHandlerContext;
                this.val$m = object;
            }

            public void run() {
                AbstractChannelHandlerContext.access$600((AbstractChannelHandlerContext)this.val$next, (Object)this.val$m);
            }
        });
    }

    private void invokeChannelRead(Object msg) {
        if (!this.invokeHandler()) {
            this.fireChannelRead((Object)msg);
            return;
        }
        try {
            ((ChannelInboundHandler)this.handler()).channelRead((ChannelHandlerContext)this, (Object)msg);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelHandlerContext fireChannelReadComplete() {
        AbstractChannelHandlerContext.invokeChannelReadComplete((AbstractChannelHandlerContext)this.findContextInbound((int)64));
        return this;
    }

    static void invokeChannelReadComplete(AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelReadComplete();
            return;
        }
        Tasks tasks = next.invokeTasks;
        if (tasks == null) {
            next.invokeTasks = tasks = new Tasks((AbstractChannelHandlerContext)next);
        }
        executor.execute((Runnable)((Tasks)tasks).invokeChannelReadCompleteTask);
    }

    private void invokeChannelReadComplete() {
        if (!this.invokeHandler()) {
            this.fireChannelReadComplete();
            return;
        }
        try {
            ((ChannelInboundHandler)this.handler()).channelReadComplete((ChannelHandlerContext)this);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged() {
        AbstractChannelHandlerContext.invokeChannelWritabilityChanged((AbstractChannelHandlerContext)this.findContextInbound((int)256));
        return this;
    }

    static void invokeChannelWritabilityChanged(AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelWritabilityChanged();
            return;
        }
        Tasks tasks = next.invokeTasks;
        if (tasks == null) {
            next.invokeTasks = tasks = new Tasks((AbstractChannelHandlerContext)next);
        }
        executor.execute((Runnable)((Tasks)tasks).invokeChannelWritableStateChangedTask);
    }

    private void invokeChannelWritabilityChanged() {
        if (!this.invokeHandler()) {
            this.fireChannelWritabilityChanged();
            return;
        }
        try {
            ((ChannelInboundHandler)this.handler()).channelWritabilityChanged((ChannelHandlerContext)this);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return this.bind((SocketAddress)localAddress, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.connect((SocketAddress)remoteAddress, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture disconnect() {
        return this.disconnect((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture close() {
        return this.close((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture deregister() {
        return this.deregister((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        if (localAddress == null) {
            throw new NullPointerException((String)"localAddress");
        }
        if (this.isNotValidPromise((ChannelPromise)promise, (boolean)false)) {
            return promise;
        }
        AbstractChannelHandlerContext next = this.findContextOutbound((int)512);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeBind((SocketAddress)localAddress, (ChannelPromise)promise);
            return promise;
        }
        AbstractChannelHandlerContext.safeExecute((EventExecutor)executor, (Runnable)new Runnable((AbstractChannelHandlerContext)this, (AbstractChannelHandlerContext)next, (SocketAddress)localAddress, (ChannelPromise)promise){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            final /* synthetic */ SocketAddress val$localAddress;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractChannelHandlerContext this$0;
            {
                this.this$0 = this$0;
                this.val$next = abstractChannelHandlerContext;
                this.val$localAddress = socketAddress;
                this.val$promise = channelPromise;
            }

            public void run() {
                AbstractChannelHandlerContext.access$900((AbstractChannelHandlerContext)this.val$next, (SocketAddress)this.val$localAddress, (ChannelPromise)this.val$promise);
            }
        }, (ChannelPromise)promise, null);
        return promise;
    }

    private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
        if (!this.invokeHandler()) {
            this.bind((SocketAddress)localAddress, (ChannelPromise)promise);
            return;
        }
        try {
            ((ChannelOutboundHandler)this.handler()).bind((ChannelHandlerContext)this, (SocketAddress)localAddress, (ChannelPromise)promise);
            return;
        }
        catch (Throwable t) {
            AbstractChannelHandlerContext.notifyOutboundHandlerException((Throwable)t, (ChannelPromise)promise);
            return;
        }
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.connect((SocketAddress)remoteAddress, null, (ChannelPromise)promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        if (remoteAddress == null) {
            throw new NullPointerException((String)"remoteAddress");
        }
        if (this.isNotValidPromise((ChannelPromise)promise, (boolean)false)) {
            return promise;
        }
        AbstractChannelHandlerContext next = this.findContextOutbound((int)1024);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeConnect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
            return promise;
        }
        AbstractChannelHandlerContext.safeExecute((EventExecutor)executor, (Runnable)new Runnable((AbstractChannelHandlerContext)this, (AbstractChannelHandlerContext)next, (SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            final /* synthetic */ SocketAddress val$remoteAddress;
            final /* synthetic */ SocketAddress val$localAddress;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractChannelHandlerContext this$0;
            {
                this.this$0 = this$0;
                this.val$next = abstractChannelHandlerContext;
                this.val$remoteAddress = socketAddress;
                this.val$localAddress = socketAddress2;
                this.val$promise = channelPromise;
            }

            public void run() {
                AbstractChannelHandlerContext.access$1000((AbstractChannelHandlerContext)this.val$next, (SocketAddress)this.val$remoteAddress, (SocketAddress)this.val$localAddress, (ChannelPromise)this.val$promise);
            }
        }, (ChannelPromise)promise, null);
        return promise;
    }

    private void invokeConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        if (!this.invokeHandler()) {
            this.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
            return;
        }
        try {
            ((ChannelOutboundHandler)this.handler()).connect((ChannelHandlerContext)this, (SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
            return;
        }
        catch (Throwable t) {
            AbstractChannelHandlerContext.notifyOutboundHandlerException((Throwable)t, (ChannelPromise)promise);
            return;
        }
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        if (!this.channel().metadata().hasDisconnect()) {
            return this.close((ChannelPromise)promise);
        }
        if (this.isNotValidPromise((ChannelPromise)promise, (boolean)false)) {
            return promise;
        }
        AbstractChannelHandlerContext next = this.findContextOutbound((int)2048);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeDisconnect((ChannelPromise)promise);
            return promise;
        }
        AbstractChannelHandlerContext.safeExecute((EventExecutor)executor, (Runnable)new Runnable((AbstractChannelHandlerContext)this, (AbstractChannelHandlerContext)next, (ChannelPromise)promise){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractChannelHandlerContext this$0;
            {
                this.this$0 = this$0;
                this.val$next = abstractChannelHandlerContext;
                this.val$promise = channelPromise;
            }

            public void run() {
                AbstractChannelHandlerContext.access$1100((AbstractChannelHandlerContext)this.val$next, (ChannelPromise)this.val$promise);
            }
        }, (ChannelPromise)promise, null);
        return promise;
    }

    private void invokeDisconnect(ChannelPromise promise) {
        if (!this.invokeHandler()) {
            this.disconnect((ChannelPromise)promise);
            return;
        }
        try {
            ((ChannelOutboundHandler)this.handler()).disconnect((ChannelHandlerContext)this, (ChannelPromise)promise);
            return;
        }
        catch (Throwable t) {
            AbstractChannelHandlerContext.notifyOutboundHandlerException((Throwable)t, (ChannelPromise)promise);
            return;
        }
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        if (this.isNotValidPromise((ChannelPromise)promise, (boolean)false)) {
            return promise;
        }
        AbstractChannelHandlerContext next = this.findContextOutbound((int)4096);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeClose((ChannelPromise)promise);
            return promise;
        }
        AbstractChannelHandlerContext.safeExecute((EventExecutor)executor, (Runnable)new Runnable((AbstractChannelHandlerContext)this, (AbstractChannelHandlerContext)next, (ChannelPromise)promise){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractChannelHandlerContext this$0;
            {
                this.this$0 = this$0;
                this.val$next = abstractChannelHandlerContext;
                this.val$promise = channelPromise;
            }

            public void run() {
                AbstractChannelHandlerContext.access$1200((AbstractChannelHandlerContext)this.val$next, (ChannelPromise)this.val$promise);
            }
        }, (ChannelPromise)promise, null);
        return promise;
    }

    private void invokeClose(ChannelPromise promise) {
        if (!this.invokeHandler()) {
            this.close((ChannelPromise)promise);
            return;
        }
        try {
            ((ChannelOutboundHandler)this.handler()).close((ChannelHandlerContext)this, (ChannelPromise)promise);
            return;
        }
        catch (Throwable t) {
            AbstractChannelHandlerContext.notifyOutboundHandlerException((Throwable)t, (ChannelPromise)promise);
            return;
        }
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        if (this.isNotValidPromise((ChannelPromise)promise, (boolean)false)) {
            return promise;
        }
        AbstractChannelHandlerContext next = this.findContextOutbound((int)8192);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeDeregister((ChannelPromise)promise);
            return promise;
        }
        AbstractChannelHandlerContext.safeExecute((EventExecutor)executor, (Runnable)new Runnable((AbstractChannelHandlerContext)this, (AbstractChannelHandlerContext)next, (ChannelPromise)promise){
            final /* synthetic */ AbstractChannelHandlerContext val$next;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractChannelHandlerContext this$0;
            {
                this.this$0 = this$0;
                this.val$next = abstractChannelHandlerContext;
                this.val$promise = channelPromise;
            }

            public void run() {
                AbstractChannelHandlerContext.access$1300((AbstractChannelHandlerContext)this.val$next, (ChannelPromise)this.val$promise);
            }
        }, (ChannelPromise)promise, null);
        return promise;
    }

    private void invokeDeregister(ChannelPromise promise) {
        if (!this.invokeHandler()) {
            this.deregister((ChannelPromise)promise);
            return;
        }
        try {
            ((ChannelOutboundHandler)this.handler()).deregister((ChannelHandlerContext)this, (ChannelPromise)promise);
            return;
        }
        catch (Throwable t) {
            AbstractChannelHandlerContext.notifyOutboundHandlerException((Throwable)t, (ChannelPromise)promise);
            return;
        }
    }

    @Override
    public ChannelHandlerContext read() {
        AbstractChannelHandlerContext next = this.findContextOutbound((int)16384);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeRead();
            return this;
        }
        Tasks tasks = next.invokeTasks;
        if (tasks == null) {
            next.invokeTasks = tasks = new Tasks((AbstractChannelHandlerContext)next);
        }
        executor.execute((Runnable)((Tasks)tasks).invokeReadTask);
        return this;
    }

    private void invokeRead() {
        if (!this.invokeHandler()) {
            this.read();
            return;
        }
        try {
            ((ChannelOutboundHandler)this.handler()).read((ChannelHandlerContext)this);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
            return;
        }
    }

    @Override
    public ChannelFuture write(Object msg) {
        return this.write((Object)msg, (ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        this.write((Object)msg, (boolean)false, (ChannelPromise)promise);
        return promise;
    }

    private void invokeWrite(Object msg, ChannelPromise promise) {
        if (this.invokeHandler()) {
            this.invokeWrite0((Object)msg, (ChannelPromise)promise);
            return;
        }
        this.write((Object)msg, (ChannelPromise)promise);
    }

    private void invokeWrite0(Object msg, ChannelPromise promise) {
        try {
            ((ChannelOutboundHandler)this.handler()).write((ChannelHandlerContext)this, (Object)msg, (ChannelPromise)promise);
            return;
        }
        catch (Throwable t) {
            AbstractChannelHandlerContext.notifyOutboundHandlerException((Throwable)t, (ChannelPromise)promise);
        }
    }

    @Override
    public ChannelHandlerContext flush() {
        AbstractChannelHandlerContext next = this.findContextOutbound((int)65536);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeFlush();
            return this;
        }
        Tasks tasks = next.invokeTasks;
        if (tasks == null) {
            next.invokeTasks = tasks = new Tasks((AbstractChannelHandlerContext)next);
        }
        AbstractChannelHandlerContext.safeExecute((EventExecutor)executor, (Runnable)((Tasks)tasks).invokeFlushTask, (ChannelPromise)this.channel().voidPromise(), null);
        return this;
    }

    private void invokeFlush() {
        if (this.invokeHandler()) {
            this.invokeFlush0();
            return;
        }
        this.flush();
    }

    private void invokeFlush0() {
        try {
            ((ChannelOutboundHandler)this.handler()).flush((ChannelHandlerContext)this);
            return;
        }
        catch (Throwable t) {
            this.notifyHandlerException((Throwable)t);
        }
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        this.write((Object)msg, (boolean)true, (ChannelPromise)promise);
        return promise;
    }

    private void invokeWriteAndFlush(Object msg, ChannelPromise promise) {
        if (this.invokeHandler()) {
            this.invokeWrite0((Object)msg, (ChannelPromise)promise);
            this.invokeFlush0();
            return;
        }
        this.writeAndFlush((Object)msg, (ChannelPromise)promise);
    }

    private void write(Object msg, boolean flush, ChannelPromise promise) {
        ObjectUtil.checkNotNull(msg, (String)"msg");
        try {
            if (this.isNotValidPromise((ChannelPromise)promise, (boolean)true)) {
                ReferenceCountUtil.release((Object)msg);
                return;
            }
        }
        catch (RuntimeException e) {
            ReferenceCountUtil.release((Object)msg);
            throw e;
        }
        AbstractChannelHandlerContext next = this.findContextOutbound((int)(flush ? 98304 : 32768));
        Object m = this.pipeline.touch((Object)msg, (AbstractChannelHandlerContext)next);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            if (flush) {
                next.invokeWriteAndFlush((Object)m, (ChannelPromise)promise);
                return;
            }
            next.invokeWrite((Object)m, (ChannelPromise)promise);
            return;
        }
        AbstractWriteTask task = flush ? WriteAndFlushTask.newInstance((AbstractChannelHandlerContext)next, (Object)m, (ChannelPromise)promise) : WriteTask.newInstance((AbstractChannelHandlerContext)next, (Object)m, (ChannelPromise)promise);
        if (AbstractChannelHandlerContext.safeExecute((EventExecutor)executor, (Runnable)task, (ChannelPromise)promise, (Object)m)) return;
        task.cancel();
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.writeAndFlush((Object)msg, (ChannelPromise)this.newPromise());
    }

    private static void notifyOutboundHandlerException(Throwable cause, ChannelPromise promise) {
        PromiseNotificationUtil.tryFailure(promise, (Throwable)cause, (InternalLogger)(promise instanceof VoidChannelPromise ? null : logger));
    }

    private void notifyHandlerException(Throwable cause) {
        if (AbstractChannelHandlerContext.inExceptionCaught((Throwable)cause)) {
            if (!logger.isWarnEnabled()) return;
            logger.warn((String)"An exception was thrown by a user handler while handling an exceptionCaught event", (Throwable)cause);
            return;
        }
        this.invokeExceptionCaught((Throwable)cause);
    }

    private static boolean inExceptionCaught(Throwable cause) {
        do {
            StackTraceElement[] trace;
            if ((trace = cause.getStackTrace()) == null) continue;
            for (StackTraceElement t : trace) {
                if (t == null) break;
                if (!"exceptionCaught".equals((Object)t.getMethodName())) continue;
                return true;
            }
        } while ((cause = cause.getCause()) != null);
        return false;
    }

    @Override
    public ChannelPromise newPromise() {
        return new DefaultChannelPromise((Channel)this.channel(), (EventExecutor)this.executor());
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise((Channel)this.channel(), (EventExecutor)this.executor());
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        ChannelFuture succeededFuture = this.succeededFuture;
        if (succeededFuture != null) return succeededFuture;
        this.succeededFuture = succeededFuture = new SucceededChannelFuture((Channel)this.channel(), (EventExecutor)this.executor());
        return succeededFuture;
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return new FailedChannelFuture((Channel)this.channel(), (EventExecutor)this.executor(), (Throwable)cause);
    }

    private boolean isNotValidPromise(ChannelPromise promise, boolean allowVoidPromise) {
        if (promise == null) {
            throw new NullPointerException((String)"promise");
        }
        if (promise.isDone()) {
            if (!promise.isCancelled()) throw new IllegalArgumentException((String)("promise already done: " + promise));
            return true;
        }
        if (promise.channel() != this.channel()) {
            throw new IllegalArgumentException((String)String.format((String)"promise.channel does not match: %s (expected: %s)", (Object[])new Object[]{promise.channel(), this.channel()}));
        }
        if (promise.getClass() == DefaultChannelPromise.class) {
            return false;
        }
        if (!allowVoidPromise && promise instanceof VoidChannelPromise) {
            throw new IllegalArgumentException((String)(StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation"));
        }
        if (!(promise instanceof AbstractChannel.CloseFuture)) return false;
        throw new IllegalArgumentException((String)(StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline"));
    }

    private AbstractChannelHandlerContext findContextInbound(int mask) {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.next;
        } while ((ctx.executionMask & mask) == 0);
        return ctx;
    }

    private AbstractChannelHandlerContext findContextOutbound(int mask) {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.prev;
        } while ((ctx.executionMask & mask) == 0);
        return ctx;
    }

    @Override
    public ChannelPromise voidPromise() {
        return this.channel().voidPromise();
    }

    final void setRemoved() {
        this.handlerState = 3;
    }

    final boolean setAddComplete() {
        int oldState;
        do {
            if ((oldState = this.handlerState) != 3) continue;
            return false;
        } while (!HANDLER_STATE_UPDATER.compareAndSet((AbstractChannelHandlerContext)this, (int)oldState, (int)2));
        return true;
    }

    final void setAddPending() {
        boolean updated = HANDLER_STATE_UPDATER.compareAndSet((AbstractChannelHandlerContext)this, (int)0, (int)1);
        if ($assertionsDisabled) return;
        if (updated) return;
        throw new AssertionError();
    }

    final void callHandlerAdded() throws Exception {
        if (!this.setAddComplete()) return;
        this.handler().handlerAdded((ChannelHandlerContext)this);
    }

    final void callHandlerRemoved() throws Exception {
        try {
            if (this.handlerState != 2) return;
            this.handler().handlerRemoved((ChannelHandlerContext)this);
            return;
        }
        finally {
            this.setRemoved();
        }
    }

    private boolean invokeHandler() {
        int handlerState = this.handlerState;
        if (handlerState == 2) return true;
        if (this.ordered) return false;
        if (handlerState != 1) return false;
        return true;
    }

    @Override
    public boolean isRemoved() {
        if (this.handlerState != 3) return false;
        return true;
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.channel().attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.channel().hasAttr(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean safeExecute(EventExecutor executor, Runnable runnable, ChannelPromise promise, Object msg) {
        try {
            executor.execute((Runnable)runnable);
            return true;
        }
        catch (Throwable cause) {
            try {
                promise.setFailure((Throwable)cause);
                return false;
            }
            finally {
                if (msg != null) {
                    ReferenceCountUtil.release((Object)msg);
                }
            }
        }
    }

    @Override
    public String toHintString() {
        return '\'' + this.name + "' will handle the message from this point.";
    }

    public String toString() {
        return StringUtil.simpleClassName(ChannelHandlerContext.class) + '(' + this.name + ", " + this.channel() + ')';
    }

    static /* synthetic */ void access$000(AbstractChannelHandlerContext x0) {
        x0.invokeChannelRegistered();
    }

    static /* synthetic */ void access$100(AbstractChannelHandlerContext x0) {
        x0.invokeChannelUnregistered();
    }

    static /* synthetic */ void access$200(AbstractChannelHandlerContext x0) {
        x0.invokeChannelActive();
    }

    static /* synthetic */ void access$300(AbstractChannelHandlerContext x0) {
        x0.invokeChannelInactive();
    }

    static /* synthetic */ void access$400(AbstractChannelHandlerContext x0, Throwable x1) {
        x0.invokeExceptionCaught((Throwable)x1);
    }

    static /* synthetic */ void access$500(AbstractChannelHandlerContext x0, Object x1) {
        x0.invokeUserEventTriggered((Object)x1);
    }

    static /* synthetic */ void access$600(AbstractChannelHandlerContext x0, Object x1) {
        x0.invokeChannelRead((Object)x1);
    }

    static /* synthetic */ void access$900(AbstractChannelHandlerContext x0, SocketAddress x1, ChannelPromise x2) {
        x0.invokeBind((SocketAddress)x1, (ChannelPromise)x2);
    }

    static /* synthetic */ void access$1000(AbstractChannelHandlerContext x0, SocketAddress x1, SocketAddress x2, ChannelPromise x3) {
        x0.invokeConnect((SocketAddress)x1, (SocketAddress)x2, (ChannelPromise)x3);
    }

    static /* synthetic */ void access$1100(AbstractChannelHandlerContext x0, ChannelPromise x1) {
        x0.invokeDisconnect((ChannelPromise)x1);
    }

    static /* synthetic */ void access$1200(AbstractChannelHandlerContext x0, ChannelPromise x1) {
        x0.invokeClose((ChannelPromise)x1);
    }

    static /* synthetic */ void access$1300(AbstractChannelHandlerContext x0, ChannelPromise x1) {
        x0.invokeDeregister((ChannelPromise)x1);
    }

    static /* synthetic */ DefaultChannelPipeline access$1600(AbstractChannelHandlerContext x0) {
        return x0.pipeline;
    }

    static /* synthetic */ void access$1700(AbstractChannelHandlerContext x0, Object x1, ChannelPromise x2) {
        x0.invokeWrite((Object)x1, (ChannelPromise)x2);
    }

    static /* synthetic */ void access$2100(AbstractChannelHandlerContext x0) {
        x0.invokeFlush();
    }

    static /* synthetic */ void access$2300(AbstractChannelHandlerContext x0) {
        x0.invokeChannelReadComplete();
    }

    static /* synthetic */ void access$2400(AbstractChannelHandlerContext x0) {
        x0.invokeRead();
    }

    static /* synthetic */ void access$2500(AbstractChannelHandlerContext x0) {
        x0.invokeChannelWritabilityChanged();
    }
}

