/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ByteBufOutputStream
extends OutputStream
implements DataOutput {
    private final ByteBuf buffer;
    private final int startIndex;
    private final DataOutputStream utf8out = new DataOutputStream((OutputStream)this);

    public ByteBufOutputStream(ByteBuf buffer) {
        if (buffer == null) {
            throw new NullPointerException((String)"buffer");
        }
        this.buffer = buffer;
        this.startIndex = buffer.writerIndex();
    }

    public int writtenBytes() {
        return this.buffer.writerIndex() - this.startIndex;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        this.buffer.writeBytes((byte[])b, (int)off, (int)len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.buffer.writeBytes((byte[])b);
    }

    @Override
    public void write(int b) throws IOException {
        this.buffer.writeByte((int)b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.buffer.writeBoolean((boolean)v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.buffer.writeByte((int)v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.buffer.writeCharSequence((CharSequence)s, (Charset)CharsetUtil.US_ASCII);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.buffer.writeChar((int)v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        int len = s.length();
        int i = 0;
        while (i < len) {
            this.buffer.writeChar((int)s.charAt((int)i));
            ++i;
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.buffer.writeDouble((double)v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.buffer.writeFloat((float)v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.buffer.writeInt((int)v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.buffer.writeLong((long)v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.buffer.writeShort((int)((short)v));
    }

    @Override
    public void writeUTF(String s) throws IOException {
        this.utf8out.writeUTF((String)s);
    }

    public ByteBuf buffer() {
        return this.buffer;
    }
}

