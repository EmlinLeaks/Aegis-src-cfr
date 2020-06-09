/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TCharSetDecorator;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TCharSetDecorator
extends AbstractSet<Character>
implements Set<Character>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TCharSet _set;

    public TCharSetDecorator() {
    }

    public TCharSetDecorator(TCharSet set) {
        Objects.requireNonNull(set);
        this._set = set;
    }

    public TCharSet getSet() {
        return this._set;
    }

    @Override
    public boolean add(Character value) {
        if (value == null) return false;
        if (!this._set.add((char)value.charValue())) return false;
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
            if (!(val instanceof Character)) return false;
            char v = ((Character)val).charValue();
            if (!this._set.contains((char)v)) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this._set.clear();
    }

    @Override
    public boolean remove(Object value) {
        if (!(value instanceof Character)) return false;
        if (!this._set.remove((char)((Character)value).charValue())) return false;
        return true;
    }

    @Override
    public Iterator<Character> iterator() {
        return new Iterator<Character>((TCharSetDecorator)this){
            private final gnu.trove.iterator.TCharIterator it;
            final /* synthetic */ TCharSetDecorator this$0;
            {
                this.this$0 = this$0;
                this.it = this.this$0._set.iterator();
            }

            public Character next() {
                return Character.valueOf((char)this.it.next());
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
        if (o instanceof Character) return this._set.contains((char)((Character)o).charValue());
        return false;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._set = (TCharSet)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._set);
    }
}

