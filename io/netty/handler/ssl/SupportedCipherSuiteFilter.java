/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.CipherSuiteFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class SupportedCipherSuiteFilter
implements CipherSuiteFilter {
    public static final SupportedCipherSuiteFilter INSTANCE = new SupportedCipherSuiteFilter();

    private SupportedCipherSuiteFilter() {
    }

    @Override
    public String[] filterCipherSuites(Iterable<String> ciphers, List<String> defaultCiphers, Set<String> supportedCiphers) {
        ArrayList<String> newCiphers;
        if (defaultCiphers == null) {
            throw new NullPointerException((String)"defaultCiphers");
        }
        if (supportedCiphers == null) {
            throw new NullPointerException((String)"supportedCiphers");
        }
        if (ciphers == null) {
            newCiphers = new ArrayList<String>((int)defaultCiphers.size());
            ciphers = defaultCiphers;
        } else {
            newCiphers = new ArrayList<E>((int)supportedCiphers.size());
        }
        Iterator<String> iterator = ciphers.iterator();
        while (iterator.hasNext()) {
            String c = iterator.next();
            if (c == null) {
                return newCiphers.toArray(new String[0]);
            }
            if (!supportedCiphers.contains((Object)c)) continue;
            newCiphers.add(c);
        }
        return newCiphers.toArray(new String[0]);
    }
}

