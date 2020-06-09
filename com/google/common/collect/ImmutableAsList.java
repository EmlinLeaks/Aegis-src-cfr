/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;

@GwtCompatible(serializable=true, emulated=true)
abstract class ImmutableAsList<E>
extends ImmutableList<E> {
    ImmutableAsList() {
    }

    abstract ImmutableCollection<E> delegateCollection();

    @Override
    public boolean contains(Object target) {
        return this.delegateCollection().contains((Object)target);
    }

    @Override
    public int size() {
        return this.delegateCollection().size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegateCollection().isEmpty();
    }

    @Override
    boolean isPartialView() {
        return this.delegateCollection().isPartialView();
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException((String)"Use SerializedForm");
    }

    @GwtIncompatible
    @Override
    Object writeReplace() {
        return new SerializedForm(this.delegateCollection());
    }
}

