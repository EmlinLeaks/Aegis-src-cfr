/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractUnsafeSwappedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.PlatformDependent;

final class UnsafeHeapSwappedByteBuf
extends AbstractUnsafeSwappedByteBuf {
    UnsafeHeapSwappedByteBuf(AbstractByteBuf buf) {
        super((AbstractByteBuf)buf);
    }

    private static int idx(ByteBuf wrapped, int index) {
        return wrapped.arrayOffset() + index;
    }

    @Override
    protected long _getLong(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getLong((byte[])wrapped.array(), (int)UnsafeHeapSwappedByteBuf.idx((ByteBuf)wrapped, (int)index));
    }

    @Override
    protected int _getInt(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getInt((byte[])wrapped.array(), (int)UnsafeHeapSwappedByteBuf.idx((ByteBuf)wrapped, (int)index));
    }

    @Override
    protected short _getShort(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getShort((byte[])wrapped.array(), (int)UnsafeHeapSwappedByteBuf.idx((ByteBuf)wrapped, (int)index));
    }

    @Override
    protected void _setShort(AbstractByteBuf wrapped, int index, short value) {
        PlatformDependent.putShort((byte[])wrapped.array(), (int)UnsafeHeapSwappedByteBuf.idx((ByteBuf)wrapped, (int)index), (short)value);
    }

    @Override
    protected void _setInt(AbstractByteBuf wrapped, int index, int value) {
        PlatformDependent.putInt((byte[])wrapped.array(), (int)UnsafeHeapSwappedByteBuf.idx((ByteBuf)wrapped, (int)index), (int)value);
    }

    @Override
    protected void _setLong(AbstractByteBuf wrapped, int index, long value) {
        PlatformDependent.putLong((byte[])wrapped.array(), (int)UnsafeHeapSwappedByteBuf.idx((ByteBuf)wrapped, (int)index), (long)value);
    }
}

