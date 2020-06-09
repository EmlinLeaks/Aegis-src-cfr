/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.CartesianList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.math.IntMath;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible
final class CartesianList<E>
extends AbstractList<List<E>>
implements RandomAccess {
    private final transient ImmutableList<List<E>> axes;
    private final transient int[] axesSizeProduct;

    static <E> List<List<E>> create(List<? extends List<? extends E>> lists) {
        ImmutableList.Builder<E> axesBuilder = new ImmutableList.Builder<E>((int)lists.size());
        Iterator<List<E>> i$ = lists.iterator();
        while (i$.hasNext()) {
            List<? extends E> list = i$.next();
            ImmutableList<E> copy = ImmutableList.copyOf(list);
            if (copy.isEmpty()) {
                return ImmutableList.of();
            }
            axesBuilder.add(copy);
        }
        return new CartesianList<E>(axesBuilder.build());
    }

    CartesianList(ImmutableList<List<E>> axes) {
        this.axes = axes;
        int[] axesSizeProduct = new int[axes.size() + 1];
        axesSizeProduct[axes.size()] = 1;
        try {
            for (int i = axes.size() - 1; i >= 0; --i) {
                axesSizeProduct[i] = IntMath.checkedMultiply((int)axesSizeProduct[i + 1], (int)((List)axes.get((int)i)).size());
            }
        }
        catch (ArithmeticException e) {
            throw new IllegalArgumentException((String)"Cartesian product too large; must have size at most Integer.MAX_VALUE");
        }
        this.axesSizeProduct = axesSizeProduct;
    }

    private int getAxisIndexForProductIndex(int index, int axis) {
        return index / this.axesSizeProduct[axis + 1] % ((List)this.axes.get((int)axis)).size();
    }

    @Override
    public ImmutableList<E> get(int index) {
        Preconditions.checkElementIndex((int)index, (int)this.size());
        return new ImmutableList<E>((CartesianList)this, (int)index){
            final /* synthetic */ int val$index;
            final /* synthetic */ CartesianList this$0;
            {
                this.this$0 = cartesianList;
                this.val$index = n;
            }

            public int size() {
                return CartesianList.access$000((CartesianList)this.this$0).size();
            }

            public E get(int axis) {
                Preconditions.checkElementIndex((int)axis, (int)this.size());
                int axisIndex = CartesianList.access$100((CartesianList)this.this$0, (int)this.val$index, (int)axis);
                return (E)((List)CartesianList.access$000((CartesianList)this.this$0).get((int)axis)).get((int)axisIndex);
            }

            boolean isPartialView() {
                return true;
            }
        };
    }

    @Override
    public int size() {
        return this.axesSizeProduct[0];
    }

    @Override
    public boolean contains(@Nullable Object o) {
        int index;
        if (!(o instanceof List)) {
            return false;
        }
        List list = (List)o;
        if (list.size() != this.axes.size()) {
            return false;
        }
        ListIterator<E> itr = list.listIterator();
        do {
            if (!itr.hasNext()) return true;
        } while (((List)this.axes.get((int)(index = itr.nextIndex()))).contains(itr.next()));
        return false;
    }

    static /* synthetic */ ImmutableList access$000(CartesianList x0) {
        return x0.axes;
    }

    static /* synthetic */ int access$100(CartesianList x0, int x1, int x2) {
        return x0.getAxisIndexForProductIndex((int)x1, (int)x2);
    }
}

