/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.set.hash;

import gnu.trove.TLongCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TLongHashSet
extends TLongHash
implements TLongSet,
Externalizable {
    static final long serialVersionUID = 1L;

    public TLongHashSet() {
    }

    public TLongHashSet(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TLongHashSet(int initialCapacity, float load_factor) {
        super((int)initialCapacity, (float)load_factor);
    }

    public TLongHashSet(int initial_capacity, float load_factor, long no_entry_value) {
        super((int)initial_capacity, (float)load_factor, (long)no_entry_value);
        if (no_entry_value == 0L) return;
        Arrays.fill((long[])this._set, (long)no_entry_value);
    }

    public TLongHashSet(Collection<? extends Long> collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        this.addAll(collection);
    }

    public TLongHashSet(TLongCollection collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        if (collection instanceof TLongHashSet) {
            TLongHashSet hashset = (TLongHashSet)collection;
            this._loadFactor = hashset._loadFactor;
            this.no_entry_value = hashset.no_entry_value;
            if (this.no_entry_value != 0L) {
                Arrays.fill((long[])this._set, (long)this.no_entry_value);
            }
            this.setUp((int)TLongHashSet.saturatedCast((long)TLongHashSet.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.addAll((TLongCollection)collection);
    }

    public TLongHashSet(long[] array) {
        this((int)Math.max((int)array.length, (int)10));
        this.addAll((long[])array);
    }

    @Override
    public TLongIterator iterator() {
        return new TLongHashIterator((TLongHashSet)this, (TLongHash)this);
    }

    @Override
    public long[] toArray() {
        return this.toArray((long[])new long[this._size]);
    }

    @Override
    public long[] toArray(long[] dest) {
        if (dest.length < this._size) {
            dest = new long[this._size];
        }
        long[] set = this._set;
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
    public boolean add(long val) {
        int index = this.insertKey((long)val);
        if (index < 0) {
            return false;
        }
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean remove(long val) {
        int index = this.index((long)val);
        if (index < 0) return false;
        this.removeAt((int)index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        long c;
        ? element;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Long)) return false;
        } while (this.contains((long)(c = ((Long)element).longValue())));
        return false;
    }

    @Override
    public boolean containsAll(TLongCollection collection) {
        long element;
        TLongIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((long)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(long[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((long)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        boolean changed = false;
        Iterator<? extends Long> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Long element = iterator.next();
            long e = element.longValue();
            if (!this.add((long)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TLongCollection collection) {
        boolean changed = false;
        TLongIterator iter = collection.iterator();
        while (iter.hasNext()) {
            long element = iter.next();
            if (!this.add((long)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(long[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.add((long)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TLongIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Long.valueOf((long)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TLongCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TLongIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((long)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(long[] array) {
        boolean changed = false;
        Arrays.sort((long[])array);
        long[] set = this._set;
        byte[] states = this._states;
        this._autoCompactTemporaryDisable = true;
        int i = set.length;
        do {
            if (i-- <= 0) {
                this._autoCompactTemporaryDisable = false;
                return changed;
            }
            if (states[i] != 1 || Arrays.binarySearch((long[])array, (long)set[i]) >= 0) continue;
            this.removeAt((int)i);
            changed = true;
        } while (true);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            long c;
            ? element = iterator.next();
            if (!(element instanceof Long) || !this.remove((long)(c = ((Long)element).longValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TLongCollection collection) {
        boolean changed = false;
        TLongIterator iter = collection.iterator();
        while (iter.hasNext()) {
            long element = iter.next();
            if (!this.remove((long)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(long[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((long)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        super.clear();
        long[] set = this._set;
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
        long[] oldSet = this._set;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            long o = oldSet[i];
            int n = this.insertKey((long)o);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TLongSet)) {
            return false;
        }
        TLongSet that = (TLongSet)other;
        if (that.size() != this.size()) {
            return false;
        }
        int i = this._states.length;
        do {
            if (i-- <= 0) return true;
        } while (this._states[i] != 1 || that.contains((long)this._set[i]));
        return false;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            hashcode += HashFunctions.hash((long)this._set[i]);
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
            buffy.append((long)this._set[i]);
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
        out.writeLong((long)this.no_entry_value);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeLong((long)this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        if (version >= 1) {
            this._loadFactor = in.readFloat();
            this.no_entry_value = in.readLong();
            if (this.no_entry_value != 0L) {
                Arrays.fill((long[])this._set, (long)this.no_entry_value);
            }
        }
        this.setUp((int)size);
        while (size-- > 0) {
            long val = in.readLong();
            this.add((long)val);
        }
    }
}

