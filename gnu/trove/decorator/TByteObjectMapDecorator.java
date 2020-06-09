/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TByteObjectMapDecorator;
import gnu.trove.map.TByteObjectMap;
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

public class TByteObjectMapDecorator<V>
extends AbstractMap<Byte, V>
implements Map<Byte, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TByteObjectMap<V> _map;

    public TByteObjectMapDecorator() {
    }

    public TByteObjectMapDecorator(TByteObjectMap<V> map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TByteObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Byte key, V value) {
        byte k;
        if (key == null) {
            k = this._map.getNoEntryKey();
            return (V)((V)this._map.put((byte)k, value));
        }
        k = this.unwrapKey((Byte)key);
        return (V)this._map.put((byte)k, value);
    }

    @Override
    public V get(Object key) {
        byte k;
        if (key != null) {
            if (!(key instanceof Byte)) return (V)null;
            k = this.unwrapKey((Byte)((Byte)key));
            return (V)((V)this._map.get((byte)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.get((byte)k);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public V remove(Object key) {
        byte k;
        if (key != null) {
            if (!(key instanceof Byte)) return (V)null;
            k = this.unwrapKey((Byte)((Byte)key));
            return (V)((V)this._map.remove((byte)k));
        }
        k = this._map.getNoEntryKey();
        return (V)this._map.remove((byte)k);
    }

    @Override
    public Set<Map.Entry<Byte, V>> entrySet() {
        return new AbstractSet<Map.Entry<Byte, V>>((TByteObjectMapDecorator)this){
            final /* synthetic */ TByteObjectMapDecorator this$0;
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

            public Iterator<Map.Entry<Byte, V>> iterator() {
                return new Iterator<Map.Entry<Byte, V>>(this){
                    private final gnu.trove.iterator.TByteObjectIterator<V> it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Byte, V> next() {
                        this.it.advance();
                        byte k = this.it.key();
                        Byte key = k == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((byte)k);
                        V v = this.it.value();
                        return new Map.Entry<Byte, V>(this, v, (Byte)key){
                            private V val;
                            final /* synthetic */ Object val$v;
                            final /* synthetic */ Byte val$key;
                            final /* synthetic */ gnu.trove.decorator.TByteObjectMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = object;
                                this.val$key = by;
                                this.val = this.val$v;
                            }

                            public boolean equals(Object o) {
                                if (!(o instanceof Map.Entry)) return false;
                                if (!((Map.Entry)o).getKey().equals((Object)this.val$key)) return false;
                                if (!((Map.Entry)o).getValue().equals(this.val)) return false;
                                return true;
                            }

                            public Byte getKey() {
                                return this.val$key;
                            }

                            public V getValue() {
                                return (V)this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public V setValue(V value) {
                                this.val = value;
                                return (V)this.this$2.this$1.this$0.put((Byte)this.val$key, value);
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

            public boolean add(Map.Entry<Byte, V> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Byte key = (Byte)((Map.Entry)o).getKey();
                this.this$0._map.remove((byte)this.this$0.unwrapKey((Byte)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Byte, V>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        return this._map.containsValue((Object)val);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return this._map.containsKey((byte)this._map.getNoEntryKey());
        }
        if (!(key instanceof Byte)) return false;
        if (!this._map.containsKey((byte)((Byte)key).byteValue())) return false;
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
    public void putAll(Map<? extends Byte, ? extends V> map) {
        Iterator<Map.Entry<Byte, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Byte, V> e = it.next();
            this.put((Byte)e.getKey(), e.getValue());
        }
    }

    protected Byte wrapKey(byte k) {
        return Byte.valueOf((byte)k);
    }

    protected byte unwrapKey(Byte key) {
        return key.byteValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TByteObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject(this._map);
    }
}

