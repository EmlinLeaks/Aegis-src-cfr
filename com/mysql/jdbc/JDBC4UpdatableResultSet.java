/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.JDBC4MysqlSQLXML;
import com.mysql.jdbc.JDBC4NClob;
import com.mysql.jdbc.JDBC4PreparedStatement;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.NotUpdatable;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.UpdatableResultSet;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

public class JDBC4UpdatableResultSet
extends UpdatableResultSet {
    public JDBC4UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        super((String)catalog, (Field[])fields, (RowData)tuples, (MySQLConnection)conn, (StatementImpl)creatorStmt);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        this.updateNCharacterStream((int)columnIndex, (Reader)x, (int)((int)length));
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        this.updateAsciiStream((int)this.findColumn((String)columnLabel), (InputStream)x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        this.updateAsciiStream((int)this.findColumn((String)columnLabel), (InputStream)x, (long)length);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        this.updateBinaryStream((int)this.findColumn((String)columnLabel), (InputStream)x);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        this.updateBinaryStream((int)this.findColumn((String)columnLabel), (InputStream)x, (long)length);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        this.updateBlob((int)this.findColumn((String)columnLabel), (InputStream)inputStream);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        this.updateBlob((int)this.findColumn((String)columnLabel), (InputStream)inputStream, (long)length);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        this.updateCharacterStream((int)this.findColumn((String)columnLabel), (Reader)reader);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        this.updateCharacterStream((int)this.findColumn((String)columnLabel), (Reader)reader, (long)length);
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        this.updateClob((int)this.findColumn((String)columnLabel), (Reader)reader);
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        this.updateClob((int)this.findColumn((String)columnLabel), (Reader)reader, (long)length);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        this.updateNCharacterStream((int)this.findColumn((String)columnLabel), (Reader)reader);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        this.updateNCharacterStream((int)this.findColumn((String)columnLabel), (Reader)reader, (long)length);
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        this.updateNClob((int)this.findColumn((String)columnLabel), (Reader)reader);
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        this.updateNClob((int)this.findColumn((String)columnLabel), (Reader)reader, (long)length);
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        this.updateSQLXML((int)this.findColumn((String)columnLabel), (SQLXML)xmlObject);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateNCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        String fieldEncoding = this.fields[columnIndex - 1].getEncoding();
        if (fieldEncoding == null) throw new SQLException((String)"Can not call updateNCharacterStream() when field's character set isn't UTF-8");
        if (!fieldEncoding.equals((Object)"UTF-8")) {
            throw new SQLException((String)"Can not call updateNCharacterStream() when field's character set isn't UTF-8");
        }
        if (!this.onInsertRow) {
            if (!this.doingUpdates) {
                this.doingUpdates = true;
                this.syncUpdate();
            }
            ((JDBC4PreparedStatement)this.updater).setNCharacterStream((int)columnIndex, (Reader)x, (long)((long)length));
            return;
        }
        ((JDBC4PreparedStatement)this.inserter).setNCharacterStream((int)columnIndex, (Reader)x, (long)((long)length));
        if (x == null) {
            this.thisRow.setColumnValue((int)(columnIndex - 1), null);
            return;
        }
        this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])STREAM_DATA_MARKER);
        // MONITOREXIT : object
        return;
    }

    public void updateNCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        this.updateNCharacterStream((int)this.findColumn((String)columnName), (Reader)reader, (int)length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        String fieldEncoding = this.fields[columnIndex - 1].getEncoding();
        if (fieldEncoding == null) throw new SQLException((String)"Can not call updateNClob() when field's character set isn't UTF-8");
        if (!fieldEncoding.equals((Object)"UTF-8")) {
            throw new SQLException((String)"Can not call updateNClob() when field's character set isn't UTF-8");
        }
        if (nClob == null) {
            this.updateNull((int)columnIndex);
            return;
        }
        this.updateNCharacterStream((int)columnIndex, (Reader)nClob.getCharacterStream(), (int)((int)nClob.length()));
        // MONITOREXIT : object
        return;
    }

    @Override
    public void updateNClob(String columnName, NClob nClob) throws SQLException {
        this.updateNClob((int)this.findColumn((String)columnName), (NClob)nClob);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateNString(int columnIndex, String x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        String fieldEncoding = this.fields[columnIndex - 1].getEncoding();
        if (fieldEncoding == null) throw new SQLException((String)"Can not call updateNString() when field's character set isn't UTF-8");
        if (!fieldEncoding.equals((Object)"UTF-8")) {
            throw new SQLException((String)"Can not call updateNString() when field's character set isn't UTF-8");
        }
        if (!this.onInsertRow) {
            if (!this.doingUpdates) {
                this.doingUpdates = true;
                this.syncUpdate();
            }
            ((JDBC4PreparedStatement)this.updater).setNString((int)columnIndex, (String)x);
            return;
        }
        ((JDBC4PreparedStatement)this.inserter).setNString((int)columnIndex, (String)x);
        if (x == null) {
            this.thisRow.setColumnValue((int)(columnIndex - 1), null);
            return;
        }
        this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])StringUtils.getBytes((String)x, (SingleByteCharsetConverter)this.charConverter, (String)fieldEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor()));
        // MONITOREXIT : object
        return;
    }

    @Override
    public void updateNString(String columnName, String x) throws SQLException {
        this.updateNString((int)this.findColumn((String)columnName), (String)x);
    }

    @Override
    public int getHoldability() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    protected NClob getNativeNClob(int columnIndex) throws SQLException {
        String stringVal = this.getStringForNClob((int)columnIndex);
        if (stringVal != null) return this.getNClobFromString((String)stringVal, (int)columnIndex);
        return null;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
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

    private final NClob getNClobFromString(String stringVal, int columnIndex) throws SQLException {
        return new JDBC4NClob((String)stringVal, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        String fieldEncoding = this.fields[columnIndex - 1].getEncoding();
        if (fieldEncoding == null) throw new SQLException((String)"Can not call getNString() when field's charset isn't UTF-8");
        if (fieldEncoding.equals((Object)"UTF-8")) return this.getString((int)columnIndex);
        throw new SQLException((String)"Can not call getNString() when field's charset isn't UTF-8");
    }

    @Override
    public String getNString(String columnName) throws SQLException {
        return this.getNString((int)this.findColumn((String)columnName));
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
        return new JDBC4MysqlSQLXML((ResultSetInternalMethods)this, (int)columnIndex, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return this.getSQLXML((int)this.findColumn((String)columnLabel));
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

    @Override
    public boolean isClosed() throws SQLException {
        return this.isClosed;
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
}

