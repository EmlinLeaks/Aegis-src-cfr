/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jline.Terminal;
import jline.TerminalFactory;
import jline.TerminalSupport;
import jline.internal.Log;
import jline.internal.ShutdownHooks;

public abstract class TerminalSupport
implements Terminal {
    public static final int DEFAULT_WIDTH = 80;
    public static final int DEFAULT_HEIGHT = 24;
    private ShutdownHooks.Task shutdownTask;
    private boolean supported;
    private boolean echoEnabled;
    private boolean ansiSupported;

    protected TerminalSupport(boolean supported) {
        this.supported = supported;
    }

    @Override
    public void init() throws Exception {
        if (this.shutdownTask != null) {
            ShutdownHooks.remove((ShutdownHooks.Task)this.shutdownTask);
        }
        this.shutdownTask = ShutdownHooks.add(new ShutdownHooks.Task((TerminalSupport)this){
            final /* synthetic */ TerminalSupport this$0;
            {
                this.this$0 = terminalSupport;
            }

            public void run() throws Exception {
                this.this$0.restore();
            }
        });
    }

    @Override
    public void restore() throws Exception {
        TerminalFactory.resetIf((Terminal)this);
        if (this.shutdownTask == null) return;
        ShutdownHooks.remove((ShutdownHooks.Task)this.shutdownTask);
        this.shutdownTask = null;
    }

    @Override
    public void reset() throws Exception {
        this.restore();
        this.init();
    }

    @Override
    public final boolean isSupported() {
        return this.supported;
    }

    @Override
    public synchronized boolean isAnsiSupported() {
        return this.ansiSupported;
    }

    protected synchronized void setAnsiSupported(boolean supported) {
        this.ansiSupported = supported;
        Log.debug((Object[])new Object[]{"Ansi supported: ", Boolean.valueOf((boolean)supported)});
    }

    @Override
    public OutputStream wrapOutIfNeeded(OutputStream out) {
        return out;
    }

    @Override
    public boolean hasWeirdWrap() {
        return true;
    }

    @Override
    public int getWidth() {
        return 80;
    }

    @Override
    public int getHeight() {
        return 24;
    }

    @Override
    public synchronized boolean isEchoEnabled() {
        return this.echoEnabled;
    }

    @Override
    public synchronized void setEchoEnabled(boolean enabled) {
        this.echoEnabled = enabled;
        Log.debug((Object[])new Object[]{"Echo enabled: ", Boolean.valueOf((boolean)enabled)});
    }

    @Override
    public InputStream wrapInIfNeeded(InputStream in) throws IOException {
        return in;
    }

    @Override
    public String getOutputEncoding() {
        return null;
    }
}

