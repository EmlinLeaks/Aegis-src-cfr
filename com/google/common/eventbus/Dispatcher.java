/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.eventbus;

import com.google.common.eventbus.Dispatcher;
import com.google.common.eventbus.Subscriber;
import java.util.Iterator;

abstract class Dispatcher {
    Dispatcher() {
    }

    static Dispatcher perThreadDispatchQueue() {
        return new PerThreadQueuedDispatcher(null);
    }

    static Dispatcher legacyAsync() {
        return new LegacyAsyncDispatcher(null);
    }

    static Dispatcher immediate() {
        return ImmediateDispatcher.INSTANCE;
    }

    abstract void dispatch(Object var1, Iterator<Subscriber> var2);
}

