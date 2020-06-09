/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TIntSetDecorator;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TIntSetDecorator
extends AbstractSet<Integer>
implements Set<Integer>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TIntSet _set;

    public TIntSetDecorator() {
    }

    public TIntSetDecorator(TIntSet set) {
        Objects.requireNonNull(set);
        this._set = set;
    }

    public TIntSet getSet() {
        return this._set;
    }

    @Override
    public boolean add(Integer value) {
        if (value == null) return false;
        if (!this._set.add((int)value.intValue())) return false;
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
            if (!(val instanceof Integer)) return false;
            int v = ((Integer)val).intValue();
            if (!this._set.contains((int)v)) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this._set.clear();
    }

    @Override
    public boolean remove(Object value) {
        if (!(value instanceof Integer)) return false;
        if (!this._set.remove((int)((Integer)value).intValue())) return false;
        return true;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>((TIntSetDecorator)this){
            private final gnu.trove.iterator.TIntIterator it;
            final /* synthetic */ TIntSetDecorator this$0;
            {
                this.this$0 = this$0;
                this.it = this.this$0._set.iterator();
            }

            public Integer next() {
                return Integer.valueOf((int)this.it.next());
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
        if (o instanceof Integer) return this._set.contains((int)((Integer)o).intValue());
        return false;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._set = (TIntSet)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._set);
    }
}

