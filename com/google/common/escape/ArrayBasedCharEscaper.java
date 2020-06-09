/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.ArrayBasedEscaperMap;
import com.google.common.escape.CharEscaper;
import java.util.Map;

@Beta
@GwtCompatible
public abstract class ArrayBasedCharEscaper
extends CharEscaper {
    private final char[][] replacements;
    private final int replacementsLength;
    private final char safeMin;
    private final char safeMax;

    protected ArrayBasedCharEscaper(Map<Character, String> replacementMap, char safeMin, char safeMax) {
        this((ArrayBasedEscaperMap)ArrayBasedEscaperMap.create(replacementMap), (char)safeMin, (char)safeMax);
    }

    protected ArrayBasedCharEscaper(ArrayBasedEscaperMap escaperMap, char safeMin, char safeMax) {
        Preconditions.checkNotNull(escaperMap);
        this.replacements = escaperMap.getReplacementArray();
        this.replacementsLength = this.replacements.length;
        if (safeMax < safeMin) {
            safeMax = '\u0000';
            safeMin = (char)65535;
        }
        this.safeMin = safeMin;
        this.safeMax = safeMax;
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
            if (c > this.safeMax) return this.escapeSlow((String)s, (int)i);
            if (c < this.safeMin) {
                return this.escapeSlow((String)s, (int)i);
            }
            ++i;
        }
        return s;
    }

    @Override
    protected final char[] escape(char c) {
        char[] chars;
        if (c < this.replacementsLength && (chars = this.replacements[c]) != null) {
            return chars;
        }
        if (c < this.safeMin) return this.escapeUnsafe((char)c);
        if (c > this.safeMax) return this.escapeUnsafe((char)c);
        return null;
    }

    protected abstract char[] escapeUnsafe(char var1);
}

