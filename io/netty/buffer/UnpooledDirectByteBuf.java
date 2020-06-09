/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
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

public class UnpooledDirectByteBuf
extends AbstractReferenceCountedByteBuf {
    private final ByteBufAllocator alloc;
    ByteBuffer buffer;
    private ByteBuffer tmpNioBuf;
    private int capacity;
    private boolean doNotFree;

    public UnpooledDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super((int)maxCapacity);
        if (alloc == null) {
            throw new NullPointerException((String)"alloc");
        }
        ObjectUtil.checkPositiveOrZero((int)initialCapacity, (String)"initialCapacity");
        ObjectUtil.checkPositiveOrZero((int)maxCapacity, (String)"maxCapacity");
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException((String)String.format((String)"initialCapacity(%d) > maxCapacity(%d)", (Object[])new Object[]{Integer.valueOf((int)initialCapacity), Integer.valueOf((int)maxCapacity)}));
        }
        this.alloc = alloc;
        this.setByteBuffer((ByteBuffer)this.allocateDirect((int)initialCapacity), (boolean)false);
    }

    protected UnpooledDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity) {
        this((ByteBufAllocator)alloc, (ByteBuffer)initialBuffer, (int)maxCapacity, (boolean)false, (boolean)true);
    }

    UnpooledDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity, boolean doFree, boolean slice) {
        super((int)maxCapacity);
        if (alloc == null) {
            throw new NullPointerException((String)"alloc");
        }
        if (initialBuffer == null) {
            throw new NullPointerException((String)"initialBuffer");
        }
        if (!initialBuffer.isDirect()) {
            throw new IllegalArgumentException((String)"initialBuffer is not a direct buffer.");
        }
        if (initialBuffer.isReadOnly()) {
            throw new IllegalArgumentException((String)"initialBuffer is a read-only buffer.");
        }
        int initialCapacity = initialBuffer.remaining();
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException((String)String.format((String)"initialCapacity(%d) > maxCapacity(%d)", (Object[])new Object[]{Integer.valueOf((int)initialCapacity), Integer.valueOf((int)maxCapacity)}));
        }
        this.alloc = alloc;
        this.doNotFree = !doFree;
        this.setByteBuffer((ByteBuffer)(slice ? initialBuffer.slice() : initialBuffer).order((ByteOrder)ByteOrder.BIG_ENDIAN), (boolean)false);
        this.writerIndex((int)initialCapacity);
    }

    protected ByteBuffer allocateDirect(int initialCapacity) {
        return ByteBuffer.allocateDirect((int)initialCapacity);
    }

    protected void freeDirect(ByteBuffer buffer) {
        PlatformDependent.freeDirectBuffer((ByteBuffer)buffer);
    }

    void setByteBuffer(ByteBuffer buffer, boolean tryFree) {
        ByteBuffer oldBuffer;
        if (tryFree && (oldBuffer = this.buffer) != null) {
            if (this.doNotFree) {
                this.doNotFree = false;
            } else {
                this.freeDirect((ByteBuffer)oldBuffer);
            }
        }
        this.buffer = buffer;
        this.tmpNioBuf = null;
        this.capacity = buffer.remaining();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        int bytesToCopy;
        this.checkNewCapacity((int)newCapacity);
        int oldCapacity = this.capacity;
        if (newCapacity == oldCapacity) {
            return this;
        }
        if (newCapacity > oldCapacity) {
            bytesToCopy = oldCapacity;
        } else {
            this.trimIndicesToCapacity((int)newCapacity);
            bytesToCopy = newCapacity;
        }
        ByteBuffer oldBuffer = this.buffer;
        ByteBuffer newBuffer = this.allocateDirect((int)newCapacity);
        oldBuffer.position((int)0).limit((int)bytesToCopy);
        newBuffer.position((int)0).limit((int)bytesToCopy);
        newBuffer.put((ByteBuffer)oldBuffer).clear();
        this.setByteBuffer((ByteBuffer)newBuffer, (boolean)true);
        return this;
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
        this.getBytes((int)index, (byte[])dst, (int)dstIndex, (int)length, (boolean)false);
        return this;
    }

    void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.length);
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        tmpBuf.get((byte[])dst, (int)dstIndex, (int)length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.checkReadableBytes((int)length);
        this.getBytes((int)this.readerIndex, (byte[])dst, (int)dstIndex, (int)length, (boolean)true);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.getBytes((int)index, (ByteBuffer)dst, (boolean)false);
        return this;
    }

    void getBytes(int index, ByteBuffer dst, boolean internal) {
        this.checkIndex((int)index, (int)dst.remaining());
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position((int)index).limit((int)(index + dst.remaining()));
        dst.put((ByteBuffer)tmpBuf);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        int length = dst.remaining();
        this.checkReadableBytes((int)length);
        this.getBytes((int)this.readerIndex, (ByteBuffer)dst, (boolean)true);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.ensureAccessible();
        this._setByte((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        this.buffer.put((int)index, (byte)((byte)value));
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.ensureAccessible();
        this._setShort((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        this.buffer.putShort((int)index, (short)((short)value));
    }

    @Override
    protected void _setShortLE(int index, int value) {
        this.buffer.putShort((int)index, (short)ByteBufUtil.swapShort((short)((short)value)));
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.ensureAccessible();
        this._setMedium((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        this.setByte((int)index, (int)((byte)(value >>> 16)));
        this.setByte((int)(index + 1), (int)((byte)(value >>> 8)));
        this.setByte((int)(index + 2), (int)((byte)value));
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        this.setByte((int)index, (int)((byte)value));
        this.setByte((int)(index + 1), (int)((byte)(value >>> 8)));
        this.setByte((int)(index + 2), (int)((byte)(value >>> 16)));
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.ensureAccessible();
        this._setInt((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        this.buffer.putInt((int)index, (int)value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        this.buffer.putInt((int)index, (int)ByteBufUtil.swapInt((int)value));
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.ensureAccessible();
        this._setLong((int)index, (long)value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        this.buffer.putLong((int)index, (long)value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        this.buffer.putLong((int)index, (long)ByteBufUtil.swapLong((long)value));
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.capacity());
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
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        tmpBuf.put((byte[])src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.ensureAccessible();
        ByteBuffer tmpBuf = this.internalNioBuffer();
        if (src == tmpBuf) {
            src = src.duplicate();
        }
        tmpBuf.clear().position((int)index).limit((int)(index + src.remaining()));
        tmpBuf.put((ByteBuffer)src);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.getBytes((int)index, (OutputStream)out, (int)length, (boolean)false);
        return this;
    }

    void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return;
        }
        ByteBufUtil.readBytes((ByteBufAllocator)this.alloc(), (ByteBuffer)(internal ? this.internalNioBuffer() : this.buffer.duplicate()), (int)index, (int)length, (OutputStream)out);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.checkReadableBytes((int)length);
        this.getBytes((int)this.readerIndex, (OutputStream)out, (int)length, (boolean)true);
        this.readerIndex += length;
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.getBytes((int)index, (GatheringByteChannel)out, (int)length, (boolean)false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        return out.write((ByteBuffer)tmpBuf);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.getBytes((int)index, (FileChannel)out, (long)position, (int)length, (boolean)false);
    }

    private int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        return out.write((ByteBuffer)tmpBuf, (long)position);
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
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.ensureAccessible();
        if (this.buffer.hasArray()) {
            return in.read((byte[])this.buffer.array(), (int)(this.buffer.arrayOffset() + index), (int)length);
        }
        byte[] tmp = ByteBufUtil.threadLocalTempArray((int)length);
        int readBytes = in.read((byte[])tmp, (int)0, (int)length);
        if (readBytes <= 0) {
            return readBytes;
        }
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index);
        tmpBuf.put((byte[])tmp, (int)0, (int)readBytes);
        return readBytes;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        try {
            return in.read((ByteBuffer)tmpBuf);
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position((int)index).limit((int)(index + length));
        try {
            return in.read((ByteBuffer)tmpBuf, (long)position);
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
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[]{this.nioBuffer((int)index, (int)length)};
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.ensureAccessible();
        try {
            ByteBuffer src = (ByteBuffer)this.buffer.duplicate().clear().position((int)index).limit((int)(index + length));
            return this.alloc().directBuffer((int)length, (int)this.maxCapacity()).writeBytes((ByteBuffer)src);
        }
        catch (IllegalArgumentException ignored) {
            throw new IndexOutOfBoundsException((String)("Too many bytes to read - Need " + (index + length)));
        }
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.checkIndex((int)index, (int)length);
        return (ByteBuffer)this.internalNioBuffer().clear().position((int)index).limit((int)(index + length));
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf != null) return tmpNioBuf;
        this.tmpNioBuf = tmpNioBuf = this.buffer.duplicate();
        return tmpNioBuf;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex((int)index, (int)length);
        return ((ByteBuffer)this.buffer.duplicate().position((int)index).limit((int)(index + length))).slice();
    }

    @Override
    protected void deallocate() {
        ByteBuffer buffer = this.buffer;
        if (buffer == null) {
            return;
        }
        this.buffer = null;
        if (this.doNotFree) return;
        this.freeDirect((ByteBuffer)buffer);
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }
}

