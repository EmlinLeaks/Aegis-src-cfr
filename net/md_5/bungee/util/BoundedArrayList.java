/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.util;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;

public class BoundedArrayList<E>
extends ArrayList<E> {
    private final int maxSize;

    public BoundedArrayList(int maxSize) {
        this.maxSize = maxSize;
    }

    private void checkSize(int increment) {
        Preconditions.checkState((boolean)(this.size() + increment <= this.maxSize), (String)"Adding %s elements would exceed capacity of %s", (int)increment, (int)this.maxSize);
    }

    @Override
    public boolean add(E e) {
        this.checkSize((int)1);
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        this.checkSize((int)1);
        super.add((int)index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        this.checkSize((int)c.size());
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        this.checkSize((int)c.size());
        return super.addAll((int)index, c);
    }
}

