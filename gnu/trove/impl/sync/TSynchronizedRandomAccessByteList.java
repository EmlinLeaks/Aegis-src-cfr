/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedByteList;
import gnu.trove.list.TByteList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessByteList
extends TSynchronizedByteList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessByteList(TByteList list) {
        super((TByteList)list);
    }

    public TSynchronizedRandomAccessByteList(TByteList list, Object mutex) {
        super((TByteList)list, (Object)mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TByteList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedRandomAccessByteList((TByteList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    private Object writeReplace() {
        return new TSynchronizedByteList((TByteList)this.list);
    }
}

