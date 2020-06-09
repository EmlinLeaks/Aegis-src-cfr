/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TShortObjectIterator<V>
extends TAdvancingIterator {
    public short key();

    public V value();

    public V setValue(V var1);
}

