/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Beta
@GwtIncompatible
public final class LittleEndianDataOutputStream
extends FilterOutputStream
implements DataOutput {
    public LittleEndianDataOutputStream(OutputStream out) {
        super((OutputStream)new DataOutputStream((OutputStream)Preconditions.checkNotNull(out)));
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write((byte[])b, (int)off, (int)len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        ((DataOutputStream)this.out).writeBoolean((boolean)v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        ((DataOutputStream)this.out).writeByte((int)v);
    }

    @Deprecated
    @Override
    public void writeBytes(String s) throws IOException {
        ((DataOutputStream)this.out).writeBytes((String)s);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.writeShort((int)v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        int i = 0;
        while (i < s.length()) {
            this.writeChar((int)s.charAt((int)i));
            ++i;
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.writeLong((long)Double.doubleToLongBits((double)v));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.writeInt((int)Float.floatToIntBits((float)v));
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.out.write((int)(255 & v));
        this.out.write((int)(255 & v >> 8));
        this.out.write((int)(255 & v >> 16));
        this.out.write((int)(255 & v >> 24));
    }

    @Override
    public void writeLong(long v) throws IOException {
        byte[] bytes = Longs.toByteArray((long)Long.reverseBytes((long)v));
        this.write((byte[])bytes, (int)0, (int)bytes.length);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.out.write((int)(255 & v));
        this.out.write((int)(255 & v >> 8));
    }

    @Override
    public void writeUTF(String str) throws IOException {
        ((DataOutputStream)this.out).writeUTF((String)str);
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }
}

