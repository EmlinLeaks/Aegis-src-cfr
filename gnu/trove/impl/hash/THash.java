/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.PrimeFinder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class THash
implements Externalizable {
    static final long serialVersionUID = -1792948471915530295L;
    protected static final float DEFAULT_LOAD_FACTOR = 0.5f;
    protected static final int DEFAULT_CAPACITY = 10;
    protected transient int _size;
    protected transient int _free;
    protected float _loadFactor;
    protected int _maxSize;
    protected int _autoCompactRemovesRemaining;
    protected float _autoCompactionFactor;
    protected transient boolean _autoCompactTemporaryDisable = false;

    public THash() {
        this((int)10, (float)0.5f);
    }

    public THash(int initialCapacity) {
        this((int)initialCapacity, (float)0.5f);
    }

    public THash(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException((String)("negative capacity: " + initialCapacity));
        }
        if (0.0f >= loadFactor) {
            throw new IllegalArgumentException((String)("load factor out of range: " + loadFactor));
        }
        this._loadFactor = loadFactor;
        this._autoCompactionFactor = loadFactor;
        this.setUp((int)THash.saturatedCast((long)THash.fastCeil((double)((double)initialCapacity / (double)loadFactor))));
    }

    protected static long fastCeil(double v) {
        long possible_result = (long)v;
        if (!(v - (double)possible_result > 0.0)) return possible_result;
        ++possible_result;
        return possible_result;
    }

    protected static int saturatedCast(long v) {
        int r = (int)(v & Integer.MAX_VALUE);
        if ((long)r == v) return r;
        return Integer.MAX_VALUE;
    }

    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    public int size() {
        return this._size;
    }

    public abstract int capacity();

    public void ensureCapacity(int desiredCapacity) {
        if (desiredCapacity <= this._maxSize - this.size()) return;
        this.rehash((int)PrimeFinder.nextPrime((int)Math.max((int)(this._size + 1), (int)THash.saturatedCast((long)(THash.fastCeil((double)((double)(desiredCapacity + this._size) / (double)this._loadFactor)) + 1L)))));
        if (this.capacity() >= PrimeFinder.largestPrime) {
            this._loadFactor = 1.0f;
        }
        this.computeMaxSize((int)this.capacity());
    }

    public void compact() {
        this.rehash((int)PrimeFinder.nextPrime((int)Math.max((int)(this._size + 1), (int)THash.saturatedCast((long)(THash.fastCeil((double)((double)this._size / (double)this._loadFactor)) + 1L)))));
        this.computeMaxSize((int)this.capacity());
        if (this._autoCompactionFactor == 0.0f) return;
        this.computeNextAutoCompactionAmount((int)this.size());
    }

    public void setAutoCompactionFactor(float factor) {
        if (factor < 0.0f) {
            throw new IllegalArgumentException((String)("Factor must be >= 0: " + factor));
        }
        this._autoCompactionFactor = factor;
    }

    public float getAutoCompactionFactor() {
        return this._autoCompactionFactor;
    }

    public final void trimToSize() {
        this.compact();
    }

    protected void removeAt(int index) {
        --this._size;
        if (this._autoCompactionFactor == 0.0f) return;
        --this._autoCompactRemovesRemaining;
        if (this._autoCompactTemporaryDisable) return;
        if (this._autoCompactRemovesRemaining > 0) return;
        this.compact();
    }

    public void clear() {
        this._size = 0;
        this._free = this.capacity();
    }

    protected int setUp(int initialCapacity) {
        int capacity = PrimeFinder.nextPrime((int)initialCapacity);
        if (capacity >= PrimeFinder.largestPrime) {
            this._loadFactor = 1.0f;
        }
        this.computeMaxSize((int)capacity);
        this.computeNextAutoCompactionAmount((int)initialCapacity);
        return capacity;
    }

    protected abstract void rehash(int var1);

    public void tempDisableAutoCompaction() {
        this._autoCompactTemporaryDisable = true;
    }

    public void reenableAutoCompaction(boolean check_for_compaction) {
        this._autoCompactTemporaryDisable = false;
        if (!check_for_compaction) return;
        if (this._autoCompactRemovesRemaining > 0) return;
        if (this._autoCompactionFactor == 0.0f) return;
        this.compact();
    }

    protected void computeMaxSize(int capacity) {
        this._maxSize = Math.min((int)(capacity - 1), (int)((int)((float)capacity * this._loadFactor)));
        this._free = capacity - this._size;
    }

    protected void computeNextAutoCompactionAmount(int size) {
        if (this._autoCompactionFactor == 0.0f) return;
        this._autoCompactRemovesRemaining = (int)((float)size * this._autoCompactionFactor + 0.5f);
    }

    protected final void postInsertHook(boolean usedFreeSlot) {
        if (usedFreeSlot) {
            --this._free;
        }
        if (++this._size <= this._maxSize) {
            if (this._free != 0) return;
        }
        int newCapacity = this._size > this._maxSize ? PrimeFinder.nextPrime((int)(this.capacity() << 1)) : this.capacity();
        this.rehash((int)newCapacity);
        this.computeMaxSize((int)this.capacity());
    }

    protected int calculateGrownCapacity() {
        return this.capacity() << 1;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeFloat((float)this._loadFactor);
        out.writeFloat((float)this._autoCompactionFactor);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        float old_factor = this._loadFactor;
        this._loadFactor = Math.abs((float)in.readFloat());
        this._autoCompactionFactor = in.readFloat();
        if (old_factor == this._loadFactor) return;
        this.setUp((int)THash.saturatedCast((long)((long)Math.ceil((double)(10.0 / (double)this._loadFactor)))));
    }
}

