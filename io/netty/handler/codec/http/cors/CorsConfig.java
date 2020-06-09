/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.util.internal.StringUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public final class CorsConfig {
    private final Set<String> origins;
    private final boolean anyOrigin;
    private final boolean enabled;
    private final Set<String> exposeHeaders;
    private final boolean allowCredentials;
    private final long maxAge;
    private final Set<HttpMethod> allowedRequestMethods;
    private final Set<String> allowedRequestHeaders;
    private final boolean allowNullOrigin;
    private final Map<CharSequence, Callable<?>> preflightHeaders;
    private final boolean shortCircuit;

    CorsConfig(CorsConfigBuilder builder) {
        this.origins = new LinkedHashSet<String>(builder.origins);
        this.anyOrigin = builder.anyOrigin;
        this.enabled = builder.enabled;
        this.exposeHeaders = builder.exposeHeaders;
        this.allowCredentials = builder.allowCredentials;
        this.maxAge = builder.maxAge;
        this.allowedRequestMethods = builder.requestMethods;
        this.allowedRequestHeaders = builder.requestHeaders;
        this.allowNullOrigin = builder.allowNullOrigin;
        this.preflightHeaders = builder.preflightHeaders;
        this.shortCircuit = builder.shortCircuit;
    }

    public boolean isCorsSupportEnabled() {
        return this.enabled;
    }

    public boolean isAnyOriginSupported() {
        return this.anyOrigin;
    }

    public String origin() {
        if (this.origins.isEmpty()) {
            return "*";
        }
        String string = this.origins.iterator().next();
        return string;
    }

    public Set<String> origins() {
        return this.origins;
    }

    public boolean isNullOriginAllowed() {
        return this.allowNullOrigin;
    }

    public Set<String> exposedHeaders() {
        return Collections.unmodifiableSet(this.exposeHeaders);
    }

    public boolean isCredentialsAllowed() {
        return this.allowCredentials;
    }

    public long maxAge() {
        return this.maxAge;
    }

    public Set<HttpMethod> allowedRequestMethods() {
        return Collections.unmodifiableSet(this.allowedRequestMethods);
    }

    public Set<String> allowedRequestHeaders() {
        return Collections.unmodifiableSet(this.allowedRequestHeaders);
    }

    public HttpHeaders preflightResponseHeaders() {
        if (this.preflightHeaders.isEmpty()) {
            return EmptyHttpHeaders.INSTANCE;
        }
        DefaultHttpHeaders preflightHeaders = new DefaultHttpHeaders();
        Iterator<Map.Entry<CharSequence, Callable<?>>> iterator = this.preflightHeaders.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CharSequence, Callable<?>> entry = iterator.next();
            ? value = CorsConfig.getValue(entry.getValue());
            if (value instanceof Iterable) {
                ((HttpHeaders)preflightHeaders).add((CharSequence)entry.getKey(), (Iterable)value);
                continue;
            }
            ((HttpHeaders)preflightHeaders).add((CharSequence)entry.getKey(), value);
        }
        return preflightHeaders;
    }

    public boolean isShortCircuit() {
        return this.shortCircuit;
    }

    @Deprecated
    public boolean isShortCurcuit() {
        return this.isShortCircuit();
    }

    private static <T> T getValue(Callable<T> callable) {
        try {
            return (T)callable.call();
        }
        catch (Exception e) {
            throw new IllegalStateException((String)("Could not generate value for callable [" + callable + ']'), (Throwable)e);
        }
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + "[enabled=" + this.enabled + ", origins=" + this.origins + ", anyOrigin=" + this.anyOrigin + ", exposedHeaders=" + this.exposeHeaders + ", isCredentialsAllowed=" + this.allowCredentials + ", maxAge=" + this.maxAge + ", allowedRequestMethods=" + this.allowedRequestMethods + ", allowedRequestHeaders=" + this.allowedRequestHeaders + ", preflightHeaders=" + this.preflightHeaders + ']';
    }

    @Deprecated
    public static Builder withAnyOrigin() {
        return new Builder();
    }

    @Deprecated
    public static Builder withOrigin(String origin) {
        if (!"*".equals((Object)origin)) return new Builder((String[])new String[]{origin});
        return new Builder();
    }

    @Deprecated
    public static Builder withOrigins(String ... origins) {
        return new Builder((String[])origins);
    }
}

