/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list.array;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class TIntArrayList
implements TIntList,
Externalizable {
    static final long serialVersionUID = 1L;
    protected static final int DEFAULT_CAPACITY = 10;
    protected int[] _data;
    protected int _pos;
    protected int no_entry_value;

    public TIntArrayList() {
        this((int)10, (int)0);
    }

    public TIntArrayList(int capacity) {
        this((int)capacity, (int)0);
    }

    public TIntArrayList(int capacity, int no_entry_value) {
        this._data = new int[capacity];
        this._pos = 0;
        this.no_entry_value = no_entry_value;
    }

    public TIntArrayList(TIntCollection collection) {
        this((int)collection.size());
        this.addAll((TIntCollection)collection);
    }

    public TIntArrayList(int[] values) {
        this((int)values.length);
        this.add((int[])values);
    }

    protected TIntArrayList(int[] values, int no_entry_value, boolean wrap) {
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

    public static TIntArrayList wrap(int[] values) {
        return TIntArrayList.wrap((int[])values, (int)0);
    }

    public static TIntArrayList wrap(int[] values, int no_entry_value) {
        return new TIntArrayList((int[])values, (int)no_entry_value, (boolean)true){

            public void ensureCapacity(int capacity) {
                if (capacity <= this._data.length) return;
                throw new IllegalStateException((String)"Can not grow ArrayList wrapped external array");
            }
        };
    }

    @Override
    public int getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this._data.length) return;
        int newCap = Math.max((int)(this._data.length << 1), (int)capacity);
        int[] tmp = new int[newCap];
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
        int[] tmp = new int[this.size()];
        this.toArray((int[])tmp, (int)0, (int)tmp.length);
        this._data = tmp;
    }

    @Override
    public boolean add(int val) {
        this.ensureCapacity((int)(this._pos + 1));
        this._data[this._pos++] = val;
        return true;
    }

    @Override
    public void add(int[] vals) {
        this.add((int[])vals, (int)0, (int)vals.length);
    }

    @Override
    public void add(int[] vals, int offset, int length) {
        this.ensureCapacity((int)(this._pos + length));
        System.arraycopy((Object)vals, (int)offset, (Object)this._data, (int)this._pos, (int)length);
        this._pos += length;
    }

    @Override
    public void insert(int offset, int value) {
        if (offset == this._pos) {
            this.add((int)value);
            return;
        }
        this.ensureCapacity((int)(this._pos + 1));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + 1), (int)(this._pos - offset));
        this._data[offset] = value;
        ++this._pos;
    }

    @Override
    public void insert(int offset, int[] values) {
        this.insert((int)offset, (int[])values, (int)0, (int)values.length);
    }

    @Override
    public void insert(int offset, int[] values, int valOffset, int len) {
        if (offset == this._pos) {
            this.add((int[])values, (int)valOffset, (int)len);
            return;
        }
        this.ensureCapacity((int)(this._pos + len));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + len), (int)(this._pos - offset));
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)len);
        this._pos += len;
    }

    @Override
    public int get(int offset) {
        if (offset < this._pos) return this._data[offset];
        throw new ArrayIndexOutOfBoundsException((int)offset);
    }

    public int getQuick(int offset) {
        return this._data[offset];
    }

    @Override
    public int set(int offset, int val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        int prev_val = this._data[offset];
        this._data[offset] = val;
        return prev_val;
    }

    @Override
    public int replace(int offset, int val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        int old = this._data[offset];
        this._data[offset] = val;
        return old;
    }

    @Override
    public void set(int offset, int[] values) {
        this.set((int)offset, (int[])values, (int)0, (int)values.length);
    }

    @Override
    public void set(int offset, int[] values, int valOffset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset + length > this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)length);
    }

    public void setQuick(int offset, int val) {
        this._data[offset] = val;
    }

    @Override
    public void clear() {
        this.clearQuick();
        Arrays.fill((int[])this._data, (int)this.no_entry_value);
    }

    public void clearQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(int value) {
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
    public int removeAt(int offset) {
        int old = this.get((int)offset);
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
    public TIntIterator iterator() {
        return new TIntArrayIterator((TIntArrayList)this, (int)0);
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
        if (this == collection) {
            return true;
        }
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
        int[] arrn = array;
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            int element = arrn[n2];
            if (this.add((int)element)) {
                changed = true;
            }
            ++n2;
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
        int[] data = this._data;
        int i = this._pos;
        while (i-- > 0) {
            if (Arrays.binarySearch((int[])array, (int)data[i]) >= 0) continue;
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
            int c;
            ? element = iterator.next();
            if (!(element instanceof Integer) || !this.remove((int)(c = ((Integer)element).intValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TIntCollection collection) {
        if (collection == this) {
            this.clear();
            return true;
        }
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
    public void transformValues(TIntFunction function) {
        int i = 0;
        while (i < this._pos) {
            this._data[i] = function.execute((int)this._data[i]);
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
        int tmp = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = tmp;
    }

    @Override
    public TIntList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException((String)("end index " + end + " greater than begin index " + begin));
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException((String)"begin index can not be < 0");
        }
        if (end > this._data.length) {
            throw new IndexOutOfBoundsException((String)("end index < " + this._data.length));
        }
        TIntArrayList list = new TIntArrayList((int)(end - begin));
        int i = begin;
        while (i < end) {
            list.add((int)this._data[i]);
            ++i;
        }
        return list;
    }

    @Override
    public int[] toArray() {
        return this.toArray((int)0, (int)this._pos);
    }

    @Override
    public int[] toArray(int offset, int len) {
        int[] rv = new int[len];
        this.toArray((int[])rv, (int)offset, (int)len);
        return rv;
    }

    @Override
    public int[] toArray(int[] dest) {
        int len = dest.length;
        if (dest.length > this._pos) {
            len = this._pos;
            dest[len] = this.no_entry_value;
        }
        this.toArray((int[])dest, (int)0, (int)len);
        return dest;
    }

    @Override
    public int[] toArray(int[] dest, int offset, int len) {
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
    public int[] toArray(int[] dest, int source_pos, int dest_pos, int len) {
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
        if (!(other instanceof TIntList)) {
            return false;
        }
        if (other instanceof TIntArrayList) {
            TIntArrayList that = (TIntArrayList)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = this._pos;
            do {
                if (i-- <= 0) return true;
            } while (this._data[i] == that._data[i]);
            return false;
        }
        TIntList that = (TIntList)other;
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
    public boolean forEach(TIntProcedure procedure) {
        int i = 0;
        while (i < this._pos) {
            if (!procedure.execute((int)this._data[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TIntProcedure procedure) {
        int i = this._pos;
        do {
            if (i-- <= 0) return true;
        } while (procedure.execute((int)this._data[i]));
        return false;
    }

    @Override
    public void sort() {
        Arrays.sort((int[])this._data, (int)0, (int)this._pos);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        Arrays.sort((int[])this._data, (int)fromIndex, (int)toIndex);
    }

    @Override
    public void fill(int val) {
        Arrays.fill((int[])this._data, (int)0, (int)this._pos, (int)val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, int val) {
        if (toIndex > this._pos) {
            this.ensureCapacity((int)toIndex);
            this._pos = toIndex;
        }
        Arrays.fill((int[])this._data, (int)fromIndex, (int)toIndex, (int)val);
    }

    @Override
    public int binarySearch(int value) {
        return this.binarySearch((int)value, (int)0, (int)this._pos);
    }

    @Override
    public int binarySearch(int value, int fromIndex, int toIndex) {
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
            int midVal = this._data[mid];
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
    public int indexOf(int value) {
        return this.indexOf((int)0, (int)value);
    }

    @Override
    public int indexOf(int offset, int value) {
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
    public int lastIndexOf(int value) {
        return this.lastIndexOf((int)this._pos, (int)value);
    }

    @Override
    public int lastIndexOf(int offset, int value) {
        int i = offset;
        do {
            if (i-- <= 0) return -1;
        } while (this._data[i] != value);
        return i;
    }

    @Override
    public boolean contains(int value) {
        if (this.lastIndexOf((int)value) < 0) return false;
        return true;
    }

    @Override
    public TIntList grep(TIntProcedure condition) {
        TIntArrayList list = new TIntArrayList();
        int i = 0;
        while (i < this._pos) {
            if (condition.execute((int)this._data[i])) {
                list.add((int)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public TIntList inverseGrep(TIntProcedure condition) {
        TIntArrayList list = new TIntArrayList();
        int i = 0;
        while (i < this._pos) {
            if (!condition.execute((int)this._data[i])) {
                list.add((int)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public int max() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find maximum of an empty list");
        }
        int max = Integer.MIN_VALUE;
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
    public int min() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find minimum of an empty list");
        }
        int min = Integer.MAX_VALUE;
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
    public int sum() {
        int sum = 0;
        int i = 0;
        while (i < this._pos) {
            sum += this._data[i];
            ++i;
        }
        return sum;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        int end = this._pos - 1;
        for (int i = 0; i < end; ++i) {
            buf.append((int)this._data[i]);
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((int)this._data[this._pos - 1]);
        }
        buf.append((String)"}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeInt((int)this._pos);
        out.writeInt((int)this.no_entry_value);
        int len = this._data.length;
        out.writeInt((int)len);
        int i = 0;
        while (i < len) {
            out.writeInt((int)this._data[i]);
            ++i;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._pos = in.readInt();
        this.no_entry_value = in.readInt();
        int len = in.readInt();
        this._data = new int[len];
        int i = 0;
        while (i < len) {
            this._data[i] = in.readInt();
            ++i;
        }
    }
}

