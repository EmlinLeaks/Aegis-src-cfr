/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.CharEscaperBuilder;
import com.google.common.escape.Escaper;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Beta
@GwtCompatible
public final class CharEscaperBuilder {
    private final Map<Character, String> map = new HashMap<Character, String>();
    private int max = -1;

    @CanIgnoreReturnValue
    public CharEscaperBuilder addEscape(char c, String r) {
        this.map.put((Character)Character.valueOf((char)c), (String)Preconditions.checkNotNull(r));
        if (c <= this.max) return this;
        this.max = (int)c;
        return this;
    }

    @CanIgnoreReturnValue
    public CharEscaperBuilder addEscapes(char[] cs, String r) {
        Preconditions.checkNotNull(r);
        char[] arr$ = cs;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            char c = arr$[i$];
            this.addEscape((char)c, (String)r);
            ++i$;
        }
        return this;
    }

    public char[][] toArray() {
        char[][] result = new char[this.max + 1][];
        Iterator<Map.Entry<Character, String>> i$ = this.map.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<Character, String> entry = i$.next();
            result[entry.getKey().charValue()] = entry.getValue().toCharArray();
        }
        return result;
    }

    public Escaper toEscaper() {
        return new CharArrayDecorator((char[][])this.toArray());
    }
}

