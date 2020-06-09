/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.JDBC4ResultSet;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.NotUpdatable;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StatementImpl;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeParseException;

public class JDBC42ResultSet
extends JDBC4ResultSet {
    public JDBC42ResultSet(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt) {
        super((long)updateCount, (long)updateID, (MySQLConnection)conn, (StatementImpl)creatorStmt);
    }

    public JDBC42ResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        super((String)catalog, (Field[])fields, (RowData)tuples, (MySQLConnection)conn, (StatementImpl)creatorStmt);
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        if (type == null) {
            throw SQLError.createSQLException((String)"Type parameter can not be null", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (type.equals(LocalDate.class)) {
            T t;
            Date date = this.getDate((int)columnIndex);
            if (date == null) {
                t = null;
                return (T)((T)t);
            }
            t = (T)type.cast((Object)date.toLocalDate());
            return (T)t;
        }
        if (type.equals(LocalDateTime.class)) {
            T t;
            Timestamp timestamp = this.getTimestamp((int)columnIndex);
            if (timestamp == null) {
                t = null;
                return (T)((T)t);
            }
            t = (T)type.cast((Object)timestamp.toLocalDateTime());
            return (T)t;
        }
        if (type.equals(LocalTime.class)) {
            T t;
            Time time = this.getTime((int)columnIndex);
            if (time == null) {
                t = null;
                return (T)((T)t);
            }
            t = (T)type.cast((Object)time.toLocalTime());
            return (T)t;
        }
        if (type.equals(OffsetDateTime.class)) {
            try {
                T t;
                String string = this.getString((int)columnIndex);
                if (string == null) {
                    t = null;
                    return (T)((T)t);
                }
                t = (T)type.cast((Object)OffsetDateTime.parse((CharSequence)string));
                return (T)t;
            }
            catch (DateTimeParseException string) {
                return (T)((T)super.getObject((int)columnIndex, type));
            }
        }
        if (!type.equals(OffsetTime.class)) return (T)super.getObject((int)columnIndex, type);
        try {
            T t;
            String string = this.getString((int)columnIndex);
            if (string == null) {
                t = null;
                return (T)((T)t);
            }
            t = (T)type.cast((Object)OffsetTime.parse((CharSequence)string));
            return (T)t;
        }
        catch (DateTimeParseException string) {
            // empty catch block
        }
        return (T)super.getObject((int)columnIndex, type);
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        throw new NotUpdatable();
    }
}

