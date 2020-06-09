/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TCharObjectMapDecorator;
import gnu.trove.map.TCharObjectMap;
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

public class TCharObjectMapDecorator<V>
extends AbstractMap<Character, V>
implements Map<Character, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharObjectMap<V> _map;

    public TCharObjectMapDecorator() {
    }

    public TCharObjectMapDecorator(TCharObjectMap<V> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TCharObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Character key, V value) {
        char k;
        if (key == null) {
            k = this._map.getNoEntryKey();
            return (V)((V)this._map.put((char)k, value));
        }
        k = this.unwrapKey((Character)key);
        return (V)this._map.put((char)k, value);
    }

    @Override
    public V get(Object key) {
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return (V)null;
            k = this.unwrapKey((Character)((Character)key));
            return (V)((V)this._map.get((char)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.get((char)k);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public V remove(Object key) {
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return (V)null;
            k = this.unwrapKey((Character)((Character)key));
            return (V)((V)this._map.remove((char)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.remove((char)k);
    }

    @Override
    public Set<Map.Entry<Character, V>> entrySet() {
        return new AbstractSet<Map.Entry<Character, V>>((TCharObjectMapDecorator)this){
            final /* synthetic */ TCharObjectMapDecorator this$0;
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

            public Iterator<Map.Entry<Character, V>> iterator() {
                return new Iterator<Map.Entry<Character, V>>(this){
                    private final gnu.trove.iterator.TCharObjectIterator<V> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Character, V> next() {
                        this.it.advance();
                        char k = this.it.key();
                        Character key = k == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((char)k);
                        V v = this.it.value();
                        return new Map.Entry<Character, V>(this, v, (Character)key){
                            private V val;
                            final /* synthetic */ Object val$v;
                            final /* synthetic */ Character val$key;
                            final /* synthetic */ gnu.trove.decorator.TCharObjectMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = object;
                                this.val$key = c;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals(this.val)) return false;
                                return true;
                            }

                            public Character getKey() {
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
                                return (V)this.this$2.this$1.this$0.put((Character)this.val$key, value);
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

            public boolean add(Map.Entry<Character, V> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Character key = (Character)((Map.Entry)o).getKey();
                this.this$0._map.remove((char)this.this$0.unwrapKey((Character)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Character, V>> c) {
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
            return this._map.containsKey((char)this._map.getNoEntryKey());
        }
        if (!(key instanceof Character)) return false;
        if (!this._map.containsKey((char)((Character)key).charValue())) return false;
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
    public void putAll(Map<? extends Character, ? extends V> map) {
        Iterator<Map.Entry<Character, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Character, V> e = it.next();
            this.put((Character)e.getKey(), e.getValue());
        }
    }

    protected Character wrapKey(char k) {
        return Character.valueOf((char)k);
    }

    protected char unwrapKey(Character key) {
        return key.charValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TCharObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

