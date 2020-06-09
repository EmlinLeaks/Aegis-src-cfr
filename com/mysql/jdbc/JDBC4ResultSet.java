/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.JDBC4MysqlSQLXML;
import com.mysql.jdbc.JDBC4NClob;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.NotUpdatable;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StatementImpl;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Struct;

public class JDBC4ResultSet
extends ResultSetImpl {
    public JDBC4ResultSet(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt) {
        super((long)updateCount, (long)updateID, (MySQLConnection)conn, (StatementImpl)creatorStmt);
    }

    public JDBC4ResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        super((String)catalog, (Field[])fields, (RowData)tuples, (MySQLConnection)conn, (StatementImpl)creatorStmt);
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        this.checkColumnBounds((int)columnIndex);
        String fieldEncoding = this.fields[columnIndex - 1].getEncoding();
        if (fieldEncoding == null) throw new SQLException((String)"Can not call getNCharacterStream() when field's charset isn't UTF-8");
        if (fieldEncoding.equals((Object)"UTF-8")) return this.getCharacterStream((int)columnIndex);
        throw new SQLException((String)"Can not call getNCharacterStream() when field's charset isn't UTF-8");
    }

    @Override
    public Reader getNCharacterStream(String columnName) throws SQLException {
        return this.getNCharacterStream((int)this.findColumn((String)columnName));
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        this.checkColumnBounds((int)columnIndex);
        String fieldEncoding = this.fields[columnIndex - 1].getEncoding();
        if (fieldEncoding == null) throw new SQLException((String)"Can not call getNClob() when field's charset isn't UTF-8");
        if (!fieldEncoding.equals((Object)"UTF-8")) {
            throw new SQLException((String)"Can not call getNClob() when field's charset isn't UTF-8");
        }
        if (this.isBinaryEncoded) return this.getNativeNClob((int)columnIndex);
        String asString = this.getStringForNClob((int)columnIndex);
        if (asString != null) return new JDBC4NClob((String)asString, (ExceptionInterceptor)this.getExceptionInterceptor());
        return null;
    }

    @Override
    public NClob getNClob(String columnName) throws SQLException {
        return this.getNClob((int)this.findColumn((String)columnName));
    }

    protected NClob getNativeNClob(int columnIndex) throws SQLException {
        String stringVal = this.getStringForNClob((int)columnIndex);
        if (stringVal != null) return this.getNClobFromString((String)stringVal, (int)columnIndex);
        return null;
    }

    private String getStringForNClob(int columnIndex) throws SQLException {
        String asString = null;
        String forcedEncoding = "UTF-8";
        try {
            byte[] asBytes = null;
            asBytes = !this.isBinaryEncoded ? this.getBytes((int)columnIndex) : this.getNativeBytes((int)columnIndex, (boolean)true);
            if (asBytes == null) return asString;
            return new String((byte[])asBytes, (String)forcedEncoding);
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)("Unsupported character encoding " + forcedEncoding), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    private final NClob getNClobFromString(String stringVal, int columnIndex) throws SQLException {
        return new JDBC4NClob((String)stringVal, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        this.checkColumnBounds((int)columnIndex);
        String fieldEncoding = this.fields[columnIndex - 1].getEncoding();
        if (fieldEncoding == null) throw new SQLException((String)"Can not call getNString() when field's charset isn't UTF-8");
        if (fieldEncoding.equals((Object)"UTF-8")) return this.getString((int)columnIndex);
        throw new SQLException((String)"Can not call getNString() when field's charset isn't UTF-8");
    }

    @Override
    public String getNString(String columnName) throws SQLException {
        return this.getNString((int)this.findColumn((String)columnName));
    }

    public void updateNCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateNCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        this.updateNCharacterStream((int)this.findColumn((String)columnName), (Reader)reader, (int)length);
    }

    @Override
    public void updateNClob(String columnName, NClob nClob) throws SQLException {
        this.updateNClob((int)this.findColumn((String)columnName), (NClob)nClob);
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateRowId(String columnName, RowId x) throws SQLException {
        this.updateRowId((int)this.findColumn((String)columnName), (RowId)x);
    }

    @Override
    public int getHoldability() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return this.getRowId((int)this.findColumn((String)columnLabel));
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        this.checkColumnBounds((int)columnIndex);
        return new JDBC4MysqlSQLXML((ResultSetInternalMethods)this, (int)columnIndex, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return this.getSQLXML((int)this.findColumn((String)columnLabel));
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        this.updateAsciiStream((int)this.findColumn((String)columnLabel), (InputStream)x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        this.updateAsciiStream((int)this.findColumn((String)columnLabel), (InputStream)x, (long)length);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        this.updateBinaryStream((int)this.findColumn((String)columnLabel), (InputStream)x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        this.updateBinaryStream((int)this.findColumn((String)columnLabel), (InputStream)x, (long)length);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        this.updateBlob((int)this.findColumn((String)columnLabel), (InputStream)inputStream);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        this.updateBlob((int)this.findColumn((String)columnLabel), (InputStream)inputStream, (long)length);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        this.updateCharacterStream((int)this.findColumn((String)columnLabel), (Reader)reader);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        this.updateCharacterStream((int)this.findColumn((String)columnLabel), (Reader)reader, (long)length);
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        this.updateClob((int)this.findColumn((String)columnLabel), (Reader)reader);
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        this.updateClob((int)this.findColumn((String)columnLabel), (Reader)reader, (long)length);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        this.updateNCharacterStream((int)this.findColumn((String)columnLabel), (Reader)reader);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        this.updateNCharacterStream((int)this.findColumn((String)columnLabel), (Reader)reader, (long)length);
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        this.updateNClob((int)this.findColumn((String)columnLabel), (Reader)reader);
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        this.updateNClob((int)this.findColumn((String)columnLabel), (Reader)reader, (long)length);
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        this.updateNString((int)this.findColumn((String)columnLabel), (String)nString);
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        this.updateSQLXML((int)this.findColumn((String)columnLabel), (SQLXML)xmlObject);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        this.checkClosed();
        return iface.isInstance((Object)this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return (T)iface.cast((Object)this);
        }
        catch (ClassCastException cce) {
            throw SQLError.createSQLException((String)("Unable to unwrap to " + iface.toString()), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        if (type == null) {
            throw SQLError.createSQLException((String)"Type parameter can not be null", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (type.equals(Struct.class)) {
            throw new SQLFeatureNotSupportedException();
        }
        if (type.equals(RowId.class)) {
            return (T)this.getRowId((int)columnIndex);
        }
        if (type.equals(NClob.class)) {
            return (T)this.getNClob((int)columnIndex);
        }
        if (!type.equals(SQLXML.class)) return (T)super.getObject((int)columnIndex, type);
        return (T)this.getSQLXML((int)columnIndex);
    }
}

