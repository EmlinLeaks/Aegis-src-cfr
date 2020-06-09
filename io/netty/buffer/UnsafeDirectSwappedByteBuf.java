/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractUnsafeSwappedByteBuf;
import io.netty.util.internal.PlatformDependent;

final class UnsafeDirectSwappedByteBuf
extends AbstractUnsafeSwappedByteBuf {
    UnsafeDirectSwappedByteBuf(AbstractByteBuf buf) {
        super((AbstractByteBuf)buf);
    }

    private static long addr(AbstractByteBuf wrapped, int index) {
        return wrapped.memoryAddress() + (long)index;
    }

    @Override
    protected long _getLong(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getLong((long)UnsafeDirectSwappedByteBuf.addr((AbstractByteBuf)wrapped, (int)index));
    }

    @Override
    protected int _getInt(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getInt((long)UnsafeDirectSwappedByteBuf.addr((AbstractByteBuf)wrapped, (int)index));
    }

    @Override
    protected short _getShort(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getShort((long)UnsafeDirectSwappedByteBuf.addr((AbstractByteBuf)wrapped, (int)index));
    }

    @Override
    protected void _setShort(AbstractByteBuf wrapped, int index, short value) {
        PlatformDependent.putShort((long)UnsafeDirectSwappedByteBuf.addr((AbstractByteBuf)wrapped, (int)index), (short)value);
    }

    @Override
    protected void _setInt(AbstractByteBuf wrapped, int index, int value) {
        PlatformDependent.putInt((long)UnsafeDirectSwappedByteBuf.addr((AbstractByteBuf)wrapped, (int)index), (int)value);
    }

    @Override
    protected void _setLong(AbstractByteBuf wrapped, int index, long value) {
        PlatformDependent.putLong((long)UnsafeDirectSwappedByteBuf.addr((AbstractByteBuf)wrapped, (int)index), (long)value);
    }
}

