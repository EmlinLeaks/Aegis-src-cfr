/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import java.util.List;
import java.util.Set;

public interface CipherSuiteFilter {
    public String[] filterCipherSuites(Iterable<String> var1, List<String> var2, Set<String> var3);
}

