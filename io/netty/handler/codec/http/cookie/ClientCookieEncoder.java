/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public final class ClientCookieEncoder
extends CookieEncoder {
    public static final ClientCookieEncoder STRICT = new ClientCookieEncoder((boolean)true);
    public static final ClientCookieEncoder LAX = new ClientCookieEncoder((boolean)false);
    private static final Comparator<Cookie> COOKIE_COMPARATOR = new Comparator<Cookie>(){

        public int compare(Cookie c1, Cookie c2) {
            String path1 = c1.path();
            String path2 = c2.path();
            int len1 = path1 == null ? Integer.MAX_VALUE : path1.length();
            int len2 = path2 == null ? Integer.MAX_VALUE : path2.length();
            int diff = len2 - len1;
            if (diff == 0) return -1;
            return diff;
        }
    };

    private ClientCookieEncoder(boolean strict) {
        super((boolean)strict);
    }

    public String encode(String name, String value) {
        return this.encode((Cookie)new DefaultCookie((String)name, (String)value));
    }

    public String encode(Cookie cookie) {
        StringBuilder buf = CookieUtil.stringBuilder();
        this.encode((StringBuilder)buf, (Cookie)ObjectUtil.checkNotNull(cookie, (String)"cookie"));
        return CookieUtil.stripTrailingSeparator((StringBuilder)buf);
    }

    public String encode(Cookie ... cookies) {
        if (ObjectUtil.checkNotNull(cookies, (String)"cookies").length == 0) {
            return null;
        }
        StringBuilder buf = CookieUtil.stringBuilder();
        if (this.strict) {
            if (cookies.length == 1) {
                this.encode((StringBuilder)buf, (Cookie)cookies[0]);
                return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
            }
            Cookie[] cookiesSorted = Arrays.copyOf(cookies, (int)cookies.length);
            Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
            Cookie[] arrcookie = cookiesSorted;
            int n = arrcookie.length;
            int n2 = 0;
            while (n2 < n) {
                Cookie c = arrcookie[n2];
                this.encode((StringBuilder)buf, (Cookie)c);
                ++n2;
            }
            return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
        }
        Cookie[] cookiesSorted = cookies;
        int n = cookiesSorted.length;
        int n3 = 0;
        while (n3 < n) {
            Cookie c = cookiesSorted[n3];
            this.encode((StringBuilder)buf, (Cookie)c);
            ++n3;
        }
        return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
    }

    public String encode(Collection<? extends Cookie> cookies) {
        if (ObjectUtil.checkNotNull(cookies, (String)"cookies").isEmpty()) {
            return null;
        }
        StringBuilder buf = CookieUtil.stringBuilder();
        if (this.strict) {
            if (cookies.size() == 1) {
                this.encode((StringBuilder)buf, (Cookie)cookies.iterator().next());
                return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
            }
            Cookie[] cookiesSorted = cookies.toArray(new Cookie[0]);
            Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
            Cookie[] arrcookie = cookiesSorted;
            int n = arrcookie.length;
            int n2 = 0;
            while (n2 < n) {
                Cookie c = arrcookie[n2];
                this.encode((StringBuilder)buf, (Cookie)c);
                ++n2;
            }
            return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
        }
        Iterator<? extends Cookie> cookiesSorted = cookies.iterator();
        while (cookiesSorted.hasNext()) {
            Cookie c = cookiesSorted.next();
            this.encode((StringBuilder)buf, (Cookie)c);
        }
        return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
    }

    public String encode(Iterable<? extends Cookie> cookies) {
        Iterator<? extends Cookie> cookiesIt = ObjectUtil.checkNotNull(cookies, (String)"cookies").iterator();
        if (!cookiesIt.hasNext()) {
            return null;
        }
        StringBuilder buf = CookieUtil.stringBuilder();
        if (!this.strict) {
            while (cookiesIt.hasNext()) {
                this.encode((StringBuilder)buf, (Cookie)cookiesIt.next());
            }
            return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
        }
        Cookie firstCookie = cookiesIt.next();
        if (!cookiesIt.hasNext()) {
            this.encode((StringBuilder)buf, (Cookie)firstCookie);
            return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
        }
        ArrayList<Cookie> cookiesList = InternalThreadLocalMap.get().arrayList();
        cookiesList.add(firstCookie);
        while (cookiesIt.hasNext()) {
            cookiesList.add(cookiesIt.next());
        }
        Cookie[] cookiesSorted = cookiesList.toArray(new Cookie[0]);
        Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
        Cookie[] arrcookie = cookiesSorted;
        int n = arrcookie.length;
        int n2 = 0;
        while (n2 < n) {
            Cookie c = arrcookie[n2];
            this.encode((StringBuilder)buf, (Cookie)c);
            ++n2;
        }
        return CookieUtil.stripTrailingSeparatorOrNull((StringBuilder)buf);
    }

    private void encode(StringBuilder buf, Cookie c) {
        String name = c.name();
        String value = c.value() != null ? c.value() : "";
        this.validateCookie((String)name, (String)value);
        if (c.wrap()) {
            CookieUtil.addQuoted((StringBuilder)buf, (String)name, (String)value);
            return;
        }
        CookieUtil.add((StringBuilder)buf, (String)name, (String)value);
    }
}

