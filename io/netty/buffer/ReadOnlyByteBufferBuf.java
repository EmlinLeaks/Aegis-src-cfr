/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

class ReadOnlyByteBufferBuf
extends AbstractReferenceCountedByteBuf {
    protected final ByteBuffer buffer;
    private final ByteBufAllocator allocator;
    private ByteBuffer tmpNioBuf;

    ReadOnlyByteBufferBuf(ByteBufAllocator allocator, ByteBuffer buffer) {
        super((int)buffer.remaining());
        if (!buffer.isReadOnly()) {
            throw new IllegalArgumentException((String)("must be a readonly buffer: " + StringUtil.simpleClassName((Object)buffer)));
        }
        this.allocator = allocator;
        this.buffer = buffer.slice().order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writerIndex((int)this.buffer.limit());
    }

    @Override
    protected void deallocate() {
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isWritable(int numBytes) {
        return false;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return 1;
    }

    @Override
    public byte getByte(int index) {
        this.ensureAccessible();
        return this._getByte((int)index);
    }

    @Override
    protected byte _getByte(int index) {
        return this.buffer.get((int)index);
    }

    @Override
    public short getShort(int index) {
        this.ensureAccessible();
        return this._getShort((int)index);
    }

    @Override
    protected short _getShort(int index) {
        return this.buffer.getShort((int)index);
    }

    @Override
    public short getShortLE(int index) {
        this.ensureAccessible();
        return this._getShortLE((int)index);
    }

    @Override
    protected short _getShortLE(int index) {
        return ByteBufUtil.swapShort((short)this.buffer.getShort((int)index));
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.ensureAccessible();
        return this._getUnsignedMedium((int)index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return (this.getByte((int)index) & 255) << 16 | (this.getByte((int)(index + 1)) & 255) << 8 | this.getByte((int)(index + 2)) & 255;
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        this.ensureAccessible();
        return this._getUnsignedMediumLE((int)index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return this.getByte((int)index) & 255 | (this.getByte((int)(index + 1)) & 255) << 8 | (this.getByte((int)(index + 2)) & 255) << 16;
    }

    @Override
    public int getInt(int index) {
        this.ensureAccessible();
        return this._getInt((int)index);
    }

    @Override
    protected int _getInt(int index) {
        return this.buffer.getInt((int)index);
    }

    @Override
    public int getIntLE(int index) {
        this.ensureAccessible();
        return this._getIntLE((int)index);
    }

    @Override
    protected int _getIntLE(int index) {
        return ByteBufUtil.swapInt((int)this.buffer.getInt((int)index));
    }

    @Override
    public long getLong(int index) {
        this.ensureAccessible();
        return this._getLong((int)index);
    }

    @Override
    protected long _getLong(int index) {
        return this.buffer.getLong((int)index);
    }

    @Override
    public long getLongLE(int index) {
        this.ensureAccessible();
        return this._getLongLE((int)index);
    }

    @Override
    protected long _getLongLE(int index) {
        return ByteBufUtil.swapLong((long)this.buffer.getLong((int)index));
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.capacity());
        if (dst.hasArray()) {
            this.getBytes((int)index, (byte[])dst.array(), (int)(dst.arrayOffset() + dstIndex), (int)length);
            return this;
        }
        if (dst.nioBufferCount() <= 0) {
            dst.setBytes((int)dstIndex, (ByteBuf)this, (int)index, (int)length);
            return this;
        }
        ByteBuffer[] arrbyteBuffer = dst.nioBuffers((int)dstIndex, (int)length);
        int n = arrbyteBuffer.length;
        int n2 = 0;
        while (n2 < n) {
            ByteBuffer bb = arrbyteBuffer[n2];
            int bbLen = bb.remaining();
            this.getBytes((int)index, (ByteBuffer)bb);
            index += bbLen;
            ++n2;
        }
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.length);
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        tmpBuf.get((byte[])dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.checkIndex((int)index, (int)dst.remaining());
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index).limit((int)(index + dst.remaining()));
        dst.put((ByteBuffer)tmpBuf);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShortLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setIntLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLongLE(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int capacity() {
        return this.maxCapacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.allocator;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return this.buffer.isReadOnly();
    }

    @Override
    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return this;
        }
        if (this.buffer.hasArray()) {
            out.write((byte[])this.buffer.array(), (int)(index + this.buffer.arrayOffset()), (int)length);
            return this;
        }
        byte[] tmp = ByteBufUtil.threadLocalTempArray((int)length);
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index);
        tmpBuf.get((byte[])tmp, (int)0, (int)length);
        out.write((byte[])tmp, (int)0, (int)length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        return out.write((ByteBuffer)tmpBuf);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        return out.write((ByteBuffer)tmpBuf, (long)position);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        throw new ReadOnlyBufferException();
    }

    protected final ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf != null) return tmpNioBuf;
        this.tmpNioBuf = tmpNioBuf = this.buffer.duplicate();
        return tmpNioBuf;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        ByteBuffer src;
        this.ensureAccessible();
        try {
            src = (ByteBuffer)this.internalNioBuffer().clear().position((int)index).limit((int)(index + length));
        }
        catch (IllegalArgumentException ignored) {
            throw new IndexOutOfBoundsException((String)("Too many bytes to read - Need " + (index + length)));
        }
        ByteBuf dst = src.isDirect() ? this.alloc().directBuffer((int)length) : this.alloc().heapBuffer((int)length);
        dst.writeBytes((ByteBuffer)src);
        return dst;
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[]{this.nioBuffer((int)index, (int)length)};
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex((int)index, (int)length);
        return (ByteBuffer)this.buffer.duplicate().position((int)index).limit((int)(index + length));
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.ensureAccessible();
        return (ByteBuffer)this.internalNioBuffer().clear().position((int)index).limit((int)(index + length));
    }

    @Override
    public boolean hasArray() {
        return this.buffer.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buffer.array();
    }

    @Override
    public int arrayOffset() {
        return this.buffer.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }
}

