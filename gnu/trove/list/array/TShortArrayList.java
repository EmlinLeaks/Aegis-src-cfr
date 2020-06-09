/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list.array;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.list.TShortList;
import gnu.trove.list.array.TShortArrayList;
import gnu.trove.procedure.TShortProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class TShortArrayList
implements TShortList,
Externalizable {
    static final long serialVersionUID = 1L;
    protected static final int DEFAULT_CAPACITY = 10;
    protected short[] _data;
    protected int _pos;
    protected short no_entry_value;

    public TShortArrayList() {
        this((int)10, (short)0);
    }

    public TShortArrayList(int capacity) {
        this((int)capacity, (short)0);
    }

    public TShortArrayList(int capacity, short no_entry_value) {
        this._data = new short[capacity];
        this._pos = 0;
        this.no_entry_value = no_entry_value;
    }

    public TShortArrayList(TShortCollection collection) {
        this((int)collection.size());
        this.addAll((TShortCollection)collection);
    }

    public TShortArrayList(short[] values) {
        this((int)values.length);
        this.add((short[])values);
    }

    protected TShortArrayList(short[] values, short no_entry_value, boolean wrap) {
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

    public static TShortArrayList wrap(short[] values) {
        return TShortArrayList.wrap((short[])values, (short)0);
    }

    public static TShortArrayList wrap(short[] values, short no_entry_value) {
        return new TShortArrayList((short[])values, (short)no_entry_value, (boolean)true){

            public void ensureCapacity(int capacity) {
                if (capacity <= this._data.length) return;
                throw new IllegalStateException((String)"Can not grow ArrayList wrapped external array");
            }
        };
    }

    @Override
    public short getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this._data.length) return;
        int newCap = Math.max((int)(this._data.length << 1), (int)capacity);
        short[] tmp = new short[newCap];
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
        short[] tmp = new short[this.size()];
        this.toArray((short[])tmp, (int)0, (int)tmp.length);
        this._data = tmp;
    }

    @Override
    public boolean add(short val) {
        this.ensureCapacity((int)(this._pos + 1));
        this._data[this._pos++] = val;
        return true;
    }

    @Override
    public void add(short[] vals) {
        this.add((short[])vals, (int)0, (int)vals.length);
    }

    @Override
    public void add(short[] vals, int offset, int length) {
        this.ensureCapacity((int)(this._pos + length));
        System.arraycopy((Object)vals, (int)offset, (Object)this._data, (int)this._pos, (int)length);
        this._pos += length;
    }

    @Override
    public void insert(int offset, short value) {
        if (offset == this._pos) {
            this.add((short)value);
            return;
        }
        this.ensureCapacity((int)(this._pos + 1));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + 1), (int)(this._pos - offset));
        this._data[offset] = value;
        ++this._pos;
    }

    @Override
    public void insert(int offset, short[] values) {
        this.insert((int)offset, (short[])values, (int)0, (int)values.length);
    }

    @Override
    public void insert(int offset, short[] values, int valOffset, int len) {
        if (offset == this._pos) {
            this.add((short[])values, (int)valOffset, (int)len);
            return;
        }
        this.ensureCapacity((int)(this._pos + len));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + len), (int)(this._pos - offset));
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)len);
        this._pos += len;
    }

    @Override
    public short get(int offset) {
        if (offset < this._pos) return this._data[offset];
        throw new ArrayIndexOutOfBoundsException((int)offset);
    }

    public short getQuick(int offset) {
        return this._data[offset];
    }

    @Override
    public short set(int offset, short val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        short prev_val = this._data[offset];
        this._data[offset] = val;
        return prev_val;
    }

    @Override
    public short replace(int offset, short val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        short old = this._data[offset];
        this._data[offset] = val;
        return old;
    }

    @Override
    public void set(int offset, short[] values) {
        this.set((int)offset, (short[])values, (int)0, (int)values.length);
    }

    @Override
    public void set(int offset, short[] values, int valOffset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset + length > this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)length);
    }

    public void setQuick(int offset, short val) {
        this._data[offset] = val;
    }

    @Override
    public void clear() {
        this.clearQuick();
        Arrays.fill((short[])this._data, (short)this.no_entry_value);
    }

    public void clearQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(short value) {
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
    public short removeAt(int offset) {
        short old = this.get((int)offset);
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
    public TShortIterator iterator() {
        return new TShortArrayIterator((TShortArrayList)this, (int)0);
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
        if (this == collection) {
            return true;
        }
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
        short[] arrs = array;
        int n = arrs.length;
        int n2 = 0;
        while (n2 < n) {
            short element = arrs[n2];
            if (this.add((short)element)) {
                changed = true;
            }
            ++n2;
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
        short[] data = this._data;
        int i = this._pos;
        while (i-- > 0) {
            if (Arrays.binarySearch((short[])array, (short)data[i]) >= 0) continue;
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
            short c;
            ? element = iterator.next();
            if (!(element instanceof Short) || !this.remove((short)(c = ((Short)element).shortValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TShortCollection collection) {
        if (collection == this) {
            this.clear();
            return true;
        }
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
    public void transformValues(TShortFunction function) {
        int i = 0;
        while (i < this._pos) {
            this._data[i] = function.execute((short)this._data[i]);
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
        short tmp = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = tmp;
    }

    @Override
    public TShortList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException((String)("end index " + end + " greater than begin index " + begin));
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException((String)"begin index can not be < 0");
        }
        if (end > this._data.length) {
            throw new IndexOutOfBoundsException((String)("end index < " + this._data.length));
        }
        TShortArrayList list = new TShortArrayList((int)(end - begin));
        int i = begin;
        while (i < end) {
            list.add((short)this._data[i]);
            ++i;
        }
        return list;
    }

    @Override
    public short[] toArray() {
        return this.toArray((int)0, (int)this._pos);
    }

    @Override
    public short[] toArray(int offset, int len) {
        short[] rv = new short[len];
        this.toArray((short[])rv, (int)offset, (int)len);
        return rv;
    }

    @Override
    public short[] toArray(short[] dest) {
        int len = dest.length;
        if (dest.length > this._pos) {
            len = this._pos;
            dest[len] = this.no_entry_value;
        }
        this.toArray((short[])dest, (int)0, (int)len);
        return dest;
    }

    @Override
    public short[] toArray(short[] dest, int offset, int len) {
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
    public short[] toArray(short[] dest, int source_pos, int dest_pos, int len) {
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
        if (!(other instanceof TShortList)) {
            return false;
        }
        if (other instanceof TShortArrayList) {
            TShortArrayList that = (TShortArrayList)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = this._pos;
            do {
                if (i-- <= 0) return true;
            } while (this._data[i] == that._data[i]);
            return false;
        }
        TShortList that = (TShortList)other;
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
    public boolean forEach(TShortProcedure procedure) {
        int i = 0;
        while (i < this._pos) {
            if (!procedure.execute((short)this._data[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TShortProcedure procedure) {
        int i = this._pos;
        do {
            if (i-- <= 0) return true;
        } while (procedure.execute((short)this._data[i]));
        return false;
    }

    @Override
    public void sort() {
        Arrays.sort((short[])this._data, (int)0, (int)this._pos);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        Arrays.sort((short[])this._data, (int)fromIndex, (int)toIndex);
    }

    @Override
    public void fill(short val) {
        Arrays.fill((short[])this._data, (int)0, (int)this._pos, (short)val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, short val) {
        if (toIndex > this._pos) {
            this.ensureCapacity((int)toIndex);
            this._pos = toIndex;
        }
        Arrays.fill((short[])this._data, (int)fromIndex, (int)toIndex, (short)val);
    }

    @Override
    public int binarySearch(short value) {
        return this.binarySearch((short)value, (int)0, (int)this._pos);
    }

    @Override
    public int binarySearch(short value, int fromIndex, int toIndex) {
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
            short midVal = this._data[mid];
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
    public int indexOf(short value) {
        return this.indexOf((int)0, (short)value);
    }

    @Override
    public int indexOf(int offset, short value) {
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
    public int lastIndexOf(short value) {
        return this.lastIndexOf((int)this._pos, (short)value);
    }

    @Override
    public int lastIndexOf(int offset, short value) {
        int i = offset;
        do {
            if (i-- <= 0) return -1;
        } while (this._data[i] != value);
        return i;
    }

    @Override
    public boolean contains(short value) {
        if (this.lastIndexOf((short)value) < 0) return false;
        return true;
    }

    @Override
    public TShortList grep(TShortProcedure condition) {
        TShortArrayList list = new TShortArrayList();
        int i = 0;
        while (i < this._pos) {
            if (condition.execute((short)this._data[i])) {
                list.add((short)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public TShortList inverseGrep(TShortProcedure condition) {
        TShortArrayList list = new TShortArrayList();
        int i = 0;
        while (i < this._pos) {
            if (!condition.execute((short)this._data[i])) {
                list.add((short)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public short max() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find maximum of an empty list");
        }
        short max = -32768;
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
    public short min() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find minimum of an empty list");
        }
        short min = 32767;
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
    public short sum() {
        short sum = 0;
        int i = 0;
        while (i < this._pos) {
            sum = (short)(sum + this._data[i]);
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
        out.writeShort((int)this.no_entry_value);
        int len = this._data.length;
        out.writeInt((int)len);
        int i = 0;
        while (i < len) {
            out.writeShort((int)this._data[i]);
            ++i;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._pos = in.readInt();
        this.no_entry_value = in.readShort();
        int len = in.readInt();
        this._data = new short[len];
        int i = 0;
        while (i < len) {
            this._data[i] = in.readShort();
            ++i;
        }
    }
}

