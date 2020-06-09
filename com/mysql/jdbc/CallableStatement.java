/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ByteArrayRow;
import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.DatabaseMetaDataUsingInfoSchema;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.Wrapper;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CallableStatement
extends PreparedStatement
implements java.sql.CallableStatement {
    protected static final Constructor<?> JDBC_4_CSTMT_2_ARGS_CTOR;
    protected static final Constructor<?> JDBC_4_CSTMT_4_ARGS_CTOR;
    private static final int NOT_OUTPUT_PARAMETER_INDICATOR = Integer.MIN_VALUE;
    private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
    protected boolean callingStoredFunction = false;
    private ResultSetInternalMethods functionReturnValueResults;
    private boolean hasOutputParams = false;
    private ResultSetInternalMethods outputParameterResults;
    protected boolean outputParamWasNull = false;
    private int[] parameterIndexToRsIndex;
    protected CallableStatementParamInfo paramInfo;
    private CallableStatementParam returnValueParam;
    private int[] placeholderToParameterIndexMap;

    private static String mangleParameterName(String origParameterName) {
        if (origParameterName == null) {
            return null;
        }
        int offset = 0;
        if (origParameterName.length() > 0 && origParameterName.charAt((int)0) == '@') {
            offset = 1;
        }
        StringBuilder paramNameBuf = new StringBuilder((int)(PARAMETER_NAMESPACE_PREFIX.length() + origParameterName.length()));
        paramNameBuf.append((String)PARAMETER_NAMESPACE_PREFIX);
        paramNameBuf.append((String)origParameterName.substring((int)offset));
        return paramNameBuf.toString();
    }

    public CallableStatement(MySQLConnection conn, CallableStatementParamInfo paramInfo) throws SQLException {
        super((MySQLConnection)conn, (String)paramInfo.nativeSql, (String)paramInfo.catalogInUse);
        this.paramInfo = paramInfo;
        this.callingStoredFunction = this.paramInfo.isFunctionCall;
        if (this.callingStoredFunction) {
            ++this.parameterCount;
        }
        this.retrieveGeneratedKeys = true;
    }

    protected static CallableStatement getInstance(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
        if (Util.isJdbc4()) return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_4_ARGS_CTOR, (Object[])new Object[]{conn, sql, catalog, Boolean.valueOf((boolean)isFunctionCall)}, (ExceptionInterceptor)conn.getExceptionInterceptor());
        return new CallableStatement((MySQLConnection)conn, (String)sql, (String)catalog, (boolean)isFunctionCall);
    }

    protected static CallableStatement getInstance(MySQLConnection conn, CallableStatementParamInfo paramInfo) throws SQLException {
        if (Util.isJdbc4()) return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_2_ARGS_CTOR, (Object[])new Object[]{conn, paramInfo}, (ExceptionInterceptor)conn.getExceptionInterceptor());
        return new CallableStatement((MySQLConnection)conn, (CallableStatementParamInfo)paramInfo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void generateParameterMap() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.paramInfo == null) {
            // MONITOREXIT : object
            return;
        }
        int parameterCountFromMetaData = this.paramInfo.getParameterCount();
        if (this.paramInfo != null && this.parameterCount != parameterCountFromMetaData) {
            int startPos;
            int parenOpenPos;
            int parenClosePos;
            this.placeholderToParameterIndexMap = new int[this.parameterCount];
            int startIndex = 0;
            if (this.callingStoredFunction) {
                this.placeholderToParameterIndexMap[0] = 0;
                startIndex = 1;
            }
            int n = startPos = this.callingStoredFunction ? StringUtils.indexOfIgnoreCase((String)this.originalSql, (String)"SELECT") : StringUtils.indexOfIgnoreCase((String)this.originalSql, (String)"CALL");
            if (startPos != -1 && (parenOpenPos = this.originalSql.indexOf((int)40, (int)(startPos + 4))) != -1 && (parenClosePos = StringUtils.indexOfIgnoreCase((int)parenOpenPos, (String)this.originalSql, (String)")", (String)"'", (String)"'", StringUtils.SEARCH_MODE__ALL)) != -1) {
                List<String> parsedParameters = StringUtils.split((String)this.originalSql.substring((int)(parenOpenPos + 1), (int)parenClosePos), (String)",", (String)"'\"", (String)"'\"", (boolean)true);
                int numParsedParameters = parsedParameters.size();
                int placeholderCount = startIndex;
                for (int i = 0; i < numParsedParameters; ++i) {
                    if (!parsedParameters.get((int)i).equals((Object)"?")) continue;
                    this.placeholderToParameterIndexMap[placeholderCount++] = startIndex + i;
                }
            }
        }
        // MONITOREXIT : object
        return;
    }

    public CallableStatement(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
        super((MySQLConnection)conn, (String)sql, (String)catalog);
        this.callingStoredFunction = isFunctionCall;
        if (!this.callingStoredFunction) {
            if (!StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"CALL")) {
                this.fakeParameterTypes((boolean)false);
            } else {
                this.determineParameterTypes();
            }
            this.generateParameterMap();
        } else {
            this.determineParameterTypes();
            ++this.parameterCount;
            this.generateParameterMap();
        }
        this.retrieveGeneratedKeys = true;
    }

    @Override
    public void addBatch() throws SQLException {
        this.setOutParams();
        super.addBatch();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CallableStatementParam checkIsOutputParam(int paramIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.callingStoredFunction && paramIndex == 1) {
            if (this.returnValueParam == null) {
                this.returnValueParam = new CallableStatementParam((String)"", (int)0, (boolean)false, (boolean)true, (int)12, (String)"VARCHAR", (int)0, (int)0, (short)2, (int)5);
            }
            // MONITOREXIT : object
            return this.returnValueParam;
        }
        this.checkParameterIndexBounds((int)paramIndex);
        int localParamIndex = paramIndex - 1;
        if (this.placeholderToParameterIndexMap != null) {
            localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
        }
        CallableStatementParam paramDescriptor = this.paramInfo.getParameter((int)localParamIndex);
        if (this.connection.getNoAccessToProcedureBodies()) {
            paramDescriptor.isOut = true;
            paramDescriptor.isIn = true;
            paramDescriptor.inOutModifier = 2;
        } else if (!paramDescriptor.isOut) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"CallableStatement.9") + paramIndex + Messages.getString((String)"CallableStatement.10")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.hasOutputParams = true;
        // MONITOREXIT : object
        return paramDescriptor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkParameterIndexBounds(int paramIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.paramInfo.checkBounds((int)paramIndex);
        // MONITOREXIT : object
        return;
    }

    private void checkStreamability() throws SQLException {
        if (!this.hasOutputParams) return;
        if (!this.createStreamingResultSet()) return;
        throw SQLError.createSQLException((String)Messages.getString((String)"CallableStatement.14"), (String)"S1C00", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearParameters() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        super.clearParameters();
        try {
            if (this.outputParameterResults != null) {
                this.outputParameterResults.close();
            }
            Object var3_2 = null;
            this.outputParameterResults = null;
            return;
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            this.outputParameterResults = null;
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fakeParameterTypes(boolean isReallyProcedure) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        Field[] fields = new Field[]{new Field((String)"", (String)"PROCEDURE_CAT", (int)1, (int)0), new Field((String)"", (String)"PROCEDURE_SCHEM", (int)1, (int)0), new Field((String)"", (String)"PROCEDURE_NAME", (int)1, (int)0), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)0), new Field((String)"", (String)"COLUMN_TYPE", (int)1, (int)0), new Field((String)"", (String)"DATA_TYPE", (int)5, (int)0), new Field((String)"", (String)"TYPE_NAME", (int)1, (int)0), new Field((String)"", (String)"PRECISION", (int)4, (int)0), new Field((String)"", (String)"LENGTH", (int)4, (int)0), new Field((String)"", (String)"SCALE", (int)5, (int)0), new Field((String)"", (String)"RADIX", (int)5, (int)0), new Field((String)"", (String)"NULLABLE", (int)5, (int)0), new Field((String)"", (String)"REMARKS", (int)1, (int)0)};
        String procName = isReallyProcedure ? this.extractProcedureName() : null;
        byte[] procNameAsBytes = null;
        try {
            procNameAsBytes = procName == null ? null : StringUtils.getBytes((String)procName, (String)"UTF-8");
        }
        catch (UnsupportedEncodingException ueEx) {
            procNameAsBytes = StringUtils.s2b((String)procName, (MySQLConnection)this.connection);
        }
        ArrayList<ResultSetRow> resultRows = new ArrayList<ResultSetRow>();
        int numOfParameters = this.callingStoredFunction ? this.parameterCount + 1 : this.parameterCount;
        int i = 0;
        do {
            if (i >= numOfParameters) {
                ResultSet paramTypesRs = DatabaseMetaData.buildResultSet((Field[])fields, resultRows, (MySQLConnection)this.connection);
                this.convertGetProcedureColumnsToInternalDescriptors((ResultSet)paramTypesRs);
                // MONITOREXIT : object
                return;
            }
            byte[][] row = new byte[][]{null, null, procNameAsBytes, StringUtils.s2b((String)String.valueOf((int)i), (MySQLConnection)this.connection), this.callingStoredFunction && i == 0 ? StringUtils.s2b((String)String.valueOf((int)4), (MySQLConnection)this.connection) : StringUtils.s2b((String)String.valueOf((int)1), (MySQLConnection)this.connection), StringUtils.s2b((String)String.valueOf((int)12), (MySQLConnection)this.connection), StringUtils.s2b((String)"VARCHAR", (MySQLConnection)this.connection), StringUtils.s2b((String)Integer.toString((int)65535), (MySQLConnection)this.connection), StringUtils.s2b((String)Integer.toString((int)65535), (MySQLConnection)this.connection), StringUtils.s2b((String)Integer.toString((int)0), (MySQLConnection)this.connection), StringUtils.s2b((String)Integer.toString((int)10), (MySQLConnection)this.connection), StringUtils.s2b((String)Integer.toString((int)2), (MySQLConnection)this.connection), null};
            resultRows.add((ResultSetRow)new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.getExceptionInterceptor()));
            ++i;
        } while (true);
    }

    /*
     * Exception decompiling
     */
    private void determineParameterTypes() throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [4[TRYBLOCK]], but top level block is 9[CATCHBLOCK]
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
     */
    protected ResultSet getParamTypes(String catalog, String routineName) throws SQLException {
        ResultSet resultSet;
        boolean getProcRetFuncsCurrentValue = this.connection.getGetProceduresReturnsFunctions();
        try {
            this.connection.setGetProceduresReturnsFunctions((boolean)this.callingStoredFunction);
            resultSet = this.connection.getMetaData().getProcedureColumns((String)catalog, null, (String)routineName, (String)"%");
            Object var6_5 = null;
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            this.connection.setGetProceduresReturnsFunctions((boolean)getProcRetFuncsCurrentValue);
            throw throwable;
        }
        this.connection.setGetProceduresReturnsFunctions((boolean)getProcRetFuncsCurrentValue);
        return resultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void convertGetProcedureColumnsToInternalDescriptors(ResultSet paramTypesRs) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.paramInfo = new CallableStatementParamInfo((CallableStatement)this, (ResultSet)paramTypesRs);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean execute() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        boolean returnVal = false;
        this.checkStreamability();
        this.setInOutParamsOnServer();
        this.setOutParams();
        returnVal = super.execute();
        if (this.callingStoredFunction) {
            this.functionReturnValueResults = this.results;
            this.functionReturnValueResults.next();
            this.results = null;
        }
        this.retrieveOutParams();
        if (!this.callingStoredFunction) {
            // MONITOREXIT : object
            return returnVal;
        }
        // MONITOREXIT : object
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet executeQuery() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.checkStreamability();
        ResultSet execResults = null;
        this.setInOutParamsOnServer();
        this.setOutParams();
        execResults = super.executeQuery();
        this.retrieveOutParams();
        // MONITOREXIT : object
        return execResults;
    }

    @Override
    public int executeUpdate() throws SQLException {
        return Util.truncateAndConvertToInt((long)this.executeLargeUpdate());
    }

    private String extractProcedureName() throws SQLException {
        String sanitizedSql = StringUtils.stripComments((String)this.originalSql, (String)"`\"'", (String)"`\"'", (boolean)true, (boolean)false, (boolean)true, (boolean)true);
        int endCallIndex = StringUtils.indexOfIgnoreCase((String)sanitizedSql, (String)"CALL ");
        int offset = 5;
        if (endCallIndex == -1) {
            endCallIndex = StringUtils.indexOfIgnoreCase((String)sanitizedSql, (String)"SELECT ");
            offset = 7;
        }
        if (endCallIndex == -1) throw SQLError.createSQLException((String)Messages.getString((String)"CallableStatement.1"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        StringBuilder nameBuf = new StringBuilder();
        String trimmedStatement = sanitizedSql.substring((int)(endCallIndex + offset)).trim();
        int statementLength = trimmedStatement.length();
        int i = 0;
        while (i < statementLength) {
            char c = trimmedStatement.charAt((int)i);
            if (Character.isWhitespace((char)c)) return nameBuf.toString();
            if (c == '(') return nameBuf.toString();
            if (c == '?') {
                return nameBuf.toString();
            }
            nameBuf.append((char)c);
            ++i;
        }
        return nameBuf.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String fixParameterName(String paramNameIn) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!(paramNameIn != null && paramNameIn.length() != 0 || this.hasParametersView())) {
            String string;
            if (Messages.getString((String)"CallableStatement.0") + paramNameIn == null) {
                string = Messages.getString((String)"CallableStatement.15");
                throw SQLError.createSQLException((String)string, (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            string = Messages.getString((String)"CallableStatement.16");
            throw SQLError.createSQLException((String)string, (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (paramNameIn == null && this.hasParametersView()) {
            paramNameIn = "nullpn";
        }
        if (this.connection.getNoAccessToProcedureBodies()) {
            throw SQLError.createSQLException((String)"No access to parameters by name when connection has been configured not to access procedure bodies", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        // MONITOREXIT : object
        return CallableStatement.mangleParameterName((String)paramNameIn);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Array getArray(int i) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)i);
        Array retValue = rs.getArray((int)this.mapOutputParameterIndexToRsIndex((int)i));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Array getArray(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Array retValue = rs.getArray((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        BigDecimal retValue = rs.getBigDecimal((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        BigDecimal retValue = rs.getBigDecimal((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex), (int)scale);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        BigDecimal retValue = rs.getBigDecimal((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Blob retValue = rs.getBlob((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Blob retValue = rs.getBlob((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        boolean retValue = rs.getBoolean((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        boolean retValue = rs.getBoolean((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        byte retValue = rs.getByte((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte getByte(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        byte retValue = rs.getByte((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        byte[] retValue = rs.getBytes((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        byte[] retValue = rs.getBytes((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Clob retValue = rs.getClob((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Clob getClob(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Clob retValue = rs.getClob((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Date getDate(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Date retValue = rs.getDate((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Date retValue = rs.getDate((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex), (Calendar)cal);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Date getDate(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Date retValue = rs.getDate((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Date retValue = rs.getDate((String)this.fixParameterName((String)parameterName), (Calendar)cal);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        double retValue = rs.getDouble((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double getDouble(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        double retValue = rs.getDouble((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        float retValue = rs.getFloat((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float getFloat(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        float retValue = rs.getFloat((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getInt(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        int retValue = rs.getInt((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getInt(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        int retValue = rs.getInt((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getLong(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        long retValue = rs.getLong((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getLong(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        long retValue = rs.getLong((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    protected int getNamedParamIndex(String paramName, boolean forOut) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.connection.getNoAccessToProcedureBodies()) {
            throw SQLError.createSQLException((String)"No access to parameters by name when connection has been configured not to access procedure bodies", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (paramName == null) throw SQLError.createSQLException((String)Messages.getString((String)"CallableStatement.2"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (paramName.length() == 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"CallableStatement.2"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.paramInfo == null) throw SQLError.createSQLException((String)(Messages.getString((String)"CallableStatement.3") + paramName + Messages.getString((String)"CallableStatement.4")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        CallableStatementParam namedParamInfo = this.paramInfo.getParameter((String)paramName);
        if (namedParamInfo == null) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"CallableStatement.3") + paramName + Messages.getString((String)"CallableStatement.4")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (forOut && !namedParamInfo.isOut) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"CallableStatement.5") + paramName + Messages.getString((String)"CallableStatement.6")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.placeholderToParameterIndexMap == null) {
            // MONITOREXIT : object
            return namedParamInfo.index + 1;
        }
        int i = 0;
        while (i < this.placeholderToParameterIndexMap.length) {
            if (this.placeholderToParameterIndexMap[i] == namedParamInfo.index) {
                // MONITOREXIT : object
                return i + 1;
            }
            ++i;
        }
        throw SQLError.createSQLException((String)("Can't find local placeholder mapping for parameter named \"" + paramName + "\"."), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        CallableStatementParam paramDescriptor = this.checkIsOutputParam((int)parameterIndex);
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Object retVal = rs.getObjectStoredProc((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex), (int)paramDescriptor.desiredJdbcType);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retVal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Object retVal = rs.getObject((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex), map);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retVal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getObject(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Object retValue = rs.getObject((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Object retValue = rs.getObject((String)this.fixParameterName((String)parameterName), map);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        T retVal = ((ResultSetImpl)rs).getObject((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex), type);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return (T)retVal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        T retValue = ((ResultSetImpl)rs).getObject((String)this.fixParameterName((String)parameterName), type);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return (T)retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ResultSetInternalMethods getOutputParameters(int paramIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.outputParamWasNull = false;
        if (paramIndex == 1 && this.callingStoredFunction && this.returnValueParam != null) {
            // MONITOREXIT : object
            return this.functionReturnValueResults;
        }
        if (this.outputParameterResults == null) {
            if (this.paramInfo.numberOfParameters() != 0) throw SQLError.createSQLException((String)Messages.getString((String)"CallableStatement.8"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            throw SQLError.createSQLException((String)Messages.getString((String)"CallableStatement.7"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        // MONITOREXIT : object
        return this.outputParameterResults;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.placeholderToParameterIndexMap == null) {
            // MONITOREXIT : object
            return this.paramInfo;
        }
        // MONITOREXIT : object
        return new CallableStatementParamInfo((CallableStatement)this, (CallableStatementParamInfo)this.paramInfo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Ref retValue = rs.getRef((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Ref getRef(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Ref retValue = rs.getRef((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short getShort(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        short retValue = rs.getShort((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short getShort(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        short retValue = rs.getShort((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getString(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        String retValue = rs.getString((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getString(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        String retValue = rs.getString((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Time getTime(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Time retValue = rs.getTime((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Time retValue = rs.getTime((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex), (Calendar)cal);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Time getTime(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Time retValue = rs.getTime((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Time retValue = rs.getTime((String)this.fixParameterName((String)parameterName), (Calendar)cal);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Timestamp retValue = rs.getTimestamp((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Timestamp retValue = rs.getTimestamp((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex), (Calendar)cal);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Timestamp getTimestamp(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Timestamp retValue = rs.getTimestamp((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Timestamp retValue = rs.getTimestamp((String)this.fixParameterName((String)parameterName), (Calendar)cal);
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        URL retValue = rs.getURL((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public URL getURL(String parameterName) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        URL retValue = rs.getURL((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        // MONITOREXIT : object
        return retValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected int mapOutputParameterIndexToRsIndex(int paramIndex) throws SQLException {
        int rsIndex;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.returnValueParam != null && paramIndex == 1) {
            // MONITOREXIT : object
            return 1;
        }
        this.checkParameterIndexBounds((int)paramIndex);
        int localParamIndex = paramIndex - 1;
        if (this.placeholderToParameterIndexMap != null) {
            localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
        }
        if ((rsIndex = this.parameterIndexToRsIndex[localParamIndex]) == Integer.MIN_VALUE) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"CallableStatement.21") + paramIndex + Messages.getString((String)"CallableStatement.22")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        // MONITOREXIT : object
        return rsIndex + 1;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        CallableStatementParam paramDescriptor = this.checkIsOutputParam((int)parameterIndex);
        paramDescriptor.desiredJdbcType = sqlType;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        this.registerOutParameter((int)parameterIndex, (int)sqlType);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        this.checkIsOutputParam((int)parameterIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.registerOutParameter((int)this.getNamedParamIndex((String)parameterName, (boolean)true), (int)sqlType);
        // MONITOREXIT : object
        return;
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        this.registerOutParameter((int)this.getNamedParamIndex((String)parameterName, (boolean)true), (int)sqlType);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        this.registerOutParameter((int)this.getNamedParamIndex((String)parameterName, (boolean)true), (int)sqlType, (String)typeName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void retrieveOutParams() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        int numParameters = this.paramInfo.numberOfParameters();
        this.parameterIndexToRsIndex = new int[numParameters];
        for (int i = 0; i < numParameters; ++i) {
            this.parameterIndexToRsIndex[i] = Integer.MIN_VALUE;
        }
        int localParamIndex = 0;
        if (numParameters <= 0) {
            this.outputParameterResults = null;
            return;
        }
        StringBuilder outParameterQuery = new StringBuilder((String)"SELECT ");
        boolean firstParam = true;
        boolean hadOutputParams = false;
        Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator();
        while (paramIter.hasNext()) {
            CallableStatementParam retrParamInfo = paramIter.next();
            if (!retrParamInfo.isOut) continue;
            hadOutputParams = true;
            this.parameterIndexToRsIndex[retrParamInfo.index] = localParamIndex++;
            if (retrParamInfo.paramName == null && this.hasParametersView()) {
                retrParamInfo.paramName = "nullnp" + retrParamInfo.index;
            }
            String outParameterName = CallableStatement.mangleParameterName((String)retrParamInfo.paramName);
            if (!firstParam) {
                outParameterQuery.append((String)",");
            } else {
                firstParam = false;
            }
            if (!outParameterName.startsWith((String)"@")) {
                outParameterQuery.append((char)'@');
            }
            outParameterQuery.append((String)outParameterName);
        }
        if (!hadOutputParams) {
            this.outputParameterResults = null;
            return;
        }
        Statement outParameterStmt = null;
        ResultSet outParamRs = null;
        try {
            outParameterStmt = this.connection.createStatement();
            outParamRs = outParameterStmt.executeQuery((String)outParameterQuery.toString());
            this.outputParameterResults = ((ResultSetInternalMethods)outParamRs).copy();
            if (!this.outputParameterResults.next()) {
                this.outputParameterResults.close();
                this.outputParameterResults = null;
            }
            Object var11_10 = null;
            if (outParameterStmt == null) return;
            {
                outParameterStmt.close();
                return;
            }
        }
        catch (Throwable throwable) {
            Object var11_11 = null;
            if (outParameterStmt == null) throw throwable;
            outParameterStmt.close();
            throw throwable;
        }
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        this.setAsciiStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (InputStream)x, (int)length);
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        this.setBigDecimal((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (BigDecimal)x);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        this.setBinaryStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (InputStream)x, (int)length);
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        this.setBoolean((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (boolean)x);
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        this.setByte((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (byte)x);
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        this.setBytes((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (byte[])x);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        this.setCharacterStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)reader, (int)length);
    }

    @Override
    public void setDate(String parameterName, Date x) throws SQLException {
        this.setDate((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Date)x);
    }

    @Override
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        this.setDate((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Date)x, (Calendar)cal);
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        this.setDouble((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (double)x);
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        this.setFloat((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (float)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void setInOutParamsOnServer() throws SQLException {
        var1_1 = this.checkClosed().getConnectionMutex();
        // MONITORENTER : var1_1
        if (this.paramInfo.numParameters > 0) {
            paramIter = this.paramInfo.iterator();
            while (paramIter.hasNext()) {
                inParamInfo = paramIter.next();
                if (!inParamInfo.isOut || !inParamInfo.isIn) continue;
                if (inParamInfo.paramName == null && this.hasParametersView()) {
                    inParamInfo.paramName = "nullnp" + inParamInfo.index;
                }
                inOutParameterName = CallableStatement.mangleParameterName((String)inParamInfo.paramName);
                queryBuf = new StringBuilder((int)(4 + inOutParameterName.length() + 1 + 1));
                queryBuf.append((String)"SET ");
                queryBuf.append((String)inOutParameterName);
                queryBuf.append((String)"=?");
                setPstmt = null;
                try {
                    setPstmt = ((Wrapper)this.connection.clientPrepareStatement((String)queryBuf.toString())).unwrap(PreparedStatement.class);
                    if (this.isNull[inParamInfo.index]) {
                        setPstmt.setBytesNoEscapeNoQuotes((int)1, (byte[])"NULL".getBytes());
                    } else {
                        parameterAsBytes = this.getBytesRepresentation((int)inParamInfo.index);
                        if (parameterAsBytes != null) {
                            if (parameterAsBytes.length > 8 && parameterAsBytes[0] == 95 && parameterAsBytes[1] == 98 && parameterAsBytes[2] == 105 && parameterAsBytes[3] == 110 && parameterAsBytes[4] == 97 && parameterAsBytes[5] == 114 && parameterAsBytes[6] == 121 && parameterAsBytes[7] == 39) {
                                setPstmt.setBytesNoEscapeNoQuotes((int)1, (byte[])parameterAsBytes);
                            } else {
                                sqlType = inParamInfo.desiredJdbcType;
                                switch (sqlType) {
                                    case -7: 
                                    case -4: 
                                    case -3: 
                                    case -2: 
                                    case 2000: 
                                    case 2004: {
                                        setPstmt.setBytes((int)1, (byte[])parameterAsBytes);
                                        ** break;
                                    }
                                }
                                setPstmt.setBytesNoEscape((int)1, (byte[])parameterAsBytes);
                                ** break;
                            }
lbl36: // 2 sources:
                        } else {
                            setPstmt.setNull((int)1, (int)0);
                        }
                    }
                    setPstmt.executeUpdate();
                    var10_9 = null;
                    if (setPstmt == null) continue;
                    setPstmt.close();
                }
                catch (Throwable var9_10) {
                    var10_9 = null;
                    if (setPstmt == null) throw var9_10;
                    setPstmt.close();
                    throw var9_10;
                }
            }
        }
        // MONITOREXIT : var1_1
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        this.setInt((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (int)x);
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        this.setLong((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (long)x);
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        this.setNull((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (int)sqlType);
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        this.setNull((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (int)sqlType, (String)typeName);
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        this.setObject((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Object)x);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        this.setObject((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Object)x, (int)targetSqlType);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    private void setOutParams() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.paramInfo.numParameters > 0) {
            Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator();
            while (paramIter.hasNext()) {
                CallableStatementParam outParamInfo = paramIter.next();
                if (this.callingStoredFunction || !outParamInfo.isOut) continue;
                if (outParamInfo.paramName == null && this.hasParametersView()) {
                    outParamInfo.paramName = "nullnp" + outParamInfo.index;
                }
                String outParameterName = CallableStatement.mangleParameterName((String)outParamInfo.paramName);
                int outParamIndex = 0;
                if (this.placeholderToParameterIndexMap == null) {
                    outParamIndex = outParamInfo.index + 1;
                } else {
                    boolean found = false;
                    for (int i = 0; i < this.placeholderToParameterIndexMap.length; ++i) {
                        if (this.placeholderToParameterIndexMap[i] != outParamInfo.index) continue;
                        outParamIndex = i + 1;
                        found = true;
                        break;
                    }
                    if (!found) {
                        throw SQLError.createSQLException((String)(Messages.getString((String)"CallableStatement.21") + outParamInfo.paramName + Messages.getString((String)"CallableStatement.22")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                }
                this.setBytesNoEscapeNoQuotes((int)outParamIndex, (byte[])StringUtils.getBytes((String)outParameterName, (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor()));
            }
        }
        // MONITOREXIT : object
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        this.setShort((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (short)x);
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        this.setString((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (String)x);
    }

    @Override
    public void setTime(String parameterName, Time x) throws SQLException {
        this.setTime((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Time)x);
    }

    @Override
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        this.setTime((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Time)x, (Calendar)cal);
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        this.setTimestamp((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Timestamp)x);
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        this.setTimestamp((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Timestamp)x, (Calendar)cal);
    }

    @Override
    public void setURL(String parameterName, URL val) throws SQLException {
        this.setURL((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (URL)val);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean wasNull() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.outputParamWasNull;
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return Util.truncateAndConvertToInt((long[])this.executeLargeBatch());
    }

    @Override
    protected int getParameterIndexOffset() {
        if (!this.callingStoredFunction) return super.getParameterIndexOffset();
        return -1;
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        this.setAsciiStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (InputStream)x);
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        this.setAsciiStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (InputStream)x, (long)length);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        this.setBinaryStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (InputStream)x);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        this.setBinaryStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (InputStream)x, (long)length);
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        this.setBlob((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Blob)x);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        this.setBlob((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (InputStream)inputStream);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        this.setBlob((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (InputStream)inputStream, (long)length);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        this.setCharacterStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)reader);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        this.setCharacterStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)reader, (long)length);
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        this.setClob((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Clob)x);
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        this.setClob((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)reader);
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        this.setClob((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)reader, (long)length);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        this.setNCharacterStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)value);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        this.setNCharacterStream((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)value, (long)length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled unnecessary exception pruning
     */
    private boolean checkReadOnlyProcedure() throws SQLException {
        block20 : {
            Statement ps;
            ResultSet rs;
            block18 : {
                boolean bl;
                block19 : {
                    String sqlDataAccess;
                    Object object = this.checkClosed().getConnectionMutex();
                    // MONITORENTER : object
                    if (this.connection.getNoAccessToProcedureBodies()) {
                        // MONITOREXIT : object
                        return false;
                    }
                    if (this.paramInfo.isReadOnlySafeChecked) {
                        // MONITOREXIT : object
                        return this.paramInfo.isReadOnlySafeProcedure;
                    }
                    rs = null;
                    ps = null;
                    String procName = this.extractProcedureName();
                    String catalog = this.currentCatalog;
                    if (procName.indexOf((String)".") != -1) {
                        catalog = procName.substring((int)0, (int)procName.indexOf((String)"."));
                        if (StringUtils.startsWithIgnoreCaseAndWs((String)catalog, (String)"`") && catalog.trim().endsWith((String)"`")) {
                            catalog = catalog.substring((int)1, (int)(catalog.length() - 1));
                        }
                        procName = procName.substring((int)(procName.indexOf((String)".") + 1));
                        procName = StringUtils.toString((byte[])StringUtils.stripEnclosure((byte[])StringUtils.getBytes((String)procName), (String)"`", (String)"`"));
                    }
                    ps = this.connection.prepareStatement((String)"SELECT SQL_DATA_ACCESS FROM information_schema.routines WHERE routine_schema = ? AND routine_name = ?");
                    ps.setMaxRows((int)0);
                    ps.setFetchSize((int)0);
                    ps.setString((int)1, (String)catalog);
                    ps.setString((int)2, (String)procName);
                    rs = ps.executeQuery();
                    if (!rs.next() || !"READS SQL DATA".equalsIgnoreCase((String)(sqlDataAccess = rs.getString((int)1))) && !"NO SQL".equalsIgnoreCase((String)sqlDataAccess)) break block18;
                    CallableStatementParamInfo callableStatementParamInfo = this.paramInfo;
                    // MONITORENTER : callableStatementParamInfo
                    this.paramInfo.isReadOnlySafeChecked = true;
                    this.paramInfo.isReadOnlySafeProcedure = true;
                    // MONITOREXIT : callableStatementParamInfo
                    bl = true;
                    Object var10_10 = null;
                    if (rs == null) break block19;
                    rs.close();
                }
                if (ps == null) return bl;
                ps.close();
                return bl;
            }
            try {
                Object var10_11 = null;
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                break block20;
                catch (SQLException e) {
                    Object var10_12 = null;
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                if (rs != null) {
                    rs.close();
                }
                if (ps == null) throw throwable;
                ps.close();
                throw throwable;
            }
        }
        this.paramInfo.isReadOnlySafeChecked = false;
        this.paramInfo.isReadOnlySafeProcedure = false;
        // MONITOREXIT : object
        return false;
    }

    @Override
    protected boolean checkReadOnlySafeStatement() throws SQLException {
        if (super.checkReadOnlySafeStatement()) return true;
        if (this.checkReadOnlyProcedure()) return true;
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean hasParametersView() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        try {
            if (this.connection.versionMeetsMinimum((int)5, (int)5, (int)0)) {
                DatabaseMetaDataUsingInfoSchema dbmd1 = new DatabaseMetaDataUsingInfoSchema((MySQLConnection)this.connection, (String)this.connection.getCatalog());
                // MONITOREXIT : object
                return dbmd1.gethasParametersView();
            }
            // MONITOREXIT : object
            return false;
        }
        catch (SQLException e) {
            // MONITOREXIT : object
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long executeLargeUpdate() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        long returnVal = -1L;
        this.checkStreamability();
        if (this.callingStoredFunction) {
            this.execute();
            // MONITOREXIT : object
            return -1L;
        }
        this.setInOutParamsOnServer();
        this.setOutParams();
        returnVal = super.executeLargeUpdate();
        this.retrieveOutParams();
        // MONITOREXIT : object
        return returnVal;
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        if (!this.hasOutputParams) return super.executeLargeBatch();
        throw SQLError.createSQLException((String)"Can't call executeBatch() on CallableStatement with OUTPUT parameters", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    static /* synthetic */ int[] access$000(CallableStatement x0) {
        return x0.placeholderToParameterIndexMap;
    }

    static {
        if (!Util.isJdbc4()) {
            JDBC_4_CSTMT_4_ARGS_CTOR = null;
            JDBC_4_CSTMT_2_ARGS_CTOR = null;
            return;
        }
        try {
            String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42CallableStatement" : "com.mysql.jdbc.JDBC4CallableStatement";
            JDBC_4_CSTMT_2_ARGS_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(MySQLConnection.class, CallableStatementParamInfo.class);
            JDBC_4_CSTMT_4_ARGS_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(MySQLConnection.class, String.class, String.class, Boolean.TYPE);
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

