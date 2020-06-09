/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection;
import gnu.trove.set.TByteSet;
import java.io.Serializable;

public class TUnmodifiableByteSet
extends TUnmodifiableByteCollection
implements TByteSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableByteSet(TByteSet s) {
        super((TByteCollection)s);
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

