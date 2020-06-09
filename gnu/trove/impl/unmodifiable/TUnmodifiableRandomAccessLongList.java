/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableLongList;
import gnu.trove.list.TLongList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessLongList
extends TUnmodifiableLongList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessLongList(TLongList list) {
        super((TLongList)list);
    }

    @Override
    public TLongList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessLongList((TLongList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableLongList((TLongList)this.list);
    }
}

