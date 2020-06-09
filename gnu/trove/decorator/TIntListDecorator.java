/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.list.TIntList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TIntListDecorator
extends AbstractList<Integer>
implements List<Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TIntList list;

    public TIntListDecorator() {
    }

    public TIntListDecorator(TIntList list) {
        Objects.requireNonNull(list);
        this.list = list;
    }

    public TIntList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Integer get(int index) {
        int value = this.list.get((int)index);
        if (value != this.list.getNoEntryValue()) return Integer.valueOf((int)value);
        return null;
    }

    @Override
    public Integer set(int index, Integer value) {
        int previous_value = this.list.set((int)index, (int)value.intValue());
        if (previous_value != this.list.getNoEntryValue()) return Integer.valueOf((int)previous_value);
        return null;
    }

    @Override
    public void add(int index, Integer value) {
        this.list.insert((int)index, (int)value.intValue());
    }

    @Override
    public Integer remove(int index) {
        int previous_value = this.list.removeAt((int)index);
        if (previous_value != this.list.getNoEntryValue()) return Integer.valueOf((int)previous_value);
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TIntList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this.list);
    }
}

