/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Constants;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.StringUtils;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.SQLException;

public class Buffer {
    static final int MAX_BYTES_TO_DUMP = 512;
    static final int NO_LENGTH_LIMIT = -1;
    static final long NULL_LENGTH = -1L;
    private int bufLength = 0;
    private byte[] byteBuffer;
    private int position = 0;
    protected boolean wasMultiPacket = false;
    public static final short TYPE_ID_ERROR = 255;
    public static final short TYPE_ID_EOF = 254;
    public static final short TYPE_ID_AUTH_SWITCH = 254;
    public static final short TYPE_ID_LOCAL_INFILE = 251;
    public static final short TYPE_ID_OK = 0;

    public Buffer(byte[] buf) {
        this.byteBuffer = buf;
        this.setBufLength((int)buf.length);
    }

    Buffer(int size) {
        this.byteBuffer = new byte[size];
        this.setBufLength((int)this.byteBuffer.length);
        this.position = 4;
    }

    final void clear() {
        this.position = 4;
    }

    final void dump() {
        this.dump((int)this.getBufLength());
    }

    final String dump(int numBytes) {
        int n;
        int n2 = numBytes > this.getBufLength() ? this.getBufLength() : numBytes;
        if (numBytes > this.getBufLength()) {
            n = this.getBufLength();
            return StringUtils.dumpAsHex((byte[])this.getBytes((int)0, (int)n2), (int)n);
        }
        n = numBytes;
        return StringUtils.dumpAsHex((byte[])this.getBytes((int)0, (int)n2), (int)n);
    }

    final String dumpClampedBytes(int numBytes) {
        int numBytesToDump = numBytes < 512 ? numBytes : 512;
        String dumped = StringUtils.dumpAsHex((byte[])this.getBytes((int)0, (int)(numBytesToDump > this.getBufLength() ? this.getBufLength() : numBytesToDump)), (int)(numBytesToDump > this.getBufLength() ? this.getBufLength() : numBytesToDump));
        if (numBytesToDump >= numBytes) return dumped;
        return dumped + " ....(packet exceeds max. dump length)";
    }

    final void dumpHeader() {
        int i = 0;
        while (i < 4) {
            String hexVal = Integer.toHexString((int)(this.readByte((int)i) & 255));
            if (hexVal.length() == 1) {
                hexVal = "0" + hexVal;
            }
            System.out.print((String)(hexVal + " "));
            ++i;
        }
    }

    final void dumpNBytes(int start, int nBytes) {
        StringBuilder asciiBuf = new StringBuilder();
        for (int i = start; i < start + nBytes && i < this.getBufLength(); ++i) {
            String hexVal = Integer.toHexString((int)(this.readByte((int)i) & 255));
            if (hexVal.length() == 1) {
                hexVal = "0" + hexVal;
            }
            System.out.print((String)(hexVal + " "));
            if (this.readByte((int)i) > 32 && this.readByte((int)i) < 127) {
                asciiBuf.append((char)((char)this.readByte((int)i)));
            } else {
                asciiBuf.append((String)".");
            }
            asciiBuf.append((String)" ");
        }
        System.out.println((String)("    " + asciiBuf.toString()));
    }

    final void ensureCapacity(int additionalData) throws SQLException {
        if (this.position + additionalData <= this.getBufLength()) return;
        if (this.position + additionalData < this.byteBuffer.length) {
            this.setBufLength((int)this.byteBuffer.length);
            return;
        }
        int newLength = (int)((double)this.byteBuffer.length * 1.25);
        if (newLength < this.byteBuffer.length + additionalData) {
            newLength = this.byteBuffer.length + (int)((double)additionalData * 1.25);
        }
        if (newLength < this.byteBuffer.length) {
            newLength = this.byteBuffer.length + additionalData;
        }
        byte[] newBytes = new byte[newLength];
        System.arraycopy((Object)this.byteBuffer, (int)0, (Object)newBytes, (int)0, (int)this.byteBuffer.length);
        this.byteBuffer = newBytes;
        this.setBufLength((int)this.byteBuffer.length);
    }

    public int fastSkipLenString() {
        long len = this.readFieldLength();
        this.position = (int)((long)this.position + len);
        return (int)len;
    }

    public void fastSkipLenByteArray() {
        long len = this.readFieldLength();
        if (len == -1L) return;
        if (len == 0L) {
            return;
        }
        this.position = (int)((long)this.position + len);
    }

