/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TShortIntMapDecorator;
import gnu.trove.map.TShortIntMap;
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

public class TShortIntMapDecorator
extends AbstractMap<Short, Integer>
implements Map<Short, Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TShortIntMap _map;

    public TShortIntMapDecorator() {
    }

    public TShortIntMapDecorator(TShortIntMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TShortIntMap getMap() {
        return this._map;
    }

    @Override
    public Integer put(Short key, Integer value) {
        short k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        int v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        int retval = this._map.put((short)k, (int)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((int)retval);
        return null;
    }

    @Override
    public Integer get(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.get((short)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((int)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Integer remove(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.remove((short)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((int)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Short, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<Short, Integer>>((TShortIntMapDecorator)this){
            final /* synthetic */ TShortIntMapDecorator this$0;
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

            public Iterator<Map.Entry<Short, Integer>> iterator() {
                return new Iterator<Map.Entry<Short, Integer>>(this){
                    private final gnu.trove.iterator.TShortIntIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Short, Integer> next() {
                        this.it.advance();
                        short ik = this.it.key();
                        Short key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((short)ik);
                        int iv = this.it.value();
                        Integer v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((int)iv);
                        return new Map.Entry<Short, Integer>(this, (Integer)v, (Short)key){
                            private Integer val;
                            final /* synthetic */ Integer val$v;
                            final /* synthetic */ Short val$key;
                            final /* synthetic */ gnu.trove.decorator.TShortIntMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = n;
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

                            public Integer getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Integer setValue(Integer value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put((Short)this.val$key, (Integer)value);
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

            public boolean add(Map.Entry<Short, Integer> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Short key = (Short)((Map.Entry)o).getKey();
                this.this$0._map.remove((short)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Short, Integer>> c) {
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
    public void putAll(Map<? extends Short, ? extends Integer> map) {
        Iterator<Map.Entry<? extends Short, ? extends Integer>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Short, ? extends Integer> e = it.next();
            this.put((Short)e.getKey(), (Integer)e.getValue());
        }
    }

    protected Short wrapKey(short k) {
        return Short.valueOf((short)k);
    }

    protected short unwrapKey(Object key) {
        return ((Short)key).shortValue();
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
        this._map = (TShortIntMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

