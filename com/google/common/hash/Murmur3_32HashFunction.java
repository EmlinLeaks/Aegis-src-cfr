/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.hash;

import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Murmur3_32HashFunction;
import java.io.Serializable;
import javax.annotation.Nullable;

final class Murmur3_32HashFunction
extends AbstractStreamingHashFunction
implements Serializable {
    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private final int seed;
    private static final long serialVersionUID = 0L;

    Murmur3_32HashFunction(int seed) {
        this.seed = seed;
    }

    @Override
    public int bits() {
        return 32;
    }

    @Override
    public Hasher newHasher() {
        return new Murmur3_32Hasher((int)this.seed);
    }

    public String toString() {
        return "Hashing.murmur3_32(" + this.seed + ")";
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Murmur3_32HashFunction)) return false;
        Murmur3_32HashFunction other = (Murmur3_32HashFunction)object;
        if (this.seed != other.seed) return false;
        return true;
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ this.seed;
    }

    @Override
    public HashCode hashInt(int input) {
        int k1 = Murmur3_32HashFunction.mixK1((int)input);
        int h1 = Murmur3_32HashFunction.mixH1((int)this.seed, (int)k1);
        return Murmur3_32HashFunction.fmix((int)h1, (int)4);
    }

    @Override
    public HashCode hashLong(long input) {
        int low = (int)input;
        int high = (int)(input >>> 32);
        int k1 = Murmur3_32HashFunction.mixK1((int)low);
        int h1 = Murmur3_32HashFunction.mixH1((int)this.seed, (int)k1);
        k1 = Murmur3_32HashFunction.mixK1((int)high);
        h1 = Murmur3_32HashFunction.mixH1((int)h1, (int)k1);
        return Murmur3_32HashFunction.fmix((int)h1, (int)8);
    }

    @Override
    public HashCode hashUnencodedChars(CharSequence input) {
        int h1 = this.seed;
        int i = 1;
        do {
            if (i >= input.length()) {
                if ((input.length() & 1) != 1) return Murmur3_32HashFunction.fmix((int)h1, (int)(2 * input.length()));
                int k1 = input.charAt((int)(input.length() - 1));
                k1 = Murmur3_32HashFunction.mixK1((int)k1);
                h1 ^= k1;
                return Murmur3_32HashFunction.fmix((int)h1, (int)(2 * input.length()));
            }
            int k1 = input.charAt((int)(i - 1)) | input.charAt((int)i) << 16;
            k1 = Murmur3_32HashFunction.mixK1((int)k1);
            h1 = Murmur3_32HashFunction.mixH1((int)h1, (int)k1);
            i += 2;
        } while (true);
    }

    private static int mixK1(int k1) {
        k1 *= -862048943;
        k1 = Integer.rotateLeft((int)k1, (int)15);
        return k1 *= 461845907;
    }

    private static int mixH1(int h1, int k1) {
        h1 ^= k1;
        h1 = Integer.rotateLeft((int)h1, (int)13);
        return h1 * 5 + -430675100;
    }

    private static HashCode fmix(int h1, int length) {
        h1 ^= length;
        h1 ^= h1 >>> 16;
        h1 *= -2048144789;
        h1 ^= h1 >>> 13;
        h1 *= -1028477387;
        h1 ^= h1 >>> 16;
        return HashCode.fromInt((int)h1);
    }

    static /* synthetic */ int access$000(int x0) {
        return Murmur3_32HashFunction.mixK1((int)x0);
    }

    static /* synthetic */ int access$100(int x0, int x1) {
        return Murmur3_32HashFunction.mixH1((int)x0, (int)x1);
    }

    static /* synthetic */ HashCode access$200(int x0, int x1) {
        return Murmur3_32HashFunction.fmix((int)x0, (int)x1);
    }
}

