/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TObjectShortMapDecorator;
import gnu.trove.map.TObjectShortMap;
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

public class TObjectShortMapDecorator<K>
extends AbstractMap<K, Short>
implements Map<K, Short>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TObjectShortMap<K> _map;

    public TObjectShortMapDecorator() {
    }

    public TObjectShortMapDecorator(TObjectShortMap<K> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TObjectShortMap<K> getMap() {
        return this._map;
    }

    @Override
    public Short put(K key, Short value) {
        if (value != null) return this.wrapValue((short)this._map.put(key, (short)this.unwrapValue((Object)value)));
        return this.wrapValue((short)this._map.put(key, (short)this._map.getNoEntryValue()));
    }

    @Override
    public Short get(Object key) {
        short v = this._map.get((Object)key);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((short)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Short remove(Object key) {
        short v = this._map.remove((Object)key);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((short)v);
        return null;
    }

    @Override
    public Set<Map.Entry<K, Short>> entrySet() {
        return new AbstractSet<Map.Entry<K, Short>>((TObjectShortMapDecorator)this){
            final /* synthetic */ TObjectShortMapDecorator this$0;
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

            public Iterator<Map.Entry<K, Short>> iterator() {
                return new Iterator<Map.Entry<K, Short>>(this){
                    private final gnu.trove.iterator.TObjectShortIterator<K> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<K, Short> next() {
                        this.it.advance();
                        K key = this.it.key();
                        Short v = this.this$1.this$0.wrapValue((short)this.it.value());
                        return new Map.Entry<K, Short>(this, (Short)v, key){
                            private Short val;
                            final /* synthetic */ Short val$v;
                            final /* synthetic */ Object val$key;
                            final /* synthetic */ gnu.trove.decorator.TObjectShortMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = s;
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

                            public Short getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Short setValue(Short value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put(this.val$key, (Short)value);
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

            public boolean add(Map.Entry<K, Short> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                K key = ((Map.Entry)o).getKey();
                this.this$0._map.remove(key);
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<K, Short>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        if (!(val instanceof Short)) return false;
        if (!this._map.containsValue((short)this.unwrapValue((Object)val))) return false;
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
    public void putAll(Map<? extends K, ? extends Short> map) {
        Iterator<Map.Entry<K, Short>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<K, Short> e = it.next();
            this.put(e.getKey(), (Short)e.getValue());
        }
    }

    protected Short wrapValue(short k) {
        return Short.valueOf((short)k);
    }

    protected short unwrapValue(Object value) {
        return ((Short)value).shortValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TObjectShortMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

