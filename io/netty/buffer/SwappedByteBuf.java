/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

@Deprecated
public class SwappedByteBuf
extends ByteBuf {
    private final ByteBuf buf;
    private final ByteOrder order;

    public SwappedByteBuf(ByteBuf buf) {
        if (buf == null) {
            throw new NullPointerException((String)"buf");
        }
        this.buf = buf;
        if (buf.order() == ByteOrder.BIG_ENDIAN) {
            this.order = ByteOrder.LITTLE_ENDIAN;
            return;
        }
        this.order = ByteOrder.BIG_ENDIAN;
    }

    @Override
    public ByteOrder order() {
        return this.order;
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (endianness == null) {
            throw new NullPointerException((String)"endianness");
        }
        if (endianness != this.order) return this.buf;
        return this;
    }

    @Override
    public ByteBuf unwrap() {
        return this.buf;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    @Override
    public int capacity() {
        return this.buf.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.buf.capacity((int)newCapacity);
        return this;
    }

    @Override
    public int maxCapacity() {
        return this.buf.maxCapacity();
    }

    @Override
    public boolean isReadOnly() {
        return this.buf.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return Unpooled.unmodifiableBuffer((ByteBuf)this);
    }

    @Override
    public boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        this.buf.readerIndex((int)readerIndex);
        return this;
    }

    @Override
    public int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        this.buf.writerIndex((int)writerIndex);
        return this;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        this.buf.setIndex((int)readerIndex, (int)writerIndex);
        return this;
    }

    @Override
    public int readableBytes() {
        return this.buf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.buf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    @Override
    public int maxFastWritableBytes() {
        return this.buf.maxFastWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.buf.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return this.buf.isReadable((int)size);
    }

    @Override
    public boolean isWritable() {
        return this.buf.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return this.buf.isWritable((int)size);
    }

    @Override
    public ByteBuf clear() {
        this.buf.clear();
        return this;
    }

    @Override
    public ByteBuf markReaderIndex() {
        this.buf.markReaderIndex();
        return this;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        this.buf.resetReaderIndex();
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        this.buf.markWriterIndex();
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        this.buf.resetWriterIndex();
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        this.buf.discardReadBytes();
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        this.buf.discardSomeReadBytes();
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int writableBytes) {
        this.buf.ensureWritable((int)writableBytes);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return this.buf.ensureWritable((int)minWritableBytes, (boolean)force);
    }

    @Override
    public boolean getBoolean(int index) {
        return this.buf.getBoolean((int)index);
    }

    @Override
    public byte getByte(int index) {
        return this.buf.getByte((int)index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return this.buf.getUnsignedByte((int)index);
    }

    @Override
    public short getShort(int index) {
        return ByteBufUtil.swapShort((short)this.buf.getShort((int)index));
    }

    @Override
    public short getShortLE(int index) {
        return this.buf.getShort((int)index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.getShort((int)index) & 65535;
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.getShortLE((int)index) & 65535;
    }

    @Override
    public int getMedium(int index) {
        return ByteBufUtil.swapMedium((int)this.buf.getMedium((int)index));
    }

    @Override
    public int getMediumLE(int index) {
        return this.buf.getMedium((int)index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.getMedium((int)index) & 16777215;
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.getMediumLE((int)index) & 16777215;
    }

    @Override
    public int getInt(int index) {
        return ByteBufUtil.swapInt((int)this.buf.getInt((int)index));
    }

    @Override
    public int getIntLE(int index) {
        return this.buf.getInt((int)index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return (long)this.getInt((int)index) & 0xFFFFFFFFL;
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return (long)this.getIntLE((int)index) & 0xFFFFFFFFL;
    }

    @Override
    public long getLong(int index) {
        return ByteBufUtil.swapLong((long)this.buf.getLong((int)index));
    }

    @Override
    public long getLongLE(int index) {
        return this.buf.getLong((int)index);
    }

    @Override
    public char getChar(int index) {
        return (char)this.getShort((int)index);
    }

    @Override
    public float getFloat(int index) {
        return Float.intBitsToFloat((int)this.getInt((int)index));
    }

    @Override
    public double getDouble(int index) {
        return Double.longBitsToDouble((long)this.getLong((int)index));
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        this.buf.getBytes((int)index, (ByteBuf)dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.buf.getBytes((int)index, (ByteBuf)dst, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.buf.getBytes((int)index, (ByteBuf)dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        this.buf.getBytes((int)index, (byte[])dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.buf.getBytes((int)index, (byte[])dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.buf.getBytes((int)index, (ByteBuffer)dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.buf.getBytes((int)index, (OutputStream)out, (int)length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.buf.getBytes((int)index, (GatheringByteChannel)out, (int)length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.buf.getBytes((int)index, (FileChannel)out, (long)position, (int)length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.buf.getCharSequence((int)index, (int)length, (Charset)charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        this.buf.setBoolean((int)index, (boolean)value);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.buf.setByte((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.buf.setShort((int)index, (int)ByteBufUtil.swapShort((short)((short)value)));
        return this;
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.buf.setShort((int)index, (int)((short)value));
        return this;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.buf.setMedium((int)index, (int)ByteBufUtil.swapMedium((int)value));
        return this;
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.buf.setMedium((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.buf.setInt((int)index, (int)ByteBufUtil.swapInt((int)value));
        return this;
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.buf.setInt((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.buf.setLong((int)index, (long)ByteBufUtil.swapLong((long)value));
        return this;
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.buf.setLong((int)index, (long)value);
        return this;
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        this.setShort((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        this.setInt((int)index, (int)Float.floatToRawIntBits((float)value));
        return this;
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        this.setLong((int)index, (long)Double.doubleToRawLongBits((double)value));
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        this.buf.setBytes((int)index, (ByteBuf)src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        this.buf.setBytes((int)index, (ByteBuf)src, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.buf.setBytes((int)index, (ByteBuf)src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        this.buf.setBytes((int)index, (byte[])src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.buf.setBytes((int)index, (byte[])src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.buf.setBytes((int)index, (ByteBuffer)src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.buf.setBytes((int)index, (InputStream)in, (int)length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.buf.setBytes((int)index, (ScatteringByteChannel)in, (int)length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return this.buf.setBytes((int)index, (FileChannel)in, (long)position, (int)length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        this.buf.setZero((int)index, (int)length);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.buf.setCharSequence((int)index, (CharSequence)sequence, (Charset)charset);
    }

    @Override
    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return ByteBufUtil.swapShort((short)this.buf.readShort());
    }

    @Override
    public short readShortLE() {
        return this.buf.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.readShort() & 65535;
    }

    @Override
    public int readUnsignedShortLE() {
        return this.readShortLE() & 65535;
    }

    @Override
    public int readMedium() {
        return ByteBufUtil.swapMedium((int)this.buf.readMedium());
    }

    @Override
    public int readMediumLE() {
        return this.buf.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        return this.readMedium() & 16777215;
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.readMediumLE() & 16777215;
    }

    @Override
    public int readInt() {
        return ByteBufUtil.swapInt((int)this.buf.readInt());
    }

    @Override
    public int readIntLE() {
        return this.buf.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return (long)this.readInt() & 0xFFFFFFFFL;
    }

    @Override
    public long readUnsignedIntLE() {
        return (long)this.readIntLE() & 0xFFFFFFFFL;
    }

    @Override
    public long readLong() {
        return ByteBufUtil.swapLong((long)this.buf.readLong());
    }

    @Override
    public long readLongLE() {
        return this.buf.readLong();
    }

    @Override
    public char readChar() {
        return (char)this.readShort();
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat((int)this.readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble((long)this.readLong());
    }

    @Override
    public ByteBuf readBytes(int length) {
        return this.buf.readBytes((int)length).order((ByteOrder)this.order());
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.buf.readSlice((int)length).order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.buf.readRetainedSlice((int)length).order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        this.buf.readBytes((ByteBuf)dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        this.buf.readBytes((ByteBuf)dst, (int)length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        this.buf.readBytes((ByteBuf)dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        this.buf.readBytes((byte[])dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.buf.readBytes((byte[])dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        this.buf.readBytes((ByteBuffer)dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.buf.readBytes((OutputStream)out, (int)length);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return this.buf.readBytes((GatheringByteChannel)out, (int)length);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return this.buf.readBytes((FileChannel)out, (long)position, (int)length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return this.buf.readCharSequence((int)length, (Charset)charset);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        this.buf.skipBytes((int)length);
        return this;
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        this.buf.writeBoolean((boolean)value);
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        this.buf.writeByte((int)value);
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        this.buf.writeShort((int)ByteBufUtil.swapShort((short)((short)value)));
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        this.buf.writeShort((int)((short)value));
        return this;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        this.buf.writeMedium((int)ByteBufUtil.swapMedium((int)value));
        return this;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        this.buf.writeMedium((int)value);
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        this.buf.writeInt((int)ByteBufUtil.swapInt((int)value));
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        this.buf.writeInt((int)value);
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        this.buf.writeLong((long)ByteBufUtil.swapLong((long)value));
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        this.buf.writeLong((long)value);
        return this;
    }

    @Override
    public ByteBuf writeChar(int value) {
        this.writeShort((int)value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        this.writeInt((int)Float.floatToRawIntBits((float)value));
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        this.writeLong((long)Double.doubleToRawLongBits((double)value));
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        this.buf.writeBytes((ByteBuf)src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        this.buf.writeBytes((ByteBuf)src, (int)length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        this.buf.writeBytes((ByteBuf)src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        this.buf.writeBytes((byte[])src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        this.buf.writeBytes((byte[])src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        this.buf.writeBytes((ByteBuffer)src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return this.buf.writeBytes((InputStream)in, (int)length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return this.buf.writeBytes((ScatteringByteChannel)in, (int)length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return this.buf.writeBytes((FileChannel)in, (long)position, (int)length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        this.buf.writeZero((int)length);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.buf.writeCharSequence((CharSequence)sequence, (Charset)charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return this.buf.indexOf((int)fromIndex, (int)toIndex, (byte)value);
    }

    @Override
    public int bytesBefore(byte value) {
        return this.buf.bytesBefore((byte)value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return this.buf.bytesBefore((int)length, (byte)value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return this.buf.bytesBefore((int)index, (int)length, (byte)value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return this.buf.forEachByte((ByteProcessor)processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return this.buf.forEachByte((int)index, (int)length, (ByteProcessor)processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return this.buf.forEachByteDesc((ByteProcessor)processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return this.buf.forEachByteDesc((int)index, (int)length, (ByteProcessor)processor);
    }

    @Override
    public ByteBuf copy() {
        return this.buf.copy().order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.buf.copy((int)index, (int)length).order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf slice() {
        return this.buf.slice().order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.buf.retainedSlice().order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.buf.slice((int)index, (int)length).order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.buf.retainedSlice((int)index, (int)length).order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf duplicate() {
        return this.buf.duplicate().order((ByteOrder)this.order);
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.buf.retainedDuplicate().order((ByteOrder)this.order);
    }

    @Override
    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer().order((ByteOrder)this.order);
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.buf.nioBuffer((int)index, (int)length).order((ByteOrder)this.order);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.nioBuffer((int)index, (int)length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        ByteBuffer[] nioBuffers = this.buf.nioBuffers();
        int i = 0;
        while (i < nioBuffers.length) {
            nioBuffers[i] = nioBuffers[i].order((ByteOrder)this.order);
            ++i;
        }
        return nioBuffers;
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        ByteBuffer[] nioBuffers = this.buf.nioBuffers((int)index, (int)length);
        int i = 0;
        while (i < nioBuffers.length) {
            nioBuffers[i] = nioBuffers[i].order((ByteOrder)this.order);
            ++i;
        }
        return nioBuffers;
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.buf.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return this.buf.toString((Charset)charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return this.buf.toString((int)index, (int)length, (Charset)charset);
    }

    @Override
    public int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    final boolean isAccessible() {
        return this.buf.isAccessible();
    }

    @Override
    public ByteBuf retain() {
        this.buf.retain();
        return this;
    }

    @Override
    public ByteBuf retain(int increment) {
        this.buf.retain((int)increment);
        return this;
    }

    @Override
    public ByteBuf touch() {
        this.buf.touch();
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        this.buf.touch((Object)hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.buf.release((int)decrement);
    }

    @Override
    public int hashCode() {
        return this.buf.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ByteBuf)) return false;
        return ByteBufUtil.equals((ByteBuf)this, (ByteBuf)((ByteBuf)obj));
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return ByteBufUtil.compare((ByteBuf)this, (ByteBuf)buffer);
    }

    @Override
    public String toString() {
        return "Swapped(" + this.buf + ')';
    }
}

