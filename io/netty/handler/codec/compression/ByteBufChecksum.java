/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.ByteBufChecksum;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

abstract class ByteBufChecksum
implements Checksum {
    private static final Method ADLER32_UPDATE_METHOD = ByteBufChecksum.updateByteBuffer((Checksum)new Adler32());
    private static final Method CRC32_UPDATE_METHOD = ByteBufChecksum.updateByteBuffer((Checksum)new CRC32());
    private final ByteProcessor updateProcessor = new ByteProcessor((ByteBufChecksum)this){
        final /* synthetic */ ByteBufChecksum this$0;
        {
            this.this$0 = this$0;
        }

        public boolean process(byte value) throws java.lang.Exception {
            this.this$0.update((int)value);
            return true;
        }
    };

    ByteBufChecksum() {
    }

    private static Method updateByteBuffer(Checksum checksum) {
        if (PlatformDependent.javaVersion() < 8) return null;
        try {
            Method method = checksum.getClass().getDeclaredMethod((String)"update", ByteBuffer.class);
            method.invoke((Object)checksum, (Object[])new Object[]{ByteBuffer.allocate((int)1)});
            return method;
        }
        catch (Throwable ignore) {
            return null;
        }
    }

    static ByteBufChecksum wrapChecksum(Checksum checksum) {
        ObjectUtil.checkNotNull(checksum, (String)"checksum");
        if (checksum instanceof ByteBufChecksum) {
            return (ByteBufChecksum)checksum;
        }
        if (checksum instanceof Adler32 && ADLER32_UPDATE_METHOD != null) {
            return new ReflectiveByteBufChecksum((Checksum)checksum, (Method)ADLER32_UPDATE_METHOD);
        }
        if (!(checksum instanceof CRC32)) return new SlowByteBufChecksum((Checksum)checksum);
        if (CRC32_UPDATE_METHOD == null) return new SlowByteBufChecksum((Checksum)checksum);
        return new ReflectiveByteBufChecksum((Checksum)checksum, (Method)CRC32_UPDATE_METHOD);
    }

    public void update(ByteBuf b, int off, int len) {
        if (b.hasArray()) {
            this.update((byte[])b.array(), (int)(b.arrayOffset() + off), (int)len);
            return;
        }
        b.forEachByte((int)off, (int)len, (ByteProcessor)this.updateProcessor);
    }
}

