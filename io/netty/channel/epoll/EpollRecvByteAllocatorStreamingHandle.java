/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;

final class EpollRecvByteAllocatorStreamingHandle
extends EpollRecvByteAllocatorHandle {
    EpollRecvByteAllocatorStreamingHandle(RecvByteBufAllocator.ExtendedHandle handle) {
        super((RecvByteBufAllocator.ExtendedHandle)handle);
    }

    @Override
    boolean maybeMoreDataToRead() {
        if (this.lastBytesRead() == this.attemptedBytesRead()) return true;
        if (this.isReceivedRdHup()) return true;
        return false;
    }
}

