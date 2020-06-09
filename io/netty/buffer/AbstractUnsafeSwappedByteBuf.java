/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteOrder;

abstract class AbstractUnsafeSwappedByteBuf
extends SwappedByteBuf {
    private final boolean nativeByteOrder;
    private final AbstractByteBuf wrapped;

    AbstractUnsafeSwappedByteBuf(AbstractByteBuf buf) {
        super((ByteBuf)buf);
        assert (PlatformDependent.isUnaligned());
        this.wrapped = buf;
        this.nativeByteOrder = PlatformDependent.BIG_ENDIAN_NATIVE_ORDER == (this.order() == ByteOrder.BIG_ENDIAN);
    }

    @Override
    public final long getLong(int index) {
        long l;
        this.wrapped.checkIndex((int)index, (int)8);
        long v = this._getLong((AbstractByteBuf)this.wrapped, (int)index);
        if (this.nativeByteOrder) {
            l = v;
            return l;
        }
        l = Long.reverseBytes((long)v);
        return l;
    }

    @Override
    public final float getFloat(int index) {
        return Float.intBitsToFloat((int)this.getInt((int)index));
    }

    @Override
    public final double getDouble(int index) {
        return Double.longBitsToDouble((long)this.getLong((int)index));
    }

    @Override
    public final char getChar(int index) {
        return (char)this.getShort((int)index);
    }

    @Override
    public final long getUnsignedInt(int index) {
        return (long)this.getInt((int)index) & 0xFFFFFFFFL;
    }

    @Override
    public final int getInt(int index) {
        int n;
        this.wrapped.checkIndex((int)index, (int)4);
        int v = this._getInt((AbstractByteBuf)this.wrapped, (int)index);
        if (this.nativeByteOrder) {
            n = v;
            return n;
        }
        n = Integer.reverseBytes((int)v);
        return n;
    }

    @Override
    public final int getUnsignedShort(int index) {
        return this.getShort((int)index) & 65535;
    }

    @Override
    public final short getShort(int index) {
        short s;
        this.wrapped.checkIndex((int)index, (int)2);
        short v = this._getShort((AbstractByteBuf)this.wrapped, (int)index);
        if (this.nativeByteOrder) {
            s = v;
            return s;
        }
        s = Short.reverseBytes((short)v);
        return s;
    }

    @Override
    public final ByteBuf setShort(int index, int value) {
        this.wrapped.checkIndex((int)index, (int)2);
        this._setShort((AbstractByteBuf)this.wrapped, (int)index, (short)(this.nativeByteOrder ? (short)value : Short.reverseBytes((short)((short)value))));
        return this;
    }

    @Override
    public final ByteBuf setInt(int index, int value) {
        this.wrapped.checkIndex((int)index, (int)4);
        this._setInt((AbstractByteBuf)this.wrapped, (int)index, (int)(this.nativeByteOrder ? value : Integer.reverseBytes((int)value)));
        return this;
    }

    @Override
    public final ByteBuf setLong(int index, long value) {
        this.wrapped.checkIndex((int)index, (int)8);
        this._setLong((AbstractByteBuf)this.wrapped, (int)index, (long)(this.nativeByteOrder ? value : Long.reverseBytes((long)value)));
        return this;
    }

    @Override
    public final ByteBuf setChar(int index, int value) {
        this.setShort((int)index, (int)value);
        return this;
    }

    @Override
    public final ByteBuf setFloat(int index, float value) {
        this.setInt((int)index, (int)Float.floatToRawIntBits((float)value));
        return this;
    }

    @Override
    public final ByteBuf setDouble(int index, double value) {
        this.setLong((int)index, (long)Double.doubleToRawLongBits((double)value));
        return this;
    }

    @Override
    public final ByteBuf writeShort(int value) {
        this.wrapped.ensureWritable0((int)2);
        this._setShort((AbstractByteBuf)this.wrapped, (int)this.wrapped.writerIndex, (short)(this.nativeByteOrder ? (short)value : Short.reverseBytes((short)((short)value))));
        this.wrapped.writerIndex += 2;
        return this;
    }

    @Override
    public final ByteBuf writeInt(int value) {
        this.wrapped.ensureWritable0((int)4);
        this._setInt((AbstractByteBuf)this.wrapped, (int)this.wrapped.writerIndex, (int)(this.nativeByteOrder ? value : Integer.reverseBytes((int)value)));
        this.wrapped.writerIndex += 4;
        return this;
    }

    @Override
    public final ByteBuf writeLong(long value) {
        this.wrapped.ensureWritable0((int)8);
        this._setLong((AbstractByteBuf)this.wrapped, (int)this.wrapped.writerIndex, (long)(this.nativeByteOrder ? value : Long.reverseBytes((long)value)));
        this.wrapped.writerIndex += 8;
        return this;
    }

    @Override
    public final ByteBuf writeChar(int value) {
        this.writeShort((int)value);
        return this;
    }

    @Override
    public final ByteBuf writeFloat(float value) {
        this.writeInt((int)Float.floatToRawIntBits((float)value));
        return this;
    }

    @Override
    public final ByteBuf writeDouble(double value) {
        this.writeLong((long)Double.doubleToRawLongBits((double)value));
        return this;
    }

    protected abstract short _getShort(AbstractByteBuf var1, int var2);

    protected abstract int _getInt(AbstractByteBuf var1, int var2);

    protected abstract long _getLong(AbstractByteBuf var1, int var2);

    protected abstract void _setShort(AbstractByteBuf var1, int var2, short var3);

    protected abstract void _setInt(AbstractByteBuf var1, int var2, int var3);

    protected abstract void _setLong(AbstractByteBuf var1, int var2, long var3);
}

