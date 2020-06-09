/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.CipherSuiteFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class IdentityCipherSuiteFilter
implements CipherSuiteFilter {
    public static final IdentityCipherSuiteFilter INSTANCE = new IdentityCipherSuiteFilter((boolean)true);
    public static final IdentityCipherSuiteFilter INSTANCE_DEFAULTING_TO_SUPPORTED_CIPHERS = new IdentityCipherSuiteFilter((boolean)false);
    private final boolean defaultToDefaultCiphers;

    private IdentityCipherSuiteFilter(boolean defaultToDefaultCiphers) {
        this.defaultToDefaultCiphers = defaultToDefaultCiphers;
    }

    @Override
    public String[] filterCipherSuites(Iterable<String> ciphers, List<String> defaultCiphers, Set<String> supportedCiphers) {
        if (ciphers == null) {
            String[] arrstring;
            if (this.defaultToDefaultCiphers) {
                arrstring = defaultCiphers.toArray(new String[0]);
                return arrstring;
            }
            arrstring = supportedCiphers.toArray(new String[0]);
            return arrstring;
        }
        ArrayList<String> newCiphers = new ArrayList<String>((int)supportedCiphers.size());
        Iterator<String> iterator = ciphers.iterator();
        while (iterator.hasNext()) {
            String c = iterator.next();
            if (c == null) {
                return newCiphers.toArray(new String[0]);
            }
            newCiphers.add(c);
        }
        return newCiphers.toArray(new String[0]);
    }
}

