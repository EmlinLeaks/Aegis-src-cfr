/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.log;

import com.mysql.jdbc.Util;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.LogUtils;
import com.mysql.jdbc.profiler.ProfilerEvent;
import java.io.PrintStream;
import java.util.Date;

public class StandardLogger
implements Log {
    private static final int FATAL = 0;
    private static final int ERROR = 1;
    private static final int WARN = 2;
    private static final int INFO = 3;
    private static final int DEBUG = 4;
    private static final int TRACE = 5;
    private boolean logLocationInfo = true;

    public StandardLogger(String name) {
        this((String)name, (boolean)false);
    }

    public StandardLogger(String name, boolean logLocationInfo) {
        this.logLocationInfo = logLocationInfo;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isFatalEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void logDebug(Object message) {
        this.logInternal((int)4, (Object)message, null);
    }

    @Override
    public void logDebug(Object message, Throwable exception) {
        this.logInternal((int)4, (Object)message, (Throwable)exception);
    }

    @Override
    public void logError(Object message) {
        this.logInternal((int)1, (Object)message, null);
    }

    @Override
    public void logError(Object message, Throwable exception) {
        this.logInternal((int)1, (Object)message, (Throwable)exception);
    }

    @Override
    public void logFatal(Object message) {
        this.logInternal((int)0, (Object)message, null);
    }

    @Override
    public void logFatal(Object message, Throwable exception) {
        this.logInternal((int)0, (Object)message, (Throwable)exception);
    }

    @Override
    public void logInfo(Object message) {
        this.logInternal((int)3, (Object)message, null);
    }

    @Override
    public void logInfo(Object message, Throwable exception) {
        this.logInternal((int)3, (Object)message, (Throwable)exception);
    }

    @Override
    public void logTrace(Object message) {
        this.logInternal((int)5, (Object)message, null);
    }

    @Override
    public void logTrace(Object message, Throwable exception) {
        this.logInternal((int)5, (Object)message, (Throwable)exception);
    }

    @Override
    public void logWarn(Object message) {
        this.logInternal((int)2, (Object)message, null);
    }

    @Override
    public void logWarn(Object message, Throwable exception) {
        this.logInternal((int)2, (Object)message, (Throwable)exception);
    }

    protected String logInternal(int level, Object msg, Throwable exception) {
        StringBuilder msgBuf = new StringBuilder();
        msgBuf.append((String)new Date().toString());
        msgBuf.append((String)" ");
        switch (level) {
            case 0: {
                msgBuf.append((String)"FATAL: ");
                break;
            }
            case 1: {
                msgBuf.append((String)"ERROR: ");
                break;
            }
            case 2: {
                msgBuf.append((String)"WARN: ");
                break;
            }
            case 3: {
                msgBuf.append((String)"INFO: ");
                break;
            }
            case 4: {
                msgBuf.append((String)"DEBUG: ");
                break;
            }
            case 5: {
                msgBuf.append((String)"TRACE: ");
            }
        }
        if (msg instanceof ProfilerEvent) {
            msgBuf.append((String)msg.toString());
        } else {
            if (this.logLocationInfo && level != 5) {
                Throwable locationException = new Throwable();
                msgBuf.append((String)LogUtils.findCallingClassAndMethod((Throwable)locationException));
                msgBuf.append((String)" ");
            }
            if (msg != null) {
                msgBuf.append((String)String.valueOf((Object)msg));
            }
        }
        if (exception != null) {
            msgBuf.append((String)"\n");
            msgBuf.append((String)"\n");
            msgBuf.append((String)"EXCEPTION STACK TRACE:");
            msgBuf.append((String)"\n");
            msgBuf.append((String)"\n");
            msgBuf.append((String)Util.stackTraceToString((Throwable)exception));
        }
        String messageAsString = msgBuf.toString();
        System.err.println((String)messageAsString);
        return messageAsString;
    }
}

