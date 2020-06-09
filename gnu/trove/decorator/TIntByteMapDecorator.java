/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TIntByteMapDecorator;
import gnu.trove.map.TIntByteMap;
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

public class TIntByteMapDecorator
extends AbstractMap<Integer, Byte>
implements Map<Integer, Byte>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TIntByteMap _map;

    public TIntByteMapDecorator() {
    }

    public TIntByteMapDecorator(TIntByteMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TIntByteMap getMap() {
        return this._map;
    }

    @Override
    public Byte put(Integer key, Byte value) {
        int k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        byte v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        byte retval = this._map.put((int)k, (byte)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((byte)retval);
        return null;
    }

    @Override
    public Byte get(Object key) {
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        byte v = this._map.get((int)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((byte)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Byte remove(Object key) {
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        byte v = this._map.remove((int)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((byte)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Integer, Byte>> entrySet() {
        return new AbstractSet<Map.Entry<Integer, Byte>>((TIntByteMapDecorator)this){
            final /* synthetic */ TIntByteMapDecorator this$0;
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

            public Iterator<Map.Entry<Integer, Byte>> iterator() {
                return new Iterator<Map.Entry<Integer, Byte>>(this){
                    private final gnu.trove.iterator.TIntByteIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Integer, Byte> next() {
                        this.it.advance();
                        int ik = this.it.key();
                        Integer key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((int)ik);
                        byte iv = this.it.value();
                        Byte v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((byte)iv);
                        return new Map.Entry<Integer, Byte>(this, (Byte)v, (Integer)key){
                            private Byte val;
                            final /* synthetic */ Byte val$v;
                            final /* synthetic */ Integer val$key;
                            final /* synthetic */ gnu.trove.decorator.TIntByteMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = by;
                                this.val$key = n;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals((Object)this.val)) return false;
                                return true;
                            }

                            public Integer getKey() {
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
                                return this.this$2.this$1.this$0.put((Integer)this.val$key, (Byte)value);
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

            public boolean add(Map.Entry<Integer, Byte> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Integer key = (Integer)((Map.Entry)o).getKey();
                this.this$0._map.remove((int)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Integer, Byte>> c) {
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
            return this._map.containsKey((int)this._map.getNoEntryKey());
        }
        if (!(key instanceof Integer)) return false;
        if (!this._map.containsKey((int)this.unwrapKey((Object)key))) return false;
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
    public void putAll(Map<? extends Integer, ? extends Byte> map) {
        Iterator<Map.Entry<? extends Integer, ? extends Byte>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Integer, ? extends Byte> e = it.next();
            this.put((Integer)e.getKey(), (Byte)e.getValue());
        }
    }

    protected Integer wrapKey(int k) {
        return Integer.valueOf((int)k);
    }

    protected int unwrapKey(Object key) {
        return ((Integer)key).intValue();
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
        this._map = (TIntByteMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

