/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TIntObjectMapDecorator;
import gnu.trove.map.TIntObjectMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TIntObjectMapDecorator<V>
extends AbstractMap<Integer, V>
implements Map<Integer, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TIntObjectMap<V> _map;

    public TIntObjectMapDecorator() {
    }

    public TIntObjectMapDecorator(TIntObjectMap<V> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TIntObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Integer key, V value) {
        int k;
        if (key == null) {
            k = this._map.getNoEntryKey();
            return (V)((V)this._map.put((int)k, value));
        }
        k = this.unwrapKey((Integer)key);
        return (V)this._map.put((int)k, value);
    }

    @Override
    public V get(Object key) {
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return (V)null;
            k = this.unwrapKey((Integer)((Integer)key));
            return (V)((V)this._map.get((int)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.get((int)k);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public V remove(Object key) {
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return (V)null;
            k = this.unwrapKey((Integer)((Integer)key));
            return (V)((V)this._map.remove((int)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.remove((int)k);
    }

    @Override
    public Set<Map.Entry<Integer, V>> entrySet() {
        return new AbstractSet<Map.Entry<Integer, V>>((TIntObjectMapDecorator)this){
            final /* synthetic */ TIntObjectMapDecorator this$0;
            {
                this.this$0 = this$0;
            }

            public int size() {
                return this.this$0._map.size();
            }

            public boolean isEmpty() {
                return this.this$0.isEmpty();
            }

            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry)) return false;
                K k = ((Map.Entry)o).getKey();
                V v = ((Map.Entry)o).getValue();
                if (!this.this$0.containsKey(k)) return false;
                if (!this.this$0.get(k).equals(v)) return false;
                return true;
            }

            public Iterator<Map.Entry<Integer, V>> iterator() {
                return new Iterator<Map.Entry<Integer, V>>(this){
                    private final gnu.trove.iterator.TIntObjectIterator<V> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Integer, V> next() {
                        this.it.advance();
                        int k = this.it.key();
                        Integer key = k == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((int)k);
                        V v = this.it.value();
                        return new Map.Entry<Integer, V>(this, v, (Integer)key){
                            private V val;
                            final /* synthetic */ Object val$v;
                            final /* synthetic */ Integer val$key;
                            final /* synthetic */ gnu.trove.decorator.TIntObjectMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = object;
                                this.val$key = n;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals(this.val)) return false;
                                return true;
                            }

                            public Integer getKey() {
                                return this.val$key;
                            }

                            public V getValue() {
                                return (V)this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V value) {
                                this.val = value;
                                return (V)this.this$2.this$1.this$0.put((Integer)this.val$key, value);
                            }
                        };
                    }

                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    public void remove() {
                        this.it.remove();
                    }
                };
            }

            public boolean add(Map.Entry<Integer, V> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Integer key = (Integer)((Map.Entry)o).getKey();
                this.this$0._map.remove((int)this.this$0.unwrapKey((Integer)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Integer, V>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        return this._map.containsValue((Object)val);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return this._map.containsKey((int)this._map.getNoEntryKey());
        }
        if (!(key instanceof Integer)) return false;
        if (!this._map.containsKey((int)((Integer)key).intValue())) return false;
        return true;
    }

    @Override
    public int size() {
        return this._map.size();
    }

    @Override
    public boolean isEmpty() {
        if (this.size() != 0) return false;
        return true;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> map) {
        Iterator<Map.Entry<Integer, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Integer, V> e = it.next();
            this.put((Integer)e.getKey(), e.getValue());
        }
    }

    protected Integer wrapKey(int k) {
        return Integer.valueOf((int)k);
    }

    protected int unwrapKey(Integer key) {
        return key.intValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TIntObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

