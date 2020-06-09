/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AbbreviationMap<V> {
    private String key;
    private V value;
    private final Map<Character, AbbreviationMap<V>> children = new TreeMap<Character, AbbreviationMap<V>>();
    private int keysBeyond;

    public boolean contains(String aKey) {
        if (this.get((String)aKey) == null) return false;
        return true;
    }

    public V get(String aKey) {
        char[] chars = AbbreviationMap.charsOf((String)aKey);
        AbbreviationMap<V> child = this;
        char[] arr$ = chars;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            char each = arr$[i$];
            child = child.children.get((Object)Character.valueOf((char)each));
            if (child == null) {
                return (V)null;
            }
            ++i$;
        }
        return (V)child.value;
    }

    public void put(String aKey, V newValue) {
        if (newValue == null) {
            throw new NullPointerException();
        }
        if (aKey.length() == 0) {
            throw new IllegalArgumentException();
        }
        char[] chars = AbbreviationMap.charsOf((String)aKey);
        this.add((char[])chars, newValue, (int)0, (int)chars.length);
    }

    public void putAll(Iterable<String> keys, V newValue) {
        Iterator<String> i$ = keys.iterator();
        while (i$.hasNext()) {
            String each = i$.next();
            this.put((String)each, newValue);
        }
    }

    private boolean add(char[] chars, V newValue, int offset, int length) {
        boolean newKeyAdded;
        if (offset == length) {
            this.value = newValue;
            boolean wasAlreadyAKey = this.key != null;
            this.key = new String((char[])chars);
            if (wasAlreadyAKey) return false;
            return true;
        }
        char nextChar = chars[offset];
        AbbreviationMap<V> child = this.children.get((Object)Character.valueOf((char)nextChar));
        if (child == null) {
            child = new AbbreviationMap<V>();
            this.children.put((Character)Character.valueOf((char)nextChar), child);
        }
        if (newKeyAdded = AbbreviationMap.super.add((char[])chars, newValue, (int)(offset + 1), (int)length)) {
            ++this.keysBeyond;
        }
        if (this.key != null) return newKeyAdded;
        this.value = this.keysBeyond > 1 ? null : newValue;
        return newKeyAdded;
    }

    public void remove(String aKey) {
        if (aKey.length() == 0) {
            throw new IllegalArgumentException();
        }
        char[] keyChars = AbbreviationMap.charsOf((String)aKey);
        this.remove((char[])keyChars, (int)0, (int)keyChars.length);
    }

    private boolean remove(char[] aKey, int offset, int length) {
        if (offset == length) {
            return this.removeAtEndOfKey();
        }
        char nextChar = aKey[offset];
        AbbreviationMap<V> child = this.children.get((Object)Character.valueOf((char)nextChar));
        if (child == null) return false;
        if (!AbbreviationMap.super.remove((char[])aKey, (int)(offset + 1), (int)length)) {
            return false;
        }
        --this.keysBeyond;
        if (child.keysBeyond == 0) {
            this.children.remove((Object)Character.valueOf((char)nextChar));
        }
        if (this.keysBeyond != 1) return true;
        if (this.key != null) return true;
        this.setValueToThatOfOnlyChild();
        return true;
    }

    private void setValueToThatOfOnlyChild() {
        Map.Entry<Character, AbbreviationMap<V>> entry = this.children.entrySet().iterator().next();
        AbbreviationMap<V> onlyChild = entry.getValue();
        this.value = onlyChild.value;
    }

    private boolean removeAtEndOfKey() {
        if (this.key == null) {
            return false;
        }
        this.key = null;
        if (this.keysBeyond == 1) {
            this.setValueToThatOfOnlyChild();
            return true;
        }
        this.value = null;
        return true;
    }

    public Map<String, V> toJavaUtilMap() {
        TreeMap<K, V> mappings = new TreeMap<K, V>();
        this.addToMappings(mappings);
        return mappings;
    }

    private void addToMappings(Map<String, V> mappings) {
        if (this.key != null) {
            mappings.put((String)this.key, this.value);
        }
        Iterator<AbbreviationMap<V>> i$ = this.children.values().iterator();
        while (i$.hasNext()) {
            AbbreviationMap<V> each = i$.next();
            AbbreviationMap.super.addToMappings(mappings);
        }
    }

    private static char[] charsOf(String aKey) {
        char[] chars = new char[aKey.length()];
        aKey.getChars((int)0, (int)aKey.length(), (char[])chars, (int)0);
        return chars;
    }
}

