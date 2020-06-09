/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CharsetMapping;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.SQLError;
import java.sql.SQLException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ResultSetMetaData
implements java.sql.ResultSetMetaData {
    Field[] fields;
    boolean useOldAliasBehavior = false;
    boolean treatYearAsDate = true;
    private ExceptionInterceptor exceptionInterceptor;

    private static int clampedGetLength(Field f) {
        long fieldLength = f.getLength();
        if (fieldLength <= Integer.MAX_VALUE) return (int)fieldLength;
        fieldLength = Integer.MAX_VALUE;
        return (int)fieldLength;
    }

    private static final boolean isDecimalType(int type) {
        switch (type) {
            case -7: 
            case -6: 
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return true;
            }
        }
        return false;
    }

    public ResultSetMetaData(Field[] fields, boolean useOldAliasBehavior, boolean treatYearAsDate, ExceptionInterceptor exceptionInterceptor) {
        this.fields = fields;
        this.useOldAliasBehavior = useOldAliasBehavior;
        this.treatYearAsDate = treatYearAsDate;
        this.exceptionInterceptor = exceptionInterceptor;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        Field f = this.getField((int)column);
        String database = f.getDatabaseName();
        if (database == null) {
            return "";
        }
        String string = database;
        return string;
    }

    public String getColumnCharacterEncoding(int column) throws SQLException {
        String mysqlName = this.getColumnCharacterSet((int)column);
        String javaName = null;
        if (mysqlName == null) return javaName;
        try {
            return CharsetMapping.getJavaEncodingForMysqlCharset((String)mysqlName);
        }
        catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    public String getColumnCharacterSet(int column) throws SQLException {
        return this.getField((int)column).getEncoding();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        boolean bl;
        Field f = this.getField((int)column);
        if (!f.isBinary() && !f.isBlob()) {
            bl = false;
            return ResultSetMetaData.getClassNameForJavaType((int)f.getSQLType(), (boolean)f.isUnsigned(), (int)f.getMysqlType(), (boolean)bl, (boolean)f.isOpaqueBinary(), (boolean)this.treatYearAsDate);
        }
        bl = true;
        return ResultSetMetaData.getClassNameForJavaType((int)f.getSQLType(), (boolean)f.isUnsigned(), (int)f.getMysqlType(), (boolean)bl, (boolean)f.isOpaqueBinary(), (boolean)this.treatYearAsDate);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return this.fields.length;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        Field f = this.getField((int)column);
        int lengthInBytes = ResultSetMetaData.clampedGetLength((Field)f);
        return lengthInBytes / f.getMaxBytesPerCharacter();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        if (!this.useOldAliasBehavior) return this.getField((int)column).getColumnLabel();
        return this.getColumnName((int)column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        if (this.useOldAliasBehavior) {
            return this.getField((int)column).getName();
        }
        String name = this.getField((int)column).getNameNoAliases();
        if (name == null) return name;
        if (name.length() != 0) return name;
        return this.getField((int)column).getName();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return this.getField((int)column).getSQLType();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        Field field = this.getField((int)column);
        int mysqlType = field.getMysqlType();
        int jdbcType = field.getSQLType();
        switch (mysqlType) {
            case 16: {
                return "BIT";
            }
            case 0: 
            case 246: {
                if (!field.isUnsigned()) return "DECIMAL";
                return "DECIMAL UNSIGNED";
            }
            case 1: {
                if (!field.isUnsigned()) return "TINYINT";
                return "TINYINT UNSIGNED";
            }
            case 2: {
                if (!field.isUnsigned()) return "SMALLINT";
                return "SMALLINT UNSIGNED";
            }
            case 3: {
                if (!field.isUnsigned()) return "INT";
                return "INT UNSIGNED";
            }
            case 4: {
                if (!field.isUnsigned()) return "FLOAT";
                return "FLOAT UNSIGNED";
            }
            case 5: {
                if (!field.isUnsigned()) return "DOUBLE";
                return "DOUBLE UNSIGNED";
            }
            case 6: {
                return "NULL";
            }
            case 7: {
                return "TIMESTAMP";
            }
            case 8: {
                if (!field.isUnsigned()) return "BIGINT";
                return "BIGINT UNSIGNED";
            }
            case 9: {
                if (!field.isUnsigned()) return "MEDIUMINT";
                return "MEDIUMINT UNSIGNED";
            }
            case 10: {
                return "DATE";
            }
            case 11: {
                return "TIME";
            }
            case 12: {
                return "DATETIME";
            }
            case 249: {
                return "TINYBLOB";
            }
            case 250: {
                return "MEDIUMBLOB";
            }
            case 251: {
                return "LONGBLOB";
            }
            case 252: {
                if (!this.getField((int)column).isBinary()) return "TEXT";
                return "BLOB";
            }
            case 15: {
                return "VARCHAR";
            }
            case 253: {
                if (jdbcType != -3) return "VARCHAR";
                return "VARBINARY";
            }
            case 254: {
                if (jdbcType != -2) return "CHAR";
                return "BINARY";
            }
            case 247: {
                return "ENUM";
            }
            case 13: {
                return "YEAR";
            }
            case 248: {
                return "SET";
            }
            case 255: {
                return "GEOMETRY";
            }
            case 245: {
                return "JSON";
            }
        }
        return "UNKNOWN";
    }

    protected Field getField(int columnIndex) throws SQLException {
        if (columnIndex < 1) throw SQLError.createSQLException((String)Messages.getString((String)"ResultSetMetaData.46"), (String)"S1002", (ExceptionInterceptor)this.exceptionInterceptor);
        if (columnIndex <= this.fields.length) return this.fields[columnIndex - 1];
        throw SQLError.createSQLException((String)Messages.getString((String)"ResultSetMetaData.46"), (String)"S1002", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        Field f = this.getField((int)column);
        if (ResultSetMetaData.isDecimalType((int)f.getSQLType())) {
            if (f.getDecimals() <= 0) return ResultSetMetaData.clampedGetLength((Field)f) + f.getPrecisionAdjustFactor();
            return ResultSetMetaData.clampedGetLength((Field)f) - 1 + f.getPrecisionAdjustFactor();
        }
        switch (f.getMysqlType()) {
            case 249: 
            case 250: 
            case 251: 
            case 252: {
                return ResultSetMetaData.clampedGetLength((Field)f);
            }
        }
        return ResultSetMetaData.clampedGetLength((Field)f) / f.getMaxBytesPerCharacter();
    }

    @Override
    public int getScale(int column) throws SQLException {
        Field f = this.getField((int)column);
        if (!ResultSetMetaData.isDecimalType((int)f.getSQLType())) return 0;
        return f.getDecimals();
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return "";
    }

    @Override
    public String getTableName(int column) throws SQLException {
        String res;
        String string = res = this.useOldAliasBehavior ? this.getField((int)column).getTableName() : this.getField((int)column).getTableNameNoAliases();
        if (res == null) {
            return "";
        }
        String string2 = res;
        return string2;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        Field f = this.getField((int)column);
        return f.isAutoIncrement();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        Field field = this.getField((int)column);
        int sqlType = field.getSQLType();
        switch (sqlType) {
            case -7: 
            case -6: 
            case -5: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 91: 
            case 92: 
            case 93: {
                return false;
            }
            case -1: 
            case 1: 
            case 12: {
                if (field.isBinary()) {
                    return true;
                }
                String collationName = field.getCollation();
                if (collationName == null) return false;
                if (collationName.endsWith((String)"_ci")) return false;
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return this.isWritable((int)column);
    }

    @Override
    public int isNullable(int column) throws SQLException {
        if (this.getField((int)column).isNotNull()) return 0;
        return 1;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return this.getField((int)column).isReadOnly();
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        Field f = this.getField((int)column);
        int sqlType = f.getSQLType();
        switch (sqlType) {
            case -6: 
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                if (f.isUnsigned()) return false;
                return true;
            }
            case 91: 
            case 92: 
            case 93: {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        if (this.isReadOnly((int)column)) return false;
        return true;
    }

    public String toString() {
        StringBuilder toStringBuf = new StringBuilder();
        toStringBuf.append((String)super.toString());
        toStringBuf.append((String)" - Field level information: ");
        int i = 0;
        while (i < this.fields.length) {
            toStringBuf.append((String)"\n\t");
            toStringBuf.append((String)this.fields[i].toString());
            ++i;
        }
        return toStringBuf.toString();
    }

    static String getClassNameForJavaType(int javaType, boolean isUnsigned, int mysqlTypeIfKnown, boolean isBinaryOrBlob, boolean isOpaqueBinary, boolean treatYearAsDate) {
        switch (javaType) {
            case -7: 
            case 16: {
                return "java.lang.Boolean";
            }
            case -6: {
                if (!isUnsigned) return "java.lang.Integer";
                return "java.lang.Integer";
            }
            case 5: {
                if (!isUnsigned) return "java.lang.Integer";
                return "java.lang.Integer";
            }
            case 4: {
                if (!isUnsigned) return "java.lang.Integer";
                if (mysqlTypeIfKnown != 9) return "java.lang.Long";
                return "java.lang.Integer";
            }
            case -5: {
                if (isUnsigned) return "java.math.BigInteger";
                return "java.lang.Long";
            }
            case 2: 
            case 3: {
                return "java.math.BigDecimal";
            }
            case 7: {
                return "java.lang.Float";
            }
            case 6: 
            case 8: {
                return "java.lang.Double";
            }
            case -1: 
            case 1: 
            case 12: {
                if (isOpaqueBinary) return "[B";
                return "java.lang.String";
            }
            case -4: 
            case -3: 
            case -2: {
                if (mysqlTypeIfKnown == 255) {
                    return "[B";
                }
                if (!isBinaryOrBlob) return "java.lang.String";
                return "[B";
            }
            case 91: {
                if (treatYearAsDate) return "java.sql.Date";
                if (mysqlTypeIfKnown != 13) return "java.sql.Date";
                return "java.lang.Short";
            }
            case 92: {
                return "java.sql.Time";
            }
            case 93: {
                return "java.sql.Timestamp";
            }
        }
        return "java.lang.Object";
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

