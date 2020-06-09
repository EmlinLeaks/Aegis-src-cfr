/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractMemoryHttpData;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.nio.charset.Charset;

public class MemoryAttribute
extends AbstractMemoryHttpData
implements Attribute {
    public MemoryAttribute(String name) {
        this((String)name, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public MemoryAttribute(String name, long definedSize) {
        this((String)name, (long)definedSize, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public MemoryAttribute(String name, Charset charset) {
        super((String)name, (Charset)charset, (long)0L);
    }

    public MemoryAttribute(String name, long definedSize, Charset charset) {
        super((String)name, (Charset)charset, (long)definedSize);
    }

    public MemoryAttribute(String name, String value) throws IOException {
        this((String)name, (String)value, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public MemoryAttribute(String name, String value, Charset charset) throws IOException {
        super((String)name, (Charset)charset, (long)0L);
        this.setValue((String)value);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.Attribute;
    }

    @Override
    public String getValue() {
        return this.getByteBuf().toString((Charset)this.getCharset());
    }

    @Override
    public void setValue(String value) throws IOException {
        if (value == null) {
            throw new NullPointerException((String)"value");
        }
        byte[] bytes = value.getBytes((Charset)this.getCharset());
        this.checkSize((long)((long)bytes.length));
        ByteBuf buffer = Unpooled.wrappedBuffer((byte[])bytes);
        if (this.definedSize > 0L) {
            this.definedSize = (long)buffer.readableBytes();
        }
        this.setContent((ByteBuf)buffer);
    }

    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        int localsize = buffer.readableBytes();
        this.checkSize((long)(this.size + (long)localsize));
        if (this.definedSize > 0L && this.definedSize < this.size + (long)localsize) {
            this.definedSize = this.size + (long)localsize;
        }
        super.addContent((ByteBuf)buffer, (boolean)last);
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute attribute = (Attribute)o;
        return this.getName().equalsIgnoreCase((String)attribute.getName());
    }

    @Override
    public int compareTo(InterfaceHttpData other) {
        if (other instanceof Attribute) return this.compareTo((Attribute)((Attribute)other));
        throw new ClassCastException((String)("Cannot compare " + (Object)((Object)this.getHttpDataType()) + " with " + (Object)((Object)other.getHttpDataType())));
    }

    @Override
    public int compareTo(Attribute o) {
        return this.getName().compareToIgnoreCase((String)o.getName());
    }

    public String toString() {
        return this.getName() + '=' + this.getValue();
    }

    @Override
    public Attribute copy() {
        ByteBuf byteBuf;
        ByteBuf content = this.content();
        if (content != null) {
            byteBuf = content.copy();
            return this.replace((ByteBuf)byteBuf);
        }
        byteBuf = null;
        return this.replace((ByteBuf)byteBuf);
    }

    @Override
    public Attribute duplicate() {
        ByteBuf byteBuf;
        ByteBuf content = this.content();
        if (content != null) {
            byteBuf = content.duplicate();
            return this.replace((ByteBuf)byteBuf);
        }
        byteBuf = null;
        return this.replace((ByteBuf)byteBuf);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Attribute retainedDuplicate() {
        ByteBuf content = this.content();
        if (content == null) return this.replace(null);
        content = content.retainedDuplicate();
        boolean success = false;
        try {
            Attribute duplicate = this.replace((ByteBuf)content);
            success = true;
            Attribute attribute = duplicate;
            return attribute;
        }
        finally {
            if (!success) {
                content.release();
            }
        }
    }

    @Override
    public Attribute replace(ByteBuf content) {
        MemoryAttribute attr = new MemoryAttribute((String)this.getName());
        attr.setCharset((Charset)this.getCharset());
        if (content == null) return attr;
        try {
            attr.setContent((ByteBuf)content);
            return attr;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public Attribute retain() {
        super.retain();
        return this;
    }

    @Override
    public Attribute retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public Attribute touch() {
        super.touch();
        return this;
    }

    @Override
    public Attribute touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

