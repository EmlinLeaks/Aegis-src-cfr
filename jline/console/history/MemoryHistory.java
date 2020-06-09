/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.history;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import jline.console.history.History;
import jline.console.history.MemoryHistory;
import jline.internal.Preconditions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MemoryHistory
implements History {
    public static final int DEFAULT_MAX_SIZE = 500;
    private final LinkedList<CharSequence> items = new LinkedList<E>();
    private int maxSize = 500;
    private boolean ignoreDuplicates = true;
    private boolean autoTrim = false;
    private int offset = 0;
    private int index = 0;

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        this.maybeResize();
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public boolean isIgnoreDuplicates() {
        return this.ignoreDuplicates;
    }

    public void setIgnoreDuplicates(boolean flag) {
        this.ignoreDuplicates = flag;
    }

    public boolean isAutoTrim() {
        return this.autoTrim;
    }

    public void setAutoTrim(boolean flag) {
        this.autoTrim = flag;
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public int index() {
        return this.offset + this.index;
    }

    @Override
    public void clear() {
        this.items.clear();
        this.offset = 0;
        this.index = 0;
    }

    @Override
    public CharSequence get(int index) {
        return this.items.get((int)(index - this.offset));
    }

    @Override
    public void set(int index, CharSequence item) {
        this.items.set((int)(index - this.offset), (CharSequence)item);
    }

    @Override
    public void add(CharSequence item) {
        Preconditions.checkNotNull(item);
        if (this.isAutoTrim()) {
            item = String.valueOf((Object)item).trim();
        }
        if (this.isIgnoreDuplicates() && !this.items.isEmpty() && item.equals((Object)this.items.getLast())) {
            return;
        }
        this.internalAdd((CharSequence)item);
    }

    @Override
    public CharSequence remove(int i) {
        return this.items.remove((int)i);
    }

    @Override
    public CharSequence removeFirst() {
        return this.items.removeFirst();
    }

    @Override
    public CharSequence removeLast() {
        return this.items.removeLast();
    }

    protected void internalAdd(CharSequence item) {
        this.items.add((CharSequence)item);
        this.maybeResize();
    }

    @Override
    public void replace(CharSequence item) {
        this.items.removeLast();
        this.add((CharSequence)item);
    }

    private void maybeResize() {
        do {
            if (this.size() <= this.getMaxSize()) {
                this.index = this.size();
                return;
            }
            this.items.removeFirst();
            ++this.offset;
        } while (true);
    }

    @Override
    public ListIterator<History.Entry> entries(int index) {
        return new EntriesIterator((MemoryHistory)this, (int)(index - this.offset), null);
    }

    @Override
    public ListIterator<History.Entry> entries() {
        return this.entries((int)this.offset);
    }

    @Override
    public Iterator<History.Entry> iterator() {
        return this.entries();
    }

    @Override
    public boolean moveToLast() {
        int lastEntry = this.size() - 1;
        if (lastEntry < 0) return false;
        if (lastEntry == this.index) return false;
        this.index = this.size() - 1;
        return true;
    }

    @Override
    public boolean moveTo(int index) {
        if ((index -= this.offset) < 0) return false;
        if (index >= this.size()) return false;
        this.index = index;
        return true;
    }

    @Override
    public boolean moveToFirst() {
        if (this.size() <= 0) return false;
        if (this.index == 0) return false;
        this.index = 0;
        return true;
    }

    @Override
    public void moveToEnd() {
        this.index = this.size();
    }

    @Override
    public CharSequence current() {
        if (this.index < this.size()) return this.items.get((int)this.index);
        return "";
    }

    @Override
    public boolean previous() {
        if (this.index <= 0) {
            return false;
        }
        --this.index;
        return true;
    }

    @Override
    public boolean next() {
        if (this.index >= this.size()) {
            return false;
        }
        ++this.index;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<History.Entry> i$ = this.iterator();
        while (i$.hasNext()) {
            History.Entry e = i$.next();
            sb.append((String)(e.toString() + "\n"));
        }
        return sb.toString();
    }

    static /* synthetic */ LinkedList access$100(MemoryHistory x0) {
        return x0.items;
    }

    static /* synthetic */ int access$200(MemoryHistory x0) {
        return x0.offset;
    }
}

