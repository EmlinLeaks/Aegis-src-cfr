/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.spdy.DefaultSpdyDataFrame;
import io.netty.handler.codec.spdy.DefaultSpdyGoAwayFrame;
import io.netty.handler.codec.spdy.DefaultSpdyHeadersFrame;
import io.netty.handler.codec.spdy.DefaultSpdyPingFrame;
import io.netty.handler.codec.spdy.DefaultSpdyRstStreamFrame;
import io.netty.handler.codec.spdy.DefaultSpdySettingsFrame;
import io.netty.handler.codec.spdy.DefaultSpdySynReplyFrame;
import io.netty.handler.codec.spdy.DefaultSpdySynStreamFrame;
import io.netty.handler.codec.spdy.DefaultSpdyWindowUpdateFrame;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyFrameCodec;
import io.netty.handler.codec.spdy.SpdyFrameDecoder;
import io.netty.handler.codec.spdy.SpdyFrameDecoderDelegate;
import io.netty.handler.codec.spdy.SpdyFrameEncoder;
import io.netty.handler.codec.spdy.SpdyGoAwayFrame;
import io.netty.handler.codec.spdy.SpdyHeaderBlockDecoder;
import io.netty.handler.codec.spdy.SpdyHeaderBlockEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyPingFrame;
import io.netty.handler.codec.spdy.SpdyProtocolException;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.handler.codec.spdy.SpdySessionStatus;
import io.netty.handler.codec.spdy.SpdySettingsFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;
import io.netty.handler.codec.spdy.SpdySynReplyFrame;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.handler.codec.spdy.SpdyWindowUpdateFrame;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.List;

