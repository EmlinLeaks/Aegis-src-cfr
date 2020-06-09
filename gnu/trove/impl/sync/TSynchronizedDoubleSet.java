/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.set.TDoubleSet;

public class TSynchronizedDoubleSet
extends TSynchronizedDoubleCollection
implements TDoubleSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedDoubleSet(TDoubleSet s) {
        super((TDoubleCollection)s);
    }

    public TSynchronizedDoubleSet(TDoubleSet s, Object mutex) {
        super((TDoubleCollection)s, (Object)mutex);
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

