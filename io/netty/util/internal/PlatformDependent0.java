/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.ConstantTimeUtils;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

@SuppressJava6Requirement(reason="Unsafe access is guarded")
final class PlatformDependent0 {
    private static final InternalLogger logger;
    private static final long ADDRESS_FIELD_OFFSET;
    private static final long BYTE_ARRAY_BASE_OFFSET;
    private static final Constructor<?> DIRECT_BUFFER_CONSTRUCTOR;
    private static final Throwable EXPLICIT_NO_UNSAFE_CAUSE;
    private static final Method ALLOCATE_ARRAY_METHOD;
    private static final int JAVA_VERSION;
    private static final boolean IS_ANDROID;
    private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
    private static final Object INTERNAL_UNSAFE;
    private static final boolean IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE;
    static final Unsafe UNSAFE;
    static final int HASH_CODE_ASCII_SEED = -1028477387;
    static final int HASH_CODE_C1 = -862048943;
    static final int HASH_CODE_C2 = 461845907;
    private static final long UNSAFE_COPY_THRESHOLD = 0x100000L;
    private static final boolean UNALIGNED;

    static boolean isExplicitNoUnsafe() {
        if (EXPLICIT_NO_UNSAFE_CAUSE == null) return false;
        return true;
    }

    private static Throwable explicitNoUnsafeCause0() {
        boolean noUnsafe = SystemPropertyUtil.getBoolean((String)"io.netty.noUnsafe", (boolean)false);
        logger.debug((String)"-Dio.netty.noUnsafe: {}", (Object)Boolean.valueOf((boolean)noUnsafe));
        if (noUnsafe) {
            logger.debug((String)"sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
            return new UnsupportedOperationException((String)"sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
        }
        String unsafePropName = SystemPropertyUtil.contains((String)"io.netty.tryUnsafe") ? "io.netty.tryUnsafe" : "org.jboss.netty.tryUnsafe";
        if (SystemPropertyUtil.getBoolean((String)unsafePropName, (boolean)true)) return null;
        String msg = "sun.misc.Unsafe: unavailable (" + unsafePropName + ")";
        logger.debug((String)msg);
        return new UnsupportedOperationException((String)msg);
    }

    static boolean isUnaligned() {
        return UNALIGNED;
    }

    static boolean hasUnsafe() {
        if (UNSAFE == null) return false;
        return true;
    }

    static Throwable getUnsafeUnavailabilityCause() {
        return UNSAFE_UNAVAILABILITY_CAUSE;
    }

    static boolean unalignedAccess() {
        return UNALIGNED;
    }

    static void throwException(Throwable cause) {
        UNSAFE.throwException((Throwable)ObjectUtil.checkNotNull(cause, (String)"cause"));
    }

    static boolean hasDirectBufferNoCleanerConstructor() {
        if (DIRECT_BUFFER_CONSTRUCTOR == null) return false;
        return true;
    }

    static ByteBuffer reallocateDirectNoCleaner(ByteBuffer buffer, int capacity) {
        return PlatformDependent0.newDirectBuffer((long)UNSAFE.reallocateMemory((long)PlatformDependent0.directBufferAddress((ByteBuffer)buffer), (long)((long)capacity)), (int)capacity);
    }

    static ByteBuffer allocateDirectNoCleaner(int capacity) {
        return PlatformDependent0.newDirectBuffer((long)UNSAFE.allocateMemory((long)((long)Math.max((int)1, (int)capacity))), (int)capacity);
    }

    static boolean hasAllocateArrayMethod() {
        if (ALLOCATE_ARRAY_METHOD == null) return false;
        return true;
    }

    static byte[] allocateUninitializedArray(int size) {
        try {
            return (byte[])ALLOCATE_ARRAY_METHOD.invoke((Object)INTERNAL_UNSAFE, (Object[])new Object[]{Byte.TYPE, Integer.valueOf((int)size)});
        }
        catch (IllegalAccessException e) {
            throw new Error((Throwable)e);
        }
        catch (InvocationTargetException e) {
            throw new Error((Throwable)e);
        }
    }

    static ByteBuffer newDirectBuffer(long address, int capacity) {
        ObjectUtil.checkPositiveOrZero((int)capacity, (String)"capacity");
        try {
            return (ByteBuffer)DIRECT_BUFFER_CONSTRUCTOR.newInstance((Object[])new Object[]{Long.valueOf((long)address), Integer.valueOf((int)capacity)});
        }
        catch (Throwable cause) {
            if (!(cause instanceof Error)) throw new Error((Throwable)cause);
            throw (Error)cause;
        }
    }

    static long directBufferAddress(ByteBuffer buffer) {
        return PlatformDependent0.getLong((Object)buffer, (long)ADDRESS_FIELD_OFFSET);
    }

    static long byteArrayBaseOffset() {
        return BYTE_ARRAY_BASE_OFFSET;
    }

    static Object getObject(Object object, long fieldOffset) {
        return UNSAFE.getObject((Object)object, (long)fieldOffset);
    }

    static int getInt(Object object, long fieldOffset) {
        return UNSAFE.getInt((Object)object, (long)fieldOffset);
    }

    private static long getLong(Object object, long fieldOffset) {
        return UNSAFE.getLong((Object)object, (long)fieldOffset);
    }

    static long objectFieldOffset(Field field) {
        return UNSAFE.objectFieldOffset((Field)field);
    }

    static byte getByte(long address) {
        return UNSAFE.getByte((long)address);
    }

    static short getShort(long address) {
        return UNSAFE.getShort((long)address);
    }

    static int getInt(long address) {
        return UNSAFE.getInt((long)address);
    }

    static long getLong(long address) {
        return UNSAFE.getLong((long)address);
    }

    static byte getByte(byte[] data, int index) {
        return UNSAFE.getByte((Object)data, (long)(BYTE_ARRAY_BASE_OFFSET + (long)index));
    }

    static short getShort(byte[] data, int index) {
        return UNSAFE.getShort((Object)data, (long)(BYTE_ARRAY_BASE_OFFSET + (long)index));
    }

    static int getInt(byte[] data, int index) {
        return UNSAFE.getInt((Object)data, (long)(BYTE_ARRAY_BASE_OFFSET + (long)index));
    }

    static long getLong(byte[] data, int index) {
        return UNSAFE.getLong((Object)data, (long)(BYTE_ARRAY_BASE_OFFSET + (long)index));
    }

    static void putByte(long address, byte value) {
        UNSAFE.putByte((long)address, (byte)value);
    }

    static void putShort(long address, short value) {
        UNSAFE.putShort((long)address, (short)value);
    }

    static void putInt(long address, int value) {
        UNSAFE.putInt((long)address, (int)value);
    }

    static void putLong(long address, long value) {
        UNSAFE.putLong((long)address, (long)value);
    }

    static void putByte(byte[] data, int index, byte value) {
        UNSAFE.putByte((Object)data, (long)(BYTE_ARRAY_BASE_OFFSET + (long)index), (byte)value);
    }

    static void putShort(byte[] data, int index, short value) {
        UNSAFE.putShort((Object)data, (long)(BYTE_ARRAY_BASE_OFFSET + (long)index), (short)value);
    }

    static void putInt(byte[] data, int index, int value) {
        UNSAFE.putInt((Object)data, (long)(BYTE_ARRAY_BASE_OFFSET + (long)index), (int)value);
    }

    static void putLong(byte[] data, int index, long value) {
        UNSAFE.putLong((Object)data, (long)(BYTE_ARRAY_BASE_OFFSET + (long)index), (long)value);
    }

    static void putObject(Object o, long offset, Object x) {
        UNSAFE.putObject((Object)o, (long)offset, (Object)x);
    }

    static void copyMemory(long srcAddr, long dstAddr, long length) {
        if (PlatformDependent0.javaVersion() <= 8) {
            PlatformDependent0.copyMemoryWithSafePointPolling((long)srcAddr, (long)dstAddr, (long)length);
            return;
        }
        UNSAFE.copyMemory((long)srcAddr, (long)dstAddr, (long)length);
    }

    private static void copyMemoryWithSafePointPolling(long srcAddr, long dstAddr, long length) {
        while (length > 0L) {
            long size = Math.min((long)length, (long)0x100000L);
            UNSAFE.copyMemory((long)srcAddr, (long)dstAddr, (long)size);
            length -= size;
            srcAddr += size;
            dstAddr += size;
        }
    }

    static void copyMemory(Object src, long srcOffset, Object dst, long dstOffset, long length) {
        if (PlatformDependent0.javaVersion() <= 8) {
            PlatformDependent0.copyMemoryWithSafePointPolling((Object)src, (long)srcOffset, (Object)dst, (long)dstOffset, (long)length);
            return;
        }
        UNSAFE.copyMemory((Object)src, (long)srcOffset, (Object)dst, (long)dstOffset, (long)length);
    }

    private static void copyMemoryWithSafePointPolling(Object src, long srcOffset, Object dst, long dstOffset, long length) {
        while (length > 0L) {
            long size = Math.min((long)length, (long)0x100000L);
            UNSAFE.copyMemory((Object)src, (long)srcOffset, (Object)dst, (long)dstOffset, (long)size);
            length -= size;
            srcOffset += size;
            dstOffset += size;
        }
    }

    static void setMemory(long address, long bytes, byte value) {
        UNSAFE.setMemory((long)address, (long)bytes, (byte)value);
    }

    static void setMemory(Object o, long offset, long bytes, byte value) {
        UNSAFE.setMemory((Object)o, (long)offset, (long)bytes, (byte)value);
    }

    static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        long pos;
        int remainingBytes = length & 7;
        long baseOffset1 = BYTE_ARRAY_BASE_OFFSET + (long)startPos1;
        long diff = (long)(startPos2 - startPos1);
        if (length >= 8) {
            long end = baseOffset1 + (long)remainingBytes;
            for (long i = baseOffset1 - 8L + (long)length; i >= end; i -= 8L) {
                if (UNSAFE.getLong((Object)bytes1, (long)i) == UNSAFE.getLong((Object)bytes2, (long)(i + diff))) continue;
                return false;
            }
        }
        if (remainingBytes >= 4 && UNSAFE.getInt((Object)bytes1, (long)(pos = baseOffset1 + (long)(remainingBytes -= 4))) != UNSAFE.getInt((Object)bytes2, (long)(pos + diff))) {
            return false;
        }
        long baseOffset2 = baseOffset1 + diff;
        if (remainingBytes >= 2) {
            if (UNSAFE.getChar((Object)bytes1, (long)baseOffset1) != UNSAFE.getChar((Object)bytes2, (long)baseOffset2)) return false;
            if (remainingBytes == 2) return true;
            if (UNSAFE.getByte((Object)bytes1, (long)(baseOffset1 + 2L)) != UNSAFE.getByte((Object)bytes2, (long)(baseOffset2 + 2L))) return false;
            return true;
        }
        if (remainingBytes == 0) return true;
        if (UNSAFE.getByte((Object)bytes1, (long)baseOffset1) == UNSAFE.getByte((Object)bytes2, (long)baseOffset2)) return true;
        return false;
    }

    static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        long pos;
        long result = 0L;
        long remainingBytes = (long)(length & 7);
        long baseOffset1 = BYTE_ARRAY_BASE_OFFSET + (long)startPos1;
        long end = baseOffset1 + remainingBytes;
        long diff = (long)(startPos2 - startPos1);
        for (long i = baseOffset1 - 8L + (long)length; i >= end; result |= PlatformDependent0.UNSAFE.getLong((Object)bytes1, (long)i) ^ PlatformDependent0.UNSAFE.getLong((Object)bytes2, (long)(i + diff)), i -= 8L) {
        }
        if (remainingBytes >= 4L) {
            result |= (long)(UNSAFE.getInt((Object)bytes1, (long)baseOffset1) ^ UNSAFE.getInt((Object)bytes2, (long)(baseOffset1 + diff)));
            remainingBytes -= 4L;
        }
        if (remainingBytes >= 2L) {
            pos = end - remainingBytes;
            result |= (long)(UNSAFE.getChar((Object)bytes1, (long)pos) ^ UNSAFE.getChar((Object)bytes2, (long)(pos + diff)));
            remainingBytes -= 2L;
        }
        if (remainingBytes != 1L) return ConstantTimeUtils.equalsConstantTime((long)result, (long)0L);
        pos = end - 1L;
        result |= (long)(UNSAFE.getByte((Object)bytes1, (long)pos) ^ UNSAFE.getByte((Object)bytes2, (long)(pos + diff)));
        return ConstantTimeUtils.equalsConstantTime((long)result, (long)0L);
    }

