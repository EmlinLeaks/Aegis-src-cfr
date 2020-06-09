/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.EscapeProcessorResult;
import com.mysql.jdbc.EscapeTokenizer;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.TimeUtil;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

class EscapeProcessor {
    private static Map<String, String> JDBC_CONVERT_TO_MYSQL_TYPE_MAP;
    private static Map<String, String> JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP;

    EscapeProcessor() {
    }

    public static final Object escapeSQL(String sql, boolean serverSupportsConvertFn, MySQLConnection conn) throws SQLException {
        int nextEndBrace;
        boolean replaceEscapeSequence = false;
        String escapeSequence = null;
        if (sql == null) {
            return null;
        }
        int beginBrace = sql.indexOf((int)123);
        int n = nextEndBrace = beginBrace == -1 ? -1 : sql.indexOf((int)125, (int)beginBrace);
        if (nextEndBrace == -1) {
            return sql;
        }
        StringBuilder newSql = new StringBuilder();
        EscapeTokenizer escapeTokenizer = new EscapeTokenizer((String)sql);
        byte usesVariables = 0;
        boolean callingStoredFunction = false;
        block4 : while (escapeTokenizer.hasMoreTokens()) {
            String token = escapeTokenizer.nextToken();
            if (token.length() == 0) continue;
            if (token.charAt((int)0) == '{') {
                int nestedBrace;
                String collapsedToken;
                if (!token.endsWith((String)"}")) {
                    throw SQLError.createSQLException((String)("Not a valid escape sequence: " + token), (ExceptionInterceptor)conn.getExceptionInterceptor());
                }
                if (token.length() > 2 && (nestedBrace = token.indexOf((int)123, (int)2)) != -1) {
                    StringBuilder buf = new StringBuilder((String)token.substring((int)0, (int)1));
                    Object remainingResults = EscapeProcessor.escapeSQL((String)token.substring((int)1, (int)(token.length() - 1)), (boolean)serverSupportsConvertFn, (MySQLConnection)conn);
                    String remaining = null;
                    if (remainingResults instanceof String) {
                        remaining = (String)remainingResults;
                    } else {
                        remaining = ((EscapeProcessorResult)remainingResults).escapedSql;
                        if (usesVariables != 1) {
                            usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
                        }
                    }
                    buf.append((String)remaining);
                    buf.append((char)'}');
                    token = buf.toString();
                }
                if (StringUtils.startsWithIgnoreCase((String)(collapsedToken = EscapeProcessor.removeWhitespace((String)token)), (String)"{escape")) {
                    try {
                        StringTokenizer st = new StringTokenizer((String)token, (String)" '");
                        st.nextToken();
                        escapeSequence = st.nextToken();
                        if (escapeSequence.length() < 3) {
                            newSql.append((String)token);
                            continue;
                        }
                        escapeSequence = escapeSequence.substring((int)1, (int)(escapeSequence.length() - 1));
                        replaceEscapeSequence = true;
                    }
                    catch (NoSuchElementException e) {
                        newSql.append((String)token);
                    }
                    continue;
                }
                if (StringUtils.startsWithIgnoreCase((String)collapsedToken, (String)"{fn")) {
                    int endPos;
                    int startPos = token.toLowerCase().indexOf((String)"fn ") + 3;
                    String fnToken = token.substring((int)startPos, (int)(endPos = token.length() - 1));
                    if (StringUtils.startsWithIgnoreCaseAndWs((String)fnToken, (String)"convert")) {
                        newSql.append((String)EscapeProcessor.processConvertToken((String)fnToken, (boolean)serverSupportsConvertFn, (MySQLConnection)conn));
                        continue;
                    }
                    newSql.append((String)fnToken);
                    continue;
                }
                if (StringUtils.startsWithIgnoreCase((String)collapsedToken, (String)"{d")) {
                    int startPos = token.indexOf((int)39) + 1;
                    int endPos = token.lastIndexOf((int)39);
                    if (startPos == -1 || endPos == -1) {
                        newSql.append((String)token);
                        continue;
                    }
                    String argument = token.substring((int)startPos, (int)endPos);
                    try {
                        StringTokenizer st = new StringTokenizer((String)argument, (String)" -");
                        String year4 = st.nextToken();
                        String month2 = st.nextToken();
                        String day2 = st.nextToken();
                        String dateString = "'" + year4 + "-" + month2 + "-" + day2 + "'";
                        newSql.append((String)dateString);
                        continue;
                    }
                    catch (NoSuchElementException e) {
                        throw SQLError.createSQLException((String)("Syntax error for DATE escape sequence '" + argument + "'"), (String)"42000", (ExceptionInterceptor)conn.getExceptionInterceptor());
                    }
                }
                if (StringUtils.startsWithIgnoreCase((String)collapsedToken, (String)"{ts")) {
                    EscapeProcessor.processTimestampToken((MySQLConnection)conn, (StringBuilder)newSql, (String)token);
                    continue;
                }
                if (StringUtils.startsWithIgnoreCase((String)collapsedToken, (String)"{t")) {
                    EscapeProcessor.processTimeToken((MySQLConnection)conn, (StringBuilder)newSql, (String)token);
                    continue;
                }
                if (StringUtils.startsWithIgnoreCase((String)collapsedToken, (String)"{call") || StringUtils.startsWithIgnoreCase((String)collapsedToken, (String)"{?=call")) {
                    int startPos = StringUtils.indexOfIgnoreCase((String)token, (String)"CALL") + 5;
                    int endPos = token.length() - 1;
                    if (StringUtils.startsWithIgnoreCase((String)collapsedToken, (String)"{?=call")) {
                        callingStoredFunction = true;
                        newSql.append((String)"SELECT ");
                        newSql.append((String)token.substring((int)startPos, (int)endPos));
                    } else {
                        callingStoredFunction = false;
                        newSql.append((String)"CALL ");
                        newSql.append((String)token.substring((int)startPos, (int)endPos));
                    }
                    for (int i = endPos - 1; i >= startPos; --i) {
                        char c = token.charAt((int)i);
                        if (Character.isWhitespace((char)c)) {
                            continue;
                        }
                        if (c == ')') continue block4;
                        newSql.append((String)"()");
                        continue block4;
                    }
                    continue;
                }
                if (StringUtils.startsWithIgnoreCase((String)collapsedToken, (String)"{oj")) {
                    newSql.append((String)token);
                    continue;
                }
                newSql.append((String)token);
                continue;
            }
            newSql.append((String)token);
        }
        String escapedSql = newSql.toString();
        if (replaceEscapeSequence) {
            String currentSql = escapedSql;
            while (currentSql.indexOf(escapeSequence) != -1) {
                int escapePos = currentSql.indexOf(escapeSequence);
                String lhs = currentSql.substring((int)0, (int)escapePos);
                String rhs = currentSql.substring((int)(escapePos + 1), (int)currentSql.length());
                currentSql = lhs + "\\" + rhs;
            }
            escapedSql = currentSql;
        }
        EscapeProcessorResult epr = new EscapeProcessorResult();
        epr.escapedSql = escapedSql;
        epr.callingStoredFunction = callingStoredFunction;
        if (usesVariables == true) return epr;
        if (escapeTokenizer.sawVariableUse()) {
            epr.usesVariables = 1;
            return epr;
        }
        epr.usesVariables = 0;
        return epr;
    }

