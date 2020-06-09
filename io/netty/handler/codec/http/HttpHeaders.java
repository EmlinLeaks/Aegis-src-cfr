/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class HttpHeaders
implements Iterable<Map.Entry<String, String>> {
    @Deprecated
    public static final HttpHeaders EMPTY_HEADERS = EmptyHttpHeaders.instance();

    @Deprecated
    public static boolean isKeepAlive(HttpMessage message) {
        return HttpUtil.isKeepAlive((HttpMessage)message);
    }

    @Deprecated
    public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
        HttpUtil.setKeepAlive((HttpMessage)message, (boolean)keepAlive);
    }

    @Deprecated
    public static String getHeader(HttpMessage message, String name) {
        return message.headers().get((String)name);
    }

    @Deprecated
    public static String getHeader(HttpMessage message, CharSequence name) {
        return message.headers().get((CharSequence)name);
    }

    @Deprecated
    public static String getHeader(HttpMessage message, String name, String defaultValue) {
        return message.headers().get((CharSequence)name, (String)defaultValue);
    }

    @Deprecated
    public static String getHeader(HttpMessage message, CharSequence name, String defaultValue) {
        return message.headers().get((CharSequence)name, (String)defaultValue);
    }

    @Deprecated
    public static void setHeader(HttpMessage message, String name, Object value) {
        message.headers().set((String)name, (Object)value);
    }

    @Deprecated
    public static void setHeader(HttpMessage message, CharSequence name, Object value) {
        message.headers().set((CharSequence)name, (Object)value);
    }

    @Deprecated
    public static void setHeader(HttpMessage message, String name, Iterable<?> values) {
        message.headers().set((String)name, values);
    }

    @Deprecated
    public static void setHeader(HttpMessage message, CharSequence name, Iterable<?> values) {
        message.headers().set((CharSequence)name, values);
    }

    @Deprecated
    public static void addHeader(HttpMessage message, String name, Object value) {
        message.headers().add((String)name, (Object)value);
    }

    @Deprecated
    public static void addHeader(HttpMessage message, CharSequence name, Object value) {
        message.headers().add((CharSequence)name, (Object)value);
    }

    @Deprecated
    public static void removeHeader(HttpMessage message, String name) {
        message.headers().remove((String)name);
    }

    @Deprecated
    public static void removeHeader(HttpMessage message, CharSequence name) {
        message.headers().remove((CharSequence)name);
    }

    @Deprecated
    public static void clearHeaders(HttpMessage message) {
        message.headers().clear();
    }

    @Deprecated
    public static int getIntHeader(HttpMessage message, String name) {
        return HttpHeaders.getIntHeader((HttpMessage)message, (CharSequence)name);
    }

    @Deprecated
    public static int getIntHeader(HttpMessage message, CharSequence name) {
        String value = message.headers().get((CharSequence)name);
        if (value != null) return Integer.parseInt((String)value);
        throw new NumberFormatException((String)("header not found: " + name));
    }

    @Deprecated
    public static int getIntHeader(HttpMessage message, String name, int defaultValue) {
        return message.headers().getInt((CharSequence)name, (int)defaultValue);
    }

    @Deprecated
    public static int getIntHeader(HttpMessage message, CharSequence name, int defaultValue) {
        return message.headers().getInt((CharSequence)name, (int)defaultValue);
    }

    @Deprecated
    public static void setIntHeader(HttpMessage message, String name, int value) {
        message.headers().setInt((CharSequence)name, (int)value);
    }

    @Deprecated
    public static void setIntHeader(HttpMessage message, CharSequence name, int value) {
        message.headers().setInt((CharSequence)name, (int)value);
    }

    @Deprecated
    public static void setIntHeader(HttpMessage message, String name, Iterable<Integer> values) {
        message.headers().set((String)name, values);
    }

    @Deprecated
    public static void setIntHeader(HttpMessage message, CharSequence name, Iterable<Integer> values) {
        message.headers().set((CharSequence)name, values);
    }

    @Deprecated
    public static void addIntHeader(HttpMessage message, String name, int value) {
        message.headers().add((String)name, (Object)Integer.valueOf((int)value));
    }

    @Deprecated
    public static void addIntHeader(HttpMessage message, CharSequence name, int value) {
        message.headers().addInt((CharSequence)name, (int)value);
    }

    @Deprecated
    public static Date getDateHeader(HttpMessage message, String name) throws ParseException {
        return HttpHeaders.getDateHeader((HttpMessage)message, (CharSequence)name);
    }

    @Deprecated
    public static Date getDateHeader(HttpMessage message, CharSequence name) throws ParseException {
        String value = message.headers().get((CharSequence)name);
        if (value == null) {
            throw new ParseException((String)("header not found: " + name), (int)0);
        }
        Date date = DateFormatter.parseHttpDate((CharSequence)value);
        if (date != null) return date;
        throw new ParseException((String)("header can't be parsed into a Date: " + value), (int)0);
    }

    @Deprecated
    public static Date getDateHeader(HttpMessage message, String name, Date defaultValue) {
        return HttpHeaders.getDateHeader((HttpMessage)message, (CharSequence)name, (Date)defaultValue);
    }

    @Deprecated
    public static Date getDateHeader(HttpMessage message, CharSequence name, Date defaultValue) {
        Date date;
        String value = HttpHeaders.getHeader((HttpMessage)message, (CharSequence)name);
        Date date2 = DateFormatter.parseHttpDate((CharSequence)value);
        if (date2 != null) {
            date = date2;
            return date;
        }
        date = defaultValue;
        return date;
    }

    @Deprecated
    public static void setDateHeader(HttpMessage message, String name, Date value) {
        HttpHeaders.setDateHeader((HttpMessage)message, (CharSequence)name, (Date)value);
    }

    @Deprecated
    public static void setDateHeader(HttpMessage message, CharSequence name, Date value) {
        if (value != null) {
            message.headers().set((CharSequence)name, (Object)DateFormatter.format((Date)value));
            return;
        }
        message.headers().set((CharSequence)name, null);
    }

    @Deprecated
    public static void setDateHeader(HttpMessage message, String name, Iterable<Date> values) {
        message.headers().set((String)name, values);
    }

    @Deprecated
    public static void setDateHeader(HttpMessage message, CharSequence name, Iterable<Date> values) {
        message.headers().set((CharSequence)name, values);
    }

    @Deprecated
    public static void addDateHeader(HttpMessage message, String name, Date value) {
        message.headers().add((String)name, (Object)value);
    }

    @Deprecated
    public static void addDateHeader(HttpMessage message, CharSequence name, Date value) {
        message.headers().add((CharSequence)name, (Object)value);
    }

    @Deprecated
    public static long getContentLength(HttpMessage message) {
        return HttpUtil.getContentLength((HttpMessage)message);
    }

    @Deprecated
    public static long getContentLength(HttpMessage message, long defaultValue) {
        return HttpUtil.getContentLength((HttpMessage)message, (long)defaultValue);
    }

    @Deprecated
    public static void setContentLength(HttpMessage message, long length) {
        HttpUtil.setContentLength((HttpMessage)message, (long)length);
    }

    @Deprecated
    public static String getHost(HttpMessage message) {
        return message.headers().get((CharSequence)HttpHeaderNames.HOST);
    }

    @Deprecated
    public static String getHost(HttpMessage message, String defaultValue) {
        return message.headers().get((CharSequence)HttpHeaderNames.HOST, (String)defaultValue);
    }

    @Deprecated
    public static void setHost(HttpMessage message, String value) {
        message.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)value);
    }

    @Deprecated
    public static void setHost(HttpMessage message, CharSequence value) {
        message.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)value);
    }

    @Deprecated
    public static Date getDate(HttpMessage message) throws ParseException {
        return HttpHeaders.getDateHeader((HttpMessage)message, (CharSequence)HttpHeaderNames.DATE);
    }

    @Deprecated
    public static Date getDate(HttpMessage message, Date defaultValue) {
        return HttpHeaders.getDateHeader((HttpMessage)message, (CharSequence)HttpHeaderNames.DATE, (Date)defaultValue);
    }

    @Deprecated
    public static void setDate(HttpMessage message, Date value) {
        message.headers().set((CharSequence)HttpHeaderNames.DATE, (Object)value);
    }

    @Deprecated
    public static boolean is100ContinueExpected(HttpMessage message) {
        return HttpUtil.is100ContinueExpected((HttpMessage)message);
    }

    @Deprecated
    public static void set100ContinueExpected(HttpMessage message) {
        HttpUtil.set100ContinueExpected((HttpMessage)message, (boolean)true);
    }

    @Deprecated
    public static void set100ContinueExpected(HttpMessage message, boolean set) {
        HttpUtil.set100ContinueExpected((HttpMessage)message, (boolean)set);
    }

    @Deprecated
    public static boolean isTransferEncodingChunked(HttpMessage message) {
        return HttpUtil.isTransferEncodingChunked((HttpMessage)message);
    }

    @Deprecated
    public static void removeTransferEncodingChunked(HttpMessage m) {
        HttpUtil.setTransferEncodingChunked((HttpMessage)m, (boolean)false);
    }

    @Deprecated
    public static void setTransferEncodingChunked(HttpMessage m) {
        HttpUtil.setTransferEncodingChunked((HttpMessage)m, (boolean)true);
    }

    @Deprecated
    public static boolean isContentLengthSet(HttpMessage m) {
        return HttpUtil.isContentLengthSet((HttpMessage)m);
    }

    @Deprecated
    public static boolean equalsIgnoreCase(CharSequence name1, CharSequence name2) {
        return AsciiString.contentEqualsIgnoreCase((CharSequence)name1, (CharSequence)name2);
    }

    @Deprecated
    public static void encodeAscii(CharSequence seq, ByteBuf buf) {
        if (seq instanceof AsciiString) {
            ByteBufUtil.copy((AsciiString)((AsciiString)seq), (int)0, (ByteBuf)buf, (int)seq.length());
            return;
        }
        buf.writeCharSequence((CharSequence)seq, (Charset)CharsetUtil.US_ASCII);
    }

    @Deprecated
    public static CharSequence newEntity(String name) {
        return new AsciiString((CharSequence)name);
    }

    protected HttpHeaders() {
    }

    public abstract String get(String var1);

    public String get(CharSequence name) {
        return this.get((String)name.toString());
    }

    public String get(CharSequence name, String defaultValue) {
        String value = this.get((CharSequence)name);
        if (value != null) return value;
        return defaultValue;
    }

    public abstract Integer getInt(CharSequence var1);

    public abstract int getInt(CharSequence var1, int var2);

    public abstract Short getShort(CharSequence var1);

    public abstract short getShort(CharSequence var1, short var2);

    public abstract Long getTimeMillis(CharSequence var1);

    public abstract long getTimeMillis(CharSequence var1, long var2);

    public abstract List<String> getAll(String var1);

    public List<String> getAll(CharSequence name) {
        return this.getAll((String)name.toString());
    }

    public abstract List<Map.Entry<String, String>> entries();

    public abstract boolean contains(String var1);

    @Deprecated
    @Override
    public abstract Iterator<Map.Entry<String, String>> iterator();

    public abstract Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence();

    public Iterator<String> valueStringIterator(CharSequence name) {
        return this.getAll((CharSequence)name).iterator();
    }

    public Iterator<? extends CharSequence> valueCharSequenceIterator(CharSequence name) {
        return this.valueStringIterator((CharSequence)name);
    }

    public boolean contains(CharSequence name) {
        return this.contains((String)name.toString());
    }

    public abstract boolean isEmpty();

    public abstract int size();

    public abstract Set<String> names();

    public abstract HttpHeaders add(String var1, Object var2);

    public HttpHeaders add(CharSequence name, Object value) {
        return this.add((String)name.toString(), (Object)value);
    }

    public abstract HttpHeaders add(String var1, Iterable<?> var2);

    public HttpHeaders add(CharSequence name, Iterable<?> values) {
        return this.add((String)name.toString(), values);
    }

    public HttpHeaders add(HttpHeaders headers) {
        if (headers == null) {
            throw new NullPointerException((String)"headers");
        }
        Iterator<Map.Entry<String, String>> iterator = headers.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> e = iterator.next();
            this.add((String)e.getKey(), (Object)e.getValue());
        }
        return this;
    }

    public abstract HttpHeaders addInt(CharSequence var1, int var2);

    public abstract HttpHeaders addShort(CharSequence var1, short var2);

    public abstract HttpHeaders set(String var1, Object var2);

    public HttpHeaders set(CharSequence name, Object value) {
        return this.set((String)name.toString(), (Object)value);
    }

    public abstract HttpHeaders set(String var1, Iterable<?> var2);

    public HttpHeaders set(CharSequence name, Iterable<?> values) {
        return this.set((String)name.toString(), values);
    }

    public HttpHeaders set(HttpHeaders headers) {
        ObjectUtil.checkNotNull(headers, (String)"headers");
        this.clear();
        if (headers.isEmpty()) {
            return this;
        }
        Iterator<Map.Entry<String, String>> iterator = headers.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            this.add((String)entry.getKey(), (Object)entry.getValue());
        }
        return this;
    }

    public HttpHeaders setAll(HttpHeaders headers) {
        ObjectUtil.checkNotNull(headers, (String)"headers");
        if (headers.isEmpty()) {
            return this;
        }
        Iterator<Map.Entry<String, String>> iterator = headers.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            this.set((String)entry.getKey(), (Object)entry.getValue());
        }
        return this;
    }

    public abstract HttpHeaders setInt(CharSequence var1, int var2);

    public abstract HttpHeaders setShort(CharSequence var1, short var2);

    public abstract HttpHeaders remove(String var1);

    public HttpHeaders remove(CharSequence name) {
        return this.remove((String)name.toString());
    }

    public abstract HttpHeaders clear();

    public boolean contains(String name, String value, boolean ignoreCase) {
        Iterator<String> valueIterator = this.valueStringIterator((CharSequence)name);
        if (ignoreCase) {
            do {
                if (!valueIterator.hasNext()) return false;
            } while (!valueIterator.next().equalsIgnoreCase((String)value));
            return true;
        }
        do {
            if (!valueIterator.hasNext()) return false;
        } while (!valueIterator.next().equals((Object)value));
        return true;
    }

    public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
        Iterator<? extends CharSequence> itr = this.valueCharSequenceIterator((CharSequence)name);
        do {
            if (!itr.hasNext()) return false;
        } while (!HttpHeaders.containsCommaSeparatedTrimmed((CharSequence)itr.next(), (CharSequence)value, (boolean)ignoreCase));
        return true;
    }

    private static boolean containsCommaSeparatedTrimmed(CharSequence rawNext, CharSequence expected, boolean ignoreCase) {
        int end;
        int begin = 0;
        if (!ignoreCase) {
            end = AsciiString.indexOf((CharSequence)rawNext, (char)',', (int)begin);
            if (end == -1) {
                if (!AsciiString.contentEquals((CharSequence)AsciiString.trim((CharSequence)rawNext), (CharSequence)expected)) return false;
                return true;
            }
        } else {
            int end2 = AsciiString.indexOf((CharSequence)rawNext, (char)',', (int)begin);
            if (end2 == -1) {
                if (!AsciiString.contentEqualsIgnoreCase((CharSequence)AsciiString.trim((CharSequence)rawNext), (CharSequence)expected)) return false;
                return true;
            }
            do {
                if (AsciiString.contentEqualsIgnoreCase((CharSequence)AsciiString.trim((CharSequence)rawNext.subSequence((int)begin, (int)end2)), (CharSequence)expected)) {
                    return true;
                }
                begin = end2 + 1;
            } while ((end2 = AsciiString.indexOf((CharSequence)rawNext, (char)',', (int)begin)) != -1);
            if (begin >= rawNext.length()) return false;
            if (!AsciiString.contentEqualsIgnoreCase((CharSequence)AsciiString.trim((CharSequence)rawNext.subSequence((int)begin, (int)rawNext.length())), (CharSequence)expected)) return false;
            return true;
        }
        do {
            if (AsciiString.contentEquals((CharSequence)AsciiString.trim((CharSequence)rawNext.subSequence((int)begin, (int)end)), (CharSequence)expected)) {
                return true;
            }
            begin = end + 1;
        } while ((end = AsciiString.indexOf((CharSequence)rawNext, (char)',', (int)begin)) != -1);
        if (begin >= rawNext.length()) return false;
        if (!AsciiString.contentEquals((CharSequence)AsciiString.trim((CharSequence)rawNext.subSequence((int)begin, (int)rawNext.length())), (CharSequence)expected)) return false;
        return true;
    }

    public final String getAsString(CharSequence name) {
        return this.get((CharSequence)name);
    }

    public final List<String> getAllAsString(CharSequence name) {
        return this.getAll((CharSequence)name);
    }

    public final Iterator<Map.Entry<String, String>> iteratorAsString() {
        return this.iterator();
    }

    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        return this.contains((String)name.toString(), (String)value.toString(), (boolean)ignoreCase);
    }

    public String toString() {
        return HeadersUtils.toString(this.getClass(), this.iteratorCharSequence(), (int)this.size());
    }

    public HttpHeaders copy() {
        return new DefaultHttpHeaders().set((HttpHeaders)this);
    }
}

