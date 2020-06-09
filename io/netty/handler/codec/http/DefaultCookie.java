/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.Cookie;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Deprecated
public class DefaultCookie
extends io.netty.handler.codec.http.cookie.DefaultCookie
implements Cookie {
    private String comment;
    private String commentUrl;
    private boolean discard;
    private Set<Integer> ports = Collections.emptySet();
    private Set<Integer> unmodifiablePorts = this.ports;
    private int version;

    public DefaultCookie(String name, String value) {
        super((String)name, (String)value);
    }

    @Deprecated
    @Override
    public String getName() {
        return this.name();
    }

    @Deprecated
    @Override
    public String getValue() {
        return this.value();
    }

    @Deprecated
    @Override
    public String getDomain() {
        return this.domain();
    }

    @Deprecated
    @Override
    public String getPath() {
        return this.path();
    }

    @Deprecated
    @Override
    public String getComment() {
        return this.comment();
    }

    @Deprecated
    @Override
    public String comment() {
        return this.comment;
    }

    @Deprecated
    @Override
    public void setComment(String comment) {
        this.comment = this.validateValue((String)"comment", (String)comment);
    }

    @Deprecated
    @Override
    public String getCommentUrl() {
        return this.commentUrl();
    }

    @Deprecated
    @Override
    public String commentUrl() {
        return this.commentUrl;
    }

    @Deprecated
    @Override
    public void setCommentUrl(String commentUrl) {
        this.commentUrl = this.validateValue((String)"commentUrl", (String)commentUrl);
    }

    @Deprecated
    @Override
    public boolean isDiscard() {
        return this.discard;
    }

    @Deprecated
    @Override
    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    @Deprecated
    @Override
    public Set<Integer> getPorts() {
        return this.ports();
    }

    @Deprecated
    @Override
    public Set<Integer> ports() {
        if (this.unmodifiablePorts != null) return this.unmodifiablePorts;
        this.unmodifiablePorts = Collections.unmodifiableSet(this.ports);
        return this.unmodifiablePorts;
    }

    @Deprecated
    @Override
    public void setPorts(int ... ports) {
        if (ports == null) {
            throw new NullPointerException((String)"ports");
        }
        int[] portsCopy = (int[])ports.clone();
        if (portsCopy.length == 0) {
            this.ports = Collections.emptySet();
            this.unmodifiablePorts = this.ports;
            return;
        }
        TreeSet<Integer> newPorts = new TreeSet<Integer>();
        int[] arrn = portsCopy;
        int n = arrn.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                this.ports = newPorts;
                this.unmodifiablePorts = null;
                return;
            }
            int p = arrn[n2];
            if (p <= 0) throw new IllegalArgumentException((String)("port out of range: " + p));
            if (p > 65535) {
                throw new IllegalArgumentException((String)("port out of range: " + p));
            }
            newPorts.add((Integer)Integer.valueOf((int)p));
            ++n2;
        } while (true);
    }

    @Deprecated
    @Override
    public void setPorts(Iterable<Integer> ports) {
        TreeSet<Integer> newPorts = new TreeSet<Integer>();
        for (int p : ports) {
            if (p <= 0) throw new IllegalArgumentException((String)("port out of range: " + p));
            if (p > 65535) {
                throw new IllegalArgumentException((String)("port out of range: " + p));
            }
            newPorts.add((Integer)Integer.valueOf((int)p));
        }
        if (newPorts.isEmpty()) {
            this.ports = Collections.emptySet();
            this.unmodifiablePorts = this.ports;
            return;
        }
        this.ports = newPorts;
        this.unmodifiablePorts = null;
    }

    @Deprecated
    @Override
    public long getMaxAge() {
        return this.maxAge();
    }

    @Deprecated
    @Override
    public int getVersion() {
        return this.version();
    }

    @Deprecated
    @Override
    public int version() {
        return this.version;
    }

    @Deprecated
    @Override
    public void setVersion(int version) {
        this.version = version;
    }
}

