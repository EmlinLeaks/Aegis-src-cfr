/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.util;

import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.strategy.HashingStrategy;
import java.util.Collection;
import net.md_5.bungee.util.CaseInsensitiveHashingStrategy;

public class CaseInsensitiveSet
extends TCustomHashSet<String> {
    public CaseInsensitiveSet() {
        super(CaseInsensitiveHashingStrategy.INSTANCE);
    }

    public CaseInsensitiveSet(Collection<? extends String> collection) {
        super(CaseInsensitiveHashingStrategy.INSTANCE, collection);
    }
}

