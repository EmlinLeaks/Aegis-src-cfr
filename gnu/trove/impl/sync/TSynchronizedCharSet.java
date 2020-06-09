/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.impl.sync.TSynchronizedCharCollection;
import gnu.trove.set.TCharSet;

public class TSynchronizedCharSet
extends TSynchronizedCharCollection
implements TCharSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedCharSet(TCharSet s) {
        super((TCharCollection)s);
    }

    public TSynchronizedCharSet(TCharSet s, Object mutex) {
        super((TCharCollection)s, (Object)mutex);
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

