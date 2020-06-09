/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import jline.WindowsTerminal;
import jline.internal.Configuration;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiOutputStream;
import org.fusesource.jansi.WindowsAnsiOutputStream;

public class AnsiWindowsTerminal
extends WindowsTerminal {
    private final boolean ansiSupported = AnsiWindowsTerminal.detectAnsiSupport();

    @Override
    public OutputStream wrapOutIfNeeded(OutputStream out) {
        return AnsiWindowsTerminal.wrapOutputStream((OutputStream)out);
    }

    private static OutputStream wrapOutputStream(OutputStream stream) {
        if (!Configuration.isWindows()) return stream;
        try {
            return new WindowsAnsiOutputStream((OutputStream)stream);
        }
        catch (Throwable ignore) {
            return new AnsiOutputStream((OutputStream)stream);
        }
    }

    private static boolean detectAnsiSupport() {
        OutputStream out = AnsiConsole.wrapOutputStream((OutputStream)new ByteArrayOutputStream());
        try {
            out.close();
            return out instanceof WindowsAnsiOutputStream;
        }
        catch (Exception e) {
            // empty catch block
        }
        return out instanceof WindowsAnsiOutputStream;
    }

    @Override
    public boolean isAnsiSupported() {
        return this.ansiSupported;
    }

    @Override
    public boolean hasWeirdWrap() {
        return false;
    }
}

