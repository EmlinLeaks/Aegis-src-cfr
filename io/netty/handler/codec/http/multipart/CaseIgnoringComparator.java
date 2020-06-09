/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import java.io.Serializable;
import java.util.Comparator;

final class CaseIgnoringComparator
implements Comparator<CharSequence>,
Serializable {
    private static final long serialVersionUID = 4582133183775373862L;
    static final CaseIgnoringComparator INSTANCE = new CaseIgnoringComparator();

    private CaseIgnoringComparator() {
    }

    @Override
    public int compare(CharSequence o1, CharSequence o2) {
        int o1Length = o1.length();
        int o2Length = o2.length();
        int min = Math.min((int)o1Length, (int)o2Length);
        int i = 0;
        while (i < min) {
            char c2;
            char c1 = o1.charAt((int)i);
            if (c1 != (c2 = o2.charAt((int)i)) && (c1 = Character.toUpperCase((char)c1)) != (c2 = Character.toUpperCase((char)c2)) && (c1 = Character.toLowerCase((char)c1)) != (c2 = Character.toLowerCase((char)c2))) {
                return c1 - c2;
            }
            ++i;
        }
        return o1Length - o2Length;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

