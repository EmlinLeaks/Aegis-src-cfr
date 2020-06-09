/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TLongSetDecorator;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TLongSetDecorator
extends AbstractSet<Long>
implements Set<Long>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TLongSet _set;

    public TLongSetDecorator() {
    }

    public TLongSetDecorator(TLongSet set) {
        Objects.requireNonNull(set);
        this._set = set;
    }

    public TLongSet getSet() {
        return this._set;
    }

    @Override
    public boolean add(Long value) {
        if (value == null) return false;
        if (!this._set.add((long)value.longValue())) return false;
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
            if (!(val instanceof Long)) return false;
            long v = ((Long)val).longValue();
            if (!this._set.contains((long)v)) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this._set.clear();
    }

    @Override
    public boolean remove(Object value) {
        if (!(value instanceof Long)) return false;
        if (!this._set.remove((long)((Long)value).longValue())) return false;
        return true;
    }

    @Override
    public Iterator<Long> iterator() {
        return new Iterator<Long>((TLongSetDecorator)this){
            private final gnu.trove.iterator.TLongIterator it;
            final /* synthetic */ TLongSetDecorator this$0;
            {
                this.this$0 = this$0;
                this.it = this.this$0._set.iterator();
            }

            public Long next() {
                return Long.valueOf((long)this.it.next());
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
        if (o instanceof Long) return this._set.contains((long)((Long)o).longValue());
        return false;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._set = (TLongSet)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._set);
    }
}

