/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import jline.AnsiWindowsTerminal;
import jline.Terminal;
import jline.TerminalFactory;
import jline.UnixTerminal;
import jline.UnsupportedTerminal;
import jline.internal.Configuration;
import jline.internal.Log;
import jline.internal.Preconditions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TerminalFactory {
    public static final String JLINE_TERMINAL = "jline.terminal";
    public static final String AUTO = "auto";
    public static final String UNIX = "unix";
    public static final String WIN = "win";
    public static final String WINDOWS = "windows";
    public static final String NONE = "none";
    public static final String OFF = "off";
    public static final String FALSE = "false";
    private static Terminal term = null;
    private static final Map<Flavor, Class<? extends Terminal>> FLAVORS = new HashMap<Flavor, Class<? extends Terminal>>();

    public static synchronized Terminal create() {
        Terminal t;
        block13 : {
            if (Log.TRACE) {
                Log.trace((Object[])new Object[]{new Throwable((String)"CREATE MARKER")});
            }
            String type = Configuration.getString((String)JLINE_TERMINAL, (String)AUTO);
            if ("dumb".equals((Object)System.getenv((String)"TERM"))) {
                type = NONE;
                Log.debug((Object[])new Object[]{"$TERM=dumb; setting type=", type});
            }
            Log.debug((Object[])new Object[]{"Creating terminal; type=", type});
            try {
                String tmp = type.toLowerCase();
                if (tmp.equals((Object)UNIX)) {
                    t = TerminalFactory.getFlavor((Flavor)Flavor.UNIX);
                    break block13;
                }
                if (tmp.equals((Object)WIN) | tmp.equals((Object)WINDOWS)) {
                    t = TerminalFactory.getFlavor((Flavor)Flavor.WINDOWS);
                    break block13;
                }
                if (tmp.equals((Object)NONE) || tmp.equals((Object)OFF) || tmp.equals((Object)FALSE)) {
                    t = new UnsupportedTerminal();
                    break block13;
                }
                if (tmp.equals((Object)AUTO)) {
                    String os = Configuration.getOsName();
                    Flavor flavor = Flavor.UNIX;
                    if (os.contains((CharSequence)WINDOWS)) {
                        flavor = Flavor.WINDOWS;
                    }
                    t = TerminalFactory.getFlavor((Flavor)flavor);
                    break block13;
                }
                try {
                    t = (Terminal)Thread.currentThread().getContextClassLoader().loadClass((String)type).newInstance();
                }
                catch (Exception e) {
                    throw new IllegalArgumentException((String)MessageFormat.format((String)"Invalid terminal type: {0}", (Object[])new Object[]{type}), (Throwable)e);
                }
            }
            catch (Exception e) {
                Log.error((Object[])new Object[]{"Failed to construct terminal; falling back to unsupported", e});
                t = new UnsupportedTerminal();
            }
        }
        Log.debug((Object[])new Object[]{"Created Terminal: ", t});
        try {
            t.init();
            return t;
        }
        catch (Throwable e) {
            Log.error((Object[])new Object[]{"Terminal initialization failed; falling back to unsupported", e});
            return new UnsupportedTerminal();
        }
    }

    public static synchronized void reset() {
        term = null;
    }

    public static synchronized void resetIf(Terminal t) {
        if (t != term) return;
        TerminalFactory.reset();
    }

    public static synchronized void configure(String type) {
        Preconditions.checkNotNull(type);
        System.setProperty((String)JLINE_TERMINAL, (String)type);
    }

    public static synchronized void configure(Type type) {
        Preconditions.checkNotNull(type);
        TerminalFactory.configure((String)type.name().toLowerCase());
    }

    public static synchronized Terminal get() {
        if (term != null) return term;
        term = TerminalFactory.create();
        return term;
    }

    public static Terminal getFlavor(Flavor flavor) throws Exception {
        Class<? extends Terminal> type = FLAVORS.get((Object)((Object)flavor));
        if (type == null) throw new InternalError();
        return type.newInstance();
    }

    public static void registerFlavor(Flavor flavor, Class<? extends Terminal> type) {
        FLAVORS.put((Flavor)flavor, type);
    }

    static {
        TerminalFactory.registerFlavor((Flavor)Flavor.WINDOWS, AnsiWindowsTerminal.class);
        TerminalFactory.registerFlavor((Flavor)Flavor.UNIX, UnixTerminal.class);
    }
}

