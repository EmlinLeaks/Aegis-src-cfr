/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.ChecksumHashFunction;
import com.google.common.hash.Hasher;
import java.io.Serializable;
import java.util.zip.Checksum;

final class ChecksumHashFunction
extends AbstractStreamingHashFunction
implements Serializable {
    private final Supplier<? extends Checksum> checksumSupplier;
    private final int bits;
    private final String toString;
    private static final long serialVersionUID = 0L;

    ChecksumHashFunction(Supplier<? extends Checksum> checksumSupplier, int bits, String toString) {
        this.checksumSupplier = Preconditions.checkNotNull(checksumSupplier);
        Preconditions.checkArgument((boolean)(bits == 32 || bits == 64), (String)"bits (%s) must be either 32 or 64", (int)bits);
        this.bits = bits;
        this.toString = Preconditions.checkNotNull(toString);
    }

    @Override
    public int bits() {
        return this.bits;
    }

    @Override
    public Hasher newHasher() {
        return new ChecksumHasher((ChecksumHashFunction)this, (Checksum)this.checksumSupplier.get(), null);
    }

    public String toString() {
        return this.toString;
    }

    static /* synthetic */ int access$100(ChecksumHashFunction x0) {
        return x0.bits;
    }
}

