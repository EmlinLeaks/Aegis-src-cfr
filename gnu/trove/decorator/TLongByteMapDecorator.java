/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TLongByteMapDecorator;
import gnu.trove.map.TLongByteMap;
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

public class TLongByteMapDecorator
extends AbstractMap<Long, Byte>
implements Map<Long, Byte>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TLongByteMap _map;

    public TLongByteMapDecorator() {
    }

    public TLongByteMapDecorator(TLongByteMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TLongByteMap getMap() {
        return this._map;
    }

    @Override
    public Byte put(Long key, Byte value) {
        long k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        byte v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        byte retval = this._map.put((long)k, (byte)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((byte)retval);
        return null;
    }

    @Override
    public Byte get(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        byte v = this._map.get((long)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((byte)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Byte remove(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        byte v = this._map.remove((long)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((byte)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Long, Byte>> entrySet() {
        return new AbstractSet<Map.Entry<Long, Byte>>((TLongByteMapDecorator)this){
            final /* synthetic */ TLongByteMapDecorator this$0;
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

            public Iterator<Map.Entry<Long, Byte>> iterator() {
                return new Iterator<Map.Entry<Long, Byte>>(this){
                    private final gnu.trove.iterator.TLongByteIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Long, Byte> next() {
                        this.it.advance();
                        long ik = this.it.key();
                        Long key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((long)ik);
                        byte iv = this.it.value();
                        Byte v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((byte)iv);
                        return new Map.Entry<Long, Byte>(this, (Byte)v, (Long)key){
                            private Byte val;
                            final /* synthetic */ Byte val$v;
                            final /* synthetic */ Long val$key;
                            final /* synthetic */ gnu.trove.decorator.TLongByteMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = by;
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

                            public Byte getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Byte setValue(Byte value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put((Long)this.val$key, (Byte)value);
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

            public boolean add(Map.Entry<Long, Byte> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Long key = (Long)((Map.Entry)o).getKey();
                this.this$0._map.remove((long)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Long, Byte>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        if (!(val instanceof Byte)) return false;
        if (!this._map.containsValue((byte)this.unwrapValue((Object)val))) return false;
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
    public void putAll(Map<? extends Long, ? extends Byte> map) {
        Iterator<Map.Entry<? extends Long, ? extends Byte>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Long, ? extends Byte> e = it.next();
            this.put((Long)e.getKey(), (Byte)e.getValue());
        }
    }

    protected Long wrapKey(long k) {
        return Long.valueOf((long)k);
    }

    protected long unwrapKey(Object key) {
        return ((Long)key).longValue();
    }

    protected Byte wrapValue(byte k) {
        return Byte.valueOf((byte)k);
    }

    protected byte unwrapValue(Object value) {
        return ((Byte)value).byteValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TLongByteMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

