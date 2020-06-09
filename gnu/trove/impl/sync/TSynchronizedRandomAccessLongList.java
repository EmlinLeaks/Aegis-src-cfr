/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedLongList;
import gnu.trove.list.TLongList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessLongList
extends TSynchronizedLongList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessLongList(TLongList list) {
        super((TLongList)list);
    }

    public TSynchronizedRandomAccessLongList(TLongList list, Object mutex) {
        super((TLongList)list, (Object)mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TLongList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedRandomAccessLongList((TLongList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    private Object writeReplace() {
        return new TSynchronizedLongList((TLongList)this.list);
    }
}

