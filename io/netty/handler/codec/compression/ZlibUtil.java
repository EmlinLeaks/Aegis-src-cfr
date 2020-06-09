/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.jcraft.jzlib.Deflater
 *  com.jcraft.jzlib.Inflater
 *  com.jcraft.jzlib.JZlib
 *  com.jcraft.jzlib.JZlib$WrapperType
 */
package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.ZlibUtil;
import io.netty.handler.codec.compression.ZlibWrapper;

final class ZlibUtil {
    static void fail(Inflater z, String message, int resultCode) {
        throw ZlibUtil.inflaterException((Inflater)z, (String)message, (int)resultCode);
    }

    static void fail(Deflater z, String message, int resultCode) {
        throw ZlibUtil.deflaterException((Deflater)z, (String)message, (int)resultCode);
    }

    static DecompressionException inflaterException(Inflater z, String message, int resultCode) {
        String string;
        if (z.msg != null) {
            string = ": " + z.msg;
            return new DecompressionException((String)(message + " (" + resultCode + ')' + string));
        }
        string = "";
        return new DecompressionException((String)(message + " (" + resultCode + ')' + string));
    }

    static CompressionException deflaterException(Deflater z, String message, int resultCode) {
        String string;
        if (z.msg != null) {
            string = ": " + z.msg;
            return new CompressionException((String)(message + " (" + resultCode + ')' + string));
        }
        string = "";
        return new CompressionException((String)(message + " (" + resultCode + ')' + string));
    }

    static JZlib.WrapperType convertWrapperType(ZlibWrapper wrapper) {
        switch (1.$SwitchMap$io$netty$handler$codec$compression$ZlibWrapper[wrapper.ordinal()]) {
            case 1: {
                return JZlib.W_NONE;
            }
            case 2: {
                return JZlib.W_ZLIB;
            }
            case 3: {
                return JZlib.W_GZIP;
            }
            case 4: {
                return JZlib.W_ANY;
            }
        }
        throw new Error();
    }

    static int wrapperOverhead(ZlibWrapper wrapper) {
        switch (1.$SwitchMap$io$netty$handler$codec$compression$ZlibWrapper[wrapper.ordinal()]) {
            case 1: {
                return 0;
            }
            case 2: 
            case 4: {
                return 2;
            }
            case 3: {
                return 10;
            }
        }
        throw new Error();
    }

    private ZlibUtil() {
    }
}

