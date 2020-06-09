/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtIncompatible
class MultiReader
extends Reader {
    private final Iterator<? extends CharSource> it;
    private Reader current;

    MultiReader(Iterator<? extends CharSource> readers) throws IOException {
        this.it = readers;
        this.advance();
    }

    private void advance() throws IOException {
        this.close();
        if (!this.it.hasNext()) return;
        this.current = this.it.next().openStream();
    }

    @Override
    public int read(@Nullable char[] cbuf, int off, int len) throws IOException {
        if (this.current == null) {
            return -1;
        }
        int result = this.current.read((char[])cbuf, (int)off, (int)len);
        if (result != -1) return result;
        this.advance();
        return this.read((char[])cbuf, (int)off, (int)len);
    }

    @Override
    public long skip(long n) throws IOException {
        Preconditions.checkArgument((boolean)(n >= 0L), (Object)"n is negative");
        if (n <= 0L) return 0L;
        while (this.current != null) {
            long result = this.current.skip((long)n);
            if (result > 0L) {
                return result;
            }
            this.advance();
        }
        return 0L;
    }

    @Override
    public boolean ready() throws IOException {
        if (this.current == null) return false;
        if (!this.current.ready()) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (this.current == null) return;
        try {
            this.current.close();
            return;
        }
        finally {
            this.current = null;
        }
    }
}