    private static void processTimeToken(MySQLConnection conn, StringBuilder newSql, String token) throws SQLException {
        int startPos = token.indexOf((int)39) + 1;
        int endPos = token.lastIndexOf((int)39);
        if (startPos == -1 || endPos == -1) {
            newSql.append((String)token);
            return;
        }
        String argument = token.substring((int)startPos, (int)endPos);
        try {
            StringTokenizer st = new StringTokenizer((String)argument, (String)" :.");
            String hour = st.nextToken();
            String minute = st.nextToken();
            String second = st.nextToken();
            boolean serverSupportsFractionalSecond = false;
            String fractionalSecond = "";
            if (st.hasMoreTokens() && conn.versionMeetsMinimum((int)5, (int)6, (int)4)) {
                serverSupportsFractionalSecond = true;
                fractionalSecond = "." + st.nextToken();
            }
            if (!conn.getUseTimezone() || !conn.getUseLegacyDatetimeCode()) {
                newSql.append((String)"'");
                newSql.append((String)hour);
                newSql.append((String)":");
                newSql.append((String)minute);
                newSql.append((String)":");
                newSql.append((String)second);
                newSql.append((String)fractionalSecond);
                newSql.append((String)"'");
                return;
            }
            Calendar sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
            try {
                int hourInt = Integer.parseInt((String)hour);
                int minuteInt = Integer.parseInt((String)minute);
                int secondInt = Integer.parseInt((String)second);
                Time toBeAdjusted = TimeUtil.fastTimeCreate((Calendar)sessionCalendar, (int)hourInt, (int)minuteInt, (int)secondInt, (ExceptionInterceptor)conn.getExceptionInterceptor());
                Time inServerTimezone = TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, null, (Time)toBeAdjusted, (TimeZone)sessionCalendar.getTimeZone(), (TimeZone)conn.getServerTimezoneTZ(), (boolean)false);
                newSql.append((String)"'");
                newSql.append((String)inServerTimezone.toString());
                if (serverSupportsFractionalSecond) {
                    newSql.append((String)fractionalSecond);
                }
                newSql.append((String)"'");
                return;
            }
            catch (NumberFormatException nfe) {
                throw SQLError.createSQLException((String)("Syntax error in TIMESTAMP escape sequence '" + token + "'."), (String)"S1009", (ExceptionInterceptor)conn.getExceptionInterceptor());
            }
        }
        catch (NoSuchElementException e) {
            throw SQLError.createSQLException((String)("Syntax error for escape sequence '" + argument + "'"), (String)"42000", (ExceptionInterceptor)conn.getExceptionInterceptor());
        }
    }

    private static void processTimestampToken(MySQLConnection conn, StringBuilder newSql, String token) throws SQLException {
        int startPos = token.indexOf((int)39) + 1;
        int endPos = token.lastIndexOf((int)39);
        if (startPos == -1 || endPos == -1) {
            newSql.append((String)token);
            return;
        }
        String argument = token.substring((int)startPos, (int)endPos);
        try {
            if (!conn.getUseLegacyDatetimeCode()) {
                Timestamp ts = Timestamp.valueOf((String)argument);
                ts = TimeUtil.adjustTimestampNanosPrecision((Timestamp)ts, (int)6, (boolean)(!conn.isServerTruncatesFracSecs()));
                SimpleDateFormat tsdf = TimeUtil.getSimpleDateFormat(null, (String)"''yyyy-MM-dd HH:mm:ss", null, (TimeZone)conn.getServerTimezoneTZ());
                newSql.append((String)tsdf.format((Date)ts));
                if (ts.getNanos() > 0 && conn.versionMeetsMinimum((int)5, (int)6, (int)4)) {
                    newSql.append((char)'.');
                    newSql.append((String)TimeUtil.formatNanos((int)ts.getNanos(), (boolean)true, (int)6));
                }
                newSql.append((char)'\'');
                return;
            }
            StringTokenizer st = new StringTokenizer((String)argument, (String)" .-:");
            try {
                String year4 = st.nextToken();
                String month2 = st.nextToken();
                String day2 = st.nextToken();
                String hour = st.nextToken();
                String minute = st.nextToken();
                String second = st.nextToken();
                boolean serverSupportsFractionalSecond = false;
                String fractionalSecond = "";
                if (st.hasMoreTokens() && conn.versionMeetsMinimum((int)5, (int)6, (int)4)) {
                    serverSupportsFractionalSecond = true;
                    fractionalSecond = "." + st.nextToken();
                }
                if (!conn.getUseTimezone() && !conn.getUseJDBCCompliantTimezoneShift()) {
                    newSql.append((String)"'").append((String)year4).append((String)"-").append((String)month2).append((String)"-").append((String)day2).append((String)" ").append((String)hour).append((String)":").append((String)minute).append((String)":").append((String)second).append((String)fractionalSecond).append((String)"'");
                    return;
                }
                Calendar sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
                try {
                    int year4Int = Integer.parseInt((String)year4);
                    int month2Int = Integer.parseInt((String)month2);
                    int day2Int = Integer.parseInt((String)day2);
                    int hourInt = Integer.parseInt((String)hour);
                    int minuteInt = Integer.parseInt((String)minute);
                    int secondInt = Integer.parseInt((String)second);
                    boolean useGmtMillis = conn.getUseGmtMillisForDatetimes();
                    Timestamp toBeAdjusted = TimeUtil.fastTimestampCreate((boolean)useGmtMillis, (Calendar)(useGmtMillis ? Calendar.getInstance((TimeZone)TimeZone.getTimeZone((String)"GMT")) : null), (Calendar)sessionCalendar, (int)year4Int, (int)month2Int, (int)day2Int, (int)hourInt, (int)minuteInt, (int)secondInt, (int)0);
                    Timestamp inServerTimezone = TimeUtil.changeTimezone((MySQLConnection)conn, (Calendar)sessionCalendar, null, (Timestamp)toBeAdjusted, (TimeZone)sessionCalendar.getTimeZone(), (TimeZone)conn.getServerTimezoneTZ(), (boolean)false);
                    newSql.append((String)"'");
                    String timezoneLiteral = inServerTimezone.toString();
                    int indexOfDot = timezoneLiteral.indexOf((String)".");
                    if (indexOfDot != -1) {
                        timezoneLiteral = timezoneLiteral.substring((int)0, (int)indexOfDot);
                    }
                    newSql.append((String)timezoneLiteral);
                    if (serverSupportsFractionalSecond) {
                        newSql.append((String)fractionalSecond);
                    }
                    newSql.append((String)"'");
                    return;
                }
                catch (NumberFormatException nfe) {
                    throw SQLError.createSQLException((String)("Syntax error in TIMESTAMP escape sequence '" + token + "'."), (String)"S1009", (ExceptionInterceptor)conn.getExceptionInterceptor());
                }
            }
            catch (NoSuchElementException e) {
                throw SQLError.createSQLException((String)("Syntax error for TIMESTAMP escape sequence '" + argument + "'"), (String)"42000", (ExceptionInterceptor)conn.getExceptionInterceptor());
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            SQLException sqlEx = SQLError.createSQLException((String)("Syntax error for TIMESTAMP escape sequence '" + argument + "'"), (String)"42000", (ExceptionInterceptor)conn.getExceptionInterceptor());
            sqlEx.initCause((Throwable)illegalArgumentException);
            throw sqlEx;
        }
    }

    private static String processConvertToken(String functionToken, boolean serverSupportsConvertFn, MySQLConnection conn) throws SQLException {
        int firstIndexOfParen = functionToken.indexOf((String)"(");
        if (firstIndexOfParen == -1) {
            throw SQLError.createSQLException((String)("Syntax error while processing {fn convert (... , ...)} token, missing opening parenthesis in token '" + functionToken + "'."), (String)"42000", (ExceptionInterceptor)conn.getExceptionInterceptor());
        }
        int indexOfComma = functionToken.lastIndexOf((String)",");
        if (indexOfComma == -1) {
            throw SQLError.createSQLException((String)("Syntax error while processing {fn convert (... , ...)} token, missing comma in token '" + functionToken + "'."), (String)"42000", (ExceptionInterceptor)conn.getExceptionInterceptor());
        }
        int indexOfCloseParen = functionToken.indexOf((int)41, (int)indexOfComma);
        if (indexOfCloseParen == -1) {
            throw SQLError.createSQLException((String)("Syntax error while processing {fn convert (... , ...)} token, missing closing parenthesis in token '" + functionToken + "'."), (String)"42000", (ExceptionInterceptor)conn.getExceptionInterceptor());
        }
        String expression = functionToken.substring((int)(firstIndexOfParen + 1), (int)indexOfComma);
        String type = functionToken.substring((int)(indexOfComma + 1), (int)indexOfCloseParen);
        String newType = null;
        String trimmedType = type.trim();
        if (StringUtils.startsWithIgnoreCase((String)trimmedType, (String)"SQL_")) {
            trimmedType = trimmedType.substring((int)4, (int)trimmedType.length());
        }
        if (serverSupportsConvertFn) {
            newType = JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get((Object)trimmedType.toUpperCase((Locale)Locale.ENGLISH));
        } else {
            newType = JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP.get((Object)trimmedType.toUpperCase((Locale)Locale.ENGLISH));
            if (newType == null) {
                throw SQLError.createSQLException((String)("Can't find conversion re-write for type '" + type + "' that is applicable for this server version while processing escape tokens."), (String)"S1000", (ExceptionInterceptor)conn.getExceptionInterceptor());
            }
        }
        if (newType == null) {
            throw SQLError.createSQLException((String)("Unsupported conversion type '" + type.trim() + "' found while processing escape token."), (String)"S1000", (ExceptionInterceptor)conn.getExceptionInterceptor());
        }
        int replaceIndex = newType.indexOf((String)"?");
        if (replaceIndex != -1) {
            StringBuilder convertRewrite = new StringBuilder((String)newType.substring((int)0, (int)replaceIndex));
            convertRewrite.append((String)expression);
            convertRewrite.append((String)newType.substring((int)(replaceIndex + 1), (int)newType.length()));
            return convertRewrite.toString();
        }
        StringBuilder castRewrite = new StringBuilder((String)"CAST(");
        castRewrite.append((String)expression);
        castRewrite.append((String)" AS ");
        castRewrite.append((String)newType);
        castRewrite.append((String)")");
        return castRewrite.toString();
    }

    private static String removeWhitespace(String toCollapse) {
        if (toCollapse == null) {
            return null;
        }
        int length = toCollapse.length();
        StringBuilder collapsed = new StringBuilder((int)length);
        int i = 0;
        while (i < length) {
            char c = toCollapse.charAt((int)i);
            if (!Character.isWhitespace((char)c)) {
                collapsed.append((char)c);
            }
            ++i;
        }
        return collapsed.toString();
    }

    static {
        HashMap<Object, Object> tempMap = new HashMap<String, String>();
        tempMap.put("BIGINT", "0 + ?");
        tempMap.put("BINARY", "BINARY");
        tempMap.put("BIT", "0 + ?");
        tempMap.put("CHAR", "CHAR");
        tempMap.put("DATE", "DATE");
        tempMap.put("DECIMAL", "0.0 + ?");
        tempMap.put("DOUBLE", "0.0 + ?");
        tempMap.put("FLOAT", "0.0 + ?");
        tempMap.put("INTEGER", "0 + ?");
        tempMap.put("LONGVARBINARY", "BINARY");
        tempMap.put("LONGVARCHAR", "CONCAT(?)");
        tempMap.put("REAL", "0.0 + ?");
        tempMap.put("SMALLINT", "CONCAT(?)");
        tempMap.put("TIME", "TIME");
        tempMap.put("TIMESTAMP", "DATETIME");
        tempMap.put("TINYINT", "CONCAT(?)");
        tempMap.put("VARBINARY", "BINARY");
        tempMap.put("VARCHAR", "CONCAT(?)");
        JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
        tempMap = new HashMap<String, String>(JDBC_CONVERT_TO_MYSQL_TYPE_MAP);
        tempMap.put("BINARY", "CONCAT(?)");
        tempMap.put((Object)"CHAR", (Object)"CONCAT(?)");
        tempMap.remove((Object)"DATE");
        tempMap.put((Object)"LONGVARBINARY", (Object)"CONCAT(?)");
        tempMap.remove((Object)"TIME");
        tempMap.remove((Object)"TIMESTAMP");
        tempMap.put((Object)"VARBINARY", (Object)"CONCAT(?)");
        JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP = Collections.unmodifiableMap(tempMap);
    }
}

