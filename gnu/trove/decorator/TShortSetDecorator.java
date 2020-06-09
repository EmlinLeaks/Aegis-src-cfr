/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TShortSetDecorator;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TShortSetDecorator
extends AbstractSet<Short>
implements Set<Short>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TShortSet _set;

    public TShortSetDecorator() {
    }

    public TShortSetDecorator(TShortSet set) {
        Objects.requireNonNull(set);
        this._set = set;
    }

    public TShortSet getSet() {
        return this._set;
    }

    @Override
    public boolean add(Short value) {
        if (value == null) return false;
        if (!this._set.add((short)value.shortValue())) return false;
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (this._set.equals((Object)other)) {
            return true;
        }
        if (!(other instanceof Set)) return false;
        Set that = (Set)other;
        if (that.size() != this._set.size()) {
            return false;
        }
        Iterator<E> it = that.iterator();
        int i = that.size();
        while (i-- > 0) {
            E val = it.next();
            if (!(val instanceof Short)) return false;
            short v = ((Short)val).shortValue();
            if (!this._set.contains((short)v)) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this._set.clear();
    }

    @Override
    public boolean remove(Object value) {
        if (!(value instanceof Short)) return false;
        if (!this._set.remove((short)((Short)value).shortValue())) return false;
        return true;
    }

    @Override
    public Iterator<Short> iterator() {
        return new Iterator<Short>((TShortSetDecorator)this){
            private final gnu.trove.iterator.TShortIterator it;
            final /* synthetic */ TShortSetDecorator this$0;
            {
                this.this$0 = this$0;
                this.it = this.this$0._set.iterator();
            }

            public Short next() {
                return Short.valueOf((short)this.it.next());
            }

            public boolean hasNext() {
                return this.it.hasNext();
            }

            public void remove() {
                this.it.remove();
            }
        };
    }

    @Override
    public int size() {
        return this._set.size();
    }

    @Override
    public boolean isEmpty() {
        if (this._set.size() != 0) return false;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Short) return this._set.contains((short)((Short)o).shortValue());
        return false;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._set = (TShortSet)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._set);
    }
}

