/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TDoubleSetDecorator;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TDoubleSetDecorator
extends AbstractSet<Double>
implements Set<Double>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TDoubleSet _set;

    public TDoubleSetDecorator() {
    }

    public TDoubleSetDecorator(TDoubleSet set) {
        Objects.requireNonNull(set);
        this._set = set;
    }

    public TDoubleSet getSet() {
        return this._set;
    }

    @Override
    public boolean add(Double value) {
        if (value == null) return false;
        if (!this._set.add((double)value.doubleValue())) return false;
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
            if (!(val instanceof Double)) return false;
            double v = ((Double)val).doubleValue();
            if (!this._set.contains((double)v)) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this._set.clear();
    }

    @Override
    public boolean remove(Object value) {
        if (!(value instanceof Double)) return false;
        if (!this._set.remove((double)((Double)value).doubleValue())) return false;
        return true;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>((TDoubleSetDecorator)this){
            private final gnu.trove.iterator.TDoubleIterator it;
            final /* synthetic */ TDoubleSetDecorator this$0;
            {
                this.this$0 = this$0;
                this.it = this.this$0._set.iterator();
            }

            public Double next() {
                return Double.valueOf((double)this.it.next());
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
        if (o instanceof Double) return this._set.contains((double)((Double)o).doubleValue());
        return false;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._set = (TDoubleSet)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._set);
    }
}

