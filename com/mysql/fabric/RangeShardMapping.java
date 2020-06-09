/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric;

import com.mysql.fabric.RangeShardMapping;
import com.mysql.fabric.ShardIndex;
import com.mysql.fabric.ShardMapping;
import com.mysql.fabric.ShardTable;
import com.mysql.fabric.ShardingType;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RangeShardMapping
extends ShardMapping {
    public RangeShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices) {
        super((int)mappingId, (ShardingType)shardingType, (String)globalGroupName, shardTables, new TreeSet<ShardIndex>(RangeShardIndexSorter.instance));
        this.shardIndices.addAll(shardIndices);
    }

    @Override
    protected ShardIndex getShardIndexForKey(String stringKey) {
        ShardIndex i;
        Integer lowerBound;
        Integer key = Integer.valueOf((int)-1);
        key = Integer.valueOf((int)Integer.parseInt((String)stringKey));
        Iterator<E> i$ = this.shardIndices.iterator();
        do {
            if (!i$.hasNext()) return null;
            i = (ShardIndex)i$.next();
            lowerBound = Integer.valueOf((String)i.getBound());
        } while (key.intValue() < lowerBound.intValue());
        return i;
    }
}

