/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import io.netty.util.internal.logging.FormattingTuple;
import io.netty.util.internal.logging.MessageFormatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

class JdkLogger
extends AbstractInternalLogger {
    private static final long serialVersionUID = -1767272577989225979L;
    final transient Logger logger;
    static final String SELF = JdkLogger.class.getName();
    static final String SUPER = AbstractInternalLogger.class.getName();

    JdkLogger(Logger logger) {
        super((String)logger.getName());
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isLoggable((Level)Level.FINEST);
    }

    @Override
    public void trace(String msg) {
        if (!this.logger.isLoggable((Level)Level.FINEST)) return;
        this.log((String)SELF, (Level)Level.FINEST, (String)msg, null);
    }

    @Override
    public void trace(String format, Object arg) {
        if (!this.logger.isLoggable((Level)Level.FINEST)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.log((String)SELF, (Level)Level.FINEST, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        if (!this.logger.isLoggable((Level)Level.FINEST)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.log((String)SELF, (Level)Level.FINEST, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void trace(String format, Object ... argArray) {
        if (!this.logger.isLoggable((Level)Level.FINEST)) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])argArray);
        this.log((String)SELF, (Level)Level.FINEST, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (!this.logger.isLoggable((Level)Level.FINEST)) return;
        this.log((String)SELF, (Level)Level.FINEST, (String)msg, (Throwable)t);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isLoggable((Level)Level.FINE);
    }

    @Override
    public void debug(String msg) {
        if (!this.logger.isLoggable((Level)Level.FINE)) return;
        this.log((String)SELF, (Level)Level.FINE, (String)msg, null);
    }

    @Override
    public void debug(String format, Object arg) {
        if (!this.logger.isLoggable((Level)Level.FINE)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.log((String)SELF, (Level)Level.FINE, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        if (!this.logger.isLoggable((Level)Level.FINE)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.log((String)SELF, (Level)Level.FINE, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void debug(String format, Object ... argArray) {
        if (!this.logger.isLoggable((Level)Level.FINE)) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])argArray);
        this.log((String)SELF, (Level)Level.FINE, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (!this.logger.isLoggable((Level)Level.FINE)) return;
        this.log((String)SELF, (Level)Level.FINE, (String)msg, (Throwable)t);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isLoggable((Level)Level.INFO);
    }

    @Override
    public void info(String msg) {
        if (!this.logger.isLoggable((Level)Level.INFO)) return;
        this.log((String)SELF, (Level)Level.INFO, (String)msg, null);
    }

    @Override
    public void info(String format, Object arg) {
        if (!this.logger.isLoggable((Level)Level.INFO)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.log((String)SELF, (Level)Level.INFO, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        if (!this.logger.isLoggable((Level)Level.INFO)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.log((String)SELF, (Level)Level.INFO, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void info(String format, Object ... argArray) {
        if (!this.logger.isLoggable((Level)Level.INFO)) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])argArray);
        this.log((String)SELF, (Level)Level.INFO, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void info(String msg, Throwable t) {
        if (!this.logger.isLoggable((Level)Level.INFO)) return;
        this.log((String)SELF, (Level)Level.INFO, (String)msg, (Throwable)t);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isLoggable((Level)Level.WARNING);
    }

    @Override
    public void warn(String msg) {
        if (!this.logger.isLoggable((Level)Level.WARNING)) return;
        this.log((String)SELF, (Level)Level.WARNING, (String)msg, null);
    }

    @Override
    public void warn(String format, Object arg) {
        if (!this.logger.isLoggable((Level)Level.WARNING)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.log((String)SELF, (Level)Level.WARNING, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        if (!this.logger.isLoggable((Level)Level.WARNING)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.log((String)SELF, (Level)Level.WARNING, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void warn(String format, Object ... argArray) {
        if (!this.logger.isLoggable((Level)Level.WARNING)) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])argArray);
        this.log((String)SELF, (Level)Level.WARNING, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (!this.logger.isLoggable((Level)Level.WARNING)) return;
        this.log((String)SELF, (Level)Level.WARNING, (String)msg, (Throwable)t);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isLoggable((Level)Level.SEVERE);
    }

    @Override
    public void error(String msg) {
        if (!this.logger.isLoggable((Level)Level.SEVERE)) return;
        this.log((String)SELF, (Level)Level.SEVERE, (String)msg, null);
    }

    @Override
    public void error(String format, Object arg) {
        if (!this.logger.isLoggable((Level)Level.SEVERE)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.log((String)SELF, (Level)Level.SEVERE, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        if (!this.logger.isLoggable((Level)Level.SEVERE)) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.log((String)SELF, (Level)Level.SEVERE, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void error(String format, Object ... arguments) {
        if (!this.logger.isLoggable((Level)Level.SEVERE)) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])arguments);
        this.log((String)SELF, (Level)Level.SEVERE, (String)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void error(String msg, Throwable t) {
        if (!this.logger.isLoggable((Level)Level.SEVERE)) return;
        this.log((String)SELF, (Level)Level.SEVERE, (String)msg, (Throwable)t);
    }

    private void log(String callerFQCN, Level level, String msg, Throwable t) {
        LogRecord record = new LogRecord((Level)level, (String)msg);
        record.setLoggerName((String)this.name());
        record.setThrown((Throwable)t);
        JdkLogger.fillCallerData((String)callerFQCN, (LogRecord)record);
        this.logger.log((LogRecord)record);
    }

    private static void fillCallerData(String callerFQCN, LogRecord record) {
        StackTraceElement[] steArray = new Throwable().getStackTrace();
        int selfIndex = -1;
        for (int i = 0; i < steArray.length; ++i) {
            String className = steArray[i].getClassName();
            if (!className.equals((Object)callerFQCN) && !className.equals((Object)SUPER)) continue;
            selfIndex = i;
            break;
        }
        int found = -1;
        for (int i = selfIndex + 1; i < steArray.length; ++i) {
            String className = steArray[i].getClassName();
            if (className.equals((Object)callerFQCN) || className.equals((Object)SUPER)) continue;
            found = i;
            break;
        }
        if (found == -1) return;
        StackTraceElement ste = steArray[found];
        record.setSourceClassName((String)ste.getClassName());
        record.setSourceMethodName((String)ste.getMethodName());
    }
}

