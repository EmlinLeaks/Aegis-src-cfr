/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.DuplicatedByteBuf;

class UnpooledDuplicatedByteBuf
extends DuplicatedByteBuf {
    UnpooledDuplicatedByteBuf(AbstractByteBuf buffer) {
        super((ByteBuf)buffer);
    }

    @Override
    public AbstractByteBuf unwrap() {
        return (AbstractByteBuf)super.unwrap();
    }

    @Override
    protected byte _getByte(int index) {
        return this.unwrap()._getByte((int)index);
    }

    @Override
    protected short _getShort(int index) {
        return this.unwrap()._getShort((int)index);
    }

    @Override
    protected short _getShortLE(int index) {
        return this.unwrap()._getShortLE((int)index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return this.unwrap()._getUnsignedMedium((int)index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return this.unwrap()._getUnsignedMediumLE((int)index);
    }

    @Override
    protected int _getInt(int index) {
        return this.unwrap()._getInt((int)index);
    }

    @Override
    protected int _getIntLE(int index) {
        return this.unwrap()._getIntLE((int)index);
    }

    @Override
    protected long _getLong(int index) {
        return this.unwrap()._getLong((int)index);
    }

    @Override
    protected long _getLongLE(int index) {
        return this.unwrap()._getLongLE((int)index);
    }

    @Override
    protected void _setByte(int index, int value) {
        this.unwrap()._setByte((int)index, (int)value);
    }

    @Override
    protected void _setShort(int index, int value) {
        this.unwrap()._setShort((int)index, (int)value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        this.unwrap()._setShortLE((int)index, (int)value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        this.unwrap()._setMedium((int)index, (int)value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        this.unwrap()._setMediumLE((int)index, (int)value);
    }

    @Override
    protected void _setInt(int index, int value) {
        this.unwrap()._setInt((int)index, (int)value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        this.unwrap()._setIntLE((int)index, (int)value);
    }

    @Override
    protected void _setLong(int index, long value) {
        this.unwrap()._setLong((int)index, (long)value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        this.unwrap()._setLongLE((int)index, (long)value);
    }
}

