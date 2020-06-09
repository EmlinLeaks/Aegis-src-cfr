/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract class SocketWritableByteChannel
implements WritableByteChannel {
    private final FileDescriptor fd;

    protected SocketWritableByteChannel(FileDescriptor fd) {
        this.fd = ObjectUtil.checkNotNull(fd, (String)"fd");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final int write(ByteBuffer src) throws IOException {
        int written;
        int position = src.position();
        int limit = src.limit();
        if (src.isDirect()) {
            written = this.fd.write((ByteBuffer)src, (int)position, (int)src.limit());
        } else {
            int readableBytes = limit - position;
            ByteBuf buffer = null;
            try {
                if (readableBytes == 0) {
                    buffer = Unpooled.EMPTY_BUFFER;
                } else {
                    ByteBufAllocator alloc = this.alloc();
                    if (alloc.isDirectBufferPooled()) {
                        buffer = alloc.directBuffer((int)readableBytes);
                    } else {
                        buffer = ByteBufUtil.threadLocalDirectBuffer();
                        if (buffer == null) {
                            buffer = Unpooled.directBuffer((int)readableBytes);
                        }
                    }
                }
                buffer.writeBytes((ByteBuffer)src.duplicate());
                ByteBuffer nioBuffer = buffer.internalNioBuffer((int)buffer.readerIndex(), (int)readableBytes);
                written = this.fd.write((ByteBuffer)nioBuffer, (int)nioBuffer.position(), (int)nioBuffer.limit());
            }
            finally {
                if (buffer != null) {
                    buffer.release();
                }
            }
        }
        if (written <= 0) return written;
        src.position((int)(position + written));
        return written;
    }

    @Override
    public final boolean isOpen() {
        return this.fd.isOpen();
    }

    @Override
    public final void close() throws IOException {
        this.fd.close();
    }

    protected abstract ByteBufAllocator alloc();
}

