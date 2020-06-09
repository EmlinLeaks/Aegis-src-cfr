/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.nio;

import io.netty.channel.nio.SelectedSelectionKeySet;
import java.nio.channels.SelectionKey;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;

final class SelectedSelectionKeySet
extends AbstractSet<SelectionKey> {
    SelectionKey[] keys = new SelectionKey[1024];
    int size;

    SelectedSelectionKeySet() {
    }

    @Override
    public boolean add(SelectionKey o) {
        if (o == null) {
            return false;
        }
        this.keys[this.size++] = o;
        if (this.size != this.keys.length) return true;
        this.increaseCapacity();
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<SelectionKey> iterator() {
        return new Iterator<SelectionKey>((SelectedSelectionKeySet)this){
            private int idx;
            final /* synthetic */ SelectedSelectionKeySet this$0;
            {
                this.this$0 = this$0;
            }

            public boolean hasNext() {
                if (this.idx >= this.this$0.size) return false;
                return true;
            }

            public SelectionKey next() {
                if (this.hasNext()) return this.this$0.keys[this.idx++];
                throw new java.util.NoSuchElementException();
            }

            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        };
    }

    void reset() {
        this.reset((int)0);
    }

    void reset(int start) {
        Arrays.fill((Object[])this.keys, (int)start, (int)this.size, null);
        this.size = 0;
    }

    private void increaseCapacity() {
        SelectionKey[] newKeys = new SelectionKey[this.keys.length << 1];
        System.arraycopy((Object)this.keys, (int)0, (Object)newKeys, (int)0, (int)this.size);
        this.keys = newKeys;
    }
}

