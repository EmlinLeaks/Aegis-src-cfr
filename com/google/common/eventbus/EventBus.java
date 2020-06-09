/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Dispatcher;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscriber;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.eventbus.SubscriberRegistry;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public class EventBus {
    private static final Logger logger = Logger.getLogger((String)EventBus.class.getName());
    private final String identifier;
    private final Executor executor;
    private final SubscriberExceptionHandler exceptionHandler;
    private final SubscriberRegistry subscribers = new SubscriberRegistry((EventBus)this);
    private final Dispatcher dispatcher;

    public EventBus() {
        this((String)"default");
    }

    public EventBus(String identifier) {
        this((String)identifier, (Executor)MoreExecutors.directExecutor(), (Dispatcher)Dispatcher.perThreadDispatchQueue(), (SubscriberExceptionHandler)LoggingHandler.INSTANCE);
    }

    public EventBus(SubscriberExceptionHandler exceptionHandler) {
        this((String)"default", (Executor)MoreExecutors.directExecutor(), (Dispatcher)Dispatcher.perThreadDispatchQueue(), (SubscriberExceptionHandler)exceptionHandler);
    }

    EventBus(String identifier, Executor executor, Dispatcher dispatcher, SubscriberExceptionHandler exceptionHandler) {
        this.identifier = Preconditions.checkNotNull(identifier);
        this.executor = Preconditions.checkNotNull(executor);
        this.dispatcher = Preconditions.checkNotNull(dispatcher);
        this.exceptionHandler = Preconditions.checkNotNull(exceptionHandler);
    }

    public final String identifier() {
        return this.identifier;
    }

    final Executor executor() {
        return this.executor;
    }

    void handleSubscriberException(Throwable e, SubscriberExceptionContext context) {
        Preconditions.checkNotNull(e);
        Preconditions.checkNotNull(context);
        try {
            this.exceptionHandler.handleException((Throwable)e, (SubscriberExceptionContext)context);
            return;
        }
        catch (Throwable e2) {
            logger.log((Level)Level.SEVERE, (String)String.format((Locale)Locale.ROOT, (String)"Exception %s thrown while handling exception: %s", (Object[])new Object[]{e2, e}), (Throwable)e2);
        }
    }

    public void register(Object object) {
        this.subscribers.register((Object)object);
    }

    public void unregister(Object object) {
        this.subscribers.unregister((Object)object);
    }

    public void post(Object event) {
        Iterator<Subscriber> eventSubscribers = this.subscribers.getSubscribers((Object)event);
        if (eventSubscribers.hasNext()) {
            this.dispatcher.dispatch((Object)event, eventSubscribers);
            return;
        }
        if (event instanceof DeadEvent) return;
        this.post((Object)new DeadEvent((Object)this, (Object)event));
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).addValue((Object)this.identifier).toString();
    }
}

