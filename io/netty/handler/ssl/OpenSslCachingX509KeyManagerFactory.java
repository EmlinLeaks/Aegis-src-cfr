/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslCachingX509KeyManagerFactory;
import java.security.Provider;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;

public final class OpenSslCachingX509KeyManagerFactory
extends KeyManagerFactory {
    public OpenSslCachingX509KeyManagerFactory(KeyManagerFactory factory) {
        super((KeyManagerFactorySpi)new KeyManagerFactorySpi((KeyManagerFactory)factory){
            final /* synthetic */ KeyManagerFactory val$factory;
            {
                this.val$factory = keyManagerFactory;
            }

            protected void engineInit(java.security.KeyStore keyStore, char[] chars) throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException {
                this.val$factory.init((java.security.KeyStore)keyStore, (char[])chars);
            }

            protected void engineInit(javax.net.ssl.ManagerFactoryParameters managerFactoryParameters) throws java.security.InvalidAlgorithmParameterException {
                this.val$factory.init((javax.net.ssl.ManagerFactoryParameters)managerFactoryParameters);
            }

            protected javax.net.ssl.KeyManager[] engineGetKeyManagers() {
                return this.val$factory.getKeyManagers();
            }
        }, (Provider)factory.getProvider(), (String)factory.getAlgorithm());
    }
}

