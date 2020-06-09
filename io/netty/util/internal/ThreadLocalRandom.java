/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class ThreadLocalRandom
extends Random {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadLocalRandom.class);
    private static final AtomicLong seedUniquifier = new AtomicLong();
    private static volatile long initialSeedUniquifier = SystemPropertyUtil.getLong((String)"io.netty.initialSeedUniquifier", (long)0L);
    private static final Thread seedGeneratorThread;
    private static final BlockingQueue<Long> seedQueue;
    private static final long seedGeneratorStartTime;
    private static volatile long seedGeneratorEndTime;
    private static final long multiplier = 25214903917L;
    private static final long addend = 11L;
    private static final long mask = 0xFFFFFFFFFFFFL;
    private long rnd;
    boolean initialized = true;
    private long pad0;
    private long pad1;
    private long pad2;
    private long pad3;
    private long pad4;
    private long pad5;
    private long pad6;
    private long pad7;
    private static final long serialVersionUID = -5851777807851030925L;

    public static void setInitialSeedUniquifier(long initialSeedUniquifier) {
        ThreadLocalRandom.initialSeedUniquifier = initialSeedUniquifier;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long getInitialSeedUniquifier() {
        boolean interrupted;
        long initialSeedUniquifier;
        block10 : {
            long waitTime;
            initialSeedUniquifier = ThreadLocalRandom.initialSeedUniquifier;
            if (initialSeedUniquifier != 0L) {
                return initialSeedUniquifier;
            }
            Class<ThreadLocalRandom> class_ = ThreadLocalRandom.class;
            // MONITORENTER : io.netty.util.internal.ThreadLocalRandom.class
            initialSeedUniquifier = ThreadLocalRandom.initialSeedUniquifier;
            if (initialSeedUniquifier != 0L) {
                // MONITOREXIT : class_
                return initialSeedUniquifier;
            }
            long timeoutSeconds = 3L;
            long deadLine = seedGeneratorStartTime + TimeUnit.SECONDS.toNanos((long)3L);
            interrupted = false;
            do {
                waitTime = deadLine - System.nanoTime();
                try {
                    Long seed = waitTime <= 0L ? (Long)seedQueue.poll() : seedQueue.poll((long)waitTime, (TimeUnit)TimeUnit.NANOSECONDS);
                    if (seed == null) continue;
                    initialSeedUniquifier = seed.longValue();
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    logger.warn((String)"Failed to generate a seed from SecureRandom due to an InterruptedException.");
                }
                break block10;
            } while (waitTime > 0L);
            seedGeneratorThread.interrupt();
            logger.warn((String)"Failed to generate a seed from SecureRandom within {} seconds. Not enough entropy?", (Object)Long.valueOf((long)3L));
        }
        initialSeedUniquifier ^= 3627065505421648153L;
        ThreadLocalRandom.initialSeedUniquifier = initialSeedUniquifier ^= Long.reverse((long)System.nanoTime());
        if (interrupted) {
            Thread.currentThread().interrupt();
            seedGeneratorThread.interrupt();
        }
        if (seedGeneratorEndTime == 0L) {
            seedGeneratorEndTime = System.nanoTime();
        }
        // MONITOREXIT : class_
        return initialSeedUniquifier;
    }

    private static long newSeed() {
        long current;
        long actualCurrent;
        long next;
        while (!seedUniquifier.compareAndSet((long)current, (long)(next = (actualCurrent = (current = seedUniquifier.get()) != 0L ? current : ThreadLocalRandom.getInitialSeedUniquifier()) * 181783497276652981L))) {
        }
        if (current != 0L) return next ^ System.nanoTime();
        if (!logger.isDebugEnabled()) return next ^ System.nanoTime();
        if (seedGeneratorEndTime != 0L) {
            logger.debug((String)String.format((String)"-Dio.netty.initialSeedUniquifier: 0x%016x (took %d ms)", (Object[])new Object[]{Long.valueOf((long)actualCurrent), Long.valueOf((long)TimeUnit.NANOSECONDS.toMillis((long)(seedGeneratorEndTime - seedGeneratorStartTime)))}));
            return next ^ System.nanoTime();
        }
        logger.debug((String)String.format((String)"-Dio.netty.initialSeedUniquifier: 0x%016x", (Object[])new Object[]{Long.valueOf((long)actualCurrent)}));
        return next ^ System.nanoTime();
    }

    private static long mix64(long z) {
        z = (z ^ z >>> 33) * -49064778989728563L;
        z = (z ^ z >>> 33) * -4265267296055464877L;
        return z ^ z >>> 33;
    }

    ThreadLocalRandom() {
        super((long)ThreadLocalRandom.newSeed());
    }

    public static ThreadLocalRandom current() {
        return InternalThreadLocalMap.get().random();
    }

    @Override
    public void setSeed(long seed) {
        if (this.initialized) {
            throw new UnsupportedOperationException();
        }
        this.rnd = (seed ^ 25214903917L) & 0xFFFFFFFFFFFFL;
    }

    @Override
    protected int next(int bits) {
        this.rnd = this.rnd * 25214903917L + 11L & 0xFFFFFFFFFFFFL;
        return (int)(this.rnd >>> 48 - bits);
    }

    public int nextInt(int least, int bound) {
        if (least < bound) return this.nextInt((int)(bound - least)) + least;
        throw new IllegalArgumentException();
    }

    public long nextLong(long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException((String)"n must be positive");
        }
        long offset = 0L;
        while (n >= Integer.MAX_VALUE) {
            long nextn;
            int bits = this.next((int)2);
            long half = n >>> 1;
            long l = nextn = (bits & 2) == 0 ? half : n - half;
            if ((bits & 1) == 0) {
                offset += n - nextn;
            }
            n = nextn;
        }
        return offset + (long)this.nextInt((int)((int)n));
    }

    public long nextLong(long least, long bound) {
        if (least < bound) return this.nextLong((long)(bound - least)) + least;
        throw new IllegalArgumentException();
    }

    public double nextDouble(double n) {
        if (!(n <= 0.0)) return this.nextDouble() * n;
        throw new IllegalArgumentException((String)"n must be positive");
    }

    public double nextDouble(double least, double bound) {
        if (!(least >= bound)) return this.nextDouble() * (bound - least) + least;
        throw new IllegalArgumentException();
    }

    static /* synthetic */ long access$002(long x0) {
        seedGeneratorEndTime = x0;
        return seedGeneratorEndTime;
    }

    static /* synthetic */ BlockingQueue access$100() {
        return seedQueue;
    }

    static /* synthetic */ InternalLogger access$200() {
        return logger;
    }

    static {
        if (initialSeedUniquifier != 0L) {
            seedGeneratorThread = null;
            seedQueue = null;
            seedGeneratorStartTime = 0L;
            return;
        }
        boolean secureRandom = SystemPropertyUtil.getBoolean((String)"java.util.secureRandomSeed", (boolean)false);
        if (secureRandom) {
            seedQueue = new LinkedBlockingQueue<Long>();
            seedGeneratorStartTime = System.nanoTime();
            seedGeneratorThread = new Thread((String)"initialSeedUniquifierGenerator"){

                public void run() {
                    java.security.SecureRandom random = new java.security.SecureRandom();
                    byte[] seed = random.generateSeed((int)8);
                    ThreadLocalRandom.access$002((long)System.nanoTime());
                    long s = ((long)seed[0] & 255L) << 56 | ((long)seed[1] & 255L) << 48 | ((long)seed[2] & 255L) << 40 | ((long)seed[3] & 255L) << 32 | ((long)seed[4] & 255L) << 24 | ((long)seed[5] & 255L) << 16 | ((long)seed[6] & 255L) << 8 | (long)seed[7] & 255L;
                    ThreadLocalRandom.access$100().add(Long.valueOf((long)s));
                }
            };
            seedGeneratorThread.setDaemon((boolean)true);
            seedGeneratorThread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new Thread.UncaughtExceptionHandler(){

                public void uncaughtException(Thread t, java.lang.Throwable e) {
                    ThreadLocalRandom.access$200().debug((String)"An exception has been raised by {}", (Object)t.getName(), (Object)e);
                }
            });
            seedGeneratorThread.start();
            return;
        }
        initialSeedUniquifier = ThreadLocalRandom.mix64((long)System.currentTimeMillis()) ^ ThreadLocalRandom.mix64((long)System.nanoTime());
        seedGeneratorThread = null;
        seedQueue = null;
        seedGeneratorStartTime = 0L;
    }
}

