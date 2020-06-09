/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.ReferenceCounted;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedAttribute
implements Attribute {
    private Attribute attribute;
    private final long limitSize;
    private long maxSize = -1L;

    public MixedAttribute(String name, long limitSize) {
        this((String)name, (long)limitSize, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public MixedAttribute(String name, long definedSize, long limitSize) {
        this((String)name, (long)definedSize, (long)limitSize, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public MixedAttribute(String name, long limitSize, Charset charset) {
        this.limitSize = limitSize;
        this.attribute = new MemoryAttribute((String)name, (Charset)charset);
    }

    public MixedAttribute(String name, long definedSize, long limitSize, Charset charset) {
        this.limitSize = limitSize;
        this.attribute = new MemoryAttribute((String)name, (long)definedSize, (Charset)charset);
    }

    public MixedAttribute(String name, String value, long limitSize) {
        this((String)name, (String)value, (long)limitSize, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public MixedAttribute(String name, String value, long limitSize, Charset charset) {
        this.limitSize = limitSize;
        if ((long)value.length() > this.limitSize) {
            try {
                this.attribute = new DiskAttribute((String)name, (String)value, (Charset)charset);
                return;
            }
            catch (IOException e) {
                try {
                    this.attribute = new MemoryAttribute((String)name, (String)value, (Charset)charset);
                    return;
                }
                catch (IOException ignore) {
                    throw new IllegalArgumentException((Throwable)e);
                }
            }
        }
        try {
            this.attribute = new MemoryAttribute((String)name, (String)value, (Charset)charset);
            return;
        }
        catch (IOException e) {
            throw new IllegalArgumentException((Throwable)e);
        }
    }

    @Override
    public long getMaxSize() {
        return this.maxSize;
    }

    @Override
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        this.attribute.setMaxSize((long)maxSize);
    }

    @Override
    public void checkSize(long newSize) throws IOException {
        if (this.maxSize < 0L) return;
        if (newSize <= this.maxSize) return;
        throw new IOException((String)"Size exceed allowed maximum capacity");
    }

    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        if (this.attribute instanceof MemoryAttribute) {
            this.checkSize((long)(this.attribute.length() + (long)buffer.readableBytes()));
            if (this.attribute.length() + (long)buffer.readableBytes() > this.limitSize) {
                DiskAttribute diskAttribute = new DiskAttribute((String)this.attribute.getName(), (long)this.attribute.definedLength());
                diskAttribute.setMaxSize((long)this.maxSize);
                if (((MemoryAttribute)this.attribute).getByteBuf() != null) {
                    diskAttribute.addContent((ByteBuf)((MemoryAttribute)this.attribute).getByteBuf(), (boolean)false);
                }
                this.attribute = diskAttribute;
            }
        }
        this.attribute.addContent((ByteBuf)buffer, (boolean)last);
    }

    @Override
    public void delete() {
        this.attribute.delete();
    }

    @Override
    public byte[] get() throws IOException {
        return this.attribute.get();
    }

    @Override
    public ByteBuf getByteBuf() throws IOException {
        return this.attribute.getByteBuf();
    }

    @Override
    public Charset getCharset() {
        return this.attribute.getCharset();
    }

    @Override
    public String getString() throws IOException {
        return this.attribute.getString();
    }

    @Override
    public String getString(Charset encoding) throws IOException {
        return this.attribute.getString((Charset)encoding);
    }

    @Override
    public boolean isCompleted() {
        return this.attribute.isCompleted();
    }

    @Override
    public boolean isInMemory() {
        return this.attribute.isInMemory();
    }

    @Override
    public long length() {
        return this.attribute.length();
    }

    @Override
    public long definedLength() {
        return this.attribute.definedLength();
    }

    @Override
    public boolean renameTo(File dest) throws IOException {
        return this.attribute.renameTo((File)dest);
    }

    @Override
    public void setCharset(Charset charset) {
        this.attribute.setCharset((Charset)charset);
    }

    @Override
    public void setContent(ByteBuf buffer) throws IOException {
        this.checkSize((long)((long)buffer.readableBytes()));
        if ((long)buffer.readableBytes() > this.limitSize && this.attribute instanceof MemoryAttribute) {
            this.attribute = new DiskAttribute((String)this.attribute.getName(), (long)this.attribute.definedLength());
            this.attribute.setMaxSize((long)this.maxSize);
        }
        this.attribute.setContent((ByteBuf)buffer);
    }

    @Override
    public void setContent(File file) throws IOException {
        this.checkSize((long)file.length());
        if (file.length() > this.limitSize && this.attribute instanceof MemoryAttribute) {
            this.attribute = new DiskAttribute((String)this.attribute.getName(), (long)this.attribute.definedLength());
            this.attribute.setMaxSize((long)this.maxSize);
        }
        this.attribute.setContent((File)file);
    }

    @Override
    public void setContent(InputStream inputStream) throws IOException {
        if (this.attribute instanceof MemoryAttribute) {
            this.attribute = new DiskAttribute((String)this.attribute.getName(), (long)this.attribute.definedLength());
            this.attribute.setMaxSize((long)this.maxSize);
        }
        this.attribute.setContent((InputStream)inputStream);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return this.attribute.getHttpDataType();
    }

    @Override
    public String getName() {
        return this.attribute.getName();
    }

    public int hashCode() {
        return this.attribute.hashCode();
    }

    public boolean equals(Object obj) {
        return this.attribute.equals((Object)obj);
    }

    @Override
    public int compareTo(InterfaceHttpData o) {
        return this.attribute.compareTo(o);
    }

    public String toString() {
        return "Mixed: " + this.attribute;
    }

    @Override
    public String getValue() throws IOException {
        return this.attribute.getValue();
    }

    @Override
    public void setValue(String value) throws IOException {
        if (value != null) {
            this.checkSize((long)((long)value.getBytes().length));
        }
        this.attribute.setValue((String)value);
    }

    @Override
    public ByteBuf getChunk(int length) throws IOException {
        return this.attribute.getChunk((int)length);
    }

    @Override
    public File getFile() throws IOException {
        return this.attribute.getFile();
    }

    @Override
    public Attribute copy() {
        return this.attribute.copy();
    }

    @Override
    public Attribute duplicate() {
        return this.attribute.duplicate();
    }

    @Override
    public Attribute retainedDuplicate() {
        return this.attribute.retainedDuplicate();
    }

    @Override
    public Attribute replace(ByteBuf content) {
        return this.attribute.replace((ByteBuf)content);
    }

    @Override
    public ByteBuf content() {
        return this.attribute.content();
    }

    @Override
    public int refCnt() {
        return this.attribute.refCnt();
    }

    @Override
    public Attribute retain() {
        this.attribute.retain();
        return this;
    }

    @Override
    public Attribute retain(int increment) {
        this.attribute.retain((int)increment);
        return this;
    }

    @Override
    public Attribute touch() {
        this.attribute.touch();
        return this;
    }

    @Override
    public Attribute touch(Object hint) {
        this.attribute.touch((Object)hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.attribute.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.attribute.release((int)decrement);
    }
}

