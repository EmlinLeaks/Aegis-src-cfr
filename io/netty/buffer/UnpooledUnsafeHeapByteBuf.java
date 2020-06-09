/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.buffer.UnsafeHeapSwappedByteBuf;
import io.netty.util.internal.PlatformDependent;

public class UnpooledUnsafeHeapByteBuf
extends UnpooledHeapByteBuf {
    public UnpooledUnsafeHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super((ByteBufAllocator)alloc, (int)initialCapacity, (int)maxCapacity);
    }

    @Override
    protected byte[] allocateArray(int initialCapacity) {
        return PlatformDependent.allocateUninitializedArray((int)initialCapacity);
    }

    @Override
    public byte getByte(int index) {
        this.checkIndex((int)index);
        return this._getByte((int)index);
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufUtil.getByte((byte[])this.array, (int)index);
    }

    @Override
    public short getShort(int index) {
        this.checkIndex((int)index, (int)2);
        return this._getShort((int)index);
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufUtil.getShort((byte[])this.array, (int)index);
    }

    @Override
    public short getShortLE(int index) {
        this.checkIndex((int)index, (int)2);
        return this._getShortLE((int)index);
    }

    @Override
    protected short _getShortLE(int index) {
        return UnsafeByteBufUtil.getShortLE((byte[])this.array, (int)index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.checkIndex((int)index, (int)3);
        return this._getUnsignedMedium((int)index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufUtil.getUnsignedMedium((byte[])this.array, (int)index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        this.checkIndex((int)index, (int)3);
        return this._getUnsignedMediumLE((int)index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return UnsafeByteBufUtil.getUnsignedMediumLE((byte[])this.array, (int)index);
    }

    @Override
    public int getInt(int index) {
        this.checkIndex((int)index, (int)4);
        return this._getInt((int)index);
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufUtil.getInt((byte[])this.array, (int)index);
    }

    @Override
    public int getIntLE(int index) {
        this.checkIndex((int)index, (int)4);
        return this._getIntLE((int)index);
    }

    @Override
    protected int _getIntLE(int index) {
        return UnsafeByteBufUtil.getIntLE((byte[])this.array, (int)index);
    }

    @Override
    public long getLong(int index) {
        this.checkIndex((int)index, (int)8);
        return this._getLong((int)index);
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufUtil.getLong((byte[])this.array, (int)index);
    }

    @Override
    public long getLongLE(int index) {
        this.checkIndex((int)index, (int)8);
        return this._getLongLE((int)index);
    }

    @Override
    protected long _getLongLE(int index) {
        return UnsafeByteBufUtil.getLongLE((byte[])this.array, (int)index);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.checkIndex((int)index);
        this._setByte((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufUtil.setByte((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.checkIndex((int)index, (int)2);
        this._setShort((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufUtil.setShort((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.checkIndex((int)index, (int)2);
        this._setShortLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setShortLE(int index, int value) {
        UnsafeByteBufUtil.setShortLE((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.checkIndex((int)index, (int)3);
        this._setMedium((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufUtil.setMedium((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.checkIndex((int)index, (int)3);
        this._setMediumLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        UnsafeByteBufUtil.setMediumLE((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.checkIndex((int)index, (int)4);
        this._setInt((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufUtil.setInt((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.checkIndex((int)index, (int)4);
        this._setIntLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setIntLE(int index, int value) {
        UnsafeByteBufUtil.setIntLE((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.checkIndex((int)index, (int)8);
        this._setLong((int)index, (long)value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufUtil.setLong((byte[])this.array, (int)index, (long)value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.checkIndex((int)index, (int)8);
        this._setLongLE((int)index, (long)value);
        return this;
    }

    @Override
    protected void _setLongLE(int index, long value) {
        UnsafeByteBufUtil.setLongLE((byte[])this.array, (int)index, (long)value);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        if (PlatformDependent.javaVersion() < 7) return super.setZero((int)index, (int)length);
        this.checkIndex((int)index, (int)length);
        UnsafeByteBufUtil.setZero((byte[])this.array, (int)index, (int)length);
        return this;
    }

    @Override
    public ByteBuf writeZero(int length) {
        if (PlatformDependent.javaVersion() < 7) return super.writeZero((int)length);
        this.ensureWritable((int)length);
        int wIndex = this.writerIndex;
        UnsafeByteBufUtil.setZero((byte[])this.array, (int)wIndex, (int)length);
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

