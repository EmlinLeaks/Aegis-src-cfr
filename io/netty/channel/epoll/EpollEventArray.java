/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.epoll.Native;
import io.netty.channel.unix.Buffer;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class EpollEventArray {
    private static final int EPOLL_EVENT_SIZE = Native.sizeofEpollEvent();
    private static final int EPOLL_DATA_OFFSET = Native.offsetofEpollData();
    private ByteBuffer memory;
    private long memoryAddress;
    private int length;

    EpollEventArray(int length) {
        if (length < 1) {
            throw new IllegalArgumentException((String)("length must be >= 1 but was " + length));
        }
        this.length = length;
        this.memory = Buffer.allocateDirectWithNativeOrder((int)EpollEventArray.calculateBufferCapacity((int)length));
        this.memoryAddress = Buffer.memoryAddress((ByteBuffer)this.memory);
    }

    long memoryAddress() {
        return this.memoryAddress;
    }

    int length() {
        return this.length;
    }

    void increase() {
        this.length <<= 1;
        ByteBuffer buffer = Buffer.allocateDirectWithNativeOrder((int)EpollEventArray.calculateBufferCapacity((int)this.length));
        Buffer.free((ByteBuffer)this.memory);
        this.memory = buffer;
        this.memoryAddress = Buffer.memoryAddress((ByteBuffer)buffer);
    }

    void free() {
        Buffer.free((ByteBuffer)this.memory);
        this.memoryAddress = 0L;
    }

    int events(int index) {
        return this.getInt((int)index, (int)0);
    }

    int fd(int index) {
        return this.getInt((int)index, (int)EPOLL_DATA_OFFSET);
    }

    private int getInt(int index, int offset) {
        if (!PlatformDependent.hasUnsafe()) return this.memory.getInt((int)(index * EPOLL_EVENT_SIZE + offset));
        return PlatformDependent.getInt((long)(this.memoryAddress + (long)(index * EPOLL_EVENT_SIZE) + (long)offset));
    }

    private static int calculateBufferCapacity(int capacity) {
        return capacity * EPOLL_EVENT_SIZE;
    }
}

