/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.TimeUtil;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public abstract class ResultSetRow {
    protected ExceptionInterceptor exceptionInterceptor;
    protected Field[] metadata;

    protected ResultSetRow(ExceptionInterceptor exceptionInterceptor) {
        this.exceptionInterceptor = exceptionInterceptor;
    }

    public abstract void closeOpenStreams();

    public abstract InputStream getBinaryInputStream(int var1) throws SQLException;

    public abstract byte[] getColumnValue(int var1) throws SQLException;

    protected final Date getDateFast(int columnIndex, byte[] dateAsBytes, int offset, int length, MySQLConnection conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
        int year = 0;
        int month = 0;
        int day = 0;
        try {
            int i;
            if (dateAsBytes == null) {
                return null;
            }
            boolean allZeroDate = true;
            boolean onlyTimePresent = false;
            for (i = 0; i < length; ++i) {
                if (dateAsBytes[offset + i] != 58) continue;
                onlyTimePresent = true;
                break;
            }
            for (i = 0; i < length; ++i) {
                byte b = dateAsBytes[offset + i];
                if (b == 32 || b == 45 || b == 47) {
                    onlyTimePresent = false;
                }
                if (b == 48 || b == 32 || b == 58 || b == 45 || b == 47 || b == 46) continue;
                allZeroDate = false;
                break;
            }
            int decimalIndex = -1;
            for (int i2 = 0; i2 < length; ++i2) {
                if (dateAsBytes[offset + i2] != 46) continue;
                decimalIndex = i2;
                break;
            }
            if (decimalIndex > -1) {
                length = decimalIndex;
            }
            if (!onlyTimePresent && allZeroDate) {
                if ("convertToNull".equals((Object)conn.getZeroDateTimeBehavior())) {
                    return null;
                }
                if (!"exception".equals((Object)conn.getZeroDateTimeBehavior())) return rs.fastDateCreate((Calendar)targetCalendar, (int)1, (int)1, (int)1);
                throw SQLError.createSQLException((String)("Value '" + StringUtils.toString((byte[])dateAsBytes) + "' can not be represented as java.sql.Date"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            }
            if (this.metadata[columnIndex].getMysqlType() == 7) {
                switch (length) {
                    case 19: 
                    case 21: 
                    case 29: {
                        year = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 0), (int)(offset + 4));
                        month = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 5), (int)(offset + 7));
                        day = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 8), (int)(offset + 10));
                        return rs.fastDateCreate((Calendar)targetCalendar, (int)year, (int)month, (int)day);
                    }
                    case 8: 
                    case 14: {
                        year = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 0), (int)(offset + 4));
                        month = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 4), (int)(offset + 6));
                        day = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 6), (int)(offset + 8));
                        return rs.fastDateCreate((Calendar)targetCalendar, (int)year, (int)month, (int)day);
                    }
                    case 6: 
                    case 10: 
                    case 12: {
                        year = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 0), (int)(offset + 2));
                        if (year <= 69) {
                            year += 100;
                        }
                        month = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 2), (int)(offset + 4));
                        day = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 4), (int)(offset + 6));
                        return rs.fastDateCreate((Calendar)targetCalendar, (int)(year + 1900), (int)month, (int)day);
                    }
                    case 4: {
                        year = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 0), (int)(offset + 4));
                        if (year <= 69) {
                            year += 100;
                        }
                        month = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 2), (int)(offset + 4));
                        return rs.fastDateCreate((Calendar)targetCalendar, (int)(year + 1900), (int)month, (int)1);
                    }
                    case 2: {
                        year = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 0), (int)(offset + 2));
                        if (year > 69) return rs.fastDateCreate((Calendar)targetCalendar, (int)(year + 1900), (int)1, (int)1);
                        year += 100;
                        return rs.fastDateCreate((Calendar)targetCalendar, (int)(year + 1900), (int)1, (int)1);
                    }
                }
                throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_Date", (Object[])new Object[]{StringUtils.toString((byte[])dateAsBytes), Integer.valueOf((int)(columnIndex + 1))}), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            }
            if (this.metadata[columnIndex].getMysqlType() == 13) {
                if (length != 2 && length != 1) {
                    year = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 0), (int)(offset + 4));
                    return rs.fastDateCreate((Calendar)targetCalendar, (int)year, (int)1, (int)1);
                }
                year = StringUtils.getInt((byte[])dateAsBytes, (int)offset, (int)(offset + length));
                if (year > 69) return rs.fastDateCreate((Calendar)targetCalendar, (int)(year += 1900), (int)1, (int)1);
                year += 100;
                return rs.fastDateCreate((Calendar)targetCalendar, (int)(year += 1900), (int)1, (int)1);
            }
            if (this.metadata[columnIndex].getMysqlType() == 11) {
                return rs.fastDateCreate((Calendar)targetCalendar, (int)1970, (int)1, (int)1);
            }
            if (length < 10) {
                if (length != 8) throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_Date", (Object[])new Object[]{StringUtils.toString((byte[])dateAsBytes), Integer.valueOf((int)(columnIndex + 1))}), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
                return rs.fastDateCreate((Calendar)targetCalendar, (int)1970, (int)1, (int)1);
            }
            if (length != 18) {
                year = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 0), (int)(offset + 4));
                month = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 5), (int)(offset + 7));
                day = StringUtils.getInt((byte[])dateAsBytes, (int)(offset + 8), (int)(offset + 10));
                return rs.fastDateCreate((Calendar)targetCalendar, (int)year, (int)month, (int)day);
            }
            StringTokenizer st = new StringTokenizer((String)StringUtils.toString((byte[])dateAsBytes, (int)offset, (int)length, (String)"ISO8859_1"), (String)"- ");
            year = Integer.parseInt((String)st.nextToken());
            month = Integer.parseInt((String)st.nextToken());
            day = Integer.parseInt((String)st.nextToken());
            return rs.fastDateCreate((Calendar)targetCalendar, (int)year, (int)month, (int)day);
        }
        catch (SQLException sqlEx) {
            throw sqlEx;
        }
        catch (Exception e) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_Date", (Object[])new Object[]{StringUtils.toString((byte[])dateAsBytes), Integer.valueOf((int)(columnIndex + 1))}), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
    }

    public abstract Date getDateFast(int var1, MySQLConnection var2, ResultSetImpl var3, Calendar var4) throws SQLException;

    public abstract int getInt(int var1) throws SQLException;

    public abstract long getLong(int var1) throws SQLException;

    protected Date getNativeDate(int columnIndex, byte[] bits, int offset, int length, MySQLConnection conn, ResultSetImpl rs, Calendar cal) throws SQLException {
        Calendar calendar;
        int year = 0;
        int month = 0;
        int day = 0;
        if (length != 0) {
            year = bits[offset + 0] & 255 | (bits[offset + 1] & 255) << 8;
            month = bits[offset + 2];
            day = bits[offset + 3];
        }
        if (length == 0 || year == 0 && month == 0 && day == 0) {
            if ("convertToNull".equals((Object)conn.getZeroDateTimeBehavior())) {
                return null;
            }
            if ("exception".equals((Object)conn.getZeroDateTimeBehavior())) {
                throw SQLError.createSQLException((String)"Value '0000-00-00' can not be represented as java.sql.Date", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            }
            year = 1;
            month = 1;
            day = 1;
        }
        if (!rs.useLegacyDatetimeCode) {
            return TimeUtil.fastDateCreate((int)year, (int)month, (int)day, (Calendar)cal);
        }
        if (cal == null) {
            calendar = rs.getCalendarInstanceForSessionOrNew();
            return rs.fastDateCreate((Calendar)calendar, (int)year, (int)month, (int)day);
        }
        calendar = cal;
        return rs.fastDateCreate((Calendar)calendar, (int)year, (int)month, (int)day);
    }

    public abstract Date getNativeDate(int var1, MySQLConnection var2, ResultSetImpl var3, Calendar var4) throws SQLException;

    protected Object getNativeDateTimeValue(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        int year = 0;
        int month = 0;
        int day = 0;
        byte hour = 0;
        byte minute = 0;
        int seconds = 0;
        int nanos = 0;
        if (bits == null) {
            return null;
        }
        Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
        boolean populatedFromDateTimeValue = false;
        switch (mysqlType) {
            case 7: 
            case 12: {
                populatedFromDateTimeValue = true;
                if (length == 0) break;
                year = bits[offset + 0] & 255 | (bits[offset + 1] & 255) << 8;
                month = bits[offset + 2];
                day = bits[offset + 3];
                if (length > 4) {
                    hour = bits[offset + 4];
                    minute = bits[offset + 5];
                    seconds = bits[offset + 6];
                }
                if (length <= 7) break;
                nanos = (bits[offset + 7] & 255 | (bits[offset + 8] & 255) << 8 | (bits[offset + 9] & 255) << 16 | (bits[offset + 10] & 255) << 24) * 1000;
                break;
            }
            case 10: {
                populatedFromDateTimeValue = true;
                if (length == 0) break;
                year = bits[offset + 0] & 255 | (bits[offset + 1] & 255) << 8;
                month = bits[offset + 2];
                day = bits[offset + 3];
                break;
            }
            case 11: {
                populatedFromDateTimeValue = true;
                if (length != 0) {
                    hour = bits[offset + 5];
                    minute = bits[offset + 6];
                    seconds = bits[offset + 7];
                }
                year = 1970;
                month = 1;
                day = 1;
                break;
            }
            default: {
                populatedFromDateTimeValue = false;
            }
        }
        switch (jdbcType) {
            case 92: {
                if (!populatedFromDateTimeValue) return rs.getNativeTimeViaParseConversion((int)(columnIndex + 1), (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward);
                if (!rs.useLegacyDatetimeCode) {
                    return TimeUtil.fastTimeCreate((int)hour, (int)minute, (int)seconds, (Calendar)targetCalendar, (ExceptionInterceptor)this.exceptionInterceptor);
                }
                Time time = TimeUtil.fastTimeCreate((Calendar)rs.getCalendarInstanceForSessionOrNew(), (int)hour, (int)minute, (int)seconds, (ExceptionInterceptor)this.exceptionInterceptor);
                return TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Time)time, (TimeZone)conn.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
            }
            case 91: {
                if (!populatedFromDateTimeValue) return rs.getNativeDateViaParseConversion((int)(columnIndex + 1));
                if (year == 0 && month == 0 && day == 0) {
                    if ("convertToNull".equals((Object)conn.getZeroDateTimeBehavior())) {
                        return null;
                    }
                    if ("exception".equals((Object)conn.getZeroDateTimeBehavior())) {
                        throw new SQLException((String)"Value '0000-00-00' can not be represented as java.sql.Date", (String)"S1009");
                    }
                    year = 1;
                    month = 1;
                    day = 1;
                }
                if (rs.useLegacyDatetimeCode) return rs.fastDateCreate((Calendar)rs.getCalendarInstanceForSessionOrNew(), (int)year, (int)month, (int)day);
                return TimeUtil.fastDateCreate((int)year, (int)month, (int)day, (Calendar)targetCalendar);
            }
            case 93: {
                if (!populatedFromDateTimeValue) return rs.getNativeTimestampViaParseConversion((int)(columnIndex + 1), (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward);
                if (year == 0 && month == 0 && day == 0) {
                    if ("convertToNull".equals((Object)conn.getZeroDateTimeBehavior())) {
                        return null;
                    }
                    if ("exception".equals((Object)conn.getZeroDateTimeBehavior())) {
                        throw new SQLException((String)"Value '0000-00-00' can not be represented as java.sql.Timestamp", (String)"S1009");
                    }
                    year = 1;
                    month = 1;
                    day = 1;
                }
                if (!rs.useLegacyDatetimeCode) {
                    return TimeUtil.fastTimestampCreate((TimeZone)tz, (int)year, (int)month, (int)day, (int)hour, (int)minute, (int)seconds, (int)nanos);
                }
                boolean useGmtMillis = conn.getUseGmtMillisForDatetimes();
                Timestamp ts = rs.fastTimestampCreate((Calendar)rs.getCalendarInstanceForSessionOrNew(), (int)year, (int)month, (int)day, (int)hour, (int)minute, (int)seconds, (int)nanos, (boolean)useGmtMillis);
                return TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Timestamp)ts, (TimeZone)conn.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
            }
        }
        throw new SQLException((String)"Internal error - conversion method doesn't support this type", (String)"S1000");
    }

    public abstract Object getNativeDateTimeValue(int var1, Calendar var2, int var3, int var4, TimeZone var5, boolean var6, MySQLConnection var7, ResultSetImpl var8) throws SQLException;

    protected double getNativeDouble(byte[] bits, int offset) {
        long valueAsLong = (long)(bits[offset + 0] & 255) | (long)(bits[offset + 1] & 255) << 8 | (long)(bits[offset + 2] & 255) << 16 | (long)(bits[offset + 3] & 255) << 24 | (long)(bits[offset + 4] & 255) << 32 | (long)(bits[offset + 5] & 255) << 40 | (long)(bits[offset + 6] & 255) << 48 | (long)(bits[offset + 7] & 255) << 56;
        return Double.longBitsToDouble((long)valueAsLong);
    }

    public abstract double getNativeDouble(int var1) throws SQLException;

    protected float getNativeFloat(byte[] bits, int offset) {
        int asInt = bits[offset + 0] & 255 | (bits[offset + 1] & 255) << 8 | (bits[offset + 2] & 255) << 16 | (bits[offset + 3] & 255) << 24;
        return Float.intBitsToFloat((int)asInt);
    }

    public abstract float getNativeFloat(int var1) throws SQLException;

    protected int getNativeInt(byte[] bits, int offset) {
        return bits[offset + 0] & 255 | (bits[offset + 1] & 255) << 8 | (bits[offset + 2] & 255) << 16 | (bits[offset + 3] & 255) << 24;
    }

    public abstract int getNativeInt(int var1) throws SQLException;

    protected long getNativeLong(byte[] bits, int offset) {
        return (long)(bits[offset + 0] & 255) | (long)(bits[offset + 1] & 255) << 8 | (long)(bits[offset + 2] & 255) << 16 | (long)(bits[offset + 3] & 255) << 24 | (long)(bits[offset + 4] & 255) << 32 | (long)(bits[offset + 5] & 255) << 40 | (long)(bits[offset + 6] & 255) << 48 | (long)(bits[offset + 7] & 255) << 56;
    }

    public abstract long getNativeLong(int var1) throws SQLException;

    protected short getNativeShort(byte[] bits, int offset) {
        return (short)(bits[offset + 0] & 255 | (bits[offset + 1] & 255) << 8);
    }

    public abstract short getNativeShort(int var1) throws SQLException;

    protected Time getNativeTime(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        byte hour = 0;
        int minute = 0;
        int seconds = 0;
        if (length != 0) {
            hour = bits[offset + 5];
            minute = bits[offset + 6];
            seconds = bits[offset + 7];
        }
        if (!rs.useLegacyDatetimeCode) {
            return TimeUtil.fastTimeCreate((int)hour, (int)minute, (int)seconds, (Calendar)targetCalendar, (ExceptionInterceptor)this.exceptionInterceptor);
        }
        Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
        Time time = TimeUtil.fastTimeCreate((Calendar)sessionCalendar, (int)hour, (int)minute, (int)seconds, (ExceptionInterceptor)this.exceptionInterceptor);
        return TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Time)time, (TimeZone)conn.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
    }

    public abstract Time getNativeTime(int var1, Calendar var2, TimeZone var3, boolean var4, MySQLConnection var5, ResultSetImpl var6) throws SQLException;

    protected Timestamp getNativeTimestamp(byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int seconds = 0;
        int nanos = 0;
        if (length != 0) {
            year = bits[offset + 0] & 255 | (bits[offset + 1] & 255) << 8;
            month = bits[offset + 2];
            day = bits[offset + 3];
            if (length > 4) {
                hour = bits[offset + 4];
                minute = bits[offset + 5];
                seconds = bits[offset + 6];
            }
            if (length > 7) {
                nanos = (bits[offset + 7] & 255 | (bits[offset + 8] & 255) << 8 | (bits[offset + 9] & 255) << 16 | (bits[offset + 10] & 255) << 24) * 1000;
            }
        }
        if (length == 0 || year == 0 && month == 0 && day == 0) {
            if ("convertToNull".equals((Object)conn.getZeroDateTimeBehavior())) {
                return null;
            }
            if ("exception".equals((Object)conn.getZeroDateTimeBehavior())) {
                throw SQLError.createSQLException((String)"Value '0000-00-00' can not be represented as java.sql.Timestamp", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            }
            year = 1;
            month = 1;
            day = 1;
        }
        if (!rs.useLegacyDatetimeCode) {
            return TimeUtil.fastTimestampCreate((TimeZone)tz, (int)year, (int)month, (int)day, (int)hour, (int)minute, (int)seconds, (int)nanos);
        }
        boolean useGmtMillis = conn.getUseGmtMillisForDatetimes();
        Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
        Timestamp ts = rs.fastTimestampCreate((Calendar)sessionCalendar, (int)year, (int)month, (int)day, (int)hour, (int)minute, (int)seconds, (int)nanos, (boolean)useGmtMillis);
        return TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Timestamp)ts, (TimeZone)conn.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
    }

    public abstract Timestamp getNativeTimestamp(int var1, Calendar var2, TimeZone var3, boolean var4, MySQLConnection var5, ResultSetImpl var6) throws SQLException;

    public abstract Reader getReader(int var1) throws SQLException;

    public abstract String getString(int var1, String var2, MySQLConnection var3) throws SQLException;

    protected String getString(String encoding, MySQLConnection conn, byte[] value, int offset, int length) throws SQLException {
        String stringVal = null;
        if (conn == null) return StringUtils.toAsciiString((byte[])value, (int)offset, (int)length);
        if (!conn.getUseUnicode()) return StringUtils.toAsciiString((byte[])value, (int)offset, (int)length);
        try {
            if (encoding == null) {
                return StringUtils.toString((byte[])value);
            }
            SingleByteCharsetConverter converter = conn.getCharsetConverter((String)encoding);
            if (converter == null) return StringUtils.toString((byte[])value, (int)offset, (int)length, (String)encoding);
            return converter.toString((byte[])value, (int)offset, (int)length);
        }
        catch (UnsupportedEncodingException E) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Unsupported_character_encoding____101") + encoding + "'."), (String)"0S100", (ExceptionInterceptor)this.exceptionInterceptor);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    protected Time getTimeFast(int columnIndex, byte[] timeAsBytes, int offset, int fullLength, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        hr = 0;
        min = 0;
        sec = 0;
        nanos = 0;
        decimalIndex = -1;
        try {
            if (timeAsBytes == null) {
                return null;
            }
            allZeroTime = true;
            onlyTimePresent = false;
            for (i = 0; i < fullLength; ++i) {
                if (timeAsBytes[offset + i] != 58) continue;
                onlyTimePresent = true;
                break;
            }
            for (i = 0; i < fullLength; ++i) {
                if (timeAsBytes[offset + i] != 46) continue;
                decimalIndex = i;
                break;
            }
            for (i = 0; i < fullLength; ++i) {
                b = timeAsBytes[offset + i];
                if (b == 32 || b == 45 || b == 47) {
                    onlyTimePresent = false;
                }
                if (b == 48 || b == 32 || b == 58 || b == 45 || b == 47 || b == 46) continue;
                allZeroTime = false;
                break;
            }
            if (!onlyTimePresent && allZeroTime) {
                if ("convertToNull".equals((Object)conn.getZeroDateTimeBehavior())) {
                    return null;
                }
                if ("exception".equals((Object)conn.getZeroDateTimeBehavior()) == false) return rs.fastTimeCreate((Calendar)targetCalendar, (int)0, (int)0, (int)0);
                throw SQLError.createSQLException((String)("Value '" + StringUtils.toString((byte[])timeAsBytes) + "' can not be represented as java.sql.Time"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            }
            timeColField = this.metadata[columnIndex];
            length = fullLength;
            if (decimalIndex != -1) {
                length = decimalIndex;
                if (decimalIndex + 2 > fullLength) throw new IllegalArgumentException();
                nanos = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + decimalIndex + 1), (int)(offset + fullLength));
                numDigits = fullLength - (decimalIndex + 1);
                if (numDigits < 9) {
                    factor = (int)Math.pow((double)10.0, (double)((double)(9 - numDigits)));
                    nanos *= factor;
                }
            }
            if (timeColField.getMysqlType() == 7) {
                switch (length) {
                    case 19: {
                        hr = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + length - 8), (int)(offset + length - 6));
                        min = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + length - 5), (int)(offset + length - 3));
                        sec = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + length - 2), (int)(offset + length));
                        ** break;
                    }
                    case 12: 
                    case 14: {
                        hr = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + length - 6), (int)(offset + length - 4));
                        min = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + length - 4), (int)(offset + length - 2));
                        sec = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + length - 2), (int)(offset + length));
                        ** break;
                    }
                    case 10: {
                        hr = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + 6), (int)(offset + 8));
                        min = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + 8), (int)(offset + 10));
                        sec = 0;
                        ** break;
                    }
                }
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + (columnIndex + 1) + "(" + timeColField + ")."), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
lbl59: // 3 sources:
                precisionLost = new SQLWarning((String)(Messages.getString((String)"ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + timeColField + ")."));
            } else if (timeColField.getMysqlType() == 12) {
                hr = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + 11), (int)(offset + 13));
                min = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + 14), (int)(offset + 16));
                sec = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + 17), (int)(offset + 19));
                precisionLost = new SQLWarning((String)(Messages.getString((String)"ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + (columnIndex + 1) + "(" + timeColField + ")."));
            } else {
                if (timeColField.getMysqlType() == 10) {
                    return rs.fastTimeCreate(null, (int)0, (int)0, (int)0);
                }
                if (length != 5 && length != 8) {
                    throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Bad_format_for_Time____267") + StringUtils.toString((byte[])timeAsBytes) + Messages.getString((String)"ResultSet.___in_column__268") + (columnIndex + 1)), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
                }
                hr = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + 0), (int)(offset + 2));
                min = StringUtils.getInt((byte[])timeAsBytes, (int)(offset + 3), (int)(offset + 5));
                sec = length == 5 ? 0 : StringUtils.getInt((byte[])timeAsBytes, (int)(offset + 6), (int)(offset + 8));
            }
            sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
            if (rs.useLegacyDatetimeCode != false) return TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Time)rs.fastTimeCreate((Calendar)sessionCalendar, (int)hr, (int)min, (int)sec), (TimeZone)conn.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
            if (targetCalendar != null) return rs.fastTimeCreate((Calendar)targetCalendar, (int)hr, (int)min, (int)sec);
            targetCalendar = Calendar.getInstance((TimeZone)tz, (Locale)Locale.US);
            return rs.fastTimeCreate((Calendar)targetCalendar, (int)hr, (int)min, (int)sec);
        }
        catch (RuntimeException ex) {
            sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    public abstract Time getTimeFast(int var1, Calendar var2, TimeZone var3, boolean var4, MySQLConnection var5, ResultSetImpl var6) throws SQLException;

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    protected Timestamp getTimestampFast(int columnIndex, byte[] timestampAsBytes, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs, boolean useGmtMillis, boolean useJDBCCompliantTimezoneShift) throws SQLException {
        try {
            sessionCalendar = useJDBCCompliantTimezoneShift != false ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
            sessionCalendar = TimeUtil.setProlepticIfNeeded((Calendar)sessionCalendar, (Calendar)targetCalendar);
            allZeroTimestamp = true;
            onlyTimePresent = false;
            for (i = 0; i < length; ++i) {
                if (timestampAsBytes[offset + i] != 58) continue;
                onlyTimePresent = true;
                break;
            }
            for (i = 0; i < length; ++i) {
                b = timestampAsBytes[offset + i];
                if (b == 32 || b == 45 || b == 47) {
                    onlyTimePresent = false;
                }
                if (b == 48 || b == 32 || b == 58 || b == 45 || b == 47 || b == 46) continue;
                allZeroTimestamp = false;
                break;
            }
            if (!onlyTimePresent && allZeroTimestamp) {
                if ("convertToNull".equals((Object)conn.getZeroDateTimeBehavior())) {
                    return null;
                }
                if ("exception".equals((Object)conn.getZeroDateTimeBehavior())) {
                    throw SQLError.createSQLException((String)("Value '" + StringUtils.toString((byte[])timestampAsBytes) + "' can not be represented as java.sql.Timestamp"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
                }
                if (rs.useLegacyDatetimeCode != false) return rs.fastTimestampCreate(null, (int)1, (int)1, (int)1, (int)0, (int)0, (int)0, (int)0, (boolean)useGmtMillis);
                return TimeUtil.fastTimestampCreate((TimeZone)tz, (int)1, (int)1, (int)1, (int)0, (int)0, (int)0, (int)0);
            }
            if (this.metadata[columnIndex].getMysqlType() == 13) {
                if (rs.useLegacyDatetimeCode != false) return TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Timestamp)rs.fastTimestampCreate((Calendar)sessionCalendar, (int)StringUtils.getInt((byte[])timestampAsBytes, (int)offset, (int)4), (int)1, (int)1, (int)0, (int)0, (int)0, (int)0, (boolean)useGmtMillis), (TimeZone)conn.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
                return TimeUtil.fastTimestampCreate((TimeZone)tz, (int)StringUtils.getInt((byte[])timestampAsBytes, (int)offset, (int)4), (int)1, (int)1, (int)0, (int)0, (int)0, (int)0);
            }
            year = 0;
            month = 0;
            day = 0;
            hour = 0;
            minutes = 0;
            seconds = 0;
            nanos = 0;
            decimalIndex = -1;
            for (i = 0; i < length; ++i) {
                if (timestampAsBytes[offset + i] != 46) continue;
                decimalIndex = i;
                break;
            }
            if (decimalIndex == offset + length - 1) {
                --length;
            } else if (decimalIndex != -1) {
                if (decimalIndex + 2 > length) throw new IllegalArgumentException();
                nanos = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + decimalIndex + 1), (int)(offset + length));
                numDigits = length - (decimalIndex + 1);
                if (numDigits < 9) {
                    factor = (int)Math.pow((double)10.0, (double)((double)(9 - numDigits)));
                    nanos *= factor;
                }
                length = decimalIndex;
            }
            switch (length) {
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 29: {
                    year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 4));
                    month = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 5), (int)(offset + 7));
                    day = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 8), (int)(offset + 10));
                    hour = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 11), (int)(offset + 13));
                    minutes = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 14), (int)(offset + 16));
                    seconds = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 17), (int)(offset + 19));
                    ** break;
                }
                case 14: {
                    year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 4));
                    month = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 4), (int)(offset + 6));
                    day = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 6), (int)(offset + 8));
                    hour = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 8), (int)(offset + 10));
                    minutes = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 10), (int)(offset + 12));
                    seconds = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 12), (int)(offset + 14));
                    ** break;
                }
                case 12: {
                    year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 2));
                    if (year <= 69) {
                        year += 100;
                    }
                    year += 1900;
                    month = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 2), (int)(offset + 4));
                    day = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 4), (int)(offset + 6));
                    hour = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 6), (int)(offset + 8));
                    minutes = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 8), (int)(offset + 10));
                    seconds = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 10), (int)(offset + 12));
                    ** break;
                }
                case 10: {
                    hasDash = false;
                    for (i = 0; i < length; ++i) {
                        if (timestampAsBytes[offset + i] != 45) continue;
                        hasDash = true;
                        break;
                    }
                    if (this.metadata[columnIndex].getMysqlType() == 10 || hasDash) {
                        year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 4));
                        month = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 5), (int)(offset + 7));
                        day = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 8), (int)(offset + 10));
                        hour = 0;
                        minutes = 0;
                        ** break;
                    }
                    year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 2));
                    if (year <= 69) {
                        year += 100;
                    }
                    month = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 2), (int)(offset + 4));
                    day = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 4), (int)(offset + 6));
                    hour = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 6), (int)(offset + 8));
                    minutes = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 8), (int)(offset + 10));
                    year += 1900;
                    ** break;
                }
                case 8: {
                    hasColon = false;
                    for (i = 0; i < length; ++i) {
                        if (timestampAsBytes[offset + i] != 58) continue;
                        hasColon = true;
                        break;
                    }
                    if (hasColon) {
                        hour = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 2));
                        minutes = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 3), (int)(offset + 5));
                        seconds = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 6), (int)(offset + 8));
                        year = 1970;
                        month = 1;
                        day = 1;
                        ** break;
                    }
                    year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 4));
                    month = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 4), (int)(offset + 6));
                    day = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 6), (int)(offset + 8));
                    year -= 1900;
                    --month;
                    ** break;
                }
                case 6: {
                    year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 2));
                    if (year <= 69) {
                        year += 100;
                    }
                    year += 1900;
                    month = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 2), (int)(offset + 4));
                    day = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 4), (int)(offset + 6));
                    ** break;
                }
                case 4: {
                    year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 2));
                    if (year <= 69) {
                        year += 100;
                    }
                    month = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 2), (int)(offset + 4));
                    day = 1;
                    ** break;
                }
                case 2: {
                    year = StringUtils.getInt((byte[])timestampAsBytes, (int)(offset + 0), (int)(offset + 2));
                    if (year <= 69) {
                        year += 100;
                    }
                    year += 1900;
                    month = 1;
                    day = 1;
                    ** break;
                }
            }
            throw new SQLException((String)("Bad format for Timestamp '" + StringUtils.toString((byte[])timestampAsBytes) + "' in column " + (columnIndex + 1) + "."), (String)"S1009");
