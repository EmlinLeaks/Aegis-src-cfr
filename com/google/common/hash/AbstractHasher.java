/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.hash;

import com.google.common.hash.Hasher;
import com.google.common.hash.PrimitiveSink;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.nio.charset.Charset;

@CanIgnoreReturnValue
abstract class AbstractHasher
implements Hasher {
    AbstractHasher() {
    }

    @Override
    public final Hasher putBoolean(boolean b) {
        byte by;
        if (b) {
            by = 1;
            return this.putByte((byte)by);
        }
        by = 0;
        return this.putByte((byte)by);
    }

    @Override
    public final Hasher putDouble(double d) {
        return this.putLong((long)Double.doubleToRawLongBits((double)d));
    }

    @Override
    public final Hasher putFloat(float f) {
        return this.putInt((int)Float.floatToRawIntBits((float)f));
    }

    @Override
    public Hasher putUnencodedChars(CharSequence charSequence) {
        int i = 0;
        int len = charSequence.length();
        while (i < len) {
            this.putChar((char)charSequence.charAt((int)i));
            ++i;
        }
        return this;
    }

    @Override
    public Hasher putString(CharSequence charSequence, Charset charset) {
        return this.putBytes((byte[])charSequence.toString().getBytes((Charset)charset));
    }
}

