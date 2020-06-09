/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.spdy.DefaultSpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyDataFrame
extends DefaultSpdyStreamFrame
implements SpdyDataFrame {
    private final ByteBuf data;

    public DefaultSpdyDataFrame(int streamId) {
        this((int)streamId, (ByteBuf)Unpooled.buffer((int)0));
    }

    public DefaultSpdyDataFrame(int streamId, ByteBuf data) {
        super((int)streamId);
        if (data == null) {
            throw new NullPointerException((String)"data");
        }
        this.data = DefaultSpdyDataFrame.validate((ByteBuf)data);
    }

    private static ByteBuf validate(ByteBuf data) {
        if (data.readableBytes() <= 16777215) return data;
        throw new IllegalArgumentException((String)"data payload cannot exceed 16777215 bytes");
    }

    @Override
    public SpdyDataFrame setStreamId(int streamId) {
        super.setStreamId((int)streamId);
        return this;
    }

    @Override
    public SpdyDataFrame setLast(boolean last) {
        super.setLast((boolean)last);
        return this;
    }

    @Override
    public ByteBuf content() {
        if (this.data.refCnt() > 0) return this.data;
        throw new IllegalReferenceCountException((int)this.data.refCnt());
    }

    @Override
    public SpdyDataFrame copy() {
        return this.replace((ByteBuf)this.content().copy());
    }

    @Override
    public SpdyDataFrame duplicate() {
        return this.replace((ByteBuf)this.content().duplicate());
    }

    @Override
    public SpdyDataFrame retainedDuplicate() {
        return this.replace((ByteBuf)this.content().retainedDuplicate());
    }

    @Override
    public SpdyDataFrame replace(ByteBuf content) {
        DefaultSpdyDataFrame frame = new DefaultSpdyDataFrame((int)this.streamId(), (ByteBuf)content);
        frame.setLast((boolean)this.isLast());
        return frame;
    }

    @Override
    public int refCnt() {
        return this.data.refCnt();
    }

    @Override
    public SpdyDataFrame retain() {
        this.data.retain();
        return this;
    }

    @Override
    public SpdyDataFrame retain(int increment) {
        this.data.retain((int)increment);
        return this;
    }

    @Override
    public SpdyDataFrame touch() {
        this.data.touch();
        return this;
    }

    @Override
    public SpdyDataFrame touch(Object hint) {
        this.data.touch((Object)hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.data.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.data.release((int)decrement);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((String)"(last: ").append((boolean)this.isLast()).append((char)')').append((String)StringUtil.NEWLINE).append((String)"--> Stream-ID = ").append((int)this.streamId()).append((String)StringUtil.NEWLINE).append((String)"--> Size = ");
        if (this.refCnt() == 0) {
            buf.append((String)"(freed)");
            return buf.toString();
        }
        buf.append((int)this.content().readableBytes());
        return buf.toString();
    }
}

