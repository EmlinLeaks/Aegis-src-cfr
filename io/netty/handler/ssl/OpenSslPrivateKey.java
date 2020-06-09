/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.SSL
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslKeyMaterial;
import io.netty.handler.ssl.OpenSslPrivateKey;
import io.netty.internal.tcnative.SSL;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

final class OpenSslPrivateKey
extends AbstractReferenceCounted
implements PrivateKey {
    private long privateKeyAddress;

    OpenSslPrivateKey(long privateKeyAddress) {
        this.privateKeyAddress = privateKeyAddress;
    }

    @Override
    public String getAlgorithm() {
        return "unknown";
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return null;
    }

    private long privateKeyAddress() {
        if (this.refCnt() > 0) return this.privateKeyAddress;
        throw new IllegalReferenceCountException();
    }

    @Override
    protected void deallocate() {
        SSL.freePrivateKey((long)this.privateKeyAddress);
        this.privateKeyAddress = 0L;
    }

    @Override
    public OpenSslPrivateKey retain() {
        super.retain();
        return this;
    }

    @Override
    public OpenSslPrivateKey retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public OpenSslPrivateKey touch() {
        super.touch();
        return this;
    }

    @Override
    public OpenSslPrivateKey touch(Object hint) {
        return this;
    }

    @Override
    public void destroy() {
        this.release((int)this.refCnt());
    }

    @Override
    public boolean isDestroyed() {
        if (this.refCnt() != 0) return false;
        return true;
    }

    OpenSslKeyMaterial newKeyMaterial(long certificateChain, X509Certificate[] chain) {
        return new OpenSslPrivateKeyMaterial((OpenSslPrivateKey)this, (long)certificateChain, (X509Certificate[])chain);
    }

    static /* synthetic */ long access$000(OpenSslPrivateKey x0) {
        return x0.privateKeyAddress();
    }
}

