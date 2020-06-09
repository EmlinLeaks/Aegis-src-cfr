/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.ReadOnlyHttpHeaders;
import io.netty.util.AsciiString;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ReadOnlyHttpHeaders
extends HttpHeaders {
    private final CharSequence[] nameValuePairs;

    public ReadOnlyHttpHeaders(boolean validateHeaders, CharSequence ... nameValuePairs) {
        if ((nameValuePairs.length & 1) != 0) {
            throw ReadOnlyHttpHeaders.newInvalidArraySizeException();
        }
        if (validateHeaders) {
            ReadOnlyHttpHeaders.validateHeaders((CharSequence[])nameValuePairs);
        }
        this.nameValuePairs = nameValuePairs;
    }

    private static IllegalArgumentException newInvalidArraySizeException() {
        return new IllegalArgumentException((String)"nameValuePairs must be arrays of [name, value] pairs");
    }

    private static void validateHeaders(CharSequence ... keyValuePairs) {
        int i = 0;
        while (i < keyValuePairs.length) {
            DefaultHttpHeaders.HttpNameValidator.validateName((CharSequence)keyValuePairs[i]);
            i += 2;
        }
    }

    private CharSequence get0(CharSequence name) {
        int nameHash = AsciiString.hashCode((CharSequence)name);
        int i = 0;
        while (i < this.nameValuePairs.length) {
            CharSequence roName = this.nameValuePairs[i];
            if (AsciiString.hashCode((CharSequence)roName) == nameHash && AsciiString.contentEqualsIgnoreCase((CharSequence)roName, (CharSequence)name)) {
                return this.nameValuePairs[i + 1];
            }
            i += 2;
        }
        return null;
    }

    @Override
    public String get(String name) {
        CharSequence value = this.get0((CharSequence)name);
        if (value == null) {
            return null;
        }
        String string = value.toString();
        return string;
    }

    @Override
    public Integer getInt(CharSequence name) {
        CharSequence value = this.get0((CharSequence)name);
        if (value == null) {
            return null;
        }
        Integer n = Integer.valueOf((int)CharSequenceValueConverter.INSTANCE.convertToInt((CharSequence)value));
        return n;
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        int n;
        CharSequence value = this.get0((CharSequence)name);
        if (value == null) {
            n = defaultValue;
            return n;
        }
        n = CharSequenceValueConverter.INSTANCE.convertToInt((CharSequence)value);
        return n;
    }

    @Override
    public Short getShort(CharSequence name) {
        CharSequence value = this.get0((CharSequence)name);
        if (value == null) {
            return null;
        }
        Short s = Short.valueOf((short)CharSequenceValueConverter.INSTANCE.convertToShort((CharSequence)value));
        return s;
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        short s;
        CharSequence value = this.get0((CharSequence)name);
        if (value == null) {
            s = defaultValue;
            return s;
        }
        s = CharSequenceValueConverter.INSTANCE.convertToShort((CharSequence)value);
        return s;
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        CharSequence value = this.get0((CharSequence)name);
        if (value == null) {
            return null;
        }
        Long l = Long.valueOf((long)CharSequenceValueConverter.INSTANCE.convertToTimeMillis((CharSequence)value));
        return l;
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        long l;
        CharSequence value = this.get0((CharSequence)name);
        if (value == null) {
            l = defaultValue;
            return l;
        }
        l = CharSequenceValueConverter.INSTANCE.convertToTimeMillis((CharSequence)value);
        return l;
    }

    @Override
    public List<String> getAll(String name) {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        int nameHash = AsciiString.hashCode((CharSequence)name);
        ArrayList<String> values = new ArrayList<String>((int)4);
        int i = 0;
        while (i < this.nameValuePairs.length) {
            CharSequence roName = this.nameValuePairs[i];
            if (AsciiString.hashCode((CharSequence)roName) == nameHash && AsciiString.contentEqualsIgnoreCase((CharSequence)roName, (CharSequence)name)) {
                values.add((String)this.nameValuePairs[i + 1].toString());
            }
            i += 2;
        }
        return values;
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>((int)this.size());
        int i = 0;
        while (i < this.nameValuePairs.length) {
            entries.add(new AbstractMap.SimpleImmutableEntry<String, String>(this.nameValuePairs[i].toString(), this.nameValuePairs[i + 1].toString()));
            i += 2;
        }
        return entries;
    }

    @Override
    public boolean contains(String name) {
        if (this.get0((CharSequence)name) == null) return false;
        return true;
    }

    @Override
    public boolean contains(String name, String value, boolean ignoreCase) {
        return this.containsValue((CharSequence)name, (CharSequence)value, (boolean)ignoreCase);
    }

    @Override
    public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
        if (ignoreCase) {
            int i = 0;
            while (i < this.nameValuePairs.length) {
                if (AsciiString.contentEqualsIgnoreCase((CharSequence)this.nameValuePairs[i], (CharSequence)name) && AsciiString.contentEqualsIgnoreCase((CharSequence)this.nameValuePairs[i + 1], (CharSequence)value)) {
                    return true;
                }
                i += 2;
            }
            return false;
        }
        int i = 0;
        while (i < this.nameValuePairs.length) {
            if (AsciiString.contentEqualsIgnoreCase((CharSequence)this.nameValuePairs[i], (CharSequence)name) && AsciiString.contentEquals((CharSequence)this.nameValuePairs[i + 1], (CharSequence)value)) {
                return true;
            }
            i += 2;
        }
        return false;
    }

    @Override
    public Iterator<String> valueStringIterator(CharSequence name) {
        return new ReadOnlyStringValueIterator((ReadOnlyHttpHeaders)this, (CharSequence)name);
    }

    public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
        return new ReadOnlyValueIterator((ReadOnlyHttpHeaders)this, (CharSequence)name);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return new ReadOnlyStringIterator((ReadOnlyHttpHeaders)this, null);
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        return new ReadOnlyIterator((ReadOnlyHttpHeaders)this, null);
    }

    @Override
    public boolean isEmpty() {
        if (this.nameValuePairs.length != 0) return false;
        return true;
    }

    @Override
    public int size() {
        return this.nameValuePairs.length >>> 1;
    }

    @Override
    public Set<String> names() {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet<String> names = new LinkedHashSet<String>((int)this.size());
        int i = 0;
        while (i < this.nameValuePairs.length) {
            names.add((String)this.nameValuePairs[i].toString());
            i += 2;
        }
        return names;
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders add(String name, Iterable<?> values) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders addInt(CharSequence name, int value) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders addShort(CharSequence name, short value) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> values) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders setInt(CharSequence name, int value) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders setShort(CharSequence name, short value) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders remove(String name) {
        throw new UnsupportedOperationException((String)"read only");
    }

    @Override
    public HttpHeaders clear() {
        throw new UnsupportedOperationException((String)"read only");
    }

    static /* synthetic */ CharSequence[] access$200(ReadOnlyHttpHeaders x0) {
        return x0.nameValuePairs;
    }
}

