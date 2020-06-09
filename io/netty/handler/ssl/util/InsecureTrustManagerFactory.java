/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class InsecureTrustManagerFactory
extends SimpleTrustManagerFactory {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(InsecureTrustManagerFactory.class);
    public static final TrustManagerFactory INSTANCE = new InsecureTrustManagerFactory();
    private static final TrustManager tm = new X509TrustManager(){

        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, java.lang.String s) {
            if (!InsecureTrustManagerFactory.access$000().isDebugEnabled()) return;
            InsecureTrustManagerFactory.access$000().debug((java.lang.String)("Accepting a client certificate: " + chain[0].getSubjectDN()));
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, java.lang.String s) {
            if (!InsecureTrustManagerFactory.access$000().isDebugEnabled()) return;
            InsecureTrustManagerFactory.access$000().debug((java.lang.String)("Accepting a server certificate: " + chain[0].getSubjectDN()));
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return io.netty.util.internal.EmptyArrays.EMPTY_X509_CERTIFICATES;
        }
    };

    private InsecureTrustManagerFactory() {
    }

    @Override
    protected void engineInit(KeyStore keyStore) throws Exception {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{tm};
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }
}

