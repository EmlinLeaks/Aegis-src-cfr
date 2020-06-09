/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.nio.charset.Charset;

abstract class AbstractStreamingHashFunction
implements HashFunction {
    AbstractStreamingHashFunction() {
    }

    @Override
    public <T> HashCode hashObject(T instance, Funnel<? super T> funnel) {
        return this.newHasher().putObject(instance, funnel).hash();
    }

    @Override
    public HashCode hashUnencodedChars(CharSequence input) {
        return this.newHasher().putUnencodedChars((CharSequence)input).hash();
    }

    @Override
    public HashCode hashString(CharSequence input, Charset charset) {
        return this.newHasher().putString((CharSequence)input, (Charset)charset).hash();
    }

    @Override
    public HashCode hashInt(int input) {
        return this.newHasher().putInt((int)input).hash();
    }

    @Override
    public HashCode hashLong(long input) {
        return this.newHasher().putLong((long)input).hash();
    }

    @Override
    public HashCode hashBytes(byte[] input) {
        return this.newHasher().putBytes((byte[])input).hash();
    }

    @Override
    public HashCode hashBytes(byte[] input, int off, int len) {
        return this.newHasher().putBytes((byte[])input, (int)off, (int)len).hash();
    }

    @Override
    public Hasher newHasher(int expectedInputSize) {
        Preconditions.checkArgument((boolean)(expectedInputSize >= 0));
        return this.newHasher();
    }
}

