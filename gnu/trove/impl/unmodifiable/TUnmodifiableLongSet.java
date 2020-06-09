/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TLongCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection;
import gnu.trove.set.TLongSet;
import java.io.Serializable;

public class TUnmodifiableLongSet
extends TUnmodifiableLongCollection
implements TLongSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableLongSet(TLongSet s) {
        super((TLongCollection)s);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (this.c.equals((Object)o)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.c.hashCode();
    }
}

