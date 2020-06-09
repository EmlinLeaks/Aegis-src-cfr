/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioByteChannel;
import io.netty.channel.oio.OioByteStreamChannel;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.WritableByteChannel;

@Deprecated
public abstract class OioByteStreamChannel
extends AbstractOioByteChannel {
    private static final InputStream CLOSED_IN = new InputStream(){

        public int read() {
            return -1;
        }
    };
    private static final OutputStream CLOSED_OUT = new OutputStream(){

        public void write(int b) throws IOException {
            throw new java.nio.channels.ClosedChannelException();
        }
    };
    private InputStream is;
    private OutputStream os;
    private WritableByteChannel outChannel;

    protected OioByteStreamChannel(Channel parent) {
        super((Channel)parent);
    }

    protected final void activate(InputStream is, OutputStream os) {
        if (this.is != null) {
            throw new IllegalStateException((String)"input was set already");
        }
        if (this.os != null) {
            throw new IllegalStateException((String)"output was set already");
        }
        if (is == null) {
            throw new NullPointerException((String)"is");
        }
        if (os == null) {
            throw new NullPointerException((String)"os");
        }
        this.is = is;
        this.os = os;
    }

    @Override
    public boolean isActive() {
        InputStream is = this.is;
        if (is == null) return false;
        if (is == CLOSED_IN) {
            return false;
        }
        OutputStream os = this.os;
        if (os == null) return false;
        if (os == CLOSED_OUT) return false;
        return true;
    }

    @Override
    protected int available() {
        try {
            return this.is.available();
        }
        catch (IOException ignored) {
            return 0;
        }
    }

    @Override
    protected int doReadBytes(ByteBuf buf) throws Exception {
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        allocHandle.attemptedBytesRead((int)Math.max((int)1, (int)Math.min((int)this.available(), (int)buf.maxWritableBytes())));
        return buf.writeBytes((InputStream)this.is, (int)allocHandle.attemptedBytesRead());
    }

    @Override
    protected void doWriteBytes(ByteBuf buf) throws Exception {
        OutputStream os = this.os;
        if (os == null) {
            throw new NotYetConnectedException();
        }
        buf.readBytes((OutputStream)os, (int)buf.readableBytes());
    }

    @Override
    protected void doWriteFileRegion(FileRegion region) throws Exception {
        long localWritten;
        OutputStream os = this.os;
        if (os == null) {
            throw new NotYetConnectedException();
        }
        if (this.outChannel == null) {
            this.outChannel = Channels.newChannel((OutputStream)os);
        }
        long written = 0L;
        do {
            if ((localWritten = region.transferTo((WritableByteChannel)this.outChannel, (long)written)) != -1L) continue;
            OioByteStreamChannel.checkEOF((FileRegion)region);
            return;
        } while ((written += localWritten) < region.count());
    }

    private static void checkEOF(FileRegion region) throws IOException {
        if (region.transferred() >= region.count()) return;
        throw new EOFException((String)("Expected to be able to write " + region.count() + " bytes, but only wrote " + region.transferred()));
    }

    @Override
    protected void doClose() throws Exception {
        InputStream is = this.is;
        OutputStream os = this.os;
        this.is = CLOSED_IN;
        this.os = CLOSED_OUT;
        try {
            if (is == null) return;
            is.close();
            return;
        }
        finally {
            if (os != null) {
                os.close();
            }
        }
    }
}

