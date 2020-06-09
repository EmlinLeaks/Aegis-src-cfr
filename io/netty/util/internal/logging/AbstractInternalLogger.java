/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.AbstractInternalLogger;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class AbstractInternalLogger
implements InternalLogger,
Serializable {
    private static final long serialVersionUID = -6382972526573193470L;
    static final String EXCEPTION_MESSAGE = "Unexpected exception:";
    private final String name;

    protected AbstractInternalLogger(String name) {
        if (name == null) {
            throw new NullPointerException((String)"name");
        }
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isEnabled(InternalLogLevel level) {
        switch (1.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
            case 1: {
                return this.isTraceEnabled();
            }
            case 2: {
                return this.isDebugEnabled();
            }
            case 3: {
                return this.isInfoEnabled();
            }
            case 4: {
                return this.isWarnEnabled();
            }
            case 5: {
                return this.isErrorEnabled();
            }
        }
        throw new Error();
    }

    @Override
    public void trace(Throwable t) {
        this.trace((String)EXCEPTION_MESSAGE, (Throwable)t);
    }

    @Override
    public void debug(Throwable t) {
        this.debug((String)EXCEPTION_MESSAGE, (Throwable)t);
    }

    @Override
    public void info(Throwable t) {
        this.info((String)EXCEPTION_MESSAGE, (Throwable)t);
    }

    @Override
    public void warn(Throwable t) {
        this.warn((String)EXCEPTION_MESSAGE, (Throwable)t);
    }

    @Override
    public void error(Throwable t) {
        this.error((String)EXCEPTION_MESSAGE, (Throwable)t);
    }

    @Override
    public void log(InternalLogLevel level, String msg, Throwable cause) {
        switch (1.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
            case 1: {
                this.trace((String)msg, (Throwable)cause);
                return;
            }
            case 2: {
                this.debug((String)msg, (Throwable)cause);
                return;
            }
            case 3: {
                this.info((String)msg, (Throwable)cause);
                return;
            }
            case 4: {
                this.warn((String)msg, (Throwable)cause);
                return;
            }
            case 5: {
                this.error((String)msg, (Throwable)cause);
                return;
            }
        }
        throw new Error();
    }

    @Override
    public void log(InternalLogLevel level, Throwable cause) {
        switch (1.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
            case 1: {
                this.trace((Throwable)cause);
                return;
            }
            case 2: {
                this.debug((Throwable)cause);
                return;
            }
            case 3: {
                this.info((Throwable)cause);
                return;
            }
            case 4: {
                this.warn((Throwable)cause);
                return;
            }
            case 5: {
                this.error((Throwable)cause);
                return;
            }
        }
        throw new Error();
    }

    @Override
    public void log(InternalLogLevel level, String msg) {
        switch (1.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
            case 1: {
                this.trace((String)msg);
                return;
            }
            case 2: {
                this.debug((String)msg);
                return;
            }
            case 3: {
                this.info((String)msg);
                return;
            }
            case 4: {
                this.warn((String)msg);
                return;
            }
            case 5: {
                this.error((String)msg);
                return;
            }
        }
        throw new Error();
    }

    @Override
    public void log(InternalLogLevel level, String format, Object arg) {
        switch (1.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
            case 1: {
                this.trace((String)format, (Object)arg);
                return;
            }
            case 2: {
                this.debug((String)format, (Object)arg);
                return;
            }
            case 3: {
                this.info((String)format, (Object)arg);
                return;
            }
            case 4: {
                this.warn((String)format, (Object)arg);
                return;
            }
            case 5: {
                this.error((String)format, (Object)arg);
                return;
            }
        }
        throw new Error();
    }

    @Override
    public void log(InternalLogLevel level, String format, Object argA, Object argB) {
        switch (1.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
            case 1: {
                this.trace((String)format, (Object)argA, (Object)argB);
                return;
            }
            case 2: {
                this.debug((String)format, (Object)argA, (Object)argB);
                return;
            }
            case 3: {
                this.info((String)format, (Object)argA, (Object)argB);
                return;
            }
            case 4: {
                this.warn((String)format, (Object)argA, (Object)argB);
                return;
            }
            case 5: {
                this.error((String)format, (Object)argA, (Object)argB);
                return;
            }
        }
        throw new Error();
    }

    @Override
    public void log(InternalLogLevel level, String format, Object ... arguments) {
        switch (1.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
            case 1: {
                this.trace((String)format, (Object[])arguments);
                return;
            }
            case 2: {
                this.debug((String)format, (Object[])arguments);
                return;
            }
            case 3: {
                this.info((String)format, (Object[])arguments);
                return;
            }
            case 4: {
                this.warn((String)format, (Object[])arguments);
                return;
            }
            case 5: {
                this.error((String)format, (Object[])arguments);
                return;
            }
        }
        throw new Error();
    }

    protected Object readResolve() throws ObjectStreamException {
        return InternalLoggerFactory.getInstance((String)this.name());
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + '(' + this.name() + ')';
    }
}

