/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TIntDoubleIterator
extends TAdvancingIterator {
    public int key();

    public double value();

    public double setValue(double var1);
}

