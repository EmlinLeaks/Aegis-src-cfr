/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyRstStreamFrame
extends DefaultSpdyStreamFrame
implements SpdyRstStreamFrame {
    private SpdyStreamStatus status;

    public DefaultSpdyRstStreamFrame(int streamId, int statusCode) {
        this((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.valueOf((int)statusCode));
    }

    public DefaultSpdyRstStreamFrame(int streamId, SpdyStreamStatus status) {
        super((int)streamId);
        this.setStatus((SpdyStreamStatus)status);
    }

    @Override
    public SpdyRstStreamFrame setStreamId(int streamId) {
        super.setStreamId((int)streamId);
        return this;
    }

    @Override
    public SpdyRstStreamFrame setLast(boolean last) {
        super.setLast((boolean)last);
        return this;
    }

    @Override
    public SpdyStreamStatus status() {
        return this.status;
    }

    @Override
    public SpdyRstStreamFrame setStatus(SpdyStreamStatus status) {
        this.status = status;
        return this;
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + StringUtil.NEWLINE + "--> Stream-ID = " + this.streamId() + StringUtil.NEWLINE + "--> Status: " + this.status();
    }
}

