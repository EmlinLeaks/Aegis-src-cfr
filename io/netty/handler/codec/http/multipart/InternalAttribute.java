/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class InternalAttribute
extends AbstractReferenceCounted
implements InterfaceHttpData {
    private final List<ByteBuf> value = new ArrayList<ByteBuf>();
    private final Charset charset;
    private int size;

    InternalAttribute(Charset charset) {
        this.charset = charset;
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.InternalAttribute;
    }

    public void addValue(String value) {
        if (value == null) {
            throw new NullPointerException((String)"value");
        }
        ByteBuf buf = Unpooled.copiedBuffer((CharSequence)value, (Charset)this.charset);
        this.value.add((ByteBuf)buf);
        this.size += buf.readableBytes();
    }

    public void addValue(String value, int rank) {
        if (value == null) {
            throw new NullPointerException((String)"value");
        }
        ByteBuf buf = Unpooled.copiedBuffer((CharSequence)value, (Charset)this.charset);
        this.value.add((int)rank, (ByteBuf)buf);
        this.size += buf.readableBytes();
    }

    public void setValue(String value, int rank) {
        if (value == null) {
            throw new NullPointerException((String)"value");
        }
        ByteBuf buf = Unpooled.copiedBuffer((CharSequence)value, (Charset)this.charset);
        ByteBuf old = this.value.set((int)rank, (ByteBuf)buf);
        if (old != null) {
            this.size -= old.readableBytes();
            old.release();
        }
        this.size += buf.readableBytes();
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof InternalAttribute)) {
            return false;
        }
        InternalAttribute attribute = (InternalAttribute)o;
        return this.getName().equalsIgnoreCase((String)attribute.getName());
    }

    @Override
    public int compareTo(InterfaceHttpData o) {
        if (o instanceof InternalAttribute) return this.compareTo((InternalAttribute)((InternalAttribute)o));
        throw new ClassCastException((String)("Cannot compare " + (Object)((Object)this.getHttpDataType()) + " with " + (Object)((Object)o.getHttpDataType())));
    }

    @Override
    public int compareTo(InternalAttribute o) {
        return this.getName().compareToIgnoreCase((String)o.getName());
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        Iterator<ByteBuf> iterator = this.value.iterator();
        while (iterator.hasNext()) {
            ByteBuf elt = iterator.next();
            result.append((String)elt.toString((Charset)this.charset));
        }
        return result.toString();
    }

    public int size() {
        return this.size;
    }

    public ByteBuf toByteBuf() {
        return Unpooled.compositeBuffer().addComponents(this.value).writerIndex((int)this.size()).readerIndex((int)0);
    }

    @Override
    public String getName() {
        return "InternalAttribute";
    }

    @Override
    protected void deallocate() {
    }

    @Override
    public InterfaceHttpData retain() {
        Iterator<ByteBuf> iterator = this.value.iterator();
        while (iterator.hasNext()) {
            ByteBuf buf = iterator.next();
            buf.retain();
        }
        return this;
    }

    @Override
    public InterfaceHttpData retain(int increment) {
        Iterator<ByteBuf> iterator = this.value.iterator();
        while (iterator.hasNext()) {
            ByteBuf buf = iterator.next();
            buf.retain((int)increment);
        }
        return this;
    }

    @Override
    public InterfaceHttpData touch() {
        Iterator<ByteBuf> iterator = this.value.iterator();
        while (iterator.hasNext()) {
            ByteBuf buf = iterator.next();
            buf.touch();
        }
        return this;
    }

    @Override
    public InterfaceHttpData touch(Object hint) {
        Iterator<ByteBuf> iterator = this.value.iterator();
        while (iterator.hasNext()) {
            ByteBuf buf = iterator.next();
            buf.touch((Object)hint);
        }
        return this;
    }
}

