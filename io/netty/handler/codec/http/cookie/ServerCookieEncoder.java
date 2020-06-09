/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ServerCookieEncoder
extends CookieEncoder {
    public static final ServerCookieEncoder STRICT = new ServerCookieEncoder((boolean)true);
    public static final ServerCookieEncoder LAX = new ServerCookieEncoder((boolean)false);

    private ServerCookieEncoder(boolean strict) {
        super((boolean)strict);
    }

    public String encode(String name, String value) {
        return this.encode((Cookie)new DefaultCookie((String)name, (String)value));
    }

    public String encode(Cookie cookie) {
        String name = ObjectUtil.checkNotNull(cookie, (String)"cookie").name();
        String value = cookie.value() != null ? cookie.value() : "";
        this.validateCookie((String)name, (String)value);
        StringBuilder buf = CookieUtil.stringBuilder();
        if (cookie.wrap()) {
            CookieUtil.addQuoted((StringBuilder)buf, (String)name, (String)value);
        } else {
            CookieUtil.add((StringBuilder)buf, (String)name, (String)value);
        }
        if (cookie.maxAge() != Long.MIN_VALUE) {
            CookieUtil.add((StringBuilder)buf, (String)"Max-Age", (long)cookie.maxAge());
            Date expires = new Date((long)(cookie.maxAge() * 1000L + System.currentTimeMillis()));
            buf.append((String)"Expires");
            buf.append((char)'=');
            DateFormatter.append((Date)expires, (StringBuilder)buf);
            buf.append((char)';');
            buf.append((char)' ');
        }
        if (cookie.path() != null) {
            CookieUtil.add((StringBuilder)buf, (String)"Path", (String)cookie.path());
        }
        if (cookie.domain() != null) {
            CookieUtil.add((StringBuilder)buf, (String)"Domain", (String)cookie.domain());
        }
        if (cookie.isSecure()) {
            CookieUtil.add((StringBuilder)buf, (String)"Secure");
        }
        if (!cookie.isHttpOnly()) return CookieUtil.stripTrailingSeparator((StringBuilder)buf);
        CookieUtil.add((StringBuilder)buf, (String)"HTTPOnly");
        return CookieUtil.stripTrailingSeparator((StringBuilder)buf);
    }

    private static List<String> dedup(List<String> encoded, Map<String, Integer> nameToLastIndex) {
        boolean[] isLastInstance = new boolean[encoded.size()];
        for (int idx : nameToLastIndex.values()) {
            isLastInstance[idx] = true;
        }
        ArrayList<String> dedupd = new ArrayList<String>((int)nameToLastIndex.size());
        int i = 0;
        int n = encoded.size();
        while (i < n) {
            if (isLastInstance[i]) {
                dedupd.add((String)encoded.get((int)i));
            }
            ++i;
        }
        return dedupd;
    }

    public List<String> encode(Cookie ... cookies) {
        List<String> list;
        if (ObjectUtil.checkNotNull(cookies, (String)"cookies").length == 0) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>((int)cookies.length);
        HashMap<String, Integer> nameToIndex = this.strict && cookies.length > 1 ? new HashMap<String, Integer>() : null;
        boolean hasDupdName = false;
        for (int i = 0; i < cookies.length; ++i) {
            Cookie c = cookies[i];
            encoded.add((String)this.encode((Cookie)c));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put((String)c.name(), (Integer)Integer.valueOf((int)i)) != null;
        }
        if (hasDupdName) {
            list = ServerCookieEncoder.dedup(encoded, nameToIndex);
            return list;
        }
        list = encoded;
        return list;
    }

    public List<String> encode(Collection<? extends Cookie> cookies) {
        List<String> list;
        if (ObjectUtil.checkNotNull(cookies, (String)"cookies").isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>((int)cookies.size());
        HashMap<String, Integer> nameToIndex = this.strict && cookies.size() > 1 ? new HashMap<String, Integer>() : null;
        int i = 0;
        boolean hasDupdName = false;
        for (Cookie c : cookies) {
            encoded.add((String)this.encode((Cookie)c));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put((String)c.name(), (Integer)Integer.valueOf((int)i++)) != null;
        }
        if (hasDupdName) {
            list = ServerCookieEncoder.dedup(encoded, nameToIndex);
            return list;
        }
        list = encoded;
        return list;
    }

    public List<String> encode(Iterable<? extends Cookie> cookies) {
        List<String> list;
        boolean hasDupdName;
        Iterator<? extends Cookie> cookiesIt = ObjectUtil.checkNotNull(cookies, (String)"cookies").iterator();
        if (!cookiesIt.hasNext()) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>();
        Cookie firstCookie = cookiesIt.next();
        HashMap<String, Integer> nameToIndex = this.strict && cookiesIt.hasNext() ? new HashMap<String, Integer>() : null;
        int i = 0;
        encoded.add(this.encode((Cookie)firstCookie));
        boolean bl = hasDupdName = nameToIndex != null && nameToIndex.put(firstCookie.name(), Integer.valueOf((int)i++)) != null;
        while (cookiesIt.hasNext()) {
            Cookie c = cookiesIt.next();
            encoded.add((String)this.encode((Cookie)c));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put((String)c.name(), (Integer)Integer.valueOf((int)i++)) != null;
        }
        if (hasDupdName) {
            list = ServerCookieEncoder.dedup(encoded, nameToIndex);
            return list;
        }
        list = encoded;
        return list;
    }
}

