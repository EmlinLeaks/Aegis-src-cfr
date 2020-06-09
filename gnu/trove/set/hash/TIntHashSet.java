/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.set.hash;

import gnu.trove.TIntCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TIntHashSet
extends TIntHash
implements TIntSet,
Externalizable {
    static final long serialVersionUID = 1L;

    public TIntHashSet() {
    }

    public TIntHashSet(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TIntHashSet(int initialCapacity, float load_factor) {
        super((int)initialCapacity, (float)load_factor);
    }

    public TIntHashSet(int initial_capacity, float load_factor, int no_entry_value) {
        super((int)initial_capacity, (float)load_factor, (int)no_entry_value);
        if (no_entry_value == 0) return;
        Arrays.fill((int[])this._set, (int)no_entry_value);
    }

    public TIntHashSet(Collection<? extends Integer> collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        this.addAll(collection);
    }

    public TIntHashSet(TIntCollection collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        if (collection instanceof TIntHashSet) {
            TIntHashSet hashset = (TIntHashSet)collection;
            this._loadFactor = hashset._loadFactor;
            this.no_entry_value = hashset.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill((int[])this._set, (int)this.no_entry_value);
            }
            this.setUp((int)TIntHashSet.saturatedCast((long)TIntHashSet.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.addAll((TIntCollection)collection);
    }

    public TIntHashSet(int[] array) {
        this((int)Math.max((int)array.length, (int)10));
        this.addAll((int[])array);
    }

    @Override
    public TIntIterator iterator() {
        return new TIntHashIterator((TIntHashSet)this, (TIntHash)this);
    }

    @Override
    public int[] toArray() {
        return this.toArray((int[])new int[this._size]);
    }

    @Override
    public int[] toArray(int[] dest) {
        if (dest.length < this._size) {
            dest = new int[this._size];
        }
        int[] set = this._set;
        byte[] states = this._states;
        int i = states.length;
        int j = 0;
        do {
            if (i-- <= 0) {
                if (dest.length <= this._size) return dest;
                dest[this._size] = this.no_entry_value;
                return dest;
            }
            if (states[i] != 1) continue;
            dest[j++] = set[i];
        } while (true);
    }

    @Override
    public boolean add(int val) {
        int index = this.insertKey((int)val);
        if (index < 0) {
            return false;
        }
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean remove(int val) {
        int index = this.index((int)val);
        if (index < 0) return false;
        this.removeAt((int)index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        ? element;
        int c;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Integer)) return false;
        } while (this.contains((int)(c = ((Integer)element).intValue())));
        return false;
    }

    @Override
    public boolean containsAll(TIntCollection collection) {
        int element;
        TIntIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((int)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(int[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((int)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        boolean changed = false;
        Iterator<? extends Integer> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Integer element = iterator.next();
            int e = element.intValue();
            if (!this.add((int)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TIntCollection collection) {
        boolean changed = false;
        TIntIterator iter = collection.iterator();
        while (iter.hasNext()) {
            int element = iter.next();
            if (!this.add((int)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(int[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.add((int)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TIntIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Integer.valueOf((int)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TIntCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TIntIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((int)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(int[] array) {
        boolean changed = false;
        Arrays.sort((int[])array);
        int[] set = this._set;
        byte[] states = this._states;
        this._autoCompactTemporaryDisable = true;
        int i = set.length;
        do {
            if (i-- <= 0) {
                this._autoCompactTemporaryDisable = false;
                return changed;
            }
            if (states[i] != 1 || Arrays.binarySearch((int[])array, (int)set[i]) >= 0) continue;
            this.removeAt((int)i);
            changed = true;
        } while (true);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            int c;
            ? element = iterator.next();
            if (!(element instanceof Integer) || !this.remove((int)(c = ((Integer)element).intValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TIntCollection collection) {
        boolean changed = false;
        TIntIterator iter = collection.iterator();
        while (iter.hasNext()) {
            int element = iter.next();
            if (!this.remove((int)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(int[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((int)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        super.clear();
        int[] set = this._set;
        byte[] states = this._states;
        int i = set.length;
        while (i-- > 0) {
            set[i] = this.no_entry_value;
            states[i] = 0;
        }
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        int[] oldSet = this._set;
        byte[] oldStates = this._states;
        this._set = new int[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            int o = oldSet[i];
            int n = this.insertKey((int)o);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TIntSet)) {
            return false;
        }
        TIntSet that = (TIntSet)other;
        if (that.size() != this.size()) {
            return false;
        }
        int i = this._states.length;
        do {
            if (i-- <= 0) return true;
        } while (this._states[i] != 1 || that.contains((int)this._set[i]));
        return false;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            hashcode += HashFunctions.hash((int)this._set[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buffy = new StringBuilder((int)(this._size * 2 + 2));
        buffy.append((String)"{");
        int i = this._states.length;
        int j = 1;
        do {
            if (i-- <= 0) {
                buffy.append((String)"}");
                return buffy.toString();
            }
            if (this._states[i] != 1) continue;
            buffy.append((int)this._set[i]);
            if (j++ >= this._size) continue;
            buffy.append((String)",");
        } while (true);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)1);
        super.writeExternal((ObjectOutput)out);
        out.writeInt((int)this._size);
        out.writeFloat((float)this._loadFactor);
        out.writeInt((int)this.no_entry_value);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeInt((int)this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        if (version >= 1) {
            this._loadFactor = in.readFloat();
            this.no_entry_value = in.readInt();
            if (this.no_entry_value != 0) {
                Arrays.fill((int[])this._set, (int)this.no_entry_value);
            }
        }
        this.setUp((int)size);
        while (size-- > 0) {
            int val = in.readInt();
            this.add((int)val);
        }
    }
}

