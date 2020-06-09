/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.serialization;

public interface ClassResolver {
    public Class<?> resolve(String var1) throws ClassNotFoundException;
}

