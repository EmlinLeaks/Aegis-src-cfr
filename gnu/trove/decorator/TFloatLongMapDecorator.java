/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TFloatLongMapDecorator;
import gnu.trove.map.TFloatLongMap;
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

public class TFloatLongMapDecorator
extends AbstractMap<Float, Long>
implements Map<Float, Long>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatLongMap _map;

    public TFloatLongMapDecorator() {
    }

    public TFloatLongMapDecorator(TFloatLongMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TFloatLongMap getMap() {
        return this._map;
    }

    @Override
    public Long put(Float key, Long value) {
        float k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        long v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        long retval = this._map.put((float)k, (long)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((long)retval);
        return null;
    }

    @Override
    public Long get(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        long v = this._map.get((float)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((long)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Long remove(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        long v = this._map.remove((float)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((long)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Float, Long>> entrySet() {
        return new AbstractSet<Map.Entry<Float, Long>>((TFloatLongMapDecorator)this){
            final /* synthetic */ TFloatLongMapDecorator this$0;
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

            public Iterator<Map.Entry<Float, Long>> iterator() {
                return new Iterator<Map.Entry<Float, Long>>(this){
                    private final gnu.trove.iterator.TFloatLongIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Float, Long> next() {
                        this.it.advance();
                        float ik = this.it.key();
                        Float key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((float)ik);
                        long iv = this.it.value();
                        Long v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((long)iv);
                        return new Map.Entry<Float, Long>(this, (Long)v, (Float)key){
                            private Long val;
                            final /* synthetic */ Long val$v;
                            final /* synthetic */ Float val$key;
                            final /* synthetic */ gnu.trove.decorator.TFloatLongMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = l;
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

                            public Long getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Long setValue(Long value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put((Float)this.val$key, (Long)value);
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

            public boolean add(Map.Entry<Float, Long> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Float key = (Float)((Map.Entry)o).getKey();
                this.this$0._map.remove((float)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Float, Long>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        if (!(val instanceof Long)) return false;
        if (!this._map.containsValue((long)this.unwrapValue((Object)val))) return false;
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
    public void putAll(Map<? extends Float, ? extends Long> map) {
        Iterator<Map.Entry<? extends Float, ? extends Long>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Float, ? extends Long> e = it.next();
            this.put((Float)e.getKey(), (Long)e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf((float)k);
    }

    protected float unwrapKey(Object key) {
        return ((Float)key).floatValue();
    }

    protected Long wrapValue(long k) {
        return Long.valueOf((long)k);
    }

    protected long unwrapValue(Object value) {
        return ((Long)value).longValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TFloatLongMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

