/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSink;
import com.google.common.io.Closer;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

@GwtIncompatible
public abstract class ByteSink {
    protected ByteSink() {
    }

    public CharSink asCharSink(Charset charset) {
        return new AsCharSink((ByteSink)this, (Charset)charset, null);
    }

    public abstract OutputStream openStream() throws IOException;

    public OutputStream openBufferedStream() throws IOException {
        BufferedOutputStream bufferedOutputStream;
        OutputStream out = this.openStream();
        if (out instanceof BufferedOutputStream) {
            bufferedOutputStream = (BufferedOutputStream)out;
            return bufferedOutputStream;
        }
        bufferedOutputStream = new BufferedOutputStream((OutputStream)out);
        return bufferedOutputStream;
    }

    public void write(byte[] bytes) throws IOException {
        Preconditions.checkNotNull(bytes);
        Closer closer = Closer.create();
        try {
            OutputStream out = closer.register(this.openStream());
            out.write((byte[])bytes);
            out.flush();
            return;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    @CanIgnoreReturnValue
    public long writeFrom(InputStream input) throws IOException {
        Preconditions.checkNotNull(input);
        Closer closer = Closer.create();
        try {
            OutputStream out = closer.register(this.openStream());
            long written = ByteStreams.copy((InputStream)input, (OutputStream)out);
            out.flush();
            long l = written;
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }
}

