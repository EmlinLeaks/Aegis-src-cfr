/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

public class CombinedChannelDuplexHandler<I extends ChannelInboundHandler, O extends ChannelOutboundHandler>
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CombinedChannelDuplexHandler.class);
    private DelegatingChannelHandlerContext inboundCtx;
    private DelegatingChannelHandlerContext outboundCtx;
    private volatile boolean handlerAdded;
    private I inboundHandler;
    private O outboundHandler;

    protected CombinedChannelDuplexHandler() {
        this.ensureNotSharable();
    }

    public CombinedChannelDuplexHandler(I inboundHandler, O outboundHandler) {
        this.ensureNotSharable();
        this.init(inboundHandler, outboundHandler);
    }

    protected final void init(I inboundHandler, O outboundHandler) {
        this.validate(inboundHandler, outboundHandler);
        this.inboundHandler = inboundHandler;
        this.outboundHandler = outboundHandler;
    }

    private void validate(I inboundHandler, O outboundHandler) {
        if (this.inboundHandler != null) {
            throw new IllegalStateException((String)("init() can not be invoked if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with non-default constructor."));
        }
        if (inboundHandler == null) {
            throw new NullPointerException((String)"inboundHandler");
        }
        if (outboundHandler == null) {
            throw new NullPointerException((String)"outboundHandler");
        }
        if (inboundHandler instanceof ChannelOutboundHandler) {
            throw new IllegalArgumentException((String)("inboundHandler must not implement " + ChannelOutboundHandler.class.getSimpleName() + " to get combined."));
        }
        if (!(outboundHandler instanceof ChannelInboundHandler)) return;
        throw new IllegalArgumentException((String)("outboundHandler must not implement " + ChannelInboundHandler.class.getSimpleName() + " to get combined."));
    }

    protected final I inboundHandler() {
        return (I)this.inboundHandler;
    }

    protected final O outboundHandler() {
        return (O)this.outboundHandler;
    }

    private void checkAdded() {
        if (this.handlerAdded) return;
        throw new IllegalStateException((String)"handler not added to pipeline yet");
    }

    public final void removeInboundHandler() {
        this.checkAdded();
        this.inboundCtx.remove();
    }

    public final void removeOutboundHandler() {
        this.checkAdded();
        this.outboundCtx.remove();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (this.inboundHandler == null) {
            throw new IllegalStateException((String)("init() must be invoked before being added to a " + ChannelPipeline.class.getSimpleName() + " if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with the default constructor."));
        }
        this.outboundCtx = new DelegatingChannelHandlerContext((ChannelHandlerContext)ctx, this.outboundHandler);
        this.inboundCtx = new DelegatingChannelHandlerContext((CombinedChannelDuplexHandler)this, (ChannelHandlerContext)ctx, this.inboundHandler){
            final /* synthetic */ CombinedChannelDuplexHandler this$0;
            {
                this.this$0 = this$0;
                super((ChannelHandlerContext)ctx, (ChannelHandler)handler);
            }

            public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
                if (CombinedChannelDuplexHandler.access$000((CombinedChannelDuplexHandler)this.this$0).removed) {
                    super.fireExceptionCaught((Throwable)cause);
                    return this;
                }
                try {
                    CombinedChannelDuplexHandler.access$100((CombinedChannelDuplexHandler)this.this$0).exceptionCaught((ChannelHandlerContext)CombinedChannelDuplexHandler.access$000((CombinedChannelDuplexHandler)this.this$0), (Throwable)cause);
                    return this;
                }
                catch (Throwable error) {
                    if (CombinedChannelDuplexHandler.access$200().isDebugEnabled()) {
                        CombinedChannelDuplexHandler.access$200().debug((String)"An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", (Object)io.netty.util.internal.ThrowableUtil.stackTraceToString((Throwable)error), (Object)cause);
                        return this;
                    }
                    if (!CombinedChannelDuplexHandler.access$200().isWarnEnabled()) return this;
                    CombinedChannelDuplexHandler.access$200().warn((String)"An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", (Object)error, (Object)cause);
                    return this;
                }
            }
        };
        this.handlerAdded = true;
        try {
            this.inboundHandler.handlerAdded((ChannelHandlerContext)this.inboundCtx);
            return;
        }
        finally {
            this.outboundHandler.handlerAdded((ChannelHandlerContext)this.outboundCtx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            this.inboundCtx.remove();
            return;
        }
        finally {
            this.outboundCtx.remove();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelRegistered((ChannelHandlerContext)this.inboundCtx);
            return;
        }
        this.inboundCtx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelUnregistered((ChannelHandlerContext)this.inboundCtx);
            return;
        }
        this.inboundCtx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelActive((ChannelHandlerContext)this.inboundCtx);
            return;
        }
        this.inboundCtx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelInactive((ChannelHandlerContext)this.inboundCtx);
            return;
        }
        this.inboundCtx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.exceptionCaught((ChannelHandlerContext)this.inboundCtx, (Throwable)cause);
            return;
        }
        this.inboundCtx.fireExceptionCaught((Throwable)cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.userEventTriggered((ChannelHandlerContext)this.inboundCtx, (Object)evt);
            return;
        }
        this.inboundCtx.fireUserEventTriggered((Object)evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelRead((ChannelHandlerContext)this.inboundCtx, (Object)msg);
            return;
        }
        this.inboundCtx.fireChannelRead((Object)msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelReadComplete((ChannelHandlerContext)this.inboundCtx);
            return;
        }
        this.inboundCtx.fireChannelReadComplete();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.inboundCtx).ctx);
        if (!this.inboundCtx.removed) {
            this.inboundHandler.channelWritabilityChanged((ChannelHandlerContext)this.inboundCtx);
            return;
        }
        this.inboundCtx.fireChannelWritabilityChanged();
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.outboundCtx).ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.bind((ChannelHandlerContext)this.outboundCtx, (SocketAddress)localAddress, (ChannelPromise)promise);
            return;
        }
        this.outboundCtx.bind((SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.outboundCtx).ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.connect((ChannelHandlerContext)this.outboundCtx, (SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
            return;
        }
        this.outboundCtx.connect((SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.outboundCtx).ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.disconnect((ChannelHandlerContext)this.outboundCtx, (ChannelPromise)promise);
            return;
        }
        this.outboundCtx.disconnect((ChannelPromise)promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.outboundCtx).ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.close((ChannelHandlerContext)this.outboundCtx, (ChannelPromise)promise);
            return;
        }
        this.outboundCtx.close((ChannelPromise)promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.outboundCtx).ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.deregister((ChannelHandlerContext)this.outboundCtx, (ChannelPromise)promise);
            return;
        }
        this.outboundCtx.deregister((ChannelPromise)promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.outboundCtx).ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.read((ChannelHandlerContext)this.outboundCtx);
            return;
        }
        this.outboundCtx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.outboundCtx).ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.write((ChannelHandlerContext)this.outboundCtx, (Object)msg, (ChannelPromise)promise);
            return;
        }
        this.outboundCtx.write((Object)msg, (ChannelPromise)promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        assert (ctx == ((DelegatingChannelHandlerContext)this.outboundCtx).ctx);
        if (!this.outboundCtx.removed) {
            this.outboundHandler.flush((ChannelHandlerContext)this.outboundCtx);
            return;
        }
        this.outboundCtx.flush();
    }

    static /* synthetic */ DelegatingChannelHandlerContext access$000(CombinedChannelDuplexHandler x0) {
        return x0.outboundCtx;
    }

    static /* synthetic */ ChannelOutboundHandler access$100(CombinedChannelDuplexHandler x0) {
        return x0.outboundHandler;
    }

    static /* synthetic */ InternalLogger access$200() {
        return logger;
    }
}

