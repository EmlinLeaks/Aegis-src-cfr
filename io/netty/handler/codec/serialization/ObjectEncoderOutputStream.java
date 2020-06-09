/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.serialization.CompactObjectOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

public class ObjectEncoderOutputStream
extends OutputStream
implements ObjectOutput {
    private final DataOutputStream out;
    private final int estimatedLength;

    public ObjectEncoderOutputStream(OutputStream out) {
        this((OutputStream)out, (int)512);
    }

    public ObjectEncoderOutputStream(OutputStream out, int estimatedLength) {
        if (out == null) {
            throw new NullPointerException((String)"out");
        }
        if (estimatedLength < 0) {
            throw new IllegalArgumentException((String)("estimatedLength: " + estimatedLength));
        }
        this.out = out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream((OutputStream)out);
        this.estimatedLength = estimatedLength;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeObject(Object obj) throws IOException {
        ByteBuf buf = Unpooled.buffer((int)this.estimatedLength);
        try {
            CompactObjectOutputStream oout = new CompactObjectOutputStream((OutputStream)new ByteBufOutputStream((ByteBuf)buf));
            try {
                oout.writeObject((Object)obj);
                oout.flush();
            }
            finally {
                oout.close();
            }
            int objectSize = buf.readableBytes();
            this.writeInt((int)objectSize);
            buf.getBytes((int)0, (OutputStream)this, (int)objectSize);
            return;
        }
        finally {
            buf.release();
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write((int)b);
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    public final int size() {
        return this.out.size();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write((byte[])b, (int)off, (int)len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.out.write((byte[])b);
    }

    @Override
    public final void writeBoolean(boolean v) throws IOException {
        this.out.writeBoolean((boolean)v);
    }

    @Override
    public final void writeByte(int v) throws IOException {
        this.out.writeByte((int)v);
    }

    @Override
    public final void writeBytes(String s) throws IOException {
        this.out.writeBytes((String)s);
    }

    @Override
    public final void writeChar(int v) throws IOException {
        this.out.writeChar((int)v);
    }

    @Override
    public final void writeChars(String s) throws IOException {
        this.out.writeChars((String)s);
    }

    @Override
    public final void writeDouble(double v) throws IOException {
        this.out.writeDouble((double)v);
    }

    @Override
    public final void writeFloat(float v) throws IOException {
        this.out.writeFloat((float)v);
    }

    @Override
    public final void writeInt(int v) throws IOException {
        this.out.writeInt((int)v);
    }

    @Override
    public final void writeLong(long v) throws IOException {
        this.out.writeLong((long)v);
    }

    @Override
    public final void writeShort(int v) throws IOException {
        this.out.writeShort((int)v);
    }

    @Override
    public final void writeUTF(String str) throws IOException {
        this.out.writeUTF((String)str);
    }
}

