/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class WebSocket00FrameEncoder
extends MessageToMessageEncoder<WebSocketFrame>
implements WebSocketFrameEncoder {
    private static final ByteBuf _0X00 = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.directBuffer((int)1, (int)1).writeByte((int)0));
    private static final ByteBuf _0XFF = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.directBuffer((int)1, (int)1).writeByte((int)-1));
    private static final ByteBuf _0XFF_0X00 = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.directBuffer((int)2, (int)2).writeByte((int)-1).writeByte((int)0));

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        if (msg instanceof TextWebSocketFrame) {
            ByteBuf data = msg.content();
            out.add((Object)_0X00.duplicate());
            out.add((Object)data.retain());
            out.add((Object)_0XFF.duplicate());
            return;
        }
        if (msg instanceof CloseWebSocketFrame) {
            out.add((Object)_0XFF_0X00.duplicate());
            return;
        }
        ByteBuf data = msg.content();
        int dataLen = data.readableBytes();
        ByteBuf buf = ctx.alloc().buffer((int)5);
        boolean release = true;
        try {
            buf.writeByte((int)-128);
            int b1 = dataLen >>> 28 & 127;
            int b2 = dataLen >>> 14 & 127;
            int b3 = dataLen >>> 7 & 127;
            int b4 = dataLen & 127;
            if (b1 == 0) {
                if (b2 == 0) {
                    if (b3 == 0) {
                        buf.writeByte((int)b4);
                    } else {
                        buf.writeByte((int)(b3 | 128));
                        buf.writeByte((int)b4);
                    }
                } else {
                    buf.writeByte((int)(b2 | 128));
                    buf.writeByte((int)(b3 | 128));
                    buf.writeByte((int)b4);
                }
            } else {
                buf.writeByte((int)(b1 | 128));
                buf.writeByte((int)(b2 | 128));
                buf.writeByte((int)(b3 | 128));
                buf.writeByte((int)b4);
            }
            out.add((Object)buf);
            out.add((Object)data.retain());
            release = false;
            return;
        }
        finally {
            if (release) {
                buf.release();
            }
        }
    }
}

