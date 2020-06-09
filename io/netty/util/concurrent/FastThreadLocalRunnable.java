/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;

final class FastThreadLocalRunnable
implements Runnable {
    private final Runnable runnable;

    private FastThreadLocalRunnable(Runnable runnable) {
        this.runnable = ObjectUtil.checkNotNull(runnable, (String)"runnable");
    }

    @Override
    public void run() {
        try {
            this.runnable.run();
            return;
        }
        finally {
            FastThreadLocal.removeAll();
        }
    }

    static Runnable wrap(Runnable runnable) {
        Runnable runnable2;
        if (runnable instanceof FastThreadLocalRunnable) {
            runnable2 = runnable;
            return runnable2;
        }
        runnable2 = new FastThreadLocalRunnable((Runnable)runnable);
        return runnable2;
    }
}

