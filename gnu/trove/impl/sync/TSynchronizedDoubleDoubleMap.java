/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedDoubleSet;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.procedure.TDoubleDoubleProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleDoubleMap
implements TDoubleDoubleMap,
Serializable {
    private static final long serialVersionUID = 1978198479659022715L;
    private final TDoubleDoubleMap m;
    final Object mutex;
    private transient TDoubleSet keySet = null;
    private transient TDoubleCollection values = null;

    public TSynchronizedDoubleDoubleMap(TDoubleDoubleMap m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
        this.mutex = this;
    }

    public TSynchronizedDoubleDoubleMap(TDoubleDoubleMap m, Object mutex) {
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
    public boolean containsKey(double key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.containsKey((double)key);
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
    public double get(double key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.get((double)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double put(double key, double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.put((double)key, (double)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double remove(double key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.remove((double)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(Map<? extends Double, ? extends Double> map) {
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
    public void putAll(TDoubleDoubleMap map) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.m.putAll((TDoubleDoubleMap)map);
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
    public TDoubleSet keySet() {
        Object object = this.mutex;
        // MONITORENTER : object
        if (this.keySet == null) {
            this.keySet = new TSynchronizedDoubleSet((TDoubleSet)this.m.keySet(), (Object)this.mutex);
        }
        // MONITOREXIT : object
        return this.keySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] keys() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.keys();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] keys(double[] array) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.keys((double[])array);
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
    public TDoubleDoubleIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public double getNoEntryKey() {
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
    public double putIfAbsent(double key, double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.putIfAbsent((double)key, (double)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.forEachKey((TDoubleProcedure)procedure);
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
    public boolean forEachEntry(TDoubleDoubleProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.forEachEntry((TDoubleDoubleProcedure)procedure);
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
    public boolean retainEntries(TDoubleDoubleProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.retainEntries((TDoubleDoubleProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean increment(double key) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.increment((double)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean adjustValue(double key, double amount) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.adjustValue((double)key, (double)amount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double adjustOrPutValue(double key, double adjust_amount, double put_amount) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.m.adjustOrPutValue((double)key, (double)adjust_amount, (double)put_amount);
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

