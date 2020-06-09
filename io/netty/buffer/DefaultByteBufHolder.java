/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public class DefaultByteBufHolder
implements ByteBufHolder {
    private final ByteBuf data;

    public DefaultByteBufHolder(ByteBuf data) {
        if (data == null) {
            throw new NullPointerException((String)"data");
        }
        this.data = data;
    }

    @Override
    public ByteBuf content() {
        if (this.data.refCnt() > 0) return this.data;
        throw new IllegalReferenceCountException((int)this.data.refCnt());
    }

    @Override
    public ByteBufHolder copy() {
        return this.replace((ByteBuf)this.data.copy());
    }

    @Override
    public ByteBufHolder duplicate() {
        return this.replace((ByteBuf)this.data.duplicate());
    }

    @Override
    public ByteBufHolder retainedDuplicate() {
        return this.replace((ByteBuf)this.data.retainedDuplicate());
    }

    @Override
    public ByteBufHolder replace(ByteBuf content) {
        return new DefaultByteBufHolder((ByteBuf)content);
    }

    @Override
    public int refCnt() {
        return this.data.refCnt();
    }

    @Override
    public ByteBufHolder retain() {
        this.data.retain();
        return this;
    }

    @Override
    public ByteBufHolder retain(int increment) {
        this.data.retain((int)increment);
        return this;
    }

    @Override
    public ByteBufHolder touch() {
        this.data.touch();
        return this;
    }

    @Override
    public ByteBufHolder touch(Object hint) {
        this.data.touch((Object)hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.data.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.data.release((int)decrement);
    }

    protected final String contentToString() {
        return this.data.toString();
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + '(' + this.contentToString() + ')';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ByteBufHolder)) return false;
        return this.data.equals((Object)((ByteBufHolder)o).content());
    }

    public int hashCode() {
        return this.data.hashCode();
    }
}

