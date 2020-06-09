/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

class WrappedByteBuf
extends ByteBuf {
    protected final ByteBuf buf;

    protected WrappedByteBuf(ByteBuf buf) {
        if (buf == null) {
            throw new NullPointerException((String)"buf");
        }
        this.buf = buf;
    }

    @Override
    public final boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    @Override
    public final long memoryAddress() {
        return this.buf.memoryAddress();
    }

    @Override
    public final int capacity() {
        return this.buf.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.buf.capacity((int)newCapacity);
        return this;
    }

    @Override
    public final int maxCapacity() {
        return this.buf.maxCapacity();
    }

    @Override
    public final ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    @Override
    public final ByteOrder order() {
        return this.buf.order();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        return this.buf.order((ByteOrder)endianness);
    }

    @Override
    public final ByteBuf unwrap() {
        return this.buf;
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.buf.asReadOnly();
    }

    @Override
    public boolean isReadOnly() {
        return this.buf.isReadOnly();
    }

    @Override
    public final boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public final int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public final ByteBuf readerIndex(int readerIndex) {
        this.buf.readerIndex((int)readerIndex);
        return this;
    }

    @Override
    public final int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public final ByteBuf writerIndex(int writerIndex) {
        this.buf.writerIndex((int)writerIndex);
        return this;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        this.buf.setIndex((int)readerIndex, (int)writerIndex);
        return this;
    }

    @Override
    public final int readableBytes() {
        return this.buf.readableBytes();
    }

    @Override
    public final int writableBytes() {
        return this.buf.writableBytes();
    }

    @Override
    public final int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    @Override
    public int maxFastWritableBytes() {
        return this.buf.maxFastWritableBytes();
    }

    @Override
    public final boolean isReadable() {
        return this.buf.isReadable();
    }

    @Override
    public final boolean isWritable() {
        return this.buf.isWritable();
    }

    @Override
    public final ByteBuf clear() {
        this.buf.clear();
        return this;
    }

    @Override
    public final ByteBuf markReaderIndex() {
        this.buf.markReaderIndex();
        return this;
    }

    @Override
    public final ByteBuf resetReaderIndex() {
        this.buf.resetReaderIndex();
        return this;
    }

    @Override
    public final ByteBuf markWriterIndex() {
        this.buf.markWriterIndex();
        return this;
    }

    @Override
    public final ByteBuf resetWriterIndex() {
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
    public ByteBuf ensureWritable(int minWritableBytes) {
        this.buf.ensureWritable((int)minWritableBytes);
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
        return this.buf.getShort((int)index);
    }

    @Override
    public short getShortLE(int index) {
        return this.buf.getShortLE((int)index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.buf.getUnsignedShort((int)index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.buf.getUnsignedShortLE((int)index);
    }

    @Override
    public int getMedium(int index) {
        return this.buf.getMedium((int)index);
    }

    @Override
    public int getMediumLE(int index) {
        return this.buf.getMediumLE((int)index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.buf.getUnsignedMedium((int)index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.buf.getUnsignedMediumLE((int)index);
    }

    @Override
    public int getInt(int index) {
        return this.buf.getInt((int)index);
    }

    @Override
    public int getIntLE(int index) {
        return this.buf.getIntLE((int)index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.buf.getUnsignedInt((int)index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return this.buf.getUnsignedIntLE((int)index);
    }

    @Override
    public long getLong(int index) {
        return this.buf.getLong((int)index);
    }

    @Override
    public long getLongLE(int index) {
        return this.buf.getLongLE((int)index);
    }

    @Override
    public char getChar(int index) {
        return this.buf.getChar((int)index);
    }

    @Override
    public float getFloat(int index) {
        return this.buf.getFloat((int)index);
    }

    @Override
    public double getDouble(int index) {
        return this.buf.getDouble((int)index);
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
        this.buf.setShort((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.buf.setShortLE((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.buf.setMedium((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.buf.setMediumLE((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.buf.setInt((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.buf.setIntLE((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.buf.setLong((int)index, (long)value);
        return this;
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.buf.setLongLE((int)index, (long)value);
        return this;
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        this.buf.setChar((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        this.buf.setFloat((int)index, (float)value);
        return this;
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        this.buf.setDouble((int)index, (double)value);
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
        return this.buf.readShort();
    }

    @Override
    public short readShortLE() {
        return this.buf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.buf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.buf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.buf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.buf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.buf.readInt();
    }

    @Override
    public int readIntLE() {
        return this.buf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.buf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public long readLongLE() {
        return this.buf.readLongLE();
    }

    @Override
    public char readChar() {
        return this.buf.readChar();
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return this.buf.readBytes((int)length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.buf.readSlice((int)length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.buf.readRetainedSlice((int)length);
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
        this.buf.writeShort((int)value);
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        this.buf.writeShortLE((int)value);
        return this;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        this.buf.writeMedium((int)value);
        return this;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        this.buf.writeMediumLE((int)value);
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        this.buf.writeInt((int)value);
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        this.buf.writeIntLE((int)value);
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        this.buf.writeLong((long)value);
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        this.buf.writeLongLE((long)value);
        return this;
    }

    @Override
    public ByteBuf writeChar(int value) {
        this.buf.writeChar((int)value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        this.buf.writeFloat((float)value);
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        this.buf.writeDouble((double)value);
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
        return this.buf.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.buf.copy((int)index, (int)length);
    }

    @Override
    public ByteBuf slice() {
        return this.buf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.buf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.buf.slice((int)index, (int)length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.buf.retainedSlice((int)index, (int)length);
    }

    @Override
    public ByteBuf duplicate() {
        return this.buf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.buf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.buf.nioBuffer((int)index, (int)length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.buf.nioBuffers((int)index, (int)length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.buf.internalNioBuffer((int)index, (int)length);
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
    public String toString(Charset charset) {
        return this.buf.toString((Charset)charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return this.buf.toString((int)index, (int)length, (Charset)charset);
    }

    @Override
    public int hashCode() {
        return this.buf.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.buf.equals((Object)obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return this.buf.compareTo((ByteBuf)buffer);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName((Object)this) + '(' + this.buf.toString() + ')';
    }

    @Override
    public ByteBuf retain(int increment) {
        this.buf.retain((int)increment);
        return this;
    }

    @Override
    public ByteBuf retain() {
        this.buf.retain();
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
    public final boolean isReadable(int size) {
        return this.buf.isReadable((int)size);
    }

    @Override
    public final boolean isWritable(int size) {
        return this.buf.isWritable((int)size);
    }

    @Override
    public final int refCnt() {
        return this.buf.refCnt();
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
    final boolean isAccessible() {
        return this.buf.isAccessible();
    }
}

