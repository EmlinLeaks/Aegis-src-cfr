/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TLongCharIterator
extends TAdvancingIterator {
    public long key();

    public char value();

    public char setValue(char var1);
}

