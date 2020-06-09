/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultEventExecutorChooserFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;

public final class DefaultEventExecutorChooserFactory
implements EventExecutorChooserFactory {
    public static final DefaultEventExecutorChooserFactory INSTANCE = new DefaultEventExecutorChooserFactory();

    private DefaultEventExecutorChooserFactory() {
    }

    @Override
    public EventExecutorChooserFactory.EventExecutorChooser newChooser(EventExecutor[] executors) {
        if (!DefaultEventExecutorChooserFactory.isPowerOfTwo((int)executors.length)) return new GenericEventExecutorChooser((EventExecutor[])executors);
        return new PowerOfTwoEventExecutorChooser((EventExecutor[])executors);
    }

    private static boolean isPowerOfTwo(int val) {
        if ((val & -val) != val) return false;
        return true;
    }
}

