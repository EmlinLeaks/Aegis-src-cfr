/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.sync.TSynchronizedCharCollection;
import gnu.trove.impl.sync.TSynchronizedRandomAccessCharList;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedCharList
extends TSynchronizedCharCollection
implements TCharList {
    static final long serialVersionUID = -7754090372962971524L;
    final TCharList list;

    public TSynchronizedCharList(TCharList list) {
        super((TCharCollection)list);
        this.list = list;
    }

    public TSynchronizedCharList(TCharList list, Object mutex) {
        super((TCharCollection)list, (Object)mutex);
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
    public char get(int index) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.get((int)index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char set(int index, char element) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.set((int)index, (char)element);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, char[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (char[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, char[] values, int valOffset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (char[])values, (int)valOffset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char replace(int offset, char val) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.replace((int)offset, (char)val);
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
    public char removeAt(int offset) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.removeAt((int)offset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(char[] vals) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((char[])vals);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(char[] vals, int offset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((char[])vals, (int)offset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, char value) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (char)value);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, char[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (char[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, char[] values, int valOffset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (char[])values, (int)valOffset, (int)len);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(char o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((char)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(char o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((char)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TCharList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedCharList((TCharList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char[] toArray(int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char[] toArray(char[] dest, int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((char[])dest, (int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char[] toArray(char[] dest, int source_pos, int dest_pos, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((char[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(int offset, char value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((int)offset, (char)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(int offset, char value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((int)offset, (char)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(char val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((char)val);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(int fromIndex, int toIndex, char val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((int)fromIndex, (int)toIndex, (char)val);
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
    public int binarySearch(char value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((char)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int binarySearch(char value, int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((char)value, (int)fromIndex, (int)toIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TCharList grep(TCharProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.grep((TCharProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TCharList inverseGrep(TCharProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.inverseGrep((TCharProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char max() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.max();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char min() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.min();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char sum() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.sum();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachDescending(TCharProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.forEachDescending((TCharProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TCharFunction function) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.transformValues((TCharFunction)function);
        // MONITOREXIT : object
        return;
    }

    private Object readResolve() {
        TSynchronizedCharList tSynchronizedCharList;
        if (this.list instanceof RandomAccess) {
            tSynchronizedCharList = new TSynchronizedRandomAccessCharList((TCharList)this.list);
            return tSynchronizedCharList;
        }
        tSynchronizedCharList = this;
        return tSynchronizedCharList;
    }
}

