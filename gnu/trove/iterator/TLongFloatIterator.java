/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TLongFloatIterator
extends TAdvancingIterator {
    public long key();

    public float value();

    public float setValue(float var1);
}

