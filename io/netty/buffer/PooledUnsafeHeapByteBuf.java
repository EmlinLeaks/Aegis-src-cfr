/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledHeapByteBuf;
import io.netty.buffer.PooledUnsafeHeapByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.buffer.UnsafeHeapSwappedByteBuf;
import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;

final class PooledUnsafeHeapByteBuf
extends PooledHeapByteBuf {
    private static final Recycler<PooledUnsafeHeapByteBuf> RECYCLER = new Recycler<PooledUnsafeHeapByteBuf>(){

        protected PooledUnsafeHeapByteBuf newObject(Recycler.Handle<PooledUnsafeHeapByteBuf> handle) {
            return new PooledUnsafeHeapByteBuf(handle, (int)0);
        }
    };

    static PooledUnsafeHeapByteBuf newUnsafeInstance(int maxCapacity) {
        PooledUnsafeHeapByteBuf buf = RECYCLER.get();
        buf.reuse((int)maxCapacity);
        return buf;
    }

    private PooledUnsafeHeapByteBuf(Recycler.Handle<PooledUnsafeHeapByteBuf> recyclerHandle, int maxCapacity) {
        super(recyclerHandle, (int)maxCapacity);
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufUtil.getByte((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufUtil.getShort((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected short _getShortLE(int index) {
        return UnsafeByteBufUtil.getShortLE((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufUtil.getUnsignedMedium((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return UnsafeByteBufUtil.getUnsignedMediumLE((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufUtil.getInt((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected int _getIntLE(int index) {
        return UnsafeByteBufUtil.getIntLE((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufUtil.getLong((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected long _getLongLE(int index) {
        return UnsafeByteBufUtil.getLongLE((byte[])((byte[])this.memory), (int)this.idx((int)index));
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufUtil.setByte((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufUtil.setShort((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        UnsafeByteBufUtil.setShortLE((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufUtil.setMedium((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        UnsafeByteBufUtil.setMediumLE((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufUtil.setInt((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        UnsafeByteBufUtil.setIntLE((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)value);
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufUtil.setLong((byte[])((byte[])this.memory), (int)this.idx((int)index), (long)value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        UnsafeByteBufUtil.setLongLE((byte[])((byte[])this.memory), (int)this.idx((int)index), (long)value);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        if (PlatformDependent.javaVersion() < 7) return super.setZero((int)index, (int)length);
        this.checkIndex((int)index, (int)length);
        UnsafeByteBufUtil.setZero((byte[])((byte[])this.memory), (int)this.idx((int)index), (int)length);
        return this;
    }

    @Override
    public ByteBuf writeZero(int length) {
        if (PlatformDependent.javaVersion() < 7) return super.writeZero((int)length);
        this.ensureWritable((int)length);
        int wIndex = this.writerIndex;
        UnsafeByteBufUtil.setZero((byte[])((byte[])this.memory), (int)this.idx((int)wIndex), (int)length);
        this.writerIndex = wIndex + length;
        return this;
    }

    @Deprecated
    @Override
    protected SwappedByteBuf newSwappedByteBuf() {
        if (!PlatformDependent.isUnaligned()) return super.newSwappedByteBuf();
        return new UnsafeHeapSwappedByteBuf((AbstractByteBuf)this);
    }
}

