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
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteStreams;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

@Beta
@GwtIncompatible
public final class ByteStreams {
    private static final int ZERO_COPY_CHUNK_SIZE = 524288;
    private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream(){

        public void write(int b) {
        }

        public void write(byte[] b) {
            Preconditions.checkNotNull(b);
        }

        public void write(byte[] b, int off, int len) {
            Preconditions.checkNotNull(b);
        }

        public String toString() {
            return "ByteStreams.nullOutputStream()";
        }
    };

    static byte[] createBuffer() {
        return new byte[8192];
    }

    private ByteStreams() {
    }

    @CanIgnoreReturnValue
    public static long copy(InputStream from, OutputStream to) throws IOException {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to);
        byte[] buf = ByteStreams.createBuffer();
        long total = 0L;
        int r;
        while ((r = from.read((byte[])buf)) != -1) {
            to.write((byte[])buf, (int)0, (int)r);
            total += (long)r;
        }
        return total;
    }

    @CanIgnoreReturnValue
    public static long copy(ReadableByteChannel from, WritableByteChannel to) throws IOException {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to);
        if (from instanceof FileChannel) {
            long oldPosition;
            long copied;
            FileChannel sourceChannel = (FileChannel)from;
            long position = oldPosition = sourceChannel.position();
            do {
                copied = sourceChannel.transferTo((long)position, (long)524288L, (WritableByteChannel)to);
                sourceChannel.position((long)(position += copied));
            } while (copied > 0L || position < sourceChannel.size());
            return position - oldPosition;
        }
        ByteBuffer buf = ByteBuffer.wrap((byte[])ByteStreams.createBuffer());
        long total = 0L;
        while (from.read((ByteBuffer)buf) != -1) {
            buf.flip();
            while (buf.hasRemaining()) {
                total += (long)to.write((ByteBuffer)buf);
            }
            buf.clear();
        }
        return total;
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream((int)Math.max((int)32, (int)in.available()));
        ByteStreams.copy((InputStream)in, (OutputStream)out);
        return out.toByteArray();
    }

    static byte[] toByteArray(InputStream in, int expectedSize) throws IOException {
        int read;
        byte[] bytes = new byte[expectedSize];
        for (int remaining = expectedSize; remaining > 0; remaining -= read) {
            int off = expectedSize - remaining;
            read = in.read((byte[])bytes, (int)off, (int)remaining);
            if (read != -1) continue;
            return Arrays.copyOf((byte[])bytes, (int)off);
        }
        int b = in.read();
        if (b == -1) {
            return bytes;
        }
        FastByteArrayOutputStream out = new FastByteArrayOutputStream(null);
        out.write((int)b);
        ByteStreams.copy((InputStream)in, (OutputStream)out);
        byte[] result = new byte[bytes.length + out.size()];
        System.arraycopy((Object)bytes, (int)0, (Object)result, (int)0, (int)bytes.length);
        out.writeTo((byte[])result, (int)bytes.length);
        return result;
    }

    @CanIgnoreReturnValue
    public static long exhaust(InputStream in) throws IOException {
        long read;
        long total = 0L;
        byte[] buf = ByteStreams.createBuffer();
        while ((read = (long)in.read((byte[])buf)) != -1L) {
            total += read;
        }
        return total;
    }

    public static ByteArrayDataInput newDataInput(byte[] bytes) {
        return ByteStreams.newDataInput((ByteArrayInputStream)new ByteArrayInputStream((byte[])bytes));
    }

    public static ByteArrayDataInput newDataInput(byte[] bytes, int start) {
        Preconditions.checkPositionIndex((int)start, (int)bytes.length);
        return ByteStreams.newDataInput((ByteArrayInputStream)new ByteArrayInputStream((byte[])bytes, (int)start, (int)(bytes.length - start)));
    }

    public static ByteArrayDataInput newDataInput(ByteArrayInputStream byteArrayInputStream) {
        return new ByteArrayDataInputStream((ByteArrayInputStream)Preconditions.checkNotNull(byteArrayInputStream));
    }

    public static ByteArrayDataOutput newDataOutput() {
        return ByteStreams.newDataOutput((ByteArrayOutputStream)new ByteArrayOutputStream());
    }

    public static ByteArrayDataOutput newDataOutput(int size) {
        if (size >= 0) return ByteStreams.newDataOutput((ByteArrayOutputStream)new ByteArrayOutputStream((int)size));
        throw new IllegalArgumentException((String)String.format((String)"Invalid size: %s", (Object[])new Object[]{Integer.valueOf((int)size)}));
    }

    public static ByteArrayDataOutput newDataOutput(ByteArrayOutputStream byteArrayOutputSteam) {
        return new ByteArrayDataOutputStream((ByteArrayOutputStream)Preconditions.checkNotNull(byteArrayOutputSteam));
    }

    public static OutputStream nullOutputStream() {
        return NULL_OUTPUT_STREAM;
    }

    public static InputStream limit(InputStream in, long limit) {
        return new LimitedInputStream((InputStream)in, (long)limit);
    }

    public static void readFully(InputStream in, byte[] b) throws IOException {
        ByteStreams.readFully((InputStream)in, (byte[])b, (int)0, (int)b.length);
    }

    public static void readFully(InputStream in, byte[] b, int off, int len) throws IOException {
        int read = ByteStreams.read((InputStream)in, (byte[])b, (int)off, (int)len);
        if (read == len) return;
        throw new EOFException((String)("reached end of stream after reading " + read + " bytes; " + len + " bytes expected"));
    }

    public static void skipFully(InputStream in, long n) throws IOException {
        long skipped = ByteStreams.skipUpTo((InputStream)in, (long)n);
        if (skipped >= n) return;
        throw new EOFException((String)("reached end of stream after skipping " + skipped + " bytes; " + n + " bytes expected"));
    }

    static long skipUpTo(InputStream in, long n) throws IOException {
        long totalSkipped = 0L;
        byte[] buf = ByteStreams.createBuffer();
        while (totalSkipped < n) {
            int skip;
            long remaining = n - totalSkipped;
            long skipped = ByteStreams.skipSafely((InputStream)in, (long)remaining);
            if (skipped == 0L && (skipped = (long)in.read((byte[])buf, (int)0, (int)(skip = (int)Math.min((long)remaining, (long)((long)buf.length))))) == -1L) {
                return totalSkipped;
            }
            totalSkipped += skipped;
        }
        return totalSkipped;
    }

    private static long skipSafely(InputStream in, long n) throws IOException {
        int available = in.available();
        if (available == 0) {
            return 0L;
        }
        long l = in.skip((long)Math.min((long)((long)available), (long)n));
        return l;
    }

    @CanIgnoreReturnValue
    public static <T> T readBytes(InputStream input, ByteProcessor<T> processor) throws IOException {
        int read;
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(processor);
        byte[] buf = ByteStreams.createBuffer();
        do {
            if ((read = input.read((byte[])buf)) == -1) return (T)processor.getResult();
        } while (processor.processBytes((byte[])buf, (int)0, (int)read));
        return (T)processor.getResult();
    }

    @CanIgnoreReturnValue
    public static int read(InputStream in, byte[] b, int off, int len) throws IOException {
        Preconditions.checkNotNull(in);
        Preconditions.checkNotNull(b);
        if (len < 0) {
            throw new IndexOutOfBoundsException((String)"len is negative");
        }
        int total = 0;
        while (total < len) {
            int result = in.read((byte[])b, (int)(off + total), (int)(len - total));
            if (result == -1) {
                return total;
            }
            total += result;
        }
        return total;
    }
}

