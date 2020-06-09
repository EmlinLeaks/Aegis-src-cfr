/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.PrimitiveSink;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

@GwtIncompatible
public abstract class ByteSource {
    protected ByteSource() {
    }

    public CharSource asCharSource(Charset charset) {
        return new AsCharSource((ByteSource)this, (Charset)charset);
    }

    public abstract InputStream openStream() throws IOException;

    public InputStream openBufferedStream() throws IOException {
        BufferedInputStream bufferedInputStream;
        InputStream in = this.openStream();
        if (in instanceof BufferedInputStream) {
            bufferedInputStream = (BufferedInputStream)in;
            return bufferedInputStream;
        }
        bufferedInputStream = new BufferedInputStream((InputStream)in);
        return bufferedInputStream;
    }

    public ByteSource slice(long offset, long length) {
        return new SlicedByteSource((ByteSource)this, (long)offset, (long)length);
    }

    public boolean isEmpty() throws IOException {
        Optional<Long> sizeIfKnown = this.sizeIfKnown();
        if (sizeIfKnown.isPresent() && sizeIfKnown.get().longValue() == 0L) {
            return true;
        }
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            boolean bl = in.read() == -1;
            return bl;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    @Beta
    public Optional<Long> sizeIfKnown() {
        return Optional.absent();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long size() throws IOException {
        Optional<Long> sizeIfKnown = this.sizeIfKnown();
        if (sizeIfKnown.isPresent()) {
            return sizeIfKnown.get().longValue();
        }
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            long l = this.countBySkipping((InputStream)in);
            return l;
        }
        catch (IOException e) {
        }
        finally {
            closer.close();
        }
        closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            long l = ByteStreams.exhaust((InputStream)in);
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    private long countBySkipping(InputStream in) throws IOException {
        long skipped;
        long count = 0L;
        while ((skipped = ByteStreams.skipUpTo((InputStream)in, (long)Integer.MAX_VALUE)) > 0L) {
            count += skipped;
        }
        return count;
    }

    @CanIgnoreReturnValue
    public long copyTo(OutputStream output) throws IOException {
        Preconditions.checkNotNull(output);
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            long l = ByteStreams.copy((InputStream)in, (OutputStream)output);
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    @CanIgnoreReturnValue
    public long copyTo(ByteSink sink) throws IOException {
        Preconditions.checkNotNull(sink);
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            OutputStream out = closer.register(sink.openStream());
            long l = ByteStreams.copy((InputStream)in, (OutputStream)out);
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    public byte[] read() throws IOException {
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            byte[] arrby = ByteStreams.toByteArray((InputStream)in);
            return arrby;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    @Beta
    @CanIgnoreReturnValue
    public <T> T read(ByteProcessor<T> processor) throws IOException {
        Preconditions.checkNotNull(processor);
        Closer closer = Closer.create();
        try {
            InputStream in = closer.register(this.openStream());
            T t = ByteStreams.readBytes((InputStream)in, processor);
            return (T)((T)t);
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    public HashCode hash(HashFunction hashFunction) throws IOException {
        Hasher hasher = hashFunction.newHasher();
        this.copyTo((OutputStream)Funnels.asOutputStream((PrimitiveSink)hasher));
        return hasher.hash();
    }

    public boolean contentEquals(ByteSource other) throws IOException {
        Preconditions.checkNotNull(other);
        byte[] buf1 = ByteStreams.createBuffer();
        byte[] buf2 = ByteStreams.createBuffer();
        Closer closer = Closer.create();
        try {
            InputStream in1 = closer.register(this.openStream());
            InputStream in2 = closer.register(other.openStream());
            do {
                int read1;
                int read2;
                if ((read1 = ByteStreams.read((InputStream)in1, (byte[])buf1, (int)0, (int)buf1.length)) != (read2 = ByteStreams.read((InputStream)in2, (byte[])buf2, (int)0, (int)buf2.length)) || !Arrays.equals((byte[])buf1, (byte[])buf2)) {
                    boolean bl = false;
                    return bl;
                }
                if (read1 != buf1.length) {
                    boolean bl = true;
                    return bl;
                }
                continue;
                break;
            } while (true);
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    public static ByteSource concat(Iterable<? extends ByteSource> sources) {
        return new ConcatenatedByteSource(sources);
    }

    public static ByteSource concat(Iterator<? extends ByteSource> sources) {
        return ByteSource.concat(ImmutableList.copyOf(sources));
    }

    public static ByteSource concat(ByteSource ... sources) {
        return ByteSource.concat(ImmutableList.copyOf(sources));
    }

    public static ByteSource wrap(byte[] b) {
        return new ByteArrayByteSource((byte[])b);
    }

    public static ByteSource empty() {
        return EmptyByteSource.INSTANCE;
    }
}

