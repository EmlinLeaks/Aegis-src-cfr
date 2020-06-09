/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.CodecOutputList;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractList;
import java.util.RandomAccess;

final class CodecOutputList
extends AbstractList<Object>
implements RandomAccess {
    private static final CodecOutputListRecycler NOOP_RECYCLER = new CodecOutputListRecycler(){

        public void recycle(CodecOutputList object) {
        }
    };
    private static final FastThreadLocal<CodecOutputLists> CODEC_OUTPUT_LISTS_POOL = new FastThreadLocal<CodecOutputLists>(){

        protected CodecOutputLists initialValue() throws java.lang.Exception {
            return new CodecOutputLists((int)16);
        }
    };
    private final CodecOutputListRecycler recycler;
    private int size;
    private Object[] array;
    private boolean insertSinceRecycled;

    static CodecOutputList newInstance() {
        return CODEC_OUTPUT_LISTS_POOL.get().getOrCreate();
    }

    private CodecOutputList(CodecOutputListRecycler recycler, int size) {
        this.recycler = recycler;
        this.array = new Object[size];
    }

    @Override
    public Object get(int index) {
        this.checkIndex((int)index);
        return this.array[index];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean add(Object element) {
        ObjectUtil.checkNotNull(element, (String)"element");
        try {
            this.insert((int)this.size, (Object)element);
        }
        catch (IndexOutOfBoundsException ignore) {
            this.expandArray();
            this.insert((int)this.size, (Object)element);
        }
        ++this.size;
        return true;
    }

    @Override
    public Object set(int index, Object element) {
        ObjectUtil.checkNotNull(element, (String)"element");
        this.checkIndex((int)index);
        Object old = this.array[index];
        this.insert((int)index, (Object)element);
        return old;
    }

    @Override
    public void add(int index, Object element) {
        ObjectUtil.checkNotNull(element, (String)"element");
        this.checkIndex((int)index);
        if (this.size == this.array.length) {
            this.expandArray();
        }
        if (index != this.size) {
            System.arraycopy((Object)this.array, (int)index, (Object)this.array, (int)(index + 1), (int)(this.size - index));
        }
        this.insert((int)index, (Object)element);
        ++this.size;
    }

    @Override
    public Object remove(int index) {
        this.checkIndex((int)index);
        Object old = this.array[index];
        int len = this.size - index - 1;
        if (len > 0) {
            System.arraycopy((Object)this.array, (int)(index + 1), (Object)this.array, (int)index, (int)len);
        }
        this.array[--this.size] = null;
        return old;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    boolean insertSinceRecycled() {
        return this.insertSinceRecycled;
    }

    void recycle() {
        int i = 0;
        do {
            if (i >= this.size) {
                this.size = 0;
                this.insertSinceRecycled = false;
                this.recycler.recycle((CodecOutputList)this);
                return;
            }
            this.array[i] = null;
            ++i;
        } while (true);
    }

    Object getUnsafe(int index) {
        return this.array[index];
    }

    private void checkIndex(int index) {
        if (index < this.size) return;
        throw new IndexOutOfBoundsException();
    }

    private void insert(int index, Object element) {
        this.array[index] = element;
        this.insertSinceRecycled = true;
    }

    private void expandArray() {
        int newCapacity = this.array.length << 1;
        if (newCapacity < 0) {
            throw new OutOfMemoryError();
        }
        Object[] newArray = new Object[newCapacity];
        System.arraycopy((Object)this.array, (int)0, (Object)newArray, (int)0, (int)this.array.length);
        this.array = newArray;
    }

    static /* synthetic */ CodecOutputListRecycler access$100() {
        return NOOP_RECYCLER;
    }
}

