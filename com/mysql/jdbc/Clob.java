/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.OutputStreamWatcher;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.WatchableOutputStream;
import com.mysql.jdbc.WatchableWriter;
import com.mysql.jdbc.WriterWatcher;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.SQLException;

public class Clob
implements java.sql.Clob,
OutputStreamWatcher,
WriterWatcher {
    private String charData;
    private ExceptionInterceptor exceptionInterceptor;

    Clob(ExceptionInterceptor exceptionInterceptor) {
        this.charData = "";
        this.exceptionInterceptor = exceptionInterceptor;
    }

    Clob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
        this.charData = charDataInit;
        this.exceptionInterceptor = exceptionInterceptor;
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        if (this.charData == null) return null;
        return new ByteArrayInputStream((byte[])StringUtils.getBytes((String)this.charData));
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        if (this.charData == null) return null;
        return new StringReader((String)this.charData);
    }

    @Override
    public String getSubString(long startPos, int length) throws SQLException {
        if (startPos < 1L) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Clob.6"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        int adjustedStartPos = (int)startPos - 1;
        int adjustedEndIndex = adjustedStartPos + length;
        if (this.charData == null) return null;
        if (adjustedEndIndex <= this.charData.length()) return this.charData.substring((int)adjustedStartPos, (int)adjustedEndIndex);
        throw SQLError.createSQLException((String)Messages.getString((String)"Clob.7"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public long length() throws SQLException {
        if (this.charData == null) return 0L;
        return (long)this.charData.length();
    }

    @Override
    public long position(java.sql.Clob arg0, long arg1) throws SQLException {
        return this.position((String)arg0.getSubString((long)1L, (int)((int)arg0.length())), (long)arg1);
    }

    @Override
    public long position(String stringToFind, long startPos) throws SQLException {
        if (startPos < 1L) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"Clob.8") + startPos + Messages.getString((String)"Clob.9")), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (this.charData == null) return -1L;
        if (startPos - 1L > (long)this.charData.length()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Clob.10"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        int pos = this.charData.indexOf((String)stringToFind, (int)((int)(startPos - 1L)));
        if (pos == -1) {
            return -1L;
        }
        long l = (long)(pos + 1);
        return l;
    }

    @Override
    public OutputStream setAsciiStream(long indexToWriteAt) throws SQLException {
        if (indexToWriteAt < 1L) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Clob.0"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        WatchableOutputStream bytesOut = new WatchableOutputStream();
        bytesOut.setWatcher((OutputStreamWatcher)this);
        if (indexToWriteAt <= 0L) return bytesOut;
        bytesOut.write((byte[])StringUtils.getBytes((String)this.charData), (int)0, (int)((int)(indexToWriteAt - 1L)));
        return bytesOut;
    }

    @Override
    public Writer setCharacterStream(long indexToWriteAt) throws SQLException {
        if (indexToWriteAt < 1L) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Clob.1"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        WatchableWriter writer = new WatchableWriter();
        writer.setWatcher((WriterWatcher)this);
        if (indexToWriteAt <= 1L) return writer;
        writer.write((String)this.charData, (int)0, (int)((int)(indexToWriteAt - 1L)));
        return writer;
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        if (pos < 1L) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Clob.2"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (str == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Clob.3"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        StringBuilder charBuf = new StringBuilder((String)this.charData);
        int strLength = str.length();
        charBuf.replace((int)((int)(--pos)), (int)((int)(pos + (long)strLength)), (String)str);
        this.charData = charBuf.toString();
        return strLength;
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        if (pos < 1L) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Clob.4"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (str == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Clob.5"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        StringBuilder charBuf = new StringBuilder((String)this.charData);
        --pos;
        try {
            String replaceString = str.substring((int)offset, (int)(offset + len));
            charBuf.replace((int)((int)pos), (int)((int)(pos + (long)replaceString.length())), (String)replaceString);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw SQLError.createSQLException((String)e.getMessage(), (String)"S1009", (Throwable)e, (ExceptionInterceptor)this.exceptionInterceptor);
        }
        this.charData = charBuf.toString();
        return len;
    }

    @Override
    public void streamClosed(WatchableOutputStream out) {
        int streamSize = out.size();
        if (streamSize < this.charData.length()) {
            try {
                out.write((byte[])StringUtils.getBytes((String)this.charData, null, null, (boolean)false, null, (ExceptionInterceptor)this.exceptionInterceptor), (int)streamSize, (int)(this.charData.length() - streamSize));
            }
            catch (SQLException ex) {
                // empty catch block
            }
        }
        this.charData = StringUtils.toAsciiString((byte[])out.toByteArray());
    }

    @Override
    public void truncate(long length) throws SQLException {
        if (length > (long)this.charData.length()) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"Clob.11") + this.charData.length() + Messages.getString((String)"Clob.12") + length + Messages.getString((String)"Clob.13")), (ExceptionInterceptor)this.exceptionInterceptor);
        }
        this.charData = this.charData.substring((int)0, (int)((int)length));
    }

    public void writerClosed(char[] charDataBeingWritten) {
        this.charData = new String((char[])charDataBeingWritten);
    }

    @Override
    public void writerClosed(WatchableWriter out) {
        int dataLength = out.size();
        if (dataLength < this.charData.length()) {
            out.write((String)this.charData, (int)dataLength, (int)(this.charData.length() - dataLength));
        }
        this.charData = out.toString();
    }

    @Override
    public void free() throws SQLException {
        this.charData = null;
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        return new StringReader((String)this.getSubString((long)pos, (int)((int)length)));
    }
}

