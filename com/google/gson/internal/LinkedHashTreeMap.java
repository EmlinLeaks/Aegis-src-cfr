/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

import com.google.gson.internal.LinkedHashTreeMap;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class LinkedHashTreeMap<K, V>
extends AbstractMap<K, V>
implements Serializable {
    private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>(){

        public int compare(Comparable a, Comparable b) {
            return a.compareTo(b);
        }
    };
    Comparator<? super K> comparator;
    Node<K, V>[] table;
    final Node<K, V> header;
    int size = 0;
    int modCount = 0;
    int threshold;
    private LinkedHashTreeMap<K, V> entrySet;
    private LinkedHashTreeMap<K, V> keySet;

    public LinkedHashTreeMap() {
        this(NATURAL_ORDER);
    }

    public LinkedHashTreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator != null ? comparator : NATURAL_ORDER;
        this.header = new Node<K, V>();
        this.table = new Node[16];
        this.threshold = this.table.length / 2 + this.table.length / 4;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public V get(Object key) {
        V v;
        Node<K, V> node = this.findByObject((Object)key);
        if (node != null) {
            v = (V)node.value;
            return (V)((V)v);
        }
        v = null;
        return (V)v;
    }

    @Override
    public boolean containsKey(Object key) {
        if (this.findByObject((Object)key) == null) return false;
        return true;
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException((String)"key == null");
        }
        Node<K, V> created = this.find(key, (boolean)true);
        V result = created.value;
        created.value = value;
        return (V)result;
    }

    @Override
    public void clear() {
        Arrays.fill((Object[])this.table, null);
        this.size = 0;
        ++this.modCount;
        Node<K, V> header = this.header;
        Node<K, V> e = header.next;
        do {
            if (e == header) {
                header.prev = header;
                header.next = header.prev;
                return;
            }
            Node<K, V> next = e.next;
            e.prev = null;
            e.next = null;
            e = next;
        } while (true);
    }

    @Override
    public V remove(Object key) {
        V v;
        Node<K, V> node = this.removeInternalByKey((Object)key);
        if (node != null) {
            v = (V)node.value;
            return (V)((V)v);
        }
        v = null;
        return (V)v;
    }

    Node<K, V> find(K key, boolean create) {
        Node<K, V> created;
        Comparator<K> comparator = this.comparator;
        Node<K, V>[] table = this.table;
        int hash = LinkedHashTreeMap.secondaryHash((int)key.hashCode());
        int index = hash & table.length - 1;
        Node<K, V> nearest = table[index];
        int comparison = 0;
        if (nearest != null) {
            Comparable comparableKey = comparator == NATURAL_ORDER ? (Comparable)key : null;
            do {
                Node<K, V> child;
                int n = comparison = comparableKey != null ? comparableKey.compareTo(nearest.key) : comparator.compare(key, nearest.key);
                if (comparison == 0) {
                    return nearest;
                }
                Node<K, V> node = child = comparison < 0 ? nearest.left : nearest.right;
                if (child == null) break;
                nearest = child;
            } while (true);
        }
        if (!create) {
            return null;
        }
        Node<K, V> header = this.header;
        if (nearest == null) {
            if (comparator == NATURAL_ORDER && !(key instanceof Comparable)) {
                throw new ClassCastException((String)(key.getClass().getName() + " is not Comparable"));
            }
            created = new Node<K, V>(nearest, key, (int)hash, header, header.prev);
            table[index] = created;
        } else {
            created = new Node<K, V>(nearest, key, (int)hash, header, header.prev);
            if (comparison < 0) {
                nearest.left = created;
            } else {
                nearest.right = created;
            }
            this.rebalance(nearest, (boolean)true);
        }
        if (this.size++ > this.threshold) {
            this.doubleCapacity();
        }
        ++this.modCount;
        return created;
    }

    Node<K, V> findByObject(Object key) {
        try {
            if (key == null) return null;
            Node<Object, V> node = this.find(key, (boolean)false);
            return node;
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    Node<K, V> findByEntry(Map.Entry<?, ?> entry) {
        Node<K, V> mine = this.findByObject(entry.getKey());
        if (mine == null) return null;
        if (!this.equal(mine.value, entry.getValue())) return null;
        boolean bl = true;
        boolean valuesEqual = bl;
        if (!valuesEqual) return null;
        Node<K, V> node = mine;
        return node;
    }

    private boolean equal(Object a, Object b) {
        if (a == b) return true;
        if (a == null) return false;
        if (!a.equals((Object)b)) return false;
        return true;
    }

    private static int secondaryHash(int h) {
        h ^= h >>> 20 ^ h >>> 12;
        return h ^ h >>> 7 ^ h >>> 4;
    }

    void removeInternal(Node<K, V> node, boolean unlink) {
        if (unlink) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
        }
        Node<K, V> left = node.left;
        Node<K, V> right = node.right;
        Node<K, V> originalParent = node.parent;
        if (left != null && right != null) {
            Node<K, V> adjacent = left.height > right.height ? left.last() : right.first();
            this.removeInternal(adjacent, (boolean)false);
            int leftHeight = 0;
            left = node.left;
            if (left != null) {
                leftHeight = left.height;
                adjacent.left = left;
                left.parent = adjacent;
                node.left = null;
            }
            int rightHeight = 0;
            right = node.right;
            if (right != null) {
                rightHeight = right.height;
                adjacent.right = right;
                right.parent = adjacent;
                node.right = null;
            }
            adjacent.height = Math.max((int)leftHeight, (int)rightHeight) + 1;
            this.replaceInParent(node, adjacent);
            return;
        }
        if (left != null) {
            this.replaceInParent(node, left);
            node.left = null;
        } else if (right != null) {
            this.replaceInParent(node, right);
            node.right = null;
        } else {
            this.replaceInParent(node, null);
        }
        this.rebalance(originalParent, (boolean)false);
        --this.size;
        ++this.modCount;
    }

    Node<K, V> removeInternalByKey(Object key) {
        Node<K, V> node = this.findByObject((Object)key);
        if (node == null) return node;
        this.removeInternal(node, (boolean)true);
        return node;
    }

    private void replaceInParent(Node<K, V> node, Node<K, V> replacement) {
        Node<K, V> parent = node.parent;
        node.parent = null;
        if (replacement != null) {
            replacement.parent = parent;
        }
        if (parent == null) {
            int index = node.hash & this.table.length - 1;
            this.table[index] = replacement;
            return;
        }
        if (parent.left == node) {
            parent.left = replacement;
            return;
        }
        assert (parent.right == node);
        parent.right = replacement;
    }

    private void rebalance(Node<K, V> unbalanced, boolean insert) {
        Node<K, V> node = unbalanced;
        while (node != null) {
            Node<K, V> left = node.left;
            Node<K, V> right = node.right;
            int leftHeight = left != null ? left.height : 0;
            int rightHeight = right != null ? right.height : 0;
            int delta = leftHeight - rightHeight;
            if (delta == -2) {
                int rightRightHeight;
                Node<K, V> rightRight;
                Node<K, V> rightLeft = right.left;
                int rightLeftHeight = rightLeft != null ? rightLeft.height : 0;
                int rightDelta = rightLeftHeight - (rightRightHeight = (rightRight = right.right) != null ? rightRight.height : 0);
                if (rightDelta == -1 || rightDelta == 0 && !insert) {
                    this.rotateLeft(node);
                } else {
                    assert (rightDelta == 1);
                    this.rotateRight(right);
                    this.rotateLeft(node);
                }
                if (insert) {
                    return;
                }
            } else if (delta == 2) {
                int leftRightHeight;
                Node<K, V> leftRight;
                Node<K, V> leftLeft = left.left;
                int leftLeftHeight = leftLeft != null ? leftLeft.height : 0;
                int leftDelta = leftLeftHeight - (leftRightHeight = (leftRight = left.right) != null ? leftRight.height : 0);
                if (leftDelta == 1 || leftDelta == 0 && !insert) {
                    this.rotateRight(node);
                } else {
                    assert (leftDelta == -1);
                    this.rotateLeft(left);
                    this.rotateRight(node);
                }
                if (insert) {
                    return;
                }
            } else if (delta == 0) {
                node.height = leftHeight + 1;
                if (insert) {
                    return;
                }
            } else {
                assert (delta == -1 || delta == 1);
                node.height = Math.max((int)leftHeight, (int)rightHeight) + 1;
                if (!insert) {
                    return;
                }
            }
            node = node.parent;
        }
    }

    private void rotateLeft(Node<K, V> root) {
        Node<K, V> left = root.left;
        Node<K, V> pivot = root.right;
        Node<K, V> pivotLeft = pivot.left;
        Node<K, V> pivotRight = pivot.right;
        root.right = pivotLeft;
        if (pivotLeft != null) {
            pivotLeft.parent = root;
        }
        this.replaceInParent(root, pivot);
        pivot.left = root;
        root.parent = pivot;
        root.height = Math.max((int)(left != null ? left.height : 0), (int)(pivotLeft != null ? pivotLeft.height : 0)) + 1;
        pivot.height = Math.max((int)root.height, (int)(pivotRight != null ? pivotRight.height : 0)) + 1;
    }

    private void rotateRight(Node<K, V> root) {
        Node<K, V> pivot = root.left;
        Node<K, V> right = root.right;
        Node<K, V> pivotLeft = pivot.left;
        Node<K, V> pivotRight = pivot.right;
        root.left = pivotRight;
        if (pivotRight != null) {
            pivotRight.parent = root;
        }
        this.replaceInParent(root, pivot);
        pivot.right = root;
        root.parent = pivot;
        root.height = Math.max((int)(right != null ? right.height : 0), (int)(pivotRight != null ? pivotRight.height : 0)) + 1;
        pivot.height = Math.max((int)root.height, (int)(pivotLeft != null ? pivotLeft.height : 0)) + 1;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Object object;
        LinkedHashTreeMap<K, V> result = this.entrySet;
        if (result != null) {
            object = result;
            return object;
        }
        object = this.entrySet = new EntrySet((LinkedHashTreeMap)this);
        return object;
    }

    @Override
    public Set<K> keySet() {
        Object object;
        LinkedHashTreeMap<K, V> result = this.keySet;
        if (result != null) {
            object = result;
            return object;
        }
        object = this.keySet = new KeySet((LinkedHashTreeMap)this);
        return object;
    }

    private void doubleCapacity() {
        this.table = LinkedHashTreeMap.doubleCapacity(this.table);
        this.threshold = this.table.length / 2 + this.table.length / 4;
    }

    static <K, V> Node<K, V>[] doubleCapacity(Node<K, V>[] oldTable) {
        int oldCapacity = oldTable.length;
        Node[] newTable = new Node[oldCapacity * 2];
        AvlIterator<K, V> iterator = new AvlIterator<K, V>();
        AvlBuilder<K, V> leftBuilder = new AvlBuilder<K, V>();
        AvlBuilder<K, V> rightBuilder = new AvlBuilder<K, V>();
        int i = 0;
        while (i < oldCapacity) {
            Node<K, V> root = oldTable[i];
            if (root != null) {
                Node<K, V> node;
                iterator.reset(root);
                int leftSize = 0;
                int rightSize = 0;
                while ((node = iterator.next()) != null) {
                    if ((node.hash & oldCapacity) == 0) {
                        ++leftSize;
                        continue;
                    }
                    ++rightSize;
                }
                leftBuilder.reset((int)leftSize);
                rightBuilder.reset((int)rightSize);
                iterator.reset(root);
                while ((node = iterator.next()) != null) {
                    if ((node.hash & oldCapacity) == 0) {
                        leftBuilder.add(node);
                        continue;
                    }
                    rightBuilder.add(node);
                }
                newTable[i] = leftSize > 0 ? leftBuilder.root() : null;
                newTable[i + oldCapacity] = rightSize > 0 ? rightBuilder.root() : null;
            }
            ++i;
        }
        return newTable;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new LinkedHashMap<K, V>(this);
    }
}

