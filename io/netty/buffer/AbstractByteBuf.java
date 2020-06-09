/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDuplicatedByteBuf;
import io.netty.buffer.UnpooledSlicedByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public abstract class AbstractByteBuf
extends ByteBuf {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractByteBuf.class);
    private static final String LEGACY_PROP_CHECK_ACCESSIBLE = "io.netty.buffer.bytebuf.checkAccessible";
    private static final String PROP_CHECK_ACCESSIBLE = "io.netty.buffer.checkAccessible";
    static final boolean checkAccessible = SystemPropertyUtil.contains((String)"io.netty.buffer.checkAccessible") ? SystemPropertyUtil.getBoolean((String)"io.netty.buffer.checkAccessible", (boolean)true) : SystemPropertyUtil.getBoolean((String)"io.netty.buffer.bytebuf.checkAccessible", (boolean)true);
    private static final String PROP_CHECK_BOUNDS = "io.netty.buffer.checkBounds";
    private static final boolean checkBounds = SystemPropertyUtil.getBoolean((String)"io.netty.buffer.checkBounds", (boolean)true);
    static final ResourceLeakDetector<ByteBuf> leakDetector;
    int readerIndex;
    int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
    private int maxCapacity;

    protected AbstractByteBuf(int maxCapacity) {
        ObjectUtil.checkPositiveOrZero((int)maxCapacity, (String)"maxCapacity");
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ByteBuf asReadOnly() {
        if (!this.isReadOnly()) return Unpooled.unmodifiableBuffer((ByteBuf)this);
        return this;
    }

    @Override
    public int maxCapacity() {
        return this.maxCapacity;
    }

    protected final void maxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public int readerIndex() {
        return this.readerIndex;
    }

    private static void checkIndexBounds(int readerIndex, int writerIndex, int capacity) {
        if (readerIndex < 0 || readerIndex > writerIndex) throw new IndexOutOfBoundsException((String)String.format((String)"readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))", (Object[])new Object[]{Integer.valueOf((int)readerIndex), Integer.valueOf((int)writerIndex), Integer.valueOf((int)capacity)}));
        if (writerIndex <= capacity) return;
        throw new IndexOutOfBoundsException((String)String.format((String)"readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))", (Object[])new Object[]{Integer.valueOf((int)readerIndex), Integer.valueOf((int)writerIndex), Integer.valueOf((int)capacity)}));
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        if (checkBounds) {
            AbstractByteBuf.checkIndexBounds((int)readerIndex, (int)this.writerIndex, (int)this.capacity());
        }
        this.readerIndex = readerIndex;
        return this;
    }

    @Override
    public int writerIndex() {
        return this.writerIndex;
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        if (checkBounds) {
            AbstractByteBuf.checkIndexBounds((int)this.readerIndex, (int)writerIndex, (int)this.capacity());
        }
        this.writerIndex = writerIndex;
        return this;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        if (checkBounds) {
            AbstractByteBuf.checkIndexBounds((int)readerIndex, (int)writerIndex, (int)this.capacity());
        }
        this.setIndex0((int)readerIndex, (int)writerIndex);
        return this;
    }

    @Override
    public ByteBuf clear() {
        this.writerIndex = 0;
        this.readerIndex = 0;
        return this;
    }

    @Override
    public boolean isReadable() {
        if (this.writerIndex <= this.readerIndex) return false;
        return true;
    }

    @Override
    public boolean isReadable(int numBytes) {
        if (this.writerIndex - this.readerIndex < numBytes) return false;
        return true;
    }

    @Override
    public boolean isWritable() {
        if (this.capacity() <= this.writerIndex) return false;
        return true;
    }

    @Override
    public boolean isWritable(int numBytes) {
        if (this.capacity() - this.writerIndex < numBytes) return false;
        return true;
    }

    @Override
    public int readableBytes() {
        return this.writerIndex - this.readerIndex;
    }

    @Override
    public int writableBytes() {
        return this.capacity() - this.writerIndex;
    }

    @Override
    public int maxWritableBytes() {
        return this.maxCapacity() - this.writerIndex;
    }

    @Override
    public ByteBuf markReaderIndex() {
        this.markedReaderIndex = this.readerIndex;
        return this;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        this.readerIndex((int)this.markedReaderIndex);
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        this.markedWriterIndex = this.writerIndex;
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        this.writerIndex((int)this.markedWriterIndex);
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        this.ensureAccessible();
        if (this.readerIndex == 0) {
            return this;
        }
        if (this.readerIndex != this.writerIndex) {
            this.setBytes((int)0, (ByteBuf)this, (int)this.readerIndex, (int)(this.writerIndex - this.readerIndex));
            this.writerIndex -= this.readerIndex;
            this.adjustMarkers((int)this.readerIndex);
            this.readerIndex = 0;
            return this;
        }
        this.adjustMarkers((int)this.readerIndex);
        this.readerIndex = 0;
        this.writerIndex = 0;
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        this.ensureAccessible();
        if (this.readerIndex == 0) {
            return this;
        }
        if (this.readerIndex == this.writerIndex) {
            this.adjustMarkers((int)this.readerIndex);
            this.readerIndex = 0;
            this.writerIndex = 0;
            return this;
        }
        if (this.readerIndex < this.capacity() >>> 1) return this;
        this.setBytes((int)0, (ByteBuf)this, (int)this.readerIndex, (int)(this.writerIndex - this.readerIndex));
        this.writerIndex -= this.readerIndex;
        this.adjustMarkers((int)this.readerIndex);
        this.readerIndex = 0;
        return this;
    }

    protected final void adjustMarkers(int decrement) {
        int markedReaderIndex = this.markedReaderIndex;
        if (markedReaderIndex > decrement) {
            this.markedReaderIndex = markedReaderIndex - decrement;
            this.markedWriterIndex -= decrement;
            return;
        }
        this.markedReaderIndex = 0;
        int markedWriterIndex = this.markedWriterIndex;
        if (markedWriterIndex <= decrement) {
            this.markedWriterIndex = 0;
            return;
        }
        this.markedWriterIndex = markedWriterIndex - decrement;
    }

    protected final void trimIndicesToCapacity(int newCapacity) {
        if (this.writerIndex() <= newCapacity) return;
        this.setIndex0((int)Math.min((int)this.readerIndex(), (int)newCapacity), (int)newCapacity);
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        ObjectUtil.checkPositiveOrZero((int)minWritableBytes, (String)"minWritableBytes");
        this.ensureWritable0((int)minWritableBytes);
        return this;
    }

    final void ensureWritable0(int minWritableBytes) {
        int fastCapacity;
        this.ensureAccessible();
        if (minWritableBytes <= this.writableBytes()) {
            return;
        }
        int writerIndex = this.writerIndex();
        if (checkBounds && minWritableBytes > this.maxCapacity - writerIndex) {
            throw new IndexOutOfBoundsException((String)String.format((String)"writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s", (Object[])new Object[]{Integer.valueOf((int)writerIndex), Integer.valueOf((int)minWritableBytes), Integer.valueOf((int)this.maxCapacity), this}));
        }
        int minNewCapacity = writerIndex + minWritableBytes;
        int newCapacity = this.alloc().calculateNewCapacity((int)minNewCapacity, (int)this.maxCapacity);
        if (newCapacity > (fastCapacity = writerIndex + this.maxFastWritableBytes()) && minNewCapacity <= fastCapacity) {
            newCapacity = fastCapacity;
        }
        this.capacity((int)newCapacity);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        int fastCapacity;
        int writerIndex;
        this.ensureAccessible();
        ObjectUtil.checkPositiveOrZero((int)minWritableBytes, (String)"minWritableBytes");
        if (minWritableBytes <= this.writableBytes()) {
            return 0;
        }
        int maxCapacity = this.maxCapacity();
        if (minWritableBytes > maxCapacity - (writerIndex = this.writerIndex())) {
            if (!force) return 1;
            if (this.capacity() == maxCapacity) {
                return 1;
            }
            this.capacity((int)maxCapacity);
            return 3;
        }
        int minNewCapacity = writerIndex + minWritableBytes;
        int newCapacity = this.alloc().calculateNewCapacity((int)minNewCapacity, (int)maxCapacity);
        if (newCapacity > (fastCapacity = writerIndex + this.maxFastWritableBytes()) && minNewCapacity <= fastCapacity) {
            newCapacity = fastCapacity;
        }
        this.capacity((int)newCapacity);
        return 2;
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (endianness == this.order()) {
            return this;
        }
        if (endianness != null) return this.newSwappedByteBuf();
        throw new NullPointerException((String)"endianness");
    }

    protected SwappedByteBuf newSwappedByteBuf() {
        return new SwappedByteBuf((ByteBuf)this);
    }

    @Override
    public byte getByte(int index) {
        this.checkIndex((int)index);
        return this._getByte((int)index);
    }

    protected abstract byte _getByte(int var1);

    @Override
    public boolean getBoolean(int index) {
        if (this.getByte((int)index) == 0) return false;
        return true;
    }

    @Override
    public short getUnsignedByte(int index) {
        return (short)(this.getByte((int)index) & 255);
    }

    @Override
    public short getShort(int index) {
        this.checkIndex((int)index, (int)2);
        return this._getShort((int)index);
    }

    protected abstract short _getShort(int var1);

    @Override
    public short getShortLE(int index) {
        this.checkIndex((int)index, (int)2);
        return this._getShortLE((int)index);
    }

    protected abstract short _getShortLE(int var1);

    @Override
    public int getUnsignedShort(int index) {
        return this.getShort((int)index) & 65535;
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.getShortLE((int)index) & 65535;
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.checkIndex((int)index, (int)3);
        return this._getUnsignedMedium((int)index);
    }

    protected abstract int _getUnsignedMedium(int var1);

    @Override
    public int getUnsignedMediumLE(int index) {
        this.checkIndex((int)index, (int)3);
        return this._getUnsignedMediumLE((int)index);
    }

    protected abstract int _getUnsignedMediumLE(int var1);

    @Override
    public int getMedium(int index) {
        int value = this.getUnsignedMedium((int)index);
        if ((value & 8388608) == 0) return value;
        value |= -16777216;
        return value;
    }

    @Override
    public int getMediumLE(int index) {
        int value = this.getUnsignedMediumLE((int)index);
        if ((value & 8388608) == 0) return value;
        value |= -16777216;
        return value;
    }

    @Override
    public int getInt(int index) {
        this.checkIndex((int)index, (int)4);
        return this._getInt((int)index);
    }

    protected abstract int _getInt(int var1);

    @Override
    public int getIntLE(int index) {
        this.checkIndex((int)index, (int)4);
        return this._getIntLE((int)index);
    }

    protected abstract int _getIntLE(int var1);

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
        this.checkIndex((int)index, (int)8);
        return this._getLong((int)index);
    }

    protected abstract long _getLong(int var1);

    @Override
    public long getLongLE(int index) {
        this.checkIndex((int)index, (int)8);
        return this._getLongLE((int)index);
    }

    protected abstract long _getLongLE(int var1);

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
    public ByteBuf getBytes(int index, byte[] dst) {
        this.getBytes((int)index, (byte[])dst, (int)0, (int)dst.length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        this.getBytes((int)index, (ByteBuf)dst, (int)dst.writableBytes());
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.getBytes((int)index, (ByteBuf)dst, (int)dst.writerIndex(), (int)length);
        dst.writerIndex((int)(dst.writerIndex() + length));
        return this;
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        if (CharsetUtil.US_ASCII.equals((Object)charset)) return new AsciiString((byte[])ByteBufUtil.getBytes((ByteBuf)this, (int)index, (int)length, (boolean)true), (boolean)false);
        if (!CharsetUtil.ISO_8859_1.equals((Object)charset)) return this.toString((int)index, (int)length, (Charset)charset);
        return new AsciiString((byte[])ByteBufUtil.getBytes((ByteBuf)this, (int)index, (int)length, (boolean)true), (boolean)false);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        CharSequence sequence = this.getCharSequence((int)this.readerIndex, (int)length, (Charset)charset);
        this.readerIndex += length;
        return sequence;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.checkIndex((int)index);
        this._setByte((int)index, (int)value);
        return this;
    }

    protected abstract void _setByte(int var1, int var2);

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        this.setByte((int)index, (int)(value ? 1 : 0));
        return this;
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.checkIndex((int)index, (int)2);
        this._setShort((int)index, (int)value);
        return this;
    }

    protected abstract void _setShort(int var1, int var2);

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.checkIndex((int)index, (int)2);
        this._setShortLE((int)index, (int)value);
        return this;
    }

    protected abstract void _setShortLE(int var1, int var2);

    @Override
    public ByteBuf setChar(int index, int value) {
        this.setShort((int)index, (int)value);
        return this;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.checkIndex((int)index, (int)3);
        this._setMedium((int)index, (int)value);
        return this;
    }

    protected abstract void _setMedium(int var1, int var2);

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.checkIndex((int)index, (int)3);
        this._setMediumLE((int)index, (int)value);
        return this;
    }

    protected abstract void _setMediumLE(int var1, int var2);

    @Override
    public ByteBuf setInt(int index, int value) {
        this.checkIndex((int)index, (int)4);
        this._setInt((int)index, (int)value);
        return this;
    }

    protected abstract void _setInt(int var1, int var2);

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.checkIndex((int)index, (int)4);
        this._setIntLE((int)index, (int)value);
        return this;
    }

    protected abstract void _setIntLE(int var1, int var2);

    @Override
    public ByteBuf setFloat(int index, float value) {
        this.setInt((int)index, (int)Float.floatToRawIntBits((float)value));
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.checkIndex((int)index, (int)8);
        this._setLong((int)index, (long)value);
        return this;
    }

    protected abstract void _setLong(int var1, long var2);

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.checkIndex((int)index, (int)8);
        this._setLongLE((int)index, (long)value);
        return this;
    }

    protected abstract void _setLongLE(int var1, long var2);

    @Override
    public ByteBuf setDouble(int index, double value) {
        this.setLong((int)index, (long)Double.doubleToRawLongBits((double)value));
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        this.setBytes((int)index, (byte[])src, (int)0, (int)src.length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        this.setBytes((int)index, (ByteBuf)src, (int)src.readableBytes());
        return this;
    }

    private static void checkReadableBounds(ByteBuf src, int length) {
        if (length <= src.readableBytes()) return;
        throw new IndexOutOfBoundsException((String)String.format((String)"length(%d) exceeds src.readableBytes(%d) where src is: %s", (Object[])new Object[]{Integer.valueOf((int)length), Integer.valueOf((int)src.readableBytes()), src}));
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        this.checkIndex((int)index, (int)length);
        if (src == null) {
            throw new NullPointerException((String)"src");
        }
        if (checkBounds) {
            AbstractByteBuf.checkReadableBounds((ByteBuf)src, (int)length);
        }
        this.setBytes((int)index, (ByteBuf)src, (int)src.readerIndex(), (int)length);
        src.readerIndex((int)(src.readerIndex() + length));
        return this;
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        int i;
        if (length == 0) {
            return this;
        }
        this.checkIndex((int)index, (int)length);
        int nLong = length >>> 3;
        int nBytes = length & 7;
        for (i = nLong; i > 0; index += 8, --i) {
            this._setLong((int)index, (long)0L);
        }
        if (nBytes == 4) {
            this._setInt((int)index, (int)0);
            return this;
        }
        if (nBytes < 4) {
            i = nBytes;
            while (i > 0) {
                this._setByte((int)index, (int)0);
                ++index;
                --i;
            }
            return this;
        }
        this._setInt((int)index, (int)0);
        index += 4;
        i = nBytes - 4;
        while (i > 0) {
            this._setByte((int)index, (int)0);
            ++index;
            --i;
        }
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.setCharSequence0((int)index, (CharSequence)sequence, (Charset)charset, (boolean)false);
    }

    private int setCharSequence0(int index, CharSequence sequence, Charset charset, boolean expand) {
        if (charset.equals((Object)CharsetUtil.UTF_8)) {
            int length = ByteBufUtil.utf8MaxBytes((CharSequence)sequence);
            if (expand) {
                this.ensureWritable0((int)length);
                this.checkIndex0((int)index, (int)length);
                return ByteBufUtil.writeUtf8((AbstractByteBuf)this, (int)index, (CharSequence)sequence, (int)sequence.length());
            }
            this.checkIndex((int)index, (int)length);
            return ByteBufUtil.writeUtf8((AbstractByteBuf)this, (int)index, (CharSequence)sequence, (int)sequence.length());
        }
        if (charset.equals((Object)CharsetUtil.US_ASCII) || charset.equals((Object)CharsetUtil.ISO_8859_1)) {
            int length = sequence.length();
            if (expand) {
                this.ensureWritable0((int)length);
                this.checkIndex0((int)index, (int)length);
                return ByteBufUtil.writeAscii((AbstractByteBuf)this, (int)index, (CharSequence)sequence, (int)length);
            }
            this.checkIndex((int)index, (int)length);
            return ByteBufUtil.writeAscii((AbstractByteBuf)this, (int)index, (CharSequence)sequence, (int)length);
        }
        byte[] bytes = sequence.toString().getBytes((Charset)charset);
        if (expand) {
            this.ensureWritable0((int)bytes.length);
        }
        this.setBytes((int)index, (byte[])bytes);
        return bytes.length;
    }

    @Override
    public byte readByte() {
        this.checkReadableBytes0((int)1);
        int i = this.readerIndex;
        byte b = this._getByte((int)i);
        this.readerIndex = i + 1;
        return b;
    }

    @Override
    public boolean readBoolean() {
        if (this.readByte() == 0) return false;
        return true;
    }

    @Override
    public short readUnsignedByte() {
        return (short)(this.readByte() & 255);
    }

    @Override
    public short readShort() {
        this.checkReadableBytes0((int)2);
        short v = this._getShort((int)this.readerIndex);
        this.readerIndex += 2;
        return v;
    }

    @Override
    public short readShortLE() {
        this.checkReadableBytes0((int)2);
        short v = this._getShortLE((int)this.readerIndex);
        this.readerIndex += 2;
        return v;
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
        int value = this.readUnsignedMedium();
        if ((value & 8388608) == 0) return value;
        value |= -16777216;
        return value;
    }

    @Override
    public int readMediumLE() {
        int value = this.readUnsignedMediumLE();
        if ((value & 8388608) == 0) return value;
        value |= -16777216;
        return value;
    }

    @Override
    public int readUnsignedMedium() {
        this.checkReadableBytes0((int)3);
        int v = this._getUnsignedMedium((int)this.readerIndex);
        this.readerIndex += 3;
        return v;
    }

    @Override
    public int readUnsignedMediumLE() {
        this.checkReadableBytes0((int)3);
        int v = this._getUnsignedMediumLE((int)this.readerIndex);
        this.readerIndex += 3;
        return v;
    }

    @Override
    public int readInt() {
        this.checkReadableBytes0((int)4);
        int v = this._getInt((int)this.readerIndex);
        this.readerIndex += 4;
        return v;
    }

    @Override
    public int readIntLE() {
        this.checkReadableBytes0((int)4);
        int v = this._getIntLE((int)this.readerIndex);
        this.readerIndex += 4;
        return v;
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
        this.checkReadableBytes0((int)8);
        long v = this._getLong((int)this.readerIndex);
        this.readerIndex += 8;
        return v;
    }

    @Override
    public long readLongLE() {
        this.checkReadableBytes0((int)8);
        long v = this._getLongLE((int)this.readerIndex);
        this.readerIndex += 8;
        return v;
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
        this.checkReadableBytes((int)length);
        if (length == 0) {
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBuf buf = this.alloc().buffer((int)length, (int)this.maxCapacity);
        buf.writeBytes((ByteBuf)this, (int)this.readerIndex, (int)length);
        this.readerIndex += length;
        return buf;
    }

    @Override
    public ByteBuf readSlice(int length) {
        this.checkReadableBytes((int)length);
        ByteBuf slice = this.slice((int)this.readerIndex, (int)length);
        this.readerIndex += length;
        return slice;
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        this.checkReadableBytes((int)length);
        ByteBuf slice = this.retainedSlice((int)this.readerIndex, (int)length);
        this.readerIndex += length;
        return slice;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.checkReadableBytes((int)length);
        this.getBytes((int)this.readerIndex, (byte[])dst, (int)dstIndex, (int)length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        this.readBytes((byte[])dst, (int)0, (int)dst.length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        this.readBytes((ByteBuf)dst, (int)dst.writableBytes());
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        if (checkBounds && length > dst.writableBytes()) {
            throw new IndexOutOfBoundsException((String)String.format((String)"length(%d) exceeds dst.writableBytes(%d) where dst is: %s", (Object[])new Object[]{Integer.valueOf((int)length), Integer.valueOf((int)dst.writableBytes()), dst}));
        }
        this.readBytes((ByteBuf)dst, (int)dst.writerIndex(), (int)length);
        dst.writerIndex((int)(dst.writerIndex() + length));
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        this.checkReadableBytes((int)length);
        this.getBytes((int)this.readerIndex, (ByteBuf)dst, (int)dstIndex, (int)length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        int length = dst.remaining();
        this.checkReadableBytes((int)length);
        this.getBytes((int)this.readerIndex, (ByteBuffer)dst);
        this.readerIndex += length;
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        this.checkReadableBytes((int)length);
        int readBytes = this.getBytes((int)this.readerIndex, (GatheringByteChannel)out, (int)length);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        this.checkReadableBytes((int)length);
        int readBytes = this.getBytes((int)this.readerIndex, (FileChannel)out, (long)position, (int)length);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.checkReadableBytes((int)length);
        this.getBytes((int)this.readerIndex, (OutputStream)out, (int)length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf skipBytes(int length) {
        this.checkReadableBytes((int)length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        this.writeByte((int)(value ? 1 : 0));
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        this.ensureWritable0((int)1);
        this._setByte((int)this.writerIndex++, (int)value);
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        this.ensureWritable0((int)2);
        this._setShort((int)this.writerIndex, (int)value);
        this.writerIndex += 2;
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        this.ensureWritable0((int)2);
        this._setShortLE((int)this.writerIndex, (int)value);
        this.writerIndex += 2;
        return this;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        this.ensureWritable0((int)3);
        this._setMedium((int)this.writerIndex, (int)value);
        this.writerIndex += 3;
        return this;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        this.ensureWritable0((int)3);
        this._setMediumLE((int)this.writerIndex, (int)value);
        this.writerIndex += 3;
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        this.ensureWritable0((int)4);
        this._setInt((int)this.writerIndex, (int)value);
        this.writerIndex += 4;
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        this.ensureWritable0((int)4);
        this._setIntLE((int)this.writerIndex, (int)value);
        this.writerIndex += 4;
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        this.ensureWritable0((int)8);
        this._setLong((int)this.writerIndex, (long)value);
        this.writerIndex += 8;
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        this.ensureWritable0((int)8);
        this._setLongLE((int)this.writerIndex, (long)value);
        this.writerIndex += 8;
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
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        this.ensureWritable((int)length);
        this.setBytes((int)this.writerIndex, (byte[])src, (int)srcIndex, (int)length);
        this.writerIndex += length;
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        this.writeBytes((byte[])src, (int)0, (int)src.length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        this.writeBytes((ByteBuf)src, (int)src.readableBytes());
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        if (checkBounds) {
            AbstractByteBuf.checkReadableBounds((ByteBuf)src, (int)length);
        }
        this.writeBytes((ByteBuf)src, (int)src.readerIndex(), (int)length);
        src.readerIndex((int)(src.readerIndex() + length));
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        this.ensureWritable((int)length);
        this.setBytes((int)this.writerIndex, (ByteBuf)src, (int)srcIndex, (int)length);
        this.writerIndex += length;
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        int length = src.remaining();
        this.ensureWritable0((int)length);
        this.setBytes((int)this.writerIndex, (ByteBuffer)src);
        this.writerIndex += length;
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        this.ensureWritable((int)length);
        int writtenBytes = this.setBytes((int)this.writerIndex, (InputStream)in, (int)length);
        if (writtenBytes <= 0) return writtenBytes;
        this.writerIndex += writtenBytes;
        return writtenBytes;
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        this.ensureWritable((int)length);
        int writtenBytes = this.setBytes((int)this.writerIndex, (ScatteringByteChannel)in, (int)length);
        if (writtenBytes <= 0) return writtenBytes;
        this.writerIndex += writtenBytes;
        return writtenBytes;
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        this.ensureWritable((int)length);
        int writtenBytes = this.setBytes((int)this.writerIndex, (FileChannel)in, (long)position, (int)length);
        if (writtenBytes <= 0) return writtenBytes;
        this.writerIndex += writtenBytes;
        return writtenBytes;
    }

    @Override
    public ByteBuf writeZero(int length) {
        int i;
        if (length == 0) {
            return this;
        }
        this.ensureWritable((int)length);
        int wIndex = this.writerIndex;
        this.checkIndex0((int)wIndex, (int)length);
        int nLong = length >>> 3;
        int nBytes = length & 7;
        for (i = nLong; i > 0; wIndex += 8, --i) {
            this._setLong((int)wIndex, (long)0L);
        }
        if (nBytes == 4) {
            this._setInt((int)wIndex, (int)0);
            wIndex += 4;
        } else if (nBytes < 4) {
            for (i = nBytes; i > 0; ++wIndex, --i) {
                this._setByte((int)wIndex, (int)0);
            }
        } else {
            this._setInt((int)wIndex, (int)0);
            wIndex += 4;
            for (i = nBytes - 4; i > 0; ++wIndex, --i) {
                this._setByte((int)wIndex, (int)0);
            }
        }
        this.writerIndex = wIndex;
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        int written = this.setCharSequence0((int)this.writerIndex, (CharSequence)sequence, (Charset)charset, (boolean)true);
        this.writerIndex += written;
        return written;
    }

    @Override
    public ByteBuf copy() {
        return this.copy((int)this.readerIndex, (int)this.readableBytes());
    }

    @Override
    public ByteBuf duplicate() {
        this.ensureAccessible();
        return new UnpooledDuplicatedByteBuf((AbstractByteBuf)this);
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.duplicate().retain();
    }

    @Override
    public ByteBuf slice() {
        return this.slice((int)this.readerIndex, (int)this.readableBytes());
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.slice().retain();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        this.ensureAccessible();
        return new UnpooledSlicedByteBuf((AbstractByteBuf)this, (int)index, (int)length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.slice((int)index, (int)length).retain();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.nioBuffer((int)this.readerIndex, (int)this.readableBytes());
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers((int)this.readerIndex, (int)this.readableBytes());
    }

    @Override
    public String toString(Charset charset) {
        return this.toString((int)this.readerIndex, (int)this.readableBytes(), (Charset)charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return ByteBufUtil.decodeString((ByteBuf)this, (int)index, (int)length, (Charset)charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        if (fromIndex > toIndex) return this.lastIndexOf((int)fromIndex, (int)toIndex, (byte)value);
        return this.firstIndexOf((int)fromIndex, (int)toIndex, (byte)value);
    }

    private int firstIndexOf(int fromIndex, int toIndex, byte value) {
        if ((fromIndex = Math.max((int)fromIndex, (int)0)) >= toIndex) return -1;
        if (this.capacity() == 0) {
            return -1;
        }
        this.checkIndex((int)fromIndex, (int)(toIndex - fromIndex));
        int i = fromIndex;
        while (i < toIndex) {
            if (this._getByte((int)i) == value) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    private int lastIndexOf(int fromIndex, int toIndex, byte value) {
        if ((fromIndex = Math.min((int)fromIndex, (int)this.capacity())) < 0) return -1;
        if (this.capacity() == 0) {
            return -1;
        }
        this.checkIndex((int)toIndex, (int)(fromIndex - toIndex));
        int i = fromIndex - 1;
        while (i >= toIndex) {
            if (this._getByte((int)i) == value) {
                return i;
            }
            --i;
        }
        return -1;
    }

    @Override
    public int bytesBefore(byte value) {
        return this.bytesBefore((int)this.readerIndex(), (int)this.readableBytes(), (byte)value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        this.checkReadableBytes((int)length);
        return this.bytesBefore((int)this.readerIndex(), (int)length, (byte)value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        int endIndex = this.indexOf((int)index, (int)(index + length), (byte)value);
        if (endIndex >= 0) return endIndex - index;
        return -1;
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        this.ensureAccessible();
        try {
            return this.forEachByteAsc0((int)this.readerIndex, (int)this.writerIndex, (ByteProcessor)processor);
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
            return -1;
        }
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        this.checkIndex((int)index, (int)length);
        try {
            return this.forEachByteAsc0((int)index, (int)(index + length), (ByteProcessor)processor);
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
            return -1;
        }
    }

    int forEachByteAsc0(int start, int end, ByteProcessor processor) throws Exception {
        while (start < end) {
            if (!processor.process((byte)this._getByte((int)start))) {
                return start;
            }
            ++start;
        }
        return -1;
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        this.ensureAccessible();
        try {
            return this.forEachByteDesc0((int)(this.writerIndex - 1), (int)this.readerIndex, (ByteProcessor)processor);
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
            return -1;
        }
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        this.checkIndex((int)index, (int)length);
        try {
            return this.forEachByteDesc0((int)(index + length - 1), (int)index, (ByteProcessor)processor);
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
            return -1;
        }
    }

    int forEachByteDesc0(int rStart, int rEnd, ByteProcessor processor) throws Exception {
        while (rStart >= rEnd) {
            if (!processor.process((byte)this._getByte((int)rStart))) {
                return rStart;
            }
            --rStart;
        }
        return -1;
    }

    @Override
    public int hashCode() {
        return ByteBufUtil.hashCode((ByteBuf)this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteBuf)) return false;
        if (!ByteBufUtil.equals((ByteBuf)this, (ByteBuf)((ByteBuf)o))) return false;
        return true;
    }

    @Override
    public int compareTo(ByteBuf that) {
        return ByteBufUtil.compare((ByteBuf)this, (ByteBuf)that);
    }

    @Override
    public String toString() {
        ByteBuf unwrapped;
        if (this.refCnt() == 0) {
            return StringUtil.simpleClassName((Object)this) + "(freed)";
        }
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((String)"(ridx: ").append((int)this.readerIndex).append((String)", widx: ").append((int)this.writerIndex).append((String)", cap: ").append((int)this.capacity());
        if (this.maxCapacity != Integer.MAX_VALUE) {
            buf.append((char)'/').append((int)this.maxCapacity);
        }
        if ((unwrapped = this.unwrap()) != null) {
            buf.append((String)", unwrapped: ").append((Object)unwrapped);
        }
        buf.append((char)')');
        return buf.toString();
    }

    protected final void checkIndex(int index) {
        this.checkIndex((int)index, (int)1);
    }

    protected final void checkIndex(int index, int fieldLength) {
        this.ensureAccessible();
        this.checkIndex0((int)index, (int)fieldLength);
    }

    private static void checkRangeBounds(String indexName, int index, int fieldLength, int capacity) {
        if (!MathUtil.isOutOfBounds((int)index, (int)fieldLength, (int)capacity)) return;
        throw new IndexOutOfBoundsException((String)String.format((String)"%s: %d, length: %d (expected: range(0, %d))", (Object[])new Object[]{indexName, Integer.valueOf((int)index), Integer.valueOf((int)fieldLength), Integer.valueOf((int)capacity)}));
    }

    final void checkIndex0(int index, int fieldLength) {
        if (!checkBounds) return;
        AbstractByteBuf.checkRangeBounds((String)"index", (int)index, (int)fieldLength, (int)this.capacity());
    }

    protected final void checkSrcIndex(int index, int length, int srcIndex, int srcCapacity) {
        this.checkIndex((int)index, (int)length);
        if (!checkBounds) return;
        AbstractByteBuf.checkRangeBounds((String)"srcIndex", (int)srcIndex, (int)length, (int)srcCapacity);
    }

    protected final void checkDstIndex(int index, int length, int dstIndex, int dstCapacity) {
        this.checkIndex((int)index, (int)length);
        if (!checkBounds) return;
        AbstractByteBuf.checkRangeBounds((String)"dstIndex", (int)dstIndex, (int)length, (int)dstCapacity);
    }

    protected final void checkDstIndex(int length, int dstIndex, int dstCapacity) {
        this.checkReadableBytes((int)length);
        if (!checkBounds) return;
        AbstractByteBuf.checkRangeBounds((String)"dstIndex", (int)dstIndex, (int)length, (int)dstCapacity);
    }

    protected final void checkReadableBytes(int minimumReadableBytes) {
        ObjectUtil.checkPositiveOrZero((int)minimumReadableBytes, (String)"minimumReadableBytes");
        this.checkReadableBytes0((int)minimumReadableBytes);
    }

    protected final void checkNewCapacity(int newCapacity) {
        this.ensureAccessible();
        if (!checkBounds) return;
        if (newCapacity < 0) throw new IllegalArgumentException((String)("newCapacity: " + newCapacity + " (expected: 0-" + this.maxCapacity() + ')'));
        if (newCapacity <= this.maxCapacity()) return;
        throw new IllegalArgumentException((String)("newCapacity: " + newCapacity + " (expected: 0-" + this.maxCapacity() + ')'));
    }

    private void checkReadableBytes0(int minimumReadableBytes) {
        this.ensureAccessible();
        if (!checkBounds) return;
        if (this.readerIndex <= this.writerIndex - minimumReadableBytes) return;
        throw new IndexOutOfBoundsException((String)String.format((String)"readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s", (Object[])new Object[]{Integer.valueOf((int)this.readerIndex), Integer.valueOf((int)minimumReadableBytes), Integer.valueOf((int)this.writerIndex), this}));
    }

    protected final void ensureAccessible() {
        if (!checkAccessible) return;
        if (this.isAccessible()) return;
        throw new IllegalReferenceCountException((int)0);
    }

    final void setIndex0(int readerIndex, int writerIndex) {
        this.readerIndex = readerIndex;
        this.writerIndex = writerIndex;
    }

    final void discardMarks() {
        this.markedWriterIndex = 0;
        this.markedReaderIndex = 0;
    }

    static {
        if (logger.isDebugEnabled()) {
            logger.debug((String)"-D{}: {}", (Object)"io.netty.buffer.checkAccessible", (Object)Boolean.valueOf((boolean)checkAccessible));
            logger.debug((String)"-D{}: {}", (Object)"io.netty.buffer.checkBounds", (Object)Boolean.valueOf((boolean)checkBounds));
        }
        leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ByteBuf.class);
    }
}

