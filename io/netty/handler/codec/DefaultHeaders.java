/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultHeaders<K, V, T extends Headers<K, V, T>>
implements Headers<K, V, T> {
    static final int HASH_CODE_SEED = -1028477387;
    private final HeaderEntry<K, V>[] entries;
    protected final HeaderEntry<K, V> head;
    private final byte hashMask;
    private final ValueConverter<V> valueConverter;
    private final NameValidator<K> nameValidator;
    private final HashingStrategy<K> hashingStrategy;
    int size;

    public DefaultHeaders(ValueConverter<V> valueConverter) {
        this(HashingStrategy.JAVA_HASHER, valueConverter);
    }

    public DefaultHeaders(ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
        this(HashingStrategy.JAVA_HASHER, valueConverter, nameValidator);
    }

    public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter) {
        this(nameHashingStrategy, valueConverter, NameValidator.NOT_NULL);
    }

    public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
        this(nameHashingStrategy, valueConverter, nameValidator, (int)16);
    }

    public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator, int arraySizeHint) {
        this.valueConverter = ObjectUtil.checkNotNull(valueConverter, (String)"valueConverter");
        this.nameValidator = ObjectUtil.checkNotNull(nameValidator, (String)"nameValidator");
        this.hashingStrategy = ObjectUtil.checkNotNull(nameHashingStrategy, (String)"nameHashingStrategy");
        this.entries = new HeaderEntry[io.netty.util.internal.MathUtil.findNextPositivePowerOfTwo((int)java.lang.Math.max((int)2, (int)java.lang.Math.min((int)arraySizeHint, (int)128)))];
        this.hashMask = (byte)(this.entries.length - 1);
        this.head = new HeaderEntry<K, V>();
    }

    @Override
    public V get(K name) {
        ObjectUtil.checkNotNull(name, (String)"name");
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        HeaderEntry<K, V> e = this.entries[i];
        V value = null;
        while (e != null) {
            if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
                value = (V)e.value;
            }
            e = e.next;
        }
        return (V)value;
    }

    @Override
    public V get(K name, V defaultValue) {
        V value = this.get(name);
        if (value != null) return (V)value;
        return (V)defaultValue;
    }

    @Override
    public V getAndRemove(K name) {
        int h = this.hashingStrategy.hashCode(name);
        return (V)this.remove0((int)h, (int)this.index((int)h), ObjectUtil.checkNotNull(name, (String)"name"));
    }

    @Override
    public V getAndRemove(K name, V defaultValue) {
        V value = this.getAndRemove(name);
        if (value != null) return (V)value;
        return (V)defaultValue;
    }

    @Override
    public List<V> getAll(K name) {
        ObjectUtil.checkNotNull(name, (String)"name");
        LinkedList<V> values = new LinkedList<V>();
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        HeaderEntry<K, V> e = this.entries[i];
        while (e != null) {
            if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
                values.addFirst(e.getValue());
            }
            e = e.next;
        }
        return values;
    }

    public Iterator<V> valueIterator(K name) {
        return new ValueIterator((DefaultHeaders)this, name);
    }

    @Override
    public List<V> getAllAndRemove(K name) {
        List<V> all = this.getAll(name);
        this.remove(name);
        return all;
    }

    @Override
    public boolean contains(K name) {
        if (this.get(name) == null) return false;
        return true;
    }

    @Override
    public boolean containsObject(K name, Object value) {
        return this.contains(name, this.valueConverter.convertObject((Object)ObjectUtil.checkNotNull(value, (String)"value")));
    }

    @Override
    public boolean containsBoolean(K name, boolean value) {
        return this.contains(name, this.valueConverter.convertBoolean((boolean)value));
    }

    @Override
    public boolean containsByte(K name, byte value) {
        return this.contains(name, this.valueConverter.convertByte((byte)value));
    }

    @Override
    public boolean containsChar(K name, char value) {
        return this.contains(name, this.valueConverter.convertChar((char)value));
    }

    @Override
    public boolean containsShort(K name, short value) {
        return this.contains(name, this.valueConverter.convertShort((short)value));
    }

    @Override
    public boolean containsInt(K name, int value) {
        return this.contains(name, this.valueConverter.convertInt((int)value));
    }

    @Override
    public boolean containsLong(K name, long value) {
        return this.contains(name, this.valueConverter.convertLong((long)value));
    }

    @Override
    public boolean containsFloat(K name, float value) {
        return this.contains(name, this.valueConverter.convertFloat((float)value));
    }

    @Override
    public boolean containsDouble(K name, double value) {
        return this.contains(name, this.valueConverter.convertDouble((double)value));
    }

    @Override
    public boolean containsTimeMillis(K name, long value) {
        return this.contains(name, this.valueConverter.convertTimeMillis((long)value));
    }

    @Override
    public boolean contains(K name, V value) {
        return this.contains(name, value, HashingStrategy.JAVA_HASHER);
    }

    public final boolean contains(K name, V value, HashingStrategy<? super V> valueHashingStrategy) {
        ObjectUtil.checkNotNull(name, (String)"name");
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        HeaderEntry<K, V> e = this.entries[i];
        while (e != null) {
            if (e.hash == h && this.hashingStrategy.equals(name, e.key) && valueHashingStrategy.equals(value, e.value)) {
                return true;
            }
            e = e.next;
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        if (this.head != this.head.after) return false;
        return true;
    }

    @Override
    public Set<K> names() {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet<K> names = new LinkedHashSet<K>((int)this.size());
        HeaderEntry<K, V> e = this.head.after;
        while (e != this.head) {
            names.add(e.getKey());
            e = e.after;
        }
        return names;
    }

    @Override
    public T add(K name, V value) {
        this.nameValidator.validateName(name);
        ObjectUtil.checkNotNull(value, (String)"value");
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        this.add0((int)h, (int)i, name, value);
        return (T)this.thisT();
    }

    @Override
    public T add(K name, Iterable<? extends V> values) {
        this.nameValidator.validateName(name);
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        Iterator<V> iterator = values.iterator();
        while (iterator.hasNext()) {
            V v = iterator.next();
            this.add0((int)h, (int)i, name, v);
        }
        return (T)this.thisT();
    }

    @Override
    public T add(K name, V ... values) {
        this.nameValidator.validateName(name);
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        V[] arrV = values;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            V v = arrV[n2];
            this.add0((int)h, (int)i, name, v);
            ++n2;
        }
        return (T)this.thisT();
    }

    @Override
    public T addObject(K name, Object value) {
        return (T)this.add(name, this.valueConverter.convertObject((Object)ObjectUtil.checkNotNull(value, (String)"value")));
    }

    @Override
    public T addObject(K name, Iterable<?> values) {
        Iterator<?> iterator = values.iterator();
        while (iterator.hasNext()) {
            ? value = iterator.next();
            this.addObject(name, value);
        }
        return (T)this.thisT();
    }

    @Override
    public T addObject(K name, Object ... values) {
        Object[] arrobject = values;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object value = arrobject[n2];
            this.addObject(name, (Object)value);
            ++n2;
        }
        return (T)this.thisT();
    }

    @Override
    public T addInt(K name, int value) {
        return (T)this.add(name, this.valueConverter.convertInt((int)value));
    }

    @Override
    public T addLong(K name, long value) {
        return (T)this.add(name, this.valueConverter.convertLong((long)value));
    }

    @Override
    public T addDouble(K name, double value) {
        return (T)this.add(name, this.valueConverter.convertDouble((double)value));
    }

    @Override
    public T addTimeMillis(K name, long value) {
        return (T)this.add(name, this.valueConverter.convertTimeMillis((long)value));
    }

    @Override
    public T addChar(K name, char value) {
        return (T)this.add(name, this.valueConverter.convertChar((char)value));
    }

    @Override
    public T addBoolean(K name, boolean value) {
        return (T)this.add(name, this.valueConverter.convertBoolean((boolean)value));
    }

    @Override
    public T addFloat(K name, float value) {
        return (T)this.add(name, this.valueConverter.convertFloat((float)value));
    }

    @Override
    public T addByte(K name, byte value) {
        return (T)this.add(name, this.valueConverter.convertByte((byte)value));
    }

    @Override
    public T addShort(K name, short value) {
        return (T)this.add(name, this.valueConverter.convertShort((short)value));
    }

    @Override
    public T add(Headers<? extends K, ? extends V, ?> headers) {
        if (headers == this) {
            throw new IllegalArgumentException((String)"can't add to itself.");
        }
        this.addImpl(headers);
        return (T)this.thisT();
    }

    protected void addImpl(Headers<? extends K, ? extends V, ?> headers) {
        if (headers instanceof DefaultHeaders) {
            DefaultHeaders defaultHeaders = (DefaultHeaders)headers;
            HeaderEntry<K, V> e = defaultHeaders.head.after;
            if (defaultHeaders.hashingStrategy == this.hashingStrategy && defaultHeaders.nameValidator == this.nameValidator) {
                while (e != defaultHeaders.head) {
                    this.add0((int)e.hash, (int)this.index((int)e.hash), e.key, e.value);
                    e = e.after;
                }
                return;
            }
            while (e != defaultHeaders.head) {
                this.add(e.key, e.value);
                e = e.after;
            }
            return;
        }
        Iterator<Map.Entry<K, V>> defaultHeaders = headers.iterator();
        while (defaultHeaders.hasNext()) {
            Map.Entry<K, V> header = defaultHeaders.next();
            this.add(header.getKey(), header.getValue());
        }
    }

    @Override
    public T set(K name, V value) {
        this.nameValidator.validateName(name);
        ObjectUtil.checkNotNull(value, (String)"value");
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        this.remove0((int)h, (int)i, name);
        this.add0((int)h, (int)i, name, value);
        return (T)this.thisT();
    }

    @Override
    public T set(K name, Iterable<? extends V> values) {
        this.nameValidator.validateName(name);
        ObjectUtil.checkNotNull(values, (String)"values");
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        this.remove0((int)h, (int)i, name);
        Iterator<V> iterator = values.iterator();
        while (iterator.hasNext()) {
            V v = iterator.next();
            if (v == null) {
                return (T)((T)this.thisT());
            }
            this.add0((int)h, (int)i, name, v);
        }
        return (T)this.thisT();
    }

    @Override
    public T set(K name, V ... values) {
        this.nameValidator.validateName(name);
        ObjectUtil.checkNotNull(values, (String)"values");
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        this.remove0((int)h, (int)i, name);
        V[] arrV = values;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            V v = arrV[n2];
            if (v == null) {
                return (T)((T)this.thisT());
            }
            this.add0((int)h, (int)i, name, v);
            ++n2;
        }
        return (T)this.thisT();
    }

    @Override
    public T setObject(K name, Object value) {
        ObjectUtil.checkNotNull(value, (String)"value");
        V convertedValue = ObjectUtil.checkNotNull(this.valueConverter.convertObject((Object)value), (String)"convertedValue");
        return (T)this.set(name, convertedValue);
    }

    @Override
    public T setObject(K name, Iterable<?> values) {
        this.nameValidator.validateName(name);
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        this.remove0((int)h, (int)i, name);
        Iterator<?> iterator = values.iterator();
        while (iterator.hasNext()) {
            ? v = iterator.next();
            if (v == null) {
                return (T)((T)this.thisT());
            }
            this.add0((int)h, (int)i, name, this.valueConverter.convertObject(v));
        }
        return (T)this.thisT();
    }

    @Override
    public T setObject(K name, Object ... values) {
        this.nameValidator.validateName(name);
        int h = this.hashingStrategy.hashCode(name);
        int i = this.index((int)h);
        this.remove0((int)h, (int)i, name);
        Object[] arrobject = values;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object v = arrobject[n2];
            if (v == null) {
                return (T)((T)this.thisT());
            }
            this.add0((int)h, (int)i, name, this.valueConverter.convertObject((Object)v));
            ++n2;
        }
        return (T)this.thisT();
    }

    @Override
    public T setInt(K name, int value) {
        return (T)this.set(name, this.valueConverter.convertInt((int)value));
    }

    @Override
    public T setLong(K name, long value) {
        return (T)this.set(name, this.valueConverter.convertLong((long)value));
    }

    @Override
    public T setDouble(K name, double value) {
        return (T)this.set(name, this.valueConverter.convertDouble((double)value));
    }

    @Override
    public T setTimeMillis(K name, long value) {
        return (T)this.set(name, this.valueConverter.convertTimeMillis((long)value));
    }

    @Override
    public T setFloat(K name, float value) {
        return (T)this.set(name, this.valueConverter.convertFloat((float)value));
    }

    @Override
    public T setChar(K name, char value) {
        return (T)this.set(name, this.valueConverter.convertChar((char)value));
    }

    @Override
    public T setBoolean(K name, boolean value) {
        return (T)this.set(name, this.valueConverter.convertBoolean((boolean)value));
    }

    @Override
    public T setByte(K name, byte value) {
        return (T)this.set(name, this.valueConverter.convertByte((byte)value));
    }

    @Override
    public T setShort(K name, short value) {
        return (T)this.set(name, this.valueConverter.convertShort((short)value));
    }

    @Override
    public T set(Headers<? extends K, ? extends V, ?> headers) {
        if (headers == this) return (T)this.thisT();
        this.clear();
        this.addImpl(headers);
        return (T)this.thisT();
    }

    @Override
    public T setAll(Headers<? extends K, ? extends V, ?> headers) {
        if (headers == this) return (T)this.thisT();
        Iterator<K> iterator = headers.names().iterator();
        do {
            if (!iterator.hasNext()) {
                this.addImpl(headers);
                return (T)this.thisT();
            }
            K key = iterator.next();
            this.remove(key);
        } while (true);
    }

    @Override
    public boolean remove(K name) {
        if (this.getAndRemove(name) == null) return false;
        return true;
    }

    @Override
    public T clear() {
        Arrays.fill((Object[])this.entries, null);
        this.head.after = this.head;
        this.head.before = this.head.after;
        this.size = 0;
        return (T)this.thisT();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new HeaderIterator((DefaultHeaders)this, null);
    }

    @Override
    public Boolean getBoolean(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Boolean bl = Boolean.valueOf((boolean)this.valueConverter.convertToBoolean(v));
            return bl;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public boolean getBoolean(K name, boolean defaultValue) {
        boolean bl;
        Boolean v = this.getBoolean(name);
        if (v != null) {
            bl = v.booleanValue();
            return bl;
        }
        bl = defaultValue;
        return bl;
    }

    @Override
    public Byte getByte(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Byte by = Byte.valueOf((byte)this.valueConverter.convertToByte(v));
            return by;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public byte getByte(K name, byte defaultValue) {
        byte by;
        Byte v = this.getByte(name);
        if (v != null) {
            by = v.byteValue();
            return by;
        }
        by = defaultValue;
        return by;
    }

    @Override
    public Character getChar(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Character c = Character.valueOf((char)this.valueConverter.convertToChar(v));
            return c;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public char getChar(K name, char defaultValue) {
        char c;
        Character v = this.getChar(name);
        if (v != null) {
            c = v.charValue();
            return c;
        }
        c = defaultValue;
        return c;
    }

    @Override
    public Short getShort(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Short s = Short.valueOf((short)this.valueConverter.convertToShort(v));
            return s;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public short getShort(K name, short defaultValue) {
        short s;
        Short v = this.getShort(name);
        if (v != null) {
            s = v.shortValue();
            return s;
        }
        s = defaultValue;
        return s;
    }

    @Override
    public Integer getInt(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Integer n = Integer.valueOf((int)this.valueConverter.convertToInt(v));
            return n;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public int getInt(K name, int defaultValue) {
        int n;
        Integer v = this.getInt(name);
        if (v != null) {
            n = v.intValue();
            return n;
        }
        n = defaultValue;
        return n;
    }

    @Override
    public Long getLong(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Long l = Long.valueOf((long)this.valueConverter.convertToLong(v));
            return l;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public long getLong(K name, long defaultValue) {
        long l;
        Long v = this.getLong(name);
        if (v != null) {
            l = v.longValue();
            return l;
        }
        l = defaultValue;
        return l;
    }

    @Override
    public Float getFloat(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Float f = Float.valueOf((float)this.valueConverter.convertToFloat(v));
            return f;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public float getFloat(K name, float defaultValue) {
        float f;
        Float v = this.getFloat(name);
        if (v != null) {
            f = v.floatValue();
            return f;
        }
        f = defaultValue;
        return f;
    }

    @Override
    public Double getDouble(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Double d = Double.valueOf((double)this.valueConverter.convertToDouble(v));
            return d;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public double getDouble(K name, double defaultValue) {
        double d;
        Double v = this.getDouble(name);
        if (v != null) {
            d = v.doubleValue();
            return d;
        }
        d = defaultValue;
        return d;
    }

    @Override
    public Long getTimeMillis(K name) {
        V v = this.get(name);
        try {
            if (v == null) return null;
            Long l = Long.valueOf((long)this.valueConverter.convertToTimeMillis(v));
            return l;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public long getTimeMillis(K name, long defaultValue) {
        long l;
        Long v = this.getTimeMillis(name);
        if (v != null) {
            l = v.longValue();
            return l;
        }
        l = defaultValue;
        return l;
    }

    @Override
    public Boolean getBooleanAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Boolean bl = Boolean.valueOf((boolean)this.valueConverter.convertToBoolean(v));
            return bl;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public boolean getBooleanAndRemove(K name, boolean defaultValue) {
        boolean bl;
        Boolean v = this.getBooleanAndRemove(name);
        if (v != null) {
            bl = v.booleanValue();
            return bl;
        }
        bl = defaultValue;
        return bl;
    }

    @Override
    public Byte getByteAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Byte by = Byte.valueOf((byte)this.valueConverter.convertToByte(v));
            return by;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public byte getByteAndRemove(K name, byte defaultValue) {
        byte by;
        Byte v = this.getByteAndRemove(name);
        if (v != null) {
            by = v.byteValue();
            return by;
        }
        by = defaultValue;
        return by;
    }

    @Override
    public Character getCharAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Character c = Character.valueOf((char)this.valueConverter.convertToChar(v));
            return c;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public char getCharAndRemove(K name, char defaultValue) {
        char c;
        Character v = this.getCharAndRemove(name);
        if (v != null) {
            c = v.charValue();
            return c;
        }
        c = defaultValue;
        return c;
    }

    @Override
    public Short getShortAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Short s = Short.valueOf((short)this.valueConverter.convertToShort(v));
            return s;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public short getShortAndRemove(K name, short defaultValue) {
        short s;
        Short v = this.getShortAndRemove(name);
        if (v != null) {
            s = v.shortValue();
            return s;
        }
        s = defaultValue;
        return s;
    }

    @Override
    public Integer getIntAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Integer n = Integer.valueOf((int)this.valueConverter.convertToInt(v));
            return n;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public int getIntAndRemove(K name, int defaultValue) {
        int n;
        Integer v = this.getIntAndRemove(name);
        if (v != null) {
            n = v.intValue();
            return n;
        }
        n = defaultValue;
        return n;
    }

    @Override
    public Long getLongAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Long l = Long.valueOf((long)this.valueConverter.convertToLong(v));
            return l;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public long getLongAndRemove(K name, long defaultValue) {
        long l;
        Long v = this.getLongAndRemove(name);
        if (v != null) {
            l = v.longValue();
            return l;
        }
        l = defaultValue;
        return l;
    }

    @Override
    public Float getFloatAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Float f = Float.valueOf((float)this.valueConverter.convertToFloat(v));
            return f;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public float getFloatAndRemove(K name, float defaultValue) {
        float f;
        Float v = this.getFloatAndRemove(name);
        if (v != null) {
            f = v.floatValue();
            return f;
        }
        f = defaultValue;
        return f;
    }

    @Override
    public Double getDoubleAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Double d = Double.valueOf((double)this.valueConverter.convertToDouble(v));
            return d;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public double getDoubleAndRemove(K name, double defaultValue) {
        double d;
        Double v = this.getDoubleAndRemove(name);
        if (v != null) {
            d = v.doubleValue();
            return d;
        }
        d = defaultValue;
        return d;
    }

    @Override
    public Long getTimeMillisAndRemove(K name) {
        V v = this.getAndRemove(name);
        try {
            if (v == null) return null;
            Long l = Long.valueOf((long)this.valueConverter.convertToTimeMillis(v));
            return l;
        }
        catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public long getTimeMillisAndRemove(K name, long defaultValue) {
        long l;
        Long v = this.getTimeMillisAndRemove(name);
        if (v != null) {
            l = v.longValue();
            return l;
        }
        l = defaultValue;
        return l;
    }

    public boolean equals(Object o) {
        if (o instanceof Headers) return this.equals((Headers)o, HashingStrategy.JAVA_HASHER);
        return false;
    }

    public int hashCode() {
        return this.hashCode(HashingStrategy.JAVA_HASHER);
    }

    public final boolean equals(Headers<K, V, ?> h2, HashingStrategy<V> valueHashingStrategy) {
        if (h2.size() != this.size()) {
            return false;
        }
        if (this == h2) {
            return true;
        }
        Iterator<K> iterator = this.names().iterator();
        block0 : while (iterator.hasNext()) {
            K name = iterator.next();
            List<V> otherValues = h2.getAll(name);
            List<V> values = this.getAll(name);
            if (otherValues.size() != values.size()) {
                return false;
            }
            int i = 0;
            do {
                if (i >= otherValues.size()) continue block0;
                if (!valueHashingStrategy.equals(otherValues.get((int)i), values.get((int)i))) {
                    return false;
                }
                ++i;
            } while (true);
            break;
        }
        return true;
    }

    public final int hashCode(HashingStrategy<V> valueHashingStrategy) {
        int result = -1028477387;
        Iterator<K> iterator = this.names().iterator();
        block0 : while (iterator.hasNext()) {
            K name = iterator.next();
            result = 31 * result + this.hashingStrategy.hashCode(name);
            List<V> values = this.getAll(name);
            int i = 0;
            do {
                if (i >= values.size()) continue block0;
                result = 31 * result + valueHashingStrategy.hashCode(values.get((int)i));
                ++i;
            } while (true);
            break;
        }
        return result;
    }

    public String toString() {
        return HeadersUtils.toString(this.getClass(), this.iterator(), (int)this.size());
    }

    protected HeaderEntry<K, V> newHeaderEntry(int h, K name, V value, HeaderEntry<K, V> next) {
        return new HeaderEntry<K, V>((int)h, name, value, next, this.head);
    }

    protected ValueConverter<V> valueConverter() {
        return this.valueConverter;
    }

    private int index(int hash) {
        return hash & this.hashMask;
    }

    private void add0(int h, int i, K name, V value) {
        this.entries[i] = this.newHeaderEntry((int)h, name, value, this.entries[i]);
        ++this.size;
    }

    private V remove0(int h, int i, K name) {
        HeaderEntry<K, V> e = this.entries[i];
        if (e == null) {
            return (V)null;
        }
        V value = null;
        HeaderEntry<K, V> next = e.next;
        while (next != null) {
            if (next.hash == h && this.hashingStrategy.equals(name, next.key)) {
                value = (V)next.value;
                e.next = next.next;
                next.remove();
                --this.size;
            } else {
                e = next;
            }
            next = e.next;
        }
        e = this.entries[i];
        if (e.hash != h) return (V)value;
        if (!this.hashingStrategy.equals(name, e.key)) return (V)value;
        if (value == null) {
            value = (V)e.value;
        }
        this.entries[i] = e.next;
        e.remove();
        --this.size;
        return (V)value;
    }

    private HeaderEntry<K, V> remove0(HeaderEntry<K, V> entry, HeaderEntry<K, V> previous) {
        int i = this.index((int)entry.hash);
        HeaderEntry<K, V> e = this.entries[i];
        if (e == entry) {
            this.entries[i] = entry.next;
            previous = this.entries[i];
        } else {
            previous.next = entry.next;
        }
        entry.remove();
        --this.size;
        return previous;
    }

    private T thisT() {
        return (T)this;
    }

    public DefaultHeaders<K, V, T> copy() {
        DefaultHeaders<K, V, T> copy = new DefaultHeaders<K, V, T>(this.hashingStrategy, this.valueConverter, this.nameValidator, (int)this.entries.length);
        copy.addImpl(this);
        return copy;
    }

    static /* synthetic */ HashingStrategy access$100(DefaultHeaders x0) {
        return x0.hashingStrategy;
    }

    static /* synthetic */ HeaderEntry[] access$200(DefaultHeaders x0) {
        return x0.entries;
    }

    static /* synthetic */ int access$300(DefaultHeaders x0, int x1) {
        return x0.index((int)x1);
    }

    static /* synthetic */ HeaderEntry access$400(DefaultHeaders x0, HeaderEntry x1, HeaderEntry x2) {
        return x0.remove0(x1, x2);
    }
}

