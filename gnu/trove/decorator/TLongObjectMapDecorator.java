/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TLongObjectMapDecorator;
import gnu.trove.map.TLongObjectMap;
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

public class TLongObjectMapDecorator<V>
extends AbstractMap<Long, V>
implements Map<Long, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TLongObjectMap<V> _map;

    public TLongObjectMapDecorator() {
    }

    public TLongObjectMapDecorator(TLongObjectMap<V> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TLongObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Long key, V value) {
        long k;
        if (key == null) {
            k = this._map.getNoEntryKey();
            return (V)((V)this._map.put((long)k, value));
        }
        k = this.unwrapKey((Long)key);
        return (V)this._map.put((long)k, value);
    }

    @Override
    public V get(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return (V)null;
            k = this.unwrapKey((Long)((Long)key));
            return (V)((V)this._map.get((long)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.get((long)k);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public V remove(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return (V)null;
            k = this.unwrapKey((Long)((Long)key));
            return (V)((V)this._map.remove((long)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.remove((long)k);
    }

    @Override
    public Set<Map.Entry<Long, V>> entrySet() {
        return new AbstractSet<Map.Entry<Long, V>>((TLongObjectMapDecorator)this){
            final /* synthetic */ TLongObjectMapDecorator this$0;
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

            public Iterator<Map.Entry<Long, V>> iterator() {
                return new Iterator<Map.Entry<Long, V>>(this){
                    private final gnu.trove.iterator.TLongObjectIterator<V> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Long, V> next() {
                        this.it.advance();
                        long k = this.it.key();
                        Long key = k == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((long)k);
                        V v = this.it.value();
                        return new Map.Entry<Long, V>(this, v, (Long)key){
                            private V val;
                            final /* synthetic */ Object val$v;
                            final /* synthetic */ Long val$key;
                            final /* synthetic */ gnu.trove.decorator.TLongObjectMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = object;
                                this.val$key = l;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals(this.val)) return false;
                                return true;
                            }

                            public Long getKey() {
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
                                return (V)this.this$2.this$1.this$0.put((Long)this.val$key, value);
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

            public boolean add(Map.Entry<Long, V> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Long key = (Long)((Map.Entry)o).getKey();
                this.this$0._map.remove((long)this.this$0.unwrapKey((Long)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Long, V>> c) {
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
            return this._map.containsKey((long)this._map.getNoEntryKey());
        }
        if (!(key instanceof Long)) return false;
        if (!this._map.containsKey((long)((Long)key).longValue())) return false;
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
    public void putAll(Map<? extends Long, ? extends V> map) {
        Iterator<Map.Entry<Long, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Long, V> e = it.next();
            this.put((Long)e.getKey(), e.getValue());
        }
    }

    protected Long wrapKey(long k) {
        return Long.valueOf((long)k);
    }

    protected long unwrapKey(Long key) {
        return key.longValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TLongObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

