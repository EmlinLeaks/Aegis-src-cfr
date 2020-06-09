/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Multiset;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Multiset<E>
extends Collection<E> {
    public int count(@Nullable Object var1);

    @CanIgnoreReturnValue
    public int add(@Nullable E var1, int var2);

    @CanIgnoreReturnValue
    public int remove(@Nullable Object var1, int var2);

    @CanIgnoreReturnValue
    public int setCount(E var1, int var2);

    @CanIgnoreReturnValue
    public boolean setCount(E var1, int var2, int var3);

    public Set<E> elementSet();

    public Set<Entry<E>> entrySet();

    @Override
    public boolean equals(@Nullable Object var1);

    @Override
    public int hashCode();

    public String toString();

    @Override
    public Iterator<E> iterator();

    @Override
    public boolean contains(@Nullable Object var1);

    @Override
    public boolean containsAll(Collection<?> var1);

    @CanIgnoreReturnValue
    @Override
    public boolean add(E var1);

    @CanIgnoreReturnValue
    @Override
    public boolean remove(@Nullable Object var1);

    @CanIgnoreReturnValue
    @Override
    public boolean removeAll(Collection<?> var1);

    @CanIgnoreReturnValue
    @Override
    public boolean retainAll(Collection<?> var1);
}

