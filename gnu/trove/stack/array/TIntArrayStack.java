/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.stack.array;

import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.stack.TIntStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TIntArrayStack
implements TIntStack,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TIntArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TIntArrayStack() {
        this((int)10);
    }

    public TIntArrayStack(int capacity) {
        this._list = new TIntArrayList((int)capacity);
    }

    public TIntArrayStack(int capacity, int no_entry_value) {
        this._list = new TIntArrayList((int)capacity, (int)no_entry_value);
    }

    public TIntArrayStack(TIntStack stack) {
        if (!(stack instanceof TIntArrayStack)) throw new UnsupportedOperationException((String)"Only support TIntArrayStack");
        TIntArrayStack array_stack = (TIntArrayStack)stack;
        this._list = new TIntArrayList((TIntCollection)array_stack._list);
    }

    @Override
    public int getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(int val) {
        this._list.add((int)val);
    }

    @Override
    public int pop() {
        return this._list.removeAt((int)(this._list.size() - 1));
    }

    @Override
    public int peek() {
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
    public int[] toArray() {
        int[] retval = this._list.toArray();
        this.reverse((int[])retval, (int)0, (int)this.size());
        return retval;
    }

    @Override
    public void toArray(int[] dest) {
        int size = this.size();
        int start = size - dest.length;
        if (start < 0) {
            start = 0;
        }
        int length = Math.min((int)size, (int)dest.length);
        this._list.toArray((int[])dest, (int)start, (int)length);
        this.reverse((int[])dest, (int)0, (int)length);
        if (dest.length <= size) return;
        dest[size] = this._list.getNoEntryValue();
    }

    private void reverse(int[] dest, int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException((String)"from cannot be greater than to");
        }
        int i = from;
        int j = to - 1;
        while (i < j) {
            this.swap((int[])dest, (int)i, (int)j);
            ++i;
            --j;
        }
    }

    private void swap(int[] dest, int i, int j) {
        int tmp = dest[i];
        dest[i] = dest[j];
        dest[j] = tmp;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        for (int i = this._list.size() - 1; i > 0; --i) {
            buf.append((int)this._list.get((int)i));
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((int)this._list.get((int)0));
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
        TIntArrayStack that = (TIntArrayStack)o;
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
        this._list = (TIntArrayList)in.readObject();
    }
}

