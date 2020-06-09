/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import io.netty.util.internal.logging.FormattingTuple;
import io.netty.util.internal.logging.MessageFormatter;
import org.apache.commons.logging.Log;

@Deprecated
class CommonsLogger
extends AbstractInternalLogger {
    private static final long serialVersionUID = 8647838678388394885L;
    private final transient Log logger;

    CommonsLogger(Log logger, String name) {
        super((String)name);
        if (logger == null) {
            throw new NullPointerException((String)"logger");
        }
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        this.logger.trace((Object)msg);
    }

    @Override
    public void trace(String format, Object arg) {
        if (!this.logger.isTraceEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.logger.trace((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        if (!this.logger.isTraceEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.logger.trace((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void trace(String format, Object ... arguments) {
        if (!this.logger.isTraceEnabled()) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])arguments);
        this.logger.trace((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void trace(String msg, Throwable t) {
        this.logger.trace((Object)msg, (Throwable)t);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        this.logger.debug((Object)msg);
    }

    @Override
    public void debug(String format, Object arg) {
        if (!this.logger.isDebugEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.logger.debug((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        if (!this.logger.isDebugEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.logger.debug((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void debug(String format, Object ... arguments) {
        if (!this.logger.isDebugEnabled()) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])arguments);
        this.logger.debug((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void debug(String msg, Throwable t) {
        this.logger.debug((Object)msg, (Throwable)t);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        this.logger.info((Object)msg);
    }

    @Override
    public void info(String format, Object arg) {
        if (!this.logger.isInfoEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.logger.info((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        if (!this.logger.isInfoEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.logger.info((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void info(String format, Object ... arguments) {
        if (!this.logger.isInfoEnabled()) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])arguments);
        this.logger.info((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void info(String msg, Throwable t) {
        this.logger.info((Object)msg, (Throwable)t);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        this.logger.warn((Object)msg);
    }

    @Override
    public void warn(String format, Object arg) {
        if (!this.logger.isWarnEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.logger.warn((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        if (!this.logger.isWarnEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.logger.warn((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void warn(String format, Object ... arguments) {
        if (!this.logger.isWarnEnabled()) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])arguments);
        this.logger.warn((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void warn(String msg, Throwable t) {
        this.logger.warn((Object)msg, (Throwable)t);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        this.logger.error((Object)msg);
    }

    @Override
    public void error(String format, Object arg) {
        if (!this.logger.isErrorEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)arg);
        this.logger.error((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        if (!this.logger.isErrorEnabled()) return;
        FormattingTuple ft = MessageFormatter.format((String)format, (Object)argA, (Object)argB);
        this.logger.error((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void error(String format, Object ... arguments) {
        if (!this.logger.isErrorEnabled()) return;
        FormattingTuple ft = MessageFormatter.arrayFormat((String)format, (Object[])arguments);
        this.logger.error((Object)ft.getMessage(), (Throwable)ft.getThrowable());
    }

    @Override
    public void error(String msg, Throwable t) {
        this.logger.error((Object)msg, (Throwable)t);
    }
}

