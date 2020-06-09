/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.OpenSslX509KeyManagerFactory;
import io.netty.handler.ssl.SslContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;

public final class OpenSslX509KeyManagerFactory
extends KeyManagerFactory {
    private final OpenSslKeyManagerFactorySpi spi;

    public OpenSslX509KeyManagerFactory() {
        this((OpenSslKeyManagerFactorySpi)OpenSslX509KeyManagerFactory.newOpenSslKeyManagerFactorySpi(null));
    }

    public OpenSslX509KeyManagerFactory(Provider provider) {
        this((OpenSslKeyManagerFactorySpi)OpenSslX509KeyManagerFactory.newOpenSslKeyManagerFactorySpi((Provider)provider));
    }

    public OpenSslX509KeyManagerFactory(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        this((OpenSslKeyManagerFactorySpi)OpenSslX509KeyManagerFactory.newOpenSslKeyManagerFactorySpi((String)algorithm, (Provider)provider));
    }

    private OpenSslX509KeyManagerFactory(OpenSslKeyManagerFactorySpi spi) {
        super((KeyManagerFactorySpi)spi, (Provider)spi.kmf.getProvider(), (String)spi.kmf.getAlgorithm());
        this.spi = spi;
    }

    private static OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(Provider provider) {
        try {
            return OpenSslX509KeyManagerFactory.newOpenSslKeyManagerFactorySpi(null, (Provider)provider);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException((Throwable)e);
        }
    }

    private static OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        KeyManagerFactory keyManagerFactory;
        if (algorithm == null) {
            algorithm = KeyManagerFactory.getDefaultAlgorithm();
        }
        if (provider == null) {
            keyManagerFactory = KeyManagerFactory.getInstance((String)algorithm);
            return new OpenSslKeyManagerFactorySpi((KeyManagerFactory)keyManagerFactory);
        }
        keyManagerFactory = KeyManagerFactory.getInstance((String)algorithm, (Provider)provider);
        return new OpenSslKeyManagerFactorySpi((KeyManagerFactory)keyManagerFactory);
    }

    OpenSslKeyMaterialProvider newProvider() {
        return this.spi.newProvider();
    }

    public static OpenSslX509KeyManagerFactory newEngineBased(File certificateChain, String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return OpenSslX509KeyManagerFactory.newEngineBased((X509Certificate[])SslContext.toX509Certificates((File)certificateChain), (String)password);
    }

    public static OpenSslX509KeyManagerFactory newEngineBased(X509Certificate[] certificateChain, String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        OpenSslKeyStore store = new OpenSslKeyStore((X509Certificate[])((X509Certificate[])certificateChain.clone()), (boolean)false, null);
        store.load(null, null);
        OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
        factory.init((KeyStore)store, (char[])(password == null ? null : password.toCharArray()));
        return factory;
    }

    public static OpenSslX509KeyManagerFactory newKeyless(File chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return OpenSslX509KeyManagerFactory.newKeyless((X509Certificate[])SslContext.toX509Certificates((File)chain));
    }

    public static OpenSslX509KeyManagerFactory newKeyless(InputStream chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return OpenSslX509KeyManagerFactory.newKeyless((X509Certificate[])SslContext.toX509Certificates((InputStream)chain));
    }

    public static OpenSslX509KeyManagerFactory newKeyless(X509Certificate ... certificateChain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        OpenSslKeyStore store = new OpenSslKeyStore((X509Certificate[])((X509Certificate[])certificateChain.clone()), (boolean)true, null);
        store.load(null, null);
        OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
        factory.init((KeyStore)store, null);
        return factory;
    }
}

