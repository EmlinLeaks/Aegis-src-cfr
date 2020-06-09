/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.AsciiString;
import io.netty.util.NetUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;

public final class NetUtil {
    public static final Inet4Address LOCALHOST4;
    public static final Inet6Address LOCALHOST6;
    public static final InetAddress LOCALHOST;
    public static final NetworkInterface LOOPBACK_IF;
    public static final int SOMAXCONN;
    private static final int IPV6_WORD_COUNT = 8;
    private static final int IPV6_MAX_CHAR_COUNT = 39;
    private static final int IPV6_BYTE_COUNT = 16;
    private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
    private static final int IPV6_MIN_SEPARATORS = 2;
    private static final int IPV6_MAX_SEPARATORS = 8;
    private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
    private static final int IPV4_SEPARATORS = 3;
    private static final boolean IPV4_PREFERRED;
    private static final boolean IPV6_ADDRESSES_PREFERRED;
    private static final InternalLogger logger;

    /*
     * Exception decompiling
     */
    private static Integer sysctlGetInt(String sysctlKey) throws IOException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 5[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
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

    public static boolean isIpV4StackPreferred() {
        return IPV4_PREFERRED;
    }

    public static boolean isIpV6AddressesPreferred() {
        return IPV6_ADDRESSES_PREFERRED;
    }

    public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
        int percentPos;
        if (NetUtil.isValidIpV4Address((String)ipAddressString)) {
            return NetUtil.validIpV4ToBytes((String)ipAddressString);
        }
        if (!NetUtil.isValidIpV6Address((String)ipAddressString)) return null;
        if (ipAddressString.charAt((int)0) == '[') {
            ipAddressString = ipAddressString.substring((int)1, (int)(ipAddressString.length() - 1));
        }
        if ((percentPos = ipAddressString.indexOf((int)37)) < 0) return NetUtil.getIPv6ByName((CharSequence)ipAddressString, (boolean)true);
        ipAddressString = ipAddressString.substring((int)0, (int)percentPos);
        return NetUtil.getIPv6ByName((CharSequence)ipAddressString, (boolean)true);
    }

    private static int decimalDigit(String str, int pos) {
        return str.charAt((int)pos) - 48;
    }

    private static byte ipv4WordToByte(String ip, int from, int toExclusive) {
        int ret = NetUtil.decimalDigit((String)ip, (int)from);
        if (++from == toExclusive) {
            return (byte)ret;
        }
        ret = ret * 10 + NetUtil.decimalDigit((String)ip, (int)from);
        if (++from != toExclusive) return (byte)(ret * 10 + NetUtil.decimalDigit((String)ip, (int)from));
        return (byte)ret;
    }

    static byte[] validIpV4ToBytes(String ip) {
        byte[] arrby = new byte[4];
        int i = ip.indexOf((int)46, (int)1);
        arrby[0] = NetUtil.ipv4WordToByte((String)ip, (int)0, (int)i);
        int n = i + 1;
        i = ip.indexOf((int)46, (int)(i + 2));
        arrby[1] = NetUtil.ipv4WordToByte((String)ip, (int)n, (int)i);
        int n2 = i + 1;
        i = ip.indexOf((int)46, (int)(i + 2));
        arrby[2] = NetUtil.ipv4WordToByte((String)ip, (int)n2, (int)i);
        arrby[3] = NetUtil.ipv4WordToByte((String)ip, (int)(i + 1), (int)ip.length());
        return arrby;
    }

    public static String intToIpAddress(int i) {
        StringBuilder buf = new StringBuilder((int)15);
        buf.append((int)(i >> 24 & 255));
        buf.append((char)'.');
        buf.append((int)(i >> 16 & 255));
        buf.append((char)'.');
        buf.append((int)(i >> 8 & 255));
        buf.append((char)'.');
        buf.append((int)(i & 255));
        return buf.toString();
    }

    public static String bytesToIpAddress(byte[] bytes) {
        return NetUtil.bytesToIpAddress((byte[])bytes, (int)0, (int)bytes.length);
    }

