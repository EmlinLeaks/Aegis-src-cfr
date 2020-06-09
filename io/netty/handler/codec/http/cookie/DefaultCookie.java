/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.util.internal.ObjectUtil;

public class DefaultCookie
implements Cookie {
    private final String name;
    private String value;
    private boolean wrap;
    private String domain;
    private String path;
    private long maxAge = Long.MIN_VALUE;
    private boolean secure;
    private boolean httpOnly;

    public DefaultCookie(String name, String value) {
        name = ObjectUtil.checkNotNull(name, (String)"name").trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException((String)"empty name");
        }
        this.name = name;
        this.setValue((String)value);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = ObjectUtil.checkNotNull(value, (String)"value");
    }

    @Override
    public boolean wrap() {
        return this.wrap;
    }

    @Override
    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    @Override
    public String domain() {
        return this.domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = CookieUtil.validateAttributeValue((String)"domain", (String)domain);
    }

    @Override
    public String path() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        this.path = CookieUtil.validateAttributeValue((String)"path", (String)path);
    }

    @Override
    public long maxAge() {
        return this.maxAge;
    }

    @Override
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public int hashCode() {
        return this.name().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cookie)) {
            return false;
        }
        Cookie that = (Cookie)o;
        if (!this.name().equals((Object)that.name())) {
            return false;
        }
        if (this.path() == null) {
            if (that.path() != null) {
                return false;
            }
        } else {
            if (that.path() == null) {
                return false;
            }
            if (!this.path().equals((Object)that.path())) {
                return false;
            }
        }
        if (this.domain() != null) return this.domain().equalsIgnoreCase((String)that.domain());
        if (that.domain() == null) return true;
        return false;
    }

    @Override
    public int compareTo(Cookie c) {
        int v = this.name().compareTo((String)c.name());
        if (v != 0) {
            return v;
        }
        if (this.path() == null) {
            if (c.path() != null) {
                return -1;
            }
        } else {
            if (c.path() == null) {
                return 1;
            }
            v = this.path().compareTo((String)c.path());
            if (v != 0) {
                return v;
            }
        }
        if (this.domain() == null) {
            if (c.domain() == null) return 0;
            return -1;
        }
        if (c.domain() != null) return this.domain().compareToIgnoreCase((String)c.domain());
        return 1;
    }

    @Deprecated
    protected String validateValue(String name, String value) {
        return CookieUtil.validateAttributeValue((String)name, (String)value);
    }

    public String toString() {
        StringBuilder buf = CookieUtil.stringBuilder().append((String)this.name()).append((char)'=').append((String)this.value());
        if (this.domain() != null) {
            buf.append((String)", domain=").append((String)this.domain());
        }
        if (this.path() != null) {
            buf.append((String)", path=").append((String)this.path());
        }
        if (this.maxAge() >= 0L) {
            buf.append((String)", maxAge=").append((long)this.maxAge()).append((char)'s');
        }
        if (this.isSecure()) {
            buf.append((String)", secure");
        }
        if (!this.isHttpOnly()) return buf.toString();
        buf.append((String)", HTTPOnly");
        return buf.toString();
    }
}

