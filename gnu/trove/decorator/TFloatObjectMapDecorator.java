/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TFloatObjectMapDecorator;
import gnu.trove.map.TFloatObjectMap;
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

public class TFloatObjectMapDecorator<V>
extends AbstractMap<Float, V>
implements Map<Float, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatObjectMap<V> _map;

    public TFloatObjectMapDecorator() {
    }

    public TFloatObjectMapDecorator(TFloatObjectMap<V> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TFloatObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Float key, V value) {
        float k;
        if (key == null) {
            k = this._map.getNoEntryKey();
            return (V)((V)this._map.put((float)k, value));
        }
        k = this.unwrapKey((Float)key);
        return (V)this._map.put((float)k, value);
    }

    @Override
    public V get(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return (V)null;
            k = this.unwrapKey((Float)((Float)key));
            return (V)((V)this._map.get((float)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.get((float)k);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public V remove(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return (V)null;
            k = this.unwrapKey((Float)((Float)key));
            return (V)((V)this._map.remove((float)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.remove((float)k);
    }

    @Override
    public Set<Map.Entry<Float, V>> entrySet() {
        return new AbstractSet<Map.Entry<Float, V>>((TFloatObjectMapDecorator)this){
            final /* synthetic */ TFloatObjectMapDecorator this$0;
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

            public Iterator<Map.Entry<Float, V>> iterator() {
                return new Iterator<Map.Entry<Float, V>>(this){
                    private final gnu.trove.iterator.TFloatObjectIterator<V> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Float, V> next() {
                        this.it.advance();
                        float k = this.it.key();
                        Float key = k == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((float)k);
                        V v = this.it.value();
                        return new Map.Entry<Float, V>(this, v, (Float)key){
                            private V val;
                            final /* synthetic */ Object val$v;
                            final /* synthetic */ Float val$key;
                            final /* synthetic */ gnu.trove.decorator.TFloatObjectMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = object;
                                this.val$key = f;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals(this.val)) return false;
                                return true;
                            }

                            public Float getKey() {
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
                                return (V)this.this$2.this$1.this$0.put((Float)this.val$key, value);
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

            public boolean add(Map.Entry<Float, V> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Float key = (Float)((Map.Entry)o).getKey();
                this.this$0._map.remove((float)this.this$0.unwrapKey((Float)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Float, V>> c) {
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
            return this._map.containsKey((float)this._map.getNoEntryKey());
        }
        if (!(key instanceof Float)) return false;
        if (!this._map.containsKey((float)((Float)key).floatValue())) return false;
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
    public void putAll(Map<? extends Float, ? extends V> map) {
        Iterator<Map.Entry<Float, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Float, V> e = it.next();
            this.put((Float)e.getKey(), e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf((float)k);
    }

    protected float unwrapKey(Float key) {
        return key.floatValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TFloatObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

