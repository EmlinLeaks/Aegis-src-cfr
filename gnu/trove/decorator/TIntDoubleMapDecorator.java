/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.decorator;

import gnu.trove.decorator.TIntDoubleMapDecorator;
import gnu.trove.map.TIntDoubleMap;
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

public class TIntDoubleMapDecorator
extends AbstractMap<Integer, Double>
implements Map<Integer, Double>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TIntDoubleMap _map;

    public TIntDoubleMapDecorator() {
    }

    public TIntDoubleMapDecorator(TIntDoubleMap map) {
        Objects.requireNonNull(map);
        this._map = map;
    }

    public TIntDoubleMap getMap() {
        return this._map;
    }

    @Override
    public Double put(Integer key, Double value) {
        int k = key == null ? this._map.getNoEntryKey() : this.unwrapKey((Object)key);
        double v = value == null ? this._map.getNoEntryValue() : this.unwrapValue((Object)value);
        double retval = this._map.put((int)k, (double)v);
        if (retval != this._map.getNoEntryValue()) return this.wrapValue((double)retval);
        return null;
    }

    @Override
    public Double get(Object key) {
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        double v = this._map.get((int)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((double)v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Double remove(Object key) {
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
            k = this.unwrapKey((Object)key);
        } else {
            k = this._map.getNoEntryKey();
        }
        double v = this._map.remove((int)k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue((double)v);
        return null;
    }

    @Override
    public Set<Map.Entry<Integer, Double>> entrySet() {
        return new AbstractSet<Map.Entry<Integer, Double>>((TIntDoubleMapDecorator)this){
            final /* synthetic */ TIntDoubleMapDecorator this$0;
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

            public Iterator<Map.Entry<Integer, Double>> iterator() {
                return new Iterator<Map.Entry<Integer, Double>>(this){
                    private final gnu.trove.iterator.TIntDoubleIterator it;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.it = this.this$1.this$0._map.iterator();
                    }

                    public Map.Entry<Integer, Double> next() {
                        this.it.advance();
                        int ik = this.it.key();
                        Integer key = ik == this.this$1.this$0._map.getNoEntryKey() ? null : this.this$1.this$0.wrapKey((int)ik);
                        double iv = this.it.value();
                        Double v = iv == this.this$1.this$0._map.getNoEntryValue() ? null : this.this$1.this$0.wrapValue((double)iv);
                        return new Map.Entry<Integer, Double>(this, (Double)v, (Integer)key){
                            private Double val;
                            final /* synthetic */ Double val$v;
                            final /* synthetic */ Integer val$key;
                            final /* synthetic */ gnu.trove.decorator.TIntDoubleMapDecorator$1$1 this$2;
                            {
                                this.this$2 = this$2;
                                this.val$v = d;
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

                            public Double getValue() {
                                return this.val;
                            }

                            public int hashCode() {
                                return this.val$key.hashCode() + this.val.hashCode();
                            }

                            public Double setValue(Double value) {
                                this.val = value;
                                return this.this$2.this$1.this$0.put((Integer)this.val$key, (Double)value);
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

            public boolean add(Map.Entry<Integer, Double> o) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                boolean modified = false;
                if (!this.contains((Object)o)) return modified;
                Integer key = (Integer)((Map.Entry)o).getKey();
                this.this$0._map.remove((int)this.this$0.unwrapKey((Object)key));
                return true;
            }

            public boolean addAll(java.util.Collection<? extends Map.Entry<Integer, Double>> c) {
                throw new java.lang.UnsupportedOperationException();
            }

            public void clear() {
                this.this$0.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        if (!(val instanceof Double)) return false;
        if (!this._map.containsValue((double)this.unwrapValue((Object)val))) return false;
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
    public void putAll(Map<? extends Integer, ? extends Double> map) {
        Iterator<Map.Entry<? extends Integer, ? extends Double>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Integer, ? extends Double> e = it.next();
            this.put((Integer)e.getKey(), (Double)e.getValue());
        }
    }

    protected Integer wrapKey(int k) {
        return Integer.valueOf((int)k);
    }

    protected int unwrapKey(Object key) {
        return ((Integer)key).intValue();
    }

    protected Double wrapValue(double k) {
        return Double.valueOf((double)k);
    }

    protected double unwrapValue(Object value) {
        return ((Double)value).doubleValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TIntDoubleMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeObject((Object)this._map);
    }
}

