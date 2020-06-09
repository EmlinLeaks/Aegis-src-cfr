/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.BloomFilterStrategies;

enum BloomFilterStrategies implements BloomFilter.Strategy
{
    MURMUR128_MITZ_32{

        public <T> boolean put(T object, com.google.common.hash.Funnel<? super T> funnel, int numHashFunctions, com.google.common.hash.BloomFilterStrategies$BitArray bits) {
            long bitSize = bits.bitSize();
            long hash64 = com.google.common.hash.Hashing.murmur3_128().hashObject(object, funnel).asLong();
            int hash1 = (int)hash64;
            int hash2 = (int)(hash64 >>> 32);
            boolean bitsChanged = false;
            int i = 1;
            while (i <= numHashFunctions) {
                int combinedHash = hash1 + i * hash2;
                if (combinedHash < 0) {
                    combinedHash ^= -1;
                }
                bitsChanged |= bits.set((long)((long)combinedHash % bitSize));
                ++i;
            }
            return bitsChanged;
        }

        public <T> boolean mightContain(T object, com.google.common.hash.Funnel<? super T> funnel, int numHashFunctions, com.google.common.hash.BloomFilterStrategies$BitArray bits) {
            long bitSize = bits.bitSize();
            long hash64 = com.google.common.hash.Hashing.murmur3_128().hashObject(object, funnel).asLong();
            int hash1 = (int)hash64;
            int hash2 = (int)(hash64 >>> 32);
            int i = 1;
            while (i <= numHashFunctions) {
                int combinedHash = hash1 + i * hash2;
                if (combinedHash < 0) {
                    combinedHash ^= -1;
                }
                if (!bits.get((long)((long)combinedHash % bitSize))) {
                    return false;
                }
                ++i;
            }
            return true;
        }
    }
    ,
    MURMUR128_MITZ_64{

        public <T> boolean put(T object, com.google.common.hash.Funnel<? super T> funnel, int numHashFunctions, com.google.common.hash.BloomFilterStrategies$BitArray bits) {
            long bitSize = bits.bitSize();
            byte[] bytes = com.google.common.hash.Hashing.murmur3_128().hashObject(object, funnel).getBytesInternal();
            long hash1 = this.lowerEight((byte[])bytes);
            long hash2 = this.upperEight((byte[])bytes);
            boolean bitsChanged = false;
            long combinedHash = hash1;
            int i = 0;
            while (i < numHashFunctions) {
                bitsChanged |= bits.set((long)((combinedHash & Long.MAX_VALUE) % bitSize));
                combinedHash += hash2;
                ++i;
            }
            return bitsChanged;
        }

        public <T> boolean mightContain(T object, com.google.common.hash.Funnel<? super T> funnel, int numHashFunctions, com.google.common.hash.BloomFilterStrategies$BitArray bits) {
            long bitSize = bits.bitSize();
            byte[] bytes = com.google.common.hash.Hashing.murmur3_128().hashObject(object, funnel).getBytesInternal();
            long hash1 = this.lowerEight((byte[])bytes);
            long hash2 = this.upperEight((byte[])bytes);
            long combinedHash = hash1;
            int i = 0;
            while (i < numHashFunctions) {
                if (!bits.get((long)((combinedHash & Long.MAX_VALUE) % bitSize))) {
                    return false;
                }
                combinedHash += hash2;
                ++i;
            }
            return true;
        }

        private long lowerEight(byte[] bytes) {
            return com.google.common.primitives.Longs.fromBytes((byte)bytes[7], (byte)bytes[6], (byte)bytes[5], (byte)bytes[4], (byte)bytes[3], (byte)bytes[2], (byte)bytes[1], (byte)bytes[0]);
        }

        private long upperEight(byte[] bytes) {
            return com.google.common.primitives.Longs.fromBytes((byte)bytes[15], (byte)bytes[14], (byte)bytes[13], (byte)bytes[12], (byte)bytes[11], (byte)bytes[10], (byte)bytes[9], (byte)bytes[8]);
        }
    };
    
}

