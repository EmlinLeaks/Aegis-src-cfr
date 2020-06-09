/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.stream;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class EndianSwitchableInputStream
extends FilterInputStream
implements DataInput {
    private final ByteOrder endianness;

    public EndianSwitchableInputStream(InputStream stream, ByteOrder endianness) {
        super((InputStream)(stream instanceof DataInputStream ? stream : new DataInputStream((InputStream)stream)));
        this.endianness = endianness;
    }

    public ByteOrder getEndianness() {
        return this.endianness;
    }

    protected DataInputStream getBackingStream() {
        return (DataInputStream)this.in;
    }

    @Override
    public void readFully(byte[] bytes) throws IOException {
        this.getBackingStream().readFully((byte[])bytes);
    }

    @Override
    public void readFully(byte[] bytes, int i, int i1) throws IOException {
        this.getBackingStream().readFully((byte[])bytes, (int)i, (int)i1);
    }

    @Override
    public int skipBytes(int i) throws IOException {
        return this.getBackingStream().skipBytes((int)i);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.getBackingStream().readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.getBackingStream().readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.getBackingStream().readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short ret = this.getBackingStream().readShort();
        if (this.endianness != ByteOrder.LITTLE_ENDIAN) return ret;
        return Short.reverseBytes((short)ret);
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int ret = this.getBackingStream().readUnsignedShort();
        if (this.endianness != ByteOrder.LITTLE_ENDIAN) return ret;
        return (int)((char)(Integer.reverseBytes((int)ret) >> 16));
    }

    @Override
    public char readChar() throws IOException {
        char ret = this.getBackingStream().readChar();
        if (this.endianness != ByteOrder.LITTLE_ENDIAN) return ret;
        return Character.reverseBytes((char)ret);
    }

    @Override
    public int readInt() throws IOException {
        int n;
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            n = Integer.reverseBytes((int)this.getBackingStream().readInt());
            return n;
        }
        n = this.getBackingStream().readInt();
        return n;
    }

    @Override
    public long readLong() throws IOException {
        long l;
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes((long)this.getBackingStream().readLong());
            return l;
        }
        l = this.getBackingStream().readLong();
        return l;
    }

    @Override
    public float readFloat() throws IOException {
        int result = this.readInt();
        if (this.endianness != ByteOrder.LITTLE_ENDIAN) return Float.intBitsToFloat((int)result);
        result = Integer.reverseBytes((int)result);
        return Float.intBitsToFloat((int)result);
    }

    @Override
    public double readDouble() throws IOException {
        long result = this.readLong();
        if (this.endianness != ByteOrder.LITTLE_ENDIAN) return Double.longBitsToDouble((long)result);
        result = Long.reverseBytes((long)result);
        return Double.longBitsToDouble((long)result);
    }

    @Override
    public String readLine() throws IOException {
        return this.getBackingStream().readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return this.getBackingStream().readUTF();
    }
}

