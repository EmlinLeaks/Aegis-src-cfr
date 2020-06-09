/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.SSL
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.ssl.DefaultOpenSslKeyMaterial;
import io.netty.handler.ssl.OpenSslKeyMaterial;
import io.netty.handler.ssl.OpenSslPrivateKey;
import io.netty.handler.ssl.PemEncoded;
import io.netty.handler.ssl.PemX509Certificate;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.internal.tcnative.SSL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509KeyManager;

class OpenSslKeyMaterialProvider {
    private final X509KeyManager keyManager;
    private final String password;

    OpenSslKeyMaterialProvider(X509KeyManager keyManager, String password) {
        this.keyManager = keyManager;
        this.password = password;
    }

    static void validateKeyMaterialSupported(X509Certificate[] keyCertChain, PrivateKey key, String keyPassword) throws SSLException {
        OpenSslKeyMaterialProvider.validateSupported((X509Certificate[])keyCertChain);
        OpenSslKeyMaterialProvider.validateSupported((PrivateKey)key, (String)keyPassword);
    }

    private static void validateSupported(PrivateKey key, String password) throws SSLException {
        if (key == null) {
            return;
        }
        long pkeyBio = 0L;
        long pkey = 0L;
        try {
            pkeyBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, (PrivateKey)key);
            pkey = SSL.parsePrivateKey((long)pkeyBio, (String)password);
            return;
        }
        catch (Exception e) {
            throw new SSLException((String)("PrivateKey type not supported " + key.getFormat()), (Throwable)e);
        }
        finally {
            SSL.freeBIO((long)pkeyBio);
            if (pkey != 0L) {
                SSL.freePrivateKey((long)pkey);
            }
        }
    }

    private static void validateSupported(X509Certificate[] certificates) throws SSLException {
        if (certificates == null) return;
        if (certificates.length == 0) {
            return;
        }
        long chainBio = 0L;
        long chain = 0L;
        PemEncoded encoded = null;
        try {
            encoded = PemX509Certificate.toPEM((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, (boolean)true, (X509Certificate[])certificates);
            chainBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, (PemEncoded)encoded.retain());
            chain = SSL.parseX509Chain((long)chainBio);
            return;
        }
        catch (Exception e) {
            throw new SSLException((String)"Certificate type not supported", (Throwable)e);
        }
        finally {
            SSL.freeBIO((long)chainBio);
            if (chain != 0L) {
                SSL.freeX509Chain((long)chain);
            }
            if (encoded != null) {
                encoded.release();
            }
        }
    }

    X509KeyManager keyManager() {
        return this.keyManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator allocator, String alias) throws Exception {
        X509Certificate[] certificates = this.keyManager.getCertificateChain((String)alias);
        if (certificates == null) return null;
        if (certificates.length == 0) {
            return null;
        }
        PrivateKey key = this.keyManager.getPrivateKey((String)alias);
        PemEncoded encoded = PemX509Certificate.toPEM((ByteBufAllocator)allocator, (boolean)true, (X509Certificate[])certificates);
        long chainBio = 0L;
        long pkeyBio = 0L;
        long chain = 0L;
        long pkey = 0L;
        try {
            OpenSslKeyMaterial keyMaterial;
            chainBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)allocator, (PemEncoded)encoded.retain());
            chain = SSL.parseX509Chain((long)chainBio);
            if (key instanceof OpenSslPrivateKey) {
                keyMaterial = ((OpenSslPrivateKey)key).newKeyMaterial((long)chain, (X509Certificate[])certificates);
            } else {
                pkeyBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)allocator, (PrivateKey)key);
                pkey = key == null ? 0L : SSL.parsePrivateKey((long)pkeyBio, (String)this.password);
                keyMaterial = new DefaultOpenSslKeyMaterial((long)chain, (long)pkey, (X509Certificate[])certificates);
            }
            chain = 0L;
            pkey = 0L;
            OpenSslKeyMaterial openSslKeyMaterial = keyMaterial;
            return openSslKeyMaterial;
        }
        finally {
            SSL.freeBIO((long)chainBio);
            SSL.freeBIO((long)pkeyBio);
            if (chain != 0L) {
                SSL.freeX509Chain((long)chain);
            }
            if (pkey != 0L) {
                SSL.freePrivateKey((long)pkey);
            }
            encoded.release();
        }
    }

    void destroy() {
    }
}

