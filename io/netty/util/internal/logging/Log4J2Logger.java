/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.MessageFactory
 *  org.apache.logging.log4j.spi.ExtendedLogger
 *  org.apache.logging.log4j.spi.ExtendedLoggerWrapper
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4J2Logger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

class Log4J2Logger
extends ExtendedLoggerWrapper
implements InternalLogger {
    private static final long serialVersionUID = 5485418394879791397L;
    private static final boolean VARARGS_ONLY = AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

        public Boolean run() {
            try {
                Logger.class.getMethod((String)"debug", String.class, Object.class);
                return Boolean.valueOf((boolean)false);
            }
            catch (java.lang.NoSuchMethodException ignore) {
                return Boolean.valueOf((boolean)true);
            }
            catch (java.lang.SecurityException ignore) {
                return Boolean.valueOf((boolean)false);
            }
        }
    }).booleanValue();

    Log4J2Logger(Logger logger) {
        super((ExtendedLogger)((ExtendedLogger)logger), (String)logger.getName(), (MessageFactory)logger.getMessageFactory());
        if (!VARARGS_ONLY) return;
        throw new UnsupportedOperationException((String)"Log4J2 version mismatch");
    }

    @Override
    public String name() {
        return this.getName();
    }

    @Override
    public void trace(Throwable t) {
        this.log((Level)Level.TRACE, (String)"Unexpected exception:", (Throwable)t);
    }

    @Override
    public void debug(Throwable t) {
        this.log((Level)Level.DEBUG, (String)"Unexpected exception:", (Throwable)t);
    }

    @Override
    public void info(Throwable t) {
        this.log((Level)Level.INFO, (String)"Unexpected exception:", (Throwable)t);
    }

    @Override
    public void warn(Throwable t) {
        this.log((Level)Level.WARN, (String)"Unexpected exception:", (Throwable)t);
    }

    @Override
    public void error(Throwable t) {
        this.log((Level)Level.ERROR, (String)"Unexpected exception:", (Throwable)t);
    }

    @Override
    public boolean isEnabled(InternalLogLevel level) {
        return this.isEnabled((Level)Log4J2Logger.toLevel((InternalLogLevel)level));
    }

    @Override
    public void log(InternalLogLevel level, String msg) {
        this.log((Level)Log4J2Logger.toLevel((InternalLogLevel)level), (String)msg);
    }

    @Override
    public void log(InternalLogLevel level, String format, Object arg) {
        this.log((Level)Log4J2Logger.toLevel((InternalLogLevel)level), (String)format, (Object)arg);
    }

    @Override
    public void log(InternalLogLevel level, String format, Object argA, Object argB) {
        this.log((Level)Log4J2Logger.toLevel((InternalLogLevel)level), (String)format, (Object)argA, (Object)argB);
    }

    @Override
    public void log(InternalLogLevel level, String format, Object ... arguments) {
        this.log((Level)Log4J2Logger.toLevel((InternalLogLevel)level), (String)format, (Object[])arguments);
    }

    @Override
    public void log(InternalLogLevel level, String msg, Throwable t) {
        this.log((Level)Log4J2Logger.toLevel((InternalLogLevel)level), (String)msg, (Throwable)t);
    }

    @Override
    public void log(InternalLogLevel level, Throwable t) {
        this.log((Level)Log4J2Logger.toLevel((InternalLogLevel)level), (String)"Unexpected exception:", (Throwable)t);
    }

    private static Level toLevel(InternalLogLevel level) {
        switch (2.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
            case 1: {
                return Level.INFO;
            }
            case 2: {
                return Level.DEBUG;
            }
            case 3: {
                return Level.WARN;
            }
            case 4: {
                return Level.ERROR;
            }
            case 5: {
                return Level.TRACE;
            }
        }
        throw new Error();
    }
}

