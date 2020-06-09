/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TShortCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCollection;
import gnu.trove.set.TShortSet;
import java.io.Serializable;

public class TUnmodifiableShortSet
extends TUnmodifiableShortCollection
implements TShortSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableShortSet(TShortSet s) {
        super((TShortCollection)s);
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

