/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.jni.zlib;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.jni.zlib.NativeCompressImpl;

public class NativeZlib
implements BungeeZlib {
    private final NativeCompressImpl nativeCompress = new NativeCompressImpl();
    private boolean compress;
    private long ctx;

    @Override
    public void init(boolean compress, int level) {
        this.free();
        this.compress = compress;
        this.ctx = this.nativeCompress.init((boolean)compress, (int)level);
    }

    @Override
    public void free() {
        if (this.ctx != 0L) {
            this.nativeCompress.end((long)this.ctx, (boolean)this.compress);
            this.ctx = 0L;
        }
        this.nativeCompress.consumed = 0;
        this.nativeCompress.finished = false;
    }

    @Override
    public void process(ByteBuf in, ByteBuf out) throws DataFormatException {
        in.memoryAddress();
        out.memoryAddress();
        Preconditions.checkState((boolean)(this.ctx != 0L), (Object)"Invalid pointer to compress!");
        while (!this.nativeCompress.finished && (this.compress || in.isReadable())) {
            out.ensureWritable((int)8192);
            int processed = this.nativeCompress.process((long)this.ctx, (long)(in.memoryAddress() + (long)in.readerIndex()), (int)in.readableBytes(), (long)(out.memoryAddress() + (long)out.writerIndex()), (int)out.writableBytes(), (boolean)this.compress);
            in.readerIndex((int)(in.readerIndex() + this.nativeCompress.consumed));
            out.writerIndex((int)(out.writerIndex() + processed));
        }
        this.nativeCompress.reset((long)this.ctx, (boolean)this.compress);
        this.nativeCompress.consumed = 0;
        this.nativeCompress.finished = false;
    }

    public NativeCompressImpl getNativeCompress() {
        return this.nativeCompress;
    }
}

