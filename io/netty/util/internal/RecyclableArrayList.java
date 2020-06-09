/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.Recycler;
import io.netty.util.internal.RecyclableArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public final class RecyclableArrayList
extends ArrayList<Object> {
    private static final long serialVersionUID = -8605125654176467947L;
    private static final int DEFAULT_INITIAL_CAPACITY = 8;
    private static final Recycler<RecyclableArrayList> RECYCLER = new Recycler<RecyclableArrayList>(){

        protected RecyclableArrayList newObject(Recycler.Handle<RecyclableArrayList> handle) {
            return new RecyclableArrayList(handle);
        }
    };
    private boolean insertSinceRecycled;
    private final Recycler.Handle<RecyclableArrayList> handle;

    public static RecyclableArrayList newInstance() {
        return RecyclableArrayList.newInstance((int)8);
    }

    public static RecyclableArrayList newInstance(int minCapacity) {
        RecyclableArrayList ret = RECYCLER.get();
        ret.ensureCapacity((int)minCapacity);
        return ret;
    }

    private RecyclableArrayList(Recycler.Handle<RecyclableArrayList> handle) {
        this(handle, (int)8);
    }

    private RecyclableArrayList(Recycler.Handle<RecyclableArrayList> handle, int initialCapacity) {
        super((int)initialCapacity);
        this.handle = handle;
    }

    @Override
    public boolean addAll(Collection<?> c) {
        RecyclableArrayList.checkNullElements(c);
        if (!super.addAll(c)) return false;
        this.insertSinceRecycled = true;
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        RecyclableArrayList.checkNullElements(c);
        if (!super.addAll((int)index, c)) return false;
        this.insertSinceRecycled = true;
        return true;
    }

    private static void checkNullElements(Collection<?> c) {
        if (!(c instanceof RandomAccess) || !(c instanceof List)) {
            ? element;
            Iterator<?> list = c.iterator();
            do {
                if (!list.hasNext()) return;
            } while ((element = list.next()) != null);
            throw new IllegalArgumentException((String)"c contains null values");
        }
        List list = (List)c;
        int size = list.size();
        int i = 0;
        while (i < size) {
            if (list.get((int)i) == null) {
                throw new IllegalArgumentException((String)"c contains null values");
            }
            ++i;
        }
    }

    @Override
    public boolean add(Object element) {
        if (element == null) {
            throw new NullPointerException((String)"element");
        }
        if (!super.add(element)) return false;
        this.insertSinceRecycled = true;
        return true;
    }

    @Override
    public void add(int index, Object element) {
        if (element == null) {
            throw new NullPointerException((String)"element");
        }
        super.add((int)index, element);
        this.insertSinceRecycled = true;
    }

    @Override
    public Object set(int index, Object element) {
        if (element == null) {
            throw new NullPointerException((String)"element");
        }
        Object old = super.set((int)index, element);
        this.insertSinceRecycled = true;
        return old;
    }

    public boolean insertSinceRecycled() {
        return this.insertSinceRecycled;
    }

    public boolean recycle() {
        this.clear();
        this.insertSinceRecycled = false;
        this.handle.recycle((RecyclableArrayList)this);
        return true;
    }
}