    static boolean isZero(byte[] bytes, int startPos, int length) {
        if (length <= 0) {
            return true;
        }
        long baseOffset = BYTE_ARRAY_BASE_OFFSET + (long)startPos;
        int remainingBytes = length & 7;
        long end = baseOffset + (long)remainingBytes;
        for (long i = baseOffset - 8L + (long)length; i >= end; i -= 8L) {
            if (UNSAFE.getLong((Object)bytes, (long)i) == 0L) continue;
            return false;
        }
        if (remainingBytes >= 4 && UNSAFE.getInt((Object)bytes, (long)(baseOffset + (long)(remainingBytes -= 4))) != 0) {
            return false;
        }
        if (remainingBytes >= 2) {
            if (UNSAFE.getChar((Object)bytes, (long)baseOffset) != '\u0000') return false;
            if (remainingBytes == 2) return true;
            if (bytes[startPos + 2] != 0) return false;
            return true;
        }
        if (bytes[startPos] != 0) return false;
        return true;
    }

    static int hashCodeAscii(byte[] bytes, int startPos, int length) {
        int hash = -1028477387;
        long baseOffset = BYTE_ARRAY_BASE_OFFSET + (long)startPos;
        int remainingBytes = length & 7;
        long end = baseOffset + (long)remainingBytes;
        for (long i = baseOffset - 8L + (long)length; i >= end; i -= 8L) {
            hash = PlatformDependent0.hashCodeAsciiCompute((long)UNSAFE.getLong((Object)bytes, (long)i), (int)hash);
        }
        if (remainingBytes == 0) {
            return hash;
        }
        int hcConst = -862048943;
        if (remainingBytes != 2 & remainingBytes != 4 & remainingBytes != 6) {
            hash = hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((byte)UNSAFE.getByte((Object)bytes, (long)baseOffset));
            hcConst = 461845907;
            ++baseOffset;
        }
        if (remainingBytes != 1 & remainingBytes != 4 & remainingBytes != 5) {
            hash = hash * hcConst + PlatformDependent0.hashCodeAsciiSanitize((short)UNSAFE.getShort((Object)bytes, (long)baseOffset));
            hcConst = hcConst == -862048943 ? 461845907 : -862048943;
            baseOffset += 2L;
        }
        if (remainingBytes < 4) return hash;
        return hash * hcConst + PlatformDependent0.hashCodeAsciiSanitize((int)UNSAFE.getInt((Object)bytes, (long)baseOffset));
    }

