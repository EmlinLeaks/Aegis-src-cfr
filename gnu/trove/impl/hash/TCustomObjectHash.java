/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.strategy.HashingStrategy;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TCustomObjectHash<T>
extends TObjectHash<T> {
    static final long serialVersionUID = 8766048185963756400L;
    protected HashingStrategy<? super T> strategy;

    public TCustomObjectHash() {
    }

    public TCustomObjectHash(HashingStrategy<? super T> strategy) {
        this.strategy = strategy;
    }

    public TCustomObjectHash(HashingStrategy<? super T> strategy, int initialCapacity) {
        super((int)initialCapacity);
        this.strategy = strategy;
    }

    public TCustomObjectHash(HashingStrategy<? super T> strategy, int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.strategy = strategy;
    }

    @Override
    protected int hash(Object obj) {
        return this.strategy.computeHashCode(obj);
    }

    @Override
    protected boolean equals(Object one, Object two) {
        if (two == REMOVED) return false;
        if (!this.strategy.equals(one, two)) return false;
        return true;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeObject(this.strategy);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.strategy = (HashingStrategy)in.readObject();
    }
}

