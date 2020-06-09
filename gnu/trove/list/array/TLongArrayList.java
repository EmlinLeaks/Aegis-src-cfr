/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list.array;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.procedure.TLongProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class TLongArrayList
implements TLongList,
Externalizable {
    static final long serialVersionUID = 1L;
    protected static final int DEFAULT_CAPACITY = 10;
    protected long[] _data;
    protected int _pos;
    protected long no_entry_value;

    public TLongArrayList() {
        this((int)10, (long)0L);
    }

    public TLongArrayList(int capacity) {
        this((int)capacity, (long)0L);
    }

    public TLongArrayList(int capacity, long no_entry_value) {
        this._data = new long[capacity];
        this._pos = 0;
        this.no_entry_value = no_entry_value;
    }

    public TLongArrayList(TLongCollection collection) {
        this((int)collection.size());
        this.addAll((TLongCollection)collection);
    }

    public TLongArrayList(long[] values) {
        this((int)values.length);
        this.add((long[])values);
    }

    protected TLongArrayList(long[] values, long no_entry_value, boolean wrap) {
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

    public static TLongArrayList wrap(long[] values) {
        return TLongArrayList.wrap((long[])values, (long)0L);
    }

    public static TLongArrayList wrap(long[] values, long no_entry_value) {
        return new TLongArrayList((long[])values, (long)no_entry_value, (boolean)true){

            public void ensureCapacity(int capacity) {
                if (capacity <= this._data.length) return;
                throw new IllegalStateException((String)"Can not grow ArrayList wrapped external array");
            }
        };
    }

    @Override
    public long getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this._data.length) return;
        int newCap = Math.max((int)(this._data.length << 1), (int)capacity);
        long[] tmp = new long[newCap];
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
        long[] tmp = new long[this.size()];
        this.toArray((long[])tmp, (int)0, (int)tmp.length);
        this._data = tmp;
    }

    @Override
    public boolean add(long val) {
        this.ensureCapacity((int)(this._pos + 1));
        this._data[this._pos++] = val;
        return true;
    }

    @Override
    public void add(long[] vals) {
        this.add((long[])vals, (int)0, (int)vals.length);
    }

    @Override
    public void add(long[] vals, int offset, int length) {
        this.ensureCapacity((int)(this._pos + length));
        System.arraycopy((Object)vals, (int)offset, (Object)this._data, (int)this._pos, (int)length);
        this._pos += length;
    }

    @Override
    public void insert(int offset, long value) {
        if (offset == this._pos) {
            this.add((long)value);
            return;
        }
        this.ensureCapacity((int)(this._pos + 1));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + 1), (int)(this._pos - offset));
        this._data[offset] = value;
        ++this._pos;
    }

    @Override
    public void insert(int offset, long[] values) {
        this.insert((int)offset, (long[])values, (int)0, (int)values.length);
    }

    @Override
    public void insert(int offset, long[] values, int valOffset, int len) {
        if (offset == this._pos) {
            this.add((long[])values, (int)valOffset, (int)len);
            return;
        }
        this.ensureCapacity((int)(this._pos + len));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + len), (int)(this._pos - offset));
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)len);
        this._pos += len;
    }

    @Override
    public long get(int offset) {
        if (offset < this._pos) return this._data[offset];
        throw new ArrayIndexOutOfBoundsException((int)offset);
    }

    public long getQuick(int offset) {
        return this._data[offset];
    }

    @Override
    public long set(int offset, long val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        long prev_val = this._data[offset];
        this._data[offset] = val;
        return prev_val;
    }

    @Override
    public long replace(int offset, long val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        long old = this._data[offset];
        this._data[offset] = val;
        return old;
    }

    @Override
    public void set(int offset, long[] values) {
        this.set((int)offset, (long[])values, (int)0, (int)values.length);
    }

    @Override
    public void set(int offset, long[] values, int valOffset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset + length > this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)length);
    }

    public void setQuick(int offset, long val) {
        this._data[offset] = val;
    }

    @Override
    public void clear() {
        this.clearQuick();
        Arrays.fill((long[])this._data, (long)this.no_entry_value);
    }

    public void clearQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(long value) {
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
    public long removeAt(int offset) {
        long old = this.get((int)offset);
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
    public TLongIterator iterator() {
        return new TLongArrayIterator((TLongArrayList)this, (int)0);
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
        if (this == collection) {
            return true;
        }
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
        long[] arrl = array;
        int n = arrl.length;
        int n2 = 0;
        while (n2 < n) {
            long element = arrl[n2];
            if (this.add((long)element)) {
                changed = true;
            }
            ++n2;
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
        long[] data = this._data;
        int i = this._pos;
        while (i-- > 0) {
            if (Arrays.binarySearch((long[])array, (long)data[i]) >= 0) continue;
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
            long c;
            ? element = iterator.next();
            if (!(element instanceof Long) || !this.remove((long)(c = ((Long)element).longValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TLongCollection collection) {
        if (collection == this) {
            this.clear();
            return true;
        }
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
    public void transformValues(TLongFunction function) {
        int i = 0;
        while (i < this._pos) {
            this._data[i] = function.execute((long)this._data[i]);
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
        long tmp = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = tmp;
    }

    @Override
    public TLongList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException((String)("end index " + end + " greater than begin index " + begin));
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException((String)"begin index can not be < 0");
        }
        if (end > this._data.length) {
            throw new IndexOutOfBoundsException((String)("end index < " + this._data.length));
        }
        TLongArrayList list = new TLongArrayList((int)(end - begin));
        int i = begin;
        while (i < end) {
            list.add((long)this._data[i]);
            ++i;
        }
        return list;
    }

    @Override
    public long[] toArray() {
        return this.toArray((int)0, (int)this._pos);
    }

    @Override
    public long[] toArray(int offset, int len) {
        long[] rv = new long[len];
        this.toArray((long[])rv, (int)offset, (int)len);
        return rv;
    }

    @Override
    public long[] toArray(long[] dest) {
        int len = dest.length;
        if (dest.length > this._pos) {
            len = this._pos;
            dest[len] = this.no_entry_value;
        }
        this.toArray((long[])dest, (int)0, (int)len);
        return dest;
    }

    @Override
    public long[] toArray(long[] dest, int offset, int len) {
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
    public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) {
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
        if (!(other instanceof TLongList)) {
            return false;
        }
        if (other instanceof TLongArrayList) {
            TLongArrayList that = (TLongArrayList)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = this._pos;
            do {
                if (i-- <= 0) return true;
            } while (this._data[i] == that._data[i]);
            return false;
        }
        TLongList that = (TLongList)other;
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
            h += HashFunctions.hash((long)this._data[i]);
        }
        return h;
    }

    @Override
    public boolean forEach(TLongProcedure procedure) {
        int i = 0;
        while (i < this._pos) {
            if (!procedure.execute((long)this._data[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TLongProcedure procedure) {
        int i = this._pos;
        do {
            if (i-- <= 0) return true;
        } while (procedure.execute((long)this._data[i]));
        return false;
    }

    @Override
    public void sort() {
        Arrays.sort((long[])this._data, (int)0, (int)this._pos);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        Arrays.sort((long[])this._data, (int)fromIndex, (int)toIndex);
    }

    @Override
    public void fill(long val) {
        Arrays.fill((long[])this._data, (int)0, (int)this._pos, (long)val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        if (toIndex > this._pos) {
            this.ensureCapacity((int)toIndex);
            this._pos = toIndex;
        }
        Arrays.fill((long[])this._data, (int)fromIndex, (int)toIndex, (long)val);
    }

    @Override
    public int binarySearch(long value) {
        return this.binarySearch((long)value, (int)0, (int)this._pos);
    }

    @Override
    public int binarySearch(long value, int fromIndex, int toIndex) {
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
            long midVal = this._data[mid];
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
    public int indexOf(long value) {
        return this.indexOf((int)0, (long)value);
    }

    @Override
    public int indexOf(int offset, long value) {
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
    public int lastIndexOf(long value) {
        return this.lastIndexOf((int)this._pos, (long)value);
    }

    @Override
    public int lastIndexOf(int offset, long value) {
        int i = offset;
        do {
            if (i-- <= 0) return -1;
        } while (this._data[i] != value);
        return i;
    }

    @Override
    public boolean contains(long value) {
        if (this.lastIndexOf((long)value) < 0) return false;
        return true;
    }

    @Override
    public TLongList grep(TLongProcedure condition) {
        TLongArrayList list = new TLongArrayList();
        int i = 0;
        while (i < this._pos) {
            if (condition.execute((long)this._data[i])) {
                list.add((long)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public TLongList inverseGrep(TLongProcedure condition) {
        TLongArrayList list = new TLongArrayList();
        int i = 0;
        while (i < this._pos) {
            if (!condition.execute((long)this._data[i])) {
                list.add((long)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public long max() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find maximum of an empty list");
        }
        long max = Long.MIN_VALUE;
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
    public long min() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find minimum of an empty list");
        }
        long min = Long.MAX_VALUE;
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
    public long sum() {
        long sum = 0L;
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
            buf.append((long)this._data[i]);
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((long)this._data[this._pos - 1]);
        }
        buf.append((String)"}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeInt((int)this._pos);
        out.writeLong((long)this.no_entry_value);
        int len = this._data.length;
        out.writeInt((int)len);
        int i = 0;
        while (i < len) {
            out.writeLong((long)this._data[i]);
            ++i;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._pos = in.readInt();
        this.no_entry_value = in.readLong();
        int len = in.readInt();
        this._data = new long[len];
        int i = 0;
        while (i < len) {
            this._data[i] = in.readLong();
            ++i;
        }
    }
}

