/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ConscryptAlpnSslEngine;
import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.net.ssl.SSLEngine;

final class Conscrypt {
    private static final Method IS_CONSCRYPT_SSLENGINE = Conscrypt.loadIsConscryptEngine();
    private static final boolean CAN_INSTANCE_PROVIDER = Conscrypt.canInstanceProvider();

    private static Method loadIsConscryptEngine() {
        try {
            Class<?> conscryptClass = Class.forName((String)"org.conscrypt.Conscrypt", (boolean)true, (ClassLoader)ConscryptAlpnSslEngine.class.getClassLoader());
            return conscryptClass.getMethod((String)"isConscrypt", SSLEngine.class);
        }
        catch (Throwable ignore) {
            return null;
        }
    }

    private static boolean canInstanceProvider() {
        try {
            Class<?> providerClass = Class.forName((String)"org.conscrypt.OpenSSLProvider", (boolean)true, (ClassLoader)ConscryptAlpnSslEngine.class.getClassLoader());
            providerClass.newInstance();
            return true;
        }
        catch (Throwable ignore) {
            return false;
        }
    }

    static boolean isAvailable() {
        if (!CAN_INSTANCE_PROVIDER) return false;
        if (IS_CONSCRYPT_SSLENGINE == null) return false;
        if (PlatformDependent.javaVersion() < 8) return false;
        return true;
    }

    static boolean isEngineSupported(SSLEngine engine) {
        if (!Conscrypt.isAvailable()) return false;
        if (!Conscrypt.isConscryptEngine((SSLEngine)engine)) return false;
        return true;
    }

    private static boolean isConscryptEngine(SSLEngine engine) {
        try {
            return ((Boolean)IS_CONSCRYPT_SSLENGINE.invoke(null, (Object[])new Object[]{engine})).booleanValue();
        }
        catch (IllegalAccessException ignore) {
            return false;
        }
        catch (InvocationTargetException ex) {
            throw new RuntimeException((Throwable)ex);
        }
    }

    private Conscrypt() {
    }
}

