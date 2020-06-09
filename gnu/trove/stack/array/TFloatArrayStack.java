/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.stack.array;

import gnu.trove.TFloatCollection;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.stack.TFloatStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TFloatArrayStack
implements TFloatStack,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TFloatArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TFloatArrayStack() {
        this((int)10);
    }

    public TFloatArrayStack(int capacity) {
        this._list = new TFloatArrayList((int)capacity);
    }

    public TFloatArrayStack(int capacity, float no_entry_value) {
        this._list = new TFloatArrayList((int)capacity, (float)no_entry_value);
    }

    public TFloatArrayStack(TFloatStack stack) {
        if (!(stack instanceof TFloatArrayStack)) throw new UnsupportedOperationException((String)"Only support TFloatArrayStack");
        TFloatArrayStack array_stack = (TFloatArrayStack)stack;
        this._list = new TFloatArrayList((TFloatCollection)array_stack._list);
    }

    @Override
    public float getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(float val) {
        this._list.add((float)val);
    }

    @Override
    public float pop() {
        return this._list.removeAt((int)(this._list.size() - 1));
    }

    @Override
    public float peek() {
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
    public float[] toArray() {
        float[] retval = this._list.toArray();
        this.reverse((float[])retval, (int)0, (int)this.size());
        return retval;
    }

    @Override
    public void toArray(float[] dest) {
        int size = this.size();
        int start = size - dest.length;
        if (start < 0) {
            start = 0;
        }
        int length = Math.min((int)size, (int)dest.length);
        this._list.toArray((float[])dest, (int)start, (int)length);
        this.reverse((float[])dest, (int)0, (int)length);
        if (dest.length <= size) return;
        dest[size] = this._list.getNoEntryValue();
    }

    private void reverse(float[] dest, int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException((String)"from cannot be greater than to");
        }
        int i = from;
        int j = to - 1;
        while (i < j) {
            this.swap((float[])dest, (int)i, (int)j);
            ++i;
            --j;
        }
    }

    private void swap(float[] dest, int i, int j) {
        float tmp = dest[i];
        dest[i] = dest[j];
        dest[j] = tmp;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        for (int i = this._list.size() - 1; i > 0; --i) {
            buf.append((float)this._list.get((int)i));
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((float)this._list.get((int)0));
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
        TFloatArrayStack that = (TFloatArrayStack)o;
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
        this._list = (TFloatArrayList)in.readObject();
    }
}

