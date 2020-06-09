/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.list.TByteList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TByteListDecorator
extends AbstractList<Byte>
implements List<Byte>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TByteList list;

    public TByteListDecorator() {
    }

    public TByteListDecorator(TByteList list) {
        Objects.requireNonNull(list);
        this.list = list;
    }

    public TByteList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Byte get(int index) {
        byte value = this.list.get((int)index);
        if (value != this.list.getNoEntryValue()) return Byte.valueOf((byte)value);
        return null;
    }

    @Override
    public Byte set(int index, Byte value) {
        byte previous_value = this.list.set((int)index, (byte)value.byteValue());
        if (previous_value != this.list.getNoEntryValue()) return Byte.valueOf((byte)previous_value);
        return null;
    }

    @Override
    public void add(int index, Byte value) {
        this.list.insert((int)index, (byte)value.byteValue());
    }

    @Override
    public Byte remove(int index) {
        byte previous_value = this.list.removeAt((int)index);
        if (previous_value != this.list.getNoEntryValue()) return Byte.valueOf((byte)previous_value);
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TByteList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this.list);
    }
}

