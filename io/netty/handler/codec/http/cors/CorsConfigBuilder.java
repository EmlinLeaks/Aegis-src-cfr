/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.util.AsciiString;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public final class CorsConfigBuilder {
    final Set<String> origins;
    final boolean anyOrigin;
    boolean allowNullOrigin;
    boolean enabled = true;
    boolean allowCredentials;
    final Set<String> exposeHeaders = new HashSet<String>();
    long maxAge;
    final Set<HttpMethod> requestMethods = new HashSet<HttpMethod>();
    final Set<String> requestHeaders = new HashSet<String>();
    final Map<CharSequence, Callable<?>> preflightHeaders = new HashMap<CharSequence, Callable<?>>();
    private boolean noPreflightHeaders;
    boolean shortCircuit;

    public static CorsConfigBuilder forAnyOrigin() {
        return new CorsConfigBuilder();
    }

    public static CorsConfigBuilder forOrigin(String origin) {
        if (!"*".equals((Object)origin)) return new CorsConfigBuilder((String[])new String[]{origin});
        return new CorsConfigBuilder();
    }

    public static CorsConfigBuilder forOrigins(String ... origins) {
        return new CorsConfigBuilder((String[])origins);
    }

    CorsConfigBuilder(String ... origins) {
        this.origins = new LinkedHashSet<String>(Arrays.asList(origins));
        this.anyOrigin = false;
    }

    CorsConfigBuilder() {
        this.anyOrigin = true;
        this.origins = Collections.emptySet();
    }

    public CorsConfigBuilder allowNullOrigin() {
        this.allowNullOrigin = true;
        return this;
    }

    public CorsConfigBuilder disable() {
        this.enabled = false;
        return this;
    }

    public CorsConfigBuilder exposeHeaders(String ... headers) {
        this.exposeHeaders.addAll(Arrays.asList(headers));
        return this;
    }

    public CorsConfigBuilder exposeHeaders(CharSequence ... headers) {
        CharSequence[] arrcharSequence = headers;
        int n = arrcharSequence.length;
        int n2 = 0;
        while (n2 < n) {
            CharSequence header = arrcharSequence[n2];
            this.exposeHeaders.add((String)header.toString());
            ++n2;
        }
        return this;
    }

    public CorsConfigBuilder allowCredentials() {
        this.allowCredentials = true;
        return this;
    }

    public CorsConfigBuilder maxAge(long max) {
        this.maxAge = max;
        return this;
    }

    public CorsConfigBuilder allowedRequestMethods(HttpMethod ... methods) {
        this.requestMethods.addAll(Arrays.asList(methods));
        return this;
    }

    public CorsConfigBuilder allowedRequestHeaders(String ... headers) {
        this.requestHeaders.addAll(Arrays.asList(headers));
        return this;
    }

    public CorsConfigBuilder allowedRequestHeaders(CharSequence ... headers) {
        CharSequence[] arrcharSequence = headers;
        int n = arrcharSequence.length;
        int n2 = 0;
        while (n2 < n) {
            CharSequence header = arrcharSequence[n2];
            this.requestHeaders.add((String)header.toString());
            ++n2;
        }
        return this;
    }

    public CorsConfigBuilder preflightResponseHeader(CharSequence name, Object ... values) {
        if (values.length == 1) {
            this.preflightHeaders.put((CharSequence)name, new ConstantValueGenerator((Object)values[0], null));
            return this;
        }
        this.preflightResponseHeader((CharSequence)name, Arrays.asList(values));
        return this;
    }

    public <T> CorsConfigBuilder preflightResponseHeader(CharSequence name, Iterable<T> value) {
        this.preflightHeaders.put((CharSequence)name, new ConstantValueGenerator(value, null));
        return this;
    }

    public <T> CorsConfigBuilder preflightResponseHeader(CharSequence name, Callable<T> valueGenerator) {
        this.preflightHeaders.put((CharSequence)name, valueGenerator);
        return this;
    }

    public CorsConfigBuilder noPreflightResponseHeaders() {
        this.noPreflightHeaders = true;
        return this;
    }

    public CorsConfigBuilder shortCircuit() {
        this.shortCircuit = true;
        return this;
    }

    public CorsConfig build() {
        if (!this.preflightHeaders.isEmpty()) return new CorsConfig((CorsConfigBuilder)this);
        if (this.noPreflightHeaders) return new CorsConfig((CorsConfigBuilder)this);
        this.preflightHeaders.put((CharSequence)HttpHeaderNames.DATE, DateValueGenerator.INSTANCE);
        this.preflightHeaders.put((CharSequence)HttpHeaderNames.CONTENT_LENGTH, new ConstantValueGenerator((Object)"0", null));
        return new CorsConfig((CorsConfigBuilder)this);
    }
}

