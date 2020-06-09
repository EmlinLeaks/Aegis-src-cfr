/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.nio.charset.Charset;
import java.security.MessageDigest;

final class WebSocketUtil {
    private static final FastThreadLocal<MessageDigest> MD5 = new FastThreadLocal<MessageDigest>(){

        protected MessageDigest initialValue() throws java.lang.Exception {
            try {
                return MessageDigest.getInstance((String)"MD5");
            }
            catch (java.security.NoSuchAlgorithmException e) {
                throw new java.lang.InternalError((String)"MD5 not supported on this platform - Outdated?");
            }
        }
    };
    private static final FastThreadLocal<MessageDigest> SHA1 = new FastThreadLocal<MessageDigest>(){

        protected MessageDigest initialValue() throws java.lang.Exception {
            try {
                return MessageDigest.getInstance((String)"SHA1");
            }
            catch (java.security.NoSuchAlgorithmException e) {
                throw new java.lang.InternalError((String)"SHA-1 not supported on this platform - Outdated?");
            }
        }
    };

    static byte[] md5(byte[] data) {
        return WebSocketUtil.digest(MD5, (byte[])data);
    }

    static byte[] sha1(byte[] data) {
        return WebSocketUtil.digest(SHA1, (byte[])data);
    }

    private static byte[] digest(FastThreadLocal<MessageDigest> digestFastThreadLocal, byte[] data) {
        MessageDigest digest = digestFastThreadLocal.get();
        digest.reset();
        return digest.digest((byte[])data);
    }

    @SuppressJava6Requirement(reason="Guarded with java version check")
    static String base64(byte[] data) {
        if (PlatformDependent.javaVersion() >= 8) {
            return java.util.Base64.getEncoder().encodeToString((byte[])data);
        }
        ByteBuf encodedData = Unpooled.wrappedBuffer((byte[])data);
        ByteBuf encoded = Base64.encode((ByteBuf)encodedData);
        String encodedString = encoded.toString((Charset)CharsetUtil.UTF_8);
        encoded.release();
        return encodedString;
    }

    static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        PlatformDependent.threadLocalRandom().nextBytes((byte[])bytes);
        return bytes;
    }

    static int randomNumber(int minimum, int maximum) {
        assert (minimum < maximum);
        double fraction = PlatformDependent.threadLocalRandom().nextDouble();
        return (int)((double)minimum + fraction * (double)(maximum - minimum));
    }

    private WebSocketUtil() {
    }
}

