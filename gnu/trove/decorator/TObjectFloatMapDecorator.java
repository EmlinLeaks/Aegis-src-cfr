/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TObjectFloatMapDecorator;
import gnu.trove.map.TObjectFloatMap;
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

public class TObjectFloatMapDecorator<K>
extends AbstractMap<K, Float>
implements Map<K, Float>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TObjectFloatMap<K> _map;

    public TObjectFloatMapDecorator() {
    }

    public TObjectFloatMapDecorator(TObjectFloatMap<K> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TObjectFloatMap<K> getMap() {
        return this._map;
    }

    @Override
    public Float put(K key, Float value) {
        if (value != null) return this.wrapValue((float)this._map.put(key, (float)this.unwrapValue((Object)value)));
        return this.wrapValue((float)this._map.put(key, (float)this._map.getNoEntryValue()));
    }

    @Override
    public Float get(Object key) {
        float v = this._map.get((Object)key);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((float)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Float remove(Object key) {
        float v = this._map.remove((Object)key);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((float)v);
        return null;
    }

    @Override
    public Set<Map.Entry<K, Float>> entrySet() {
        return new AbstractSet<Map.Entry<K, Float>>((TObjectFloatMapDecorator)this){
            final /* synthetic */ TObjectFloatMapDecorator this$0;
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

            public Iterator<Map.Entry<K, Float>> iterator() {
                return new Iterator<Map.Entry<K, Float>>(this){
                    private final gnu.trove.iterator.TObjectFloatIterator<K> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<K, Float> next() {
                        this.it.advance();
                        K key = this.it.key();
                        Float v = this.this$1.this$0.wrapValue((float)this.it.value());
                        return new Map.Entry<K, Float>(this, (Float)v, key){
                            private Float val;
                            final /* synthetic */ Float val$v;
                            final /* synthetic */ Object val$key;
                            final /* synthetic */ gnu.trove.decorator.TObjectFloatMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = f;
                                this.val$key = object;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals((Object)this.val)) return false;
                                return true;
                            }

                            public K getKey() {
                                return (K)this.val$key;
                            }

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put(this.val$key, (Float)value);
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

            public boolean add(Map.Entry<K, Float> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                K key = ((Map.Entry)o).getKey();
                this.this$0._map.remove(key);
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<K, Float>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        if (!(val instanceof Float)) return false;
        if (!this._map.containsValue((float)this.unwrapValue((Object)val))) return false;
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return this._map.containsKey((Object)key);
    }

    @Override
    public int size() {
        return this._map.size();
    }

    @Override
    public boolean isEmpty() {
        if (this._map.size() != 0) return false;
        return true;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> map) {
        Iterator<Map.Entry<K, Float>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<K, Float> e = it.next();
            this.put(e.getKey(), (Float)e.getValue());
        }
    }

    protected Float wrapValue(float k) {
        return Float.valueOf((float)k);
    }

    protected float unwrapValue(Object value) {
        return ((Float)value).floatValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TObjectFloatMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

