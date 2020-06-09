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

public class TextWebSocketFrame
extends WebSocketFrame {
    public TextWebSocketFrame() {
        super((ByteBuf)Unpooled.buffer((int)0));
    }

    public TextWebSocketFrame(String text) {
        super((ByteBuf)TextWebSocketFrame.fromText((String)text));
    }

    public TextWebSocketFrame(ByteBuf binaryData) {
        super((ByteBuf)binaryData);
    }

    public TextWebSocketFrame(boolean finalFragment, int rsv, String text) {
        super((boolean)finalFragment, (int)rsv, (ByteBuf)TextWebSocketFrame.fromText((String)text));
    }

    private static ByteBuf fromText(String text) {
        if (text == null) return Unpooled.EMPTY_BUFFER;
        if (!text.isEmpty()) return Unpooled.copiedBuffer((CharSequence)text, (Charset)CharsetUtil.UTF_8);
        return Unpooled.EMPTY_BUFFER;
    }

    public TextWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super((boolean)finalFragment, (int)rsv, (ByteBuf)binaryData);
    }

    public String text() {
        return this.content().toString((Charset)CharsetUtil.UTF_8);
    }

    @Override
    public TextWebSocketFrame copy() {
        return (TextWebSocketFrame)super.copy();
    }

    @Override
    public TextWebSocketFrame duplicate() {
        return (TextWebSocketFrame)super.duplicate();
    }

    @Override
    public TextWebSocketFrame retainedDuplicate() {
        return (TextWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public TextWebSocketFrame replace(ByteBuf content) {
        return new TextWebSocketFrame((boolean)this.isFinalFragment(), (int)this.rsv(), (ByteBuf)content);
    }

    @Override
    public TextWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public TextWebSocketFrame retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public TextWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public TextWebSocketFrame touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

