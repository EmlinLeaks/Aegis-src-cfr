/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.concurrent.FastThreadLocal;
import java.security.KeyStore;
import java.security.Provider;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;

public abstract class SimpleTrustManagerFactory
extends TrustManagerFactory {
    private static final Provider PROVIDER = new Provider((String)"", (double)0.0, (String)""){
        private static final long serialVersionUID = -2680540247105807895L;
    };
    private static final FastThreadLocal<SimpleTrustManagerFactorySpi> CURRENT_SPI = new FastThreadLocal<SimpleTrustManagerFactorySpi>(){

        protected SimpleTrustManagerFactorySpi initialValue() {
            return new SimpleTrustManagerFactorySpi();
        }
    };

    protected SimpleTrustManagerFactory() {
        this((String)"");
    }

    protected SimpleTrustManagerFactory(String name) {
        super((TrustManagerFactorySpi)((TrustManagerFactorySpi)CURRENT_SPI.get()), (Provider)PROVIDER, (String)name);
        CURRENT_SPI.get().init((SimpleTrustManagerFactory)this);
        CURRENT_SPI.remove();
        if (name != null) return;
        throw new NullPointerException((String)"name");
    }

    protected abstract void engineInit(KeyStore var1) throws Exception;

    protected abstract void engineInit(ManagerFactoryParameters var1) throws Exception;

    protected abstract TrustManager[] engineGetTrustManagers();
}

