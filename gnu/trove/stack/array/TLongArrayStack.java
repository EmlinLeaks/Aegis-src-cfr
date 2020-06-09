/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.stack.array;

import gnu.trove.TLongCollection;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.stack.TLongStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TLongArrayStack
implements TLongStack,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TLongArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TLongArrayStack() {
        this((int)10);
    }

    public TLongArrayStack(int capacity) {
        this._list = new TLongArrayList((int)capacity);
    }

    public TLongArrayStack(int capacity, long no_entry_value) {
        this._list = new TLongArrayList((int)capacity, (long)no_entry_value);
    }

    public TLongArrayStack(TLongStack stack) {
        if (!(stack instanceof TLongArrayStack)) throw new UnsupportedOperationException((String)"Only support TLongArrayStack");
        TLongArrayStack array_stack = (TLongArrayStack)stack;
        this._list = new TLongArrayList((TLongCollection)array_stack._list);
    }

    @Override
    public long getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(long val) {
        this._list.add((long)val);
    }

    @Override
    public long pop() {
        return this._list.removeAt((int)(this._list.size() - 1));
    }

    @Override
    public long peek() {
        return this._list.get((int)(this._list.size() - 1));
    }

    @Override
    public int size() {
        return this._list.size();
    }

    @Override
    public void clear() {
        this._list.clear();
    }

    @Override
    public long[] toArray() {
        long[] retval = this._list.toArray();
        this.reverse((long[])retval, (int)0, (int)this.size());
        return retval;
    }

    @Override
    public void toArray(long[] dest) {
        int size = this.size();
        int start = size - dest.length;
        if (start < 0) {
            start = 0;
        }
        int length = Math.min((int)size, (int)dest.length);
        this._list.toArray((long[])dest, (int)start, (int)length);
        this.reverse((long[])dest, (int)0, (int)length);
        if (dest.length <= size) return;
        dest[size] = this._list.getNoEntryValue();
    }

    private void reverse(long[] dest, int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException((String)"from cannot be greater than to");
        }
        int i = from;
        int j = to - 1;
        while (i < j) {
            this.swap((long[])dest, (int)i, (int)j);
            ++i;
            --j;
        }
    }

    private void swap(long[] dest, int i, int j) {
        long tmp = dest[i];
        dest[i] = dest[j];
        dest[j] = tmp;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        for (int i = this._list.size() - 1; i > 0; --i) {
            buf.append((long)this._list.get((int)i));
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((long)this._list.get((int)0));
        }
        buf.append((String)"}");
        return buf.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) return false;
        if (this.getClass() != o.getClass()) {
            return false;
        }
        TLongArrayStack that = (TLongArrayStack)o;
        return this._list.equals((Object)that._list);
    }

    public int hashCode() {
        return this._list.hashCode();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._list);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._list = (TLongArrayList)in.readObject();
    }
}

