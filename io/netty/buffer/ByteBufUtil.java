/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.WrappedByteBuf;
import io.netty.buffer.WrappedCompositeByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Locale;

public final class ByteBufUtil {
    private static final InternalLogger logger;
    private static final FastThreadLocal<byte[]> BYTE_ARRAYS;
    private static final byte WRITE_UTF_UNKNOWN = 63;
    private static final int MAX_CHAR_BUFFER_SIZE;
    private static final int THREAD_LOCAL_BUFFER_SIZE;
    private static final int MAX_BYTES_PER_CHAR_UTF8;
    static final int WRITE_CHUNK_SIZE = 8192;
    static final ByteBufAllocator DEFAULT_ALLOCATOR;
    static final int MAX_TL_ARRAY_LEN = 1024;
    private static final ByteProcessor FIND_NON_ASCII;

    static byte[] threadLocalTempArray(int minLength) {
        byte[] arrby;
        if (minLength <= 1024) {
            arrby = BYTE_ARRAYS.get();
            return arrby;
        }
        arrby = PlatformDependent.allocateUninitializedArray((int)minLength);
        return arrby;
    }

    public static String hexDump(ByteBuf buffer) {
        return ByteBufUtil.hexDump((ByteBuf)buffer, (int)buffer.readerIndex(), (int)buffer.readableBytes());
    }

    public static String hexDump(ByteBuf buffer, int fromIndex, int length) {
        return HexUtil.hexDump((ByteBuf)((ByteBuf)buffer), (int)((int)fromIndex), (int)((int)length));
    }

    public static String hexDump(byte[] array) {
        return ByteBufUtil.hexDump((byte[])array, (int)0, (int)array.length);
    }

    public static String hexDump(byte[] array, int fromIndex, int length) {
        return HexUtil.hexDump((byte[])((byte[])array), (int)((int)fromIndex), (int)((int)length));
    }

    public static byte decodeHexByte(CharSequence s, int pos) {
        return StringUtil.decodeHexByte((CharSequence)s, (int)pos);
    }

    public static byte[] decodeHexDump(CharSequence hexDump) {
        return StringUtil.decodeHexDump((CharSequence)hexDump, (int)0, (int)hexDump.length());
    }

