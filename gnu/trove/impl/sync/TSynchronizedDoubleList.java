/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedDoubleList
extends TSynchronizedDoubleCollection
implements TDoubleList {
    static final long serialVersionUID = -7754090372962971524L;
    final TDoubleList list;

    public TSynchronizedDoubleList(TDoubleList list) {
        super((TDoubleCollection)list);
        this.list = list;
    }

    public TSynchronizedDoubleList(TDoubleList list, Object mutex) {
        super((TDoubleCollection)list, (Object)mutex);
        this.list = list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean equals(Object o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.equals((Object)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int hashCode() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.hashCode();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double get(int index) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.get((int)index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double set(int index, double element) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.set((int)index, (double)element);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, double[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (double[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, double[] values, int valOffset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (double[])values, (int)valOffset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double replace(int offset, double val) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.replace((int)offset, (double)val);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(int offset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.remove((int)offset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double removeAt(int offset) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.removeAt((int)offset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(double[] vals) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((double[])vals);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(double[] vals, int offset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((double[])vals, (int)offset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (double)value);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, double[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (double[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, double[] values, int valOffset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (double[])values, (int)valOffset, (int)len);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(double o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((double)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(double o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((double)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TDoubleList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedDoubleList((TDoubleList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] toArray(int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] toArray(double[] dest, int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((double[])dest, (int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((double[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(int offset, double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((int)offset, (double)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(int offset, double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((int)offset, (double)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(double val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((double)val);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(int fromIndex, int toIndex, double val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((int)fromIndex, (int)toIndex, (double)val);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void reverse() {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.reverse();
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void reverse(int from, int to) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.reverse((int)from, (int)to);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shuffle(Random rand) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.shuffle((Random)rand);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sort() {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.sort();
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sort(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.sort((int)fromIndex, (int)toIndex);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int binarySearch(double value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((double)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int binarySearch(double value, int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((double)value, (int)fromIndex, (int)toIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TDoubleList grep(TDoubleProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.grep((TDoubleProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TDoubleList inverseGrep(TDoubleProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.inverseGrep((TDoubleProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double max() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.max();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double min() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.min();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double sum() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.sum();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachDescending(TDoubleProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.forEachDescending((TDoubleProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TDoubleFunction function) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.transformValues((TDoubleFunction)function);
        // MONITOREXIT : object
        return;
    }

    private Object readResolve() {
        TSynchronizedDoubleList tSynchronizedDoubleList;
        if (this.list instanceof RandomAccess) {
            tSynchronizedDoubleList = new TSynchronizedRandomAccessDoubleList((TDoubleList)this.list);
            return tSynchronizedDoubleList;
        }
        tSynchronizedDoubleList = this;
        return tSynchronizedDoubleList;
    }
}

