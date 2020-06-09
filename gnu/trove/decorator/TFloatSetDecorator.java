/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TFloatSetDecorator;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TFloatSetDecorator
extends AbstractSet<Float>
implements Set<Float>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TFloatSet _set;

    public TFloatSetDecorator() {
    }

    public TFloatSetDecorator(TFloatSet set) {
        Objects.requireNonNull(set);
        this._set = set;
    }

    public TFloatSet getSet() {
        return this._set;
    }

    @Override
    public boolean add(Float value) {
        if (value == null) return false;
        if (!this._set.add((float)value.floatValue())) return false;
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
            if (!(val instanceof Float)) return false;
            float v = ((Float)val).floatValue();
            if (!this._set.contains((float)v)) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this._set.clear();
    }

    @Override
    public boolean remove(Object value) {
        if (!(value instanceof Float)) return false;
        if (!this._set.remove((float)((Float)value).floatValue())) return false;
        return true;
    }

    @Override
    public Iterator<Float> iterator() {
        return new Iterator<Float>((TFloatSetDecorator)this){
            private final gnu.trove.iterator.TFloatIterator it;
            final /* synthetic */ TFloatSetDecorator this$0;
            {
                this.this$0 = this$0;
                this.it = this.this$0._set.iterator();
            }

            public Float next() {
                return Float.valueOf((float)this.it.next());
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
        if (o instanceof Float) return this._set.contains((float)((Float)o).floatValue());
        return false;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._set = (TFloatSet)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._set);
    }
}

