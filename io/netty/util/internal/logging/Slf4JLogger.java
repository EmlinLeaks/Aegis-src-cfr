/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import org.slf4j.Logger;

final class Slf4JLogger
extends AbstractInternalLogger {
    private static final long serialVersionUID = 108038972685130825L;
    private final transient Logger logger;

    Slf4JLogger(Logger logger) {
        super((String)logger.getName());
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        this.logger.trace((String)msg);
    }

    @Override
    public void trace(String format, Object arg) {
        this.logger.trace((String)format, (Object)arg);
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        this.logger.trace((String)format, (Object)argA, (Object)argB);
    }

    @Override
    public void trace(String format, Object ... argArray) {
        this.logger.trace((String)format, (Object[])argArray);
    }

    @Override
    public void trace(String msg, Throwable t) {
        this.logger.trace((String)msg, (Throwable)t);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        this.logger.debug((String)msg);
    }

    @Override
    public void debug(String format, Object arg) {
        this.logger.debug((String)format, (Object)arg);
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        this.logger.debug((String)format, (Object)argA, (Object)argB);
    }

    @Override
    public void debug(String format, Object ... argArray) {
        this.logger.debug((String)format, (Object[])argArray);
    }

    @Override
    public void debug(String msg, Throwable t) {
        this.logger.debug((String)msg, (Throwable)t);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        this.logger.info((String)msg);
    }

    @Override
    public void info(String format, Object arg) {
        this.logger.info((String)format, (Object)arg);
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        this.logger.info((String)format, (Object)argA, (Object)argB);
    }

    @Override
    public void info(String format, Object ... argArray) {
        this.logger.info((String)format, (Object[])argArray);
    }

    @Override
    public void info(String msg, Throwable t) {
        this.logger.info((String)msg, (Throwable)t);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        this.logger.warn((String)msg);
    }

    @Override
    public void warn(String format, Object arg) {
        this.logger.warn((String)format, (Object)arg);
    }

    @Override
    public void warn(String format, Object ... argArray) {
        this.logger.warn((String)format, (Object[])argArray);
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        this.logger.warn((String)format, (Object)argA, (Object)argB);
    }

    @Override
    public void warn(String msg, Throwable t) {
        this.logger.warn((String)msg, (Throwable)t);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        this.logger.error((String)msg);
    }

    @Override
    public void error(String format, Object arg) {
        this.logger.error((String)format, (Object)arg);
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        this.logger.error((String)format, (Object)argA, (Object)argB);
    }

    @Override
    public void error(String format, Object ... argArray) {
        this.logger.error((String)format, (Object[])argArray);
    }

    @Override
    public void error(String msg, Throwable t) {
        this.logger.error((String)msg, (Throwable)t);
    }
}

