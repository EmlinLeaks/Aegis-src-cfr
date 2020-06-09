/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.Headers;
import io.netty.handler.codec.HeadersUtils;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class HeadersUtils {
    private HeadersUtils() {
    }

    public static <K, V> List<String> getAllAsString(Headers<K, V, ?> headers, K name) {
        List<V> allNames = headers.getAll(name);
        return new AbstractList<String>(allNames){
            final /* synthetic */ List val$allNames;
            {
                this.val$allNames = list;
            }

            public String get(int index) {
                E value = this.val$allNames.get((int)index);
                if (value == null) return null;
                String string = value.toString();
                return string;
            }

            public int size() {
                return this.val$allNames.size();
            }
        };
    }

    public static <K, V> String getAsString(Headers<K, V, ?> headers, K name) {
        V orig = headers.get(name);
        if (orig == null) return null;
        String string = orig.toString();
        return string;
    }

    public static Iterator<Map.Entry<String, String>> iteratorAsString(Iterable<Map.Entry<CharSequence, CharSequence>> headers) {
        return new StringEntryIterator(headers.iterator());
    }

    public static <K, V> String toString(Class<?> headersClass, Iterator<Map.Entry<K, V>> headersIt, int size) {
        String simpleName = headersClass.getSimpleName();
        if (size == 0) {
            return simpleName + "[]";
        }
        StringBuilder sb = new StringBuilder((int)(simpleName.length() + 2 + size * 20)).append((String)simpleName).append((char)'[');
        do {
            if (!headersIt.hasNext()) {
                sb.setLength((int)(sb.length() - 2));
                return sb.append((char)']').toString();
            }
            Map.Entry<K, V> header = headersIt.next();
            sb.append(header.getKey()).append((String)": ").append(header.getValue()).append((String)", ");
        } while (true);
    }

    public static Set<String> namesAsString(Headers<CharSequence, CharSequence, ?> headers) {
        return new CharSequenceDelegatingStringSet(headers.names());
    }
}

