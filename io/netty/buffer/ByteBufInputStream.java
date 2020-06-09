/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ByteBufInputStream
extends InputStream
implements DataInput {
    private final ByteBuf buffer;
    private final int startIndex;
    private final int endIndex;
    private boolean closed;
    private final boolean releaseOnClose;
    private StringBuilder lineBuf;

    public ByteBufInputStream(ByteBuf buffer) {
        this((ByteBuf)buffer, (int)buffer.readableBytes());
    }

    public ByteBufInputStream(ByteBuf buffer, int length) {
        this((ByteBuf)buffer, (int)length, (boolean)false);
    }

    public ByteBufInputStream(ByteBuf buffer, boolean releaseOnClose) {
        this((ByteBuf)buffer, (int)buffer.readableBytes(), (boolean)releaseOnClose);
    }

    public ByteBufInputStream(ByteBuf buffer, int length, boolean releaseOnClose) {
        if (buffer == null) {
            throw new NullPointerException((String)"buffer");
        }
        if (length < 0) {
            if (!releaseOnClose) throw new IllegalArgumentException((String)("length: " + length));
            buffer.release();
            throw new IllegalArgumentException((String)("length: " + length));
        }
        if (length > buffer.readableBytes()) {
            if (!releaseOnClose) throw new IndexOutOfBoundsException((String)("Too many bytes to be read - Needs " + length + ", maximum is " + buffer.readableBytes()));
            buffer.release();
            throw new IndexOutOfBoundsException((String)("Too many bytes to be read - Needs " + length + ", maximum is " + buffer.readableBytes()));
        }
        this.releaseOnClose = releaseOnClose;
        this.buffer = buffer;
        this.startIndex = buffer.readerIndex();
        this.endIndex = this.startIndex + length;
        buffer.markReaderIndex();
    }

    public int readBytes() {
        return this.buffer.readerIndex() - this.startIndex;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
            return;
        }
        finally {
            if (this.releaseOnClose && !this.closed) {
                this.closed = true;
                this.buffer.release();
            }
        }
    }

    @Override
    public int available() throws IOException {
        return this.endIndex - this.buffer.readerIndex();
    }

    @Override
    public void mark(int readlimit) {
        this.buffer.markReaderIndex();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        int available = this.available();
        if (available != 0) return this.buffer.readByte() & 255;
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int available = this.available();
        if (available == 0) {
            return -1;
        }
        len = Math.min((int)available, (int)len);
        this.buffer.readBytes((byte[])b, (int)off, (int)len);
        return len;
    }

    @Override
    public void reset() throws IOException {
        this.buffer.resetReaderIndex();
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= Integer.MAX_VALUE) return (long)this.skipBytes((int)((int)n));
        return (long)this.skipBytes((int)Integer.MAX_VALUE);
    }

    @Override
    public boolean readBoolean() throws IOException {
        this.checkAvailable((int)1);
        if (this.read() == 0) return false;
        return true;
    }

    @Override
    public byte readByte() throws IOException {
        int available = this.available();
        if (available != 0) return this.buffer.readByte();
        throw new EOFException();
    }

    @Override
    public char readChar() throws IOException {
        return (char)this.readShort();
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble((long)this.readLong());
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat((int)this.readInt());
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.readFully((byte[])b, (int)0, (int)b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.checkAvailable((int)len);
        this.buffer.readBytes((byte[])b, (int)off, (int)len);
    }

    @Override
    public int readInt() throws IOException {
        this.checkAvailable((int)4);
        return this.buffer.readInt();
    }

    @Override
    public String readLine() throws IOException {
        int available = this.available();
        if (available == 0) {
            return null;
        }
        if (this.lineBuf != null) {
            this.lineBuf.setLength((int)0);
        }
        block4 : do {
            short c = this.buffer.readUnsignedByte();
            --available;
            switch (c) {
                case 10: {
                    break block4;
                }
                case 13: {
                    if (available <= 0 || (char)this.buffer.getUnsignedByte((int)this.buffer.readerIndex()) != '\n') break block4;
                    this.buffer.skipBytes((int)1);
                    --available;
                    break block4;
                }
                default: {
                    if (this.lineBuf == null) {
                        this.lineBuf = new StringBuilder();
                    }
                    this.lineBuf.append((char)((char)c));
                    if (available > 0) continue block4;
                }
            }
            break;
        } while (true);
        if (this.lineBuf == null) return "";
        if (this.lineBuf.length() <= 0) return "";
        String string = this.lineBuf.toString();
        return string;
    }

    @Override
    public long readLong() throws IOException {
        this.checkAvailable((int)8);
        return this.buffer.readLong();
    }

    @Override
    public short readShort() throws IOException {
        this.checkAvailable((int)2);
        return this.buffer.readShort();
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF((DataInput)this);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.readByte() & 255;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.readShort() & 65535;
    }

    @Override
    public int skipBytes(int n) throws IOException {
        int nBytes = Math.min((int)this.available(), (int)n);
        this.buffer.skipBytes((int)nBytes);
        return nBytes;
    }

    private void checkAvailable(int fieldSize) throws IOException {
        if (fieldSize < 0) {
            throw new IndexOutOfBoundsException((String)"fieldSize cannot be a negative number");
        }
        if (fieldSize <= this.available()) return;
        throw new EOFException((String)("fieldSize is too long! Length is " + fieldSize + ", but maximum is " + this.available()));
    }
}