    protected final byte[] getBufferSource() {
        return this.byteBuffer;
    }

    public int getBufLength() {
        return this.bufLength;
    }

    public byte[] getByteBuffer() {
        return this.byteBuffer;
    }

    final byte[] getBytes(int len) {
        byte[] b = new byte[len];
        System.arraycopy((Object)this.byteBuffer, (int)this.position, (Object)b, (int)0, (int)len);
        this.position += len;
        return b;
    }

    byte[] getBytes(int offset, int len) {
        byte[] dest = new byte[len];
        System.arraycopy((Object)this.byteBuffer, (int)offset, (Object)dest, (int)0, (int)len);
        return dest;
    }

    int getCapacity() {
        return this.byteBuffer.length;
    }

    public ByteBuffer getNioBuffer() {
        throw new IllegalArgumentException((String)Messages.getString((String)"ByteArrayBuffer.0"));
    }

    public int getPosition() {
        return this.position;
    }

    final boolean isEOFPacket() {
        if ((this.byteBuffer[0] & 255) != 254) return false;
        if (this.getBufLength() > 5) return false;
        return true;
    }

    final boolean isAuthMethodSwitchRequestPacket() {
        if ((this.byteBuffer[0] & 255) != 254) return false;
        return true;
    }

    final boolean isOKPacket() {
        if ((this.byteBuffer[0] & 255) != 0) return false;
        return true;
    }

    final boolean isResultSetOKPacket() {
        if ((this.byteBuffer[0] & 255) != 254) return false;
        if (this.getBufLength() >= 16777215) return false;
        return true;
    }

    final boolean isRawPacket() {
        if ((this.byteBuffer[0] & 255) != 1) return false;
        return true;
    }

    final long newReadLength() {
        int sw = this.byteBuffer[this.position++] & 255;
        switch (sw) {
            case 251: {
                return 0L;
            }
            case 252: {
                return (long)this.readInt();
            }
            case 253: {
                return (long)this.readLongInt();
            }
            case 254: {
                return this.readLongLong();
            }
        }
        return (long)sw;
    }

    final byte readByte() {
        return this.byteBuffer[this.position++];
    }

    final byte readByte(int readAt) {
        return this.byteBuffer[readAt];
    }

    final long readFieldLength() {
        int sw = this.byteBuffer[this.position++] & 255;
        switch (sw) {
            case 251: {
                return -1L;
            }
            case 252: {
                return (long)this.readInt();
            }
            case 253: {
                return (long)this.readLongInt();
            }
            case 254: {
                return this.readLongLong();
            }
        }
        return (long)sw;
    }

    final int readInt() {
        byte[] b = this.byteBuffer;
        return b[this.position++] & 255 | (b[this.position++] & 255) << 8;
    }

    final int readIntAsLong() {
        byte[] b = this.byteBuffer;
        return b[this.position++] & 255 | (b[this.position++] & 255) << 8 | (b[this.position++] & 255) << 16 | (b[this.position++] & 255) << 24;
    }

    final byte[] readLenByteArray(int offset) {
        long len = this.readFieldLength();
        if (len == -1L) {
            return null;
        }
        if (len == 0L) {
            return Constants.EMPTY_BYTE_ARRAY;
        }
        this.position += offset;
        return this.getBytes((int)((int)len));
    }

    final long readLength() {
        int sw = this.byteBuffer[this.position++] & 255;
        switch (sw) {
            case 251: {
                return 0L;
            }
            case 252: {
                return (long)this.readInt();
            }
            case 253: {
                return (long)this.readLongInt();
            }
            case 254: {
                return this.readLong();
            }
        }
        return (long)sw;
    }

    final long readLong() {
        byte[] b = this.byteBuffer;
        return (long)b[this.position++] & 255L | ((long)b[this.position++] & 255L) << 8 | (long)(b[this.position++] & 255) << 16 | (long)(b[this.position++] & 255) << 24;
    }

    final int readLongInt() {
        byte[] b = this.byteBuffer;
        return b[this.position++] & 255 | (b[this.position++] & 255) << 8 | (b[this.position++] & 255) << 16;
    }

    final long readLongLong() {
        byte[] b = this.byteBuffer;
        return (long)(b[this.position++] & 255) | (long)(b[this.position++] & 255) << 8 | (long)(b[this.position++] & 255) << 16 | (long)(b[this.position++] & 255) << 24 | (long)(b[this.position++] & 255) << 32 | (long)(b[this.position++] & 255) << 40 | (long)(b[this.position++] & 255) << 48 | (long)(b[this.position++] & 255) << 56;
    }

