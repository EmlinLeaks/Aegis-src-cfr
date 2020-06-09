/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.hash.ChecksumHashFunction;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.MacHashFunction;
import com.google.common.hash.Murmur3_128HashFunction;
import com.google.common.hash.Murmur3_32HashFunction;
import com.google.common.hash.SipHashFunction;
import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.Checksum;
import javax.crypto.spec.SecretKeySpec;

@Beta
public final class Hashing {
    private static final int GOOD_FAST_HASH_SEED = (int)System.currentTimeMillis();

    public static HashFunction goodFastHash(int minimumBits) {
        int bits = Hashing.checkPositiveAndMakeMultipleOf32((int)minimumBits);
        if (bits == 32) {
            return Murmur3_32Holder.GOOD_FAST_HASH_FUNCTION_32;
        }
        if (bits <= 128) {
            return Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
        }
        int hashFunctionsNeeded = (bits + 127) / 128;
        HashFunction[] hashFunctions = new HashFunction[hashFunctionsNeeded];
        hashFunctions[0] = Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
        int seed = GOOD_FAST_HASH_SEED;
        int i = 1;
        while (i < hashFunctionsNeeded) {
            hashFunctions[i] = Hashing.murmur3_128((int)(seed += 1500450271));
            ++i;
        }
        return new ConcatenatedHashFunction((HashFunction[])hashFunctions, null);
    }

    public static HashFunction murmur3_32(int seed) {
        return new Murmur3_32HashFunction((int)seed);
    }

    public static HashFunction murmur3_32() {
        return Murmur3_32Holder.MURMUR3_32;
    }

    public static HashFunction murmur3_128(int seed) {
        return new Murmur3_128HashFunction((int)seed);
    }

    public static HashFunction murmur3_128() {
        return Murmur3_128Holder.MURMUR3_128;
    }

    public static HashFunction sipHash24() {
        return SipHash24Holder.SIP_HASH_24;
    }

    public static HashFunction sipHash24(long k0, long k1) {
        return new SipHashFunction((int)2, (int)4, (long)k0, (long)k1);
    }

    public static HashFunction md5() {
        return Md5Holder.MD5;
    }

    public static HashFunction sha1() {
        return Sha1Holder.SHA_1;
    }

    public static HashFunction sha256() {
        return Sha256Holder.SHA_256;
    }

    public static HashFunction sha384() {
        return Sha384Holder.SHA_384;
    }

    public static HashFunction sha512() {
        return Sha512Holder.SHA_512;
    }

    public static HashFunction hmacMd5(Key key) {
        return new MacHashFunction((String)"HmacMD5", (Key)key, (String)Hashing.hmacToString((String)"hmacMd5", (Key)key));
    }

    public static HashFunction hmacMd5(byte[] key) {
        return Hashing.hmacMd5((Key)new SecretKeySpec((byte[])Preconditions.checkNotNull(key), (String)"HmacMD5"));
    }

    public static HashFunction hmacSha1(Key key) {
        return new MacHashFunction((String)"HmacSHA1", (Key)key, (String)Hashing.hmacToString((String)"hmacSha1", (Key)key));
    }

    public static HashFunction hmacSha1(byte[] key) {
        return Hashing.hmacSha1((Key)new SecretKeySpec((byte[])Preconditions.checkNotNull(key), (String)"HmacSHA1"));
    }

    public static HashFunction hmacSha256(Key key) {
        return new MacHashFunction((String)"HmacSHA256", (Key)key, (String)Hashing.hmacToString((String)"hmacSha256", (Key)key));
    }

    public static HashFunction hmacSha256(byte[] key) {
        return Hashing.hmacSha256((Key)new SecretKeySpec((byte[])Preconditions.checkNotNull(key), (String)"HmacSHA256"));
    }

    public static HashFunction hmacSha512(Key key) {
        return new MacHashFunction((String)"HmacSHA512", (Key)key, (String)Hashing.hmacToString((String)"hmacSha512", (Key)key));
    }

    public static HashFunction hmacSha512(byte[] key) {
        return Hashing.hmacSha512((Key)new SecretKeySpec((byte[])Preconditions.checkNotNull(key), (String)"HmacSHA512"));
    }

    private static String hmacToString(String methodName, Key key) {
        return String.format((String)"Hashing.%s(Key[algorithm=%s, format=%s])", (Object[])new Object[]{methodName, key.getAlgorithm(), key.getFormat()});
    }

    public static HashFunction crc32c() {
        return Crc32cHolder.CRC_32_C;
    }

    public static HashFunction crc32() {
        return Crc32Holder.CRC_32;
    }

    public static HashFunction adler32() {
        return Adler32Holder.ADLER_32;
    }

