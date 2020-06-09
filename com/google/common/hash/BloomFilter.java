/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.BloomFilterStrategies;
import com.google.common.hash.Funnel;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import javax.annotation.Nullable;

@Beta
public final class BloomFilter<T>
implements Predicate<T>,
Serializable {
    private final BloomFilterStrategies.BitArray bits;
    private final int numHashFunctions;
    private final Funnel<? super T> funnel;
    private final Strategy strategy;

    private BloomFilter(BloomFilterStrategies.BitArray bits, int numHashFunctions, Funnel<? super T> funnel, Strategy strategy) {
        Preconditions.checkArgument((boolean)(numHashFunctions > 0), (String)"numHashFunctions (%s) must be > 0", (int)numHashFunctions);
        Preconditions.checkArgument((boolean)(numHashFunctions <= 255), (String)"numHashFunctions (%s) must be <= 255", (int)numHashFunctions);
        this.bits = Preconditions.checkNotNull(bits);
        this.numHashFunctions = numHashFunctions;
        this.funnel = Preconditions.checkNotNull(funnel);
        this.strategy = Preconditions.checkNotNull(strategy);
    }

    public BloomFilter<T> copy() {
        return new BloomFilter<T>((BloomFilterStrategies.BitArray)this.bits.copy(), (int)this.numHashFunctions, this.funnel, (Strategy)this.strategy);
    }

    public boolean mightContain(T object) {
        return this.strategy.mightContain(object, this.funnel, (int)this.numHashFunctions, (BloomFilterStrategies.BitArray)this.bits);
    }

    @Deprecated
    @Override
    public boolean apply(T input) {
        return this.mightContain(input);
    }

    @CanIgnoreReturnValue
    public boolean put(T object) {
        return this.strategy.put(object, this.funnel, (int)this.numHashFunctions, (BloomFilterStrategies.BitArray)this.bits);
    }

    public double expectedFpp() {
        return Math.pow((double)((double)this.bits.bitCount() / (double)this.bitSize()), (double)((double)this.numHashFunctions));
    }

    @VisibleForTesting
    long bitSize() {
        return this.bits.bitSize();
    }

    public boolean isCompatible(BloomFilter<T> that) {
        Preconditions.checkNotNull(that);
        if (this == that) return false;
        if (this.numHashFunctions != that.numHashFunctions) return false;
        if (this.bitSize() != that.bitSize()) return false;
        if (!this.strategy.equals((Object)that.strategy)) return false;
        if (!this.funnel.equals(that.funnel)) return false;
        return true;
    }

    public void putAll(BloomFilter<T> that) {
        Preconditions.checkNotNull(that);
        Preconditions.checkArgument((boolean)(this != that), (Object)"Cannot combine a BloomFilter with itself.");
        Preconditions.checkArgument((boolean)(this.numHashFunctions == that.numHashFunctions), (String)"BloomFilters must have the same number of hash functions (%s != %s)", (int)this.numHashFunctions, (int)that.numHashFunctions);
        Preconditions.checkArgument((boolean)(this.bitSize() == that.bitSize()), (String)"BloomFilters must have the same size underlying bit arrays (%s != %s)", (long)this.bitSize(), (long)that.bitSize());
        Preconditions.checkArgument((boolean)this.strategy.equals((Object)that.strategy), (String)"BloomFilters must have equal strategies (%s != %s)", (Object)this.strategy, (Object)that.strategy);
        Preconditions.checkArgument((boolean)this.funnel.equals(that.funnel), (String)"BloomFilters must have equal funnels (%s != %s)", this.funnel, that.funnel);
        this.bits.putAll((BloomFilterStrategies.BitArray)that.bits);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof BloomFilter)) return false;
        BloomFilter that = (BloomFilter)object;
        if (this.numHashFunctions != that.numHashFunctions) return false;
        if (!this.funnel.equals(that.funnel)) return false;
        if (!this.bits.equals((Object)that.bits)) return false;
        if (!this.strategy.equals((Object)that.strategy)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{Integer.valueOf((int)this.numHashFunctions), this.funnel, this.strategy, this.bits});
    }

    public static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions, double fpp) {
        return BloomFilter.create(funnel, (long)((long)expectedInsertions), (double)fpp);
    }

    public static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions, double fpp) {
        return BloomFilter.create(funnel, (long)expectedInsertions, (double)fpp, (Strategy)BloomFilterStrategies.MURMUR128_MITZ_64);
    }

    @VisibleForTesting
    static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions, double fpp, Strategy strategy) {
        Preconditions.checkNotNull(funnel);
        Preconditions.checkArgument((boolean)(expectedInsertions >= 0L), (String)"Expected insertions (%s) must be >= 0", (long)expectedInsertions);
        Preconditions.checkArgument((boolean)(fpp > 0.0), (String)"False positive probability (%s) must be > 0.0", (Object)Double.valueOf((double)fpp));
        Preconditions.checkArgument((boolean)(fpp < 1.0), (String)"False positive probability (%s) must be < 1.0", (Object)Double.valueOf((double)fpp));
        Preconditions.checkNotNull(strategy);
        if (expectedInsertions == 0L) {
            expectedInsertions = 1L;
        }
        long numBits = BloomFilter.optimalNumOfBits((long)expectedInsertions, (double)fpp);
        int numHashFunctions = BloomFilter.optimalNumOfHashFunctions((long)expectedInsertions, (long)numBits);
        try {
            return new BloomFilter<T>((BloomFilterStrategies.BitArray)new BloomFilterStrategies.BitArray((long)numBits), (int)numHashFunctions, funnel, (Strategy)strategy);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException((String)("Could not create BloomFilter of " + numBits + " bits"), (Throwable)e);
        }
    }

    public static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions) {
        return BloomFilter.create(funnel, (long)((long)expectedInsertions));
    }

    public static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions) {
        return BloomFilter.create(funnel, (long)expectedInsertions, (double)0.03);
    }

    @VisibleForTesting
    static int optimalNumOfHashFunctions(long n, long m) {
        return Math.max((int)1, (int)((int)Math.round((double)((double)m / (double)n * Math.log((double)2.0)))));
    }

    @VisibleForTesting
    static long optimalNumOfBits(long n, double p) {
        if (p != 0.0) return (long)((double)(-n) * Math.log((double)p) / (Math.log((double)2.0) * Math.log((double)2.0)));
        p = Double.MIN_VALUE;
        return (long)((double)(-n) * Math.log((double)p) / (Math.log((double)2.0) * Math.log((double)2.0)));
    }

    private Object writeReplace() {
        return new SerialForm<T>(this);
    }

    public void writeTo(OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream((OutputStream)out);
        dout.writeByte((int)SignedBytes.checkedCast((long)((long)this.strategy.ordinal())));
        dout.writeByte((int)UnsignedBytes.checkedCast((long)((long)this.numHashFunctions)));
        dout.writeInt((int)this.bits.data.length);
        long[] arr$ = this.bits.data;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            long value = arr$[i$];
            dout.writeLong((long)value);
            ++i$;
        }
    }

    public static <T> BloomFilter<T> readFrom(InputStream in, Funnel<T> funnel) throws IOException {
        Preconditions.checkNotNull(in, (Object)"InputStream");
        Preconditions.checkNotNull(funnel, (Object)"Funnel");
        int strategyOrdinal = -1;
        int numHashFunctions = -1;
        int dataLength = -1;
        try {
            DataInputStream din = new DataInputStream((InputStream)in);
            strategyOrdinal = (int)din.readByte();
            numHashFunctions = UnsignedBytes.toInt((byte)din.readByte());
            dataLength = din.readInt();
            BloomFilterStrategies strategy = BloomFilterStrategies.values()[strategyOrdinal];
            long[] data = new long[dataLength];
            int i = 0;
            while (i < data.length) {
                data[i] = din.readLong();
                ++i;
            }
            return new BloomFilter<T>((BloomFilterStrategies.BitArray)new BloomFilterStrategies.BitArray((long[])data), (int)numHashFunctions, funnel, (Strategy)strategy);
        }
        catch (RuntimeException e) {
            String message = "Unable to deserialize BloomFilter from InputStream. strategyOrdinal: " + strategyOrdinal + " numHashFunctions: " + numHashFunctions + " dataLength: " + dataLength;
            throw new IOException((String)message, (Throwable)e);
        }
    }

    static /* synthetic */ BloomFilterStrategies.BitArray access$000(BloomFilter x0) {
        return x0.bits;
    }

    static /* synthetic */ int access$100(BloomFilter x0) {
        return x0.numHashFunctions;
    }

    static /* synthetic */ Funnel access$200(BloomFilter x0) {
        return x0.funnel;
    }

    static /* synthetic */ Strategy access$300(BloomFilter x0) {
        return x0.strategy;
    }
}

