/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@Beta
public abstract class HashCode {
    private static final char[] hexDigits = "0123456789abcdef".toCharArray();

    HashCode() {
    }

    public abstract int bits();

    public abstract int asInt();

    public abstract long asLong();

    public abstract long padToLong();

    public abstract byte[] asBytes();

    @CanIgnoreReturnValue
    public int writeBytesTo(byte[] dest, int offset, int maxLength) {
        maxLength = Ints.min((int[])new int[]{maxLength, this.bits() / 8});
        Preconditions.checkPositionIndexes((int)offset, (int)(offset + maxLength), (int)dest.length);
        this.writeBytesToImpl((byte[])dest, (int)offset, (int)maxLength);
        return maxLength;
    }

    abstract void writeBytesToImpl(byte[] var1, int var2, int var3);

    byte[] getBytesInternal() {
        return this.asBytes();
    }

    abstract boolean equalsSameBits(HashCode var1);

    public static HashCode fromInt(int hash) {
        return new IntHashCode((int)hash);
    }

    public static HashCode fromLong(long hash) {
        return new LongHashCode((long)hash);
    }

    public static HashCode fromBytes(byte[] bytes) {
        Preconditions.checkArgument((boolean)(bytes.length >= 1), (Object)"A HashCode must contain at least 1 byte.");
        return HashCode.fromBytesNoCopy((byte[])((byte[])bytes.clone()));
    }

    static HashCode fromBytesNoCopy(byte[] bytes) {
        return new BytesHashCode((byte[])bytes);
    }

    public static HashCode fromString(String string) {
        Preconditions.checkArgument((boolean)(string.length() >= 2), (String)"input string (%s) must have at least 2 characters", (Object)string);
        Preconditions.checkArgument((boolean)(string.length() % 2 == 0), (String)"input string (%s) must have an even number of characters", (Object)string);
        byte[] bytes = new byte[string.length() / 2];
        int i = 0;
        while (i < string.length()) {
            int ch1 = HashCode.decode((char)string.charAt((int)i)) << 4;
            int ch2 = HashCode.decode((char)string.charAt((int)(i + 1)));
            bytes[i / 2] = (byte)(ch1 + ch2);
            i += 2;
        }
        return HashCode.fromBytesNoCopy((byte[])bytes);
    }

    private static int decode(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - 48;
        }
        if (ch < 'a') throw new IllegalArgumentException((String)("Illegal hexadecimal character: " + ch));
        if (ch > 'f') throw new IllegalArgumentException((String)("Illegal hexadecimal character: " + ch));
        return ch - 97 + 10;
    }

    public final boolean equals(@Nullable Object object) {
        if (!(object instanceof HashCode)) return false;
        HashCode that = (HashCode)object;
        if (this.bits() != that.bits()) return false;
        if (!this.equalsSameBits((HashCode)that)) return false;
        return true;
    }

    public final int hashCode() {
        if (this.bits() >= 32) {
            return this.asInt();
        }
        byte[] bytes = this.getBytesInternal();
        int val = bytes[0] & 255;
        int i = 1;
        while (i < bytes.length) {
            val |= (bytes[i] & 255) << i * 8;
            ++i;
        }
        return val;
    }

    public final String toString() {
        byte[] bytes = this.getBytesInternal();
        StringBuilder sb = new StringBuilder((int)(2 * bytes.length));
        byte[] arr$ = bytes;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            byte b = arr$[i$];
            sb.append((char)hexDigits[b >> 4 & 15]).append((char)hexDigits[b & 15]);
            ++i$;
        }
        return sb.toString();
    }
}

