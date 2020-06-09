/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCollection;
import gnu.trove.set.TCharSet;
import java.io.Serializable;

public class TUnmodifiableCharSet
extends TUnmodifiableCharCollection
implements TCharSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableCharSet(TCharSet s) {
        super((TCharCollection)s);
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

