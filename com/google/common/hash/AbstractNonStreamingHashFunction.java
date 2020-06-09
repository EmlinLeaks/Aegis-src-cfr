/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractNonStreamingHashFunction;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.nio.charset.Charset;

abstract class AbstractNonStreamingHashFunction
implements HashFunction {
    AbstractNonStreamingHashFunction() {
    }

    @Override
    public Hasher newHasher() {
        return new BufferingHasher((AbstractNonStreamingHashFunction)this, (int)32);
    }

    @Override
    public Hasher newHasher(int expectedInputSize) {
        Preconditions.checkArgument((boolean)(expectedInputSize >= 0));
        return new BufferingHasher((AbstractNonStreamingHashFunction)this, (int)expectedInputSize);
    }

    @Override
    public <T> HashCode hashObject(T instance, Funnel<? super T> funnel) {
        return this.newHasher().putObject(instance, funnel).hash();
    }

    @Override
    public HashCode hashUnencodedChars(CharSequence input) {
        int len = input.length();
        Hasher hasher = this.newHasher((int)(len * 2));
        int i = 0;
        while (i < len) {
            hasher.putChar((char)input.charAt((int)i));
            ++i;
        }
        return hasher.hash();
    }

    @Override
    public HashCode hashString(CharSequence input, Charset charset) {
        return this.hashBytes((byte[])input.toString().getBytes((Charset)charset));
    }

    @Override
    public HashCode hashInt(int input) {
        return this.newHasher((int)4).putInt((int)input).hash();
    }

    @Override
    public HashCode hashLong(long input) {
        return this.newHasher((int)8).putLong((long)input).hash();
    }

    @Override
    public HashCode hashBytes(byte[] input) {
        return this.hashBytes((byte[])input, (int)0, (int)input.length);
    }
}