public class SpdyFrameCodec
extends ByteToMessageDecoder
implements SpdyFrameDecoderDelegate,
ChannelOutboundHandler {
    private static final SpdyProtocolException INVALID_FRAME = new SpdyProtocolException((String)"Received invalid frame");
    private final SpdyFrameDecoder spdyFrameDecoder;
    private final SpdyFrameEncoder spdyFrameEncoder;
    private final SpdyHeaderBlockDecoder spdyHeaderBlockDecoder;
    private final SpdyHeaderBlockEncoder spdyHeaderBlockEncoder;
    private SpdyHeadersFrame spdyHeadersFrame;
    private SpdySettingsFrame spdySettingsFrame;
    private ChannelHandlerContext ctx;
    private boolean read;
    private final boolean validateHeaders;

    public SpdyFrameCodec(SpdyVersion version) {
        this((SpdyVersion)version, (boolean)true);
    }

    public SpdyFrameCodec(SpdyVersion version, boolean validateHeaders) {
        this((SpdyVersion)version, (int)8192, (int)16384, (int)6, (int)15, (int)8, (boolean)validateHeaders);
    }

    public SpdyFrameCodec(SpdyVersion version, int maxChunkSize, int maxHeaderSize, int compressionLevel, int windowBits, int memLevel) {
        this((SpdyVersion)version, (int)maxChunkSize, (int)maxHeaderSize, (int)compressionLevel, (int)windowBits, (int)memLevel, (boolean)true);
    }

    public SpdyFrameCodec(SpdyVersion version, int maxChunkSize, int maxHeaderSize, int compressionLevel, int windowBits, int memLevel, boolean validateHeaders) {
        this((SpdyVersion)version, (int)maxChunkSize, (SpdyHeaderBlockDecoder)SpdyHeaderBlockDecoder.newInstance((SpdyVersion)version, (int)maxHeaderSize), (SpdyHeaderBlockEncoder)SpdyHeaderBlockEncoder.newInstance((SpdyVersion)version, (int)compressionLevel, (int)windowBits, (int)memLevel), (boolean)validateHeaders);
    }

    protected SpdyFrameCodec(SpdyVersion version, int maxChunkSize, SpdyHeaderBlockDecoder spdyHeaderBlockDecoder, SpdyHeaderBlockEncoder spdyHeaderBlockEncoder, boolean validateHeaders) {
        this.spdyFrameDecoder = new SpdyFrameDecoder((SpdyVersion)version, (SpdyFrameDecoderDelegate)this, (int)maxChunkSize);
        this.spdyFrameEncoder = new SpdyFrameEncoder((SpdyVersion)version);
        this.spdyHeaderBlockDecoder = spdyHeaderBlockDecoder;
        this.spdyHeaderBlockEncoder = spdyHeaderBlockEncoder;
        this.validateHeaders = validateHeaders;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded((ChannelHandlerContext)ctx);
        this.ctx = ctx;
        ctx.channel().closeFuture().addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((SpdyFrameCodec)this){
            final /* synthetic */ SpdyFrameCodec this$0;
            {
                this.this$0 = this$0;
            }

            public void operationComplete(ChannelFuture future) throws Exception {
                SpdyFrameCodec.access$000((SpdyFrameCodec)this.this$0).end();
                SpdyFrameCodec.access$100((SpdyFrameCodec)this.this$0).end();
            }
        });
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.spdyFrameDecoder.decode((ByteBuf)in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (!this.read && !ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
        this.read = false;
        super.channelReadComplete((ChannelHandlerContext)ctx);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind((SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect((ChannelPromise)promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close((ChannelPromise)promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister((ChannelPromise)promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf frame;
        if (msg instanceof SpdyDataFrame) {
            SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
            ByteBuf frame2 = this.spdyFrameEncoder.encodeDataFrame((ByteBufAllocator)ctx.alloc(), (int)spdyDataFrame.streamId(), (boolean)spdyDataFrame.isLast(), (ByteBuf)spdyDataFrame.content());
            spdyDataFrame.release();
            ctx.write((Object)frame2, (ChannelPromise)promise);
            return;
        }
        if (msg instanceof SpdySynStreamFrame) {
            ByteBuf frame3;
            SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
            ByteBuf headerBlock = this.spdyHeaderBlockEncoder.encode((ByteBufAllocator)ctx.alloc(), (SpdyHeadersFrame)spdySynStreamFrame);
            try {
                frame3 = this.spdyFrameEncoder.encodeSynStreamFrame((ByteBufAllocator)ctx.alloc(), (int)spdySynStreamFrame.streamId(), (int)spdySynStreamFrame.associatedStreamId(), (byte)spdySynStreamFrame.priority(), (boolean)spdySynStreamFrame.isLast(), (boolean)spdySynStreamFrame.isUnidirectional(), (ByteBuf)headerBlock);
            }
            finally {
                headerBlock.release();
            }
            ctx.write((Object)frame3, (ChannelPromise)promise);
            return;
        }
        if (msg instanceof SpdySynReplyFrame) {
            ByteBuf frame4;
            SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
            ByteBuf headerBlock = this.spdyHeaderBlockEncoder.encode((ByteBufAllocator)ctx.alloc(), (SpdyHeadersFrame)spdySynReplyFrame);
            try {
                frame4 = this.spdyFrameEncoder.encodeSynReplyFrame((ByteBufAllocator)ctx.alloc(), (int)spdySynReplyFrame.streamId(), (boolean)spdySynReplyFrame.isLast(), (ByteBuf)headerBlock);
            }
            finally {
                headerBlock.release();
            }
            ctx.write((Object)frame4, (ChannelPromise)promise);
            return;
        }
        if (msg instanceof SpdyRstStreamFrame) {
            SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
            ByteBuf frame5 = this.spdyFrameEncoder.encodeRstStreamFrame((ByteBufAllocator)ctx.alloc(), (int)spdyRstStreamFrame.streamId(), (int)spdyRstStreamFrame.status().code());
            ctx.write((Object)frame5, (ChannelPromise)promise);
            return;
        }
        if (msg instanceof SpdySettingsFrame) {
            SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
            ByteBuf frame6 = this.spdyFrameEncoder.encodeSettingsFrame((ByteBufAllocator)ctx.alloc(), (SpdySettingsFrame)spdySettingsFrame);
            ctx.write((Object)frame6, (ChannelPromise)promise);
            return;
        }
        if (msg instanceof SpdyPingFrame) {
            SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
            ByteBuf frame7 = this.spdyFrameEncoder.encodePingFrame((ByteBufAllocator)ctx.alloc(), (int)spdyPingFrame.id());
            ctx.write((Object)frame7, (ChannelPromise)promise);
            return;
        }
        if (msg instanceof SpdyGoAwayFrame) {
            SpdyGoAwayFrame spdyGoAwayFrame = (SpdyGoAwayFrame)msg;
            ByteBuf frame8 = this.spdyFrameEncoder.encodeGoAwayFrame((ByteBufAllocator)ctx.alloc(), (int)spdyGoAwayFrame.lastGoodStreamId(), (int)spdyGoAwayFrame.status().code());
            ctx.write((Object)frame8, (ChannelPromise)promise);
            return;
        }
        if (!(msg instanceof SpdyHeadersFrame)) {
            if (!(msg instanceof SpdyWindowUpdateFrame)) throw new UnsupportedMessageTypeException((Object)msg, new Class[0]);
            SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame)msg;
            ByteBuf frame9 = this.spdyFrameEncoder.encodeWindowUpdateFrame((ByteBufAllocator)ctx.alloc(), (int)spdyWindowUpdateFrame.streamId(), (int)spdyWindowUpdateFrame.deltaWindowSize());
            ctx.write((Object)frame9, (ChannelPromise)promise);
            return;
        }
        SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
        ByteBuf headerBlock = this.spdyHeaderBlockEncoder.encode((ByteBufAllocator)ctx.alloc(), (SpdyHeadersFrame)spdyHeadersFrame);
        try {
            frame = this.spdyFrameEncoder.encodeHeadersFrame((ByteBufAllocator)ctx.alloc(), (int)spdyHeadersFrame.streamId(), (boolean)spdyHeadersFrame.isLast(), (ByteBuf)headerBlock);
        }
        finally {
            headerBlock.release();
        }
        ctx.write((Object)frame, (ChannelPromise)promise);
    }

    @Override
    public void readDataFrame(int streamId, boolean last, ByteBuf data) {
        this.read = true;
        DefaultSpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame((int)streamId, (ByteBuf)data);
        spdyDataFrame.setLast((boolean)last);
        this.ctx.fireChannelRead((Object)spdyDataFrame);
    }

    @Override
    public void readSynStreamFrame(int streamId, int associatedToStreamId, byte priority, boolean last, boolean unidirectional) {
        DefaultSpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame((int)streamId, (int)associatedToStreamId, (byte)priority, (boolean)this.validateHeaders);
        spdySynStreamFrame.setLast((boolean)last);
        spdySynStreamFrame.setUnidirectional((boolean)unidirectional);
        this.spdyHeadersFrame = spdySynStreamFrame;
    }

    @Override
    public void readSynReplyFrame(int streamId, boolean last) {
        DefaultSpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame((int)streamId, (boolean)this.validateHeaders);
        spdySynReplyFrame.setLast((boolean)last);
        this.spdyHeadersFrame = spdySynReplyFrame;
    }

    @Override
    public void readRstStreamFrame(int streamId, int statusCode) {
        this.read = true;
        DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (int)statusCode);
        this.ctx.fireChannelRead((Object)spdyRstStreamFrame);
    }

    @Override
    public void readSettingsFrame(boolean clearPersisted) {
        this.read = true;
        this.spdySettingsFrame = new DefaultSpdySettingsFrame();
        this.spdySettingsFrame.setClearPreviouslyPersistedSettings((boolean)clearPersisted);
    }

    @Override
    public void readSetting(int id, int value, boolean persistValue, boolean persisted) {
        this.spdySettingsFrame.setValue((int)id, (int)value, (boolean)persistValue, (boolean)persisted);
    }

    @Override
    public void readSettingsEnd() {
        this.read = true;
        SpdySettingsFrame frame = this.spdySettingsFrame;
        this.spdySettingsFrame = null;
        this.ctx.fireChannelRead((Object)frame);
    }

    @Override
    public void readPingFrame(int id) {
        this.read = true;
        DefaultSpdyPingFrame spdyPingFrame = new DefaultSpdyPingFrame((int)id);
        this.ctx.fireChannelRead((Object)spdyPingFrame);
    }

    @Override
    public void readGoAwayFrame(int lastGoodStreamId, int statusCode) {
        this.read = true;
        DefaultSpdyGoAwayFrame spdyGoAwayFrame = new DefaultSpdyGoAwayFrame((int)lastGoodStreamId, (int)statusCode);
        this.ctx.fireChannelRead((Object)spdyGoAwayFrame);
    }

    @Override
    public void readHeadersFrame(int streamId, boolean last) {
        this.spdyHeadersFrame = new DefaultSpdyHeadersFrame((int)streamId, (boolean)this.validateHeaders);
        this.spdyHeadersFrame.setLast((boolean)last);
    }

    @Override
    public void readWindowUpdateFrame(int streamId, int deltaWindowSize) {
        this.read = true;
        DefaultSpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame((int)streamId, (int)deltaWindowSize);
        this.ctx.fireChannelRead((Object)spdyWindowUpdateFrame);
    }

    @Override
    public void readHeaderBlock(ByteBuf headerBlock) {
        try {
            this.spdyHeaderBlockDecoder.decode((ByteBufAllocator)this.ctx.alloc(), (ByteBuf)headerBlock, (SpdyHeadersFrame)this.spdyHeadersFrame);
            return;
        }
        catch (Exception e) {
            this.ctx.fireExceptionCaught((Throwable)e);
            return;
        }
        finally {
            headerBlock.release();
        }
    }

    @Override
    public void readHeaderBlockEnd() {
        SpdyHeadersFrame frame = null;
        try {
            this.spdyHeaderBlockDecoder.endHeaderBlock((SpdyHeadersFrame)this.spdyHeadersFrame);
            frame = this.spdyHeadersFrame;
            this.spdyHeadersFrame = null;
        }
        catch (Exception e) {
            this.ctx.fireExceptionCaught((Throwable)e);
        }
        if (frame == null) return;
        this.read = true;
        this.ctx.fireChannelRead((Object)frame);
    }

    @Override
    public void readFrameError(String message) {
        this.ctx.fireExceptionCaught((Throwable)INVALID_FRAME);
    }

    static /* synthetic */ SpdyHeaderBlockDecoder access$000(SpdyFrameCodec x0) {
        return x0.spdyHeaderBlockDecoder;
    }

    static /* synthetic */ SpdyHeaderBlockEncoder access$100(SpdyFrameCodec x0) {
        return x0.spdyHeaderBlockEncoder;
    }
}

