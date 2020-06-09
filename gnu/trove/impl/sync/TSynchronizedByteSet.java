/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.impl.sync.TSynchronizedByteCollection;
import gnu.trove.set.TByteSet;

public class TSynchronizedByteSet
extends TSynchronizedByteCollection
implements TByteSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedByteSet(TByteSet s) {
        super((TByteCollection)s);
    }

    public TSynchronizedByteSet(TByteSet s, Object mutex) {
        super((TByteCollection)s, (Object)mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean equals(Object o) {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.c.equals((Object)o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int hashCode() {
        Object object = this.mutex;
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.c.hashCode();
    }
}

