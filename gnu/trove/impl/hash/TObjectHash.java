/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.hash.THash;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class TObjectHash<T>
extends THash {
    static final long serialVersionUID = -3461112548087185871L;
    public transient Object[] _set;
    public static final Object REMOVED = new Object();
    public static final Object FREE = new Object();
    protected boolean consumeFreeSlot;

    public TObjectHash() {
    }

    public TObjectHash(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TObjectHash(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    @Override
    public int capacity() {
        return this._set.length;
    }

    @Override
    protected void removeAt(int index) {
        this._set[index] = REMOVED;
        super.removeAt((int)index);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._set = new Object[capacity];
        Arrays.fill((Object[])this._set, (Object)FREE);
        return capacity;
    }

    public boolean forEach(TObjectProcedure<? super T> procedure) {
        Object[] set = this._set;
        int i = set.length;
        do {
            if (i-- <= 0) return true;
        } while (set[i] == FREE || set[i] == REMOVED || procedure.execute(set[i]));
        return false;
    }

    public boolean contains(Object obj) {
        if (this.index((Object)obj) < 0) return false;
        return true;
    }

    protected int index(Object obj) {
        if (obj == null) {
            return this.indexForNull();
        }
        int hash = this.hash((Object)obj) & Integer.MAX_VALUE;
        int index = hash % this._set.length;
        Object cur = this._set[index];
        if (cur == FREE) {
            return -1;
        }
        if (cur == obj) return index;
        if (!this.equals((Object)obj, (Object)cur)) return this.indexRehashed((Object)obj, (int)index, (int)hash, (Object)cur);
        return index;
    }

    private int indexRehashed(Object obj, int index, int hash, Object cur) {
        Object[] set = this._set;
        int length = set.length;
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;
        do {
            if ((index -= probe) < 0) {
                index += length;
            }
            if ((cur = set[index]) == FREE) {
                return -1;
            }
            if (cur == obj) return index;
            if (!this.equals((Object)obj, (Object)cur)) continue;
            return index;
        } while (index != loopIndex);
        return -1;
    }

    private int indexForNull() {
        int index = 0;
        Object[] arrobject = this._set;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object o = arrobject[n2];
            if (o == null) {
                return index;
            }
            if (o == FREE) {
                return -1;
            }
            ++index;
            ++n2;
        }
        return -1;
    }

    @Deprecated
    protected int insertionIndex(T obj) {
        return this.insertKey(obj);
    }

    protected int insertKey(T key) {
        this.consumeFreeSlot = false;
        if (key == null) {
            return this.insertKeyForNull();
        }
        int hash = this.hash(key) & Integer.MAX_VALUE;
        int index = hash % this._set.length;
        Object cur = this._set[index];
        if (cur == FREE) {
            this.consumeFreeSlot = true;
            this._set[index] = key;
            return index;
        }
        if (cur == key) return -index - 1;
        if (!this.equals(key, (Object)cur)) return this.insertKeyRehash(key, (int)index, (int)hash, (Object)cur);
        return -index - 1;
    }

    private int insertKeyRehash(T key, int index, int hash, Object cur) {
        Object[] set = this._set;
        int length = set.length;
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;
        int firstRemoved = -1;
        do {
            if (cur == REMOVED && firstRemoved == -1) {
                firstRemoved = index;
            }
            if ((index -= probe) < 0) {
                index += length;
            }
            if ((cur = set[index]) == FREE) {
                if (firstRemoved != -1) {
                    this._set[firstRemoved] = key;
                    return firstRemoved;
                }
                this.consumeFreeSlot = true;
                this._set[index] = key;
                return index;
            }
            if (cur == key) return -index - 1;
            if (!this.equals(key, (Object)cur)) continue;
            return -index - 1;
        } while (index != loopIndex);
        if (firstRemoved == -1) throw new IllegalStateException((String)"No free or removed slots available. Key set full?!!");
        this._set[firstRemoved] = key;
        return firstRemoved;
    }

    private int insertKeyForNull() {
        int index = 0;
        int firstRemoved = -1;
        Object[] arrobject = this._set;
        int n = arrobject.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                if (firstRemoved == -1) throw new IllegalStateException((String)"Could not find insertion index for null key. Key set full!?!!");
                this._set[firstRemoved] = null;
                return firstRemoved;
            }
            Object o = arrobject[n2];
            if (o == REMOVED && firstRemoved == -1) {
                firstRemoved = index;
            }
            if (o == FREE) {
                if (firstRemoved == -1) break;
                this._set[firstRemoved] = null;
                return firstRemoved;
            }
            if (o == null) {
                return -index - 1;
            }
            ++index;
            ++n2;
        } while (true);
        this.consumeFreeSlot = true;
        this._set[index] = null;
        return index;
    }

    protected final void throwObjectContractViolation(Object o1, Object o2) throws IllegalArgumentException {
        throw this.buildObjectContractViolation((Object)o1, (Object)o2, (String)"");
    }

    protected final void throwObjectContractViolation(Object o1, Object o2, int size, int oldSize, Object[] oldKeys) throws IllegalArgumentException {
        String extra = this.dumpExtraInfo((Object)o1, (Object)o2, (int)this.size(), (int)oldSize, (Object[])oldKeys);
        throw this.buildObjectContractViolation((Object)o1, (Object)o2, (String)extra);
    }

    protected final IllegalArgumentException buildObjectContractViolation(Object o1, Object o2, String extra) {
        return new IllegalArgumentException((String)("Equal objects must have equal hashcodes. During rehashing, Trove discovered that the following two objects claim to be equal (as in java.lang.Object.equals()) but their hashCodes (or those calculated by your TObjectHashingStrategy) are not equal.This violates the general contract of java.lang.Object.hashCode().  See bullet point two in that method's documentation. object #1 =" + TObjectHash.objectInfo((Object)o1) + "; object #2 =" + TObjectHash.objectInfo((Object)o2) + "\n" + extra));
    }

    protected boolean equals(Object notnull, Object two) {
        if (two == null) return false;
        if (two != REMOVED) return notnull.equals((Object)two);
        return false;
    }

    protected int hash(Object notnull) {
        return notnull.hashCode();
    }

    protected static String reportPotentialConcurrentMod(int newSize, int oldSize) {
        if (newSize == oldSize) return "";
        return "[Warning] apparent concurrent modification of the key set. Size before and after rehash() do not match " + oldSize + " vs " + newSize;
    }

    protected String dumpExtraInfo(Object newVal, Object oldVal, int currentSize, int oldSize, Object[] oldKeys) {
        StringBuilder b = new StringBuilder();
        b.append((String)this.dumpKeyTypes((Object)newVal, (Object)oldVal));
        b.append((String)TObjectHash.reportPotentialConcurrentMod((int)currentSize, (int)oldSize));
        b.append((String)TObjectHash.detectKeyLoss((Object[])oldKeys, (int)oldSize));
        if (newVal != oldVal) return b.toString();
        b.append((String)"Inserting same object twice, rehashing bug. Object= ").append((Object)oldVal);
        return b.toString();
    }

    private static String detectKeyLoss(Object[] keys, int oldSize) {
        StringBuilder buf = new StringBuilder();
        Set<Object> k = TObjectHash.makeKeySet((Object[])keys);
        if (k.size() == oldSize) return buf.toString();
        buf.append((String)"\nhashCode() and/or equals() have inconsistent implementation");
        buf.append((String)"\nKey set lost entries, now got ").append((int)k.size()).append((String)" instead of ").append((int)oldSize);
        buf.append((String)". This can manifest itself as an apparent duplicate key.");
        return buf.toString();
    }

    private static Set<Object> makeKeySet(Object[] keys) {
        HashSet<Object> types = new HashSet<Object>();
        Object[] arrobject = keys;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object o = arrobject[n2];
            if (o != FREE && o != REMOVED) {
                types.add((Object)o);
            }
            ++n2;
        }
        return types;
    }

    private static String equalsSymmetryInfo(Object a, Object b) {
        StringBuilder buf = new StringBuilder();
        if (a == b) {
            return "a == b";
        }
        if (a.getClass() == b.getClass()) return buf.toString();
        buf.append((String)"Class of objects differ a=").append(a.getClass()).append((String)" vs b=").append(b.getClass());
        boolean aEb = a.equals((Object)b);
        boolean bEa = b.equals((Object)a);
        if (aEb == bEa) return buf.toString();
        buf.append((String)"\nequals() of a or b object are asymmetric");
        buf.append((String)"\na.equals(b) =").append((boolean)aEb);
        buf.append((String)"\nb.equals(a) =").append((boolean)bEa);
        return buf.toString();
    }

    protected static String objectInfo(Object o) {
        int n;
        Object object = o == null ? "class null" : o.getClass();
        if (o == null) {
            n = 0;
            return object + " id= " + System.identityHashCode((Object)o) + " hashCode= " + n + " toString= " + String.valueOf((Object)o);
        }
        n = o.hashCode();
        return object + " id= " + System.identityHashCode((Object)o) + " hashCode= " + n + " toString= " + String.valueOf((Object)o);
    }

    private String dumpKeyTypes(Object newVal, Object oldVal) {
        StringBuilder buf = new StringBuilder();
        HashSet<Class<?>> types = new HashSet<Class<?>>();
        Object[] arrobject = this._set;
        int n = arrobject.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                if (types.size() <= 1) return buf.toString();
                buf.append((String)"\nMore than one type used for keys. Watch out for asymmetric equals(). Read about the 'Liskov substitution principle' and the implications for equals() in java.");
                buf.append((String)"\nKey types: ").append(types);
                buf.append((String)TObjectHash.equalsSymmetryInfo((Object)newVal, (Object)oldVal));
                return buf.toString();
            }
            Object o = arrobject[n2];
            if (o != FREE && o != REMOVED) {
                if (o != null) {
                    types.add(o.getClass());
                } else {
                    types.add(null);
                }
            }
            ++n2;
        } while (true);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
    }
}

