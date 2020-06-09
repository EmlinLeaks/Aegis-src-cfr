/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InternalAttribute;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpPostRequestEncoder
implements ChunkedInput<HttpContent> {
    private static final Map.Entry[] percentEncodings = new Map.Entry[]{new AbstractMap.SimpleImmutableEntry<Pattern, String>(Pattern.compile((String)"\\*"), "%2A"), new AbstractMap.SimpleImmutableEntry<Pattern, String>(Pattern.compile((String)"\\+"), "%20"), new AbstractMap.SimpleImmutableEntry<Pattern, String>(Pattern.compile((String)"~"), "%7E")};
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private final Charset charset;
    private boolean isChunked;
    private final List<InterfaceHttpData> bodyListDatas;
    final List<InterfaceHttpData> multipartHttpDatas;
    private final boolean isMultipart;
    String multipartDataBoundary;
    String multipartMixedBoundary;
    private boolean headerFinalized;
    private final EncoderMode encoderMode;
    private boolean isLastChunk;
    private boolean isLastChunkSent;
    private FileUpload currentFileUpload;
    private boolean duringMixedMode;
    private long globalBodySize;
    private long globalProgress;
    private ListIterator<InterfaceHttpData> iterator;
    private ByteBuf currentBuffer;
    private InterfaceHttpData currentData;
    private boolean isKey = true;

    public HttpPostRequestEncoder(HttpRequest request, boolean multipart) throws ErrorDataEncoderException {
        this((HttpDataFactory)new DefaultHttpDataFactory((long)16384L), (HttpRequest)request, (boolean)multipart, (Charset)HttpConstants.DEFAULT_CHARSET, (EncoderMode)EncoderMode.RFC1738);
    }

    public HttpPostRequestEncoder(HttpDataFactory factory, HttpRequest request, boolean multipart) throws ErrorDataEncoderException {
        this((HttpDataFactory)factory, (HttpRequest)request, (boolean)multipart, (Charset)HttpConstants.DEFAULT_CHARSET, (EncoderMode)EncoderMode.RFC1738);
    }

    public HttpPostRequestEncoder(HttpDataFactory factory, HttpRequest request, boolean multipart, Charset charset, EncoderMode encoderMode) throws ErrorDataEncoderException {
        this.request = ObjectUtil.checkNotNull(request, (String)"request");
        this.charset = ObjectUtil.checkNotNull(charset, (String)"charset");
        this.factory = ObjectUtil.checkNotNull(factory, (String)"factory");
        if (HttpMethod.TRACE.equals((Object)request.method())) {
            throw new ErrorDataEncoderException((String)"Cannot create a Encoder if request is a TRACE");
        }
        this.bodyListDatas = new ArrayList<InterfaceHttpData>();
        this.isLastChunk = false;
        this.isLastChunkSent = false;
        this.isMultipart = multipart;
        this.multipartHttpDatas = new ArrayList<InterfaceHttpData>();
        this.encoderMode = encoderMode;
        if (!this.isMultipart) return;
        this.initDataMultipart();
    }

    public void cleanFiles() {
        this.factory.cleanRequestHttpData((HttpRequest)this.request);
    }

    public boolean isMultipart() {
        return this.isMultipart;
    }

    private void initDataMultipart() {
        this.multipartDataBoundary = HttpPostRequestEncoder.getNewMultipartDelimiter();
    }

    private void initMixedMultipart() {
        this.multipartMixedBoundary = HttpPostRequestEncoder.getNewMultipartDelimiter();
    }

    private static String getNewMultipartDelimiter() {
        return Long.toHexString((long)PlatformDependent.threadLocalRandom().nextLong());
    }

    public List<InterfaceHttpData> getBodyListAttributes() {
        return this.bodyListDatas;
    }

    public void setBodyHttpDatas(List<InterfaceHttpData> datas) throws ErrorDataEncoderException {
        if (datas == null) {
            throw new NullPointerException((String)"datas");
        }
        this.globalBodySize = 0L;
        this.bodyListDatas.clear();
        this.currentFileUpload = null;
        this.duringMixedMode = false;
        this.multipartHttpDatas.clear();
        Iterator<InterfaceHttpData> iterator = datas.iterator();
        while (iterator.hasNext()) {
            InterfaceHttpData data = iterator.next();
            this.addBodyHttpData((InterfaceHttpData)data);
        }
    }

    public void addBodyAttribute(String name, String value) throws ErrorDataEncoderException {
        String svalue = value != null ? value : "";
        Attribute data = this.factory.createAttribute((HttpRequest)this.request, (String)ObjectUtil.checkNotNull(name, (String)"name"), (String)svalue);
        this.addBodyHttpData((InterfaceHttpData)data);
    }

    public void addBodyFileUpload(String name, File file, String contentType, boolean isText) throws ErrorDataEncoderException {
        this.addBodyFileUpload((String)name, (String)file.getName(), (File)file, (String)contentType, (boolean)isText);
    }

    public void addBodyFileUpload(String name, String filename, File file, String contentType, boolean isText) throws ErrorDataEncoderException {
        ObjectUtil.checkNotNull(name, (String)"name");
        ObjectUtil.checkNotNull(file, (String)"file");
        if (filename == null) {
            filename = "";
        }
        String scontentType = contentType;
        String contentTransferEncoding = null;
        if (contentType == null) {
            scontentType = isText ? "text/plain" : "application/octet-stream";
        }
        if (!isText) {
            contentTransferEncoding = HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value();
        }
        FileUpload fileUpload = this.factory.createFileUpload((HttpRequest)this.request, (String)name, (String)filename, (String)scontentType, (String)contentTransferEncoding, null, (long)file.length());
        try {
            fileUpload.setContent((File)file);
        }
        catch (IOException e) {
            throw new ErrorDataEncoderException((Throwable)e);
        }
        this.addBodyHttpData((InterfaceHttpData)fileUpload);
    }

    public void addBodyFileUploads(String name, File[] file, String[] contentType, boolean[] isText) throws ErrorDataEncoderException {
        if (file.length != contentType.length && file.length != isText.length) {
            throw new IllegalArgumentException((String)"Different array length");
        }
        int i = 0;
        while (i < file.length) {
            this.addBodyFileUpload((String)name, (File)file[i], (String)contentType[i], (boolean)isText[i]);
            ++i;
        }
    }

    public void addBodyHttpData(InterfaceHttpData data) throws ErrorDataEncoderException {
        boolean localMixed;
        if (this.headerFinalized) {
            throw new ErrorDataEncoderException((String)"Cannot add value once finalized");
        }
        this.bodyListDatas.add((InterfaceHttpData)ObjectUtil.checkNotNull(data, (String)"data"));
        if (!this.isMultipart) {
            if (!(data instanceof Attribute)) {
                if (!(data instanceof FileUpload)) return;
                FileUpload fileUpload = (FileUpload)data;
                String key = this.encodeAttribute((String)fileUpload.getName(), (Charset)this.charset);
                String value = this.encodeAttribute((String)fileUpload.getFilename(), (Charset)this.charset);
                Attribute newattribute = this.factory.createAttribute((HttpRequest)this.request, (String)key, (String)value);
                this.multipartHttpDatas.add((InterfaceHttpData)newattribute);
                this.globalBodySize += (long)(newattribute.getName().length() + 1) + newattribute.length() + 1L;
                return;
            }
            Attribute attribute = (Attribute)data;
            try {
                String key = this.encodeAttribute((String)attribute.getName(), (Charset)this.charset);
                String value = this.encodeAttribute((String)attribute.getValue(), (Charset)this.charset);
                Attribute newattribute = this.factory.createAttribute((HttpRequest)this.request, (String)key, (String)value);
                this.multipartHttpDatas.add((InterfaceHttpData)newattribute);
                this.globalBodySize += (long)(newattribute.getName().length() + 1) + newattribute.length() + 1L;
                return;
            }
            catch (IOException e) {
                throw new ErrorDataEncoderException((Throwable)e);
            }
        }
        if (data instanceof Attribute) {
            InternalAttribute internal;
            if (this.duringMixedMode) {
                internal = new InternalAttribute((Charset)this.charset);
                internal.addValue((String)("\r\n--" + this.multipartMixedBoundary + "--"));
                this.multipartHttpDatas.add((InterfaceHttpData)internal);
                this.multipartMixedBoundary = null;
                this.currentFileUpload = null;
                this.duringMixedMode = false;
            }
            internal = new InternalAttribute((Charset)this.charset);
            if (!this.multipartHttpDatas.isEmpty()) {
                internal.addValue((String)"\r\n");
            }
            internal.addValue((String)("--" + this.multipartDataBoundary + "\r\n"));
            Attribute attribute = (Attribute)data;
            internal.addValue((String)(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + attribute.getName() + "\"\r\n"));
            internal.addValue((String)(HttpHeaderNames.CONTENT_LENGTH + ": " + attribute.length() + "\r\n"));
            Charset localcharset = attribute.getCharset();
            if (localcharset != null) {
                internal.addValue((String)(HttpHeaderNames.CONTENT_TYPE + ": " + "text/plain" + "; " + HttpHeaderValues.CHARSET + '=' + localcharset.name() + "\r\n"));
            }
            internal.addValue((String)"\r\n");
            this.multipartHttpDatas.add((InterfaceHttpData)internal);
            this.multipartHttpDatas.add((InterfaceHttpData)data);
            this.globalBodySize += attribute.length() + (long)internal.size();
            return;
        }
        if (!(data instanceof FileUpload)) return;
        FileUpload fileUpload = (FileUpload)data;
        InternalAttribute internal = new InternalAttribute((Charset)this.charset);
        if (!this.multipartHttpDatas.isEmpty()) {
            internal.addValue((String)"\r\n");
        }
        if (this.duringMixedMode) {
            if (this.currentFileUpload != null && this.currentFileUpload.getName().equals((Object)fileUpload.getName())) {
                localMixed = true;
            } else {
                internal.addValue((String)("--" + this.multipartMixedBoundary + "--"));
                this.multipartHttpDatas.add((InterfaceHttpData)internal);
                this.multipartMixedBoundary = null;
                internal = new InternalAttribute((Charset)this.charset);
                internal.addValue((String)"\r\n");
                localMixed = false;
                this.currentFileUpload = fileUpload;
                this.duringMixedMode = false;
            }
        } else if (this.encoderMode != EncoderMode.HTML5 && this.currentFileUpload != null && this.currentFileUpload.getName().equals((Object)fileUpload.getName())) {
            this.initMixedMultipart();
            InternalAttribute pastAttribute = (InternalAttribute)this.multipartHttpDatas.get((int)(this.multipartHttpDatas.size() - 2));
            this.globalBodySize -= (long)pastAttribute.size();
            StringBuilder replacement = new StringBuilder((int)(139 + this.multipartDataBoundary.length() + this.multipartMixedBoundary.length() * 2 + fileUpload.getFilename().length() + fileUpload.getName().length())).append((String)"--").append((String)this.multipartDataBoundary).append((String)"\r\n").append((CharSequence)HttpHeaderNames.CONTENT_DISPOSITION).append((String)": ").append((CharSequence)HttpHeaderValues.FORM_DATA).append((String)"; ").append((CharSequence)HttpHeaderValues.NAME).append((String)"=\"").append((String)fileUpload.getName()).append((String)"\"\r\n").append((CharSequence)HttpHeaderNames.CONTENT_TYPE).append((String)": ").append((CharSequence)HttpHeaderValues.MULTIPART_MIXED).append((String)"; ").append((CharSequence)HttpHeaderValues.BOUNDARY).append((char)'=').append((String)this.multipartMixedBoundary).append((String)"\r\n\r\n").append((String)"--").append((String)this.multipartMixedBoundary).append((String)"\r\n").append((CharSequence)HttpHeaderNames.CONTENT_DISPOSITION).append((String)": ").append((CharSequence)HttpHeaderValues.ATTACHMENT);
            if (!fileUpload.getFilename().isEmpty()) {
                replacement.append((String)"; ").append((CharSequence)HttpHeaderValues.FILENAME).append((String)"=\"").append((String)this.currentFileUpload.getFilename()).append((char)'\"');
            }
            replacement.append((String)"\r\n");
            pastAttribute.setValue((String)replacement.toString(), (int)1);
            pastAttribute.setValue((String)"", (int)2);
            this.globalBodySize += (long)pastAttribute.size();
            localMixed = true;
            this.duringMixedMode = true;
        } else {
            localMixed = false;
            this.currentFileUpload = fileUpload;
            this.duringMixedMode = false;
        }
        if (localMixed) {
            internal.addValue((String)("--" + this.multipartMixedBoundary + "\r\n"));
            if (fileUpload.getFilename().isEmpty()) {
                internal.addValue((String)(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.ATTACHMENT + "\r\n"));
            } else {
                internal.addValue((String)(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.ATTACHMENT + "; " + HttpHeaderValues.FILENAME + "=\"" + fileUpload.getFilename() + "\"\r\n"));
            }
        } else {
            internal.addValue((String)("--" + this.multipartDataBoundary + "\r\n"));
            if (fileUpload.getFilename().isEmpty()) {
                internal.addValue((String)(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + fileUpload.getName() + "\"\r\n"));
            } else {
                internal.addValue((String)(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + fileUpload.getName() + "\"; " + HttpHeaderValues.FILENAME + "=\"" + fileUpload.getFilename() + "\"\r\n"));
            }
        }
        internal.addValue((String)(HttpHeaderNames.CONTENT_LENGTH + ": " + fileUpload.length() + "\r\n"));
        internal.addValue((String)(HttpHeaderNames.CONTENT_TYPE + ": " + fileUpload.getContentType()));
        String contentTransferEncoding = fileUpload.getContentTransferEncoding();
        if (contentTransferEncoding != null && contentTransferEncoding.equals((Object)HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
            internal.addValue((String)("\r\n" + HttpHeaderNames.CONTENT_TRANSFER_ENCODING + ": " + HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value() + "\r\n\r\n"));
        } else if (fileUpload.getCharset() != null) {
            internal.addValue((String)("; " + HttpHeaderValues.CHARSET + '=' + fileUpload.getCharset().name() + "\r\n\r\n"));
        } else {
            internal.addValue((String)"\r\n\r\n");
        }
        this.multipartHttpDatas.add((InterfaceHttpData)internal);
        this.multipartHttpDatas.add((InterfaceHttpData)data);
        this.globalBodySize += fileUpload.length() + (long)internal.size();
    }

    public HttpRequest finalizeRequest() throws ErrorDataEncoderException {
        if (this.headerFinalized) throw new ErrorDataEncoderException((String)"Header already encoded");
        if (this.isMultipart) {
            InternalAttribute internal = new InternalAttribute((Charset)this.charset);
            if (this.duringMixedMode) {
                internal.addValue((String)("\r\n--" + this.multipartMixedBoundary + "--"));
            }
            internal.addValue((String)("\r\n--" + this.multipartDataBoundary + "--\r\n"));
            this.multipartHttpDatas.add((InterfaceHttpData)internal);
            this.multipartMixedBoundary = null;
            this.currentFileUpload = null;
            this.duringMixedMode = false;
            this.globalBodySize += (long)internal.size();
        }
        this.headerFinalized = true;
        HttpHeaders headers = this.request.headers();
        List<String> contentTypes = headers.getAll((CharSequence)HttpHeaderNames.CONTENT_TYPE);
        List<String> transferEncoding = headers.getAll((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
        if (contentTypes != null) {
            headers.remove((CharSequence)HttpHeaderNames.CONTENT_TYPE);
            for (String contentType : contentTypes) {
                String lowercased = contentType.toLowerCase();
                if (lowercased.startsWith((String)HttpHeaderValues.MULTIPART_FORM_DATA.toString()) || lowercased.startsWith((String)HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) continue;
                headers.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, (Object)contentType);
            }
        }
        if (this.isMultipart) {
            String value = HttpHeaderValues.MULTIPART_FORM_DATA + "; " + HttpHeaderValues.BOUNDARY + '=' + this.multipartDataBoundary;
            headers.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, (Object)value);
        } else {
            headers.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, (Object)HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
        }
        long realSize = this.globalBodySize;
        if (!this.isMultipart) {
            --realSize;
        }
        this.iterator = this.multipartHttpDatas.listIterator();
        headers.set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)String.valueOf((long)realSize));
        if (realSize <= 8096L && !this.isMultipart) {
            HttpContent chunk = this.nextChunk();
            if (!(this.request instanceof FullHttpRequest)) return new WrappedFullHttpRequest((HttpRequest)this.request, (HttpContent)chunk, null);
            FullHttpRequest fullRequest = (FullHttpRequest)this.request;
            ByteBuf chunkContent = chunk.content();
            if (fullRequest.content() == chunkContent) return fullRequest;
            fullRequest.content().clear().writeBytes((ByteBuf)chunkContent);
            chunkContent.release();
            return fullRequest;
        }
        this.isChunked = true;
        if (transferEncoding != null) {
            headers.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
            for (CharSequence v : transferEncoding) {
                if (HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase((CharSequence)v)) continue;
                headers.add((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)v);
            }
        }
        HttpUtil.setTransferEncodingChunked((HttpMessage)this.request, (boolean)true);
        return new WrappedHttpRequest((HttpRequest)this.request);
    }

    public boolean isChunked() {
        return this.isChunked;
    }

    private String encodeAttribute(String s, Charset charset) throws ErrorDataEncoderException {
        if (s == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode((String)s, (String)charset.name());
            if (this.encoderMode != EncoderMode.RFC3986) return encoded;
            Map.Entry[] arrentry = percentEncodings;
            int n = arrentry.length;
            int n2 = 0;
            while (n2 < n) {
                Map.Entry entry = arrentry[n2];
                String replacement = (String)entry.getValue();
                encoded = ((Pattern)entry.getKey()).matcher((CharSequence)encoded).replaceAll((String)replacement);
                ++n2;
            }
            return encoded;
        }
        catch (UnsupportedEncodingException e) {
            throw new ErrorDataEncoderException((String)charset.name(), (Throwable)e);
        }
    }

    private ByteBuf fillByteBuf() {
        int length = this.currentBuffer.readableBytes();
        if (length > 8096) {
            return this.currentBuffer.readRetainedSlice((int)8096);
        }
        ByteBuf slice = this.currentBuffer;
        this.currentBuffer = null;
        return slice;
    }

    private HttpContent encodeNextChunkMultipart(int sizeleft) throws ErrorDataEncoderException {
        ByteBuf buffer;
        if (this.currentData == null) {
            return null;
        }
        if (this.currentData instanceof InternalAttribute) {
            buffer = ((InternalAttribute)this.currentData).toByteBuf();
            this.currentData = null;
        } else {
            try {
                buffer = ((HttpData)this.currentData).getChunk((int)sizeleft);
            }
            catch (IOException e) {
                throw new ErrorDataEncoderException((Throwable)e);
            }
            if (buffer.capacity() == 0) {
                this.currentData = null;
                return null;
            }
        }
        this.currentBuffer = this.currentBuffer == null ? buffer : Unpooled.wrappedBuffer((ByteBuf[])new ByteBuf[]{this.currentBuffer, buffer});
        if (this.currentBuffer.readableBytes() < 8096) {
            this.currentData = null;
            return null;
        }
        buffer = this.fillByteBuf();
        return new DefaultHttpContent((ByteBuf)buffer);
    }

    private HttpContent encodeNextChunkUrlEncoded(int sizeleft) throws ErrorDataEncoderException {
        ByteBuf buffer;
        if (this.currentData == null) {
            return null;
        }
        int size = sizeleft;
        if (this.isKey) {
            String key = this.currentData.getName();
            buffer = Unpooled.wrappedBuffer((byte[])key.getBytes());
            this.isKey = false;
            this.currentBuffer = this.currentBuffer == null ? Unpooled.wrappedBuffer((ByteBuf[])new ByteBuf[]{buffer, Unpooled.wrappedBuffer((byte[])"=".getBytes())}) : Unpooled.wrappedBuffer((ByteBuf[])new ByteBuf[]{this.currentBuffer, buffer, Unpooled.wrappedBuffer((byte[])"=".getBytes())});
            size -= buffer.readableBytes() + 1;
            if (this.currentBuffer.readableBytes() >= 8096) {
                buffer = this.fillByteBuf();
                return new DefaultHttpContent((ByteBuf)buffer);
            }
        }
        try {
            buffer = ((HttpData)this.currentData).getChunk((int)size);
        }
        catch (IOException e) {
            throw new ErrorDataEncoderException((Throwable)e);
        }
        ByteBuf delimiter = null;
        if (buffer.readableBytes() < size) {
            this.isKey = true;
            ByteBuf byteBuf = delimiter = this.iterator.hasNext() ? Unpooled.wrappedBuffer((byte[])"&".getBytes()) : null;
        }
        if (buffer.capacity() == 0) {
            this.currentData = null;
            if (this.currentBuffer == null) {
                if (delimiter == null) {
                    return null;
                }
                this.currentBuffer = delimiter;
            } else if (delimiter != null) {
                this.currentBuffer = Unpooled.wrappedBuffer((ByteBuf[])new ByteBuf[]{this.currentBuffer, delimiter});
            }
            if (this.currentBuffer.readableBytes() < 8096) return null;
            buffer = this.fillByteBuf();
            return new DefaultHttpContent((ByteBuf)buffer);
        }
        this.currentBuffer = this.currentBuffer == null ? (delimiter != null ? Unpooled.wrappedBuffer((ByteBuf[])new ByteBuf[]{buffer, delimiter}) : buffer) : (delimiter != null ? Unpooled.wrappedBuffer((ByteBuf[])new ByteBuf[]{this.currentBuffer, buffer, delimiter}) : Unpooled.wrappedBuffer((ByteBuf[])new ByteBuf[]{this.currentBuffer, buffer}));
        if (this.currentBuffer.readableBytes() < 8096) {
            this.currentData = null;
            this.isKey = true;
            return null;
        }
        buffer = this.fillByteBuf();
        return new DefaultHttpContent((ByteBuf)buffer);
    }

    @Override
    public void close() throws Exception {
    }

    @Deprecated
    @Override
    public HttpContent readChunk(ChannelHandlerContext ctx) throws Exception {
        return this.readChunk((ByteBufAllocator)ctx.alloc());
    }

    @Override
    public HttpContent readChunk(ByteBufAllocator allocator) throws Exception {
        if (this.isLastChunkSent) {
            return null;
        }
        HttpContent nextChunk = this.nextChunk();
        this.globalProgress += (long)nextChunk.content().readableBytes();
        return nextChunk;
    }

    private HttpContent nextChunk() throws ErrorDataEncoderException {
        HttpContent chunk;
        if (this.isLastChunk) {
            this.isLastChunkSent = true;
            return LastHttpContent.EMPTY_LAST_CONTENT;
        }
        int size = this.calculateRemainingSize();
        if (size <= 0) {
            ByteBuf buffer = this.fillByteBuf();
            return new DefaultHttpContent((ByteBuf)buffer);
        }
        if (this.currentData != null) {
            chunk = this.isMultipart ? this.encodeNextChunkMultipart((int)size) : this.encodeNextChunkUrlEncoded((int)size);
            if (chunk != null) {
                return chunk;
            }
            size = this.calculateRemainingSize();
        }
        if (!this.iterator.hasNext()) {
            return this.lastChunk();
        }
        while (size > 0) {
            if (!this.iterator.hasNext()) return this.lastChunk();
            this.currentData = this.iterator.next();
            chunk = this.isMultipart ? this.encodeNextChunkMultipart((int)size) : this.encodeNextChunkUrlEncoded((int)size);
            if (chunk != null) return chunk;
            size = this.calculateRemainingSize();
        }
        return this.lastChunk();
    }

    private int calculateRemainingSize() {
        int size = 8096;
        if (this.currentBuffer == null) return size;
        size -= this.currentBuffer.readableBytes();
        return size;
    }

    private HttpContent lastChunk() {
        this.isLastChunk = true;
        if (this.currentBuffer == null) {
            this.isLastChunkSent = true;
            return LastHttpContent.EMPTY_LAST_CONTENT;
        }
        ByteBuf buffer = this.currentBuffer;
        this.currentBuffer = null;
        return new DefaultHttpContent((ByteBuf)buffer);
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        return this.isLastChunkSent;
    }

    @Override
    public long length() {
        long l;
        if (this.isMultipart) {
            l = this.globalBodySize;
            return l;
        }
        l = this.globalBodySize - 1L;
        return l;
    }

    @Override
    public long progress() {
        return this.globalProgress;
    }
}

