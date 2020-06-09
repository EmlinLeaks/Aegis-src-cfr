/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;

class EpollRecvByteAllocatorHandle
extends RecvByteBufAllocator.DelegatingHandle
implements RecvByteBufAllocator.ExtendedHandle {
    private final PreferredDirectByteBufAllocator preferredDirectByteBufAllocator = new PreferredDirectByteBufAllocator();
    private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier((EpollRecvByteAllocatorHandle)this){
        final /* synthetic */ EpollRecvByteAllocatorHandle this$0;
        {
            this.this$0 = this$0;
        }

        public boolean get() {
            return this.this$0.maybeMoreDataToRead();
        }
    };
    private boolean isEdgeTriggered;
    private boolean receivedRdHup;

    EpollRecvByteAllocatorHandle(RecvByteBufAllocator.ExtendedHandle handle) {
        super((RecvByteBufAllocator.Handle)handle);
    }

    final void receivedRdHup() {
        this.receivedRdHup = true;
    }

    final boolean isReceivedRdHup() {
        return this.receivedRdHup;
    }

    boolean maybeMoreDataToRead() {
        if (this.isEdgeTriggered) {
            if (this.lastBytesRead() > 0) return true;
        }
        if (this.isEdgeTriggered) return false;
        if (this.lastBytesRead() != this.attemptedBytesRead()) return false;
        return true;
    }

    final void edgeTriggered(boolean edgeTriggered) {
        this.isEdgeTriggered = edgeTriggered;
    }

    final boolean isEdgeTriggered() {
        return this.isEdgeTriggered;
    }

    @Override
    public final ByteBuf allocate(ByteBufAllocator alloc) {
        this.preferredDirectByteBufAllocator.updateAllocator((ByteBufAllocator)alloc);
        return this.delegate().allocate((ByteBufAllocator)this.preferredDirectByteBufAllocator);
    }

    @Override
    public final boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
        return ((RecvByteBufAllocator.ExtendedHandle)this.delegate()).continueReading((UncheckedBooleanSupplier)maybeMoreDataSupplier);
    }

    @Override
    public final boolean continueReading() {
        return this.continueReading((UncheckedBooleanSupplier)this.defaultMaybeMoreDataSupplier);
    }
}

