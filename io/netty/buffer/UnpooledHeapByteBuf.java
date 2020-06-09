/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.HeapByteBufUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledHeapByteBuf
extends AbstractReferenceCountedByteBuf {
    private final ByteBufAllocator alloc;
    byte[] array;
    private ByteBuffer tmpNioBuf;

    public UnpooledHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super((int)maxCapacity);
        ObjectUtil.checkNotNull(alloc, (String)"alloc");
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException((String)String.format((String)"initialCapacity(%d) > maxCapacity(%d)", (Object[])new Object[]{Integer.valueOf((int)initialCapacity), Integer.valueOf((int)maxCapacity)}));
        }
        this.alloc = alloc;
        this.setArray((byte[])this.allocateArray((int)initialCapacity));
        this.setIndex((int)0, (int)0);
    }

    protected UnpooledHeapByteBuf(ByteBufAllocator alloc, byte[] initialArray, int maxCapacity) {
        super((int)maxCapacity);
        ObjectUtil.checkNotNull(alloc, (String)"alloc");
        ObjectUtil.checkNotNull(initialArray, (String)"initialArray");
        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException((String)String.format((String)"initialCapacity(%d) > maxCapacity(%d)", (Object[])new Object[]{Integer.valueOf((int)initialArray.length), Integer.valueOf((int)maxCapacity)}));
        }
        this.alloc = alloc;
        this.setArray((byte[])initialArray);
        this.setIndex((int)0, (int)initialArray.length);
    }

    protected byte[] allocateArray(int initialCapacity) {
        return new byte[initialCapacity];
    }

    protected void freeArray(byte[] array) {
    }

    private void setArray(byte[] initialArray) {
        this.array = initialArray;
        this.tmpNioBuf = null;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    public int capacity() {
        return this.array.length;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        int bytesToCopy;
        this.checkNewCapacity((int)newCapacity);
        byte[] oldArray = this.array;
        int oldCapacity = oldArray.length;
        if (newCapacity == oldCapacity) {
            return this;
        }
        if (newCapacity > oldCapacity) {
            bytesToCopy = oldCapacity;
        } else {
            this.trimIndicesToCapacity((int)newCapacity);
            bytesToCopy = newCapacity;
        }
        byte[] newArray = this.allocateArray((int)newCapacity);
        System.arraycopy((Object)oldArray, (int)0, (Object)newArray, (int)0, (int)bytesToCopy);
        this.setArray((byte[])newArray);
        this.freeArray((byte[])oldArray);
        return this;
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        this.ensureAccessible();
        return this.array;
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.capacity());
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory((byte[])this.array, (int)index, (long)(dst.memoryAddress() + (long)dstIndex), (long)((long)length));
            return this;
        }
        if (dst.hasArray()) {
            this.getBytes((int)index, (byte[])dst.array(), (int)(dst.arrayOffset() + dstIndex), (int)length);
            return this;
        }
        dst.setBytes((int)dstIndex, (byte[])this.array, (int)index, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.length);
        System.arraycopy((Object)this.array, (int)index, (Object)dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.ensureAccessible();
        dst.put((byte[])this.array, (int)index, (int)dst.remaining());
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.ensureAccessible();
        out.write((byte[])this.array, (int)index, (int)length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        this.ensureAccessible();
        return this.getBytes((int)index, (GatheringByteChannel)out, (int)length, (boolean)false);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        this.ensureAccessible();
        return this.getBytes((int)index, (FileChannel)out, (long)position, (int)length, (boolean)false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        ByteBuffer tmpBuf;
        this.ensureAccessible();
        if (internal) {
            tmpBuf = this.internalNioBuffer();
            return out.write((ByteBuffer)((ByteBuffer)tmpBuf.clear().position((int)index).limit((int)(index + length))));
        }
        tmpBuf = ByteBuffer.wrap((byte[])this.array);
        return out.write((ByteBuffer)((ByteBuffer)tmpBuf.clear().position((int)index).limit((int)(index + length))));
    }

    private int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : ByteBuffer.wrap((byte[])this.array);
        return out.write((ByteBuffer)((ByteBuffer)tmpBuf.clear().position((int)index).limit((int)(index + length))), (long)position);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        this.checkReadableBytes((int)length);
        int readBytes = this.getBytes((int)this.readerIndex, (GatheringByteChannel)out, (int)length, (boolean)true);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        this.checkReadableBytes((int)length);
        int readBytes = this.getBytes((int)this.readerIndex, (FileChannel)out, (long)position, (int)length, (boolean)true);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.capacity());
        if (src.hasMemoryAddress()) {
            PlatformDependent.copyMemory((long)(src.memoryAddress() + (long)srcIndex), (byte[])this.array, (int)index, (long)((long)length));
            return this;
        }
        if (src.hasArray()) {
            this.setBytes((int)index, (byte[])src.array(), (int)(src.arrayOffset() + srcIndex), (int)length);
            return this;
        }
        src.getBytes((int)srcIndex, (byte[])this.array, (int)index, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.length);
        System.arraycopy((Object)src, (int)srcIndex, (Object)this.array, (int)index, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.ensureAccessible();
        src.get((byte[])this.array, (int)index, (int)src.remaining());
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.ensureAccessible();
        return in.read((byte[])this.array, (int)index, (int)length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.ensureAccessible();
        try {
            return in.read((ByteBuffer)((ByteBuffer)this.internalNioBuffer().clear().position((int)index).limit((int)(index + length))));
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        this.ensureAccessible();
        try {
            return in.read((ByteBuffer)((ByteBuffer)this.internalNioBuffer().clear().position((int)index).limit((int)(index + length))), (long)position);
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.ensureAccessible();
        return ByteBuffer.wrap((byte[])this.array, (int)index, (int)length).slice();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[]{this.nioBuffer((int)index, (int)length)};
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.checkIndex((int)index, (int)length);
        return (ByteBuffer)this.internalNioBuffer().clear().position((int)index).limit((int)(index + length));
    }

    @Override
    public byte getByte(int index) {
        this.ensureAccessible();
        return this._getByte((int)index);
    }

    @Override
    protected byte _getByte(int index) {
        return HeapByteBufUtil.getByte((byte[])this.array, (int)index);
    }

    @Override
    public short getShort(int index) {
        this.ensureAccessible();
        return this._getShort((int)index);
    }

    @Override
    protected short _getShort(int index) {
        return HeapByteBufUtil.getShort((byte[])this.array, (int)index);
    }

    @Override
    public short getShortLE(int index) {
        this.ensureAccessible();
        return this._getShortLE((int)index);
    }

    @Override
    protected short _getShortLE(int index) {
        return HeapByteBufUtil.getShortLE((byte[])this.array, (int)index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.ensureAccessible();
        return this._getUnsignedMedium((int)index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return HeapByteBufUtil.getUnsignedMedium((byte[])this.array, (int)index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        this.ensureAccessible();
        return this._getUnsignedMediumLE((int)index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return HeapByteBufUtil.getUnsignedMediumLE((byte[])this.array, (int)index);
    }

    @Override
    public int getInt(int index) {
        this.ensureAccessible();
        return this._getInt((int)index);
    }

    @Override
    protected int _getInt(int index) {
        return HeapByteBufUtil.getInt((byte[])this.array, (int)index);
    }

    @Override
    public int getIntLE(int index) {
        this.ensureAccessible();
        return this._getIntLE((int)index);
    }

    @Override
    protected int _getIntLE(int index) {
        return HeapByteBufUtil.getIntLE((byte[])this.array, (int)index);
    }

    @Override
    public long getLong(int index) {
        this.ensureAccessible();
        return this._getLong((int)index);
    }

    @Override
    protected long _getLong(int index) {
        return HeapByteBufUtil.getLong((byte[])this.array, (int)index);
    }

    @Override
    public long getLongLE(int index) {
        this.ensureAccessible();
        return this._getLongLE((int)index);
    }

    @Override
    protected long _getLongLE(int index) {
        return HeapByteBufUtil.getLongLE((byte[])this.array, (int)index);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.ensureAccessible();
        this._setByte((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        HeapByteBufUtil.setByte((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.ensureAccessible();
        this._setShort((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        HeapByteBufUtil.setShort((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.ensureAccessible();
        this._setShortLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setShortLE(int index, int value) {
        HeapByteBufUtil.setShortLE((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.ensureAccessible();
        this._setMedium((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        HeapByteBufUtil.setMedium((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.ensureAccessible();
        this._setMediumLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        HeapByteBufUtil.setMediumLE((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.ensureAccessible();
        this._setInt((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        HeapByteBufUtil.setInt((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.ensureAccessible();
        this._setIntLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setIntLE(int index, int value) {
        HeapByteBufUtil.setIntLE((byte[])this.array, (int)index, (int)value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.ensureAccessible();
        this._setLong((int)index, (long)value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        HeapByteBufUtil.setLong((byte[])this.array, (int)index, (long)value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.ensureAccessible();
        this._setLongLE((int)index, (long)value);
        return this;
    }

    @Override
    protected void _setLongLE(int index, long value) {
        HeapByteBufUtil.setLongLE((byte[])this.array, (int)index, (long)value);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex((int)index, (int)length);
        return this.alloc().heapBuffer((int)length, (int)this.maxCapacity()).writeBytes((byte[])this.array, (int)index, (int)length);
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf != null) return tmpNioBuf;
        this.tmpNioBuf = tmpNioBuf = ByteBuffer.wrap((byte[])this.array);
        return tmpNioBuf;
    }

    @Override
    protected void deallocate() {
        this.freeArray((byte[])this.array);
        this.array = EmptyArrays.EMPTY_BYTES;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }
}

