/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TPrimitiveIterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class THashPrimitiveIterator
implements TPrimitiveIterator {
    protected final TPrimitiveHash _hash;
    protected int _expectedSize;
    protected int _index;

    public THashPrimitiveIterator(TPrimitiveHash hash) {
        this._hash = hash;
        this._expectedSize = this._hash.size();
        this._index = this._hash.capacity();
    }

    protected final int nextIndex() {
        if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
        }
        byte[] states = this._hash._states;
        int i = this._index;
        while (i-- > 0) {
            if (states[i] == 1) return i;
        }
        return i;
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
}

