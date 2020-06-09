/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.Escaper;
import com.google.common.escape.Platform;

@Beta
@GwtCompatible
public abstract class CharEscaper
extends Escaper {
    private static final int DEST_PAD_MULTIPLIER = 2;

    protected CharEscaper() {
    }

    @Override
    public String escape(String string) {
        Preconditions.checkNotNull(string);
        int length = string.length();
        int index = 0;
        while (index < length) {
            if (this.escape((char)string.charAt((int)index)) != null) {
                return this.escapeSlow((String)string, (int)index);
            }
            ++index;
        }
        return string;
    }

    protected final String escapeSlow(String s, int index) {
        int slen = s.length();
        char[] dest = Platform.charBufferFromThreadLocal();
        int destSize = dest.length;
        int destIndex = 0;
        int lastEscape = 0;
        while (index < slen) {
            char[] r = this.escape((char)s.charAt((int)index));
            if (r != null) {
                int charsSkipped = index - lastEscape;
                int rlen = r.length;
                int sizeNeeded = destIndex + charsSkipped + rlen;
                if (destSize < sizeNeeded) {
                    destSize = sizeNeeded + 2 * (slen - index);
                    dest = CharEscaper.growBuffer((char[])dest, (int)destIndex, (int)destSize);
                }
                if (charsSkipped > 0) {
                    s.getChars((int)lastEscape, (int)index, (char[])dest, (int)destIndex);
                    destIndex += charsSkipped;
                }
                if (rlen > 0) {
                    System.arraycopy((Object)r, (int)0, (Object)dest, (int)destIndex, (int)rlen);
                    destIndex += rlen;
                }
                lastEscape = index + 1;
            }
            ++index;
        }
        int charsLeft = slen - lastEscape;
        if (charsLeft <= 0) return new String((char[])dest, (int)0, (int)destIndex);
        int sizeNeeded = destIndex + charsLeft;
        if (destSize < sizeNeeded) {
            dest = CharEscaper.growBuffer((char[])dest, (int)destIndex, (int)sizeNeeded);
        }
        s.getChars((int)lastEscape, (int)slen, (char[])dest, (int)destIndex);
        destIndex = sizeNeeded;
        return new String((char[])dest, (int)0, (int)destIndex);
    }

    protected abstract char[] escape(char var1);

    private static char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index <= 0) return copy;
        System.arraycopy((Object)dest, (int)0, (Object)copy, (int)0, (int)index);
        return copy;
    }
}

