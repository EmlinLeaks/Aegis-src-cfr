/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.impl.sync.TSynchronizedLongCollection;
import gnu.trove.set.TLongSet;

public class TSynchronizedLongSet
extends TSynchronizedLongCollection
implements TLongSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedLongSet(TLongSet s) {
        super((TLongCollection)s);
    }

    public TSynchronizedLongSet(TLongSet s, Object mutex) {
        super((TLongCollection)s, (Object)mutex);
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

