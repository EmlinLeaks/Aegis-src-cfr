/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.SQLError;
import java.sql.ParameterMetaData;
import java.sql.SQLException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MysqlParameterMetadata
implements ParameterMetaData {
    boolean returnSimpleMetadata = false;
    ResultSetMetaData metadata = null;
    int parameterCount = 0;
    private ExceptionInterceptor exceptionInterceptor;

    MysqlParameterMetadata(Field[] fieldInfo, int parameterCount, ExceptionInterceptor exceptionInterceptor) {
        this.metadata = new ResultSetMetaData((Field[])fieldInfo, (boolean)false, (boolean)true, (ExceptionInterceptor)exceptionInterceptor);
        this.parameterCount = parameterCount;
        this.exceptionInterceptor = exceptionInterceptor;
    }

    MysqlParameterMetadata(int count) {
        this.parameterCount = count;
        this.returnSimpleMetadata = true;
    }

    @Override
    public int getParameterCount() throws SQLException {
        return this.parameterCount;
    }

    @Override
    public int isNullable(int arg0) throws SQLException {
        this.checkAvailable();
        return this.metadata.isNullable((int)arg0);
    }

    private void checkAvailable() throws SQLException {
        if (this.metadata == null) throw SQLError.createSQLException((String)"Parameter metadata not available for the given statement", (String)"S1C00", (ExceptionInterceptor)this.exceptionInterceptor);
        if (this.metadata.fields != null) return;
        throw SQLError.createSQLException((String)"Parameter metadata not available for the given statement", (String)"S1C00", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public boolean isSigned(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            this.checkBounds((int)arg0);
            return false;
        }
        this.checkAvailable();
        return this.metadata.isSigned((int)arg0);
    }

    @Override
    public int getPrecision(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            this.checkBounds((int)arg0);
            return 0;
        }
        this.checkAvailable();
        return this.metadata.getPrecision((int)arg0);
    }

    @Override
    public int getScale(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            this.checkBounds((int)arg0);
            return 0;
        }
        this.checkAvailable();
        return this.metadata.getScale((int)arg0);
    }

    @Override
    public int getParameterType(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            this.checkBounds((int)arg0);
            return 12;
        }
        this.checkAvailable();
        return this.metadata.getColumnType((int)arg0);
    }

    @Override
    public String getParameterTypeName(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            this.checkBounds((int)arg0);
            return "VARCHAR";
        }
        this.checkAvailable();
        return this.metadata.getColumnTypeName((int)arg0);
    }

    @Override
    public String getParameterClassName(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            this.checkBounds((int)arg0);
            return "java.lang.String";
        }
        this.checkAvailable();
        return this.metadata.getColumnClassName((int)arg0);
    }

    @Override
    public int getParameterMode(int arg0) throws SQLException {
        return 1;
    }

    private void checkBounds(int paramNumber) throws SQLException {
        if (paramNumber < 1) {
            throw SQLError.createSQLException((String)("Parameter index of '" + paramNumber + "' is invalid."), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (paramNumber <= this.parameterCount) return;
        throw SQLError.createSQLException((String)("Parameter index of '" + paramNumber + "' is greater than number of parameters, which is '" + this.parameterCount + "'."), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance((Object)this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return (T)iface.cast((Object)this);
        }
        catch (ClassCastException cce) {
            throw SQLError.createSQLException((String)("Unable to unwrap to " + iface.toString()), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
    }
}

