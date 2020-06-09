/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractHttpData;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

public abstract class AbstractDiskHttpData
extends AbstractHttpData {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
    private File file;
    private boolean isRenamed;
    private FileChannel fileChannel;

    protected AbstractDiskHttpData(String name, Charset charset, long size) {
        super((String)name, (Charset)charset, (long)size);
    }

    protected abstract String getDiskFilename();

    protected abstract String getPrefix();

    protected abstract String getBaseDirectory();

    protected abstract String getPostfix();

    protected abstract boolean deleteOnExit();

    private File tempFile() throws IOException {
        String diskFilename = this.getDiskFilename();
        String newpostfix = diskFilename != null ? '_' + diskFilename : this.getPostfix();
        File tmpFile = this.getBaseDirectory() == null ? File.createTempFile((String)this.getPrefix(), (String)newpostfix) : File.createTempFile((String)this.getPrefix(), (String)newpostfix, (File)new File((String)this.getBaseDirectory()));
        if (!this.deleteOnExit()) return tmpFile;
        tmpFile.deleteOnExit();
        return tmpFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setContent(ByteBuf buffer) throws IOException {
        if (buffer == null) {
            throw new NullPointerException((String)"buffer");
        }
        try {
            this.size = (long)buffer.readableBytes();
            this.checkSize((long)this.size);
            if (this.definedSize > 0L && this.definedSize < this.size) {
                throw new IOException((String)("Out of size: " + this.size + " > " + this.definedSize));
            }
            if (this.file == null) {
                this.file = this.tempFile();
            }
            if (buffer.readableBytes() == 0) {
                if (this.file.createNewFile()) return;
                if (this.file.length() == 0L) {
                    return;
                }
                if (!this.file.delete()) throw new IOException((String)("file exists already: " + this.file));
                if (this.file.createNewFile()) return;
                throw new IOException((String)("file exists already: " + this.file));
            }
            RandomAccessFile accessFile = new RandomAccessFile((File)this.file, (String)"rw");
            accessFile.setLength((long)0L);
            try {
                FileChannel localfileChannel = accessFile.getChannel();
                ByteBuffer byteBuffer = buffer.nioBuffer();
                int written = 0;
                while ((long)written < this.size) {
                    written += localfileChannel.write((ByteBuffer)byteBuffer);
                }
                buffer.readerIndex((int)(buffer.readerIndex() + written));
                localfileChannel.force((boolean)false);
            }
            finally {
                accessFile.close();
            }
            this.setCompleted();
            return;
        }
        finally {
            buffer.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        if (buffer != null) {
            try {
                int localsize = buffer.readableBytes();
                this.checkSize((long)(this.size + (long)localsize));
                if (this.definedSize > 0L && this.definedSize < this.size + (long)localsize) {
                    throw new IOException((String)("Out of size: " + (this.size + (long)localsize) + " > " + this.definedSize));
                }
                ByteBuffer byteBuffer = buffer.nioBufferCount() == 1 ? buffer.nioBuffer() : buffer.copy().nioBuffer();
                int written = 0;
                if (this.file == null) {
                    this.file = this.tempFile();
                }
                if (this.fileChannel == null) {
                    RandomAccessFile accessFile = new RandomAccessFile((File)this.file, (String)"rw");
                    this.fileChannel = accessFile.getChannel();
                }
                while (written < localsize) {
                    written += this.fileChannel.write((ByteBuffer)byteBuffer);
                }
                this.size += (long)localsize;
                buffer.readerIndex((int)(buffer.readerIndex() + written));
            }
            finally {
                buffer.release();
            }
        }
        if (!last) {
            if (buffer != null) return;
            throw new NullPointerException((String)"buffer");
        }
        if (this.file == null) {
            this.file = this.tempFile();
        }
        if (this.fileChannel == null) {
            RandomAccessFile accessFile = new RandomAccessFile((File)this.file, (String)"rw");
            this.fileChannel = accessFile.getChannel();
        }
        this.fileChannel.force((boolean)false);
        this.fileChannel.close();
        this.fileChannel = null;
        this.setCompleted();
    }

    @Override
    public void setContent(File file) throws IOException {
        if (this.file != null) {
            this.delete();
        }
        this.file = file;
        this.size = file.length();
        this.checkSize((long)this.size);
        this.isRenamed = true;
        this.setCompleted();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setContent(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException((String)"inputStream");
        }
        if (this.file != null) {
            this.delete();
        }
        this.file = this.tempFile();
        RandomAccessFile accessFile = new RandomAccessFile((File)this.file, (String)"rw");
        accessFile.setLength((long)0L);
        int written = 0;
        try {
            FileChannel localfileChannel = accessFile.getChannel();
            byte[] bytes = new byte[16384];
            ByteBuffer byteBuffer = ByteBuffer.wrap((byte[])bytes);
            int read = inputStream.read((byte[])bytes);
            while (read > 0) {
                byteBuffer.position((int)read).flip();
                this.checkSize((long)((long)(written += localfileChannel.write((ByteBuffer)byteBuffer))));
                read = inputStream.read((byte[])bytes);
            }
            localfileChannel.force((boolean)false);
        }
        finally {
            accessFile.close();
        }
        this.size = (long)written;
        if (this.definedSize > 0L && this.definedSize < this.size) {
            if (!this.file.delete()) {
                logger.warn((String)"Failed to delete: {}", (Object)this.file);
            }
            this.file = null;
            throw new IOException((String)("Out of size: " + this.size + " > " + this.definedSize));
        }
        this.isRenamed = true;
        this.setCompleted();
    }

    @Override
    public void delete() {
        if (this.fileChannel != null) {
            try {
                this.fileChannel.force((boolean)false);
                this.fileChannel.close();
            }
            catch (IOException e) {
                logger.warn((String)"Failed to close a file.", (Throwable)e);
            }
            this.fileChannel = null;
        }
        if (this.isRenamed) return;
        if (this.file != null && this.file.exists() && !this.file.delete()) {
            logger.warn((String)"Failed to delete: {}", (Object)this.file);
        }
        this.file = null;
    }

    @Override
    public byte[] get() throws IOException {
        if (this.file != null) return AbstractDiskHttpData.readFrom((File)this.file);
        return EmptyArrays.EMPTY_BYTES;
    }

    @Override
    public ByteBuf getByteBuf() throws IOException {
        if (this.file == null) {
            return Unpooled.EMPTY_BUFFER;
        }
        byte[] array = AbstractDiskHttpData.readFrom((File)this.file);
        return Unpooled.wrappedBuffer((byte[])array);
    }

    @Override
    public ByteBuf getChunk(int length) throws IOException {
        int readnow;
        int read;
        if (this.file == null) return Unpooled.EMPTY_BUFFER;
        if (length == 0) {
            return Unpooled.EMPTY_BUFFER;
        }
        if (this.fileChannel == null) {
            RandomAccessFile accessFile = new RandomAccessFile((File)this.file, (String)"r");
            this.fileChannel = accessFile.getChannel();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)length);
        for (read = 0; read < length; read += readnow) {
            readnow = this.fileChannel.read((ByteBuffer)byteBuffer);
            if (readnow != -1) continue;
            this.fileChannel.close();
            this.fileChannel = null;
            break;
        }
        if (read == 0) {
            return Unpooled.EMPTY_BUFFER;
        }
        byteBuffer.flip();
        ByteBuf buffer = Unpooled.wrappedBuffer((ByteBuffer)byteBuffer);
        buffer.readerIndex((int)0);
        buffer.writerIndex((int)read);
        return buffer;
    }

    @Override
    public String getString() throws IOException {
        return this.getString((Charset)HttpConstants.DEFAULT_CHARSET);
    }

    @Override
    public String getString(Charset encoding) throws IOException {
        if (this.file == null) {
            return "";
        }
        if (encoding == null) {
            byte[] array = AbstractDiskHttpData.readFrom((File)this.file);
            return new String((byte[])array, (String)HttpConstants.DEFAULT_CHARSET.name());
        }
        byte[] array = AbstractDiskHttpData.readFrom((File)this.file);
        return new String((byte[])array, (String)encoding.name());
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean renameTo(File dest) throws IOException {
        long position;
        if (dest == null) {
            throw new NullPointerException((String)"dest");
        }
        if (this.file == null) {
            throw new IOException((String)"No file defined so cannot be renamed");
        }
        if (this.file.renameTo((File)dest)) {
            this.file = dest;
            this.isRenamed = true;
            return true;
        }
        IOException exception = null;
        RandomAccessFile inputAccessFile = null;
        RandomAccessFile outputAccessFile = null;
        long chunkSize = 8196L;
        try {
            inputAccessFile = new RandomAccessFile((File)this.file, (String)"r");
            outputAccessFile = new RandomAccessFile((File)dest, (String)"rw");
            FileChannel in = inputAccessFile.getChannel();
            FileChannel out = outputAccessFile.getChannel();
            for (position = 0L; position < this.size; position += in.transferTo((long)position, (long)chunkSize, (WritableByteChannel)out)) {
                if (chunkSize >= this.size - position) continue;
                chunkSize = this.size - position;
            }
        }
        catch (IOException e) {
            exception = e;
        }
        finally {
            if (inputAccessFile != null) {
                try {
                    inputAccessFile.close();
                }
                catch (IOException e) {
                    if (exception == null) {
                        exception = e;
                    }
                    logger.warn((String)"Multiple exceptions detected, the following will be suppressed {}", (Throwable)e);
                }
            }
            if (outputAccessFile != null) {
                try {
                    outputAccessFile.close();
                }
                catch (IOException e) {
                    if (exception == null) {
                        exception = e;
                    }
                    logger.warn((String)"Multiple exceptions detected, the following will be suppressed {}", (Throwable)e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        if (position != this.size) {
            if (dest.delete()) return false;
            logger.warn((String)"Failed to delete: {}", (Object)dest);
            return false;
        }
        if (!this.file.delete()) {
            logger.warn((String)"Failed to delete: {}", (Object)this.file);
        }
        this.file = dest;
        this.isRenamed = true;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static byte[] readFrom(File src) throws IOException {
        long srcsize = src.length();
        if (srcsize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException((String)"File too big to be loaded in memory");
        }
        RandomAccessFile accessFile = new RandomAccessFile((File)src, (String)"r");
        byte[] array = new byte[(int)srcsize];
        try {
            FileChannel fileChannel = accessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.wrap((byte[])array);
            int read = 0;
            while ((long)read < srcsize) {
                read += fileChannel.read((ByteBuffer)byteBuffer);
            }
            return array;
        }
        finally {
            accessFile.close();
        }
    }

    @Override
    public File getFile() throws IOException {
        return this.file;
    }

    @Override
    public HttpData touch() {
        return this;
    }

    @Override
    public HttpData touch(Object hint) {
        return this;
    }
}

