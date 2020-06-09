/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.AbstractChannelHandlerContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundInvoker;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineException;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelHandlerContext;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.DefaultChannelProgressivePromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.FailedChannelFuture;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.SucceededChannelFuture;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultChannelPipeline
implements ChannelPipeline {
    static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
    private static final String HEAD_NAME = DefaultChannelPipeline.generateName0(HeadContext.class);
    private static final String TAIL_NAME = DefaultChannelPipeline.generateName0(TailContext.class);
    private static final FastThreadLocal<Map<Class<?>, String>> nameCaches = new FastThreadLocal<Map<Class<?>, String>>(){

        protected Map<Class<?>, String> initialValue() {
            return new java.util.WeakHashMap<Class<?>, String>();
        }
    };
    private static final AtomicReferenceFieldUpdater<DefaultChannelPipeline, MessageSizeEstimator.Handle> ESTIMATOR = AtomicReferenceFieldUpdater.newUpdater(DefaultChannelPipeline.class, MessageSizeEstimator.Handle.class, (String)"estimatorHandle");
    final AbstractChannelHandlerContext head;
    final AbstractChannelHandlerContext tail;
    private final Channel channel;
    private final ChannelFuture succeededFuture;
    private final VoidChannelPromise voidPromise;
    private final boolean touch = ResourceLeakDetector.isEnabled();
    private Map<EventExecutorGroup, EventExecutor> childExecutors;
    private volatile MessageSizeEstimator.Handle estimatorHandle;
    private boolean firstRegistration = true;
    private PendingHandlerCallback pendingHandlerCallbackHead;
    private boolean registered;

    protected DefaultChannelPipeline(Channel channel) {
        this.channel = ObjectUtil.checkNotNull(channel, (String)"channel");
        this.succeededFuture = new SucceededChannelFuture((Channel)channel, null);
        this.voidPromise = new VoidChannelPromise((Channel)channel, (boolean)true);
        this.tail = new TailContext((DefaultChannelPipeline)this, (DefaultChannelPipeline)this);
        this.head = new HeadContext((DefaultChannelPipeline)this, (DefaultChannelPipeline)this);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    final MessageSizeEstimator.Handle estimatorHandle() {
        MessageSizeEstimator.Handle handle = this.estimatorHandle;
        if (handle != null) return handle;
        handle = this.channel.config().getMessageSizeEstimator().newHandle();
        if (ESTIMATOR.compareAndSet((DefaultChannelPipeline)this, null, (MessageSizeEstimator.Handle)handle)) return handle;
        return this.estimatorHandle;
    }

    final Object touch(Object msg, AbstractChannelHandlerContext next) {
        Object object;
        if (this.touch) {
            object = ReferenceCountUtil.touch(msg, (Object)next);
            return object;
        }
        object = msg;
        return object;
    }

    private AbstractChannelHandlerContext newContext(EventExecutorGroup group, String name, ChannelHandler handler) {
        return new DefaultChannelHandlerContext((DefaultChannelPipeline)this, (EventExecutor)this.childExecutor((EventExecutorGroup)group), (String)name, (ChannelHandler)handler);
    }

    private EventExecutor childExecutor(EventExecutorGroup group) {
        EventExecutor childExecutor;
        if (group == null) {
            return null;
        }
        Boolean pinEventExecutor = this.channel.config().getOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
        if (pinEventExecutor != null && !pinEventExecutor.booleanValue()) {
            return group.next();
        }
        Map<EventExecutorGroup, EventExecutor> childExecutors = this.childExecutors;
        if (childExecutors == null) {
            childExecutors = this.childExecutors = new IdentityHashMap<EventExecutorGroup, EventExecutor>((int)4);
        }
        if ((childExecutor = childExecutors.get((Object)group)) != null) return childExecutor;
        childExecutor = group.next();
        childExecutors.put((EventExecutorGroup)group, (EventExecutor)childExecutor);
        return childExecutor;
    }

    @Override
    public final Channel channel() {
        return this.channel;
    }

    @Override
    public final ChannelPipeline addFirst(String name, ChannelHandler handler) {
        return this.addFirst(null, (String)name, (ChannelHandler)handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
        DefaultChannelPipeline defaultChannelPipeline = this;
        // MONITORENTER : defaultChannelPipeline
        DefaultChannelPipeline.checkMultiplicity((ChannelHandler)handler);
        name = this.filterName((String)name, (ChannelHandler)handler);
        AbstractChannelHandlerContext newCtx = this.newContext((EventExecutorGroup)group, (String)name, (ChannelHandler)handler);
        this.addFirst0((AbstractChannelHandlerContext)newCtx);
        if (!this.registered) {
            newCtx.setAddPending();
            this.callHandlerCallbackLater((AbstractChannelHandlerContext)newCtx, (boolean)true);
            // MONITOREXIT : defaultChannelPipeline
            return this;
        }
        EventExecutor executor = newCtx.executor();
        if (!executor.inEventLoop()) {
            this.callHandlerAddedInEventLoop((AbstractChannelHandlerContext)newCtx, (EventExecutor)executor);
            // MONITOREXIT : defaultChannelPipeline
            return this;
        }
        // MONITOREXIT : defaultChannelPipeline
        this.callHandlerAdded0((AbstractChannelHandlerContext)newCtx);
        return this;
    }

    private void addFirst0(AbstractChannelHandlerContext newCtx) {
        AbstractChannelHandlerContext nextCtx = this.head.next;
        newCtx.prev = this.head;
        newCtx.next = nextCtx;
        this.head.next = newCtx;
        nextCtx.prev = newCtx;
    }

    @Override
    public final ChannelPipeline addLast(String name, ChannelHandler handler) {
        return this.addLast(null, (String)name, (ChannelHandler)handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
        DefaultChannelPipeline defaultChannelPipeline = this;
        // MONITORENTER : defaultChannelPipeline
        DefaultChannelPipeline.checkMultiplicity((ChannelHandler)handler);
        AbstractChannelHandlerContext newCtx = this.newContext((EventExecutorGroup)group, (String)this.filterName((String)name, (ChannelHandler)handler), (ChannelHandler)handler);
        this.addLast0((AbstractChannelHandlerContext)newCtx);
        if (!this.registered) {
            newCtx.setAddPending();
            this.callHandlerCallbackLater((AbstractChannelHandlerContext)newCtx, (boolean)true);
            // MONITOREXIT : defaultChannelPipeline
            return this;
        }
        EventExecutor executor = newCtx.executor();
        if (!executor.inEventLoop()) {
            this.callHandlerAddedInEventLoop((AbstractChannelHandlerContext)newCtx, (EventExecutor)executor);
            // MONITOREXIT : defaultChannelPipeline
            return this;
        }
        // MONITOREXIT : defaultChannelPipeline
        this.callHandlerAdded0((AbstractChannelHandlerContext)newCtx);
        return this;
    }

    private void addLast0(AbstractChannelHandlerContext newCtx) {
        AbstractChannelHandlerContext prev;
        newCtx.prev = prev = this.tail.prev;
        newCtx.next = this.tail;
        prev.next = newCtx;
        this.tail.prev = newCtx;
    }

    @Override
    public final ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
        return this.addBefore(null, (String)baseName, (String)name, (ChannelHandler)handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        DefaultChannelPipeline defaultChannelPipeline = this;
        // MONITORENTER : defaultChannelPipeline
        DefaultChannelPipeline.checkMultiplicity((ChannelHandler)handler);
        name = this.filterName((String)name, (ChannelHandler)handler);
        AbstractChannelHandlerContext ctx = this.getContextOrDie((String)baseName);
        AbstractChannelHandlerContext newCtx = this.newContext((EventExecutorGroup)group, (String)name, (ChannelHandler)handler);
        DefaultChannelPipeline.addBefore0((AbstractChannelHandlerContext)ctx, (AbstractChannelHandlerContext)newCtx);
        if (!this.registered) {
            newCtx.setAddPending();
            this.callHandlerCallbackLater((AbstractChannelHandlerContext)newCtx, (boolean)true);
            // MONITOREXIT : defaultChannelPipeline
            return this;
        }
        EventExecutor executor = newCtx.executor();
        if (!executor.inEventLoop()) {
            this.callHandlerAddedInEventLoop((AbstractChannelHandlerContext)newCtx, (EventExecutor)executor);
            // MONITOREXIT : defaultChannelPipeline
            return this;
        }
        // MONITOREXIT : defaultChannelPipeline
        this.callHandlerAdded0((AbstractChannelHandlerContext)newCtx);
        return this;
    }

    private static void addBefore0(AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
        newCtx.prev = ctx.prev;
        newCtx.next = ctx;
        ctx.prev.next = newCtx;
        ctx.prev = newCtx;
    }

    private String filterName(String name, ChannelHandler handler) {
        if (name == null) {
            return this.generateName((ChannelHandler)handler);
        }
        this.checkDuplicateName((String)name);
        return name;
    }

    @Override
    public final ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
        return this.addAfter(null, (String)baseName, (String)name, (ChannelHandler)handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        DefaultChannelPipeline defaultChannelPipeline = this;
        // MONITORENTER : defaultChannelPipeline
        DefaultChannelPipeline.checkMultiplicity((ChannelHandler)handler);
        name = this.filterName((String)name, (ChannelHandler)handler);
        AbstractChannelHandlerContext ctx = this.getContextOrDie((String)baseName);
        AbstractChannelHandlerContext newCtx = this.newContext((EventExecutorGroup)group, (String)name, (ChannelHandler)handler);
        DefaultChannelPipeline.addAfter0((AbstractChannelHandlerContext)ctx, (AbstractChannelHandlerContext)newCtx);
        if (!this.registered) {
            newCtx.setAddPending();
            this.callHandlerCallbackLater((AbstractChannelHandlerContext)newCtx, (boolean)true);
            // MONITOREXIT : defaultChannelPipeline
            return this;
        }
        EventExecutor executor = newCtx.executor();
        if (!executor.inEventLoop()) {
            this.callHandlerAddedInEventLoop((AbstractChannelHandlerContext)newCtx, (EventExecutor)executor);
            // MONITOREXIT : defaultChannelPipeline
            return this;
        }
        // MONITOREXIT : defaultChannelPipeline
        this.callHandlerAdded0((AbstractChannelHandlerContext)newCtx);
        return this;
    }

    private static void addAfter0(AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
        newCtx.prev = ctx;
        newCtx.next = ctx.next;
        ctx.next.prev = newCtx;
        ctx.next = newCtx;
    }

    public final ChannelPipeline addFirst(ChannelHandler handler) {
        return this.addFirst(null, (ChannelHandler)handler);
    }

    @Override
    public final ChannelPipeline addFirst(ChannelHandler ... handlers) {
        return this.addFirst(null, (ChannelHandler[])handlers);
    }

    @Override
    public final ChannelPipeline addFirst(EventExecutorGroup executor, ChannelHandler ... handlers) {
        int size;
        if (handlers == null) {
            throw new NullPointerException((String)"handlers");
        }
        if (handlers.length == 0) return this;
        if (handlers[0] == null) {
            return this;
        }
        for (size = 1; size < handlers.length && handlers[size] != null; ++size) {
        }
        int i = size - 1;
        while (i >= 0) {
            ChannelHandler h = handlers[i];
            this.addFirst((EventExecutorGroup)executor, null, (ChannelHandler)h);
            --i;
        }
        return this;
    }

    public final ChannelPipeline addLast(ChannelHandler handler) {
        return this.addLast(null, (ChannelHandler)handler);
    }

    @Override
    public final ChannelPipeline addLast(ChannelHandler ... handlers) {
        return this.addLast(null, (ChannelHandler[])handlers);
    }

    @Override
    public final ChannelPipeline addLast(EventExecutorGroup executor, ChannelHandler ... handlers) {
        if (handlers == null) {
            throw new NullPointerException((String)"handlers");
        }
        ChannelHandler[] arrchannelHandler = handlers;
        int n = arrchannelHandler.length;
        int n2 = 0;
        while (n2 < n) {
            ChannelHandler h = arrchannelHandler[n2];
            if (h == null) {
                return this;
            }
            this.addLast((EventExecutorGroup)executor, null, (ChannelHandler)h);
            ++n2;
        }
        return this;
    }

    private String generateName(ChannelHandler handler) {
        Class<?> handlerType;
        Map<Class<?>, String> cache = nameCaches.get();
        String name = cache.get(handlerType = handler.getClass());
        if (name == null) {
            name = DefaultChannelPipeline.generateName0(handlerType);
            cache.put(handlerType, (String)name);
        }
        if (this.context0((String)name) == null) return name;
        String baseName = name.substring((int)0, (int)(name.length() - 1));
        int i = 1;
        String newName;
        while (this.context0((String)(newName = baseName + i)) != null) {
            ++i;
        }
        return newName;
    }

    private static String generateName0(Class<?> handlerType) {
        return StringUtil.simpleClassName(handlerType) + "#0";
    }

    @Override
    public final ChannelPipeline remove(ChannelHandler handler) {
        this.remove((AbstractChannelHandlerContext)this.getContextOrDie((ChannelHandler)handler));
        return this;
    }

    @Override
    public final ChannelHandler remove(String name) {
        return this.remove((AbstractChannelHandlerContext)this.getContextOrDie((String)name)).handler();
    }

    @Override
    public final <T extends ChannelHandler> T remove(Class<T> handlerType) {
        return (T)this.remove((AbstractChannelHandlerContext)this.getContextOrDie(handlerType)).handler();
    }

    public final <T extends ChannelHandler> T removeIfExists(String name) {
        return (T)this.removeIfExists((ChannelHandlerContext)this.context((String)name));
    }

    public final <T extends ChannelHandler> T removeIfExists(Class<T> handlerType) {
        return (T)this.removeIfExists((ChannelHandlerContext)this.context(handlerType));
    }

    public final <T extends ChannelHandler> T removeIfExists(ChannelHandler handler) {
        return (T)this.removeIfExists((ChannelHandlerContext)this.context((ChannelHandler)handler));
    }

    private <T extends ChannelHandler> T removeIfExists(ChannelHandlerContext ctx) {
        if (ctx != null) return (T)this.remove((AbstractChannelHandlerContext)((AbstractChannelHandlerContext)ctx)).handler();
        return (T)null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private AbstractChannelHandlerContext remove(AbstractChannelHandlerContext ctx) {
        if (!$assertionsDisabled) {
            if (ctx == this.head) throw new AssertionError();
            if (ctx == this.tail) {
                throw new AssertionError();
            }
        }
        DefaultChannelPipeline defaultChannelPipeline = this;
        // MONITORENTER : defaultChannelPipeline
        this.atomicRemoveFromHandlerList((AbstractChannelHandlerContext)ctx);
        if (!this.registered) {
            this.callHandlerCallbackLater((AbstractChannelHandlerContext)ctx, (boolean)false);
            // MONITOREXIT : defaultChannelPipeline
            return ctx;
        }
        EventExecutor executor = ctx.executor();
        if (!executor.inEventLoop()) {
            executor.execute((Runnable)new Runnable((DefaultChannelPipeline)this, (AbstractChannelHandlerContext)ctx){
                final /* synthetic */ AbstractChannelHandlerContext val$ctx;
                final /* synthetic */ DefaultChannelPipeline this$0;
                {
                    this.this$0 = this$0;
                    this.val$ctx = abstractChannelHandlerContext;
                }

                public void run() {
                    DefaultChannelPipeline.access$000((DefaultChannelPipeline)this.this$0, (AbstractChannelHandlerContext)this.val$ctx);
                }
            });
            // MONITOREXIT : defaultChannelPipeline
            return ctx;
        }
        // MONITOREXIT : defaultChannelPipeline
        this.callHandlerRemoved0((AbstractChannelHandlerContext)ctx);
        return ctx;
    }

    private synchronized void atomicRemoveFromHandlerList(AbstractChannelHandlerContext ctx) {
        AbstractChannelHandlerContext next;
        AbstractChannelHandlerContext prev = ctx.prev;
        prev.next = next = ctx.next;
        next.prev = prev;
    }

    @Override
    public final ChannelHandler removeFirst() {
        if (this.head.next != this.tail) return this.remove((AbstractChannelHandlerContext)this.head.next).handler();
        throw new NoSuchElementException();
    }

    @Override
    public final ChannelHandler removeLast() {
        if (this.head.next != this.tail) return this.remove((AbstractChannelHandlerContext)this.tail.prev).handler();
        throw new NoSuchElementException();
    }

    @Override
    public final ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
        this.replace((AbstractChannelHandlerContext)this.getContextOrDie((ChannelHandler)oldHandler), (String)newName, (ChannelHandler)newHandler);
        return this;
    }

    @Override
    public final ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
        return this.replace((AbstractChannelHandlerContext)this.getContextOrDie((String)oldName), (String)newName, (ChannelHandler)newHandler);
    }

    @Override
    public final <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
        return (T)this.replace((AbstractChannelHandlerContext)this.getContextOrDie(oldHandlerType), (String)newName, (ChannelHandler)newHandler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ChannelHandler replace(AbstractChannelHandlerContext ctx, String newName, ChannelHandler newHandler) {
        if (!$assertionsDisabled) {
            if (ctx == this.head) throw new AssertionError();
            if (ctx == this.tail) {
                throw new AssertionError();
            }
        }
        DefaultChannelPipeline defaultChannelPipeline = this;
        // MONITORENTER : defaultChannelPipeline
        DefaultChannelPipeline.checkMultiplicity((ChannelHandler)newHandler);
        if (newName == null) {
            newName = this.generateName((ChannelHandler)newHandler);
        } else {
            boolean sameName = ctx.name().equals((Object)newName);
            if (!sameName) {
                this.checkDuplicateName((String)newName);
            }
        }
        AbstractChannelHandlerContext newCtx = this.newContext((EventExecutorGroup)ctx.executor, (String)newName, (ChannelHandler)newHandler);
        DefaultChannelPipeline.replace0((AbstractChannelHandlerContext)ctx, (AbstractChannelHandlerContext)newCtx);
        if (!this.registered) {
            this.callHandlerCallbackLater((AbstractChannelHandlerContext)newCtx, (boolean)true);
            this.callHandlerCallbackLater((AbstractChannelHandlerContext)ctx, (boolean)false);
            // MONITOREXIT : defaultChannelPipeline
            return ctx.handler();
        }
        EventExecutor executor = ctx.executor();
        if (!executor.inEventLoop()) {
            executor.execute((Runnable)new Runnable((DefaultChannelPipeline)this, (AbstractChannelHandlerContext)newCtx, (AbstractChannelHandlerContext)ctx){
                final /* synthetic */ AbstractChannelHandlerContext val$newCtx;
                final /* synthetic */ AbstractChannelHandlerContext val$ctx;
                final /* synthetic */ DefaultChannelPipeline this$0;
                {
                    this.this$0 = this$0;
                    this.val$newCtx = abstractChannelHandlerContext;
                    this.val$ctx = abstractChannelHandlerContext2;
                }

                public void run() {
                    DefaultChannelPipeline.access$100((DefaultChannelPipeline)this.this$0, (AbstractChannelHandlerContext)this.val$newCtx);
                    DefaultChannelPipeline.access$000((DefaultChannelPipeline)this.this$0, (AbstractChannelHandlerContext)this.val$ctx);
                }
            });
            // MONITOREXIT : defaultChannelPipeline
            return ctx.handler();
        }
        // MONITOREXIT : defaultChannelPipeline
        this.callHandlerAdded0((AbstractChannelHandlerContext)newCtx);
        this.callHandlerRemoved0((AbstractChannelHandlerContext)ctx);
        return ctx.handler();
    }

    private static void replace0(AbstractChannelHandlerContext oldCtx, AbstractChannelHandlerContext newCtx) {
        AbstractChannelHandlerContext prev = oldCtx.prev;
        AbstractChannelHandlerContext next = oldCtx.next;
        newCtx.prev = prev;
        newCtx.next = next;
        prev.next = newCtx;
        next.prev = newCtx;
        oldCtx.prev = newCtx;
        oldCtx.next = newCtx;
    }

    private static void checkMultiplicity(ChannelHandler handler) {
        if (!(handler instanceof ChannelHandlerAdapter)) return;
        ChannelHandlerAdapter h = (ChannelHandlerAdapter)handler;
        if (!h.isSharable() && h.added) {
            throw new ChannelPipelineException((String)(h.getClass().getName() + " is not a @Sharable handler, so can't be added or removed multiple times."));
        }
        h.added = true;
    }

    private void callHandlerAdded0(AbstractChannelHandlerContext ctx) {
        try {
            ctx.callHandlerAdded();
            return;
        }
        catch (Throwable t) {
            boolean removed;
            block5 : {
                removed = false;
                try {
                    this.atomicRemoveFromHandlerList((AbstractChannelHandlerContext)ctx);
                    ctx.callHandlerRemoved();
                    removed = true;
                }
                catch (Throwable t2) {
                    if (!logger.isWarnEnabled()) break block5;
                    logger.warn((String)("Failed to remove a handler: " + ctx.name()), (Throwable)t2);
                }
            }
            if (removed) {
                this.fireExceptionCaught((Throwable)new ChannelPipelineException((String)(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; removed."), (Throwable)t));
                return;
            }
            this.fireExceptionCaught((Throwable)new ChannelPipelineException((String)(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; also failed to remove."), (Throwable)t));
        }
    }

    private void callHandlerRemoved0(AbstractChannelHandlerContext ctx) {
        try {
            ctx.callHandlerRemoved();
            return;
        }
        catch (Throwable t) {
            this.fireExceptionCaught((Throwable)new ChannelPipelineException((String)(ctx.handler().getClass().getName() + ".handlerRemoved() has thrown an exception."), (Throwable)t));
        }
    }

    final void invokeHandlerAddedIfNeeded() {
        assert (this.channel.eventLoop().inEventLoop());
        if (!this.firstRegistration) return;
        this.firstRegistration = false;
        this.callHandlerAddedForAllHandlers();
    }

    @Override
    public final ChannelHandler first() {
        ChannelHandlerContext first = this.firstContext();
        if (first != null) return first.handler();
        return null;
    }

    @Override
    public final ChannelHandlerContext firstContext() {
        AbstractChannelHandlerContext first = this.head.next;
        if (first != this.tail) return this.head.next;
        return null;
    }

    @Override
    public final ChannelHandler last() {
        AbstractChannelHandlerContext last = this.tail.prev;
        if (last != this.head) return last.handler();
        return null;
    }

    @Override
    public final ChannelHandlerContext lastContext() {
        AbstractChannelHandlerContext last = this.tail.prev;
        if (last != this.head) return last;
        return null;
    }

    @Override
    public final ChannelHandler get(String name) {
        ChannelHandlerContext ctx = this.context((String)name);
        if (ctx != null) return ctx.handler();
        return null;
    }

    @Override
    public final <T extends ChannelHandler> T get(Class<T> handlerType) {
        ChannelHandlerContext ctx = this.context(handlerType);
        if (ctx != null) return (T)ctx.handler();
        return (T)null;
    }

    @Override
    public final ChannelHandlerContext context(String name) {
        if (name != null) return this.context0((String)name);
        throw new NullPointerException((String)"name");
    }

    @Override
    public final ChannelHandlerContext context(ChannelHandler handler) {
        if (handler == null) {
            throw new NullPointerException((String)"handler");
        }
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            if (ctx.handler() == handler) {
                return ctx;
            }
            ctx = ctx.next;
        }
        return null;
    }

    @Override
    public final ChannelHandlerContext context(Class<? extends ChannelHandler> handlerType) {
        if (handlerType == null) {
            throw new NullPointerException((String)"handlerType");
        }
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            if (handlerType.isAssignableFrom(ctx.handler().getClass())) {
                return ctx;
            }
            ctx = ctx.next;
        }
        return null;
    }

    @Override
    public final List<String> names() {
        ArrayList<String> list = new ArrayList<String>();
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            list.add((String)ctx.name());
            ctx = ctx.next;
        }
        return list;
    }

    @Override
    public final Map<String, ChannelHandler> toMap() {
        LinkedHashMap<String, ChannelHandler> map = new LinkedHashMap<String, ChannelHandler>();
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != this.tail) {
            map.put((String)ctx.name(), (ChannelHandler)ctx.handler());
            ctx = ctx.next;
        }
        return map;
    }

    @Override
    public final Iterator<Map.Entry<String, ChannelHandler>> iterator() {
        return this.toMap().entrySet().iterator();
    }

    public final String toString() {
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((char)'{');
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != this.tail) {
            buf.append((char)'(').append((String)ctx.name()).append((String)" = ").append((String)ctx.handler().getClass().getName()).append((char)')');
            ctx = ctx.next;
            if (ctx == this.tail) break;
            buf.append((String)", ");
        }
        buf.append((char)'}');
        return buf.toString();
    }

    @Override
    public final ChannelPipeline fireChannelRegistered() {
        AbstractChannelHandlerContext.invokeChannelRegistered((AbstractChannelHandlerContext)this.head);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelUnregistered() {
        AbstractChannelHandlerContext.invokeChannelUnregistered((AbstractChannelHandlerContext)this.head);
        return this;
    }

    private synchronized void destroy() {
        this.destroyUp((AbstractChannelHandlerContext)this.head.next, (boolean)false);
    }

    private void destroyUp(AbstractChannelHandlerContext ctx, boolean inEventLoop) {
        Thread currentThread = Thread.currentThread();
        AbstractChannelHandlerContext tail = this.tail;
        do {
            if (ctx == tail) {
                this.destroyDown((Thread)currentThread, (AbstractChannelHandlerContext)tail.prev, (boolean)inEventLoop);
                return;
            }
            EventExecutor executor = ctx.executor();
            if (!inEventLoop && !executor.inEventLoop((Thread)currentThread)) {
                AbstractChannelHandlerContext finalCtx = ctx;
                executor.execute((Runnable)new Runnable((DefaultChannelPipeline)this, (AbstractChannelHandlerContext)finalCtx){
                    final /* synthetic */ AbstractChannelHandlerContext val$finalCtx;
                    final /* synthetic */ DefaultChannelPipeline this$0;
                    {
                        this.this$0 = this$0;
                        this.val$finalCtx = abstractChannelHandlerContext;
                    }

                    public void run() {
                        DefaultChannelPipeline.access$200((DefaultChannelPipeline)this.this$0, (AbstractChannelHandlerContext)this.val$finalCtx, (boolean)true);
                    }
                });
                return;
            }
            ctx = ctx.next;
            inEventLoop = false;
        } while (true);
    }

    private void destroyDown(Thread currentThread, AbstractChannelHandlerContext ctx, boolean inEventLoop) {
        AbstractChannelHandlerContext head = this.head;
        while (ctx != head) {
            EventExecutor executor = ctx.executor();
            if (!inEventLoop && !executor.inEventLoop((Thread)currentThread)) {
                AbstractChannelHandlerContext finalCtx = ctx;
                executor.execute((Runnable)new Runnable((DefaultChannelPipeline)this, (AbstractChannelHandlerContext)finalCtx){
                    final /* synthetic */ AbstractChannelHandlerContext val$finalCtx;
                    final /* synthetic */ DefaultChannelPipeline this$0;
                    {
                        this.this$0 = this$0;
                        this.val$finalCtx = abstractChannelHandlerContext;
                    }

                    public void run() {
                        DefaultChannelPipeline.access$300((DefaultChannelPipeline)this.this$0, (Thread)Thread.currentThread(), (AbstractChannelHandlerContext)this.val$finalCtx, (boolean)true);
                    }
                });
                return;
            }
            this.atomicRemoveFromHandlerList((AbstractChannelHandlerContext)ctx);
            this.callHandlerRemoved0((AbstractChannelHandlerContext)ctx);
            ctx = ctx.prev;
            inEventLoop = false;
        }
        return;
    }

    @Override
    public final ChannelPipeline fireChannelActive() {
        AbstractChannelHandlerContext.invokeChannelActive((AbstractChannelHandlerContext)this.head);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelInactive() {
        AbstractChannelHandlerContext.invokeChannelInactive((AbstractChannelHandlerContext)this.head);
        return this;
    }

    @Override
    public final ChannelPipeline fireExceptionCaught(Throwable cause) {
        AbstractChannelHandlerContext.invokeExceptionCaught((AbstractChannelHandlerContext)this.head, (Throwable)cause);
        return this;
    }

    @Override
    public final ChannelPipeline fireUserEventTriggered(Object event) {
        AbstractChannelHandlerContext.invokeUserEventTriggered((AbstractChannelHandlerContext)this.head, (Object)event);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelRead(Object msg) {
        AbstractChannelHandlerContext.invokeChannelRead((AbstractChannelHandlerContext)this.head, (Object)msg);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelReadComplete() {
        AbstractChannelHandlerContext.invokeChannelReadComplete((AbstractChannelHandlerContext)this.head);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelWritabilityChanged() {
        AbstractChannelHandlerContext.invokeChannelWritabilityChanged((AbstractChannelHandlerContext)this.head);
        return this;
    }

    @Override
    public final ChannelFuture bind(SocketAddress localAddress) {
        return this.tail.bind((SocketAddress)localAddress);
    }

    @Override
    public final ChannelFuture connect(SocketAddress remoteAddress) {
        return this.tail.connect((SocketAddress)remoteAddress);
    }

    @Override
    public final ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.tail.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress);
    }

    @Override
    public final ChannelFuture disconnect() {
        return this.tail.disconnect();
    }

    @Override
    public final ChannelFuture close() {
        return this.tail.close();
    }

    @Override
    public final ChannelFuture deregister() {
        return this.tail.deregister();
    }

    @Override
    public final ChannelPipeline flush() {
        this.tail.flush();
        return this;
    }

    @Override
    public final ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.tail.bind((SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public final ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.tail.connect((SocketAddress)remoteAddress, (ChannelPromise)promise);
    }

    @Override
    public final ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.tail.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public final ChannelFuture disconnect(ChannelPromise promise) {
        return this.tail.disconnect((ChannelPromise)promise);
    }

    @Override
    public final ChannelFuture close(ChannelPromise promise) {
        return this.tail.close((ChannelPromise)promise);
    }

    @Override
    public final ChannelFuture deregister(ChannelPromise promise) {
        return this.tail.deregister((ChannelPromise)promise);
    }

    @Override
    public final ChannelPipeline read() {
        this.tail.read();
        return this;
    }

    @Override
    public final ChannelFuture write(Object msg) {
        return this.tail.write((Object)msg);
    }

    @Override
    public final ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.tail.write((Object)msg, (ChannelPromise)promise);
    }

    @Override
    public final ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.tail.writeAndFlush((Object)msg, (ChannelPromise)promise);
    }

    @Override
    public final ChannelFuture writeAndFlush(Object msg) {
        return this.tail.writeAndFlush((Object)msg);
    }

    @Override
    public final ChannelPromise newPromise() {
        return new DefaultChannelPromise((Channel)this.channel);
    }

    @Override
    public final ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise((Channel)this.channel);
    }

    @Override
    public final ChannelFuture newSucceededFuture() {
        return this.succeededFuture;
    }

    @Override
    public final ChannelFuture newFailedFuture(Throwable cause) {
        return new FailedChannelFuture((Channel)this.channel, null, (Throwable)cause);
    }

    @Override
    public final ChannelPromise voidPromise() {
        return this.voidPromise;
    }

    private void checkDuplicateName(String name) {
        if (this.context0((String)name) == null) return;
        throw new IllegalArgumentException((String)("Duplicate handler name: " + name));
    }

    private AbstractChannelHandlerContext context0(String name) {
        AbstractChannelHandlerContext context = this.head.next;
        while (context != this.tail) {
            if (context.name().equals((Object)name)) {
                return context;
            }
            context = context.next;
        }
        return null;
    }

    private AbstractChannelHandlerContext getContextOrDie(String name) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context((String)name);
        if (ctx != null) return ctx;
        throw new NoSuchElementException((String)name);
    }

    private AbstractChannelHandlerContext getContextOrDie(ChannelHandler handler) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context((ChannelHandler)handler);
        if (ctx != null) return ctx;
        throw new NoSuchElementException((String)handler.getClass().getName());
    }

    private AbstractChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> handlerType) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handlerType);
        if (ctx != null) return ctx;
        throw new NoSuchElementException((String)handlerType.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void callHandlerAddedForAllHandlers() {
        DefaultChannelPipeline defaultChannelPipeline = this;
        // MONITORENTER : defaultChannelPipeline
        assert (!this.registered);
        this.registered = true;
        PendingHandlerCallback pendingHandlerCallbackHead = this.pendingHandlerCallbackHead;
        this.pendingHandlerCallbackHead = null;
        // MONITOREXIT : defaultChannelPipeline
        PendingHandlerCallback task = pendingHandlerCallbackHead;
        while (task != null) {
            task.execute();
            task = task.next;
        }
    }

    private void callHandlerCallbackLater(AbstractChannelHandlerContext ctx, boolean added) {
        assert (!this.registered);
        PendingHandlerCallback task = added ? new PendingHandlerAddedTask((DefaultChannelPipeline)this, (AbstractChannelHandlerContext)ctx) : new PendingHandlerRemovedTask((DefaultChannelPipeline)this, (AbstractChannelHandlerContext)ctx);
        PendingHandlerCallback pending = this.pendingHandlerCallbackHead;
        if (pending == null) {
            this.pendingHandlerCallbackHead = task;
            return;
        }
        do {
            if (pending.next == null) {
                pending.next = task;
                return;
            }
            pending = pending.next;
        } while (true);
    }

    private void callHandlerAddedInEventLoop(AbstractChannelHandlerContext newCtx, EventExecutor executor) {
        newCtx.setAddPending();
        executor.execute((Runnable)new Runnable((DefaultChannelPipeline)this, (AbstractChannelHandlerContext)newCtx){
            final /* synthetic */ AbstractChannelHandlerContext val$newCtx;
            final /* synthetic */ DefaultChannelPipeline this$0;
            {
                this.this$0 = this$0;
                this.val$newCtx = abstractChannelHandlerContext;
            }

            public void run() {
                DefaultChannelPipeline.access$100((DefaultChannelPipeline)this.this$0, (AbstractChannelHandlerContext)this.val$newCtx);
            }
        });
    }

    protected void onUnhandledInboundException(Throwable cause) {
        try {
            logger.warn((String)"An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.", (Throwable)cause);
            return;
        }
        finally {
            ReferenceCountUtil.release((Object)cause);
        }
    }

    protected void onUnhandledInboundChannelActive() {
    }

    protected void onUnhandledInboundChannelInactive() {
    }

    protected void onUnhandledInboundMessage(Object msg) {
        try {
            logger.debug((String)"Discarded inbound message {} that reached at the tail of the pipeline. Please check your pipeline configuration.", (Object)msg);
            return;
        }
        finally {
            ReferenceCountUtil.release((Object)msg);
        }
    }

    protected void onUnhandledInboundMessage(ChannelHandlerContext ctx, Object msg) {
        this.onUnhandledInboundMessage((Object)msg);
        if (!logger.isDebugEnabled()) return;
        logger.debug((String)"Discarded message pipeline : {}. Channel : {}.", ctx.pipeline().names(), (Object)ctx.channel());
    }

    protected void onUnhandledInboundChannelReadComplete() {
    }

    protected void onUnhandledInboundUserEventTriggered(Object evt) {
        ReferenceCountUtil.release((Object)evt);
    }

    protected void onUnhandledChannelWritabilityChanged() {
    }

    protected void incrementPendingOutboundBytes(long size) {
        ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
        if (buffer == null) return;
        buffer.incrementPendingOutboundBytes((long)size);
    }

    protected void decrementPendingOutboundBytes(long size) {
        ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
        if (buffer == null) return;
        buffer.decrementPendingOutboundBytes((long)size);
    }

    static /* synthetic */ void access$000(DefaultChannelPipeline x0, AbstractChannelHandlerContext x1) {
        x0.callHandlerRemoved0((AbstractChannelHandlerContext)x1);
    }

    static /* synthetic */ void access$100(DefaultChannelPipeline x0, AbstractChannelHandlerContext x1) {
        x0.callHandlerAdded0((AbstractChannelHandlerContext)x1);
    }

    static /* synthetic */ void access$200(DefaultChannelPipeline x0, AbstractChannelHandlerContext x1, boolean x2) {
        x0.destroyUp((AbstractChannelHandlerContext)x1, (boolean)x2);
    }

    static /* synthetic */ void access$300(DefaultChannelPipeline x0, Thread x1, AbstractChannelHandlerContext x2, boolean x3) {
        x0.destroyDown((Thread)x1, (AbstractChannelHandlerContext)x2, (boolean)x3);
    }

    static /* synthetic */ String access$400() {
        return TAIL_NAME;
    }

    static /* synthetic */ String access$500() {
        return HEAD_NAME;
    }

    static /* synthetic */ Channel access$600(DefaultChannelPipeline x0) {
        return x0.channel;
    }

    static /* synthetic */ void access$700(DefaultChannelPipeline x0) {
        x0.destroy();
    }

    static /* synthetic */ void access$800(DefaultChannelPipeline x0, AbstractChannelHandlerContext x1) {
        x0.atomicRemoveFromHandlerList((AbstractChannelHandlerContext)x1);
    }
}

