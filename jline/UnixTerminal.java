/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline;

import jline.TerminalSupport;
import jline.internal.Log;
import jline.internal.TerminalLineSettings;

public class UnixTerminal
extends TerminalSupport {
    private final TerminalLineSettings settings = new TerminalLineSettings();

    public UnixTerminal() throws Exception {
        super((boolean)true);
    }

    protected TerminalLineSettings getSettings() {
        return this.settings;
    }

    @Override
    public void init() throws Exception {
        super.init();
        this.setAnsiSupported((boolean)true);
        this.settings.set((String)"-icanon min 1 -icrnl -inlcr -ixon");
        this.settings.set((String)"dsusp undef");
        this.setEchoEnabled((boolean)false);
    }

    @Override
    public void restore() throws Exception {
        this.settings.restore();
        super.restore();
    }

    @Override
    public int getWidth() {
        int w = this.settings.getProperty((String)"columns");
        if (w < 1) {
            return 80;
        }
        int n = w;
        return n;
    }

    @Override
    public int getHeight() {
        int h = this.settings.getProperty((String)"rows");
        if (h < 1) {
            return 24;
        }
        int n = h;
        return n;
    }

    @Override
    public synchronized void setEchoEnabled(boolean enabled) {
        try {
            if (enabled) {
                this.settings.set((String)"echo");
            } else {
                this.settings.set((String)"-echo");
            }
            super.setEchoEnabled((boolean)enabled);
            return;
        }
        catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Log.error((Object[])new Object[]{"Failed to ", enabled ? "enable" : "disable", " echo", e});
        }
    }

    public void disableInterruptCharacter() {
        try {
            this.settings.set((String)"intr undef");
            return;
        }
        catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Log.error((Object[])new Object[]{"Failed to disable interrupt character", e});
        }
    }

    public void enableInterruptCharacter() {
        try {
            this.settings.set((String)"intr ^C");
            return;
        }
        catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Log.error((Object[])new Object[]{"Failed to enable interrupt character", e});
        }
    }
}

