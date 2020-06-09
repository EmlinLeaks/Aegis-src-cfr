/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.set.hash;

import gnu.trove.TShortCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TShortHash;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.set.TShortSet;
import gnu.trove.set.hash.TShortHashSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TShortHashSet
extends TShortHash
implements TShortSet,
Externalizable {
    static final long serialVersionUID = 1L;

    public TShortHashSet() {
    }

    public TShortHashSet(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TShortHashSet(int initialCapacity, float load_factor) {
        super((int)initialCapacity, (float)load_factor);
    }

    public TShortHashSet(int initial_capacity, float load_factor, short no_entry_value) {
        super((int)initial_capacity, (float)load_factor, (short)no_entry_value);
        if (no_entry_value == 0) return;
        Arrays.fill((short[])this._set, (short)no_entry_value);
    }

    public TShortHashSet(Collection<? extends Short> collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        this.addAll(collection);
    }

    public TShortHashSet(TShortCollection collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        if (collection instanceof TShortHashSet) {
            TShortHashSet hashset = (TShortHashSet)collection;
            this._loadFactor = hashset._loadFactor;
            this.no_entry_value = hashset.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill((short[])this._set, (short)this.no_entry_value);
            }
            this.setUp((int)TShortHashSet.saturatedCast((long)TShortHashSet.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.addAll((TShortCollection)collection);
    }

    public TShortHashSet(short[] array) {
        this((int)Math.max((int)array.length, (int)10));
        this.addAll((short[])array);
    }

    @Override
    public TShortIterator iterator() {
        return new TShortHashIterator((TShortHashSet)this, (TShortHash)this);
    }

    @Override
    public short[] toArray() {
        return this.toArray((short[])new short[this._size]);
    }

    @Override
    public short[] toArray(short[] dest) {
        if (dest.length < this._size) {
            dest = new short[this._size];
        }
        short[] set = this._set;
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
    public boolean add(short val) {
        int index = this.insertKey((short)val);
        if (index < 0) {
            return false;
        }
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean remove(short val) {
        int index = this.index((short)val);
        if (index < 0) return false;
        this.removeAt((int)index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        ? element;
        short c;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Short)) return false;
        } while (this.contains((short)(c = ((Short)element).shortValue())));
        return false;
    }

    @Override
    public boolean containsAll(TShortCollection collection) {
        short element;
        TShortIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((short)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(short[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((short)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Short> collection) {
        boolean changed = false;
        Iterator<? extends Short> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Short element = iterator.next();
            short e = element.shortValue();
            if (!this.add((short)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TShortCollection collection) {
        boolean changed = false;
        TShortIterator iter = collection.iterator();
        while (iter.hasNext()) {
            short element = iter.next();
            if (!this.add((short)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(short[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.add((short)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TShortIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Short.valueOf((short)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TShortCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TShortIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((short)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(short[] array) {
        boolean changed = false;
        Arrays.sort((short[])array);
        short[] set = this._set;
        byte[] states = this._states;
        this._autoCompactTemporaryDisable = true;
        int i = set.length;
        do {
            if (i-- <= 0) {
                this._autoCompactTemporaryDisable = false;
                return changed;
            }
            if (states[i] != 1 || Arrays.binarySearch((short[])array, (short)set[i]) >= 0) continue;
            this.removeAt((int)i);
            changed = true;
        } while (true);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            short c;
            ? element = iterator.next();
            if (!(element instanceof Short) || !this.remove((short)(c = ((Short)element).shortValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TShortCollection collection) {
        boolean changed = false;
        TShortIterator iter = collection.iterator();
        while (iter.hasNext()) {
            short element = iter.next();
            if (!this.remove((short)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(short[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((short)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        super.clear();
        short[] set = this._set;
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
        short[] oldSet = this._set;
        byte[] oldStates = this._states;
        this._set = new short[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            short o = oldSet[i];
            int n = this.insertKey((short)o);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TShortSet)) {
            return false;
        }
        TShortSet that = (TShortSet)other;
        if (that.size() != this.size()) {
            return false;
        }
        int i = this._states.length;
        do {
            if (i-- <= 0) return true;
        } while (this._states[i] != 1 || that.contains((short)this._set[i]));
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
        out.writeShort((int)this.no_entry_value);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeShort((int)this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        if (version >= 1) {
            this._loadFactor = in.readFloat();
            this.no_entry_value = in.readShort();
            if (this.no_entry_value != 0) {
                Arrays.fill((short[])this._set, (short)this.no_entry_value);
            }
        }
        this.setUp((int)size);
        while (size-- > 0) {
            short val = in.readShort();
            this.add((short)val);
        }
    }
}

