/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.sync.TSynchronizedByteCollection;
import gnu.trove.impl.sync.TSynchronizedRandomAccessByteList;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedByteList
extends TSynchronizedByteCollection
implements TByteList {
    static final long serialVersionUID = -7754090372962971524L;
    final TByteList list;

    public TSynchronizedByteList(TByteList list) {
        super((TByteCollection)list);
        this.list = list;
    }

    public TSynchronizedByteList(TByteList list, Object mutex) {
        super((TByteCollection)list, (Object)mutex);
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
    public byte get(int index) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.get((int)index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte set(int index, byte element) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.set((int)index, (byte)element);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, byte[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (byte[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set(int offset, byte[] values, int valOffset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.set((int)offset, (byte[])values, (int)valOffset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte replace(int offset, byte val) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.replace((int)offset, (byte)val);
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
    public byte removeAt(int offset) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.removeAt((int)offset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(byte[] vals) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((byte[])vals);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(byte[] vals, int offset, int length) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.add((byte[])vals, (int)offset, (int)length);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, byte value) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (byte)value);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, byte[] values) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (byte[])values);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(int offset, byte[] values, int valOffset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.insert((int)offset, (byte[])values, (int)valOffset, (int)len);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(byte o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((byte)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(byte o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((byte)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TByteList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedByteList((TByteList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] toArray(int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] toArray(byte[] dest, int offset, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((byte[])dest, (int)offset, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.toArray((byte[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(int offset, byte value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.indexOf((int)offset, (byte)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(int offset, byte value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.lastIndexOf((int)offset, (byte)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(byte val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((byte)val);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fill(int fromIndex, int toIndex, byte val) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.fill((int)fromIndex, (int)toIndex, (byte)val);
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
    public int binarySearch(byte value) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((byte)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int binarySearch(byte value, int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.binarySearch((byte)value, (int)fromIndex, (int)toIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TByteList grep(TByteProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.grep((TByteProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TByteList inverseGrep(TByteProcedure condition) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.inverseGrep((TByteProcedure)condition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte max() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.max();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte min() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.min();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte sum() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.sum();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachDescending(TByteProcedure procedure) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.list.forEachDescending((TByteProcedure)procedure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TByteFunction function) {
        Object object = this.mutex;
        // MONITORENTER : object
        this.list.transformValues((TByteFunction)function);
        // MONITOREXIT : object
        return;
    }

    private Object readResolve() {
        TSynchronizedByteList tSynchronizedByteList;
        if (this.list instanceof RandomAccess) {
            tSynchronizedByteList = new TSynchronizedRandomAccessByteList((TByteList)this.list);
            return tSynchronizedByteList;
        }
        tSynchronizedByteList = this;
        return tSynchronizedByteList;
    }
}

