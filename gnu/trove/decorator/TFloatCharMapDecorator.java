/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TFloatCharMapDecorator;
import gnu.trove.map.TFloatCharMap;
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

public class TFloatCharMapDecorator
extends AbstractMap<Float, Character>
implements Map<Float, Character>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatCharMap _map;

    public TFloatCharMapDecorator() {
    }

    public TFloatCharMapDecorator(TFloatCharMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TFloatCharMap getMap() {
        return this._map;
    }

    @Override
    public Character put(Float key, Character value) {
        float k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        char v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        char retval = this._map.put((float)k, (char)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((char)retval);
        return null;
    }

    @Override
    public Character get(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.get((float)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((char)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Character remove(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.remove((float)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((char)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Float, Character>> entrySet() {
        return new AbstractSet<Map.Entry<Float, Character>>((TFloatCharMapDecorator)this){
            final /* synthetic */ TFloatCharMapDecorator this$0;
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

            public Iterator<Map.Entry<Float, Character>> iterator() {
                return new Iterator<Map.Entry<Float, Character>>(this){
                    private final gnu.trove.iterator.TFloatCharIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Float, Character> next() {
                        this.it.advance();
                        float ik = this.it.key();
                        Float key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((float)ik);
                        char iv = this.it.value();
                        Character v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((char)iv);
                        return new Map.Entry<Float, Character>(this, (Character)v, (Float)key){
                            private Character val;
                            final /* synthetic */ Character val$v;
                            final /* synthetic */ Float val$key;
                            final /* synthetic */ gnu.trove.decorator.TFloatCharMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = c;
                                this.val$key = f;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals((Object)this.val)) return false;
                                return true;
                            }

                            public Float getKey() {
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
                                return this.this$2.this$1.this$0.put((Float)this.val$key, (Character)value);
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

            public boolean add(Map.Entry<Float, Character> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Float key = (Float)((Map.Entry)o).getKey();
                this.this$0._map.remove((float)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Float, Character>> c) {
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
            return this._map.containsKey((float)this._map.getNoEntryKey());
        }
        if (!(key instanceof Float)) return false;
        if (!this._map.containsKey((float)this.unwrapKey((Object)key))) return false;
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
    public void putAll(Map<? extends Float, ? extends Character> map) {
        Iterator<Map.Entry<? extends Float, ? extends Character>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Float, ? extends Character> e = it.next();
            this.put((Float)e.getKey(), (Character)e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf((float)k);
    }

    protected float unwrapKey(Object key) {
        return ((Float)key).floatValue();
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
        this._map = (TFloatCharMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

