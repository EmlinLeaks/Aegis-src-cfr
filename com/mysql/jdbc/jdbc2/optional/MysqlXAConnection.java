/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlXAException;
import com.mysql.jdbc.log.Log;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class MysqlXAConnection
extends MysqlPooledConnection
implements XAConnection,
XAResource {
    private static final int MAX_COMMAND_LENGTH = 300;
    private Connection underlyingConnection;
    private static final Map<Integer, Integer> MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
    private Log log;
    protected boolean logXaCommands;
    private static final Constructor<?> JDBC_4_XA_CONNECTION_WRAPPER_CTOR;

    protected static MysqlXAConnection getInstance(Connection mysqlConnection, boolean logXaCommands) throws SQLException {
        if (Util.isJdbc4()) return (MysqlXAConnection)Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, (Object[])new Object[]{mysqlConnection, Boolean.valueOf((boolean)logXaCommands)}, (ExceptionInterceptor)mysqlConnection.getExceptionInterceptor());
        return new MysqlXAConnection((Connection)mysqlConnection, (boolean)logXaCommands);
    }

    public MysqlXAConnection(Connection connection, boolean logXaCommands) throws SQLException {
        super((Connection)connection);
        this.underlyingConnection = connection;
        this.log = connection.getLog();
        this.logXaCommands = logXaCommands;
    }

    @Override
    public XAResource getXAResource() throws SQLException {
        return this;
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    @Override
    public boolean setTransactionTimeout(int arg0) throws XAException {
        return false;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        if (!(xares instanceof MysqlXAConnection)) return false;
        return this.underlyingConnection.isSameResource((Connection)((MysqlXAConnection)xares).underlyingConnection);
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return MysqlXAConnection.recover((java.sql.Connection)this.underlyingConnection, (int)flag);
    }

    /*
     * Exception decompiling
     */
    protected static Xid[] recover(java.sql.Connection c, int flag) throws XAException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:404)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:482)
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
    public int prepare(Xid xid) throws XAException {
        StringBuilder commandBuf = new StringBuilder((int)300);
        commandBuf.append((String)"XA PREPARE ");
        MysqlXAConnection.appendXid((StringBuilder)commandBuf, (Xid)xid);
        this.dispatchCommand((String)commandBuf.toString());
        return 0;
    }

    @Override
    public void forget(Xid xid) throws XAException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void rollback(Xid xid) throws XAException {
        StringBuilder commandBuf = new StringBuilder((int)300);
        commandBuf.append((String)"XA ROLLBACK ");
        MysqlXAConnection.appendXid((StringBuilder)commandBuf, (Xid)xid);
        try {
            this.dispatchCommand((String)commandBuf.toString());
            Object var4_3 = null;
            this.underlyingConnection.setInGlobalTx((boolean)false);
            return;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            this.underlyingConnection.setInGlobalTx((boolean)false);
            throw throwable;
        }
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void end(Xid xid, int flags) throws XAException {
        commandBuf = new StringBuilder((int)300);
        commandBuf.append((String)"XA END ");
        MysqlXAConnection.appendXid((StringBuilder)commandBuf, (Xid)xid);
        switch (flags) {
            case 67108864: {
                ** break;
            }
            case 33554432: {
                commandBuf.append((String)" SUSPEND");
                ** break;
            }
            case 536870912: {
                ** break;
            }
        }
        throw new XAException((int)-5);
lbl15: // 3 sources:
        this.dispatchCommand((String)commandBuf.toString());
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void start(Xid xid, int flags) throws XAException {
        commandBuf = new StringBuilder((int)300);
        commandBuf.append((String)"XA START ");
        MysqlXAConnection.appendXid((StringBuilder)commandBuf, (Xid)xid);
        switch (flags) {
            case 2097152: {
                commandBuf.append((String)" JOIN");
                ** break;
            }
            case 134217728: {
                commandBuf.append((String)" RESUME");
                ** break;
            }
            case 0: {
                ** break;
            }
        }
        throw new XAException((int)-5);
lbl17: // 3 sources:
        this.dispatchCommand((String)commandBuf.toString());
        this.underlyingConnection.setInGlobalTx((boolean)true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        StringBuilder commandBuf = new StringBuilder((int)300);
        commandBuf.append((String)"XA COMMIT ");
        MysqlXAConnection.appendXid((StringBuilder)commandBuf, (Xid)xid);
        if (onePhase) {
            commandBuf.append((String)" ONE PHASE");
        }
        try {
            this.dispatchCommand((String)commandBuf.toString());
            Object var5_4 = null;
            this.underlyingConnection.setInGlobalTx((boolean)false);
            return;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            this.underlyingConnection.setInGlobalTx((boolean)false);
            throw throwable;
        }
    }

    /*
     * Exception decompiling
     */
    private ResultSet dispatchCommand(String command) throws XAException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 5[CATCHBLOCK]
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

    protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
        Integer xaCode = MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get((Object)Integer.valueOf((int)sqlEx.getErrorCode()));
        if (xaCode == null) return (XAException)new MysqlXAException((int)-7, (String)Messages.getString((String)"MysqlXAConnection.003"), null).initCause((Throwable)sqlEx);
        return (XAException)new MysqlXAException((int)xaCode.intValue(), (String)sqlEx.getMessage(), null).initCause((Throwable)sqlEx);
    }

    private static void appendXid(StringBuilder builder, Xid xid) {
        byte[] gtrid = xid.getGlobalTransactionId();
        byte[] btrid = xid.getBranchQualifier();
        if (gtrid != null) {
            StringUtils.appendAsHex((StringBuilder)builder, (byte[])gtrid);
        }
        builder.append((char)',');
        if (btrid != null) {
            StringUtils.appendAsHex((StringBuilder)builder, (byte[])btrid);
        }
        builder.append((char)',');
        StringUtils.appendAsHex((StringBuilder)builder, (int)xid.getFormatId());
    }

    @Override
    public synchronized java.sql.Connection getConnection() throws SQLException {
        return this.getConnection((boolean)false, (boolean)true);
    }

    static {
        HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
        temp.put(Integer.valueOf((int)1397), Integer.valueOf((int)-4));
        temp.put(Integer.valueOf((int)1398), Integer.valueOf((int)-5));
        temp.put(Integer.valueOf((int)1399), Integer.valueOf((int)-7));
        temp.put(Integer.valueOf((int)1400), Integer.valueOf((int)-9));
        temp.put(Integer.valueOf((int)1401), Integer.valueOf((int)-3));
        temp.put(Integer.valueOf((int)1402), Integer.valueOf((int)100));
        temp.put(Integer.valueOf((int)1440), Integer.valueOf((int)-8));
        temp.put(Integer.valueOf((int)1613), Integer.valueOf((int)106));
        temp.put(Integer.valueOf((int)1614), Integer.valueOf((int)102));
        MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
        if (!Util.isJdbc4()) {
            JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
            return;
        }
        try {
            JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName((String)"com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection").getConstructor(Connection.class, Boolean.TYPE);
            return;
        }
        catch (SecurityException e) {
            throw new RuntimeException((Throwable)e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException((Throwable)e);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

