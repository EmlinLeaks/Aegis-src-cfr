/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.sync.TSynchronizedIntCollection;
import gnu.trove.impl.sync.TSynchronizedRandomAccessIntList;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedIntList
extends TSynchronizedIntCollection
implements TIntList {
    static final long serialVersionUID = -7754090372962971524L;
    final TIntList list;

    public TSynchronizedIntList(TIntList list) {
        super((TIntCollection)list);
        this.list = list;
    }

    public TSynchronizedIntList(TIntList list, Object mutex) {
        super((TIntCollection)list, (Object)mutex);
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
    public int get(int index) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.get((int)index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int set(int index, int element) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.set((int)index, (int)element);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, int[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (int[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, int[] values, int valOffset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (int[])values, (int)valOffset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int replace(int offset, int val) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.replace((int)offset, (int)val);
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
    public int removeAt(int offset) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.removeAt((int)offset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(int[] vals) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((int[])vals);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(int[] vals, int offset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((int[])vals, (int)offset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, int value) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (int)value);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, int[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (int[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, int[] values, int valOffset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (int[])values, (int)valOffset, (int)len);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(int o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((int)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(int o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((int)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TIntList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedIntList((TIntList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int[] toArray(int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int[] toArray(int[] dest, int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((int[])dest, (int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int[] toArray(int[] dest, int source_pos, int dest_pos, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((int[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(int offset, int value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((int)offset, (int)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(int offset, int value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((int)offset, (int)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(int val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((int)val);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(int fromIndex, int toIndex, int val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((int)fromIndex, (int)toIndex, (int)val);
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
    public int binarySearch(int value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((int)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int binarySearch(int value, int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((int)value, (int)fromIndex, (int)toIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TIntList grep(TIntProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.grep((TIntProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TIntList inverseGrep(TIntProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.inverseGrep((TIntProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int max() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.max();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int min() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.min();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sum() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.sum();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachDescending(TIntProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.forEachDescending((TIntProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TIntFunction function) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.transformValues((TIntFunction)function);
        // MONITOREXIT : object
        return;
    }

    private Object readResolve() {
        TSynchronizedIntList tSynchronizedIntList;
        if (this.list instanceof RandomAccess) {
            tSynchronizedIntList = new TSynchronizedRandomAccessIntList((TIntList)this.list);
            return tSynchronizedIntList;
        }
        tSynchronizedIntList = this;
        return tSynchronizedIntList;
    }
}

