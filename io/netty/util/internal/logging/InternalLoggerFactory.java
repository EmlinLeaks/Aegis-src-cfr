/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.JdkLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

public abstract class InternalLoggerFactory {
    private static volatile InternalLoggerFactory defaultFactory;

    private static InternalLoggerFactory newDefaultFactory(String name) {
        InternalLoggerFactory f;
        try {
            f = new Slf4JLoggerFactory((boolean)true);
            f.newInstance((String)name).debug((String)"Using SLF4J as the default logging framework");
            return f;
        }
        catch (Throwable ignore1) {
            try {
                f = Log4JLoggerFactory.INSTANCE;
                f.newInstance((String)name).debug((String)"Using Log4J as the default logging framework");
                return f;
            }
            catch (Throwable ignore2) {
                try {
                    f = Log4J2LoggerFactory.INSTANCE;
                    f.newInstance((String)name).debug((String)"Using Log4J2 as the default logging framework");
                    return f;
                }
                catch (Throwable ignore3) {
                    f = JdkLoggerFactory.INSTANCE;
                    f.newInstance((String)name).debug((String)"Using java.util.logging as the default logging framework");
                }
            }
        }
        return f;
    }

    public static InternalLoggerFactory getDefaultFactory() {
        if (defaultFactory != null) return defaultFactory;
        defaultFactory = InternalLoggerFactory.newDefaultFactory((String)InternalLoggerFactory.class.getName());
        return defaultFactory;
    }

    public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
        if (defaultFactory == null) {
            throw new NullPointerException((String)"defaultFactory");
        }
        InternalLoggerFactory.defaultFactory = defaultFactory;
    }

    public static InternalLogger getInstance(Class<?> clazz) {
        return InternalLoggerFactory.getInstance((String)clazz.getName());
    }

    public static InternalLogger getInstance(String name) {
        return InternalLoggerFactory.getDefaultFactory().newInstance((String)name);
    }

    protected abstract InternalLogger newInstance(String var1);
}

