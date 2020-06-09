/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.buffer.UnpooledUnsafeNoCleanerDirectByteBuf;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

final class UnsafeByteBufUtil {
    private static final boolean UNALIGNED = PlatformDependent.isUnaligned();
    private static final byte ZERO = 0;

    static byte getByte(long address) {
        return PlatformDependent.getByte((long)address);
    }

    static short getShort(long address) {
        short s;
        if (!UNALIGNED) return (short)(PlatformDependent.getByte((long)address) << 8 | PlatformDependent.getByte((long)(address + 1L)) & 255);
        short v = PlatformDependent.getShort((long)address);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            s = v;
            return s;
        }
        s = Short.reverseBytes((short)v);
        return s;
    }

    static short getShortLE(long address) {
        short s;
        if (!UNALIGNED) return (short)(PlatformDependent.getByte((long)address) & 255 | PlatformDependent.getByte((long)(address + 1L)) << 8);
        short v = PlatformDependent.getShort((long)address);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            s = Short.reverseBytes((short)v);
            return s;
        }
        s = v;
        return s;
    }

    static int getUnsignedMedium(long address) {
        short s;
        if (!UNALIGNED) return (PlatformDependent.getByte((long)address) & 255) << 16 | (PlatformDependent.getByte((long)(address + 1L)) & 255) << 8 | PlatformDependent.getByte((long)(address + 2L)) & 255;
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            s = PlatformDependent.getShort((long)(address + 1L));
            return (PlatformDependent.getByte((long)address) & 255) << 16 | s & 65535;
        }
        s = Short.reverseBytes((short)PlatformDependent.getShort((long)(address + 1L)));
        return (PlatformDependent.getByte((long)address) & 255) << 16 | s & 65535;
    }

    static int getUnsignedMediumLE(long address) {
        short s;
        if (!UNALIGNED) return PlatformDependent.getByte((long)address) & 255 | (PlatformDependent.getByte((long)(address + 1L)) & 255) << 8 | (PlatformDependent.getByte((long)(address + 2L)) & 255) << 16;
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            s = Short.reverseBytes((short)PlatformDependent.getShort((long)(address + 1L)));
            return PlatformDependent.getByte((long)address) & 255 | (s & 65535) << 8;
        }
        s = PlatformDependent.getShort((long)(address + 1L));
        return PlatformDependent.getByte((long)address) & 255 | (s & 65535) << 8;
    }

    static int getInt(long address) {
        int n;
        if (!UNALIGNED) return PlatformDependent.getByte((long)address) << 24 | (PlatformDependent.getByte((long)(address + 1L)) & 255) << 16 | (PlatformDependent.getByte((long)(address + 2L)) & 255) << 8 | PlatformDependent.getByte((long)(address + 3L)) & 255;
        int v = PlatformDependent.getInt((long)address);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            n = v;
            return n;
        }
        n = Integer.reverseBytes((int)v);
        return n;
    }

    static int getIntLE(long address) {
        int n;
        if (!UNALIGNED) return PlatformDependent.getByte((long)address) & 255 | (PlatformDependent.getByte((long)(address + 1L)) & 255) << 8 | (PlatformDependent.getByte((long)(address + 2L)) & 255) << 16 | PlatformDependent.getByte((long)(address + 3L)) << 24;
        int v = PlatformDependent.getInt((long)address);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            n = Integer.reverseBytes((int)v);
            return n;
        }
        n = v;
        return n;
    }

    static long getLong(long address) {
        long l;
        if (!UNALIGNED) return (long)PlatformDependent.getByte((long)address) << 56 | ((long)PlatformDependent.getByte((long)(address + 1L)) & 255L) << 48 | ((long)PlatformDependent.getByte((long)(address + 2L)) & 255L) << 40 | ((long)PlatformDependent.getByte((long)(address + 3L)) & 255L) << 32 | ((long)PlatformDependent.getByte((long)(address + 4L)) & 255L) << 24 | ((long)PlatformDependent.getByte((long)(address + 5L)) & 255L) << 16 | ((long)PlatformDependent.getByte((long)(address + 6L)) & 255L) << 8 | (long)PlatformDependent.getByte((long)(address + 7L)) & 255L;
        long v = PlatformDependent.getLong((long)address);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            l = v;
            return l;
        }
        l = Long.reverseBytes((long)v);
        return l;
    }

    static long getLongLE(long address) {
        long l;
        if (!UNALIGNED) return (long)PlatformDependent.getByte((long)address) & 255L | ((long)PlatformDependent.getByte((long)(address + 1L)) & 255L) << 8 | ((long)PlatformDependent.getByte((long)(address + 2L)) & 255L) << 16 | ((long)PlatformDependent.getByte((long)(address + 3L)) & 255L) << 24 | ((long)PlatformDependent.getByte((long)(address + 4L)) & 255L) << 32 | ((long)PlatformDependent.getByte((long)(address + 5L)) & 255L) << 40 | ((long)PlatformDependent.getByte((long)(address + 6L)) & 255L) << 48 | (long)PlatformDependent.getByte((long)(address + 7L)) << 56;
        long v = PlatformDependent.getLong((long)address);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            l = Long.reverseBytes((long)v);
            return l;
        }
        l = v;
        return l;
    }

    static void setByte(long address, int value) {
        PlatformDependent.putByte((long)address, (byte)((byte)value));
    }

    static void setShort(long address, int value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((long)address, (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((long)(address + 1L), (byte)((byte)value));
            return;
        }
        PlatformDependent.putShort((long)address, (short)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : Short.reverseBytes((short)((short)value))));
    }

    static void setShortLE(long address, int value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((long)address, (byte)((byte)value));
            PlatformDependent.putByte((long)(address + 1L), (byte)((byte)(value >>> 8)));
            return;
        }
        PlatformDependent.putShort((long)address, (short)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)((short)value)) : (short)value));
    }

    static void setMedium(long address, int value) {
        PlatformDependent.putByte((long)address, (byte)((byte)(value >>> 16)));
        if (!UNALIGNED) {
            PlatformDependent.putByte((long)(address + 1L), (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((long)(address + 2L), (byte)((byte)value));
            return;
        }
        PlatformDependent.putShort((long)(address + 1L), (short)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : Short.reverseBytes((short)((short)value))));
    }

    static void setMediumLE(long address, int value) {
        PlatformDependent.putByte((long)address, (byte)((byte)value));
        if (!UNALIGNED) {
            PlatformDependent.putByte((long)(address + 1L), (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((long)(address + 2L), (byte)((byte)(value >>> 16)));
            return;
        }
        PlatformDependent.putShort((long)(address + 1L), (short)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)((short)(value >>> 8))) : (short)(value >>> 8)));
    }

    static void setInt(long address, int value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((long)address, (byte)((byte)(value >>> 24)));
            PlatformDependent.putByte((long)(address + 1L), (byte)((byte)(value >>> 16)));
            PlatformDependent.putByte((long)(address + 2L), (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((long)(address + 3L), (byte)((byte)value));
            return;
        }
        PlatformDependent.putInt((long)address, (int)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Integer.reverseBytes((int)value)));
    }

    static void setIntLE(long address, int value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((long)address, (byte)((byte)value));
            PlatformDependent.putByte((long)(address + 1L), (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((long)(address + 2L), (byte)((byte)(value >>> 16)));
            PlatformDependent.putByte((long)(address + 3L), (byte)((byte)(value >>> 24)));
            return;
        }
        PlatformDependent.putInt((long)address, (int)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes((int)value) : value));
    }

    static void setLong(long address, long value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((long)address, (byte)((byte)((int)(value >>> 56))));
            PlatformDependent.putByte((long)(address + 1L), (byte)((byte)((int)(value >>> 48))));
            PlatformDependent.putByte((long)(address + 2L), (byte)((byte)((int)(value >>> 40))));
            PlatformDependent.putByte((long)(address + 3L), (byte)((byte)((int)(value >>> 32))));
            PlatformDependent.putByte((long)(address + 4L), (byte)((byte)((int)(value >>> 24))));
            PlatformDependent.putByte((long)(address + 5L), (byte)((byte)((int)(value >>> 16))));
            PlatformDependent.putByte((long)(address + 6L), (byte)((byte)((int)(value >>> 8))));
            PlatformDependent.putByte((long)(address + 7L), (byte)((byte)((int)value)));
            return;
        }
        PlatformDependent.putLong((long)address, (long)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Long.reverseBytes((long)value)));
    }

    static void setLongLE(long address, long value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((long)address, (byte)((byte)((int)value)));
            PlatformDependent.putByte((long)(address + 1L), (byte)((byte)((int)(value >>> 8))));
            PlatformDependent.putByte((long)(address + 2L), (byte)((byte)((int)(value >>> 16))));
            PlatformDependent.putByte((long)(address + 3L), (byte)((byte)((int)(value >>> 24))));
            PlatformDependent.putByte((long)(address + 4L), (byte)((byte)((int)(value >>> 32))));
            PlatformDependent.putByte((long)(address + 5L), (byte)((byte)((int)(value >>> 40))));
            PlatformDependent.putByte((long)(address + 6L), (byte)((byte)((int)(value >>> 48))));
            PlatformDependent.putByte((long)(address + 7L), (byte)((byte)((int)(value >>> 56))));
            return;
        }
        PlatformDependent.putLong((long)address, (long)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes((long)value) : value));
    }

    static byte getByte(byte[] array, int index) {
        return PlatformDependent.getByte((byte[])array, (int)index);
    }

    static short getShort(byte[] array, int index) {
        short s;
        if (!UNALIGNED) return (short)(PlatformDependent.getByte((byte[])array, (int)index) << 8 | PlatformDependent.getByte((byte[])array, (int)(index + 1)) & 255);
        short v = PlatformDependent.getShort((byte[])array, (int)index);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            s = v;
            return s;
        }
        s = Short.reverseBytes((short)v);
        return s;
    }

    static short getShortLE(byte[] array, int index) {
        short s;
        if (!UNALIGNED) return (short)(PlatformDependent.getByte((byte[])array, (int)index) & 255 | PlatformDependent.getByte((byte[])array, (int)(index + 1)) << 8);
        short v = PlatformDependent.getShort((byte[])array, (int)index);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            s = Short.reverseBytes((short)v);
            return s;
        }
        s = v;
        return s;
    }

    static int getUnsignedMedium(byte[] array, int index) {
        short s;
        if (!UNALIGNED) return (PlatformDependent.getByte((byte[])array, (int)index) & 255) << 16 | (PlatformDependent.getByte((byte[])array, (int)(index + 1)) & 255) << 8 | PlatformDependent.getByte((byte[])array, (int)(index + 2)) & 255;
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            s = PlatformDependent.getShort((byte[])array, (int)(index + 1));
            return (PlatformDependent.getByte((byte[])array, (int)index) & 255) << 16 | s & 65535;
        }
        s = Short.reverseBytes((short)PlatformDependent.getShort((byte[])array, (int)(index + 1)));
        return (PlatformDependent.getByte((byte[])array, (int)index) & 255) << 16 | s & 65535;
    }

    static int getUnsignedMediumLE(byte[] array, int index) {
        short s;
        if (!UNALIGNED) return PlatformDependent.getByte((byte[])array, (int)index) & 255 | (PlatformDependent.getByte((byte[])array, (int)(index + 1)) & 255) << 8 | (PlatformDependent.getByte((byte[])array, (int)(index + 2)) & 255) << 16;
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            s = Short.reverseBytes((short)PlatformDependent.getShort((byte[])array, (int)(index + 1)));
            return PlatformDependent.getByte((byte[])array, (int)index) & 255 | (s & 65535) << 8;
        }
        s = PlatformDependent.getShort((byte[])array, (int)(index + 1));
        return PlatformDependent.getByte((byte[])array, (int)index) & 255 | (s & 65535) << 8;
    }

    static int getInt(byte[] array, int index) {
        int n;
        if (!UNALIGNED) return PlatformDependent.getByte((byte[])array, (int)index) << 24 | (PlatformDependent.getByte((byte[])array, (int)(index + 1)) & 255) << 16 | (PlatformDependent.getByte((byte[])array, (int)(index + 2)) & 255) << 8 | PlatformDependent.getByte((byte[])array, (int)(index + 3)) & 255;
        int v = PlatformDependent.getInt((byte[])array, (int)index);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            n = v;
            return n;
        }
        n = Integer.reverseBytes((int)v);
        return n;
    }

    static int getIntLE(byte[] array, int index) {
        int n;
        if (!UNALIGNED) return PlatformDependent.getByte((byte[])array, (int)index) & 255 | (PlatformDependent.getByte((byte[])array, (int)(index + 1)) & 255) << 8 | (PlatformDependent.getByte((byte[])array, (int)(index + 2)) & 255) << 16 | PlatformDependent.getByte((byte[])array, (int)(index + 3)) << 24;
        int v = PlatformDependent.getInt((byte[])array, (int)index);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            n = Integer.reverseBytes((int)v);
            return n;
        }
        n = v;
        return n;
    }

    static long getLong(byte[] array, int index) {
        long l;
        if (!UNALIGNED) return (long)PlatformDependent.getByte((byte[])array, (int)index) << 56 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 1)) & 255L) << 48 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 2)) & 255L) << 40 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 3)) & 255L) << 32 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 4)) & 255L) << 24 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 5)) & 255L) << 16 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 6)) & 255L) << 8 | (long)PlatformDependent.getByte((byte[])array, (int)(index + 7)) & 255L;
        long v = PlatformDependent.getLong((byte[])array, (int)index);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            l = v;
            return l;
        }
        l = Long.reverseBytes((long)v);
        return l;
    }

    static long getLongLE(byte[] array, int index) {
        long l;
        if (!UNALIGNED) return (long)PlatformDependent.getByte((byte[])array, (int)index) & 255L | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 1)) & 255L) << 8 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 2)) & 255L) << 16 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 3)) & 255L) << 24 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 4)) & 255L) << 32 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 5)) & 255L) << 40 | ((long)PlatformDependent.getByte((byte[])array, (int)(index + 6)) & 255L) << 48 | (long)PlatformDependent.getByte((byte[])array, (int)(index + 7)) << 56;
        long v = PlatformDependent.getLong((byte[])array, (int)index);
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            l = Long.reverseBytes((long)v);
            return l;
        }
        l = v;
        return l;
    }

    static void setByte(byte[] array, int index, int value) {
        PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)value));
    }

    static void setShort(byte[] array, int index, int value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((byte[])array, (int)(index + 1), (byte)((byte)value));
            return;
        }
        PlatformDependent.putShort((byte[])array, (int)index, (short)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : Short.reverseBytes((short)((short)value))));
    }

    static void setShortLE(byte[] array, int index, int value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)value));
            PlatformDependent.putByte((byte[])array, (int)(index + 1), (byte)((byte)(value >>> 8)));
            return;
        }
        PlatformDependent.putShort((byte[])array, (int)index, (short)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)((short)value)) : (short)value));
    }

    static void setMedium(byte[] array, int index, int value) {
        PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)(value >>> 16)));
        if (!UNALIGNED) {
            PlatformDependent.putByte((byte[])array, (int)(index + 1), (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((byte[])array, (int)(index + 2), (byte)((byte)value));
            return;
        }
        PlatformDependent.putShort((byte[])array, (int)(index + 1), (short)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : Short.reverseBytes((short)((short)value))));
    }

    static void setMediumLE(byte[] array, int index, int value) {
        PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)value));
        if (!UNALIGNED) {
            PlatformDependent.putByte((byte[])array, (int)(index + 1), (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((byte[])array, (int)(index + 2), (byte)((byte)(value >>> 16)));
            return;
        }
        PlatformDependent.putShort((byte[])array, (int)(index + 1), (short)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)((short)(value >>> 8))) : (short)(value >>> 8)));
    }

    static void setInt(byte[] array, int index, int value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)(value >>> 24)));
            PlatformDependent.putByte((byte[])array, (int)(index + 1), (byte)((byte)(value >>> 16)));
            PlatformDependent.putByte((byte[])array, (int)(index + 2), (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((byte[])array, (int)(index + 3), (byte)((byte)value));
            return;
        }
        PlatformDependent.putInt((byte[])array, (int)index, (int)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Integer.reverseBytes((int)value)));
    }

    static void setIntLE(byte[] array, int index, int value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)value));
            PlatformDependent.putByte((byte[])array, (int)(index + 1), (byte)((byte)(value >>> 8)));
            PlatformDependent.putByte((byte[])array, (int)(index + 2), (byte)((byte)(value >>> 16)));
            PlatformDependent.putByte((byte[])array, (int)(index + 3), (byte)((byte)(value >>> 24)));
            return;
        }
        PlatformDependent.putInt((byte[])array, (int)index, (int)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes((int)value) : value));
    }

    static void setLong(byte[] array, int index, long value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)((int)(value >>> 56))));
            PlatformDependent.putByte((byte[])array, (int)(index + 1), (byte)((byte)((int)(value >>> 48))));
            PlatformDependent.putByte((byte[])array, (int)(index + 2), (byte)((byte)((int)(value >>> 40))));
            PlatformDependent.putByte((byte[])array, (int)(index + 3), (byte)((byte)((int)(value >>> 32))));
            PlatformDependent.putByte((byte[])array, (int)(index + 4), (byte)((byte)((int)(value >>> 24))));
            PlatformDependent.putByte((byte[])array, (int)(index + 5), (byte)((byte)((int)(value >>> 16))));
            PlatformDependent.putByte((byte[])array, (int)(index + 6), (byte)((byte)((int)(value >>> 8))));
            PlatformDependent.putByte((byte[])array, (int)(index + 7), (byte)((byte)((int)value)));
            return;
        }
        PlatformDependent.putLong((byte[])array, (int)index, (long)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Long.reverseBytes((long)value)));
    }

    static void setLongLE(byte[] array, int index, long value) {
        if (!UNALIGNED) {
            PlatformDependent.putByte((byte[])array, (int)index, (byte)((byte)((int)value)));
            PlatformDependent.putByte((byte[])array, (int)(index + 1), (byte)((byte)((int)(value >>> 8))));
            PlatformDependent.putByte((byte[])array, (int)(index + 2), (byte)((byte)((int)(value >>> 16))));
            PlatformDependent.putByte((byte[])array, (int)(index + 3), (byte)((byte)((int)(value >>> 24))));
            PlatformDependent.putByte((byte[])array, (int)(index + 4), (byte)((byte)((int)(value >>> 32))));
            PlatformDependent.putByte((byte[])array, (int)(index + 5), (byte)((byte)((int)(value >>> 40))));
            PlatformDependent.putByte((byte[])array, (int)(index + 6), (byte)((byte)((int)(value >>> 48))));
            PlatformDependent.putByte((byte[])array, (int)(index + 7), (byte)((byte)((int)(value >>> 56))));
            return;
        }
        PlatformDependent.putLong((byte[])array, (int)index, (long)(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes((long)value) : value));
    }

    static void setZero(byte[] array, int index, int length) {
        if (length == 0) {
            return;
        }
        PlatformDependent.setMemory((byte[])array, (int)index, (long)((long)length), (byte)0);
    }

    static ByteBuf copy(AbstractByteBuf buf, long addr, int index, int length) {
        buf.checkIndex((int)index, (int)length);
        ByteBuf copy = buf.alloc().directBuffer((int)length, (int)buf.maxCapacity());
        if (length == 0) return copy;
        if (copy.hasMemoryAddress()) {
            PlatformDependent.copyMemory((long)addr, (long)copy.memoryAddress(), (long)((long)length));
            copy.setIndex((int)0, (int)length);
            return copy;
        }
        copy.writeBytes((ByteBuf)buf, (int)index, (int)length);
        return copy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int setBytes(AbstractByteBuf buf, long addr, int index, InputStream in, int length) throws IOException {
        buf.checkIndex((int)index, (int)length);
        ByteBuf tmpBuf = buf.alloc().heapBuffer((int)length);
        try {
            byte[] tmp = tmpBuf.array();
            int offset = tmpBuf.arrayOffset();
            int readBytes = in.read((byte[])tmp, (int)offset, (int)length);
            if (readBytes > 0) {
                PlatformDependent.copyMemory((byte[])tmp, (int)offset, (long)addr, (long)((long)readBytes));
            }
            int n = readBytes;
            return n;
        }
        finally {
            tmpBuf.release();
        }
    }

    static void getBytes(AbstractByteBuf buf, long addr, int index, ByteBuf dst, int dstIndex, int length) {
        buf.checkIndex((int)index, (int)length);
        ObjectUtil.checkNotNull(dst, (String)"dst");
        if (MathUtil.isOutOfBounds((int)dstIndex, (int)length, (int)dst.capacity())) {
            throw new IndexOutOfBoundsException((String)("dstIndex: " + dstIndex));
        }
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory((long)addr, (long)(dst.memoryAddress() + (long)dstIndex), (long)((long)length));
            return;
        }
        if (dst.hasArray()) {
            PlatformDependent.copyMemory((long)addr, (byte[])dst.array(), (int)(dst.arrayOffset() + dstIndex), (long)((long)length));
            return;
        }
        dst.setBytes((int)dstIndex, (ByteBuf)buf, (int)index, (int)length);
    }

    static void getBytes(AbstractByteBuf buf, long addr, int index, byte[] dst, int dstIndex, int length) {
        buf.checkIndex((int)index, (int)length);
        ObjectUtil.checkNotNull(dst, (String)"dst");
        if (MathUtil.isOutOfBounds((int)dstIndex, (int)length, (int)dst.length)) {
            throw new IndexOutOfBoundsException((String)("dstIndex: " + dstIndex));
        }
        if (length == 0) return;
        PlatformDependent.copyMemory((long)addr, (byte[])dst, (int)dstIndex, (long)((long)length));
    }

    static void getBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer dst) {
        buf.checkIndex((int)index, (int)dst.remaining());
        if (dst.remaining() == 0) {
            return;
        }
        if (dst.isDirect()) {
            if (dst.isReadOnly()) {
                throw new ReadOnlyBufferException();
            }
            long dstAddress = PlatformDependent.directBufferAddress((ByteBuffer)dst);
            PlatformDependent.copyMemory((long)addr, (long)(dstAddress + (long)dst.position()), (long)((long)dst.remaining()));
            dst.position((int)(dst.position() + dst.remaining()));
            return;
        }
        if (dst.hasArray()) {
            PlatformDependent.copyMemory((long)addr, (byte[])dst.array(), (int)(dst.arrayOffset() + dst.position()), (long)((long)dst.remaining()));
            dst.position((int)(dst.position() + dst.remaining()));
            return;
        }
        dst.put((ByteBuffer)buf.nioBuffer());
    }

    static void setBytes(AbstractByteBuf buf, long addr, int index, ByteBuf src, int srcIndex, int length) {
        buf.checkIndex((int)index, (int)length);
        ObjectUtil.checkNotNull(src, (String)"src");
        if (MathUtil.isOutOfBounds((int)srcIndex, (int)length, (int)src.capacity())) {
            throw new IndexOutOfBoundsException((String)("srcIndex: " + srcIndex));
        }
        if (length == 0) return;
        if (src.hasMemoryAddress()) {
            PlatformDependent.copyMemory((long)(src.memoryAddress() + (long)srcIndex), (long)addr, (long)((long)length));
            return;
        }
        if (src.hasArray()) {
            PlatformDependent.copyMemory((byte[])src.array(), (int)(src.arrayOffset() + srcIndex), (long)addr, (long)((long)length));
            return;
        }
        src.getBytes((int)srcIndex, (ByteBuf)buf, (int)index, (int)length);
    }

    static void setBytes(AbstractByteBuf buf, long addr, int index, byte[] src, int srcIndex, int length) {
        buf.checkIndex((int)index, (int)length);
        if (length == 0) return;
        PlatformDependent.copyMemory((byte[])src, (int)srcIndex, (long)addr, (long)((long)length));
    }

    static void setBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer src) {
        int length = src.remaining();
        if (length == 0) {
            return;
        }
        if (src.isDirect()) {
            buf.checkIndex((int)index, (int)length);
            long srcAddress = PlatformDependent.directBufferAddress((ByteBuffer)src);
            PlatformDependent.copyMemory((long)(srcAddress + (long)src.position()), (long)addr, (long)((long)length));
            src.position((int)(src.position() + length));
            return;
        }
        if (src.hasArray()) {
            buf.checkIndex((int)index, (int)length);
            PlatformDependent.copyMemory((byte[])src.array(), (int)(src.arrayOffset() + src.position()), (long)addr, (long)((long)length));
            src.position((int)(src.position() + length));
            return;
        }
        if (length < 8) {
            UnsafeByteBufUtil.setSingleBytes((AbstractByteBuf)buf, (long)addr, (int)index, (ByteBuffer)src, (int)length);
            return;
        }
        assert (buf.nioBufferCount() == 1);
        ByteBuffer internalBuffer = buf.internalNioBuffer((int)index, (int)length);
        internalBuffer.put((ByteBuffer)src);
    }

    private static void setSingleBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer src, int length) {
        buf.checkIndex((int)index, (int)length);
        int srcPosition = src.position();
        int srcLimit = src.limit();
        long dstAddr = addr;
        int srcIndex = srcPosition;
        do {
            if (srcIndex >= srcLimit) {
                src.position((int)srcLimit);
                return;
            }
            byte value = src.get((int)srcIndex);
            PlatformDependent.putByte((long)dstAddr, (byte)value);
            ++dstAddr;
            ++srcIndex;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void getBytes(AbstractByteBuf buf, long addr, int index, OutputStream out, int length) throws IOException {
        buf.checkIndex((int)index, (int)length);
        if (length == 0) return;
        int len = Math.min((int)length, (int)8192);
        if (len <= 1024 || !buf.alloc().isDirectBufferPooled()) {
            UnsafeByteBufUtil.getBytes((long)addr, (byte[])ByteBufUtil.threadLocalTempArray((int)len), (int)0, (int)len, (OutputStream)out, (int)length);
            return;
        }
        ByteBuf tmpBuf = buf.alloc().heapBuffer((int)len);
        try {
            byte[] tmp = tmpBuf.array();
            int offset = tmpBuf.arrayOffset();
            UnsafeByteBufUtil.getBytes((long)addr, (byte[])tmp, (int)offset, (int)len, (OutputStream)out, (int)length);
            return;
        }
        finally {
            tmpBuf.release();
        }
    }

    private static void getBytes(long inAddr, byte[] in, int inOffset, int inLen, OutputStream out, int outLen) throws IOException {
        int len;
        do {
            len = Math.min((int)inLen, (int)outLen);
            PlatformDependent.copyMemory((long)inAddr, (byte[])in, (int)inOffset, (long)((long)len));
            out.write((byte[])in, (int)inOffset, (int)len);
            inAddr += (long)len;
        } while ((outLen -= len) > 0);
    }

    static void setZero(long addr, int length) {
        if (length == 0) {
            return;
        }
        PlatformDependent.setMemory((long)addr, (long)((long)length), (byte)0);
    }

    static UnpooledUnsafeDirectByteBuf newUnsafeDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        if (!PlatformDependent.useDirectBufferNoCleaner()) return new UnpooledUnsafeDirectByteBuf((ByteBufAllocator)alloc, (int)initialCapacity, (int)maxCapacity);
        return new UnpooledUnsafeNoCleanerDirectByteBuf((ByteBufAllocator)alloc, (int)initialCapacity, (int)maxCapacity);
    }

    private UnsafeByteBufUtil() {
    }
}

