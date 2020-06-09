/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedLongDoubleMap
implements TLongDoubleMap,
Serializable {
    private static final long serialVersionUID = 1978198479659022715L;
    private final TLongDoubleMap m;
    final Object mutex;
    private transient TLongSet keySet = null;
    private transient TDoubleCollection values = null;

    public TSynchronizedLongDoubleMap(TLongDoubleMap m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
        this.mutex = this;
    }

    public TSynchronizedLongDoubleMap(TLongDoubleMap m, Object mutex) {
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
    public boolean containsKey(long key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.containsKey((long)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsValue(double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.containsValue((double)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double get(long key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.get((long)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double put(long key, double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.put((long)key, (double)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double remove(long key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.remove((long)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(Map<? extends Long, ? extends Double> map) {
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
    public void putAll(TLongDoubleMap map) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.m.putAll((TLongDoubleMap)map);
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
    public TLongSet keySet() {
        Object object = this.mutex;
        // MONITORENTER : object
        if (this.keySet == null) {
            this.keySet = new TSynchronizedLongSet((TLongSet)this.m.keySet(), (Object)this.mutex);
        }
        // MONITOREXIT : object
        return this.keySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long[] keys() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.keys();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long[] keys(long[] array) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.keys((long[])array);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TDoubleCollection valueCollection() {
        Object object = this.mutex;
        // MONITORENTER : object
        if (this.values == null) {
            this.values = new TSynchronizedDoubleCollection((TDoubleCollection)this.m.valueCollection(), (Object)this.mutex);
        }
        // MONITOREXIT : object
        return this.values;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] values() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.values();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] values(double[] array) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.values((double[])array);
    }

    @Override
    public TLongDoubleIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double putIfAbsent(long key, double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.putIfAbsent((long)key, (double)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.forEachKey((TLongProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.forEachValue((TDoubleProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachEntry(TLongDoubleProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.forEachEntry((TLongDoubleProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TDoubleFunction function) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.m.transformValues((TDoubleFunction)function);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TLongDoubleProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.retainEntries((TLongDoubleProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean increment(long key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.increment((long)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean adjustValue(long key, double amount) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.adjustValue((long)key, (double)amount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double adjustOrPutValue(long key, double adjust_amount, double put_amount) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.adjustOrPutValue((long)key, (double)adjust_amount, (double)put_amount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.equals((Object)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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

