/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.OperationNotSupportedException;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StreamingNotifiable;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.sql.SQLException;

public class RowDataDynamic
implements RowData {
    private int columnCount;
    private Field[] metadata;
    private int index = -1;
    private MysqlIO io;
    private boolean isAfterEnd = false;
    private boolean noMoreRows = false;
    private boolean isBinaryEncoded = false;
    private ResultSetRow nextRow;
    private ResultSetImpl owner;
    private boolean streamerClosed = false;
    private boolean wasEmpty = false;
    private boolean useBufferRowExplicit;
    private boolean moreResultsExisted;
    private ExceptionInterceptor exceptionInterceptor;

    public RowDataDynamic(MysqlIO io, int colCount, Field[] fields, boolean isBinaryEncoded) throws SQLException {
        this.io = io;
        this.columnCount = colCount;
        this.isBinaryEncoded = isBinaryEncoded;
        this.metadata = fields;
        this.exceptionInterceptor = this.io.getExceptionInterceptor();
        this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit((Field[])this.metadata);
    }

    @Override
    public void addRow(ResultSetRow row) throws SQLException {
        this.notSupported();
    }

    @Override
    public void afterLast() throws SQLException {
        this.notSupported();
    }

    @Override
    public void beforeFirst() throws SQLException {
        this.notSupported();
    }

    @Override
    public void beforeLast() throws SQLException {
        this.notSupported();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        Object mutex = this;
        MySQLConnection conn = null;
        if (this.owner != null && (conn = this.owner.connection) != null) {
            mutex = conn.getConnectionMutex();
        }
        boolean hadMore = false;
        int howMuchMore = 0;
        RowDataDynamic rowDataDynamic = mutex;
        // MONITORENTER : rowDataDynamic
        while (this.next() != null) {
            hadMore = true;
            if (++howMuchMore % 100 != 0) continue;
            Thread.yield();
        }
        if (conn != null) {
            if (!conn.getClobberStreamingResults() && conn.getNetTimeoutForStreamingResults() > 0) {
                String oldValue = conn.getServerVariable((String)"net_write_timeout");
                if (oldValue == null || oldValue.length() == 0) {
                    oldValue = "60";
                }
                this.io.clearInputStream();
                java.sql.Statement stmt = null;
                try {
                    stmt = conn.createStatement();
                    ((StatementImpl)stmt).executeSimpleNonQuery((MySQLConnection)conn, (String)("SET net_write_timeout=" + oldValue));
                    Object var9_8 = null;
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (Throwable throwable) {
                    Object var9_9 = null;
                    if (stmt == null) throw throwable;
                    stmt.close();
                    throw throwable;
                }
            }
            if (conn.getUseUsageAdvisor() && hadMore) {
                this.owner.connection.getProfilerEventHandlerInstance().processEvent((byte)0, (MySQLConnection)this.owner.connection, (Statement)this.owner.owningStatement, null, (long)0L, null, (String)Messages.getString((String)"RowDataDynamic.1", (Object[])new String[]{String.valueOf((int)howMuchMore), this.owner.pointOfOrigin}));
            }
        }
        // MONITOREXIT : rowDataDynamic
        this.metadata = null;
        this.owner = null;
    }

    @Override
    public ResultSetRow getAt(int ind) throws SQLException {
        this.notSupported();
        return null;
    }

    @Override
    public int getCurrentRowNumber() throws SQLException {
        this.notSupported();
        return -1;
    }

    @Override
    public ResultSetInternalMethods getOwner() {
        return this.owner;
    }

    @Override
    public boolean hasNext() throws SQLException {
        boolean hasNext = this.nextRow != null;
        if (hasNext) return hasNext;
        if (this.streamerClosed) return hasNext;
        this.io.closeStreamer((RowData)this);
        this.streamerClosed = true;
        return hasNext;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return this.isAfterEnd;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        if (this.index >= 0) return false;
        return true;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean isEmpty() throws SQLException {
        this.notSupported();
        return false;
    }

    @Override
    public boolean isFirst() throws SQLException {
        this.notSupported();
        return false;
    }

    @Override
    public boolean isLast() throws SQLException {
        this.notSupported();
        return false;
    }

    @Override
    public void moveRowRelative(int rows) throws SQLException {
        this.notSupported();
    }

    @Override
    public ResultSetRow next() throws SQLException {
        this.nextRecord();
        if (this.nextRow == null && !this.streamerClosed && !this.moreResultsExisted) {
            this.io.closeStreamer((RowData)this);
            this.streamerClosed = true;
        }
        if (this.nextRow == null) return this.nextRow;
        if (this.index == Integer.MAX_VALUE) return this.nextRow;
        ++this.index;
        return this.nextRow;
    }

    private void nextRecord() throws SQLException {
        try {
            if (!this.noMoreRows) {
                this.nextRow = this.io.nextRow((Field[])this.metadata, (int)this.columnCount, (boolean)this.isBinaryEncoded, (int)1007, (boolean)true, (boolean)this.useBufferRowExplicit, (boolean)true, null);
                if (this.nextRow != null) return;
                this.noMoreRows = true;
                this.isAfterEnd = true;
                this.moreResultsExisted = this.io.tackOnMoreStreamingResults((ResultSetImpl)this.owner);
                if (this.index != -1) return;
                this.wasEmpty = true;
                return;
            }
            this.nextRow = null;
            this.isAfterEnd = true;
            return;
        }
        catch (SQLException sqlEx) {
            if (sqlEx instanceof StreamingNotifiable) {
                ((StreamingNotifiable)((Object)sqlEx)).setWasStreamingResults();
            }
            this.noMoreRows = true;
            throw sqlEx;
        }
        catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"RowDataDynamic.2", (Object[])new String[]{ex.getClass().getName(), ex.getMessage(), Util.stackTraceToString((Throwable)ex)}), (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    private void notSupported() throws SQLException {
        throw new OperationNotSupportedException();
    }

    @Override
    public void removeRow(int ind) throws SQLException {
        this.notSupported();
    }

    @Override
    public void setCurrentRow(int rowNumber) throws SQLException {
        this.notSupported();
    }

    @Override
    public void setOwner(ResultSetImpl rs) {
        this.owner = rs;
    }

    @Override
    public int size() {
        return -1;
    }

    @Override
    public boolean wasEmpty() {
        return this.wasEmpty;
    }

    @Override
    public void setMetadata(Field[] metadata) {
        this.metadata = metadata;
    }
}

