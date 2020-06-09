/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.OpenSslKeyMaterial;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import java.net.Socket;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

final class OpenSslKeyMaterialManager {
    static final String KEY_TYPE_RSA = "RSA";
    static final String KEY_TYPE_DH_RSA = "DH_RSA";
    static final String KEY_TYPE_EC = "EC";
    static final String KEY_TYPE_EC_EC = "EC_EC";
    static final String KEY_TYPE_EC_RSA = "EC_RSA";
    private static final Map<String, String> KEY_TYPES = new HashMap<String, String>();
    private final OpenSslKeyMaterialProvider provider;

    OpenSslKeyMaterialManager(OpenSslKeyMaterialProvider provider) {
        this.provider = provider;
    }

    void setKeyMaterialServerSide(ReferenceCountedOpenSslEngine engine) throws SSLException {
        String[] authMethods = engine.authMethods();
        if (authMethods.length == 0) {
            return;
        }
        HashSet<String> aliases = new HashSet<String>((int)authMethods.length);
        String[] arrstring = authMethods;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String alias;
            String authMethod = arrstring[n2];
            String type = KEY_TYPES.get((Object)authMethod);
            if (type != null && (alias = this.chooseServerAlias((ReferenceCountedOpenSslEngine)engine, (String)type)) != null && aliases.add(alias) && !this.setKeyMaterial((ReferenceCountedOpenSslEngine)engine, (String)alias)) {
                return;
            }
            ++n2;
        }
    }

    void setKeyMaterialClientSide(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) throws SSLException {
        String alias = this.chooseClientAlias((ReferenceCountedOpenSslEngine)engine, (String[])keyTypes, (X500Principal[])issuer);
        if (alias == null) return;
        this.setKeyMaterial((ReferenceCountedOpenSslEngine)engine, (String)alias);
    }

    private boolean setKeyMaterial(ReferenceCountedOpenSslEngine engine, String alias) throws SSLException {
        OpenSslKeyMaterial keyMaterial = null;
        try {
            keyMaterial = this.provider.chooseKeyMaterial((ByteBufAllocator)engine.alloc, (String)alias);
            boolean bl = keyMaterial == null || engine.setKeyMaterial((OpenSslKeyMaterial)keyMaterial);
            return bl;
        }
        catch (SSLException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SSLException((Throwable)e);
        }
        finally {
            if (keyMaterial != null) {
                keyMaterial.release();
            }
        }
    }

    private String chooseClientAlias(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) {
        X509KeyManager manager = this.provider.keyManager();
        if (!(manager instanceof X509ExtendedKeyManager)) return manager.chooseClientAlias((String[])keyTypes, (Principal[])issuer, null);
        return ((X509ExtendedKeyManager)manager).chooseEngineClientAlias((String[])keyTypes, (Principal[])issuer, (SSLEngine)engine);
    }

    private String chooseServerAlias(ReferenceCountedOpenSslEngine engine, String type) {
        X509KeyManager manager = this.provider.keyManager();
        if (!(manager instanceof X509ExtendedKeyManager)) return manager.chooseServerAlias((String)type, null, null);
        return ((X509ExtendedKeyManager)manager).chooseEngineServerAlias((String)type, null, (SSLEngine)engine);
    }

    static {
        KEY_TYPES.put((String)KEY_TYPE_RSA, (String)KEY_TYPE_RSA);
        KEY_TYPES.put((String)"DHE_RSA", (String)KEY_TYPE_RSA);
        KEY_TYPES.put((String)"ECDHE_RSA", (String)KEY_TYPE_RSA);
        KEY_TYPES.put((String)"ECDHE_ECDSA", (String)KEY_TYPE_EC);
        KEY_TYPES.put((String)"ECDH_RSA", (String)KEY_TYPE_EC_RSA);
        KEY_TYPES.put((String)"ECDH_ECDSA", (String)KEY_TYPE_EC_EC);
        KEY_TYPES.put((String)KEY_TYPE_DH_RSA, (String)KEY_TYPE_DH_RSA);
    }
}

