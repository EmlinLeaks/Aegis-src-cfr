/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.AppendableWriter;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;
import com.google.common.io.LineReader;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

@Beta
@GwtIncompatible
public final class CharStreams {
    static CharBuffer createBuffer() {
        return CharBuffer.allocate((int)2048);
    }

    private CharStreams() {
    }

    @CanIgnoreReturnValue
    public static long copy(Readable from, Appendable to) throws IOException {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to);
        CharBuffer buf = CharStreams.createBuffer();
        long total = 0L;
        while (from.read((CharBuffer)buf) != -1) {
            buf.flip();
            to.append((CharSequence)buf);
            total += (long)buf.remaining();
            buf.clear();
        }
        return total;
    }

    public static String toString(Readable r) throws IOException {
        return CharStreams.toStringBuilder((Readable)r).toString();
    }

    private static StringBuilder toStringBuilder(Readable r) throws IOException {
        StringBuilder sb = new StringBuilder();
        CharStreams.copy((Readable)r, (Appendable)sb);
        return sb;
    }

    public static List<String> readLines(Readable r) throws IOException {
        String line;
        ArrayList<String> result = new ArrayList<String>();
        LineReader lineReader = new LineReader((Readable)r);
        while ((line = lineReader.readLine()) != null) {
            result.add((String)line);
        }
        return result;
    }

    @CanIgnoreReturnValue
    public static <T> T readLines(Readable readable, LineProcessor<T> processor) throws IOException {
        String line;
        Preconditions.checkNotNull(readable);
        Preconditions.checkNotNull(processor);
        LineReader lineReader = new LineReader((Readable)readable);
        do {
            if ((line = lineReader.readLine()) == null) return (T)processor.getResult();
        } while (processor.processLine((String)line));
        return (T)((T)processor.getResult());
    }

    @CanIgnoreReturnValue
    public static long exhaust(Readable readable) throws IOException {
        long read;
        long total = 0L;
        CharBuffer buf = CharStreams.createBuffer();
        while ((read = (long)readable.read((CharBuffer)buf)) != -1L) {
            total += read;
            buf.clear();
        }
        return total;
    }

    public static void skipFully(Reader reader, long n) throws IOException {
        Preconditions.checkNotNull(reader);
        while (n > 0L) {
            long amt = reader.skip((long)n);
            if (amt == 0L) {
                throw new EOFException();
            }
            n -= amt;
        }
    }

    public static Writer nullWriter() {
        return NullWriter.INSTANCE;
    }

    public static Writer asWriter(Appendable target) {
        if (!(target instanceof Writer)) return new AppendableWriter((Appendable)target);
        return (Writer)target;
    }
}

