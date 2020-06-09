/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.jni.cipher;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.jni.cipher.NativeCipherImpl;

public class NativeCipher
implements BungeeCipher {
    private final NativeCipherImpl nativeCipher = new NativeCipherImpl();
    private long ctx;

    @Override
    public void init(boolean forEncryption, SecretKey key) throws GeneralSecurityException {
        Preconditions.checkArgument((boolean)(key.getEncoded().length == 16), (Object)"Invalid key size");
        this.free();
        this.ctx = this.nativeCipher.init((boolean)forEncryption, (byte[])key.getEncoded());
    }

    @Override
    public void free() {
        if (this.ctx == 0L) return;
        this.nativeCipher.free((long)this.ctx);
        this.ctx = 0L;
    }

    @Override
    public void cipher(ByteBuf in, ByteBuf out) throws GeneralSecurityException {
        in.memoryAddress();
        out.memoryAddress();
        Preconditions.checkState((boolean)(this.ctx != 0L), (Object)"Invalid pointer to AES key!");
        int length = in.readableBytes();
        if (length <= 0) {
            return;
        }
        out.ensureWritable((int)length);
        this.nativeCipher.cipher((long)this.ctx, (long)(in.memoryAddress() + (long)in.readerIndex()), (long)(out.memoryAddress() + (long)out.writerIndex()), (int)length);
        in.readerIndex((int)in.writerIndex());
        out.writerIndex((int)(out.writerIndex() + length));
    }

    @Override
    public ByteBuf cipher(ChannelHandlerContext ctx, ByteBuf in) throws GeneralSecurityException {
        int readableBytes = in.readableBytes();
        ByteBuf heapOut = ctx.alloc().directBuffer((int)readableBytes);
        this.cipher((ByteBuf)in, (ByteBuf)heapOut);
        return heapOut;
    }

    public NativeCipherImpl getNativeCipher() {
        return this.nativeCipher;
    }
}

