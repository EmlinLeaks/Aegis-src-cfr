/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.CaseIgnoringComparator;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpPostRequestDecoder;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostMultipartRequestDecoder
implements InterfaceHttpPostRequestDecoder {
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private Charset charset;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<InterfaceHttpData>();
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<CharSequence, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
    private ByteBuf undecodedChunk;
    private int bodyListHttpDataRank;
    private String multipartDataBoundary;
    private String multipartMixedBoundary;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
    private Map<CharSequence, Attribute> currentFieldAttributes;
    private FileUpload currentFileUpload;
    private Attribute currentAttribute;
    private boolean destroyed;
    private int discardThreshold = 10485760;
    private static final String FILENAME_ENCODED = HttpHeaderValues.FILENAME.toString() + '*';

    public HttpPostMultipartRequestDecoder(HttpRequest request) {
        this((HttpDataFactory)new DefaultHttpDataFactory((long)16384L), (HttpRequest)request, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request) {
        this((HttpDataFactory)factory, (HttpRequest)request, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
        this.request = ObjectUtil.checkNotNull(request, (String)"request");
        this.charset = ObjectUtil.checkNotNull(charset, (String)"charset");
        this.factory = ObjectUtil.checkNotNull(factory, (String)"factory");
        this.setMultipart((String)this.request.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE));
        if (request instanceof HttpContent) {
            this.offer((HttpContent)((HttpContent)((Object)request)));
            return;
        }
        this.undecodedChunk = Unpooled.buffer();
        this.parseBody();
    }

    private void setMultipart(String contentType) {
        String[] dataBoundary = HttpPostRequestDecoder.getMultipartDataBoundary((String)contentType);
        if (dataBoundary != null) {
            this.multipartDataBoundary = dataBoundary[0];
            if (dataBoundary.length > 1 && dataBoundary[1] != null) {
                this.charset = Charset.forName((String)dataBoundary[1]);
            }
        } else {
            this.multipartDataBoundary = null;
        }
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
    }

    private void checkDestroyed() {
        if (!this.destroyed) return;
        throw new IllegalStateException((String)(HttpPostMultipartRequestDecoder.class.getSimpleName() + " was destroyed already"));
    }

    @Override
    public boolean isMultipart() {
        this.checkDestroyed();
        return true;
    }

    @Override
    public void setDiscardThreshold(int discardThreshold) {
        this.discardThreshold = ObjectUtil.checkPositiveOrZero((int)discardThreshold, (String)"discardThreshold");
    }

    @Override
    public int getDiscardThreshold() {
        return this.discardThreshold;
    }

    @Override
    public List<InterfaceHttpData> getBodyHttpDatas() {
        this.checkDestroyed();
        if (this.isLastChunk) return this.bodyListHttpData;
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    @Override
    public List<InterfaceHttpData> getBodyHttpDatas(String name) {
        this.checkDestroyed();
        if (this.isLastChunk) return this.bodyMapHttpData.get((Object)name);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    @Override
    public InterfaceHttpData getBodyHttpData(String name) {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        List<InterfaceHttpData> list = this.bodyMapHttpData.get((Object)name);
        if (list == null) return null;
        return list.get((int)0);
    }

    @Override
    public HttpPostMultipartRequestDecoder offer(HttpContent content) {
        this.checkDestroyed();
        ByteBuf buf = content.content();
        if (this.undecodedChunk == null) {
            this.undecodedChunk = buf.copy();
        } else {
            this.undecodedChunk.writeBytes((ByteBuf)buf);
        }
        if (content instanceof LastHttpContent) {
            this.isLastChunk = true;
        }
        this.parseBody();
        if (this.undecodedChunk == null) return this;
        if (this.undecodedChunk.writerIndex() <= this.discardThreshold) return this;
        this.undecodedChunk.discardReadBytes();
        return this;
    }

    @Override
    public boolean hasNext() {
        this.checkDestroyed();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
            throw new HttpPostRequestDecoder.EndOfDataDecoderException();
        }
        if (this.bodyListHttpData.isEmpty()) return false;
        if (this.bodyListHttpDataRank >= this.bodyListHttpData.size()) return false;
        return true;
    }

    @Override
    public InterfaceHttpData next() {
        this.checkDestroyed();
        if (!this.hasNext()) return null;
        return this.bodyListHttpData.get((int)this.bodyListHttpDataRank++);
    }

    @Override
    public InterfaceHttpData currentPartialHttpData() {
        if (this.currentFileUpload == null) return this.currentAttribute;
        return this.currentFileUpload;
    }

    private void parseBody() {
        if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
            this.parseBodyMultipart();
            return;
        }
        if (!this.isLastChunk) return;
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
    }

    protected void addHttpData(InterfaceHttpData data) {
        if (data == null) {
            return;
        }
        List<InterfaceHttpData> datas = this.bodyMapHttpData.get((Object)data.getName());
        if (datas == null) {
            datas = new ArrayList<InterfaceHttpData>((int)1);
            this.bodyMapHttpData.put((String)data.getName(), datas);
        }
        datas.add((InterfaceHttpData)data);
        this.bodyListHttpData.add((InterfaceHttpData)data);
    }

    private void parseBodyMultipart() {
        if (this.undecodedChunk == null) return;
        if (this.undecodedChunk.readableBytes() == 0) {
            return;
        }
        InterfaceHttpData data = this.decodeMultipart((HttpPostRequestDecoder.MultiPartStatus)this.currentStatus);
        while (data != null) {
            this.addHttpData((InterfaceHttpData)data);
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE) return;
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
                return;
            }
            data = this.decodeMultipart((HttpPostRequestDecoder.MultiPartStatus)this.currentStatus);
        }
    }

    private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus state) {
        switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[state.ordinal()]) {
            case 1: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)"Should not be called with the current getStatus");
            }
            case 2: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)"Should not be called with the current getStatus");
            }
            case 3: {
                return this.findMultipartDelimiter((String)this.multipartDataBoundary, (HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, (HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
            }
            case 4: {
                return this.findMultipartDisposition();
            }
            case 5: {
                Charset localCharset = null;
                Attribute charsetAttribute = this.currentFieldAttributes.get((Object)HttpHeaderValues.CHARSET);
                if (charsetAttribute != null) {
                    try {
                        localCharset = Charset.forName((String)charsetAttribute.getValue());
                    }
                    catch (IOException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    catch (UnsupportedCharsetException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                }
                Attribute nameAttribute = this.currentFieldAttributes.get((Object)HttpHeaderValues.NAME);
                if (this.currentAttribute == null) {
                    long size;
                    Attribute lengthAttribute = this.currentFieldAttributes.get((Object)HttpHeaderNames.CONTENT_LENGTH);
                    try {
                        size = lengthAttribute != null ? Long.parseLong((String)lengthAttribute.getValue()) : 0L;
                    }
                    catch (IOException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    catch (NumberFormatException ignored) {
                        size = 0L;
                    }
                    try {
                        this.currentAttribute = size > 0L ? this.factory.createAttribute((HttpRequest)this.request, (String)HttpPostMultipartRequestDecoder.cleanString((String)nameAttribute.getValue()), (long)size) : this.factory.createAttribute((HttpRequest)this.request, (String)HttpPostMultipartRequestDecoder.cleanString((String)nameAttribute.getValue()));
                    }
                    catch (NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    catch (IOException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    if (localCharset != null) {
                        this.currentAttribute.setCharset((Charset)localCharset);
                    }
                }
                if (!HttpPostMultipartRequestDecoder.loadDataMultipart((ByteBuf)this.undecodedChunk, (String)this.multipartDataBoundary, (HttpData)this.currentAttribute)) {
                    return null;
                }
                Attribute finalAttribute = this.currentAttribute;
                this.currentAttribute = null;
                this.currentFieldAttributes = null;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                return finalAttribute;
            }
            case 6: {
                return this.getFileUpload((String)this.multipartDataBoundary);
            }
            case 7: {
                return this.findMultipartDelimiter((String)this.multipartMixedBoundary, (HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, (HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
            }
            case 8: {
                return this.findMultipartDisposition();
            }
            case 9: {
                return this.getFileUpload((String)this.multipartMixedBoundary);
            }
            case 10: {
                return null;
            }
            case 11: {
                return null;
            }
        }
        throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)"Shouldn't reach here.");
    }

    private static void skipControlCharacters(ByteBuf undecodedChunk) {
        char c;
        if (!undecodedChunk.hasArray()) {
            try {
                HttpPostMultipartRequestDecoder.skipControlCharactersStandard((ByteBuf)undecodedChunk);
                return;
            }
            catch (IndexOutOfBoundsException e1) {
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException((Throwable)e1);
            }
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize((ByteBuf)undecodedChunk);
        do {
            if (sao.pos >= sao.limit) throw new HttpPostRequestDecoder.NotEnoughDataDecoderException((String)"Access out of bounds");
        } while (Character.isISOControl((char)(c = (char)(sao.bytes[sao.pos++] & 255))) || Character.isWhitespace((char)c));
        sao.setReadPosition((int)1);
    }

    private static void skipControlCharactersStandard(ByteBuf undecodedChunk) {
        char c;
        while (Character.isISOControl((char)(c = (char)undecodedChunk.readUnsignedByte())) || Character.isWhitespace((char)c)) {
        }
        undecodedChunk.readerIndex((int)(undecodedChunk.readerIndex() - 1));
    }

    private InterfaceHttpData findMultipartDelimiter(String delimiter, HttpPostRequestDecoder.MultiPartStatus dispositionStatus, HttpPostRequestDecoder.MultiPartStatus closeDelimiterStatus) {
        String newline;
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            HttpPostMultipartRequestDecoder.skipControlCharacters((ByteBuf)this.undecodedChunk);
        }
        catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
            this.undecodedChunk.readerIndex((int)readerIndex);
            return null;
        }
        this.skipOneLine();
        try {
            newline = HttpPostMultipartRequestDecoder.readDelimiter((ByteBuf)this.undecodedChunk, (String)delimiter);
        }
        catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
            this.undecodedChunk.readerIndex((int)readerIndex);
            return null;
        }
        if (newline.equals((Object)delimiter)) {
            this.currentStatus = dispositionStatus;
            return this.decodeMultipart((HttpPostRequestDecoder.MultiPartStatus)dispositionStatus);
        }
        if (newline.equals((Object)(delimiter + "--"))) {
            this.currentStatus = closeDelimiterStatus;
            if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) return null;
            this.currentFieldAttributes = null;
            return this.decodeMultipart((HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
        }
        this.undecodedChunk.readerIndex((int)readerIndex);
        throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)"No Multipart delimiter found");
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private InterfaceHttpData findMultipartDisposition() {
        readerIndex = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            this.currentFieldAttributes = new TreeMap<CharSequence, Attribute>(CaseIgnoringComparator.INSTANCE);
        }
        block17 : do {
            block33 : {
                block32 : {
                    block30 : {
                        block31 : {
                            if (this.skipOneLine()) break block30;
                            try {
                                HttpPostMultipartRequestDecoder.skipControlCharacters((ByteBuf)this.undecodedChunk);
                                newline = HttpPostMultipartRequestDecoder.readLine((ByteBuf)this.undecodedChunk, (Charset)this.charset);
                            }
                            catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
                                this.undecodedChunk.readerIndex((int)readerIndex);
                                return null;
                            }
                            contents = HttpPostMultipartRequestDecoder.splitMultipartHeader((String)newline);
                            if (!HttpHeaderNames.CONTENT_DISPOSITION.contentEqualsIgnoreCase((CharSequence)contents[0])) break block31;
                            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                                checkSecondArg = HttpHeaderValues.FORM_DATA.contentEqualsIgnoreCase((CharSequence)contents[1]);
                            } else {
                                v0 = checkSecondArg = HttpHeaderValues.ATTACHMENT.contentEqualsIgnoreCase((CharSequence)contents[1]) != false || HttpHeaderValues.FILE.contentEqualsIgnoreCase((CharSequence)contents[1]) != false;
                            }
                            if (!checkSecondArg) continue;
                            break block32;
                        }
                        if (HttpHeaderNames.CONTENT_TRANSFER_ENCODING.contentEqualsIgnoreCase((CharSequence)contents[0])) {
                            try {
                                attribute = this.factory.createAttribute((HttpRequest)this.request, (String)HttpHeaderNames.CONTENT_TRANSFER_ENCODING.toString(), (String)HttpPostMultipartRequestDecoder.cleanString((String)contents[1]));
                            }
                            catch (NullPointerException e) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                            }
                            catch (IllegalArgumentException e) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                            }
                            this.currentFieldAttributes.put((CharSequence)HttpHeaderNames.CONTENT_TRANSFER_ENCODING, (Attribute)attribute);
                            continue;
                        }
                        if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase((CharSequence)contents[0])) {
                            try {
                                attribute = this.factory.createAttribute((HttpRequest)this.request, (String)HttpHeaderNames.CONTENT_LENGTH.toString(), (String)HttpPostMultipartRequestDecoder.cleanString((String)contents[1]));
                            }
                            catch (NullPointerException e) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                            }
                            catch (IllegalArgumentException e) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                            }
                            this.currentFieldAttributes.put((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Attribute)attribute);
                            continue;
                        }
                        if (!HttpHeaderNames.CONTENT_TYPE.contentEqualsIgnoreCase((CharSequence)contents[0])) continue;
                        if (HttpHeaderValues.MULTIPART_MIXED.contentEqualsIgnoreCase((CharSequence)contents[1])) {
                            if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)"Mixed Multipart found in a previous Mixed Multipart");
                            values = StringUtil.substringAfter((String)contents[2], (char)'=');
                            this.multipartMixedBoundary = "--" + values;
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                            return this.decodeMultipart((HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                        }
                        break block33;
                    }
                    filenameAttribute = this.currentFieldAttributes.get((Object)HttpHeaderValues.FILENAME);
                    if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                        if (filenameAttribute == null) throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)"Filename not found");
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
                        return this.decodeMultipart((HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
                    }
                    if (filenameAttribute != null) {
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
                        return this.decodeMultipart((HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
                    }
                    this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                    return this.decodeMultipart((HttpPostRequestDecoder.MultiPartStatus)HttpPostRequestDecoder.MultiPartStatus.FIELD);
                }
                i = 2;
                do {
                    if (i >= contents.length) continue block17;
                    values = contents[i].split((String)"=", (int)2);
                    try {
                        attribute = this.getContentDispositionAttribute((String[])values);
                    }
                    catch (NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    this.currentFieldAttributes.put((CharSequence)attribute.getName(), (Attribute)attribute);
                    ++i;
                } while (true);
            }
            i = 1;
            do {
                if (i >= contents.length) ** break;
                charsetHeader = HttpHeaderValues.CHARSET.toString();
                if (contents[i].regionMatches((boolean)true, (int)0, (String)charsetHeader, (int)0, (int)charsetHeader.length())) {
                    values = StringUtil.substringAfter((String)contents[i], (char)'=');
                    try {
                        attribute = this.factory.createAttribute((HttpRequest)this.request, (String)charsetHeader, (String)HttpPostMultipartRequestDecoder.cleanString((String)values));
                    }
                    catch (NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    this.currentFieldAttributes.put((CharSequence)HttpHeaderValues.CHARSET, (Attribute)attribute);
                } else {
                    try {
                        attribute = this.factory.createAttribute((HttpRequest)this.request, (String)HttpPostMultipartRequestDecoder.cleanString((String)contents[0]), (String)contents[i]);
                    }
                    catch (NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
                    }
                    this.currentFieldAttributes.put((CharSequence)attribute.getName(), (Attribute)attribute);
                }
                ++i;
            } while (true);
            break;
        } while (true);
    }

    private Attribute getContentDispositionAttribute(String ... values) {
        String name = HttpPostMultipartRequestDecoder.cleanString((String)values[0]);
        String value = values[1];
        if (HttpHeaderValues.FILENAME.contentEquals((CharSequence)name)) {
            int last = value.length() - 1;
            if (last <= 0) return this.factory.createAttribute((HttpRequest)this.request, (String)name, (String)value);
            if (value.charAt((int)0) != '\"') return this.factory.createAttribute((HttpRequest)this.request, (String)name, (String)value);
            if (value.charAt((int)last) != '\"') return this.factory.createAttribute((HttpRequest)this.request, (String)name, (String)value);
            value = value.substring((int)1, (int)last);
            return this.factory.createAttribute((HttpRequest)this.request, (String)name, (String)value);
        }
        if (!FILENAME_ENCODED.equals((Object)name)) {
            value = HttpPostMultipartRequestDecoder.cleanString((String)value);
            return this.factory.createAttribute((HttpRequest)this.request, (String)name, (String)value);
        }
        try {
            name = HttpHeaderValues.FILENAME.toString();
            String[] split = value.split((String)"'", (int)3);
            value = QueryStringDecoder.decodeComponent((String)split[2], (Charset)Charset.forName((String)split[0]));
            return this.factory.createAttribute((HttpRequest)this.request, (String)name, (String)value);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
        }
        catch (UnsupportedCharsetException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
        }
    }

    protected InterfaceHttpData getFileUpload(String delimiter) {
        Attribute charsetAttribute;
        Attribute encoding = this.currentFieldAttributes.get((Object)HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
        Charset localCharset = this.charset;
        HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
        if (encoding != null) {
            String code;
            try {
                code = encoding.getValue().toLowerCase();
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
            }
            if (code.equals((Object)HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
                localCharset = CharsetUtil.US_ASCII;
            } else if (code.equals((Object)HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
                localCharset = CharsetUtil.ISO_8859_1;
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
            } else {
                if (!code.equals((Object)HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)("TransferEncoding Unknown: " + code));
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
            }
        }
        if ((charsetAttribute = this.currentFieldAttributes.get((Object)HttpHeaderValues.CHARSET)) != null) {
            try {
                localCharset = Charset.forName((String)charsetAttribute.getValue());
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
            }
            catch (UnsupportedCharsetException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
            }
        }
        if (this.currentFileUpload == null) {
            long size;
            Attribute filenameAttribute = this.currentFieldAttributes.get((Object)HttpHeaderValues.FILENAME);
            Attribute nameAttribute = this.currentFieldAttributes.get((Object)HttpHeaderValues.NAME);
            Attribute contentTypeAttribute = this.currentFieldAttributes.get((Object)HttpHeaderNames.CONTENT_TYPE);
            Attribute lengthAttribute = this.currentFieldAttributes.get((Object)HttpHeaderNames.CONTENT_LENGTH);
            try {
                size = lengthAttribute != null ? Long.parseLong((String)lengthAttribute.getValue()) : 0L;
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
            }
            catch (NumberFormatException ignored) {
                size = 0L;
            }
            try {
                String contentType = contentTypeAttribute != null ? contentTypeAttribute.getValue() : "application/octet-stream";
                this.currentFileUpload = this.factory.createFileUpload((HttpRequest)this.request, (String)HttpPostMultipartRequestDecoder.cleanString((String)nameAttribute.getValue()), (String)HttpPostMultipartRequestDecoder.cleanString((String)filenameAttribute.getValue()), (String)contentType, (String)mechanism.value(), (Charset)localCharset, (long)size);
            }
            catch (NullPointerException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
            }
            catch (IllegalArgumentException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
            }
        }
        if (!HttpPostMultipartRequestDecoder.loadDataMultipart((ByteBuf)this.undecodedChunk, (String)delimiter, (HttpData)this.currentFileUpload)) {
            return null;
        }
        if (!this.currentFileUpload.isCompleted()) return null;
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
            this.currentFieldAttributes = null;
        } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
            this.cleanMixedAttributes();
        }
        FileUpload fileUpload = this.currentFileUpload;
        this.currentFileUpload = null;
        return fileUpload;
    }

    @Override
    public void destroy() {
        this.cleanFiles();
        this.destroyed = true;
        if (this.undecodedChunk == null) return;
        if (this.undecodedChunk.refCnt() <= 0) return;
        this.undecodedChunk.release();
        this.undecodedChunk = null;
    }

    @Override
    public void cleanFiles() {
        this.checkDestroyed();
        this.factory.cleanRequestHttpData((HttpRequest)this.request);
    }

    @Override
    public void removeHttpDataFromClean(InterfaceHttpData data) {
        this.checkDestroyed();
        this.factory.removeHttpDataFromClean((HttpRequest)this.request, (InterfaceHttpData)data);
    }

    private void cleanMixedAttributes() {
        this.currentFieldAttributes.remove((Object)HttpHeaderValues.CHARSET);
        this.currentFieldAttributes.remove((Object)HttpHeaderNames.CONTENT_LENGTH);
        this.currentFieldAttributes.remove((Object)HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
        this.currentFieldAttributes.remove((Object)HttpHeaderNames.CONTENT_TYPE);
        this.currentFieldAttributes.remove((Object)HttpHeaderValues.FILENAME);
    }

    private static String readLineStandard(ByteBuf undecodedChunk, Charset charset) {
        int readerIndex = undecodedChunk.readerIndex();
        try {
            ByteBuf line = Unpooled.buffer((int)64);
            while (undecodedChunk.isReadable()) {
                byte nextByte = undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = undecodedChunk.getByte((int)undecodedChunk.readerIndex());
                    if (nextByte == 10) {
                        undecodedChunk.readByte();
                        return line.toString((Charset)charset);
                    }
                    line.writeByte((int)13);
                    continue;
                }
                if (nextByte == 10) {
                    return line.toString((Charset)charset);
                }
                line.writeByte((int)nextByte);
            }
        }
        catch (IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex((int)readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException((Throwable)e);
        }
        undecodedChunk.readerIndex((int)readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private static String readLine(ByteBuf undecodedChunk, Charset charset) {
        if (!undecodedChunk.hasArray()) {
            return HttpPostMultipartRequestDecoder.readLineStandard((ByteBuf)undecodedChunk, (Charset)charset);
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize((ByteBuf)undecodedChunk);
        int readerIndex = undecodedChunk.readerIndex();
        try {
            ByteBuf line = Unpooled.buffer((int)64);
            while (sao.pos < sao.limit) {
                byte nextByte;
                if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                    if (sao.pos < sao.limit) {
                        if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                            sao.setReadPosition((int)0);
                            return line.toString((Charset)charset);
                        }
                        --sao.pos;
                        line.writeByte((int)13);
                        continue;
                    }
                    line.writeByte((int)nextByte);
                    continue;
                }
                if (nextByte == 10) {
                    sao.setReadPosition((int)0);
                    return line.toString((Charset)charset);
                }
                line.writeByte((int)nextByte);
            }
        }
        catch (IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex((int)readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException((Throwable)e);
        }
        undecodedChunk.readerIndex((int)readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private static String readDelimiterStandard(ByteBuf undecodedChunk, String delimiter) {
        int readerIndex = undecodedChunk.readerIndex();
        try {
            byte nextByte;
            StringBuilder sb = new StringBuilder((int)64);
            int len = delimiter.length();
            for (int delimiterPos = 0; undecodedChunk.isReadable() && delimiterPos < len; ++delimiterPos) {
                nextByte = undecodedChunk.readByte();
                if (nextByte != delimiter.charAt((int)delimiterPos)) {
                    undecodedChunk.readerIndex((int)readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                sb.append((char)((char)nextByte));
            }
            if (undecodedChunk.isReadable()) {
                nextByte = undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = undecodedChunk.readByte();
                    if (nextByte == 10) {
                        return sb.toString();
                    }
                    undecodedChunk.readerIndex((int)readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                if (nextByte == 10) {
                    return sb.toString();
                }
                if (nextByte == 45) {
                    sb.append((char)'-');
                    nextByte = undecodedChunk.readByte();
                    if (nextByte == 45) {
                        sb.append((char)'-');
                        if (!undecodedChunk.isReadable()) return sb.toString();
                        nextByte = undecodedChunk.readByte();
                        if (nextByte == 13) {
                            nextByte = undecodedChunk.readByte();
                            if (nextByte == 10) {
                                return sb.toString();
                            }
                            undecodedChunk.readerIndex((int)readerIndex);
                            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                        }
                        if (nextByte == 10) {
                            return sb.toString();
                        }
                        undecodedChunk.readerIndex((int)(undecodedChunk.readerIndex() - 1));
                        return sb.toString();
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex((int)readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException((Throwable)e);
        }
        undecodedChunk.readerIndex((int)readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private static String readDelimiter(ByteBuf undecodedChunk, String delimiter) {
        if (!undecodedChunk.hasArray()) {
            return HttpPostMultipartRequestDecoder.readDelimiterStandard((ByteBuf)undecodedChunk, (String)delimiter);
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize((ByteBuf)undecodedChunk);
        int readerIndex = undecodedChunk.readerIndex();
        int len = delimiter.length();
        try {
            byte nextByte;
            StringBuilder sb = new StringBuilder((int)64);
            for (int delimiterPos = 0; sao.pos < sao.limit && delimiterPos < len; ++delimiterPos) {
                if ((nextByte = sao.bytes[sao.pos++]) != delimiter.charAt((int)delimiterPos)) {
                    undecodedChunk.readerIndex((int)readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                sb.append((char)((char)nextByte));
            }
            if (sao.pos < sao.limit) {
                if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                    if (sao.pos >= sao.limit) {
                        undecodedChunk.readerIndex((int)readerIndex);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                    }
                    if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                        sao.setReadPosition((int)0);
                        return sb.toString();
                    }
                    undecodedChunk.readerIndex((int)readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                if (nextByte == 10) {
                    sao.setReadPosition((int)0);
                    return sb.toString();
                }
                if (nextByte == 45) {
                    sb.append((char)'-');
                    if (sao.pos < sao.limit && (nextByte = sao.bytes[sao.pos++]) == 45) {
                        sb.append((char)'-');
                        if (sao.pos >= sao.limit) {
                            sao.setReadPosition((int)0);
                            return sb.toString();
                        }
                        if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                            if (sao.pos >= sao.limit) {
                                undecodedChunk.readerIndex((int)readerIndex);
                                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                            }
                            if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                                sao.setReadPosition((int)0);
                                return sb.toString();
                            }
                            undecodedChunk.readerIndex((int)readerIndex);
                            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                        }
                        if (nextByte == 10) {
                            sao.setReadPosition((int)0);
                            return sb.toString();
                        }
                        sao.setReadPosition((int)1);
                        return sb.toString();
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex((int)readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException((Throwable)e);
        }
        undecodedChunk.readerIndex((int)readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private static boolean loadDataMultipartStandard(ByteBuf undecodedChunk, String delimiter, HttpData httpData) {
        int startReaderIndex = undecodedChunk.readerIndex();
        int delimeterLength = delimiter.length();
        int index = 0;
        int lastPosition = startReaderIndex;
        int prevByte = 10;
        boolean delimiterFound = false;
        while (undecodedChunk.isReadable()) {
            byte nextByte = undecodedChunk.readByte();
            if (prevByte == 10 && nextByte == delimiter.codePointAt((int)index)) {
                if (delimeterLength != ++index) continue;
                delimiterFound = true;
                break;
            }
            lastPosition = undecodedChunk.readerIndex();
            if (nextByte == 10) {
                index = 0;
                lastPosition -= prevByte == 13 ? 2 : 1;
            }
            prevByte = (int)nextByte;
        }
        if (prevByte == 13) {
            --lastPosition;
        }
        ByteBuf content = undecodedChunk.copy((int)startReaderIndex, (int)(lastPosition - startReaderIndex));
        try {
            httpData.addContent((ByteBuf)content, (boolean)delimiterFound);
        }
        catch (IOException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
        }
        undecodedChunk.readerIndex((int)lastPosition);
        return delimiterFound;
    }

    private static boolean loadDataMultipart(ByteBuf undecodedChunk, String delimiter, HttpData httpData) {
        if (!undecodedChunk.hasArray()) {
            return HttpPostMultipartRequestDecoder.loadDataMultipartStandard((ByteBuf)undecodedChunk, (String)delimiter, (HttpData)httpData);
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize((ByteBuf)undecodedChunk);
        int startReaderIndex = undecodedChunk.readerIndex();
        int delimeterLength = delimiter.length();
        int index = 0;
        int lastRealPos = sao.pos;
        int prevByte = 10;
        boolean delimiterFound = false;
        while (sao.pos < sao.limit) {
            int nextByte = sao.bytes[sao.pos++];
            if (prevByte == 10 && nextByte == delimiter.codePointAt((int)index)) {
                if (delimeterLength != ++index) continue;
                delimiterFound = true;
                break;
            }
            lastRealPos = sao.pos;
            if (nextByte == 10) {
                index = 0;
                lastRealPos -= prevByte == 13 ? 2 : 1;
            }
            prevByte = nextByte;
        }
        if (prevByte == 13) {
            --lastRealPos;
        }
        int lastPosition = sao.getReadPosition((int)lastRealPos);
        ByteBuf content = undecodedChunk.copy((int)startReaderIndex, (int)(lastPosition - startReaderIndex));
        try {
            httpData.addContent((ByteBuf)content, (boolean)delimiterFound);
        }
        catch (IOException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
        }
        undecodedChunk.readerIndex((int)lastPosition);
        return delimiterFound;
    }

    /*
     * Unable to fully structure code
     */
    private static String cleanString(String field) {
        size = field.length();
        sb = new StringBuilder((int)size);
        i = 0;
        while (i < size) {
            nextChar = field.charAt((int)i);
            switch (nextChar) {
                case '\t': 
                case ',': 
                case ':': 
                case ';': 
                case '=': {
                    sb.append((char)' ');
                    ** break;
                }
                case '\"': {
                    ** break;
                }
            }
            sb.append((char)nextChar);
lbl15: // 3 sources:
            ++i;
        }
        return sb.toString().trim();
    }

    private boolean skipOneLine() {
        if (!this.undecodedChunk.isReadable()) {
            return false;
        }
        byte nextByte = this.undecodedChunk.readByte();
        if (nextByte == 13) {
            if (!this.undecodedChunk.isReadable()) {
                this.undecodedChunk.readerIndex((int)(this.undecodedChunk.readerIndex() - 1));
                return false;
            }
            nextByte = this.undecodedChunk.readByte();
            if (nextByte == 10) {
                return true;
            }
            this.undecodedChunk.readerIndex((int)(this.undecodedChunk.readerIndex() - 2));
            return false;
        }
        if (nextByte == 10) {
            return true;
        }
        this.undecodedChunk.readerIndex((int)(this.undecodedChunk.readerIndex() - 1));
        return false;
    }

    private static String[] splitMultipartHeader(String sb) {
        int nameStart;
        int nameEnd;
        int colonEnd;
        char ch;
        ArrayList<String> headers = new ArrayList<String>((int)1);
        for (nameEnd = nameStart = HttpPostBodyUtil.findNonWhitespace((String)sb, (int)0); nameEnd < sb.length() && (ch = sb.charAt((int)nameEnd)) != ':' && !Character.isWhitespace((char)ch); ++nameEnd) {
        }
        for (colonEnd = nameEnd; colonEnd < sb.length(); ++colonEnd) {
            if (sb.charAt((int)colonEnd) != ':') continue;
            ++colonEnd;
            break;
        }
        int valueStart = HttpPostBodyUtil.findNonWhitespace((String)sb, (int)colonEnd);
        int valueEnd = HttpPostBodyUtil.findEndOfString((String)sb);
        headers.add(sb.substring((int)nameStart, (int)nameEnd));
        String svalue = valueStart >= valueEnd ? "" : sb.substring((int)valueStart, (int)valueEnd);
        String[] values = svalue.indexOf((int)59) >= 0 ? HttpPostMultipartRequestDecoder.splitMultipartHeaderValues((String)svalue) : svalue.split((String)",");
        for (String value : values) {
            headers.add(value.trim());
        }
        String[] array = new String[headers.size()];
        int i = 0;
        while (i < headers.size()) {
            array[i] = (String)headers.get((int)i);
            ++i;
        }
        return array;
    }

    private static String[] splitMultipartHeaderValues(String svalue) {
        ArrayList<String> values = InternalThreadLocalMap.get().arrayList((int)1);
        boolean inQuote = false;
        boolean escapeNext = false;
        int start = 0;
        int i = 0;
        do {
            if (i >= svalue.length()) {
                values.add(svalue.substring((int)start));
                return values.toArray(new String[0]);
            }
            char c = svalue.charAt((int)i);
            if (inQuote) {
                if (escapeNext) {
                    escapeNext = false;
                } else if (c == '\\') {
                    escapeNext = true;
                } else if (c == '\"') {
                    inQuote = false;
                }
            } else if (c == '\"') {
                inQuote = true;
            } else if (c == ';') {
                values.add(svalue.substring((int)start, (int)i));
                start = i + 1;
            }
            ++i;
        } while (true);
    }
}

