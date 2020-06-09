/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCounted;
import java.nio.charset.Charset;

public class CloseWebSocketFrame
extends WebSocketFrame {
    public CloseWebSocketFrame() {
        super((ByteBuf)Unpooled.buffer((int)0));
    }

    public CloseWebSocketFrame(WebSocketCloseStatus status) {
        this((int)status.code(), (String)status.reasonText());
    }

    public CloseWebSocketFrame(WebSocketCloseStatus status, String reasonText) {
        this((int)status.code(), (String)reasonText);
    }

    public CloseWebSocketFrame(int statusCode, String reasonText) {
        this((boolean)true, (int)0, (int)statusCode, (String)reasonText);
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv) {
        this((boolean)finalFragment, (int)rsv, (ByteBuf)Unpooled.buffer((int)0));
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv, int statusCode, String reasonText) {
        super((boolean)finalFragment, (int)rsv, (ByteBuf)CloseWebSocketFrame.newBinaryData((int)statusCode, (String)reasonText));
    }

    private static ByteBuf newBinaryData(int statusCode, String reasonText) {
        if (reasonText == null) {
            reasonText = "";
        }
        ByteBuf binaryData = Unpooled.buffer((int)(2 + reasonText.length()));
        binaryData.writeShort((int)statusCode);
        if (!reasonText.isEmpty()) {
            binaryData.writeCharSequence((CharSequence)reasonText, (Charset)CharsetUtil.UTF_8);
        }
        binaryData.readerIndex((int)0);
        return binaryData;
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super((boolean)finalFragment, (int)rsv, (ByteBuf)binaryData);
    }

    public int statusCode() {
        ByteBuf binaryData = this.content();
        if (binaryData == null) return -1;
        if (binaryData.capacity() == 0) {
            return -1;
        }
        binaryData.readerIndex((int)0);
        return binaryData.getShort((int)0);
    }

    public String reasonText() {
        ByteBuf binaryData = this.content();
        if (binaryData == null) return "";
        if (binaryData.capacity() <= 2) {
            return "";
        }
        binaryData.readerIndex((int)2);
        String reasonText = binaryData.toString((Charset)CharsetUtil.UTF_8);
        binaryData.readerIndex((int)0);
        return reasonText;
    }

    @Override
    public CloseWebSocketFrame copy() {
        return (CloseWebSocketFrame)super.copy();
    }

    @Override
    public CloseWebSocketFrame duplicate() {
        return (CloseWebSocketFrame)super.duplicate();
    }

    @Override
    public CloseWebSocketFrame retainedDuplicate() {
        return (CloseWebSocketFrame)super.retainedDuplicate();
    }

    @Override
    public CloseWebSocketFrame replace(ByteBuf content) {
        return new CloseWebSocketFrame((boolean)this.isFinalFragment(), (int)this.rsv(), (ByteBuf)content);
    }

    @Override
    public CloseWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public CloseWebSocketFrame retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public CloseWebSocketFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public CloseWebSocketFrame touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

