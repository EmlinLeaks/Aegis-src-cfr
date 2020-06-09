/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiOutputStream;
import org.fusesource.jansi.WindowsAnsiOutputStream;
import org.fusesource.jansi.internal.CLibrary;

public class AnsiConsole {
    public static final PrintStream system_out = System.out;
    public static final PrintStream out = new PrintStream((OutputStream)AnsiConsole.wrapOutputStream((OutputStream)system_out));
    public static final PrintStream system_err = System.err;
    public static final PrintStream err = new PrintStream((OutputStream)AnsiConsole.wrapOutputStream((OutputStream)system_err));
    private static int installed;

    public static OutputStream wrapOutputStream(OutputStream stream) {
        if (Boolean.getBoolean((String)"jansi.passthrough")) {
            return stream;
        }
        if (Boolean.getBoolean((String)"jansi.strip")) {
            return new AnsiOutputStream((OutputStream)stream);
        }
        String os = System.getProperty((String)"os.name");
        if (os.startsWith((String)"Windows")) {
            try {
                return new WindowsAnsiOutputStream((OutputStream)stream);
            }
            catch (Throwable ignore) {
                return new AnsiOutputStream((OutputStream)stream);
            }
        }
        try {
            int rc = CLibrary.isatty((int)CLibrary.STDOUT_FILENO);
            if (rc != 0) return new FilterOutputStream((OutputStream)stream){

                public void close() throws java.io.IOException {
                    this.write((byte[])AnsiOutputStream.REST_CODE);
                    this.flush();
                    super.close();
                }
            };
            return new AnsiOutputStream((OutputStream)stream);
        }
        catch (NoClassDefFoundError ignore) {
            return new /* invalid duplicate definition of identical inner class */;
        }
        catch (UnsatisfiedLinkError ignore) {
            // empty catch block
        }
        return new /* invalid duplicate definition of identical inner class */;
    }

    public static PrintStream out() {
        return out;
    }

    public static PrintStream err() {
        return err;
    }

    public static synchronized void systemInstall() {
        if (++installed != 1) return;
        System.setOut((PrintStream)out);
        System.setErr((PrintStream)err);
    }

    public static synchronized void systemUninstall() {
        if (--installed != 0) return;
        System.setOut((PrintStream)system_out);
        System.setErr((PrintStream)system_err);
    }
}

