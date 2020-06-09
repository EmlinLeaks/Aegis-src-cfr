/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TCharFloatMapDecorator;
import gnu.trove.map.TCharFloatMap;
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

public class TCharFloatMapDecorator
extends AbstractMap<Character, Float>
implements Map<Character, Float>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharFloatMap _map;

    public TCharFloatMapDecorator() {
    }

    public TCharFloatMapDecorator(TCharFloatMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TCharFloatMap getMap() {
        return this._map;
    }

    @Override
    public Float put(Character key, Float value) {
        char k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        float v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        float retval = this._map.put((char)k, (float)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((float)retval);
        return null;
    }

    @Override
    public Float get(Object key) {
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        float v = this._map.get((char)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((float)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Float remove(Object key) {
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        float v = this._map.remove((char)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((float)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Character, Float>> entrySet() {
        return new AbstractSet<Map.Entry<Character, Float>>((TCharFloatMapDecorator)this){
            final /* synthetic */ TCharFloatMapDecorator this$0;
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

            public Iterator<Map.Entry<Character, Float>> iterator() {
                return new Iterator<Map.Entry<Character, Float>>(this){
                    private final gnu.trove.iterator.TCharFloatIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Character, Float> next() {
                        this.it.advance();
                        char ik = this.it.key();
                        Character key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((char)ik);
                        float iv = this.it.value();
                        Float v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((float)iv);
                        return new Map.Entry<Character, Float>(this, (Float)v, (Character)key){
                            private Float val;
                            final /* synthetic */ Float val$v;
                            final /* synthetic */ Character val$key;
                            final /* synthetic */ gnu.trove.decorator.TCharFloatMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = f;
                                this.val$key = c;
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

                            public Float getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Float setValue(Float value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put((Character)this.val$key, (Float)value);
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

            public boolean add(Map.Entry<Character, Float> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Character key = (Character)((Map.Entry)o).getKey();
                this.this$0._map.remove((char)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Character, Float>> c) {
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
    public void putAll(Map<? extends Character, ? extends Float> map) {
        Iterator<Map.Entry<? extends Character, ? extends Float>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Character, ? extends Float> e = it.next();
            this.put((Character)e.getKey(), (Float)e.getValue());
        }
    }

    protected Character wrapKey(char k) {
        return Character.valueOf((char)k);
    }

    protected char unwrapKey(Object key) {
        return ((Character)key).charValue();
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
        this._map = (TCharFloatMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

