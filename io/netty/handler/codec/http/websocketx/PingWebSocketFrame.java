/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCounted;

public class PingWebSocketFrame
extends WebSocketFrame {
    public PingWebSocketFrame() {
        super((boolean)true, (int)0, (ByteBuf)Unpooled.buffer((int)0));
    }

    public PingWebSocketFrame(ByteBuf binaryData) {
        super((ByteBuf)binaryData);
    }

    public PingWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super((boolean)finalFragment, (int)rsv, (ByteBuf)binaryData);
    }

    @Override
    public PingWebSocketFrame copy() {
        return (PingWebSocketFrame)super.copy();
    }

    @Override
    public PingWebSocketFrame duplicate() {
        return (PingWebSocketFrame)super.duplicate();
    }

    @Override
    public PingWebSocketFrame retainedDuplicate() {
        return (PingWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public PingWebSocketFrame replace(ByteBuf content) {
        return new PingWebSocketFrame((boolean)this.isFinalFragment(), (int)this.rsv(), (ByteBuf)content);
    }

    @Override
    public PingWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public PingWebSocketFrame retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public PingWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public PingWebSocketFrame touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

