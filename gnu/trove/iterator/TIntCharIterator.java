/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TIntCharIterator
extends TAdvancingIterator {
    public int key();

    public char value();

    public char setValue(char var1);
}

