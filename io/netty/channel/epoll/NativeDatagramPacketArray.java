/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Limits;
import java.net.InetSocketAddress;

final class NativeDatagramPacketArray {
    private final NativeDatagramPacket[] packets = new NativeDatagramPacket[Limits.UIO_MAX_IOV];
    private final IovArray iovArray = new IovArray();
    private final byte[] ipv4Bytes = new byte[4];
    private final MyMessageProcessor processor = new MyMessageProcessor((NativeDatagramPacketArray)this, null);
    private int count;

    NativeDatagramPacketArray() {
        int i = 0;
        while (i < this.packets.length) {
            this.packets[i] = new NativeDatagramPacket((NativeDatagramPacketArray)this);
            ++i;
        }
    }

    boolean addWritable(ByteBuf buf, int index, int len) {
        return this.add0((ByteBuf)buf, (int)index, (int)len, null);
    }

    private boolean add0(ByteBuf buf, int index, int len, InetSocketAddress recipient) {
        if (this.count == this.packets.length) {
            return false;
        }
        if (len == 0) {
            return true;
        }
        int offset = this.iovArray.count();
        if (offset == Limits.IOV_MAX) return false;
        if (!this.iovArray.add((ByteBuf)buf, (int)index, (int)len)) {
            return false;
        }
        NativeDatagramPacket p = this.packets[this.count];
        ((NativeDatagramPacket)p).init((long)((long)this.iovArray.memoryAddress((int)offset)), (int)((int)(this.iovArray.count() - offset)), (InetSocketAddress)((InetSocketAddress)recipient));
        ++this.count;
        return true;
    }

    void add(ChannelOutboundBuffer buffer, boolean connected) throws Exception {
        ((MyMessageProcessor)this.processor).connected = (boolean)connected;
        buffer.forEachFlushedMessage((ChannelOutboundBuffer.MessageProcessor)this.processor);
    }

    int count() {
        return this.count;
    }

    NativeDatagramPacket[] packets() {
        return this.packets;
    }

    void clear() {
        this.count = 0;
        this.iovArray.clear();
    }

    void release() {
        this.iovArray.release();
    }

    static /* synthetic */ boolean access$300(NativeDatagramPacketArray x0, ByteBuf x1, int x2, int x3, InetSocketAddress x4) {
        return x0.add0((ByteBuf)x1, (int)x2, (int)x3, (InetSocketAddress)x4);
    }

    static /* synthetic */ byte[] access$400(NativeDatagramPacketArray x0) {
        return x0.ipv4Bytes;
    }
}

