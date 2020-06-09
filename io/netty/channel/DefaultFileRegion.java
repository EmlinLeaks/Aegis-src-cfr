/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.FileRegion;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class DefaultFileRegion
extends AbstractReferenceCounted
implements FileRegion {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
    private final File f;
    private final long position;
    private final long count;
    private long transferred;
    private FileChannel file;

    public DefaultFileRegion(FileChannel file, long position, long count) {
        if (file == null) {
            throw new NullPointerException((String)"file");
        }
        ObjectUtil.checkPositiveOrZero((long)position, (String)"position");
        ObjectUtil.checkPositiveOrZero((long)count, (String)"count");
        this.file = file;
        this.position = position;
        this.count = count;
        this.f = null;
    }

    public DefaultFileRegion(File f, long position, long count) {
        if (f == null) {
            throw new NullPointerException((String)"f");
        }
        ObjectUtil.checkPositiveOrZero((long)position, (String)"position");
        ObjectUtil.checkPositiveOrZero((long)count, (String)"count");
        this.position = position;
        this.count = count;
        this.f = f;
    }

    public boolean isOpen() {
        if (this.file == null) return false;
        return true;
    }

    public void open() throws IOException {
        if (this.isOpen()) return;
        if (this.refCnt() <= 0) return;
        this.file = new RandomAccessFile((File)this.f, (String)"r").getChannel();
    }

    @Override
    public long position() {
        return this.position;
    }

    @Override
    public long count() {
        return this.count;
    }

    @Deprecated
    @Override
    public long transfered() {
        return this.transferred;
    }

    @Override
    public long transferred() {
        return this.transferred;
    }

    @Override
    public long transferTo(WritableByteChannel target, long position) throws IOException {
        long count = this.count - position;
        if (count < 0L) throw new IllegalArgumentException((String)("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')'));
        if (position < 0L) {
            throw new IllegalArgumentException((String)("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')'));
        }
        if (count == 0L) {
            return 0L;
        }
        if (this.refCnt() == 0) {
            throw new IllegalReferenceCountException((int)0);
        }
        this.open();
        long written = this.file.transferTo((long)(this.position + position), (long)count, (WritableByteChannel)target);
        if (written > 0L) {
            this.transferred += written;
            return written;
        }
        if (written != 0L) return written;
        DefaultFileRegion.validate((DefaultFileRegion)this, (long)position);
        return written;
    }

    @Override
    protected void deallocate() {
        FileChannel file = this.file;
        if (file == null) {
            return;
        }
        this.file = null;
        try {
            file.close();
            return;
        }
        catch (IOException e) {
            logger.warn((String)"Failed to close a file.", (Throwable)e);
        }
    }

    @Override
    public FileRegion retain() {
        super.retain();
        return this;
    }

    @Override
    public FileRegion retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public FileRegion touch() {
        return this;
    }

    @Override
    public FileRegion touch(Object hint) {
        return this;
    }

    static void validate(DefaultFileRegion region, long position) throws IOException {
        long count = region.count - position;
        long size = region.file.size();
        if (region.position + count + position <= size) return;
        throw new IOException((String)("Underlying file size " + size + " smaller then requested count " + region.count));
    }
}

