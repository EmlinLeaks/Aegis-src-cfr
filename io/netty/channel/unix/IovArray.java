/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.unix.Buffer;
import io.netty.channel.unix.Limits;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

public final class IovArray
implements ChannelOutboundBuffer.MessageProcessor {
    private static final int ADDRESS_SIZE = Buffer.addressSize();
    private static final int IOV_SIZE = 2 * ADDRESS_SIZE;
    private static final int CAPACITY = Limits.IOV_MAX * IOV_SIZE;
    private final ByteBuffer memory = Buffer.allocateDirectWithNativeOrder((int)CAPACITY);
    private final long memoryAddress = Buffer.memoryAddress((ByteBuffer)this.memory);
    private int count;
    private long size;
    private long maxBytes = Limits.SSIZE_MAX;

    public void clear() {
        this.count = 0;
        this.size = 0L;
    }

    @Deprecated
    public boolean add(ByteBuf buf) {
        return this.add((ByteBuf)buf, (int)buf.readerIndex(), (int)buf.readableBytes());
    }

    public boolean add(ByteBuf buf, int offset, int len) {
        ByteBuffer[] buffers;
        if (this.count == Limits.IOV_MAX) {
            return false;
        }
        if (buf.nioBufferCount() == 1) {
            if (len == 0) {
                return true;
            }
            if (buf.hasMemoryAddress()) {
                return this.add((long)(buf.memoryAddress() + (long)offset), (int)len);
            }
            ByteBuffer nioBuffer = buf.internalNioBuffer((int)offset, (int)len);
            return this.add((long)(Buffer.memoryAddress((ByteBuffer)nioBuffer) + (long)nioBuffer.position()), (int)len);
        }
        ByteBuffer[] arrbyteBuffer = buffers = buf.nioBuffers((int)offset, (int)len);
        int n = arrbyteBuffer.length;
        int n2 = 0;
        while (n2 < n) {
            ByteBuffer nioBuffer = arrbyteBuffer[n2];
            int remaining = nioBuffer.remaining();
            if (remaining != 0) {
                if (!this.add((long)(Buffer.memoryAddress((ByteBuffer)nioBuffer) + (long)nioBuffer.position()), (int)remaining)) return false;
                if (this.count == Limits.IOV_MAX) {
                    return false;
                }
            }
            ++n2;
        }
        return true;
    }

    private boolean add(long addr, int len) {
        assert (addr != 0L);
        if (this.maxBytes - (long)len < this.size && this.count > 0) {
            return false;
        }
        int baseOffset = IovArray.idx((int)this.count);
        int lengthOffset = baseOffset + ADDRESS_SIZE;
        this.size += (long)len;
        ++this.count;
        if (ADDRESS_SIZE == 8) {
            if (PlatformDependent.hasUnsafe()) {
                PlatformDependent.putLong((long)((long)baseOffset + this.memoryAddress), (long)addr);
                PlatformDependent.putLong((long)((long)lengthOffset + this.memoryAddress), (long)((long)len));
                return true;
            }
            this.memory.putLong((int)baseOffset, (long)addr);
            this.memory.putLong((int)lengthOffset, (long)((long)len));
            return true;
        }
        assert (ADDRESS_SIZE == 4);
        if (PlatformDependent.hasUnsafe()) {
            PlatformDependent.putInt((long)((long)baseOffset + this.memoryAddress), (int)((int)addr));
            PlatformDependent.putInt((long)((long)lengthOffset + this.memoryAddress), (int)len);
            return true;
        }
        this.memory.putInt((int)baseOffset, (int)((int)addr));
        this.memory.putInt((int)lengthOffset, (int)len);
        return true;
    }

    public int count() {
        return this.count;
    }

    public long size() {
        return this.size;
    }

    public void maxBytes(long maxBytes) {
        this.maxBytes = Math.min((long)Limits.SSIZE_MAX, (long)ObjectUtil.checkPositive((long)maxBytes, (String)"maxBytes"));
    }

    public long maxBytes() {
        return this.maxBytes;
    }

    public long memoryAddress(int offset) {
        return this.memoryAddress + (long)IovArray.idx((int)offset);
    }

    public void release() {
        Buffer.free((ByteBuffer)this.memory);
    }

    @Override
    public boolean processMessage(Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) return false;
        ByteBuf buffer = (ByteBuf)msg;
        return this.add((ByteBuf)buffer, (int)buffer.readerIndex(), (int)buffer.readableBytes());
    }

    private static int idx(int index) {
        return IOV_SIZE * index;
    }
}

