/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.impl.sync.TSynchronizedRandomAccessFloatList;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedFloatList
extends TSynchronizedFloatCollection
implements TFloatList {
    static final long serialVersionUID = -7754090372962971524L;
    final TFloatList list;

    public TSynchronizedFloatList(TFloatList list) {
        super((TFloatCollection)list);
        this.list = list;
    }

    public TSynchronizedFloatList(TFloatList list, Object mutex) {
        super((TFloatCollection)list, (Object)mutex);
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
    public float get(int index) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.get((int)index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float set(int index, float element) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.set((int)index, (float)element);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, float[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (float[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, float[] values, int valOffset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (float[])values, (int)valOffset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float replace(int offset, float val) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.replace((int)offset, (float)val);
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
    public float removeAt(int offset) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.removeAt((int)offset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(float[] vals) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((float[])vals);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(float[] vals, int offset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((float[])vals, (int)offset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, float value) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (float)value);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, float[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (float[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, float[] values, int valOffset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (float[])values, (int)valOffset, (int)len);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(float o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((float)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(float o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((float)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TFloatList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedFloatList((TFloatList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] toArray(int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] toArray(float[] dest, int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((float[])dest, (int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] toArray(float[] dest, int source_pos, int dest_pos, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((float[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(int offset, float value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((int)offset, (float)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(int offset, float value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((int)offset, (float)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(float val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((float)val);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(int fromIndex, int toIndex, float val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((int)fromIndex, (int)toIndex, (float)val);
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
    public int binarySearch(float value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((float)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int binarySearch(float value, int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((float)value, (int)fromIndex, (int)toIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TFloatList grep(TFloatProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.grep((TFloatProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TFloatList inverseGrep(TFloatProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.inverseGrep((TFloatProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float max() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.max();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float min() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.min();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float sum() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.sum();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachDescending(TFloatProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.forEachDescending((TFloatProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TFloatFunction function) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.transformValues((TFloatFunction)function);
        // MONITOREXIT : object
        return;
    }

    private Object readResolve() {
        TSynchronizedFloatList tSynchronizedFloatList;
        if (this.list instanceof RandomAccess) {
            tSynchronizedFloatList = new TSynchronizedRandomAccessFloatList((TFloatList)this.list);
            return tSynchronizedFloatList;
        }
        tSynchronizedFloatList = this;
        return tSynchronizedFloatList;
    }
}

