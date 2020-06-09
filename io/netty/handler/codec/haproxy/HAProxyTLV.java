/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.haproxy.HAProxyTLV;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;

public class HAProxyTLV
extends DefaultByteBufHolder {
    private final Type type;
    private final byte typeByteValue;

    HAProxyTLV(Type type, byte typeByteValue, ByteBuf content) {
        super((ByteBuf)content);
        ObjectUtil.checkNotNull(type, (String)"type");
        this.type = type;
        this.typeByteValue = typeByteValue;
    }

    public Type type() {
        return this.type;
    }

    public byte typeByteValue() {
        return this.typeByteValue;
    }

    @Override
    public HAProxyTLV copy() {
        return this.replace((ByteBuf)this.content().copy());
    }

    @Override
    public HAProxyTLV duplicate() {
        return this.replace((ByteBuf)this.content().duplicate());
    }

    @Override
    public HAProxyTLV retainedDuplicate() {
        return this.replace((ByteBuf)this.content().retainedDuplicate());
    }

    @Override
    public HAProxyTLV replace(ByteBuf content) {
        return new HAProxyTLV((Type)this.type, (byte)this.typeByteValue, (ByteBuf)content);
    }

    @Override
    public HAProxyTLV retain() {
        super.retain();
        return this;
    }

    @Override
    public HAProxyTLV retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public HAProxyTLV touch() {
        super.touch();
        return this;
    }

    @Override
    public HAProxyTLV touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

