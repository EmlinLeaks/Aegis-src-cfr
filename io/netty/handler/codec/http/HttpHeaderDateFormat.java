/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpHeaderDateFormat;
import io.netty.util.concurrent.FastThreadLocal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Deprecated
public final class HttpHeaderDateFormat
extends SimpleDateFormat {
    private static final long serialVersionUID = -925286159755905325L;
    private final SimpleDateFormat format1 = new HttpHeaderDateFormatObsolete1();
    private final SimpleDateFormat format2 = new HttpHeaderDateFormatObsolete2();
    private static final FastThreadLocal<HttpHeaderDateFormat> dateFormatThreadLocal = new FastThreadLocal<HttpHeaderDateFormat>(){

        protected HttpHeaderDateFormat initialValue() {
            return new HttpHeaderDateFormat();
        }
    };

    public static HttpHeaderDateFormat get() {
        return dateFormatThreadLocal.get();
    }

    private HttpHeaderDateFormat() {
        super((String)"E, dd MMM yyyy HH:mm:ss z", (Locale)Locale.ENGLISH);
        this.setTimeZone((TimeZone)TimeZone.getTimeZone((String)"GMT"));
    }

    @Override
    public Date parse(String text, ParsePosition pos) {
        Date date = super.parse((String)text, (ParsePosition)pos);
        if (date == null) {
            date = this.format1.parse((String)text, (ParsePosition)pos);
        }
        if (date != null) return date;
        return this.format2.parse((String)text, (ParsePosition)pos);
    }
}

