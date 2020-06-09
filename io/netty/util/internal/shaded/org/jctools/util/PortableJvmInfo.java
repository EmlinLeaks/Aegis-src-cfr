/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.util;

public interface PortableJvmInfo {
    public static final int CACHE_LINE_SIZE = Integer.getInteger((String)"jctools.cacheLineSize", (int)64).intValue();
    public static final int CPUs = Runtime.getRuntime().availableProcessors();
    public static final int RECOMENDED_OFFER_BATCH = CPUs * 4;
    public static final int RECOMENDED_POLL_BATCH = CPUs * 4;
}

