/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.SQLError;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JDBC42Helper {
    static Object convertJavaTimeToJavaSql(Object x) {
        if (x instanceof LocalDate) {
            return Date.valueOf((LocalDate)((LocalDate)x));
        }
        if (x instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime)((LocalDateTime)x));
        }
        if (!(x instanceof LocalTime)) return x;
        return Time.valueOf((LocalTime)((LocalTime)x));
    }

    static boolean isSqlTypeSupported(int sqlType) {
        if (sqlType == 2012) return false;
        if (sqlType == 2013) return false;
        if (sqlType == 2014) return false;
        return true;
    }

    static int checkSqlType(int sqlType, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (!JDBC42Helper.isSqlTypeSupported((int)sqlType)) throw SQLError.createSQLFeatureNotSupportedException((String)(Messages.getString((String)"UnsupportedSQLType.0") + JDBCType.valueOf((int)sqlType)), (String)"S1C00", (ExceptionInterceptor)exceptionInterceptor);
        return sqlType;
    }

    static int translateAndCheckSqlType(SQLType sqlType, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        return JDBC42Helper.checkSqlType((int)sqlType.getVendorTypeNumber().intValue(), (ExceptionInterceptor)exceptionInterceptor);
    }
}

