/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslX509TrustManagerWrapper;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@SuppressJava6Requirement(reason="Usage guarded by java version check")
final class OpenSslX509TrustManagerWrapper {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(OpenSslX509TrustManagerWrapper.class);
    private static final TrustManagerWrapper WRAPPER;

    private OpenSslX509TrustManagerWrapper() {
    }

    static X509TrustManager wrapIfNeeded(X509TrustManager trustManager) {
        return WRAPPER.wrapIfNeeded((X509TrustManager)trustManager);
    }

    private static SSLContext newSSLContext() throws NoSuchAlgorithmException {
        return SSLContext.getInstance((String)"TLS");
    }

    static /* synthetic */ SSLContext access$000() throws NoSuchAlgorithmException {
        return OpenSslX509TrustManagerWrapper.newSSLContext();
    }

    static {
        TrustManagerWrapper wrapper = new TrustManagerWrapper(){

            public X509TrustManager wrapIfNeeded(X509TrustManager manager) {
                return manager;
            }
        };
        Throwable cause = null;
        Throwable unsafeCause = PlatformDependent.getUnsafeUnavailabilityCause();
        if (unsafeCause == null) {
            SSLContext context;
            try {
                context = OpenSslX509TrustManagerWrapper.newSSLContext();
                context.init(null, (TrustManager[])new TrustManager[]{new X509TrustManager(){

                    public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                        throw new java.security.cert.CertificateException();
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                        throw new java.security.cert.CertificateException();
                    }

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return io.netty.util.internal.EmptyArrays.EMPTY_X509_CERTIFICATES;
                    }
                }}, null);
            }
            catch (Throwable error) {
                context = null;
                cause = error;
            }
            if (cause != null) {
                LOGGER.debug((String)"Unable to access wrapped TrustManager", (Throwable)cause);
            } else {
                SSLContext finalContext = context;
                Object maybeWrapper = AccessController.doPrivileged(new PrivilegedAction<Object>((SSLContext)finalContext){
                    final /* synthetic */ SSLContext val$finalContext;
                    {
                        this.val$finalContext = sSLContext;
                    }

                    public Object run() {
                        try {
                            java.lang.reflect.Field contextSpiField = SSLContext.class.getDeclaredField((String)"contextSpi");
                            long spiOffset = PlatformDependent.objectFieldOffset((java.lang.reflect.Field)contextSpiField);
                            Object spi = PlatformDependent.getObject((Object)this.val$finalContext, (long)spiOffset);
                            if (spi == null) throw new java.lang.NoSuchFieldException();
                            java.lang.Class<?> clazz = spi.getClass();
                            do {
                                try {
                                    java.lang.reflect.Field trustManagerField = clazz.getDeclaredField((String)"trustManager");
                                    long tmOffset = PlatformDependent.objectFieldOffset((java.lang.reflect.Field)trustManagerField);
                                    Object trustManager = PlatformDependent.getObject((Object)spi, (long)tmOffset);
                                    if (trustManager instanceof javax.net.ssl.X509ExtendedTrustManager) {
                                        return new io.netty.handler.ssl.OpenSslX509TrustManagerWrapper$UnsafeTrustManagerWrapper((long)spiOffset, (long)tmOffset);
                                    }
                                }
                                catch (java.lang.NoSuchFieldException trustManagerField) {
                                    // empty catch block
                                }
                            } while ((clazz = clazz.getSuperclass()) != null);
                            throw new java.lang.NoSuchFieldException();
                        }
                        catch (java.lang.NoSuchFieldException e) {
                            return e;
                        }
                        catch (java.lang.SecurityException e) {
                            return e;
                        }
                    }
                });
                if (maybeWrapper instanceof Throwable) {
                    LOGGER.debug((String)"Unable to access wrapped TrustManager", (Throwable)((Throwable)maybeWrapper));
                } else {
                    wrapper = (TrustManagerWrapper)maybeWrapper;
                }
            }
        } else {
            LOGGER.debug((String)"Unable to access wrapped TrustManager", cause);
        }
        WRAPPER = wrapper;
    }
}

