/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.ArrayBasedEscaperMap;
import com.google.common.escape.UnicodeEscaper;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public abstract class ArrayBasedUnicodeEscaper
extends UnicodeEscaper {
    private final char[][] replacements;
    private final int replacementsLength;
    private final int safeMin;
    private final int safeMax;
    private final char safeMinChar;
    private final char safeMaxChar;

    protected ArrayBasedUnicodeEscaper(Map<Character, String> replacementMap, int safeMin, int safeMax, @Nullable String unsafeReplacement) {
        this((ArrayBasedEscaperMap)ArrayBasedEscaperMap.create(replacementMap), (int)safeMin, (int)safeMax, (String)unsafeReplacement);
    }

    protected ArrayBasedUnicodeEscaper(ArrayBasedEscaperMap escaperMap, int safeMin, int safeMax, @Nullable String unsafeReplacement) {
        Preconditions.checkNotNull(escaperMap);
        this.replacements = escaperMap.getReplacementArray();
        this.replacementsLength = this.replacements.length;
        if (safeMax < safeMin) {
            safeMax = -1;
            safeMin = Integer.MAX_VALUE;
        }
        this.safeMin = safeMin;
        this.safeMax = safeMax;
        if (safeMin >= 55296) {
            this.safeMinChar = (char)65535;
            this.safeMaxChar = '\u0000';
            return;
        }
        this.safeMinChar = (char)safeMin;
        this.safeMaxChar = (char)Math.min((int)safeMax, (int)55295);
    }

    @Override
    public final String escape(String s) {
        Preconditions.checkNotNull(s);
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt((int)i);
            if (c < this.replacementsLength) {
                if (this.replacements[c] != null) return this.escapeSlow((String)s, (int)i);
            }
            if (c > this.safeMaxChar) return this.escapeSlow((String)s, (int)i);
            if (c < this.safeMinChar) {
                return this.escapeSlow((String)s, (int)i);
            }
            ++i;
        }
        return s;
    }

    @Override
    protected final int nextEscapeIndex(CharSequence csq, int index, int end) {
        while (index < end) {
            char c = csq.charAt((int)index);
            if (c < this.replacementsLength) {
                if (this.replacements[c] != null) return index;
            }
            if (c > this.safeMaxChar) return index;
            if (c < this.safeMinChar) {
                return index;
            }
            ++index;
        }
        return index;
    }

    @Override
    protected final char[] escape(int cp) {
        char[] chars;
        if (cp < this.replacementsLength && (chars = this.replacements[cp]) != null) {
            return chars;
        }
        if (cp < this.safeMin) return this.escapeUnsafe((int)cp);
        if (cp > this.safeMax) return this.escapeUnsafe((int)cp);
        return null;
    }

    protected abstract char[] escapeUnsafe(int var1);
}

