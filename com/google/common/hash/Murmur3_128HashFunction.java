/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.hash;

import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Murmur3_128HashFunction;
import java.io.Serializable;
import javax.annotation.Nullable;

final class Murmur3_128HashFunction
extends AbstractStreamingHashFunction
implements Serializable {
    private final int seed;
    private static final long serialVersionUID = 0L;

    Murmur3_128HashFunction(int seed) {
        this.seed = seed;
    }

    @Override
    public int bits() {
        return 128;
    }

    @Override
    public Hasher newHasher() {
        return new Murmur3_128Hasher((int)this.seed);
    }

    public String toString() {
        return "Hashing.murmur3_128(" + this.seed + ")";
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Murmur3_128HashFunction)) return false;
        Murmur3_128HashFunction other = (Murmur3_128HashFunction)object;
        if (this.seed != other.seed) return false;
        return true;
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ this.seed;
    }
}

