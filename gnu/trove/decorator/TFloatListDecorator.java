/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.list.TFloatList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TFloatListDecorator
extends AbstractList<Float>
implements List<Float>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatList list;

    public TFloatListDecorator() {
    }

    public TFloatListDecorator(TFloatList list) {
        Objects.requireNonNull(list);
        this.list = list;
    }

    public TFloatList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Float get(int index) {
        float value = this.list.get((int)index);
        if (value != this.list.getNoEntryValue()) return Float.valueOf((float)value);
        return null;
    }

    @Override
    public Float set(int index, Float value) {
        float previous_value = this.list.set((int)index, (float)value.floatValue());
        if (previous_value != this.list.getNoEntryValue()) return Float.valueOf((float)previous_value);
        return null;
    }

    @Override
    public void add(int index, Float value) {
        this.list.insert((int)index, (float)value.floatValue());
    }

    @Override
    public Float remove(int index) {
        float previous_value = this.list.removeAt((int)index);
        if (previous_value != this.list.getNoEntryValue()) return Float.valueOf((float)previous_value);
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TFloatList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this.list);
    }
}

