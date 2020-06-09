/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedFloatList;
import gnu.trove.list.TFloatList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessFloatList
extends TSynchronizedFloatList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessFloatList(TFloatList list) {
        super((TFloatList)list);
    }

    public TSynchronizedRandomAccessFloatList(TFloatList list, Object mutex) {
        super((TFloatList)list, (Object)mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TFloatList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedRandomAccessFloatList((TFloatList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    private Object writeReplace() {
        return new TSynchronizedFloatList((TFloatList)this.list);
    }
}

