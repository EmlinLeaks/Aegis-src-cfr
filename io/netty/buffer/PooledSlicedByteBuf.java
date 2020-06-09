/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractPooledDerivedByteBuf;
import io.netty.buffer.AbstractUnpooledSlicedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledDuplicatedByteBuf;
import io.netty.buffer.PooledSlicedByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.Recycler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledSlicedByteBuf
extends AbstractPooledDerivedByteBuf {
    private static final Recycler<PooledSlicedByteBuf> RECYCLER = new Recycler<PooledSlicedByteBuf>(){

        protected PooledSlicedByteBuf newObject(Recycler.Handle<PooledSlicedByteBuf> handle) {
            return new PooledSlicedByteBuf(handle);
        }
    };
    int adjustment;

    static PooledSlicedByteBuf newInstance(AbstractByteBuf unwrapped, ByteBuf wrapped, int index, int length) {
        AbstractUnpooledSlicedByteBuf.checkSliceOutOfBounds((int)index, (int)length, (ByteBuf)unwrapped);
        return PooledSlicedByteBuf.newInstance0((AbstractByteBuf)unwrapped, (ByteBuf)wrapped, (int)index, (int)length);
    }

    private static PooledSlicedByteBuf newInstance0(AbstractByteBuf unwrapped, ByteBuf wrapped, int adjustment, int length) {
        PooledSlicedByteBuf slice = RECYCLER.get();
        slice.init((AbstractByteBuf)unwrapped, (ByteBuf)wrapped, (int)0, (int)length, (int)length);
        slice.discardMarks();
        slice.adjustment = adjustment;
        return slice;
    }

    private PooledSlicedByteBuf(Recycler.Handle<PooledSlicedByteBuf> handle) {
        super(handle);
    }

    @Override
    public int capacity() {
        return this.maxCapacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        throw new UnsupportedOperationException((String)"sliced buffer");
    }

    @Override
    public int arrayOffset() {
        return this.idx((int)this.unwrap().arrayOffset());
    }

    @Override
    public long memoryAddress() {
        return this.unwrap().memoryAddress() + (long)this.adjustment;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex0((int)index, (int)length);
        return this.unwrap().nioBuffer((int)this.idx((int)index), (int)length);
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        this.checkIndex0((int)index, (int)length);
        return this.unwrap().nioBuffers((int)this.idx((int)index), (int)length);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex0((int)index, (int)length);
        return this.unwrap().copy((int)this.idx((int)index), (int)length);
    }

    @Override
    public ByteBuf slice(int index, int length) {
        this.checkIndex0((int)index, (int)length);
        return super.slice((int)this.idx((int)index), (int)length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        this.checkIndex0((int)index, (int)length);
        return PooledSlicedByteBuf.newInstance0((AbstractByteBuf)this.unwrap(), (ByteBuf)this, (int)this.idx((int)index), (int)length);
    }

    @Override
    public ByteBuf duplicate() {
        return this.duplicate0().setIndex((int)this.idx((int)this.readerIndex()), (int)this.idx((int)this.writerIndex()));
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return PooledDuplicatedByteBuf.newInstance((AbstractByteBuf)this.unwrap(), (ByteBuf)this, (int)this.idx((int)this.readerIndex()), (int)this.idx((int)this.writerIndex()));
    }

    @Override
    public byte getByte(int index) {
        this.checkIndex0((int)index, (int)1);
        return this.unwrap().getByte((int)this.idx((int)index));
    }

    @Override
    protected byte _getByte(int index) {
        return this.unwrap()._getByte((int)this.idx((int)index));
    }

    @Override
    public short getShort(int index) {
        this.checkIndex0((int)index, (int)2);
        return this.unwrap().getShort((int)this.idx((int)index));
    }

    @Override
    protected short _getShort(int index) {
        return this.unwrap()._getShort((int)this.idx((int)index));
    }

    @Override
    public short getShortLE(int index) {
        this.checkIndex0((int)index, (int)2);
        return this.unwrap().getShortLE((int)this.idx((int)index));
    }

    @Override
    protected short _getShortLE(int index) {
        return this.unwrap()._getShortLE((int)this.idx((int)index));
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.checkIndex0((int)index, (int)3);
        return this.unwrap().getUnsignedMedium((int)this.idx((int)index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return this.unwrap()._getUnsignedMedium((int)this.idx((int)index));
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        this.checkIndex0((int)index, (int)3);
        return this.unwrap().getUnsignedMediumLE((int)this.idx((int)index));
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return this.unwrap()._getUnsignedMediumLE((int)this.idx((int)index));
    }

    @Override
    public int getInt(int index) {
        this.checkIndex0((int)index, (int)4);
        return this.unwrap().getInt((int)this.idx((int)index));
    }

    @Override
    protected int _getInt(int index) {
        return this.unwrap()._getInt((int)this.idx((int)index));
    }

    @Override
    public int getIntLE(int index) {
        this.checkIndex0((int)index, (int)4);
        return this.unwrap().getIntLE((int)this.idx((int)index));
    }

    @Override
    protected int _getIntLE(int index) {
        return this.unwrap()._getIntLE((int)this.idx((int)index));
    }

    @Override
    public long getLong(int index) {
        this.checkIndex0((int)index, (int)8);
        return this.unwrap().getLong((int)this.idx((int)index));
    }

    @Override
    protected long _getLong(int index) {
        return this.unwrap()._getLong((int)this.idx((int)index));
    }

    @Override
    public long getLongLE(int index) {
        this.checkIndex0((int)index, (int)8);
        return this.unwrap().getLongLE((int)this.idx((int)index));
    }

    @Override
    protected long _getLongLE(int index) {
        return this.unwrap()._getLongLE((int)this.idx((int)index));
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkIndex0((int)index, (int)length);
        this.unwrap().getBytes((int)this.idx((int)index), (ByteBuf)dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkIndex0((int)index, (int)length);
        this.unwrap().getBytes((int)this.idx((int)index), (byte[])dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.checkIndex0((int)index, (int)dst.remaining());
        this.unwrap().getBytes((int)this.idx((int)index), (ByteBuffer)dst);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.checkIndex0((int)index, (int)1);
        this.unwrap().setByte((int)this.idx((int)index), (int)value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        this.unwrap()._setByte((int)this.idx((int)index), (int)value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.checkIndex0((int)index, (int)2);
        this.unwrap().setShort((int)this.idx((int)index), (int)value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        this.unwrap()._setShort((int)this.idx((int)index), (int)value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.checkIndex0((int)index, (int)2);
        this.unwrap().setShortLE((int)this.idx((int)index), (int)value);
        return this;
    }

    @Override
    protected void _setShortLE(int index, int value) {
        this.unwrap()._setShortLE((int)this.idx((int)index), (int)value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.checkIndex0((int)index, (int)3);
        this.unwrap().setMedium((int)this.idx((int)index), (int)value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        this.unwrap()._setMedium((int)this.idx((int)index), (int)value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.checkIndex0((int)index, (int)3);
        this.unwrap().setMediumLE((int)this.idx((int)index), (int)value);
        return this;
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        this.unwrap()._setMediumLE((int)this.idx((int)index), (int)value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.checkIndex0((int)index, (int)4);
        this.unwrap().setInt((int)this.idx((int)index), (int)value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        this.unwrap()._setInt((int)this.idx((int)index), (int)value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.checkIndex0((int)index, (int)4);
        this.unwrap().setIntLE((int)this.idx((int)index), (int)value);
        return this;
    }

    @Override
    protected void _setIntLE(int index, int value) {
        this.unwrap()._setIntLE((int)this.idx((int)index), (int)value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.checkIndex0((int)index, (int)8);
        this.unwrap().setLong((int)this.idx((int)index), (long)value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        this.unwrap()._setLong((int)this.idx((int)index), (long)value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.checkIndex0((int)index, (int)8);
        this.unwrap().setLongLE((int)this.idx((int)index), (long)value);
        return this;
    }

    @Override
    protected void _setLongLE(int index, long value) {
        this.unwrap().setLongLE((int)this.idx((int)index), (long)value);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkIndex0((int)index, (int)length);
        this.unwrap().setBytes((int)this.idx((int)index), (byte[])src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkIndex0((int)index, (int)length);
        this.unwrap().setBytes((int)this.idx((int)index), (ByteBuf)src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.checkIndex0((int)index, (int)src.remaining());
        this.unwrap().setBytes((int)this.idx((int)index), (ByteBuffer)src);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.checkIndex0((int)index, (int)length);
        this.unwrap().getBytes((int)this.idx((int)index), (OutputStream)out, (int)length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        this.checkIndex0((int)index, (int)length);
        return this.unwrap().getBytes((int)this.idx((int)index), (GatheringByteChannel)out, (int)length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        this.checkIndex0((int)index, (int)length);
        return this.unwrap().getBytes((int)this.idx((int)index), (FileChannel)out, (long)position, (int)length);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.checkIndex0((int)index, (int)length);
        return this.unwrap().setBytes((int)this.idx((int)index), (InputStream)in, (int)length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.checkIndex0((int)index, (int)length);
        return this.unwrap().setBytes((int)this.idx((int)index), (ScatteringByteChannel)in, (int)length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        this.checkIndex0((int)index, (int)length);
        return this.unwrap().setBytes((int)this.idx((int)index), (FileChannel)in, (long)position, (int)length);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        this.checkIndex0((int)index, (int)length);
        int ret = this.unwrap().forEachByte((int)this.idx((int)index), (int)length, (ByteProcessor)processor);
        if (ret >= this.adjustment) return ret - this.adjustment;
        return -1;
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        this.checkIndex0((int)index, (int)length);
        int ret = this.unwrap().forEachByteDesc((int)this.idx((int)index), (int)length, (ByteProcessor)processor);
        if (ret >= this.adjustment) return ret - this.adjustment;
        return -1;
    }

    private int idx(int index) {
        return index + this.adjustment;
    }
}

