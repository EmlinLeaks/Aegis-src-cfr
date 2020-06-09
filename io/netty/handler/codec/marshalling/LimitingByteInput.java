/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.jboss.marshalling.ByteInput
 */
package io.netty.handler.codec.marshalling;

import io.netty.handler.codec.marshalling.LimitingByteInput;
import java.io.IOException;
import org.jboss.marshalling.ByteInput;

class LimitingByteInput
implements ByteInput {
    private static final TooBigObjectException EXCEPTION = new TooBigObjectException();
    private final ByteInput input;
    private final long limit;
    private long read;

    LimitingByteInput(ByteInput input, long limit) {
        if (limit <= 0L) {
            throw new IllegalArgumentException((String)"The limit MUST be > 0");
        }
        this.input = input;
        this.limit = limit;
    }

    public void close() throws IOException {
    }

    public int available() throws IOException {
        return this.readable((int)this.input.available());
    }

    public int read() throws IOException {
        int readable = this.readable((int)1);
        if (readable <= 0) throw EXCEPTION;
        int b = this.input.read();
        ++this.read;
        return b;
    }

    public int read(byte[] array) throws IOException {
        return this.read((byte[])array, (int)0, (int)array.length);
    }

    public int read(byte[] array, int offset, int length) throws IOException {
        int readable = this.readable((int)length);
        if (readable <= 0) throw EXCEPTION;
        int i = this.input.read((byte[])array, (int)offset, (int)readable);
        this.read += (long)i;
        return i;
    }

    public long skip(long bytes) throws IOException {
        int readable = this.readable((int)((int)bytes));
        if (readable <= 0) throw EXCEPTION;
        long i = this.input.skip((long)((long)readable));
        this.read += i;
        return i;
    }

    private int readable(int length) {
        return (int)Math.min((long)((long)length), (long)(this.limit - this.read));
    }
}

