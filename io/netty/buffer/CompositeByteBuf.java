/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.AbstractUnpooledSlicedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.DuplicatedByteBuf;
import io.netty.buffer.PooledDuplicatedByteBuf;
import io.netty.buffer.PooledSlicedByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CompositeByteBuf
extends AbstractReferenceCountedByteBuf
implements Iterable<ByteBuf> {
    private static final ByteBuffer EMPTY_NIO_BUFFER = Unpooled.EMPTY_BUFFER.nioBuffer();
    private static final Iterator<ByteBuf> EMPTY_ITERATOR = Collections.emptyList().iterator();
    private final ByteBufAllocator alloc;
    private final boolean direct;
    private final int maxNumComponents;
    private int componentCount;
    private Component[] components;
    private boolean freed;
    static final ByteWrapper<byte[]> BYTE_ARRAY_WRAPPER = new ByteWrapper<byte[]>(){

        public ByteBuf wrap(byte[] bytes) {
            return Unpooled.wrappedBuffer((byte[])bytes);
        }

        public boolean isEmpty(byte[] bytes) {
            if (bytes.length != 0) return false;
            return true;
        }
    };
    static final ByteWrapper<ByteBuffer> BYTE_BUFFER_WRAPPER = new ByteWrapper<ByteBuffer>(){

        public ByteBuf wrap(ByteBuffer bytes) {
            return Unpooled.wrappedBuffer((ByteBuffer)bytes);
        }

        public boolean isEmpty(ByteBuffer bytes) {
            if (bytes.hasRemaining()) return false;
            return true;
        }
    };
    private Component lastAccessed;

    private CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, int initSize) {
        super((int)Integer.MAX_VALUE);
        if (alloc == null) {
            throw new NullPointerException((String)"alloc");
        }
        if (maxNumComponents < 1) {
            throw new IllegalArgumentException((String)("maxNumComponents: " + maxNumComponents + " (expected: >= 1)"));
        }
        this.alloc = alloc;
        this.direct = direct;
        this.maxNumComponents = maxNumComponents;
        this.components = CompositeByteBuf.newCompArray((int)initSize, (int)maxNumComponents);
    }

    public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents) {
        this((ByteBufAllocator)alloc, (boolean)direct, (int)maxNumComponents, (int)0);
    }

    public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf ... buffers) {
        this((ByteBufAllocator)alloc, (boolean)direct, (int)maxNumComponents, (ByteBuf[])buffers, (int)0);
    }

    CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf[] buffers, int offset) {
        this((ByteBufAllocator)alloc, (boolean)direct, (int)maxNumComponents, (int)(buffers.length - offset));
        this.addComponents0((boolean)false, (int)0, (ByteBuf[])buffers, (int)offset);
        this.consolidateIfNeeded();
        this.setIndex0((int)0, (int)this.capacity());
    }

    public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, Iterable<ByteBuf> buffers) {
        this((ByteBufAllocator)alloc, (boolean)direct, (int)maxNumComponents, (int)(buffers instanceof Collection ? ((Collection)buffers).size() : 0));
        this.addComponents((boolean)false, (int)0, buffers);
        this.setIndex((int)0, (int)this.capacity());
    }

    <T> CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteWrapper<T> wrapper, T[] buffers, int offset) {
        this((ByteBufAllocator)alloc, (boolean)direct, (int)maxNumComponents, (int)(buffers.length - offset));
        this.addComponents0((boolean)false, (int)0, wrapper, buffers, (int)offset);
        this.consolidateIfNeeded();
        this.setIndex((int)0, (int)this.capacity());
    }

    private static Component[] newCompArray(int initComponents, int maxNumComponents) {
        int capacityGuess = Math.min((int)16, (int)maxNumComponents);
        return new Component[Math.max((int)initComponents, (int)capacityGuess)];
    }

    CompositeByteBuf(ByteBufAllocator alloc) {
        super((int)Integer.MAX_VALUE);
        this.alloc = alloc;
        this.direct = false;
        this.maxNumComponents = 0;
        this.components = null;
    }

    public CompositeByteBuf addComponent(ByteBuf buffer) {
        return this.addComponent((boolean)false, (ByteBuf)buffer);
    }

    public CompositeByteBuf addComponents(ByteBuf ... buffers) {
        return this.addComponents((boolean)false, (ByteBuf[])buffers);
    }

    public CompositeByteBuf addComponents(Iterable<ByteBuf> buffers) {
        return this.addComponents((boolean)false, buffers);
    }

    public CompositeByteBuf addComponent(int cIndex, ByteBuf buffer) {
        return this.addComponent((boolean)false, (int)cIndex, (ByteBuf)buffer);
    }

    public CompositeByteBuf addComponent(boolean increaseWriterIndex, ByteBuf buffer) {
        return this.addComponent((boolean)increaseWriterIndex, (int)this.componentCount, (ByteBuf)buffer);
    }

    public CompositeByteBuf addComponents(boolean increaseWriterIndex, ByteBuf ... buffers) {
        ObjectUtil.checkNotNull(buffers, (String)"buffers");
        this.addComponents0((boolean)increaseWriterIndex, (int)this.componentCount, (ByteBuf[])buffers, (int)0);
        this.consolidateIfNeeded();
        return this;
    }

    public CompositeByteBuf addComponents(boolean increaseWriterIndex, Iterable<ByteBuf> buffers) {
        return this.addComponents((boolean)increaseWriterIndex, (int)this.componentCount, buffers);
    }

    public CompositeByteBuf addComponent(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
        ObjectUtil.checkNotNull(buffer, (String)"buffer");
        this.addComponent0((boolean)increaseWriterIndex, (int)cIndex, (ByteBuf)buffer);
        this.consolidateIfNeeded();
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int addComponent0(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
        assert (buffer != null);
        boolean wasAdded = false;
        try {
            this.checkComponentIndex((int)cIndex);
            Component c = this.newComponent((ByteBuf)CompositeByteBuf.ensureAccessible((ByteBuf)buffer), (int)0);
            int readableBytes = c.length();
            this.addComp((int)cIndex, (Component)c);
            wasAdded = true;
            if (readableBytes > 0 && cIndex < this.componentCount - 1) {
                this.updateComponentOffsets((int)cIndex);
            } else if (cIndex > 0) {
                c.reposition((int)this.components[cIndex - 1].endOffset);
            }
            if (increaseWriterIndex) {
                this.writerIndex += readableBytes;
            }
            int n = cIndex;
            return n;
        }
        finally {
            if (!wasAdded) {
                buffer.release();
            }
        }
    }

    private static ByteBuf ensureAccessible(ByteBuf buf) {
        if (!checkAccessible) return buf;
        if (buf.isAccessible()) return buf;
        throw new IllegalReferenceCountException((int)0);
    }

    private Component newComponent(ByteBuf buf, int offset) {
        int srcIndex = buf.readerIndex();
        int len = buf.readableBytes();
        ByteBuf unwrapped = buf;
        int unwrappedIndex = srcIndex;
        while (unwrapped instanceof WrappedByteBuf || unwrapped instanceof SwappedByteBuf) {
            unwrapped = unwrapped.unwrap();
        }
        if (unwrapped instanceof AbstractUnpooledSlicedByteBuf) {
            unwrappedIndex += ((AbstractUnpooledSlicedByteBuf)unwrapped).idx((int)0);
            unwrapped = unwrapped.unwrap();
        } else if (unwrapped instanceof PooledSlicedByteBuf) {
            unwrappedIndex += ((PooledSlicedByteBuf)unwrapped).adjustment;
            unwrapped = unwrapped.unwrap();
        } else if (unwrapped instanceof DuplicatedByteBuf || unwrapped instanceof PooledDuplicatedByteBuf) {
            unwrapped = unwrapped.unwrap();
        }
        ByteBuf slice = buf.capacity() == len ? buf : null;
        return new Component((ByteBuf)buf.order((ByteOrder)ByteOrder.BIG_ENDIAN), (int)srcIndex, (ByteBuf)unwrapped.order((ByteOrder)ByteOrder.BIG_ENDIAN), (int)unwrappedIndex, (int)offset, (int)len, (ByteBuf)slice);
    }

    public CompositeByteBuf addComponents(int cIndex, ByteBuf ... buffers) {
        ObjectUtil.checkNotNull(buffers, (String)"buffers");
        this.addComponents0((boolean)false, (int)cIndex, (ByteBuf[])buffers, (int)0);
        this.consolidateIfNeeded();
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CompositeByteBuf addComponents0(boolean increaseWriterIndex, int cIndex, ByteBuf[] buffers, int arrOffset) {
        int len = buffers.length;
        int count = len - arrOffset;
        int ci = Integer.MAX_VALUE;
        try {
            ByteBuf b;
            this.checkComponentIndex((int)cIndex);
            this.shiftComps((int)cIndex, (int)count);
            int nextOffset = cIndex > 0 ? this.components[cIndex - 1].endOffset : 0;
            ci = cIndex;
            while (arrOffset < len && (b = buffers[arrOffset]) != null) {
                Component c;
                this.components[ci] = c = this.newComponent((ByteBuf)CompositeByteBuf.ensureAccessible((ByteBuf)b), (int)nextOffset);
                nextOffset = c.endOffset;
                ++arrOffset;
                ++ci;
            }
            b = this;
            return b;
        }
        finally {
            if (ci < this.componentCount) {
                if (ci < cIndex + count) {
                    this.removeCompRange((int)ci, (int)(cIndex + count));
                    while (arrOffset < len) {
                        ReferenceCountUtil.safeRelease((Object)buffers[arrOffset]);
                        ++arrOffset;
                    }
                }
                this.updateComponentOffsets((int)ci);
            }
            if (increaseWriterIndex && ci > cIndex && ci <= this.componentCount) {
                this.writerIndex += this.components[ci - 1].endOffset - this.components[cIndex].offset;
            }
        }
    }

    private <T> int addComponents0(boolean increaseWriterIndex, int cIndex, ByteWrapper<T> wrapper, T[] buffers, int offset) {
        this.checkComponentIndex((int)cIndex);
        int i = offset;
        int len = buffers.length;
        while (i < len) {
            int size;
            T b = buffers[i];
            if (b == null) {
                return cIndex;
            }
            if (!wrapper.isEmpty(b) && (cIndex = this.addComponent0((boolean)increaseWriterIndex, (int)cIndex, (ByteBuf)wrapper.wrap(b)) + 1) > (size = this.componentCount)) {
                cIndex = size;
            }
            ++i;
        }
        return cIndex;
    }

    public CompositeByteBuf addComponents(int cIndex, Iterable<ByteBuf> buffers) {
        return this.addComponents((boolean)false, (int)cIndex, buffers);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompositeByteBuf addFlattenedComponents(boolean increaseWriterIndex, ByteBuf buffer) {
        ObjectUtil.checkNotNull(buffer, (String)"buffer");
        int ridx = buffer.readerIndex();
        int widx = buffer.writerIndex();
        if (ridx == widx) {
            buffer.release();
            return this;
        }
        if (!(buffer instanceof CompositeByteBuf)) {
            this.addComponent0((boolean)increaseWriterIndex, (int)this.componentCount, (ByteBuf)buffer);
            this.consolidateIfNeeded();
            return this;
        }
        CompositeByteBuf from = (CompositeByteBuf)buffer;
        from.checkIndex((int)ridx, (int)(widx - ridx));
        Component[] fromComponents = from.components;
        int compCountBefore = this.componentCount;
        int writerIndexBefore = this.writerIndex;
        try {
            block12 : {
                int cidx = from.toComponentIndex0((int)ridx);
                int newOffset = this.capacity();
                do {
                    Component component = fromComponents[cidx];
                    int compOffset = component.offset;
                    int fromIdx = Math.max((int)ridx, (int)compOffset);
                    int toIdx = Math.min((int)widx, (int)component.endOffset);
                    int len = toIdx - fromIdx;
                    if (len > 0) {
                        this.addComp((int)this.componentCount, (Component)new Component((ByteBuf)component.srcBuf.retain(), (int)component.srcIdx((int)fromIdx), (ByteBuf)component.buf, (int)component.idx((int)fromIdx), (int)newOffset, (int)len, null));
                    }
                    if (widx == toIdx) {
                        if (increaseWriterIndex) {
                            break;
                        }
                        break block12;
                    }
                    newOffset += len;
                    ++cidx;
                } while (true);
                this.writerIndex = writerIndexBefore + (widx - ridx);
            }
            this.consolidateIfNeeded();
            buffer.release();
            buffer = null;
            CompositeByteBuf cidx = this;
            return cidx;
        }
        finally {
            if (buffer != null) {
                if (increaseWriterIndex) {
                    this.writerIndex = writerIndexBefore;
                }
                for (int cidx = this.componentCount - 1; cidx >= compCountBefore; --cidx) {
                    this.components[cidx].free();
                    this.removeComp((int)cidx);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CompositeByteBuf addComponents(boolean increaseIndex, int cIndex, Iterable<ByteBuf> buffers) {
        if (buffers instanceof ByteBuf) {
            return this.addComponent((boolean)increaseIndex, (int)cIndex, (ByteBuf)((ByteBuf)((Object)buffers)));
        }
        ObjectUtil.checkNotNull(buffers, (String)"buffers");
        Iterator<ByteBuf> it = buffers.iterator();
        try {
            ByteBuf b;
            this.checkComponentIndex((int)cIndex);
            while (it.hasNext() && (b = it.next()) != null) {
                cIndex = this.addComponent0((boolean)increaseIndex, (int)cIndex, (ByteBuf)b) + 1;
                cIndex = Math.min((int)cIndex, (int)this.componentCount);
            }
        }
        finally {
            do {
                if (!it.hasNext()) {
                }
                ReferenceCountUtil.safeRelease((Object)it.next());
            } while (true);
        }
        this.consolidateIfNeeded();
        return this;
    }

    private void consolidateIfNeeded() {
        int size = this.componentCount;
        if (size <= this.maxNumComponents) return;
        this.consolidate0((int)0, (int)size);
    }

    private void checkComponentIndex(int cIndex) {
        this.ensureAccessible();
        if (cIndex < 0) throw new IndexOutOfBoundsException((String)String.format((String)"cIndex: %d (expected: >= 0 && <= numComponents(%d))", (Object[])new Object[]{Integer.valueOf((int)cIndex), Integer.valueOf((int)this.componentCount)}));
        if (cIndex <= this.componentCount) return;
        throw new IndexOutOfBoundsException((String)String.format((String)"cIndex: %d (expected: >= 0 && <= numComponents(%d))", (Object[])new Object[]{Integer.valueOf((int)cIndex), Integer.valueOf((int)this.componentCount)}));
    }

    private void checkComponentIndex(int cIndex, int numComponents) {
        this.ensureAccessible();
        if (cIndex < 0) throw new IndexOutOfBoundsException((String)String.format((String)"cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", (Object[])new Object[]{Integer.valueOf((int)cIndex), Integer.valueOf((int)numComponents), Integer.valueOf((int)this.componentCount)}));
        if (cIndex + numComponents <= this.componentCount) return;
        throw new IndexOutOfBoundsException((String)String.format((String)"cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", (Object[])new Object[]{Integer.valueOf((int)cIndex), Integer.valueOf((int)numComponents), Integer.valueOf((int)this.componentCount)}));
    }

    private void updateComponentOffsets(int cIndex) {
        int size = this.componentCount;
        if (size <= cIndex) {
            return;
        }
        int nextIndex = cIndex > 0 ? this.components[cIndex - 1].endOffset : 0;
        while (cIndex < size) {
            Component c = this.components[cIndex];
            c.reposition((int)nextIndex);
            nextIndex = c.endOffset;
            ++cIndex;
        }
    }

    public CompositeByteBuf removeComponent(int cIndex) {
        this.checkComponentIndex((int)cIndex);
        Component comp = this.components[cIndex];
        if (this.lastAccessed == comp) {
            this.lastAccessed = null;
        }
        comp.free();
        this.removeComp((int)cIndex);
        if (comp.length() <= 0) return this;
        this.updateComponentOffsets((int)cIndex);
        return this;
    }

    public CompositeByteBuf removeComponents(int cIndex, int numComponents) {
        this.checkComponentIndex((int)cIndex, (int)numComponents);
        if (numComponents == 0) {
            return this;
        }
        int endIndex = cIndex + numComponents;
        boolean needsUpdate = false;
        int i = cIndex;
        do {
            if (i >= endIndex) {
                this.removeCompRange((int)cIndex, (int)endIndex);
                if (!needsUpdate) return this;
                this.updateComponentOffsets((int)cIndex);
                return this;
            }
            Component c = this.components[i];
            if (c.length() > 0) {
                needsUpdate = true;
            }
            if (this.lastAccessed == c) {
                this.lastAccessed = null;
            }
            c.free();
            ++i;
        } while (true);
    }

    @Override
    public Iterator<ByteBuf> iterator() {
        CompositeByteBufIterator compositeByteBufIterator;
        this.ensureAccessible();
        if (this.componentCount == 0) {
            compositeByteBufIterator = EMPTY_ITERATOR;
            return compositeByteBufIterator;
        }
        compositeByteBufIterator = new CompositeByteBufIterator((CompositeByteBuf)this, null);
        return compositeByteBufIterator;
    }

    @Override
    protected int forEachByteAsc0(int start, int end, ByteProcessor processor) throws Exception {
        if (end <= start) {
            return -1;
        }
        int i = this.toComponentIndex0((int)start);
        int length = end - start;
        while (length > 0) {
            Component c = this.components[i];
            if (c.offset != c.endOffset) {
                int result;
                ByteBuf s = c.buf;
                int localStart = c.idx((int)start);
                int localLength = Math.min((int)length, (int)(c.endOffset - start));
                int n = result = s instanceof AbstractByteBuf ? ((AbstractByteBuf)s).forEachByteAsc0((int)localStart, (int)(localStart + localLength), (ByteProcessor)processor) : s.forEachByte((int)localStart, (int)localLength, (ByteProcessor)processor);
                if (result != -1) {
                    return result - c.adjustment;
                }
                start += localLength;
                length -= localLength;
            }
            ++i;
        }
        return -1;
    }

    @Override
    protected int forEachByteDesc0(int rStart, int rEnd, ByteProcessor processor) throws Exception {
        if (rEnd > rStart) {
            return -1;
        }
        int i = this.toComponentIndex0((int)rStart);
        int length = 1 + rStart - rEnd;
        while (length > 0) {
            Component c = this.components[i];
            if (c.offset != c.endOffset) {
                int result;
                ByteBuf s = c.buf;
                int localRStart = c.idx((int)(length + rEnd));
                int localLength = Math.min((int)length, (int)localRStart);
                int localIndex = localRStart - localLength;
                int n = result = s instanceof AbstractByteBuf ? ((AbstractByteBuf)s).forEachByteDesc0((int)(localRStart - 1), (int)localIndex, (ByteProcessor)processor) : s.forEachByteDesc((int)localIndex, (int)localLength, (ByteProcessor)processor);
                if (result != -1) {
                    return result - c.adjustment;
                }
                length -= localLength;
            }
            --i;
        }
        return -1;
    }

    public List<ByteBuf> decompose(int offset, int length) {
        ByteBuf slice;
        this.checkIndex((int)offset, (int)length);
        if (length == 0) {
            return Collections.emptyList();
        }
        int componentId = this.toComponentIndex0((int)offset);
        int bytesToSlice = length;
        Component firstC = this.components[componentId];
        if ((bytesToSlice -= (slice = firstC.buf.slice((int)firstC.idx((int)offset), (int)Math.min((int)(firstC.endOffset - offset), (int)bytesToSlice))).readableBytes()) == 0) {
            return Collections.singletonList(slice);
        }
        ArrayList<ByteBuf> sliceList = new ArrayList<ByteBuf>((int)(this.componentCount - componentId));
        sliceList.add(slice);
        do {
            Component component = this.components[++componentId];
            slice = component.buf.slice((int)component.idx((int)component.offset), (int)Math.min((int)component.length(), (int)bytesToSlice));
            sliceList.add(slice);
        } while ((bytesToSlice -= slice.readableBytes()) > 0);
        return sliceList;
    }

    @Override
    public boolean isDirect() {
        int size = this.componentCount;
        if (size == 0) {
            return false;
        }
        int i = 0;
        while (i < size) {
            if (!this.components[i].buf.isDirect()) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean hasArray() {
        switch (this.componentCount) {
            case 0: {
                return true;
            }
            case 1: {
                return this.components[0].buf.hasArray();
            }
        }
        return false;
    }

    @Override
    public byte[] array() {
        switch (this.componentCount) {
            case 0: {
                return EmptyArrays.EMPTY_BYTES;
            }
            case 1: {
                return this.components[0].buf.array();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public int arrayOffset() {
        switch (this.componentCount) {
            case 0: {
                return 0;
            }
            case 1: {
                Component c = this.components[0];
                return c.idx((int)c.buf.arrayOffset());
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMemoryAddress() {
        switch (this.componentCount) {
            case 0: {
                return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
            }
            case 1: {
                return this.components[0].buf.hasMemoryAddress();
            }
        }
        return false;
    }

    @Override
    public long memoryAddress() {
        switch (this.componentCount) {
            case 0: {
                return Unpooled.EMPTY_BUFFER.memoryAddress();
            }
            case 1: {
                Component c = this.components[0];
                return c.buf.memoryAddress() + (long)c.adjustment;
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public int capacity() {
        int size = this.componentCount;
        if (size <= 0) return 0;
        int n = this.components[size - 1].endOffset;
        return n;
    }

    @Override
    public CompositeByteBuf capacity(int newCapacity) {
        int i;
        int cLength;
        this.checkNewCapacity((int)newCapacity);
        int size = this.componentCount;
        int oldCapacity = this.capacity();
        if (newCapacity > oldCapacity) {
            int paddingLength = newCapacity - oldCapacity;
            ByteBuf padding = this.allocBuffer((int)paddingLength).setIndex((int)0, (int)paddingLength);
            this.addComponent0((boolean)false, (int)size, (ByteBuf)padding);
            if (this.componentCount < this.maxNumComponents) return this;
            this.consolidateIfNeeded();
            return this;
        }
        if (newCapacity >= oldCapacity) return this;
        this.lastAccessed = null;
        int bytesToTrim = oldCapacity - newCapacity;
        for (i = size - 1; i >= 0; bytesToTrim -= cLength, --i) {
            Component c = this.components[i];
            cLength = c.length();
            if (bytesToTrim < cLength) {
                c.endOffset -= bytesToTrim;
                ByteBuf slice = ((Component)c).slice;
                if (slice == null) break;
                ((Component)c).slice = (ByteBuf)slice.slice((int)0, (int)c.length());
                break;
            }
            c.free();
        }
        this.removeCompRange((int)(i + 1), (int)size);
        if (this.readerIndex() > newCapacity) {
            this.setIndex0((int)newCapacity, (int)newCapacity);
            return this;
        }
        if (this.writerIndex <= newCapacity) return this;
        this.writerIndex = newCapacity;
        return this;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    public int numComponents() {
        return this.componentCount;
    }

    public int maxNumComponents() {
        return this.maxNumComponents;
    }

    public int toComponentIndex(int offset) {
        this.checkIndex((int)offset);
        return this.toComponentIndex0((int)offset);
    }

    private int toComponentIndex0(int offset) {
        int size = this.componentCount;
        if (offset == 0) {
            for (int i = 0; i < size; ++i) {
                if (this.components[i].endOffset <= 0) continue;
                return i;
            }
        }
        if (size <= 2) {
            if (size == 1) return 0;
            if (offset < this.components[0].endOffset) return 0;
            return 1;
        }
        int low = 0;
        int high = size;
        while (low <= high) {
            int mid = low + high >>> 1;
            Component c = this.components[mid];
            if (offset >= c.endOffset) {
                low = mid + 1;
                continue;
            }
            if (offset >= c.offset) return mid;
            high = mid - 1;
        }
        throw new Error((String)"should not reach here");
    }

    public int toByteIndex(int cIndex) {
        this.checkComponentIndex((int)cIndex);
        return this.components[cIndex].offset;
    }

    @Override
    public byte getByte(int index) {
        Component c = this.findComponent((int)index);
        return c.buf.getByte((int)c.idx((int)index));
    }

    @Override
    protected byte _getByte(int index) {
        Component c = this.findComponent0((int)index);
        return c.buf.getByte((int)c.idx((int)index));
    }

    @Override
    protected short _getShort(int index) {
        Component c = this.findComponent0((int)index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShort((int)c.idx((int)index));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (short)(this._getByte((int)index) & 255 | (this._getByte((int)(index + 1)) & 255) << 8);
        return (short)((this._getByte((int)index) & 255) << 8 | this._getByte((int)(index + 1)) & 255);
    }

    @Override
    protected short _getShortLE(int index) {
        Component c = this.findComponent0((int)index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShortLE((int)c.idx((int)index));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (short)((this._getByte((int)index) & 255) << 8 | this._getByte((int)(index + 1)) & 255);
        return (short)(this._getByte((int)index) & 255 | (this._getByte((int)(index + 1)) & 255) << 8);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        Component c = this.findComponent0((int)index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMedium((int)c.idx((int)index));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return this._getShort((int)index) & 65535 | (this._getByte((int)(index + 2)) & 255) << 16;
        return (this._getShort((int)index) & 65535) << 8 | this._getByte((int)(index + 2)) & 255;
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        Component c = this.findComponent0((int)index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMediumLE((int)c.idx((int)index));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (this._getShortLE((int)index) & 65535) << 8 | this._getByte((int)(index + 2)) & 255;
        return this._getShortLE((int)index) & 65535 | (this._getByte((int)(index + 2)) & 255) << 16;
    }

    @Override
    protected int _getInt(int index) {
        Component c = this.findComponent0((int)index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getInt((int)c.idx((int)index));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return this._getShort((int)index) & 65535 | (this._getShort((int)(index + 2)) & 65535) << 16;
        return (this._getShort((int)index) & 65535) << 16 | this._getShort((int)(index + 2)) & 65535;
    }

    @Override
    protected int _getIntLE(int index) {
        Component c = this.findComponent0((int)index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getIntLE((int)c.idx((int)index));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (this._getShortLE((int)index) & 65535) << 16 | this._getShortLE((int)(index + 2)) & 65535;
        return this._getShortLE((int)index) & 65535 | (this._getShortLE((int)(index + 2)) & 65535) << 16;
    }

    @Override
    protected long _getLong(int index) {
        Component c = this.findComponent0((int)index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLong((int)c.idx((int)index));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return (long)this._getInt((int)index) & 0xFFFFFFFFL | ((long)this._getInt((int)(index + 4)) & 0xFFFFFFFFL) << 32;
        return ((long)this._getInt((int)index) & 0xFFFFFFFFL) << 32 | (long)this._getInt((int)(index + 4)) & 0xFFFFFFFFL;
    }

    @Override
    protected long _getLongLE(int index) {
        Component c = this.findComponent0((int)index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLongLE((int)c.idx((int)index));
        }
        if (this.order() != ByteOrder.BIG_ENDIAN) return ((long)this._getIntLE((int)index) & 0xFFFFFFFFL) << 32 | (long)this._getIntLE((int)(index + 4)) & 0xFFFFFFFFL;
        return (long)this._getIntLE((int)index) & 0xFFFFFFFFL | ((long)this._getIntLE((int)(index + 4)) & 0xFFFFFFFFL) << 32;
    }

    @Override
    public CompositeByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0((int)index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            c.buf.getBytes((int)c.idx((int)index), (byte[])dst, (int)dstIndex, (int)localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompositeByteBuf getBytes(int index, ByteBuffer dst) {
        int limit = dst.limit();
        int length = dst.remaining();
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0((int)index);
        try {
            while (length > 0) {
                Component c = this.components[i];
                int localLength = Math.min((int)length, (int)(c.endOffset - index));
                dst.limit((int)(dst.position() + localLength));
                c.buf.getBytes((int)c.idx((int)index), (ByteBuffer)dst);
                index += localLength;
                length -= localLength;
                ++i;
            }
            return this;
        }
        finally {
            dst.limit((int)limit);
        }
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex((int)index, (int)length, (int)dstIndex, (int)dst.capacity());
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0((int)index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            c.buf.getBytes((int)c.idx((int)index), (ByteBuf)dst, (int)dstIndex, (int)localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            ++i;
        }
        return this;
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
    public CompositeByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0((int)index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            c.buf.getBytes((int)c.idx((int)index), (OutputStream)out, (int)localLength);
            index += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    @Override
    public CompositeByteBuf setByte(int index, int value) {
        Component c = this.findComponent((int)index);
        c.buf.setByte((int)c.idx((int)index), (int)value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        Component c = this.findComponent0((int)index);
        c.buf.setByte((int)c.idx((int)index), (int)value);
    }

    @Override
    public CompositeByteBuf setShort(int index, int value) {
        this.checkIndex((int)index, (int)2);
        this._setShort((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        Component c = this.findComponent0((int)index);
        if (index + 2 <= c.endOffset) {
            c.buf.setShort((int)c.idx((int)index), (int)value);
            return;
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setByte((int)index, (int)((byte)(value >>> 8)));
            this._setByte((int)(index + 1), (int)((byte)value));
            return;
        }
        this._setByte((int)index, (int)((byte)value));
        this._setByte((int)(index + 1), (int)((byte)(value >>> 8)));
    }

    @Override
    protected void _setShortLE(int index, int value) {
        Component c = this.findComponent0((int)index);
        if (index + 2 <= c.endOffset) {
            c.buf.setShortLE((int)c.idx((int)index), (int)value);
            return;
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setByte((int)index, (int)((byte)value));
            this._setByte((int)(index + 1), (int)((byte)(value >>> 8)));
            return;
        }
        this._setByte((int)index, (int)((byte)(value >>> 8)));
        this._setByte((int)(index + 1), (int)((byte)value));
    }

    @Override
    public CompositeByteBuf setMedium(int index, int value) {
        this.checkIndex((int)index, (int)3);
        this._setMedium((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        Component c = this.findComponent0((int)index);
        if (index + 3 <= c.endOffset) {
            c.buf.setMedium((int)c.idx((int)index), (int)value);
            return;
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShort((int)index, (int)((short)(value >> 8)));
            this._setByte((int)(index + 2), (int)((byte)value));
            return;
        }
        this._setShort((int)index, (int)((short)value));
        this._setByte((int)(index + 2), (int)((byte)(value >>> 16)));
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        Component c = this.findComponent0((int)index);
        if (index + 3 <= c.endOffset) {
            c.buf.setMediumLE((int)c.idx((int)index), (int)value);
            return;
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShortLE((int)index, (int)((short)value));
            this._setByte((int)(index + 2), (int)((byte)(value >>> 16)));
            return;
        }
        this._setShortLE((int)index, (int)((short)(value >> 8)));
        this._setByte((int)(index + 2), (int)((byte)value));
    }

    @Override
    public CompositeByteBuf setInt(int index, int value) {
        this.checkIndex((int)index, (int)4);
        this._setInt((int)index, (int)value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        Component c = this.findComponent0((int)index);
        if (index + 4 <= c.endOffset) {
            c.buf.setInt((int)c.idx((int)index), (int)value);
            return;
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShort((int)index, (int)((short)(value >>> 16)));
            this._setShort((int)(index + 2), (int)((short)value));
            return;
        }
        this._setShort((int)index, (int)((short)value));
        this._setShort((int)(index + 2), (int)((short)(value >>> 16)));
    }

    @Override
    protected void _setIntLE(int index, int value) {
        Component c = this.findComponent0((int)index);
        if (index + 4 <= c.endOffset) {
            c.buf.setIntLE((int)c.idx((int)index), (int)value);
            return;
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShortLE((int)index, (int)((short)value));
            this._setShortLE((int)(index + 2), (int)((short)(value >>> 16)));
            return;
        }
        this._setShortLE((int)index, (int)((short)(value >>> 16)));
        this._setShortLE((int)(index + 2), (int)((short)value));
    }

    @Override
    public CompositeByteBuf setLong(int index, long value) {
        this.checkIndex((int)index, (int)8);
        this._setLong((int)index, (long)value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        Component c = this.findComponent0((int)index);
        if (index + 8 <= c.endOffset) {
            c.buf.setLong((int)c.idx((int)index), (long)value);
            return;
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setInt((int)index, (int)((int)(value >>> 32)));
            this._setInt((int)(index + 4), (int)((int)value));
            return;
        }
        this._setInt((int)index, (int)((int)value));
        this._setInt((int)(index + 4), (int)((int)(value >>> 32)));
    }

    @Override
    protected void _setLongLE(int index, long value) {
        Component c = this.findComponent0((int)index);
        if (index + 8 <= c.endOffset) {
            c.buf.setLongLE((int)c.idx((int)index), (long)value);
            return;
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setIntLE((int)index, (int)((int)value));
            this._setIntLE((int)(index + 4), (int)((int)(value >>> 32)));
            return;
        }
        this._setIntLE((int)index, (int)((int)(value >>> 32)));
        this._setIntLE((int)(index + 4), (int)((int)value));
    }

    @Override
    public CompositeByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0((int)index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            c.buf.setBytes((int)c.idx((int)index), (byte[])src, (int)srcIndex, (int)localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompositeByteBuf setBytes(int index, ByteBuffer src) {
        int limit = src.limit();
        int length = src.remaining();
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0((int)index);
        try {
            while (length > 0) {
                Component c = this.components[i];
                int localLength = Math.min((int)length, (int)(c.endOffset - index));
                src.limit((int)(src.position() + localLength));
                c.buf.setBytes((int)c.idx((int)index), (ByteBuffer)src);
                index += localLength;
                length -= localLength;
                ++i;
            }
            return this;
        }
        finally {
            src.limit((int)limit);
        }
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex((int)index, (int)length, (int)srcIndex, (int)src.capacity());
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0((int)index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            c.buf.setBytes((int)c.idx((int)index), (ByteBuf)src, (int)srcIndex, (int)localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return in.read((byte[])EmptyArrays.EMPTY_BYTES);
        }
        int i = this.toComponentIndex0((int)index);
        int readBytes = 0;
        do {
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            if (localLength == 0) {
                ++i;
                continue;
            }
            int localReadBytes = c.buf.setBytes((int)c.idx((int)index), (InputStream)in, (int)localLength);
            if (localReadBytes < 0) {
                if (readBytes != 0) return readBytes;
                return -1;
            }
            index += localReadBytes;
            length -= localReadBytes;
            readBytes += localReadBytes;
            if (localReadBytes != localLength) continue;
            ++i;
        } while (length > 0);
        return readBytes;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return in.read((ByteBuffer)EMPTY_NIO_BUFFER);
        }
        int i = this.toComponentIndex0((int)index);
        int readBytes = 0;
        do {
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            if (localLength == 0) {
                ++i;
                continue;
            }
            int localReadBytes = c.buf.setBytes((int)c.idx((int)index), (ScatteringByteChannel)in, (int)localLength);
            if (localReadBytes == 0) {
                return readBytes;
            }
            if (localReadBytes < 0) {
                if (readBytes != 0) return readBytes;
                return -1;
            }
            index += localReadBytes;
            length -= localReadBytes;
            readBytes += localReadBytes;
            if (localReadBytes != localLength) continue;
            ++i;
        } while (length > 0);
        return readBytes;
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        this.checkIndex((int)index, (int)length);
        if (length == 0) {
            return in.read((ByteBuffer)EMPTY_NIO_BUFFER, (long)position);
        }
        int i = this.toComponentIndex0((int)index);
        int readBytes = 0;
        do {
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            if (localLength == 0) {
                ++i;
                continue;
            }
            int localReadBytes = c.buf.setBytes((int)c.idx((int)index), (FileChannel)in, (long)(position + (long)readBytes), (int)localLength);
            if (localReadBytes == 0) {
                return readBytes;
            }
            if (localReadBytes < 0) {
                if (readBytes != 0) return readBytes;
                return -1;
            }
            index += localReadBytes;
            length -= localReadBytes;
            readBytes += localReadBytes;
            if (localReadBytes != localLength) continue;
            ++i;
        } while (length > 0);
        return readBytes;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex((int)index, (int)length);
        ByteBuf dst = this.allocBuffer((int)length);
        if (length == 0) return dst;
        this.copyTo((int)index, (int)length, (int)this.toComponentIndex0((int)index), (ByteBuf)dst);
        return dst;
    }

    private void copyTo(int index, int length, int componentId, ByteBuf dst) {
        int dstIndex = 0;
        int i = componentId;
        do {
            if (length <= 0) {
                dst.writerIndex((int)dst.capacity());
                return;
            }
            Component c = this.components[i];
            int localLength = Math.min((int)length, (int)(c.endOffset - index));
            c.buf.getBytes((int)c.idx((int)index), (ByteBuf)dst, (int)dstIndex, (int)localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            ++i;
        } while (true);
    }

    public ByteBuf component(int cIndex) {
        this.checkComponentIndex((int)cIndex);
        return this.components[cIndex].duplicate();
    }

    public ByteBuf componentAtOffset(int offset) {
        return this.findComponent((int)offset).duplicate();
    }

    public ByteBuf internalComponent(int cIndex) {
        this.checkComponentIndex((int)cIndex);
        return this.components[cIndex].slice();
    }

    public ByteBuf internalComponentAtOffset(int offset) {
        return this.findComponent((int)offset).slice();
    }

    private Component findComponent(int offset) {
        Component la = this.lastAccessed;
        if (la != null && offset >= la.offset && offset < la.endOffset) {
            this.ensureAccessible();
            return la;
        }
        this.checkIndex((int)offset);
        return this.findIt((int)offset);
    }

    private Component findComponent0(int offset) {
        Component la = this.lastAccessed;
        if (la == null) return this.findIt((int)offset);
        if (offset < la.offset) return this.findIt((int)offset);
        if (offset >= la.endOffset) return this.findIt((int)offset);
        return la;
    }

    private Component findIt(int offset) {
        int low = 0;
        int high = this.componentCount;
        while (low <= high) {
            int mid = low + high >>> 1;
            Component c = this.components[mid];
            if (offset >= c.endOffset) {
                low = mid + 1;
                continue;
            }
            if (offset >= c.offset) {
                this.lastAccessed = c;
                return c;
            }
            high = mid - 1;
        }
        throw new Error((String)"should not reach here");
    }

    @Override
    public int nioBufferCount() {
        int size = this.componentCount;
        switch (size) {
            case 0: {
                return 1;
            }
            case 1: {
                return this.components[0].buf.nioBufferCount();
            }
        }
        int count = 0;
        int i = 0;
        while (i < size) {
            count += this.components[i].buf.nioBufferCount();
            ++i;
        }
        return count;
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        switch (this.componentCount) {
            case 0: {
                return EMPTY_NIO_BUFFER;
            }
            case 1: {
                return this.components[0].internalNioBuffer((int)index, (int)length);
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex((int)index, (int)length);
        switch (this.componentCount) {
            case 0: {
                return EMPTY_NIO_BUFFER;
            }
            case 1: {
                Component c = this.components[0];
                ByteBuf buf = c.buf;
                if (buf.nioBufferCount() != 1) break;
                return buf.nioBuffer((int)c.idx((int)index), (int)length);
            }
        }
        ByteBuffer[] buffers = this.nioBuffers((int)index, (int)length);
        if (buffers.length == 1) {
            return buffers[0];
        }
        ByteBuffer merged = ByteBuffer.allocate((int)length).order((ByteOrder)this.order());
        ByteBuffer[] arrbyteBuffer = buffers;
        int n = arrbyteBuffer.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                merged.flip();
                return merged;
            }
            ByteBuffer buf = arrbyteBuffer[n2];
            merged.put((ByteBuffer)buf);
            ++n2;
        } while (true);
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

    public CompositeByteBuf consolidate() {
        this.ensureAccessible();
        this.consolidate0((int)0, (int)this.componentCount);
        return this;
    }

    public CompositeByteBuf consolidate(int cIndex, int numComponents) {
        this.checkComponentIndex((int)cIndex, (int)numComponents);
        this.consolidate0((int)cIndex, (int)numComponents);
        return this;
    }

    private void consolidate0(int cIndex, int numComponents) {
        if (numComponents <= 1) {
            return;
        }
        int endCIndex = cIndex + numComponents;
        int startOffset = cIndex != 0 ? this.components[cIndex].offset : 0;
        int capacity = this.components[endCIndex - 1].endOffset - startOffset;
        ByteBuf consolidated = this.allocBuffer((int)capacity);
        for (int i = cIndex; i < endCIndex; ++i) {
            this.components[i].transferTo((ByteBuf)consolidated);
        }
        this.lastAccessed = null;
        this.removeCompRange((int)(cIndex + 1), (int)endCIndex);
        this.components[cIndex] = this.newComponent((ByteBuf)consolidated, (int)0);
        if (cIndex == 0) {
            if (numComponents == this.componentCount) return;
        }
        this.updateComponentOffsets((int)cIndex);
    }

    public CompositeByteBuf discardReadComponents() {
        int firstComponentId;
        this.ensureAccessible();
        int readerIndex = this.readerIndex();
        if (readerIndex == 0) {
            return this;
        }
        int writerIndex = this.writerIndex();
        if (readerIndex == writerIndex && writerIndex == this.capacity()) {
            int i = 0;
            int size = this.componentCount;
            do {
                if (i >= size) {
                    this.lastAccessed = null;
                    this.clearComps();
                    this.setIndex((int)0, (int)0);
                    this.adjustMarkers((int)readerIndex);
                    return this;
                }
                this.components[i].free();
                ++i;
            } while (true);
        }
        Component c = null;
        int size = this.componentCount;
        for (firstComponentId = 0; firstComponentId < size; ++firstComponentId) {
            c = this.components[firstComponentId];
            if (c.endOffset > readerIndex) break;
            c.free();
        }
        if (firstComponentId == 0) {
            return this;
        }
        Component la = this.lastAccessed;
        if (la != null && la.endOffset <= readerIndex) {
            this.lastAccessed = null;
        }
        this.removeCompRange((int)0, (int)firstComponentId);
        int offset = c.offset;
        this.updateComponentOffsets((int)0);
        this.setIndex((int)(readerIndex - offset), (int)(writerIndex - offset));
        this.adjustMarkers((int)offset);
        return this;
    }

    @Override
    public CompositeByteBuf discardReadBytes() {
        Component la;
        int firstComponentId;
        this.ensureAccessible();
        int readerIndex = this.readerIndex();
        if (readerIndex == 0) {
            return this;
        }
        int writerIndex = this.writerIndex();
        if (readerIndex == writerIndex && writerIndex == this.capacity()) {
            int i = 0;
            int size = this.componentCount;
            do {
                if (i >= size) {
                    this.lastAccessed = null;
                    this.clearComps();
                    this.setIndex((int)0, (int)0);
                    this.adjustMarkers((int)readerIndex);
                    return this;
                }
                this.components[i].free();
                ++i;
            } while (true);
        }
        Component c = null;
        int size = this.componentCount;
        for (firstComponentId = 0; firstComponentId < size; ++firstComponentId) {
            c = this.components[firstComponentId];
            if (c.endOffset > readerIndex) break;
            c.free();
        }
        int trimmedBytes = readerIndex - c.offset;
        c.offset = 0;
        c.endOffset -= readerIndex;
        c.srcAdjustment += readerIndex;
        c.adjustment += readerIndex;
        ByteBuf slice = ((Component)c).slice;
        if (slice != null) {
            ((Component)c).slice = (ByteBuf)slice.slice((int)trimmedBytes, (int)c.length());
        }
        if ((la = this.lastAccessed) != null && la.endOffset <= readerIndex) {
            this.lastAccessed = null;
        }
        this.removeCompRange((int)0, (int)firstComponentId);
        this.updateComponentOffsets((int)0);
        this.setIndex((int)0, (int)(writerIndex - readerIndex));
        this.adjustMarkers((int)readerIndex);
        return this;
    }

    private ByteBuf allocBuffer(int capacity) {
        ByteBuf byteBuf;
        if (this.direct) {
            byteBuf = this.alloc().directBuffer((int)capacity);
            return byteBuf;
        }
        byteBuf = this.alloc().heapBuffer((int)capacity);
        return byteBuf;
    }

    @Override
    public String toString() {
        String result = super.toString();
        result = result.substring((int)0, (int)(result.length() - 1));
        return result + ", components=" + this.componentCount + ')';
    }

    @Override
    public CompositeByteBuf readerIndex(int readerIndex) {
        super.readerIndex((int)readerIndex);
        return this;
    }

    @Override
    public CompositeByteBuf writerIndex(int writerIndex) {
        super.writerIndex((int)writerIndex);
        return this;
    }

    @Override
    public CompositeByteBuf setIndex(int readerIndex, int writerIndex) {
        super.setIndex((int)readerIndex, (int)writerIndex);
        return this;
    }

    @Override
    public CompositeByteBuf clear() {
        super.clear();
        return this;
    }

    @Override
    public CompositeByteBuf markReaderIndex() {
        super.markReaderIndex();
        return this;
    }

    @Override
    public CompositeByteBuf resetReaderIndex() {
        super.resetReaderIndex();
        return this;
    }

    @Override
    public CompositeByteBuf markWriterIndex() {
        super.markWriterIndex();
        return this;
    }

    @Override
    public CompositeByteBuf resetWriterIndex() {
        super.resetWriterIndex();
        return this;
    }

    @Override
    public CompositeByteBuf ensureWritable(int minWritableBytes) {
        super.ensureWritable((int)minWritableBytes);
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst) {
        return this.getBytes((int)index, (ByteBuf)dst, (int)dst.writableBytes());
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.getBytes((int)index, (ByteBuf)dst, (int)dst.writerIndex(), (int)length);
        dst.writerIndex((int)(dst.writerIndex() + length));
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, byte[] dst) {
        return this.getBytes((int)index, (byte[])dst, (int)0, (int)dst.length);
    }

    @Override
    public CompositeByteBuf setBoolean(int index, boolean value) {
        int n;
        if (value) {
            n = 1;
            return this.setByte((int)index, (int)n);
        }
        n = 0;
        return this.setByte((int)index, (int)n);
    }

    @Override
    public CompositeByteBuf setChar(int index, int value) {
        return this.setShort((int)index, (int)value);
    }

    @Override
    public CompositeByteBuf setFloat(int index, float value) {
        return this.setInt((int)index, (int)Float.floatToRawIntBits((float)value));
    }

    @Override
    public CompositeByteBuf setDouble(int index, double value) {
        return this.setLong((int)index, (long)Double.doubleToRawLongBits((double)value));
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src) {
        super.setBytes((int)index, (ByteBuf)src, (int)src.readableBytes());
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src, int length) {
        super.setBytes((int)index, (ByteBuf)src, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, byte[] src) {
        return this.setBytes((int)index, (byte[])src, (int)0, (int)src.length);
    }

    @Override
    public CompositeByteBuf setZero(int index, int length) {
        super.setZero((int)index, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst) {
        super.readBytes((ByteBuf)dst, (int)dst.writableBytes());
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst, int length) {
        super.readBytes((ByteBuf)dst, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        super.readBytes((ByteBuf)dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(byte[] dst) {
        super.readBytes((byte[])dst, (int)0, (int)dst.length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        super.readBytes((byte[])dst, (int)dstIndex, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuffer dst) {
        super.readBytes((ByteBuffer)dst);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(OutputStream out, int length) throws IOException {
        super.readBytes((OutputStream)out, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf skipBytes(int length) {
        super.skipBytes((int)length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBoolean(boolean value) {
        this.writeByte((int)(value ? 1 : 0));
        return this;
    }

    @Override
    public CompositeByteBuf writeByte(int value) {
        this.ensureWritable0((int)1);
        this._setByte((int)this.writerIndex++, (int)value);
        return this;
    }

    @Override
    public CompositeByteBuf writeShort(int value) {
        super.writeShort((int)value);
        return this;
    }

    @Override
    public CompositeByteBuf writeMedium(int value) {
        super.writeMedium((int)value);
        return this;
    }

    @Override
    public CompositeByteBuf writeInt(int value) {
        super.writeInt((int)value);
        return this;
    }

    @Override
    public CompositeByteBuf writeLong(long value) {
        super.writeLong((long)value);
        return this;
    }

    @Override
    public CompositeByteBuf writeChar(int value) {
        super.writeShort((int)value);
        return this;
    }

    @Override
    public CompositeByteBuf writeFloat(float value) {
        super.writeInt((int)Float.floatToRawIntBits((float)value));
        return this;
    }

    @Override
    public CompositeByteBuf writeDouble(double value) {
        super.writeLong((long)Double.doubleToRawLongBits((double)value));
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src) {
        super.writeBytes((ByteBuf)src, (int)src.readableBytes());
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src, int length) {
        super.writeBytes((ByteBuf)src, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        super.writeBytes((ByteBuf)src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(byte[] src) {
        super.writeBytes((byte[])src, (int)0, (int)src.length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        super.writeBytes((byte[])src, (int)srcIndex, (int)length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuffer src) {
        super.writeBytes((ByteBuffer)src);
        return this;
    }

    @Override
    public CompositeByteBuf writeZero(int length) {
        super.writeZero((int)length);
        return this;
    }

    @Override
    public CompositeByteBuf retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public CompositeByteBuf retain() {
        super.retain();
        return this;
    }

    @Override
    public CompositeByteBuf touch() {
        return this;
    }

    @Override
    public CompositeByteBuf touch(Object hint) {
        return this;
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers((int)this.readerIndex(), (int)this.readableBytes());
    }

    @Override
    public CompositeByteBuf discardSomeReadBytes() {
        return this.discardReadComponents();
    }

    @Override
    protected void deallocate() {
        if (this.freed) {
            return;
        }
        this.freed = true;
        int i = 0;
        int size = this.componentCount;
        while (i < size) {
            this.components[i].free();
            ++i;
        }
    }

    @Override
    boolean isAccessible() {
        if (this.freed) return false;
        return true;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    private void clearComps() {
        this.removeCompRange((int)0, (int)this.componentCount);
    }

    private void removeComp(int i) {
        this.removeCompRange((int)i, (int)(i + 1));
    }

    private void removeCompRange(int from, int to) {
        int newSize;
        if (from >= to) {
            return;
        }
        int size = this.componentCount;
        if (!$assertionsDisabled) {
            if (from < 0) throw new AssertionError();
            if (to > size) {
                throw new AssertionError();
            }
        }
        if (to < size) {
            System.arraycopy((Object)this.components, (int)to, (Object)this.components, (int)from, (int)(size - to));
        }
        int i = newSize = size - to + from;
        do {
            if (i >= size) {
                this.componentCount = newSize;
                return;
            }
            this.components[i] = null;
            ++i;
        } while (true);
    }

    private void addComp(int i, Component c) {
        this.shiftComps((int)i, (int)1);
        this.components[i] = c;
    }

    private void shiftComps(int i, int count) {
        int size = this.componentCount;
        int newSize = size + count;
        if (!$assertionsDisabled) {
            if (i < 0) throw new AssertionError();
            if (i > size) throw new AssertionError();
            if (count <= 0) {
                throw new AssertionError();
            }
        }
        if (newSize > this.components.length) {
            Component[] newArr;
            int newArrSize = Math.max((int)(size + (size >> 1)), (int)newSize);
            if (i == size) {
                newArr = (Component[])Arrays.copyOf(this.components, (int)newArrSize, Component[].class);
            } else {
                newArr = new Component[newArrSize];
                if (i > 0) {
                    System.arraycopy((Object)this.components, (int)0, (Object)newArr, (int)0, (int)i);
                }
                if (i < size) {
                    System.arraycopy((Object)this.components, (int)i, (Object)newArr, (int)(i + count), (int)(size - i));
                }
            }
            this.components = newArr;
        } else if (i < size) {
            System.arraycopy((Object)this.components, (int)i, (Object)this.components, (int)(i + count), (int)(size - i));
        }
        this.componentCount = newSize;
    }

    static /* synthetic */ Component[] access$200(CompositeByteBuf x0) {
        return x0.components;
    }
}

