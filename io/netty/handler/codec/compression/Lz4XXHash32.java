/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.jpountz.xxhash.XXHash32
 *  net.jpountz.xxhash.XXHashFactory
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.ByteBufChecksum;
import io.netty.handler.codec.compression.CompressionUtil;
import java.nio.ByteBuffer;
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

public final class Lz4XXHash32
extends ByteBufChecksum {
    private static final XXHash32 XXHASH32 = XXHashFactory.fastestInstance().hash32();
    private final int seed;
    private boolean used;
    private int value;

    public Lz4XXHash32(int seed) {
        this.seed = seed;
    }

    @Override
    public void update(int b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(byte[] b, int off, int len) {
        if (this.used) {
            throw new IllegalStateException();
        }
        this.value = XXHASH32.hash((byte[])b, (int)off, (int)len, (int)this.seed);
        this.used = true;
    }

    @Override
    public void update(ByteBuf b, int off, int len) {
        if (this.used) {
            throw new IllegalStateException();
        }
        this.value = b.hasArray() ? XXHASH32.hash((byte[])b.array(), (int)(b.arrayOffset() + off), (int)len, (int)this.seed) : XXHASH32.hash((ByteBuffer)CompressionUtil.safeNioBuffer((ByteBuf)b, (int)off, (int)len), (int)this.seed);
        this.used = true;
    }

    @Override
    public long getValue() {
        if (this.used) return (long)this.value & 0xFFFFFFFL;
        throw new IllegalStateException();
    }

    @Override
    public void reset() {
        this.used = false;
    }
}

