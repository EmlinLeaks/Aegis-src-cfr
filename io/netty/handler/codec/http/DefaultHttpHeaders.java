/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.DefaultHeadersImpl;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.HashingStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultHttpHeaders
extends HttpHeaders {
    private static final int HIGHEST_INVALID_VALUE_CHAR_MASK = -16;
    private static final ByteProcessor HEADER_NAME_VALIDATOR = new ByteProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            DefaultHttpHeaders.access$000((byte)value);
            return true;
        }
    };
    static final DefaultHeaders.NameValidator<CharSequence> HttpNameValidator = new DefaultHeaders.NameValidator<CharSequence>(){

        public void validateName(CharSequence name) {
            if (name == null) throw new IllegalArgumentException((String)("empty headers are not allowed [" + name + "]"));
            if (name.length() == 0) {
                throw new IllegalArgumentException((String)("empty headers are not allowed [" + name + "]"));
            }
            if (name instanceof AsciiString) {
                try {
                    ((AsciiString)name).forEachByte((ByteProcessor)DefaultHttpHeaders.access$100());
                    return;
                }
                catch (java.lang.Exception e) {
                    io.netty.util.internal.PlatformDependent.throwException((java.lang.Throwable)e);
                    return;
                }
            }
            int index = 0;
            while (index < name.length()) {
                DefaultHttpHeaders.access$200((char)name.charAt((int)index));
                ++index;
            }
        }
    };
    private final DefaultHeaders<CharSequence, CharSequence, ?> headers;

    public DefaultHttpHeaders() {
        this((boolean)true);
    }

    public DefaultHttpHeaders(boolean validate) {
        this((boolean)validate, DefaultHttpHeaders.nameValidator((boolean)validate));
    }

    protected DefaultHttpHeaders(boolean validate, DefaultHeaders.NameValidator<CharSequence> nameValidator) {
        this(new DefaultHeadersImpl<CharSequence, CharSequence>(AsciiString.CASE_INSENSITIVE_HASHER, DefaultHttpHeaders.valueConverter((boolean)validate), nameValidator));
    }

    protected DefaultHttpHeaders(DefaultHeaders<CharSequence, CharSequence, ?> headers) {
        this.headers = headers;
    }

    @Override
    public HttpHeaders add(HttpHeaders headers) {
        if (!(headers instanceof DefaultHttpHeaders)) return super.add((HttpHeaders)headers);
        this.headers.add(((DefaultHttpHeaders)headers).headers);
        return this;
    }

    @Override
    public HttpHeaders set(HttpHeaders headers) {
        if (!(headers instanceof DefaultHttpHeaders)) return super.set((HttpHeaders)headers);
        this.headers.set(((DefaultHttpHeaders)headers).headers);
        return this;
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        this.headers.addObject((CharSequence)name, (Object)value);
        return this;
    }

    @Override
    public HttpHeaders add(CharSequence name, Object value) {
        this.headers.addObject((CharSequence)name, (Object)value);
        return this;
    }

    @Override
    public HttpHeaders add(String name, Iterable<?> values) {
        this.headers.addObject((CharSequence)name, values);
        return this;
    }

    @Override
    public HttpHeaders add(CharSequence name, Iterable<?> values) {
        this.headers.addObject((CharSequence)name, values);
        return this;
    }

    @Override
    public HttpHeaders addInt(CharSequence name, int value) {
        this.headers.addInt((CharSequence)name, (int)value);
        return this;
    }

    @Override
    public HttpHeaders addShort(CharSequence name, short value) {
        this.headers.addShort((CharSequence)name, (short)value);
        return this;
    }

    @Override
    public HttpHeaders remove(String name) {
        this.headers.remove((CharSequence)name);
        return this;
    }

    @Override
    public HttpHeaders remove(CharSequence name) {
        this.headers.remove((CharSequence)name);
        return this;
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        this.headers.setObject((CharSequence)name, (Object)value);
        return this;
    }

    @Override
    public HttpHeaders set(CharSequence name, Object value) {
        this.headers.setObject((CharSequence)name, (Object)value);
        return this;
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> values) {
        this.headers.setObject((CharSequence)name, values);
        return this;
    }

    @Override
    public HttpHeaders set(CharSequence name, Iterable<?> values) {
        this.headers.setObject((CharSequence)name, values);
        return this;
    }

    @Override
    public HttpHeaders setInt(CharSequence name, int value) {
        this.headers.setInt((CharSequence)name, (int)value);
        return this;
    }

    @Override
    public HttpHeaders setShort(CharSequence name, short value) {
        this.headers.setShort((CharSequence)name, (short)value);
        return this;
    }

    @Override
    public HttpHeaders clear() {
        this.headers.clear();
        return this;
    }

    @Override
    public String get(String name) {
        return this.get((CharSequence)name);
    }

    @Override
    public String get(CharSequence name) {
        return HeadersUtils.getAsString(this.headers, name);
    }

    @Override
    public Integer getInt(CharSequence name) {
        return this.headers.getInt((CharSequence)name);
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        return this.headers.getInt((CharSequence)name, (int)defaultValue);
    }

    @Override
    public Short getShort(CharSequence name) {
        return this.headers.getShort((CharSequence)name);
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        return this.headers.getShort((CharSequence)name, (short)defaultValue);
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        return this.headers.getTimeMillis((CharSequence)name);
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        return this.headers.getTimeMillis((CharSequence)name, (long)defaultValue);
    }

    @Override
    public List<String> getAll(String name) {
        return this.getAll((CharSequence)name);
    }

    @Override
    public List<String> getAll(CharSequence name) {
        return HeadersUtils.getAllAsString(this.headers, name);
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Map.Entry<String, String>> entriesConverted = new ArrayList<Map.Entry<String, String>>((int)this.headers.size());
        Iterator<Map.Entry<String, String>> iterator = this.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            entriesConverted.add(entry);
        }
        return entriesConverted;
    }

    @Deprecated
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return HeadersUtils.iteratorAsString(this.headers);
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        return this.headers.iterator();
    }

    @Override
    public Iterator<String> valueStringIterator(CharSequence name) {
        Iterator<CharSequence> itr = this.valueCharSequenceIterator((CharSequence)name);
        return new Iterator<String>((DefaultHttpHeaders)this, itr){
            final /* synthetic */ Iterator val$itr;
            final /* synthetic */ DefaultHttpHeaders this$0;
            {
                this.this$0 = this$0;
                this.val$itr = iterator;
            }

            public boolean hasNext() {
                return this.val$itr.hasNext();
            }

            public String next() {
                return ((CharSequence)this.val$itr.next()).toString();
            }

            public void remove() {
                this.val$itr.remove();
            }
        };
    }

    public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
        return this.headers.valueIterator((CharSequence)name);
    }

    @Override
    public boolean contains(String name) {
        return this.contains((CharSequence)name);
    }

    @Override
    public boolean contains(CharSequence name) {
        return this.headers.contains((CharSequence)name);
    }

    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public boolean contains(String name, String value, boolean ignoreCase) {
        return this.contains((CharSequence)name, (CharSequence)value, (boolean)ignoreCase);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        HashingStrategy<CharSequence> hashingStrategy;
        if (ignoreCase) {
            hashingStrategy = AsciiString.CASE_INSENSITIVE_HASHER;
            return this.headers.contains((CharSequence)name, (CharSequence)value, hashingStrategy);
        }
        hashingStrategy = AsciiString.CASE_SENSITIVE_HASHER;
        return this.headers.contains((CharSequence)name, (CharSequence)value, hashingStrategy);
    }

    @Override
    public Set<String> names() {
        return HeadersUtils.namesAsString(this.headers);
    }

    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpHeaders)) return false;
        if (!this.headers.equals(((DefaultHttpHeaders)o).headers, AsciiString.CASE_SENSITIVE_HASHER)) return false;
        return true;
    }

    public int hashCode() {
        return this.headers.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public HttpHeaders copy() {
        return new DefaultHttpHeaders(this.headers.copy());
    }

    private static void validateHeaderNameElement(byte value) {
        switch (value) {
            case 0: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 32: 
            case 44: 
            case 58: 
            case 59: 
            case 61: {
                throw new IllegalArgumentException((String)("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + value));
            }
        }
        if (value >= 0) return;
        throw new IllegalArgumentException((String)("a header name cannot contain non-ASCII character: " + value));
    }

    private static void validateHeaderNameElement(char value) {
        switch (value) {
            case '\u0000': 
            case '\t': 
            case '\n': 
            case '\u000b': 
            case '\f': 
            case '\r': 
            case ' ': 
            case ',': 
            case ':': 
            case ';': 
            case '=': {
                throw new IllegalArgumentException((String)("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + value));
            }
        }
        if (value <= '') return;
        throw new IllegalArgumentException((String)("a header name cannot contain non-ASCII character: " + value));
    }

    static ValueConverter<CharSequence> valueConverter(boolean validate) {
        HeaderValueConverter headerValueConverter;
        if (validate) {
            headerValueConverter = HeaderValueConverterAndValidator.INSTANCE;
            return headerValueConverter;
        }
        headerValueConverter = HeaderValueConverter.INSTANCE;
        return headerValueConverter;
    }

    static DefaultHeaders.NameValidator<CharSequence> nameValidator(boolean validate) {
        DefaultHeaders.NameValidator nameValidator;
        if (validate) {
            nameValidator = HttpNameValidator;
            return nameValidator;
        }
        nameValidator = DefaultHeaders.NameValidator.NOT_NULL;
        return nameValidator;
    }

    static /* synthetic */ void access$000(byte x0) {
        DefaultHttpHeaders.validateHeaderNameElement((byte)x0);
    }

    static /* synthetic */ ByteProcessor access$100() {
        return HEADER_NAME_VALIDATOR;
    }

    static /* synthetic */ void access$200(char x0) {
        DefaultHttpHeaders.validateHeaderNameElement((char)x0);
    }
}

