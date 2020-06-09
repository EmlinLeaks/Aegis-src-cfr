/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.list.TDoubleList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TDoubleListDecorator
extends AbstractList<Double>
implements List<Double>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TDoubleList list;

    public TDoubleListDecorator() {
    }

    public TDoubleListDecorator(TDoubleList list) {
        Objects.requireNonNull(list);
        this.list = list;
    }

    public TDoubleList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Double get(int index) {
        double value = this.list.get((int)index);
        if (value != this.list.getNoEntryValue()) return Double.valueOf((double)value);
        return null;
    }

    @Override
    public Double set(int index, Double value) {
        double previous_value = this.list.set((int)index, (double)value.doubleValue());
        if (previous_value != this.list.getNoEntryValue()) return Double.valueOf((double)previous_value);
        return null;
    }

    @Override
    public void add(int index, Double value) {
        this.list.insert((int)index, (double)value.doubleValue());
    }

    @Override
    public Double remove(int index) {
        double previous_value = this.list.removeAt((int)index);
        if (previous_value != this.list.getNoEntryValue()) return Double.valueOf((double)previous_value);
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TDoubleList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this.list);
    }
}

