/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list.linked;

import gnu.trove.list.TLinkable;
import gnu.trove.list.linked.TLinkedList;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.ListIterator;

public class TLinkedList<T extends TLinkable<T>>
extends AbstractSequentialList<T>
implements Externalizable {
    static final long serialVersionUID = 1L;
    protected T _head;
    protected T _tail;
    protected int _size = 0;

    @Override
    public ListIterator<T> listIterator(int index) {
        return new IteratorImpl((TLinkedList)this, (int)index);
    }

    @Override
    public int size() {
        return this._size;
    }

    @Override
    public void add(int index, T linkable) {
        if (index < 0) throw new IndexOutOfBoundsException((String)("index:" + index));
        if (index > this.size()) {
            throw new IndexOutOfBoundsException((String)("index:" + index));
        }
        this.insert((int)index, linkable);
    }

    @Override
    public boolean add(T linkable) {
        this.insert((int)this._size, linkable);
        return true;
    }

    public void addFirst(T linkable) {
        this.insert((int)0, linkable);
    }

    public void addLast(T linkable) {
        this.insert((int)this.size(), linkable);
    }

    @Override
    public void clear() {
        if (null != this._head) {
            for (T link = this._head.getNext(); link != null; link = link.getNext()) {
                Object prev = link.getPrevious();
                prev.setNext(null);
                link.setPrevious(null);
            }
            this._tail = null;
            this._head = null;
        }
        this._size = 0;
    }

    @Override
    public Object[] toArray() {
        Object[] o = new Object[this._size];
        int i = 0;
        T link = this._head;
        while (link != null) {
            o[i++] = link;
            link = link.getNext();
        }
        return o;
    }

    public Object[] toUnlinkedArray() {
        Object[] o = new Object[this._size];
        int i = 0;
        T link = this._head;
        do {
            if (link == null) {
                this._size = 0;
                this._tail = null;
                this._head = null;
                return o;
            }
            o[i] = link;
            T tmp = link;
            link = link.getNext();
            tmp.setNext(null);
            tmp.setPrevious(null);
            ++i;
        } while (true);
    }

    public T[] toUnlinkedArray(T[] a) {
        int size = this.size();
        if (a.length < size) {
            a = (TLinkable[])Array.newInstance(a.getClass().getComponentType(), (int)size);
        }
        int i = 0;
        T link = this._head;
        do {
            if (link == null) {
                this._size = 0;
                this._tail = null;
                this._head = null;
                return a;
            }
            a[i] = link;
            T tmp = link;
            link = link.getNext();
            tmp.setNext(null);
            tmp.setPrevious(null);
            ++i;
        } while (true);
    }

    @Override
    public boolean contains(Object o) {
        T link = this._head;
        while (link != null) {
            if (o.equals(link)) {
                return true;
            }
            link = link.getNext();
        }
        return false;
    }

    @Override
    public T get(int index) {
        if (index < 0) throw new IndexOutOfBoundsException((String)("Index: " + index + ", Size: " + this._size));
        if (index >= this._size) {
            throw new IndexOutOfBoundsException((String)("Index: " + index + ", Size: " + this._size));
        }
        if (index > this._size >> 1) {
            int position = this._size - 1;
            T node = this._tail;
            while (position > index) {
                node = node.getPrevious();
                --position;
            }
            return (T)node;
        }
        int position = 0;
        T node = this._head;
        while (position < index) {
            node = node.getNext();
            ++position;
        }
        return (T)node;
    }

    public T getFirst() {
        return (T)this._head;
    }

    public T getLast() {
        return (T)this._tail;
    }

    public T getNext(T current) {
        return (T)current.getNext();
    }

    public T getPrevious(T current) {
        return (T)current.getPrevious();
    }

    public T removeFirst() {
        T o = this._head;
        if (o == null) {
            return (T)null;
        }
        Object n = o.getNext();
        o.setNext(null);
        if (null != n) {
            n.setPrevious(null);
        }
        this._head = n;
        if (--this._size != 0) return (T)o;
        this._tail = null;
        return (T)o;
    }

    public T removeLast() {
        T o = this._tail;
        if (o == null) {
            return (T)null;
        }
        Object prev = o.getPrevious();
        o.setPrevious(null);
        if (null != prev) {
            prev.setNext(null);
        }
        this._tail = prev;
        if (--this._size != 0) return (T)o;
        this._head = null;
        return (T)o;
    }

    protected void insert(int index, T linkable) {
        if (this._size == 0) {
            this._tail = linkable;
            this._head = this._tail;
        } else if (index == 0) {
            linkable.setNext(this._head);
            this._head.setPrevious(linkable);
            this._head = linkable;
        } else if (index == this._size) {
            this._tail.setNext(linkable);
            linkable.setPrevious(this._tail);
            this._tail = linkable;
        } else {
            Object node = this.get((int)index);
            T before = node.getPrevious();
            if (before != null) {
                before.setNext(linkable);
            }
            linkable.setPrevious(before);
            linkable.setNext((Object)node);
            node.setPrevious(linkable);
        }
        ++this._size;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof TLinkable)) return false;
        TLinkable link = (TLinkable)o;
        T p = link.getPrevious();
        T n = link.getNext();
        if (n == null && p == null) {
            if (o != this._head) {
                return false;
            }
            this._tail = null;
            this._head = null;
        } else if (n == null) {
            link.setPrevious(null);
            p.setNext(null);
            this._tail = p;
        } else if (p == null) {
            link.setNext(null);
            n.setPrevious(null);
            this._head = n;
        } else {
            p.setNext(n);
            n.setPrevious(p);
            link.setNext(null);
            link.setPrevious(null);
        }
        --this._size;
        return true;
    }

    public void addBefore(T current, T newElement) {
        if (current == this._head) {
            this.addFirst(newElement);
            return;
        }
        if (current == null) {
            this.addLast(newElement);
            return;
        }
        T p = current.getPrevious();
        newElement.setNext(current);
        p.setNext(newElement);
        newElement.setPrevious(p);
        current.setPrevious(newElement);
        ++this._size;
    }

    public void addAfter(T current, T newElement) {
        if (current == this._tail) {
            this.addLast(newElement);
            return;
        }
        if (current == null) {
            this.addFirst(newElement);
            return;
        }
        T n = current.getNext();
        newElement.setPrevious(current);
        newElement.setNext(n);
        current.setNext(newElement);
        n.setPrevious(newElement);
        ++this._size;
    }

    public boolean forEachValue(TObjectProcedure<T> procedure) {
        T node = this._head;
        while (node != null) {
            boolean keep_going = procedure.execute(node);
            if (!keep_going) {
                return false;
            }
            node = node.getNext();
        }
        return true;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        out.writeInt((int)this._size);
        out.writeObject(this._head);
        out.writeObject(this._tail);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._size = in.readInt();
        this._head = (TLinkable)in.readObject();
        this._tail = (TLinkable)in.readObject();
    }
}

