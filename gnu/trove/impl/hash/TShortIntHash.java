/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.procedure.TShortProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TShortIntHash
extends TPrimitiveHash {
    static final long serialVersionUID = 1L;
    public transient short[] _set;
    protected short no_entry_key;
    protected int no_entry_value;
    protected boolean consumeFreeSlot;

    public TShortIntHash() {
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TShortIntHash(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TShortIntHash(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TShortIntHash(int initialCapacity, float loadFactor, short no_entry_key, int no_entry_value) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_key = no_entry_key;
        this.no_entry_value = no_entry_value;
    }

    public short getNoEntryKey() {
        return this.no_entry_key;
    }

    public int getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._set = new short[capacity];
        return capacity;
    }

    public boolean contains(short val) {
        if (this.index((short)val) < 0) return false;
        return true;
    }

    public boolean forEach(TShortProcedure procedure) {
        byte[] states = this._states;
        short[] set = this._set;
        int i = set.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((short)set[i]));
        return false;
    }

    @Override
    protected void removeAt(int index) {
        this._set[index] = this.no_entry_key;
        super.removeAt((int)index);
    }

    protected int index(short key) {
        byte[] states = this._states;
        short[] set = this._set;
        int length = states.length;
        int hash = HashFunctions.hash((int)key) & Integer.MAX_VALUE;
        int index = hash % length;
        byte state = states[index];
        if (state == 0) {
            return -1;
        }
        if (state != 1) return this.indexRehashed((short)key, (int)index, (int)hash, (byte)state);
        if (set[index] != key) return this.indexRehashed((short)key, (int)index, (int)hash, (byte)state);
        return index;
    }

    int indexRehashed(short key, int index, int hash, byte state) {
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

    protected int insertKey(short val) {
        int hash = HashFunctions.hash((int)val) & Integer.MAX_VALUE;
        int index = hash % this._states.length;
        byte state = this._states[index];
        this.consumeFreeSlot = false;
        if (state == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt((int)index, (short)val);
            return index;
        }
        if (state != 1) return this.insertKeyRehash((short)val, (int)index, (int)hash, (byte)state);
        if (this._set[index] != val) return this.insertKeyRehash((short)val, (int)index, (int)hash, (byte)state);
        return -index - 1;
    }

    int insertKeyRehash(short val, int index, int hash, byte state) {
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
                    this.insertKeyAt((int)firstRemoved, (short)val);
                    return firstRemoved;
                }
                this.consumeFreeSlot = true;
                this.insertKeyAt((int)index, (short)val);
                return index;
            }
            if (state != 1 || this._set[index] != val) continue;
            return -index - 1;
        } while (index != loopIndex);
        if (firstRemoved == -1) throw new IllegalStateException((String)"No free or removed slots available. Key set full?!!");
        this.insertKeyAt((int)firstRemoved, (short)val);
        return firstRemoved;
    }

    void insertKeyAt(int index, short val) {
        this._set[index] = val;
        this._states[index] = 1;
    }

    protected int XinsertKey(short key) {
        byte[] states = this._states;
        short[] set = this._set;
        int length = states.length;
        int hash = HashFunctions.hash((int)key) & Integer.MAX_VALUE;
        int index = hash % length;
        byte state = states[index];
        this.consumeFreeSlot = false;
        if (state == 0) {
            this.consumeFreeSlot = true;
            set[index] = key;
            states[index] = 1;
            return index;
        }
        if (state == 1 && set[index] == key) {
            return -index - 1;
        }
        int probe = 1 + hash % (length - 2);
        if (state != 2) {
            do {
                if ((index -= probe) >= 0) continue;
                index += length;
            } while ((state = states[index]) == 1 && set[index] != key);
        }
        if (state == 2) {
            int firstRemoved = index;
            while (state != 0 && (state == 2 || set[index] != key)) {
                if ((index -= probe) < 0) {
                    index += length;
                }
                state = states[index];
            }
            if (state == 1) {
                return -index - 1;
            }
            set[index] = key;
            states[index] = 1;
            return firstRemoved;
        }
        if (state == 1) {
            return -index - 1;
        }
        this.consumeFreeSlot = true;
        set[index] = key;
        states[index] = 1;
        return index;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeShort((int)this.no_entry_key);
        out.writeInt((int)this.no_entry_value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.no_entry_key = in.readShort();
        this.no_entry_value = in.readInt();
    }
}

