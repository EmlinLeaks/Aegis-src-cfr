/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCounted;

public class BinaryWebSocketFrame
extends WebSocketFrame {
    public BinaryWebSocketFrame() {
        super((ByteBuf)Unpooled.buffer((int)0));
    }

    public BinaryWebSocketFrame(ByteBuf binaryData) {
        super((ByteBuf)binaryData);
    }

    public BinaryWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super((boolean)finalFragment, (int)rsv, (ByteBuf)binaryData);
    }

    @Override
    public BinaryWebSocketFrame copy() {
        return (BinaryWebSocketFrame)super.copy();
    }

    @Override
    public BinaryWebSocketFrame duplicate() {
        return (BinaryWebSocketFrame)super.duplicate();
    }

    @Override
    public BinaryWebSocketFrame retainedDuplicate() {
        return (BinaryWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public BinaryWebSocketFrame replace(ByteBuf content) {
        return new BinaryWebSocketFrame((boolean)this.isFinalFragment(), (int)this.rsv(), (ByteBuf)content);
    }

    @Override
    public BinaryWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public BinaryWebSocketFrame retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public BinaryWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public BinaryWebSocketFrame touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

