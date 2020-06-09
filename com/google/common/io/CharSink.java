/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.io.Closer;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

@GwtIncompatible
public abstract class CharSink {
    protected CharSink() {
    }

    public abstract Writer openStream() throws IOException;

    public Writer openBufferedStream() throws IOException {
        BufferedWriter bufferedWriter;
        Writer writer = this.openStream();
        if (writer instanceof BufferedWriter) {
            bufferedWriter = (BufferedWriter)writer;
            return bufferedWriter;
        }
        bufferedWriter = new BufferedWriter((Writer)writer);
        return bufferedWriter;
    }

    public void write(CharSequence charSequence) throws IOException {
        Preconditions.checkNotNull(charSequence);
        Closer closer = Closer.create();
        try {
            Writer out = closer.register(this.openStream());
            out.append((CharSequence)charSequence);
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

    public void writeLines(Iterable<? extends CharSequence> lines) throws IOException {
        this.writeLines(lines, (String)System.getProperty((String)"line.separator"));
    }

    public void writeLines(Iterable<? extends CharSequence> lines, String lineSeparator) throws IOException {
        Preconditions.checkNotNull(lines);
        Preconditions.checkNotNull(lineSeparator);
        Closer closer = Closer.create();
        try {
            Writer out = closer.register(this.openBufferedStream());
            for (CharSequence line : lines) {
                out.append((CharSequence)line).append((CharSequence)lineSeparator);
            }
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
    public long writeFrom(Readable readable) throws IOException {
        Preconditions.checkNotNull(readable);
        Closer closer = Closer.create();
        try {
            Writer out = closer.register(this.openStream());
            long written = CharStreams.copy((Readable)readable, (Appendable)out);
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

