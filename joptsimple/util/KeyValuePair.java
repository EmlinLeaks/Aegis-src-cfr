/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.util;

public final class KeyValuePair {
    public final String key;
    public final String value;

    private KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static KeyValuePair valueOf(String asString) {
        int equalsIndex = asString.indexOf((int)61);
        if (equalsIndex == -1) {
            return new KeyValuePair((String)asString, (String)"");
        }
        String aKey = asString.substring((int)0, (int)equalsIndex);
        String aValue = equalsIndex == asString.length() - 1 ? "" : asString.substring((int)(equalsIndex + 1));
        return new KeyValuePair((String)aKey, (String)aValue);
    }

    public boolean equals(Object that) {
        if (!(that instanceof KeyValuePair)) {
            return false;
        }
        KeyValuePair other = (KeyValuePair)that;
        if (!this.key.equals((Object)other.key)) return false;
        if (!this.value.equals((Object)other.value)) return false;
        return true;
    }

    public int hashCode() {
        return this.key.hashCode() ^ this.value.hashCode();
    }

    public String toString() {
        return this.key + '=' + this.value;
    }
}

