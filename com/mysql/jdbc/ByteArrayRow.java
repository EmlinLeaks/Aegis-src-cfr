/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class ByteArrayRow
extends ResultSetRow {
    byte[][] internalRowData;

    public ByteArrayRow(byte[][] internalRowData, ExceptionInterceptor exceptionInterceptor) {
        super((ExceptionInterceptor)exceptionInterceptor);
        this.internalRowData = internalRowData;
    }

    @Override
    public byte[] getColumnValue(int index) throws SQLException {
        return this.internalRowData[index];
    }

    @Override
    public void setColumnValue(int index, byte[] value) throws SQLException {
        this.internalRowData[index] = value;
    }

    @Override
    public String getString(int index, String encoding, MySQLConnection conn) throws SQLException {
        byte[] columnData = this.internalRowData[index];
        if (columnData != null) return this.getString((String)encoding, (MySQLConnection)conn, (byte[])columnData, (int)0, (int)columnData.length);
        return null;
    }

    @Override
    public boolean isNull(int index) throws SQLException {
        if (this.internalRowData[index] != null) return false;
        return true;
    }

    @Override
    public boolean isFloatingPointNumber(int index) throws SQLException {
        byte[] numAsBytes = this.internalRowData[index];
        if (this.internalRowData[index] == null) return false;
        if (this.internalRowData[index].length == 0) {
            return false;
        }
        int i = 0;
        while (i < numAsBytes.length) {
            if ((char)numAsBytes[i] == 'e') return true;
            if ((char)numAsBytes[i] == 'E') {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public long length(int index) throws SQLException {
        if (this.internalRowData[index] != null) return (long)this.internalRowData[index].length;
        return 0L;
    }

    @Override
    public int getInt(int columnIndex) {
        if (this.internalRowData[columnIndex] != null) return StringUtils.getInt((byte[])this.internalRowData[columnIndex]);
        return 0;
    }

    @Override
    public long getLong(int columnIndex) {
        if (this.internalRowData[columnIndex] != null) return StringUtils.getLong((byte[])this.internalRowData[columnIndex]);
        return 0L;
    }

    @Override
    public Timestamp getTimestampFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs, boolean useGmtMillis, boolean useJDBCCompliantTimezoneShift) throws SQLException {
        byte[] columnValue = this.internalRowData[columnIndex];
        if (columnValue != null) return this.getTimestampFast((int)columnIndex, (byte[])this.internalRowData[columnIndex], (int)0, (int)columnValue.length, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs, (boolean)useGmtMillis, (boolean)useJDBCCompliantTimezoneShift);
        return null;
    }

    @Override
    public double getNativeDouble(int columnIndex) throws SQLException {
        if (this.internalRowData[columnIndex] != null) return this.getNativeDouble((byte[])this.internalRowData[columnIndex], (int)0);
        return 0.0;
    }

    @Override
    public float getNativeFloat(int columnIndex) throws SQLException {
        if (this.internalRowData[columnIndex] != null) return this.getNativeFloat((byte[])this.internalRowData[columnIndex], (int)0);
        return 0.0f;
    }

    @Override
    public int getNativeInt(int columnIndex) throws SQLException {
        if (this.internalRowData[columnIndex] != null) return this.getNativeInt((byte[])this.internalRowData[columnIndex], (int)0);
        return 0;
    }

    @Override
    public long getNativeLong(int columnIndex) throws SQLException {
        if (this.internalRowData[columnIndex] != null) return this.getNativeLong((byte[])this.internalRowData[columnIndex], (int)0);
        return 0L;
    }

    @Override
    public short getNativeShort(int columnIndex) throws SQLException {
        if (this.internalRowData[columnIndex] != null) return this.getNativeShort((byte[])this.internalRowData[columnIndex], (int)0);
        return 0;
    }

    @Override
    public Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        byte[] bits = this.internalRowData[columnIndex];
        if (bits != null) return this.getNativeTimestamp((byte[])bits, (int)0, (int)bits.length, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs);
        return null;
    }

    @Override
    public void closeOpenStreams() {
    }

    @Override
    public InputStream getBinaryInputStream(int columnIndex) throws SQLException {
        if (this.internalRowData[columnIndex] != null) return new ByteArrayInputStream((byte[])this.internalRowData[columnIndex]);
        return null;
    }

    @Override
    public Reader getReader(int columnIndex) throws SQLException {
        InputStream stream = this.getBinaryInputStream((int)columnIndex);
        if (stream == null) {
            return null;
        }
        try {
            return new InputStreamReader((InputStream)stream, (String)this.metadata[columnIndex].getEncoding());
        }
        catch (UnsupportedEncodingException e) {
            SQLException sqlEx = SQLError.createSQLException((String)"", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
    }

    @Override
    public Time getTimeFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        byte[] columnValue = this.internalRowData[columnIndex];
        if (columnValue != null) return this.getTimeFast((int)columnIndex, (byte[])this.internalRowData[columnIndex], (int)0, (int)columnValue.length, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs);
        return null;
    }

    @Override
    public Date getDateFast(int columnIndex, MySQLConnection conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
        byte[] columnValue = this.internalRowData[columnIndex];
        if (columnValue != null) return this.getDateFast((int)columnIndex, (byte[])this.internalRowData[columnIndex], (int)0, (int)columnValue.length, (MySQLConnection)conn, (ResultSetImpl)rs, (Calendar)targetCalendar);
        return null;
    }

    @Override
    public Object getNativeDateTimeValue(int columnIndex, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        byte[] columnValue = this.internalRowData[columnIndex];
        if (columnValue != null) return this.getNativeDateTimeValue((int)columnIndex, (byte[])columnValue, (int)0, (int)columnValue.length, (Calendar)targetCalendar, (int)jdbcType, (int)mysqlType, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs);
        return null;
    }

    @Override
    public Date getNativeDate(int columnIndex, MySQLConnection conn, ResultSetImpl rs, Calendar cal) throws SQLException {
        byte[] columnValue = this.internalRowData[columnIndex];
        if (columnValue != null) return this.getNativeDate((int)columnIndex, (byte[])columnValue, (int)0, (int)columnValue.length, (MySQLConnection)conn, (ResultSetImpl)rs, (Calendar)cal);
        return null;
    }

    @Override
    public Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        byte[] columnValue = this.internalRowData[columnIndex];
        if (columnValue != null) return this.getNativeTime((int)columnIndex, (byte[])columnValue, (int)0, (int)columnValue.length, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs);
        return null;
    }

    @Override
    public int getBytesSize() {
        if (this.internalRowData == null) {
            return 0;
        }
        int bytesSize = 0;
        int i = 0;
        while (i < this.internalRowData.length) {
            if (this.internalRowData[i] != null) {
                bytesSize += this.internalRowData[i].length;
            }
            ++i;
        }
        return bytesSize;
    }
}

