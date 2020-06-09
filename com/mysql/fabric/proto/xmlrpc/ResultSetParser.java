/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.proto.xmlrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ResultSetParser {
    public List<Map<String, ?>> parse(Map<String, ?> info, List<List<Object>> rows) {
        List fieldNames = (List)info.get((Object)"names");
        HashMap<E, Integer> fieldNameIndexes = new HashMap<E, Integer>();
        for (int i = 0; i < fieldNames.size(); ++i) {
            fieldNameIndexes.put(fieldNames.get((int)i), Integer.valueOf((int)i));
        }
        ArrayList<Map<String, ?>> result = new ArrayList<Map<String, ?>>((int)rows.size());
        Iterator<List<Object>> i$ = rows.iterator();
        while (i$.hasNext()) {
            List<Object> r = i$.next();
            HashMap<K, Object> resultRow = new HashMap<K, Object>();
            for (Map.Entry<K, V> f : fieldNameIndexes.entrySet()) {
                resultRow.put(f.getKey(), r.get((int)((Integer)f.getValue()).intValue()));
            }
            result.add(resultRow);
        }
        return result;
    }
}

