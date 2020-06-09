/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.OpenSslKeyMaterial;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.net.ssl.X509KeyManager;

final class OpenSslCachingKeyMaterialProvider
extends OpenSslKeyMaterialProvider {
    private final ConcurrentMap<String, OpenSslKeyMaterial> cache = new ConcurrentHashMap<String, OpenSslKeyMaterial>();

    OpenSslCachingKeyMaterialProvider(X509KeyManager keyManager, String password) {
        super((X509KeyManager)keyManager, (String)password);
    }

    @Override
    OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator allocator, String alias) throws Exception {
        OpenSslKeyMaterial material = (OpenSslKeyMaterial)this.cache.get((Object)alias);
        if (material != null) return material.retain();
        material = super.chooseKeyMaterial((ByteBufAllocator)allocator, (String)alias);
        if (material == null) {
            return null;
        }
        OpenSslKeyMaterial old = this.cache.putIfAbsent((String)alias, (OpenSslKeyMaterial)material);
        if (old == null) return material.retain();
        material.release();
        material = old;
        return material.retain();
    }

    @Override
    void destroy() {
        do {
            Iterator<V> iterator = this.cache.values().iterator();
            while (iterator.hasNext()) {
                ((OpenSslKeyMaterial)iterator.next()).release();
                iterator.remove();
            }
        } while (!this.cache.isEmpty());
    }
}

