/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Constants;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.OutputStreamWatcher;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.WatchableOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class Blob
implements java.sql.Blob,
OutputStreamWatcher {
    private byte[] binaryData = null;
    private boolean isClosed = false;
    private ExceptionInterceptor exceptionInterceptor;

    Blob(ExceptionInterceptor exceptionInterceptor) {
        this.setBinaryData((byte[])Constants.EMPTY_BYTE_ARRAY);
        this.exceptionInterceptor = exceptionInterceptor;
    }

    Blob(byte[] data, ExceptionInterceptor exceptionInterceptor) {
        this.setBinaryData((byte[])data);
        this.exceptionInterceptor = exceptionInterceptor;
    }

    Blob(byte[] data, ResultSetInternalMethods creatorResultSetToSet, int columnIndexToSet) {
        this.setBinaryData((byte[])data);
    }

    private synchronized byte[] getBinaryData() {
        return this.binaryData;
    }

    @Override
    public synchronized InputStream getBinaryStream() throws SQLException {
        this.checkClosed();
        return new ByteArrayInputStream((byte[])this.getBinaryData());
    }

    @Override
    public synchronized byte[] getBytes(long pos, int length) throws SQLException {
        this.checkClosed();
        if (pos < 1L) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Blob.2"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (--pos > (long)this.binaryData.length) {
            throw SQLError.createSQLException((String)"\"pos\" argument can not be larger than the BLOB's length.", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (pos + (long)length > (long)this.binaryData.length) {
            throw SQLError.createSQLException((String)"\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        byte[] newData = new byte[length];
        System.arraycopy((Object)this.getBinaryData(), (int)((int)pos), (Object)newData, (int)0, (int)length);
        return newData;
    }

    @Override
    public synchronized long length() throws SQLException {
        this.checkClosed();
        return (long)this.getBinaryData().length;
    }

    @Override
    public synchronized long position(byte[] pattern, long start) throws SQLException {
        throw SQLError.createSQLException((String)"Not implemented", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public synchronized long position(java.sql.Blob pattern, long start) throws SQLException {
        this.checkClosed();
        return this.position((byte[])pattern.getBytes((long)0L, (int)((int)pattern.length())), (long)start);
    }

    private synchronized void setBinaryData(byte[] newBinaryData) {
        this.binaryData = newBinaryData;
    }

    @Override
    public synchronized OutputStream setBinaryStream(long indexToWriteAt) throws SQLException {
        this.checkClosed();
        if (indexToWriteAt < 1L) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Blob.0"), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        WatchableOutputStream bytesOut = new WatchableOutputStream();
        bytesOut.setWatcher((OutputStreamWatcher)this);
        if (indexToWriteAt <= 0L) return bytesOut;
        bytesOut.write((byte[])this.binaryData, (int)0, (int)((int)(indexToWriteAt - 1L)));
        return bytesOut;
    }

    @Override
    public synchronized int setBytes(long writeAt, byte[] bytes) throws SQLException {
        this.checkClosed();
        return this.setBytes((long)writeAt, (byte[])bytes, (int)0, (int)bytes.length);
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public synchronized int setBytes(long writeAt, byte[] bytes, int offset, int length) throws SQLException {
        this.checkClosed();
        bytesOut = this.setBinaryStream((long)writeAt);
        try {
            try {
                bytesOut.write((byte[])bytes, (int)offset, (int)length);
            }
            catch (IOException ioEx) {
                sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Blob.1"), (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
                sqlEx.initCause((Throwable)ioEx);
                throw sqlEx;
            }
            var10_6 = null;
            bytesOut.close();
            return length;
            catch (IOException doNothing) {
                return length;
            }
        }
        catch (Throwable var9_12) {
            var10_7 = null;
            ** try [egrp 2[TRYBLOCK] [3 : 66->74)] { 
lbl22: // 1 sources:
            bytesOut.close();
            throw var9_12;
lbl24: // 1 sources:
            catch (IOException doNothing) {
                // empty catch block
            }
            throw var9_12;
        }
    }

    public synchronized void streamClosed(byte[] byteData) {
        this.binaryData = byteData;
    }

    @Override
    public synchronized void streamClosed(WatchableOutputStream out) {
        int streamSize = out.size();
        if (streamSize < this.binaryData.length) {
            out.write((byte[])this.binaryData, (int)streamSize, (int)(this.binaryData.length - streamSize));
        }
        this.binaryData = out.toByteArray();
    }

    @Override
    public synchronized void truncate(long len) throws SQLException {
        this.checkClosed();
        if (len < 0L) {
            throw SQLError.createSQLException((String)"\"len\" argument can not be < 1.", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (len > (long)this.binaryData.length) {
            throw SQLError.createSQLException((String)"\"len\" argument can not be larger than the BLOB's length.", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        byte[] newData = new byte[(int)len];
        System.arraycopy((Object)this.getBinaryData(), (int)0, (Object)newData, (int)0, (int)((int)len));
        this.binaryData = newData;
    }

    @Override
    public synchronized void free() throws SQLException {
        this.binaryData = null;
        this.isClosed = true;
    }

    @Override
    public synchronized InputStream getBinaryStream(long pos, long length) throws SQLException {
        this.checkClosed();
        if (pos < 1L) {
            throw SQLError.createSQLException((String)"\"pos\" argument can not be < 1.", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (--pos > (long)this.binaryData.length) {
            throw SQLError.createSQLException((String)"\"pos\" argument can not be larger than the BLOB's length.", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
        if (pos + length <= (long)this.binaryData.length) return new ByteArrayInputStream((byte[])this.getBinaryData(), (int)((int)pos), (int)((int)length));
        throw SQLError.createSQLException((String)"\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    private synchronized void checkClosed() throws SQLException {
        if (!this.isClosed) return;
        throw SQLError.createSQLException((String)"Invalid operation on closed BLOB", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
    }
}

