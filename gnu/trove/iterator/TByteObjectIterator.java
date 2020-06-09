/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TByteObjectIterator<V>
extends TAdvancingIterator {
    public byte key();

    public V value();

    public V setValue(V var1);
}

