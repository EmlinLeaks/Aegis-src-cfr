/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class StringUtil {
    public static final String EMPTY_STRING = "";
    public static final String NEWLINE = SystemPropertyUtil.get((String)"line.separator", (String)"\n");
    public static final char DOUBLE_QUOTE = '\"';
    public static final char COMMA = ',';
    public static final char LINE_FEED = '\n';
    public static final char CARRIAGE_RETURN = '\r';
    public static final char TAB = '\t';
    public static final char SPACE = ' ';
    private static final String[] BYTE2HEX_PAD = new String[256];
    private static final String[] BYTE2HEX_NOPAD = new String[256];
    private static final int CSV_NUMBER_ESCAPE_CHARACTERS = 7;
    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    private StringUtil() {
    }

    public static String substringAfter(String value, char delim) {
        int pos = value.indexOf((int)delim);
        if (pos < 0) return null;
        return value.substring((int)(pos + 1));
    }

    public static boolean commonSuffixOfLength(String s, String p, int len) {
        if (s == null) return false;
        if (p == null) return false;
        if (len < 0) return false;
        if (!s.regionMatches((int)(s.length() - len), (String)p, (int)(p.length() - len), (int)len)) return false;
        return true;
    }

    public static String byteToHexStringPadded(int value) {
        return BYTE2HEX_PAD[value & 255];
    }

    public static <T extends Appendable> T byteToHexStringPadded(T buf, int value) {
        try {
            buf.append((CharSequence)StringUtil.byteToHexStringPadded((int)value));
            return (T)((T)buf);
        }
        catch (IOException e) {
            PlatformDependent.throwException((Throwable)e);
        }
        return (T)buf;
    }

    public static String toHexStringPadded(byte[] src) {
        return StringUtil.toHexStringPadded((byte[])src, (int)0, (int)src.length);
    }

    public static String toHexStringPadded(byte[] src, int offset, int length) {
        return StringUtil.toHexStringPadded(new StringBuilder((int)(length << 1)), (byte[])src, (int)offset, (int)length).toString();
    }

    public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src) {
        return (T)StringUtil.toHexStringPadded(dst, (byte[])src, (int)0, (int)src.length);
    }

    public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src, int offset, int length) {
        int end = offset + length;
        int i = offset;
        while (i < end) {
            StringUtil.byteToHexStringPadded(dst, (int)src[i]);
            ++i;
        }
        return (T)dst;
    }

    public static String byteToHexString(int value) {
        return BYTE2HEX_NOPAD[value & 255];
    }

    public static <T extends Appendable> T byteToHexString(T buf, int value) {
        try {
            buf.append((CharSequence)StringUtil.byteToHexString((int)value));
            return (T)((T)buf);
        }
        catch (IOException e) {
            PlatformDependent.throwException((Throwable)e);
        }
        return (T)buf;
    }

    public static String toHexString(byte[] src) {
        return StringUtil.toHexString((byte[])src, (int)0, (int)src.length);
    }

    public static String toHexString(byte[] src, int offset, int length) {
        return StringUtil.toHexString(new StringBuilder((int)(length << 1)), (byte[])src, (int)offset, (int)length).toString();
    }

    public static <T extends Appendable> T toHexString(T dst, byte[] src) {
        return (T)StringUtil.toHexString(dst, (byte[])src, (int)0, (int)src.length);
    }

    public static <T extends Appendable> T toHexString(T dst, byte[] src, int offset, int length) {
        int i;
        assert (length >= 0);
        if (length == 0) {
            return (T)dst;
        }
        int end = offset + length;
        int endMinusOne = end - 1;
        for (i = offset; i < endMinusOne && src[i] == 0; ++i) {
        }
        StringUtil.byteToHexString(dst, (int)src[i++]);
        int remaining = end - i;
        StringUtil.toHexStringPadded(dst, (byte[])src, (int)i, (int)remaining);
        return (T)dst;
    }

    public static int decodeHexNibble(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 55;
        }
        if (c < 'a') return -1;
        if (c > 'f') return -1;
        return c - 87;
    }

    public static byte decodeHexByte(CharSequence s, int pos) {
        int hi = StringUtil.decodeHexNibble((char)s.charAt((int)pos));
        int lo = StringUtil.decodeHexNibble((char)s.charAt((int)(pos + 1)));
        if (hi == -1) throw new IllegalArgumentException((String)String.format((String)"invalid hex byte '%s' at index %d of '%s'", (Object[])new Object[]{s.subSequence((int)pos, (int)(pos + 2)), Integer.valueOf((int)pos), s}));
        if (lo != -1) return (byte)((hi << 4) + lo);
        throw new IllegalArgumentException((String)String.format((String)"invalid hex byte '%s' at index %d of '%s'", (Object[])new Object[]{s.subSequence((int)pos, (int)(pos + 2)), Integer.valueOf((int)pos), s}));
    }

    public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) {
        if (length < 0) throw new IllegalArgumentException((String)("length: " + length));
        if ((length & 1) != 0) {
            throw new IllegalArgumentException((String)("length: " + length));
        }
        if (length == 0) {
            return EmptyArrays.EMPTY_BYTES;
        }
        byte[] bytes = new byte[length >>> 1];
        int i = 0;
        while (i < length) {
            bytes[i >>> 1] = StringUtil.decodeHexByte((CharSequence)hexDump, (int)(fromIndex + i));
            i += 2;
        }
        return bytes;
    }

    public static byte[] decodeHexDump(CharSequence hexDump) {
        return StringUtil.decodeHexDump((CharSequence)hexDump, (int)0, (int)hexDump.length());
    }

    public static String simpleClassName(Object o) {
        if (o != null) return StringUtil.simpleClassName(o.getClass());
        return "null_object";
    }

    public static String simpleClassName(Class<?> clazz) {
        String className = ObjectUtil.checkNotNull(clazz, (String)"clazz").getName();
        int lastDotIdx = className.lastIndexOf((int)46);
        if (lastDotIdx <= -1) return className;
        return className.substring((int)(lastDotIdx + 1));
    }

    public static CharSequence escapeCsv(CharSequence value) {
        return StringUtil.escapeCsv((CharSequence)value, (boolean)false);
    }

    public static CharSequence escapeCsv(CharSequence value, boolean trimWhiteSpace) {
        int last;
        int start;
        int length = ObjectUtil.checkNotNull(value, (String)"value").length();
        if (trimWhiteSpace) {
            start = StringUtil.indexOfFirstNonOwsChar((CharSequence)value, (int)length);
            last = StringUtil.indexOfLastNonOwsChar((CharSequence)value, (int)start, (int)length);
        } else {
            start = 0;
            last = length - 1;
        }
        if (start > last) {
            return EMPTY_STRING;
        }
        int firstUnescapedSpecial = -1;
        boolean quoted = false;
        if (StringUtil.isDoubleQuote((char)value.charAt((int)start))) {
            boolean bl = quoted = StringUtil.isDoubleQuote((char)value.charAt((int)last)) && last > start;
            if (quoted) {
                ++start;
                --last;
            } else {
                firstUnescapedSpecial = start;
            }
        }
        if (firstUnescapedSpecial < 0) {
            int i;
            if (quoted) {
                for (i = start; i <= last; ++i) {
                    if (!StringUtil.isDoubleQuote((char)value.charAt((int)i))) continue;
                    if (i == last || !StringUtil.isDoubleQuote((char)value.charAt((int)(i + 1)))) {
                        firstUnescapedSpecial = i;
                        break;
                    }
                    ++i;
                }
            } else {
                for (i = start; i <= last; ++i) {
                    char c = value.charAt((int)i);
                    if (c == '\n' || c == '\r' || c == ',') {
                        firstUnescapedSpecial = i;
                        break;
                    }
                    if (!StringUtil.isDoubleQuote((char)c)) continue;
                    if (i == last || !StringUtil.isDoubleQuote((char)value.charAt((int)(i + 1)))) {
                        firstUnescapedSpecial = i;
                        break;
                    }
                    ++i;
                }
            }
            if (firstUnescapedSpecial < 0) {
                CharSequence charSequence;
                if (quoted) {
                    charSequence = value.subSequence((int)(start - 1), (int)(last + 2));
                    return charSequence;
                }
                charSequence = value.subSequence((int)start, (int)(last + 1));
                return charSequence;
            }
        }
        StringBuilder result = new StringBuilder((int)(last - start + 1 + 7));
        result.append((char)'\"').append((CharSequence)value, (int)start, (int)firstUnescapedSpecial);
        int i = firstUnescapedSpecial;
        while (i <= last) {
            char c = value.charAt((int)i);
            if (StringUtil.isDoubleQuote((char)c)) {
                result.append((char)'\"');
                if (i < last && StringUtil.isDoubleQuote((char)value.charAt((int)(i + 1)))) {
                    ++i;
                }
            }
            result.append((char)c);
            ++i;
        }
        return result.append((char)'\"');
    }

    public static CharSequence unescapeCsv(CharSequence value) {
        boolean quoted;
        int length = ObjectUtil.checkNotNull(value, (String)"value").length();
        if (length == 0) {
            return value;
        }
        int last = length - 1;
        boolean bl = quoted = StringUtil.isDoubleQuote((char)value.charAt((int)0)) && StringUtil.isDoubleQuote((char)value.charAt((int)last)) && length != 1;
        if (!quoted) {
            StringUtil.validateCsvFormat((CharSequence)value);
            return value;
        }
        StringBuilder unescaped = InternalThreadLocalMap.get().stringBuilder();
        int i = 1;
        while (i < last) {
            char current = value.charAt((int)i);
            if (current == '\"') {
                if (!StringUtil.isDoubleQuote((char)value.charAt((int)(i + 1)))) throw StringUtil.newInvalidEscapedCsvFieldException((CharSequence)value, (int)i);
                if (i + 1 == last) throw StringUtil.newInvalidEscapedCsvFieldException((CharSequence)value, (int)i);
                ++i;
            }
            unescaped.append((char)current);
            ++i;
        }
        return unescaped.toString();
    }

    /*
     * Unable to fully structure code
     */
    public static List<CharSequence> unescapeCsvFields(CharSequence value) {
        unescaped = new ArrayList<CharSequence>((int)2);
        current = InternalThreadLocalMap.get().stringBuilder();
        quoted = false;
        last = value.length() - 1;
        block8 : for (i = 0; i <= last; ++i) {
            c = value.charAt((int)i);
            if (quoted) {
                switch (c) {
                    case '\"': {
                        if (i == last) {
                            unescaped.add((CharSequence)current.toString());
                            return unescaped;
                        }
                        if ((next = value.charAt((int)(++i))) == '\"') {
                            current.append((char)'\"');
                            ** break;
                        }
                        if (next != ',') throw StringUtil.newInvalidEscapedCsvFieldException((CharSequence)value, (int)(i - 1));
                        quoted = false;
                        unescaped.add((CharSequence)current.toString());
                        current.setLength((int)0);
                        ** break;
                    }
                }
                current.append((char)c);
                ** break;
lbl27: // 3 sources:
                continue;
            }
            switch (c) {
                case ',': {
                    unescaped.add((CharSequence)current.toString());
                    current.setLength((int)0);
                    continue block8;
                }
                case '\"': {
                    if (current.length() != 0) throw StringUtil.newInvalidEscapedCsvFieldException((CharSequence)value, (int)i);
                    quoted = true;
                    continue block8;
                }
                case '\n': 
                case '\r': {
                    throw StringUtil.newInvalidEscapedCsvFieldException((CharSequence)value, (int)i);
                }
            }
            current.append((char)c);
        }
        if (quoted) {
            throw StringUtil.newInvalidEscapedCsvFieldException((CharSequence)value, (int)last);
        }
        unescaped.add(current.toString());
        return unescaped;
    }

    /*
     * Exception decompiling
     */
    private static void validateCsvFormat(CharSequence value) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:404)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:482)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    private static IllegalArgumentException newInvalidEscapedCsvFieldException(CharSequence value, int index) {
        return new IllegalArgumentException((String)("invalid escaped CSV field: " + value + " index: " + index));
    }

    public static int length(String s) {
        if (s == null) {
            return 0;
        }
        int n = s.length();
        return n;
    }

    public static boolean isNullOrEmpty(String s) {
        if (s == null) return true;
        if (s.isEmpty()) return true;
        return false;
    }

    public static int indexOfNonWhiteSpace(CharSequence seq, int offset) {
        while (offset < seq.length()) {
            if (!Character.isWhitespace((char)seq.charAt((int)offset))) {
                return offset;
            }
            ++offset;
        }
        return -1;
    }

    public static boolean isSurrogate(char c) {
        if (c < '\ud800') return false;
        if (c > '\udfff') return false;
        return true;
    }

    private static boolean isDoubleQuote(char c) {
        if (c != '\"') return false;
        return true;
    }

    public static boolean endsWith(CharSequence s, char c) {
        int len = s.length();
        if (len <= 0) return false;
        if (s.charAt((int)(len - 1)) != c) return false;
        return true;
    }

    public static CharSequence trimOws(CharSequence value) {
        CharSequence charSequence;
        int length = value.length();
        if (length == 0) {
            return value;
        }
        int start = StringUtil.indexOfFirstNonOwsChar((CharSequence)value, (int)length);
        int end = StringUtil.indexOfLastNonOwsChar((CharSequence)value, (int)start, (int)length);
        if (start == 0 && end == length - 1) {
            charSequence = value;
            return charSequence;
        }
        charSequence = value.subSequence((int)start, (int)(end + 1));
        return charSequence;
    }

    public static CharSequence join(CharSequence separator, Iterable<? extends CharSequence> elements) {
        ObjectUtil.checkNotNull(separator, (String)"separator");
        ObjectUtil.checkNotNull(elements, (String)"elements");
        Iterator<? extends CharSequence> iterator = elements.iterator();
        if (!iterator.hasNext()) {
            return EMPTY_STRING;
        }
        CharSequence firstElement = iterator.next();
        if (!iterator.hasNext()) {
            return firstElement;
        }
        StringBuilder builder = new StringBuilder((CharSequence)firstElement);
        do {
            builder.append((CharSequence)separator).append((CharSequence)iterator.next());
        } while (iterator.hasNext());
        return builder;
    }

    private static int indexOfFirstNonOwsChar(CharSequence value, int length) {
        int i = 0;
        while (i < length) {
            if (!StringUtil.isOws((char)value.charAt((int)i))) return i;
            ++i;
        }
        return i;
    }

    private static int indexOfLastNonOwsChar(CharSequence value, int start, int length) {
        int i = length - 1;
        while (i > start) {
            if (!StringUtil.isOws((char)value.charAt((int)i))) return i;
            --i;
        }
        return i;
    }

    private static boolean isOws(char c) {
        if (c == ' ') return true;
        if (c == '\t') return true;
        return false;
    }

    static {
        int i = 0;
        while (i < BYTE2HEX_PAD.length) {
            String str = Integer.toHexString((int)i);
            StringUtil.BYTE2HEX_PAD[i] = i > 15 ? str : '0' + str;
            StringUtil.BYTE2HEX_NOPAD[i] = str;
            ++i;
        }
    }
}

