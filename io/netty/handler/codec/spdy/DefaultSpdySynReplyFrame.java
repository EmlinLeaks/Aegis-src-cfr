/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdySynReplyFrame;
import io.netty.util.internal.StringUtil;

public class DefaultSpdySynReplyFrame
extends DefaultSpdyHeadersFrame
implements SpdySynReplyFrame {
    public DefaultSpdySynReplyFrame(int streamId) {
        super((int)streamId);
    }

    public DefaultSpdySynReplyFrame(int streamId, boolean validateHeaders) {
        super((int)streamId, (boolean)validateHeaders);
    }

    @Override
    public SpdySynReplyFrame setStreamId(int streamId) {
        super.setStreamId((int)streamId);
        return this;
    }

    @Override
    public SpdySynReplyFrame setLast(boolean last) {
        super.setLast((boolean)last);
        return this;
    }

    @Override
    public SpdySynReplyFrame setInvalid() {
        super.setInvalid();
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((String)"(last: ").append((boolean)this.isLast()).append((char)')').append((String)StringUtil.NEWLINE).append((String)"--> Stream-ID = ").append((int)this.streamId()).append((String)StringUtil.NEWLINE).append((String)"--> Headers:").append((String)StringUtil.NEWLINE);
        this.appendHeaders((StringBuilder)buf);
        buf.setLength((int)(buf.length() - StringUtil.NEWLINE.length()));
        return buf.toString();
    }
}

