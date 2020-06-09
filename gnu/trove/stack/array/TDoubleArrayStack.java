/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.stack.array;

import gnu.trove.TDoubleCollection;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.stack.TDoubleStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TDoubleArrayStack
implements TDoubleStack,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TDoubleArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TDoubleArrayStack() {
        this((int)10);
    }

    public TDoubleArrayStack(int capacity) {
        this._list = new TDoubleArrayList((int)capacity);
    }

    public TDoubleArrayStack(int capacity, double no_entry_value) {
        this._list = new TDoubleArrayList((int)capacity, (double)no_entry_value);
    }

    public TDoubleArrayStack(TDoubleStack stack) {
        if (!(stack instanceof TDoubleArrayStack)) throw new UnsupportedOperationException((String)"Only support TDoubleArrayStack");
        TDoubleArrayStack array_stack = (TDoubleArrayStack)stack;
        this._list = new TDoubleArrayList((TDoubleCollection)array_stack._list);
    }

    @Override
    public double getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(double val) {
        this._list.add((double)val);
    }

    @Override
    public double pop() {
        return this._list.removeAt((int)(this._list.size() - 1));
    }

    @Override
    public double peek() {
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
    public double[] toArray() {
        double[] retval = this._list.toArray();
        this.reverse((double[])retval, (int)0, (int)this.size());
        return retval;
    }

    @Override
    public void toArray(double[] dest) {
        int size = this.size();
        int start = size - dest.length;
        if (start < 0) {
            start = 0;
        }
        int length = Math.min((int)size, (int)dest.length);
        this._list.toArray((double[])dest, (int)start, (int)length);
        this.reverse((double[])dest, (int)0, (int)length);
        if (dest.length <= size) return;
        dest[size] = this._list.getNoEntryValue();
    }

    private void reverse(double[] dest, int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException((String)"from cannot be greater than to");
        }
        int i = from;
        int j = to - 1;
        while (i < j) {
            this.swap((double[])dest, (int)i, (int)j);
            ++i;
            --j;
        }
    }

    private void swap(double[] dest, int i, int j) {
        double tmp = dest[i];
        dest[i] = dest[j];
        dest[j] = tmp;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        for (int i = this._list.size() - 1; i > 0; --i) {
            buf.append((double)this._list.get((int)i));
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((double)this._list.get((int)0));
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
        TDoubleArrayStack that = (TDoubleArrayStack)o;
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
        this._list = (TDoubleArrayList)in.readObject();
    }
}

