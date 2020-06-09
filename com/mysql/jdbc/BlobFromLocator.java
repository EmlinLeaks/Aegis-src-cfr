/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.BlobFromLocator;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.SQLError;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlobFromLocator
implements Blob {
    private List<String> primaryKeyColumns = null;
    private List<String> primaryKeyValues = null;
    private ResultSetImpl creatorResultSet;
    private String blobColumnName = null;
    private String tableName = null;
    private int numColsInResultSet = 0;
    private int numPrimaryKeys = 0;
    private String quotedId;
    private ExceptionInterceptor exceptionInterceptor;

    BlobFromLocator(ResultSetImpl creatorResultSetToSet, int blobColumnIndex, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        this.exceptionInterceptor = exceptionInterceptor;
        this.creatorResultSet = creatorResultSetToSet;
        this.numColsInResultSet = this.creatorResultSet.fields.length;
        this.quotedId = this.creatorResultSet.connection.getMetaData().getIdentifierQuoteString();
        if (this.numColsInResultSet <= 1) {
            this.notEnoughInformationInQuery();
        } else {
            this.primaryKeyColumns = new ArrayList<String>();
            this.primaryKeyValues = new ArrayList<String>();
            for (int i = 0; i < this.numColsInResultSet; ++i) {
                if (!this.creatorResultSet.fields[i].isPrimaryKey()) continue;
                StringBuilder keyName = new StringBuilder();
                keyName.append((String)this.quotedId);
                String originalColumnName = this.creatorResultSet.fields[i].getOriginalName();
                if (originalColumnName != null && originalColumnName.length() > 0) {
                    keyName.append((String)originalColumnName);
                } else {
                    keyName.append((String)this.creatorResultSet.fields[i].getName());
                }
                keyName.append((String)this.quotedId);
                this.primaryKeyColumns.add((String)keyName.toString());
                this.primaryKeyValues.add((String)this.creatorResultSet.getString((int)(i + 1)));
            }
        }
        this.numPrimaryKeys = this.primaryKeyColumns.size();
        if (this.numPrimaryKeys == 0) {
            this.notEnoughInformationInQuery();
        }
        if (this.creatorResultSet.fields[0].getOriginalTableName() != null) {
            StringBuilder tableNameBuffer = new StringBuilder();
            String databaseName = this.creatorResultSet.fields[0].getDatabaseName();
            if (databaseName != null && databaseName.length() > 0) {
                tableNameBuffer.append((String)this.quotedId);
                tableNameBuffer.append((String)databaseName);
                tableNameBuffer.append((String)this.quotedId);
                tableNameBuffer.append((char)'.');
            }
            tableNameBuffer.append((String)this.quotedId);
            tableNameBuffer.append((String)this.creatorResultSet.fields[0].getOriginalTableName());
            tableNameBuffer.append((String)this.quotedId);
            this.tableName = tableNameBuffer.toString();
        } else {
            StringBuilder tableNameBuffer = new StringBuilder();
            tableNameBuffer.append((String)this.quotedId);
            tableNameBuffer.append((String)this.creatorResultSet.fields[0].getTableName());
            tableNameBuffer.append((String)this.quotedId);
            this.tableName = tableNameBuffer.toString();
        }
        this.blobColumnName = this.quotedId + this.creatorResultSet.getString((int)blobColumnIndex) + this.quotedId;
    }

    private void notEnoughInformationInQuery() throws SQLException {
        throw SQLError.createSQLException((String)"Emulated BLOB locators must come from a ResultSet with only one table selected, and all primary keys selected", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public OutputStream setBinaryStream(long indexToWriteAt) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return new BufferedInputStream((InputStream)new LocatorInputStream((BlobFromLocator)this), (int)this.creatorResultSet.connection.getLocatorFetchBufferSize());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public int setBytes(long writeAt, byte[] bytes, int offset, int length) throws SQLException {
        pStmt = null;
        if (offset + length > bytes.length) {
            length = bytes.length - offset;
        }
        bytesToWrite = new byte[length];
        System.arraycopy((Object)bytes, (int)offset, (Object)bytesToWrite, (int)0, (int)length);
        query = new StringBuilder((String)"UPDATE ");
        query.append((String)this.tableName);
        query.append((String)" SET ");
        query.append((String)this.blobColumnName);
        query.append((String)" = INSERT(");
        query.append((String)this.blobColumnName);
        query.append((String)", ");
        query.append((long)writeAt);
        query.append((String)", ");
        query.append((int)length);
        query.append((String)", ?) WHERE ");
        query.append((String)this.primaryKeyColumns.get((int)0));
        query.append((String)" = ?");
        for (i = 1; i < this.numPrimaryKeys; ++i) {
            query.append((String)" AND ");
            query.append((String)this.primaryKeyColumns.get((int)i));
            query.append((String)" = ?");
        }
        try {
            pStmt = this.creatorResultSet.connection.prepareStatement((String)query.toString());
            pStmt.setBytes((int)1, (byte[])bytesToWrite);
            for (i = 0; i < this.numPrimaryKeys; ++i) {
                pStmt.setString((int)(i + 2), (String)this.primaryKeyValues.get((int)i));
            }
            rowsUpdated = pStmt.executeUpdate();
            if (rowsUpdated != 1) {
                throw SQLError.createSQLException((String)"BLOB data not found! Did primary keys change?", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            }
            var11_9 = null;
            if (pStmt == null) return (int)this.length();
            try {
                pStmt.close();
            }
            catch (SQLException sqlEx) {
                // empty catch block
            }
            pStmt = null;
            return (int)this.length();
        }
        catch (Throwable var10_13) {
            block10 : {
                var11_10 = null;
                if (pStmt == null) throw var10_13;
                ** try [egrp 1[TRYBLOCK] [2 : 334->344)] { 
lbl63: // 1 sources:
                pStmt.close();
                break block10;
lbl65: // 1 sources:
                catch (SQLException sqlEx) {
                    // empty catch block
                }
            }
            pStmt = null;
            throw var10_13;
        }
    }

    @Override
    public int setBytes(long writeAt, byte[] bytes) throws SQLException {
        return this.setBytes((long)writeAt, (byte[])bytes, (int)0, (int)bytes.length);
    }

    /*
     * Exception decompiling
     */
    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 3[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Exception decompiling
     */
    @Override
    public long length() throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 6[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        return this.position((byte[])pattern.getBytes((long)0L, (int)((int)pattern.length())), (long)start);
    }

    /*
     * Exception decompiling
     */
    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 6[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public void truncate(long length) throws SQLException {
        pStmt = null;
        query = new StringBuilder((String)"UPDATE ");
        query.append((String)this.tableName);
        query.append((String)" SET ");
        query.append((String)this.blobColumnName);
        query.append((String)" = LEFT(");
        query.append((String)this.blobColumnName);
        query.append((String)", ");
        query.append((long)length);
        query.append((String)") WHERE ");
        query.append((String)this.primaryKeyColumns.get((int)0));
        query.append((String)" = ?");
        for (i = 1; i < this.numPrimaryKeys; ++i) {
            query.append((String)" AND ");
            query.append((String)this.primaryKeyColumns.get((int)i));
            query.append((String)" = ?");
        }
        try {
            pStmt = this.creatorResultSet.connection.prepareStatement((String)query.toString());
            for (i = 0; i < this.numPrimaryKeys; ++i) {
                pStmt.setString((int)(i + 1), (String)this.primaryKeyValues.get((int)i));
            }
            rowsUpdated = pStmt.executeUpdate();
            if (rowsUpdated != 1) {
                throw SQLError.createSQLException((String)"BLOB data not found! Did primary keys change?", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            }
            var7_5 = null;
            if (pStmt == null) return;
            try {
                pStmt.close();
                return;
            }
            catch (SQLException sqlEx) {
                // empty catch block
            }
            return;
        }
        catch (Throwable var6_9) {
            block8 : {
                var7_6 = null;
                if (pStmt == null) throw var6_9;
                ** try [egrp 1[TRYBLOCK] [2 : 271->280)] { 
lbl52: // 1 sources:
                pStmt.close();
                break block8;
lbl54: // 1 sources:
                catch (SQLException sqlEx) {
                    // empty catch block
                }
            }
            pStmt = null;
            throw var6_9;
        }
    }

    PreparedStatement createGetBytesStatement() throws SQLException {
        StringBuilder query = new StringBuilder((String)"SELECT SUBSTRING(");
        query.append((String)this.blobColumnName);
        query.append((String)", ");
        query.append((String)"?");
        query.append((String)", ");
        query.append((String)"?");
        query.append((String)") FROM ");
        query.append((String)this.tableName);
        query.append((String)" WHERE ");
        query.append((String)this.primaryKeyColumns.get((int)0));
        query.append((String)" = ?");
        int i = 1;
        while (i < this.numPrimaryKeys) {
            query.append((String)" AND ");
            query.append((String)this.primaryKeyColumns.get((int)i));
            query.append((String)" = ?");
            ++i;
        }
        return this.creatorResultSet.connection.prepareStatement((String)query.toString());
    }

    /*
     * Exception decompiling
     */
    byte[] getBytesInternal(PreparedStatement pStmt, long pos, int length) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 4[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void free() throws SQLException {
        this.creatorResultSet = null;
        this.primaryKeyColumns = null;
        this.primaryKeyValues = null;
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        return new LocatorInputStream((BlobFromLocator)this, (long)pos, (long)length);
    }

    static /* synthetic */ ExceptionInterceptor access$000(BlobFromLocator x0) {
        return x0.exceptionInterceptor;
    }
}

