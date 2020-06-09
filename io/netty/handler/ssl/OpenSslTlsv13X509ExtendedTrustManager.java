/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkSslEngine;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslTlsv13X509ExtendedTrustManager;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509ExtendedTrustManager;

@SuppressJava6Requirement(reason="Usage guarded by java version check")
final class OpenSslTlsv13X509ExtendedTrustManager
extends X509ExtendedTrustManager {
    private final X509ExtendedTrustManager tm;

    private OpenSslTlsv13X509ExtendedTrustManager(X509ExtendedTrustManager tm) {
        this.tm = tm;
    }

    static X509ExtendedTrustManager wrap(X509ExtendedTrustManager tm) {
        if (PlatformDependent.javaVersion() >= 11) return tm;
        if (!OpenSsl.isTlsv13Supported()) return tm;
        return new OpenSslTlsv13X509ExtendedTrustManager((X509ExtendedTrustManager)tm);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
        this.tm.checkClientTrusted((X509Certificate[])x509Certificates, (String)s, (Socket)socket);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
        this.tm.checkServerTrusted((X509Certificate[])x509Certificates, (String)s, (Socket)socket);
    }

    private static SSLEngine wrapEngine(SSLEngine engine) {
        SSLSession session = engine.getHandshakeSession();
        if (session == null) return engine;
        if (!"TLSv1.3".equals((Object)session.getProtocol())) return engine;
        return new JdkSslEngine((SSLEngine)engine, (SSLEngine)engine, (SSLSession)session){
            final /* synthetic */ SSLEngine val$engine;
            final /* synthetic */ SSLSession val$session;
            {
                this.val$engine = sSLEngine;
                this.val$session = sSLSession;
                super((SSLEngine)engine);
            }

            public String getNegotiatedApplicationProtocol() {
                if (!(this.val$engine instanceof io.netty.handler.ssl.ApplicationProtocolAccessor)) return super.getNegotiatedApplicationProtocol();
                return ((io.netty.handler.ssl.ApplicationProtocolAccessor)((Object)this.val$engine)).getNegotiatedApplicationProtocol();
            }

            public SSLSession getHandshakeSession() {
                if (PlatformDependent.javaVersion() < 7) return new SSLSession(this){
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = this$0;
                    }

                    public byte[] getId() {
                        return this.this$0.val$session.getId();
                    }

                    public javax.net.ssl.SSLSessionContext getSessionContext() {
                        return this.this$0.val$session.getSessionContext();
                    }

                    public long getCreationTime() {
                        return this.this$0.val$session.getCreationTime();
                    }

                    public long getLastAccessedTime() {
                        return this.this$0.val$session.getLastAccessedTime();
                    }

                    public void invalidate() {
                        this.this$0.val$session.invalidate();
                    }

                    public boolean isValid() {
                        return this.this$0.val$session.isValid();
                    }

                    public void putValue(String s, Object o) {
                        this.this$0.val$session.putValue((String)s, (Object)o);
                    }

                    public Object getValue(String s) {
                        return this.this$0.val$session.getValue((String)s);
                    }

                    public void removeValue(String s) {
                        this.this$0.val$session.removeValue((String)s);
                    }

                    public String[] getValueNames() {
                        return this.this$0.val$session.getValueNames();
                    }

                    public java.security.cert.Certificate[] getPeerCertificates() throws javax.net.ssl.SSLPeerUnverifiedException {
                        return this.this$0.val$session.getPeerCertificates();
                    }

                    public java.security.cert.Certificate[] getLocalCertificates() {
                        return this.this$0.val$session.getLocalCertificates();
                    }

                    public javax.security.cert.X509Certificate[] getPeerCertificateChain() throws javax.net.ssl.SSLPeerUnverifiedException {
                        return this.this$0.val$session.getPeerCertificateChain();
                    }

                    public java.security.Principal getPeerPrincipal() throws javax.net.ssl.SSLPeerUnverifiedException {
                        return this.this$0.val$session.getPeerPrincipal();
                    }

                    public java.security.Principal getLocalPrincipal() {
                        return this.this$0.val$session.getLocalPrincipal();
                    }

                    public String getCipherSuite() {
                        return this.this$0.val$session.getCipherSuite();
                    }

                    public String getProtocol() {
                        return "TLSv1.2";
                    }

                    public String getPeerHost() {
                        return this.this$0.val$session.getPeerHost();
                    }

                    public int getPeerPort() {
                        return this.this$0.val$session.getPeerPort();
                    }

                    public int getPacketBufferSize() {
                        return this.this$0.val$session.getPacketBufferSize();
                    }

                    public int getApplicationBufferSize() {
                        return this.this$0.val$session.getApplicationBufferSize();
                    }
                };
                if (!(this.val$session instanceof io.netty.handler.ssl.ExtendedOpenSslSession)) return new /* invalid duplicate definition of identical inner class */;
                io.netty.handler.ssl.ExtendedOpenSslSession extendedOpenSslSession = (io.netty.handler.ssl.ExtendedOpenSslSession)this.val$session;
                return new io.netty.handler.ssl.ExtendedOpenSslSession(this, (io.netty.handler.ssl.OpenSslSession)extendedOpenSslSession, (io.netty.handler.ssl.ExtendedOpenSslSession)extendedOpenSslSession){
                    final /* synthetic */ io.netty.handler.ssl.ExtendedOpenSslSession val$extendedOpenSslSession;
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = this$0;
                        this.val$extendedOpenSslSession = extendedOpenSslSession;
                        super((io.netty.handler.ssl.OpenSslSession)wrapped);
                    }

                    public java.util.List getRequestedServerNames() {
                        return this.val$extendedOpenSslSession.getRequestedServerNames();
                    }

                    public String[] getPeerSupportedSignatureAlgorithms() {
                        return this.val$extendedOpenSslSession.getPeerSupportedSignatureAlgorithms();
                    }

                    public String getProtocol() {
                        return "TLSv1.2";
                    }
                };
            }
        };
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
        this.tm.checkClientTrusted((X509Certificate[])x509Certificates, (String)s, (SSLEngine)OpenSslTlsv13X509ExtendedTrustManager.wrapEngine((SSLEngine)sslEngine));
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
        this.tm.checkServerTrusted((X509Certificate[])x509Certificates, (String)s, (SSLEngine)OpenSslTlsv13X509ExtendedTrustManager.wrapEngine((SSLEngine)sslEngine));
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        this.tm.checkClientTrusted((X509Certificate[])x509Certificates, (String)s);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        this.tm.checkServerTrusted((X509Certificate[])x509Certificates, (String)s);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.tm.getAcceptedIssuers();
    }
}

