/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TByteIntIterator
extends TAdvancingIterator {
    public byte key();

    public int value();

    public int setValue(int var1);
}

