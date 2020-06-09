/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.jboss.marshalling.ByteOutput
 */
package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.jboss.marshalling.ByteOutput;

class ChannelBufferByteOutput
implements ByteOutput {
    private final ByteBuf buffer;

    ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    public void write(int b) throws IOException {
        this.buffer.writeByte((int)b);
    }

    public void write(byte[] bytes) throws IOException {
        this.buffer.writeBytes((byte[])bytes);
    }

    public void write(byte[] bytes, int srcIndex, int length) throws IOException {
        this.buffer.writeBytes((byte[])bytes, (int)srcIndex, (int)length);
    }

    ByteBuf getBuffer() {
        return this.buffer;
    }
}

