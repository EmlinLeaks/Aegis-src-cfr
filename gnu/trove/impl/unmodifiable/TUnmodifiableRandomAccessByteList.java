/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableByteList;
import gnu.trove.list.TByteList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessByteList
extends TUnmodifiableByteList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessByteList(TByteList list) {
        super((TByteList)list);
    }

    @Override
    public TByteList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessByteList((TByteList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableByteList((TByteList)this.list);
    }
}

