/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.Crc32cHashFunction;
import com.google.common.hash.Hasher;

final class Crc32cHashFunction
extends AbstractStreamingHashFunction {
    Crc32cHashFunction() {
    }

    @Override
    public int bits() {
        return 32;
    }

    @Override
    public Hasher newHasher() {
        return new Crc32cHasher();
    }

    public String toString() {
        return "Hashing.crc32c()";
    }
}

