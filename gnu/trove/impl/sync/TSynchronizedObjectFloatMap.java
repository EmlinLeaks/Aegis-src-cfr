/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.sync.SynchronizedSet;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectFloatMap<K>
implements TObjectFloatMap<K>,
Serializable {
    private static final long serialVersionUID = 1978198479659022715L;
    private final TObjectFloatMap<K> m;
    final Object mutex;
    private transient Set<K> keySet = null;
    private transient TFloatCollection values = null;

    public TSynchronizedObjectFloatMap(TObjectFloatMap<K> m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
        this.mutex = this;
    }

    public TSynchronizedObjectFloatMap(TObjectFloatMap<K> m, Object mutex) {
        this.m = m;
        this.mutex = mutex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int size() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isEmpty() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKey(Object key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.containsKey((Object)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsValue(float value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.containsValue((float)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float get(Object key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.get((Object)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float put(K key, float value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.put(key, (float)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float remove(Object key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.remove((Object)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(Map<? extends K, ? extends Float> map) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.m.putAll(map);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(TObjectFloatMap<? extends K> map) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.m.putAll(map);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Object object = this.mutex;
        // MONITORENTER : object
        this.m.clear();
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<K> keySet() {
        Object object = this.mutex;
        // MONITORENTER : object
        if (this.keySet == null) {
            this.keySet = new SynchronizedSet<K>(this.m.keySet(), (Object)this.mutex);
        }
        // MONITOREXIT : object
        return this.keySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object[] keys() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.keys();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public K[] keys(K[] array) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.keys(array);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TFloatCollection valueCollection() {
        Object object = this.mutex;
        // MONITORENTER : object
        if (this.values == null) {
            this.values = new TSynchronizedFloatCollection((TFloatCollection)this.m.valueCollection(), (Object)this.mutex);
        }
        // MONITOREXIT : object
        return this.values;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] values() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.values();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] values(float[] array) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.values((float[])array);
    }

    @Override
    public TObjectFloatIterator<K> iterator() {
        return this.m.iterator();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float putIfAbsent(K key, float value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.putIfAbsent(key, (float)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.forEachKey(procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.forEachValue((TFloatProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachEntry(TObjectFloatProcedure<? super K> procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.forEachEntry(procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TFloatFunction function) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.m.transformValues((TFloatFunction)function);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TObjectFloatProcedure<? super K> procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.retainEntries(procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean increment(K key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.increment(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean adjustValue(K key, float amount) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.adjustValue(key, (float)amount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float adjustOrPutValue(K key, float adjust_amount, float put_amount) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.adjustOrPutValue(key, (float)adjust_amount, (float)put_amount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean equals(Object o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.equals((Object)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int hashCode() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.hashCode();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        Object object = this.mutex;
        // MONITORENTER : object
        s.defaultWriteObject();
        // MONITOREXIT : object
        return;
    }
}

