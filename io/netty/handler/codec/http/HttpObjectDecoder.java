/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpExpectationFailedEvent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.internal.AppendableCharSequence;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public abstract class HttpObjectDecoder
extends ByteToMessageDecoder {
    private static final String EMPTY_VALUE = "";
    private final int maxChunkSize;
    private final boolean chunkedSupported;
    protected final boolean validateHeaders;
    private final HeaderParser headerParser;
    private final LineParser lineParser;
    private HttpMessage message;
    private long chunkSize;
    private long contentLength = Long.MIN_VALUE;
    private volatile boolean resetRequested;
    private CharSequence name;
    private CharSequence value;
    private LastHttpContent trailer;
    private State currentState = State.SKIP_CONTROL_CHARS;

    protected HttpObjectDecoder() {
        this((int)4096, (int)8192, (int)8192, (boolean)true);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
        this((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)chunkedSupported, (boolean)true);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
        this((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)chunkedSupported, (boolean)validateHeaders, (int)128);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders, int initialBufferSize) {
        ObjectUtil.checkPositive((int)maxInitialLineLength, (String)"maxInitialLineLength");
        ObjectUtil.checkPositive((int)maxHeaderSize, (String)"maxHeaderSize");
        ObjectUtil.checkPositive((int)maxChunkSize, (String)"maxChunkSize");
        AppendableCharSequence seq = new AppendableCharSequence((int)initialBufferSize);
        this.lineParser = new LineParser((AppendableCharSequence)seq, (int)maxInitialLineLength);
        this.headerParser = new HeaderParser((AppendableCharSequence)seq, (int)maxHeaderSize);
        this.maxChunkSize = maxChunkSize;
        this.chunkedSupported = chunkedSupported;
        this.validateHeaders = validateHeaders;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (this.resetRequested) {
            this.resetNow();
        }
        switch (1.$SwitchMap$io$netty$handler$codec$http$HttpObjectDecoder$State[this.currentState.ordinal()]) {
            case 1: {
                if (!HttpObjectDecoder.skipControlCharacters((ByteBuf)buffer)) {
                    return;
                }
                this.currentState = State.READ_INITIAL;
            }
            case 3: {
                try {
                    AppendableCharSequence line = this.lineParser.parse((ByteBuf)buffer);
                    if (line == null) {
                        return;
                    }
                    String[] initialLine = HttpObjectDecoder.splitInitialLine((AppendableCharSequence)line);
                    if (initialLine.length < 3) {
                        this.currentState = State.SKIP_CONTROL_CHARS;
                        return;
                    }
                    this.message = this.createMessage((String[])initialLine);
                    this.currentState = State.READ_HEADER;
                }
                catch (Exception e) {
                    out.add((Object)this.invalidMessage((ByteBuf)buffer, (Exception)e));
                    return;
                }
            }
            case 4: {
                try {
                    State nextState = this.readHeaders((ByteBuf)buffer);
                    if (nextState == null) {
                        return;
                    }
                    this.currentState = nextState;
                    switch (nextState) {
                        case SKIP_CONTROL_CHARS: {
                            out.add((Object)this.message);
                            out.add((Object)LastHttpContent.EMPTY_LAST_CONTENT);
                            this.resetNow();
                            return;
                        }
                        case READ_CHUNK_SIZE: {
                            if (!this.chunkedSupported) {
                                throw new IllegalArgumentException((String)"Chunked messages not supported");
                            }
                            out.add((Object)this.message);
                            return;
                        }
                    }
                    long contentLength = this.contentLength();
                    if (contentLength == 0L || contentLength == -1L && this.isDecodingRequest()) {
                        out.add((Object)this.message);
                        out.add((Object)LastHttpContent.EMPTY_LAST_CONTENT);
                        this.resetNow();
                        return;
                    }
                    assert (nextState == State.READ_FIXED_LENGTH_CONTENT || nextState == State.READ_VARIABLE_LENGTH_CONTENT);
                    out.add((Object)this.message);
                    if (nextState != State.READ_FIXED_LENGTH_CONTENT) return;
                    this.chunkSize = contentLength;
                    return;
                }
                catch (Exception e) {
                    out.add((Object)this.invalidMessage((ByteBuf)buffer, (Exception)e));
                    return;
                }
            }
            case 5: {
                int toRead = Math.min((int)buffer.readableBytes(), (int)this.maxChunkSize);
                if (toRead <= 0) return;
                ByteBuf content = buffer.readRetainedSlice((int)toRead);
                out.add((Object)new DefaultHttpContent((ByteBuf)content));
                return;
            }
            case 6: {
                int readLimit = buffer.readableBytes();
                if (readLimit == 0) {
                    return;
                }
                int toRead = Math.min((int)readLimit, (int)this.maxChunkSize);
                if ((long)toRead > this.chunkSize) {
                    toRead = (int)this.chunkSize;
                }
                ByteBuf content = buffer.readRetainedSlice((int)toRead);
                this.chunkSize -= (long)toRead;
                if (this.chunkSize == 0L) {
                    out.add((Object)new DefaultLastHttpContent((ByteBuf)content, (boolean)this.validateHeaders));
                    this.resetNow();
                    return;
                }
                out.add((Object)new DefaultHttpContent((ByteBuf)content));
                return;
            }
            case 2: {
                try {
                    AppendableCharSequence line = this.lineParser.parse((ByteBuf)buffer);
                    if (line == null) {
                        return;
                    }
                    int chunkSize = HttpObjectDecoder.getChunkSize((String)line.toString());
                    this.chunkSize = (long)chunkSize;
                    if (chunkSize == 0) {
                        this.currentState = State.READ_CHUNK_FOOTER;
                        return;
                    }
                    this.currentState = State.READ_CHUNKED_CONTENT;
                }
                catch (Exception e) {
                    out.add((Object)this.invalidChunk((ByteBuf)buffer, (Exception)e));
                    return;
                }
            }
            case 7: {
                assert (this.chunkSize <= Integer.MAX_VALUE);
                int toRead = Math.min((int)((int)this.chunkSize), (int)this.maxChunkSize);
                if ((toRead = Math.min((int)toRead, (int)buffer.readableBytes())) == 0) {
                    return;
                }
                DefaultHttpContent chunk = new DefaultHttpContent((ByteBuf)buffer.readRetainedSlice((int)toRead));
                this.chunkSize -= (long)toRead;
                out.add((Object)chunk);
                if (this.chunkSize != 0L) {
                    return;
                }
                this.currentState = State.READ_CHUNK_DELIMITER;
            }
            case 8: {
                int wIdx = buffer.writerIndex();
                int rIdx = buffer.readerIndex();
                while (wIdx > rIdx) {
                    byte next;
                    if ((next = buffer.getByte((int)rIdx++)) != 10) continue;
                    this.currentState = State.READ_CHUNK_SIZE;
                    break;
                }
                buffer.readerIndex((int)rIdx);
                return;
            }
            case 9: {
                try {
                    LastHttpContent trailer = this.readTrailingHeaders((ByteBuf)buffer);
                    if (trailer == null) {
                        return;
                    }
                    out.add((Object)trailer);
                    this.resetNow();
                    return;
                }
                catch (Exception e) {
                    out.add((Object)this.invalidChunk((ByteBuf)buffer, (Exception)e));
                    return;
                }
            }
            case 10: {
                buffer.skipBytes((int)buffer.readableBytes());
                return;
            }
            case 11: {
                int readableBytes = buffer.readableBytes();
                if (readableBytes <= 0) return;
                out.add((Object)buffer.readBytes((int)readableBytes));
                break;
            }
        }
    }

    @Override
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        boolean prematureClosure;
        super.decodeLast((ChannelHandlerContext)ctx, (ByteBuf)in, out);
        if (this.resetRequested) {
            this.resetNow();
        }
        if (this.message == null) return;
        boolean chunked = HttpUtil.isTransferEncodingChunked((HttpMessage)this.message);
        if (this.currentState == State.READ_VARIABLE_LENGTH_CONTENT && !in.isReadable() && !chunked) {
            out.add((Object)LastHttpContent.EMPTY_LAST_CONTENT);
            this.resetNow();
            return;
        }
        if (this.currentState == State.READ_HEADER) {
            out.add((Object)this.invalidMessage((ByteBuf)Unpooled.EMPTY_BUFFER, (Exception)new PrematureChannelClosureException((String)"Connection closed before received headers")));
            this.resetNow();
            return;
        }
        if (this.isDecodingRequest() || chunked) {
            prematureClosure = true;
        } else {
            boolean bl = prematureClosure = this.contentLength() > 0L;
        }
        if (!prematureClosure) {
            out.add((Object)LastHttpContent.EMPTY_LAST_CONTENT);
        }
        this.resetNow();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof HttpExpectationFailedEvent) {
            switch (this.currentState) {
                case READ_CHUNK_SIZE: 
                case READ_VARIABLE_LENGTH_CONTENT: 
                case READ_FIXED_LENGTH_CONTENT: {
                    this.reset();
                    break;
                }
            }
        }
        super.userEventTriggered((ChannelHandlerContext)ctx, (Object)evt);
    }

    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        if (!(msg instanceof HttpResponse)) return false;
        HttpResponse res = (HttpResponse)msg;
        int code = res.status().code();
        if (code >= 100 && code < 200) {
            if (code != 101) return true;
            if (res.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT)) return true;
            if (!res.headers().contains((CharSequence)HttpHeaderNames.UPGRADE, (CharSequence)HttpHeaderValues.WEBSOCKET, (boolean)true)) return true;
            return false;
        }
        switch (code) {
            case 204: 
            case 304: {
                return true;
            }
        }
        return false;
    }

    protected boolean isSwitchingToNonHttp1Protocol(HttpResponse msg) {
        if (msg.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
            return false;
        }
        String newProtocol = msg.headers().get((CharSequence)HttpHeaderNames.UPGRADE);
        if (newProtocol == null) return true;
        if (newProtocol.contains((CharSequence)HttpVersion.HTTP_1_0.text())) return false;
        if (newProtocol.contains((CharSequence)HttpVersion.HTTP_1_1.text())) return false;
        return true;
    }

    public void reset() {
        this.resetRequested = true;
    }

    private void resetNow() {
        HttpResponse res;
        HttpMessage message = this.message;
        this.message = null;
        this.name = null;
        this.value = null;
        this.contentLength = Long.MIN_VALUE;
        this.lineParser.reset();
        this.headerParser.reset();
        this.trailer = null;
        if (!this.isDecodingRequest() && (res = (HttpResponse)message) != null && this.isSwitchingToNonHttp1Protocol((HttpResponse)res)) {
            this.currentState = State.UPGRADED;
            return;
        }
        this.resetRequested = false;
        this.currentState = State.SKIP_CONTROL_CHARS;
    }

    private HttpMessage invalidMessage(ByteBuf in, Exception cause) {
        this.currentState = State.BAD_MESSAGE;
        in.skipBytes((int)in.readableBytes());
        if (this.message == null) {
            this.message = this.createInvalidMessage();
        }
        this.message.setDecoderResult((DecoderResult)DecoderResult.failure((Throwable)cause));
        HttpMessage ret = this.message;
        this.message = null;
        return ret;
    }

    private HttpContent invalidChunk(ByteBuf in, Exception cause) {
        this.currentState = State.BAD_MESSAGE;
        in.skipBytes((int)in.readableBytes());
        DefaultLastHttpContent chunk = new DefaultLastHttpContent((ByteBuf)Unpooled.EMPTY_BUFFER);
        chunk.setDecoderResult((DecoderResult)DecoderResult.failure((Throwable)cause));
        this.message = null;
        this.trailer = null;
        return chunk;
    }

    private static boolean skipControlCharacters(ByteBuf buffer) {
        boolean skiped = false;
        int wIdx = buffer.writerIndex();
        int rIdx = buffer.readerIndex();
        while (wIdx > rIdx) {
            short c;
            if (Character.isISOControl((int)(c = buffer.getUnsignedByte((int)rIdx++))) || Character.isWhitespace((int)c)) continue;
            --rIdx;
            skiped = true;
            break;
        }
        buffer.readerIndex((int)rIdx);
        return skiped;
    }

    private State readHeaders(ByteBuf buffer) {
        HttpMessage message = this.message;
        HttpHeaders headers = message.headers();
        AppendableCharSequence line = this.headerParser.parse((ByteBuf)buffer);
        if (line == null) {
            return null;
        }
        if (line.length() > 0) {
            do {
                char firstChar = line.charAtUnsafe((int)0);
                if (this.name != null && (firstChar == ' ' || firstChar == '\t')) {
                    String trimmedLine = line.toString().trim();
                    String valueStr = String.valueOf((Object)this.value);
                    this.value = valueStr + ' ' + trimmedLine;
                } else {
                    if (this.name != null) {
                        headers.add((CharSequence)this.name, (Object)this.value);
                    }
                    this.splitHeader((AppendableCharSequence)line);
                }
                line = this.headerParser.parse((ByteBuf)buffer);
                if (line != null) continue;
                return null;
            } while (line.length() > 0);
        }
        if (this.name != null) {
            headers.add((CharSequence)this.name, (Object)this.value);
        }
        this.name = null;
        this.value = null;
        if (this.isContentAlwaysEmpty((HttpMessage)message)) {
            HttpUtil.setTransferEncodingChunked((HttpMessage)message, (boolean)false);
            return State.SKIP_CONTROL_CHARS;
        }
        if (HttpUtil.isTransferEncodingChunked((HttpMessage)message)) {
            return State.READ_CHUNK_SIZE;
        }
        if (this.contentLength() < 0L) return State.READ_VARIABLE_LENGTH_CONTENT;
        return State.READ_FIXED_LENGTH_CONTENT;
    }

    private long contentLength() {
        if (this.contentLength != Long.MIN_VALUE) return this.contentLength;
        this.contentLength = HttpUtil.getContentLength((HttpMessage)this.message, (long)-1L);
        return this.contentLength;
    }

    private LastHttpContent readTrailingHeaders(ByteBuf buffer) {
        AppendableCharSequence line = this.headerParser.parse((ByteBuf)buffer);
        if (line == null) {
            return null;
        }
        LastHttpContent trailer = this.trailer;
        if (line.length() == 0 && trailer == null) {
            return LastHttpContent.EMPTY_LAST_CONTENT;
        }
        CharSequence lastHeader = null;
        if (trailer == null) {
            trailer = this.trailer = new DefaultLastHttpContent((ByteBuf)Unpooled.EMPTY_BUFFER, (boolean)this.validateHeaders);
        }
        do {
            if (line.length() <= 0) {
                this.trailer = null;
                return trailer;
            }
            char firstChar = line.charAtUnsafe((int)0);
            if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
                List<String> current = trailer.trailingHeaders().getAll(lastHeader);
                if (current.isEmpty()) continue;
                int lastPos = current.size() - 1;
                String lineTrimmed = line.toString().trim();
                String currentLastPos = current.get((int)lastPos);
                current.set((int)lastPos, (String)(currentLastPos + lineTrimmed));
                continue;
            }
            this.splitHeader((AppendableCharSequence)line);
            CharSequence headerName = this.name;
            if (!(HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase((CharSequence)headerName) || HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase((CharSequence)headerName) || HttpHeaderNames.TRAILER.contentEqualsIgnoreCase((CharSequence)headerName))) {
                trailer.trailingHeaders().add((CharSequence)headerName, (Object)this.value);
            }
            lastHeader = this.name;
            this.name = null;
            this.value = null;
        } while ((line = this.headerParser.parse((ByteBuf)buffer)) != null);
        return null;
    }

    protected abstract boolean isDecodingRequest();

    protected abstract HttpMessage createMessage(String[] var1) throws Exception;

    protected abstract HttpMessage createInvalidMessage();

    private static int getChunkSize(String hex) {
        hex = hex.trim();
        int i = 0;
        while (i < hex.length()) {
            char c = hex.charAt((int)i);
            if (c == ';' || Character.isWhitespace((char)c) || Character.isISOControl((char)c)) {
                hex = hex.substring((int)0, (int)i);
                return Integer.parseInt((String)hex, (int)16);
            }
            ++i;
        }
        return Integer.parseInt((String)hex, (int)16);
    }

    private static String[] splitInitialLine(AppendableCharSequence sb) {
        int aStart = HttpObjectDecoder.findNonWhitespace((AppendableCharSequence)sb, (int)0);
        int aEnd = HttpObjectDecoder.findWhitespace((AppendableCharSequence)sb, (int)aStart);
        int bStart = HttpObjectDecoder.findNonWhitespace((AppendableCharSequence)sb, (int)aEnd);
        int bEnd = HttpObjectDecoder.findWhitespace((AppendableCharSequence)sb, (int)bStart);
        int cStart = HttpObjectDecoder.findNonWhitespace((AppendableCharSequence)sb, (int)bEnd);
        int cEnd = HttpObjectDecoder.findEndOfString((AppendableCharSequence)sb);
        return new String[]{sb.subStringUnsafe((int)aStart, (int)aEnd), sb.subStringUnsafe((int)bStart, (int)bEnd), cStart < cEnd ? sb.subStringUnsafe((int)cStart, (int)cEnd) : ""};
    }

    private void splitHeader(AppendableCharSequence sb) {
        int nameStart;
        int nameEnd;
        int colonEnd;
        char ch;
        int length = sb.length();
        for (nameEnd = nameStart = HttpObjectDecoder.findNonWhitespace((AppendableCharSequence)sb, (int)0); nameEnd < length && (ch = sb.charAtUnsafe((int)nameEnd)) != ':' && (this.isDecodingRequest() || !Character.isWhitespace((char)ch)); ++nameEnd) {
        }
        for (colonEnd = nameEnd; colonEnd < length; ++colonEnd) {
            if (sb.charAtUnsafe((int)colonEnd) != ':') continue;
            ++colonEnd;
            break;
        }
        this.name = sb.subStringUnsafe((int)nameStart, (int)nameEnd);
        int valueStart = HttpObjectDecoder.findNonWhitespace((AppendableCharSequence)sb, (int)colonEnd);
        if (valueStart == length) {
            this.value = "";
            return;
        }
        int valueEnd = HttpObjectDecoder.findEndOfString((AppendableCharSequence)sb);
        this.value = sb.subStringUnsafe((int)valueStart, (int)valueEnd);
    }

    private static int findNonWhitespace(AppendableCharSequence sb, int offset) {
        int result = offset;
        while (result < sb.length()) {
            if (!Character.isWhitespace((char)sb.charAtUnsafe((int)result))) {
                return result;
            }
            ++result;
        }
        return sb.length();
    }

    private static int findWhitespace(AppendableCharSequence sb, int offset) {
        int result = offset;
        while (result < sb.length()) {
            if (Character.isWhitespace((char)sb.charAtUnsafe((int)result))) {
                return result;
            }
            ++result;
        }
        return sb.length();
    }

    private static int findEndOfString(AppendableCharSequence sb) {
        int result = sb.length() - 1;
        while (result > 0) {
            if (!Character.isWhitespace((char)sb.charAtUnsafe((int)result))) {
                return result + 1;
            }
            --result;
        }
        return 0;
    }
}

