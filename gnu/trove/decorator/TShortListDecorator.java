/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.list.TShortList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TShortListDecorator
extends AbstractList<Short>
implements List<Short>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TShortList list;

    public TShortListDecorator() {
    }

    public TShortListDecorator(TShortList list) {
        Objects.requireNonNull(list);
        this.list = list;
    }

    public TShortList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Short get(int index) {
        short value = this.list.get((int)index);
        if (value != this.list.getNoEntryValue()) return Short.valueOf((short)value);
        return null;
    }

    @Override
    public Short set(int index, Short value) {
        short previous_value = this.list.set((int)index, (short)value.shortValue());
        if (previous_value != this.list.getNoEntryValue()) return Short.valueOf((short)previous_value);
        return null;
    }

    @Override
    public void add(int index, Short value) {
        this.list.insert((int)index, (short)value.shortValue());
    }

    @Override
    public Short remove(int index) {
        short previous_value = this.list.removeAt((int)index);
        if (previous_value != this.list.getNoEntryValue()) return Short.valueOf((short)previous_value);
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TShortList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this.list);
    }
}

