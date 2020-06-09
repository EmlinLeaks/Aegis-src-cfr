/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class Closeables {
    @VisibleForTesting
    static final Logger logger = Logger.getLogger((String)Closeables.class.getName());

    private Closeables() {
    }

    public static void close(@Nullable Closeable closeable, boolean swallowIOException) throws IOException {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
            return;
        }
        catch (IOException e) {
            if (!swallowIOException) throw e;
            logger.log((Level)Level.WARNING, (String)"IOException thrown while closing Closeable.", (Throwable)e);
            return;
        }
    }

    public static void closeQuietly(@Nullable InputStream inputStream) {
        try {
            Closeables.close((Closeable)inputStream, (boolean)true);
            return;
        }
        catch (IOException impossible) {
            throw new AssertionError((Object)impossible);
        }
    }

    public static void closeQuietly(@Nullable Reader reader) {
        try {
            Closeables.close((Closeable)reader, (boolean)true);
            return;
        }
        catch (IOException impossible) {
            throw new AssertionError((Object)impossible);
        }
    }
}

