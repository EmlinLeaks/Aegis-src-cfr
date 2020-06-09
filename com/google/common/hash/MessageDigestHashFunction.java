/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.MessageDigestHashFunction;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class MessageDigestHashFunction
extends AbstractStreamingHashFunction
implements Serializable {
    private final MessageDigest prototype;
    private final int bytes;
    private final boolean supportsClone;
    private final String toString;

    MessageDigestHashFunction(String algorithmName, String toString) {
        this.prototype = MessageDigestHashFunction.getMessageDigest((String)algorithmName);
        this.bytes = this.prototype.getDigestLength();
        this.toString = Preconditions.checkNotNull(toString);
        this.supportsClone = MessageDigestHashFunction.supportsClone((MessageDigest)this.prototype);
    }

    MessageDigestHashFunction(String algorithmName, int bytes, String toString) {
        this.toString = Preconditions.checkNotNull(toString);
        this.prototype = MessageDigestHashFunction.getMessageDigest((String)algorithmName);
        int maxLength = this.prototype.getDigestLength();
        Preconditions.checkArgument((boolean)(bytes >= 4 && bytes <= maxLength), (String)"bytes (%s) must be >= 4 and < %s", (int)bytes, (int)maxLength);
        this.bytes = bytes;
        this.supportsClone = MessageDigestHashFunction.supportsClone((MessageDigest)this.prototype);
    }

    private static boolean supportsClone(MessageDigest digest) {
        try {
            digest.clone();
            return true;
        }
        catch (CloneNotSupportedException e) {
            return false;
        }
    }

    @Override
    public int bits() {
        return this.bytes * 8;
    }

    public String toString() {
        return this.toString;
    }

    private static MessageDigest getMessageDigest(String algorithmName) {
        try {
            return MessageDigest.getInstance((String)algorithmName);
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError((Object)e);
        }
    }

    @Override
    public Hasher newHasher() {
        if (!this.supportsClone) return new MessageDigestHasher((MessageDigest)MessageDigestHashFunction.getMessageDigest((String)this.prototype.getAlgorithm()), (int)this.bytes, null);
        try {
            return new MessageDigestHasher((MessageDigest)((MessageDigest)this.prototype.clone()), (int)this.bytes, null);
        }
        catch (CloneNotSupportedException e) {
            // empty catch block
        }
        return new MessageDigestHasher((MessageDigest)MessageDigestHashFunction.getMessageDigest((String)this.prototype.getAlgorithm()), (int)this.bytes, null);
    }

    Object writeReplace() {
        return new SerializedForm((String)this.prototype.getAlgorithm(), (int)this.bytes, (String)this.toString, null);
    }
}

