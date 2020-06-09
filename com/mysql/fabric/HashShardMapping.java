/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric;

import com.mysql.fabric.HashShardMapping;
import com.mysql.fabric.ShardIndex;
import com.mysql.fabric.ShardMapping;
import com.mysql.fabric.ShardTable;
import com.mysql.fabric.ShardingType;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HashShardMapping
extends ShardMapping {
    private static final MessageDigest md5Hasher;

    public HashShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices) {
        super((int)mappingId, (ShardingType)shardingType, (String)globalGroupName, shardTables, new TreeSet<ShardIndex>(ReverseShardIndexSorter.instance));
        this.shardIndices.addAll(shardIndices);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ShardIndex getShardIndexForKey(String stringKey) {
        ShardIndex i;
        MessageDigest messageDigest = md5Hasher;
        // MONITORENTER : messageDigest
        String hashedKey = new BigInteger((int)1, (byte[])md5Hasher.digest((byte[])stringKey.getBytes())).toString((int)16).toUpperCase();
        // MONITOREXIT : messageDigest
        for (int i2 = 0; i2 < 32 - hashedKey.length(); ++i2) {
            hashedKey = "0" + hashedKey;
        }
        Iterator<E> i$ = this.shardIndices.iterator();
        do {
            if (!i$.hasNext()) return (ShardIndex)this.shardIndices.iterator().next();
        } while ((i = (ShardIndex)i$.next()).getBound().compareTo((String)hashedKey) > 0);
        return i;
    }

    static {
        try {
            md5Hasher = MessageDigest.getInstance((String)"MD5");
            return;
        }
        catch (NoSuchAlgorithmException ex) {
            throw new ExceptionInInitializerError((Throwable)ex);
        }
    }
}

