/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.JZlibDecoder;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ZlibCodecFactory {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
    private static final int DEFAULT_JDK_WINDOW_SIZE = 15;
    private static final int DEFAULT_JDK_MEM_LEVEL = 8;
    private static final boolean noJdkZlibDecoder = SystemPropertyUtil.getBoolean((String)"io.netty.noJdkZlibDecoder", (boolean)(PlatformDependent.javaVersion() < 7));
    private static final boolean noJdkZlibEncoder;
    private static final boolean supportsWindowSizeAndMemLevel;

    public static boolean isSupportingWindowSizeAndMemLevel() {
        return supportsWindowSizeAndMemLevel;
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibEncoder((int)compressionLevel);
        if (!noJdkZlibEncoder) return new JdkZlibEncoder((int)compressionLevel);
        return new JZlibEncoder((int)compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibEncoder((ZlibWrapper)wrapper);
        if (!noJdkZlibEncoder) return new JdkZlibEncoder((ZlibWrapper)wrapper);
        return new JZlibEncoder((ZlibWrapper)wrapper);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibEncoder((ZlibWrapper)wrapper, (int)compressionLevel);
        if (!noJdkZlibEncoder) return new JdkZlibEncoder((ZlibWrapper)wrapper, (int)compressionLevel);
        return new JZlibEncoder((ZlibWrapper)wrapper, (int)compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibEncoder((ZlibWrapper)wrapper, (int)compressionLevel, (int)windowBits, (int)memLevel);
        if (noJdkZlibEncoder) return new JZlibEncoder((ZlibWrapper)wrapper, (int)compressionLevel, (int)windowBits, (int)memLevel);
        if (windowBits != 15) return new JZlibEncoder((ZlibWrapper)wrapper, (int)compressionLevel, (int)windowBits, (int)memLevel);
        if (memLevel == 8) return new JdkZlibEncoder((ZlibWrapper)wrapper, (int)compressionLevel);
        return new JZlibEncoder((ZlibWrapper)wrapper, (int)compressionLevel, (int)windowBits, (int)memLevel);
    }

    public static ZlibEncoder newZlibEncoder(byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibEncoder((byte[])dictionary);
        if (!noJdkZlibEncoder) return new JdkZlibEncoder((byte[])dictionary);
        return new JZlibEncoder((byte[])dictionary);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibEncoder((int)compressionLevel, (byte[])dictionary);
        if (!noJdkZlibEncoder) return new JdkZlibEncoder((int)compressionLevel, (byte[])dictionary);
        return new JZlibEncoder((int)compressionLevel, (byte[])dictionary);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibEncoder((int)compressionLevel, (int)windowBits, (int)memLevel, (byte[])dictionary);
        if (noJdkZlibEncoder) return new JZlibEncoder((int)compressionLevel, (int)windowBits, (int)memLevel, (byte[])dictionary);
        if (windowBits != 15) return new JZlibEncoder((int)compressionLevel, (int)windowBits, (int)memLevel, (byte[])dictionary);
        if (memLevel == 8) return new JdkZlibEncoder((int)compressionLevel, (byte[])dictionary);
        return new JZlibEncoder((int)compressionLevel, (int)windowBits, (int)memLevel, (byte[])dictionary);
    }

    public static ZlibDecoder newZlibDecoder() {
        if (PlatformDependent.javaVersion() < 7) return new JZlibDecoder();
        if (!noJdkZlibDecoder) return new JdkZlibDecoder((boolean)true);
        return new JZlibDecoder();
    }

    public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibDecoder((ZlibWrapper)wrapper);
        if (!noJdkZlibDecoder) return new JdkZlibDecoder((ZlibWrapper)wrapper, (boolean)true);
        return new JZlibDecoder((ZlibWrapper)wrapper);
    }

    public static ZlibDecoder newZlibDecoder(byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) return new JZlibDecoder((byte[])dictionary);
        if (!noJdkZlibDecoder) return new JdkZlibDecoder((byte[])dictionary);
        return new JZlibDecoder((byte[])dictionary);
    }

    private ZlibCodecFactory() {
    }

    static {
        logger.debug((String)"-Dio.netty.noJdkZlibDecoder: {}", (Object)Boolean.valueOf((boolean)noJdkZlibDecoder));
        noJdkZlibEncoder = SystemPropertyUtil.getBoolean((String)"io.netty.noJdkZlibEncoder", (boolean)false);
        logger.debug((String)"-Dio.netty.noJdkZlibEncoder: {}", (Object)Boolean.valueOf((boolean)noJdkZlibEncoder));
        supportsWindowSizeAndMemLevel = noJdkZlibDecoder || PlatformDependent.javaVersion() >= 7;
    }
}

