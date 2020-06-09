/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TByteLongMapDecorator;
import gnu.trove.map.TByteLongMap;
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

public class TByteLongMapDecorator
extends AbstractMap<Byte, Long>
implements Map<Byte, Long>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TByteLongMap _map;

    public TByteLongMapDecorator() {
    }

    public TByteLongMapDecorator(TByteLongMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TByteLongMap getMap() {
        return this._map;
    }

    @Override
    public Long put(Byte key, Long value) {
        byte k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        long v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        long retval = this._map.put((byte)k, (long)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((long)retval);
        return null;
    }

    @Override
    public Long get(Object key) {
        byte k;
        if (key != null) {
            if (!(key instanceof Byte)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        long v = this._map.get((byte)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((long)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Long remove(Object key) {
        byte k;
        if (key != null) {
            if (!(key instanceof Byte)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        long v = this._map.remove((byte)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((long)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Byte, Long>> entrySet() {
        return new AbstractSet<Map.Entry<Byte, Long>>((TByteLongMapDecorator)this){
            final /* synthetic */ TByteLongMapDecorator this$0;
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

            public Iterator<Map.Entry<Byte, Long>> iterator() {
                return new Iterator<Map.Entry<Byte, Long>>(this){
                    private final gnu.trove.iterator.TByteLongIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Byte, Long> next() {
                        this.it.advance();
                        byte ik = this.it.key();
                        Byte key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((byte)ik);
                        long iv = this.it.value();
                        Long v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((long)iv);
                        return new Map.Entry<Byte, Long>(this, (Long)v, (Byte)key){
                            private Long val;
                            final /* synthetic */ Long val$v;
                            final /* synthetic */ Byte val$key;
                            final /* synthetic */ gnu.trove.decorator.TByteLongMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = l;
                                this.val$key = by;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals((Object)this.val)) return false;
                                return true;
                            }

                            public Byte getKey() {
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
                                return this.this$2.this$1.this$0.put((Byte)this.val$key, (Long)value);
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

            public boolean add(Map.Entry<Byte, Long> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Byte key = (Byte)((Map.Entry)o).getKey();
                this.this$0._map.remove((byte)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Byte, Long>> c) {
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
            return this._map.containsKey((byte)this._map.getNoEntryKey());
        }
        if (!(key instanceof Byte)) return false;
        if (!this._map.containsKey((byte)this.unwrapKey((Object)key))) return false;
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
    public void putAll(Map<? extends Byte, ? extends Long> map) {
        Iterator<Map.Entry<? extends Byte, ? extends Long>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Byte, ? extends Long> e = it.next();
            this.put((Byte)e.getKey(), (Long)e.getValue());
        }
    }

    protected Byte wrapKey(byte k) {
        return Byte.valueOf((byte)k);
    }

    protected byte unwrapKey(Object key) {
        return ((Byte)key).byteValue();
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
        this._map = (TByteLongMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

