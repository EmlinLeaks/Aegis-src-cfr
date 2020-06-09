/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.impl.sync.TSynchronizedIntCollection;
import gnu.trove.set.TIntSet;

public class TSynchronizedIntSet
extends TSynchronizedIntCollection
implements TIntSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedIntSet(TIntSet s) {
        super((TIntCollection)s);
    }

    public TSynchronizedIntSet(TIntSet s, Object mutex) {
        super((TIntCollection)s, (Object)mutex);
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

