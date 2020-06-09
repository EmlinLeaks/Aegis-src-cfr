/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list;

import gnu.trove.list.TLinkable;

public abstract class TLinkableAdapter<T extends TLinkable>
implements TLinkable<T> {
    private volatile T next;
    private volatile T prev;

    @Override
    public T getNext() {
        return (T)this.next;
    }

    @Override
    public void setNext(T next) {
        this.next = next;
    }

    @Override
    public T getPrevious() {
        return (T)this.prev;
    }

    @Override
    public void setPrevious(T prev) {
        this.prev = prev;
    }
}

