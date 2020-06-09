/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCounted;

public class PongWebSocketFrame
extends WebSocketFrame {
    public PongWebSocketFrame() {
        super((ByteBuf)Unpooled.buffer((int)0));
    }

    public PongWebSocketFrame(ByteBuf binaryData) {
        super((ByteBuf)binaryData);
    }

    public PongWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super((boolean)finalFragment, (int)rsv, (ByteBuf)binaryData);
    }

    @Override
    public PongWebSocketFrame copy() {
        return (PongWebSocketFrame)super.copy();
    }

    @Override
    public PongWebSocketFrame duplicate() {
        return (PongWebSocketFrame)super.duplicate();
    }

    @Override
    public PongWebSocketFrame retainedDuplicate() {
        return (PongWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public PongWebSocketFrame replace(ByteBuf content) {
        return new PongWebSocketFrame((boolean)this.isFinalFragment(), (int)this.rsv(), (ByteBuf)content);
    }

    @Override
    public PongWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public PongWebSocketFrame retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public PongWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public PongWebSocketFrame touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

