/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.buffer.UnsafeDirectSwappedByteBuf;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class UnpooledUnsafeDirectByteBuf
extends UnpooledDirectByteBuf {
    long memoryAddress;

    public UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super((ByteBufAllocator)alloc, (int)initialCapacity, (int)maxCapacity);
    }

    protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity) {
        super((ByteBufAllocator)alloc, (ByteBuffer)initialBuffer, (int)maxCapacity, (boolean)false, (boolean)true);
    }

    UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity, boolean doFree) {
        super((ByteBufAllocator)alloc, (ByteBuffer)initialBuffer, (int)maxCapacity, (boolean)doFree, (boolean)false);
    }

    @Override
    final void setByteBuffer(ByteBuffer buffer, boolean tryFree) {
        super.setByteBuffer((ByteBuffer)buffer, (boolean)tryFree);
        this.memoryAddress = PlatformDependent.directBufferAddress((ByteBuffer)buffer);
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

    @Override
    public byte getByte(int index) {
        this.checkIndex((int)index);
        return this._getByte((int)index);
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufUtil.getByte((long)this.addr((int)index));
    }

    @Override
    public short getShort(int index) {
        this.checkIndex((int)index, (int)2);
        return this._getShort((int)index);
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
    public int getUnsignedMedium(int index) {
        this.checkIndex((int)index, (int)3);
        return this._getUnsignedMedium((int)index);
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
    public int getInt(int index) {
        this.checkIndex((int)index, (int)4);
        return this._getInt((int)index);
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
    public long getLong(int index) {
        this.checkIndex((int)index, (int)8);
        return this._getLong((int)index);
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
    void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (byte[])dst, (int)dstIndex, (int)length);
    }

    @Override
    void getBytes(int index, ByteBuffer dst, boolean internal) {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (ByteBuffer)dst);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.checkIndex((int)index);
        this._setByte((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufUtil.setByte((long)this.addr((int)index), (int)value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.checkIndex((int)index, (int)2);
        this._setShort((int)index, (int)value);
        return this;
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
    public ByteBuf setMedium(int index, int value) {
        this.checkIndex((int)index, (int)3);
        this._setMedium((int)index, (int)value);
        return this;
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
    public ByteBuf setInt(int index, int value) {
        this.checkIndex((int)index, (int)4);
        this._setInt((int)index, (int)value);
        return this;
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
    public ByteBuf setLong(int index, long value) {
        this.checkIndex((int)index, (int)8);
        this._setLong((int)index, (long)value);
        return this;
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
    void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (OutputStream)out, (int)length);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return UnsafeByteBufUtil.setBytes((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (InputStream)in, (int)length);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return UnsafeByteBufUtil.copy((AbstractByteBuf)this, (long)this.addr((int)index), (int)index, (int)length);
    }

    final long addr(int index) {
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

