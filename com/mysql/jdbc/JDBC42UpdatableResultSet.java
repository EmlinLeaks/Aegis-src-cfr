/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.JDBC42Helper;
import com.mysql.jdbc.JDBC4UpdatableResultSet;
import com.mysql.jdbc.MySQLConnection;
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

public class JDBC42UpdatableResultSet
extends JDBC4UpdatableResultSet {
    public JDBC42UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        super((String)catalog, (Field[])fields, (RowData)tuples, (MySQLConnection)conn, (StatementImpl)creatorStmt);
    }

    private int translateAndCheckSqlType(SQLType sqlType) throws SQLException {
        return JDBC42Helper.translateAndCheckSqlType((SQLType)sqlType, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (type == null) {
            throw SQLError.createSQLException((String)"Type parameter can not be null", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (type.equals(LocalDate.class)) {
            // MONITOREXIT : object
            return (T)type.cast((Object)this.getDate((int)columnIndex).toLocalDate());
        }
        if (type.equals(LocalDateTime.class)) {
            // MONITOREXIT : object
            return (T)type.cast((Object)this.getTimestamp((int)columnIndex).toLocalDateTime());
        }
        if (type.equals(LocalTime.class)) {
            // MONITOREXIT : object
            return (T)type.cast((Object)this.getTime((int)columnIndex).toLocalTime());
        }
        if (type.equals(OffsetDateTime.class)) {
            try {
                // MONITOREXIT : object
                return (T)type.cast((Object)OffsetDateTime.parse((CharSequence)this.getString((int)columnIndex)));
            }
            catch (DateTimeParseException dateTimeParseException) {
                return (T)((T)super.getObject((int)columnIndex, type));
            }
        }
        if (type.equals(OffsetTime.class)) {
            try {
                // MONITOREXIT : object
                return (T)type.cast((Object)OffsetTime.parse((CharSequence)this.getString((int)columnIndex)));
            }
            catch (DateTimeParseException dateTimeParseException) {
                // empty catch block
            }
        }
        // MONITOREXIT : object
        return (T)super.getObject((int)columnIndex, type);
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        super.updateObject((int)columnIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x));
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        super.updateObject((int)columnIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (int)scaleOrLength);
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        super.updateObject((String)columnLabel, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x));
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        super.updateObject((String)columnLabel, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (int)scaleOrLength);
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        super.updateObjectInternal((int)columnIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (Integer)Integer.valueOf((int)this.translateAndCheckSqlType((SQLType)targetSqlType)), (int)0);
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        super.updateObjectInternal((int)columnIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (Integer)Integer.valueOf((int)this.translateAndCheckSqlType((SQLType)targetSqlType)), (int)scaleOrLength);
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        super.updateObjectInternal((int)this.findColumn((String)columnLabel), (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (Integer)Integer.valueOf((int)this.translateAndCheckSqlType((SQLType)targetSqlType)), (int)0);
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        super.updateObjectInternal((int)this.findColumn((String)columnLabel), (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (Integer)Integer.valueOf((int)this.translateAndCheckSqlType((SQLType)targetSqlType)), (int)scaleOrLength);
    }
}

