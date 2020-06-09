/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ProtocolDetectionResult;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.codec.haproxy.HAProxyProtocolException;
import io.netty.handler.codec.haproxy.HAProxyProtocolVersion;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.List;

public class HAProxyMessageDecoder
extends ByteToMessageDecoder {
    private static final int V1_MAX_LENGTH = 108;
    private static final int V2_MAX_LENGTH = 65551;
    private static final int V2_MIN_LENGTH = 232;
    private static final int V2_MAX_TLV = 65319;
    private static final byte[] BINARY_PREFIX = new byte[]{13, 10, 13, 10, 0, 13, 10, 81, 85, 73, 84, 10};
    private static final byte[] TEXT_PREFIX = new byte[]{80, 82, 79, 88, 89};
    private static final int BINARY_PREFIX_LENGTH = BINARY_PREFIX.length;
    private static final ProtocolDetectionResult<HAProxyProtocolVersion> DETECTION_RESULT_V1 = ProtocolDetectionResult.detected(HAProxyProtocolVersion.V1);
    private static final ProtocolDetectionResult<HAProxyProtocolVersion> DETECTION_RESULT_V2 = ProtocolDetectionResult.detected(HAProxyProtocolVersion.V2);
    private HeaderExtractor headerExtractor;
    private boolean discarding;
    private int discardedBytes;
    private final boolean failFast;
    private boolean finished;
    private int version = -1;
    private final int v2MaxHeaderSize;

    public HAProxyMessageDecoder() {
        this((boolean)true);
    }

    public HAProxyMessageDecoder(boolean failFast) {
        this.v2MaxHeaderSize = 65551;
        this.failFast = failFast;
    }

    public HAProxyMessageDecoder(int maxTlvSize) {
        this((int)maxTlvSize, (boolean)true);
    }

    public HAProxyMessageDecoder(int maxTlvSize, boolean failFast) {
        int calcMax;
        this.v2MaxHeaderSize = maxTlvSize < 1 ? 232 : (maxTlvSize > 65319 ? 65551 : ((calcMax = maxTlvSize + 232) > 65551 ? 65551 : calcMax));
        this.failFast = failFast;
    }

    private static int findVersion(ByteBuf buffer) {
        int n = buffer.readableBytes();
        if (n < 13) {
            return -1;
        }
        int idx = buffer.readerIndex();
        if (!HAProxyMessageDecoder.match((byte[])BINARY_PREFIX, (ByteBuf)buffer, (int)idx)) return 1;
        int n2 = (int)buffer.getByte((int)(idx + BINARY_PREFIX_LENGTH));
        return n2;
    }

    private static int findEndOfHeader(ByteBuf buffer) {
        int n = buffer.readableBytes();
        if (n < 16) {
            return -1;
        }
        int offset = buffer.readerIndex() + 14;
        int totalHeaderBytes = 16 + buffer.getUnsignedShort((int)offset);
        if (n < totalHeaderBytes) return -1;
        return totalHeaderBytes;
    }

    private static int findEndOfLine(ByteBuf buffer) {
        int n = buffer.writerIndex();
        int i = buffer.readerIndex();
        while (i < n) {
            byte b = buffer.getByte((int)i);
            if (b == 13 && i < n - 1 && buffer.getByte((int)(i + 1)) == 10) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    @Override
    public boolean isSingleDecode() {
        return true;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead((ChannelHandlerContext)ctx, (Object)msg);
        if (!this.finished) return;
        ctx.pipeline().remove((ChannelHandler)this);
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.version == -1 && (this.version = HAProxyMessageDecoder.findVersion((ByteBuf)in)) == -1) {
            return;
        }
        ByteBuf decoded = this.version == 1 ? this.decodeLine((ChannelHandlerContext)ctx, (ByteBuf)in) : this.decodeStruct((ChannelHandlerContext)ctx, (ByteBuf)in);
        if (decoded == null) return;
        this.finished = true;
        try {
            if (this.version == 1) {
                out.add((Object)HAProxyMessage.decodeHeader((String)decoded.toString((Charset)CharsetUtil.US_ASCII)));
                return;
            }
            out.add((Object)HAProxyMessage.decodeHeader((ByteBuf)decoded));
            return;
        }
        catch (HAProxyProtocolException e) {
            this.fail((ChannelHandlerContext)ctx, null, (Exception)e);
        }
    }

    private ByteBuf decodeStruct(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        if (this.headerExtractor != null) return this.headerExtractor.extract((ChannelHandlerContext)ctx, (ByteBuf)buffer);
        this.headerExtractor = new StructHeaderExtractor((HAProxyMessageDecoder)this, (int)this.v2MaxHeaderSize);
        return this.headerExtractor.extract((ChannelHandlerContext)ctx, (ByteBuf)buffer);
    }

    private ByteBuf decodeLine(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        if (this.headerExtractor != null) return this.headerExtractor.extract((ChannelHandlerContext)ctx, (ByteBuf)buffer);
        this.headerExtractor = new LineHeaderExtractor((HAProxyMessageDecoder)this, (int)108);
        return this.headerExtractor.extract((ChannelHandlerContext)ctx, (ByteBuf)buffer);
    }

    private void failOverLimit(ChannelHandlerContext ctx, int length) {
        this.failOverLimit((ChannelHandlerContext)ctx, (String)String.valueOf((int)length));
    }

    private void failOverLimit(ChannelHandlerContext ctx, String length) {
        int maxLength = this.version == 1 ? 108 : this.v2MaxHeaderSize;
        this.fail((ChannelHandlerContext)ctx, (String)("header length (" + length + ") exceeds the allowed maximum (" + maxLength + ')'), null);
    }

    private void fail(ChannelHandlerContext ctx, String errMsg, Exception e) {
        HAProxyProtocolException ppex;
        this.finished = true;
        ctx.close();
        if (errMsg != null && e != null) {
            ppex = new HAProxyProtocolException((String)errMsg, (Throwable)e);
            throw ppex;
        }
        if (errMsg != null) {
            ppex = new HAProxyProtocolException((String)errMsg);
            throw ppex;
        }
        if (e != null) {
            ppex = new HAProxyProtocolException((Throwable)e);
            throw ppex;
        }
        ppex = new HAProxyProtocolException();
        throw ppex;
    }

    public static ProtocolDetectionResult<HAProxyProtocolVersion> detectProtocol(ByteBuf buffer) {
        if (buffer.readableBytes() < 12) {
            return ProtocolDetectionResult.needsMoreData();
        }
        int idx = buffer.readerIndex();
        if (HAProxyMessageDecoder.match((byte[])BINARY_PREFIX, (ByteBuf)buffer, (int)idx)) {
            return DETECTION_RESULT_V2;
        }
        if (!HAProxyMessageDecoder.match((byte[])TEXT_PREFIX, (ByteBuf)buffer, (int)idx)) return ProtocolDetectionResult.invalid();
        return DETECTION_RESULT_V1;
    }

    private static boolean match(byte[] prefix, ByteBuf buffer, int idx) {
        int i = 0;
        while (i < prefix.length) {
            byte b = buffer.getByte((int)(idx + i));
            if (b != prefix[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    static /* synthetic */ boolean access$000(HAProxyMessageDecoder x0) {
        return x0.discarding;
    }

    static /* synthetic */ void access$100(HAProxyMessageDecoder x0, ChannelHandlerContext x1, int x2) {
        x0.failOverLimit((ChannelHandlerContext)x1, (int)x2);
    }

    static /* synthetic */ int access$202(HAProxyMessageDecoder x0, int x1) {
        x0.discardedBytes = x1;
        return x0.discardedBytes;
    }

    static /* synthetic */ boolean access$002(HAProxyMessageDecoder x0, boolean x1) {
        x0.discarding = x1;
        return x0.discarding;
    }

    static /* synthetic */ boolean access$300(HAProxyMessageDecoder x0) {
        return x0.failFast;
    }

    static /* synthetic */ int access$200(HAProxyMessageDecoder x0) {
        return x0.discardedBytes;
    }

    static /* synthetic */ void access$400(HAProxyMessageDecoder x0, ChannelHandlerContext x1, String x2) {
        x0.failOverLimit((ChannelHandlerContext)x1, (String)x2);
    }

    static /* synthetic */ int access$500(ByteBuf x0) {
        return HAProxyMessageDecoder.findEndOfLine((ByteBuf)x0);
    }

    static /* synthetic */ int access$600(ByteBuf x0) {
        return HAProxyMessageDecoder.findEndOfHeader((ByteBuf)x0);
    }
}

