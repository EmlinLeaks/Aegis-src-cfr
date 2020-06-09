/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.SipHashFunction;
import java.io.Serializable;
import javax.annotation.Nullable;

final class SipHashFunction
extends AbstractStreamingHashFunction
implements Serializable {
    private final int c;
    private final int d;
    private final long k0;
    private final long k1;
    private static final long serialVersionUID = 0L;

    SipHashFunction(int c, int d, long k0, long k1) {
        Preconditions.checkArgument((boolean)(c > 0), (String)"The number of SipRound iterations (c=%s) during Compression must be positive.", (int)c);
        Preconditions.checkArgument((boolean)(d > 0), (String)"The number of SipRound iterations (d=%s) during Finalization must be positive.", (int)d);
        this.c = c;
        this.d = d;
        this.k0 = k0;
        this.k1 = k1;
    }

    @Override
    public int bits() {
        return 64;
    }

    @Override
    public Hasher newHasher() {
        return new SipHasher((int)this.c, (int)this.d, (long)this.k0, (long)this.k1);
    }

    public String toString() {
        return "Hashing.sipHash" + this.c + "" + this.d + "(" + this.k0 + ", " + this.k1 + ")";
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof SipHashFunction)) return false;
        SipHashFunction other = (SipHashFunction)object;
        if (this.c != other.c) return false;
        if (this.d != other.d) return false;
        if (this.k0 != other.k0) return false;
        if (this.k1 != other.k1) return false;
        return true;
    }

    public int hashCode() {
        return (int)((long)(this.getClass().hashCode() ^ this.c ^ this.d) ^ this.k0 ^ this.k1);
    }
}