    static int hashCodeAsciiCompute(long value, int hash) {
        return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((int)((int)value)) * 461845907 + (int)((value & 2242545357458243584L) >>> 32);
    }

    static int hashCodeAsciiSanitize(int value) {
        return value & 522133279;
    }

    static int hashCodeAsciiSanitize(short value) {
        return value & 7967;
    }

    static int hashCodeAsciiSanitize(byte value) {
        return value & 31;
    }

    static ClassLoader getClassLoader(Class<?> clazz) {
        if (System.getSecurityManager() != null) return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(clazz){
            final /* synthetic */ Class val$clazz;
            {
                this.val$clazz = class_;
            }

            public ClassLoader run() {
                return this.val$clazz.getClassLoader();
            }
        });
        return clazz.getClassLoader();
    }

    static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() != null) return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
        return Thread.currentThread().getContextClassLoader();
    }

    static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() != null) return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            public ClassLoader run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
        return ClassLoader.getSystemClassLoader();
    }

    static int addressSize() {
        return UNSAFE.addressSize();
    }

    static long allocateMemory(long size) {
        return UNSAFE.allocateMemory((long)size);
    }

    static void freeMemory(long address) {
        UNSAFE.freeMemory((long)address);
    }

    static long reallocateMemory(long address, long newSize) {
        return UNSAFE.reallocateMemory((long)address, (long)newSize);
    }

    static boolean isAndroid() {
        return IS_ANDROID;
    }

    private static boolean isAndroid0() {
        String vmName = SystemPropertyUtil.get((String)"java.vm.name");
        boolean isAndroid = "Dalvik".equals((Object)vmName);
        if (!isAndroid) return isAndroid;
        logger.debug((String)"Platform: Android");
        return isAndroid;
    }

    private static boolean explicitTryReflectionSetAccessible0() {
        boolean bl;
        if (PlatformDependent0.javaVersion() < 9) {
            bl = true;
            return SystemPropertyUtil.getBoolean((String)"io.netty.tryReflectionSetAccessible", (boolean)bl);
        }
        bl = false;
        return SystemPropertyUtil.getBoolean((String)"io.netty.tryReflectionSetAccessible", (boolean)bl);
    }

    static boolean isExplicitTryReflectionSetAccessible() {
        return IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE;
    }

    static int javaVersion() {
        return JAVA_VERSION;
    }

    private static int javaVersion0() {
        int majorVersion = PlatformDependent0.isAndroid0() ? 6 : PlatformDependent0.majorVersionFromJavaSpecificationVersion();
        logger.debug((String)"Java version: {}", (Object)Integer.valueOf((int)majorVersion));
        return majorVersion;
    }

    static int majorVersionFromJavaSpecificationVersion() {
        return PlatformDependent0.majorVersion((String)SystemPropertyUtil.get((String)"java.specification.version", (String)"1.6"));
    }

    static int majorVersion(String javaSpecVersion) {
        String[] components = javaSpecVersion.split((String)"\\.");
        int[] version = new int[components.length];
        int i = 0;
        do {
            if (i >= components.length) {
                if (version[0] != 1) return version[0];
                if ($assertionsDisabled) return version[1];
                if (version[1] >= 6) return version[1];
                throw new AssertionError();
            }
            version[i] = Integer.parseInt((String)components[i]);
            ++i;
        } while (true);
    }

    private PlatformDependent0() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        ByteBuffer direct;
        Unsafe unsafe;
        logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
        EXPLICIT_NO_UNSAFE_CAUSE = PlatformDependent0.explicitNoUnsafeCause0();
        JAVA_VERSION = PlatformDependent0.javaVersion0();
        IS_ANDROID = PlatformDependent0.isAndroid0();
        IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE = PlatformDependent0.explicitTryReflectionSetAccessible0();
        Field addressField = null;
        Method allocateArrayMethod = null;
        Throwable unsafeUnavailabilityCause = null;
        Object internalUnsafe = null;
        unsafeUnavailabilityCause = EXPLICIT_NO_UNSAFE_CAUSE;
        if (unsafeUnavailabilityCause != null) {
            direct = null;
            addressField = null;
            unsafe = null;
            internalUnsafe = null;
        } else {
            long byteArrayIndexScale;
            Unsafe finalUnsafe;
            direct = ByteBuffer.allocateDirect((int)1);
            Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                public Object run() {
                    try {
                        Field unsafeField = Unsafe.class.getDeclaredField((String)"theUnsafe");
                        Throwable cause = io.netty.util.internal.ReflectionUtil.trySetAccessible((java.lang.reflect.AccessibleObject)unsafeField, (boolean)false);
                        if (cause == null) return unsafeField.get(null);
                        return cause;
                    }
                    catch (java.lang.NoSuchFieldException e) {
                        return e;
                    }
                    catch (java.lang.SecurityException e) {
                        return e;
                    }
                    catch (IllegalAccessException e) {
                        return e;
                    }
                    catch (java.lang.NoClassDefFoundError e) {
                        return e;
                    }
                }
            });
            if (maybeUnsafe instanceof Throwable) {
                unsafe = null;
                unsafeUnavailabilityCause = (Throwable)maybeUnsafe;
                logger.debug((String)"sun.misc.Unsafe.theUnsafe: unavailable", (Throwable)((Throwable)maybeUnsafe));
            } else {
                unsafe = (Unsafe)maybeUnsafe;
                logger.debug((String)"sun.misc.Unsafe.theUnsafe: available");
            }
            if (unsafe != null) {
                finalUnsafe = unsafe;
                Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>((Unsafe)finalUnsafe){
                    final /* synthetic */ Unsafe val$finalUnsafe;
                    {
                        this.val$finalUnsafe = unsafe;
                    }

                    public Object run() {
                        try {
                            this.val$finalUnsafe.getClass().getDeclaredMethod((String)"copyMemory", Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE);
                            return null;
                        }
                        catch (java.lang.NoSuchMethodException e) {
                            return e;
                        }
                        catch (java.lang.SecurityException e) {
                            return e;
                        }
                    }
                });
                if (maybeException == null) {
                    logger.debug((String)"sun.misc.Unsafe.copyMemory: available");
                } else {
                    unsafe = null;
                    unsafeUnavailabilityCause = (Throwable)maybeException;
                    logger.debug((String)"sun.misc.Unsafe.copyMemory: unavailable", (Throwable)((Throwable)maybeException));
                }
            }
            if (unsafe != null) {
                finalUnsafe = unsafe;
                Object maybeAddressField = AccessController.doPrivileged(new PrivilegedAction<Object>((Unsafe)finalUnsafe, (ByteBuffer)direct){
                    final /* synthetic */ Unsafe val$finalUnsafe;
                    final /* synthetic */ ByteBuffer val$direct;
                    {
                        this.val$finalUnsafe = unsafe;
                        this.val$direct = byteBuffer;
                    }

                    public Object run() {
                        try {
                            Field field = java.nio.Buffer.class.getDeclaredField((String)"address");
                            long offset = this.val$finalUnsafe.objectFieldOffset((Field)field);
                            long address = this.val$finalUnsafe.getLong((Object)this.val$direct, (long)offset);
                            if (address != 0L) return field;
                            return null;
                        }
                        catch (java.lang.NoSuchFieldException e) {
                            return e;
                        }
                        catch (java.lang.SecurityException e) {
                            return e;
                        }
                    }
                });
                if (maybeAddressField instanceof Field) {
                    addressField = (Field)maybeAddressField;
                    logger.debug((String)"java.nio.Buffer.address: available");
                } else {
                    unsafeUnavailabilityCause = (Throwable)maybeAddressField;
                    logger.debug((String)"java.nio.Buffer.address: unavailable", (Throwable)((Throwable)maybeAddressField));
                    unsafe = null;
                }
            }
            if (unsafe != null && (byteArrayIndexScale = (long)unsafe.arrayIndexScale(byte[].class)) != 1L) {
                logger.debug((String)"unsafe.arrayIndexScale is {} (expected: 1). Not using unsafe.", (Object)Long.valueOf((long)byteArrayIndexScale));
                unsafeUnavailabilityCause = new UnsupportedOperationException((String)"Unexpected unsafe.arrayIndexScale");
                unsafe = null;
            }
        }
        UNSAFE_UNAVAILABILITY_CAUSE = unsafeUnavailabilityCause;
        UNSAFE = unsafe;
        if (unsafe == null) {
            ADDRESS_FIELD_OFFSET = -1L;
            BYTE_ARRAY_BASE_OFFSET = -1L;
            UNALIGNED = false;
            DIRECT_BUFFER_CONSTRUCTOR = null;
            ALLOCATE_ARRAY_METHOD = null;
        } else {
            boolean unaligned;
            Constructor directBufferConstructor;
            long address = -1L;
            try {
                Object maybeDirectBufferConstructor = AccessController.doPrivileged(new PrivilegedAction<Object>((ByteBuffer)direct){
                    final /* synthetic */ ByteBuffer val$direct;
                    {
                        this.val$direct = byteBuffer;
                    }

                    public Object run() {
                        try {
                            Constructor<?> constructor = this.val$direct.getClass().getDeclaredConstructor(Long.TYPE, Integer.TYPE);
                            Throwable cause = io.netty.util.internal.ReflectionUtil.trySetAccessible(constructor, (boolean)true);
                            if (cause == null) return constructor;
                            return cause;
                        }
                        catch (java.lang.NoSuchMethodException e) {
                            return e;
                        }
                        catch (java.lang.SecurityException e) {
                            return e;
                        }
                    }
                });
                if (maybeDirectBufferConstructor instanceof Constructor) {
                    address = UNSAFE.allocateMemory((long)1L);
                    try {
                        ((Constructor)maybeDirectBufferConstructor).newInstance((Object[])new Object[]{Long.valueOf((long)address), Integer.valueOf((int)1)});
                        directBufferConstructor = (Constructor)maybeDirectBufferConstructor;
                        logger.debug((String)"direct buffer constructor: available");
                    }
                    catch (InstantiationException e) {
                        directBufferConstructor = null;
                    }
                    catch (IllegalAccessException e) {
                        directBufferConstructor = null;
                    }
                    catch (InvocationTargetException e) {
                        directBufferConstructor = null;
                    }
                } else {
                    logger.debug((String)"direct buffer constructor: unavailable", (Throwable)((Throwable)maybeDirectBufferConstructor));
                    directBufferConstructor = null;
                }
            }
            finally {
                if (address != -1L) {
                    UNSAFE.freeMemory((long)address);
                }
            }
            DIRECT_BUFFER_CONSTRUCTOR = directBufferConstructor;
            ADDRESS_FIELD_OFFSET = PlatformDependent0.objectFieldOffset((Field)addressField);
            BYTE_ARRAY_BASE_OFFSET = (long)UNSAFE.arrayBaseOffset(byte[].class);
            Object maybeUnaligned = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                public Object run() {
                    try {
                        Throwable cause;
                        Method unalignedMethod;
                        Class<?> bitsClass = Class.forName((String)"java.nio.Bits", (boolean)false, (ClassLoader)PlatformDependent0.getSystemClassLoader());
                        int version = PlatformDependent0.javaVersion();
                        if (version >= 9) {
                            String fieldName = version >= 11 ? "UNALIGNED" : "unaligned";
                            try {
                                Field unalignedField = bitsClass.getDeclaredField((String)fieldName);
                                if (unalignedField.getType() == Boolean.TYPE) {
                                    long offset = UNSAFE.staticFieldOffset((Field)unalignedField);
                                    Object object = UNSAFE.staticFieldBase((Field)unalignedField);
                                    return Boolean.valueOf((boolean)UNSAFE.getBoolean((Object)object, (long)offset));
                                }
                            }
                            catch (java.lang.NoSuchFieldException unalignedField) {
                                // empty catch block
                            }
                        }
                        if ((cause = io.netty.util.internal.ReflectionUtil.trySetAccessible((java.lang.reflect.AccessibleObject)(unalignedMethod = bitsClass.getDeclaredMethod((String)"unaligned", new Class[0])), (boolean)true)) == null) return unalignedMethod.invoke(null, (Object[])new Object[0]);
                        return cause;
                    }
                    catch (java.lang.NoSuchMethodException e) {
                        return e;
                    }
                    catch (java.lang.SecurityException e) {
                        return e;
                    }
                    catch (IllegalAccessException e) {
                        return e;
                    }
                    catch (java.lang.ClassNotFoundException e) {
                        return e;
                    }
                    catch (InvocationTargetException e) {
                        return e;
                    }
                }
            });
            if (maybeUnaligned instanceof Boolean) {
                unaligned = ((Boolean)maybeUnaligned).booleanValue();
                logger.debug((String)"java.nio.Bits.unaligned: available, {}", (Object)Boolean.valueOf((boolean)unaligned));
            } else {
                String arch = SystemPropertyUtil.get((String)"os.arch", (String)"");
                unaligned = arch.matches((String)"^(i[3-6]86|x86(_64)?|x64|amd64)$");
                Throwable t = (Throwable)maybeUnaligned;
                logger.debug((String)"java.nio.Bits.unaligned: unavailable {}", (Object)Boolean.valueOf((boolean)unaligned), (Object)t);
            }
            UNALIGNED = unaligned;
            if (PlatformDependent0.javaVersion() >= 9) {
                Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                    public Object run() {
                        try {
                            Class<?> internalUnsafeClass = PlatformDependent0.getClassLoader(PlatformDependent0.class).loadClass((String)"jdk.internal.misc.Unsafe");
                            Method method = internalUnsafeClass.getDeclaredMethod((String)"getUnsafe", new Class[0]);
                            return method.invoke(null, (Object[])new Object[0]);
                        }
                        catch (Throwable e) {
                            return e;
                        }
                    }
                });
                if (!(maybeException instanceof Throwable)) {
                    Object finalInternalUnsafe = internalUnsafe = maybeException;
                    maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>((Object)finalInternalUnsafe){
                        final /* synthetic */ Object val$finalInternalUnsafe;
                        {
                            this.val$finalInternalUnsafe = object;
                        }

                        public Object run() {
                            try {
                                return this.val$finalInternalUnsafe.getClass().getDeclaredMethod((String)"allocateUninitializedArray", Class.class, Integer.TYPE);
                            }
                            catch (java.lang.NoSuchMethodException e) {
                                return e;
                            }
                            catch (java.lang.SecurityException e) {
                                return e;
                            }
                        }
                    });
                    if (maybeException instanceof Method) {
                        try {
                            Method m = (Method)maybeException;
                            byte[] bytes = (byte[])m.invoke((Object)finalInternalUnsafe, (Object[])new Object[]{Byte.TYPE, Integer.valueOf((int)8)});
                            assert (bytes.length == 8);
                            allocateArrayMethod = m;
                        }
                        catch (IllegalAccessException e) {
                            maybeException = e;
                        }
                        catch (InvocationTargetException e) {
                            maybeException = e;
                        }
                    }
                }
                if (maybeException instanceof Throwable) {
                    logger.debug((String)"jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable", (Throwable)((Throwable)maybeException));
                } else {
                    logger.debug((String)"jdk.internal.misc.Unsafe.allocateUninitializedArray(int): available");
                }
            } else {
                logger.debug((String)"jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9");
            }
            ALLOCATE_ARRAY_METHOD = allocateArrayMethod;
        }
        INTERNAL_UNSAFE = internalUnsafe;
        logger.debug((String)"java.nio.DirectByteBuffer.<init>(long, int): {}", (Object)(DIRECT_BUFFER_CONSTRUCTOR != null ? "available" : "unavailable"));
    }
}

