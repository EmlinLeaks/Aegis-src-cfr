/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TLongIntMapDecorator;
import gnu.trove.map.TLongIntMap;
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

public class TLongIntMapDecorator
extends AbstractMap<Long, Integer>
implements Map<Long, Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TLongIntMap _map;

    public TLongIntMapDecorator() {
    }

    public TLongIntMapDecorator(TLongIntMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TLongIntMap getMap() {
        return this._map;
    }

    @Override
    public Integer put(Long key, Integer value) {
        long k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        int v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        int retval = this._map.put((long)k, (int)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((int)retval);
        return null;
    }

    @Override
    public Integer get(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.get((long)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((int)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Integer remove(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.remove((long)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((int)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Long, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<Long, Integer>>((TLongIntMapDecorator)this){
            final /* synthetic */ TLongIntMapDecorator this$0;
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

            public Iterator<Map.Entry<Long, Integer>> iterator() {
                return new Iterator<Map.Entry<Long, Integer>>(this){
                    private final gnu.trove.iterator.TLongIntIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Long, Integer> next() {
                        this.it.advance();
                        long ik = this.it.key();
                        Long key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((long)ik);
                        int iv = this.it.value();
                        Integer v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((int)iv);
                        return new Map.Entry<Long, Integer>(this, (Integer)v, (Long)key){
                            private Integer val;
                            final /* synthetic */ Integer val$v;
                            final /* synthetic */ Long val$key;
                            final /* synthetic */ gnu.trove.decorator.TLongIntMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = n;
                                this.val$key = l;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals((Object)this.val)) return false;
                                return true;
                            }

                            public Long getKey() {
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
                                return this.this$2.this$1.this$0.put((Long)this.val$key, (Integer)value);
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

            public boolean add(Map.Entry<Long, Integer> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Long key = (Long)((Map.Entry)o).getKey();
                this.this$0._map.remove((long)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Long, Integer>> c) {
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
            return this._map.containsKey((long)this._map.getNoEntryKey());
        }
        if (!(key instanceof Long)) return false;
        if (!this._map.containsKey((long)this.unwrapKey((Object)key))) return false;
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
    public void putAll(Map<? extends Long, ? extends Integer> map) {
        Iterator<Map.Entry<? extends Long, ? extends Integer>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Long, ? extends Integer> e = it.next();
            this.put((Long)e.getKey(), (Integer)e.getValue());
        }
    }

    protected Long wrapKey(long k) {
        return Long.valueOf((long)k);
    }

    protected long unwrapKey(Object key) {
        return ((Long)key).longValue();
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
        this._map = (TLongIntMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

