/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslProvider;

public enum SslProvider {
    JDK,
    OPENSSL,
    OPENSSL_REFCNT;
    

    public static boolean isAlpnSupported(SslProvider provider) {
        switch (1.$SwitchMap$io$netty$handler$ssl$SslProvider[provider.ordinal()]) {
            case 1: {
                return JdkAlpnApplicationProtocolNegotiator.isAlpnSupported();
            }
            case 2: 
            case 3: {
                return OpenSsl.isAlpnSupported();
            }
        }
        throw new Error((String)("Unknown SslProvider: " + (Object)((Object)provider)));
    }
}

