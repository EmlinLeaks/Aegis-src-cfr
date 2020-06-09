/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.procedure.TIntProcedure;
import java.util.Arrays;

public abstract class TIntHash
extends TPrimitiveHash {
    static final long serialVersionUID = 1L;
    public transient int[] _set;
    protected int no_entry_value;
    protected boolean consumeFreeSlot;

    public TIntHash() {
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
        if (this.no_entry_value == 0) return;
        Arrays.fill((int[])this._set, (int)this.no_entry_value);
    }

    public TIntHash(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
        if (this.no_entry_value == 0) return;
        Arrays.fill((int[])this._set, (int)this.no_entry_value);
    }

    public TIntHash(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
        if (this.no_entry_value == 0) return;
        Arrays.fill((int[])this._set, (int)this.no_entry_value);
    }

    public TIntHash(int initialCapacity, float loadFactor, int no_entry_value) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = no_entry_value;
        if (no_entry_value == 0) return;
        Arrays.fill((int[])this._set, (int)no_entry_value);
    }

    public int getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._set = new int[capacity];
        return capacity;
    }

    public boolean contains(int val) {
        if (this.index((int)val) < 0) return false;
        return true;
    }

    public boolean forEach(TIntProcedure procedure) {
        byte[] states = this._states;
        int[] set = this._set;
        int i = set.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((int)set[i]));
        return false;
    }

    @Override
    protected void removeAt(int index) {
        this._set[index] = this.no_entry_value;
        super.removeAt((int)index);
    }

    protected int index(int val) {
        byte[] states = this._states;
        int[] set = this._set;
        int length = states.length;
        int hash = HashFunctions.hash((int)val) & Integer.MAX_VALUE;
        int index = hash % length;
        byte state = states[index];
        if (state == 0) {
            return -1;
        }
        if (state != 1) return this.indexRehashed((int)val, (int)index, (int)hash, (byte)state);
        if (set[index] != val) return this.indexRehashed((int)val, (int)index, (int)hash, (byte)state);
        return index;
    }

    int indexRehashed(int key, int index, int hash, byte state) {
        int length = this._set.length;
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;
        do {
            if ((index -= probe) < 0) {
                index += length;
            }
            if ((state = this._states[index]) == 0) {
                return -1;
            }
            if (key != this._set[index] || state == 2) continue;
            return index;
        } while (index != loopIndex);
        return -1;
    }

    protected int insertKey(int val) {
        int hash = HashFunctions.hash((int)val) & Integer.MAX_VALUE;
        int index = hash % this._states.length;
        byte state = this._states[index];
        this.consumeFreeSlot = false;
        if (state == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt((int)index, (int)val);
            return index;
        }
        if (state != 1) return this.insertKeyRehash((int)val, (int)index, (int)hash, (byte)state);
        if (this._set[index] != val) return this.insertKeyRehash((int)val, (int)index, (int)hash, (byte)state);
        return -index - 1;
    }

    int insertKeyRehash(int val, int index, int hash, byte state) {
        int length = this._set.length;
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;
        int firstRemoved = -1;
        do {
            if (state == 2 && firstRemoved == -1) {
                firstRemoved = index;
            }
            if ((index -= probe) < 0) {
                index += length;
            }
            if ((state = this._states[index]) == 0) {
                if (firstRemoved != -1) {
                    this.insertKeyAt((int)firstRemoved, (int)val);
                    return firstRemoved;
                }
                this.consumeFreeSlot = true;
                this.insertKeyAt((int)index, (int)val);
                return index;
            }
            if (state != 1 || this._set[index] != val) continue;
            return -index - 1;
        } while (index != loopIndex);
        if (firstRemoved == -1) throw new IllegalStateException((String)"No free or removed slots available. Key set full?!!");
        this.insertKeyAt((int)firstRemoved, (int)val);
        return firstRemoved;
    }

    void insertKeyAt(int index, int val) {
        this._set[index] = val;
        this._states[index] = 1;
    }
}

