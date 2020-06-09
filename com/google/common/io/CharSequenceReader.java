/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

@GwtIncompatible
final class CharSequenceReader
extends Reader {
    private CharSequence seq;
    private int pos;
    private int mark;

    public CharSequenceReader(CharSequence seq) {
        this.seq = Preconditions.checkNotNull(seq);
    }

    private void checkOpen() throws IOException {
        if (this.seq != null) return;
        throw new IOException((String)"reader closed");
    }

    private boolean hasRemaining() {
        if (this.remaining() <= 0) return false;
        return true;
    }

    private int remaining() {
        return this.seq.length() - this.pos;
    }

    @Override
    public synchronized int read(CharBuffer target) throws IOException {
        Preconditions.checkNotNull(target);
        this.checkOpen();
        if (!this.hasRemaining()) {
            return -1;
        }
        int charsToRead = Math.min((int)target.remaining(), (int)this.remaining());
        int i = 0;
        while (i < charsToRead) {
            target.put((char)this.seq.charAt((int)this.pos++));
            ++i;
        }
        return charsToRead;
    }

    @Override
    public synchronized int read() throws IOException {
        this.checkOpen();
        if (!this.hasRemaining()) return -1;
        int n = (int)this.seq.charAt((int)this.pos++);
        return n;
    }

    @Override
    public synchronized int read(char[] cbuf, int off, int len) throws IOException {
        Preconditions.checkPositionIndexes((int)off, (int)(off + len), (int)cbuf.length);
        this.checkOpen();
        if (!this.hasRemaining()) {
            return -1;
        }
        int charsToRead = Math.min((int)len, (int)this.remaining());
        int i = 0;
        while (i < charsToRead) {
            cbuf[off + i] = this.seq.charAt((int)this.pos++);
            ++i;
        }
        return charsToRead;
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        Preconditions.checkArgument((boolean)(n >= 0L), (String)"n (%s) may not be negative", (long)n);
        this.checkOpen();
        int charsToSkip = (int)Math.min((long)((long)this.remaining()), (long)n);
        this.pos += charsToSkip;
        return (long)charsToSkip;
    }

    @Override
    public synchronized boolean ready() throws IOException {
        this.checkOpen();
        return true;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readAheadLimit) throws IOException {
        Preconditions.checkArgument((boolean)(readAheadLimit >= 0), (String)"readAheadLimit (%s) may not be negative", (int)readAheadLimit);
        this.checkOpen();
        this.mark = this.pos;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.checkOpen();
        this.pos = this.mark;
    }

    @Override
    public synchronized void close() throws IOException {
        this.seq = null;
    }
}