    public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) {
        return StringUtil.decodeHexDump((CharSequence)hexDump, (int)fromIndex, (int)length);
    }

    public static boolean ensureWritableSuccess(int ensureWritableResult) {
        if (ensureWritableResult == 0) return true;
        if (ensureWritableResult == 2) return true;
        return false;
    }

    public static int hashCode(ByteBuf buffer) {
        int i;
        int aLen = buffer.readableBytes();
        int intCount = aLen >>> 2;
        int byteCount = aLen & 3;
        int hashCode = 1;
        int arrayIndex = buffer.readerIndex();
        if (buffer.order() == ByteOrder.BIG_ENDIAN) {
            for (i = intCount; i > 0; arrayIndex += 4, --i) {
                hashCode = 31 * hashCode + buffer.getInt((int)arrayIndex);
            }
        } else {
            for (i = intCount; i > 0; arrayIndex += 4, --i) {
                hashCode = 31 * hashCode + ByteBufUtil.swapInt((int)buffer.getInt((int)arrayIndex));
            }
        }
        i = byteCount;
        do {
            if (i <= 0) {
                if (hashCode != 0) return hashCode;
                return 1;
            }
            hashCode = 31 * hashCode + buffer.getByte((int)arrayIndex++);
            --i;
        } while (true);
    }

    public static int indexOf(ByteBuf needle, ByteBuf haystack) {
        int attempts = haystack.readableBytes() - needle.readableBytes() + 1;
        int i = 0;
        while (i < attempts) {
            if (ByteBufUtil.equals((ByteBuf)needle, (int)needle.readerIndex(), (ByteBuf)haystack, (int)(haystack.readerIndex() + i), (int)needle.readableBytes())) {
                return haystack.readerIndex() + i;
            }
            ++i;
        }
        return -1;
    }

    public static boolean equals(ByteBuf a, int aStartIndex, ByteBuf b, int bStartIndex, int length) {
        int i;
        if (aStartIndex < 0) throw new IllegalArgumentException((String)"All indexes and lengths must be non-negative");
        if (bStartIndex < 0) throw new IllegalArgumentException((String)"All indexes and lengths must be non-negative");
        if (length < 0) {
            throw new IllegalArgumentException((String)"All indexes and lengths must be non-negative");
        }
        if (a.writerIndex() - length < aStartIndex) return false;
        if (b.writerIndex() - length < bStartIndex) {
            return false;
        }
        int longCount = length >>> 3;
        int byteCount = length & 7;
        if (a.order() == b.order()) {
            for (i = longCount; i > 0; aStartIndex += 8, bStartIndex += 8, --i) {
                if (a.getLong((int)aStartIndex) == b.getLong((int)bStartIndex)) continue;
                return false;
            }
        } else {
            for (i = longCount; i > 0; aStartIndex += 8, bStartIndex += 8, --i) {
                if (a.getLong((int)aStartIndex) == ByteBufUtil.swapLong((long)b.getLong((int)bStartIndex))) continue;
                return false;
            }
        }
        i = byteCount;
        while (i > 0) {
            if (a.getByte((int)aStartIndex) != b.getByte((int)bStartIndex)) {
                return false;
            }
            ++aStartIndex;
            ++bStartIndex;
            --i;
        }
        return true;
    }

    public static boolean equals(ByteBuf bufferA, ByteBuf bufferB) {
        int aLen = bufferA.readableBytes();
        if (aLen == bufferB.readableBytes()) return ByteBufUtil.equals((ByteBuf)bufferA, (int)bufferA.readerIndex(), (ByteBuf)bufferB, (int)bufferB.readerIndex(), (int)aLen);
        return false;
    }

    public static int compare(ByteBuf bufferA, ByteBuf bufferB) {
        int aLen = bufferA.readableBytes();
        int bLen = bufferB.readableBytes();
        int minLength = Math.min((int)aLen, (int)bLen);
        int uintCount = minLength >>> 2;
        int byteCount = minLength & 3;
        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();
        if (uintCount > 0) {
            long res;
            boolean bufferAIsBigEndian = bufferA.order() == ByteOrder.BIG_ENDIAN;
            int uintCountIncrement = uintCount << 2;
            if (bufferA.order() == bufferB.order()) {
                res = bufferAIsBigEndian ? ByteBufUtil.compareUintBigEndian((ByteBuf)bufferA, (ByteBuf)bufferB, (int)aIndex, (int)bIndex, (int)uintCountIncrement) : ByteBufUtil.compareUintLittleEndian((ByteBuf)bufferA, (ByteBuf)bufferB, (int)aIndex, (int)bIndex, (int)uintCountIncrement);
            } else {
                long l = res = bufferAIsBigEndian ? ByteBufUtil.compareUintBigEndianA((ByteBuf)bufferA, (ByteBuf)bufferB, (int)aIndex, (int)bIndex, (int)uintCountIncrement) : ByteBufUtil.compareUintBigEndianB((ByteBuf)bufferA, (ByteBuf)bufferB, (int)aIndex, (int)bIndex, (int)uintCountIncrement);
            }
            if (res != 0L) {
                return (int)Math.min((long)Integer.MAX_VALUE, (long)Math.max((long)Integer.MIN_VALUE, (long)res));
            }
            aIndex += uintCountIncrement;
            bIndex += uintCountIncrement;
        }
        int aEnd = aIndex + byteCount;
        while (aIndex < aEnd) {
            int comp = bufferA.getUnsignedByte((int)aIndex) - bufferB.getUnsignedByte((int)bIndex);
            if (comp != 0) {
                return comp;
            }
            ++aIndex;
            ++bIndex;
        }
        return aLen - bLen;
    }

    private static long compareUintBigEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedInt((int)aIndex) - bufferB.getUnsignedInt((int)bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintLittleEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedIntLE((int)aIndex) - bufferB.getUnsignedIntLE((int)bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintBigEndianA(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedInt((int)aIndex) - bufferB.getUnsignedIntLE((int)bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintBigEndianB(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedIntLE((int)aIndex) - bufferB.getUnsignedInt((int)bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    public static int indexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if (fromIndex > toIndex) return ByteBufUtil.lastIndexOf((ByteBuf)buffer, (int)fromIndex, (int)toIndex, (byte)value);
        return ByteBufUtil.firstIndexOf((ByteBuf)buffer, (int)fromIndex, (int)toIndex, (byte)value);
    }

    public static short swapShort(short value) {
        return Short.reverseBytes((short)value);
    }

    public static int swapMedium(int value) {
        int swapped = value << 16 & 16711680 | value & 65280 | value >>> 16 & 255;
        if ((swapped & 8388608) == 0) return swapped;
        swapped |= -16777216;
        return swapped;
    }

    public static int swapInt(int value) {
        return Integer.reverseBytes((int)value);
    }

    public static long swapLong(long value) {
        return Long.reverseBytes((long)value);
    }

    public static ByteBuf writeShortBE(ByteBuf buf, int shortValue) {
        ByteBuf byteBuf;
        if (buf.order() == ByteOrder.BIG_ENDIAN) {
            byteBuf = buf.writeShort((int)shortValue);
            return byteBuf;
        }
        byteBuf = buf.writeShortLE((int)shortValue);
        return byteBuf;
    }

    public static ByteBuf setShortBE(ByteBuf buf, int index, int shortValue) {
        ByteBuf byteBuf;
        if (buf.order() == ByteOrder.BIG_ENDIAN) {
            byteBuf = buf.setShort((int)index, (int)shortValue);
            return byteBuf;
        }
        byteBuf = buf.setShortLE((int)index, (int)shortValue);
        return byteBuf;
    }

    public static ByteBuf writeMediumBE(ByteBuf buf, int mediumValue) {
        ByteBuf byteBuf;
        if (buf.order() == ByteOrder.BIG_ENDIAN) {
            byteBuf = buf.writeMedium((int)mediumValue);
            return byteBuf;
        }
        byteBuf = buf.writeMediumLE((int)mediumValue);
        return byteBuf;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuf readBytes(ByteBufAllocator alloc, ByteBuf buffer, int length) {
        boolean release = true;
        ByteBuf dst = alloc.buffer((int)length);
        try {
            buffer.readBytes((ByteBuf)dst);
            release = false;
            ByteBuf byteBuf = dst;
            return byteBuf;
        }
        finally {
            if (release) {
                dst.release();
            }
        }
    }

    private static int firstIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if ((fromIndex = Math.max((int)fromIndex, (int)0)) >= toIndex) return -1;
        if (buffer.capacity() != 0) return buffer.forEachByte((int)fromIndex, (int)(toIndex - fromIndex), (ByteProcessor)new ByteProcessor.IndexOfProcessor((byte)value));
        return -1;
    }

    private static int lastIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        int capacity = buffer.capacity();
        if ((fromIndex = Math.min((int)fromIndex, (int)capacity)) < 0) return -1;
        if (capacity != 0) return buffer.forEachByteDesc((int)toIndex, (int)(fromIndex - toIndex), (ByteProcessor)new ByteProcessor.IndexOfProcessor((byte)value));
        return -1;
    }

    private static CharSequence checkCharSequenceBounds(CharSequence seq, int start, int end) {
        if (!MathUtil.isOutOfBounds((int)start, (int)(end - start), (int)seq.length())) return seq;
        throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= end (" + end + ") <= seq.length(" + seq.length() + ')'));
    }

    public static ByteBuf writeUtf8(ByteBufAllocator alloc, CharSequence seq) {
        ByteBuf buf = alloc.buffer((int)ByteBufUtil.utf8MaxBytes((CharSequence)seq));
        ByteBufUtil.writeUtf8((ByteBuf)buf, (CharSequence)seq);
        return buf;
    }

    public static int writeUtf8(ByteBuf buf, CharSequence seq) {
        int seqLength = seq.length();
        return ByteBufUtil.reserveAndWriteUtf8Seq((ByteBuf)buf, (CharSequence)seq, (int)0, (int)seqLength, (int)ByteBufUtil.utf8MaxBytes((int)seqLength));
    }

    public static int writeUtf8(ByteBuf buf, CharSequence seq, int start, int end) {
        ByteBufUtil.checkCharSequenceBounds((CharSequence)seq, (int)start, (int)end);
        return ByteBufUtil.reserveAndWriteUtf8Seq((ByteBuf)buf, (CharSequence)seq, (int)start, (int)end, (int)ByteBufUtil.utf8MaxBytes((int)(end - start)));
    }

    public static int reserveAndWriteUtf8(ByteBuf buf, CharSequence seq, int reserveBytes) {
        return ByteBufUtil.reserveAndWriteUtf8Seq((ByteBuf)buf, (CharSequence)seq, (int)0, (int)seq.length(), (int)reserveBytes);
    }

    public static int reserveAndWriteUtf8(ByteBuf buf, CharSequence seq, int start, int end, int reserveBytes) {
        return ByteBufUtil.reserveAndWriteUtf8Seq((ByteBuf)buf, (CharSequence)ByteBufUtil.checkCharSequenceBounds((CharSequence)seq, (int)start, (int)end), (int)start, (int)end, (int)reserveBytes);
    }

    private static int reserveAndWriteUtf8Seq(ByteBuf buf, CharSequence seq, int start, int end, int reserveBytes) {
        do {
            if (buf instanceof WrappedCompositeByteBuf) {
                buf = buf.unwrap();
                continue;
            }
            if (buf instanceof AbstractByteBuf) {
                AbstractByteBuf byteBuf = (AbstractByteBuf)buf;
                byteBuf.ensureWritable0((int)reserveBytes);
                int written = ByteBufUtil.writeUtf8((AbstractByteBuf)byteBuf, (int)byteBuf.writerIndex, (CharSequence)seq, (int)start, (int)end);
                byteBuf.writerIndex += written;
                return written;
            }
            if (!(buf instanceof WrappedByteBuf)) {
                byte[] bytes = seq.subSequence((int)start, (int)end).toString().getBytes((Charset)CharsetUtil.UTF_8);
                buf.writeBytes((byte[])bytes);
                return bytes.length;
            }
            buf = buf.unwrap();
        } while (true);
    }

    static int writeUtf8(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) {
        return ByteBufUtil.writeUtf8((AbstractByteBuf)buffer, (int)writerIndex, (CharSequence)seq, (int)0, (int)len);
    }

    static int writeUtf8(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int start, int end) {
        int oldWriterIndex = writerIndex;
        int i = start;
        while (i < end) {
            char c = seq.charAt((int)i);
            if (c < '?') {
                buffer._setByte((int)writerIndex++, (int)((byte)c));
            } else if (c < '\u0800') {
                buffer._setByte((int)writerIndex++, (int)((byte)(192 | c >> 6)));
                buffer._setByte((int)writerIndex++, (int)((byte)(128 | c & 63)));
            } else if (StringUtil.isSurrogate((char)c)) {
                if (!Character.isHighSurrogate((char)c)) {
                    buffer._setByte((int)writerIndex++, (int)63);
                } else {
                    if (++i == end) {
                        buffer._setByte((int)writerIndex++, (int)63);
                        return writerIndex - oldWriterIndex;
                    }
                    writerIndex = ByteBufUtil.writeUtf8Surrogate((AbstractByteBuf)buffer, (int)writerIndex, (char)c, (char)seq.charAt((int)i));
                }
            } else {
                buffer._setByte((int)writerIndex++, (int)((byte)(224 | c >> 12)));
                buffer._setByte((int)writerIndex++, (int)((byte)(128 | c >> 6 & 63)));
                buffer._setByte((int)writerIndex++, (int)((byte)(128 | c & 63)));
            }
            ++i;
        }
        return writerIndex - oldWriterIndex;
    }

    private static int writeUtf8Surrogate(AbstractByteBuf buffer, int writerIndex, char c, char c2) {
        if (Character.isLowSurrogate((char)c2)) {
            int codePoint = Character.toCodePoint((char)c, (char)c2);
            buffer._setByte((int)writerIndex++, (int)((byte)(240 | codePoint >> 18)));
            buffer._setByte((int)writerIndex++, (int)((byte)(128 | codePoint >> 12 & 63)));
            buffer._setByte((int)writerIndex++, (int)((byte)(128 | codePoint >> 6 & 63)));
            buffer._setByte((int)writerIndex++, (int)((byte)(128 | codePoint & 63)));
            return writerIndex;
        }
        buffer._setByte((int)writerIndex++, (int)63);
        buffer._setByte((int)writerIndex++, (int)(Character.isHighSurrogate((char)c2) ? 63 : (int)c2));
        return writerIndex;
    }

    public static int utf8MaxBytes(int seqLength) {
        return seqLength * MAX_BYTES_PER_CHAR_UTF8;
    }

    public static int utf8MaxBytes(CharSequence seq) {
        return ByteBufUtil.utf8MaxBytes((int)seq.length());
    }

    public static int utf8Bytes(CharSequence seq) {
        return ByteBufUtil.utf8ByteCount((CharSequence)seq, (int)0, (int)seq.length());
    }

    public static int utf8Bytes(CharSequence seq, int start, int end) {
        return ByteBufUtil.utf8ByteCount((CharSequence)ByteBufUtil.checkCharSequenceBounds((CharSequence)seq, (int)start, (int)end), (int)start, (int)end);
    }

    private static int utf8ByteCount(CharSequence seq, int start, int end) {
        int n;
        int i;
        if (seq instanceof AsciiString) {
            return end - start;
        }
        for (i = start; i < end && seq.charAt((int)i) < '?'; ++i) {
        }
        if (i < end) {
            n = i - start + ByteBufUtil.utf8BytesNonAscii((CharSequence)seq, (int)i, (int)end);
            return n;
        }
        n = i - start;
        return n;
    }

    private static int utf8BytesNonAscii(CharSequence seq, int start, int end) {
        int encodedLength = 0;
        int i = start;
        while (i < end) {
            char c = seq.charAt((int)i);
            if (c < '\u0800') {
                encodedLength += (127 - c >>> 31) + 1;
            } else if (StringUtil.isSurrogate((char)c)) {
                if (!Character.isHighSurrogate((char)c)) {
                    ++encodedLength;
                } else {
                    if (++i == end) {
                        return ++encodedLength;
                    }
                    encodedLength = !Character.isLowSurrogate((char)seq.charAt((int)i)) ? (encodedLength += 2) : (encodedLength += 4);
                }
            } else {
                encodedLength += 3;
            }
            ++i;
        }
        return encodedLength;
    }

    public static ByteBuf writeAscii(ByteBufAllocator alloc, CharSequence seq) {
        ByteBuf buf = alloc.buffer((int)seq.length());
        ByteBufUtil.writeAscii((ByteBuf)buf, (CharSequence)seq);
        return buf;
    }

    public static int writeAscii(ByteBuf buf, CharSequence seq) {
        int len = seq.length();
        if (seq instanceof AsciiString) {
            AsciiString asciiString = (AsciiString)seq;
            buf.writeBytes((byte[])asciiString.array(), (int)asciiString.arrayOffset(), (int)len);
            return len;
        }
        do {
            if (buf instanceof WrappedCompositeByteBuf) {
                buf = buf.unwrap();
                continue;
            }
            if (buf instanceof AbstractByteBuf) {
                AbstractByteBuf byteBuf = (AbstractByteBuf)buf;
                byteBuf.ensureWritable0((int)len);
                int written = ByteBufUtil.writeAscii((AbstractByteBuf)byteBuf, (int)byteBuf.writerIndex, (CharSequence)seq, (int)len);
                byteBuf.writerIndex += written;
                return written;
            }
            if (!(buf instanceof WrappedByteBuf)) {
                byte[] bytes = seq.toString().getBytes((Charset)CharsetUtil.US_ASCII);
                buf.writeBytes((byte[])bytes);
                return bytes.length;
            }
            buf = buf.unwrap();
        } while (true);
    }

    static int writeAscii(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) {
        int i = 0;
        while (i < len) {
            buffer._setByte((int)writerIndex++, (int)AsciiString.c2b((char)seq.charAt((int)i)));
            ++i;
        }
        return len;
    }

    public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset) {
        return ByteBufUtil.encodeString0((ByteBufAllocator)alloc, (boolean)false, (CharBuffer)src, (Charset)charset, (int)0);
    }

    public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset, int extraCapacity) {
        return ByteBufUtil.encodeString0((ByteBufAllocator)alloc, (boolean)false, (CharBuffer)src, (Charset)charset, (int)extraCapacity);
    }

    static ByteBuf encodeString0(ByteBufAllocator alloc, boolean enforceHeap, CharBuffer src, Charset charset, int extraCapacity) {
        CharsetEncoder encoder = CharsetUtil.encoder((Charset)charset);
        int length = (int)((double)src.remaining() * (double)encoder.maxBytesPerChar()) + extraCapacity;
        boolean release = true;
        ByteBuf dst = enforceHeap ? alloc.heapBuffer((int)length) : alloc.buffer((int)length);
        try {
            ByteBuffer dstBuf = dst.internalNioBuffer((int)dst.readerIndex(), (int)length);
            int pos = dstBuf.position();
            CoderResult cr = encoder.encode((CharBuffer)src, (ByteBuffer)dstBuf, (boolean)true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            if (!(cr = encoder.flush((ByteBuffer)dstBuf)).isUnderflow()) {
                cr.throwException();
            }
            dst.writerIndex((int)(dst.writerIndex() + dstBuf.position() - pos));
            release = false;
            ByteBuf byteBuf = dst;
            return byteBuf;
        }
        catch (CharacterCodingException x) {
            throw new IllegalStateException((Throwable)x);
        }
        finally {
            if (release) {
                dst.release();
            }
        }
    }

    static String decodeString(ByteBuf src, int readerIndex, int len, Charset charset) {
        int offset;
        byte[] array;
        if (len == 0) {
            return "";
        }
        if (src.hasArray()) {
            array = src.array();
            offset = src.arrayOffset() + readerIndex;
        } else {
            array = ByteBufUtil.threadLocalTempArray((int)len);
            offset = 0;
            src.getBytes((int)readerIndex, (byte[])array, (int)0, (int)len);
        }
        if (!CharsetUtil.US_ASCII.equals((Object)charset)) return new String((byte[])array, (int)offset, (int)len, (Charset)charset);
        return new String((byte[])array, (int)0, (int)offset, (int)len);
    }

    public static ByteBuf threadLocalDirectBuffer() {
        if (THREAD_LOCAL_BUFFER_SIZE <= 0) {
            return null;
        }
        if (!PlatformDependent.hasUnsafe()) return ThreadLocalDirectByteBuf.newInstance();
        return ThreadLocalUnsafeDirectByteBuf.newInstance();
    }

    public static byte[] getBytes(ByteBuf buf) {
        return ByteBufUtil.getBytes((ByteBuf)buf, (int)buf.readerIndex(), (int)buf.readableBytes());
    }

    public static byte[] getBytes(ByteBuf buf, int start, int length) {
        return ByteBufUtil.getBytes((ByteBuf)buf, (int)start, (int)length, (boolean)true);
    }

    public static byte[] getBytes(ByteBuf buf, int start, int length, boolean copy) {
        int capacity = buf.capacity();
        if (MathUtil.isOutOfBounds((int)start, (int)length, (int)capacity)) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= buf.capacity(" + capacity + ')'));
        }
        if (!buf.hasArray()) {
            byte[] v = PlatformDependent.allocateUninitializedArray((int)length);
            buf.getBytes((int)start, (byte[])v);
            return v;
        }
        if (!copy && start == 0) {
            if (length == capacity) return buf.array();
        }
        int baseOffset = buf.arrayOffset() + start;
        return Arrays.copyOfRange((byte[])buf.array(), (int)baseOffset, (int)(baseOffset + length));
    }

    public static void copy(AsciiString src, ByteBuf dst) {
        ByteBufUtil.copy((AsciiString)src, (int)0, (ByteBuf)dst, (int)src.length());
    }

    public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int dstIdx, int length) {
        if (MathUtil.isOutOfBounds((int)srcIdx, (int)length, (int)src.length())) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')'));
        }
        ObjectUtil.checkNotNull(dst, (String)"dst").setBytes((int)dstIdx, (byte[])src.array(), (int)(srcIdx + src.arrayOffset()), (int)length);
    }

    public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int length) {
        if (MathUtil.isOutOfBounds((int)srcIdx, (int)length, (int)src.length())) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')'));
        }
        ObjectUtil.checkNotNull(dst, (String)"dst").writeBytes((byte[])src.array(), (int)(srcIdx + src.arrayOffset()), (int)length);
    }

    public static String prettyHexDump(ByteBuf buffer) {
        return ByteBufUtil.prettyHexDump((ByteBuf)buffer, (int)buffer.readerIndex(), (int)buffer.readableBytes());
    }

    public static String prettyHexDump(ByteBuf buffer, int offset, int length) {
        return HexUtil.prettyHexDump((ByteBuf)((ByteBuf)buffer), (int)((int)offset), (int)((int)length));
    }

    public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf) {
        ByteBufUtil.appendPrettyHexDump((StringBuilder)dump, (ByteBuf)buf, (int)buf.readerIndex(), (int)buf.readableBytes());
    }

    public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf, int offset, int length) {
        HexUtil.appendPrettyHexDump((StringBuilder)((StringBuilder)dump), (ByteBuf)((ByteBuf)buf), (int)((int)offset), (int)((int)length));
    }

    public static boolean isText(ByteBuf buf, Charset charset) {
        return ByteBufUtil.isText((ByteBuf)buf, (int)buf.readerIndex(), (int)buf.readableBytes(), (Charset)charset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isText(ByteBuf buf, int index, int length, Charset charset) {
        ObjectUtil.checkNotNull(buf, (String)"buf");
        ObjectUtil.checkNotNull(charset, (String)"charset");
        int maxIndex = buf.readerIndex() + buf.readableBytes();
        if (index < 0) throw new IndexOutOfBoundsException((String)("index: " + index + " length: " + length));
        if (length < 0) throw new IndexOutOfBoundsException((String)("index: " + index + " length: " + length));
        if (index > maxIndex - length) {
            throw new IndexOutOfBoundsException((String)("index: " + index + " length: " + length));
        }
        if (charset.equals((Object)CharsetUtil.UTF_8)) {
            return ByteBufUtil.isUtf8((ByteBuf)buf, (int)index, (int)length);
        }
        if (charset.equals((Object)CharsetUtil.US_ASCII)) {
            return ByteBufUtil.isAscii((ByteBuf)buf, (int)index, (int)length);
        }
        CharsetDecoder decoder = CharsetUtil.decoder((Charset)charset, (CodingErrorAction)CodingErrorAction.REPORT, (CodingErrorAction)CodingErrorAction.REPORT);
        try {
            if (buf.nioBufferCount() == 1) {
                decoder.decode((ByteBuffer)buf.nioBuffer((int)index, (int)length));
                return true;
            }
            ByteBuf heapBuffer = buf.alloc().heapBuffer((int)length);
            try {
                heapBuffer.writeBytes((ByteBuf)buf, (int)index, (int)length);
                decoder.decode((ByteBuffer)heapBuffer.internalNioBuffer((int)heapBuffer.readerIndex(), (int)length));
                return true;
            }
            finally {
                heapBuffer.release();
            }
        }
        catch (CharacterCodingException ignore) {
            return false;
        }
    }

    private static boolean isAscii(ByteBuf buf, int index, int length) {
        if (buf.forEachByte((int)index, (int)length, (ByteProcessor)FIND_NON_ASCII) != -1) return false;
        return true;
    }

    /*
     * Unable to fully structure code
     */
    private static boolean isUtf8(ByteBuf buf, int index, int length) {
        endIndex = index + length;
        do lbl-1000: // 5 sources:
        {
            block9 : {
                block8 : {
                    if (index >= endIndex) return true;
                    if (((b1 = buf.getByte((int)index++)) & 128) == 0) ** GOTO lbl-1000
                    if ((b1 & 224) != 192) break block8;
                    if (index >= endIndex) {
                        return false;
                    }
                    if (((b2 = buf.getByte((int)index++)) & 192) != 128) {
                        return false;
                    }
                    if ((b1 & 255) >= 194) ** GOTO lbl-1000
                    return false;
                }
                if ((b1 & 240) != 224) break block9;
                if (index > endIndex - 2) {
                    return false;
                }
                b2 = buf.getByte((int)index++);
                b3 = buf.getByte((int)index++);
                if ((b2 & 192) != 128) return false;
                if ((b3 & 192) != 128) {
                    return false;
                }
                if ((b1 & 15) == 0 && (b2 & 255) < 160) {
                    return false;
                }
                if ((b1 & 15) != 13 || (b2 & 255) <= 159) ** GOTO lbl-1000
                return false;
            }
            if ((b1 & 248) != 240) return false;
            if (index > endIndex - 3) {
                return false;
            }
            b2 = buf.getByte((int)index++);
            b3 = buf.getByte((int)index++);
            b4 = buf.getByte((int)index++);
            if ((b2 & 192) != 128) return false;
            if ((b3 & 192) != 128) return false;
            if ((b4 & 192) != 128) {
                return false;
            }
            if ((b1 & 255) > 244) return false;
            if ((b1 & 255) != 240) continue;
            if ((b2 & 255) < 144) return false;
        } while ((b1 & 255) != 244 || (b2 & 255) <= 143);
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void readBytes(ByteBufAllocator allocator, ByteBuffer buffer, int position, int length, OutputStream out) throws IOException {
        if (buffer.hasArray()) {
            out.write((byte[])buffer.array(), (int)(position + buffer.arrayOffset()), (int)length);
            return;
        }
        int chunkLen = Math.min((int)length, (int)8192);
        buffer.clear().position((int)position);
        if (length <= 1024 || !allocator.isDirectBufferPooled()) {
            ByteBufUtil.getBytes((ByteBuffer)buffer, (byte[])ByteBufUtil.threadLocalTempArray((int)chunkLen), (int)0, (int)chunkLen, (OutputStream)out, (int)length);
            return;
        }
        ByteBuf tmpBuf = allocator.heapBuffer((int)chunkLen);
        try {
            byte[] tmp = tmpBuf.array();
            int offset = tmpBuf.arrayOffset();
            ByteBufUtil.getBytes((ByteBuffer)buffer, (byte[])tmp, (int)offset, (int)chunkLen, (OutputStream)out, (int)length);
            return;
        }
        finally {
            tmpBuf.release();
        }
    }

    private static void getBytes(ByteBuffer inBuffer, byte[] in, int inOffset, int inLen, OutputStream out, int outLen) throws IOException {
        int len;
        do {
            len = Math.min((int)inLen, (int)outLen);
            inBuffer.get((byte[])in, (int)inOffset, (int)len);
            out.write((byte[])in, (int)inOffset, (int)len);
        } while ((outLen -= len) > 0);
    }

    private ByteBufUtil() {
    }

    static /* synthetic */ int access$500() {
        return THREAD_LOCAL_BUFFER_SIZE;
    }

    static {
        AbstractByteBufAllocator alloc;
        logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
        BYTE_ARRAYS = new FastThreadLocal<byte[]>(){

            protected byte[] initialValue() throws java.lang.Exception {
                return PlatformDependent.allocateUninitializedArray((int)1024);
            }
        };
        MAX_BYTES_PER_CHAR_UTF8 = (int)CharsetUtil.encoder((Charset)CharsetUtil.UTF_8).maxBytesPerChar();
        String allocType = SystemPropertyUtil.get((String)"io.netty.allocator.type", (String)(PlatformDependent.isAndroid() ? "unpooled" : "pooled"));
        if ("unpooled".equals((Object)(allocType = allocType.toLowerCase((Locale)Locale.US).trim()))) {
            alloc = UnpooledByteBufAllocator.DEFAULT;
            logger.debug((String)"-Dio.netty.allocator.type: {}", (Object)allocType);
        } else if ("pooled".equals((Object)allocType)) {
            alloc = PooledByteBufAllocator.DEFAULT;
            logger.debug((String)"-Dio.netty.allocator.type: {}", (Object)allocType);
        } else {
            alloc = PooledByteBufAllocator.DEFAULT;
            logger.debug((String)"-Dio.netty.allocator.type: pooled (unknown: {})", (Object)allocType);
        }
        DEFAULT_ALLOCATOR = alloc;
        THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt((String)"io.netty.threadLocalDirectBufferSize", (int)0);
        logger.debug((String)"-Dio.netty.threadLocalDirectBufferSize: {}", (Object)Integer.valueOf((int)THREAD_LOCAL_BUFFER_SIZE));
        MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt((String)"io.netty.maxThreadLocalCharBufferSize", (int)16384);
        logger.debug((String)"-Dio.netty.maxThreadLocalCharBufferSize: {}", (Object)Integer.valueOf((int)MAX_CHAR_BUFFER_SIZE));
        FIND_NON_ASCII = new ByteProcessor(){

            public boolean process(byte value) {
                if (value < 0) return false;
                return true;
            }
        };
    }
}

