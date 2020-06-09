/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.set.hash;

import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.array.ToObjectArrayProceedure;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TCustomHashSet<E>
extends TCustomObjectHash<E>
implements Set<E>,
Iterable<E>,
Externalizable {
    static final long serialVersionUID = 1L;

    public TCustomHashSet() {
    }

    public TCustomHashSet(HashingStrategy<? super E> strategy) {
        super(strategy);
    }

    public TCustomHashSet(HashingStrategy<? super E> strategy, int initialCapacity) {
        super(strategy, (int)initialCapacity);
    }

    public TCustomHashSet(HashingStrategy<? super E> strategy, int initialCapacity, float loadFactor) {
        super(strategy, (int)initialCapacity, (float)loadFactor);
    }

    public TCustomHashSet(HashingStrategy<? super E> strategy, Collection<? extends E> collection) {
        this(strategy, (int)collection.size());
        this.addAll(collection);
    }

    @Override
    public boolean add(E obj) {
        int index = this.insertKey(obj);
        if (index < 0) {
            return false;
        }
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Set)) {
            return false;
        }
        Set that = (Set)other;
        if (that.size() == this.size()) return this.containsAll(that);
        return false;
    }

    @Override
    public int hashCode() {
        HashProcedure p = new HashProcedure((TCustomHashSet)this, null);
        this.forEach(p);
        return p.getHashCode();
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        int oldSize = this.size();
        Object[] oldSet = this._set;
        this._set = new Object[newCapacity];
        Arrays.fill((Object[])this._set, (Object)FREE);
        int i = oldCapacity;
        while (i-- > 0) {
            int index;
            Object o = oldSet[i];
            if (o == FREE || o == REMOVED || (index = this.insertKey(o)) >= 0) continue;
            this.throwObjectContractViolation((Object)this._set[-index - 1], (Object)o, (int)this.size(), (int)oldSize, (Object[])oldSet);
        }
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[this.size()];
        this.forEach(new ToObjectArrayProceedure<Object>(result));
        return result;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = this.size();
        if (a.length < size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), (int)size);
        }
        this.forEach(new ToObjectArrayProceedure<T>(a));
        if (a.length <= size) return a;
        a[size] = null;
        return a;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((Object[])this._set, (int)0, (int)this._set.length, (Object)FREE);
    }

    @Override
    public boolean remove(Object obj) {
        int index = this.index((Object)obj);
        if (index < 0) return false;
        this.removeAt((int)index);
        return true;
    }

    @Override
    public TObjectHashIterator<E> iterator() {
        return new TObjectHashIterator<E>(this);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        Iterator<?> i = collection.iterator();
        do {
            if (!i.hasNext()) return true;
        } while (this.contains(i.next()));
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean changed = false;
        int size = collection.size();
        this.ensureCapacity((int)size);
        Iterator<E> it = collection.iterator();
        while (size-- > 0) {
            if (!this.add(it.next())) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        int size = collection.size();
        Iterator<?> it = collection.iterator();
        while (size-- > 0) {
            if (!this.remove(it.next())) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        int size = this.size();
        Iterator it = this.iterator();
        while (size-- > 0) {
            if (collection.contains(it.next())) continue;
            it.remove();
            changed = true;
        }
        return changed;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEach(new TObjectProcedure<E>((TCustomHashSet)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TCustomHashSet this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(Object value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((Object)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)1);
        super.writeExternal((ObjectOutput)out);
        out.writeInt((int)this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject((Object)this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        if (version != 0) {
            super.readExternal((ObjectInput)in);
        }
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            Object val = in.readObject();
            this.add(val);
        }
    }
}

