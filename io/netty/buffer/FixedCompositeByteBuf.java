/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.FixedCompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.EmptyArrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class FixedCompositeByteBuf
extends AbstractReferenceCountedByteBuf {
    private static final ByteBuf[] EMPTY = new ByteBuf[]{Unpooled.EMPTY_BUFFER};
    private final int nioBufferCount;
    private final int capacity;
    private final ByteBufAllocator allocator;
    private final ByteOrder order;
    private final ByteBuf[] buffers;
    private final boolean direct;

    FixedCompositeByteBuf(ByteBufAllocator allocator, ByteBuf ... buffers) {
        super((int)Integer.MAX_VALUE);
        if (buffers.length == 0) {
            this.buffers = EMPTY;
            this.order = ByteOrder.BIG_ENDIAN;
            this.nioBufferCount = 1;
            this.capacity = 0;
            this.direct = false;
        } else {
            ByteBuf b = buffers[0];
            this.buffers = buffers;
            boolean direct = true;
            int nioBufferCount = b.nioBufferCount();
            int capacity = b.readableBytes();
            this.order = b.order();
            for (int i = 1; i < buffers.length; ++i) {
                b = buffers[i];
                if (buffers[i].order() != this.order) {
                    throw new IllegalArgumentException((String)"All ByteBufs need to have same ByteOrder");
                }
                nioBufferCount += b.nioBufferCount();
                capacity += b.readableBytes();
                if (b.isDirect()) continue;
                direct = false;
            }
            this.nioBufferCount = nioBufferCount;
            this.capacity = capacity;
            this.direct = direct;
        }
        this.setIndex((int)0, (int)this.capacity());
        this.allocator = allocator;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isWritable(int size) {
        return false;
    }

    @Override
    public ByteBuf discardReadBytes() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShortLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setIntLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLongLE(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, InputStream in, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public int maxCapacity() {
        return this.capacity;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.allocator;
    }

    @Override
    public ByteOrder order() {
        return this.order;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return this.direct;
    }

    private Component findComponent(int index) {
        int readable = 0;
        int i = 0;
        while (i < this.buffers.length) {
            Component comp = null;
            ByteBuf b = this.buffers[i];
            if (b instanceof Component) {
                comp = (Component)b;
                b = comp.buf;
            }
            if (index < (readable += b.readableBytes())) {
                if (comp != null) return comp;
                comp = new Component((int)i, (int)(readable - b.readableBytes()), (ByteBuf)b);
                this.buffers[i] = comp;
                return comp;
            }
            ++i;
        }
        throw new IllegalStateException();
    }

    private ByteBuf buffer(int i) {
        ByteBuf byteBuf;
        ByteBuf b = this.buffers[i];
        if (b instanceof Component) {
            byteBuf = ((Component)b).buf;
            return byteBuf;
        }
        byteBuf = b;
        return byteBuf;
    }

    @Override
    public byte getByte(int index) {
        return this._getByte((int)index);
    }

    @Override
    protected byte _getByte(int index) {
        Component c = this.findComponent((int)index);
        return c.buf.getByte((int)(index - ((Component)c).offset));
    }

    @Override
    protected short _getShort(int index) {
        Component c = this.findComponent((int)index);
        if (index + 2 <= ((Component)c).endOffset) {
            return c.buf.getShort((int)(index - ((Component)c).offset));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (short)(this._getByte((int)index) & 255 | (this._getByte((int)(index + 1)) & 255) << 8);
        return (short)((this._getByte((int)index) & 255) << 8 | this._getByte((int)(index + 1)) & 255);
    }

    @Override
    protected short _getShortLE(int index) {
        Component c = this.findComponent((int)index);
        if (index + 2 <= ((Component)c).endOffset) {
            return c.buf.getShortLE((int)(index - ((Component)c).offset));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (short)((this._getByte((int)index) & 255) << 8 | this._getByte((int)(index + 1)) & 255);
        return (short)(this._getByte((int)index) & 255 | (this._getByte((int)(index + 1)) & 255) << 8);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        Component c = this.findComponent((int)index);
        if (index + 3 <= ((Component)c).endOffset) {
            return c.buf.getUnsignedMedium((int)(index - ((Component)c).offset));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return this._getShort((int)index) & 65535 | (this._getByte((int)(index + 2)) & 255) << 16;
        return (this._getShort((int)index) & 65535) << 8 | this._getByte((int)(index + 2)) & 255;
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        Component c = this.findComponent((int)index);
        if (index + 3 <= ((Component)c).endOffset) {
            return c.buf.getUnsignedMediumLE((int)(index - ((Component)c).offset));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (this._getShortLE((int)index) & 65535) << 8 | this._getByte((int)(index + 2)) & 255;
        return this._getShortLE((int)index) & 65535 | (this._getByte((int)(index + 2)) & 255) << 16;
    }

    @Override
    protected int _getInt(int index) {
        Component c = this.findComponent((int)index);
        if (index + 4 <= ((Component)c).endOffset) {
            return c.buf.getInt((int)(index - ((Component)c).offset));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return this._getShort((int)index) & 65535 | (this._getShort((int)(index + 2)) & 65535) << 16;
        return (this._getShort((int)index) & 65535) << 16 | this._getShort((int)(index + 2)) & 65535;
    }

    @Override
    protected int _getIntLE(int index) {
        Component c = this.findComponent((int)index);
        if (index + 4 <= ((Component)c).endOffset) {
            return c.buf.getIntLE((int)(index - ((Component)c).offset));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (this._getShortLE((int)index) & 65535) << 16 | this._getShortLE((int)(index + 2)) & 65535;
        return this._getShortLE((int)index) & 65535 | (this._getShortLE((int)(index + 2)) & 65535) << 16;
    }

    @Override
    protected long _getLong(int index) {
        Component c = this.findComponent((int)index);
        if (index + 8 <= ((Component)c).endOffset) {
            return c.buf.getLong((int)(index - ((Component)c).offset));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (long)this._getInt((int)index) & 0xFFFFFFFFL | ((long)this._getInt((int)(index + 4)) & 0xFFFFFFFFL) << 32;
        return ((long)this._getInt((int)index) & 0xFFFFFFFFL) << 32 | (long)this._getInt((int)(index + 4)) & 0xFFFFFFFFL;
    }

    @Override
    protected long _getLongLE(int index) {
        Component c = this.findComponent((int)index);
        if (index + 8 <= ((Component)c).endOffset) {
            return c.buf.getLongLE((int)(index - ((Component)c).offset));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return ((long)this._getIntLE((int)index) & 0xFFFFFFFFL) << 32 | (long)this._getIntLE((int)(index + 4)) & 0xFFFFFFFFL;
        return (long)this._getIntLE((int)index) & 0xFFFFFFFFL | ((long)this._getIntLE((int)(index + 4)) & 0xFFFFFFFFL) << 32;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.length);
        if (length == 0) {
            return this;
        }
        Component c = this.findComponent((int)index);
        int i = ((Component)c).index;
        int adjustment = ((Component)c).offset;
        ByteBuf s = c.buf;
        do {
            int localLength = Math.min((int)length, (int)(s.readableBytes() - (index - adjustment)));
            s.getBytes((int)(index - adjustment), (byte[])dst, (int)dstIndex, (int)localLength);
            index += localLength;
            dstIndex += localLength;
            adjustment += s.readableBytes();
            if ((length -= localLength) <= 0) {
                return this;
            }
            s = this.buffer((int)(++i));
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        int limit = dst.limit();
        int length = dst.remaining();
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return this;
        }
        try {
            Component c = this.findComponent((int)index);
            int i = ((Component)c).index;
            int adjustment = ((Component)c).offset;
            ByteBuf s = c.buf;
            do {
                int localLength = Math.min((int)length, (int)(s.readableBytes() - (index - adjustment)));
                dst.limit((int)(dst.position() + localLength));
                s.getBytes((int)(index - adjustment), (ByteBuffer)dst);
                index += localLength;
                adjustment += s.readableBytes();
                if ((length -= localLength) <= 0) {
                    return this;
                }
                s = this.buffer((int)(++i));
            } while (true);
        }
        finally {
            dst.limit((int)limit);
        }
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.capacity());
        if (length == 0) {
            return this;
        }
        Component c = this.findComponent((int)index);
        int i = ((Component)c).index;
        int adjustment = ((Component)c).offset;
        ByteBuf s = c.buf;
        do {
            int localLength = Math.min((int)length, (int)(s.readableBytes() - (index - adjustment)));
            s.getBytes((int)(index - adjustment), (ByteBuf)dst, (int)dstIndex, (int)localLength);
            index += localLength;
            dstIndex += localLength;
            adjustment += s.readableBytes();
            if ((length -= localLength) <= 0) {
                return this;
            }
            s = this.buffer((int)(++i));
        } while (true);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        int count = this.nioBufferCount();
        if (count == 1) {
            return out.write((ByteBuffer)this.internalNioBuffer((int)index, (int)length));
        }
        long writtenBytes = out.write((ByteBuffer[])this.nioBuffers((int)index, (int)length));
        if (writtenBytes <= Integer.MAX_VALUE) return (int)writtenBytes;
        return Integer.MAX_VALUE;
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        int count = this.nioBufferCount();
        if (count == 1) {
            return out.write((ByteBuffer)this.internalNioBuffer((int)index, (int)length), (long)position);
        }
        long writtenBytes = 0L;
        ByteBuffer[] arrbyteBuffer = this.nioBuffers((int)index, (int)length);
        int n = arrbyteBuffer.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                if (writtenBytes <= Integer.MAX_VALUE) return (int)writtenBytes;
                return Integer.MAX_VALUE;
            }
            ByteBuffer buf = arrbyteBuffer[n2];
            writtenBytes += (long)out.write((ByteBuffer)buf, (long)(position + writtenBytes));
            ++n2;
        } while (true);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return this;
        }
        Component c = this.findComponent((int)index);
        int i = ((Component)c).index;
        int adjustment = ((Component)c).offset;
        ByteBuf s = c.buf;
        do {
            int localLength = Math.min((int)length, (int)(s.readableBytes() - (index - adjustment)));
            s.getBytes((int)(index - adjustment), (OutputStream)out, (int)localLength);
            index += localLength;
            adjustment += s.readableBytes();
            if ((length -= localLength) <= 0) {
                return this;
            }
            s = this.buffer((int)(++i));
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex((int)index, (int)length);
        boolean release = true;
        ByteBuf buf = this.alloc().buffer((int)length);
        try {
            buf.writeBytes((ByteBuf)this, (int)index, (int)length);
            release = false;
            ByteBuf byteBuf = buf;
            return byteBuf;
        }
        finally {
            if (release) {
                buf.release();
            }
        }
    }

    @Override
    public int nioBufferCount() {
        return this.nioBufferCount;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        ByteBuf buf;
        this.checkIndex((int)index, (int)length);
        if (this.buffers.length == 1 && (buf = this.buffer((int)0)).nioBufferCount() == 1) {
            return buf.nioBuffer((int)index, (int)length);
        }
        ByteBuffer merged = ByteBuffer.allocate((int)length).order((ByteOrder)this.order());
        ByteBuffer[] buffers = this.nioBuffers((int)index, (int)length);
        int i = 0;
        do {
            if (i >= buffers.length) {
                merged.flip();
                return merged;
            }
            merged.put((ByteBuffer)buffers[i]);
            ++i;
        } while (true);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        if (this.buffers.length != 1) throw new UnsupportedOperationException();
        return this.buffer((int)0).internalNioBuffer((int)index, (int)length);
    }

    /*
     * Exception decompiling
     */
    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:478)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.rebuildSwitches(SwitchReplacer.java:328)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:466)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public boolean hasArray() {
        switch (this.buffers.length) {
            case 0: {
                return true;
            }
            case 1: {
                return this.buffer((int)0).hasArray();
            }
        }
        return false;
    }

    @Override
    public byte[] array() {
        switch (this.buffers.length) {
            case 0: {
                return EmptyArrays.EMPTY_BYTES;
            }
            case 1: {
                return this.buffer((int)0).array();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public int arrayOffset() {
        switch (this.buffers.length) {
            case 0: {
                return 0;
            }
            case 1: {
                return this.buffer((int)0).arrayOffset();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMemoryAddress() {
        switch (this.buffers.length) {
            case 0: {
                return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
            }
            case 1: {
                return this.buffer((int)0).hasMemoryAddress();
            }
        }
        return false;
    }

    @Override
    public long memoryAddress() {
        switch (this.buffers.length) {
            case 0: {
                return Unpooled.EMPTY_BUFFER.memoryAddress();
            }
            case 1: {
                return this.buffer((int)0).memoryAddress();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected void deallocate() {
        int i = 0;
        while (i < this.buffers.length) {
            this.buffer((int)i).release();
            ++i;
        }
    }

    @Override
    public String toString() {
        String result = super.toString();
        result = result.substring((int)0, (int)(result.length() - 1));
        return result + ", components=" + this.buffers.length + ')';
    }
}

