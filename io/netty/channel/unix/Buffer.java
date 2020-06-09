/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Buffer {
    private Buffer() {
    }

    public static void free(ByteBuffer buffer) {
        PlatformDependent.freeDirectBuffer((ByteBuffer)buffer);
    }

    public static ByteBuffer allocateDirectWithNativeOrder(int capacity) {
        ByteOrder byteOrder;
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            byteOrder = ByteOrder.BIG_ENDIAN;
            return ByteBuffer.allocateDirect((int)capacity).order((ByteOrder)byteOrder);
        }
        byteOrder = ByteOrder.LITTLE_ENDIAN;
        return ByteBuffer.allocateDirect((int)capacity).order((ByteOrder)byteOrder);
    }

    public static long memoryAddress(ByteBuffer buffer) {
        assert (buffer.isDirect());
        if (!PlatformDependent.hasUnsafe()) return Buffer.memoryAddress0((ByteBuffer)buffer);
        return PlatformDependent.directBufferAddress((ByteBuffer)buffer);
    }

    public static int addressSize() {
        if (!PlatformDependent.hasUnsafe()) return Buffer.addressSize0();
        return PlatformDependent.addressSize();
    }

    private static native int addressSize0();

    private static native long memoryAddress0(ByteBuffer var0);
}

