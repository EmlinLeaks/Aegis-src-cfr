/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.eventbus.Dispatcher;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import java.util.concurrent.Executor;

@Beta
public class AsyncEventBus
extends EventBus {
    public AsyncEventBus(String identifier, Executor executor) {
        super((String)identifier, (Executor)executor, (Dispatcher)Dispatcher.legacyAsync(), (SubscriberExceptionHandler)EventBus.LoggingHandler.INSTANCE);
    }

    public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler) {
        super((String)"default", (Executor)executor, (Dispatcher)Dispatcher.legacyAsync(), (SubscriberExceptionHandler)subscriberExceptionHandler);
    }

    public AsyncEventBus(Executor executor) {
        super((String)"default", (Executor)executor, (Dispatcher)Dispatcher.legacyAsync(), (SubscriberExceptionHandler)EventBus.LoggingHandler.INSTANCE);
    }
}

