/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CharsetMapping;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.SQLError;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SingleByteCharsetConverter {
    private static final int BYTE_RANGE = 256;
    private static byte[] allBytes;
    private static final Map<String, SingleByteCharsetConverter> CONVERTER_MAP;
    private static final byte[] EMPTY_BYTE_ARRAY;
    private static byte[] unknownCharsMap;
    private char[] byteToChars = new char[256];
    private byte[] charToByteMap = new byte[65536];

    public static synchronized SingleByteCharsetConverter getInstance(String encodingName, Connection conn) throws UnsupportedEncodingException, SQLException {
        SingleByteCharsetConverter instance = CONVERTER_MAP.get((Object)encodingName);
        if (instance != null) return instance;
        return SingleByteCharsetConverter.initCharset((String)encodingName);
    }

    public static SingleByteCharsetConverter initCharset(String javaEncodingName) throws UnsupportedEncodingException, SQLException {
        try {
            if (CharsetMapping.isMultibyteCharset((String)javaEncodingName)) {
                return null;
            }
        }
        catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
        SingleByteCharsetConverter converter = new SingleByteCharsetConverter((String)javaEncodingName);
        CONVERTER_MAP.put((String)javaEncodingName, (SingleByteCharsetConverter)converter);
        return converter;
    }

    public static String toStringDefaultEncoding(byte[] buffer, int startPos, int length) {
        return new String((byte[])buffer, (int)startPos, (int)length);
    }

    private SingleByteCharsetConverter(String encodingName) throws UnsupportedEncodingException {
        String allBytesString = new String((byte[])allBytes, (int)0, (int)256, (String)encodingName);
        int allBytesLen = allBytesString.length();
        System.arraycopy((Object)unknownCharsMap, (int)0, (Object)this.charToByteMap, (int)0, (int)this.charToByteMap.length);
        int i = 0;
        while (i < 256) {
            char c;
            if (i >= allBytesLen) return;
            this.byteToChars[i] = c = allBytesString.charAt((int)i);
            this.charToByteMap[c] = allBytes[i];
            ++i;
        }
    }

    public final byte[] toBytes(char[] c) {
        if (c == null) {
            return null;
        }
        int length = c.length;
        byte[] bytes = new byte[length];
        int i = 0;
        while (i < length) {
            bytes[i] = this.charToByteMap[c[i]];
            ++i;
        }
        return bytes;
    }

    public final byte[] toBytesWrapped(char[] c, char beginWrap, char endWrap) {
        if (c == null) {
            return null;
        }
        int length = c.length + 2;
        int charLength = c.length;
        byte[] bytes = new byte[length];
        bytes[0] = this.charToByteMap[beginWrap];
        int i = 0;
        do {
            if (i >= charLength) {
                bytes[length - 1] = this.charToByteMap[endWrap];
                return bytes;
            }
            bytes[i + 1] = this.charToByteMap[c[i]];
            ++i;
        } while (true);
    }

    public final byte[] toBytes(char[] chars, int offset, int length) {
        if (chars == null) {
            return null;
        }
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] bytes = new byte[length];
        int i = 0;
        while (i < length) {
            bytes[i] = this.charToByteMap[chars[i + offset]];
            ++i;
        }
        return bytes;
    }

    public final byte[] toBytes(String s) {
        if (s == null) {
            return null;
        }
        int length = s.length();
        byte[] bytes = new byte[length];
        int i = 0;
        while (i < length) {
            bytes[i] = this.charToByteMap[s.charAt((int)i)];
            ++i;
        }
        return bytes;
    }

    public final byte[] toBytesWrapped(String s, char beginWrap, char endWrap) {
        if (s == null) {
            return null;
        }
        int stringLength = s.length();
        int length = stringLength + 2;
        byte[] bytes = new byte[length];
        bytes[0] = this.charToByteMap[beginWrap];
        int i = 0;
        do {
            if (i >= stringLength) {
                bytes[length - 1] = this.charToByteMap[endWrap];
                return bytes;
            }
            bytes[i + 1] = this.charToByteMap[s.charAt((int)i)];
            ++i;
        } while (true);
    }

    public final byte[] toBytes(String s, int offset, int length) {
        if (s == null) {
            return null;
        }
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] bytes = new byte[length];
        int i = 0;
        while (i < length) {
            char c = s.charAt((int)(i + offset));
            bytes[i] = this.charToByteMap[c];
            ++i;
        }
        return bytes;
    }

    public final String toString(byte[] buffer) {
        return this.toString((byte[])buffer, (int)0, (int)buffer.length);
    }

    public final String toString(byte[] buffer, int startPos, int length) {
        char[] charArray = new char[length];
        int readpoint = startPos;
        int i = 0;
        while (i < length) {
            charArray[i] = this.byteToChars[buffer[readpoint] - -128];
            ++readpoint;
            ++i;
        }
        return new String((char[])charArray);
    }

    static {
        int i;
        allBytes = new byte[256];
        CONVERTER_MAP = new HashMap<String, SingleByteCharsetConverter>();
        EMPTY_BYTE_ARRAY = new byte[0];
        unknownCharsMap = new byte[65536];
        for (i = -128; i <= 127; ++i) {
            SingleByteCharsetConverter.allBytes[i - -128] = (byte)i;
        }
        i = 0;
        while (i < unknownCharsMap.length) {
            SingleByteCharsetConverter.unknownCharsMap[i] = 63;
            ++i;
        }
    }
}

