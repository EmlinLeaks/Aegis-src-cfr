/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.list.TCharList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TCharListDecorator
extends AbstractList<Character>
implements List<Character>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharList list;

    public TCharListDecorator() {
    }

    public TCharListDecorator(TCharList list) {
        Objects.requireNonNull(list);
        this.list = list;
    }

    public TCharList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Character get(int index) {
        char value = this.list.get((int)index);
        if (value != this.list.getNoEntryValue()) return Character.valueOf((char)value);
        return null;
    }

    @Override
    public Character set(int index, Character value) {
        char previous_value = this.list.set((int)index, (char)value.charValue());
        if (previous_value != this.list.getNoEntryValue()) return Character.valueOf((char)previous_value);
        return null;
    }

    @Override
    public void add(int index, Character value) {
        this.list.insert((int)index, (char)value.charValue());
    }

    @Override
    public Character remove(int index) {
        char previous_value = this.list.removeAt((int)index);
        if (previous_value != this.list.getNoEntryValue()) return Character.valueOf((char)previous_value);
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TCharList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this.list);
    }
}

