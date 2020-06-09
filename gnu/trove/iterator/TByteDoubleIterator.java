/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TByteDoubleIterator
extends TAdvancingIterator {
    public byte key();

    public double value();

    public double setValue(double var1);
}

