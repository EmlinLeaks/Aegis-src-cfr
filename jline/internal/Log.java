/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.internal;

import java.io.PrintStream;
import jline.internal.Log;
import jline.internal.Preconditions;
import jline.internal.TestAccessible;

public final class Log {
    public static final boolean TRACE = Boolean.getBoolean((String)(Log.class.getName() + ".trace"));
    public static final boolean DEBUG = TRACE || Boolean.getBoolean((String)(Log.class.getName() + ".debug"));
    private static PrintStream output = System.err;

    public static PrintStream getOutput() {
        return output;
    }

    public static void setOutput(PrintStream out) {
        output = Preconditions.checkNotNull(out);
    }

    @TestAccessible
    static void render(PrintStream out, Object message) {
        if (!message.getClass().isArray()) {
            out.print((Object)message);
            return;
        }
        Object[] array = (Object[])message;
        out.print((String)"[");
        int i = 0;
        do {
            if (i >= array.length) {
                out.print((String)"]");
                return;
            }
            out.print((Object)array[i]);
            if (i + 1 < array.length) {
                out.print((String)",");
            }
            ++i;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @TestAccessible
    static void log(Level level, Object ... messages) {
        PrintStream printStream = output;
        // MONITORENTER : printStream
        output.format((String)"[%s] ", (Object[])new Object[]{level});
        int i = 0;
        do {
            if (i >= messages.length) {
                output.println();
                output.flush();
                // MONITOREXIT : printStream
                return;
            }
            if (i + 1 == messages.length && messages[i] instanceof Throwable) {
                output.println();
                ((Throwable)messages[i]).printStackTrace((PrintStream)output);
            } else {
                Log.render((PrintStream)output, (Object)messages[i]);
            }
            ++i;
        } while (true);
    }

    public static void trace(Object ... messages) {
        if (!TRACE) return;
        Log.log((Level)Level.TRACE, (Object[])messages);
    }

    public static void debug(Object ... messages) {
        if (!TRACE) {
            if (!DEBUG) return;
        }
        Log.log((Level)Level.DEBUG, (Object[])messages);
    }

    public static void info(Object ... messages) {
        Log.log((Level)Level.INFO, (Object[])messages);
    }

    public static void warn(Object ... messages) {
        Log.log((Level)Level.WARN, (Object[])messages);
    }

    public static void error(Object ... messages) {
        Log.log((Level)Level.ERROR, (Object[])messages);
    }
}

