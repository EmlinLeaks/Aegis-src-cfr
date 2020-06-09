/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.NetUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLHandshakeException;

final class SslUtils {
    static final Set<String> TLSV13_CIPHERS = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList("TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256", "TLS_AES_128_GCM_SHA256", "TLS_AES_128_CCM_8_SHA256", "TLS_AES_128_CCM_SHA256")));
    static final String PROTOCOL_SSL_V2_HELLO = "SSLv2Hello";
    static final String PROTOCOL_SSL_V2 = "SSLv2";
    static final String PROTOCOL_SSL_V3 = "SSLv3";
    static final String PROTOCOL_TLS_V1 = "TLSv1";
    static final String PROTOCOL_TLS_V1_1 = "TLSv1.1";
    static final String PROTOCOL_TLS_V1_2 = "TLSv1.2";
    static final String PROTOCOL_TLS_V1_3 = "TLSv1.3";
    static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
    static final int SSL_CONTENT_TYPE_CHANGE_CIPHER_SPEC = 20;
    static final int SSL_CONTENT_TYPE_ALERT = 21;
    static final int SSL_CONTENT_TYPE_HANDSHAKE = 22;
    static final int SSL_CONTENT_TYPE_APPLICATION_DATA = 23;
    static final int SSL_CONTENT_TYPE_EXTENSION_HEARTBEAT = 24;
    static final int SSL_RECORD_HEADER_LENGTH = 5;
    static final int NOT_ENOUGH_DATA = -1;
    static final int NOT_ENCRYPTED = -2;
    static final String[] DEFAULT_CIPHER_SUITES;
    static final String[] DEFAULT_TLSV13_CIPHER_SUITES;
    static final String[] TLSV13_CIPHER_SUITES;

    static void addIfSupported(Set<String> supported, List<String> enabled, String ... names) {
        String[] arrstring = names;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String n3 = arrstring[n2];
            if (supported.contains((Object)n3)) {
                enabled.add((String)n3);
            }
            ++n2;
        }
    }

    static void useFallbackCiphersIfDefaultIsEmpty(List<String> defaultCiphers, Iterable<String> fallbackCiphers) {
        if (!defaultCiphers.isEmpty()) return;
        Iterator<String> iterator = fallbackCiphers.iterator();
        while (iterator.hasNext()) {
            String cipher = iterator.next();
            if (cipher.startsWith((String)"SSL_") || cipher.contains((CharSequence)"_RC4_")) continue;
            defaultCiphers.add((String)cipher);
        }
    }

    static void useFallbackCiphersIfDefaultIsEmpty(List<String> defaultCiphers, String ... fallbackCiphers) {
        SslUtils.useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, Arrays.asList(fallbackCiphers));
    }

    static SSLHandshakeException toSSLHandshakeException(Throwable e) {
        if (!(e instanceof SSLHandshakeException)) return (SSLHandshakeException)new SSLHandshakeException((String)e.getMessage()).initCause((Throwable)e);
        return (SSLHandshakeException)e;
    }

    /*
     * Unable to fully structure code
     */
    static int getEncryptedPacketLength(ByteBuf buffer, int offset) {
        packetLength = 0;
        switch (buffer.getUnsignedByte((int)offset)) {
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: {
                tls = true;
                ** break;
            }
        }
        tls = false;
lbl7: // 2 sources:
        if (tls) {
            majorVersion = buffer.getUnsignedByte((int)(offset + 1));
            if (majorVersion == 3) {
                packetLength = SslUtils.unsignedShortBE((ByteBuf)buffer, (int)(offset + 3)) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            } else {
                tls = false;
            }
        }
        if (tls != false) return packetLength;
        headerLength = (buffer.getUnsignedByte((int)offset) & 128) != 0 ? 2 : 3;
        majorVersion = buffer.getUnsignedByte((int)(offset + headerLength + 1));
        if (majorVersion != 2) {
            if (majorVersion != 3) return -2;
        }
        packetLength = headerLength == 2 ? (SslUtils.shortBE((ByteBuf)buffer, (int)offset) & 32767) + 2 : (SslUtils.shortBE((ByteBuf)buffer, (int)offset) & 16383) + 3;
        if (packetLength > headerLength) return packetLength;
        return -1;
    }

    private static int unsignedShortBE(ByteBuf buffer, int offset) {
        int n;
        if (buffer.order() == ByteOrder.BIG_ENDIAN) {
            n = buffer.getUnsignedShort((int)offset);
            return n;
        }
        n = buffer.getUnsignedShortLE((int)offset);
        return n;
    }

    private static short shortBE(ByteBuf buffer, int offset) {
        short s;
        if (buffer.order() == ByteOrder.BIG_ENDIAN) {
            s = buffer.getShort((int)offset);
            return s;
        }
        s = buffer.getShortLE((int)offset);
        return s;
    }

    private static short unsignedByte(byte b) {
        return (short)(b & 255);
    }

    private static int unsignedShortBE(ByteBuffer buffer, int offset) {
        return SslUtils.shortBE((ByteBuffer)buffer, (int)offset) & 65535;
    }

    private static short shortBE(ByteBuffer buffer, int offset) {
        short s;
        if (buffer.order() == ByteOrder.BIG_ENDIAN) {
            s = buffer.getShort((int)offset);
            return s;
        }
        s = ByteBufUtil.swapShort((short)buffer.getShort((int)offset));
        return s;
    }

    static int getEncryptedPacketLength(ByteBuffer[] buffers, int offset) {
        ByteBuffer buffer = buffers[offset];
        if (buffer.remaining() >= 5) {
            return SslUtils.getEncryptedPacketLength((ByteBuffer)buffer);
        }
        ByteBuffer tmp = ByteBuffer.allocate((int)5);
        do {
            if ((buffer = buffers[offset++].duplicate()).remaining() > tmp.remaining()) {
                buffer.limit((int)(buffer.position() + tmp.remaining()));
            }
            tmp.put((ByteBuffer)buffer);
        } while (tmp.hasRemaining());
        tmp.flip();
        return SslUtils.getEncryptedPacketLength((ByteBuffer)tmp);
    }

    /*
     * Unable to fully structure code
     */
    private static int getEncryptedPacketLength(ByteBuffer buffer) {
        packetLength = 0;
        pos = buffer.position();
        switch (SslUtils.unsignedByte((byte)buffer.get((int)pos))) {
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: {
                tls = true;
                ** break;
            }
        }
        tls = false;
lbl8: // 2 sources:
        if (tls) {
            majorVersion = SslUtils.unsignedByte((byte)buffer.get((int)(pos + 1)));
            if (majorVersion == 3) {
                packetLength = SslUtils.unsignedShortBE((ByteBuffer)buffer, (int)(pos + 3)) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            } else {
                tls = false;
            }
        }
        if (tls != false) return packetLength;
        headerLength = (SslUtils.unsignedByte((byte)buffer.get((int)pos)) & 128) != 0 ? 2 : 3;
        majorVersion = SslUtils.unsignedByte((byte)buffer.get((int)(pos + headerLength + 1)));
        if (majorVersion != 2) {
            if (majorVersion != 3) return -2;
        }
        packetLength = headerLength == 2 ? (SslUtils.shortBE((ByteBuffer)buffer, (int)pos) & 32767) + 2 : (SslUtils.shortBE((ByteBuffer)buffer, (int)pos) & 16383) + 3;
        if (packetLength > headerLength) return packetLength;
        return -1;
    }

    static void handleHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean notify) {
        ctx.flush();
        if (notify) {
            ctx.fireUserEventTriggered((Object)new SslHandshakeCompletionEvent((Throwable)cause));
        }
        ctx.close();
    }

    static void zeroout(ByteBuf buffer) {
        if (buffer.isReadOnly()) return;
        buffer.setZero((int)0, (int)buffer.capacity());
    }

    static void zerooutAndRelease(ByteBuf buffer) {
        SslUtils.zeroout((ByteBuf)buffer);
        buffer.release();
    }

    static ByteBuf toBase64(ByteBufAllocator allocator, ByteBuf src) {
        ByteBuf dst = Base64.encode((ByteBuf)src, (int)src.readerIndex(), (int)src.readableBytes(), (boolean)true, (Base64Dialect)Base64Dialect.STANDARD, (ByteBufAllocator)allocator);
        src.readerIndex((int)src.writerIndex());
        return dst;
    }

    static boolean isValidHostNameForSNI(String hostname) {
        if (hostname == null) return false;
        if (hostname.indexOf((int)46) <= 0) return false;
        if (hostname.endsWith((String)".")) return false;
        if (NetUtil.isValidIpV4Address((String)hostname)) return false;
        if (NetUtil.isValidIpV6Address((String)hostname)) return false;
        return true;
    }

    static boolean isTLSv13Cipher(String cipher) {
        return TLSV13_CIPHERS.contains((Object)cipher);
    }

    private SslUtils() {
    }

    static {
        TLSV13_CIPHER_SUITES = new String[]{"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384"};
        DEFAULT_TLSV13_CIPHER_SUITES = PlatformDependent.javaVersion() >= 11 ? TLSV13_CIPHER_SUITES : EmptyArrays.EMPTY_STRINGS;
        ArrayList<String> defaultCiphers = new ArrayList<String>();
        defaultCiphers.add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
        defaultCiphers.add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        defaultCiphers.add("TLS_RSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_RSA_WITH_AES_128_CBC_SHA");
        defaultCiphers.add("TLS_RSA_WITH_AES_256_CBC_SHA");
        Collections.addAll(defaultCiphers, DEFAULT_TLSV13_CIPHER_SUITES);
        DEFAULT_CIPHER_SUITES = defaultCiphers.toArray(new String[0]);
    }
}

