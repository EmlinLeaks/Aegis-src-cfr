/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list.array;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.procedure.TFloatProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class TFloatArrayList
implements TFloatList,
Externalizable {
    static final long serialVersionUID = 1L;
    protected static final int DEFAULT_CAPACITY = 10;
    protected float[] _data;
    protected int _pos;
    protected float no_entry_value;

    public TFloatArrayList() {
        this((int)10, (float)0.0f);
    }

    public TFloatArrayList(int capacity) {
        this((int)capacity, (float)0.0f);
    }

    public TFloatArrayList(int capacity, float no_entry_value) {
        this._data = new float[capacity];
        this._pos = 0;
        this.no_entry_value = no_entry_value;
    }

    public TFloatArrayList(TFloatCollection collection) {
        this((int)collection.size());
        this.addAll((TFloatCollection)collection);
    }

    public TFloatArrayList(float[] values) {
        this((int)values.length);
        this.add((float[])values);
    }

    protected TFloatArrayList(float[] values, float no_entry_value, boolean wrap) {
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

    public static TFloatArrayList wrap(float[] values) {
        return TFloatArrayList.wrap((float[])values, (float)0.0f);
    }

    public static TFloatArrayList wrap(float[] values, float no_entry_value) {
        return new TFloatArrayList((float[])values, (float)no_entry_value, (boolean)true){

            public void ensureCapacity(int capacity) {
                if (capacity <= this._data.length) return;
                throw new IllegalStateException((String)"Can not grow ArrayList wrapped external array");
            }
        };
    }

    @Override
    public float getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this._data.length) return;
        int newCap = Math.max((int)(this._data.length << 1), (int)capacity);
        float[] tmp = new float[newCap];
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
        float[] tmp = new float[this.size()];
        this.toArray((float[])tmp, (int)0, (int)tmp.length);
        this._data = tmp;
    }

    @Override
    public boolean add(float val) {
        this.ensureCapacity((int)(this._pos + 1));
        this._data[this._pos++] = val;
        return true;
    }

    @Override
    public void add(float[] vals) {
        this.add((float[])vals, (int)0, (int)vals.length);
    }

    @Override
    public void add(float[] vals, int offset, int length) {
        this.ensureCapacity((int)(this._pos + length));
        System.arraycopy((Object)vals, (int)offset, (Object)this._data, (int)this._pos, (int)length);
        this._pos += length;
    }

    @Override
    public void insert(int offset, float value) {
        if (offset == this._pos) {
            this.add((float)value);
            return;
        }
        this.ensureCapacity((int)(this._pos + 1));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + 1), (int)(this._pos - offset));
        this._data[offset] = value;
        ++this._pos;
    }

    @Override
    public void insert(int offset, float[] values) {
        this.insert((int)offset, (float[])values, (int)0, (int)values.length);
    }

    @Override
    public void insert(int offset, float[] values, int valOffset, int len) {
        if (offset == this._pos) {
            this.add((float[])values, (int)valOffset, (int)len);
            return;
        }
        this.ensureCapacity((int)(this._pos + len));
        System.arraycopy((Object)this._data, (int)offset, (Object)this._data, (int)(offset + len), (int)(this._pos - offset));
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)len);
        this._pos += len;
    }

    @Override
    public float get(int offset) {
        if (offset < this._pos) return this._data[offset];
        throw new ArrayIndexOutOfBoundsException((int)offset);
    }

    public float getQuick(int offset) {
        return this._data[offset];
    }

    @Override
    public float set(int offset, float val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        float prev_val = this._data[offset];
        this._data[offset] = val;
        return prev_val;
    }

    @Override
    public float replace(int offset, float val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        float old = this._data[offset];
        this._data[offset] = val;
        return old;
    }

    @Override
    public void set(int offset, float[] values) {
        this.set((int)offset, (float[])values, (int)0, (int)values.length);
    }

    @Override
    public void set(int offset, float[] values, int valOffset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException((int)offset);
        if (offset + length > this._pos) {
            throw new ArrayIndexOutOfBoundsException((int)offset);
        }
        System.arraycopy((Object)values, (int)valOffset, (Object)this._data, (int)offset, (int)length);
    }

    public void setQuick(int offset, float val) {
        this._data[offset] = val;
    }

    @Override
    public void clear() {
        this.clearQuick();
        Arrays.fill((float[])this._data, (float)this.no_entry_value);
    }

    public void clearQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(float value) {
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
    public float removeAt(int offset) {
        float old = this.get((int)offset);
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
    public TFloatIterator iterator() {
        return new TFloatArrayIterator((TFloatArrayList)this, (int)0);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        ? element;
        float c;
        Iterator<?> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return true;
            element = iterator.next();
            if (!(element instanceof Float)) return false;
        } while (this.contains((float)(c = ((Float)element).floatValue())));
        return false;
    }

    @Override
    public boolean containsAll(TFloatCollection collection) {
        float element;
        if (this == collection) {
            return true;
        }
        TFloatIterator iter = collection.iterator();
        do {
            if (!iter.hasNext()) return true;
        } while (this.contains((float)(element = iter.next())));
        return false;
    }

    @Override
    public boolean containsAll(float[] array) {
        int i = array.length;
        do {
            if (i-- <= 0) return true;
        } while (this.contains((float)array[i]));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Float> collection) {
        boolean changed = false;
        Iterator<? extends Float> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Float element = iterator.next();
            float e = element.floatValue();
            if (!this.add((float)e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TFloatCollection collection) {
        boolean changed = false;
        TFloatIterator iter = collection.iterator();
        while (iter.hasNext()) {
            float element = iter.next();
            if (!this.add((float)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(float[] array) {
        boolean changed = false;
        float[] arrf = array;
        int n = arrf.length;
        int n2 = 0;
        while (n2 < n) {
            float element = arrf[n2];
            if (this.add((float)element)) {
                changed = true;
            }
            ++n2;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        TFloatIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((Object)Float.valueOf((float)iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(TFloatCollection collection) {
        if (this == collection) {
            return false;
        }
        boolean modified = false;
        TFloatIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (collection.contains((float)iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(float[] array) {
        boolean changed = false;
        Arrays.sort((float[])array);
        float[] data = this._data;
        int i = this._pos;
        while (i-- > 0) {
            if (Arrays.binarySearch((float[])array, (float)data[i]) >= 0) continue;
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
            float c;
            ? element = iterator.next();
            if (!(element instanceof Float) || !this.remove((float)(c = ((Float)element).floatValue()))) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(TFloatCollection collection) {
        if (collection == this) {
            this.clear();
            return true;
        }
        boolean changed = false;
        TFloatIterator iter = collection.iterator();
        while (iter.hasNext()) {
            float element = iter.next();
            if (!this.remove((float)element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(float[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.remove((float)array[i])) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void transformValues(TFloatFunction function) {
        int i = 0;
        while (i < this._pos) {
            this._data[i] = function.execute((float)this._data[i]);
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
        float tmp = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = tmp;
    }

    @Override
    public TFloatList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException((String)("end index " + end + " greater than begin index " + begin));
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException((String)"begin index can not be < 0");
        }
        if (end > this._data.length) {
            throw new IndexOutOfBoundsException((String)("end index < " + this._data.length));
        }
        TFloatArrayList list = new TFloatArrayList((int)(end - begin));
        int i = begin;
        while (i < end) {
            list.add((float)this._data[i]);
            ++i;
        }
        return list;
    }

    @Override
    public float[] toArray() {
        return this.toArray((int)0, (int)this._pos);
    }

    @Override
    public float[] toArray(int offset, int len) {
        float[] rv = new float[len];
        this.toArray((float[])rv, (int)offset, (int)len);
        return rv;
    }

    @Override
    public float[] toArray(float[] dest) {
        int len = dest.length;
        if (dest.length > this._pos) {
            len = this._pos;
            dest[len] = this.no_entry_value;
        }
        this.toArray((float[])dest, (int)0, (int)len);
        return dest;
    }

    @Override
    public float[] toArray(float[] dest, int offset, int len) {
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
    public float[] toArray(float[] dest, int source_pos, int dest_pos, int len) {
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
        if (!(other instanceof TFloatList)) {
            return false;
        }
        if (other instanceof TFloatArrayList) {
            TFloatArrayList that = (TFloatArrayList)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = this._pos;
            do {
                if (i-- <= 0) return true;
            } while (this._data[i] == that._data[i]);
            return false;
        }
        TFloatList that = (TFloatList)other;
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
            h += HashFunctions.hash((float)this._data[i]);
        }
        return h;
    }

    @Override
    public boolean forEach(TFloatProcedure procedure) {
        int i = 0;
        while (i < this._pos) {
            if (!procedure.execute((float)this._data[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TFloatProcedure procedure) {
        int i = this._pos;
        do {
            if (i-- <= 0) return true;
        } while (procedure.execute((float)this._data[i]));
        return false;
    }

    @Override
    public void sort() {
        Arrays.sort((float[])this._data, (int)0, (int)this._pos);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        Arrays.sort((float[])this._data, (int)fromIndex, (int)toIndex);
    }

    @Override
    public void fill(float val) {
        Arrays.fill((float[])this._data, (int)0, (int)this._pos, (float)val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, float val) {
        if (toIndex > this._pos) {
            this.ensureCapacity((int)toIndex);
            this._pos = toIndex;
        }
        Arrays.fill((float[])this._data, (int)fromIndex, (int)toIndex, (float)val);
    }

    @Override
    public int binarySearch(float value) {
        return this.binarySearch((float)value, (int)0, (int)this._pos);
    }

    @Override
    public int binarySearch(float value, int fromIndex, int toIndex) {
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
            float midVal = this._data[mid];
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
    public int indexOf(float value) {
        return this.indexOf((int)0, (float)value);
    }

    @Override
    public int indexOf(int offset, float value) {
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
    public int lastIndexOf(float value) {
        return this.lastIndexOf((int)this._pos, (float)value);
    }

    @Override
    public int lastIndexOf(int offset, float value) {
        int i = offset;
        do {
            if (i-- <= 0) return -1;
        } while (this._data[i] != value);
        return i;
    }

    @Override
    public boolean contains(float value) {
        if (this.lastIndexOf((float)value) < 0) return false;
        return true;
    }

    @Override
    public TFloatList grep(TFloatProcedure condition) {
        TFloatArrayList list = new TFloatArrayList();
        int i = 0;
        while (i < this._pos) {
            if (condition.execute((float)this._data[i])) {
                list.add((float)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public TFloatList inverseGrep(TFloatProcedure condition) {
        TFloatArrayList list = new TFloatArrayList();
        int i = 0;
        while (i < this._pos) {
            if (!condition.execute((float)this._data[i])) {
                list.add((float)this._data[i]);
            }
            ++i;
        }
        return list;
    }

    @Override
    public float max() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find maximum of an empty list");
        }
        float max = Float.NEGATIVE_INFINITY;
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
    public float min() {
        if (this.size() == 0) {
            throw new IllegalStateException((String)"cannot find minimum of an empty list");
        }
        float min = Float.POSITIVE_INFINITY;
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
    public float sum() {
        float sum = 0.0f;
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
            buf.append((float)this._data[i]);
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((float)this._data[this._pos - 1]);
        }
        buf.append((String)"}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeInt((int)this._pos);
        out.writeFloat((float)this.no_entry_value);
        int len = this._data.length;
        out.writeInt((int)len);
        int i = 0;
        while (i < len) {
            out.writeFloat((float)this._data[i]);
            ++i;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._pos = in.readInt();
        this.no_entry_value = in.readFloat();
        int len = in.readInt();
        this._data = new float[len];
        int i = 0;
        while (i < len) {
            this._data[i] = in.readFloat();
            ++i;
        }
    }
}

