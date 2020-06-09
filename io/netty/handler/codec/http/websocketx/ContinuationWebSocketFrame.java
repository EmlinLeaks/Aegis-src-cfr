/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCounted;
import java.nio.charset.Charset;

public class ContinuationWebSocketFrame
extends WebSocketFrame {
    public ContinuationWebSocketFrame() {
        this((ByteBuf)Unpooled.buffer((int)0));
    }

    public ContinuationWebSocketFrame(ByteBuf binaryData) {
        super((ByteBuf)binaryData);
    }

    public ContinuationWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super((boolean)finalFragment, (int)rsv, (ByteBuf)binaryData);
    }

    public ContinuationWebSocketFrame(boolean finalFragment, int rsv, String text) {
        this((boolean)finalFragment, (int)rsv, (ByteBuf)ContinuationWebSocketFrame.fromText((String)text));
    }

    public String text() {
        return this.content().toString((Charset)CharsetUtil.UTF_8);
    }

    private static ByteBuf fromText(String text) {
        if (text == null) return Unpooled.EMPTY_BUFFER;
        if (!text.isEmpty()) return Unpooled.copiedBuffer((CharSequence)text, (Charset)CharsetUtil.UTF_8);
        return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public ContinuationWebSocketFrame copy() {
        return (ContinuationWebSocketFrame)super.copy();
    }

    @Override
    public ContinuationWebSocketFrame duplicate() {
        return (ContinuationWebSocketFrame)super.duplicate();
    }

    @Override
    public ContinuationWebSocketFrame retainedDuplicate() {
        return (ContinuationWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public ContinuationWebSocketFrame replace(ByteBuf content) {
        return new ContinuationWebSocketFrame((boolean)this.isFinalFragment(), (int)this.rsv(), (ByteBuf)content);
    }

    @Override
    public ContinuationWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public ContinuationWebSocketFrame retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public ContinuationWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public ContinuationWebSocketFrame touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

