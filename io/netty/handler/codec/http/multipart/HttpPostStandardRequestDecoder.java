/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.CaseIgnoringComparator;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostStandardRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpPostRequestDecoder;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostStandardRequestDecoder
implements InterfaceHttpPostRequestDecoder {
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private final Charset charset;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<InterfaceHttpData>();
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<CharSequence, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
    private ByteBuf undecodedChunk;
    private int bodyListHttpDataRank;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
    private Attribute currentAttribute;
    private boolean destroyed;
    private int discardThreshold = 10485760;

    public HttpPostStandardRequestDecoder(HttpRequest request) {
        this((HttpDataFactory)new DefaultHttpDataFactory((long)16384L), (HttpRequest)request, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request) {
        this((HttpDataFactory)factory, (HttpRequest)request, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
        this.request = ObjectUtil.checkNotNull(request, (String)"request");
        this.charset = ObjectUtil.checkNotNull(charset, (String)"charset");
        this.factory = ObjectUtil.checkNotNull(factory, (String)"factory");
        try {
            if (request instanceof HttpContent) {
                this.offer((HttpContent)((HttpContent)((Object)request)));
                return;
            }
            this.undecodedChunk = Unpooled.buffer();
            this.parseBody();
            return;
        }
        catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.destroy();
            throw e;
        }
    }

    private void checkDestroyed() {
        if (!this.destroyed) return;
        throw new IllegalStateException((String)(HttpPostStandardRequestDecoder.class.getSimpleName() + " was destroyed already"));
    }

    @Override
    public boolean isMultipart() {
        this.checkDestroyed();
        return false;
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
    public HttpPostStandardRequestDecoder offer(HttpContent content) {
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
        return this.currentAttribute;
    }

    private void parseBody() {
        if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
            this.parseBodyAttributes();
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

    private void parseBodyAttributesStandard() {
        int firstpos;
        int currentpos = firstpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
            int ampersandpos;
            block7 : while (this.undecodedChunk.isReadable() && contRead) {
                char read = (char)this.undecodedChunk.readUnsignedByte();
                ++currentpos;
                switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[this.currentStatus.ordinal()]) {
                    case 1: {
                        String key;
                        if (read == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            int equalpos = currentpos - 1;
                            key = HttpPostStandardRequestDecoder.decodeAttribute((String)this.undecodedChunk.toString((int)firstpos, (int)(equalpos - firstpos), (Charset)this.charset), (Charset)this.charset);
                            this.currentAttribute = this.factory.createAttribute((HttpRequest)this.request, (String)key);
                            firstpos = currentpos;
                            continue block7;
                        }
                        if (read != '&') continue block7;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                        ampersandpos = currentpos - 1;
                        key = HttpPostStandardRequestDecoder.decodeAttribute((String)this.undecodedChunk.toString((int)firstpos, (int)(ampersandpos - firstpos), (Charset)this.charset), (Charset)this.charset);
                        this.currentAttribute = this.factory.createAttribute((HttpRequest)this.request, (String)key);
                        this.currentAttribute.setValue((String)"");
                        this.addHttpData((InterfaceHttpData)this.currentAttribute);
                        this.currentAttribute = null;
                        firstpos = currentpos;
                        contRead = true;
                        continue block7;
                    }
                    case 2: {
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            ampersandpos = currentpos - 1;
                            this.setFinalBuffer((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(ampersandpos - firstpos)));
                            firstpos = currentpos;
                            contRead = true;
                            continue block7;
                        }
                        if (read == '\r') {
                            if (this.undecodedChunk.isReadable()) {
                                read = (char)this.undecodedChunk.readUnsignedByte();
                                if (read != '\n') throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)"Bad end of line");
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                ampersandpos = ++currentpos - 2;
                                this.setFinalBuffer((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(ampersandpos - firstpos)));
                                firstpos = currentpos;
                                contRead = false;
                                continue block7;
                            }
                            --currentpos;
                            continue block7;
                        }
                        if (read != '\n') continue block7;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                        ampersandpos = currentpos - 1;
                        this.setFinalBuffer((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(ampersandpos - firstpos)));
                        firstpos = currentpos;
                        contRead = false;
                        continue block7;
                    }
                }
                contRead = false;
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(ampersandpos - firstpos)));
                } else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer((ByteBuf)Unpooled.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            } else if (contRead && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                this.currentAttribute.addContent((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(currentpos - firstpos)), (boolean)false);
                firstpos = currentpos;
            }
            this.undecodedChunk.readerIndex((int)firstpos);
            return;
        }
        catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex((int)firstpos);
            throw e;
        }
        catch (IOException e) {
            this.undecodedChunk.readerIndex((int)firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
        }
    }

    private void parseBodyAttributes() {
        int firstpos;
        if (!this.undecodedChunk.hasArray()) {
            this.parseBodyAttributesStandard();
            return;
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize((ByteBuf)this.undecodedChunk);
        int currentpos = firstpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
            int ampersandpos;
            block8 : while (sao.pos < sao.limit) {
                char read = (char)(sao.bytes[sao.pos++] & 255);
                ++currentpos;
                switch (1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus[this.currentStatus.ordinal()]) {
                    case 1: {
                        String key;
                        if (read == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            int equalpos = currentpos - 1;
                            key = HttpPostStandardRequestDecoder.decodeAttribute((String)this.undecodedChunk.toString((int)firstpos, (int)(equalpos - firstpos), (Charset)this.charset), (Charset)this.charset);
                            this.currentAttribute = this.factory.createAttribute((HttpRequest)this.request, (String)key);
                            firstpos = currentpos;
                            continue block8;
                        }
                        if (read != '&') continue block8;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                        ampersandpos = currentpos - 1;
                        key = HttpPostStandardRequestDecoder.decodeAttribute((String)this.undecodedChunk.toString((int)firstpos, (int)(ampersandpos - firstpos), (Charset)this.charset), (Charset)this.charset);
                        this.currentAttribute = this.factory.createAttribute((HttpRequest)this.request, (String)key);
                        this.currentAttribute.setValue((String)"");
                        this.addHttpData((InterfaceHttpData)this.currentAttribute);
                        this.currentAttribute = null;
                        firstpos = currentpos;
                        contRead = true;
                        continue block8;
                    }
                    case 2: {
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            ampersandpos = currentpos - 1;
                            this.setFinalBuffer((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(ampersandpos - firstpos)));
                            firstpos = currentpos;
                            contRead = true;
                            continue block8;
                        }
                        if (read == '\r') {
                            if (sao.pos < sao.limit) {
                                read = (char)(sao.bytes[sao.pos++] & 255);
                                ++currentpos;
                                if (read != '\n') {
                                    sao.setReadPosition((int)0);
                                    throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)"Bad end of line");
                                }
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                ampersandpos = currentpos - 2;
                                sao.setReadPosition((int)0);
                                this.setFinalBuffer((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(ampersandpos - firstpos)));
                                firstpos = currentpos;
                                contRead = false;
                                break block8;
                            }
                            if (sao.limit <= 0) continue block8;
                            --currentpos;
                            continue block8;
                        }
                        if (read != '\n') continue block8;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                        ampersandpos = currentpos - 1;
                        sao.setReadPosition((int)0);
                        this.setFinalBuffer((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(ampersandpos - firstpos)));
                        firstpos = currentpos;
                        contRead = false;
                        break block8;
                    }
                }
                sao.setReadPosition((int)0);
                contRead = false;
                break;
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(ampersandpos - firstpos)));
                } else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer((ByteBuf)Unpooled.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            } else if (contRead && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                this.currentAttribute.addContent((ByteBuf)this.undecodedChunk.copy((int)firstpos, (int)(currentpos - firstpos)), (boolean)false);
                firstpos = currentpos;
            }
            this.undecodedChunk.readerIndex((int)firstpos);
            return;
        }
        catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex((int)firstpos);
            throw e;
        }
        catch (IOException e) {
            this.undecodedChunk.readerIndex((int)firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
        }
        catch (IllegalArgumentException e) {
            this.undecodedChunk.readerIndex((int)firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException((Throwable)e);
        }
    }

    private void setFinalBuffer(ByteBuf buffer) throws IOException {
        this.currentAttribute.addContent((ByteBuf)buffer, (boolean)true);
        String value = HttpPostStandardRequestDecoder.decodeAttribute((String)this.currentAttribute.getByteBuf().toString((Charset)this.charset), (Charset)this.charset);
        this.currentAttribute.setValue((String)value);
        this.addHttpData((InterfaceHttpData)this.currentAttribute);
        this.currentAttribute = null;
    }

    private static String decodeAttribute(String s, Charset charset) {
        try {
            return QueryStringDecoder.decodeComponent((String)s, (Charset)charset);
        }
        catch (IllegalArgumentException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException((String)("Bad string: '" + s + '\''), (Throwable)e);
        }
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
}

