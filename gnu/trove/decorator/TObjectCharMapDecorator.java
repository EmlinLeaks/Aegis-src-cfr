/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TObjectCharMapDecorator;
import gnu.trove.map.TObjectCharMap;
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

public class TObjectCharMapDecorator<K>
extends AbstractMap<K, Character>
implements Map<K, Character>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TObjectCharMap<K> _map;

    public TObjectCharMapDecorator() {
    }

    public TObjectCharMapDecorator(TObjectCharMap<K> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TObjectCharMap<K> getMap() {
        return this._map;
    }

    @Override
    public Character put(K key, Character value) {
        if (value != null) return this.wrapValue((char)this._map.put(key, (char)this.unwrapValue((Object)value)));
        return this.wrapValue((char)this._map.put(key, (char)this._map.getNoEntryValue()));
    }

    @Override
    public Character get(Object key) {
        char v = this._map.get((Object)key);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((char)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Character remove(Object key) {
        char v = this._map.remove((Object)key);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((char)v);
        return null;
    }

    @Override
    public Set<Map.Entry<K, Character>> entrySet() {
        return new AbstractSet<Map.Entry<K, Character>>((TObjectCharMapDecorator)this){
            final /* synthetic */ TObjectCharMapDecorator this$0;
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

            public Iterator<Map.Entry<K, Character>> iterator() {
                return new Iterator<Map.Entry<K, Character>>(this){
                    private final gnu.trove.iterator.TObjectCharIterator<K> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<K, Character> next() {
                        this.it.advance();
                        K key = this.it.key();
                        Character v = this.this$1.this$0.wrapValue((char)this.it.value());
                        return new Map.Entry<K, Character>(this, (Character)v, key){
                            private Character val;
                            final /* synthetic */ Character val$v;
                            final /* synthetic */ Object val$key;
                            final /* synthetic */ gnu.trove.decorator.TObjectCharMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = c;
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

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put(this.val$key, (Character)value);
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

            public boolean add(Map.Entry<K, Character> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                K key = ((Map.Entry)o).getKey();
                this.this$0._map.remove(key);
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<K, Character>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        if (!(val instanceof Character)) return false;
        if (!this._map.containsValue((char)this.unwrapValue((Object)val))) return false;
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
    public void putAll(Map<? extends K, ? extends Character> map) {
        Iterator<Map.Entry<K, Character>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<K, Character> e = it.next();
            this.put(e.getKey(), (Character)e.getValue());
        }
    }

    protected Character wrapValue(char k) {
        return Character.valueOf((char)k);
    }

    protected char unwrapValue(Object value) {
        return ((Character)value).charValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TObjectCharMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

