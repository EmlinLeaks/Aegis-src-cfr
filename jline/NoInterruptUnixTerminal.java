/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline;

import jline.UnixTerminal;
import jline.internal.TerminalLineSettings;

public class NoInterruptUnixTerminal
extends UnixTerminal {
    @Override
    public void init() throws Exception {
        super.init();
        this.getSettings().set((String)"intr undef");
    }

    @Override
    public void restore() throws Exception {
        this.getSettings().set((String)"intr ^C");
        super.restore();
    }
}

