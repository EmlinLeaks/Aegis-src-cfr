/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.list.TLongList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TLongListDecorator
extends AbstractList<Long>
implements List<Long>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TLongList list;

    public TLongListDecorator() {
    }

    public TLongListDecorator(TLongList list) {
        Objects.requireNonNull(list);
        this.list = list;
    }

    public TLongList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Long get(int index) {
        long value = this.list.get((int)index);
        if (value != this.list.getNoEntryValue()) return Long.valueOf((long)value);
        return null;
    }

    @Override
    public Long set(int index, Long value) {
        long previous_value = this.list.set((int)index, (long)value.longValue());
        if (previous_value != this.list.getNoEntryValue()) return Long.valueOf((long)previous_value);
        return null;
    }

    @Override
    public void add(int index, Long value) {
        this.list.insert((int)index, (long)value.longValue());
    }

    @Override
    public Long remove(int index) {
        long previous_value = this.list.removeAt((int)index);
        if (previous_value != this.list.getNoEntryValue()) return Long.valueOf((long)previous_value);
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TLongList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this.list);
    }
}

