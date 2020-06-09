/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.stack.array;

import gnu.trove.TByteCollection;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.stack.TByteStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TByteArrayStack
implements TByteStack,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TByteArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TByteArrayStack() {
        this((int)10);
    }

    public TByteArrayStack(int capacity) {
        this._list = new TByteArrayList((int)capacity);
    }

    public TByteArrayStack(int capacity, byte no_entry_value) {
        this._list = new TByteArrayList((int)capacity, (byte)no_entry_value);
    }

    public TByteArrayStack(TByteStack stack) {
        if (!(stack instanceof TByteArrayStack)) throw new UnsupportedOperationException((String)"Only support TByteArrayStack");
        TByteArrayStack array_stack = (TByteArrayStack)stack;
        this._list = new TByteArrayList((TByteCollection)array_stack._list);
    }

    @Override
    public byte getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(byte val) {
        this._list.add((byte)val);
    }

    @Override
    public byte pop() {
        return this._list.removeAt((int)(this._list.size() - 1));
    }

    @Override
    public byte peek() {
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
    public byte[] toArray() {
        byte[] retval = this._list.toArray();
        this.reverse((byte[])retval, (int)0, (int)this.size());
        return retval;
    }

    @Override
    public void toArray(byte[] dest) {
        int size = this.size();
        int start = size - dest.length;
        if (start < 0) {
            start = 0;
        }
        int length = Math.min((int)size, (int)dest.length);
        this._list.toArray((byte[])dest, (int)start, (int)length);
        this.reverse((byte[])dest, (int)0, (int)length);
        if (dest.length <= size) return;
        dest[size] = this._list.getNoEntryValue();
    }

    private void reverse(byte[] dest, int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException((String)"from cannot be greater than to");
        }
        int i = from;
        int j = to - 1;
        while (i < j) {
            this.swap((byte[])dest, (int)i, (int)j);
            ++i;
            --j;
        }
    }

    private void swap(byte[] dest, int i, int j) {
        byte tmp = dest[i];
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
        TByteArrayStack that = (TByteArrayStack)o;
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
        this._list = (TByteArrayList)in.readObject();
    }
}

