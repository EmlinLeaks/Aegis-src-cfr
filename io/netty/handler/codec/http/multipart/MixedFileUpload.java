/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryFileUpload;
import io.netty.util.ReferenceCounted;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedFileUpload
implements FileUpload {
    private FileUpload fileUpload;
    private final long limitSize;
    private final long definedSize;
    private long maxSize = -1L;

    public MixedFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, long limitSize) {
        this.limitSize = limitSize;
        this.fileUpload = size > this.limitSize ? new DiskFileUpload((String)name, (String)filename, (String)contentType, (String)contentTransferEncoding, (Charset)charset, (long)size) : new MemoryFileUpload((String)name, (String)filename, (String)contentType, (String)contentTransferEncoding, (Charset)charset, (long)size);
        this.definedSize = size;
    }

    @Override
    public long getMaxSize() {
        return this.maxSize;
    }

    @Override
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        this.fileUpload.setMaxSize((long)maxSize);
    }

    @Override
    public void checkSize(long newSize) throws IOException {
        if (this.maxSize < 0L) return;
        if (newSize <= this.maxSize) return;
        throw new IOException((String)"Size exceed allowed maximum capacity");
    }

    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        if (this.fileUpload instanceof MemoryFileUpload) {
            this.checkSize((long)(this.fileUpload.length() + (long)buffer.readableBytes()));
            if (this.fileUpload.length() + (long)buffer.readableBytes() > this.limitSize) {
                DiskFileUpload diskFileUpload = new DiskFileUpload((String)this.fileUpload.getName(), (String)this.fileUpload.getFilename(), (String)this.fileUpload.getContentType(), (String)this.fileUpload.getContentTransferEncoding(), (Charset)this.fileUpload.getCharset(), (long)this.definedSize);
                diskFileUpload.setMaxSize((long)this.maxSize);
                ByteBuf data = this.fileUpload.getByteBuf();
                if (data != null && data.isReadable()) {
                    diskFileUpload.addContent((ByteBuf)data.retain(), (boolean)false);
                }
                this.fileUpload.release();
                this.fileUpload = diskFileUpload;
            }
        }
        this.fileUpload.addContent((ByteBuf)buffer, (boolean)last);
    }

    @Override
    public void delete() {
        this.fileUpload.delete();
    }

    @Override
    public byte[] get() throws IOException {
        return this.fileUpload.get();
    }

    @Override
    public ByteBuf getByteBuf() throws IOException {
        return this.fileUpload.getByteBuf();
    }

    @Override
    public Charset getCharset() {
        return this.fileUpload.getCharset();
    }

    @Override
    public String getContentType() {
        return this.fileUpload.getContentType();
    }

    @Override
    public String getContentTransferEncoding() {
        return this.fileUpload.getContentTransferEncoding();
    }

    @Override
    public String getFilename() {
        return this.fileUpload.getFilename();
    }

    @Override
    public String getString() throws IOException {
        return this.fileUpload.getString();
    }

    @Override
    public String getString(Charset encoding) throws IOException {
        return this.fileUpload.getString((Charset)encoding);
    }

    @Override
    public boolean isCompleted() {
        return this.fileUpload.isCompleted();
    }

    @Override
    public boolean isInMemory() {
        return this.fileUpload.isInMemory();
    }

    @Override
    public long length() {
        return this.fileUpload.length();
    }

    @Override
    public long definedLength() {
        return this.fileUpload.definedLength();
    }

    @Override
    public boolean renameTo(File dest) throws IOException {
        return this.fileUpload.renameTo((File)dest);
    }

    @Override
    public void setCharset(Charset charset) {
        this.fileUpload.setCharset((Charset)charset);
    }

    @Override
    public void setContent(ByteBuf buffer) throws IOException {
        this.checkSize((long)((long)buffer.readableBytes()));
        if ((long)buffer.readableBytes() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
            FileUpload memoryUpload = this.fileUpload;
            this.fileUpload = new DiskFileUpload((String)memoryUpload.getName(), (String)memoryUpload.getFilename(), (String)memoryUpload.getContentType(), (String)memoryUpload.getContentTransferEncoding(), (Charset)memoryUpload.getCharset(), (long)this.definedSize);
            this.fileUpload.setMaxSize((long)this.maxSize);
            memoryUpload.release();
        }
        this.fileUpload.setContent((ByteBuf)buffer);
    }

    @Override
    public void setContent(File file) throws IOException {
        this.checkSize((long)file.length());
        if (file.length() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
            FileUpload memoryUpload = this.fileUpload;
            this.fileUpload = new DiskFileUpload((String)memoryUpload.getName(), (String)memoryUpload.getFilename(), (String)memoryUpload.getContentType(), (String)memoryUpload.getContentTransferEncoding(), (Charset)memoryUpload.getCharset(), (long)this.definedSize);
            this.fileUpload.setMaxSize((long)this.maxSize);
            memoryUpload.release();
        }
        this.fileUpload.setContent((File)file);
    }

    @Override
    public void setContent(InputStream inputStream) throws IOException {
        if (this.fileUpload instanceof MemoryFileUpload) {
            FileUpload memoryUpload = this.fileUpload;
            this.fileUpload = new DiskFileUpload((String)this.fileUpload.getName(), (String)this.fileUpload.getFilename(), (String)this.fileUpload.getContentType(), (String)this.fileUpload.getContentTransferEncoding(), (Charset)this.fileUpload.getCharset(), (long)this.definedSize);
            this.fileUpload.setMaxSize((long)this.maxSize);
            memoryUpload.release();
        }
        this.fileUpload.setContent((InputStream)inputStream);
    }

    @Override
    public void setContentType(String contentType) {
        this.fileUpload.setContentType((String)contentType);
    }

    @Override
    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.fileUpload.setContentTransferEncoding((String)contentTransferEncoding);
    }

    @Override
    public void setFilename(String filename) {
        this.fileUpload.setFilename((String)filename);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return this.fileUpload.getHttpDataType();
    }

    @Override
    public String getName() {
        return this.fileUpload.getName();
    }

    public int hashCode() {
        return this.fileUpload.hashCode();
    }

    public boolean equals(Object obj) {
        return this.fileUpload.equals((Object)obj);
    }

    @Override
    public int compareTo(InterfaceHttpData o) {
        return this.fileUpload.compareTo(o);
    }

    public String toString() {
        return "Mixed: " + this.fileUpload;
    }

    @Override
    public ByteBuf getChunk(int length) throws IOException {
        return this.fileUpload.getChunk((int)length);
    }

    @Override
    public File getFile() throws IOException {
        return this.fileUpload.getFile();
    }

    @Override
    public FileUpload copy() {
        return this.fileUpload.copy();
    }

    @Override
    public FileUpload duplicate() {
        return this.fileUpload.duplicate();
    }

    @Override
    public FileUpload retainedDuplicate() {
        return this.fileUpload.retainedDuplicate();
    }

    @Override
    public FileUpload replace(ByteBuf content) {
        return this.fileUpload.replace((ByteBuf)content);
    }

    @Override
    public ByteBuf content() {
        return this.fileUpload.content();
    }

    @Override
    public int refCnt() {
        return this.fileUpload.refCnt();
    }

    @Override
    public FileUpload retain() {
        this.fileUpload.retain();
        return this;
    }

    @Override
    public FileUpload retain(int increment) {
        this.fileUpload.retain((int)increment);
        return this;
    }

    @Override
    public FileUpload touch() {
        this.fileUpload.touch();
        return this;
    }

    @Override
    public FileUpload touch(Object hint) {
        this.fileUpload.touch((Object)hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.fileUpload.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.fileUpload.release((int)decrement);
    }
}

