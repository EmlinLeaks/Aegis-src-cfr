/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableCharList;
import gnu.trove.list.TCharList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessCharList
extends TUnmodifiableCharList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessCharList(TCharList list) {
        super((TCharList)list);
    }

    @Override
    public TCharList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessCharList((TCharList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableCharList((TCharList)this.list);
    }
}

