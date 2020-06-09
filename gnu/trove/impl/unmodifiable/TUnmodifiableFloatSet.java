/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TFloatCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;

public class TUnmodifiableFloatSet
extends TUnmodifiableFloatCollection
implements TFloatSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableFloatSet(TFloatSet s) {
        super((TFloatCollection)s);
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

