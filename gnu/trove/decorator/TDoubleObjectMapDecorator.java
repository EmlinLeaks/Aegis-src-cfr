/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TDoubleObjectMapDecorator;
import gnu.trove.map.TDoubleObjectMap;
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

public class TDoubleObjectMapDecorator<V>
extends AbstractMap<Double, V>
implements Map<Double, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TDoubleObjectMap<V> _map;

    public TDoubleObjectMapDecorator() {
    }

    public TDoubleObjectMapDecorator(TDoubleObjectMap<V> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TDoubleObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Double key, V value) {
        double k;
        if (key == null) {
            k = this._map.getNoEntryKey();
            return (V)((V)this._map.put((double)k, value));
        }
        k = this.unwrapKey((Double)key);
        return (V)this._map.put((double)k, value);
    }

    @Override
    public V get(Object key) {
        double k;
        if (key != null) {
            if (!(key instanceof Double)) return (V)null;
            k = this.unwrapKey((Double)((Double)key));
            return (V)((V)this._map.get((double)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.get((double)k);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public V remove(Object key) {
        double k;
        if (key != null) {
            if (!(key instanceof Double)) return (V)null;
            k = this.unwrapKey((Double)((Double)key));
            return (V)((V)this._map.remove((double)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.remove((double)k);
    }

    @Override
    public Set<Map.Entry<Double, V>> entrySet() {
        return new AbstractSet<Map.Entry<Double, V>>((TDoubleObjectMapDecorator)this){
            final /* synthetic */ TDoubleObjectMapDecorator this$0;
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

            public Iterator<Map.Entry<Double, V>> iterator() {
                return new Iterator<Map.Entry<Double, V>>(this){
                    private final gnu.trove.iterator.TDoubleObjectIterator<V> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Double, V> next() {
                        this.it.advance();
                        double k = this.it.key();
                        Double key = k == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((double)k);
                        V v = this.it.value();
                        return new Map.Entry<Double, V>(this, v, (Double)key){
                            private V val;
                            final /* synthetic */ Object val$v;
                            final /* synthetic */ Double val$key;
                            final /* synthetic */ gnu.trove.decorator.TDoubleObjectMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = object;
                                this.val$key = d;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals(this.val)) return false;
                                return true;
                            }

                            public Double getKey() {
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
                                return (V)this.this$2.this$1.this$0.put((Double)this.val$key, value);
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

            public boolean add(Map.Entry<Double, V> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Double key = (Double)((Map.Entry)o).getKey();
                this.this$0._map.remove((double)this.this$0.unwrapKey((Double)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Double, V>> c) {
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
            return this._map.containsKey((double)this._map.getNoEntryKey());
        }
        if (!(key instanceof Double)) return false;
        if (!this._map.containsKey((double)((Double)key).doubleValue())) return false;
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
    public void putAll(Map<? extends Double, ? extends V> map) {
        Iterator<Map.Entry<Double, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Double, V> e = it.next();
            this.put((Double)e.getKey(), e.getValue());
        }
    }

    protected Double wrapKey(double k) {
        return Double.valueOf((double)k);
    }

    protected double unwrapKey(Double key) {
        return key.doubleValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TDoubleObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

