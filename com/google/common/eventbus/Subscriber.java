/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.j2objc.annotations.Weak
 *  javax.annotation.Nullable
 */
package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscriber;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.j2objc.annotations.Weak;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

class Subscriber {
    @Weak
    private EventBus bus;
    @VisibleForTesting
    final Object target;
    private final Method method;
    private final Executor executor;

    static Subscriber create(EventBus bus, Object listener, Method method) {
        Subscriber subscriber;
        if (Subscriber.isDeclaredThreadSafe((Method)method)) {
            subscriber = new Subscriber((EventBus)bus, (Object)listener, (Method)method);
            return subscriber;
        }
        subscriber = new SynchronizedSubscriber((EventBus)bus, (Object)listener, (Method)method, null);
        return subscriber;
    }

    private Subscriber(EventBus bus, Object target, Method method) {
        this.bus = bus;
        this.target = Preconditions.checkNotNull(target);
        this.method = method;
        method.setAccessible((boolean)true);
        this.executor = bus.executor();
    }

    final void dispatchEvent(Object event) {
        this.executor.execute((Runnable)new Runnable((Subscriber)this, (Object)event){
            final /* synthetic */ Object val$event;
            final /* synthetic */ Subscriber this$0;
            {
                this.this$0 = subscriber;
                this.val$event = object;
            }

            public void run() {
                try {
                    this.this$0.invokeSubscriberMethod((Object)this.val$event);
                    return;
                }
                catch (InvocationTargetException e) {
                    Subscriber.access$200((Subscriber)this.this$0).handleSubscriberException((Throwable)e.getCause(), (SubscriberExceptionContext)Subscriber.access$100((Subscriber)this.this$0, (Object)this.val$event));
                }
            }
        });
    }

    @VisibleForTesting
    void invokeSubscriberMethod(Object event) throws InvocationTargetException {
        try {
            this.method.invoke((Object)this.target, (Object[])new Object[]{Preconditions.checkNotNull(event)});
            return;
        }
        catch (IllegalArgumentException e) {
            throw new Error((String)("Method rejected target/argument: " + event), (Throwable)e);
        }
        catch (IllegalAccessException e) {
            throw new Error((String)("Method became inaccessible: " + event), (Throwable)e);
        }
        catch (InvocationTargetException e) {
            if (!(e.getCause() instanceof Error)) throw e;
            throw (Error)e.getCause();
        }
    }

    private SubscriberExceptionContext context(Object event) {
        return new SubscriberExceptionContext((EventBus)this.bus, (Object)event, (Object)this.target, (Method)this.method);
    }

    public final int hashCode() {
        return (31 + this.method.hashCode()) * 31 + System.identityHashCode((Object)this.target);
    }

    public final boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Subscriber)) return false;
        Subscriber that = (Subscriber)obj;
        if (this.target != that.target) return false;
        if (!this.method.equals((Object)that.method)) return false;
        return true;
    }

    private static boolean isDeclaredThreadSafe(Method method) {
        if (method.getAnnotation(AllowConcurrentEvents.class) == null) return false;
        return true;
    }

    static /* synthetic */ SubscriberExceptionContext access$100(Subscriber x0, Object x1) {
        return x0.context((Object)x1);
    }

    static /* synthetic */ EventBus access$200(Subscriber x0) {
        return x0.bus;
    }
}

