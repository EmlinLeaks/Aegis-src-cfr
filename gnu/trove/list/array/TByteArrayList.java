/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list.array;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.procedure.TByteProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class TByteArrayList
implements TByteList,
Externalizable {
    static final long serialVersionUID = 1L;
    protected static final int DEFAULT_CAPACITY = 10;
    protected byte[] _data;
    protected int _pos;
    protected byte no_entry_value;

    public TByteArrayList() {
        this((int)10, (byte)0);
    }

    public TByteArrayList(int capacity) {
        this((int)capacity, (byte)0);
    }

    public TByteArrayList(int capacity, byte no_entry_value) {
        this._data = new byte[capacity];
        this._pos = 0;
        this.no_entry_value = no_entry_value;
    }

    public TByteArrayList(TByteCollection collection) {
        this((int)collection.size());
        this.addAll((TByteCollection)collection);
    }

    public TByteArrayList(byte[] values) {
        this((int)values.length);
        this.add((byte[])values);
    }

    protected TByteArrayList(byte[] values, byte no_entry_value, boolean wrap) {
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

    public static TByteArrayList wrap(byte[] values) {
        return TByteArrayList.wrap((byte[])values, (byte)0);
    }

    public static TByteArrayList wrap(byte[] values, byte no_entry_value) {
        return new TByteArrayList((byte[])values, (byte)no_entry_value, (boolean)true){

            public void ensureCapacity(int capacity) {
                if (capacity <= this._data.length) return;
                throw new IllegalStateException((String)"Can not grow ArrayList wrapped external array");
            }
        };
    }

    @Override
    public byte getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this._data.length) return;
        int newCap = Math.max((int)(this._data.length << 1), (int)capacity);
        byte[] tmp = new byte[newCap];
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
        byte[] tmp = new byte[this.size()];
        this.toArray((byte[])tmp, (int)0, (int)tmp.length);
        this._data = tmp;
    }

    @Override
    public boolean add(byte val) {
        this.ensureCapacity((int)(this._pos + 1));
        this._data[this._pos++] = val;
        return true;
    }

    @Override
    public void add(byte[] vals) {
        this.add((byte[])vals, (int)0, (int)vals.length);
    }

    @Override
    public void add(byte[] vals, int offset, int length) {
        this.ensureCapacity((int)(this._pos + length));
        System.arraycopy((Object)vals, (int)offset, (Object)this._data, (int)this._pos, (int)length);
        this._pos += length;
    }

    @Override
    public void insert(int offset, byte value) {
        if (offset == this._pos) {
            this.add((byte)value);
            return;
        }
        this.ensureCapacity((int)(this._pos + 1));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + 1), (int)(this._pos - offset));
        this._data[offset] = value;
        ++this._pos;
    }

    @Override
    public void insert(int offset, byte[] values) {
        this.insert((int)offset, (byte[])values, (int)0, (int)values.length);
    }

    @Override
    public void insert(int offset, byte[] values, int valOffset, int len) {
        if (offset == this._pos) {
            this.add((byte[])values, (int)valOffset, (int)len);
            return;
        }
        this.ensureCapacity((int)(this._pos + len));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + len), (int)(this._pos - offset));
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)len);
        this._pos += len;
    }

    @Override
    public byte get(int offset) {
        if (offset < this._pos) return this._data[offset];
        throw new ArrayIndexOutOfBoundsException((int)offset);
    }

    public byte getQuick(int offset) {
        return this._data[offset];
    }

    @Override
    public byte set(int offset, byte val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        byte prev_val = this._data[offset];
        this._data[offset] = val;
        return prev_val;
    }

    @Override
    public byte replace(int offset, byte val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        byte old = this._data[offset];
        this._data[offset] = val;
        return old;
    }

    @Override
    public void set(int offset, byte[] values) {
        this.set((int)offset, (byte[])values, (int)0, (int)values.length);
    }

    @Override
    public void set(int offset, byte[] values, int valOffset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset + length > this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)length);
    }

    public void setQuick(int offset, byte val) {
        this._data[offset] = val;
    }

    @Override
    public void clear() {
        this.clearQuick();
        Arrays.fill((byte[])this._data, (byte)this.no_entry_value);
    }

    public void clearQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(byte value) {
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
    public byte removeAt(int offset) {
        byte old = this.get((int)offset);
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
    public TByteIterator iterator() {
        return new TByteArrayIterator((TByteArrayList)this, (int)0);
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
        if (this == collection) {
            return true;
        }
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
        byte[] arrby = array;
        int n = arrby.length;
        int n2 = 0;
        while (n2 < n) {
            byte element = arrby[n2];
            if (this.add((byte)element)) {
                changed = true;
            }
            ++n2;
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
        byte[] data = this._data;
        int i = this._pos;
        while (i-- > 0) {
            if (Arrays.binarySearch((byte[])array, (byte)data[i]) >= 0) continue;
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
            byte c;
            ? element = iterator.next();
            if (!(element instanceof Byte) || !this.remove((byte)(c = ((Byte)element).byteValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TByteCollection collection) {
        if (collection == this) {
            this.clear();
            return true;
        }
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
    public void transformValues(TByteFunction function) {
        int i = 0;
        while (i < this._pos) {
            this._data[i] = function.execute((byte)this._data[i]);
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
        byte tmp = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = tmp;
    }

    @Override
    public TByteList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException((String)("end index " + end + " greater than begin index " + begin));
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException((String)"begin index can not be < 0");
        }
        if (end > this._data.length) {
            throw new IndexOutOfBoundsException((String)("end index < " + this._data.length));
        }
        TByteArrayList list = new TByteArrayList((int)(end - begin));
        int i = begin;
        while (i < end) {
            list.add((byte)this._data[i]);
            ++i;
        }
        return list;
    }

    @Override
    public byte[] toArray() {
        return this.toArray((int)0, (int)this._pos);
    }

    @Override
    public byte[] toArray(int offset, int len) {
        byte[] rv = new byte[len];
        this.toArray((byte[])rv, (int)offset, (int)len);
        return rv;
    }

    @Override
    public byte[] toArray(byte[] dest) {
        int len = dest.length;
        if (dest.length > this._pos) {
            len = this._pos;
            dest[len] = this.no_entry_value;
        }
        this.toArray((byte[])dest, (int)0, (int)len);
        return dest;
    }

    @Override
    public byte[] toArray(byte[] dest, int offset, int len) {
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
    public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
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
        if (!(other instanceof TByteList)) {
            return false;
        }
        if (other instanceof TByteArrayList) {
            TByteArrayList that = (TByteArrayList)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = this._pos;
            do {
                if (i-- <= 0) return true;
            } while (this._data[i] == that._data[i]);
            return false;
        }
        TByteList that = (TByteList)other;
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
    public boolean forEach(TByteProcedure procedure) {
        int i = 0;
        while (i < this._pos) {
            if (!procedure.execute((byte)this._data[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TByteProcedure procedure) {
        int i = this._pos;
        do {
            if (i-- <= 0) return true;
        } while (procedure.execute((byte)this._data[i]));
        return false;
    }

    @Override
    public void sort() {
        Arrays.sort((byte[])this._data, (int)0, (int)this._pos);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        Arrays.sort((byte[])this._data, (int)fromIndex, (int)toIndex);
    }

    @Override
    public void fill(byte val) {
        Arrays.fill((byte[])this._data, (int)0, (int)this._pos, (byte)val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, byte val) {
        if (toIndex > this._pos) {
            this.ensureCapacity((int)toIndex);
            this._pos = toIndex;
        }
        Arrays.fill((byte[])this._data, (int)fromIndex, (int)toIndex, (byte)val);
    }

    @Override
    public int binarySearch(byte value) {
        return this.binarySearch((byte)value, (int)0, (int)this._pos);
    }

    @Override
    public int binarySearch(byte value, int fromIndex, int toIndex) {
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
            byte midVal = this._data[mid];
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
    public int indexOf(byte value) {
        return this.indexOf((int)0, (byte)value);
    }

    @Override
    public int indexOf(int offset, byte value) {
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
    public int lastIndexOf(byte value) {
        return this.lastIndexOf((int)this._pos, (byte)value);
    }

    @Override
    public int lastIndexOf(int offset, byte value) {
        int i = offset;
        do {
            if (i-- <= 0) return -1;
        } while (this._data[i] != value);
        return i;
    }

    @Override
    public boolean contains(byte value) {
        if (this.lastIndexOf((byte)value) < 0) return false;
        return true;
    }

    @Override
    public TByteList grep(TByteProcedure condition) {
        TByteArrayList list = new TByteArrayList();
        int i = 0;
        while (i < this._pos) {
            if (condition.execute((byte)this._data[i])) {
                list.add((byte)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public TByteList inverseGrep(TByteProcedure condition) {
        TByteArrayList list = new TByteArrayList();
        int i = 0;
        while (i < this._pos) {
            if (!condition.execute((byte)this._data[i])) {
                list.add((byte)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public byte max() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find maximum of an empty list");
        }
        byte max = -128;
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
    public byte min() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find minimum of an empty list");
        }
        byte min = 127;
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
    public byte sum() {
        byte sum = 0;
        int i = 0;
        while (i < this._pos) {
            sum = (byte)(sum + this._data[i]);
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
        out.writeByte((int)this.no_entry_value);
        int len = this._data.length;
        out.writeInt((int)len);
        int i = 0;
        while (i < len) {
            out.writeByte((int)this._data[i]);
            ++i;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._pos = in.readInt();
        this.no_entry_value = in.readByte();
        int len = in.readInt();
        this._data = new byte[len];
        int i = 0;
        while (i < len) {
            this._data[i] = in.readByte();
            ++i;
        }
    }
}

