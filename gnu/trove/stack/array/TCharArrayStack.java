/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.stack.array;

import gnu.trove.TCharCollection;
import gnu.trove.list.array.TCharArrayList;
import gnu.trove.stack.TCharStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TCharArrayStack
implements TCharStack,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TCharArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TCharArrayStack() {
        this((int)10);
    }

    public TCharArrayStack(int capacity) {
        this._list = new TCharArrayList((int)capacity);
    }

    public TCharArrayStack(int capacity, char no_entry_value) {
        this._list = new TCharArrayList((int)capacity, (char)no_entry_value);
    }

    public TCharArrayStack(TCharStack stack) {
        if (!(stack instanceof TCharArrayStack)) throw new UnsupportedOperationException((String)"Only support TCharArrayStack");
        TCharArrayStack array_stack = (TCharArrayStack)stack;
        this._list = new TCharArrayList((TCharCollection)array_stack._list);
    }

    @Override
    public char getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(char val) {
        this._list.add((char)val);
    }

    @Override
    public char pop() {
        return this._list.removeAt((int)(this._list.size() - 1));
    }

    @Override
    public char peek() {
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
    public char[] toArray() {
        char[] retval = this._list.toArray();
        this.reverse((char[])retval, (int)0, (int)this.size());
        return retval;
    }

    @Override
    public void toArray(char[] dest) {
        int size = this.size();
        int start = size - dest.length;
        if (start < 0) {
            start = 0;
        }
        int length = Math.min((int)size, (int)dest.length);
        this._list.toArray((char[])dest, (int)start, (int)length);
        this.reverse((char[])dest, (int)0, (int)length);
        if (dest.length <= size) return;
        dest[size] = this._list.getNoEntryValue();
    }

    private void reverse(char[] dest, int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException((String)"from cannot be greater than to");
        }
        int i = from;
        int j = to - 1;
        while (i < j) {
            this.swap((char[])dest, (int)i, (int)j);
            ++i;
            --j;
        }
    }

    private void swap(char[] dest, int i, int j) {
        char tmp = dest[i];
        dest[i] = dest[j];
        dest[j] = tmp;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        for (int i = this._list.size() - 1; i > 0; --i) {
            buf.append((char)this._list.get((int)i));
            buf.append((String)", ");
        }
        if (this.size() > 0) {
            buf.append((char)this._list.get((int)0));
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
        TCharArrayStack that = (TCharArrayStack)o;
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
        this._list = (TCharArrayList)in.readObject();
    }
}

