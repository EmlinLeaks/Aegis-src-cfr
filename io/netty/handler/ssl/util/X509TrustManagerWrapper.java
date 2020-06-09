/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

@SuppressJava6Requirement(reason="Usage guarded by java version check")
final class X509TrustManagerWrapper
extends X509ExtendedTrustManager {
    private final X509TrustManager delegate;

    X509TrustManagerWrapper(X509TrustManager delegate) {
        this.delegate = ObjectUtil.checkNotNull(delegate, (String)"delegate");
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String s) throws CertificateException {
        this.delegate.checkClientTrusted((X509Certificate[])chain, (String)s);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String s, Socket socket) throws CertificateException {
        this.delegate.checkClientTrusted((X509Certificate[])chain, (String)s);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String s, SSLEngine sslEngine) throws CertificateException {
        this.delegate.checkClientTrusted((X509Certificate[])chain, (String)s);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
        this.delegate.checkServerTrusted((X509Certificate[])chain, (String)s);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String s, Socket socket) throws CertificateException {
        this.delegate.checkServerTrusted((X509Certificate[])chain, (String)s);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String s, SSLEngine sslEngine) throws CertificateException {
        this.delegate.checkServerTrusted((X509Certificate[])chain, (String)s);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.delegate.getAcceptedIssuers();
    }
}

