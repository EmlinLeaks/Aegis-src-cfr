/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TShortObjectMapDecorator;
import gnu.trove.map.TShortObjectMap;
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

public class TShortObjectMapDecorator<V>
extends AbstractMap<Short, V>
implements Map<Short, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TShortObjectMap<V> _map;

    public TShortObjectMapDecorator() {
    }

    public TShortObjectMapDecorator(TShortObjectMap<V> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TShortObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Short key, V value) {
        short k;
        if (key == null) {
            k = this._map.getNoEntryKey();
            return (V)((V)this._map.put((short)k, value));
        }
        k = this.unwrapKey((Short)key);
        return (V)this._map.put((short)k, value);
    }

    @Override
    public V get(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return (V)null;
            k = this.unwrapKey((Short)((Short)key));
            return (V)((V)this._map.get((short)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.get((short)k);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public V remove(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return (V)null;
            k = this.unwrapKey((Short)((Short)key));
            return (V)((V)this._map.remove((short)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.remove((short)k);
    }

    @Override
    public Set<Map.Entry<Short, V>> entrySet() {
        return new AbstractSet<Map.Entry<Short, V>>((TShortObjectMapDecorator)this){
            final /* synthetic */ TShortObjectMapDecorator this$0;
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

            public Iterator<Map.Entry<Short, V>> iterator() {
                return new Iterator<Map.Entry<Short, V>>(this){
                    private final gnu.trove.iterator.TShortObjectIterator<V> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Short, V> next() {
                        this.it.advance();
                        short k = this.it.key();
                        Short key = k == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((short)k);
                        V v = this.it.value();
                        return new Map.Entry<Short, V>(this, v, (Short)key){
                            private V val;
                            final /* synthetic */ Object val$v;
                            final /* synthetic */ Short val$key;
                            final /* synthetic */ gnu.trove.decorator.TShortObjectMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = object;
                                this.val$key = s;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals(this.val)) return false;
                                return true;
                            }

                            public Short getKey() {
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
                                return (V)this.this$2.this$1.this$0.put((Short)this.val$key, value);
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

            public boolean add(Map.Entry<Short, V> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Short key = (Short)((Map.Entry)o).getKey();
                this.this$0._map.remove((short)this.this$0.unwrapKey((Short)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Short, V>> c) {
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
            return this._map.containsKey((short)this._map.getNoEntryKey());
        }
        if (!(key instanceof Short)) return false;
        if (!this._map.containsKey((short)((Short)key).shortValue())) return false;
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
    public void putAll(Map<? extends Short, ? extends V> map) {
        Iterator<Map.Entry<Short, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Short, V> e = it.next();
            this.put((Short)e.getKey(), e.getValue());
        }
    }

    protected Short wrapKey(short k) {
        return Short.valueOf((short)k);
    }

    protected short unwrapKey(Short key) {
        return key.shortValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TShortObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

