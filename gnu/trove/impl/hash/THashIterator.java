/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TIterator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class THashIterator<V>
implements TIterator,
Iterator<V> {
    private final TObjectHash<V> _object_hash;
    protected final THash _hash;
    protected int _expectedSize;
    protected int _index;

    protected THashIterator(TObjectHash<V> hash) {
        this._hash = hash;
        this._expectedSize = this._hash.size();
        this._index = this._hash.capacity();
        this._object_hash = hash;
    }

    @Override
    public V next() {
        this.moveToNextIndex();
        return (V)this.objectAtIndex((int)this._index);
    }

    @Override
    public boolean hasNext() {
        if (this.nextIndex() < 0) return false;
        return true;
    }

    @Override
    public void remove() {
        if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
        }
        try {
            this._hash.tempDisableAutoCompaction();
            this._hash.removeAt((int)this._index);
        }
        finally {
            this._hash.reenableAutoCompaction((boolean)false);
        }
        --this._expectedSize;
    }

    protected final void moveToNextIndex() {
        this._index = this.nextIndex();
        if (this._index >= 0) return;
        throw new NoSuchElementException();
    }

    protected final int nextIndex() {
        if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
        }
        Object[] set = this._object_hash._set;
        int i = this._index;
        while (i-- > 0) {
            if (set[i] == TObjectHash.FREE) continue;
            if (set[i] != TObjectHash.REMOVED) return i;
        }
        return i;
    }

    protected abstract V objectAtIndex(int var1);
}