    final int readnBytes() {
        int sw = this.byteBuffer[this.position++] & 255;
        switch (sw) {
            case 1: {
                return this.byteBuffer[this.position++] & 255;
            }
            case 2: {
                return this.readInt();
            }
            case 3: {
                return this.readLongInt();
            }
            case 4: {
                return (int)this.readLong();
            }
        }
        return 255;
    }

    public final String readString() {
        int len = 0;
        int maxLen = this.getBufLength();
        for (int i = this.position; i < maxLen && this.byteBuffer[i] != 0; ++len, ++i) {
        }
        String s = StringUtils.toString((byte[])this.byteBuffer, (int)this.position, (int)len);
        this.position += len + 1;
        return s;
    }

    final String readString(String encoding, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        int len = 0;
        int maxLen = this.getBufLength();
        for (int i = this.position; i < maxLen && this.byteBuffer[i] != 0; ++len, ++i) {
        }
        try {
            String string = StringUtils.toString((byte[])this.byteBuffer, (int)this.position, (int)len, (String)encoding);
            Object var8_8 = null;
            this.position += len + 1;
            return string;
        }
        catch (UnsupportedEncodingException uEE) {
            try {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ByteArrayBuffer.1") + encoding + "'"), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                this.position += len + 1;
                throw throwable;
            }
        }
    }

    final String readString(String encoding, ExceptionInterceptor exceptionInterceptor, int expectedLength) throws SQLException {
        if (this.position + expectedLength > this.getBufLength()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ByteArrayBuffer.2"), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        try {
            String string = StringUtils.toString((byte[])this.byteBuffer, (int)this.position, (int)expectedLength, (String)encoding);
            Object var6_6 = null;
            this.position += expectedLength;
            return string;
        }
        catch (UnsupportedEncodingException uEE) {
            try {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ByteArrayBuffer.1") + encoding + "'"), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
            }
            catch (Throwable throwable) {
                Object var6_7 = null;
                this.position += expectedLength;
                throw throwable;
            }
        }
    }

    public void setBufLength(int bufLengthToSet) {
        this.bufLength = bufLengthToSet;
    }

    public void setByteBuffer(byte[] byteBufferToSet) {
        this.byteBuffer = byteBufferToSet;
    }

    public void setPosition(int positionToSet) {
        this.position = positionToSet;
    }

    public void setWasMultiPacket(boolean flag) {
        this.wasMultiPacket = flag;
    }

    public String toString() {
        return this.dumpClampedBytes((int)this.getPosition());
    }

    public String toSuperString() {
        return super.toString();
    }

    public boolean wasMultiPacket() {
        return this.wasMultiPacket;
    }

    public final void writeByte(byte b) throws SQLException {
        this.ensureCapacity((int)1);
        this.byteBuffer[this.position++] = b;
    }

    public final void writeBytesNoNull(byte[] bytes) throws SQLException {
        int len = bytes.length;
        this.ensureCapacity((int)len);
        System.arraycopy((Object)bytes, (int)0, (Object)this.byteBuffer, (int)this.position, (int)len);
        this.position += len;
    }

    final void writeBytesNoNull(byte[] bytes, int offset, int length) throws SQLException {
        this.ensureCapacity((int)length);
        System.arraycopy((Object)bytes, (int)offset, (Object)this.byteBuffer, (int)this.position, (int)length);
        this.position += length;
    }

    final void writeDouble(double d) throws SQLException {
        long l = Double.doubleToLongBits((double)d);
        this.writeLongLong((long)l);
    }

    final void writeFieldLength(long length) throws SQLException {
        if (length < 251L) {
            this.writeByte((byte)((byte)((int)length)));
            return;
        }
        if (length < 65536L) {
            this.ensureCapacity((int)3);
            this.writeByte((byte)-4);
            this.writeInt((int)((int)length));
            return;
        }
        if (length < 0x1000000L) {
            this.ensureCapacity((int)4);
            this.writeByte((byte)-3);
            this.writeLongInt((int)((int)length));
            return;
        }
        this.ensureCapacity((int)9);
        this.writeByte((byte)-2);
        this.writeLongLong((long)length);
    }

