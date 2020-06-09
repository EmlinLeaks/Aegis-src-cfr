/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleList;
import gnu.trove.list.TDoubleList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessDoubleList
extends TUnmodifiableDoubleList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessDoubleList(TDoubleList list) {
        super((TDoubleList)list);
    }

    @Override
    public TDoubleList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessDoubleList((TDoubleList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableDoubleList((TDoubleList)this.list);
    }
}

