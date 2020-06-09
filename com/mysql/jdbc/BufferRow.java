/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.OperationNotSupportedException;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

public class BufferRow
extends ResultSetRow {
    private Buffer rowFromServer;
    private int homePosition = 0;
    private int preNullBitmaskHomePosition = 0;
    private int lastRequestedIndex = -1;
    private int lastRequestedPos;
    private Field[] metadata;
    private boolean isBinaryEncoded;
    private boolean[] isNull;
    private List<InputStream> openStreams;

    public BufferRow(Buffer buf, Field[] fields, boolean isBinaryEncoded, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        super((ExceptionInterceptor)exceptionInterceptor);
        this.rowFromServer = buf;
        this.metadata = fields;
        this.isBinaryEncoded = isBinaryEncoded;
        this.preNullBitmaskHomePosition = this.homePosition = this.rowFromServer.getPosition();
        if (fields == null) return;
        this.setMetadata((Field[])fields);
    }

    @Override
    public synchronized void closeOpenStreams() {
        if (this.openStreams == null) return;
        Iterator<InputStream> iter = this.openStreams.iterator();
        do {
            if (!iter.hasNext()) {
                this.openStreams.clear();
                return;
            }
            try {
                iter.next().close();
            }
            catch (IOException e) {
            }
        } while (true);
    }

    private int findAndSeekToOffset(int index) throws SQLException {
        if (this.isBinaryEncoded) return this.findAndSeekToOffsetForBinaryEncoding((int)index);
        if (index == 0) {
            this.lastRequestedIndex = 0;
            this.lastRequestedPos = this.homePosition;
            this.rowFromServer.setPosition((int)this.homePosition);
            return 0;
        }
        if (index == this.lastRequestedIndex) {
            this.rowFromServer.setPosition((int)this.lastRequestedPos);
            return this.lastRequestedPos;
        }
        int startingIndex = 0;
        if (index > this.lastRequestedIndex) {
            startingIndex = this.lastRequestedIndex >= 0 ? this.lastRequestedIndex : 0;
            this.rowFromServer.setPosition((int)this.lastRequestedPos);
        } else {
            this.rowFromServer.setPosition((int)this.homePosition);
        }
        int i = startingIndex;
        do {
            if (i >= index) {
                this.lastRequestedIndex = index;
                this.lastRequestedPos = this.rowFromServer.getPosition();
                return this.lastRequestedPos;
            }
            this.rowFromServer.fastSkipLenByteArray();
            ++i;
        } while (true);
    }

    /*
     * Unable to fully structure code
     */
    private int findAndSeekToOffsetForBinaryEncoding(int index) throws SQLException {
        if (index == 0) {
            this.lastRequestedIndex = 0;
            this.lastRequestedPos = this.homePosition;
            this.rowFromServer.setPosition((int)this.homePosition);
            return 0;
        }
        if (index == this.lastRequestedIndex) {
            this.rowFromServer.setPosition((int)this.lastRequestedPos);
            return this.lastRequestedPos;
        }
        startingIndex = 0;
        if (index > this.lastRequestedIndex) {
            if (this.lastRequestedIndex >= 0) {
                startingIndex = this.lastRequestedIndex;
            } else {
                startingIndex = 0;
                this.lastRequestedPos = this.homePosition;
            }
            this.rowFromServer.setPosition((int)this.lastRequestedPos);
        } else {
            this.rowFromServer.setPosition((int)this.homePosition);
        }
        i = startingIndex;
        do {
            if (i >= index) {
                this.lastRequestedIndex = index;
                this.lastRequestedPos = this.rowFromServer.getPosition();
                return this.lastRequestedPos;
            }
            if (!this.isNull[i]) {
                curPosition = this.rowFromServer.getPosition();
                switch (this.metadata[i].getMysqlType()) {
                    case 6: {
                        ** break;
                    }
                    case 1: {
                        this.rowFromServer.setPosition((int)(curPosition + 1));
                        ** break;
                    }
                    case 2: 
                    case 13: {
                        this.rowFromServer.setPosition((int)(curPosition + 2));
                        ** break;
                    }
                    case 3: 
                    case 9: {
                        this.rowFromServer.setPosition((int)(curPosition + 4));
                        ** break;
                    }
                    case 8: {
                        this.rowFromServer.setPosition((int)(curPosition + 8));
                        ** break;
                    }
                    case 4: {
                        this.rowFromServer.setPosition((int)(curPosition + 4));
                        ** break;
                    }
                    case 5: {
                        this.rowFromServer.setPosition((int)(curPosition + 8));
                        ** break;
                    }
                    case 11: {
                        this.rowFromServer.fastSkipLenByteArray();
                        ** break;
                    }
                    case 10: {
                        this.rowFromServer.fastSkipLenByteArray();
                        ** break;
                    }
                    case 7: 
                    case 12: {
                        this.rowFromServer.fastSkipLenByteArray();
                        ** break;
                    }
                    case 0: 
                    case 15: 
                    case 16: 
                    case 245: 
                    case 246: 
                    case 249: 
                    case 250: 
                    case 251: 
                    case 252: 
                    case 253: 
                    case 254: 
                    case 255: {
                        this.rowFromServer.fastSkipLenByteArray();
                        ** break;
                    }
                }
                throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.97") + this.metadata[i].getMysqlType() + Messages.getString((String)"MysqlIO.98") + (i + 1) + Messages.getString((String)"MysqlIO.99") + this.metadata.length + Messages.getString((String)"MysqlIO.100")), (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            }
lbl61: // 13 sources:
            ++i;
        } while (true);
    }

    @Override
    public synchronized InputStream getBinaryInputStream(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded && this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        if (length == -1L) {
            return null;
        }
        ByteArrayInputStream stream = new ByteArrayInputStream((byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length));
        if (this.openStreams != null) return stream;
        this.openStreams = new LinkedList<InputStream>();
        return stream;
    }

    @Override
    public byte[] getColumnValue(int index) throws SQLException {
        this.findAndSeekToOffset((int)index);
        if (!this.isBinaryEncoded) {
            return this.rowFromServer.readLenByteArray((int)0);
        }
        if (this.isNull[index]) {
            return null;
        }
        switch (this.metadata[index].getMysqlType()) {
            case 6: {
                return null;
            }
            case 1: {
                return new byte[]{this.rowFromServer.readByte()};
            }
            case 2: 
            case 13: {
                return this.rowFromServer.getBytes((int)2);
            }
            case 3: 
            case 9: {
                return this.rowFromServer.getBytes((int)4);
            }
            case 8: {
                return this.rowFromServer.getBytes((int)8);
            }
            case 4: {
                return this.rowFromServer.getBytes((int)4);
            }
            case 5: {
                return this.rowFromServer.getBytes((int)8);
            }
            case 0: 
            case 7: 
            case 10: 
            case 11: 
            case 12: 
            case 15: 
            case 16: 
            case 245: 
            case 246: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                return this.rowFromServer.readLenByteArray((int)0);
            }
        }
        throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.97") + this.metadata[index].getMysqlType() + Messages.getString((String)"MysqlIO.98") + (index + 1) + Messages.getString((String)"MysqlIO.99") + this.metadata.length + Messages.getString((String)"MysqlIO.100")), (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        if (length != -1L) return StringUtils.getInt((byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)(offset + (int)length));
        return 0;
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        if (length != -1L) return StringUtils.getLong((byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)(offset + (int)length));
        return 0L;
    }

    @Override
    public double getNativeDouble(int columnIndex) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return 0.0;
        }
        this.findAndSeekToOffset((int)columnIndex);
        int offset = this.rowFromServer.getPosition();
        return this.getNativeDouble((byte[])this.rowFromServer.getByteBuffer(), (int)offset);
    }

    @Override
    public float getNativeFloat(int columnIndex) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return 0.0f;
        }
        this.findAndSeekToOffset((int)columnIndex);
        int offset = this.rowFromServer.getPosition();
        return this.getNativeFloat((byte[])this.rowFromServer.getByteBuffer(), (int)offset);
    }

    @Override
    public int getNativeInt(int columnIndex) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return 0;
        }
        this.findAndSeekToOffset((int)columnIndex);
        int offset = this.rowFromServer.getPosition();
        return this.getNativeInt((byte[])this.rowFromServer.getByteBuffer(), (int)offset);
    }

    @Override
    public long getNativeLong(int columnIndex) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return 0L;
        }
        this.findAndSeekToOffset((int)columnIndex);
        int offset = this.rowFromServer.getPosition();
        return this.getNativeLong((byte[])this.rowFromServer.getByteBuffer(), (int)offset);
    }

    @Override
    public short getNativeShort(int columnIndex) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return 0;
        }
        this.findAndSeekToOffset((int)columnIndex);
        int offset = this.rowFromServer.getPosition();
        return this.getNativeShort((byte[])this.rowFromServer.getByteBuffer(), (int)offset);
    }

    @Override
    public Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        return this.getNativeTimestamp((byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length), (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs);
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
    public String getString(int columnIndex, String encoding, MySQLConnection conn) throws SQLException {
        if (this.isBinaryEncoded && this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        if (length == -1L) {
            return null;
        }
        if (length == 0L) {
            return "";
        }
        int offset = this.rowFromServer.getPosition();
        return this.getString((String)encoding, (MySQLConnection)conn, (byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length));
    }

    @Override
    public Time getTimeFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        return this.getTimeFast((int)columnIndex, (byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length), (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs);
    }

    @Override
    public Timestamp getTimestampFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs, boolean useGmtMillis, boolean useJDBCCompliantTimezoneShift) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        return this.getTimestampFast((int)columnIndex, (byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length), (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs, (boolean)useGmtMillis, (boolean)useJDBCCompliantTimezoneShift);
    }

    @Override
    public boolean isFloatingPointNumber(int index) throws SQLException {
        if (this.isBinaryEncoded) {
            switch (this.metadata[index].getSQLType()) {
                case 2: 
                case 3: 
                case 6: 
                case 8: {
                    return true;
                }
            }
            return false;
        }
        this.findAndSeekToOffset((int)index);
        long length = this.rowFromServer.readFieldLength();
        if (length == -1L) {
            return false;
        }
        if (length == 0L) {
            return false;
        }
        int offset = this.rowFromServer.getPosition();
        byte[] buffer = this.rowFromServer.getByteBuffer();
        int i = 0;
        while (i < (int)length) {
            char c = (char)buffer[offset + i];
            if (c == 'e') return true;
            if (c == 'E') {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public boolean isNull(int index) throws SQLException {
        if (this.isBinaryEncoded) return this.isNull[index];
        this.findAndSeekToOffset((int)index);
        if (this.rowFromServer.readFieldLength() != -1L) return false;
        return true;
    }

    @Override
    public long length(int index) throws SQLException {
        this.findAndSeekToOffset((int)index);
        long length = this.rowFromServer.readFieldLength();
        if (length != -1L) return length;
        return 0L;
    }

    @Override
    public void setColumnValue(int index, byte[] value) throws SQLException {
        throw new OperationNotSupportedException();
    }

    @Override
    public ResultSetRow setMetadata(Field[] f) throws SQLException {
        super.setMetadata((Field[])f);
        if (!this.isBinaryEncoded) return this;
        this.setupIsNullBitmask();
        return this;
    }

    private void setupIsNullBitmask() throws SQLException {
        if (this.isNull != null) {
            return;
        }
        this.rowFromServer.setPosition((int)this.preNullBitmaskHomePosition);
        int nullCount = (this.metadata.length + 9) / 8;
        byte[] nullBitMask = new byte[nullCount];
        for (int i = 0; i < nullCount; ++i) {
            nullBitMask[i] = this.rowFromServer.readByte();
        }
        this.homePosition = this.rowFromServer.getPosition();
        this.isNull = new boolean[this.metadata.length];
        int nullMaskPos = 0;
        int bit = 4;
        int i = 0;
        while (i < this.metadata.length) {
            boolean bl = this.isNull[i] = (nullBitMask[nullMaskPos] & bit) != 0;
            if (((bit <<= 1) & 255) == 0) {
                bit = 1;
                ++nullMaskPos;
            }
            ++i;
        }
    }

    @Override
    public Date getDateFast(int columnIndex, MySQLConnection conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        return this.getDateFast((int)columnIndex, (byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length), (MySQLConnection)conn, (ResultSetImpl)rs, (Calendar)targetCalendar);
    }

    @Override
    public Date getNativeDate(int columnIndex, MySQLConnection conn, ResultSetImpl rs, Calendar cal) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        return this.getNativeDate((int)columnIndex, (byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length), (MySQLConnection)conn, (ResultSetImpl)rs, (Calendar)cal);
    }

    @Override
    public Object getNativeDateTimeValue(int columnIndex, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        return this.getNativeDateTimeValue((int)columnIndex, (byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length), (Calendar)targetCalendar, (int)jdbcType, (int)mysqlType, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs);
    }

    @Override
    public Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        if (this.isNull((int)columnIndex)) {
            return null;
        }
        this.findAndSeekToOffset((int)columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        return this.getNativeTime((int)columnIndex, (byte[])this.rowFromServer.getByteBuffer(), (int)offset, (int)((int)length), (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)conn, (ResultSetImpl)rs);
    }

    @Override
    public int getBytesSize() {
        return this.rowFromServer.getBufLength();
    }
}

