/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedIntList;
import gnu.trove.list.TIntList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessIntList
extends TSynchronizedIntList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessIntList(TIntList list) {
        super((TIntList)list);
    }

    public TSynchronizedRandomAccessIntList(TIntList list, Object mutex) {
        super((TIntList)list, (Object)mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TIntList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return new TSynchronizedRandomAccessIntList((TIntList)this.list.subList((int)fromIndex, (int)toIndex), (Object)this.mutex);
    }

    private Object writeReplace() {
        return new TSynchronizedIntList((TIntList)this.list);
    }
}

