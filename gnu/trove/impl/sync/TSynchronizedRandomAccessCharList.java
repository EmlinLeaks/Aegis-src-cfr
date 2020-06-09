/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedCharList;
import gnu.trove.list.TCharList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessCharList
extends TSynchronizedCharList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessCharList(TCharList list) {
        super((TCharList)list);
    }

    public TSynchronizedRandomAccessCharList(TCharList list, Object mutex) {
        super((TCharList)list, (Object)mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TCharList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedRandomAccessCharList((TCharList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    private Object writeReplace() {
        return new TSynchronizedCharList((TCharList)this.list);
    }
}

