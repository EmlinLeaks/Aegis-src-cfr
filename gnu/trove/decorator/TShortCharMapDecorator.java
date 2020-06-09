/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TShortCharMapDecorator;
import gnu.trove.map.TShortCharMap;
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

public class TShortCharMapDecorator
extends AbstractMap<Short, Character>
implements Map<Short, Character>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TShortCharMap _map;

    public TShortCharMapDecorator() {
    }

    public TShortCharMapDecorator(TShortCharMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TShortCharMap getMap() {
        return this._map;
    }

    @Override
    public Character put(Short key, Character value) {
        short k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        char v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        char retval = this._map.put((short)k, (char)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((char)retval);
        return null;
    }

    @Override
    public Character get(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.get((short)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((char)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Character remove(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.remove((short)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((char)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Short, Character>> entrySet() {
        return new AbstractSet<Map.Entry<Short, Character>>((TShortCharMapDecorator)this){
            final /* synthetic */ TShortCharMapDecorator this$0;
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

            public Iterator<Map.Entry<Short, Character>> iterator() {
                return new Iterator<Map.Entry<Short, Character>>(this){
                    private final gnu.trove.iterator.TShortCharIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Short, Character> next() {
                        this.it.advance();
                        short ik = this.it.key();
                        Short key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((short)ik);
                        char iv = this.it.value();
                        Character v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((char)iv);
                        return new Map.Entry<Short, Character>(this, (Character)v, (Short)key){
                            private Character val;
                            final /* synthetic */ Character val$v;
                            final /* synthetic */ Short val$key;
                            final /* synthetic */ gnu.trove.decorator.TShortCharMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = c;
                                this.val$key = s;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals((Object)this.val)) return false;
                                return true;
                            }

                            public Short getKey() {
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
                                return this.this$2.this$1.this$0.put((Short)this.val$key, (Character)value);
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

            public boolean add(Map.Entry<Short, Character> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Short key = (Short)((Map.Entry)o).getKey();
                this.this$0._map.remove((short)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Short, Character>> c) {
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
            return this._map.containsKey((short)this._map.getNoEntryKey());
        }
        if (!(key instanceof Short)) return false;
        if (!this._map.containsKey((short)this.unwrapKey((Object)key))) return false;
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
    public void putAll(Map<? extends Short, ? extends Character> map) {
        Iterator<Map.Entry<? extends Short, ? extends Character>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Short, ? extends Character> e = it.next();
            this.put((Short)e.getKey(), (Character)e.getValue());
        }
    }

    protected Short wrapKey(short k) {
        return Short.valueOf((short)k);
    }

    protected short unwrapKey(Object key) {
        return ((Short)key).shortValue();
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
        this._map = (TShortCharMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

