/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.UriEncoder;

public final class Tag {
    public static final String PREFIX = "tag:yaml.org,2002:";
    public static final Tag YAML = new Tag((String)"tag:yaml.org,2002:yaml");
    public static final Tag MERGE = new Tag((String)"tag:yaml.org,2002:merge");
    public static final Tag SET = new Tag((String)"tag:yaml.org,2002:set");
    public static final Tag PAIRS = new Tag((String)"tag:yaml.org,2002:pairs");
    public static final Tag OMAP = new Tag((String)"tag:yaml.org,2002:omap");
    public static final Tag BINARY = new Tag((String)"tag:yaml.org,2002:binary");
    public static final Tag INT = new Tag((String)"tag:yaml.org,2002:int");
    public static final Tag FLOAT = new Tag((String)"tag:yaml.org,2002:float");
    public static final Tag TIMESTAMP = new Tag((String)"tag:yaml.org,2002:timestamp");
    public static final Tag BOOL = new Tag((String)"tag:yaml.org,2002:bool");
    public static final Tag NULL = new Tag((String)"tag:yaml.org,2002:null");
    public static final Tag STR = new Tag((String)"tag:yaml.org,2002:str");
    public static final Tag SEQ = new Tag((String)"tag:yaml.org,2002:seq");
    public static final Tag MAP = new Tag((String)"tag:yaml.org,2002:map");
    public static final Map<Tag, Set<Class<?>>> COMPATIBILITY_MAP = new HashMap<Tag, Set<Class<?>>>();
    private final String value;
    private boolean secondary = false;

    public Tag(String tag) {
        if (tag == null) {
            throw new NullPointerException((String)"Tag must be provided.");
        }
        if (tag.length() == 0) {
            throw new IllegalArgumentException((String)"Tag must not be empty.");
        }
        if (tag.trim().length() != tag.length()) {
            throw new IllegalArgumentException((String)"Tag must not contain leading or trailing spaces.");
        }
        this.value = UriEncoder.encode((String)tag);
        this.secondary = !tag.startsWith((String)PREFIX);
    }

    public Tag(Class<? extends Object> clazz) {
        if (clazz == null) {
            throw new NullPointerException((String)"Class for tag must be provided.");
        }
        this.value = PREFIX + UriEncoder.encode((String)clazz.getName());
    }

    public Tag(URI uri) {
        if (uri == null) {
            throw new NullPointerException((String)"URI for tag must be provided.");
        }
        this.value = uri.toASCIIString();
    }

    public boolean isSecondary() {
        return this.secondary;
    }

    public String getValue() {
        return this.value;
    }

    public boolean startsWith(String prefix) {
        return this.value.startsWith((String)prefix);
    }

    public String getClassName() {
        if (this.value.startsWith((String)PREFIX)) return UriEncoder.decode((String)this.value.substring((int)PREFIX.length()));
        throw new YAMLException((String)("Invalid tag: " + this.value));
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) return false;
        return this.value.equals((Object)((Tag)obj).getValue());
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean isCompatible(Class<?> clazz) {
        Set<Class<?>> set = COMPATIBILITY_MAP.get((Object)this);
        if (set == null) return false;
        return set.contains(clazz);
    }

    public boolean matches(Class<? extends Object> clazz) {
        return this.value.equals((Object)(PREFIX + clazz.getName()));
    }

    static {
        HashSet<Class<BigDecimal>> floatSet = new HashSet<Class<BigDecimal>>();
        floatSet.add(Double.class);
        floatSet.add(Float.class);
        floatSet.add(BigDecimal.class);
        COMPATIBILITY_MAP.put((Tag)FLOAT, floatSet);
        HashSet<Class<BigInteger>> intSet = new HashSet<Class<BigInteger>>();
        intSet.add(Integer.class);
        intSet.add(Long.class);
        intSet.add(BigInteger.class);
        COMPATIBILITY_MAP.put((Tag)INT, intSet);
        HashSet<Class<?>> timestampSet = new HashSet<Class<?>>();
        timestampSet.add(Date.class);
        try {
            timestampSet.add(Class.forName((String)"java.sql.Date"));
            timestampSet.add(Class.forName((String)"java.sql.Timestamp"));
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        COMPATIBILITY_MAP.put((Tag)TIMESTAMP, timestampSet);
    }
}

