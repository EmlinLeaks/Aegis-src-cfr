/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public class LinkedListMultimap<K, V>
extends AbstractMultimap<K, V>
implements ListMultimap<K, V>,
Serializable {
    private transient Node<K, V> head;
    private transient Node<K, V> tail;
    private transient Map<K, KeyList<K, V>> keyToKeyList;
    private transient int size;
    private transient int modCount;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <K, V> LinkedListMultimap<K, V> create() {
        return new LinkedListMultimap<K, V>();
    }

    public static <K, V> LinkedListMultimap<K, V> create(int expectedKeys) {
        return new LinkedListMultimap<K, V>((int)expectedKeys);
    }

    public static <K, V> LinkedListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        return new LinkedListMultimap<K, V>(multimap);
    }

    LinkedListMultimap() {
        this.keyToKeyList = Maps.newHashMap();
    }

    private LinkedListMultimap(int expectedKeys) {
        this.keyToKeyList = new HashMap<K, KeyList<K, V>>((int)expectedKeys);
    }

    private LinkedListMultimap(Multimap<? extends K, ? extends V> multimap) {
        this((int)multimap.keySet().size());
        this.putAll(multimap);
    }

    @CanIgnoreReturnValue
    private Node<K, V> addNode(@Nullable K key, @Nullable V value, @Nullable Node<K, V> nextSibling) {
        Node<K, V> node = new Node<K, V>(key, value);
        if (this.head == null) {
            this.tail = node;
            this.head = this.tail;
            this.keyToKeyList.put(key, new KeyList<K, V>(node));
            ++this.modCount;
        } else if (nextSibling == null) {
            this.tail.next = node;
            node.previous = this.tail;
            this.tail = node;
            KeyList<K, V> keyList = this.keyToKeyList.get(key);
            if (keyList == null) {
                keyList = new KeyList<K, V>(node);
                this.keyToKeyList.put(key, keyList);
                ++this.modCount;
            } else {
                ++keyList.count;
                Node<K, V> keyTail = keyList.tail;
                keyTail.nextSibling = node;
                node.previousSibling = keyTail;
                keyList.tail = node;
            }
        } else {
            KeyList<K, V> keyList = this.keyToKeyList.get(key);
            ++keyList.count;
            node.previous = nextSibling.previous;
            node.previousSibling = nextSibling.previousSibling;
            node.next = nextSibling;
            node.nextSibling = nextSibling;
            if (nextSibling.previousSibling == null) {
                this.keyToKeyList.get(key).head = node;
            } else {
                nextSibling.previousSibling.nextSibling = node;
            }
            if (nextSibling.previous == null) {
                this.head = node;
            } else {
                nextSibling.previous.next = node;
            }
            nextSibling.previous = node;
            nextSibling.previousSibling = node;
        }
        ++this.size;
        return node;
    }

    private void removeNode(Node<K, V> node) {
        if (node.previous != null) {
            node.previous.next = node.next;
        } else {
            this.head = node.next;
        }
        if (node.next != null) {
            node.next.previous = node.previous;
        } else {
            this.tail = node.previous;
        }
        if (node.previousSibling == null && node.nextSibling == null) {
            KeyList<K, V> keyList = this.keyToKeyList.remove(node.key);
            keyList.count = 0;
            ++this.modCount;
        } else {
            KeyList<K, V> keyList = this.keyToKeyList.get(node.key);
            --keyList.count;
            if (node.previousSibling == null) {
                keyList.head = node.nextSibling;
            } else {
                node.previousSibling.nextSibling = node.nextSibling;
            }
            if (node.nextSibling == null) {
                keyList.tail = node.previousSibling;
            } else {
                node.nextSibling.previousSibling = node.previousSibling;
            }
        }
        --this.size;
    }

    private void removeAllNodes(@Nullable Object key) {
        Iterators.clear(new ValueForKeyIterator((LinkedListMultimap)this, (Object)key));
    }

    private static void checkElement(@Nullable Object node) {
        if (node != null) return;
        throw new NoSuchElementException();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        if (this.head != null) return false;
        return true;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return this.keyToKeyList.containsKey((Object)key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return this.values().contains((Object)value);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean put(@Nullable K key, @Nullable V value) {
        this.addNode(key, value, null);
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public List<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
        List<V> oldValues = this.getCopy(key);
        ValueForKeyIterator keyValues = new ValueForKeyIterator((LinkedListMultimap)this, key);
        Iterator<V> newValues = values.iterator();
        while (keyValues.hasNext() && newValues.hasNext()) {
            keyValues.next();
            keyValues.set(newValues.next());
        }
        do {
            if (!keyValues.hasNext()) {
                while (newValues.hasNext()) {
                    keyValues.add(newValues.next());
                }
                return oldValues;
            }
            keyValues.next();
            keyValues.remove();
        } while (true);
    }

    private List<V> getCopy(@Nullable Object key) {
        return Collections.unmodifiableList(Lists.newArrayList(new ValueForKeyIterator((LinkedListMultimap)this, (Object)key)));
    }

    @CanIgnoreReturnValue
    @Override
    public List<V> removeAll(@Nullable Object key) {
        List<V> oldValues = this.getCopy((Object)key);
        this.removeAllNodes((Object)key);
        return oldValues;
    }

    @Override
    public void clear() {
        this.head = null;
        this.tail = null;
        this.keyToKeyList.clear();
        this.size = 0;
        ++this.modCount;
    }

    @Override
    public List<V> get(@Nullable K key) {
        return new AbstractSequentialList<V>((LinkedListMultimap)this, key){
            final /* synthetic */ Object val$key;
            final /* synthetic */ LinkedListMultimap this$0;
            {
                this.this$0 = linkedListMultimap;
                this.val$key = object;
            }

            public int size() {
                KeyList keyList = (KeyList)LinkedListMultimap.access$600((LinkedListMultimap)this.this$0).get((Object)this.val$key);
                if (keyList == null) {
                    return 0;
                }
                int n = keyList.count;
                return n;
            }

            public java.util.ListIterator<V> listIterator(int index) {
                return new ValueForKeyIterator((LinkedListMultimap)this.this$0, (Object)this.val$key, (int)index);
            }
        };
    }

    @Override
    Set<K> createKeySet() {
        class KeySetImpl
        extends com.google.common.collect.Sets$ImprovedAbstractSet<K> {
            final /* synthetic */ LinkedListMultimap this$0;

            KeySetImpl(LinkedListMultimap linkedListMultimap) {
                this.this$0 = linkedListMultimap;
            }

            public int size() {
                return LinkedListMultimap.access$600((LinkedListMultimap)this.this$0).size();
            }

            public Iterator<K> iterator() {
                return new com.google.common.collect.LinkedListMultimap$DistinctKeyIterator((LinkedListMultimap)this.this$0, null);
            }

            public boolean contains(Object key) {
                return this.this$0.containsKey((Object)key);
            }

            public boolean remove(Object o) {
                if (this.this$0.removeAll((Object)o).isEmpty()) return false;
                return true;
            }
        }
        return new KeySetImpl((LinkedListMultimap)this);
    }

    @Override
    public List<V> values() {
        return (List)super.values();
    }

    @Override
    List<V> createValues() {
        class ValuesImpl
        extends AbstractSequentialList<V> {
            final /* synthetic */ LinkedListMultimap this$0;

            ValuesImpl(LinkedListMultimap linkedListMultimap) {
                this.this$0 = linkedListMultimap;
            }

            public int size() {
                return LinkedListMultimap.access$900((LinkedListMultimap)this.this$0);
            }

            public java.util.ListIterator<V> listIterator(int index) {
                com.google.common.collect.LinkedListMultimap$NodeIterator nodeItr = new com.google.common.collect.LinkedListMultimap$NodeIterator((LinkedListMultimap)this.this$0, (int)index);
                return new com.google.common.collect.TransformedListIterator<Map.Entry<K, V>, V>((ValuesImpl)this, (java.util.ListIterator)nodeItr, (com.google.common.collect.LinkedListMultimap$NodeIterator)nodeItr){
                    final /* synthetic */ com.google.common.collect.LinkedListMultimap$NodeIterator val$nodeItr;
                    final /* synthetic */ ValuesImpl this$1;
                    {
                        this.this$1 = valuesImpl;
                        this.val$nodeItr = nodeIterator;
                        super(x0);
                    }

                    V transform(Map.Entry<K, V> entry) {
                        return (V)entry.getValue();
                    }

                    public void set(V value) {
                        this.val$nodeItr.setValue(value);
                    }
                };
            }
        }
        return new ValuesImpl((LinkedListMultimap)this);
    }

    @Override
    public List<Map.Entry<K, V>> entries() {
        return (List)super.entries();
    }

    @Override
    List<Map.Entry<K, V>> createEntries() {
        class EntriesImpl
        extends AbstractSequentialList<Map.Entry<K, V>> {
            final /* synthetic */ LinkedListMultimap this$0;

            EntriesImpl(LinkedListMultimap linkedListMultimap) {
                this.this$0 = linkedListMultimap;
            }

            public int size() {
                return LinkedListMultimap.access$900((LinkedListMultimap)this.this$0);
            }

            public java.util.ListIterator<Map.Entry<K, V>> listIterator(int index) {
                return new com.google.common.collect.LinkedListMultimap$NodeIterator((LinkedListMultimap)this.this$0, (int)index);
            }
        }
        return new EntriesImpl((LinkedListMultimap)this);
    }

    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        throw new AssertionError((Object)"should never be called");
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        return new Multimaps.AsMap<K, V>(this);
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt((int)this.size());
        Iterator<E> i$ = this.entries().iterator();
        while (i$.hasNext()) {
            Map.Entry entry = (Map.Entry)i$.next();
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.keyToKeyList = Maps.newLinkedHashMap();
        int size = stream.readInt();
        int i = 0;
        while (i < size) {
            Object key = stream.readObject();
            Object value = stream.readObject();
            this.put(key, value);
            ++i;
        }
    }

    static /* synthetic */ int access$000(LinkedListMultimap x0) {
        return x0.modCount;
    }

    static /* synthetic */ Node access$100(LinkedListMultimap x0) {
        return x0.tail;
    }

    static /* synthetic */ Node access$200(LinkedListMultimap x0) {
        return x0.head;
    }

    static /* synthetic */ void access$300(Object x0) {
        LinkedListMultimap.checkElement((Object)x0);
    }

    static /* synthetic */ void access$400(LinkedListMultimap x0, Node x1) {
        x0.removeNode(x1);
    }

    static /* synthetic */ void access$500(LinkedListMultimap x0, Object x1) {
        x0.removeAllNodes((Object)x1);
    }

    static /* synthetic */ Map access$600(LinkedListMultimap x0) {
        return x0.keyToKeyList;
    }

    static /* synthetic */ Node access$700(LinkedListMultimap x0, Object x1, Object x2, Node x3) {
        return x0.addNode(x1, x2, x3);
    }

    static /* synthetic */ int access$900(LinkedListMultimap x0) {
        return x0.size;
    }
}

