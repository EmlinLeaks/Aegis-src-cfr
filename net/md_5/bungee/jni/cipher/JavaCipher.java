/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.jni.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.jni.cipher.JavaCipher;

public class JavaCipher
implements BungeeCipher {
    private final Cipher cipher = Cipher.getInstance((String)"AES/CFB8/NoPadding");
    private static final ThreadLocal<byte[]> heapInLocal = new EmptyByteThreadLocal(null);
    private static final ThreadLocal<byte[]> heapOutLocal = new EmptyByteThreadLocal(null);

    @Override
    public void init(boolean forEncryption, SecretKey key) throws GeneralSecurityException {
        int mode = forEncryption ? 1 : 2;
        this.cipher.init((int)mode, (Key)key, (AlgorithmParameterSpec)new IvParameterSpec((byte[])key.getEncoded()));
    }

    @Override
    public void cipher(ByteBuf in, ByteBuf out) throws ShortBufferException {
        int outputSize;
        int readableBytes = in.readableBytes();
        byte[] heapIn = this.bufToByte((ByteBuf)in);
        byte[] heapOut = heapOutLocal.get();
        if (heapOut.length < (outputSize = this.cipher.getOutputSize((int)readableBytes))) {
            heapOut = new byte[outputSize];
            heapOutLocal.set((byte[])heapOut);
        }
        out.writeBytes((byte[])heapOut, (int)0, (int)this.cipher.update((byte[])heapIn, (int)0, (int)readableBytes, (byte[])heapOut));
    }

    @Override
    public ByteBuf cipher(ChannelHandlerContext ctx, ByteBuf in) throws ShortBufferException {
        int readableBytes = in.readableBytes();
        byte[] heapIn = this.bufToByte((ByteBuf)in);
        ByteBuf heapOut = ctx.alloc().heapBuffer((int)this.cipher.getOutputSize((int)readableBytes));
        heapOut.writerIndex((int)this.cipher.update((byte[])heapIn, (int)0, (int)readableBytes, (byte[])heapOut.array(), (int)heapOut.arrayOffset()));
        return heapOut;
    }

    @Override
    public void free() {
    }

    private byte[] bufToByte(ByteBuf in) {
        int readableBytes;
        byte[] heapIn = heapInLocal.get();
        if (heapIn.length < (readableBytes = in.readableBytes())) {
            heapIn = new byte[readableBytes];
            heapInLocal.set((byte[])heapIn);
        }
        in.readBytes((byte[])heapIn, (int)0, (int)readableBytes);
        return heapIn;
    }
}