    public static String bytesToIpAddress(byte[] bytes, int offset, int length) {
        switch (length) {
            case 4: {
                return new StringBuilder((int)15).append((int)(bytes[offset] & 255)).append((char)'.').append((int)(bytes[offset + 1] & 255)).append((char)'.').append((int)(bytes[offset + 2] & 255)).append((char)'.').append((int)(bytes[offset + 3] & 255)).toString();
            }
            case 16: {
                return NetUtil.toAddressString((byte[])bytes, (int)offset, (boolean)false);
            }
        }
        throw new IllegalArgumentException((String)("length: " + length + " (expected: 4 or 16)"));
    }

    public static boolean isValidIpV6Address(String ip) {
        return NetUtil.isValidIpV6Address((CharSequence)ip);
    }

    public static boolean isValidIpV6Address(CharSequence ip) {
        int start;
        int compressBegin;
        int colons;
        int end = ip.length();
        if (end < 2) {
            return false;
        }
        char c = ip.charAt((int)0);
        if (c == '[') {
            if (ip.charAt((int)(--end)) != ']') {
                return false;
            }
            start = 1;
            c = ip.charAt((int)1);
        } else {
            start = 0;
        }
        if (c == ':') {
            if (ip.charAt((int)(start + 1)) != ':') {
                return false;
            }
            colons = 2;
            compressBegin = start;
            start += 2;
        } else {
            colons = 0;
            compressBegin = -1;
        }
        int wordLen = 0;
        block5 : for (int i = start; i < end; ++i) {
            c = ip.charAt((int)i);
            if (NetUtil.isValidHexChar((char)c)) {
                if (wordLen >= 4) return false;
                ++wordLen;
                continue;
            }
            switch (c) {
                case ':': {
                    if (colons > 7) {
                        return false;
                    }
                    if (ip.charAt((int)(i - 1)) == ':') {
                        if (compressBegin >= 0) {
                            return false;
                        }
                        compressBegin = i - 1;
                    } else {
                        wordLen = 0;
                    }
                    ++colons;
                    continue block5;
                }
                case '.': {
                    if (compressBegin < 0) {
                        if (colons != 6) return false;
                    }
                    if (colons == 7) {
                        if (compressBegin >= start) return false;
                    }
                    if (colons > 7) {
                        return false;
                    }
                    int ipv4Start = i - wordLen;
                    int j = ipv4Start - 2;
                    if (NetUtil.isValidIPv4MappedChar((char)ip.charAt((int)j))) {
                        if (!NetUtil.isValidIPv4MappedChar((char)ip.charAt((int)(j - 1)))) return false;
                        if (!NetUtil.isValidIPv4MappedChar((char)ip.charAt((int)(j - 2)))) return false;
                        if (!NetUtil.isValidIPv4MappedChar((char)ip.charAt((int)(j - 3)))) {
                            return false;
                        }
                        j -= 5;
                    }
                    do {
                        if (j < start) {
                            int ipv4End = AsciiString.indexOf((CharSequence)ip, (char)'%', (int)(ipv4Start + 7));
                            if (ipv4End >= 0) return NetUtil.isValidIpV4Address((CharSequence)ip, (int)ipv4Start, (int)ipv4End);
                            ipv4End = end;
                            return NetUtil.isValidIpV4Address((CharSequence)ip, (int)ipv4Start, (int)ipv4End);
                        }
                        char tmpChar = ip.charAt((int)j);
                        if (tmpChar != '0' && tmpChar != ':') {
                            return false;
                        }
                        --j;
                    } while (true);
                }
                case '%': {
                    end = i;
                    break block5;
                }
                default: {
                    return false;
                }
            }
        }
        if (compressBegin < 0) {
            if (colons != 7) return false;
            if (wordLen <= 0) return false;
            return true;
        }
        if (compressBegin + 2 == end) return true;
        if (wordLen <= 0) return false;
        if (colons < 8) return true;
        if (compressBegin > start) return false;
        return true;
    }

