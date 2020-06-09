/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

public final class AsciiString
implements CharSequence,
Comparable<CharSequence> {
    public static final AsciiString EMPTY_STRING = AsciiString.cached((String)"");
    private static final char MAX_CHAR_VALUE = '\u00ff';
    public static final int INDEX_NOT_FOUND = -1;
    private final byte[] value;
    private final int offset;
    private final int length;
    private int hash;
    private String string;
    public static final HashingStrategy<CharSequence> CASE_INSENSITIVE_HASHER = new HashingStrategy<CharSequence>(){

        public int hashCode(CharSequence o) {
            return AsciiString.hashCode((CharSequence)o);
        }

        public boolean equals(CharSequence a, CharSequence b) {
            return AsciiString.contentEqualsIgnoreCase((CharSequence)a, (CharSequence)b);
        }
    };
    public static final HashingStrategy<CharSequence> CASE_SENSITIVE_HASHER = new HashingStrategy<CharSequence>(){

        public int hashCode(CharSequence o) {
            return AsciiString.hashCode((CharSequence)o);
        }

        public boolean equals(CharSequence a, CharSequence b) {
            return AsciiString.contentEquals((CharSequence)a, (CharSequence)b);
        }
    };

    public AsciiString(byte[] value) {
        this((byte[])value, (boolean)true);
    }

    public AsciiString(byte[] value, boolean copy) {
        this((byte[])value, (int)0, (int)value.length, (boolean)copy);
    }

    public AsciiString(byte[] value, int start, int length, boolean copy) {
        if (copy) {
            this.value = Arrays.copyOfRange((byte[])value, (int)start, (int)(start + length));
            this.offset = 0;
        } else {
            if (MathUtil.isOutOfBounds((int)start, (int)length, (int)value.length)) {
                throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length + ')'));
            }
            this.value = value;
            this.offset = start;
        }
        this.length = length;
    }

    public AsciiString(ByteBuffer value) {
        this((ByteBuffer)value, (boolean)true);
    }

    public AsciiString(ByteBuffer value, boolean copy) {
        this((ByteBuffer)value, (int)value.position(), (int)value.remaining(), (boolean)copy);
    }

    public AsciiString(ByteBuffer value, int start, int length, boolean copy) {
        if (MathUtil.isOutOfBounds((int)start, (int)length, (int)value.capacity())) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.capacity(" + value.capacity() + ')'));
        }
        if (value.hasArray()) {
            if (copy) {
                int bufferOffset = value.arrayOffset() + start;
                this.value = Arrays.copyOfRange((byte[])value.array(), (int)bufferOffset, (int)(bufferOffset + length));
                this.offset = 0;
            } else {
                this.value = value.array();
                this.offset = start;
            }
        } else {
            this.value = PlatformDependent.allocateUninitializedArray((int)length);
            int oldPos = value.position();
            value.get((byte[])this.value, (int)0, (int)length);
            value.position((int)oldPos);
            this.offset = 0;
        }
        this.length = length;
    }

    public AsciiString(char[] value) {
        this((char[])value, (int)0, (int)value.length);
    }

    public AsciiString(char[] value, int start, int length) {
        if (MathUtil.isOutOfBounds((int)start, (int)length, (int)value.length)) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length + ')'));
        }
        this.value = PlatformDependent.allocateUninitializedArray((int)length);
        int i = 0;
        int j = start;
        do {
            if (i >= length) {
                this.offset = 0;
                this.length = length;
                return;
            }
            this.value[i] = AsciiString.c2b((char)value[j]);
            ++i;
            ++j;
        } while (true);
    }

    public AsciiString(char[] value, Charset charset) {
        this((char[])value, (Charset)charset, (int)0, (int)value.length);
    }

    public AsciiString(char[] value, Charset charset, int start, int length) {
        CharBuffer cbuf = CharBuffer.wrap((char[])value, (int)start, (int)length);
        CharsetEncoder encoder = CharsetUtil.encoder((Charset)charset);
        ByteBuffer nativeBuffer = ByteBuffer.allocate((int)((int)(encoder.maxBytesPerChar() * (float)length)));
        encoder.encode((CharBuffer)cbuf, (ByteBuffer)nativeBuffer, (boolean)true);
        int bufferOffset = nativeBuffer.arrayOffset();
        this.value = Arrays.copyOfRange((byte[])nativeBuffer.array(), (int)bufferOffset, (int)(bufferOffset + nativeBuffer.position()));
        this.offset = 0;
        this.length = this.value.length;
    }

    public AsciiString(CharSequence value) {
        this((CharSequence)value, (int)0, (int)value.length());
    }

    public AsciiString(CharSequence value, int start, int length) {
        if (MathUtil.isOutOfBounds((int)start, (int)length, (int)value.length())) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length() + ')'));
        }
        this.value = PlatformDependent.allocateUninitializedArray((int)length);
        int i = 0;
        int j = start;
        do {
            if (i >= length) {
                this.offset = 0;
                this.length = length;
                return;
            }
            this.value[i] = AsciiString.c2b((char)value.charAt((int)j));
            ++i;
            ++j;
        } while (true);
    }

    public AsciiString(CharSequence value, Charset charset) {
        this((CharSequence)value, (Charset)charset, (int)0, (int)value.length());
    }

    public AsciiString(CharSequence value, Charset charset, int start, int length) {
        CharBuffer cbuf = CharBuffer.wrap((CharSequence)value, (int)start, (int)(start + length));
        CharsetEncoder encoder = CharsetUtil.encoder((Charset)charset);
        ByteBuffer nativeBuffer = ByteBuffer.allocate((int)((int)(encoder.maxBytesPerChar() * (float)length)));
        encoder.encode((CharBuffer)cbuf, (ByteBuffer)nativeBuffer, (boolean)true);
        int offset = nativeBuffer.arrayOffset();
        this.value = Arrays.copyOfRange((byte[])nativeBuffer.array(), (int)offset, (int)(offset + nativeBuffer.position()));
        this.offset = 0;
        this.length = this.value.length;
    }

    public int forEachByte(ByteProcessor visitor) throws Exception {
        return this.forEachByte0((int)0, (int)this.length(), (ByteProcessor)visitor);
    }

    public int forEachByte(int index, int length, ByteProcessor visitor) throws Exception {
        if (!MathUtil.isOutOfBounds((int)index, (int)length, (int)this.length())) return this.forEachByte0((int)index, (int)length, (ByteProcessor)visitor);
        throw new IndexOutOfBoundsException((String)("expected: 0 <= index(" + index + ") <= start + length(" + length + ") <= length(" + this.length() + ')'));
    }

    private int forEachByte0(int index, int length, ByteProcessor visitor) throws Exception {
        int len = this.offset + index + length;
        int i = this.offset + index;
        while (i < len) {
            if (!visitor.process((byte)this.value[i])) {
                return i - this.offset;
            }
            ++i;
        }
        return -1;
    }

    public int forEachByteDesc(ByteProcessor visitor) throws Exception {
        return this.forEachByteDesc0((int)0, (int)this.length(), (ByteProcessor)visitor);
    }

    public int forEachByteDesc(int index, int length, ByteProcessor visitor) throws Exception {
        if (!MathUtil.isOutOfBounds((int)index, (int)length, (int)this.length())) return this.forEachByteDesc0((int)index, (int)length, (ByteProcessor)visitor);
        throw new IndexOutOfBoundsException((String)("expected: 0 <= index(" + index + ") <= start + length(" + length + ") <= length(" + this.length() + ')'));
    }

    private int forEachByteDesc0(int index, int length, ByteProcessor visitor) throws Exception {
        int end = this.offset + index;
        int i = this.offset + index + length - 1;
        while (i >= end) {
            if (!visitor.process((byte)this.value[i])) {
                return i - this.offset;
            }
            --i;
        }
        return -1;
    }

    public byte byteAt(int index) {
        if (index < 0) throw new IndexOutOfBoundsException((String)("index: " + index + " must be in the range [0," + this.length + ")"));
        if (index >= this.length) {
            throw new IndexOutOfBoundsException((String)("index: " + index + " must be in the range [0," + this.length + ")"));
        }
        if (!PlatformDependent.hasUnsafe()) return this.value[index + this.offset];
        return PlatformDependent.getByte((byte[])this.value, (int)(index + this.offset));
    }

    public boolean isEmpty() {
        if (this.length != 0) return false;
        return true;
    }

    @Override
    public int length() {
        return this.length;
    }

    public void arrayChanged() {
        this.string = null;
        this.hash = 0;
    }

    public byte[] array() {
        return this.value;
    }

    public int arrayOffset() {
        return this.offset;
    }

    public boolean isEntireArrayUsed() {
        if (this.offset != 0) return false;
        if (this.length != this.value.length) return false;
        return true;
    }

    public byte[] toByteArray() {
        return this.toByteArray((int)0, (int)this.length());
    }

    public byte[] toByteArray(int start, int end) {
        return Arrays.copyOfRange((byte[])this.value, (int)(start + this.offset), (int)(end + this.offset));
    }

    public void copy(int srcIdx, byte[] dst, int dstIdx, int length) {
        if (MathUtil.isOutOfBounds((int)srcIdx, (int)length, (int)this.length())) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')'));
        }
        System.arraycopy((Object)this.value, (int)(srcIdx + this.offset), (Object)ObjectUtil.checkNotNull(dst, (String)"dst"), (int)dstIdx, (int)length);
    }

    @Override
    public char charAt(int index) {
        return AsciiString.b2c((byte)this.byteAt((int)index));
    }

    public boolean contains(CharSequence cs) {
        if (this.indexOf((CharSequence)cs) < 0) return false;
        return true;
    }

    @Override
    public int compareTo(CharSequence string) {
        if (this == string) {
            return 0;
        }
        int length1 = this.length();
        int length2 = string.length();
        int minLength = Math.min((int)length1, (int)length2);
        int i = 0;
        int j = this.arrayOffset();
        while (i < minLength) {
            int result = AsciiString.b2c((byte)this.value[j]) - string.charAt((int)i);
            if (result != 0) {
                return result;
            }
            ++i;
            ++j;
        }
        return length1 - length2;
    }

    public AsciiString concat(CharSequence string) {
        int thisLen = this.length();
        int thatLen = string.length();
        if (thatLen == 0) {
            return this;
        }
        if (string instanceof AsciiString) {
            AsciiString that = (AsciiString)string;
            if (this.isEmpty()) {
                return that;
            }
            byte[] newValue = PlatformDependent.allocateUninitializedArray((int)(thisLen + thatLen));
            System.arraycopy((Object)this.value, (int)this.arrayOffset(), (Object)newValue, (int)0, (int)thisLen);
            System.arraycopy((Object)that.value, (int)that.arrayOffset(), (Object)newValue, (int)thisLen, (int)thatLen);
            return new AsciiString((byte[])newValue, (boolean)false);
        }
        if (this.isEmpty()) {
            return new AsciiString((CharSequence)string);
        }
        byte[] newValue = PlatformDependent.allocateUninitializedArray((int)(thisLen + thatLen));
        System.arraycopy((Object)this.value, (int)this.arrayOffset(), (Object)newValue, (int)0, (int)thisLen);
        int i = thisLen;
        int j = 0;
        while (i < newValue.length) {
            newValue[i] = AsciiString.c2b((char)string.charAt((int)j));
            ++i;
            ++j;
        }
        return new AsciiString((byte[])newValue, (boolean)false);
    }

    public boolean endsWith(CharSequence suffix) {
        int suffixLen = suffix.length();
        return this.regionMatches((int)(this.length() - suffixLen), (CharSequence)suffix, (int)0, (int)suffixLen);
    }

    public boolean contentEqualsIgnoreCase(CharSequence string) {
        if (this == string) {
            return true;
        }
        if (string == null) return false;
        if (string.length() != this.length()) {
            return false;
        }
        if (string instanceof AsciiString) {
            AsciiString rhs = (AsciiString)string;
            int i = this.arrayOffset();
            int j = rhs.arrayOffset();
            int end = i + this.length();
            while (i < end) {
                if (!AsciiString.equalsIgnoreCase((byte)this.value[i], (byte)rhs.value[j])) {
                    return false;
                }
                ++i;
                ++j;
            }
            return true;
        }
        int i = this.arrayOffset();
        int j = 0;
        int end = this.length();
        while (j < end) {
            if (!AsciiString.equalsIgnoreCase((char)AsciiString.b2c((byte)this.value[i]), (char)string.charAt((int)j))) {
                return false;
            }
            ++i;
            ++j;
        }
        return true;
    }

    public char[] toCharArray() {
        return this.toCharArray((int)0, (int)this.length());
    }

    public char[] toCharArray(int start, int end) {
        int length = end - start;
        if (length == 0) {
            return EmptyArrays.EMPTY_CHARS;
        }
        if (MathUtil.isOutOfBounds((int)start, (int)length, (int)this.length())) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')'));
        }
        char[] buffer = new char[length];
        int i = 0;
        int j = start + this.arrayOffset();
        while (i < length) {
            buffer[i] = AsciiString.b2c((byte)this.value[j]);
            ++i;
            ++j;
        }
        return buffer;
    }

    public void copy(int srcIdx, char[] dst, int dstIdx, int length) {
        if (dst == null) {
            throw new NullPointerException((String)"dst");
        }
        if (MathUtil.isOutOfBounds((int)srcIdx, (int)length, (int)this.length())) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')'));
        }
        int dstEnd = dstIdx + length;
        int i = dstIdx;
        int j = srcIdx + this.arrayOffset();
        while (i < dstEnd) {
            dst[i] = AsciiString.b2c((byte)this.value[j]);
            ++i;
            ++j;
        }
    }

    public AsciiString subSequence(int start) {
        return this.subSequence((int)start, (int)this.length());
    }

    @Override
    public AsciiString subSequence(int start, int end) {
        return this.subSequence((int)start, (int)end, (boolean)true);
    }

    public AsciiString subSequence(int start, int end, boolean copy) {
        if (MathUtil.isOutOfBounds((int)start, (int)(end - start), (int)this.length())) {
            throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= end (" + end + ") <= length(" + this.length() + ')'));
        }
        if (start == 0 && end == this.length()) {
            return this;
        }
        if (end != start) return new AsciiString((byte[])this.value, (int)(start + this.offset), (int)(end - start), (boolean)copy);
        return EMPTY_STRING;
    }

    public int indexOf(CharSequence string) {
        return this.indexOf((CharSequence)string, (int)0);
    }

    public int indexOf(CharSequence subString, int start) {
        int subCount = subString.length();
        if (start < 0) {
            start = 0;
        }
        if (subCount <= 0) {
            int n;
            if (start < this.length) {
                n = start;
                return n;
            }
            n = this.length;
            return n;
        }
        if (subCount > this.length - start) {
            return -1;
        }
        char firstChar = subString.charAt((int)0);
        if (firstChar > '\u00ff') {
            return -1;
        }
        byte firstCharAsByte = AsciiString.c2b0((char)firstChar);
        int len = this.offset + this.length - subCount;
        int i = start + this.offset;
        while (i <= len) {
            if (this.value[i] == firstCharAsByte) {
                int o1 = i;
                int o2 = 0;
                while (++o2 < subCount && AsciiString.b2c((byte)this.value[++o1]) == subString.charAt((int)o2)) {
                }
                if (o2 == subCount) {
                    return i - this.offset;
                }
            }
            ++i;
        }
        return -1;
    }

    public int indexOf(char ch, int start) {
        if (ch > '\u00ff') {
            return -1;
        }
        if (start < 0) {
            start = 0;
        }
        byte chAsByte = AsciiString.c2b0((char)ch);
        int len = this.offset + this.length;
        int i = start + this.offset;
        while (i < len) {
            if (this.value[i] == chAsByte) {
                return i - this.offset;
            }
            ++i;
        }
        return -1;
    }

    public int lastIndexOf(CharSequence string) {
        return this.lastIndexOf((CharSequence)string, (int)this.length);
    }

    public int lastIndexOf(CharSequence subString, int start) {
        int subCount = subString.length();
        if ((start = Math.min((int)start, (int)(this.length - subCount))) < 0) {
            return -1;
        }
        if (subCount == 0) {
            return start;
        }
        char firstChar = subString.charAt((int)0);
        if (firstChar > '\u00ff') {
            return -1;
        }
        byte firstCharAsByte = AsciiString.c2b0((char)firstChar);
        int i = this.offset + start;
        while (i >= 0) {
            if (this.value[i] == firstCharAsByte) {
                int o1 = i;
                int o2 = 0;
                while (++o2 < subCount && AsciiString.b2c((byte)this.value[++o1]) == subString.charAt((int)o2)) {
                }
                if (o2 == subCount) {
                    return i - this.offset;
                }
            }
            --i;
        }
        return -1;
    }

    public boolean regionMatches(int thisStart, CharSequence string, int start, int length) {
        if (string == null) {
            throw new NullPointerException((String)"string");
        }
        if (start < 0) return false;
        if (string.length() - start < length) {
            return false;
        }
        int thisLen = this.length();
        if (thisStart < 0) return false;
        if (thisLen - thisStart < length) {
            return false;
        }
        if (length <= 0) {
            return true;
        }
        int thatEnd = start + length;
        int i = start;
        int j = thisStart + this.arrayOffset();
        while (i < thatEnd) {
            if (AsciiString.b2c((byte)this.value[j]) != string.charAt((int)i)) {
                return false;
            }
            ++i;
            ++j;
        }
        return true;
    }

    public boolean regionMatches(boolean ignoreCase, int thisStart, CharSequence string, int start, int length) {
        if (!ignoreCase) {
            return this.regionMatches((int)thisStart, (CharSequence)string, (int)start, (int)length);
        }
        if (string == null) {
            throw new NullPointerException((String)"string");
        }
        int thisLen = this.length();
        if (thisStart < 0) return false;
        if (length > thisLen - thisStart) {
            return false;
        }
        if (start < 0) return false;
        if (length > string.length() - start) {
            return false;
        }
        int thisEnd = (thisStart += this.arrayOffset()) + length;
        do {
            if (thisStart >= thisEnd) return true;
        } while (AsciiString.equalsIgnoreCase((char)AsciiString.b2c((byte)this.value[thisStart++]), (char)string.charAt((int)start++)));
        return false;
    }

    public AsciiString replace(char oldChar, char newChar) {
        int i;
        int len;
        byte[] buffer;
        byte newCharAsByte;
        byte oldCharAsByte;
        block4 : {
            if (oldChar > '\u00ff') {
                return this;
            }
            oldCharAsByte = AsciiString.c2b0((char)oldChar);
            newCharAsByte = AsciiString.c2b((char)newChar);
            len = this.offset + this.length;
            i = this.offset;
            while (i < len) {
                if (this.value[i] == oldCharAsByte) {
                    buffer = PlatformDependent.allocateUninitializedArray((int)this.length());
                    System.arraycopy((Object)this.value, (int)this.offset, (Object)buffer, (int)0, (int)(i - this.offset));
                    buffer[i - this.offset] = newCharAsByte;
                    ++i;
                    break block4;
                }
                ++i;
            }
            return this;
        }
        while (i < len) {
            byte oldValue = this.value[i];
            buffer[i - this.offset] = oldValue != oldCharAsByte ? oldValue : newCharAsByte;
            ++i;
        }
        return new AsciiString((byte[])buffer, (boolean)false);
    }

    public boolean startsWith(CharSequence prefix) {
        return this.startsWith((CharSequence)prefix, (int)0);
    }

    public boolean startsWith(CharSequence prefix, int start) {
        return this.regionMatches((int)start, (CharSequence)prefix, (int)0, (int)prefix.length());
    }

    public AsciiString toLowerCase() {
        int i;
        boolean lowercased = true;
        int len = this.length() + this.arrayOffset();
        for (i = this.arrayOffset(); i < len; ++i) {
            byte b = this.value[i];
            if (b < 65 || b > 90) continue;
            lowercased = false;
            break;
        }
        if (lowercased) {
            return this;
        }
        byte[] newValue = PlatformDependent.allocateUninitializedArray((int)this.length());
        i = 0;
        int j = this.arrayOffset();
        while (i < newValue.length) {
            newValue[i] = AsciiString.toLowerCase((byte)this.value[j]);
            ++i;
            ++j;
        }
        return new AsciiString((byte[])newValue, (boolean)false);
    }

    public AsciiString toUpperCase() {
        int i;
        boolean uppercased = true;
        int len = this.length() + this.arrayOffset();
        for (i = this.arrayOffset(); i < len; ++i) {
            byte b = this.value[i];
            if (b < 97 || b > 122) continue;
            uppercased = false;
            break;
        }
        if (uppercased) {
            return this;
        }
        byte[] newValue = PlatformDependent.allocateUninitializedArray((int)this.length());
        i = 0;
        int j = this.arrayOffset();
        while (i < newValue.length) {
            newValue[i] = AsciiString.toUpperCase((byte)this.value[j]);
            ++i;
            ++j;
        }
        return new AsciiString((byte[])newValue, (boolean)false);
    }

    public static CharSequence trim(CharSequence c) {
        int last;
        int start;
        if (c instanceof AsciiString) {
            return ((AsciiString)c).trim();
        }
        if (c instanceof String) {
            return ((String)c).trim();
        }
        int end = last = c.length() - 1;
        for (start = 0; start <= end && c.charAt((int)start) <= ' '; ++start) {
        }
        while (end >= start && c.charAt((int)end) <= ' ') {
            --end;
        }
        if (start != 0) return c.subSequence((int)start, (int)end);
        if (end != last) return c.subSequence((int)start, (int)end);
        return c;
    }

    public AsciiString trim() {
        int last;
        int start;
        int end = last = this.arrayOffset() + this.length() - 1;
        for (start = this.arrayOffset(); start <= end && this.value[start] <= 32; ++start) {
        }
        while (end >= start && this.value[end] <= 32) {
            --end;
        }
        if (start != 0) return new AsciiString((byte[])this.value, (int)start, (int)(end - start + 1), (boolean)false);
        if (end != last) return new AsciiString((byte[])this.value, (int)start, (int)(end - start + 1), (boolean)false);
        return this;
    }

    public boolean contentEquals(CharSequence a) {
        if (this == a) {
            return true;
        }
        if (a == null) return false;
        if (a.length() != this.length()) {
            return false;
        }
        if (a instanceof AsciiString) {
            return this.equals((Object)a);
        }
        int i = this.arrayOffset();
        int j = 0;
        while (j < a.length()) {
            if (AsciiString.b2c((byte)this.value[i]) != a.charAt((int)j)) {
                return false;
            }
            ++i;
            ++j;
        }
        return true;
    }

    public boolean matches(String expr) {
        return Pattern.matches((String)expr, (CharSequence)this);
    }

    public AsciiString[] split(String expr, int max) {
        return AsciiString.toAsciiStringArray((String[])Pattern.compile((String)expr).split((CharSequence)this, (int)max));
    }

    public AsciiString[] split(char delim) {
        int i;
        ArrayList<AsciiString> res = InternalThreadLocalMap.get().arrayList();
        int start = 0;
        int length = this.length();
        for (i = start; i < length; ++i) {
            if (this.charAt((int)i) != delim) continue;
            if (start == i) {
                res.add(EMPTY_STRING);
            } else {
                res.add(new AsciiString((byte[])this.value, (int)(start + this.arrayOffset()), (int)(i - start), (boolean)false));
            }
            start = i + 1;
        }
        if (start == 0) {
            res.add(this);
            return res.toArray(new AsciiString[0]);
        }
        if (start != length) {
            res.add(new AsciiString((byte[])this.value, (int)(start + this.arrayOffset()), (int)(length - start), (boolean)false));
            return res.toArray(new AsciiString[0]);
        }
        i = res.size() - 1;
        while (i >= 0) {
            if (!((AsciiString)res.get((int)i)).isEmpty()) return res.toArray(new AsciiString[0]);
            res.remove((int)i);
            --i;
        }
        return res.toArray(new AsciiString[0]);
    }

    public int hashCode() {
        int h = this.hash;
        if (h != 0) return h;
        this.hash = h = PlatformDependent.hashCodeAscii((byte[])this.value, (int)this.offset, (int)this.length);
        return h;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != AsciiString.class) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AsciiString other = (AsciiString)obj;
        if (this.length() != other.length()) return false;
        if (this.hashCode() != other.hashCode()) return false;
        if (!PlatformDependent.equals((byte[])this.array(), (int)this.arrayOffset(), (byte[])other.array(), (int)other.arrayOffset(), (int)this.length())) return false;
        return true;
    }

    @Override
    public String toString() {
        String cache = this.string;
        if (cache != null) return cache;
        this.string = cache = this.toString((int)0);
        return cache;
    }

    public String toString(int start) {
        return this.toString((int)start, (int)this.length());
    }

    public String toString(int start, int end) {
        int length = end - start;
        if (length == 0) {
            return "";
        }
        if (!MathUtil.isOutOfBounds((int)start, (int)length, (int)this.length())) return new String((byte[])this.value, (int)0, (int)(start + this.offset), (int)length);
        throw new IndexOutOfBoundsException((String)("expected: 0 <= start(" + start + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')'));
    }

    public boolean parseBoolean() {
        if (this.length < 1) return false;
        if (this.value[this.offset] == 0) return false;
        return true;
    }

    public char parseChar() {
        return this.parseChar((int)0);
    }

    public char parseChar(int start) {
        if (start + 1 >= this.length()) {
            throw new IndexOutOfBoundsException((String)("2 bytes required to convert to character. index " + start + " would go out of bounds."));
        }
        int startWithOffset = start + this.offset;
        return (char)(AsciiString.b2c((byte)this.value[startWithOffset]) << 8 | AsciiString.b2c((byte)this.value[startWithOffset + 1]));
    }

    public short parseShort() {
        return this.parseShort((int)0, (int)this.length(), (int)10);
    }

    public short parseShort(int radix) {
        return this.parseShort((int)0, (int)this.length(), (int)radix);
    }

    public short parseShort(int start, int end) {
        return this.parseShort((int)start, (int)end, (int)10);
    }

    public short parseShort(int start, int end, int radix) {
        int intValue = this.parseInt((int)start, (int)end, (int)radix);
        short result = (short)intValue;
        if (result == intValue) return result;
        throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
    }

    public int parseInt() {
        return this.parseInt((int)0, (int)this.length(), (int)10);
    }

    public int parseInt(int radix) {
        return this.parseInt((int)0, (int)this.length(), (int)radix);
    }

    public int parseInt(int start, int end) {
        return this.parseInt((int)start, (int)end, (int)10);
    }

    public int parseInt(int start, int end, int radix) {
        if (radix < 2) throw new NumberFormatException();
        if (radix > 36) {
            throw new NumberFormatException();
        }
        if (start == end) {
            throw new NumberFormatException();
        }
        int i = start;
        boolean negative = this.byteAt((int)i) == 45;
        if (!negative) return this.parseInt((int)i, (int)end, (int)radix, (boolean)negative);
        if (++i != end) return this.parseInt((int)i, (int)end, (int)radix, (boolean)negative);
        throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
    }

    private int parseInt(int start, int end, int radix, boolean negative) {
        int max = Integer.MIN_VALUE / radix;
        int result = 0;
        int currOffset = start;
        do {
            int digit;
            if (currOffset >= end) {
                if (negative) return result;
                if ((result = -result) >= 0) return result;
                throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
            }
            if ((digit = Character.digit((char)((char)(this.value[currOffset++ + this.offset] & 255)), (int)radix)) == -1) {
                throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
            }
            if (max > result) {
                throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
            }
            int next = result * radix - digit;
            if (next > result) {
                throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
            }
            result = next;
        } while (true);
    }

    public long parseLong() {
        return this.parseLong((int)0, (int)this.length(), (int)10);
    }

    public long parseLong(int radix) {
        return this.parseLong((int)0, (int)this.length(), (int)radix);
    }

    public long parseLong(int start, int end) {
        return this.parseLong((int)start, (int)end, (int)10);
    }

    public long parseLong(int start, int end, int radix) {
        if (radix < 2) throw new NumberFormatException();
        if (radix > 36) {
            throw new NumberFormatException();
        }
        if (start == end) {
            throw new NumberFormatException();
        }
        int i = start;
        boolean negative = this.byteAt((int)i) == 45;
        if (!negative) return this.parseLong((int)i, (int)end, (int)radix, (boolean)negative);
        if (++i != end) return this.parseLong((int)i, (int)end, (int)radix, (boolean)negative);
        throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
    }

    private long parseLong(int start, int end, int radix, boolean negative) {
        long max = Long.MIN_VALUE / (long)radix;
        long result = 0L;
        int currOffset = start;
        do {
            int digit;
            if (currOffset >= end) {
                if (negative) return result;
                if ((result = -result) >= 0L) return result;
                throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
            }
            if ((digit = Character.digit((char)((char)(this.value[currOffset++ + this.offset] & 255)), (int)radix)) == -1) {
                throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
            }
            if (max > result) {
                throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
            }
            long next = result * (long)radix - (long)digit;
            if (next > result) {
                throw new NumberFormatException((String)this.subSequence((int)start, (int)end, (boolean)false).toString());
            }
            result = next;
        } while (true);
    }

    public float parseFloat() {
        return this.parseFloat((int)0, (int)this.length());
    }

    public float parseFloat(int start, int end) {
        return Float.parseFloat((String)this.toString((int)start, (int)end));
    }

    public double parseDouble() {
        return this.parseDouble((int)0, (int)this.length());
    }

    public double parseDouble(int start, int end) {
        return Double.parseDouble((String)this.toString((int)start, (int)end));
    }

    public static AsciiString of(CharSequence string) {
        AsciiString asciiString;
        if (string instanceof AsciiString) {
            asciiString = (AsciiString)string;
            return asciiString;
        }
        asciiString = new AsciiString((CharSequence)string);
        return asciiString;
    }

    public static AsciiString cached(String string) {
        AsciiString asciiString = new AsciiString((CharSequence)string);
        asciiString.string = string;
        return asciiString;
    }

    public static int hashCode(CharSequence value) {
        if (value == null) {
            return 0;
        }
        if (!(value instanceof AsciiString)) return PlatformDependent.hashCodeAscii((CharSequence)value);
        return value.hashCode();
    }

    public static boolean contains(CharSequence a, CharSequence b) {
        return AsciiString.contains((CharSequence)a, (CharSequence)b, (CharEqualityComparator)DefaultCharEqualityComparator.INSTANCE);
    }

    public static boolean containsIgnoreCase(CharSequence a, CharSequence b) {
        return AsciiString.contains((CharSequence)a, (CharSequence)b, (CharEqualityComparator)AsciiCaseInsensitiveCharEqualityComparator.INSTANCE);
    }

    public static boolean contentEqualsIgnoreCase(CharSequence a, CharSequence b) {
        if (a == null || b == null) {
            if (a != b) return false;
            return true;
        }
        if (a instanceof AsciiString) {
            return ((AsciiString)a).contentEqualsIgnoreCase((CharSequence)b);
        }
        if (b instanceof AsciiString) {
            return ((AsciiString)b).contentEqualsIgnoreCase((CharSequence)a);
        }
        if (a.length() != b.length()) {
            return false;
        }
        int i = 0;
        while (i < a.length()) {
            if (!AsciiString.equalsIgnoreCase((char)a.charAt((int)i), (char)b.charAt((int)i))) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static boolean containsContentEqualsIgnoreCase(Collection<CharSequence> collection, CharSequence value) {
        CharSequence v;
        Iterator<CharSequence> iterator = collection.iterator();
        do {
            if (!iterator.hasNext()) return false;
        } while (!AsciiString.contentEqualsIgnoreCase((CharSequence)value, (CharSequence)(v = iterator.next())));
        return true;
    }

    public static boolean containsAllContentEqualsIgnoreCase(Collection<CharSequence> a, Collection<CharSequence> b) {
        CharSequence v;
        Iterator<CharSequence> iterator = b.iterator();
        do {
            if (!iterator.hasNext()) return true;
        } while (AsciiString.containsContentEqualsIgnoreCase(a, (CharSequence)(v = iterator.next())));
        return false;
    }

    public static boolean contentEquals(CharSequence a, CharSequence b) {
        if (a == null || b == null) {
            if (a != b) return false;
            return true;
        }
        if (a instanceof AsciiString) {
            return ((AsciiString)a).contentEquals((CharSequence)b);
        }
        if (b instanceof AsciiString) {
            return ((AsciiString)b).contentEquals((CharSequence)a);
        }
        if (a.length() != b.length()) {
            return false;
        }
        int i = 0;
        while (i < a.length()) {
            if (a.charAt((int)i) != b.charAt((int)i)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private static AsciiString[] toAsciiStringArray(String[] jdkResult) {
        AsciiString[] res = new AsciiString[jdkResult.length];
        int i = 0;
        while (i < jdkResult.length) {
            res[i] = new AsciiString((CharSequence)jdkResult[i]);
            ++i;
        }
        return res;
    }

    private static boolean contains(CharSequence a, CharSequence b, CharEqualityComparator cmp) {
        if (a == null) return false;
        if (b == null) return false;
        if (a.length() < b.length()) {
            return false;
        }
        if (b.length() == 0) {
            return true;
        }
        int bStart = 0;
        int i = 0;
        while (i < a.length()) {
            if (cmp.equals((char)b.charAt((int)bStart), (char)a.charAt((int)i))) {
                if (++bStart == b.length()) {
                    return true;
                }
            } else {
                if (a.length() - i < b.length()) {
                    return false;
                }
                bStart = 0;
            }
            ++i;
        }
        return false;
    }

    private static boolean regionMatchesCharSequences(CharSequence cs, int csStart, CharSequence string, int start, int length, CharEqualityComparator charEqualityComparator) {
        char c2;
        char c1;
        if (csStart < 0) return false;
        if (length > cs.length() - csStart) {
            return false;
        }
        if (start < 0) return false;
        if (length > string.length() - start) {
            return false;
        }
        int csIndex = csStart;
        int csEnd = csIndex + length;
        int stringIndex = start;
        do {
            if (csIndex >= csEnd) return true;
        } while (charEqualityComparator.equals((char)(c1 = cs.charAt((int)csIndex++)), (char)(c2 = string.charAt((int)stringIndex++))));
        return false;
    }

    public static boolean regionMatches(CharSequence cs, boolean ignoreCase, int csStart, CharSequence string, int start, int length) {
        CharEqualityComparator charEqualityComparator;
        if (cs == null) return false;
        if (string == null) {
            return false;
        }
        if (cs instanceof String && string instanceof String) {
            return ((String)cs).regionMatches((boolean)ignoreCase, (int)csStart, (String)((String)string), (int)start, (int)length);
        }
        if (cs instanceof AsciiString) {
            return ((AsciiString)cs).regionMatches((boolean)ignoreCase, (int)csStart, (CharSequence)string, (int)start, (int)length);
        }
        if (ignoreCase) {
            charEqualityComparator = GeneralCaseInsensitiveCharEqualityComparator.INSTANCE;
            return AsciiString.regionMatchesCharSequences((CharSequence)cs, (int)csStart, (CharSequence)string, (int)start, (int)length, (CharEqualityComparator)charEqualityComparator);
        }
        charEqualityComparator = DefaultCharEqualityComparator.INSTANCE;
        return AsciiString.regionMatchesCharSequences((CharSequence)cs, (int)csStart, (CharSequence)string, (int)start, (int)length, (CharEqualityComparator)charEqualityComparator);
    }

    public static boolean regionMatchesAscii(CharSequence cs, boolean ignoreCase, int csStart, CharSequence string, int start, int length) {
        CharEqualityComparator charEqualityComparator;
        if (cs == null) return false;
        if (string == null) {
            return false;
        }
        if (!ignoreCase && cs instanceof String && string instanceof String) {
            return ((String)cs).regionMatches((boolean)false, (int)csStart, (String)((String)string), (int)start, (int)length);
        }
        if (cs instanceof AsciiString) {
            return ((AsciiString)cs).regionMatches((boolean)ignoreCase, (int)csStart, (CharSequence)string, (int)start, (int)length);
        }
        if (ignoreCase) {
            charEqualityComparator = AsciiCaseInsensitiveCharEqualityComparator.INSTANCE;
            return AsciiString.regionMatchesCharSequences((CharSequence)cs, (int)csStart, (CharSequence)string, (int)start, (int)length, (CharEqualityComparator)charEqualityComparator);
        }
        charEqualityComparator = DefaultCharEqualityComparator.INSTANCE;
        return AsciiString.regionMatchesCharSequences((CharSequence)cs, (int)csStart, (CharSequence)string, (int)start, (int)length, (CharEqualityComparator)charEqualityComparator);
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str == null) return -1;
        if (searchStr == null) {
            return -1;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        int searchStrLen = searchStr.length();
        int endLimit = str.length() - searchStrLen + 1;
        if (startPos > endLimit) {
            return -1;
        }
        if (searchStrLen == 0) {
            return startPos;
        }
        int i = startPos;
        while (i < endLimit) {
            if (AsciiString.regionMatches((CharSequence)str, (boolean)true, (int)i, (CharSequence)searchStr, (int)0, (int)searchStrLen)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOfIgnoreCaseAscii(CharSequence str, CharSequence searchStr, int startPos) {
        if (str == null) return -1;
        if (searchStr == null) {
            return -1;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        int searchStrLen = searchStr.length();
        int endLimit = str.length() - searchStrLen + 1;
        if (startPos > endLimit) {
            return -1;
        }
        if (searchStrLen == 0) {
            return startPos;
        }
        int i = startPos;
        while (i < endLimit) {
            if (AsciiString.regionMatchesAscii((CharSequence)str, (boolean)true, (int)i, (CharSequence)searchStr, (int)0, (int)searchStrLen)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(CharSequence cs, char searchChar, int start) {
        if (cs instanceof String) {
            return ((String)cs).indexOf((int)searchChar, (int)start);
        }
        if (cs instanceof AsciiString) {
            return ((AsciiString)cs).indexOf((char)searchChar, (int)start);
        }
        if (cs == null) {
            return -1;
        }
        int sz = cs.length();
        int i = start < 0 ? 0 : start;
        while (i < sz) {
            if (cs.charAt((int)i) == searchChar) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    private static boolean equalsIgnoreCase(byte a, byte b) {
        if (a == b) return true;
        if (AsciiString.toLowerCase((byte)a) == AsciiString.toLowerCase((byte)b)) return true;
        return false;
    }

    private static boolean equalsIgnoreCase(char a, char b) {
        if (a == b) return true;
        if (AsciiString.toLowerCase((char)a) == AsciiString.toLowerCase((char)b)) return true;
        return false;
    }

    private static byte toLowerCase(byte b) {
        byte by;
        if (AsciiString.isUpperCase((byte)b)) {
            by = (byte)(b + 32);
            return by;
        }
        by = b;
        return by;
    }

    public static char toLowerCase(char c) {
        char c2;
        if (AsciiString.isUpperCase((char)c)) {
            c2 = (char)(c + 32);
            return c2;
        }
        c2 = c;
        return c2;
    }

    private static byte toUpperCase(byte b) {
        byte by;
        if (AsciiString.isLowerCase((byte)b)) {
            by = (byte)(b - 32);
            return by;
        }
        by = b;
        return by;
    }

    private static boolean isLowerCase(byte value) {
        if (value < 97) return false;
        if (value > 122) return false;
        return true;
    }

    public static boolean isUpperCase(byte value) {
        if (value < 65) return false;
        if (value > 90) return false;
        return true;
    }

    public static boolean isUpperCase(char value) {
        if (value < 'A') return false;
        if (value > 'Z') return false;
        return true;
    }

    public static byte c2b(char c) {
        int n;
        if (c > '\u00ff') {
            n = 63;
            return (byte)n;
        }
        n = (int)c;
        return (byte)n;
    }

    private static byte c2b0(char c) {
        return (byte)c;
    }

    public static char b2c(byte b) {
        return (char)(b & 255);
    }

    static /* synthetic */ boolean access$000(char x0, char x1) {
        return AsciiString.equalsIgnoreCase((char)x0, (char)x1);
    }
}

