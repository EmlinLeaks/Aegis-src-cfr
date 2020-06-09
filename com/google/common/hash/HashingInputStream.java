/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Beta
public final class HashingInputStream
extends FilterInputStream {
    private final Hasher hasher;

    public HashingInputStream(HashFunction hashFunction, InputStream in) {
        super((InputStream)Preconditions.checkNotNull(in));
        this.hasher = Preconditions.checkNotNull(hashFunction.newHasher());
    }

    @CanIgnoreReturnValue
    @Override
    public int read() throws IOException {
        int b = this.in.read();
        if (b == -1) return b;
        this.hasher.putByte((byte)((byte)b));
        return b;
    }

    @CanIgnoreReturnValue
    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        int numOfBytesRead = this.in.read((byte[])bytes, (int)off, (int)len);
        if (numOfBytesRead == -1) return numOfBytesRead;
        this.hasher.putBytes((byte[])bytes, (int)off, (int)numOfBytesRead);
        return numOfBytesRead;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int readlimit) {
    }

    @Override
    public void reset() throws IOException {
        throw new IOException((String)"reset not supported");
    }

    public HashCode hash() {
        return this.hasher.hash();
    }
}

