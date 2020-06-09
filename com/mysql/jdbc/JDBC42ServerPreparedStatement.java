/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.JDBC42Helper;
import com.mysql.jdbc.JDBC4ServerPreparedStatement;
import com.mysql.jdbc.MySQLConnection;
import java.sql.SQLException;
import java.sql.SQLType;

public class JDBC42ServerPreparedStatement
extends JDBC4ServerPreparedStatement {
    public JDBC42ServerPreparedStatement(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
        super((MySQLConnection)conn, (String)sql, (String)catalog, (int)resultSetType, (int)resultSetConcurrency);
    }

    private int checkSqlType(int sqlType) throws SQLException {
        return JDBC42Helper.checkSqlType((int)sqlType, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private int translateAndCheckSqlType(SQLType sqlType) throws SQLException {
        return JDBC42Helper.translateAndCheckSqlType((SQLType)sqlType, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        super.setObject((int)parameterIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        super.setObject((int)parameterIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (int)this.checkSqlType((int)targetSqlType));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        super.setObject((int)parameterIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (int)this.checkSqlType((int)targetSqlType), (int)scaleOrLength);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        super.setObject((int)parameterIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (int)this.translateAndCheckSqlType((SQLType)targetSqlType));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        super.setObject((int)parameterIndex, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (int)this.translateAndCheckSqlType((SQLType)targetSqlType), (int)scaleOrLength);
        // MONITOREXIT : object
        return;
    }
}

