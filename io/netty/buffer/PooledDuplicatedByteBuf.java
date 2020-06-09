/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractPooledDerivedByteBuf;
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

final class PooledDuplicatedByteBuf
extends AbstractPooledDerivedByteBuf {
    private static final Recycler<PooledDuplicatedByteBuf> RECYCLER = new Recycler<PooledDuplicatedByteBuf>(){

        protected PooledDuplicatedByteBuf newObject(Recycler.Handle<PooledDuplicatedByteBuf> handle) {
            return new PooledDuplicatedByteBuf(handle);
        }
    };

    static PooledDuplicatedByteBuf newInstance(AbstractByteBuf unwrapped, ByteBuf wrapped, int readerIndex, int writerIndex) {
        PooledDuplicatedByteBuf duplicate = RECYCLER.get();
        duplicate.init((AbstractByteBuf)unwrapped, (ByteBuf)wrapped, (int)readerIndex, (int)writerIndex, (int)unwrapped.maxCapacity());
        duplicate.markReaderIndex();
        duplicate.markWriterIndex();
        return duplicate;
    }

    private PooledDuplicatedByteBuf(Recycler.Handle<PooledDuplicatedByteBuf> handle) {
        super(handle);
    }

    @Override
    public int capacity() {
        return this.unwrap().capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.unwrap().capacity((int)newCapacity);
        return this;
    }

    @Override
    public int arrayOffset() {
        return this.unwrap().arrayOffset();
    }

    @Override
    public long memoryAddress() {
        return this.unwrap().memoryAddress();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.unwrap().nioBuffer((int)index, (int)length);
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.unwrap().nioBuffers((int)index, (int)length);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.unwrap().copy((int)index, (int)length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return PooledSlicedByteBuf.newInstance((AbstractByteBuf)this.unwrap(), (ByteBuf)this, (int)index, (int)length);
    }

    @Override
    public ByteBuf duplicate() {
        return this.duplicate0().setIndex((int)this.readerIndex(), (int)this.writerIndex());
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return PooledDuplicatedByteBuf.newInstance((AbstractByteBuf)this.unwrap(), (ByteBuf)this, (int)this.readerIndex(), (int)this.writerIndex());
    }

    @Override
    public byte getByte(int index) {
        return this.unwrap().getByte((int)index);
    }

    @Override
    protected byte _getByte(int index) {
        return this.unwrap()._getByte((int)index);
    }

    @Override
    public short getShort(int index) {
        return this.unwrap().getShort((int)index);
    }

    @Override
    protected short _getShort(int index) {
        return this.unwrap()._getShort((int)index);
    }

    @Override
    public short getShortLE(int index) {
        return this.unwrap().getShortLE((int)index);
    }

    @Override
    protected short _getShortLE(int index) {
        return this.unwrap()._getShortLE((int)index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.unwrap().getUnsignedMedium((int)index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return this.unwrap()._getUnsignedMedium((int)index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.unwrap().getUnsignedMediumLE((int)index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return this.unwrap()._getUnsignedMediumLE((int)index);
    }

    @Override
    public int getInt(int index) {
        return this.unwrap().getInt((int)index);
    }

    @Override
    protected int _getInt(int index) {
        return this.unwrap()._getInt((int)index);
    }

    @Override
    public int getIntLE(int index) {
        return this.unwrap().getIntLE((int)index);
    }

    @Override
    protected int _getIntLE(int index) {
        return this.unwrap()._getIntLE((int)index);
    }

    @Override
    public long getLong(int index) {
        return this.unwrap().getLong((int)index);
    }

    @Override
    protected long _getLong(int index) {
        return this.unwrap()._getLong((int)index);
    }

    @Override
    public long getLongLE(int index) {
        return this.unwrap().getLongLE((int)index);
    }

    @Override
    protected long _getLongLE(int index) {
        return this.unwrap()._getLongLE((int)index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.unwrap().getBytes((int)index, (ByteBuf)dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.unwrap().getBytes((int)index, (byte[])dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.unwrap().getBytes((int)index, (ByteBuffer)dst);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.unwrap().setByte((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        this.unwrap()._setByte((int)index, (int)value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.unwrap().setShort((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        this.unwrap()._setShort((int)index, (int)value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.unwrap().setShortLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setShortLE(int index, int value) {
        this.unwrap()._setShortLE((int)index, (int)value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.unwrap().setMedium((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        this.unwrap()._setMedium((int)index, (int)value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.unwrap().setMediumLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        this.unwrap()._setMediumLE((int)index, (int)value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.unwrap().setInt((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        this.unwrap()._setInt((int)index, (int)value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.unwrap().setIntLE((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setIntLE(int index, int value) {
        this.unwrap()._setIntLE((int)index, (int)value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.unwrap().setLong((int)index, (long)value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        this.unwrap()._setLong((int)index, (long)value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.unwrap().setLongLE((int)index, (long)value);
        return this;
    }

    @Override
    protected void _setLongLE(int index, long value) {
        this.unwrap().setLongLE((int)index, (long)value);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.unwrap().setBytes((int)index, (byte[])src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.unwrap().setBytes((int)index, (ByteBuf)src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.unwrap().setBytes((int)index, (ByteBuffer)src);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.unwrap().getBytes((int)index, (OutputStream)out, (int)length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.unwrap().getBytes((int)index, (GatheringByteChannel)out, (int)length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.unwrap().getBytes((int)index, (FileChannel)out, (long)position, (int)length);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.unwrap().setBytes((int)index, (InputStream)in, (int)length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.unwrap().setBytes((int)index, (ScatteringByteChannel)in, (int)length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return this.unwrap().setBytes((int)index, (FileChannel)in, (long)position, (int)length);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return this.unwrap().forEachByte((int)index, (int)length, (ByteProcessor)processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return this.unwrap().forEachByteDesc((int)index, (int)length, (ByteProcessor)processor);
    }
}