    final void writeFloat(float f) throws SQLException {
        this.ensureCapacity((int)4);
        int i = Float.floatToIntBits((float)f);
        byte[] b = this.byteBuffer;
        b[this.position++] = (byte)(i & 255);
        b[this.position++] = (byte)(i >>> 8);
        b[this.position++] = (byte)(i >>> 16);
        b[this.position++] = (byte)(i >>> 24);
    }

    final void writeInt(int i) throws SQLException {
        this.ensureCapacity((int)2);
        byte[] b = this.byteBuffer;
        b[this.position++] = (byte)(i & 255);
        b[this.position++] = (byte)(i >>> 8);
    }

    final void writeLenBytes(byte[] b) throws SQLException {
        int len = b.length;
        this.ensureCapacity((int)(len + 9));
        this.writeFieldLength((long)((long)len));
        System.arraycopy((Object)b, (int)0, (Object)this.byteBuffer, (int)this.position, (int)len);
        this.position += len;
    }

    final void writeLenString(String s, String encoding, String serverEncoding, SingleByteCharsetConverter converter, boolean parserKnowsUnicode, MySQLConnection conn) throws UnsupportedEncodingException, SQLException {
        byte[] b = null;
        b = converter != null ? converter.toBytes((String)s) : StringUtils.getBytes((String)s, (String)encoding, (String)serverEncoding, (boolean)parserKnowsUnicode, (MySQLConnection)conn, (ExceptionInterceptor)conn.getExceptionInterceptor());
        int len = b.length;
        this.ensureCapacity((int)(len + 9));
        this.writeFieldLength((long)((long)len));
        System.arraycopy((Object)b, (int)0, (Object)this.byteBuffer, (int)this.position, (int)len);
        this.position += len;
    }

    final void writeLong(long i) throws SQLException {
        this.ensureCapacity((int)4);
        byte[] b = this.byteBuffer;
        b[this.position++] = (byte)((int)(i & 255L));
        b[this.position++] = (byte)((int)(i >>> 8));
        b[this.position++] = (byte)((int)(i >>> 16));
        b[this.position++] = (byte)((int)(i >>> 24));
    }

    final void writeLongInt(int i) throws SQLException {
        this.ensureCapacity((int)3);
        byte[] b = this.byteBuffer;
        b[this.position++] = (byte)(i & 255);
        b[this.position++] = (byte)(i >>> 8);
        b[this.position++] = (byte)(i >>> 16);
    }

    final void writeLongLong(long i) throws SQLException {
        this.ensureCapacity((int)8);
        byte[] b = this.byteBuffer;
        b[this.position++] = (byte)((int)(i & 255L));
        b[this.position++] = (byte)((int)(i >>> 8));
        b[this.position++] = (byte)((int)(i >>> 16));
        b[this.position++] = (byte)((int)(i >>> 24));
        b[this.position++] = (byte)((int)(i >>> 32));
        b[this.position++] = (byte)((int)(i >>> 40));
        b[this.position++] = (byte)((int)(i >>> 48));
        b[this.position++] = (byte)((int)(i >>> 56));
    }

    final void writeString(String s) throws SQLException {
        this.ensureCapacity((int)(s.length() * 3 + 1));
        this.writeStringNoNull((String)s);
        this.byteBuffer[this.position++] = 0;
    }

    final void writeString(String s, String encoding, MySQLConnection conn) throws SQLException {
        this.ensureCapacity((int)(s.length() * 3 + 1));
        try {
            this.writeStringNoNull((String)s, (String)encoding, (String)encoding, (boolean)false, (MySQLConnection)conn);
        }
        catch (UnsupportedEncodingException ue) {
            throw new SQLException((String)ue.toString(), (String)"S1000");
        }
        this.byteBuffer[this.position++] = 0;
    }

    final void writeStringNoNull(String s) throws SQLException {
        int len = s.length();
        this.ensureCapacity((int)(len * 3));
        System.arraycopy((Object)StringUtils.getBytes((String)s), (int)0, (Object)this.byteBuffer, (int)this.position, (int)len);
        this.position += len;
    }

    final void writeStringNoNull(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn) throws UnsupportedEncodingException, SQLException {
        byte[] b = StringUtils.getBytes((String)s, (String)encoding, (String)serverEncoding, (boolean)parserKnowsUnicode, (MySQLConnection)conn, (ExceptionInterceptor)conn.getExceptionInterceptor());
        int len = b.length;
        this.ensureCapacity((int)len);
        System.arraycopy((Object)b, (int)0, (Object)this.byteBuffer, (int)this.position, (int)len);
        this.position += len;
    }
}

