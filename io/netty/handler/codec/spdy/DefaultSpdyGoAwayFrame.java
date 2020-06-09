/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyGoAwayFrame;
import io.netty.handler.codec.spdy.SpdySessionStatus;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyGoAwayFrame
implements SpdyGoAwayFrame {
    private int lastGoodStreamId;
    private SpdySessionStatus status;

    public DefaultSpdyGoAwayFrame(int lastGoodStreamId) {
        this((int)lastGoodStreamId, (int)0);
    }

    public DefaultSpdyGoAwayFrame(int lastGoodStreamId, int statusCode) {
        this((int)lastGoodStreamId, (SpdySessionStatus)SpdySessionStatus.valueOf((int)statusCode));
    }

    public DefaultSpdyGoAwayFrame(int lastGoodStreamId, SpdySessionStatus status) {
        this.setLastGoodStreamId((int)lastGoodStreamId);
        this.setStatus((SpdySessionStatus)status);
    }

    @Override
    public int lastGoodStreamId() {
        return this.lastGoodStreamId;
    }

    @Override
    public SpdyGoAwayFrame setLastGoodStreamId(int lastGoodStreamId) {
        ObjectUtil.checkPositiveOrZero((int)lastGoodStreamId, (String)"lastGoodStreamId");
        this.lastGoodStreamId = lastGoodStreamId;
        return this;
    }

    @Override
    public SpdySessionStatus status() {
        return this.status;
    }

    @Override
    public SpdyGoAwayFrame setStatus(SpdySessionStatus status) {
        this.status = status;
        return this;
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + StringUtil.NEWLINE + "--> Last-good-stream-ID = " + this.lastGoodStreamId() + StringUtil.NEWLINE + "--> Status: " + this.status();
    }
}

