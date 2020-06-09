/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ReadOnlyByteBufferBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class ReadOnlyUnsafeDirectByteBuf
extends ReadOnlyByteBufferBuf {
    private final long memoryAddress;

    ReadOnlyUnsafeDirectByteBuf(ByteBufAllocator allocator, ByteBuffer byteBuffer) {
        super((ByteBufAllocator)allocator, (ByteBuffer)byteBuffer);
        this.memoryAddress = PlatformDependent.directBufferAddress((ByteBuffer)this.buffer);
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
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufUtil.getUnsignedMedium((long)this.addr((int)index));
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufUtil.getInt((long)this.addr((int)index));
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufUtil.getLong((long)this.addr((int)index));
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkIndex((int)index, (int)length);
        if (dst == null) {
            throw new NullPointerException((String)"dst");
        }
        if (dstIndex < 0) throw new IndexOutOfBoundsException((String)("dstIndex: " + dstIndex));
        if (dstIndex > dst.capacity() - length) {
            throw new IndexOutOfBoundsException((String)("dstIndex: " + dstIndex));
        }
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory((long)this.addr((int)index), (long)(dst.memoryAddress() + (long)dstIndex), (long)((long)length));
            return this;
        }
        if (dst.hasArray()) {
            PlatformDependent.copyMemory((long)this.addr((int)index), (byte[])dst.array(), (int)(dst.arrayOffset() + dstIndex), (long)((long)length));
            return this;
        }
        dst.setBytes((int)dstIndex, (ByteBuf)this, (int)index, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkIndex((int)index, (int)length);
        if (dst == null) {
            throw new NullPointerException((String)"dst");
        }
        if (dstIndex < 0 || dstIndex > dst.length - length) throw new IndexOutOfBoundsException((String)String.format((String)"dstIndex: %d, length: %d (expected: range(0, %d))", (Object[])new Object[]{Integer.valueOf((int)dstIndex), Integer.valueOf((int)length), Integer.valueOf((int)dst.length)}));
        if (length == 0) return this;
        PlatformDependent.copyMemory((long)this.addr((int)index), (byte[])dst, (int)dstIndex, (long)((long)length));
        return this;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex((int)index, (int)length);
        ByteBuf copy = this.alloc().directBuffer((int)length, (int)this.maxCapacity());
        if (length == 0) return copy;
        if (copy.hasMemoryAddress()) {
            PlatformDependent.copyMemory((long)this.addr((int)index), (long)copy.memoryAddress(), (long)((long)length));
            copy.setIndex((int)0, (int)length);
            return copy;
        }
        copy.writeBytes((ByteBuf)this, (int)index, (int)length);
        return copy;
    }

    @Override
    public boolean hasMemoryAddress() {
        return true;
    }

    @Override
    public long memoryAddress() {
        return this.memoryAddress;
    }

    private long addr(int index) {
        return this.memoryAddress + (long)index;
    }
}

