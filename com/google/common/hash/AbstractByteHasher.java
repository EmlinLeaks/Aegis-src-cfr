/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractHasher;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hasher;
import com.google.common.hash.PrimitiveSink;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@CanIgnoreReturnValue
abstract class AbstractByteHasher
extends AbstractHasher {
    private final ByteBuffer scratch = ByteBuffer.allocate((int)8).order((ByteOrder)ByteOrder.LITTLE_ENDIAN);

    AbstractByteHasher() {
    }

    protected abstract void update(byte var1);

    protected void update(byte[] b) {
        this.update((byte[])b, (int)0, (int)b.length);
    }

    protected void update(byte[] b, int off, int len) {
        int i = off;
        while (i < off + len) {
            this.update((byte)b[i]);
            ++i;
        }
    }

    @Override
    public Hasher putByte(byte b) {
        this.update((byte)b);
        return this;
    }

    @Override
    public Hasher putBytes(byte[] bytes) {
        Preconditions.checkNotNull(bytes);
        this.update((byte[])bytes);
        return this;
    }

    @Override
    public Hasher putBytes(byte[] bytes, int off, int len) {
        Preconditions.checkPositionIndexes((int)off, (int)(off + len), (int)bytes.length);
        this.update((byte[])bytes, (int)off, (int)len);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Hasher update(int bytes) {
        try {
            this.update((byte[])this.scratch.array(), (int)0, (int)bytes);
            return this;
        }
        finally {
            this.scratch.clear();
        }
    }

    @Override
    public Hasher putShort(short s) {
        this.scratch.putShort((short)s);
        return this.update((int)2);
    }

    @Override
    public Hasher putInt(int i) {
        this.scratch.putInt((int)i);
        return this.update((int)4);
    }

    @Override
    public Hasher putLong(long l) {
        this.scratch.putLong((long)l);
        return this.update((int)8);
    }

    @Override
    public Hasher putChar(char c) {
        this.scratch.putChar((char)c);
        return this.update((int)2);
    }

    @Override
    public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
        funnel.funnel(instance, (PrimitiveSink)this);
        return this;
    }
}

