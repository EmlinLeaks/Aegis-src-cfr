/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.ssl.PemEncoded;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;

class PemValue
extends AbstractReferenceCounted
implements PemEncoded {
    private final ByteBuf content;
    private final boolean sensitive;

    PemValue(ByteBuf content, boolean sensitive) {
        this.content = ObjectUtil.checkNotNull(content, (String)"content");
        this.sensitive = sensitive;
    }

    @Override
    public boolean isSensitive() {
        return this.sensitive;
    }

    @Override
    public ByteBuf content() {
        int count = this.refCnt();
        if (count > 0) return this.content;
        throw new IllegalReferenceCountException((int)count);
    }

    @Override
    public PemValue copy() {
        return this.replace((ByteBuf)this.content.copy());
    }

    @Override
    public PemValue duplicate() {
        return this.replace((ByteBuf)this.content.duplicate());
    }

    @Override
    public PemValue retainedDuplicate() {
        return this.replace((ByteBuf)this.content.retainedDuplicate());
    }

    @Override
    public PemValue replace(ByteBuf content) {
        return new PemValue((ByteBuf)content, (boolean)this.sensitive);
    }

    @Override
    public PemValue touch() {
        return (PemValue)super.touch();
    }

    @Override
    public PemValue touch(Object hint) {
        this.content.touch((Object)hint);
        return this;
    }

    @Override
    public PemValue retain() {
        return (PemValue)super.retain();
    }

    @Override
    public PemValue retain(int increment) {
        return (PemValue)super.retain((int)increment);
    }

    @Override
    protected void deallocate() {
        if (this.sensitive) {
            SslUtils.zeroout((ByteBuf)this.content);
        }
        this.content.release();
    }
}

