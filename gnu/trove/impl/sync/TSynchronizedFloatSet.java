/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.set.TFloatSet;

public class TSynchronizedFloatSet
extends TSynchronizedFloatCollection
implements TFloatSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedFloatSet(TFloatSet s) {
        super((TFloatCollection)s);
    }

    public TSynchronizedFloatSet(TFloatSet s, Object mutex) {
        super((TFloatCollection)s, (Object)mutex);
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

