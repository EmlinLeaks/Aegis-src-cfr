/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TCharCharMapDecorator;
import gnu.trove.map.TCharCharMap;
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

public class TCharCharMapDecorator
extends AbstractMap<Character, Character>
implements Map<Character, Character>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharCharMap _map;

    public TCharCharMapDecorator() {
    }

    public TCharCharMapDecorator(TCharCharMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TCharCharMap getMap() {
        return this._map;
    }

    @Override
    public Character put(Character key, Character value) {
        char k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        char v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        char retval = this._map.put((char)k, (char)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((char)retval);
        return null;
    }

    @Override
    public Character get(Object key) {
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.get((char)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((char)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Character remove(Object key) {
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.remove((char)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((char)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Character, Character>> entrySet() {
        return new AbstractSet<Map.Entry<Character, Character>>((TCharCharMapDecorator)this){
            final /* synthetic */ TCharCharMapDecorator this$0;
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

            public Iterator<Map.Entry<Character, Character>> iterator() {
                return new Iterator<Map.Entry<Character, Character>>(this){
                    private final gnu.trove.iterator.TCharCharIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Character, Character> next() {
                        this.it.advance();
                        char ik = this.it.key();
                        Character key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((char)ik);
                        char iv = this.it.value();
                        Character v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((char)iv);
                        return new Map.Entry<Character, Character>(this, (Character)v, (Character)key){
                            private Character val;
                            final /* synthetic */ Character val$v;
                            final /* synthetic */ Character val$key;
                            final /* synthetic */ gnu.trove.decorator.TCharCharMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = c;
                                this.val$key = c2;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals((Object)this.val)) return false;
                                return true;
                            }

                            public Character getKey() {
                                return this.val$key;
                            }

                            public Character getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Character setValue(Character value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put((Character)this.val$key, (Character)value);
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

            public boolean add(Map.Entry<Character, Character> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Character key = (Character)((Map.Entry)o).getKey();
                this.this$0._map.remove((char)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Character, Character>> c) {
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
        if (key == null) {
            return this._map.containsKey((char)this._map.getNoEntryKey());
        }
        if (!(key instanceof Character)) return false;
        if (!this._map.containsKey((char)this.unwrapKey((Object)key))) return false;
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
    public void putAll(Map<? extends Character, ? extends Character> map) {
        Iterator<Map.Entry<? extends Character, ? extends Character>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Character, ? extends Character> e = it.next();
            this.put((Character)e.getKey(), (Character)e.getValue());
        }
    }

    protected Character wrapKey(char k) {
        return Character.valueOf((char)k);
    }

    protected char unwrapKey(Object key) {
        return ((Character)key).charValue();
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
        this._map = (TCharCharMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

