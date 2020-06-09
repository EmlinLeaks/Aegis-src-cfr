/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TLongShortIterator
extends TAdvancingIterator {
    public long key();

    public short value();

    public short setValue(short var1);
}

