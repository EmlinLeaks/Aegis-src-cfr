/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

@Deprecated
public interface ResourceLeak {
    public void record();

    public void record(Object var1);

    public boolean close();
}

