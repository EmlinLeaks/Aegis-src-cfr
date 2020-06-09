/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import io.netty.channel.unix.Errors;
import io.netty.channel.unix.Limits;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FileDescriptor {
    private static final AtomicIntegerFieldUpdater<FileDescriptor> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(FileDescriptor.class, (String)"state");
    private static final int STATE_CLOSED_MASK = 1;
    private static final int STATE_INPUT_SHUTDOWN_MASK = 2;
    private static final int STATE_OUTPUT_SHUTDOWN_MASK = 4;
    private static final int STATE_ALL_MASK = 7;
    volatile int state;
    final int fd;

    public FileDescriptor(int fd) {
        ObjectUtil.checkPositiveOrZero((int)fd, (String)"fd");
        this.fd = fd;
    }

    public final int intValue() {
        return this.fd;
    }

    public void close() throws IOException {
        int state;
        do {
            if (!FileDescriptor.isClosed((int)(state = this.state))) continue;
            return;
        } while (!this.casState((int)state, (int)(state | 7)));
        int res = FileDescriptor.close((int)this.fd);
        if (res >= 0) return;
        throw Errors.newIOException((String)"close", (int)res);
    }

    public boolean isOpen() {
        if (FileDescriptor.isClosed((int)this.state)) return false;
        return true;
    }

    public final int write(ByteBuffer buf, int pos, int limit) throws IOException {
        int res = FileDescriptor.write((int)this.fd, (ByteBuffer)buf, (int)pos, (int)limit);
        if (res < 0) return Errors.ioResult((String)"write", (int)res);
        return res;
    }

    public final int writeAddress(long address, int pos, int limit) throws IOException {
        int res = FileDescriptor.writeAddress((int)this.fd, (long)address, (int)pos, (int)limit);
        if (res < 0) return Errors.ioResult((String)"writeAddress", (int)res);
        return res;
    }

    public final long writev(ByteBuffer[] buffers, int offset, int length, long maxBytesToWrite) throws IOException {
        long res = FileDescriptor.writev((int)this.fd, (ByteBuffer[])buffers, (int)offset, (int)Math.min((int)Limits.IOV_MAX, (int)length), (long)maxBytesToWrite);
        if (res < 0L) return (long)Errors.ioResult((String)"writev", (int)((int)res));
        return res;
    }

    public final long writevAddresses(long memoryAddress, int length) throws IOException {
        long res = FileDescriptor.writevAddresses((int)this.fd, (long)memoryAddress, (int)length);
        if (res < 0L) return (long)Errors.ioResult((String)"writevAddresses", (int)((int)res));
        return res;
    }

    public final int read(ByteBuffer buf, int pos, int limit) throws IOException {
        int res = FileDescriptor.read((int)this.fd, (ByteBuffer)buf, (int)pos, (int)limit);
        if (res > 0) {
            return res;
        }
        if (res != 0) return Errors.ioResult((String)"read", (int)res);
        return -1;
    }

    public final int readAddress(long address, int pos, int limit) throws IOException {
        int res = FileDescriptor.readAddress((int)this.fd, (long)address, (int)pos, (int)limit);
        if (res > 0) {
            return res;
        }
        if (res != 0) return Errors.ioResult((String)"readAddress", (int)res);
        return -1;
    }

    public String toString() {
        return "FileDescriptor{fd=" + this.fd + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileDescriptor)) {
            return false;
        }
        if (this.fd != ((FileDescriptor)o).fd) return false;
        return true;
    }

    public int hashCode() {
        return this.fd;
    }

    public static FileDescriptor from(String path) throws IOException {
        ObjectUtil.checkNotNull(path, (String)"path");
        int res = FileDescriptor.open((String)path);
        if (res >= 0) return new FileDescriptor((int)res);
        throw Errors.newIOException((String)"open", (int)res);
    }

    public static FileDescriptor from(File file) throws IOException {
        return FileDescriptor.from((String)ObjectUtil.checkNotNull(file, (String)"file").getPath());
    }

    public static FileDescriptor[] pipe() throws IOException {
        long res = FileDescriptor.newPipe();
        if (res >= 0L) return new FileDescriptor[]{new FileDescriptor((int)((int)(res >>> 32))), new FileDescriptor((int)((int)res))};
        throw Errors.newIOException((String)"newPipe", (int)((int)res));
    }

    final boolean casState(int expected, int update) {
        return stateUpdater.compareAndSet((FileDescriptor)this, (int)expected, (int)update);
    }

    static boolean isClosed(int state) {
        if ((state & 1) == 0) return false;
        return true;
    }

    static boolean isInputShutdown(int state) {
        if ((state & 2) == 0) return false;
        return true;
    }

    static boolean isOutputShutdown(int state) {
        if ((state & 4) == 0) return false;
        return true;
    }

    static int inputShutdown(int state) {
        return state | 2;
    }

    static int outputShutdown(int state) {
        return state | 4;
    }

    private static native int open(String var0);

    private static native int close(int var0);

    private static native int write(int var0, ByteBuffer var1, int var2, int var3);

    private static native int writeAddress(int var0, long var1, int var3, int var4);

    private static native long writev(int var0, ByteBuffer[] var1, int var2, int var3, long var4);

    private static native long writevAddresses(int var0, long var1, int var3);

    private static native int read(int var0, ByteBuffer var1, int var2, int var3);

    private static native int readAddress(int var0, long var1, int var3, int var4);

    private static native long newPipe();
}

