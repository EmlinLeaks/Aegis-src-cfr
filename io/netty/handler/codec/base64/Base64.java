/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import java.nio.ByteOrder;

public final class Base64 {
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;

    private static byte[] alphabet(Base64Dialect dialect) {
        if (dialect != null) return dialect.alphabet;
        throw new NullPointerException((String)"dialect");
    }

    private static byte[] decodabet(Base64Dialect dialect) {
        if (dialect != null) return dialect.decodabet;
        throw new NullPointerException((String)"dialect");
    }

    private static boolean breakLines(Base64Dialect dialect) {
        if (dialect != null) return dialect.breakLinesByDefault;
        throw new NullPointerException((String)"dialect");
    }

    public static ByteBuf encode(ByteBuf src) {
        return Base64.encode((ByteBuf)src, (Base64Dialect)Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf src, Base64Dialect dialect) {
        return Base64.encode((ByteBuf)src, (boolean)Base64.breakLines((Base64Dialect)dialect), (Base64Dialect)dialect);
    }

    public static ByteBuf encode(ByteBuf src, boolean breakLines) {
        return Base64.encode((ByteBuf)src, (boolean)breakLines, (Base64Dialect)Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf src, boolean breakLines, Base64Dialect dialect) {
        if (src == null) {
            throw new NullPointerException((String)"src");
        }
        ByteBuf dest = Base64.encode((ByteBuf)src, (int)src.readerIndex(), (int)src.readableBytes(), (boolean)breakLines, (Base64Dialect)dialect);
        src.readerIndex((int)src.writerIndex());
        return dest;
    }

    public static ByteBuf encode(ByteBuf src, int off, int len) {
        return Base64.encode((ByteBuf)src, (int)off, (int)len, (Base64Dialect)Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, Base64Dialect dialect) {
        return Base64.encode((ByteBuf)src, (int)off, (int)len, (boolean)Base64.breakLines((Base64Dialect)dialect), (Base64Dialect)dialect);
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines) {
        return Base64.encode((ByteBuf)src, (int)off, (int)len, (boolean)breakLines, (Base64Dialect)Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect) {
        return Base64.encode((ByteBuf)src, (int)off, (int)len, (boolean)breakLines, (Base64Dialect)dialect, (ByteBufAllocator)src.alloc());
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect, ByteBufAllocator allocator) {
        int d;
        if (src == null) {
            throw new NullPointerException((String)"src");
        }
        if (dialect == null) {
            throw new NullPointerException((String)"dialect");
        }
        ByteBuf dest = allocator.buffer((int)Base64.encodedBufferSize((int)len, (boolean)breakLines)).order((ByteOrder)src.order());
        byte[] alphabet = Base64.alphabet((Base64Dialect)dialect);
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        for (d = 0; d < len2; d += 3, e += 4) {
            Base64.encode3to4((ByteBuf)src, (int)(d + off), (int)3, (ByteBuf)dest, (int)e, (byte[])alphabet);
            if (!breakLines || (lineLength += 4) != 76) continue;
            dest.setByte((int)(e + 4), (int)10);
            ++e;
            lineLength = 0;
        }
        if (d < len) {
            Base64.encode3to4((ByteBuf)src, (int)(d + off), (int)(len - d), (ByteBuf)dest, (int)e, (byte[])alphabet);
            e += 4;
        }
        if (e <= true) return dest.slice((int)0, (int)e);
        if (dest.getByte((int)(e - 1)) != 10) return dest.slice((int)0, (int)e);
        --e;
        return dest.slice((int)0, (int)e);
    }

    /*
     * Unable to fully structure code
     */
    private static void encode3to4(ByteBuf src, int srcOffset, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
        if (src.order() == ByteOrder.BIG_ENDIAN) {
            switch (numSigBytes) {
                case 1: {
                    inBuff = Base64.toInt((byte)src.getByte((int)srcOffset));
                    ** break;
                }
                case 2: {
                    inBuff = Base64.toIntBE((short)src.getShort((int)srcOffset));
                    ** break;
                }
            }
            inBuff = numSigBytes <= 0 ? 0 : Base64.toIntBE((int)src.getMedium((int)srcOffset));
lbl10: // 3 sources:
            Base64.encode3to4BigEndian((int)inBuff, (int)numSigBytes, (ByteBuf)dest, (int)destOffset, (byte[])alphabet);
            return;
        }
        switch (numSigBytes) {
            case 1: {
                inBuff = Base64.toInt((byte)src.getByte((int)srcOffset));
                ** break;
            }
            case 2: {
                inBuff = Base64.toIntLE((short)src.getShort((int)srcOffset));
                ** break;
            }
        }
        inBuff = numSigBytes <= 0 ? 0 : Base64.toIntLE((int)src.getMedium((int)srcOffset));
lbl20: // 3 sources:
        Base64.encode3to4LittleEndian((int)inBuff, (int)numSigBytes, (ByteBuf)dest, (int)destOffset, (byte[])alphabet);
    }

    static int encodedBufferSize(int len, boolean breakLines) {
        long len43 = ((long)len << 2) / 3L;
        long ret = len43 + 3L & -4L;
        if (breakLines) {
            ret += len43 / 76L;
        }
        if (ret >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        int n = (int)ret;
        return n;
    }

    private static int toInt(byte value) {
        return (value & 255) << 16;
    }

    private static int toIntBE(short value) {
        return (value & 65280) << 8 | (value & 255) << 8;
    }

    private static int toIntLE(short value) {
        return (value & 255) << 16 | value & 65280;
    }

    private static int toIntBE(int mediumValue) {
        return mediumValue & 16711680 | mediumValue & 65280 | mediumValue & 255;
    }

    private static int toIntLE(int mediumValue) {
        return (mediumValue & 255) << 16 | mediumValue & 65280 | (mediumValue & 16711680) >>> 16;
    }

    private static void encode3to4BigEndian(int inBuff, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
        switch (numSigBytes) {
            case 3: {
                dest.setInt((int)destOffset, (int)(alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 63] << 16 | alphabet[inBuff >>> 6 & 63] << 8 | alphabet[inBuff & 63]));
                return;
            }
            case 2: {
                dest.setInt((int)destOffset, (int)(alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 63] << 16 | alphabet[inBuff >>> 6 & 63] << 8 | 61));
                return;
            }
            case 1: {
                dest.setInt((int)destOffset, (int)(alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 63] << 16 | 15616 | 61));
                break;
            }
        }
    }

    private static void encode3to4LittleEndian(int inBuff, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
        switch (numSigBytes) {
            case 3: {
                dest.setInt((int)destOffset, (int)(alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 63] << 8 | alphabet[inBuff >>> 6 & 63] << 16 | alphabet[inBuff & 63] << 24));
                return;
            }
            case 2: {
                dest.setInt((int)destOffset, (int)(alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 63] << 8 | alphabet[inBuff >>> 6 & 63] << 16 | 1023410176));
                return;
            }
            case 1: {
                dest.setInt((int)destOffset, (int)(alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 63] << 8 | 3997696 | 1023410176));
                break;
            }
        }
    }

    public static ByteBuf decode(ByteBuf src) {
        return Base64.decode((ByteBuf)src, (Base64Dialect)Base64Dialect.STANDARD);
    }

    public static ByteBuf decode(ByteBuf src, Base64Dialect dialect) {
        if (src == null) {
            throw new NullPointerException((String)"src");
        }
        ByteBuf dest = Base64.decode((ByteBuf)src, (int)src.readerIndex(), (int)src.readableBytes(), (Base64Dialect)dialect);
        src.readerIndex((int)src.writerIndex());
        return dest;
    }

    public static ByteBuf decode(ByteBuf src, int off, int len) {
        return Base64.decode((ByteBuf)src, (int)off, (int)len, (Base64Dialect)Base64Dialect.STANDARD);
    }

    public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect) {
        return Base64.decode((ByteBuf)src, (int)off, (int)len, (Base64Dialect)dialect, (ByteBufAllocator)src.alloc());
    }

    public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect, ByteBufAllocator allocator) {
        if (src == null) {
            throw new NullPointerException((String)"src");
        }
        if (dialect != null) return new Decoder(null).decode((ByteBuf)src, (int)off, (int)len, (ByteBufAllocator)allocator, (Base64Dialect)dialect);
        throw new NullPointerException((String)"dialect");
    }

    static int decodedBufferSize(int len) {
        return len - (len >>> 2);
    }

    private Base64() {
    }

    static /* synthetic */ byte[] access$100(Base64Dialect x0) {
        return Base64.decodabet((Base64Dialect)x0);
    }
}

