/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.log;

import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.profiler.ProfilerEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Jdk14Logger
implements Log {
    private static final Level DEBUG = Level.FINE;
    private static final Level ERROR = Level.SEVERE;
    private static final Level FATAL = Level.SEVERE;
    private static final Level INFO = Level.INFO;
    private static final Level TRACE = Level.FINEST;
    private static final Level WARN = Level.WARNING;
    protected Logger jdkLogger = null;

    public Jdk14Logger(String name) {
        this.jdkLogger = Logger.getLogger((String)name);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.jdkLogger.isLoggable((Level)Level.FINE);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.jdkLogger.isLoggable((Level)Level.SEVERE);
    }

    @Override
    public boolean isFatalEnabled() {
        return this.jdkLogger.isLoggable((Level)Level.SEVERE);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.jdkLogger.isLoggable((Level)Level.INFO);
    }

    @Override
    public boolean isTraceEnabled() {
        return this.jdkLogger.isLoggable((Level)Level.FINEST);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.jdkLogger.isLoggable((Level)Level.WARNING);
    }

    @Override
    public void logDebug(Object message) {
        this.logInternal((Level)DEBUG, (Object)message, null);
    }

    @Override
    public void logDebug(Object message, Throwable exception) {
        this.logInternal((Level)DEBUG, (Object)message, (Throwable)exception);
    }

    @Override
    public void logError(Object message) {
        this.logInternal((Level)ERROR, (Object)message, null);
    }

    @Override
    public void logError(Object message, Throwable exception) {
        this.logInternal((Level)ERROR, (Object)message, (Throwable)exception);
    }

    @Override
    public void logFatal(Object message) {
        this.logInternal((Level)FATAL, (Object)message, null);
    }

    @Override
    public void logFatal(Object message, Throwable exception) {
        this.logInternal((Level)FATAL, (Object)message, (Throwable)exception);
    }

    @Override
    public void logInfo(Object message) {
        this.logInternal((Level)INFO, (Object)message, null);
    }

    @Override
    public void logInfo(Object message, Throwable exception) {
        this.logInternal((Level)INFO, (Object)message, (Throwable)exception);
    }

    @Override
    public void logTrace(Object message) {
        this.logInternal((Level)TRACE, (Object)message, null);
    }

    @Override
    public void logTrace(Object message, Throwable exception) {
        this.logInternal((Level)TRACE, (Object)message, (Throwable)exception);
    }

    @Override
    public void logWarn(Object message) {
        this.logInternal((Level)WARN, (Object)message, null);
    }

    @Override
    public void logWarn(Object message, Throwable exception) {
        this.logInternal((Level)WARN, (Object)message, (Throwable)exception);
    }

    private static final int findCallerStackDepth(StackTraceElement[] stackTrace) {
        int numFrames = stackTrace.length;
        int i = 0;
        while (i < numFrames) {
            String callerClassName = stackTrace[i].getClassName();
            if (!callerClassName.startsWith((String)"com.mysql.jdbc")) return i;
            if (callerClassName.startsWith((String)"com.mysql.jdbc.compliance")) {
                return i;
            }
            ++i;
        }
        return 0;
    }

    private void logInternal(Level level, Object msg, Throwable exception) {
        if (!this.jdkLogger.isLoggable((Level)level)) return;
        String messageAsString = null;
        String callerMethodName = "N/A";
        String callerClassName = "N/A";
        if (msg instanceof ProfilerEvent) {
            messageAsString = msg.toString();
        } else {
            Throwable locationException = new Throwable();
            StackTraceElement[] locations = locationException.getStackTrace();
            int frameIdx = Jdk14Logger.findCallerStackDepth((StackTraceElement[])locations);
            if (frameIdx != 0) {
                callerClassName = locations[frameIdx].getClassName();
                callerMethodName = locations[frameIdx].getMethodName();
            }
            messageAsString = String.valueOf((Object)msg);
        }
        if (exception == null) {
            this.jdkLogger.logp((Level)level, (String)callerClassName, (String)callerMethodName, (String)messageAsString);
            return;
        }
        this.jdkLogger.logp((Level)level, (String)callerClassName, (String)callerMethodName, (String)messageAsString, (Throwable)exception);
    }
}

