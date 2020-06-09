/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableIntList;
import gnu.trove.list.TIntList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessIntList
extends TUnmodifiableIntList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessIntList(TIntList list) {
        super((TIntList)list);
    }

    @Override
    public TIntList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessIntList((TIntList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableIntList((TIntList)this.list);
    }
}

