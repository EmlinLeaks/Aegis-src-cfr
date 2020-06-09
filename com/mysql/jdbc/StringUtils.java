/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CharsetMapping;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StringUtils {
    public static final Set<SearchMode> SEARCH_MODE__ALL = Collections.unmodifiableSet(EnumSet.allOf(SearchMode.class));
    public static final Set<SearchMode> SEARCH_MODE__MRK_COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__BSESC_COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.ALLOW_BACKSLASH_ESCAPE, SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__BSESC_MRK_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.ALLOW_BACKSLASH_ESCAPE, SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__MRK_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__NONE = Collections.unmodifiableSet(EnumSet.noneOf(SearchMode.class));
    private static final int NON_COMMENTS_MYSQL_VERSION_REF_LENGTH = 5;
    private static final int BYTE_RANGE = 256;
    private static byte[] allBytes = new byte[256];
    private static char[] byteToChars = new char[256];
    private static Method toPlainStringMethod;
    private static final int WILD_COMPARE_MATCH = 0;
    private static final int WILD_COMPARE_CONTINUE_WITH_WILD = 1;
    private static final int WILD_COMPARE_NO_MATCH = -1;
    static final char WILDCARD_MANY = '%';
    static final char WILDCARD_ONE = '_';
    static final char WILDCARD_ESCAPE = '\\';
    private static final ConcurrentHashMap<String, Charset> charsetsByAlias;
    private static final String platformEncoding;
    private static final String VALID_ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@";
    private static final char[] HEX_DIGITS;

    static Charset findCharset(String alias) throws UnsupportedEncodingException {
        try {
            Charset cs = charsetsByAlias.get((Object)alias);
            if (cs != null) return cs;
            cs = Charset.forName((String)alias);
            Charset oldCs = charsetsByAlias.putIfAbsent((String)alias, (Charset)cs);
            if (oldCs == null) return cs;
            return oldCs;
        }
        catch (UnsupportedCharsetException uce) {
            throw new UnsupportedEncodingException((String)alias);
        }
        catch (IllegalCharsetNameException icne) {
            throw new UnsupportedEncodingException((String)alias);
        }
        catch (IllegalArgumentException iae) {
            throw new UnsupportedEncodingException((String)alias);
        }
    }

    public static String consistentToString(BigDecimal decimal) {
        if (decimal == null) {
            return null;
        }
        if (toPlainStringMethod == null) return decimal.toString();
        try {
            return (String)toPlainStringMethod.invoke((Object)decimal, (Object[])((Object[])null));
        }
        catch (InvocationTargetException invokeEx) {
            return decimal.toString();
        }
        catch (IllegalAccessException accessEx) {
            // empty catch block
        }
        return decimal.toString();
    }

    public static String dumpAsHex(byte[] byteBuffer, int length) {
        int i;
        StringBuilder outputBuilder = new StringBuilder((int)(length * 4));
        int p = 0;
        int rows = length / 8;
        int i2 = 0;
        do {
            int j;
            if (i2 < rows && p < length) {
                int ptemp = p;
                for (j = 0; j < 8; ++ptemp, ++j) {
                    String hexVal = Integer.toHexString((int)(byteBuffer[ptemp] & 255));
                    if (hexVal.length() == 1) {
                        hexVal = "0" + hexVal;
                    }
                    outputBuilder.append((String)(hexVal + " "));
                }
                outputBuilder.append((String)"    ");
            } else {
                int n = 0;
                for (i = p; i < length; ++n, ++i) {
                    String hexVal = Integer.toHexString((int)(byteBuffer[i] & 255));
                    if (hexVal.length() == 1) {
                        hexVal = "0" + hexVal;
                    }
                    outputBuilder.append((String)(hexVal + " "));
                }
                for (i = n; i < 8; ++i) {
                    outputBuilder.append((String)"   ");
                }
                break;
            }
            for (j = 0; j < 8; ++p, ++j) {
                int b = 255 & byteBuffer[p];
                if (b > 32 && b < 127) {
                    outputBuilder.append((String)((char)b + " "));
                    continue;
                }
                outputBuilder.append((String)". ");
            }
            outputBuilder.append((String)"\n");
            ++i2;
        } while (true);
        outputBuilder.append((String)"    ");
        i = p;
        do {
            if (i >= length) {
                outputBuilder.append((String)"\n");
                return outputBuilder.toString();
            }
            int b = 255 & byteBuffer[i];
            if (b > 32 && b < 127) {
                outputBuilder.append((String)((char)b + " "));
            } else {
                outputBuilder.append((String)". ");
            }
            ++i;
        } while (true);
    }

    private static boolean endsWith(byte[] dataFrom, String suffix) {
        int i = 1;
        while (i <= suffix.length()) {
            int dfOffset = dataFrom.length - i;
            int suffixOffset = suffix.length() - i;
            if (dataFrom[dfOffset] != suffix.charAt((int)suffixOffset)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static byte[] escapeEasternUnicodeByteStream(byte[] origBytes, String origString) {
        if (origBytes == null) {
            return null;
        }
        if (origBytes.length == 0) {
            return new byte[0];
        }
        int bytesLen = origBytes.length;
        int bufIndex = 0;
        int strIndex = 0;
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream((int)bytesLen);
        do {
            if (origString.charAt((int)strIndex) == '\\') {
                bytesOut.write((int)origBytes[bufIndex++]);
            } else {
                int hiByte;
                int loByte = origBytes[bufIndex];
                if (loByte < 0) {
                    loByte += 256;
                }
                bytesOut.write((int)loByte);
                if (loByte >= 128) {
                    if (bufIndex < bytesLen - 1) {
                        hiByte = origBytes[bufIndex + 1];
                        if (hiByte < 0) {
                            hiByte += 256;
                        }
                        bytesOut.write((int)hiByte);
                        ++bufIndex;
                        if (hiByte == 92) {
                            bytesOut.write((int)hiByte);
                        }
                    }
                } else if (loByte == 92 && bufIndex < bytesLen - 1) {
                    hiByte = origBytes[bufIndex + 1];
                    if (hiByte < 0) {
                        hiByte += 256;
                    }
                    if (hiByte == 98) {
                        bytesOut.write((int)92);
                        bytesOut.write((int)98);
                        ++bufIndex;
                    }
                }
                ++bufIndex;
            }
            if (bufIndex >= bytesLen) {
                return bytesOut.toByteArray();
            }
            ++strIndex;
        } while (true);
    }

    public static char firstNonWsCharUc(String searchIn) {
        return StringUtils.firstNonWsCharUc((String)searchIn, (int)0);
    }

    public static char firstNonWsCharUc(String searchIn, int startAt) {
        if (searchIn == null) {
            return '\u0000';
        }
        int length = searchIn.length();
        int i = startAt;
        while (i < length) {
            char c = searchIn.charAt((int)i);
            if (!Character.isWhitespace((char)c)) {
                return Character.toUpperCase((char)c);
            }
            ++i;
        }
        return '\u0000';
    }

    public static char firstAlphaCharUc(String searchIn, int startAt) {
        if (searchIn == null) {
            return '\u0000';
        }
        int length = searchIn.length();
        int i = startAt;
        while (i < length) {
            char c = searchIn.charAt((int)i);
            if (Character.isLetter((char)c)) {
                return Character.toUpperCase((char)c);
            }
            ++i;
        }
        return '\u0000';
    }

    public static String fixDecimalExponent(String dString) {
        int ePos = dString.indexOf((int)69);
        if (ePos == -1) {
            ePos = dString.indexOf((int)101);
        }
        if (ePos == -1) return dString;
        if (dString.length() <= ePos + 1) return dString;
        char maybeMinusChar = dString.charAt((int)(ePos + 1));
        if (maybeMinusChar == '-') return dString;
        if (maybeMinusChar == '+') return dString;
        StringBuilder strBuilder = new StringBuilder((int)(dString.length() + 1));
        strBuilder.append((String)dString.substring((int)0, (int)(ePos + 1)));
        strBuilder.append((char)'+');
        strBuilder.append((String)dString.substring((int)(ePos + 1), (int)dString.length()));
        return strBuilder.toString();
    }

    public static byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            if (converter != null) {
                return converter.toBytes((char[])c);
            }
            if (encoding == null) {
                return StringUtils.getBytes((char[])c);
            }
            byte[] b = StringUtils.getBytes((char[])c, (String)encoding);
            if (parserKnowsUnicode) return b;
            if (!CharsetMapping.requiresEscapeEasternUnicode((String)encoding)) return b;
            if (encoding.equalsIgnoreCase((String)serverEncoding)) return b;
            return StringUtils.escapeEasternUnicodeByteStream((byte[])b, (String)new String((char[])c));
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"StringUtils.0") + encoding + Messages.getString((String)"StringUtils.1")), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            if (converter != null) {
                return converter.toBytes((char[])c, (int)offset, (int)length);
            }
            if (encoding == null) {
                return StringUtils.getBytes((char[])c, (int)offset, (int)length);
            }
            byte[] b = StringUtils.getBytes((char[])c, (int)offset, (int)length, (String)encoding);
            if (parserKnowsUnicode) return b;
            if (!CharsetMapping.requiresEscapeEasternUnicode((String)encoding)) return b;
            if (encoding.equalsIgnoreCase((String)serverEncoding)) return b;
            return StringUtils.escapeEasternUnicodeByteStream((byte[])b, (String)new String((char[])c, (int)offset, (int)length));
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"StringUtils.0") + encoding + Messages.getString((String)"StringUtils.1")), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static byte[] getBytes(char[] c, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            SingleByteCharsetConverter converter = conn != null ? conn.getCharsetConverter((String)encoding) : SingleByteCharsetConverter.getInstance((String)encoding, null);
            return StringUtils.getBytes((char[])c, (SingleByteCharsetConverter)converter, (String)encoding, (String)serverEncoding, (boolean)parserKnowsUnicode, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"StringUtils.0") + encoding + Messages.getString((String)"StringUtils.1")), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            if (converter != null) {
                return converter.toBytes((String)s);
            }
            if (encoding == null) {
                return StringUtils.getBytes((String)s);
            }
            byte[] b = StringUtils.getBytes((String)s, (String)encoding);
            if (parserKnowsUnicode) return b;
            if (!CharsetMapping.requiresEscapeEasternUnicode((String)encoding)) return b;
            if (encoding.equalsIgnoreCase((String)serverEncoding)) return b;
            return StringUtils.escapeEasternUnicodeByteStream((byte[])b, (String)s);
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"StringUtils.5") + encoding + Messages.getString((String)"StringUtils.6")), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            if (converter != null) {
                return converter.toBytes((String)s, (int)offset, (int)length);
            }
            if (encoding == null) {
                return StringUtils.getBytes((String)s, (int)offset, (int)length);
            }
            s = s.substring((int)offset, (int)(offset + length));
            byte[] b = StringUtils.getBytes((String)s, (String)encoding);
            if (parserKnowsUnicode) return b;
            if (!CharsetMapping.requiresEscapeEasternUnicode((String)encoding)) return b;
            if (encoding.equalsIgnoreCase((String)serverEncoding)) return b;
            return StringUtils.escapeEasternUnicodeByteStream((byte[])b, (String)s);
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"StringUtils.5") + encoding + Messages.getString((String)"StringUtils.6")), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static byte[] getBytes(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            SingleByteCharsetConverter converter = conn != null ? conn.getCharsetConverter((String)encoding) : SingleByteCharsetConverter.getInstance((String)encoding, null);
            return StringUtils.getBytes((String)s, (SingleByteCharsetConverter)converter, (String)encoding, (String)serverEncoding, (boolean)parserKnowsUnicode, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"StringUtils.5") + encoding + Messages.getString((String)"StringUtils.6")), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static final byte[] getBytes(String s, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            SingleByteCharsetConverter converter = conn != null ? conn.getCharsetConverter((String)encoding) : SingleByteCharsetConverter.getInstance((String)encoding, null);
            return StringUtils.getBytes((String)s, (SingleByteCharsetConverter)converter, (String)encoding, (String)serverEncoding, (int)offset, (int)length, (boolean)parserKnowsUnicode, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"StringUtils.5") + encoding + Messages.getString((String)"StringUtils.6")), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static byte[] getBytesWrapped(String s, char beginWrap, char endWrap, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            if (converter != null) {
                return converter.toBytesWrapped((String)s, (char)beginWrap, (char)endWrap);
            }
            if (encoding == null) {
                StringBuilder strBuilder = new StringBuilder((int)(s.length() + 2));
                strBuilder.append((char)beginWrap);
                strBuilder.append((String)s);
                strBuilder.append((char)endWrap);
                return StringUtils.getBytes((String)strBuilder.toString());
            }
            StringBuilder strBuilder = new StringBuilder((int)(s.length() + 2));
            strBuilder.append((char)beginWrap);
            strBuilder.append((String)s);
            strBuilder.append((char)endWrap);
            s = strBuilder.toString();
            byte[] b = StringUtils.getBytes((String)s, (String)encoding);
            if (parserKnowsUnicode) return b;
            if (!CharsetMapping.requiresEscapeEasternUnicode((String)encoding)) return b;
            if (encoding.equalsIgnoreCase((String)serverEncoding)) return b;
            return StringUtils.escapeEasternUnicodeByteStream((byte[])b, (String)s);
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"StringUtils.10") + encoding + Messages.getString((String)"StringUtils.11")), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static int getInt(byte[] buf) throws NumberFormatException {
        return StringUtils.getInt((byte[])buf, (int)0, (int)buf.length);
    }

    public static int getInt(byte[] buf, int offset, int endPos) throws NumberFormatException {
        int n;
        int s;
        char base = '\n';
        for (s = offset; s < endPos && Character.isWhitespace((char)((char)buf[s])); ++s) {
        }
        if (s == endPos) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        boolean negative = false;
        if ((char)buf[s] == '-') {
            negative = true;
            ++s;
        } else if ((char)buf[s] == '+') {
            ++s;
        }
        int save = s;
        int cutoff = Integer.MAX_VALUE / base;
        int cutlim = Integer.MAX_VALUE % base;
        if (negative) {
            ++cutlim;
        }
        boolean overflow = false;
        int i = 0;
        while (s < endPos) {
            char c = (char)buf[s];
            if (Character.isDigit((char)c)) {
                c = (char)(c - 48);
            } else {
                if (!Character.isLetter((char)c)) break;
                c = (char)(Character.toUpperCase((char)c) - 65 + 10);
            }
            if (c >= base) break;
            if (i > cutoff || i == cutoff && c > cutlim) {
                overflow = true;
            } else {
                i *= base;
                i += c;
            }
            ++s;
        }
        if (s == save) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        if (overflow) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        if (negative) {
            n = -i;
            return n;
        }
        n = i;
        return n;
    }

    public static long getLong(byte[] buf) throws NumberFormatException {
        return StringUtils.getLong((byte[])buf, (int)0, (int)buf.length);
    }

    public static long getLong(byte[] buf, int offset, int endpos) throws NumberFormatException {
        int s;
        long l;
        char base = '\n';
        for (s = offset; s < endpos && Character.isWhitespace((char)((char)buf[s])); ++s) {
        }
        if (s == endpos) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        boolean negative = false;
        if ((char)buf[s] == '-') {
            negative = true;
            ++s;
        } else if ((char)buf[s] == '+') {
            ++s;
        }
        int save = s;
        long cutoff = Long.MAX_VALUE / (long)base;
        long cutlim = (long)((int)(Long.MAX_VALUE % (long)base));
        if (negative) {
            ++cutlim;
        }
        boolean overflow = false;
        long i = 0L;
        while (s < endpos) {
            char c = (char)buf[s];
            if (Character.isDigit((char)c)) {
                c = (char)(c - 48);
            } else {
                if (!Character.isLetter((char)c)) break;
                c = (char)(Character.toUpperCase((char)c) - 65 + 10);
            }
            if (c >= base) break;
            if (i > cutoff || i == cutoff && (long)c > cutlim) {
                overflow = true;
            } else {
                i *= (long)base;
                i += (long)c;
            }
            ++s;
        }
        if (s == save) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        if (overflow) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        if (negative) {
            l = -i;
            return l;
        }
        l = i;
        return l;
    }

    public static short getShort(byte[] buf) throws NumberFormatException {
        return StringUtils.getShort((byte[])buf, (int)0, (int)buf.length);
    }

    public static short getShort(byte[] buf, int offset, int endpos) throws NumberFormatException {
        short s;
        int s2;
        char base = '\n';
        for (s2 = offset; s2 < endpos && Character.isWhitespace((char)((char)buf[s2])); ++s2) {
        }
        if (s2 == endpos) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        boolean negative = false;
        if ((char)buf[s2] == '-') {
            negative = true;
            ++s2;
        } else if ((char)buf[s2] == '+') {
            ++s2;
        }
        int save = s2;
        short cutoff = (short)(32767 / base);
        short cutlim = (short)(32767 % base);
        if (negative) {
            cutlim = (short)(cutlim + 1);
        }
        boolean overflow = false;
        short i = 0;
        while (s2 < endpos) {
            char c = (char)buf[s2];
            if (Character.isDigit((char)c)) {
                c = (char)(c - 48);
            } else {
                if (!Character.isLetter((char)c)) break;
                c = (char)(Character.toUpperCase((char)c) - 65 + 10);
            }
            if (c >= base) break;
            if (i > cutoff || i == cutoff && c > cutlim) {
                overflow = true;
            } else {
                i = (short)(i * base);
                i = (short)(i + c);
            }
            ++s2;
        }
        if (s2 == save) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        if (overflow) {
            throw new NumberFormatException((String)StringUtils.toString((byte[])buf));
        }
        if (negative) {
            s = (short)(-i);
            return s;
        }
        s = i;
        return s;
    }

    public static int indexOfIgnoreCase(String searchIn, String searchFor) {
        return StringUtils.indexOfIgnoreCase((int)0, (String)searchIn, (String)searchFor);
    }

    public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor) {
        int searchForLength;
        if (searchIn == null) return -1;
        if (searchFor == null) {
            return -1;
        }
        int searchInLength = searchIn.length();
        int stopSearchingAt = searchInLength - (searchForLength = searchFor.length());
        if (startingPosition > stopSearchingAt) return -1;
        if (searchForLength == 0) {
            return -1;
        }
        char firstCharOfSearchForUc = Character.toUpperCase((char)searchFor.charAt((int)0));
        char firstCharOfSearchForLc = Character.toLowerCase((char)searchFor.charAt((int)0));
        int i = startingPosition;
        while (i <= stopSearchingAt) {
            if (StringUtils.isCharAtPosNotEqualIgnoreCase((String)searchIn, (int)i, (char)firstCharOfSearchForUc, (char)firstCharOfSearchForLc)) {
                while (++i <= stopSearchingAt && StringUtils.isCharAtPosNotEqualIgnoreCase((String)searchIn, (int)i, (char)firstCharOfSearchForUc, (char)firstCharOfSearchForLc)) {
                }
            }
            if (i <= stopSearchingAt && StringUtils.startsWithIgnoreCase((String)searchIn, (int)i, (String)searchFor)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOfIgnoreCase(int startingPosition, String searchIn, String[] searchForSequence, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
        String searchForPart;
        if (searchIn == null) return -1;
        if (searchForSequence == null) {
            return -1;
        }
        int searchInLength = searchIn.length();
        int searchForLength = 0;
        String[] arr$ = searchForSequence;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; searchForLength += searchForPart.length(), ++i$) {
            searchForPart = arr$[i$];
        }
        if (searchForLength == 0) {
            return -1;
        }
        int searchForWordsCount = searchForSequence.length;
        int stopSearchingAt = searchInLength - (searchForLength += searchForWordsCount > 0 ? searchForWordsCount - 1 : 0);
        if (startingPosition > stopSearchingAt) {
            return -1;
        }
        if (searchMode.contains((Object)((Object)SearchMode.SKIP_BETWEEN_MARKERS)) && (openingMarkers == null || closingMarkers == null || openingMarkers.length() != closingMarkers.length())) {
            throw new IllegalArgumentException((String)Messages.getString((String)"StringUtils.15", (Object[])new String[]{openingMarkers, closingMarkers}));
        }
        if (Character.isWhitespace((char)searchForSequence[0].charAt((int)0)) && searchMode.contains((Object)((Object)SearchMode.SKIP_WHITE_SPACE))) {
            searchMode = EnumSet.copyOf(searchMode);
            searchMode.remove((Object)((Object)SearchMode.SKIP_WHITE_SPACE));
        }
        EnumSet<SearchMode> searchMode2 = EnumSet.of(SearchMode.SKIP_WHITE_SPACE);
        searchMode2.addAll(searchMode);
        searchMode2.remove((Object)((Object)SearchMode.SKIP_BETWEEN_MARKERS));
        int positionOfFirstWord = startingPosition;
        while (positionOfFirstWord <= stopSearchingAt) {
            if ((positionOfFirstWord = StringUtils.indexOfIgnoreCase((int)positionOfFirstWord, (String)searchIn, (String)searchForSequence[0], (String)openingMarkers, (String)closingMarkers, searchMode)) == -1) return -1;
            if (positionOfFirstWord > stopSearchingAt) {
                return -1;
            }
            int startingPositionForNextWord = positionOfFirstWord + searchForSequence[0].length();
            int wc = 0;
            boolean match = true;
            while (++wc < searchForWordsCount && match) {
                int positionOfNextWord = StringUtils.indexOfNextChar((int)startingPositionForNextWord, (int)(searchInLength - 1), (String)searchIn, null, null, null, searchMode2);
                if (startingPositionForNextWord == positionOfNextWord || !StringUtils.startsWithIgnoreCase((String)searchIn, (int)positionOfNextWord, (String)searchForSequence[wc])) {
                    match = false;
                    continue;
                }
                startingPositionForNextWord = positionOfNextWord + searchForSequence[wc].length();
            }
            if (match) {
                return positionOfFirstWord;
            }
            ++positionOfFirstWord;
        }
        return -1;
    }

    public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
        return StringUtils.indexOfIgnoreCase((int)startingPosition, (String)searchIn, (String)searchFor, (String)openingMarkers, (String)closingMarkers, (String)"", searchMode);
    }

    public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
        int searchForLength;
        if (searchIn == null) return -1;
        if (searchFor == null) {
            return -1;
        }
        int searchInLength = searchIn.length();
        int stopSearchingAt = searchInLength - (searchForLength = searchFor.length());
        if (startingPosition > stopSearchingAt) return -1;
        if (searchForLength == 0) {
            return -1;
        }
        if (searchMode.contains((Object)((Object)SearchMode.SKIP_BETWEEN_MARKERS))) {
            if (openingMarkers == null || closingMarkers == null || openingMarkers.length() != closingMarkers.length()) {
                throw new IllegalArgumentException((String)Messages.getString((String)"StringUtils.15", (Object[])new String[]{openingMarkers, closingMarkers}));
            }
            if (overridingMarkers == null) {
                throw new IllegalArgumentException((String)Messages.getString((String)"StringUtils.16", (Object[])new String[]{overridingMarkers, openingMarkers}));
            }
            for (char c : overridingMarkers.toCharArray()) {
                if (openingMarkers.indexOf((int)c) != -1) continue;
                throw new IllegalArgumentException((String)Messages.getString((String)"StringUtils.16", (Object[])new String[]{overridingMarkers, openingMarkers}));
            }
        }
        char firstCharOfSearchForUc = Character.toUpperCase((char)searchFor.charAt((int)0));
        char firstCharOfSearchForLc = Character.toLowerCase((char)searchFor.charAt((int)0));
        if (Character.isWhitespace((char)firstCharOfSearchForLc) && searchMode.contains((Object)((Object)SearchMode.SKIP_WHITE_SPACE))) {
            searchMode = EnumSet.copyOf(searchMode);
            searchMode.remove((Object)((Object)SearchMode.SKIP_WHITE_SPACE));
        }
        int i = startingPosition;
        while (i <= stopSearchingAt) {
            char c;
            if ((i = StringUtils.indexOfNextChar((int)i, (int)stopSearchingAt, (String)searchIn, (String)openingMarkers, (String)closingMarkers, (String)overridingMarkers, searchMode)) == -1) {
                return -1;
            }
            c = searchIn.charAt((int)i);
            if (StringUtils.isCharEqualIgnoreCase((char)c, (char)firstCharOfSearchForUc, (char)firstCharOfSearchForLc) && StringUtils.startsWithIgnoreCase((String)searchIn, (int)i, (String)searchFor)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    /*
     * Unable to fully structure code
     */
    private static int indexOfNextChar(int startingPosition, int stopPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
        if (searchIn == null) {
            return -1;
        }
        searchInLength = searchIn.length();
        if (startingPosition >= searchInLength) {
            return -1;
        }
        c0 = '\u0000';
        c1 = searchIn.charAt((int)startingPosition);
        c2 = startingPosition + 1 < searchInLength ? searchIn.charAt((int)(startingPosition + 1)) : '\u0000';
        i = startingPosition;
        while (i <= stopPosition) {
            block24 : {
                block31 : {
                    block30 : {
                        block29 : {
                            block26 : {
                                block27 : {
                                    block28 : {
                                        block25 : {
                                            c0 = c1;
                                            c1 = c2;
                                            c2 = i + 2 < searchInLength ? searchIn.charAt((int)(i + 2)) : '\u0000';
                                            dashDashCommentImmediateEnd = false;
                                            markerIndex = -1;
                                            if (!searchMode.contains((Object)SearchMode.ALLOW_BACKSLASH_ESCAPE) || c0 != '\\') break block25;
                                            c1 = c2;
                                            c2 = ++i + 2 < searchInLength ? searchIn.charAt((int)(i + 2)) : '\u0000';
                                            break block24;
                                        }
                                        if (searchMode.contains((Object)SearchMode.SKIP_BETWEEN_MARKERS) && (markerIndex = openingMarkers.indexOf((int)c0)) != -1) break block26;
                                        if (!searchMode.contains((Object)SearchMode.SKIP_BLOCK_COMMENTS) || c0 != '/' || c1 != '*') break block27;
                                        if (c2 == '!') break block28;
                                        ++i;
                                        break block29;
                                    }
                                    ++i;
                                    break block30;
                                }
                                if (searchMode.contains((Object)SearchMode.SKIP_BLOCK_COMMENTS) && c0 == '*' && c1 == '/') {
                                    c1 = c2;
                                    c2 = ++i + 2 < searchInLength ? searchIn.charAt((int)(i + 2)) : '\u0000';
                                } else if (searchMode.contains((Object)SearchMode.SKIP_LINE_COMMENTS) && (c0 == '-' && c1 == '-' && (Character.isWhitespace((char)c2) || (dashDashCommentImmediateEnd = c2 == ';') || c2 == '\u0000') || c0 == '#')) {
                                    if (dashDashCommentImmediateEnd) {
                                        ++i;
                                        c1 = ++i + 1 < searchInLength ? searchIn.charAt((int)(i + 1)) : '\u0000';
                                        c2 = i + 2 < searchInLength ? searchIn.charAt((int)(i + 2)) : '\u0000';
                                    } else {
                                        while (++i <= stopPosition && (c0 = searchIn.charAt((int)i)) != '\n' && c0 != '\r') {
                                        }
                                        v0 = c1 = i + 1 < searchInLength ? searchIn.charAt((int)(i + 1)) : '\u0000';
                                        if (c0 == '\r' && c1 == '\n') {
                                            c1 = ++i + 1 < searchInLength ? searchIn.charAt((int)(i + 1)) : '\u0000';
                                        }
                                        c2 = i + 2 < searchInLength ? searchIn.charAt((int)(i + 2)) : '\u0000';
                                    }
                                } else {
                                    if (searchMode.contains((Object)SearchMode.SKIP_WHITE_SPACE) == false) return i;
                                    if (!Character.isWhitespace((char)c0)) {
                                        return i;
                                    }
                                }
                                break block24;
                            }
                            nestedMarkersCount = 0;
                            openingMarker = c0;
                            closingMarker = closingMarkers.charAt((int)markerIndex);
                            outerIsAnOverridingMarker = overridingMarkers.indexOf((int)openingMarker) != -1;
                            do {
                                if (++i <= stopPosition && ((c0 = searchIn.charAt((int)i)) != closingMarker || nestedMarkersCount != 0)) {
                                    if (outerIsAnOverridingMarker || overridingMarkers.indexOf((int)c0) == -1) {
                                        if (c0 == openingMarker) {
                                            ++nestedMarkersCount;
                                            continue;
                                        }
                                        if (c0 == closingMarker) {
                                            --nestedMarkersCount;
                                            continue;
                                        }
                                        if (!searchMode.contains((Object)SearchMode.ALLOW_BACKSLASH_ESCAPE) || c0 != '\\') continue;
                                        ++i;
                                        continue;
                                    }
                                } else {
                                    c1 = i + 1 < searchInLength ? searchIn.charAt((int)(i + 1)) : '\u0000';
                                    c2 = i + 2 < searchInLength ? searchIn.charAt((int)(i + 2)) : '\u0000';
                                    break block24;
                                }
                                overridingMarkerIndex = openingMarkers.indexOf((int)c0);
                                overridingNestedMarkersCount = 0;
                                overridingOpeningMarker = c0;
                                overridingClosingMarker = closingMarkers.charAt((int)overridingMarkerIndex);
                                do {
                                    if (++i > stopPosition || (c0 = searchIn.charAt((int)i)) == overridingClosingMarker && overridingNestedMarkersCount == 0) ** break;
                                    if (c0 == overridingOpeningMarker) {
                                        ++overridingNestedMarkersCount;
                                        continue;
                                    }
                                    if (c0 == overridingClosingMarker) {
                                        --overridingNestedMarkersCount;
                                        continue;
                                    }
                                    if (!searchMode.contains((Object)SearchMode.ALLOW_BACKSLASH_ESCAPE) || c0 != '\\') continue;
                                    ++i;
                                } while (true);
                                break;
                            } while (true);
                        }
                        while (++i <= stopPosition && (searchIn.charAt((int)i) != '*' || (i + 1 < searchInLength ? (int)searchIn.charAt((int)(i + 1)) : 0) != 47)) {
                        }
                        ++i;
                        break block31;
                    }
                    for (j = 1; j <= 5 && ++i + j < searchInLength && Character.isDigit((char)searchIn.charAt((int)(i + j))); ++j) {
                    }
                    if (j == 5) {
                        i += 5;
                    }
                }
                c1 = i + 1 < searchInLength ? searchIn.charAt((int)(i + 1)) : '\u0000';
                c2 = i + 2 < searchInLength ? searchIn.charAt((int)(i + 2)) : '\u0000';
            }
            ++i;
        }
        return -1;
    }

    private static boolean isCharAtPosNotEqualIgnoreCase(String searchIn, int pos, char firstCharOfSearchForUc, char firstCharOfSearchForLc) {
        if (Character.toLowerCase((char)searchIn.charAt((int)pos)) == firstCharOfSearchForLc) return false;
        if (Character.toUpperCase((char)searchIn.charAt((int)pos)) == firstCharOfSearchForUc) return false;
        return true;
    }

    private static boolean isCharEqualIgnoreCase(char charToCompare, char compareToCharUC, char compareToCharLC) {
        if (Character.toLowerCase((char)charToCompare) == compareToCharLC) return true;
        if (Character.toUpperCase((char)charToCompare) == compareToCharUC) return true;
        return false;
    }

    public static List<String> split(String stringToSplit, String delimiter, boolean trim) {
        if (stringToSplit == null) {
            return new ArrayList<String>();
        }
        if (delimiter == null) {
            throw new IllegalArgumentException();
        }
        StringTokenizer tokenizer = new StringTokenizer((String)stringToSplit, (String)delimiter, (boolean)false);
        ArrayList<String> splitTokens = new ArrayList<String>((int)tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (trim) {
                token = token.trim();
            }
            splitTokens.add((String)token);
        }
        return splitTokens;
    }

    public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, boolean trim) {
        return StringUtils.split((String)stringToSplit, (String)delimiter, (String)openingMarkers, (String)closingMarkers, (String)"", (boolean)trim);
    }

    public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, String overridingMarkers, boolean trim) {
        String token;
        if (stringToSplit == null) {
            return new ArrayList<String>();
        }
        if (delimiter == null) {
            throw new IllegalArgumentException();
        }
        int delimPos = 0;
        int currentPos = 0;
        ArrayList<String> splitTokens = new ArrayList<String>();
        while ((delimPos = StringUtils.indexOfIgnoreCase((int)currentPos, (String)stringToSplit, (String)delimiter, (String)openingMarkers, (String)closingMarkers, (String)overridingMarkers, SEARCH_MODE__MRK_COM_WS)) != -1) {
            token = stringToSplit.substring((int)currentPos, (int)delimPos);
            if (trim) {
                token = token.trim();
            }
            splitTokens.add((String)token);
            currentPos = delimPos + 1;
        }
        if (currentPos >= stringToSplit.length()) return splitTokens;
        token = stringToSplit.substring((int)currentPos);
        if (trim) {
            token = token.trim();
        }
        splitTokens.add((String)token);
        return splitTokens;
    }

    private static boolean startsWith(byte[] dataFrom, String chars) {
        int charsLength = chars.length();
        if (dataFrom.length < charsLength) {
            return false;
        }
        int i = 0;
        while (i < charsLength) {
            if (dataFrom[i] != chars.charAt((int)i)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static boolean startsWithIgnoreCase(String searchIn, int startAt, String searchFor) {
        return searchIn.regionMatches((boolean)true, (int)startAt, (String)searchFor, (int)0, (int)searchFor.length());
    }

    public static boolean startsWithIgnoreCase(String searchIn, String searchFor) {
        return StringUtils.startsWithIgnoreCase((String)searchIn, (int)0, (String)searchFor);
    }

    public static boolean startsWithIgnoreCaseAndNonAlphaNumeric(String searchIn, String searchFor) {
        if (searchIn == null) {
            if (searchFor != null) return false;
            return true;
        }
        int beginPos = 0;
        int inLength = searchIn.length();
        while (beginPos < inLength) {
            char c = searchIn.charAt((int)beginPos);
            if (Character.isLetterOrDigit((char)c)) {
                return StringUtils.startsWithIgnoreCase((String)searchIn, (int)beginPos, (String)searchFor);
            }
            ++beginPos;
        }
        return StringUtils.startsWithIgnoreCase((String)searchIn, (int)beginPos, (String)searchFor);
    }

    public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor) {
        return StringUtils.startsWithIgnoreCaseAndWs((String)searchIn, (String)searchFor, (int)0);
    }

    public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor, int beginPos) {
        if (searchIn == null) {
            if (searchFor != null) return false;
            return true;
        }
        int inLength = searchIn.length();
        while (beginPos < inLength) {
            if (!Character.isWhitespace((char)searchIn.charAt((int)beginPos))) {
                return StringUtils.startsWithIgnoreCase((String)searchIn, (int)beginPos, (String)searchFor);
            }
            ++beginPos;
        }
        return StringUtils.startsWithIgnoreCase((String)searchIn, (int)beginPos, (String)searchFor);
    }

    public static int startsWithIgnoreCaseAndWs(String searchIn, String[] searchFor) {
        int i = 0;
        while (i < searchFor.length) {
            if (StringUtils.startsWithIgnoreCaseAndWs((String)searchIn, (String)searchFor[i], (int)0)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static byte[] stripEnclosure(byte[] source, String prefix, String suffix) {
        if (source.length < prefix.length() + suffix.length()) return source;
        if (!StringUtils.startsWith((byte[])source, (String)prefix)) return source;
        if (!StringUtils.endsWith((byte[])source, (String)suffix)) return source;
        int totalToStrip = prefix.length() + suffix.length();
        int enclosedLength = source.length - totalToStrip;
        byte[] enclosed = new byte[enclosedLength];
        int startPos = prefix.length();
        int numToCopy = enclosed.length;
        System.arraycopy((Object)source, (int)startPos, (Object)enclosed, (int)0, (int)numToCopy);
        return enclosed;
    }

    public static String toAsciiString(byte[] buffer) {
        return StringUtils.toAsciiString((byte[])buffer, (int)0, (int)buffer.length);
    }

    public static String toAsciiString(byte[] buffer, int startPos, int length) {
        char[] charArray = new char[length];
        int readpoint = startPos;
        int i = 0;
        while (i < length) {
            charArray[i] = (char)buffer[readpoint];
            ++readpoint;
            ++i;
        }
        return new String((char[])charArray);
    }

    public static boolean wildCompareIgnoreCase(String searchIn, String searchFor) {
        if (StringUtils.wildCompareInternal((String)searchIn, (String)searchFor) != 0) return false;
        return true;
    }

    private static int wildCompareInternal(String searchIn, String searchFor) {
        int searchInPos;
        int searchInEnd;
        int searchForEnd;
        int searchForPos;
        block20 : {
            if (searchIn == null) return -1;
            if (searchFor == null) {
                return -1;
            }
            if (searchFor.equals((Object)"%")) {
                return 0;
            }
            searchForPos = 0;
            searchForEnd = searchFor.length();
            searchInPos = 0;
            searchInEnd = searchIn.length();
            int result = -1;
            while (searchForPos != searchForEnd) {
                while (searchFor.charAt((int)searchForPos) != '%' && searchFor.charAt((int)searchForPos) != '_') {
                    if (searchFor.charAt((int)searchForPos) == '\\' && searchForPos + 1 != searchForEnd) {
                        ++searchForPos;
                    }
                    if (searchInPos == searchInEnd) return 1;
                    if (Character.toUpperCase((char)searchFor.charAt((int)searchForPos++)) != Character.toUpperCase((char)searchIn.charAt((int)searchInPos++))) {
                        return 1;
                    }
                    if (searchForPos == searchForEnd) {
                        if (searchInPos == searchInEnd) return 0;
                        return 1;
                    }
                    result = 1;
                }
                if (searchFor.charAt((int)searchForPos) == '_') {
                    do {
                        if (searchInPos == searchInEnd) {
                            return result;
                        }
                        ++searchInPos;
                    } while (++searchForPos < searchForEnd && searchFor.charAt((int)searchForPos) == '_');
                    if (searchForPos == searchForEnd) break;
                }
                if (searchFor.charAt((int)searchForPos) != '%') continue;
                ++searchForPos;
                break block20;
            }
            if (searchInPos == searchInEnd) return 0;
            return 1;
        }
        while (searchForPos != searchForEnd) {
            if (searchFor.charAt((int)searchForPos) != '%') {
                if (searchFor.charAt((int)searchForPos) != '_') break;
                if (searchInPos == searchInEnd) {
                    return -1;
                }
                ++searchInPos;
            }
            ++searchForPos;
        }
        if (searchForPos == searchForEnd) {
            return 0;
        }
        if (searchInPos == searchInEnd) {
            return -1;
        }
        char cmp = searchFor.charAt((int)searchForPos);
        if (cmp == '\\' && searchForPos + 1 != searchForEnd) {
            cmp = searchFor.charAt((int)(++searchForPos));
        }
        ++searchForPos;
        do {
            if (searchInPos != searchInEnd && Character.toUpperCase((char)searchIn.charAt((int)searchInPos)) != Character.toUpperCase((char)cmp)) {
                ++searchInPos;
                continue;
            }
            if (searchInPos++ == searchInEnd) {
                return -1;
            }
            int tmp = StringUtils.wildCompareInternal((String)searchIn.substring((int)searchInPos), (String)searchFor.substring((int)searchForPos));
            if (tmp <= 0) {
                return tmp;
            }
            if (searchInPos == searchInEnd) return -1;
        } while (true);
    }

    static byte[] s2b(String s, MySQLConnection conn) throws SQLException {
        if (s == null) {
            return null;
        }
        if (conn == null) return s.getBytes();
        if (!conn.getUseUnicode()) return s.getBytes();
        try {
            String encoding = conn.getEncoding();
            if (encoding == null) {
                return s.getBytes();
            }
            SingleByteCharsetConverter converter = conn.getCharsetConverter((String)encoding);
            if (converter == null) return s.getBytes((String)encoding);
            return converter.toBytes((String)s);
        }
        catch (UnsupportedEncodingException E) {
            return s.getBytes();
        }
    }

    public static int lastIndexOf(byte[] s, char c) {
        if (s == null) {
            return -1;
        }
        int i = s.length - 1;
        while (i >= 0) {
            if (s[i] == c) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static int indexOf(byte[] s, char c) {
        if (s == null) {
            return -1;
        }
        int length = s.length;
        int i = 0;
        while (i < length) {
            if (s[i] == c) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static boolean isNullOrEmpty(String toTest) {
        if (toTest == null) return true;
        if (toTest.length() == 0) return true;
        return false;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    public static String stripComments(String src, String stringOpens, String stringCloses, boolean slashStarComments, boolean slashSlashComments, boolean hashComments, boolean dashDashComments) {
        if (src == null) {
            return null;
        }
        strBuilder = new StringBuilder((int)src.length());
        sourceReader = new StringReader((String)src);
        contextMarker = '\u0000';
        escaped = false;
        markerTypeFound = -1;
        ind = 0;
        currentChar = '\u0000';
        try {
            block2 : do {
                block21 : {
                    block22 : {
                        block20 : {
                            if ((currentChar = sourceReader.read()) == '\uffffffff') return strBuilder.toString();
                            if (markerTypeFound != -1 && currentChar == stringCloses.charAt((int)markerTypeFound) && !escaped) {
                                contextMarker = '\u0000';
                                markerTypeFound = -1;
                            } else {
                                ind = stringOpens.indexOf((int)currentChar);
                                if (ind != -1 && !escaped && contextMarker == '\u0000') {
                                    markerTypeFound = ind;
                                    contextMarker = currentChar;
                                }
                            }
                            if (contextMarker != '\u0000' || currentChar != '/' || !slashSlashComments && !slashStarComments) break block20;
                            currentChar = sourceReader.read();
                            if (currentChar == '*' && slashStarComments) break block21;
                            if (currentChar == '/' && slashSlashComments) {
                                while ((currentChar = sourceReader.read()) != '\n' && currentChar != '\r' && currentChar >= '\u0000') {
                                }
                            }
                            break block22;
                        }
                        if (contextMarker == '\u0000' && currentChar == '#' && hashComments) {
                            while ((currentChar = sourceReader.read()) != '\n' && currentChar != '\r' && currentChar >= '\u0000') {
                            }
                        } else if (contextMarker == '\u0000' && currentChar == '-' && dashDashComments) {
                            currentChar = sourceReader.read();
                            if (currentChar == '\uffffffff' || currentChar != '-') {
                                strBuilder.append((char)'-');
                                if (currentChar == '\uffffffff') continue;
                                strBuilder.append((char)((char)currentChar));
                                continue;
                            }
                            while ((currentChar = sourceReader.read()) != '\n' && currentChar != '\r' && currentChar >= '\u0000') {
                            }
                        }
                    }
                    if (currentChar == '\uffffffff') continue;
                    strBuilder.append((char)((char)currentChar));
                    continue;
                }
                prevChar = '\u0000';
                do {
                    if ((currentChar = sourceReader.read()) == '/' && prevChar == '*') continue block2;
                    if (currentChar == '\r') {
                        currentChar = sourceReader.read();
                        if (currentChar == '\n') {
                            currentChar = sourceReader.read();
                        }
                    } else if (currentChar == '\n') {
                        currentChar = sourceReader.read();
                    }
                    if (currentChar < '\u0000') ** break;
                    prevChar = currentChar;
                } while (true);
                break;
            } while (true);
        }
        catch (IOException ioEx) {
            // empty catch block
        }
        return strBuilder.toString();
    }

    public static String sanitizeProcOrFuncName(String src) {
        if (src == null) return null;
        if (!src.equals((Object)"%")) return src;
        return null;
    }

    public static List<String> splitDBdotName(String source, String catalog, String quoteId, boolean isNoBslashEscSet) {
        String entityName;
        if (source == null) return Collections.emptyList();
        if (source.equals((Object)"%")) {
            return Collections.emptyList();
        }
        int dotIndex = -1;
        dotIndex = " ".equals((Object)quoteId) ? source.indexOf((String)".") : StringUtils.indexOfIgnoreCase((int)0, (String)source, (String)".", (String)quoteId, (String)quoteId, isNoBslashEscSet ? SEARCH_MODE__MRK_WS : SEARCH_MODE__BSESC_MRK_WS);
        String database = catalog;
        if (dotIndex != -1) {
            database = StringUtils.unQuoteIdentifier((String)source.substring((int)0, (int)dotIndex), (String)quoteId);
            entityName = StringUtils.unQuoteIdentifier((String)source.substring((int)(dotIndex + 1)), (String)quoteId);
            return Arrays.asList(database, entityName);
        } else {
            entityName = StringUtils.unQuoteIdentifier((String)source, (String)quoteId);
        }
        return Arrays.asList(database, entityName);
    }

    public static boolean isEmptyOrWhitespaceOnly(String str) {
        if (str == null) return true;
        if (str.length() == 0) {
            return true;
        }
        int length = str.length();
        int i = 0;
        while (i < length) {
            if (!Character.isWhitespace((char)str.charAt((int)i))) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static String escapeQuote(String src, String quotChar) {
        if (src == null) {
            return null;
        }
        src = StringUtils.toString((byte[])StringUtils.stripEnclosure((byte[])src.getBytes(), (String)quotChar, (String)quotChar));
        int lastNdx = src.indexOf((String)quotChar);
        String tmpSrc = src.substring((int)0, (int)lastNdx);
        tmpSrc = tmpSrc + quotChar + quotChar;
        String tmpRest = src.substring((int)(lastNdx + 1), (int)src.length());
        lastNdx = tmpRest.indexOf((String)quotChar);
        while (lastNdx > -1) {
            tmpSrc = tmpSrc + tmpRest.substring((int)0, (int)lastNdx);
            tmpSrc = tmpSrc + quotChar + quotChar;
            tmpRest = tmpRest.substring((int)(lastNdx + 1), (int)tmpRest.length());
            lastNdx = tmpRest.indexOf((String)quotChar);
        }
        return tmpSrc = tmpSrc + tmpRest;
    }

    public static String quoteIdentifier(String identifier, String quoteChar, boolean isPedantic) {
        int quoteCharNextPosition;
        int quoteCharNextExpectedPos;
        if (identifier == null) {
            return null;
        }
        identifier = identifier.trim();
        int quoteCharLength = quoteChar.length();
        if (quoteCharLength == 0) return identifier;
        if (" ".equals((Object)quoteChar)) {
            return identifier;
        }
        if (isPedantic) return quoteChar + identifier.replaceAll((String)quoteChar, (String)(quoteChar + quoteChar)) + quoteChar;
        if (!identifier.startsWith((String)quoteChar)) return quoteChar + identifier.replaceAll((String)quoteChar, (String)(quoteChar + quoteChar)) + quoteChar;
        if (!identifier.endsWith((String)quoteChar)) return quoteChar + identifier.replaceAll((String)quoteChar, (String)(quoteChar + quoteChar)) + quoteChar;
        String identifierQuoteTrimmed = identifier.substring((int)quoteCharLength, (int)(identifier.length() - quoteCharLength));
        int quoteCharPos = identifierQuoteTrimmed.indexOf((String)quoteChar);
        while (quoteCharPos >= 0 && (quoteCharNextPosition = identifierQuoteTrimmed.indexOf((String)quoteChar, (int)(quoteCharNextExpectedPos = quoteCharPos + quoteCharLength))) == quoteCharNextExpectedPos) {
            quoteCharPos = identifierQuoteTrimmed.indexOf((String)quoteChar, (int)(quoteCharNextPosition + quoteCharLength));
        }
        if (quoteCharPos >= 0) return quoteChar + identifier.replaceAll((String)quoteChar, (String)(quoteChar + quoteChar)) + quoteChar;
        return identifier;
    }

    public static String quoteIdentifier(String identifier, boolean isPedantic) {
        return StringUtils.quoteIdentifier((String)identifier, (String)"`", (boolean)isPedantic);
    }

    public static String unQuoteIdentifier(String identifier, String quoteChar) {
        if (identifier == null) {
            return null;
        }
        identifier = identifier.trim();
        int quoteCharLength = quoteChar.length();
        if (quoteCharLength == 0) return identifier;
        if (" ".equals((Object)quoteChar)) {
            return identifier;
        }
        if (!identifier.startsWith((String)quoteChar)) return identifier;
        if (!identifier.endsWith((String)quoteChar)) return identifier;
        String identifierQuoteTrimmed = identifier.substring((int)quoteCharLength, (int)(identifier.length() - quoteCharLength));
        int quoteCharPos = identifierQuoteTrimmed.indexOf((String)quoteChar);
        while (quoteCharPos >= 0) {
            int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
            int quoteCharNextPosition = identifierQuoteTrimmed.indexOf((String)quoteChar, (int)quoteCharNextExpectedPos);
            if (quoteCharNextPosition != quoteCharNextExpectedPos) return identifier;
            quoteCharPos = identifierQuoteTrimmed.indexOf((String)quoteChar, (int)(quoteCharNextPosition + quoteCharLength));
        }
        return identifier.substring((int)quoteCharLength, (int)(identifier.length() - quoteCharLength)).replaceAll((String)(quoteChar + quoteChar), (String)quoteChar);
    }

    public static int indexOfQuoteDoubleAware(String searchIn, String quoteChar, int startFrom) {
        if (searchIn == null) return -1;
        if (quoteChar == null) return -1;
        if (quoteChar.length() == 0) return -1;
        if (startFrom > searchIn.length()) {
            return -1;
        }
        int lastIndex = searchIn.length() - 1;
        int beginPos = startFrom;
        int pos = -1;
        boolean next = true;
        while (next) {
            pos = searchIn.indexOf((String)quoteChar, (int)beginPos);
            if (pos == -1 || pos == lastIndex || !searchIn.startsWith((String)quoteChar, (int)(pos + 1))) {
                next = false;
                continue;
            }
            beginPos = pos + 2;
        }
        return pos;
    }

    public static String toString(byte[] value, int offset, int length, String encoding) throws UnsupportedEncodingException {
        Charset cs = StringUtils.findCharset((String)encoding);
        return cs.decode((ByteBuffer)ByteBuffer.wrap((byte[])value, (int)offset, (int)length)).toString();
    }

    public static String toString(byte[] value, String encoding) throws UnsupportedEncodingException {
        Charset cs = StringUtils.findCharset((String)encoding);
        return cs.decode((ByteBuffer)ByteBuffer.wrap((byte[])value)).toString();
    }

    public static String toString(byte[] value, int offset, int length) {
        try {
            Charset cs = StringUtils.findCharset((String)platformEncoding);
            return cs.decode((ByteBuffer)ByteBuffer.wrap((byte[])value, (int)offset, (int)length)).toString();
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String toString(byte[] value) {
        try {
            Charset cs = StringUtils.findCharset((String)platformEncoding);
            return cs.decode((ByteBuffer)ByteBuffer.wrap((byte[])value)).toString();
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(char[] value) {
        try {
            return StringUtils.getBytes((char[])value, (int)0, (int)value.length, (String)platformEncoding);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(char[] value, int offset, int length) {
        try {
            return StringUtils.getBytes((char[])value, (int)offset, (int)length, (String)platformEncoding);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(char[] value, String encoding) throws UnsupportedEncodingException {
        return StringUtils.getBytes((char[])value, (int)0, (int)value.length, (String)encoding);
    }

    public static byte[] getBytes(char[] value, int offset, int length, String encoding) throws UnsupportedEncodingException {
        Charset cs = StringUtils.findCharset((String)encoding);
        ByteBuffer buf = cs.encode((CharBuffer)CharBuffer.wrap((char[])value, (int)offset, (int)length));
        int encodedLen = buf.limit();
        byte[] asBytes = new byte[encodedLen];
        buf.get((byte[])asBytes, (int)0, (int)encodedLen);
        return asBytes;
    }

    public static byte[] getBytes(String value) {
        try {
            return StringUtils.getBytes((String)value, (int)0, (int)value.length(), (String)platformEncoding);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(String value, int offset, int length) {
        try {
            return StringUtils.getBytes((String)value, (int)offset, (int)length, (String)platformEncoding);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(String value, String encoding) throws UnsupportedEncodingException {
        return StringUtils.getBytes((String)value, (int)0, (int)value.length(), (String)encoding);
    }

    public static byte[] getBytes(String value, int offset, int length, String encoding) throws UnsupportedEncodingException {
        if (!Util.isJdbc4()) {
            if (offset != 0) return value.substring((int)offset, (int)(offset + length)).getBytes((String)encoding);
            if (length == value.length()) return value.getBytes((String)encoding);
            return value.substring((int)offset, (int)(offset + length)).getBytes((String)encoding);
        }
        Charset cs = StringUtils.findCharset((String)encoding);
        ByteBuffer buf = cs.encode((CharBuffer)CharBuffer.wrap((char[])value.toCharArray(), (int)offset, (int)length));
        int encodedLen = buf.limit();
        byte[] asBytes = new byte[encodedLen];
        buf.get((byte[])asBytes, (int)0, (int)encodedLen);
        return asBytes;
    }

    public static final boolean isValidIdChar(char c) {
        if (VALID_ID_CHARS.indexOf((int)c) == -1) return false;
        return true;
    }

    public static void appendAsHex(StringBuilder builder, byte[] bytes) {
        builder.append((String)"0x");
        byte[] arr$ = bytes;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            byte b = arr$[i$];
            builder.append((char)HEX_DIGITS[b >>> 4 & 15]).append((char)HEX_DIGITS[b & 15]);
            ++i$;
        }
    }

    public static void appendAsHex(StringBuilder builder, int value) {
        if (value == 0) {
            builder.append((String)"0x0");
            return;
        }
        int shift = 32;
        boolean nonZeroFound = false;
        builder.append((String)"0x");
        do {
            byte nibble = (byte)(value >>> (shift -= 4) & 15);
            if (nonZeroFound) {
                builder.append((char)HEX_DIGITS[nibble]);
                continue;
            }
            if (nibble == 0) continue;
            builder.append((char)HEX_DIGITS[nibble]);
            nonZeroFound = true;
        } while (shift != 0);
    }

    public static byte[] getBytesNullTerminated(String value, String encoding) throws UnsupportedEncodingException {
        Charset cs = StringUtils.findCharset((String)encoding);
        ByteBuffer buf = cs.encode((String)value);
        int encodedLen = buf.limit();
        byte[] asBytes = new byte[encodedLen + 1];
        buf.get((byte[])asBytes, (int)0, (int)encodedLen);
        asBytes[encodedLen] = 0;
        return asBytes;
    }

    public static boolean isStrictlyNumeric(CharSequence cs) {
        if (cs == null) return false;
        if (cs.length() == 0) {
            return false;
        }
        int i = 0;
        while (i < cs.length()) {
            if (!Character.isDigit((char)cs.charAt((int)i))) {
                return false;
            }
            ++i;
        }
        return true;
    }

    static {
        charsetsByAlias = new ConcurrentHashMap<K, V>();
        platformEncoding = System.getProperty((String)"file.encoding");
        for (int i = -128; i <= 127; ++i) {
            StringUtils.allBytes[i - -128] = (byte)i;
        }
        String allBytesString = new String((byte[])allBytes, (int)0, (int)255);
        int allBytesStringLen = allBytesString.length();
        for (int i = 0; i < 255 && i < allBytesStringLen; ++i) {
            StringUtils.byteToChars[i] = allBytesString.charAt((int)i);
        }
        try {
            toPlainStringMethod = BigDecimal.class.getMethod((String)"toPlainString", new Class[0]);
        }
        catch (NoSuchMethodException nsme) {
            // empty catch block
        }
        HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    }
}

