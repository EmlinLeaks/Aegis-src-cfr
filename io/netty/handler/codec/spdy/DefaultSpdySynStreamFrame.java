/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSpdySynStreamFrame
extends DefaultSpdyHeadersFrame
implements SpdySynStreamFrame {
    private int associatedStreamId;
    private byte priority;
    private boolean unidirectional;

    public DefaultSpdySynStreamFrame(int streamId, int associatedStreamId, byte priority) {
        this((int)streamId, (int)associatedStreamId, (byte)priority, (boolean)true);
    }

    public DefaultSpdySynStreamFrame(int streamId, int associatedStreamId, byte priority, boolean validateHeaders) {
        super((int)streamId, (boolean)validateHeaders);
        this.setAssociatedStreamId((int)associatedStreamId);
        this.setPriority((byte)priority);
    }

    @Override
    public SpdySynStreamFrame setStreamId(int streamId) {
        super.setStreamId((int)streamId);
        return this;
    }

    @Override
    public SpdySynStreamFrame setLast(boolean last) {
        super.setLast((boolean)last);
        return this;
    }

    @Override
    public SpdySynStreamFrame setInvalid() {
        super.setInvalid();
        return this;
    }

    @Override
    public int associatedStreamId() {
        return this.associatedStreamId;
    }

    @Override
    public SpdySynStreamFrame setAssociatedStreamId(int associatedStreamId) {
        ObjectUtil.checkPositiveOrZero((int)associatedStreamId, (String)"associatedStreamId");
        this.associatedStreamId = associatedStreamId;
        return this;
    }

    @Override
    public byte priority() {
        return this.priority;
    }

    @Override
    public SpdySynStreamFrame setPriority(byte priority) {
        if (priority < 0) throw new IllegalArgumentException((String)("Priority must be between 0 and 7 inclusive: " + priority));
        if (priority > 7) {
            throw new IllegalArgumentException((String)("Priority must be between 0 and 7 inclusive: " + priority));
        }
        this.priority = priority;
        return this;
    }

    @Override
    public boolean isUnidirectional() {
        return this.unidirectional;
    }

    @Override
    public SpdySynStreamFrame setUnidirectional(boolean unidirectional) {
        this.unidirectional = unidirectional;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((String)"(last: ").append((boolean)this.isLast()).append((String)"; unidirectional: ").append((boolean)this.isUnidirectional()).append((char)')').append((String)StringUtil.NEWLINE).append((String)"--> Stream-ID = ").append((int)this.streamId()).append((String)StringUtil.NEWLINE);
        if (this.associatedStreamId != 0) {
            buf.append((String)"--> Associated-To-Stream-ID = ").append((int)this.associatedStreamId()).append((String)StringUtil.NEWLINE);
        }
        buf.append((String)"--> Priority = ").append((int)this.priority()).append((String)StringUtil.NEWLINE).append((String)"--> Headers:").append((String)StringUtil.NEWLINE);
        this.appendHeaders((StringBuilder)buf);
        buf.setLength((int)(buf.length() - StringUtil.NEWLINE.length()));
        return buf.toString();
    }
}

