/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.HashingStrategy;

public interface HashingStrategy<T> {
    public static final HashingStrategy JAVA_HASHER = new HashingStrategy(){

        public int hashCode(Object obj) {
            if (obj == null) return 0;
            int n = obj.hashCode();
            return n;
        }

        public boolean equals(Object a, Object b) {
            if (a == b) return true;
            if (a == null) return false;
            if (!a.equals((Object)b)) return false;
            return true;
        }
    };

    public int hashCode(T var1);

    public boolean equals(T var1, T var2);
}

