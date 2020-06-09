/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TObjectShortIterator<K>
extends TAdvancingIterator {
    public K key();

    public short value();

    public short setValue(short var1);
}

