/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.log;

import com.mysql.jdbc.Util;

public class LogUtils {
    public static final String CALLER_INFORMATION_NOT_AVAILABLE = "Caller information not available";
    private static final String LINE_SEPARATOR = System.getProperty((String)"line.separator");
    private static final int LINE_SEPARATOR_LENGTH = LINE_SEPARATOR.length();

    public static String findCallingClassAndMethod(Throwable t) {
        String stackTraceAsString = Util.stackTraceToString((Throwable)t);
        String callingClassAndMethod = CALLER_INFORMATION_NOT_AVAILABLE;
        int endInternalMethods = stackTraceAsString.lastIndexOf((String)"com.mysql.jdbc");
        if (endInternalMethods != -1) {
            int endOfLine = -1;
            int compliancePackage = stackTraceAsString.indexOf((String)"com.mysql.jdbc.compliance", (int)endInternalMethods);
            endOfLine = compliancePackage != -1 ? compliancePackage - LINE_SEPARATOR_LENGTH : stackTraceAsString.indexOf((String)LINE_SEPARATOR, (int)endInternalMethods);
            if (endOfLine != -1) {
                int nextEndOfLine = stackTraceAsString.indexOf((String)LINE_SEPARATOR, (int)(endOfLine + LINE_SEPARATOR_LENGTH));
                callingClassAndMethod = nextEndOfLine != -1 ? stackTraceAsString.substring((int)(endOfLine + LINE_SEPARATOR_LENGTH), (int)nextEndOfLine) : stackTraceAsString.substring((int)(endOfLine + LINE_SEPARATOR_LENGTH));
            }
        }
        if (callingClassAndMethod.startsWith((String)"\tat ")) return callingClassAndMethod;
        if (callingClassAndMethod.startsWith((String)"at ")) return callingClassAndMethod;
        return "at " + callingClassAndMethod;
    }
}

