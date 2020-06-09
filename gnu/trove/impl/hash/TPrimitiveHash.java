/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.hash.THash;

public abstract class TPrimitiveHash
extends THash {
    static final long serialVersionUID = 1L;
    public transient byte[] _states;
    public static final byte FREE = 0;
    public static final byte FULL = 1;
    public static final byte REMOVED = 2;

    public TPrimitiveHash() {
    }

    public TPrimitiveHash(int initialCapacity) {
        super((int)initialCapacity, (float)0.5f);
    }

    public TPrimitiveHash(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    @Override
    public int capacity() {
        return this._states.length;
    }

    @Override
    protected void removeAt(int index) {
        this._states[index] = 2;
        super.removeAt((int)index);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._states = new byte[capacity];
        return capacity;
    }
}