    private static boolean isValidIpV4Word(CharSequence word, int from, int toExclusive) {
        int len = toExclusive - from;
        if (len < 1) return false;
        if (len > 3) return false;
        char c0 = word.charAt((int)from);
        if (c0 < '0') {
            return false;
        }
        if (len == 3) {
            char c1 = word.charAt((int)(from + 1));
            if (c1 < '0') return false;
            char c2 = word.charAt((int)(from + 2));
            if (c2 < '0') return false;
            if (c0 <= '1' && c1 <= '9') {
                if (c2 <= '9') return true;
            }
            if (c0 != '2') return false;
            if (c1 > '5') return false;
            if (c2 <= '5') return true;
            if (c1 >= '5') return false;
            if (c2 > '9') return false;
            return true;
        }
        if (c0 > '9') return false;
        if (len == 1) return true;
        if (!NetUtil.isValidNumericChar((char)word.charAt((int)(from + 1)))) return false;
        return true;
    }

    private static boolean isValidHexChar(char c) {
        if (c >= '0') {
            if (c <= '9') return true;
        }
        if (c >= 'A') {
            if (c <= 'F') return true;
        }
        if (c < 'a') return false;
        if (c > 'f') return false;
        return true;
    }

    private static boolean isValidNumericChar(char c) {
        if (c < '0') return false;
        if (c > '9') return false;
        return true;
    }

    private static boolean isValidIPv4MappedChar(char c) {
        if (c == 'f') return true;
        if (c == 'F') return true;
        return false;
    }

    private static boolean isValidIPv4MappedSeparators(byte b0, byte b1, boolean mustBeZero) {
        if (b0 != b1) return false;
        if (b0 == 0) return true;
        if (mustBeZero) return false;
        if (b1 != -1) return false;
        return true;
    }

    private static boolean isValidIPv4Mapped(byte[] bytes, int currentIndex, int compressBegin, int compressLength) {
        boolean mustBeZero = compressBegin + compressLength >= 14;
        if (currentIndex > 12) return false;
        if (currentIndex < 2) return false;
        if (mustBeZero) {
            if (compressBegin >= 12) return false;
        }
        if (!NetUtil.isValidIPv4MappedSeparators((byte)bytes[currentIndex - 1], (byte)bytes[currentIndex - 2], (boolean)mustBeZero)) return false;
        if (!PlatformDependent.isZero((byte[])bytes, (int)0, (int)(currentIndex - 3))) return false;
        return true;
    }

    public static boolean isValidIpV4Address(CharSequence ip) {
        return NetUtil.isValidIpV4Address((CharSequence)ip, (int)0, (int)ip.length());
    }

    public static boolean isValidIpV4Address(String ip) {
        return NetUtil.isValidIpV4Address((String)ip, (int)0, (int)ip.length());
    }

    private static boolean isValidIpV4Address(CharSequence ip, int from, int toExcluded) {
        boolean bl;
        if (ip instanceof String) {
            bl = NetUtil.isValidIpV4Address((String)((String)ip), (int)from, (int)toExcluded);
            return bl;
        }
        if (ip instanceof AsciiString) {
            bl = NetUtil.isValidIpV4Address((AsciiString)((AsciiString)ip), (int)from, (int)toExcluded);
            return bl;
        }
        bl = NetUtil.isValidIpV4Address0((CharSequence)ip, (int)from, (int)toExcluded);
        return bl;
    }

