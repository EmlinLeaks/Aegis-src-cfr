/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpVersion
implements Comparable<HttpVersion> {
    private static final Pattern VERSION_PATTERN = Pattern.compile((String)"(\\S+)/(\\d+)\\.(\\d+)");
    private static final String HTTP_1_0_STRING = "HTTP/1.0";
    private static final String HTTP_1_1_STRING = "HTTP/1.1";
    public static final HttpVersion HTTP_1_0 = new HttpVersion((String)"HTTP", (int)1, (int)0, (boolean)false, (boolean)true);
    public static final HttpVersion HTTP_1_1 = new HttpVersion((String)"HTTP", (int)1, (int)1, (boolean)true, (boolean)true);
    private final String protocolName;
    private final int majorVersion;
    private final int minorVersion;
    private final String text;
    private final boolean keepAliveDefault;
    private final byte[] bytes;

    public static HttpVersion valueOf(String text) {
        if (text == null) {
            throw new NullPointerException((String)"text");
        }
        if ((text = text.trim()).isEmpty()) {
            throw new IllegalArgumentException((String)"text is empty (possibly HTTP/0.9)");
        }
        HttpVersion version = HttpVersion.version0((String)text);
        if (version != null) return version;
        return new HttpVersion((String)text, (boolean)true);
    }

    private static HttpVersion version0(String text) {
        if (HTTP_1_1_STRING.equals((Object)text)) {
            return HTTP_1_1;
        }
        if (!HTTP_1_0_STRING.equals((Object)text)) return null;
        return HTTP_1_0;
    }

    public HttpVersion(String text, boolean keepAliveDefault) {
        if (text == null) {
            throw new NullPointerException((String)"text");
        }
        if ((text = text.trim().toUpperCase()).isEmpty()) {
            throw new IllegalArgumentException((String)"empty text");
        }
        Matcher m = VERSION_PATTERN.matcher((CharSequence)text);
        if (!m.matches()) {
            throw new IllegalArgumentException((String)("invalid version format: " + text));
        }
        this.protocolName = m.group((int)1);
        this.majorVersion = Integer.parseInt((String)m.group((int)2));
        this.minorVersion = Integer.parseInt((String)m.group((int)3));
        this.text = this.protocolName + '/' + this.majorVersion + '.' + this.minorVersion;
        this.keepAliveDefault = keepAliveDefault;
        this.bytes = null;
    }

    public HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault) {
        this((String)protocolName, (int)majorVersion, (int)minorVersion, (boolean)keepAliveDefault, (boolean)false);
    }

    private HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault, boolean bytes) {
        if (protocolName == null) {
            throw new NullPointerException((String)"protocolName");
        }
        if ((protocolName = protocolName.trim().toUpperCase()).isEmpty()) {
            throw new IllegalArgumentException((String)"empty protocolName");
        }
        for (int i = 0; i < protocolName.length(); ++i) {
            if (Character.isISOControl((char)protocolName.charAt((int)i))) throw new IllegalArgumentException((String)"invalid character in protocolName");
            if (!Character.isWhitespace((char)protocolName.charAt((int)i))) continue;
            throw new IllegalArgumentException((String)"invalid character in protocolName");
        }
        ObjectUtil.checkPositiveOrZero((int)majorVersion, (String)"majorVersion");
        ObjectUtil.checkPositiveOrZero((int)minorVersion, (String)"minorVersion");
        this.protocolName = protocolName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.text = protocolName + '/' + majorVersion + '.' + minorVersion;
        this.keepAliveDefault = keepAliveDefault;
        if (bytes) {
            this.bytes = this.text.getBytes((Charset)CharsetUtil.US_ASCII);
            return;
        }
        this.bytes = null;
    }

    public String protocolName() {
        return this.protocolName;
    }

    public int majorVersion() {
        return this.majorVersion;
    }

    public int minorVersion() {
        return this.minorVersion;
    }

    public String text() {
        return this.text;
    }

    public boolean isKeepAliveDefault() {
        return this.keepAliveDefault;
    }

    public String toString() {
        return this.text();
    }

    public int hashCode() {
        return (this.protocolName().hashCode() * 31 + this.majorVersion()) * 31 + this.minorVersion();
    }

    public boolean equals(Object o) {
        if (!(o instanceof HttpVersion)) {
            return false;
        }
        HttpVersion that = (HttpVersion)o;
        if (this.minorVersion() != that.minorVersion()) return false;
        if (this.majorVersion() != that.majorVersion()) return false;
        if (!this.protocolName().equals((Object)that.protocolName())) return false;
        return true;
    }

    @Override
    public int compareTo(HttpVersion o) {
        int v = this.protocolName().compareTo((String)o.protocolName());
        if (v != 0) {
            return v;
        }
        v = this.majorVersion() - o.majorVersion();
        if (v == 0) return this.minorVersion() - o.minorVersion();
        return v;
    }

    void encode(ByteBuf buf) {
        if (this.bytes == null) {
            buf.writeCharSequence((CharSequence)this.text, (Charset)CharsetUtil.US_ASCII);
            return;
        }
        buf.writeBytes((byte[])this.bytes);
    }
}

