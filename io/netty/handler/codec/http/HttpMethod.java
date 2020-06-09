/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;

public class HttpMethod
implements Comparable<HttpMethod> {
    public static final HttpMethod OPTIONS = new HttpMethod((String)"OPTIONS");
    public static final HttpMethod GET = new HttpMethod((String)"GET");
    public static final HttpMethod HEAD = new HttpMethod((String)"HEAD");
    public static final HttpMethod POST = new HttpMethod((String)"POST");
    public static final HttpMethod PUT = new HttpMethod((String)"PUT");
    public static final HttpMethod PATCH = new HttpMethod((String)"PATCH");
    public static final HttpMethod DELETE = new HttpMethod((String)"DELETE");
    public static final HttpMethod TRACE = new HttpMethod((String)"TRACE");
    public static final HttpMethod CONNECT = new HttpMethod((String)"CONNECT");
    private static final EnumNameMap<HttpMethod> methodMap = new EnumNameMap<T>(new EnumNameMap.Node<HttpMethod>((String)OPTIONS.toString(), OPTIONS), new EnumNameMap.Node<HttpMethod>((String)GET.toString(), GET), new EnumNameMap.Node<HttpMethod>((String)HEAD.toString(), HEAD), new EnumNameMap.Node<HttpMethod>((String)POST.toString(), POST), new EnumNameMap.Node<HttpMethod>((String)PUT.toString(), PUT), new EnumNameMap.Node<HttpMethod>((String)PATCH.toString(), PATCH), new EnumNameMap.Node<HttpMethod>((String)DELETE.toString(), DELETE), new EnumNameMap.Node<HttpMethod>((String)TRACE.toString(), TRACE), new EnumNameMap.Node<HttpMethod>((String)CONNECT.toString(), CONNECT));
    private final AsciiString name;

    public static HttpMethod valueOf(String name) {
        HttpMethod httpMethod;
        HttpMethod result = methodMap.get((String)name);
        if (result != null) {
            httpMethod = result;
            return httpMethod;
        }
        httpMethod = new HttpMethod((String)name);
        return httpMethod;
    }

    public HttpMethod(String name) {
        name = ObjectUtil.checkNotNull(name, (String)"name").trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException((String)"empty name");
        }
        int i = 0;
        do {
            if (i >= name.length()) {
                this.name = AsciiString.cached((String)name);
                return;
            }
            char c = name.charAt((int)i);
            if (Character.isISOControl((char)c)) throw new IllegalArgumentException((String)"invalid character in name");
            if (Character.isWhitespace((char)c)) {
                throw new IllegalArgumentException((String)"invalid character in name");
            }
            ++i;
        } while (true);
    }

    public String name() {
        return this.name.toString();
    }

    public AsciiString asciiName() {
        return this.name;
    }

    public int hashCode() {
        return this.name().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpMethod)) {
            return false;
        }
        HttpMethod that = (HttpMethod)o;
        return this.name().equals((Object)that.name());
    }

    public String toString() {
        return this.name.toString();
    }

    @Override
    public int compareTo(HttpMethod o) {
        if (o != this) return this.name().compareTo((String)o.name());
        return 0;
    }
}

