/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Beta
public final class HashingOutputStream
extends FilterOutputStream {
    private final Hasher hasher;

    public HashingOutputStream(HashFunction hashFunction, OutputStream out) {
        super((OutputStream)Preconditions.checkNotNull(out));
        this.hasher = Preconditions.checkNotNull(hashFunction.newHasher());
    }

    @Override
    public void write(int b) throws IOException {
        this.hasher.putByte((byte)((byte)b));
        this.out.write((int)b);
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.hasher.putBytes((byte[])bytes, (int)off, (int)len);
        this.out.write((byte[])bytes, (int)off, (int)len);
    }

    public HashCode hash() {
        return this.hasher.hash();
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }
}

