/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric;

import com.mysql.fabric.HashShardMapping;
import com.mysql.fabric.RangeShardMapping;
import com.mysql.fabric.ShardIndex;
import com.mysql.fabric.ShardMapping;
import com.mysql.fabric.ShardMappingFactory;
import com.mysql.fabric.ShardTable;
import com.mysql.fabric.ShardingType;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ShardMappingFactory {
    public ShardMapping createShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices) {
        ShardMapping sm = null;
        switch (1.$SwitchMap$com$mysql$fabric$ShardingType[shardingType.ordinal()]) {
            case 1: {
                return new RangeShardMapping((int)mappingId, (ShardingType)shardingType, (String)globalGroupName, shardTables, shardIndices);
            }
            case 2: {
                return new HashShardMapping((int)mappingId, (ShardingType)shardingType, (String)globalGroupName, shardTables, shardIndices);
            }
        }
        throw new IllegalArgumentException((String)"Invalid ShardingType");
    }
}

