/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TObjectLongIterator<K>
extends TAdvancingIterator {
    public K key();

    public long value();

    public long setValue(long var1);
}

