/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.HeapByteBufUtil;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledHeapByteBuf;
import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

class PooledHeapByteBuf
extends PooledByteBuf<byte[]> {
    private static final Recycler<PooledHeapByteBuf> RECYCLER = new Recycler<PooledHeapByteBuf>(){

        protected PooledHeapByteBuf newObject(Recycler.Handle<PooledHeapByteBuf> handle) {
            return new PooledHeapByteBuf(handle, (int)0);
        }
    };

    static PooledHeapByteBuf newInstance(int maxCapacity) {
        PooledHeapByteBuf buf = RECYCLER.get();
        buf.reuse((int)maxCapacity);
        return buf;
    }

    PooledHeapByteBuf(Recycler.Handle<? extends PooledHeapByteBuf> recyclerHandle, int maxCapacity) {
        super(recyclerHandle, (int)maxCapacity);
    }

    @Override
    public final boolean isDirect() {
        return false;
    }

    @Override
    protected byte _getByte(int index) {
        return HeapByteBufUtil.getByte((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected short _getShort(int index) {
        return HeapByteBufUtil.getShort((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected short _getShortLE(int index) {
        return HeapByteBufUtil.getShortLE((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return HeapByteBufUtil.getUnsignedMedium((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return HeapByteBufUtil.getUnsignedMediumLE((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected int _getInt(int index) {
        return HeapByteBufUtil.getInt((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected int _getIntLE(int index) {
        return HeapByteBufUtil.getIntLE((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected long _getLong(int index) {
        return HeapByteBufUtil.getLong((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected long _getLongLE(int index) {
        return HeapByteBufUtil.getLongLE((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    public final ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.capacity());
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory((byte[])((byte[])this.memory), (int)this.idx((int)index), (long)(dst.memoryAddress() + (long)dstIndex), (long)((long)length));
            return this;
        }
        if (dst.hasArray()) {
            this.getBytes((int)index, (byte[])dst.array(), (int)(dst.arrayOffset() + dstIndex), (int)length);
            return this;
        }
        dst.setBytes((int)dstIndex, (byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length);
        return this;
    }

    @Override
    public final ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.length);
        System.arraycopy((Object)this.memory, (int)this.idx((int)index), (Object)dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public final ByteBuf getBytes(int index, ByteBuffer dst) {
        int length = dst.remaining();
        this.checkIndex((int)index, (int)length);
        dst.put((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length);
        return this;
    }

    @Override
    public final ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.checkIndex((int)index, (int)length);
        out.write((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        HeapByteBufUtil.setByte((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setShort(int index, int value) {
        HeapByteBufUtil.setShort((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        HeapByteBufUtil.setShortLE((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        HeapByteBufUtil.setMedium((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        HeapByteBufUtil.setMediumLE((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setInt(int index, int value) {
        HeapByteBufUtil.setInt((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        HeapByteBufUtil.setIntLE((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setLong(int index, long value) {
        HeapByteBufUtil.setLong((byte[])((byte[])this.memory), (int)this.idx((int)index), (long)value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        HeapByteBufUtil.setLongLE((byte[])((byte[])this.memory), (int)this.idx((int)index), (long)value);
    }

    @Override
    public final ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.capacity());
        if (src.hasMemoryAddress()) {
            PlatformDependent.copyMemory((long)(src.memoryAddress() + (long)srcIndex), (byte[])((byte[])this.memory), (int)this.idx((int)index), (long)((long)length));
            return this;
        }
        if (src.hasArray()) {
            this.setBytes((int)index, (byte[])src.array(), (int)(src.arrayOffset() + srcIndex), (int)length);
            return this;
        }
        src.getBytes((int)srcIndex, (byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length);
        return this;
    }

    @Override
    public final ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.length);
        System.arraycopy((Object)src, (int)srcIndex, (Object)this.memory, (int)this.idx((int)index), (int)length);
        return this;
    }

    @Override
    public final ByteBuf setBytes(int index, ByteBuffer src) {
        int length = src.remaining();
        this.checkIndex((int)index, (int)length);
        src.get((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length);
        return this;
    }

    @Override
    public final int setBytes(int index, InputStream in, int length) throws IOException {
        this.checkIndex((int)index, (int)length);
        return in.read((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length);
    }

    @Override
    public final ByteBuf copy(int index, int length) {
        this.checkIndex((int)index, (int)length);
        ByteBuf copy = this.alloc().heapBuffer((int)length, (int)this.maxCapacity());
        return copy.writeBytes((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length);
    }

    @Override
    final ByteBuffer duplicateInternalNioBuffer(int index, int length) {
        this.checkIndex((int)index, (int)length);
        return ByteBuffer.wrap((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length).slice();
    }

    @Override
    public final boolean hasArray() {
        return true;
    }

    @Override
    public final byte[] array() {
        this.ensureAccessible();
        return (byte[])this.memory;
    }

    @Override
    public final int arrayOffset() {
        return this.offset;
    }

    @Override
    public final boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public final long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final ByteBuffer newInternalNioBuffer(byte[] memory) {
        return ByteBuffer.wrap((byte[])memory);
    }
}

