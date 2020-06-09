/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Closer;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class Closer
implements Closeable {
    private static final Suppressor SUPPRESSOR = SuppressingSuppressor.isAvailable() ? SuppressingSuppressor.INSTANCE : LoggingSuppressor.INSTANCE;
    @VisibleForTesting
    final Suppressor suppressor;
    private final Deque<Closeable> stack = new ArrayDeque<Closeable>((int)4);
    private Throwable thrown;

    public static Closer create() {
        return new Closer((Suppressor)SUPPRESSOR);
    }

    @VisibleForTesting
    Closer(Suppressor suppressor) {
        this.suppressor = Preconditions.checkNotNull(suppressor);
    }

    @CanIgnoreReturnValue
    public <C extends Closeable> C register(@Nullable C closeable) {
        if (closeable == null) return (C)closeable;
        this.stack.addFirst(closeable);
        return (C)closeable;
    }

    public RuntimeException rethrow(Throwable e) throws IOException {
        Preconditions.checkNotNull(e);
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        throw new RuntimeException((Throwable)e);
    }

    public <X extends Exception> RuntimeException rethrow(Throwable e, Class<X> declaredType) throws IOException, Exception {
        Preconditions.checkNotNull(e);
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        Throwables.propagateIfPossible((Throwable)e, declaredType);
        throw new RuntimeException((Throwable)e);
    }

    public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable e, Class<X1> declaredType1, Class<X2> declaredType2) throws IOException, Exception {
        Preconditions.checkNotNull(e);
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        Throwables.propagateIfPossible((Throwable)e, declaredType1, declaredType2);
        throw new RuntimeException((Throwable)e);
    }

    @Override
    public void close() throws IOException {
        Throwable throwable = this.thrown;
        do {
            if (this.stack.isEmpty()) {
                if (this.thrown != null) return;
                if (throwable == null) return;
                Throwables.propagateIfPossible((Throwable)throwable, IOException.class);
                throw new AssertionError((Object)throwable);
            }
            Closeable closeable = this.stack.removeFirst();
            try {
                closeable.close();
            }
            catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                    continue;
                }
                this.suppressor.suppress((Closeable)closeable, (Throwable)throwable, (Throwable)e);
                continue;
            }
            break;
        } while (true);
    }
}

