/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.stream;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

public class EndianSwitchableOutputStream
extends FilterOutputStream
implements DataOutput {
    private final ByteOrder endianness;

    public EndianSwitchableOutputStream(OutputStream backingStream, ByteOrder endianness) {
        super((OutputStream)(backingStream instanceof DataOutputStream ? (DataOutputStream)backingStream : new DataOutputStream((OutputStream)backingStream)));
        this.endianness = endianness;
    }

    public ByteOrder getEndianness() {
        return this.endianness;
    }

    protected DataOutputStream getBackingStream() {
        return (DataOutputStream)this.out;
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        this.getBackingStream().writeBoolean((boolean)b);
    }

    @Override
    public void writeByte(int i) throws IOException {
        this.getBackingStream().writeByte((int)i);
    }

    @Override
    public void writeShort(int i) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes((int)i) >> 16;
        }
        this.getBackingStream().writeShort((int)i);
    }

    @Override
    public void writeChar(int i) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            i = (int)Character.reverseBytes((char)((char)i));
        }
        this.getBackingStream().writeChar((int)i);
    }

    @Override
    public void writeInt(int i) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes((int)i);
        }
        this.getBackingStream().writeInt((int)i);
    }

    @Override
    public void writeLong(long l) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes((long)l);
        }
        this.getBackingStream().writeLong((long)l);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        int intBits = Float.floatToIntBits((float)v);
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            intBits = Integer.reverseBytes((int)intBits);
        }
        this.getBackingStream().writeInt((int)intBits);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        long longBits = Double.doubleToLongBits((double)v);
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            longBits = Long.reverseBytes((long)longBits);
        }
        this.getBackingStream().writeLong((long)longBits);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.getBackingStream().writeBytes((String)s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.getBackingStream().writeChars((String)s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        this.getBackingStream().writeUTF((String)s);
    }
}

