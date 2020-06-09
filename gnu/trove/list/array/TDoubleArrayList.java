/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list.array;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class TDoubleArrayList
implements TDoubleList,
Externalizable {
    static final long serialVersionUID = 1L;
    protected static final int DEFAULT_CAPACITY = 10;
    protected double[] _data;
    protected int _pos;
    protected double no_entry_value;

    public TDoubleArrayList() {
        this((int)10, (double)0.0);
    }

    public TDoubleArrayList(int capacity) {
        this((int)capacity, (double)0.0);
    }

    public TDoubleArrayList(int capacity, double no_entry_value) {
        this._data = new double[capacity];
        this._pos = 0;
        this.no_entry_value = no_entry_value;
    }

    public TDoubleArrayList(TDoubleCollection collection) {
        this((int)collection.size());
        this.addAll((TDoubleCollection)collection);
    }

    public TDoubleArrayList(double[] values) {
        this((int)values.length);
        this.add((double[])values);
    }

    protected TDoubleArrayList(double[] values, double no_entry_value, boolean wrap) {
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

    public static TDoubleArrayList wrap(double[] values) {
        return TDoubleArrayList.wrap((double[])values, (double)0.0);
    }

    public static TDoubleArrayList wrap(double[] values, double no_entry_value) {
        return new TDoubleArrayList((double[])values, (double)no_entry_value, (boolean)true){

            public void ensureCapacity(int capacity) {
                if (capacity <= this._data.length) return;
                throw new IllegalStateException((String)"Can not grow ArrayList wrapped external array");
            }
        };
    }

    @Override
    public double getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this._data.length) return;
        int newCap = Math.max((int)(this._data.length << 1), (int)capacity);
        double[] tmp = new double[newCap];
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
        double[] tmp = new double[this.size()];
        this.toArray((double[])tmp, (int)0, (int)tmp.length);
        this._data = tmp;
    }

    @Override
    public boolean add(double val) {
        this.ensureCapacity((int)(this._pos + 1));
        this._data[this._pos++] = val;
        return true;
    }

    @Override
    public void add(double[] vals) {
        this.add((double[])vals, (int)0, (int)vals.length);
    }

    @Override
    public void add(double[] vals, int offset, int length) {
        this.ensureCapacity((int)(this._pos + length));
        System.arraycopy((Object)vals, (int)offset, (Object)this._data, (int)this._pos, (int)length);
        this._pos += length;
    }

    @Override
    public void insert(int offset, double value) {
        if (offset == this._pos) {
            this.add((double)value);
            return;
        }
        this.ensureCapacity((int)(this._pos + 1));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + 1), (int)(this._pos - offset));
        this._data[offset] = value;
        ++this._pos;
    }

    @Override
    public void insert(int offset, double[] values) {
        this.insert((int)offset, (double[])values, (int)0, (int)values.length);
    }

    @Override
    public void insert(int offset, double[] values, int valOffset, int len) {
        if (offset == this._pos) {
            this.add((double[])values, (int)valOffset, (int)len);
            return;
        }
        this.ensureCapacity((int)(this._pos + len));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + len), (int)(this._pos - offset));
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)len);
        this._pos += len;
    }

    @Override
    public double get(int offset) {
        if (offset < this._pos) return this._data[offset];
        throw new ArrayIndexOutOfBoundsException((int)offset);
    }

    public double getQuick(int offset) {
        return this._data[offset];
    }

    @Override
    public double set(int offset, double val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        double prev_val = this._data[offset];
        this._data[offset] = val;
        return prev_val;
    }

    @Override
    public double replace(int offset, double val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        double old = this._data[offset];
        this._data[offset] = val;
        return old;
    }

    @Override
    public void set(int offset, double[] values) {
        this.set((int)offset, (double[])values, (int)0, (int)values.length);
    }

    @Override
    public void set(int offset, double[] values, int valOffset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset + length > this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)length);
    }

    public void setQuick(int offset, double val) {
        this._data[offset] = val;
    }

    @Override
    public void clear() {
        this.clearQuick();
        Arrays.fill((double[])this._data, (double)this.no_entry_value);
    }

    public void clearQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(double value) {
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
    public double removeAt(int offset) {
        double old = this.get((int)offset);
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
    public TDoubleIterator iterator() {
        return new TDoubleArrayIterator((TDoubleArrayList)this, (int)0);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        ? element;
        double c;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Double)) return false;
        } while (this.contains((double)(c = ((Double)element).doubleValue())));
        return false;
    }

    @Override
    public boolean containsAll(TDoubleCollection collection) {
        double element;
        if (this == collection) {
            return true;
        }
        TDoubleIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((double)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(double[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((double)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Double> collection) {
        boolean changed = false;
        Iterator<? extends Double> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Double element = iterator.next();
            double e = element.doubleValue();
            if (!this.add((double)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TDoubleCollection collection) {
        boolean changed = false;
        TDoubleIterator iter = collection.iterator();
        while (iter.hasNext()) {
            double element = iter.next();
            if (!this.add((double)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(double[] array) {
        boolean changed = false;
        double[] arrd = array;
        int n = arrd.length;
        int n2 = 0;
        while (n2 < n) {
            double element = arrd[n2];
            if (this.add((double)element)) {
                changed = true;
            }
            ++n2;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TDoubleIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Double.valueOf((double)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TDoubleCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TDoubleIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((double)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(double[] array) {
        boolean changed = false;
        Arrays.sort((double[])array);
        double[] data = this._data;
        int i = this._pos;
        while (i-- > 0) {
            if (Arrays.binarySearch((double[])array, (double)data[i]) >= 0) continue;
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
            double c;
            ? element = iterator.next();
            if (!(element instanceof Double) || !this.remove((double)(c = ((Double)element).doubleValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TDoubleCollection collection) {
        if (collection == this) {
            this.clear();
            return true;
        }
        boolean changed = false;
        TDoubleIterator iter = collection.iterator();
        while (iter.hasNext()) {
            double element = iter.next();
            if (!this.remove((double)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(double[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((double)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        int i = 0;
        while (i < this._pos) {
            this._data[i] = function.execute((double)this._data[i]);
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
        double tmp = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = tmp;
    }

    @Override
    public TDoubleList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException((String)("end index " + end + " greater than begin index " + begin));
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException((String)"begin index can not be < 0");
        }
        if (end > this._data.length) {
            throw new IndexOutOfBoundsException((String)("end index < " + this._data.length));
        }
        TDoubleArrayList list = new TDoubleArrayList((int)(end - begin));
        int i = begin;
        while (i < end) {
            list.add((double)this._data[i]);
            ++i;
        }
        return list;
    }

    @Override
    public double[] toArray() {
        return this.toArray((int)0, (int)this._pos);
    }

    @Override
    public double[] toArray(int offset, int len) {
        double[] rv = new double[len];
        this.toArray((double[])rv, (int)offset, (int)len);
        return rv;
    }

    @Override
    public double[] toArray(double[] dest) {
        int len = dest.length;
        if (dest.length > this._pos) {
            len = this._pos;
            dest[len] = this.no_entry_value;
        }
        this.toArray((double[])dest, (int)0, (int)len);
        return dest;
    }

    @Override
    public double[] toArray(double[] dest, int offset, int len) {
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
    public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
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
        if (!(other instanceof TDoubleList)) {
            return false;
        }
        if (other instanceof TDoubleArrayList) {
            TDoubleArrayList that = (TDoubleArrayList)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = this._pos;
            do {
                if (i-- <= 0) return true;
            } while (this._data[i] == that._data[i]);
            return false;
        }
        TDoubleList that = (TDoubleList)other;
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
            h += HashFunctions.hash((double)this._data[i]);
        }
        return h;
    }

    @Override
    public boolean forEach(TDoubleProcedure procedure) {
        int i = 0;
        while (i < this._pos) {
            if (!procedure.execute((double)this._data[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TDoubleProcedure procedure) {
        int i = this._pos;
        do {
            if (i-- <= 0) return true;
        } while (procedure.execute((double)this._data[i]));
        return false;
    }

    @Override
    public void sort() {
        Arrays.sort((double[])this._data, (int)0, (int)this._pos);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        Arrays.sort((double[])this._data, (int)fromIndex, (int)toIndex);
    }

    @Override
    public void fill(double val) {
        Arrays.fill((double[])this._data, (int)0, (int)this._pos, (double)val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, double val) {
        if (toIndex > this._pos) {
            this.ensureCapacity((int)toIndex);
            this._pos = toIndex;
        }
        Arrays.fill((double[])this._data, (int)fromIndex, (int)toIndex, (double)val);
    }

    @Override
    public int binarySearch(double value) {
        return this.binarySearch((double)value, (int)0, (int)this._pos);
    }

    @Override
    public int binarySearch(double value, int fromIndex, int toIndex) {
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
            double midVal = this._data[mid];
            if (midVal < value) {
                low = mid + 1;
                continue;
            }
            if (!(midVal > value)) return mid;
            high = mid - 1;
        }
        return -(low + 1);
    }

    @Override
    public int indexOf(double value) {
        return this.indexOf((int)0, (double)value);
    }

    @Override
    public int indexOf(int offset, double value) {
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
    public int lastIndexOf(double value) {
        return this.lastIndexOf((int)this._pos, (double)value);
    }

    @Override
    public int lastIndexOf(int offset, double value) {
        int i = offset;
        do {
            if (i-- <= 0) return -1;
        } while (this._data[i] != value);
        return i;
    }

    @Override
    public boolean contains(double value) {
        if (this.lastIndexOf((double)value) < 0) return false;
        return true;
    }

    @Override
    public TDoubleList grep(TDoubleProcedure condition) {
        TDoubleArrayList list = new TDoubleArrayList();
        int i = 0;
        while (i < this._pos) {
            if (condition.execute((double)this._data[i])) {
                list.add((double)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public TDoubleList inverseGrep(TDoubleProcedure condition) {
        TDoubleArrayList list = new TDoubleArrayList();
        int i = 0;
        while (i < this._pos) {
            if (!condition.execute((double)this._data[i])) {
                list.add((double)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public double max() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find maximum of an empty list");
        }
        double max = Double.NEGATIVE_INFINITY;
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
    public double min() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find minimum of an empty list");
        }
        double min = Double.POSITIVE_INFINITY;
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
    public double sum() {
        double sum = 0.0;
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
            buf.append((double)this._data[i]);
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((double)this._data[this._pos - 1]);
        }
        buf.append((String)"}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeInt((int)this._pos);
        out.writeDouble((double)this.no_entry_value);
        int len = this._data.length;
        out.writeInt((int)len);
        int i = 0;
        while (i < len) {
            out.writeDouble((double)this._data[i]);
            ++i;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._pos = in.readInt();
        this.no_entry_value = in.readDouble();
        int len = in.readInt();
        this._data = new double[len];
        int i = 0;
        while (i < len) {
            this._data[i] = in.readDouble();
            ++i;
        }
    }
}

