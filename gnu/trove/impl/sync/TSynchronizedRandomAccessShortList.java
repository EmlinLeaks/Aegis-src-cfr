/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedShortList;
import gnu.trove.list.TShortList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessShortList
extends TSynchronizedShortList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessShortList(TShortList list) {
        super((TShortList)list);
    }

    public TSynchronizedRandomAccessShortList(TShortList list, Object mutex) {
        super((TShortList)list, (Object)mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TShortList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedRandomAccessShortList((TShortList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    private Object writeReplace() {
        return new TSynchronizedShortList((TShortList)this.list);
    }
}

