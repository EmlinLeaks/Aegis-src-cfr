/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.set.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.hash.TDoubleHashSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TDoubleHashSet
extends TDoubleHash
implements TDoubleSet,
Externalizable {
    static final long serialVersionUID = 1L;

    public TDoubleHashSet() {
    }

    public TDoubleHashSet(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TDoubleHashSet(int initialCapacity, float load_factor) {
        super((int)initialCapacity, (float)load_factor);
    }

    public TDoubleHashSet(int initial_capacity, float load_factor, double no_entry_value) {
        super((int)initial_capacity, (float)load_factor, (double)no_entry_value);
        if (no_entry_value == 0.0) return;
        Arrays.fill((double[])this._set, (double)no_entry_value);
    }

    public TDoubleHashSet(Collection<? extends Double> collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        this.addAll(collection);
    }

    public TDoubleHashSet(TDoubleCollection collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        if (collection instanceof TDoubleHashSet) {
            TDoubleHashSet hashset = (TDoubleHashSet)collection;
            this._loadFactor = hashset._loadFactor;
            this.no_entry_value = hashset.no_entry_value;
            if (this.no_entry_value != 0.0) {
                Arrays.fill((double[])this._set, (double)this.no_entry_value);
            }
            this.setUp((int)TDoubleHashSet.saturatedCast((long)TDoubleHashSet.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.addAll((TDoubleCollection)collection);
    }

    public TDoubleHashSet(double[] array) {
        this((int)Math.max((int)array.length, (int)10));
        this.addAll((double[])array);
    }

    @Override
    public TDoubleIterator iterator() {
        return new TDoubleHashIterator((TDoubleHashSet)this, (TDoubleHash)this);
    }

    @Override
    public double[] toArray() {
        return this.toArray((double[])new double[this._size]);
    }

    @Override
    public double[] toArray(double[] dest) {
        if (dest.length < this._size) {
            dest = new double[this._size];
        }
        double[] set = this._set;
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
    public boolean add(double val) {
        int index = this.insertKey((double)val);
        if (index < 0) {
            return false;
        }
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean remove(double val) {
        int index = this.index((double)val);
        if (index < 0) return false;
        this.removeAt((int)index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        ? element;
        double c;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Double)) return false;
        } while (this.contains((double)(c = ((Double)element).doubleValue())));
        return false;
    }

    @Override
    public boolean containsAll(TDoubleCollection collection) {
        double element;
        TDoubleIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((double)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(double[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((double)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Double> collection) {
        boolean changed = false;
        Iterator<? extends Double> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Double element = iterator.next();
            double e = element.doubleValue();
            if (!this.add((double)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TDoubleCollection collection) {
        boolean changed = false;
        TDoubleIterator iter = collection.iterator();
        while (iter.hasNext()) {
            double element = iter.next();
            if (!this.add((double)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(double[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.add((double)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TDoubleIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Double.valueOf((double)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TDoubleCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TDoubleIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((double)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(double[] array) {
        boolean changed = false;
        Arrays.sort((double[])array);
        double[] set = this._set;
        byte[] states = this._states;
        this._autoCompactTemporaryDisable = true;
        int i = set.length;
        do {
            if (i-- <= 0) {
                this._autoCompactTemporaryDisable = false;
                return changed;
            }
            if (states[i] != 1 || Arrays.binarySearch((double[])array, (double)set[i]) >= 0) continue;
            this.removeAt((int)i);
            changed = true;
        } while (true);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            double c;
            ? element = iterator.next();
            if (!(element instanceof Double) || !this.remove((double)(c = ((Double)element).doubleValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TDoubleCollection collection) {
        boolean changed = false;
        TDoubleIterator iter = collection.iterator();
        while (iter.hasNext()) {
            double element = iter.next();
            if (!this.remove((double)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(double[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((double)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        super.clear();
        double[] set = this._set;
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
        double[] oldSet = this._set;
        byte[] oldStates = this._states;
        this._set = new double[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            double o = oldSet[i];
            int n = this.insertKey((double)o);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TDoubleSet)) {
            return false;
        }
        TDoubleSet that = (TDoubleSet)other;
        if (that.size() != this.size()) {
            return false;
        }
        int i = this._states.length;
        do {
            if (i-- <= 0) return true;
        } while (this._states[i] != 1 || that.contains((double)this._set[i]));
        return false;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            hashcode += HashFunctions.hash((double)this._set[i]);
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
            buffy.append((double)this._set[i]);
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
        out.writeDouble((double)this.no_entry_value);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeDouble((double)this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        if (version >= 1) {
            this._loadFactor = in.readFloat();
            this.no_entry_value = in.readDouble();
            if (this.no_entry_value != 0.0) {
                Arrays.fill((double[])this._set, (double)this.no_entry_value);
            }
        }
        this.setUp((int)size);
        while (size-- > 0) {
            double val = in.readDouble();
            this.add((double)val);
        }
    }
}

