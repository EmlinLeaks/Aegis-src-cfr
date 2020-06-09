/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TIntCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCollection;
import gnu.trove.set.TIntSet;
import java.io.Serializable;

public class TUnmodifiableIntSet
extends TUnmodifiableIntCollection
implements TIntSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableIntSet(TIntSet s) {
        super((TIntCollection)s);
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

