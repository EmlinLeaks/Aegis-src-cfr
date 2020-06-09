/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Beta
@GwtIncompatible
public final class CountingOutputStream
extends FilterOutputStream {
    private long count;

    public CountingOutputStream(OutputStream out) {
        super((OutputStream)Preconditions.checkNotNull(out));
    }

    public long getCount() {
        return this.count;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write((byte[])b, (int)off, (int)len);
        this.count += (long)len;
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write((int)b);
        ++this.count;
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }
}

