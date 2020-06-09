/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TByteSetDecorator;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TByteSetDecorator
extends AbstractSet<Byte>
implements Set<Byte>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TByteSet _set;

    public TByteSetDecorator() {
    }

    public TByteSetDecorator(TByteSet set) {
        Objects.requireNonNull(set);
        this._set = set;
    }

    public TByteSet getSet() {
        return this._set;
    }

    @Override
    public boolean add(Byte value) {
        if (value == null) return false;
        if (!this._set.add((byte)value.byteValue())) return false;
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
            if (!(val instanceof Byte)) return false;
            byte v = ((Byte)val).byteValue();
            if (!this._set.contains((byte)v)) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this._set.clear();
    }

    @Override
    public boolean remove(Object value) {
        if (!(value instanceof Byte)) return false;
        if (!this._set.remove((byte)((Byte)value).byteValue())) return false;
        return true;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>((TByteSetDecorator)this){
            private final gnu.trove.iterator.TByteIterator it;
            final /* synthetic */ TByteSetDecorator this$0;
            {
                this.this$0 = this$0;
                this.it = this.this$0._set.iterator();
            }

            public Byte next() {
                return Byte.valueOf((byte)this.it.next());
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
        if (o instanceof Byte) return this._set.contains((byte)((Byte)o).byteValue());
        return false;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._set = (TByteSet)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._set);
    }
}

