/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.log;

import com.mysql.jdbc.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JLogger
implements Log {
    private Logger log;

    public Slf4JLogger(String name) {
        this.log = LoggerFactory.getLogger((String)name);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.log.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return this.log.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return this.log.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return this.log.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return this.log.isWarnEnabled();
    }

    @Override
    public void logDebug(Object msg) {
        this.log.debug((String)msg.toString());
    }

    @Override
    public void logDebug(Object msg, Throwable thrown) {
        this.log.debug((String)msg.toString(), (Throwable)thrown);
    }

    @Override
    public void logError(Object msg) {
        this.log.error((String)msg.toString());
    }

    @Override
    public void logError(Object msg, Throwable thrown) {
        this.log.error((String)msg.toString(), (Throwable)thrown);
    }

    @Override
    public void logFatal(Object msg) {
        this.log.error((String)msg.toString());
    }

    @Override
    public void logFatal(Object msg, Throwable thrown) {
        this.log.error((String)msg.toString(), (Throwable)thrown);
    }

    @Override
    public void logInfo(Object msg) {
        this.log.info((String)msg.toString());
    }

    @Override
    public void logInfo(Object msg, Throwable thrown) {
        this.log.info((String)msg.toString(), (Throwable)thrown);
    }

    @Override
    public void logTrace(Object msg) {
        this.log.trace((String)msg.toString());
    }

    @Override
    public void logTrace(Object msg, Throwable thrown) {
        this.log.trace((String)msg.toString(), (Throwable)thrown);
    }

    @Override
    public void logWarn(Object msg) {
        this.log.warn((String)msg.toString());
    }

    @Override
    public void logWarn(Object msg, Throwable thrown) {
        this.log.warn((String)msg.toString(), (Throwable)thrown);
    }
}

