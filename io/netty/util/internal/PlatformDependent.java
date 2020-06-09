/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.Cleaner;
import io.netty.util.internal.CleanerJava6;
import io.netty.util.internal.CleanerJava9;
import io.netty.util.internal.ConstantTimeUtils;
import io.netty.util.internal.LongAdderCounter;
import io.netty.util.internal.LongCounter;
import io.netty.util.internal.OutOfDirectMemoryError;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.SpscLinkedQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.SpscLinkedAtomicQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlatformDependent {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent.class);
    private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile((String)"\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
    private static final boolean IS_WINDOWS = PlatformDependent.isWindows0();
    private static final boolean IS_OSX = PlatformDependent.isOsx0();
    private static final boolean IS_J9_JVM = PlatformDependent.isJ9Jvm0();
    private static final boolean IS_IVKVM_DOT_NET = PlatformDependent.isIkvmDotNet0();
    private static final boolean MAYBE_SUPER_USER;
    private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
    private static final boolean DIRECT_BUFFER_PREFERRED;
    private static final long MAX_DIRECT_MEMORY;
    private static final int MPSC_CHUNK_SIZE = 1024;
    private static final int MIN_MAX_MPSC_CAPACITY = 2048;
    private static final int MAX_ALLOWED_MPSC_CAPACITY = 1073741824;
    private static final long BYTE_ARRAY_BASE_OFFSET;
    private static final File TMPDIR;
    private static final int BIT_MODE;
    private static final String NORMALIZED_ARCH;
    private static final String NORMALIZED_OS;
    private static final String[] ALLOWED_LINUX_OS_CLASSIFIERS;
    private static final Set<String> LINUX_OS_CLASSIFIERS;
    private static final int ADDRESS_SIZE;
    private static final boolean USE_DIRECT_BUFFER_NO_CLEANER;
    private static final AtomicLong DIRECT_MEMORY_COUNTER;
    private static final long DIRECT_MEMORY_LIMIT;
    private static final ThreadLocalRandomProvider RANDOM_PROVIDER;
    private static final Cleaner CLEANER;
    private static final int UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD;
    public static final boolean BIG_ENDIAN_NATIVE_ORDER;
    private static final Cleaner NOOP;

    public static boolean hasDirectBufferNoCleanerConstructor() {
        return PlatformDependent0.hasDirectBufferNoCleanerConstructor();
    }

    public static byte[] allocateUninitializedArray(int size) {
        byte[] arrby;
        if (UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD >= 0 && UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD <= size) {
            arrby = PlatformDependent0.allocateUninitializedArray((int)size);
            return arrby;
        }
        arrby = new byte[size];
        return arrby;
    }

    public static boolean isAndroid() {
        return PlatformDependent0.isAndroid();
    }

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean isOsx() {
        return IS_OSX;
    }

    public static boolean maybeSuperUser() {
        return MAYBE_SUPER_USER;
    }

    public static int javaVersion() {
        return PlatformDependent0.javaVersion();
    }

    public static boolean canEnableTcpNoDelayByDefault() {
        return CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    }

    public static boolean hasUnsafe() {
        if (UNSAFE_UNAVAILABILITY_CAUSE != null) return false;
        return true;
    }

    public static Throwable getUnsafeUnavailabilityCause() {
        return UNSAFE_UNAVAILABILITY_CAUSE;
    }

    public static boolean isUnaligned() {
        return PlatformDependent0.isUnaligned();
    }

    public static boolean directBufferPreferred() {
        return DIRECT_BUFFER_PREFERRED;
    }

    public static long maxDirectMemory() {
        return DIRECT_MEMORY_LIMIT;
    }

    public static long usedDirectMemory() {
        if (DIRECT_MEMORY_COUNTER == null) return -1L;
        long l = DIRECT_MEMORY_COUNTER.get();
        return l;
    }

    public static File tmpdir() {
        return TMPDIR;
    }

    public static int bitMode() {
        return BIT_MODE;
    }

    public static int addressSize() {
        return ADDRESS_SIZE;
    }

    public static long allocateMemory(long size) {
        return PlatformDependent0.allocateMemory((long)size);
    }

    public static void freeMemory(long address) {
        PlatformDependent0.freeMemory((long)address);
    }

    public static long reallocateMemory(long address, long newSize) {
        return PlatformDependent0.reallocateMemory((long)address, (long)newSize);
    }

    public static void throwException(Throwable t) {
        if (PlatformDependent.hasUnsafe()) {
            PlatformDependent0.throwException((Throwable)t);
            return;
        }
        PlatformDependent.throwException0((Throwable)t);
    }

    private static <E extends Throwable> void throwException0(Throwable t) throws Throwable {
        throw t;
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K, V>();
    }

    public static LongCounter newLongCounter() {
        if (PlatformDependent.javaVersion() < 8) return new AtomicLongCounter(null);
        return new LongAdderCounter();
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity) {
        return new ConcurrentHashMap<K, V>((int)initialCapacity);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity, float loadFactor) {
        return new ConcurrentHashMap<K, V>((int)initialCapacity, (float)loadFactor);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        return new ConcurrentHashMap<K, V>((int)initialCapacity, (float)loadFactor, (int)concurrencyLevel);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(Map<? extends K, ? extends V> map) {
        return new ConcurrentHashMap<K, V>(map);
    }

    public static void freeDirectBuffer(ByteBuffer buffer) {
        CLEANER.freeDirectBuffer((ByteBuffer)buffer);
    }

    public static long directBufferAddress(ByteBuffer buffer) {
        return PlatformDependent0.directBufferAddress((ByteBuffer)buffer);
    }

    public static ByteBuffer directBuffer(long memoryAddress, int size) {
        if (!PlatformDependent0.hasDirectBufferNoCleanerConstructor()) throw new UnsupportedOperationException((String)"sun.misc.Unsafe or java.nio.DirectByteBuffer.<init>(long, int) not available");
        return PlatformDependent0.newDirectBuffer((long)memoryAddress, (int)size);
    }

    public static Object getObject(Object object, long fieldOffset) {
        return PlatformDependent0.getObject((Object)object, (long)fieldOffset);
    }

    public static int getInt(Object object, long fieldOffset) {
        return PlatformDependent0.getInt((Object)object, (long)fieldOffset);
    }

    public static byte getByte(long address) {
        return PlatformDependent0.getByte((long)address);
    }

    public static short getShort(long address) {
        return PlatformDependent0.getShort((long)address);
    }

    public static int getInt(long address) {
        return PlatformDependent0.getInt((long)address);
    }

    public static long getLong(long address) {
        return PlatformDependent0.getLong((long)address);
    }

    public static byte getByte(byte[] data, int index) {
        return PlatformDependent0.getByte((byte[])data, (int)index);
    }

    public static short getShort(byte[] data, int index) {
        return PlatformDependent0.getShort((byte[])data, (int)index);
    }

    public static int getInt(byte[] data, int index) {
        return PlatformDependent0.getInt((byte[])data, (int)index);
    }

    public static long getLong(byte[] data, int index) {
        return PlatformDependent0.getLong((byte[])data, (int)index);
    }

    private static long getLongSafe(byte[] bytes, int offset) {
        if (!BIG_ENDIAN_NATIVE_ORDER) return (long)bytes[offset] & 255L | ((long)bytes[offset + 1] & 255L) << 8 | ((long)bytes[offset + 2] & 255L) << 16 | ((long)bytes[offset + 3] & 255L) << 24 | ((long)bytes[offset + 4] & 255L) << 32 | ((long)bytes[offset + 5] & 255L) << 40 | ((long)bytes[offset + 6] & 255L) << 48 | (long)bytes[offset + 7] << 56;
        return (long)bytes[offset] << 56 | ((long)bytes[offset + 1] & 255L) << 48 | ((long)bytes[offset + 2] & 255L) << 40 | ((long)bytes[offset + 3] & 255L) << 32 | ((long)bytes[offset + 4] & 255L) << 24 | ((long)bytes[offset + 5] & 255L) << 16 | ((long)bytes[offset + 6] & 255L) << 8 | (long)bytes[offset + 7] & 255L;
    }

    private static int getIntSafe(byte[] bytes, int offset) {
        if (!BIG_ENDIAN_NATIVE_ORDER) return bytes[offset] & 255 | (bytes[offset + 1] & 255) << 8 | (bytes[offset + 2] & 255) << 16 | bytes[offset + 3] << 24;
        return bytes[offset] << 24 | (bytes[offset + 1] & 255) << 16 | (bytes[offset + 2] & 255) << 8 | bytes[offset + 3] & 255;
    }

    private static short getShortSafe(byte[] bytes, int offset) {
        if (!BIG_ENDIAN_NATIVE_ORDER) return (short)(bytes[offset] & 255 | bytes[offset + 1] << 8);
        return (short)(bytes[offset] << 8 | bytes[offset + 1] & 255);
    }

    private static int hashCodeAsciiCompute(CharSequence value, int offset, int hash) {
        if (!BIG_ENDIAN_NATIVE_ORDER) return hash * -862048943 + PlatformDependent.hashCodeAsciiSanitizeInt((CharSequence)value, (int)offset) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt((CharSequence)value, (int)(offset + 4));
        return hash * -862048943 + PlatformDependent.hashCodeAsciiSanitizeInt((CharSequence)value, (int)(offset + 4)) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt((CharSequence)value, (int)offset);
    }

    private static int hashCodeAsciiSanitizeInt(CharSequence value, int offset) {
        if (!BIG_ENDIAN_NATIVE_ORDER) return (value.charAt((int)(offset + 3)) & 31) << 24 | (value.charAt((int)(offset + 2)) & 31) << 16 | (value.charAt((int)(offset + 1)) & 31) << 8 | value.charAt((int)offset) & 31;
        return value.charAt((int)(offset + 3)) & 31 | (value.charAt((int)(offset + 2)) & 31) << 8 | (value.charAt((int)(offset + 1)) & 31) << 16 | (value.charAt((int)offset) & 31) << 24;
    }

    private static int hashCodeAsciiSanitizeShort(CharSequence value, int offset) {
        if (!BIG_ENDIAN_NATIVE_ORDER) return (value.charAt((int)(offset + 1)) & 31) << 8 | value.charAt((int)offset) & 31;
        return value.charAt((int)(offset + 1)) & 31 | (value.charAt((int)offset) & 31) << 8;
    }

    private static int hashCodeAsciiSanitizeByte(char value) {
        return value & 31;
    }

    public static void putByte(long address, byte value) {
        PlatformDependent0.putByte((long)address, (byte)value);
    }

    public static void putShort(long address, short value) {
        PlatformDependent0.putShort((long)address, (short)value);
    }

    public static void putInt(long address, int value) {
        PlatformDependent0.putInt((long)address, (int)value);
    }

    public static void putLong(long address, long value) {
        PlatformDependent0.putLong((long)address, (long)value);
    }

    public static void putByte(byte[] data, int index, byte value) {
        PlatformDependent0.putByte((byte[])data, (int)index, (byte)value);
    }

    public static void putShort(byte[] data, int index, short value) {
        PlatformDependent0.putShort((byte[])data, (int)index, (short)value);
    }

    public static void putInt(byte[] data, int index, int value) {
        PlatformDependent0.putInt((byte[])data, (int)index, (int)value);
    }

    public static void putLong(byte[] data, int index, long value) {
        PlatformDependent0.putLong((byte[])data, (int)index, (long)value);
    }

    public static void putObject(Object o, long offset, Object x) {
        PlatformDependent0.putObject((Object)o, (long)offset, (Object)x);
    }

    public static long objectFieldOffset(Field field) {
        return PlatformDependent0.objectFieldOffset((Field)field);
    }

    public static void copyMemory(long srcAddr, long dstAddr, long length) {
        PlatformDependent0.copyMemory((long)srcAddr, (long)dstAddr, (long)length);
    }

    public static void copyMemory(byte[] src, int srcIndex, long dstAddr, long length) {
        PlatformDependent0.copyMemory((Object)src, (long)(BYTE_ARRAY_BASE_OFFSET + (long)srcIndex), null, (long)dstAddr, (long)length);
    }

    public static void copyMemory(long srcAddr, byte[] dst, int dstIndex, long length) {
        PlatformDependent0.copyMemory(null, (long)srcAddr, (Object)dst, (long)(BYTE_ARRAY_BASE_OFFSET + (long)dstIndex), (long)length);
    }

    public static void setMemory(byte[] dst, int dstIndex, long bytes, byte value) {
        PlatformDependent0.setMemory((Object)dst, (long)(BYTE_ARRAY_BASE_OFFSET + (long)dstIndex), (long)bytes, (byte)value);
    }

    public static void setMemory(long address, long bytes, byte value) {
        PlatformDependent0.setMemory((long)address, (long)bytes, (byte)value);
    }

    public static ByteBuffer allocateDirectNoCleaner(int capacity) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        PlatformDependent.incrementMemoryCounter((int)capacity);
        try {
            return PlatformDependent0.allocateDirectNoCleaner((int)capacity);
        }
        catch (Throwable e) {
            PlatformDependent.decrementMemoryCounter((int)capacity);
            PlatformDependent.throwException((Throwable)e);
            return null;
        }
    }

    public static ByteBuffer reallocateDirectNoCleaner(ByteBuffer buffer, int capacity) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        int len = capacity - buffer.capacity();
        PlatformDependent.incrementMemoryCounter((int)len);
        try {
            return PlatformDependent0.reallocateDirectNoCleaner((ByteBuffer)buffer, (int)capacity);
        }
        catch (Throwable e) {
            PlatformDependent.decrementMemoryCounter((int)len);
            PlatformDependent.throwException((Throwable)e);
            return null;
        }
    }

    public static void freeDirectNoCleaner(ByteBuffer buffer) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        int capacity = buffer.capacity();
        PlatformDependent0.freeMemory((long)PlatformDependent0.directBufferAddress((ByteBuffer)buffer));
        PlatformDependent.decrementMemoryCounter((int)capacity);
    }

    private static void incrementMemoryCounter(int capacity) {
        if (DIRECT_MEMORY_COUNTER == null) return;
        long newUsedMemory = DIRECT_MEMORY_COUNTER.addAndGet((long)((long)capacity));
        if (newUsedMemory <= DIRECT_MEMORY_LIMIT) return;
        DIRECT_MEMORY_COUNTER.addAndGet((long)((long)(-capacity)));
        throw new OutOfDirectMemoryError((String)("failed to allocate " + capacity + " byte(s) of direct memory (used: " + (newUsedMemory - (long)capacity) + ", max: " + DIRECT_MEMORY_LIMIT + ')'));
    }

    private static void decrementMemoryCounter(int capacity) {
        if (DIRECT_MEMORY_COUNTER == null) return;
        long usedMemory = DIRECT_MEMORY_COUNTER.addAndGet((long)((long)(-capacity)));
        if ($assertionsDisabled) return;
        if (usedMemory >= 0L) return;
        throw new AssertionError();
    }

    public static boolean useDirectBufferNoCleaner() {
        return USE_DIRECT_BUFFER_NO_CLEANER;
    }

    public static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        boolean bl;
        if (PlatformDependent.hasUnsafe() && PlatformDependent0.unalignedAccess()) {
            bl = PlatformDependent0.equals((byte[])bytes1, (int)startPos1, (byte[])bytes2, (int)startPos2, (int)length);
            return bl;
        }
        bl = PlatformDependent.equalsSafe((byte[])bytes1, (int)startPos1, (byte[])bytes2, (int)startPos2, (int)length);
        return bl;
    }

    public static boolean isZero(byte[] bytes, int startPos, int length) {
        boolean bl;
        if (PlatformDependent.hasUnsafe() && PlatformDependent0.unalignedAccess()) {
            bl = PlatformDependent0.isZero((byte[])bytes, (int)startPos, (int)length);
            return bl;
        }
        bl = PlatformDependent.isZeroSafe((byte[])bytes, (int)startPos, (int)length);
        return bl;
    }

    public static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        int n;
        if (PlatformDependent.hasUnsafe() && PlatformDependent0.unalignedAccess()) {
            n = PlatformDependent0.equalsConstantTime((byte[])bytes1, (int)startPos1, (byte[])bytes2, (int)startPos2, (int)length);
            return n;
        }
        n = ConstantTimeUtils.equalsConstantTime((byte[])bytes1, (int)startPos1, (byte[])bytes2, (int)startPos2, (int)length);
        return n;
    }

    public static int hashCodeAscii(byte[] bytes, int startPos, int length) {
        int n;
        if (PlatformDependent.hasUnsafe() && PlatformDependent0.unalignedAccess()) {
            n = PlatformDependent0.hashCodeAscii((byte[])bytes, (int)startPos, (int)length);
            return n;
        }
        n = PlatformDependent.hashCodeAsciiSafe((byte[])bytes, (int)startPos, (int)length);
        return n;
    }

    public static int hashCodeAscii(CharSequence bytes) {
        int n;
        int length = bytes.length();
        int remainingBytes = length & 7;
        int hash = -1028477387;
        if (length >= 32) {
            for (int i = length - 8; i >= remainingBytes; i -= 8) {
                hash = PlatformDependent.hashCodeAsciiCompute((CharSequence)bytes, (int)i, (int)hash);
            }
        } else if (length >= 8) {
            hash = PlatformDependent.hashCodeAsciiCompute((CharSequence)bytes, (int)(length - 8), (int)hash);
            if (length >= 16) {
                hash = PlatformDependent.hashCodeAsciiCompute((CharSequence)bytes, (int)(length - 16), (int)hash);
                if (length >= 24) {
                    hash = PlatformDependent.hashCodeAsciiCompute((CharSequence)bytes, (int)(length - 24), (int)hash);
                }
            }
        }
        if (remainingBytes == 0) {
            return hash;
        }
        int offset = 0;
        if (remainingBytes != 2 & remainingBytes != 4 & remainingBytes != 6) {
            hash = hash * -862048943 + PlatformDependent.hashCodeAsciiSanitizeByte((char)bytes.charAt((int)0));
            offset = 1;
        }
        if (remainingBytes != 1 & remainingBytes != 4 & remainingBytes != 5) {
            hash = hash * (offset == 0 ? -862048943 : 461845907) + PlatformDependent0.hashCodeAsciiSanitize((int)PlatformDependent.hashCodeAsciiSanitizeShort((CharSequence)bytes, (int)offset));
            offset += 2;
        }
        if (remainingBytes < 4) return hash;
        if (offset == 0 | offset == 3) {
            n = -862048943;
            return hash * n + PlatformDependent.hashCodeAsciiSanitizeInt((CharSequence)bytes, (int)offset);
        }
        n = 461845907;
        return hash * n + PlatformDependent.hashCodeAsciiSanitizeInt((CharSequence)bytes, (int)offset);
    }

    public static <T> Queue<T> newMpscQueue() {
        return Mpsc.newMpscQueue();
    }

    public static <T> Queue<T> newMpscQueue(int maxCapacity) {
        return Mpsc.newMpscQueue((int)maxCapacity);
    }

    public static <T> Queue<T> newSpscQueue() {
        AbstractQueue abstractQueue;
        if (PlatformDependent.hasUnsafe()) {
            abstractQueue = new SpscLinkedQueue<E>();
            return abstractQueue;
        }
        abstractQueue = new SpscLinkedAtomicQueue<E>();
        return abstractQueue;
    }

    public static <T> Queue<T> newFixedMpscQueue(int capacity) {
        AbstractQueue abstractQueue;
        if (PlatformDependent.hasUnsafe()) {
            abstractQueue = new MpscArrayQueue<E>((int)capacity);
            return abstractQueue;
        }
        abstractQueue = new MpscAtomicArrayQueue<E>((int)capacity);
        return abstractQueue;
    }

    public static ClassLoader getClassLoader(Class<?> clazz) {
        return PlatformDependent0.getClassLoader(clazz);
    }

    public static ClassLoader getContextClassLoader() {
        return PlatformDependent0.getContextClassLoader();
    }

    public static ClassLoader getSystemClassLoader() {
        return PlatformDependent0.getSystemClassLoader();
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    public static <C> Deque<C> newConcurrentDeque() {
        if (PlatformDependent.javaVersion() >= 7) return new ConcurrentLinkedDeque<E>();
        return new LinkedBlockingDeque<E>();
    }

    public static Random threadLocalRandom() {
        return RANDOM_PROVIDER.current();
    }

    private static boolean isWindows0() {
        boolean windows = SystemPropertyUtil.get((String)"os.name", (String)"").toLowerCase((Locale)Locale.US).contains((CharSequence)"win");
        if (!windows) return windows;
        logger.debug((String)"Platform: Windows");
        return windows;
    }

    private static boolean isOsx0() {
        String osname = SystemPropertyUtil.get((String)"os.name", (String)"").toLowerCase((Locale)Locale.US).replaceAll((String)"[^a-z0-9]+", (String)"");
        boolean osx = osname.startsWith((String)"macosx") || osname.startsWith((String)"osx");
        if (!osx) return osx;
        logger.debug((String)"Platform: MacOS");
        return osx;
    }

    private static boolean maybeSuperUser0() {
        String username = SystemPropertyUtil.get((String)"user.name");
        if (PlatformDependent.isWindows()) {
            return "Administrator".equals((Object)username);
        }
        if ("root".equals((Object)username)) return true;
        if ("toor".equals((Object)username)) return true;
        return false;
    }

    private static Throwable unsafeUnavailabilityCause0() {
        if (PlatformDependent.isAndroid()) {
            logger.debug((String)"sun.misc.Unsafe: unavailable (Android)");
            return new UnsupportedOperationException((String)"sun.misc.Unsafe: unavailable (Android)");
        }
        if (PlatformDependent.isIkvmDotNet()) {
            logger.debug((String)"sun.misc.Unsafe: unavailable (IKVM.NET)");
            return new UnsupportedOperationException((String)"sun.misc.Unsafe: unavailable (IKVM.NET)");
        }
        Throwable cause = PlatformDependent0.getUnsafeUnavailabilityCause();
        if (cause != null) {
            return cause;
        }
        try {
            boolean hasUnsafe = PlatformDependent0.hasUnsafe();
            logger.debug((String)"sun.misc.Unsafe: {}", (Object)(hasUnsafe ? "available" : "unavailable"));
            if (hasUnsafe) {
                return null;
            }
            Throwable throwable = PlatformDependent0.getUnsafeUnavailabilityCause();
            return throwable;
        }
        catch (Throwable t) {
            logger.trace((String)"Could not determine if Unsafe is available", (Throwable)t);
            return new UnsupportedOperationException((String)"Could not determine if Unsafe is available", (Throwable)t);
        }
    }

    public static boolean isJ9Jvm() {
        return IS_J9_JVM;
    }

    private static boolean isJ9Jvm0() {
        String vmName = SystemPropertyUtil.get((String)"java.vm.name", (String)"").toLowerCase();
        if (vmName.startsWith((String)"ibm j9")) return true;
        if (vmName.startsWith((String)"eclipse openj9")) return true;
        return false;
    }

    public static boolean isIkvmDotNet() {
        return IS_IVKVM_DOT_NET;
    }

    private static boolean isIkvmDotNet0() {
        String vmName = SystemPropertyUtil.get((String)"java.vm.name", (String)"").toUpperCase((Locale)Locale.US);
        return vmName.equals((Object)"IKVM.NET");
    }

    private static long maxDirectMemory0() {
        long maxDirectMemory = 0L;
        ClassLoader systemClassLoader = null;
        try {
            systemClassLoader = PlatformDependent.getSystemClassLoader();
            String vmName = SystemPropertyUtil.get((String)"java.vm.name", (String)"").toLowerCase();
            if (!vmName.startsWith((String)"ibm j9") && !vmName.startsWith((String)"eclipse openj9")) {
                Class<?> vmClass = Class.forName((String)"sun.misc.VM", (boolean)true, (ClassLoader)systemClassLoader);
                Method m = vmClass.getDeclaredMethod((String)"maxDirectMemory", new Class[0]);
                maxDirectMemory = ((Number)m.invoke(null, (Object[])new Object[0])).longValue();
            }
        }
        catch (Throwable vmName) {
            // empty catch block
        }
        if (maxDirectMemory > 0L) {
            return maxDirectMemory;
        }
        try {
            Class<?> mgmtFactoryClass = Class.forName((String)"java.lang.management.ManagementFactory", (boolean)true, (ClassLoader)systemClassLoader);
            Class<?> runtimeClass = Class.forName((String)"java.lang.management.RuntimeMXBean", (boolean)true, (ClassLoader)systemClassLoader);
            Object runtime = mgmtFactoryClass.getDeclaredMethod((String)"getRuntimeMXBean", new Class[0]).invoke(null, (Object[])new Object[0]);
            List vmArgs = (List)runtimeClass.getDeclaredMethod((String)"getInputArguments", new Class[0]).invoke((Object)runtime, (Object[])new Object[0]);
            for (int i = vmArgs.size() - 1; i >= 0; --i) {
                Matcher m = MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher((CharSequence)((CharSequence)vmArgs.get((int)i)));
                if (!m.matches()) {
                    continue;
                }
                maxDirectMemory = Long.parseLong((String)m.group((int)1));
                switch (m.group((int)2).charAt((int)0)) {
                    case 'K': 
                    case 'k': {
                        maxDirectMemory *= 1024L;
                        break;
                    }
                    case 'M': 
                    case 'm': {
                        maxDirectMemory *= 0x100000L;
                        break;
                    }
                    case 'G': 
                    case 'g': {
                        maxDirectMemory *= 0x40000000L;
                    }
                }
                break;
            }
        }
        catch (Throwable mgmtFactoryClass) {
            // empty catch block
        }
        if (maxDirectMemory <= 0L) {
            maxDirectMemory = Runtime.getRuntime().maxMemory();
            logger.debug((String)"maxDirectMemory: {} bytes (maybe)", (Object)Long.valueOf((long)maxDirectMemory));
            return maxDirectMemory;
        }
        logger.debug((String)"maxDirectMemory: {} bytes", (Object)Long.valueOf((long)maxDirectMemory));
        return maxDirectMemory;
    }

    private static File tmpdir0() {
        File f;
        try {
            f = PlatformDependent.toDirectory((String)SystemPropertyUtil.get((String)"io.netty.tmpdir"));
            if (f != null) {
                logger.debug((String)"-Dio.netty.tmpdir: {}", (Object)f);
                return f;
            }
            f = PlatformDependent.toDirectory((String)SystemPropertyUtil.get((String)"java.io.tmpdir"));
            if (f != null) {
                logger.debug((String)"-Dio.netty.tmpdir: {} (java.io.tmpdir)", (Object)f);
                return f;
            }
            if (PlatformDependent.isWindows()) {
                f = PlatformDependent.toDirectory((String)System.getenv((String)"TEMP"));
                if (f != null) {
                    logger.debug((String)"-Dio.netty.tmpdir: {} (%TEMP%)", (Object)f);
                    return f;
                }
                String userprofile = System.getenv((String)"USERPROFILE");
                if (userprofile != null) {
                    f = PlatformDependent.toDirectory((String)(userprofile + "\\AppData\\Local\\Temp"));
                    if (f != null) {
                        logger.debug((String)"-Dio.netty.tmpdir: {} (%USERPROFILE%\\AppData\\Local\\Temp)", (Object)f);
                        return f;
                    }
                    f = PlatformDependent.toDirectory((String)(userprofile + "\\Local Settings\\Temp"));
                    if (f != null) {
                        logger.debug((String)"-Dio.netty.tmpdir: {} (%USERPROFILE%\\Local Settings\\Temp)", (Object)f);
                        return f;
                    }
                }
            } else {
                f = PlatformDependent.toDirectory((String)System.getenv((String)"TMPDIR"));
                if (f != null) {
                    logger.debug((String)"-Dio.netty.tmpdir: {} ($TMPDIR)", (Object)f);
                    return f;
                }
            }
        }
        catch (Throwable userprofile) {
            // empty catch block
        }
        f = PlatformDependent.isWindows() ? new File((String)"C:\\Windows\\Temp") : new File((String)"/tmp");
        logger.warn((String)"Failed to get the temporary directory; falling back to: {}", (Object)f);
        return f;
    }

    private static File toDirectory(String path) {
        if (path == null) {
            return null;
        }
        File f = new File((String)path);
        f.mkdirs();
        if (!f.isDirectory()) {
            return null;
        }
        try {
            return f.getAbsoluteFile();
        }
        catch (Exception ignored) {
            return f;
        }
    }

    private static int bitMode0() {
        int bitMode = SystemPropertyUtil.getInt((String)"io.netty.bitMode", (int)0);
        if (bitMode > 0) {
            logger.debug((String)"-Dio.netty.bitMode: {}", (Object)Integer.valueOf((int)bitMode));
            return bitMode;
        }
        bitMode = SystemPropertyUtil.getInt((String)"sun.arch.data.model", (int)0);
        if (bitMode > 0) {
            logger.debug((String)"-Dio.netty.bitMode: {} (sun.arch.data.model)", (Object)Integer.valueOf((int)bitMode));
            return bitMode;
        }
        bitMode = SystemPropertyUtil.getInt((String)"com.ibm.vm.bitmode", (int)0);
        if (bitMode > 0) {
            logger.debug((String)"-Dio.netty.bitMode: {} (com.ibm.vm.bitmode)", (Object)Integer.valueOf((int)bitMode));
            return bitMode;
        }
        String arch = SystemPropertyUtil.get((String)"os.arch", (String)"").toLowerCase((Locale)Locale.US).trim();
        if ("amd64".equals((Object)arch) || "x86_64".equals((Object)arch)) {
            bitMode = 64;
        } else if ("i386".equals((Object)arch) || "i486".equals((Object)arch) || "i586".equals((Object)arch) || "i686".equals((Object)arch)) {
            bitMode = 32;
        }
        if (bitMode > 0) {
            logger.debug((String)"-Dio.netty.bitMode: {} (os.arch: {})", (Object)Integer.valueOf((int)bitMode), (Object)arch);
        }
        String vm = SystemPropertyUtil.get((String)"java.vm.name", (String)"").toLowerCase((Locale)Locale.US);
        Pattern bitPattern = Pattern.compile((String)"([1-9][0-9]+)-?bit");
        Matcher m = bitPattern.matcher((CharSequence)vm);
        if (!m.find()) return 64;
        return Integer.parseInt((String)m.group((int)1));
    }

    private static int addressSize0() {
        if (PlatformDependent.hasUnsafe()) return PlatformDependent0.addressSize();
        return -1;
    }

    private static long byteArrayBaseOffset0() {
        if (PlatformDependent.hasUnsafe()) return PlatformDependent0.byteArrayBaseOffset();
        return -1L;
    }

    private static boolean equalsSafe(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        int end = startPos1 + length;
        while (startPos1 < end) {
            if (bytes1[startPos1] != bytes2[startPos2]) {
                return false;
            }
            ++startPos1;
            ++startPos2;
        }
        return true;
    }

    private static boolean isZeroSafe(byte[] bytes, int startPos, int length) {
        int end = startPos + length;
        while (startPos < end) {
            if (bytes[startPos] != 0) {
                return false;
            }
            ++startPos;
        }
        return true;
    }

    static int hashCodeAsciiSafe(byte[] bytes, int startPos, int length) {
        int hash = -1028477387;
        int remainingBytes = length & 7;
        int end = startPos + remainingBytes;
        for (int i = startPos - 8 + length; i >= end; i -= 8) {
            hash = PlatformDependent0.hashCodeAsciiCompute((long)PlatformDependent.getLongSafe((byte[])bytes, (int)i), (int)hash);
        }
        switch (remainingBytes) {
            case 7: {
                return ((hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((byte)bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize((short)PlatformDependent.getShortSafe((byte[])bytes, (int)(startPos + 1)))) * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((int)PlatformDependent.getIntSafe((byte[])bytes, (int)(startPos + 3)));
            }
            case 6: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((short)PlatformDependent.getShortSafe((byte[])bytes, (int)startPos))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize((int)PlatformDependent.getIntSafe((byte[])bytes, (int)(startPos + 2)));
            }
            case 5: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((byte)bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize((int)PlatformDependent.getIntSafe((byte[])bytes, (int)(startPos + 1)));
            }
            case 4: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((int)PlatformDependent.getIntSafe((byte[])bytes, (int)startPos));
            }
            case 3: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((byte)bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize((short)PlatformDependent.getShortSafe((byte[])bytes, (int)(startPos + 1)));
            }
            case 2: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((short)PlatformDependent.getShortSafe((byte[])bytes, (int)startPos));
            }
            case 1: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((byte)bytes[startPos]);
            }
        }
        return hash;
    }

    public static String normalizedArch() {
        return NORMALIZED_ARCH;
    }

    public static String normalizedOs() {
        return NORMALIZED_OS;
    }

    public static Set<String> normalizedLinuxClassifiers() {
        return LINUX_OS_CLASSIFIERS;
    }

    private static void addClassifier(Set<String> allowed, Set<String> dest, String ... maybeClassifiers) {
        String[] arrstring = maybeClassifiers;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String id = arrstring[n2];
            if (allowed.contains((Object)id)) {
                dest.add((String)id);
            }
            ++n2;
        }
    }

    private static String normalizeOsReleaseVariableValue(String value) {
        return value.trim().replaceAll((String)"[\"']", (String)"");
    }

    private static String normalize(String value) {
        return value.toLowerCase((Locale)Locale.US).replaceAll((String)"[^a-z0-9]+", (String)"");
    }

    private static String normalizeArch(String value) {
        if ((value = PlatformDependent.normalize((String)value)).matches((String)"^(x8664|amd64|ia32e|em64t|x64)$")) {
            return "x86_64";
        }
        if (value.matches((String)"^(x8632|x86|i[3-6]86|ia32|x32)$")) {
            return "x86_32";
        }
        if (value.matches((String)"^(ia64|itanium64)$")) {
            return "itanium_64";
        }
        if (value.matches((String)"^(sparc|sparc32)$")) {
            return "sparc_32";
        }
        if (value.matches((String)"^(sparcv9|sparc64)$")) {
            return "sparc_64";
        }
        if (value.matches((String)"^(arm|arm32)$")) {
            return "arm_32";
        }
        if ("aarch64".equals((Object)value)) {
            return "aarch_64";
        }
        if (value.matches((String)"^(ppc|ppc32)$")) {
            return "ppc_32";
        }
        if ("ppc64".equals((Object)value)) {
            return "ppc_64";
        }
        if ("ppc64le".equals((Object)value)) {
            return "ppcle_64";
        }
        if ("s390".equals((Object)value)) {
            return "s390_32";
        }
        if (!"s390x".equals((Object)value)) return "unknown";
        return "s390_64";
    }

    private static String normalizeOs(String value) {
        if ((value = PlatformDependent.normalize((String)value)).startsWith((String)"aix")) {
            return "aix";
        }
        if (value.startsWith((String)"hpux")) {
            return "hpux";
        }
        if (value.startsWith((String)"os400")) {
            if (value.length() <= 5) return "os400";
            if (!Character.isDigit((char)value.charAt((int)5))) {
                return "os400";
            }
        }
        if (value.startsWith((String)"linux")) {
            return "linux";
        }
        if (value.startsWith((String)"macosx")) return "osx";
        if (value.startsWith((String)"osx")) {
            return "osx";
        }
        if (value.startsWith((String)"freebsd")) {
            return "freebsd";
        }
        if (value.startsWith((String)"openbsd")) {
            return "openbsd";
        }
        if (value.startsWith((String)"netbsd")) {
            return "netbsd";
        }
        if (value.startsWith((String)"solaris")) return "sunos";
        if (value.startsWith((String)"sunos")) {
            return "sunos";
        }
        if (!value.startsWith((String)"windows")) return "unknown";
        return "windows";
    }

    private PlatformDependent() {
    }

    static /* synthetic */ InternalLogger access$100() {
        return logger;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled unnecessary exception pruning
     */
    static {
        CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !PlatformDependent.isAndroid();
        UNSAFE_UNAVAILABILITY_CAUSE = PlatformDependent.unsafeUnavailabilityCause0();
        MAX_DIRECT_MEMORY = PlatformDependent.maxDirectMemory0();
        BYTE_ARRAY_BASE_OFFSET = PlatformDependent.byteArrayBaseOffset0();
        TMPDIR = PlatformDependent.tmpdir0();
        BIT_MODE = PlatformDependent.bitMode0();
        NORMALIZED_ARCH = PlatformDependent.normalizeArch((String)SystemPropertyUtil.get((String)"os.arch", (String)""));
        NORMALIZED_OS = PlatformDependent.normalizeOs((String)SystemPropertyUtil.get((String)"os.name", (String)""));
        ALLOWED_LINUX_OS_CLASSIFIERS = new String[]{"fedora", "suse", "arch"};
        ADDRESS_SIZE = PlatformDependent.addressSize0();
        BIG_ENDIAN_NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
        NOOP = new Cleaner(){

            public void freeDirectBuffer(ByteBuffer buffer) {
            }
        };
        RANDOM_PROVIDER = PlatformDependent.javaVersion() >= 7 ? new ThreadLocalRandomProvider(){

            @SuppressJava6Requirement(reason="Usage guarded by java version check")
            public Random current() {
                return java.util.concurrent.ThreadLocalRandom.current();
            }
        } : new ThreadLocalRandomProvider(){

            public Random current() {
                return io.netty.util.internal.ThreadLocalRandom.current();
            }
        };
        long maxDirectMemory = SystemPropertyUtil.getLong((String)"io.netty.maxDirectMemory", (long)-1L);
        if (maxDirectMemory == 0L || !PlatformDependent.hasUnsafe() || !PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
            USE_DIRECT_BUFFER_NO_CLEANER = false;
            DIRECT_MEMORY_COUNTER = null;
        } else {
            USE_DIRECT_BUFFER_NO_CLEANER = true;
            DIRECT_MEMORY_COUNTER = maxDirectMemory < 0L ? ((maxDirectMemory = MAX_DIRECT_MEMORY) <= 0L ? null : new AtomicLong()) : new AtomicLong();
        }
        logger.debug((String)"-Dio.netty.maxDirectMemory: {} bytes", (Object)Long.valueOf((long)maxDirectMemory));
        DIRECT_MEMORY_LIMIT = maxDirectMemory >= 1L ? maxDirectMemory : MAX_DIRECT_MEMORY;
        int tryAllocateUninitializedArray = SystemPropertyUtil.getInt((String)"io.netty.uninitializedArrayAllocationThreshold", (int)1024);
        UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD = PlatformDependent.javaVersion() >= 9 && PlatformDependent0.hasAllocateArrayMethod() ? tryAllocateUninitializedArray : -1;
        logger.debug((String)"-Dio.netty.uninitializedArrayAllocationThreshold: {}", (Object)Integer.valueOf((int)UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD));
        MAYBE_SUPER_USER = PlatformDependent.maybeSuperUser0();
        CLEANER = !PlatformDependent.isAndroid() ? (PlatformDependent.javaVersion() >= 9 ? (CleanerJava9.isSupported() ? new CleanerJava9() : NOOP) : (CleanerJava6.isSupported() ? new CleanerJava6() : NOOP)) : NOOP;
        boolean bl = DIRECT_BUFFER_PREFERRED = CLEANER != NOOP && !SystemPropertyUtil.getBoolean((String)"io.netty.noPreferDirect", (boolean)false);
        if (logger.isDebugEnabled()) {
            logger.debug((String)"-Dio.netty.noPreferDirect: {}", (Object)Boolean.valueOf((boolean)(!DIRECT_BUFFER_PREFERRED)));
        }
        if (CLEANER == NOOP && !PlatformDependent0.isExplicitNoUnsafe()) {
            logger.info((String)"Your platform does not provide complete low-level API for accessing direct buffers reliably. Unless explicitly requested, heap buffer will always be preferred to avoid potential system instability.");
        }
        String[] OS_RELEASE_FILES = new String[]{"/etc/os-release", "/usr/lib/os-release"};
        String LINUX_ID_PREFIX = "ID=";
        String LINUX_ID_LIKE_PREFIX = "ID_LIKE=";
        Set<String> allowedClassifiers = new HashSet<String>(Arrays.asList(ALLOWED_LINUX_OS_CLASSIFIERS));
        allowedClassifiers = Collections.unmodifiableSet(allowedClassifiers);
        LinkedHashSet<String> availableClassifiers = new LinkedHashSet<String>();
        for (String osReleaseFileName : OS_RELEASE_FILES) {
            String line22323232322;
            File file = new File((String)osReleaseFileName);
            if (!file.exists()) continue;
            BufferedReader reader = null;
            reader = new BufferedReader((Reader)new InputStreamReader((InputStream)new FileInputStream((File)file), (Charset)CharsetUtil.UTF_8));
            while ((line22323232322 = reader.readLine()) != null) {
                if (line22323232322.startsWith((String)"ID=")) {
                    String id = PlatformDependent.normalizeOsReleaseVariableValue((String)line22323232322.substring((int)"ID=".length()));
                    PlatformDependent.addClassifier(allowedClassifiers, availableClassifiers, (String[])new String[]{id});
                    continue;
                }
                if (!line22323232322.startsWith((String)"ID_LIKE=")) continue;
                line22323232322 = PlatformDependent.normalizeOsReleaseVariableValue((String)line22323232322.substring((int)"ID_LIKE=".length()));
                PlatformDependent.addClassifier(allowedClassifiers, availableClassifiers, (String[])line22323232322.split((String)"[ ]+"));
            }
            if (reader == null) break;
            try {
                reader.close();
            }
            catch (IOException line22323232322) {}
            break;
            catch (IOException line22323232322) {
                if (reader == null) break;
                try {
                    reader.close();
                }
                catch (IOException line22323232322) {}
                break;
                catch (Throwable throwable) {
                    if (reader == null) throw throwable;
                    try {
                        reader.close();
                        throw throwable;
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    throw throwable;
                }
            }
        }
        LINUX_OS_CLASSIFIERS = Collections.unmodifiableSet(availableClassifiers);
    }
}

