/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TFloatIntMapDecorator;
import gnu.trove.map.TFloatIntMap;
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

public class TFloatIntMapDecorator
extends AbstractMap<Float, Integer>
implements Map<Float, Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatIntMap _map;

    public TFloatIntMapDecorator() {
    }

    public TFloatIntMapDecorator(TFloatIntMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TFloatIntMap getMap() {
        return this._map;
    }

    @Override
    public Integer put(Float key, Integer value) {
        float k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        int v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        int retval = this._map.put((float)k, (int)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((int)retval);
        return null;
    }

    @Override
    public Integer get(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.get((float)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((int)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Integer remove(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.remove((float)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((int)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Float, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<Float, Integer>>((TFloatIntMapDecorator)this){
            final /* synthetic */ TFloatIntMapDecorator this$0;
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

            public Iterator<Map.Entry<Float, Integer>> iterator() {
                return new Iterator<Map.Entry<Float, Integer>>(this){
                    private final gnu.trove.iterator.TFloatIntIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Float, Integer> next() {
                        this.it.advance();
                        float ik = this.it.key();
                        Float key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((float)ik);
                        int iv = this.it.value();
                        Integer v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((int)iv);
                        return new Map.Entry<Float, Integer>(this, (Integer)v, (Float)key){
                            private Integer val;
                            final /* synthetic */ Integer val$v;
                            final /* synthetic */ Float val$key;
                            final /* synthetic */ gnu.trove.decorator.TFloatIntMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = n;
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

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put((Float)this.val$key, (Integer)value);
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

            public boolean add(Map.Entry<Float, Integer> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Float key = (Float)((Map.Entry)o).getKey();
                this.this$0._map.remove((float)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Float, Integer>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        if (!(val instanceof Integer)) return false;
        if (!this._map.containsValue((int)this.unwrapValue((Object)val))) return false;
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
    public void putAll(Map<? extends Float, ? extends Integer> map) {
        Iterator<Map.Entry<? extends Float, ? extends Integer>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Float, ? extends Integer> e = it.next();
            this.put((Float)e.getKey(), (Integer)e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf((float)k);
    }

    protected float unwrapKey(Object key) {
        return ((Float)key).floatValue();
    }

    protected Integer wrapValue(int k) {
        return Integer.valueOf((int)k);
    }

    protected int unwrapValue(Object value) {
        return ((Integer)value).intValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TFloatIntMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

