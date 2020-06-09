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
public abstract class UnicodeEscaper
extends Escaper {
    private static final int DEST_PAD = 32;

    protected UnicodeEscaper() {
    }

    protected abstract char[] escape(int var1);

    protected int nextEscapeIndex(CharSequence csq, int start, int end) {
        int index = start;
        while (index < end) {
            int cp = UnicodeEscaper.codePointAt((CharSequence)csq, (int)index, (int)end);
            if (cp < 0) return index;
            if (this.escape((int)cp) != null) {
                return index;
            }
            index += Character.isSupplementaryCodePoint((int)cp) ? 2 : 1;
        }
        return index;
    }

    @Override
    public String escape(String string) {
        String string2;
        Preconditions.checkNotNull(string);
        int end = string.length();
        int index = this.nextEscapeIndex((CharSequence)string, (int)0, (int)end);
        if (index == end) {
            string2 = string;
            return string2;
        }
        string2 = this.escapeSlow((String)string, (int)index);
        return string2;
    }

    protected final String escapeSlow(String s, int index) {
        int end = s.length();
        char[] dest = Platform.charBufferFromThreadLocal();
        int destIndex = 0;
        int unescapedChunkStart = 0;
        while (index < end) {
            int cp = UnicodeEscaper.codePointAt((CharSequence)s, (int)index, (int)end);
            if (cp < 0) {
                throw new IllegalArgumentException((String)"Trailing high surrogate at end of input");
            }
            char[] escaped = this.escape((int)cp);
            int nextIndex = index + (Character.isSupplementaryCodePoint((int)cp) ? 2 : 1);
            if (escaped != null) {
                int charsSkipped = index - unescapedChunkStart;
                int sizeNeeded = destIndex + charsSkipped + escaped.length;
                if (dest.length < sizeNeeded) {
                    int destLength = sizeNeeded + (end - index) + 32;
                    dest = UnicodeEscaper.growBuffer((char[])dest, (int)destIndex, (int)destLength);
                }
                if (charsSkipped > 0) {
                    s.getChars((int)unescapedChunkStart, (int)index, (char[])dest, (int)destIndex);
                    destIndex += charsSkipped;
                }
                if (escaped.length > 0) {
                    System.arraycopy((Object)escaped, (int)0, (Object)dest, (int)destIndex, (int)escaped.length);
                    destIndex += escaped.length;
                }
                unescapedChunkStart = nextIndex;
            }
            index = this.nextEscapeIndex((CharSequence)s, (int)nextIndex, (int)end);
        }
        int charsSkipped = end - unescapedChunkStart;
        if (charsSkipped <= 0) return new String((char[])dest, (int)0, (int)destIndex);
        int endIndex = destIndex + charsSkipped;
        if (dest.length < endIndex) {
            dest = UnicodeEscaper.growBuffer((char[])dest, (int)destIndex, (int)endIndex);
        }
        s.getChars((int)unescapedChunkStart, (int)end, (char[])dest, (int)destIndex);
        destIndex = endIndex;
        return new String((char[])dest, (int)0, (int)destIndex);
    }

    protected static int codePointAt(CharSequence seq, int index, int end) {
        char c1;
        Preconditions.checkNotNull(seq);
        if (index >= end) throw new IndexOutOfBoundsException((String)"Index exceeds specified range");
        if ((c1 = seq.charAt((int)index++)) < '\ud800') return c1;
        if (c1 > '\udfff') {
            return c1;
        }
        if (c1 > '\udbff') throw new IllegalArgumentException((String)("Unexpected low surrogate character '" + c1 + "' with value " + c1 + " at index " + (index - 1) + " in '" + seq + "'"));
        if (index == end) {
            return -c1;
        }
        char c2 = seq.charAt((int)index);
        if (!Character.isLowSurrogate((char)c2)) throw new IllegalArgumentException((String)("Expected low surrogate but got char '" + c2 + "' with value " + c2 + " at index " + index + " in '" + seq + "'"));
        return Character.toCodePoint((char)c1, (char)c2);
    }

    private static char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index <= 0) return copy;
        System.arraycopy((Object)dest, (int)0, (Object)copy, (int)0, (int)index);
        return copy;
    }
}

