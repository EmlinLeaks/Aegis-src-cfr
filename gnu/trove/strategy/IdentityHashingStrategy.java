/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.strategy;

import gnu.trove.strategy.HashingStrategy;

public class IdentityHashingStrategy<K>
implements HashingStrategy<K> {
    static final long serialVersionUID = -5188534454583764904L;
    public static final IdentityHashingStrategy<Object> INSTANCE = new IdentityHashingStrategy<K>();

    @Override
    public int computeHashCode(K object) {
        return System.identityHashCode(object);
    }

    @Override
    public boolean equals(K o1, K o2) {
        if (o1 != o2) return false;
        return true;
    }
}

