/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.util;

import gnu.trove.strategy.HashingStrategy;
import java.util.Locale;

class CaseInsensitiveHashingStrategy
implements HashingStrategy {
    static final CaseInsensitiveHashingStrategy INSTANCE = new CaseInsensitiveHashingStrategy();

    CaseInsensitiveHashingStrategy() {
    }

    public int computeHashCode(Object object) {
        return ((String)object).toLowerCase((Locale)Locale.ROOT).hashCode();
    }

    public boolean equals(Object o1, Object o2) {
        if (o1.equals((Object)o2)) return true;
        if (!(o1 instanceof String)) return false;
        if (!(o2 instanceof String)) return false;
        if (!((String)o1).toLowerCase((Locale)Locale.ROOT).equals((Object)((String)o2).toLowerCase((Locale)Locale.ROOT))) return false;
        return true;
    }
}

