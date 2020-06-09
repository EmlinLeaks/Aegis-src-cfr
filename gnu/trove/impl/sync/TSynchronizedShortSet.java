/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.impl.sync.TSynchronizedShortCollection;
import gnu.trove.set.TShortSet;

public class TSynchronizedShortSet
extends TSynchronizedShortCollection
implements TShortSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedShortSet(TShortSet s) {
        super((TShortCollection)s);
    }

    public TSynchronizedShortSet(TShortSet s, Object mutex) {
        super((TShortCollection)s, (Object)mutex);
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

