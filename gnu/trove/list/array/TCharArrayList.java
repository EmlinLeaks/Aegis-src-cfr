/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list.array;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.list.TCharList;
import gnu.trove.list.array.TCharArrayList;
import gnu.trove.procedure.TCharProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class TCharArrayList
implements TCharList,
Externalizable {
    static final long serialVersionUID = 1L;
    protected static final int DEFAULT_CAPACITY = 10;
    protected char[] _data;
    protected int _pos;
    protected char no_entry_value;

    public TCharArrayList() {
        this((int)10, (char)'\u0000');
    }

    public TCharArrayList(int capacity) {
        this((int)capacity, (char)'\u0000');
    }

    public TCharArrayList(int capacity, char no_entry_value) {
        this._data = new char[capacity];
        this._pos = 0;
        this.no_entry_value = no_entry_value;
    }

    public TCharArrayList(TCharCollection collection) {
        this((int)collection.size());
        this.addAll((TCharCollection)collection);
    }

    public TCharArrayList(char[] values) {
        this((int)values.length);
        this.add((char[])values);
    }

    protected TCharArrayList(char[] values, char no_entry_value, boolean wrap) {
        if (!wrap) {
            throw new IllegalStateException((String)"Wrong call");
        }
        if (values == null) {
            throw new IllegalArgumentException((String)"values can not be null");
        }
        this._data = values;
        this._pos = values.length;
        this.no_entry_value = no_entry_value;
    }

    public static TCharArrayList wrap(char[] values) {
        return TCharArrayList.wrap((char[])values, (char)'\u0000');
    }

    public static TCharArrayList wrap(char[] values, char no_entry_value) {
        return new TCharArrayList((char[])values, (char)no_entry_value, (boolean)true){

            public void ensureCapacity(int capacity) {
                if (capacity <= this._data.length) return;
                throw new IllegalStateException((String)"Can not grow ArrayList wrapped external array");
            }
        };
    }

    @Override
    public char getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this._data.length) return;
        int newCap = Math.max((int)(this._data.length << 1), (int)capacity);
        char[] tmp = new char[newCap];
        System.arraycopy((Object)this._data, (int)0, (Object)tmp, (int)0, (int)this._data.length);
        this._data = tmp;
    }

    @Override
    public int size() {
        return this._pos;
    }

    @Override
    public boolean isEmpty() {
        if (this._pos != 0) return false;
        return true;
    }

    public void trimToSize() {
        if (this._data.length <= this.size()) return;
        char[] tmp = new char[this.size()];
        this.toArray((char[])tmp, (int)0, (int)tmp.length);
        this._data = tmp;
    }

    @Override
    public boolean add(char val) {
        this.ensureCapacity((int)(this._pos + 1));
        this._data[this._pos++] = val;
        return true;
    }

    @Override
    public void add(char[] vals) {
        this.add((char[])vals, (int)0, (int)vals.length);
    }

    @Override
    public void add(char[] vals, int offset, int length) {
        this.ensureCapacity((int)(this._pos + length));
        System.arraycopy((Object)vals, (int)offset, (Object)this._data, (int)this._pos, (int)length);
        this._pos += length;
    }

    @Override
    public void insert(int offset, char value) {
        if (offset == this._pos) {
            this.add((char)value);
            return;
        }
        this.ensureCapacity((int)(this._pos + 1));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + 1), (int)(this._pos - offset));
        this._data[offset] = value;
        ++this._pos;
    }

    @Override
    public void insert(int offset, char[] values) {
        this.insert((int)offset, (char[])values, (int)0, (int)values.length);
    }

    @Override
    public void insert(int offset, char[] values, int valOffset, int len) {
        if (offset == this._pos) {
            this.add((char[])values, (int)valOffset, (int)len);
            return;
        }
        this.ensureCapacity((int)(this._pos + len));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + len), (int)(this._pos - offset));
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)len);
        this._pos += len;
    }

    @Override
    public char get(int offset) {
        if (offset < this._pos) return this._data[offset];
        throw new ArrayIndexOutOfBoundsException((int)offset);
    }

    public char getQuick(int offset) {
        return this._data[offset];
    }

    @Override
    public char set(int offset, char val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        char prev_val = this._data[offset];
        this._data[offset] = val;
        return prev_val;
    }

    @Override
    public char replace(int offset, char val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        char old = this._data[offset];
        this._data[offset] = val;
        return old;
    }

    @Override
    public void set(int offset, char[] values) {
        this.set((int)offset, (char[])values, (int)0, (int)values.length);
    }

    @Override
    public void set(int offset, char[] values, int valOffset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset + length > this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)length);
    }

    public void setQuick(int offset, char val) {
        this._data[offset] = val;
    }

    @Override
    public void clear() {
        this.clearQuick();
        Arrays.fill((char[])this._data, (char)this.no_entry_value);
    }

    public void clearQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(char value) {
        int index = 0;
        while (index < this._pos) {
            if (value == this._data[index]) {
                this.remove((int)index, (int)1);
                return true;
            }
            ++index;
        }
        return false;
    }

    @Override
    public char removeAt(int offset) {
        char old = this.get((int)offset);
        this.remove((int)offset, (int)1);
        return old;
    }

    @Override
    public void remove(int offset, int length) {
        if (length == 0) {
            return;
        }
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        if (offset == 0) {
            System.arraycopy((Object)this._data, (int)length, (Object)this._data, (int)0, (int)(this._pos - length));
        } else if (this._pos - length != offset) {
            System.arraycopy((Object)this._data, (int)(offset + length), (Object)this._data, (int)offset, (int)(this._pos - (offset + length)));
        }
        this._pos -= length;
    }

    @Override
    public TCharIterator iterator() {
        return new TCharArrayIterator((TCharArrayList)this, (int)0);
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
        if (this == collection) {
            return true;
        }
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
        char[] arrc = array;
        int n = arrc.length;
        int n2 = 0;
        while (n2 < n) {
            char element = arrc[n2];
            if (this.add((char)element)) {
                changed = true;
            }
            ++n2;
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
        char[] data = this._data;
        int i = this._pos;
        while (i-- > 0) {
            if (Arrays.binarySearch((char[])array, (char)data[i]) >= 0) continue;
            this.remove((int)i, (int)1);
            changed = true;
        }
        return changed;
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
        if (collection == this) {
            this.clear();
            return true;
        }
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
    public void transformValues(TCharFunction function) {
        int i = 0;
        while (i < this._pos) {
            this._data[i] = function.execute((char)this._data[i]);
            ++i;
        }
    }

    @Override
    public void reverse() {
        this.reverse((int)0, (int)this._pos);
    }

    @Override
    public void reverse(int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException((String)"from cannot be greater than to");
        }
        int i = from;
        int j = to - 1;
        while (i < j) {
            this.swap((int)i, (int)j);
            ++i;
            --j;
        }
    }

    @Override
    public void shuffle(Random rand) {
        int i = this._pos;
        while (i-- > 1) {
            this.swap((int)i, (int)rand.nextInt((int)i));
        }
    }

    private void swap(int i, int j) {
        char tmp = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = tmp;
    }

    @Override
    public TCharList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException((String)("end index " + end + " greater than begin index " + begin));
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException((String)"begin index can not be < 0");
        }
        if (end > this._data.length) {
            throw new IndexOutOfBoundsException((String)("end index < " + this._data.length));
        }
        TCharArrayList list = new TCharArrayList((int)(end - begin));
        int i = begin;
        while (i < end) {
            list.add((char)this._data[i]);
            ++i;
        }
        return list;
    }

    @Override
    public char[] toArray() {
        return this.toArray((int)0, (int)this._pos);
    }

    @Override
    public char[] toArray(int offset, int len) {
        char[] rv = new char[len];
        this.toArray((char[])rv, (int)offset, (int)len);
        return rv;
    }

    @Override
    public char[] toArray(char[] dest) {
        int len = dest.length;
        if (dest.length > this._pos) {
            len = this._pos;
            dest[len] = this.no_entry_value;
        }
        this.toArray((char[])dest, (int)0, (int)len);
        return dest;
    }

    @Override
    public char[] toArray(char[] dest, int offset, int len) {
        if (len == 0) {
            return dest;
        }
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        System.arraycopy((Object)this._data, (int)offset, (Object)dest, (int)0, (int)len);
        return dest;
    }

    @Override
    public char[] toArray(char[] dest, int source_pos, int dest_pos, int len) {
        if (len == 0) {
            return dest;
        }
        if (source_pos < 0) throw new ArrayIndexOutOfBoundsException((int)source_pos);
        if (source_pos >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)source_pos);
        }
        System.arraycopy((Object)this._data, (int)source_pos, (Object)dest, (int)dest_pos, (int)len);
        return dest;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TCharList)) {
            return false;
        }
        if (other instanceof TCharArrayList) {
            TCharArrayList that = (TCharArrayList)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = this._pos;
            do {
                if (i-- <= 0) return true;
            } while (this._data[i] == that._data[i]);
            return false;
        }
        TCharList that = (TCharList)other;
        if (that.size() != this.size()) {
            return false;
        }
        int i = 0;
        while (i < this._pos) {
            if (this._data[i] != that.get((int)i)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int i = this._pos;
        while (i-- > 0) {
            h += HashFunctions.hash((int)this._data[i]);
        }
        return h;
    }

    @Override
    public boolean forEach(TCharProcedure procedure) {
        int i = 0;
        while (i < this._pos) {
            if (!procedure.execute((char)this._data[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TCharProcedure procedure) {
        int i = this._pos;
        do {
            if (i-- <= 0) return true;
        } while (procedure.execute((char)this._data[i]));
        return false;
    }

    @Override
    public void sort() {
        Arrays.sort((char[])this._data, (int)0, (int)this._pos);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        Arrays.sort((char[])this._data, (int)fromIndex, (int)toIndex);
    }

    @Override
    public void fill(char val) {
        Arrays.fill((char[])this._data, (int)0, (int)this._pos, (char)val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, char val) {
        if (toIndex > this._pos) {
            this.ensureCapacity((int)toIndex);
            this._pos = toIndex;
        }
        Arrays.fill((char[])this._data, (int)fromIndex, (int)toIndex, (char)val);
    }

    @Override
    public int binarySearch(char value) {
        return this.binarySearch((char)value, (int)0, (int)this._pos);
    }

    @Override
    public int binarySearch(char value, int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException((int)fromIndex);
        }
        if (toIndex > this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)toIndex);
        }
        int low = fromIndex;
        int high = toIndex - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            char midVal = this._data[mid];
            if (midVal < value) {
                low = mid + 1;
                continue;
            }
            if (midVal <= value) return mid;
            high = mid - 1;
        }
        return -(low + 1);
    }

    @Override
    public int indexOf(char value) {
        return this.indexOf((int)0, (char)value);
    }

    @Override
    public int indexOf(int offset, char value) {
        int i = offset;
        while (i < this._pos) {
            if (this._data[i] == value) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(char value) {
        return this.lastIndexOf((int)this._pos, (char)value);
    }

    @Override
    public int lastIndexOf(int offset, char value) {
        int i = offset;
        do {
            if (i-- <= 0) return -1;
        } while (this._data[i] != value);
        return i;
    }

    @Override
    public boolean contains(char value) {
        if (this.lastIndexOf((char)value) < 0) return false;
        return true;
    }

    @Override
    public TCharList grep(TCharProcedure condition) {
        TCharArrayList list = new TCharArrayList();
        int i = 0;
        while (i < this._pos) {
            if (condition.execute((char)this._data[i])) {
                list.add((char)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public TCharList inverseGrep(TCharProcedure condition) {
        TCharArrayList list = new TCharArrayList();
        int i = 0;
        while (i < this._pos) {
            if (!condition.execute((char)this._data[i])) {
                list.add((char)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public char max() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find maximum of an empty list");
        }
        char max = '\u0000';
        int i = 0;
        while (i < this._pos) {
            if (this._data[i] > max) {
                max = this._data[i];
            }
            ++i;
        }
        return max;
    }

    @Override
    public char min() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find minimum of an empty list");
        }
        char min = '\uffff';
        int i = 0;
        while (i < this._pos) {
            if (this._data[i] < min) {
                min = this._data[i];
            }
            ++i;
        }
        return min;
    }

    @Override
    public char sum() {
        char sum = '\u0000';
        int i = 0;
        while (i < this._pos) {
            sum = (char)(sum + this._data[i]);
            ++i;
        }
        return sum;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        int end = this._pos - 1;
        for (int i = 0; i < end; ++i) {
            buf.append((char)this._data[i]);
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((char)this._data[this._pos - 1]);
        }
        buf.append((String)"}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeInt((int)this._pos);
        out.writeChar((int)this.no_entry_value);
        int len = this._data.length;
        out.writeInt((int)len);
        int i = 0;
        while (i < len) {
            out.writeChar((int)this._data[i]);
            ++i;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._pos = in.readInt();
        this.no_entry_value = in.readChar();
        int len = in.readInt();
        this._data = new char[len];
        int i = 0;
        while (i < len) {
            this._data[i] = in.readChar();
            ++i;
        }
    }
}

