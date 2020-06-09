/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.MacHashFunction;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;

final class MacHashFunction
extends AbstractStreamingHashFunction {
    private final Mac prototype;
    private final Key key;
    private final String toString;
    private final int bits;
    private final boolean supportsClone;

    MacHashFunction(String algorithmName, Key key, String toString) {
        this.prototype = MacHashFunction.getMac((String)algorithmName, (Key)key);
        this.key = Preconditions.checkNotNull(key);
        this.toString = Preconditions.checkNotNull(toString);
        this.bits = this.prototype.getMacLength() * 8;
        this.supportsClone = MacHashFunction.supportsClone((Mac)this.prototype);
    }

    @Override
    public int bits() {
        return this.bits;
    }

    private static boolean supportsClone(Mac mac) {
        try {
            mac.clone();
            return true;
        }
        catch (CloneNotSupportedException e) {
            return false;
        }
    }

    private static Mac getMac(String algorithmName, Key key) {
        try {
            Mac mac = Mac.getInstance((String)algorithmName);
            mac.init((Key)key);
            return mac;
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException((Throwable)e);
        }
        catch (InvalidKeyException e) {
            throw new IllegalArgumentException((Throwable)e);
        }
    }

    @Override
    public Hasher newHasher() {
        if (!this.supportsClone) return new MacHasher((Mac)MacHashFunction.getMac((String)this.prototype.getAlgorithm(), (Key)this.key), null);
        try {
            return new MacHasher((Mac)((Mac)this.prototype.clone()), null);
        }
        catch (CloneNotSupportedException e) {
            // empty catch block
        }
        return new MacHasher((Mac)MacHashFunction.getMac((String)this.prototype.getAlgorithm(), (Key)this.key), null);
    }

    public String toString() {
        return this.toString;
    }
}

