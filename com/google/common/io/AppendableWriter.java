/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.Nullable;

@GwtIncompatible
class AppendableWriter
extends Writer {
    private final Appendable target;
    private boolean closed;

    AppendableWriter(Appendable target) {
        this.target = Preconditions.checkNotNull(target);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.checkNotClosed();
        this.target.append((CharSequence)new String((char[])cbuf, (int)off, (int)len));
    }

    @Override
    public void flush() throws IOException {
        this.checkNotClosed();
        if (!(this.target instanceof Flushable)) return;
        ((Flushable)((Object)this.target)).flush();
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        if (!(this.target instanceof Closeable)) return;
        ((Closeable)((Object)this.target)).close();
    }

    @Override
    public void write(int c) throws IOException {
        this.checkNotClosed();
        this.target.append((char)((char)c));
    }

    @Override
    public void write(@Nullable String str) throws IOException {
        this.checkNotClosed();
        this.target.append((CharSequence)str);
    }

    @Override
    public void write(@Nullable String str, int off, int len) throws IOException {
        this.checkNotClosed();
        this.target.append((CharSequence)str, (int)off, (int)(off + len));
    }

    @Override
    public Writer append(char c) throws IOException {
        this.checkNotClosed();
        this.target.append((char)c);
        return this;
    }

    @Override
    public Writer append(@Nullable CharSequence charSeq) throws IOException {
        this.checkNotClosed();
        this.target.append((CharSequence)charSeq);
        return this;
    }

    @Override
    public Writer append(@Nullable CharSequence charSeq, int start, int end) throws IOException {
        this.checkNotClosed();
        this.target.append((CharSequence)charSeq, (int)start, (int)end);
        return this;
    }

    private void checkNotClosed() throws IOException {
        if (!this.closed) return;
        throw new IOException((String)"Cannot write to a closed writer.");
    }
}

