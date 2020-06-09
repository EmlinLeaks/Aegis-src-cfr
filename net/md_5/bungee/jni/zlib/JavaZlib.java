/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.jni.zlib;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.md_5.bungee.jni.zlib.BungeeZlib;

public class JavaZlib
implements BungeeZlib {
    private final byte[] buffer = new byte[8192];
    private boolean compress;
    private Deflater deflater;
    private Inflater inflater;

    @Override
    public void init(boolean compress, int level) {
        this.compress = compress;
        this.free();
        if (compress) {
            this.deflater = new Deflater((int)level);
            return;
        }
        this.inflater = new Inflater();
    }

    @Override
    public void free() {
        if (this.deflater != null) {
            this.deflater.end();
        }
        if (this.inflater == null) return;
        this.inflater.end();
    }

    @Override
    public void process(ByteBuf in, ByteBuf out) throws DataFormatException {
        byte[] inData = new byte[in.readableBytes()];
        in.readBytes((byte[])inData);
        if (this.compress) {
            this.deflater.setInput((byte[])inData);
            this.deflater.finish();
            do {
                if (this.deflater.finished()) {
                    this.deflater.reset();
                    return;
                }
                int count = this.deflater.deflate((byte[])this.buffer);
                out.writeBytes((byte[])this.buffer, (int)0, (int)count);
            } while (true);
        }
        this.inflater.setInput((byte[])inData);
        while (!this.inflater.finished() && this.inflater.getTotalIn() < inData.length) {
            int count = this.inflater.inflate((byte[])this.buffer);
            out.writeBytes((byte[])this.buffer, (int)0, (int)count);
        }
        this.inflater.reset();
    }
}

