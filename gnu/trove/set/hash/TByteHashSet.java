/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.set.hash;

import gnu.trove.TByteCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.set.TByteSet;
import gnu.trove.set.hash.TByteHashSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TByteHashSet
extends TByteHash
implements TByteSet,
Externalizable {
    static final long serialVersionUID = 1L;

    public TByteHashSet() {
    }

    public TByteHashSet(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TByteHashSet(int initialCapacity, float load_factor) {
        super((int)initialCapacity, (float)load_factor);
    }

    public TByteHashSet(int initial_capacity, float load_factor, byte no_entry_value) {
        super((int)initial_capacity, (float)load_factor, (byte)no_entry_value);
        if (no_entry_value == 0) return;
        Arrays.fill((byte[])this._set, (byte)no_entry_value);
    }

    public TByteHashSet(Collection<? extends Byte> collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        this.addAll(collection);
    }

    public TByteHashSet(TByteCollection collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        if (collection instanceof TByteHashSet) {
            TByteHashSet hashset = (TByteHashSet)collection;
            this._loadFactor = hashset._loadFactor;
            this.no_entry_value = hashset.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill((byte[])this._set, (byte)this.no_entry_value);
            }
            this.setUp((int)TByteHashSet.saturatedCast((long)TByteHashSet.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.addAll((TByteCollection)collection);
    }

    public TByteHashSet(byte[] array) {
        this((int)Math.max((int)array.length, (int)10));
        this.addAll((byte[])array);
    }

    @Override
    public TByteIterator iterator() {
        return new TByteHashIterator((TByteHashSet)this, (TByteHash)this);
    }

    @Override
    public byte[] toArray() {
        return this.toArray((byte[])new byte[this._size]);
    }

    @Override
    public byte[] toArray(byte[] dest) {
        if (dest.length < this._size) {
            dest = new byte[this._size];
        }
        byte[] set = this._set;
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
    public boolean add(byte val) {
        int index = this.insertKey((byte)val);
        if (index < 0) {
            return false;
        }
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean remove(byte val) {
        int index = this.index((byte)val);
        if (index < 0) return false;
        this.removeAt((int)index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        ? element;
        byte c;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Byte)) return false;
        } while (this.contains((byte)(c = ((Byte)element).byteValue())));
        return false;
    }

    @Override
    public boolean containsAll(TByteCollection collection) {
        byte element;
        TByteIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((byte)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(byte[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((byte)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> collection) {
        boolean changed = false;
        Iterator<? extends Byte> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Byte element = iterator.next();
            byte e = element.byteValue();
            if (!this.add((byte)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TByteCollection collection) {
        boolean changed = false;
        TByteIterator iter = collection.iterator();
        while (iter.hasNext()) {
            byte element = iter.next();
            if (!this.add((byte)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(byte[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.add((byte)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TByteIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Byte.valueOf((byte)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TByteCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TByteIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((byte)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(byte[] array) {
        boolean changed = false;
        Arrays.sort((byte[])array);
        byte[] set = this._set;
        byte[] states = this._states;
        this._autoCompactTemporaryDisable = true;
        int i = set.length;
        do {
            if (i-- <= 0) {
                this._autoCompactTemporaryDisable = false;
                return changed;
            }
            if (states[i] != 1 || Arrays.binarySearch((byte[])array, (byte)set[i]) >= 0) continue;
            this.removeAt((int)i);
            changed = true;
        } while (true);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            byte c;
            ? element = iterator.next();
            if (!(element instanceof Byte) || !this.remove((byte)(c = ((Byte)element).byteValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TByteCollection collection) {
        boolean changed = false;
        TByteIterator iter = collection.iterator();
        while (iter.hasNext()) {
            byte element = iter.next();
            if (!this.remove((byte)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(byte[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((byte)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        super.clear();
        byte[] set = this._set;
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
        byte[] oldSet = this._set;
        byte[] oldStates = this._states;
        this._set = new byte[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            byte o = oldSet[i];
            int n = this.insertKey((byte)o);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TByteSet)) {
            return false;
        }
        TByteSet that = (TByteSet)other;
        if (that.size() != this.size()) {
            return false;
        }
        int i = this._states.length;
        do {
            if (i-- <= 0) return true;
        } while (this._states[i] != 1 || that.contains((byte)this._set[i]));
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
        out.writeByte((int)this.no_entry_value);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeByte((int)this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        if (version >= 1) {
            this._loadFactor = in.readFloat();
            this.no_entry_value = in.readByte();
            if (this.no_entry_value != 0) {
                Arrays.fill((byte[])this._set, (byte)this.no_entry_value);
            }
        }
        this.setUp((int)size);
        while (size-- > 0) {
            byte val = in.readByte();
            this.add((byte)val);
        }
    }
}

