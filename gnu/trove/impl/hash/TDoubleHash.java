/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Arrays;

public abstract class TDoubleHash
extends TPrimitiveHash {
    static final long serialVersionUID = 1L;
    public transient double[] _set;
    protected double no_entry_value;
    protected boolean consumeFreeSlot;

    public TDoubleHash() {
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
        if (this.no_entry_value == 0.0) return;
        Arrays.fill((double[])this._set, (double)this.no_entry_value);
    }

    public TDoubleHash(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
        if (this.no_entry_value == 0.0) return;
        Arrays.fill((double[])this._set, (double)this.no_entry_value);
    }

    public TDoubleHash(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
        if (this.no_entry_value == 0.0) return;
        Arrays.fill((double[])this._set, (double)this.no_entry_value);
    }

    public TDoubleHash(int initialCapacity, float loadFactor, double no_entry_value) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = no_entry_value;
        if (no_entry_value == 0.0) return;
        Arrays.fill((double[])this._set, (double)no_entry_value);
    }

    public double getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._set = new double[capacity];
        return capacity;
    }

    public boolean contains(double val) {
        if (this.index((double)val) < 0) return false;
        return true;
    }

    public boolean forEach(TDoubleProcedure procedure) {
        byte[] states = this._states;
        double[] set = this._set;
        int i = set.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((double)set[i]));
        return false;
    }

    @Override
    protected void removeAt(int index) {
        this._set[index] = this.no_entry_value;
        super.removeAt((int)index);
    }

    protected int index(double val) {
        byte[] states = this._states;
        double[] set = this._set;
        int length = states.length;
        int hash = HashFunctions.hash((double)val) & Integer.MAX_VALUE;
        int index = hash % length;
        byte state = states[index];
        if (state == 0) {
            return -1;
        }
        if (state != 1) return this.indexRehashed((double)val, (int)index, (int)hash, (byte)state);
        if (set[index] != val) return this.indexRehashed((double)val, (int)index, (int)hash, (byte)state);
        return index;
    }

    int indexRehashed(double key, int index, int hash, byte state) {
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

    protected int insertKey(double val) {
        int hash = HashFunctions.hash((double)val) & Integer.MAX_VALUE;
        int index = hash % this._states.length;
        byte state = this._states[index];
        this.consumeFreeSlot = false;
        if (state == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt((int)index, (double)val);
            return index;
        }
        if (state != 1) return this.insertKeyRehash((double)val, (int)index, (int)hash, (byte)state);
        if (this._set[index] != val) return this.insertKeyRehash((double)val, (int)index, (int)hash, (byte)state);
        return -index - 1;
    }

    int insertKeyRehash(double val, int index, int hash, byte state) {
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
                    this.insertKeyAt((int)firstRemoved, (double)val);
                    return firstRemoved;
                }
                this.consumeFreeSlot = true;
                this.insertKeyAt((int)index, (double)val);
                return index;
            }
            if (state != 1 || this._set[index] != val) continue;
            return -index - 1;
        } while (index != loopIndex);
        if (firstRemoved == -1) throw new IllegalStateException((String)"No free or removed slots available. Key set full?!!");
        this.insertKeyAt((int)firstRemoved, (double)val);
        return firstRemoved;
    }

    void insertKeyAt(int index, double val) {
        this._set[index] = val;
        this._states[index] = 1;
    }
}

