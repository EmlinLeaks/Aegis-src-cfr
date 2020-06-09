/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Map;

public final class CharsetUtil {
    public static final Charset UTF_16 = Charset.forName((String)"UTF-16");
    public static final Charset UTF_16BE = Charset.forName((String)"UTF-16BE");
    public static final Charset UTF_16LE = Charset.forName((String)"UTF-16LE");
    public static final Charset UTF_8 = Charset.forName((String)"UTF-8");
    public static final Charset ISO_8859_1 = Charset.forName((String)"ISO-8859-1");
    public static final Charset US_ASCII = Charset.forName((String)"US-ASCII");
    private static final Charset[] CHARSETS = new Charset[]{UTF_16, UTF_16BE, UTF_16LE, UTF_8, ISO_8859_1, US_ASCII};

    public static Charset[] values() {
        return CHARSETS;
    }

    @Deprecated
    public static CharsetEncoder getEncoder(Charset charset) {
        return CharsetUtil.encoder((Charset)charset);
    }

    public static CharsetEncoder encoder(Charset charset, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) {
        ObjectUtil.checkNotNull(charset, (String)"charset");
        CharsetEncoder e = charset.newEncoder();
        e.onMalformedInput((CodingErrorAction)malformedInputAction).onUnmappableCharacter((CodingErrorAction)unmappableCharacterAction);
        return e;
    }

    public static CharsetEncoder encoder(Charset charset, CodingErrorAction codingErrorAction) {
        return CharsetUtil.encoder((Charset)charset, (CodingErrorAction)codingErrorAction, (CodingErrorAction)codingErrorAction);
    }

    public static CharsetEncoder encoder(Charset charset) {
        ObjectUtil.checkNotNull(charset, (String)"charset");
        Map<Charset, CharsetEncoder> map = InternalThreadLocalMap.get().charsetEncoderCache();
        CharsetEncoder e = map.get((Object)charset);
        if (e != null) {
            e.reset().onMalformedInput((CodingErrorAction)CodingErrorAction.REPLACE).onUnmappableCharacter((CodingErrorAction)CodingErrorAction.REPLACE);
            return e;
        }
        e = CharsetUtil.encoder((Charset)charset, (CodingErrorAction)CodingErrorAction.REPLACE, (CodingErrorAction)CodingErrorAction.REPLACE);
        map.put((Charset)charset, (CharsetEncoder)e);
        return e;
    }

    @Deprecated
    public static CharsetDecoder getDecoder(Charset charset) {
        return CharsetUtil.decoder((Charset)charset);
    }

    public static CharsetDecoder decoder(Charset charset, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) {
        ObjectUtil.checkNotNull(charset, (String)"charset");
        CharsetDecoder d = charset.newDecoder();
        d.onMalformedInput((CodingErrorAction)malformedInputAction).onUnmappableCharacter((CodingErrorAction)unmappableCharacterAction);
        return d;
    }

    public static CharsetDecoder decoder(Charset charset, CodingErrorAction codingErrorAction) {
        return CharsetUtil.decoder((Charset)charset, (CodingErrorAction)codingErrorAction, (CodingErrorAction)codingErrorAction);
    }

    public static CharsetDecoder decoder(Charset charset) {
        ObjectUtil.checkNotNull(charset, (String)"charset");
        Map<Charset, CharsetDecoder> map = InternalThreadLocalMap.get().charsetDecoderCache();
        CharsetDecoder d = map.get((Object)charset);
        if (d != null) {
            d.reset().onMalformedInput((CodingErrorAction)CodingErrorAction.REPLACE).onUnmappableCharacter((CodingErrorAction)CodingErrorAction.REPLACE);
            return d;
        }
        d = CharsetUtil.decoder((Charset)charset, (CodingErrorAction)CodingErrorAction.REPLACE, (CodingErrorAction)CodingErrorAction.REPLACE);
        map.put((Charset)charset, (CharsetDecoder)d);
        return d;
    }

    private CharsetUtil() {
    }
}

