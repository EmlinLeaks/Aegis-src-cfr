/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.spdy.DefaultSpdyDataFrame;
import io.netty.handler.codec.spdy.DefaultSpdyGoAwayFrame;
import io.netty.handler.codec.spdy.DefaultSpdyRstStreamFrame;
import io.netty.handler.codec.spdy.DefaultSpdyWindowUpdateFrame;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyGoAwayFrame;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyPingFrame;
import io.netty.handler.codec.spdy.SpdyProtocolException;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.handler.codec.spdy.SpdySession;
import io.netty.handler.codec.spdy.SpdySessionHandler;
import io.netty.handler.codec.spdy.SpdySessionStatus;
import io.netty.handler.codec.spdy.SpdySettingsFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;
import io.netty.handler.codec.spdy.SpdySynReplyFrame;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.handler.codec.spdy.SpdyWindowUpdateFrame;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SpdySessionHandler
extends ChannelDuplexHandler {
    private static final SpdyProtocolException PROTOCOL_EXCEPTION = ThrowableUtil.unknownStackTrace(SpdyProtocolException.newStatic(null), SpdySessionHandler.class, (String)"handleOutboundMessage(...)");
    private static final SpdyProtocolException STREAM_CLOSED = ThrowableUtil.unknownStackTrace(SpdyProtocolException.newStatic((String)"Stream closed"), SpdySessionHandler.class, (String)"removeStream(...)");
    private static final int DEFAULT_WINDOW_SIZE = 65536;
    private int initialSendWindowSize = 65536;
    private int initialReceiveWindowSize = 65536;
    private volatile int initialSessionReceiveWindowSize = 65536;
    private final SpdySession spdySession = new SpdySession((int)this.initialSendWindowSize, (int)this.initialReceiveWindowSize);
    private int lastGoodStreamId;
    private static final int DEFAULT_MAX_CONCURRENT_STREAMS = Integer.MAX_VALUE;
    private int remoteConcurrentStreams = Integer.MAX_VALUE;
    private int localConcurrentStreams = Integer.MAX_VALUE;
    private final AtomicInteger pings = new AtomicInteger();
    private boolean sentGoAwayFrame;
    private boolean receivedGoAwayFrame;
    private ChannelFutureListener closeSessionFutureListener;
    private final boolean server;
    private final int minorVersion;

    public SpdySessionHandler(SpdyVersion version, boolean server) {
        if (version == null) {
            throw new NullPointerException((String)"version");
        }
        this.server = server;
        this.minorVersion = version.getMinorVersion();
    }

    public void setSessionReceiveWindowSize(int sessionReceiveWindowSize) {
        ObjectUtil.checkPositiveOrZero((int)sessionReceiveWindowSize, (String)"sessionReceiveWindowSize");
        this.initialSessionReceiveWindowSize = sessionReceiveWindowSize;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SpdyDataFrame) {
            SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
            int streamId = spdyDataFrame.streamId();
            int deltaWindowSize = -1 * spdyDataFrame.content().readableBytes();
            int newSessionWindowSize = this.spdySession.updateReceiveWindowSize((int)0, (int)deltaWindowSize);
            if (newSessionWindowSize < 0) {
                this.issueSessionError((ChannelHandlerContext)ctx, (SpdySessionStatus)SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
            if (newSessionWindowSize <= this.initialSessionReceiveWindowSize / 2) {
                int sessionDeltaWindowSize = this.initialSessionReceiveWindowSize - newSessionWindowSize;
                this.spdySession.updateReceiveWindowSize((int)0, (int)sessionDeltaWindowSize);
                DefaultSpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame((int)0, (int)sessionDeltaWindowSize);
                ctx.writeAndFlush((Object)spdyWindowUpdateFrame);
            }
            if (!this.spdySession.isActiveStream((int)streamId)) {
                spdyDataFrame.release();
                if (streamId <= this.lastGoodStreamId) {
                    this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.PROTOCOL_ERROR);
                    return;
                }
                if (this.sentGoAwayFrame) return;
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.INVALID_STREAM);
                return;
            }
            if (this.spdySession.isRemoteSideClosed((int)streamId)) {
                spdyDataFrame.release();
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.STREAM_ALREADY_CLOSED);
                return;
            }
            if (!this.isRemoteInitiatedId((int)streamId) && !this.spdySession.hasReceivedReply((int)streamId)) {
                spdyDataFrame.release();
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }
            int newWindowSize = this.spdySession.updateReceiveWindowSize((int)streamId, (int)deltaWindowSize);
            if (newWindowSize < this.spdySession.getReceiveWindowSizeLowerBound((int)streamId)) {
                spdyDataFrame.release();
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.FLOW_CONTROL_ERROR);
                return;
            }
            if (newWindowSize < 0) {
                while (spdyDataFrame.content().readableBytes() > this.initialReceiveWindowSize) {
                    DefaultSpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame((int)streamId, (ByteBuf)spdyDataFrame.content().readRetainedSlice((int)this.initialReceiveWindowSize));
                    ctx.writeAndFlush((Object)partialDataFrame);
                }
            }
            if (newWindowSize <= this.initialReceiveWindowSize / 2 && !spdyDataFrame.isLast()) {
                int streamDeltaWindowSize = this.initialReceiveWindowSize - newWindowSize;
                this.spdySession.updateReceiveWindowSize((int)streamId, (int)streamDeltaWindowSize);
                DefaultSpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame((int)streamId, (int)streamDeltaWindowSize);
                ctx.writeAndFlush((Object)spdyWindowUpdateFrame);
            }
            if (spdyDataFrame.isLast()) {
                this.halfCloseStream((int)streamId, (boolean)true, (ChannelFuture)ctx.newSucceededFuture());
            }
        } else if (msg instanceof SpdySynStreamFrame) {
            boolean localSideClosed;
            boolean remoteSideClosed;
            SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
            int streamId = spdySynStreamFrame.streamId();
            if (spdySynStreamFrame.isInvalid() || !this.isRemoteInitiatedId((int)streamId) || this.spdySession.isActiveStream((int)streamId)) {
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }
            if (streamId <= this.lastGoodStreamId) {
                this.issueSessionError((ChannelHandlerContext)ctx, (SpdySessionStatus)SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
            byte priority = spdySynStreamFrame.priority();
            if (!this.acceptStream((int)streamId, (byte)priority, (boolean)(remoteSideClosed = spdySynStreamFrame.isLast()), (boolean)(localSideClosed = spdySynStreamFrame.isUnidirectional()))) {
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.REFUSED_STREAM);
                return;
            }
        } else if (msg instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
            int streamId = spdySynReplyFrame.streamId();
            if (spdySynReplyFrame.isInvalid() || this.isRemoteInitiatedId((int)streamId) || this.spdySession.isRemoteSideClosed((int)streamId)) {
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.INVALID_STREAM);
                return;
            }
            if (this.spdySession.hasReceivedReply((int)streamId)) {
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.STREAM_IN_USE);
                return;
            }
            this.spdySession.receivedReply((int)streamId);
            if (spdySynReplyFrame.isLast()) {
                this.halfCloseStream((int)streamId, (boolean)true, (ChannelFuture)ctx.newSucceededFuture());
            }
        } else if (msg instanceof SpdyRstStreamFrame) {
            SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
            this.removeStream((int)spdyRstStreamFrame.streamId(), (ChannelFuture)ctx.newSucceededFuture());
        } else if (msg instanceof SpdySettingsFrame) {
            SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
            int settingsMinorVersion = spdySettingsFrame.getValue((int)0);
            if (settingsMinorVersion >= 0 && settingsMinorVersion != this.minorVersion) {
                this.issueSessionError((ChannelHandlerContext)ctx, (SpdySessionStatus)SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
            int newConcurrentStreams = spdySettingsFrame.getValue((int)4);
            if (newConcurrentStreams >= 0) {
                this.remoteConcurrentStreams = newConcurrentStreams;
            }
            if (spdySettingsFrame.isPersisted((int)7)) {
                spdySettingsFrame.removeValue((int)7);
            }
            spdySettingsFrame.setPersistValue((int)7, (boolean)false);
            int newInitialWindowSize = spdySettingsFrame.getValue((int)7);
            if (newInitialWindowSize >= 0) {
                this.updateInitialSendWindowSize((int)newInitialWindowSize);
            }
        } else if (msg instanceof SpdyPingFrame) {
            SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
            if (this.isRemoteInitiatedId((int)spdyPingFrame.id())) {
                ctx.writeAndFlush((Object)spdyPingFrame);
                return;
            }
            if (this.pings.get() == 0) {
                return;
            }
            this.pings.getAndDecrement();
        } else if (msg instanceof SpdyGoAwayFrame) {
            this.receivedGoAwayFrame = true;
        } else if (msg instanceof SpdyHeadersFrame) {
            SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
            int streamId = spdyHeadersFrame.streamId();
            if (spdyHeadersFrame.isInvalid()) {
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }
            if (this.spdySession.isRemoteSideClosed((int)streamId)) {
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.INVALID_STREAM);
                return;
            }
            if (spdyHeadersFrame.isLast()) {
                this.halfCloseStream((int)streamId, (boolean)true, (ChannelFuture)ctx.newSucceededFuture());
            }
        } else if (msg instanceof SpdyWindowUpdateFrame) {
            SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame)msg;
            int streamId = spdyWindowUpdateFrame.streamId();
            int deltaWindowSize = spdyWindowUpdateFrame.deltaWindowSize();
            if (streamId != 0 && this.spdySession.isLocalSideClosed((int)streamId)) {
                return;
            }
            if (this.spdySession.getSendWindowSize((int)streamId) > Integer.MAX_VALUE - deltaWindowSize) {
                if (streamId == 0) {
                    this.issueSessionError((ChannelHandlerContext)ctx, (SpdySessionStatus)SpdySessionStatus.PROTOCOL_ERROR);
                    return;
                }
                this.issueStreamError((ChannelHandlerContext)ctx, (int)streamId, (SpdyStreamStatus)SpdyStreamStatus.FLOW_CONTROL_ERROR);
                return;
            }
            this.updateSendWindowSize((ChannelHandlerContext)ctx, (int)streamId, (int)deltaWindowSize);
        }
        ctx.fireChannelRead((Object)msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Iterator<Integer> iterator = this.spdySession.activeStreams().keySet().iterator();
        do {
            if (!iterator.hasNext()) {
                ctx.fireChannelInactive();
                return;
            }
            Integer streamId = iterator.next();
            this.removeStream((int)streamId.intValue(), (ChannelFuture)ctx.newSucceededFuture());
        } while (true);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof SpdyProtocolException) {
            this.issueSessionError((ChannelHandlerContext)ctx, (SpdySessionStatus)SpdySessionStatus.PROTOCOL_ERROR);
        }
        ctx.fireExceptionCaught((Throwable)cause);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.sendGoAwayFrame((ChannelHandlerContext)ctx, (ChannelPromise)promise);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof SpdyDataFrame || msg instanceof SpdySynStreamFrame || msg instanceof SpdySynReplyFrame || msg instanceof SpdyRstStreamFrame || msg instanceof SpdySettingsFrame || msg instanceof SpdyPingFrame || msg instanceof SpdyGoAwayFrame || msg instanceof SpdyHeadersFrame || msg instanceof SpdyWindowUpdateFrame)) {
            ctx.write((Object)msg, (ChannelPromise)promise);
            return;
        }
        this.handleOutboundMessage((ChannelHandlerContext)ctx, (Object)msg, (ChannelPromise)promise);
    }

    private void handleOutboundMessage(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof SpdyDataFrame) {
            SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
            int streamId = spdyDataFrame.streamId();
            if (this.spdySession.isLocalSideClosed((int)streamId)) {
                spdyDataFrame.release();
                promise.setFailure((Throwable)PROTOCOL_EXCEPTION);
                return;
            }
            int dataLength = spdyDataFrame.content().readableBytes();
            int sendWindowSize = this.spdySession.getSendWindowSize((int)streamId);
            int sessionSendWindowSize = this.spdySession.getSendWindowSize((int)0);
            if ((sendWindowSize = Math.min((int)sendWindowSize, (int)sessionSendWindowSize)) <= 0) {
                this.spdySession.putPendingWrite((int)streamId, (SpdySession.PendingWrite)new SpdySession.PendingWrite((SpdyDataFrame)spdyDataFrame, (ChannelPromise)promise));
                return;
            }
            if (sendWindowSize < dataLength) {
                this.spdySession.updateSendWindowSize((int)streamId, (int)(-1 * sendWindowSize));
                this.spdySession.updateSendWindowSize((int)0, (int)(-1 * sendWindowSize));
                DefaultSpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame((int)streamId, (ByteBuf)spdyDataFrame.content().readRetainedSlice((int)sendWindowSize));
                this.spdySession.putPendingWrite((int)streamId, (SpdySession.PendingWrite)new SpdySession.PendingWrite((SpdyDataFrame)spdyDataFrame, (ChannelPromise)promise));
                ChannelHandlerContext context = ctx;
                ctx.write((Object)partialDataFrame).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((SpdySessionHandler)this, (ChannelHandlerContext)context){
                    final /* synthetic */ ChannelHandlerContext val$context;
                    final /* synthetic */ SpdySessionHandler this$0;
                    {
                        this.this$0 = this$0;
                        this.val$context = channelHandlerContext;
                    }

                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) return;
                        SpdySessionHandler.access$000((SpdySessionHandler)this.this$0, (ChannelHandlerContext)this.val$context, (SpdySessionStatus)SpdySessionStatus.INTERNAL_ERROR);
                    }
                });
                return;
            }
            this.spdySession.updateSendWindowSize((int)streamId, (int)(-1 * dataLength));
            this.spdySession.updateSendWindowSize((int)0, (int)(-1 * dataLength));
            ChannelHandlerContext context = ctx;
            promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((SpdySessionHandler)this, (ChannelHandlerContext)context){
                final /* synthetic */ ChannelHandlerContext val$context;
                final /* synthetic */ SpdySessionHandler this$0;
                {
                    this.this$0 = this$0;
                    this.val$context = channelHandlerContext;
                }

                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) return;
                    SpdySessionHandler.access$000((SpdySessionHandler)this.this$0, (ChannelHandlerContext)this.val$context, (SpdySessionStatus)SpdySessionStatus.INTERNAL_ERROR);
                }
            });
            if (spdyDataFrame.isLast()) {
                this.halfCloseStream((int)streamId, (boolean)false, (ChannelFuture)promise);
            }
        } else if (msg instanceof SpdySynStreamFrame) {
            boolean localSideClosed;
            boolean remoteSideClosed;
            SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
            int streamId = spdySynStreamFrame.streamId();
            if (this.isRemoteInitiatedId((int)streamId)) {
                promise.setFailure((Throwable)PROTOCOL_EXCEPTION);
                return;
            }
            byte priority = spdySynStreamFrame.priority();
            if (!this.acceptStream((int)streamId, (byte)priority, (boolean)(remoteSideClosed = spdySynStreamFrame.isUnidirectional()), (boolean)(localSideClosed = spdySynStreamFrame.isLast()))) {
                promise.setFailure((Throwable)PROTOCOL_EXCEPTION);
                return;
            }
        } else if (msg instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
            int streamId = spdySynReplyFrame.streamId();
            if (!this.isRemoteInitiatedId((int)streamId) || this.spdySession.isLocalSideClosed((int)streamId)) {
                promise.setFailure((Throwable)PROTOCOL_EXCEPTION);
                return;
            }
            if (spdySynReplyFrame.isLast()) {
                this.halfCloseStream((int)streamId, (boolean)false, (ChannelFuture)promise);
            }
        } else if (msg instanceof SpdyRstStreamFrame) {
            SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
            this.removeStream((int)spdyRstStreamFrame.streamId(), (ChannelFuture)promise);
        } else if (msg instanceof SpdySettingsFrame) {
            SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
            int settingsMinorVersion = spdySettingsFrame.getValue((int)0);
            if (settingsMinorVersion >= 0 && settingsMinorVersion != this.minorVersion) {
                promise.setFailure((Throwable)PROTOCOL_EXCEPTION);
                return;
            }
            int newConcurrentStreams = spdySettingsFrame.getValue((int)4);
            if (newConcurrentStreams >= 0) {
                this.localConcurrentStreams = newConcurrentStreams;
            }
            if (spdySettingsFrame.isPersisted((int)7)) {
                spdySettingsFrame.removeValue((int)7);
            }
            spdySettingsFrame.setPersistValue((int)7, (boolean)false);
            int newInitialWindowSize = spdySettingsFrame.getValue((int)7);
            if (newInitialWindowSize >= 0) {
                this.updateInitialReceiveWindowSize((int)newInitialWindowSize);
            }
        } else if (msg instanceof SpdyPingFrame) {
            SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
            if (this.isRemoteInitiatedId((int)spdyPingFrame.id())) {
                ctx.fireExceptionCaught((Throwable)new IllegalArgumentException((String)("invalid PING ID: " + spdyPingFrame.id())));
                return;
            }
            this.pings.getAndIncrement();
        } else {
            if (msg instanceof SpdyGoAwayFrame) {
                promise.setFailure((Throwable)PROTOCOL_EXCEPTION);
                return;
            }
            if (msg instanceof SpdyHeadersFrame) {
                SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
                int streamId = spdyHeadersFrame.streamId();
                if (this.spdySession.isLocalSideClosed((int)streamId)) {
                    promise.setFailure((Throwable)PROTOCOL_EXCEPTION);
                    return;
                }
                if (spdyHeadersFrame.isLast()) {
                    this.halfCloseStream((int)streamId, (boolean)false, (ChannelFuture)promise);
                }
            } else if (msg instanceof SpdyWindowUpdateFrame) {
                promise.setFailure((Throwable)PROTOCOL_EXCEPTION);
                return;
            }
        }
        ctx.write((Object)msg, (ChannelPromise)promise);
    }

    private void issueSessionError(ChannelHandlerContext ctx, SpdySessionStatus status) {
        this.sendGoAwayFrame((ChannelHandlerContext)ctx, (SpdySessionStatus)status).addListener((GenericFutureListener<? extends Future<? super Void>>)new ClosingChannelFutureListener((ChannelHandlerContext)ctx, (ChannelPromise)ctx.newPromise()));
    }

    private void issueStreamError(ChannelHandlerContext ctx, int streamId, SpdyStreamStatus status) {
        boolean fireChannelRead = !this.spdySession.isRemoteSideClosed((int)streamId);
        ChannelPromise promise = ctx.newPromise();
        this.removeStream((int)streamId, (ChannelFuture)promise);
        DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)status);
        ctx.writeAndFlush((Object)spdyRstStreamFrame, (ChannelPromise)promise);
        if (!fireChannelRead) return;
        ctx.fireChannelRead((Object)spdyRstStreamFrame);
    }

    private boolean isRemoteInitiatedId(int id) {
        boolean serverId = SpdyCodecUtil.isServerId((int)id);
        if (this.server) {
            if (!serverId) return true;
        }
        if (this.server) return false;
        if (!serverId) return false;
        return true;
    }

    private void updateInitialSendWindowSize(int newInitialWindowSize) {
        int deltaWindowSize = newInitialWindowSize - this.initialSendWindowSize;
        this.initialSendWindowSize = newInitialWindowSize;
        this.spdySession.updateAllSendWindowSizes((int)deltaWindowSize);
    }

    private void updateInitialReceiveWindowSize(int newInitialWindowSize) {
        int deltaWindowSize = newInitialWindowSize - this.initialReceiveWindowSize;
        this.initialReceiveWindowSize = newInitialWindowSize;
        this.spdySession.updateAllReceiveWindowSizes((int)deltaWindowSize);
    }

    private boolean acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed) {
        int maxConcurrentStreams;
        if (this.receivedGoAwayFrame) return false;
        if (this.sentGoAwayFrame) {
            return false;
        }
        boolean remote = this.isRemoteInitiatedId((int)streamId);
        int n = maxConcurrentStreams = remote ? this.localConcurrentStreams : this.remoteConcurrentStreams;
        if (this.spdySession.numActiveStreams((boolean)remote) >= maxConcurrentStreams) {
            return false;
        }
        this.spdySession.acceptStream((int)streamId, (byte)priority, (boolean)remoteSideClosed, (boolean)localSideClosed, (int)this.initialSendWindowSize, (int)this.initialReceiveWindowSize, (boolean)remote);
        if (!remote) return true;
        this.lastGoodStreamId = streamId;
        return true;
    }

    private void halfCloseStream(int streamId, boolean remote, ChannelFuture future) {
        if (remote) {
            this.spdySession.closeRemoteSide((int)streamId, (boolean)this.isRemoteInitiatedId((int)streamId));
        } else {
            this.spdySession.closeLocalSide((int)streamId, (boolean)this.isRemoteInitiatedId((int)streamId));
        }
        if (this.closeSessionFutureListener == null) return;
        if (!this.spdySession.noActiveStreams()) return;
        future.addListener((GenericFutureListener<? extends Future<? super Void>>)this.closeSessionFutureListener);
    }

    private void removeStream(int streamId, ChannelFuture future) {
        this.spdySession.removeStream((int)streamId, (Throwable)STREAM_CLOSED, (boolean)this.isRemoteInitiatedId((int)streamId));
        if (this.closeSessionFutureListener == null) return;
        if (!this.spdySession.noActiveStreams()) return;
        future.addListener((GenericFutureListener<? extends Future<? super Void>>)this.closeSessionFutureListener);
    }

    private void updateSendWindowSize(ChannelHandlerContext ctx, int streamId, int deltaWindowSize) {
        this.spdySession.updateSendWindowSize((int)streamId, (int)deltaWindowSize);
        SpdySession.PendingWrite pendingWrite;
        while ((pendingWrite = this.spdySession.getPendingWrite((int)streamId)) != null) {
            SpdyDataFrame spdyDataFrame = pendingWrite.spdyDataFrame;
            int dataFrameSize = spdyDataFrame.content().readableBytes();
            int writeStreamId = spdyDataFrame.streamId();
            int sendWindowSize = this.spdySession.getSendWindowSize((int)writeStreamId);
            int sessionSendWindowSize = this.spdySession.getSendWindowSize((int)0);
            if ((sendWindowSize = Math.min((int)sendWindowSize, (int)sessionSendWindowSize)) <= 0) {
                return;
            }
            if (sendWindowSize < dataFrameSize) {
                this.spdySession.updateSendWindowSize((int)writeStreamId, (int)(-1 * sendWindowSize));
                this.spdySession.updateSendWindowSize((int)0, (int)(-1 * sendWindowSize));
                DefaultSpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame((int)writeStreamId, (ByteBuf)spdyDataFrame.content().readRetainedSlice((int)sendWindowSize));
                ctx.writeAndFlush((Object)partialDataFrame).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((SpdySessionHandler)this, (ChannelHandlerContext)ctx){
                    final /* synthetic */ ChannelHandlerContext val$ctx;
                    final /* synthetic */ SpdySessionHandler this$0;
                    {
                        this.this$0 = this$0;
                        this.val$ctx = channelHandlerContext;
                    }

                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) return;
                        SpdySessionHandler.access$000((SpdySessionHandler)this.this$0, (ChannelHandlerContext)this.val$ctx, (SpdySessionStatus)SpdySessionStatus.INTERNAL_ERROR);
                    }
                });
                continue;
            }
            this.spdySession.removePendingWrite((int)writeStreamId);
            this.spdySession.updateSendWindowSize((int)writeStreamId, (int)(-1 * dataFrameSize));
            this.spdySession.updateSendWindowSize((int)0, (int)(-1 * dataFrameSize));
            if (spdyDataFrame.isLast()) {
                this.halfCloseStream((int)writeStreamId, (boolean)false, (ChannelFuture)pendingWrite.promise);
            }
            ctx.writeAndFlush((Object)spdyDataFrame, (ChannelPromise)pendingWrite.promise).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((SpdySessionHandler)this, (ChannelHandlerContext)ctx){
                final /* synthetic */ ChannelHandlerContext val$ctx;
                final /* synthetic */ SpdySessionHandler this$0;
                {
                    this.this$0 = this$0;
                    this.val$ctx = channelHandlerContext;
                }

                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) return;
                    SpdySessionHandler.access$000((SpdySessionHandler)this.this$0, (ChannelHandlerContext)this.val$ctx, (SpdySessionStatus)SpdySessionStatus.INTERNAL_ERROR);
                }
            });
        }
        return;
    }

    private void sendGoAwayFrame(ChannelHandlerContext ctx, ChannelPromise future) {
        if (!ctx.channel().isActive()) {
            ctx.close((ChannelPromise)future);
            return;
        }
        ChannelFuture f = this.sendGoAwayFrame((ChannelHandlerContext)ctx, (SpdySessionStatus)SpdySessionStatus.OK);
        if (this.spdySession.noActiveStreams()) {
            f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ClosingChannelFutureListener((ChannelHandlerContext)ctx, (ChannelPromise)future));
            return;
        }
        this.closeSessionFutureListener = new ClosingChannelFutureListener((ChannelHandlerContext)ctx, (ChannelPromise)future);
    }

    private ChannelFuture sendGoAwayFrame(ChannelHandlerContext ctx, SpdySessionStatus status) {
        if (this.sentGoAwayFrame) return ctx.newSucceededFuture();
        this.sentGoAwayFrame = true;
        DefaultSpdyGoAwayFrame spdyGoAwayFrame = new DefaultSpdyGoAwayFrame((int)this.lastGoodStreamId, (SpdySessionStatus)status);
        return ctx.writeAndFlush((Object)spdyGoAwayFrame);
    }

    static /* synthetic */ void access$000(SpdySessionHandler x0, ChannelHandlerContext x1, SpdySessionStatus x2) {
        x0.issueSessionError((ChannelHandlerContext)x1, (SpdySessionStatus)x2);
    }
}

