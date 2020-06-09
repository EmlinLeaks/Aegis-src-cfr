/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;

public interface EventExecutorChooserFactory {
    public EventExecutorChooser newChooser(EventExecutor[] var1);
}

