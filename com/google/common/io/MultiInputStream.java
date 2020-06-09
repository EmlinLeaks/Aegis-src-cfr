/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtIncompatible
final class MultiInputStream
extends InputStream {
    private Iterator<? extends ByteSource> it;
    private InputStream in;

    public MultiInputStream(Iterator<? extends ByteSource> it) throws IOException {
        this.it = Preconditions.checkNotNull(it);
        this.advance();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (this.in == null) return;
        try {
            this.in.close();
            return;
        }
        finally {
            this.in = null;
        }
    }

    private void advance() throws IOException {
        this.close();
        if (!this.it.hasNext()) return;
        this.in = this.it.next().openStream();
    }

    @Override
    public int available() throws IOException {
        if (this.in != null) return this.in.available();
        return 0;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        if (this.in == null) {
            return -1;
        }
        int result = this.in.read();
        if (result != -1) return result;
        this.advance();
        return this.read();
    }

    @Override
    public int read(@Nullable byte[] b, int off, int len) throws IOException {
        if (this.in == null) {
            return -1;
        }
        int result = this.in.read((byte[])b, (int)off, (int)len);
        if (result != -1) return result;
        this.advance();
        return this.read((byte[])b, (int)off, (int)len);
    }

    @Override
    public long skip(long n) throws IOException {
        if (this.in == null) return 0L;
        if (n <= 0L) {
            return 0L;
        }
        long result = this.in.skip((long)n);
        if (result != 0L) {
            return result;
        }
        if (this.read() != -1) return 1L + this.in.skip((long)(n - 1L));
        return 0L;
    }
}