    private static boolean isValidIpV4Address(String ip, int from, int toExcluded) {
        int len = toExcluded - from;
        if (len > 15) return false;
        if (len < 7) return false;
        int i = ip.indexOf((int)46, (int)(from + 1));
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)from, (int)i)) return false;
        from = i + 2;
        i = ip.indexOf((int)46, (int)from);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(from - 1), (int)i)) return false;
        from = i + 2;
        i = ip.indexOf((int)46, (int)from);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(from - 1), (int)i)) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(i + 1), (int)toExcluded)) return false;
        return true;
    }

    private static boolean isValidIpV4Address(AsciiString ip, int from, int toExcluded) {
        int len = toExcluded - from;
        if (len > 15) return false;
        if (len < 7) return false;
        int i = ip.indexOf((char)'.', (int)(from + 1));
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)from, (int)i)) return false;
        from = i + 2;
        i = ip.indexOf((char)'.', (int)from);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(from - 1), (int)i)) return false;
        from = i + 2;
        i = ip.indexOf((char)'.', (int)from);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(from - 1), (int)i)) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(i + 1), (int)toExcluded)) return false;
        return true;
    }

    private static boolean isValidIpV4Address0(CharSequence ip, int from, int toExcluded) {
        int len = toExcluded - from;
        if (len > 15) return false;
        if (len < 7) return false;
        int i = AsciiString.indexOf((CharSequence)ip, (char)'.', (int)(from + 1));
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)from, (int)i)) return false;
        from = i + 2;
        i = AsciiString.indexOf((CharSequence)ip, (char)'.', (int)from);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(from - 1), (int)i)) return false;
        from = i + 2;
        i = AsciiString.indexOf((CharSequence)ip, (char)'.', (int)from);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(from - 1), (int)i)) return false;
        if (!NetUtil.isValidIpV4Word((CharSequence)ip, (int)(i + 1), (int)toExcluded)) return false;
        return true;
    }

    public static Inet6Address getByName(CharSequence ip) {
        return NetUtil.getByName((CharSequence)ip, (boolean)true);
    }

    public static Inet6Address getByName(CharSequence ip, boolean ipv4Mapped) {
        byte[] bytes = NetUtil.getIPv6ByName((CharSequence)ip, (boolean)ipv4Mapped);
        if (bytes == null) {
            return null;
        }
        try {
            return Inet6Address.getByAddress(null, (byte[])bytes, (int)-1);
        }
        catch (UnknownHostException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    private static byte[] getIPv6ByName(CharSequence ip, boolean ipv4Mapped) {
        boolean isCompressed;
        int tmp;
        int i;
        byte[] bytes = new byte[16];
        int ipLength = ip.length();
        int compressBegin = 0;
        int compressLength = 0;
        int currentIndex = 0;
        int value = 0;
        int begin = -1;
        int ipv6Separators = 0;
        int ipv4Separators = 0;
        boolean needsShift = false;
        block4 : for (i = 0; i < ipLength; ++i) {
            char c = ip.charAt((int)i);
            switch (c) {
                case ':': {
                    if (i - begin > 4) return null;
                    if (ipv4Separators > 0) return null;
                    if (++ipv6Separators > 8) return null;
                    if (currentIndex + 1 >= bytes.length) {
                        return null;
                    }
                    value <<= 4 - (i - begin) << 2;
                    if (compressLength > 0) {
                        compressLength -= 2;
                    }
                    bytes[currentIndex++] = (byte)((value & 15) << 4 | value >> 4 & 15);
                    bytes[currentIndex++] = (byte)((value >> 8 & 15) << 4 | value >> 12 & 15);
                    tmp = i + 1;
                    if (tmp < ipLength && ip.charAt((int)tmp) == ':') {
                        if (compressBegin != 0) return null;
                        if (++tmp < ipLength && ip.charAt((int)tmp) == ':') {
                            return null;
                        }
                        needsShift = ++ipv6Separators == 2 && value == 0;
                        compressBegin = currentIndex;
                        compressLength = bytes.length - compressBegin - 2;
                        ++i;
                    }
                    value = 0;
                    begin = -1;
                    continue block4;
                }
                case '.': {
                    tmp = i - begin;
                    if (tmp > 3) return null;
                    if (begin < 0) return null;
                    if (++ipv4Separators > 3) return null;
                    if (ipv6Separators > 0) {
                        if (currentIndex + compressLength < 12) return null;
                    }
                    if (i + 1 >= ipLength) return null;
                    if (currentIndex >= bytes.length) return null;
                    if (ipv4Separators == 1) {
                        if (!ipv4Mapped) return null;
                        if (currentIndex != 0) {
                            if (!NetUtil.isValidIPv4Mapped((byte[])bytes, (int)currentIndex, (int)compressBegin, (int)compressLength)) return null;
                        }
                        if (tmp == 3) {
                            if (!NetUtil.isValidNumericChar((char)ip.charAt((int)(i - 1)))) return null;
                            if (!NetUtil.isValidNumericChar((char)ip.charAt((int)(i - 2)))) return null;
                            if (!NetUtil.isValidNumericChar((char)ip.charAt((int)(i - 3)))) return null;
                        }
                        if (tmp == 2) {
                            if (!NetUtil.isValidNumericChar((char)ip.charAt((int)(i - 1)))) return null;
                            if (!NetUtil.isValidNumericChar((char)ip.charAt((int)(i - 2)))) return null;
                        }
                        if (tmp == 1 && !NetUtil.isValidNumericChar((char)ip.charAt((int)(i - 1)))) {
                            return null;
                        }
                    }
                    if ((begin = ((value <<= 3 - tmp << 2) & 15) * 100 + (value >> 4 & 15) * 10 + (value >> 8 & 15)) < 0) return null;
                    if (begin > 255) {
                        return null;
                    }
                    bytes[currentIndex++] = (byte)begin;
                    value = 0;
                    begin = -1;
                    continue block4;
                }
            }
            if (!NetUtil.isValidHexChar((char)c)) return null;
            if (ipv4Separators > 0 && !NetUtil.isValidNumericChar((char)c)) {
                return null;
            }
            if (begin < 0) {
                begin = i;
            } else if (i - begin > 4) {
                return null;
            }
            value += StringUtil.decodeHexNibble((char)c) << (i - begin << 2);
        }
        boolean bl = isCompressed = compressBegin > 0;
        if (ipv4Separators > 0) {
            if (begin > 0) {
                if (i - begin > 3) return null;
            }
            if (ipv4Separators != 3) return null;
            if (currentIndex >= bytes.length) {
                return null;
            }
            if (ipv6Separators == 0) {
                compressLength = 12;
            } else {
                if (ipv6Separators < 2) return null;
                if (isCompressed || ipv6Separators != 6 || ip.charAt((int)0) == ':') {
                    if (!isCompressed) return null;
                    if (ipv6Separators >= 8) return null;
                    if (ip.charAt((int)0) == ':') {
                        if (compressBegin > 2) return null;
                    }
                }
                compressLength -= 2;
            }
            value <<= 3 - (i - begin) << 2;
            begin = (value & 15) * 100 + (value >> 4 & 15) * 10 + (value >> 8 & 15);
            if (begin < 0) return null;
            if (begin > 255) {
                return null;
            }
            bytes[currentIndex++] = (byte)begin;
        } else {
            tmp = ipLength - 1;
            if (begin > 0) {
                if (i - begin > 4) return null;
            }
            if (ipv6Separators < 2) return null;
            if (!isCompressed) {
                if (ipv6Separators + 1 != 8) return null;
                if (ip.charAt((int)0) == ':') return null;
                if (ip.charAt((int)tmp) == ':') return null;
            }
            if (isCompressed) {
                if (ipv6Separators > 8) return null;
                if (ipv6Separators == 8) {
                    if (compressBegin <= 2) {
                        if (ip.charAt((int)0) != ':') return null;
                    }
                    if (compressBegin >= 14) {
                        if (ip.charAt((int)tmp) != ':') return null;
                    }
                }
            }
            if (currentIndex + 1 >= bytes.length) return null;
            if (begin < 0) {
                if (ip.charAt((int)(tmp - 1)) != ':') return null;
            }
            if (compressBegin > 2 && ip.charAt((int)0) == ':') {
                return null;
            }
            if (begin >= 0 && i - begin <= 4) {
                value <<= 4 - (i - begin) << 2;
            }
            bytes[currentIndex++] = (byte)((value & 15) << 4 | value >> 4 & 15);
            bytes[currentIndex++] = (byte)((value >> 8 & 15) << 4 | value >> 12 & 15);
        }
        i = currentIndex + compressLength;
        if (!needsShift && i < bytes.length) {
            for (i = 0; i < compressLength && (currentIndex = (begin = i + compressBegin) + compressLength) < bytes.length; ++i) {
                bytes[currentIndex] = bytes[begin];
                bytes[begin] = 0;
            }
        } else {
            if (i >= bytes.length) {
                ++compressBegin;
            }
            for (i = currentIndex; i < bytes.length; ++compressBegin, ++i) {
                for (begin = bytes.length - 1; begin >= compressBegin; --begin) {
                    bytes[begin] = bytes[begin - 1];
                }
                bytes[begin] = 0;
            }
        }
        if (ipv4Separators <= 0) return bytes;
        bytes[11] = -1;
        bytes[10] = -1;
        return bytes;
    }

    public static String toSocketAddressString(InetSocketAddress addr) {
        String hostname;
        StringBuilder sb;
        String port = String.valueOf((int)addr.getPort());
        if (!addr.isUnresolved()) {
            InetAddress address = addr.getAddress();
            String hostString = NetUtil.toAddressString((InetAddress)address);
            sb = NetUtil.newSocketAddressStringBuilder((String)hostString, (String)port, (boolean)(address instanceof Inet4Address));
            return sb.append((char)':').append((String)port).toString();
        }
        sb = NetUtil.newSocketAddressStringBuilder((String)hostname, (String)port, (boolean)(!NetUtil.isValidIpV6Address((String)(hostname = NetUtil.getHostname((InetSocketAddress)addr)))));
        return sb.append((char)':').append((String)port).toString();
    }

    public static String toSocketAddressString(String host, int port) {
        boolean bl;
        String portStr = String.valueOf((int)port);
        if (!NetUtil.isValidIpV6Address((String)host)) {
            bl = true;
            return NetUtil.newSocketAddressStringBuilder((String)host, (String)portStr, (boolean)bl).append((char)':').append((String)portStr).toString();
        }
        bl = false;
        return NetUtil.newSocketAddressStringBuilder((String)host, (String)portStr, (boolean)bl).append((char)':').append((String)portStr).toString();
    }

    private static StringBuilder newSocketAddressStringBuilder(String host, String port, boolean ipv4) {
        int hostLen = host.length();
        if (ipv4) {
            return new StringBuilder((int)(hostLen + 1 + port.length())).append((String)host);
        }
        StringBuilder stringBuilder = new StringBuilder((int)(hostLen + 3 + port.length()));
        if (hostLen <= 1) return stringBuilder.append((char)'[').append((String)host).append((char)']');
        if (host.charAt((int)0) != '[') return stringBuilder.append((char)'[').append((String)host).append((char)']');
        if (host.charAt((int)(hostLen - 1)) != ']') return stringBuilder.append((char)'[').append((String)host).append((char)']');
        return stringBuilder.append((String)host);
    }

    public static String toAddressString(InetAddress ip) {
        return NetUtil.toAddressString((InetAddress)ip, (boolean)false);
    }

    public static String toAddressString(InetAddress ip, boolean ipv4Mapped) {
        if (ip instanceof Inet4Address) {
            return ip.getHostAddress();
        }
        if (ip instanceof Inet6Address) return NetUtil.toAddressString((byte[])ip.getAddress(), (int)0, (boolean)ipv4Mapped);
        throw new IllegalArgumentException((String)("Unhandled type: " + ip));
    }

    private static String toAddressString(byte[] bytes, int offset, boolean ipv4Mapped) {
        int i;
        boolean isIpv4Mapped;
        int currentLength;
        int[] words = new int[8];
        int end = offset + words.length;
        for (i = offset; i < end; ++i) {
            words[i] = (bytes[i << 1] & 255) << 8 | bytes[(i << 1) + 1] & 255;
        }
        int currentStart = -1;
        int shortestStart = -1;
        int shortestLength = 0;
        for (i = 0; i < words.length; ++i) {
            if (words[i] == 0) {
                if (currentStart >= 0) continue;
                currentStart = i;
                continue;
            }
            if (currentStart < 0) continue;
            currentLength = i - currentStart;
            if (currentLength > shortestLength) {
                shortestStart = currentStart;
                shortestLength = currentLength;
            }
            currentStart = -1;
        }
        if (currentStart >= 0 && (currentLength = i - currentStart) > shortestLength) {
            shortestStart = currentStart;
            shortestLength = currentLength;
        }
        if (shortestLength == 1) {
            shortestLength = 0;
            shortestStart = -1;
        }
        int shortestEnd = shortestStart + shortestLength;
        StringBuilder b = new StringBuilder((int)39);
        if (shortestEnd < 0) {
            b.append((String)Integer.toHexString((int)words[0]));
            i = 1;
            while (i < words.length) {
                b.append((char)':');
                b.append((String)Integer.toHexString((int)words[i]));
                ++i;
            }
            return b.toString();
        }
        if (NetUtil.inRangeEndExclusive((int)0, (int)shortestStart, (int)shortestEnd)) {
            b.append((String)"::");
            isIpv4Mapped = ipv4Mapped && shortestEnd == 5 && words[5] == 65535;
        } else {
            b.append((String)Integer.toHexString((int)words[0]));
            isIpv4Mapped = false;
        }
        i = 1;
        while (i < words.length) {
            if (!NetUtil.inRangeEndExclusive((int)i, (int)shortestStart, (int)shortestEnd)) {
                if (!NetUtil.inRangeEndExclusive((int)(i - 1), (int)shortestStart, (int)shortestEnd)) {
                    if (!isIpv4Mapped || i == 6) {
                        b.append((char)':');
                    } else {
                        b.append((char)'.');
                    }
                }
                if (isIpv4Mapped && i > 5) {
                    b.append((int)(words[i] >> 8));
                    b.append((char)'.');
                    b.append((int)(words[i] & 255));
                } else {
                    b.append((String)Integer.toHexString((int)words[i]));
                }
            } else if (!NetUtil.inRangeEndExclusive((int)(i - 1), (int)shortestStart, (int)shortestEnd)) {
                b.append((String)"::");
            }
            ++i;
        }
        return b.toString();
    }

    public static String getHostname(InetSocketAddress addr) {
        String string;
        if (PlatformDependent.javaVersion() >= 7) {
            string = addr.getHostString();
            return string;
        }
        string = addr.getHostName();
        return string;
    }

    private static boolean inRangeEndExclusive(int value, int start, int end) {
        if (value < start) return false;
        if (value >= end) return false;
        return true;
    }

    private NetUtil() {
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }

    static /* synthetic */ Integer access$100(String x0) throws IOException {
        return NetUtil.sysctlGetInt((String)x0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        Enumeration<InetAddress> i;
        IPV4_PREFERRED = SystemPropertyUtil.getBoolean((String)"java.net.preferIPv4Stack", (boolean)false);
        IPV6_ADDRESSES_PREFERRED = SystemPropertyUtil.getBoolean((String)"java.net.preferIPv6Addresses", (boolean)false);
        logger = InternalLoggerFactory.getInstance(NetUtil.class);
        logger.debug((String)"-Djava.net.preferIPv4Stack: {}", (Object)Boolean.valueOf((boolean)IPV4_PREFERRED));
        logger.debug((String)"-Djava.net.preferIPv6Addresses: {}", (Object)Boolean.valueOf((boolean)IPV6_ADDRESSES_PREFERRED));
        byte[] LOCALHOST4_BYTES = new byte[]{127, 0, 0, 1};
        byte[] LOCALHOST6_BYTES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        Inet4Address localhost4 = null;
        try {
            localhost4 = (Inet4Address)InetAddress.getByAddress((String)"localhost", (byte[])LOCALHOST4_BYTES);
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
        }
        LOCALHOST4 = localhost4;
        Inet6Address localhost6 = null;
        try {
            localhost6 = (Inet6Address)InetAddress.getByAddress((String)"localhost", (byte[])LOCALHOST6_BYTES);
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
        }
        LOCALHOST6 = localhost6;
        ArrayList<NetworkInterface> ifaces = new ArrayList<NetworkInterface>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    if (!SocketUtils.addressesFromNetworkInterface((NetworkInterface)iface).hasMoreElements()) continue;
                    ifaces.add(iface);
                }
            }
        }
        catch (SocketException e) {
            logger.warn((String)"Failed to retrieve the list of available network interfaces", (Throwable)e);
        }
        NetworkInterface loopbackIface = null;
        InetAddress loopbackAddr = null;
        block14 : for (NetworkInterface iface : ifaces) {
            i = SocketUtils.addressesFromNetworkInterface((NetworkInterface)iface);
            while (i.hasMoreElements()) {
                InetAddress addr = i.nextElement();
                if (!addr.isLoopbackAddress()) continue;
                loopbackIface = iface;
                loopbackAddr = addr;
                break block14;
            }
        }
        if (loopbackIface == null) {
            try {
                for (NetworkInterface iface : ifaces) {
                    if (!iface.isLoopback() || !(i = SocketUtils.addressesFromNetworkInterface((NetworkInterface)iface)).hasMoreElements()) continue;
                    loopbackIface = iface;
                    loopbackAddr = i.nextElement();
                    break;
                }
                if (loopbackIface == null) {
                    logger.warn((String)"Failed to find the loopback interface");
                }
            }
            catch (SocketException e) {
                logger.warn((String)"Failed to find the loopback interface", (Throwable)e);
            }
        }
        if (loopbackIface != null) {
            logger.debug((String)"Loopback interface: {} ({}, {})", (Object[])new Object[]{loopbackIface.getName(), loopbackIface.getDisplayName(), loopbackAddr.getHostAddress()});
        } else if (loopbackAddr == null) {
            try {
                if (NetworkInterface.getByInetAddress((InetAddress)LOCALHOST6) != null) {
                    logger.debug((String)"Using hard-coded IPv6 localhost address: {}", (Object)localhost6);
                    loopbackAddr = localhost6;
                }
            }
            catch (Exception e) {
            }
            finally {
                if (loopbackAddr == null) {
                    logger.debug((String)"Using hard-coded IPv4 localhost address: {}", (Object)localhost4);
                    loopbackAddr = localhost4;
                }
            }
        }
        LOOPBACK_IF = loopbackIface;
        LOCALHOST = loopbackAddr;
        SOMAXCONN = AccessController.doPrivileged(new PrivilegedAction<Integer>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public Integer run() {
                int somaxconn = PlatformDependent.isWindows() ? 200 : 128;
                java.io.File file = new java.io.File((String)"/proc/sys/net/core/somaxconn");
                java.io.BufferedReader in = null;
                try {
                    if (file.exists()) {
                        in = new java.io.BufferedReader((java.io.Reader)new java.io.FileReader((java.io.File)file));
                        somaxconn = Integer.parseInt((String)in.readLine());
                        if (!NetUtil.access$000().isDebugEnabled()) return Integer.valueOf((int)somaxconn);
                        NetUtil.access$000().debug((String)"{}: {}", (Object)file, (Object)Integer.valueOf((int)somaxconn));
                        return Integer.valueOf((int)somaxconn);
                    }
                    Integer tmp = null;
                    if (SystemPropertyUtil.getBoolean((String)"io.netty.net.somaxconn.trySysctl", (boolean)false)) {
                        tmp = NetUtil.access$100((String)"kern.ipc.somaxconn");
                        if (tmp == null) {
                            tmp = NetUtil.access$100((String)"kern.ipc.soacceptqueue");
                            if (tmp != null) {
                                somaxconn = tmp.intValue();
                            }
                        } else {
                            somaxconn = tmp.intValue();
                        }
                    }
                    if (tmp != null) return Integer.valueOf((int)somaxconn);
                    NetUtil.access$000().debug((String)"Failed to get SOMAXCONN from sysctl and file {}. Default: {}", (Object)file, (Object)Integer.valueOf((int)somaxconn));
                    return Integer.valueOf((int)somaxconn);
                }
                catch (Exception e) {
                    NetUtil.access$000().debug((String)"Failed to get SOMAXCONN from sysctl and file {}. Default: {}", (Object[])new Object[]{file, Integer.valueOf((int)somaxconn), e});
                    return Integer.valueOf((int)somaxconn);
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Exception e) {}
                    }
                }
            }
        }).intValue();
    }
}

