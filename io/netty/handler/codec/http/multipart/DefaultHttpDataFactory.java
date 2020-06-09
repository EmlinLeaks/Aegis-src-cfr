/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.codec.http.multipart.MemoryFileUpload;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultHttpDataFactory
implements HttpDataFactory {
    public static final long MINSIZE = 16384L;
    public static final long MAXSIZE = -1L;
    private final boolean useDisk;
    private final boolean checkSize;
    private long minSize;
    private long maxSize = -1L;
    private Charset charset = HttpConstants.DEFAULT_CHARSET;
    private final Map<HttpRequest, List<HttpData>> requestFileDeleteMap = Collections.synchronizedMap(new IdentityHashMap<K, V>());

    public DefaultHttpDataFactory() {
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = 16384L;
    }

    public DefaultHttpDataFactory(Charset charset) {
        this();
        this.charset = charset;
    }

    public DefaultHttpDataFactory(boolean useDisk) {
        this.useDisk = useDisk;
        this.checkSize = false;
    }

    public DefaultHttpDataFactory(boolean useDisk, Charset charset) {
        this((boolean)useDisk);
        this.charset = charset;
    }

    public DefaultHttpDataFactory(long minSize) {
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = minSize;
    }

    public DefaultHttpDataFactory(long minSize, Charset charset) {
        this((long)minSize);
        this.charset = charset;
    }

    @Override
    public void setMaxLimit(long maxSize) {
        this.maxSize = maxSize;
    }

    private List<HttpData> getList(HttpRequest request) {
        List<HttpData> list = this.requestFileDeleteMap.get((Object)request);
        if (list != null) return list;
        list = new ArrayList<HttpData>();
        this.requestFileDeleteMap.put((HttpRequest)request, list);
        return list;
    }

    @Override
    public Attribute createAttribute(HttpRequest request, String name) {
        if (this.useDisk) {
            DiskAttribute attribute = new DiskAttribute((String)name, (Charset)this.charset);
            attribute.setMaxSize((long)this.maxSize);
            List<HttpData> list = this.getList((HttpRequest)request);
            list.add((HttpData)attribute);
            return attribute;
        }
        if (this.checkSize) {
            MixedAttribute attribute = new MixedAttribute((String)name, (long)this.minSize, (Charset)this.charset);
            attribute.setMaxSize((long)this.maxSize);
            List<HttpData> list = this.getList((HttpRequest)request);
            list.add((HttpData)attribute);
            return attribute;
        }
        MemoryAttribute attribute = new MemoryAttribute((String)name);
        attribute.setMaxSize((long)this.maxSize);
        return attribute;
    }

    @Override
    public Attribute createAttribute(HttpRequest request, String name, long definedSize) {
        if (this.useDisk) {
            DiskAttribute attribute = new DiskAttribute((String)name, (long)definedSize, (Charset)this.charset);
            attribute.setMaxSize((long)this.maxSize);
            List<HttpData> list = this.getList((HttpRequest)request);
            list.add((HttpData)attribute);
            return attribute;
        }
        if (this.checkSize) {
            MixedAttribute attribute = new MixedAttribute((String)name, (long)definedSize, (long)this.minSize, (Charset)this.charset);
            attribute.setMaxSize((long)this.maxSize);
            List<HttpData> list = this.getList((HttpRequest)request);
            list.add((HttpData)attribute);
            return attribute;
        }
        MemoryAttribute attribute = new MemoryAttribute((String)name, (long)definedSize);
        attribute.setMaxSize((long)this.maxSize);
        return attribute;
    }

    private static void checkHttpDataSize(HttpData data) {
        try {
            data.checkSize((long)data.length());
            return;
        }
        catch (IOException ignored) {
            throw new IllegalArgumentException((String)"Attribute bigger than maxSize allowed");
        }
    }

    @Override
    public Attribute createAttribute(HttpRequest request, String name, String value) {
        if (this.useDisk) {
            Attribute attribute;
            try {
                attribute = new DiskAttribute((String)name, (String)value, (Charset)this.charset);
                attribute.setMaxSize((long)this.maxSize);
            }
            catch (IOException e) {
                attribute = new MixedAttribute((String)name, (String)value, (long)this.minSize, (Charset)this.charset);
                attribute.setMaxSize((long)this.maxSize);
            }
            DefaultHttpDataFactory.checkHttpDataSize((HttpData)attribute);
            List<HttpData> list = this.getList((HttpRequest)request);
            list.add((HttpData)attribute);
            return attribute;
        }
        if (this.checkSize) {
            MixedAttribute attribute = new MixedAttribute((String)name, (String)value, (long)this.minSize, (Charset)this.charset);
            attribute.setMaxSize((long)this.maxSize);
            DefaultHttpDataFactory.checkHttpDataSize((HttpData)attribute);
            List<HttpData> list = this.getList((HttpRequest)request);
            list.add((HttpData)attribute);
            return attribute;
        }
        try {
            MemoryAttribute attribute = new MemoryAttribute((String)name, (String)value, (Charset)this.charset);
            attribute.setMaxSize((long)this.maxSize);
            DefaultHttpDataFactory.checkHttpDataSize((HttpData)attribute);
            return attribute;
        }
        catch (IOException e) {
            throw new IllegalArgumentException((Throwable)e);
        }
    }

    @Override
    public FileUpload createFileUpload(HttpRequest request, String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
        if (this.useDisk) {
            DiskFileUpload fileUpload = new DiskFileUpload((String)name, (String)filename, (String)contentType, (String)contentTransferEncoding, (Charset)charset, (long)size);
            fileUpload.setMaxSize((long)this.maxSize);
            DefaultHttpDataFactory.checkHttpDataSize((HttpData)fileUpload);
            List<HttpData> list = this.getList((HttpRequest)request);
            list.add((HttpData)fileUpload);
            return fileUpload;
        }
        if (this.checkSize) {
            MixedFileUpload fileUpload = new MixedFileUpload((String)name, (String)filename, (String)contentType, (String)contentTransferEncoding, (Charset)charset, (long)size, (long)this.minSize);
            fileUpload.setMaxSize((long)this.maxSize);
            DefaultHttpDataFactory.checkHttpDataSize((HttpData)fileUpload);
            List<HttpData> list = this.getList((HttpRequest)request);
            list.add((HttpData)fileUpload);
            return fileUpload;
        }
        MemoryFileUpload fileUpload = new MemoryFileUpload((String)name, (String)filename, (String)contentType, (String)contentTransferEncoding, (Charset)charset, (long)size);
        fileUpload.setMaxSize((long)this.maxSize);
        DefaultHttpDataFactory.checkHttpDataSize((HttpData)fileUpload);
        return fileUpload;
    }

    @Override
    public void removeHttpDataFromClean(HttpRequest request, InterfaceHttpData data) {
        HttpData n;
        if (!(data instanceof HttpData)) {
            return;
        }
        List<HttpData> list = this.requestFileDeleteMap.get((Object)request);
        if (list == null) {
            return;
        }
        Iterator<HttpData> i = list.iterator();
        do {
            if (!i.hasNext()) return;
        } while ((n = i.next()) != data);
        i.remove();
        if (!list.isEmpty()) return;
        this.requestFileDeleteMap.remove((Object)request);
    }

    @Override
    public void cleanRequestHttpData(HttpRequest request) {
        List<HttpData> list = this.requestFileDeleteMap.remove((Object)request);
        if (list == null) return;
        Iterator<HttpData> iterator = list.iterator();
        while (iterator.hasNext()) {
            HttpData data = iterator.next();
            data.release();
        }
    }

    @Override
    public void cleanAllHttpData() {
        Iterator<Map.Entry<HttpRequest, List<HttpData>>> i = this.requestFileDeleteMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<HttpRequest, List<HttpData>> e = i.next();
            List<HttpData> list = e.getValue();
            for (HttpData data : list) {
                data.release();
            }
            i.remove();
        }
    }

    @Override
    public void cleanRequestHttpDatas(HttpRequest request) {
        this.cleanRequestHttpData((HttpRequest)request);
    }

    @Override
    public void cleanAllHttpDatas() {
        this.cleanAllHttpData();
    }
}

