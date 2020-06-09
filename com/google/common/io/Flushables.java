/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.io.Flushable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
@GwtIncompatible
public final class Flushables {
    private static final Logger logger = Logger.getLogger((String)Flushables.class.getName());

    private Flushables() {
    }

    public static void flush(Flushable flushable, boolean swallowIOException) throws IOException {
        try {
            flushable.flush();
            return;
        }
        catch (IOException e) {
            if (!swallowIOException) throw e;
            logger.log((Level)Level.WARNING, (String)"IOException thrown while flushing Flushable.", (Throwable)e);
            return;
        }
    }

    public static void flushQuietly(Flushable flushable) {
        try {
            Flushables.flush((Flushable)flushable, (boolean)true);
            return;
        }
        catch (IOException e) {
            logger.log((Level)Level.SEVERE, (String)"IOException should not have been thrown.", (Throwable)e);
        }
    }
}

