/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Strings {
    public static final String EMPTY = "";
    public static final String LINE_SEPARATOR = System.getProperty((String)"line.separator");

    private Strings() {
        throw new UnsupportedOperationException();
    }

    public static String repeat(char ch, int count) {
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        while (i < count) {
            buffer.append((char)ch);
            ++i;
        }
        return buffer.toString();
    }

    public static boolean isNullOrEmpty(String target) {
        if (target == null) return true;
        if (EMPTY.equals((Object)target)) return true;
        return false;
    }

    public static String surround(String target, char begin, char end) {
        return begin + target + end;
    }

    public static String join(String[] pieces, String separator) {
        return Strings.join(Arrays.asList(pieces), (String)separator);
    }

    public static String join(List<String> pieces, String separator) {
        StringBuilder buffer = new StringBuilder();
        Iterator<String> iter = pieces.iterator();
        while (iter.hasNext()) {
            buffer.append((String)iter.next());
            if (!iter.hasNext()) continue;
            buffer.append((String)separator);
        }
        return buffer.toString();
    }
}