    private static HashFunction checksumHashFunction(ChecksumType type, String toString) {
        return new ChecksumHashFunction((Supplier<? extends Checksum>)type, (int)((ChecksumType)type).bits, (String)toString);
    }

    public static HashFunction farmHashFingerprint64() {
        return FarmHashFingerprint64Holder.FARMHASH_FINGERPRINT_64;
    }

    public static int consistentHash(HashCode hashCode, int buckets) {
        return Hashing.consistentHash((long)hashCode.padToLong(), (int)buckets);
    }

    public static int consistentHash(long input, int buckets) {
        int next;
        Preconditions.checkArgument((boolean)(buckets > 0), (String)"buckets must be positive: %s", (int)buckets);
        LinearCongruentialGenerator generator = new LinearCongruentialGenerator((long)input);
        int candidate = 0;
        while ((next = (int)((double)(candidate + 1) / generator.nextDouble())) >= 0) {
            if (next >= buckets) return candidate;
            candidate = next;
        }
        return candidate;
    }

    public static HashCode combineOrdered(Iterable<HashCode> hashCodes) {
        Iterator<HashCode> iterator = hashCodes.iterator();
        Preconditions.checkArgument((boolean)iterator.hasNext(), (Object)"Must be at least 1 hash code to combine.");
        int bits = iterator.next().bits();
        byte[] resultBytes = new byte[bits / 8];
        Iterator<HashCode> i$ = hashCodes.iterator();
        block0 : while (i$.hasNext()) {
            HashCode hashCode = i$.next();
            byte[] nextBytes = hashCode.asBytes();
            Preconditions.checkArgument((boolean)(nextBytes.length == resultBytes.length), (Object)"All hashcodes must have the same bit length.");
            int i = 0;
            do {
                if (i >= nextBytes.length) continue block0;
                resultBytes[i] = (byte)(resultBytes[i] * 37 ^ nextBytes[i]);
                ++i;
            } while (true);
            break;
        }
        return HashCode.fromBytesNoCopy((byte[])resultBytes);
    }

    public static HashCode combineUnordered(Iterable<HashCode> hashCodes) {
        Iterator<HashCode> iterator = hashCodes.iterator();
        Preconditions.checkArgument((boolean)iterator.hasNext(), (Object)"Must be at least 1 hash code to combine.");
        byte[] resultBytes = new byte[iterator.next().bits() / 8];
        Iterator<HashCode> i$ = hashCodes.iterator();
        block0 : while (i$.hasNext()) {
            HashCode hashCode = i$.next();
            byte[] nextBytes = hashCode.asBytes();
            Preconditions.checkArgument((boolean)(nextBytes.length == resultBytes.length), (Object)"All hashcodes must have the same bit length.");
            int i = 0;
            do {
                if (i >= nextBytes.length) continue block0;
                byte[] arrby = resultBytes;
                int n = i;
                arrby[n] = (byte)(arrby[n] + nextBytes[i]);
                ++i;
            } while (true);
            break;
        }
        return HashCode.fromBytesNoCopy((byte[])resultBytes);
    }

    static int checkPositiveAndMakeMultipleOf32(int bits) {
        Preconditions.checkArgument((boolean)(bits > 0), (Object)"Number of bits must be positive");
        return bits + 31 & -32;
    }

    public static HashFunction concatenating(HashFunction first, HashFunction second, HashFunction ... rest) {
        ArrayList<HashFunction> list = new ArrayList<HashFunction>();
        list.add(first);
        list.add(second);
        HashFunction[] arr$ = rest;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            HashFunction hashFunc = arr$[i$];
            list.add(hashFunc);
            ++i$;
        }
        return new ConcatenatedHashFunction((HashFunction[])list.toArray(new HashFunction[0]), null);
    }

    public static HashFunction concatenating(Iterable<HashFunction> hashFunctions) {
        Preconditions.checkNotNull(hashFunctions);
        ArrayList<HashFunction> list = new ArrayList<HashFunction>();
        for (HashFunction hashFunction : hashFunctions) {
            list.add(hashFunction);
        }
        Preconditions.checkArgument((boolean)(list.size() > 0), (String)"number of hash functions (%s) must be > 0", (int)list.size());
        return new ConcatenatedHashFunction((HashFunction[])list.toArray(new HashFunction[0]), null);
    }

    private Hashing() {
    }

    static /* synthetic */ int access$100() {
        return GOOD_FAST_HASH_SEED;
    }

    static /* synthetic */ HashFunction access$200(ChecksumType x0, String x1) {
        return Hashing.checksumHashFunction((ChecksumType)x0, (String)x1);
    }
}

