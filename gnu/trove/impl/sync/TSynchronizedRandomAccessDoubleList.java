/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedDoubleList;
import gnu.trove.list.TDoubleList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessDoubleList
extends TSynchronizedDoubleList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessDoubleList(TDoubleList list) {
        super((TDoubleList)list);
    }

    public TSynchronizedRandomAccessDoubleList(TDoubleList list, Object mutex) {
        super((TDoubleList)list, (Object)mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TDoubleList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedRandomAccessDoubleList((TDoubleList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    private Object writeReplace() {
        return new TSynchronizedDoubleList((TDoubleList)this.list);
    }
}

