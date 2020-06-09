/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractHttpData;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.ReferenceCounted;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractMemoryHttpData
extends AbstractHttpData {
    private ByteBuf byteBuf;
    private int chunkPosition;

    protected AbstractMemoryHttpData(String name, Charset charset, long size) {
        super((String)name, (Charset)charset, (long)size);
    }

    @Override
    public void setContent(ByteBuf buffer) throws IOException {
        if (buffer == null) {
            throw new NullPointerException((String)"buffer");
        }
        long localsize = (long)buffer.readableBytes();
        this.checkSize((long)localsize);
        if (this.definedSize > 0L && this.definedSize < localsize) {
            throw new IOException((String)("Out of size: " + localsize + " > " + this.definedSize));
        }
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = buffer;
        this.size = localsize;
        this.setCompleted();
    }

    @Override
    public void setContent(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException((String)"inputStream");
        }
        ByteBuf buffer = Unpooled.buffer();
        byte[] bytes = new byte[16384];
        int read = inputStream.read((byte[])bytes);
        int written = 0;
        while (read > 0) {
            buffer.writeBytes((byte[])bytes, (int)0, (int)read);
            this.checkSize((long)((long)(written += read)));
            read = inputStream.read((byte[])bytes);
        }
        this.size = (long)written;
        if (this.definedSize > 0L && this.definedSize < this.size) {
            throw new IOException((String)("Out of size: " + this.size + " > " + this.definedSize));
        }
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = buffer;
        this.setCompleted();
    }

    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        if (buffer != null) {
            long localsize = (long)buffer.readableBytes();
            this.checkSize((long)(this.size + localsize));
            if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
                throw new IOException((String)("Out of size: " + (this.size + localsize) + " > " + this.definedSize));
            }
            this.size += localsize;
            if (this.byteBuf == null) {
                this.byteBuf = buffer;
            } else if (this.byteBuf instanceof CompositeByteBuf) {
                CompositeByteBuf cbb = (CompositeByteBuf)this.byteBuf;
                cbb.addComponent((boolean)true, (ByteBuf)buffer);
            } else {
                CompositeByteBuf cbb = Unpooled.compositeBuffer((int)Integer.MAX_VALUE);
                cbb.addComponents((boolean)true, (ByteBuf[])new ByteBuf[]{this.byteBuf, buffer});
                this.byteBuf = cbb;
            }
        }
        if (last) {
            this.setCompleted();
            return;
        }
        if (buffer != null) return;
        throw new NullPointerException((String)"buffer");
    }

    @Override
    public void setContent(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException((String)"file");
        }
        long newsize = file.length();
        if (newsize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException((String)"File too big to be loaded in memory");
        }
        this.checkSize((long)newsize);
        RandomAccessFile accessFile = new RandomAccessFile((File)file, (String)"r");
        FileChannel fileChannel = accessFile.getChannel();
        byte[] array = new byte[(int)newsize];
        ByteBuffer byteBuffer = ByteBuffer.wrap((byte[])array);
        int read = 0;
        while ((long)read < newsize) {
            read += fileChannel.read((ByteBuffer)byteBuffer);
        }
        fileChannel.close();
        accessFile.close();
        byteBuffer.flip();
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = Unpooled.wrappedBuffer((int)Integer.MAX_VALUE, (ByteBuffer[])new ByteBuffer[]{byteBuffer});
        this.size = newsize;
        this.setCompleted();
    }

    @Override
    public void delete() {
        if (this.byteBuf == null) return;
        this.byteBuf.release();
        this.byteBuf = null;
    }

    @Override
    public byte[] get() {
        if (this.byteBuf == null) {
            return Unpooled.EMPTY_BUFFER.array();
        }
        byte[] array = new byte[this.byteBuf.readableBytes()];
        this.byteBuf.getBytes((int)this.byteBuf.readerIndex(), (byte[])array);
        return array;
    }

    @Override
    public String getString() {
        return this.getString((Charset)HttpConstants.DEFAULT_CHARSET);
    }

    @Override
    public String getString(Charset encoding) {
        if (this.byteBuf == null) {
            return "";
        }
        if (encoding != null) return this.byteBuf.toString((Charset)encoding);
        encoding = HttpConstants.DEFAULT_CHARSET;
        return this.byteBuf.toString((Charset)encoding);
    }

    @Override
    public ByteBuf getByteBuf() {
        return this.byteBuf;
    }

    @Override
    public ByteBuf getChunk(int length) throws IOException {
        if (this.byteBuf == null || length == 0 || this.byteBuf.readableBytes() == 0) {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
        }
        int sizeLeft = this.byteBuf.readableBytes() - this.chunkPosition;
        if (sizeLeft == 0) {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
        }
        int sliceLength = length;
        if (sizeLeft < length) {
            sliceLength = sizeLeft;
        }
        ByteBuf chunk = this.byteBuf.retainedSlice((int)this.chunkPosition, (int)sliceLength);
        this.chunkPosition += sliceLength;
        return chunk;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public boolean renameTo(File dest) throws IOException {
        int written;
        if (dest == null) {
            throw new NullPointerException((String)"dest");
        }
        if (this.byteBuf == null) {
            if (dest.createNewFile()) return true;
            throw new IOException((String)("file exists already: " + dest));
        }
        int length = this.byteBuf.readableBytes();
        RandomAccessFile accessFile = new RandomAccessFile((File)dest, (String)"rw");
        FileChannel fileChannel = accessFile.getChannel();
        if (this.byteBuf.nioBufferCount() == 1) {
            ByteBuffer byteBuffer = this.byteBuf.nioBuffer();
            for (written = 0; written < length; written += fileChannel.write((ByteBuffer)byteBuffer)) {
            }
        } else {
            ByteBuffer[] byteBuffers = this.byteBuf.nioBuffers();
            while (written < length) {
                written = (int)((long)written + fileChannel.write((ByteBuffer[])byteBuffers));
            }
        }
        fileChannel.force((boolean)false);
        fileChannel.close();
        accessFile.close();
        if (written != length) return false;
        return true;
    }

    @Override
    public File getFile() throws IOException {
        throw new IOException((String)"Not represented by a file");
    }

    @Override
    public HttpData touch() {
        return this.touch(null);
    }

    @Override
    public HttpData touch(Object hint) {
        if (this.byteBuf == null) return this;
        this.byteBuf.touch((Object)hint);
        return this;
    }
}

