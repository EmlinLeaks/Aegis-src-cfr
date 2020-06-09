/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.logging;

import io.netty.util.internal.logging.InternalLogLevel;

public enum LogLevel {
    TRACE((InternalLogLevel)InternalLogLevel.TRACE),
    DEBUG((InternalLogLevel)InternalLogLevel.DEBUG),
    INFO((InternalLogLevel)InternalLogLevel.INFO),
    WARN((InternalLogLevel)InternalLogLevel.WARN),
    ERROR((InternalLogLevel)InternalLogLevel.ERROR);
    
    private final InternalLogLevel internalLevel;

    private LogLevel(InternalLogLevel internalLevel) {
        this.internalLevel = internalLevel;
    }

    public InternalLogLevel toInternalLevel() {
        return this.internalLevel;
    }
}

