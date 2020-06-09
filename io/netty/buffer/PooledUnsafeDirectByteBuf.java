/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledUnsafeDirectByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.buffer.UnsafeDirectSwappedByteBuf;
import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class PooledUnsafeDirectByteBuf
extends PooledByteBuf<ByteBuffer> {
    private static final Recycler<PooledUnsafeDirectByteBuf> RECYCLER = new Recycler<PooledUnsafeDirectByteBuf>(){

        protected PooledUnsafeDirectByteBuf newObject(Recycler.Handle<PooledUnsafeDirectByteBuf> handle) {
            return new PooledUnsafeDirectByteBuf(handle, (int)0);
        }
    };
    private long memoryAddress;

    static PooledUnsafeDirectByteBuf newInstance(int maxCapacity) {
        PooledUnsafeDirectByteBuf buf = RECYCLER.get();
        buf.reuse((int)maxCapacity);
        return buf;
    }

    private PooledUnsafeDirectByteBuf(Recycler.Handle<PooledUnsafeDirectByteBuf> recyclerHandle, int maxCapacity) {
        super(recyclerHandle, (int)maxCapacity);
    }

    @Override
    void init(PoolChunk<ByteBuffer> chunk, ByteBuffer nioBuffer, long handle, int offset, int length, int maxLength, PoolThreadCache cache) {
        super.init(chunk, (ByteBuffer)nioBuffer, (long)handle, (int)offset, (int)length, (int)maxLength, (PoolThreadCache)cache);
        this.initMemoryAddress();
    }

    @Override
    void initUnpooled(PoolChunk<ByteBuffer> chunk, int length) {
        super.initUnpooled(chunk, (int)length);
        this.initMemoryAddress();
    }

    private void initMemoryAddress() {
        this.memoryAddress = PlatformDependent.directBufferAddress((ByteBuffer)((ByteBuffer)this.memory)) + (long)this.offset;
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
        return UnsafeByteBufUtil.getByte((long)this.addr((int)index));
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufUtil.getShort((long)this.addr((int)index));
    }

    @Override
    protected short _getShortLE(int index) {
        return UnsafeByteBufUtil.getShortLE((long)this.addr((int)index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufUtil.getUnsignedMedium((long)this.addr((int)index));
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return UnsafeByteBufUtil.getUnsignedMediumLE((long)this.addr((int)index));
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufUtil.getInt((long)this.addr((int)index));
    }

    @Override
    protected int _getIntLE(int index) {
        return UnsafeByteBufUtil.getIntLE((long)this.addr((int)index));
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufUtil.getLong((long)this.addr((int)index));
    }

    @Override
    protected long _getLongLE(int index) {
        return UnsafeByteBufUtil.getLongLE((long)this.addr((int)index));
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (ByteBuf)dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (byte[])dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (ByteBuffer)dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (OutputStream)out, (int)length);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufUtil.setByte((long)this.addr((int)index), (int)((byte)value));
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufUtil.setShort((long)this.addr((int)index), (int)value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        UnsafeByteBufUtil.setShortLE((long)this.addr((int)index), (int)value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufUtil.setMedium((long)this.addr((int)index), (int)value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        UnsafeByteBufUtil.setMediumLE((long)this.addr((int)index), (int)value);
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufUtil.setInt((long)this.addr((int)index), (int)value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        UnsafeByteBufUtil.setIntLE((long)this.addr((int)index), (int)value);
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufUtil.setLong((long)this.addr((int)index), (long)value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        UnsafeByteBufUtil.setLongLE((long)this.addr((int)index), (long)value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        UnsafeByteBufUtil.setBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (ByteBuf)src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        UnsafeByteBufUtil.setBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (byte[])src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        UnsafeByteBufUtil.setBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (ByteBuffer)src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return UnsafeByteBufUtil.setBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (InputStream)in, (int)length);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return UnsafeByteBufUtil.copy((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (int)length);
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
        return true;
    }

    @Override
    public long memoryAddress() {
        this.ensureAccessible();
        return this.memoryAddress;
    }

    private long addr(int index) {
        return this.memoryAddress + (long)index;
    }

    @Override
    protected SwappedByteBuf newSwappedByteBuf() {
        if (!PlatformDependent.isUnaligned()) return super.newSwappedByteBuf();
        return new UnsafeDirectSwappedByteBuf((AbstractByteBuf)this);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        this.checkIndex((int)index, (int)length);
        UnsafeByteBufUtil.setZero((long)this.addr((int)index), (int)length);
        return this;
    }

    @Override
    public ByteBuf writeZero(int length) {
        this.ensureWritable((int)length);
        int wIndex = this.writerIndex;
        UnsafeByteBufUtil.setZero((long)this.addr((int)wIndex), (int)length);
        this.writerIndex = wIndex + length;
        return this;
    }
}

