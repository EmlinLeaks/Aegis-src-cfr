/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledDirectByteBuf;
import io.netty.util.Recycler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

final class PooledDirectByteBuf
extends PooledByteBuf<ByteBuffer> {
    private static final Recycler<PooledDirectByteBuf> RECYCLER = new Recycler<PooledDirectByteBuf>(){

        protected PooledDirectByteBuf newObject(Recycler.Handle<PooledDirectByteBuf> handle) {
            return new PooledDirectByteBuf(handle, (int)0);
        }
    };

    static PooledDirectByteBuf newInstance(int maxCapacity) {
        PooledDirectByteBuf buf = RECYCLER.get();
        buf.reuse((int)maxCapacity);
        return buf;
    }

    private PooledDirectByteBuf(Recycler.Handle<PooledDirectByteBuf> recyclerHandle, int maxCapacity) {
        super(recyclerHandle, (int)maxCapacity);
    }

    @Override
    protected ByteBuffer newInternalNioBuffer(ByteBuffer memory) {
        return memory.duplicate();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    protected byte _getByte(int index) {
        return ((ByteBuffer)this.memory).get((int)this.idx((int)index));
    }

    @Override
    protected short _getShort(int index) {
        return ((ByteBuffer)this.memory).getShort((int)this.idx((int)index));
    }

    @Override
    protected short _getShortLE(int index) {
        return ByteBufUtil.swapShort((short)this._getShort((int)index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        index = this.idx((int)index);
        return (((ByteBuffer)this.memory).get((int)index) & 255) << 16 | (((ByteBuffer)this.memory).get((int)(index + 1)) & 255) << 8 | ((ByteBuffer)this.memory).get((int)(index + 2)) & 255;
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        index = this.idx((int)index);
        return ((ByteBuffer)this.memory).get((int)index) & 255 | (((ByteBuffer)this.memory).get((int)(index + 1)) & 255) << 8 | (((ByteBuffer)this.memory).get((int)(index + 2)) & 255) << 16;
    }

    @Override
    protected int _getInt(int index) {
        return ((ByteBuffer)this.memory).getInt((int)this.idx((int)index));
    }

    @Override
    protected int _getIntLE(int index) {
        return ByteBufUtil.swapInt((int)this._getInt((int)index));
    }

    @Override
    protected long _getLong(int index) {
        return ((ByteBuffer)this.memory).getLong((int)this.idx((int)index));
    }

    @Override
    protected long _getLongLE(int index) {
        return ByteBufUtil.swapLong((long)this._getLong((int)index));
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
        this._internalNioBuffer((int)index, (int)length, (boolean)true).get((byte[])dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.checkDstIndex((int)length, (int)dstIndex, (int)dst.length);
        this._internalNioBuffer((int)this.readerIndex, (int)length, (boolean)false).get((byte[])dst, (int)dstIndex, (int)length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        dst.put((ByteBuffer)this.duplicateInternalNioBuffer((int)index, (int)dst.remaining()));
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        int length = dst.remaining();
        this.checkReadableBytes((int)length);
        dst.put((ByteBuffer)this._internalNioBuffer((int)this.readerIndex, (int)length, (boolean)false));
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.getBytes((int)index, (OutputStream)out, (int)length, (boolean)false);
        return this;
    }

    private void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return;
        }
        ByteBufUtil.readBytes((ByteBufAllocator)this.alloc(), (ByteBuffer)(internal ? this.internalNioBuffer() : ((ByteBuffer)this.memory).duplicate()), (int)this.idx((int)index), (int)length, (OutputStream)out);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.checkReadableBytes((int)length);
        this.getBytes((int)this.readerIndex, (OutputStream)out, (int)length, (boolean)true);
        this.readerIndex += length;
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        ((ByteBuffer)this.memory).put((int)this.idx((int)index), (byte)((byte)value));
    }

    @Override
    protected void _setShort(int index, int value) {
        ((ByteBuffer)this.memory).putShort((int)this.idx((int)index), (short)((short)value));
    }

    @Override
    protected void _setShortLE(int index, int value) {
        this._setShort((int)index, (int)ByteBufUtil.swapShort((short)((short)value)));
    }

    @Override
    protected void _setMedium(int index, int value) {
        index = this.idx((int)index);
        ((ByteBuffer)this.memory).put((int)index, (byte)((byte)(value >>> 16)));
        ((ByteBuffer)this.memory).put((int)(index + 1), (byte)((byte)(value >>> 8)));
        ((ByteBuffer)this.memory).put((int)(index + 2), (byte)((byte)value));
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        index = this.idx((int)index);
        ((ByteBuffer)this.memory).put((int)index, (byte)((byte)value));
        ((ByteBuffer)this.memory).put((int)(index + 1), (byte)((byte)(value >>> 8)));
        ((ByteBuffer)this.memory).put((int)(index + 2), (byte)((byte)(value >>> 16)));
    }

    @Override
    protected void _setInt(int index, int value) {
        ((ByteBuffer)this.memory).putInt((int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        this._setInt((int)index, (int)ByteBufUtil.swapInt((int)value));
    }

    @Override
    protected void _setLong(int index, long value) {
        ((ByteBuffer)this.memory).putLong((int)this.idx((int)index), (long)value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        this._setLong((int)index, (long)ByteBufUtil.swapLong((long)value));
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.capacity());
        if (src.hasArray()) {
            this.setBytes((int)index, (byte[])src.array(), (int)(src.arrayOffset() + srcIndex), (int)length);
            return this;
        }
        if (src.nioBufferCount() <= 0) {
            src.getBytes((int)srcIndex, (ByteBuf)this, (int)index, (int)length);
            return this;
        }
        ByteBuffer[] arrbyteBuffer = src.nioBuffers((int)srcIndex, (int)length);
        int n = arrbyteBuffer.length;
        int n2 = 0;
        while (n2 < n) {
            ByteBuffer bb = arrbyteBuffer[n2];
            int bbLen = bb.remaining();
            this.setBytes((int)index, (ByteBuffer)bb);
            index += bbLen;
            ++n2;
        }
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.length);
        this._internalNioBuffer((int)index, (int)length, (boolean)false).put((byte[])src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        int length = src.remaining();
        this.checkIndex((int)index, (int)length);
        ByteBuffer tmpBuf = this.internalNioBuffer();
        if (src == tmpBuf) {
            src = src.duplicate();
        }
        index = this.idx((int)index);
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        tmpBuf.put((ByteBuffer)src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.checkIndex((int)index, (int)length);
        byte[] tmp = ByteBufUtil.threadLocalTempArray((int)length);
        int readBytes = in.read((byte[])tmp, (int)0, (int)length);
        if (readBytes <= 0) {
            return readBytes;
        }
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)this.idx((int)index));
        tmpBuf.put((byte[])tmp, (int)0, (int)readBytes);
        return readBytes;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex((int)index, (int)length);
        ByteBuf copy = this.alloc().directBuffer((int)length, (int)this.maxCapacity());
        return copy.writeBytes((ByteBuf)this, (int)index, (int)length);
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new UnsupportedOperationException((String)"direct buffer");
    }

    @Override
    public int arrayOffset() {
        throw new UnsupportedOperationException((String)"direct buffer");
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

