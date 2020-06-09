/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.JDBC42Helper;
import com.mysql.jdbc.JDBC4CallableStatement;
import com.mysql.jdbc.MySQLConnection;
import java.sql.SQLException;
import java.sql.SQLType;

public class JDBC42CallableStatement
extends JDBC4CallableStatement {
    public JDBC42CallableStatement(MySQLConnection conn, CallableStatement.CallableStatementParamInfo paramInfo) throws SQLException {
        super((MySQLConnection)conn, (CallableStatement.CallableStatementParamInfo)paramInfo);
    }

    public JDBC42CallableStatement(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
        super((MySQLConnection)conn, (String)sql, (String)catalog, (boolean)isFunctionCall);
    }

    private int checkSqlType(int sqlType) throws SQLException {
        return JDBC42Helper.checkSqlType((int)sqlType, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private int translateAndCheckSqlType(SQLType sqlType) throws SQLException {
        return JDBC42Helper.translateAndCheckSqlType((SQLType)sqlType, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
        super.registerOutParameter((int)parameterIndex, (int)this.translateAndCheckSqlType((SQLType)sqlType));
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
        super.registerOutParameter((int)parameterIndex, (int)this.translateAndCheckSqlType((SQLType)sqlType), (int)scale);
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
        super.registerOutParameter((int)parameterIndex, (int)this.translateAndCheckSqlType((SQLType)sqlType), (String)typeName);
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
        super.registerOutParameter((String)parameterName, (int)this.translateAndCheckSqlType((SQLType)sqlType));
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
        super.registerOutParameter((String)parameterName, (int)this.translateAndCheckSqlType((SQLType)sqlType), (int)scale);
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
        super.registerOutParameter((String)parameterName, (int)this.translateAndCheckSqlType((SQLType)sqlType), (String)typeName);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        super.setObject((String)parameterName, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (int)this.translateAndCheckSqlType((SQLType)targetSqlType));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        super.setObject((String)parameterName, (Object)JDBC42Helper.convertJavaTimeToJavaSql((Object)x), (int)this.translateAndCheckSqlType((SQLType)targetSqlType), (int)scaleOrLength);
        // MONITOREXIT : object
        return;
    }
}