lbl144: // 10 sources:
            if (rs.useLegacyDatetimeCode != false) return TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Timestamp)rs.fastTimestampCreate((Calendar)sessionCalendar, (int)year, (int)month, (int)day, (int)hour, (int)minutes, (int)seconds, (int)nanos, (boolean)useGmtMillis), (TimeZone)conn.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
            return TimeUtil.fastTimestampCreate((TimeZone)tz, (int)year, (int)month, (int)day, (int)hour, (int)minutes, (int)seconds, (int)nanos);
        }
        catch (RuntimeException e) {
            sqlEx = SQLError.createSQLException((String)("Cannot convert value '" + this.getString((int)columnIndex, (String)"ISO8859_1", (MySQLConnection)conn) + "' from column " + (columnIndex + 1) + " to TIMESTAMP."), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
    }

    public abstract Timestamp getTimestampFast(int var1, Calendar var2, TimeZone var3, boolean var4, MySQLConnection var5, ResultSetImpl var6, boolean var7, boolean var8) throws SQLException;

    public abstract boolean isFloatingPointNumber(int var1) throws SQLException;

    public abstract boolean isNull(int var1) throws SQLException;

    public abstract long length(int var1) throws SQLException;

    public abstract void setColumnValue(int var1, byte[] var2) throws SQLException;

    public ResultSetRow setMetadata(Field[] f) throws SQLException {
        this.metadata = f;
        return this;
    }

    public abstract int getBytesSize();
}

