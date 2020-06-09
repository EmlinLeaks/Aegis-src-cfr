/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.util;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.HashingStrategy;
import java.util.Map;
import net.md_5.bungee.util.CaseInsensitiveHashingStrategy;

public class CaseInsensitiveMap<V>
extends TCustomHashMap<String, V> {
    public CaseInsensitiveMap() {
        super(CaseInsensitiveHashingStrategy.INSTANCE);
    }

    public CaseInsensitiveMap(Map<? extends String, ? extends V> map) {
        super(CaseInsensitiveHashingStrategy.INSTANCE, map);
    }
}

