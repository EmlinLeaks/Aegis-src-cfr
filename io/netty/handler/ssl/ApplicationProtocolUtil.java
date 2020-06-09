/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class ApplicationProtocolUtil {
    private static final int DEFAULT_LIST_SIZE = 2;

    private ApplicationProtocolUtil() {
    }

    static List<String> toList(Iterable<String> protocols) {
        return ApplicationProtocolUtil.toList((int)2, protocols);
    }

    static List<String> toList(int initialListSize, Iterable<String> protocols) {
        if (protocols == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>((int)initialListSize);
        Iterator<String> iterator = protocols.iterator();
        do {
            if (!iterator.hasNext()) {
                if (!result.isEmpty()) return result;
                throw new IllegalArgumentException((String)"protocols cannot empty");
            }
            String p = iterator.next();
            if (p == null) throw new IllegalArgumentException((String)"protocol cannot be null or empty");
            if (p.isEmpty()) {
                throw new IllegalArgumentException((String)"protocol cannot be null or empty");
            }
            result.add((String)p);
        } while (true);
    }

    static List<String> toList(String ... protocols) {
        return ApplicationProtocolUtil.toList((int)2, (String[])protocols);
    }

    static List<String> toList(int initialListSize, String ... protocols) {
        if (protocols == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>((int)initialListSize);
        String[] arrstring = protocols;
        int n = arrstring.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                if (!result.isEmpty()) return result;
                throw new IllegalArgumentException((String)"protocols cannot empty");
            }
            String p = arrstring[n2];
            if (p == null) throw new IllegalArgumentException((String)"protocol cannot be null or empty");
            if (p.isEmpty()) {
                throw new IllegalArgumentException((String)"protocol cannot be null or empty");
            }
            result.add((String)p);
            ++n2;
        } while (true);
    }
}

