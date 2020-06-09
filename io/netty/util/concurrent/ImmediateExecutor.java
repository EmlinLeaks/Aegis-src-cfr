/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import java.util.concurrent.Executor;

public final class ImmediateExecutor
implements Executor {
    public static final ImmediateExecutor INSTANCE = new ImmediateExecutor();

    private ImmediateExecutor() {
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException((String)"command");
        }
        command.run();
    }
}

