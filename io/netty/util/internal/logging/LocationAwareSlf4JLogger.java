/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

final class LocationAwareSlf4JLogger
extends AbstractInternalLogger {
    static final String FQCN = LocationAwareSlf4JLogger.class.getName();
    private static final long serialVersionUID = -8292030083201538180L;
    private final transient LocationAwareLogger logger;

    LocationAwareSlf4JLogger(LocationAwareLogger logger) {
        super((String)logger.getName());
        this.logger = logger;
    }

    private void log(int level, String message) {
        this.logger.log(null, (String)FQCN, (int)level, (String)message, null, null);
    }

    private void log(int level, String message, Throwable cause) {
        this.logger.log(null, (String)FQCN, (int)level, (String)message, null, (Throwable)cause);
    }

    private void log(int level, FormattingTuple tuple) {
        this.logger.log(null, (String)FQCN, (int)level, (String)tuple.getMessage(), (Object[])tuple.getArgArray(), (Throwable)tuple.getThrowable());
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        if (!this.isTraceEnabled()) return;
        this.log((int)0, (String)msg);
    }

    @Override
    public void trace(String format, Object arg) {
        if (!this.isTraceEnabled()) return;
        this.log((int)0, (FormattingTuple)MessageFormatter.format((String)format, (Object)arg));
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        if (!this.isTraceEnabled()) return;
        this.log((int)0, (FormattingTuple)MessageFormatter.format((String)format, (Object)argA, (Object)argB));
    }

    @Override
    public void trace(String format, Object ... argArray) {
        if (!this.isTraceEnabled()) return;
        this.log((int)0, (FormattingTuple)MessageFormatter.arrayFormat((String)format, (Object[])argArray));
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (!this.isTraceEnabled()) return;
        this.log((int)0, (String)msg, (Throwable)t);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if (!this.isDebugEnabled()) return;
        this.log((int)10, (String)msg);
    }

    @Override
    public void debug(String format, Object arg) {
        if (!this.isDebugEnabled()) return;
        this.log((int)10, (FormattingTuple)MessageFormatter.format((String)format, (Object)arg));
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        if (!this.isDebugEnabled()) return;
        this.log((int)10, (FormattingTuple)MessageFormatter.format((String)format, (Object)argA, (Object)argB));
    }

    @Override
    public void debug(String format, Object ... argArray) {
        if (!this.isDebugEnabled()) return;
        this.log((int)10, (FormattingTuple)MessageFormatter.arrayFormat((String)format, (Object[])argArray));
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (!this.isDebugEnabled()) return;
        this.log((int)10, (String)msg, (Throwable)t);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        if (!this.isInfoEnabled()) return;
        this.log((int)20, (String)msg);
    }

    @Override
    public void info(String format, Object arg) {
        if (!this.isInfoEnabled()) return;
        this.log((int)20, (FormattingTuple)MessageFormatter.format((String)format, (Object)arg));
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        if (!this.isInfoEnabled()) return;
        this.log((int)20, (FormattingTuple)MessageFormatter.format((String)format, (Object)argA, (Object)argB));
    }

    @Override
    public void info(String format, Object ... argArray) {
        if (!this.isInfoEnabled()) return;
        this.log((int)20, (FormattingTuple)MessageFormatter.arrayFormat((String)format, (Object[])argArray));
    }

    @Override
    public void info(String msg, Throwable t) {
        if (!this.isInfoEnabled()) return;
        this.log((int)20, (String)msg, (Throwable)t);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        if (!this.isWarnEnabled()) return;
        this.log((int)30, (String)msg);
    }

    @Override
    public void warn(String format, Object arg) {
        if (!this.isWarnEnabled()) return;
        this.log((int)30, (FormattingTuple)MessageFormatter.format((String)format, (Object)arg));
    }

    @Override
    public void warn(String format, Object ... argArray) {
        if (!this.isWarnEnabled()) return;
        this.log((int)30, (FormattingTuple)MessageFormatter.arrayFormat((String)format, (Object[])argArray));
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        if (!this.isWarnEnabled()) return;
        this.log((int)30, (FormattingTuple)MessageFormatter.format((String)format, (Object)argA, (Object)argB));
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (!this.isWarnEnabled()) return;
        this.log((int)30, (String)msg, (Throwable)t);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        if (!this.isErrorEnabled()) return;
        this.log((int)40, (String)msg);
    }

    @Override
    public void error(String format, Object arg) {
        if (!this.isErrorEnabled()) return;
        this.log((int)40, (FormattingTuple)MessageFormatter.format((String)format, (Object)arg));
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        if (!this.isErrorEnabled()) return;
        this.log((int)40, (FormattingTuple)MessageFormatter.format((String)format, (Object)argA, (Object)argB));
    }

    @Override
    public void error(String format, Object ... argArray) {
        if (!this.isErrorEnabled()) return;
        this.log((int)40, (FormattingTuple)MessageFormatter.arrayFormat((String)format, (Object[])argArray));
    }

    @Override
    public void error(String msg, Throwable t) {
        if (!this.isErrorEnabled()) return;
        this.log((int)40, (String)msg, (Throwable)t);
    }
}

