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
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Beta
@GwtIncompatible
public final class LittleEndianDataInputStream
extends FilterInputStream
implements DataInput {
    public LittleEndianDataInputStream(InputStream in) {
        super((InputStream)Preconditions.checkNotNull(in));
    }

    @CanIgnoreReturnValue
    @Override
    public String readLine() {
        throw new UnsupportedOperationException((String)"readLine is not supported");
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        ByteStreams.readFully((InputStream)this, (byte[])b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        ByteStreams.readFully((InputStream)this, (byte[])b, (int)off, (int)len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return (int)this.in.skip((long)((long)n));
    }

    @CanIgnoreReturnValue
    @Override
    public int readUnsignedByte() throws IOException {
        int b1 = this.in.read();
        if (0 <= b1) return b1;
        throw new EOFException();
    }

    @CanIgnoreReturnValue
    @Override
    public int readUnsignedShort() throws IOException {
        byte b1 = this.readAndCheckByte();
        byte b2 = this.readAndCheckByte();
        return Ints.fromBytes((byte)0, (byte)0, (byte)b2, (byte)b1);
    }

    @CanIgnoreReturnValue
    @Override
    public int readInt() throws IOException {
        byte b1 = this.readAndCheckByte();
        byte b2 = this.readAndCheckByte();
        byte b3 = this.readAndCheckByte();
        byte b4 = this.readAndCheckByte();
        return Ints.fromBytes((byte)b4, (byte)b3, (byte)b2, (byte)b1);
    }

    @CanIgnoreReturnValue
    @Override
    public long readLong() throws IOException {
        byte b1 = this.readAndCheckByte();
        byte b2 = this.readAndCheckByte();
        byte b3 = this.readAndCheckByte();
        byte b4 = this.readAndCheckByte();
        byte b5 = this.readAndCheckByte();
        byte b6 = this.readAndCheckByte();
        byte b7 = this.readAndCheckByte();
        byte b8 = this.readAndCheckByte();
        return Longs.fromBytes((byte)b8, (byte)b7, (byte)b6, (byte)b5, (byte)b4, (byte)b3, (byte)b2, (byte)b1);
    }

    @CanIgnoreReturnValue
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat((int)this.readInt());
    }

    @CanIgnoreReturnValue
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble((long)this.readLong());
    }

    @CanIgnoreReturnValue
    @Override
    public String readUTF() throws IOException {
        return new DataInputStream((InputStream)this.in).readUTF();
    }

    @CanIgnoreReturnValue
    @Override
    public short readShort() throws IOException {
        return (short)this.readUnsignedShort();
    }

    @CanIgnoreReturnValue
    @Override
    public char readChar() throws IOException {
        return (char)this.readUnsignedShort();
    }

    @CanIgnoreReturnValue
    @Override
    public byte readByte() throws IOException {
        return (byte)this.readUnsignedByte();
    }

    @CanIgnoreReturnValue
    @Override
    public boolean readBoolean() throws IOException {
        if (this.readUnsignedByte() == 0) return false;
        return true;
    }

    private byte readAndCheckByte() throws IOException, EOFException {
        int b1 = this.in.read();
        if (-1 != b1) return (byte)b1;
        throw new EOFException();
    }
}

