/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Closer;
import com.google.common.io.LineProcessor;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtIncompatible
public abstract class CharSource {
    protected CharSource() {
    }

    @Beta
    public ByteSource asByteSource(Charset charset) {
        return new AsByteSource((CharSource)this, (Charset)charset);
    }

    public abstract Reader openStream() throws IOException;

    public BufferedReader openBufferedStream() throws IOException {
        BufferedReader bufferedReader;
        Reader reader = this.openStream();
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader)reader;
            return bufferedReader;
        }
        bufferedReader = new BufferedReader((Reader)reader);
        return bufferedReader;
    }

    @Beta
    public Optional<Long> lengthIfKnown() {
        return Optional.absent();
    }

    @Beta
    public long length() throws IOException {
        Optional<Long> lengthIfKnown = this.lengthIfKnown();
        if (lengthIfKnown.isPresent()) {
            return lengthIfKnown.get().longValue();
        }
        Closer closer = Closer.create();
        try {
            Reader reader = closer.register(this.openStream());
            long l = this.countBySkipping((Reader)reader);
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    private long countBySkipping(Reader reader) throws IOException {
        long read;
        long count = 0L;
        while ((read = reader.skip((long)Long.MAX_VALUE)) != 0L) {
            count += read;
        }
        return count;
    }

    @CanIgnoreReturnValue
    public long copyTo(Appendable appendable) throws IOException {
        Preconditions.checkNotNull(appendable);
        Closer closer = Closer.create();
        try {
            Reader reader = closer.register(this.openStream());
            long l = CharStreams.copy((Readable)reader, (Appendable)appendable);
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
    public long copyTo(CharSink sink) throws IOException {
        Preconditions.checkNotNull(sink);
        Closer closer = Closer.create();
        try {
            Reader reader = closer.register(this.openStream());
            Writer writer = closer.register(sink.openStream());
            long l = CharStreams.copy((Readable)reader, (Appendable)writer);
            return l;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    public String read() throws IOException {
        Closer closer = Closer.create();
        try {
            Reader reader = closer.register(this.openStream());
            String string = CharStreams.toString((Readable)reader);
            return string;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    @Nullable
    public String readFirstLine() throws IOException {
        Closer closer = Closer.create();
        try {
            BufferedReader reader = closer.register(this.openBufferedStream());
            String string = reader.readLine();
            return string;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    public ImmutableList<String> readLines() throws IOException {
        Closer closer = Closer.create();
        try {
            String line;
            BufferedReader reader = closer.register(this.openBufferedStream());
            ArrayList<String> result = Lists.newArrayList();
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            ImmutableList<String> immutableList = ImmutableList.copyOf(result);
            return immutableList;
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
    public <T> T readLines(LineProcessor<T> processor) throws IOException {
        Preconditions.checkNotNull(processor);
        Closer closer = Closer.create();
        try {
            Reader reader = closer.register(this.openStream());
            T t = CharStreams.readLines((Readable)reader, processor);
            return (T)((T)t);
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    public boolean isEmpty() throws IOException {
        Optional<Long> lengthIfKnown = this.lengthIfKnown();
        if (lengthIfKnown.isPresent() && lengthIfKnown.get().longValue() == 0L) {
            return true;
        }
        Closer closer = Closer.create();
        try {
            Reader reader = closer.register(this.openStream());
            boolean bl = reader.read() == -1;
            return bl;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    public static CharSource concat(Iterable<? extends CharSource> sources) {
        return new ConcatenatedCharSource(sources);
    }

    public static CharSource concat(Iterator<? extends CharSource> sources) {
        return CharSource.concat(ImmutableList.copyOf(sources));
    }

    public static CharSource concat(CharSource ... sources) {
        return CharSource.concat(ImmutableList.copyOf(sources));
    }

    public static CharSource wrap(CharSequence charSequence) {
        return new CharSequenceCharSource((CharSequence)charSequence);
    }

    public static CharSource empty() {
        return EmptyCharSource.INSTANCE;
    }
}

