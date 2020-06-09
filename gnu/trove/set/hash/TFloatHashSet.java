/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.set.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.set.TFloatSet;
import gnu.trove.set.hash.TFloatHashSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TFloatHashSet
extends TFloatHash
implements TFloatSet,
Externalizable {
    static final long serialVersionUID = 1L;

    public TFloatHashSet() {
    }

    public TFloatHashSet(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TFloatHashSet(int initialCapacity, float load_factor) {
        super((int)initialCapacity, (float)load_factor);
    }

    public TFloatHashSet(int initial_capacity, float load_factor, float no_entry_value) {
        super((int)initial_capacity, (float)load_factor, (float)no_entry_value);
        if (no_entry_value == 0.0f) return;
        Arrays.fill((float[])this._set, (float)no_entry_value);
    }

    public TFloatHashSet(Collection<? extends Float> collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        this.addAll(collection);
    }

    public TFloatHashSet(TFloatCollection collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        if (collection instanceof TFloatHashSet) {
            TFloatHashSet hashset = (TFloatHashSet)collection;
            this._loadFactor = hashset._loadFactor;
            this.no_entry_value = hashset.no_entry_value;
            if (this.no_entry_value != 0.0f) {
                Arrays.fill((float[])this._set, (float)this.no_entry_value);
            }
            this.setUp((int)TFloatHashSet.saturatedCast((long)TFloatHashSet.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.addAll((TFloatCollection)collection);
    }

    public TFloatHashSet(float[] array) {
        this((int)Math.max((int)array.length, (int)10));
        this.addAll((float[])array);
    }

    @Override
    public TFloatIterator iterator() {
        return new TFloatHashIterator((TFloatHashSet)this, (TFloatHash)this);
    }

    @Override
    public float[] toArray() {
        return this.toArray((float[])new float[this._size]);
    }

    @Override
    public float[] toArray(float[] dest) {
        if (dest.length < this._size) {
            dest = new float[this._size];
        }
        float[] set = this._set;
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
    public boolean add(float val) {
        int index = this.insertKey((float)val);
        if (index < 0) {
            return false;
        }
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean remove(float val) {
        int index = this.index((float)val);
        if (index < 0) return false;
        this.removeAt((int)index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        ? element;
        float c;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Float)) return false;
        } while (this.contains((float)(c = ((Float)element).floatValue())));
        return false;
    }

    @Override
    public boolean containsAll(TFloatCollection collection) {
        float element;
        TFloatIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((float)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(float[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((float)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Float> collection) {
        boolean changed = false;
        Iterator<? extends Float> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Float element = iterator.next();
            float e = element.floatValue();
            if (!this.add((float)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TFloatCollection collection) {
        boolean changed = false;
        TFloatIterator iter = collection.iterator();
        while (iter.hasNext()) {
            float element = iter.next();
            if (!this.add((float)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(float[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.add((float)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TFloatIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Float.valueOf((float)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TFloatCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TFloatIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((float)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(float[] array) {
        boolean changed = false;
        Arrays.sort((float[])array);
        float[] set = this._set;
        byte[] states = this._states;
        this._autoCompactTemporaryDisable = true;
        int i = set.length;
        do {
            if (i-- <= 0) {
                this._autoCompactTemporaryDisable = false;
                return changed;
            }
            if (states[i] != 1 || Arrays.binarySearch((float[])array, (float)set[i]) >= 0) continue;
            this.removeAt((int)i);
            changed = true;
        } while (true);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            float c;
            ? element = iterator.next();
            if (!(element instanceof Float) || !this.remove((float)(c = ((Float)element).floatValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TFloatCollection collection) {
        boolean changed = false;
        TFloatIterator iter = collection.iterator();
        while (iter.hasNext()) {
            float element = iter.next();
            if (!this.remove((float)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(float[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((float)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        super.clear();
        float[] set = this._set;
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
        float[] oldSet = this._set;
        byte[] oldStates = this._states;
        this._set = new float[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            float o = oldSet[i];
            int n = this.insertKey((float)o);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TFloatSet)) {
            return false;
        }
        TFloatSet that = (TFloatSet)other;
        if (that.size() != this.size()) {
            return false;
        }
        int i = this._states.length;
        do {
            if (i-- <= 0) return true;
        } while (this._states[i] != 1 || that.contains((float)this._set[i]));
        return false;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            hashcode += HashFunctions.hash((float)this._set[i]);
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
            buffy.append((float)this._set[i]);
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
        out.writeFloat((float)this.no_entry_value);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeFloat((float)this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        if (version >= 1) {
            this._loadFactor = in.readFloat();
            this.no_entry_value = in.readFloat();
            if (this.no_entry_value != 0.0f) {
                Arrays.fill((float[])this._set, (float)this.no_entry_value);
            }
        }
        this.setUp((int)size);
        while (size-- > 0) {
            float val = in.readFloat();
            this.add((float)val);
        }
    }
}

