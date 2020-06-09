/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.FixedCompositeByteBuf;
import io.netty.buffer.ReadOnlyByteBuf;
import io.netty.buffer.ReadOnlyByteBufferBuf;
import io.netty.buffer.ReadOnlyUnsafeDirectByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.buffer.UnreleasableByteBuf;
import io.netty.buffer.WrappedUnpooledUnsafeDirectByteBuf;
import io.netty.util.internal.PlatformDependent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public final class Unpooled {
    private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;
    public static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
    public static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    public static final ByteBuf EMPTY_BUFFER = ALLOC.buffer((int)0, (int)0);

    public static ByteBuf buffer() {
        return ALLOC.heapBuffer();
    }

    public static ByteBuf directBuffer() {
        return ALLOC.directBuffer();
    }

    public static ByteBuf buffer(int initialCapacity) {
        return ALLOC.heapBuffer((int)initialCapacity);
    }

    public static ByteBuf directBuffer(int initialCapacity) {
        return ALLOC.directBuffer((int)initialCapacity);
    }

    public static ByteBuf buffer(int initialCapacity, int maxCapacity) {
        return ALLOC.heapBuffer((int)initialCapacity, (int)maxCapacity);
    }

    public static ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
        return ALLOC.directBuffer((int)initialCapacity, (int)maxCapacity);
    }

    public static ByteBuf wrappedBuffer(byte[] array) {
        if (array.length != 0) return new UnpooledHeapByteBuf((ByteBufAllocator)ALLOC, (byte[])array, (int)array.length);
        return EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(byte[] array, int offset, int length) {
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        if (offset != 0) return Unpooled.wrappedBuffer((byte[])array).slice((int)offset, (int)length);
        if (length != array.length) return Unpooled.wrappedBuffer((byte[])array).slice((int)offset, (int)length);
        return Unpooled.wrappedBuffer((byte[])array);
    }

    public static ByteBuf wrappedBuffer(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return EMPTY_BUFFER;
        }
        if (!buffer.isDirect() && buffer.hasArray()) {
            return Unpooled.wrappedBuffer((byte[])buffer.array(), (int)(buffer.arrayOffset() + buffer.position()), (int)buffer.remaining()).order((ByteOrder)buffer.order());
        }
        if (PlatformDependent.hasUnsafe()) {
            if (!buffer.isReadOnly()) return new UnpooledUnsafeDirectByteBuf((ByteBufAllocator)ALLOC, (ByteBuffer)buffer, (int)buffer.remaining());
            if (!buffer.isDirect()) return new ReadOnlyByteBufferBuf((ByteBufAllocator)ALLOC, (ByteBuffer)buffer);
            return new ReadOnlyUnsafeDirectByteBuf((ByteBufAllocator)ALLOC, (ByteBuffer)buffer);
        }
        if (!buffer.isReadOnly()) return new UnpooledDirectByteBuf((ByteBufAllocator)ALLOC, (ByteBuffer)buffer, (int)buffer.remaining());
        return new ReadOnlyByteBufferBuf((ByteBufAllocator)ALLOC, (ByteBuffer)buffer);
    }

    public static ByteBuf wrappedBuffer(long memoryAddress, int size, boolean doFree) {
        return new WrappedUnpooledUnsafeDirectByteBuf((ByteBufAllocator)ALLOC, (long)memoryAddress, (int)size, (boolean)doFree);
    }

    public static ByteBuf wrappedBuffer(ByteBuf buffer) {
        if (buffer.isReadable()) {
            return buffer.slice();
        }
        buffer.release();
        return EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(byte[] ... arrays) {
        return Unpooled.wrappedBuffer((int)arrays.length, (byte[][])arrays);
    }

    public static ByteBuf wrappedBuffer(ByteBuf ... buffers) {
        return Unpooled.wrappedBuffer((int)buffers.length, (ByteBuf[])buffers);
    }

    public static ByteBuf wrappedBuffer(ByteBuffer ... buffers) {
        return Unpooled.wrappedBuffer((int)buffers.length, (ByteBuffer[])buffers);
    }

    static <T> ByteBuf wrappedBuffer(int maxNumComponents, CompositeByteBuf.ByteWrapper<T> wrapper, T[] array) {
        switch (array.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                if (wrapper.isEmpty(array[0])) return EMPTY_BUFFER;
                return wrapper.wrap(array[0]);
            }
        }
        int i = 0;
        int len = array.length;
        while (i < len) {
            T bytes = array[i];
            if (bytes == null) {
                return EMPTY_BUFFER;
            }
            if (!wrapper.isEmpty(bytes)) {
                return new CompositeByteBuf((ByteBufAllocator)ALLOC, (boolean)false, (int)maxNumComponents, wrapper, array, (int)i);
            }
            ++i;
        }
        return EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(int maxNumComponents, byte[] ... arrays) {
        return Unpooled.wrappedBuffer((int)maxNumComponents, CompositeByteBuf.BYTE_ARRAY_WRAPPER, arrays);
    }

    public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuf ... buffers) {
        switch (buffers.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                ByteBuf buffer = buffers[0];
                if (buffer.isReadable()) {
                    return Unpooled.wrappedBuffer((ByteBuf)buffer.order((ByteOrder)BIG_ENDIAN));
                }
                buffer.release();
                return EMPTY_BUFFER;
            }
        }
        int i = 0;
        while (i < buffers.length) {
            ByteBuf buf = buffers[i];
            if (buf.isReadable()) {
                return new CompositeByteBuf((ByteBufAllocator)ALLOC, (boolean)false, (int)maxNumComponents, (ByteBuf[])buffers, (int)i);
            }
            buf.release();
            ++i;
        }
        return EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuffer ... buffers) {
        return Unpooled.wrappedBuffer((int)maxNumComponents, CompositeByteBuf.BYTE_BUFFER_WRAPPER, buffers);
    }

    public static CompositeByteBuf compositeBuffer() {
        return Unpooled.compositeBuffer((int)16);
    }

    public static CompositeByteBuf compositeBuffer(int maxNumComponents) {
        return new CompositeByteBuf((ByteBufAllocator)ALLOC, (boolean)false, (int)maxNumComponents);
    }

    public static ByteBuf copiedBuffer(byte[] array) {
        if (array.length != 0) return Unpooled.wrappedBuffer((byte[])((byte[])array.clone()));
        return EMPTY_BUFFER;
    }

    public static ByteBuf copiedBuffer(byte[] array, int offset, int length) {
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] copy = PlatformDependent.allocateUninitializedArray((int)length);
        System.arraycopy((Object)array, (int)offset, (Object)copy, (int)0, (int)length);
        return Unpooled.wrappedBuffer((byte[])copy);
    }

    public static ByteBuf copiedBuffer(ByteBuffer buffer) {
        int length = buffer.remaining();
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] copy = PlatformDependent.allocateUninitializedArray((int)length);
        ByteBuffer duplicate = buffer.duplicate();
        duplicate.get((byte[])copy);
        return Unpooled.wrappedBuffer((byte[])copy).order((ByteOrder)duplicate.order());
    }

    public static ByteBuf copiedBuffer(ByteBuf buffer) {
        int readable = buffer.readableBytes();
        if (readable <= 0) return EMPTY_BUFFER;
        ByteBuf copy = Unpooled.buffer((int)readable);
        copy.writeBytes((ByteBuf)buffer, (int)buffer.readerIndex(), (int)readable);
        return copy;
    }

    public static ByteBuf copiedBuffer(byte[] ... arrays) {
        byte[] a;
        switch (arrays.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                if (arrays[0].length != 0) return Unpooled.copiedBuffer((byte[])arrays[0]);
                return EMPTY_BUFFER;
            }
        }
        int length = 0;
        byte[][] arrby = arrays;
        int n = arrby.length;
        for (int i = 0; i < n; length += a.length, ++i) {
            a = arrby[i];
            if (Integer.MAX_VALUE - length >= a.length) continue;
            throw new IllegalArgumentException((String)"The total length of the specified arrays is too big.");
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] mergedArray = PlatformDependent.allocateUninitializedArray((int)length);
        int i = 0;
        int j = 0;
        while (i < arrays.length) {
            a = arrays[i];
            System.arraycopy((Object)a, (int)0, (Object)mergedArray, (int)j, (int)a.length);
            j += a.length;
            ++i;
        }
        return Unpooled.wrappedBuffer((byte[])mergedArray);
    }

    public static ByteBuf copiedBuffer(ByteBuf ... buffers) {
        int bLen;
        switch (buffers.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                return Unpooled.copiedBuffer((ByteBuf)buffers[0]);
            }
        }
        ByteOrder order = null;
        int length = 0;
        for (ByteBuf b : buffers) {
            bLen = b.readableBytes();
            if (bLen <= 0) continue;
            if (Integer.MAX_VALUE - length < bLen) {
                throw new IllegalArgumentException((String)"The total length of the specified buffers is too big.");
            }
            length += bLen;
            if (order != null) {
                if (order.equals((Object)b.order())) continue;
                throw new IllegalArgumentException((String)"inconsistent byte order");
            }
            order = b.order();
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] mergedArray = PlatformDependent.allocateUninitializedArray((int)length);
        int i = 0;
        int j = 0;
        while (i < buffers.length) {
            ByteBuf b;
            b = buffers[i];
            bLen = b.readableBytes();
            b.getBytes((int)b.readerIndex(), (byte[])mergedArray, (int)j, (int)bLen);
            j += bLen;
            ++i;
        }
        return Unpooled.wrappedBuffer((byte[])mergedArray).order(order);
    }

    public static ByteBuf copiedBuffer(ByteBuffer ... buffers) {
        int bLen;
        switch (buffers.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                return Unpooled.copiedBuffer((ByteBuffer)buffers[0]);
            }
        }
        ByteOrder order = null;
        int length = 0;
        for (ByteBuffer b : buffers) {
            bLen = b.remaining();
            if (bLen <= 0) continue;
            if (Integer.MAX_VALUE - length < bLen) {
                throw new IllegalArgumentException((String)"The total length of the specified buffers is too big.");
            }
            length += bLen;
            if (order != null) {
                if (order.equals((Object)b.order())) continue;
                throw new IllegalArgumentException((String)"inconsistent byte order");
            }
            order = b.order();
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] mergedArray = PlatformDependent.allocateUninitializedArray((int)length);
        int i = 0;
        int j = 0;
        while (i < buffers.length) {
            ByteBuffer b;
            b = buffers[i].duplicate();
            bLen = b.remaining();
            b.get((byte[])mergedArray, (int)j, (int)bLen);
            j += bLen;
            ++i;
        }
        return Unpooled.wrappedBuffer((byte[])mergedArray).order(order);
    }

    public static ByteBuf copiedBuffer(CharSequence string, Charset charset) {
        if (string == null) {
            throw new NullPointerException((String)"string");
        }
        if (!(string instanceof CharBuffer)) return Unpooled.copiedBuffer((CharBuffer)CharBuffer.wrap((CharSequence)string), (Charset)charset);
        return Unpooled.copiedBuffer((CharBuffer)((CharBuffer)string), (Charset)charset);
    }

    public static ByteBuf copiedBuffer(CharSequence string, int offset, int length, Charset charset) {
        if (string == null) {
            throw new NullPointerException((String)"string");
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        if (!(string instanceof CharBuffer)) return Unpooled.copiedBuffer((CharBuffer)CharBuffer.wrap((CharSequence)string, (int)offset, (int)(offset + length)), (Charset)charset);
        CharBuffer buf = (CharBuffer)string;
        if (buf.hasArray()) {
            return Unpooled.copiedBuffer((char[])buf.array(), (int)(buf.arrayOffset() + buf.position() + offset), (int)length, (Charset)charset);
        }
        buf = buf.slice();
        buf.limit((int)length);
        buf.position((int)offset);
        return Unpooled.copiedBuffer((CharBuffer)buf, (Charset)charset);
    }

    public static ByteBuf copiedBuffer(char[] array, Charset charset) {
        if (array != null) return Unpooled.copiedBuffer((char[])array, (int)0, (int)array.length, (Charset)charset);
        throw new NullPointerException((String)"array");
    }

    public static ByteBuf copiedBuffer(char[] array, int offset, int length, Charset charset) {
        if (array == null) {
            throw new NullPointerException((String)"array");
        }
        if (length != 0) return Unpooled.copiedBuffer((CharBuffer)CharBuffer.wrap((char[])array, (int)offset, (int)length), (Charset)charset);
        return EMPTY_BUFFER;
    }

    private static ByteBuf copiedBuffer(CharBuffer buffer, Charset charset) {
        return ByteBufUtil.encodeString0((ByteBufAllocator)ALLOC, (boolean)true, (CharBuffer)buffer, (Charset)charset, (int)0);
    }

    @Deprecated
    public static ByteBuf unmodifiableBuffer(ByteBuf buffer) {
        ByteOrder endianness = buffer.order();
        if (endianness != BIG_ENDIAN) return new ReadOnlyByteBuf((ByteBuf)buffer.order((ByteOrder)BIG_ENDIAN)).order((ByteOrder)LITTLE_ENDIAN);
        return new ReadOnlyByteBuf((ByteBuf)buffer);
    }

    public static ByteBuf copyInt(int value) {
        ByteBuf buf = Unpooled.buffer((int)4);
        buf.writeInt((int)value);
        return buf;
    }

    public static ByteBuf copyInt(int ... values) {
        if (values == null) return EMPTY_BUFFER;
        if (values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer((int)(values.length * 4));
        int[] arrn = values;
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            int v = arrn[n2];
            buffer.writeInt((int)v);
            ++n2;
        }
        return buffer;
    }

    public static ByteBuf copyShort(int value) {
        ByteBuf buf = Unpooled.buffer((int)2);
        buf.writeShort((int)value);
        return buf;
    }

    public static ByteBuf copyShort(short ... values) {
        if (values == null) return EMPTY_BUFFER;
        if (values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer((int)(values.length * 2));
        short[] arrs = values;
        int n = arrs.length;
        int n2 = 0;
        while (n2 < n) {
            short v = arrs[n2];
            buffer.writeShort((int)v);
            ++n2;
        }
        return buffer;
    }

    public static ByteBuf copyShort(int ... values) {
        if (values == null) return EMPTY_BUFFER;
        if (values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer((int)(values.length * 2));
        int[] arrn = values;
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            int v = arrn[n2];
            buffer.writeShort((int)v);
            ++n2;
        }
        return buffer;
    }

    public static ByteBuf copyMedium(int value) {
        ByteBuf buf = Unpooled.buffer((int)3);
        buf.writeMedium((int)value);
        return buf;
    }

    public static ByteBuf copyMedium(int ... values) {
        if (values == null) return EMPTY_BUFFER;
        if (values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer((int)(values.length * 3));
        int[] arrn = values;
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            int v = arrn[n2];
            buffer.writeMedium((int)v);
            ++n2;
        }
        return buffer;
    }

    public static ByteBuf copyLong(long value) {
        ByteBuf buf = Unpooled.buffer((int)8);
        buf.writeLong((long)value);
        return buf;
    }

    public static ByteBuf copyLong(long ... values) {
        if (values == null) return EMPTY_BUFFER;
        if (values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer((int)(values.length * 8));
        long[] arrl = values;
        int n = arrl.length;
        int n2 = 0;
        while (n2 < n) {
            long v = arrl[n2];
            buffer.writeLong((long)v);
            ++n2;
        }
        return buffer;
    }

    public static ByteBuf copyBoolean(boolean value) {
        ByteBuf buf = Unpooled.buffer((int)1);
        buf.writeBoolean((boolean)value);
        return buf;
    }

    public static ByteBuf copyBoolean(boolean ... values) {
        if (values == null) return EMPTY_BUFFER;
        if (values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer((int)values.length);
        boolean[] arrbl = values;
        int n = arrbl.length;
        int n2 = 0;
        while (n2 < n) {
            boolean v = arrbl[n2];
            buffer.writeBoolean((boolean)v);
            ++n2;
        }
        return buffer;
    }

    public static ByteBuf copyFloat(float value) {
        ByteBuf buf = Unpooled.buffer((int)4);
        buf.writeFloat((float)value);
        return buf;
    }

    public static ByteBuf copyFloat(float ... values) {
        if (values == null) return EMPTY_BUFFER;
        if (values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer((int)(values.length * 4));
        float[] arrf = values;
        int n = arrf.length;
        int n2 = 0;
        while (n2 < n) {
            float v = arrf[n2];
            buffer.writeFloat((float)v);
            ++n2;
        }
        return buffer;
    }

    public static ByteBuf copyDouble(double value) {
        ByteBuf buf = Unpooled.buffer((int)8);
        buf.writeDouble((double)value);
        return buf;
    }

    public static ByteBuf copyDouble(double ... values) {
        if (values == null) return EMPTY_BUFFER;
        if (values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer((int)(values.length * 8));
        double[] arrd = values;
        int n = arrd.length;
        int n2 = 0;
        while (n2 < n) {
            double v = arrd[n2];
            buffer.writeDouble((double)v);
            ++n2;
        }
        return buffer;
    }

    public static ByteBuf unreleasableBuffer(ByteBuf buf) {
        return new UnreleasableByteBuf((ByteBuf)buf);
    }

    @Deprecated
    public static ByteBuf unmodifiableBuffer(ByteBuf ... buffers) {
        return Unpooled.wrappedUnmodifiableBuffer((boolean)true, (ByteBuf[])buffers);
    }

    public static ByteBuf wrappedUnmodifiableBuffer(ByteBuf ... buffers) {
        return Unpooled.wrappedUnmodifiableBuffer((boolean)false, (ByteBuf[])buffers);
    }

    private static ByteBuf wrappedUnmodifiableBuffer(boolean copy, ByteBuf ... buffers) {
        switch (buffers.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                return buffers[0].asReadOnly();
            }
        }
        if (!copy) return new FixedCompositeByteBuf((ByteBufAllocator)ALLOC, (ByteBuf[])buffers);
        buffers = (ByteBuf[])Arrays.copyOf(buffers, (int)buffers.length, ByteBuf[].class);
        return new FixedCompositeByteBuf((ByteBufAllocator)ALLOC, (ByteBuf[])buffers);
    }

    private Unpooled() {
    }

    static {
        if ($assertionsDisabled) return;
        if (EMPTY_BUFFER instanceof EmptyByteBuf) return;
        throw new AssertionError((Object)"EMPTY_BUFFER must be an EmptyByteBuf.");
    }
}

