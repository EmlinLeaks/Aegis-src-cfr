/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TDoubleLongIterator
extends TAdvancingIterator {
    public double key();

    public long value();

    public long setValue(long var1);
}

