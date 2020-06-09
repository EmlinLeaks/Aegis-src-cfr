/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableFloatList;
import gnu.trove.list.TFloatList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessFloatList
extends TUnmodifiableFloatList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessFloatList(TFloatList list) {
        super((TFloatList)list);
    }

    @Override
    public TFloatList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessFloatList((TFloatList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableFloatList((TFloatList)this.list);
    }
}

