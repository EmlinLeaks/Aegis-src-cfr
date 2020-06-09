/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.set.hash;

import gnu.trove.TCharCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.set.TCharSet;
import gnu.trove.set.hash.TCharHashSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TCharHashSet
extends TCharHash
implements TCharSet,
Externalizable {
    static final long serialVersionUID = 1L;

    public TCharHashSet() {
    }

    public TCharHashSet(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TCharHashSet(int initialCapacity, float load_factor) {
        super((int)initialCapacity, (float)load_factor);
    }

    public TCharHashSet(int initial_capacity, float load_factor, char no_entry_value) {
        super((int)initial_capacity, (float)load_factor, (char)no_entry_value);
        if (no_entry_value == '\u0000') return;
        Arrays.fill((char[])this._set, (char)no_entry_value);
    }

    public TCharHashSet(Collection<? extends Character> collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        this.addAll(collection);
    }

    public TCharHashSet(TCharCollection collection) {
        this((int)Math.max((int)collection.size(), (int)10));
        if (collection instanceof TCharHashSet) {
            TCharHashSet hashset = (TCharHashSet)collection;
            this._loadFactor = hashset._loadFactor;
            this.no_entry_value = hashset.no_entry_value;
            if (this.no_entry_value != '\u0000') {
                Arrays.fill((char[])this._set, (char)this.no_entry_value);
            }
            this.setUp((int)TCharHashSet.saturatedCast((long)TCharHashSet.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.addAll((TCharCollection)collection);
    }

    public TCharHashSet(char[] array) {
        this((int)Math.max((int)array.length, (int)10));
        this.addAll((char[])array);
    }

    @Override
    public TCharIterator iterator() {
        return new TCharHashIterator((TCharHashSet)this, (TCharHash)this);
    }

    @Override
    public char[] toArray() {
        return this.toArray((char[])new char[this._size]);
    }

    @Override
    public char[] toArray(char[] dest) {
        if (dest.length < this._size) {
            dest = new char[this._size];
        }
        char[] set = this._set;
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
    public boolean add(char val) {
        int index = this.insertKey((char)val);
        if (index < 0) {
            return false;
        }
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean remove(char val) {
        int index = this.index((char)val);
        if (index < 0) return false;
        this.removeAt((int)index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        char c;
        ? element;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Character)) return false;
        } while (this.contains((char)(c = ((Character)element).charValue())));
        return false;
    }

    @Override
    public boolean containsAll(TCharCollection collection) {
        char element;
        TCharIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((char)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(char[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((char)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Character> collection) {
        boolean changed = false;
        Iterator<? extends Character> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Character element = iterator.next();
            char e = element.charValue();
            if (!this.add((char)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TCharCollection collection) {
        boolean changed = false;
        TCharIterator iter = collection.iterator();
        while (iter.hasNext()) {
            char element = iter.next();
            if (!this.add((char)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(char[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.add((char)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TCharIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Character.valueOf((char)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TCharCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TCharIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((char)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(char[] array) {
        boolean changed = false;
        Arrays.sort((char[])array);
        char[] set = this._set;
        byte[] states = this._states;
        this._autoCompactTemporaryDisable = true;
        int i = set.length;
        do {
            if (i-- <= 0) {
                this._autoCompactTemporaryDisable = false;
                return changed;
            }
            if (states[i] != 1 || Arrays.binarySearch((char[])array, (char)set[i]) >= 0) continue;
            this.removeAt((int)i);
            changed = true;
        } while (true);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            char c;
            ? element = iterator.next();
            if (!(element instanceof Character) || !this.remove((char)(c = ((Character)element).charValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TCharCollection collection) {
        boolean changed = false;
        TCharIterator iter = collection.iterator();
        while (iter.hasNext()) {
            char element = iter.next();
            if (!this.remove((char)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(char[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((char)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        super.clear();
        char[] set = this._set;
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
        char[] oldSet = this._set;
        byte[] oldStates = this._states;
        this._set = new char[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            char o = oldSet[i];
            int n = this.insertKey((char)o);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TCharSet)) {
            return false;
        }
        TCharSet that = (TCharSet)other;
        if (that.size() != this.size()) {
            return false;
        }
        int i = this._states.length;
        do {
            if (i-- <= 0) return true;
        } while (this._states[i] != 1 || that.contains((char)this._set[i]));
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
            buffy.append((char)this._set[i]);
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
        out.writeChar((int)this.no_entry_value);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeChar((int)this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        if (version >= 1) {
            this._loadFactor = in.readFloat();
            this.no_entry_value = in.readChar();
            if (this.no_entry_value != '\u0000') {
                Arrays.fill((char[])this._set, (char)this.no_entry_value);
            }
        }
        this.setUp((int)size);
        while (size-- > 0) {
            char val = in.readChar();
            this.add((char)val);
        }
    }
}

