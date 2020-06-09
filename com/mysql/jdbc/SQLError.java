/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlErrorNumbers;
import com.mysql.jdbc.NotImplemented;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.exceptions.MySQLDataException;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.mysql.jdbc.exceptions.MySQLQueryInterruptedException;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
import com.mysql.jdbc.exceptions.MySQLTransientConnectionException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.BindException;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SQLError {
    static final int ER_WARNING_NOT_COMPLETE_ROLLBACK = 1196;
    private static Map<Integer, String> mysqlToSql99State;
    private static Map<Integer, String> mysqlToSqlState;
    public static final String SQL_STATE_WARNING = "01000";
    public static final String SQL_STATE_DISCONNECT_ERROR = "01002";
    public static final String SQL_STATE_DATA_TRUNCATED = "01004";
    public static final String SQL_STATE_PRIVILEGE_NOT_REVOKED = "01006";
    public static final String SQL_STATE_NO_DATA = "02000";
    public static final String SQL_STATE_WRONG_NO_OF_PARAMETERS = "07001";
    public static final String SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE = "08001";
    public static final String SQL_STATE_CONNECTION_IN_USE = "08002";
    public static final String SQL_STATE_CONNECTION_NOT_OPEN = "08003";
    public static final String SQL_STATE_CONNECTION_REJECTED = "08004";
    public static final String SQL_STATE_CONNECTION_FAILURE = "08006";
    public static final String SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN = "08007";
    public static final String SQL_STATE_COMMUNICATION_LINK_FAILURE = "08S01";
    public static final String SQL_STATE_FEATURE_NOT_SUPPORTED = "0A000";
    public static final String SQL_STATE_CARDINALITY_VIOLATION = "21000";
    public static final String SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST = "21S01";
    public static final String SQL_STATE_STRING_DATA_RIGHT_TRUNCATION = "22001";
    public static final String SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE = "22003";
    public static final String SQL_STATE_INVALID_DATETIME_FORMAT = "22007";
    public static final String SQL_STATE_DATETIME_FIELD_OVERFLOW = "22008";
    public static final String SQL_STATE_DIVISION_BY_ZERO = "22012";
    public static final String SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST = "22018";
    public static final String SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION = "23000";
    public static final String SQL_STATE_INVALID_CURSOR_STATE = "24000";
    public static final String SQL_STATE_INVALID_TRANSACTION_STATE = "25000";
    public static final String SQL_STATE_INVALID_AUTH_SPEC = "28000";
    public static final String SQL_STATE_INVALID_TRANSACTION_TERMINATION = "2D000";
    public static final String SQL_STATE_INVALID_CONDITION_NUMBER = "35000";
    public static final String SQL_STATE_INVALID_CATALOG_NAME = "3D000";
    public static final String SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE = "40001";
    public static final String SQL_STATE_SYNTAX_ERROR = "42000";
    public static final String SQL_STATE_ER_TABLE_EXISTS_ERROR = "42S01";
    public static final String SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND = "42S02";
    public static final String SQL_STATE_ER_NO_SUCH_INDEX = "42S12";
    public static final String SQL_STATE_ER_DUP_FIELDNAME = "42S21";
    public static final String SQL_STATE_ER_BAD_FIELD_ERROR = "42S22";
    public static final String SQL_STATE_INVALID_CONNECTION_ATTRIBUTE = "01S00";
    public static final String SQL_STATE_ERROR_IN_ROW = "01S01";
    public static final String SQL_STATE_NO_ROWS_UPDATED_OR_DELETED = "01S03";
    public static final String SQL_STATE_MORE_THAN_ONE_ROW_UPDATED_OR_DELETED = "01S04";
    public static final String SQL_STATE_RESIGNAL_WHEN_HANDLER_NOT_ACTIVE = "0K000";
    public static final String SQL_STATE_STACKED_DIAGNOSTICS_ACCESSED_WITHOUT_ACTIVE_HANDLER = "0Z002";
    public static final String SQL_STATE_CASE_NOT_FOUND_FOR_CASE_STATEMENT = "20000";
    public static final String SQL_STATE_NULL_VALUE_NOT_ALLOWED = "22004";
    public static final String SQL_STATE_INVALID_LOGARITHM_ARGUMENT = "2201E";
    public static final String SQL_STATE_ACTIVE_SQL_TRANSACTION = "25001";
    public static final String SQL_STATE_READ_ONLY_SQL_TRANSACTION = "25006";
    public static final String SQL_STATE_SRE_PROHIBITED_SQL_STATEMENT_ATTEMPTED = "2F003";
    public static final String SQL_STATE_SRE_FUNCTION_EXECUTED_NO_RETURN_STATEMENT = "2F005";
    public static final String SQL_STATE_ER_QUERY_INTERRUPTED = "70100";
    public static final String SQL_STATE_BASE_TABLE_OR_VIEW_ALREADY_EXISTS = "S0001";
    public static final String SQL_STATE_BASE_TABLE_NOT_FOUND = "S0002";
    public static final String SQL_STATE_INDEX_ALREADY_EXISTS = "S0011";
    public static final String SQL_STATE_INDEX_NOT_FOUND = "S0012";
    public static final String SQL_STATE_COLUMN_ALREADY_EXISTS = "S0021";
    public static final String SQL_STATE_COLUMN_NOT_FOUND = "S0022";
    public static final String SQL_STATE_NO_DEFAULT_FOR_COLUMN = "S0023";
    public static final String SQL_STATE_GENERAL_ERROR = "S1000";
    public static final String SQL_STATE_MEMORY_ALLOCATION_FAILURE = "S1001";
    public static final String SQL_STATE_INVALID_COLUMN_NUMBER = "S1002";
    public static final String SQL_STATE_ILLEGAL_ARGUMENT = "S1009";
    public static final String SQL_STATE_DRIVER_NOT_CAPABLE = "S1C00";
    public static final String SQL_STATE_TIMEOUT_EXPIRED = "S1T00";
    public static final String SQL_STATE_CLI_SPECIFIC_CONDITION = "HY000";
    public static final String SQL_STATE_MEMORY_ALLOCATION_ERROR = "HY001";
    public static final String SQL_STATE_XA_RBROLLBACK = "XA100";
    public static final String SQL_STATE_XA_RBDEADLOCK = "XA102";
    public static final String SQL_STATE_XA_RBTIMEOUT = "XA106";
    public static final String SQL_STATE_XA_RMERR = "XAE03";
    public static final String SQL_STATE_XAER_NOTA = "XAE04";
    public static final String SQL_STATE_XAER_INVAL = "XAE05";
    public static final String SQL_STATE_XAER_RMFAIL = "XAE07";
    public static final String SQL_STATE_XAER_DUPID = "XAE08";
    public static final String SQL_STATE_XAER_OUTSIDE = "XAE09";
    private static Map<String, String> sqlStateMessages;
    private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 28800L;
    private static final int DUE_TO_TIMEOUT_FALSE = 0;
    private static final int DUE_TO_TIMEOUT_MAYBE = 2;
    private static final int DUE_TO_TIMEOUT_TRUE = 1;
    private static final Constructor<?> JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR;

    static SQLWarning convertShowWarningsToSQLWarnings(Connection connection) throws SQLException {
        return SQLError.convertShowWarningsToSQLWarnings((Connection)connection, (int)0, (boolean)false);
    }

    /*
     * Exception decompiling
     */
    static SQLWarning convertShowWarningsToSQLWarnings(Connection connection, int warningCountIfKnown, boolean forTruncationOnly) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 5[CATCHBLOCK]
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

    public static void dumpSqlStatesMappingsAsXml() throws Exception {
        TreeMap<Integer, Integer> allErrorNumbers = new TreeMap<Integer, Integer>();
        HashMap<Object, String> mysqlErrorNumbersToNames = new HashMap<Object, String>();
        for (Integer errorNumber : mysqlToSql99State.keySet()) {
            allErrorNumbers.put(errorNumber, errorNumber);
        }
        for (Integer errorNumber : mysqlToSqlState.keySet()) {
            allErrorNumbers.put(errorNumber, errorNumber);
        }
        Field[] possibleFields = MysqlErrorNumbers.class.getDeclaredFields();
        for (int i = 0; i < possibleFields.length; ++i) {
            String fieldName = possibleFields[i].getName();
            if (!fieldName.startsWith((String)"ER_")) continue;
            mysqlErrorNumbersToNames.put(possibleFields[i].get(null), fieldName);
        }
        System.out.println((String)"<ErrorMappings>");
        Iterator<K> i$ = allErrorNumbers.keySet().iterator();
        do {
            String oldSqlState;
            if (!i$.hasNext()) {
                System.out.println((String)"</ErrorMappings>");
                return;
            }
            Integer errorNumber = (Integer)i$.next();
            String sql92State = SQLError.mysqlToSql99((int)errorNumber.intValue());
            System.out.println((String)("   <ErrorMapping mysqlErrorNumber=\"" + errorNumber + "\" mysqlErrorName=\"" + (String)mysqlErrorNumbersToNames.get((Object)errorNumber) + "\" legacySqlState=\"" + ((oldSqlState = SQLError.mysqlToXOpen((int)errorNumber.intValue())) == null ? "" : oldSqlState) + "\" sql92SqlState=\"" + (sql92State == null ? "" : sql92State) + "\"/>"));
        } while (true);
    }

    static String get(String stateCode) {
        return sqlStateMessages.get((Object)stateCode);
    }

    private static String mysqlToSql99(int errno) {
        Integer err = Integer.valueOf((int)errno);
        if (!mysqlToSql99State.containsKey((Object)err)) return SQL_STATE_CLI_SPECIFIC_CONDITION;
        return mysqlToSql99State.get((Object)err);
    }

    static String mysqlToSqlState(int errno, boolean useSql92States) {
        if (!useSql92States) return SQLError.mysqlToXOpen((int)errno);
        return SQLError.mysqlToSql99((int)errno);
    }

    private static String mysqlToXOpen(int errno) {
        Integer err = Integer.valueOf((int)errno);
        if (!mysqlToSqlState.containsKey((Object)err)) return SQL_STATE_GENERAL_ERROR;
        return mysqlToSqlState.get((Object)err);
    }

    public static SQLException createSQLException(String message, String sqlState, ExceptionInterceptor interceptor) {
        return SQLError.createSQLException((String)message, (String)sqlState, (int)0, (ExceptionInterceptor)interceptor);
    }

    public static SQLException createSQLException(String message, ExceptionInterceptor interceptor) {
        return SQLError.createSQLException((String)message, (ExceptionInterceptor)interceptor, null);
    }

    public static SQLException createSQLException(String message, ExceptionInterceptor interceptor, Connection conn) {
        SQLException sqlEx = new SQLException((String)message);
        return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
    }

    public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor) {
        return SQLError.createSQLException((String)message, (String)sqlState, (Throwable)cause, (ExceptionInterceptor)interceptor, null);
    }

    public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor, Connection conn) {
        SQLException sqlEx = SQLError.createSQLException((String)message, (String)sqlState, null);
        if (sqlEx.getCause() != null) return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
        sqlEx.initCause((Throwable)cause);
        return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
    }

    public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, ExceptionInterceptor interceptor) {
        return SQLError.createSQLException((String)message, (String)sqlState, (int)vendorErrorCode, (boolean)false, (ExceptionInterceptor)interceptor);
    }

    public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor) {
        return SQLError.createSQLException((String)message, (String)sqlState, (int)vendorErrorCode, (boolean)isTransient, (ExceptionInterceptor)interceptor, null);
    }

    public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor, Connection conn) {
        try {
            SQLException sqlEx = null;
            if (sqlState == null) {
                sqlEx = new SQLException((String)message, (String)sqlState, (int)vendorErrorCode);
                return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
            }
            if (sqlState.startsWith((String)"08")) {
                if (isTransient) {
                    if (!Util.isJdbc4()) {
                        sqlEx = new MySQLTransientConnectionException((String)message, (String)sqlState, (int)vendorErrorCode);
                        return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
                    }
                    sqlEx = (SQLException)Util.getInstance((String)"com.mysql.jdbc.exceptions.jdbc4.MySQLTransientConnectionException", new Class[]{String.class, String.class, Integer.TYPE}, (Object[])new Object[]{message, sqlState, Integer.valueOf((int)vendorErrorCode)}, (ExceptionInterceptor)interceptor);
                    return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
                }
                if (!Util.isJdbc4()) {
                    sqlEx = new MySQLNonTransientConnectionException((String)message, (String)sqlState, (int)vendorErrorCode);
                    return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
                }
                sqlEx = (SQLException)Util.getInstance((String)"com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException", new Class[]{String.class, String.class, Integer.TYPE}, (Object[])new Object[]{message, sqlState, Integer.valueOf((int)vendorErrorCode)}, (ExceptionInterceptor)interceptor);
                return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
            }
            if (sqlState.startsWith((String)"22")) {
                if (!Util.isJdbc4()) {
                    sqlEx = new MySQLDataException((String)message, (String)sqlState, (int)vendorErrorCode);
                    return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
                }
                sqlEx = (SQLException)Util.getInstance((String)"com.mysql.jdbc.exceptions.jdbc4.MySQLDataException", new Class[]{String.class, String.class, Integer.TYPE}, (Object[])new Object[]{message, sqlState, Integer.valueOf((int)vendorErrorCode)}, (ExceptionInterceptor)interceptor);
                return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
            }
            if (sqlState.startsWith((String)"23")) {
                if (!Util.isJdbc4()) {
                    sqlEx = new MySQLIntegrityConstraintViolationException((String)message, (String)sqlState, (int)vendorErrorCode);
                    return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
                }
                sqlEx = (SQLException)Util.getInstance((String)"com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException", new Class[]{String.class, String.class, Integer.TYPE}, (Object[])new Object[]{message, sqlState, Integer.valueOf((int)vendorErrorCode)}, (ExceptionInterceptor)interceptor);
                return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
            }
            if (sqlState.startsWith((String)"42")) {
                if (!Util.isJdbc4()) {
                    sqlEx = new MySQLSyntaxErrorException((String)message, (String)sqlState, (int)vendorErrorCode);
                    return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
                }
                sqlEx = (SQLException)Util.getInstance((String)"com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException", new Class[]{String.class, String.class, Integer.TYPE}, (Object[])new Object[]{message, sqlState, Integer.valueOf((int)vendorErrorCode)}, (ExceptionInterceptor)interceptor);
                return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
            }
            if (sqlState.startsWith((String)"40")) {
                if (!Util.isJdbc4()) {
                    sqlEx = new MySQLTransactionRollbackException((String)message, (String)sqlState, (int)vendorErrorCode);
                    return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
                }
                sqlEx = (SQLException)Util.getInstance((String)"com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException", new Class[]{String.class, String.class, Integer.TYPE}, (Object[])new Object[]{message, sqlState, Integer.valueOf((int)vendorErrorCode)}, (ExceptionInterceptor)interceptor);
                return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
            }
            if (!sqlState.startsWith((String)SQL_STATE_ER_QUERY_INTERRUPTED)) {
                sqlEx = new SQLException((String)message, (String)sqlState, (int)vendorErrorCode);
                return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
            }
            if (!Util.isJdbc4()) {
                sqlEx = new MySQLQueryInterruptedException((String)message, (String)sqlState, (int)vendorErrorCode);
                return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
            }
            sqlEx = (SQLException)Util.getInstance((String)"com.mysql.jdbc.exceptions.jdbc4.MySQLQueryInterruptedException", new Class[]{String.class, String.class, Integer.TYPE}, (Object[])new Object[]{message, sqlState, Integer.valueOf((int)vendorErrorCode)}, (ExceptionInterceptor)interceptor);
            return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)sqlEx, (Connection)conn);
        }
        catch (SQLException sqlEx) {
            SQLException unexpectedEx = new SQLException((String)("Unable to create correct SQLException class instance, error class/codes may be incorrect. Reason: " + Util.stackTraceToString((Throwable)sqlEx)), (String)SQL_STATE_GENERAL_ERROR);
            return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)unexpectedEx, (Connection)conn);
        }
    }

    public static SQLException createCommunicationsException(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException, ExceptionInterceptor interceptor) {
        SQLException exToReturn = null;
        if (!Util.isJdbc4()) {
            exToReturn = new CommunicationsException((MySQLConnection)conn, (long)lastPacketSentTimeMs, (long)lastPacketReceivedTimeMs, (Exception)underlyingException);
            return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)exToReturn, (Connection)conn);
        }
        try {
            exToReturn = (SQLException)Util.handleNewInstance(JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR, (Object[])new Object[]{conn, Long.valueOf((long)lastPacketSentTimeMs), Long.valueOf((long)lastPacketReceivedTimeMs), underlyingException}, (ExceptionInterceptor)interceptor);
            return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)exToReturn, (Connection)conn);
        }
        catch (SQLException sqlEx) {
            return sqlEx;
        }
    }

    public static String createLinkFailureMessageBasedOnHeuristics(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException) {
        long serverTimeoutSeconds = 0L;
        boolean isInteractiveClient = false;
        if (conn != null) {
            isInteractiveClient = conn.getInteractiveClient();
            String serverTimeoutSecondsStr = null;
            serverTimeoutSecondsStr = isInteractiveClient ? conn.getServerVariable((String)"interactive_timeout") : conn.getServerVariable((String)"wait_timeout");
            if (serverTimeoutSecondsStr != null) {
                try {
                    serverTimeoutSeconds = Long.parseLong((String)serverTimeoutSecondsStr);
                }
                catch (NumberFormatException nfe) {
                    serverTimeoutSeconds = 0L;
                }
            }
        }
        StringBuilder exceptionMessageBuf = new StringBuilder();
        long nowMs = System.currentTimeMillis();
        if (lastPacketSentTimeMs == 0L) {
            lastPacketSentTimeMs = nowMs;
        }
        long timeSinceLastPacketSentMs = nowMs - lastPacketSentTimeMs;
        long timeSinceLastPacketSeconds = timeSinceLastPacketSentMs / 1000L;
        long timeSinceLastPacketReceivedMs = nowMs - lastPacketReceivedTimeMs;
        int dueToTimeout = 0;
        StringBuilder timeoutMessageBuf = null;
        if (serverTimeoutSeconds != 0L) {
            if (timeSinceLastPacketSeconds > serverTimeoutSeconds) {
                dueToTimeout = 1;
                timeoutMessageBuf = new StringBuilder();
                timeoutMessageBuf.append((String)Messages.getString((String)"CommunicationsException.2"));
                if (!isInteractiveClient) {
                    timeoutMessageBuf.append((String)Messages.getString((String)"CommunicationsException.3"));
                } else {
                    timeoutMessageBuf.append((String)Messages.getString((String)"CommunicationsException.4"));
                }
            }
        } else if (timeSinceLastPacketSeconds > 28800L) {
            dueToTimeout = 2;
            timeoutMessageBuf = new StringBuilder();
            timeoutMessageBuf.append((String)Messages.getString((String)"CommunicationsException.5"));
            timeoutMessageBuf.append((String)Messages.getString((String)"CommunicationsException.6"));
            timeoutMessageBuf.append((String)Messages.getString((String)"CommunicationsException.7"));
            timeoutMessageBuf.append((String)Messages.getString((String)"CommunicationsException.8"));
        }
        if (dueToTimeout == 1 || dueToTimeout == 2) {
            exceptionMessageBuf.append((String)(lastPacketReceivedTimeMs != 0L ? Messages.getString((String)"CommunicationsException.ServerPacketTimingInfo", (Object[])new Object[]{Long.valueOf((long)timeSinceLastPacketReceivedMs), Long.valueOf((long)timeSinceLastPacketSentMs)}) : Messages.getString((String)"CommunicationsException.ServerPacketTimingInfoNoRecv", (Object[])new Object[]{Long.valueOf((long)timeSinceLastPacketSentMs)})));
            if (timeoutMessageBuf != null) {
                exceptionMessageBuf.append((CharSequence)timeoutMessageBuf);
            }
            exceptionMessageBuf.append((String)Messages.getString((String)"CommunicationsException.11"));
            exceptionMessageBuf.append((String)Messages.getString((String)"CommunicationsException.12"));
            exceptionMessageBuf.append((String)Messages.getString((String)"CommunicationsException.13"));
        } else if (underlyingException instanceof BindException) {
            exceptionMessageBuf.append((String)(conn.getLocalSocketAddress() != null && !Util.interfaceExists((String)conn.getLocalSocketAddress()) ? Messages.getString((String)"CommunicationsException.LocalSocketAddressNotAvailable") : Messages.getString((String)"CommunicationsException.TooManyClientConnections")));
        }
        if (exceptionMessageBuf.length() != 0) return exceptionMessageBuf.toString();
        exceptionMessageBuf.append((String)Messages.getString((String)"CommunicationsException.20"));
        if (conn == null) return exceptionMessageBuf.toString();
        if (!conn.getMaintainTimeStats()) return exceptionMessageBuf.toString();
        if (conn.getParanoid()) return exceptionMessageBuf.toString();
        exceptionMessageBuf.append((String)"\n\n");
        exceptionMessageBuf.append((String)(lastPacketReceivedTimeMs != 0L ? Messages.getString((String)"CommunicationsException.ServerPacketTimingInfo", (Object[])new Object[]{Long.valueOf((long)timeSinceLastPacketReceivedMs), Long.valueOf((long)timeSinceLastPacketSentMs)}) : Messages.getString((String)"CommunicationsException.ServerPacketTimingInfoNoRecv", (Object[])new Object[]{Long.valueOf((long)timeSinceLastPacketSentMs)})));
        return exceptionMessageBuf.toString();
    }

    private static SQLException runThroughExceptionInterceptor(ExceptionInterceptor exInterceptor, SQLException sqlEx, Connection conn) {
        if (exInterceptor == null) return sqlEx;
        SQLException interceptedEx = exInterceptor.interceptException((SQLException)sqlEx, (Connection)conn);
        if (interceptedEx == null) return sqlEx;
        return interceptedEx;
    }

    public static SQLException createBatchUpdateException(SQLException underlyingEx, long[] updateCounts, ExceptionInterceptor interceptor) throws SQLException {
        SQLException newEx;
        if (Util.isJdbc42()) {
            newEx = (SQLException)Util.getInstance((String)"java.sql.BatchUpdateException", new Class[]{String.class, String.class, Integer.TYPE, long[].class, Throwable.class}, (Object[])new Object[]{underlyingEx.getMessage(), underlyingEx.getSQLState(), Integer.valueOf((int)underlyingEx.getErrorCode()), updateCounts, underlyingEx}, (ExceptionInterceptor)interceptor);
            return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)newEx, null);
        }
        newEx = new BatchUpdateException((String)underlyingEx.getMessage(), (String)underlyingEx.getSQLState(), (int)underlyingEx.getErrorCode(), (int[])Util.truncateAndConvertToInt((long[])updateCounts));
        newEx.initCause((Throwable)underlyingEx);
        return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)newEx, null);
    }

    public static SQLException createSQLFeatureNotSupportedException() throws SQLException {
        if (!Util.isJdbc4()) return new NotImplemented();
        return (SQLException)Util.getInstance((String)"java.sql.SQLFeatureNotSupportedException", null, null, null);
    }

    public static SQLException createSQLFeatureNotSupportedException(String message, String sqlState, ExceptionInterceptor interceptor) throws SQLException {
        SQLException newEx;
        if (Util.isJdbc4()) {
            newEx = (SQLException)Util.getInstance((String)"java.sql.SQLFeatureNotSupportedException", new Class[]{String.class, String.class}, (Object[])new Object[]{message, sqlState}, (ExceptionInterceptor)interceptor);
            return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)newEx, null);
        }
        newEx = new NotImplemented();
        return SQLError.runThroughExceptionInterceptor((ExceptionInterceptor)interceptor, (SQLException)newEx, null);
    }

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = Class.forName((String)"com.mysql.jdbc.exceptions.jdbc4.CommunicationsException").getConstructor(MySQLConnection.class, Long.TYPE, Long.TYPE, Exception.class);
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
        } else {
            JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = null;
        }
        sqlStateMessages = new HashMap<String, String>();
        sqlStateMessages.put((String)SQL_STATE_DISCONNECT_ERROR, (String)Messages.getString((String)"SQLError.35"));
        sqlStateMessages.put((String)SQL_STATE_DATA_TRUNCATED, (String)Messages.getString((String)"SQLError.36"));
        sqlStateMessages.put((String)SQL_STATE_PRIVILEGE_NOT_REVOKED, (String)Messages.getString((String)"SQLError.37"));
        sqlStateMessages.put((String)SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, (String)Messages.getString((String)"SQLError.38"));
        sqlStateMessages.put((String)SQL_STATE_ERROR_IN_ROW, (String)Messages.getString((String)"SQLError.39"));
        sqlStateMessages.put((String)SQL_STATE_NO_ROWS_UPDATED_OR_DELETED, (String)Messages.getString((String)"SQLError.40"));
        sqlStateMessages.put((String)SQL_STATE_MORE_THAN_ONE_ROW_UPDATED_OR_DELETED, (String)Messages.getString((String)"SQLError.41"));
        sqlStateMessages.put((String)SQL_STATE_WRONG_NO_OF_PARAMETERS, (String)Messages.getString((String)"SQLError.42"));
        sqlStateMessages.put((String)SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, (String)Messages.getString((String)"SQLError.43"));
        sqlStateMessages.put((String)SQL_STATE_CONNECTION_IN_USE, (String)Messages.getString((String)"SQLError.44"));
        sqlStateMessages.put((String)SQL_STATE_CONNECTION_NOT_OPEN, (String)Messages.getString((String)"SQLError.45"));
        sqlStateMessages.put((String)SQL_STATE_CONNECTION_REJECTED, (String)Messages.getString((String)"SQLError.46"));
        sqlStateMessages.put((String)SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN, (String)Messages.getString((String)"SQLError.47"));
        sqlStateMessages.put((String)SQL_STATE_COMMUNICATION_LINK_FAILURE, (String)Messages.getString((String)"SQLError.48"));
        sqlStateMessages.put((String)SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST, (String)Messages.getString((String)"SQLError.49"));
        sqlStateMessages.put((String)SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE, (String)Messages.getString((String)"SQLError.50"));
        sqlStateMessages.put((String)SQL_STATE_DATETIME_FIELD_OVERFLOW, (String)Messages.getString((String)"SQLError.51"));
        sqlStateMessages.put((String)SQL_STATE_DIVISION_BY_ZERO, (String)Messages.getString((String)"SQLError.52"));
        sqlStateMessages.put((String)SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE, (String)Messages.getString((String)"SQLError.53"));
        sqlStateMessages.put((String)SQL_STATE_INVALID_AUTH_SPEC, (String)Messages.getString((String)"SQLError.54"));
        sqlStateMessages.put((String)SQL_STATE_SYNTAX_ERROR, (String)Messages.getString((String)"SQLError.55"));
        sqlStateMessages.put((String)SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND, (String)Messages.getString((String)"SQLError.56"));
        sqlStateMessages.put((String)SQL_STATE_BASE_TABLE_OR_VIEW_ALREADY_EXISTS, (String)Messages.getString((String)"SQLError.57"));
        sqlStateMessages.put((String)SQL_STATE_BASE_TABLE_NOT_FOUND, (String)Messages.getString((String)"SQLError.58"));
        sqlStateMessages.put((String)SQL_STATE_INDEX_ALREADY_EXISTS, (String)Messages.getString((String)"SQLError.59"));
        sqlStateMessages.put((String)SQL_STATE_INDEX_NOT_FOUND, (String)Messages.getString((String)"SQLError.60"));
        sqlStateMessages.put((String)SQL_STATE_COLUMN_ALREADY_EXISTS, (String)Messages.getString((String)"SQLError.61"));
        sqlStateMessages.put((String)SQL_STATE_COLUMN_NOT_FOUND, (String)Messages.getString((String)"SQLError.62"));
        sqlStateMessages.put((String)SQL_STATE_NO_DEFAULT_FOR_COLUMN, (String)Messages.getString((String)"SQLError.63"));
        sqlStateMessages.put((String)SQL_STATE_GENERAL_ERROR, (String)Messages.getString((String)"SQLError.64"));
        sqlStateMessages.put((String)SQL_STATE_MEMORY_ALLOCATION_FAILURE, (String)Messages.getString((String)"SQLError.65"));
        sqlStateMessages.put((String)SQL_STATE_INVALID_COLUMN_NUMBER, (String)Messages.getString((String)"SQLError.66"));
        sqlStateMessages.put((String)SQL_STATE_ILLEGAL_ARGUMENT, (String)Messages.getString((String)"SQLError.67"));
        sqlStateMessages.put((String)SQL_STATE_DRIVER_NOT_CAPABLE, (String)Messages.getString((String)"SQLError.68"));
        sqlStateMessages.put((String)SQL_STATE_TIMEOUT_EXPIRED, (String)Messages.getString((String)"SQLError.69"));
        mysqlToSqlState = new Hashtable<Integer, String>();
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1249), (String)SQL_STATE_WARNING);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1261), (String)SQL_STATE_WARNING);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1262), (String)SQL_STATE_WARNING);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1265), (String)SQL_STATE_WARNING);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1311), (String)SQL_STATE_WARNING);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1642), (String)SQL_STATE_WARNING);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1040), (String)SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1251), (String)SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1042), (String)SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1043), (String)SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1129), (String)SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1130), (String)SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1047), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1053), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1080), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1081), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1152), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1153), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1154), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1155), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1156), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1157), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1158), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1159), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1160), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1161), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1184), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1189), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1190), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1218), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1312), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1314), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1335), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1336), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1415), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1845), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1846), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1044), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1049), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1055), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1056), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1057), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1059), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1060), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1061), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1062), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1063), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1064), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1065), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1066), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1067), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1068), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1069), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1070), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1071), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1072), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1073), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1074), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1075), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1082), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1083), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1084), (String)SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1090), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1091), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1101), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1102), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1103), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1104), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1106), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1107), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1110), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1112), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1113), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1115), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1118), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1120), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1121), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1131), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1132), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1133), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1139), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1140), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1141), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1142), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1143), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1144), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1145), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1147), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1148), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1149), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1162), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1163), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1164), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1166), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1167), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1170), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1171), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1172), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1173), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1176), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1177), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1178), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1203), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1211), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1226), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1227), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1230), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1231), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1232), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1234), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1235), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1239), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1248), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1250), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1252), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1253), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1280), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1281), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1286), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1304), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1305), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1308), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1309), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1310), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1313), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1315), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1316), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1318), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1319), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1320), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1322), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1323), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1324), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1327), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1330), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1331), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1332), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1333), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1337), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1338), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1370), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1403), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1407), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1410), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1413), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1414), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1425), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1426), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1427), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1437), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1439), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1453), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1458), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1460), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1461), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1463), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1582), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1583), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1584), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1630), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1641), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1687), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1701), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1222), (String)SQL_STATE_CARDINALITY_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1241), (String)SQL_STATE_CARDINALITY_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1242), (String)SQL_STATE_CARDINALITY_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1022), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1048), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1052), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1169), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1216), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1217), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1451), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1452), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1557), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1586), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1761), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1762), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1859), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1406), (String)SQL_STATE_STRING_DATA_RIGHT_TRUNCATION);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1264), (String)SQL_STATE_WARNING);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1416), (String)SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1690), (String)SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1292), (String)SQL_STATE_INVALID_DATETIME_FORMAT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1367), (String)SQL_STATE_INVALID_DATETIME_FORMAT);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1441), (String)SQL_STATE_DATETIME_FIELD_OVERFLOW);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1365), (String)SQL_STATE_DIVISION_BY_ZERO);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1325), (String)SQL_STATE_INVALID_CURSOR_STATE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1326), (String)SQL_STATE_INVALID_CURSOR_STATE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1179), (String)SQL_STATE_INVALID_TRANSACTION_STATE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1207), (String)SQL_STATE_INVALID_TRANSACTION_STATE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1045), (String)SQL_STATE_INVALID_AUTH_SPEC);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1698), (String)SQL_STATE_INVALID_AUTH_SPEC);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1873), (String)SQL_STATE_INVALID_AUTH_SPEC);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1758), (String)SQL_STATE_INVALID_CONDITION_NUMBER);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1046), (String)SQL_STATE_INVALID_CATALOG_NAME);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1058), (String)SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1136), (String)SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1050), (String)SQL_STATE_ER_TABLE_EXISTS_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1051), (String)SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1109), (String)SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1146), (String)SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1054), (String)SQL_STATE_COLUMN_NOT_FOUND);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1247), (String)SQL_STATE_ER_BAD_FIELD_ERROR);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1037), (String)SQL_STATE_MEMORY_ALLOCATION_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1038), (String)SQL_STATE_MEMORY_ALLOCATION_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1205), (String)SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE);
        mysqlToSqlState.put((Integer)Integer.valueOf((int)1213), (String)SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE);
        mysqlToSql99State = new HashMap<Integer, String>();
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1249), (String)SQL_STATE_WARNING);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1261), (String)SQL_STATE_WARNING);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1262), (String)SQL_STATE_WARNING);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1265), (String)SQL_STATE_WARNING);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1263), (String)SQL_STATE_WARNING);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1264), (String)SQL_STATE_WARNING);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1311), (String)SQL_STATE_WARNING);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1642), (String)SQL_STATE_WARNING);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1329), (String)SQL_STATE_NO_DATA);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1643), (String)SQL_STATE_NO_DATA);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1040), (String)SQL_STATE_CONNECTION_REJECTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1251), (String)SQL_STATE_CONNECTION_REJECTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1042), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1043), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1047), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1053), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1080), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1081), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1152), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1153), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1154), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1155), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1156), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1157), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1158), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1159), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1160), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1161), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1184), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1189), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1190), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1218), (String)SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1312), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1314), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1335), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1336), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1415), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1845), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1846), (String)SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1044), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1049), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1055), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1056), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1057), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1059), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1061), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1063), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1064), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1065), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1066), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1067), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1068), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1069), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1070), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1071), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1072), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1073), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1074), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1075), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1083), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1084), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1090), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1091), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1101), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1102), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1103), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1104), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1106), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1107), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1110), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1112), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1113), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1115), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1118), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1120), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1121), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1131), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1132), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1133), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1139), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1140), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1141), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1142), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1143), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1144), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1145), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1147), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1148), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1149), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1162), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1163), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1164), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1166), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1167), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1170), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1171), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1172), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1173), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1176), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1177), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1178), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1203), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1211), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1226), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1227), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1230), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1231), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1232), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1234), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1235), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1239), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1248), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1250), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1252), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1253), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1280), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1281), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1286), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1304), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1305), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1308), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1309), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1310), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1313), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1315), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1316), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1318), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1319), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1320), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1322), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1323), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1324), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1327), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1330), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1331), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1332), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1333), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1337), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1338), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1370), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1403), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1407), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1410), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1413), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1414), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1425), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1426), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1427), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1437), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1439), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1453), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1458), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1460), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1461), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1463), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1582), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1583), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1584), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1630), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1641), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1687), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1701), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1222), (String)SQL_STATE_CARDINALITY_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1241), (String)SQL_STATE_CARDINALITY_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1242), (String)SQL_STATE_CARDINALITY_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1022), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1048), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1052), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1062), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1169), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1216), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1217), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1451), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1452), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1557), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1586), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1761), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1762), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1859), (String)SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1406), (String)SQL_STATE_STRING_DATA_RIGHT_TRUNCATION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1416), (String)SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1690), (String)SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1292), (String)SQL_STATE_INVALID_DATETIME_FORMAT);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1367), (String)SQL_STATE_INVALID_DATETIME_FORMAT);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1441), (String)SQL_STATE_DATETIME_FIELD_OVERFLOW);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1365), (String)SQL_STATE_DIVISION_BY_ZERO);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1325), (String)SQL_STATE_INVALID_CURSOR_STATE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1326), (String)SQL_STATE_INVALID_CURSOR_STATE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1179), (String)SQL_STATE_INVALID_TRANSACTION_STATE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1207), (String)SQL_STATE_INVALID_TRANSACTION_STATE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1045), (String)SQL_STATE_INVALID_AUTH_SPEC);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1698), (String)SQL_STATE_INVALID_AUTH_SPEC);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1873), (String)SQL_STATE_INVALID_AUTH_SPEC);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1758), (String)SQL_STATE_INVALID_CONDITION_NUMBER);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1046), (String)SQL_STATE_INVALID_CATALOG_NAME);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1645), (String)SQL_STATE_RESIGNAL_WHEN_HANDLER_NOT_ACTIVE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1887), (String)SQL_STATE_STACKED_DIAGNOSTICS_ACCESSED_WITHOUT_ACTIVE_HANDLER);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1339), (String)SQL_STATE_CASE_NOT_FOUND_FOR_CASE_STATEMENT);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1058), (String)SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1136), (String)SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1138), (String)SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1903), (String)SQL_STATE_INVALID_LOGARITHM_ARGUMENT);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1568), (String)SQL_STATE_ACTIVE_SQL_TRANSACTION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1792), (String)SQL_STATE_READ_ONLY_SQL_TRANSACTION);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1303), (String)SQL_STATE_SRE_PROHIBITED_SQL_STATEMENT_ATTEMPTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1321), (String)SQL_STATE_SRE_FUNCTION_EXECUTED_NO_RETURN_STATEMENT);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1050), (String)SQL_STATE_ER_TABLE_EXISTS_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1051), (String)SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1109), (String)SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1146), (String)SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1082), (String)SQL_STATE_ER_NO_SUCH_INDEX);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1060), (String)SQL_STATE_ER_DUP_FIELDNAME);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1054), (String)SQL_STATE_ER_BAD_FIELD_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1247), (String)SQL_STATE_ER_BAD_FIELD_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1317), (String)SQL_STATE_ER_QUERY_INTERRUPTED);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1037), (String)SQL_STATE_MEMORY_ALLOCATION_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1038), (String)SQL_STATE_MEMORY_ALLOCATION_ERROR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1402), (String)SQL_STATE_XA_RBROLLBACK);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1614), (String)SQL_STATE_XA_RBDEADLOCK);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1613), (String)SQL_STATE_XA_RBTIMEOUT);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1401), (String)SQL_STATE_XA_RMERR);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1397), (String)SQL_STATE_XAER_NOTA);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1398), (String)SQL_STATE_XAER_INVAL);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1399), (String)SQL_STATE_XAER_RMFAIL);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1440), (String)SQL_STATE_XAER_DUPID);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1400), (String)SQL_STATE_XAER_OUTSIDE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1205), (String)SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE);
        mysqlToSql99State.put((Integer)Integer.valueOf((int)1213), (String)SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE);
    }
}

