/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import jline.TerminalSupport;
import jline.WindowsTerminal;
import jline.internal.Configuration;
import jline.internal.Log;
import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.WindowsSupport;

public class WindowsTerminal
extends TerminalSupport {
    public static final String DIRECT_CONSOLE = WindowsTerminal.class.getName() + ".directConsole";
    public static final String ANSI = WindowsTerminal.class.getName() + ".ansi";
    private boolean directConsole;
    private int originalMode;

    public WindowsTerminal() throws Exception {
        super((boolean)true);
    }

    @Override
    public void init() throws Exception {
        super.init();
        this.setAnsiSupported((boolean)Configuration.getBoolean((String)ANSI, (boolean)true));
        this.setDirectConsole((boolean)Configuration.getBoolean((String)DIRECT_CONSOLE, (boolean)true));
        this.originalMode = this.getConsoleMode();
        this.setConsoleMode((int)(this.originalMode & ~ConsoleMode.ENABLE_ECHO_INPUT.code));
        this.setEchoEnabled((boolean)false);
    }

    @Override
    public void restore() throws Exception {
        this.setConsoleMode((int)this.originalMode);
        super.restore();
    }

    @Override
    public int getWidth() {
        int w = this.getWindowsTerminalWidth();
        if (w < 1) {
            return 80;
        }
        int n = w;
        return n;
    }

    @Override
    public int getHeight() {
        int h = this.getWindowsTerminalHeight();
        if (h < 1) {
            return 24;
        }
        int n = h;
        return n;
    }

    @Override
    public void setEchoEnabled(boolean enabled) {
        if (enabled) {
            this.setConsoleMode((int)(this.getConsoleMode() | ConsoleMode.ENABLE_ECHO_INPUT.code | ConsoleMode.ENABLE_LINE_INPUT.code | ConsoleMode.ENABLE_PROCESSED_INPUT.code | ConsoleMode.ENABLE_WINDOW_INPUT.code));
        } else {
            this.setConsoleMode((int)(this.getConsoleMode() & ~(ConsoleMode.ENABLE_LINE_INPUT.code | ConsoleMode.ENABLE_ECHO_INPUT.code | ConsoleMode.ENABLE_PROCESSED_INPUT.code | ConsoleMode.ENABLE_WINDOW_INPUT.code)));
        }
        super.setEchoEnabled((boolean)enabled);
    }

    public void setDirectConsole(boolean flag) {
        this.directConsole = flag;
        Log.debug((Object[])new Object[]{"Direct console: ", Boolean.valueOf((boolean)flag)});
    }

    public Boolean getDirectConsole() {
        return Boolean.valueOf((boolean)this.directConsole);
    }

    @Override
    public InputStream wrapInIfNeeded(InputStream in) throws IOException {
        if (!this.directConsole) return super.wrapInIfNeeded((InputStream)in);
        if (!this.isSystemIn((InputStream)in)) return super.wrapInIfNeeded((InputStream)in);
        return new InputStream((WindowsTerminal)this){
            private byte[] buf;
            int bufIdx;
            final /* synthetic */ WindowsTerminal this$0;
            {
                this.this$0 = windowsTerminal;
                this.buf = null;
                this.bufIdx = 0;
            }

            public int read() throws IOException {
                do {
                    if (this.buf != null && this.bufIdx != this.buf.length) {
                        int c = this.buf[this.bufIdx] & 255;
                        ++this.bufIdx;
                        return c;
                    }
                    this.buf = WindowsTerminal.access$000((WindowsTerminal)this.this$0);
                    this.bufIdx = 0;
                } while (true);
            }
        };
    }

    protected boolean isSystemIn(InputStream in) throws IOException {
        if (in == null) {
            return false;
        }
        if (in == System.in) {
            return true;
        }
        if (!(in instanceof FileInputStream)) return false;
        if (((FileInputStream)in).getFD() != FileDescriptor.in) return false;
        return true;
    }

    @Override
    public String getOutputEncoding() {
        int codepage = this.getConsoleOutputCodepage();
        String charsetMS = "ms" + codepage;
        if (Charset.isSupported((String)charsetMS)) {
            return charsetMS;
        }
        String charsetCP = "cp" + codepage;
        if (Charset.isSupported((String)charsetCP)) {
            return charsetCP;
        }
        Log.debug((Object[])new Object[]{"can't figure out the Java Charset of this code page (" + codepage + ")..."});
        return super.getOutputEncoding();
    }

    private int getConsoleMode() {
        return WindowsSupport.getConsoleMode();
    }

    private void setConsoleMode(int mode) {
        WindowsSupport.setConsoleMode((int)mode);
    }

    private byte[] readConsoleInput() {
        Kernel32.INPUT_RECORD[] events = null;
        try {
            events = WindowsSupport.readConsoleInput((int)1);
        }
        catch (IOException e) {
            Log.debug((Object[])new Object[]{"read Windows console input error: ", e});
        }
        if (events == null) {
            return new byte[0];
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < events.length) {
            Kernel32.KEY_EVENT_RECORD keyEvent = events[i].keyEvent;
            if (keyEvent.keyDown) {
                if (keyEvent.uchar > '\u0000') {
                    int altState = Kernel32.KEY_EVENT_RECORD.LEFT_ALT_PRESSED | Kernel32.KEY_EVENT_RECORD.RIGHT_ALT_PRESSED;
                    int ctrlState = Kernel32.KEY_EVENT_RECORD.LEFT_CTRL_PRESSED | Kernel32.KEY_EVENT_RECORD.RIGHT_CTRL_PRESSED;
                    if ((keyEvent.uchar >= '@' && keyEvent.uchar <= '_' || keyEvent.uchar >= 'a' && keyEvent.uchar <= 'z') && (keyEvent.controlKeyState & altState) != 0 && (keyEvent.controlKeyState & ctrlState) == 0) {
                        sb.append((char)'\u001b');
                    }
                    sb.append((char)keyEvent.uchar);
                } else {
                    String escapeSequence = null;
                    switch (keyEvent.keyCode) {
                        case 33: {
                            escapeSequence = "\u001b[5~";
                            break;
                        }
                        case 34: {
                            escapeSequence = "\u001b[6~";
                            break;
                        }
                        case 35: {
                            escapeSequence = "\u001b[4~";
                            break;
                        }
                        case 36: {
                            escapeSequence = "\u001b[1~";
                            break;
                        }
                        case 37: {
                            escapeSequence = "\u001b[D";
                            break;
                        }
                        case 38: {
                            escapeSequence = "\u001b[A";
                            break;
                        }
                        case 39: {
                            escapeSequence = "\u001b[C";
                            break;
                        }
                        case 40: {
                            escapeSequence = "\u001b[B";
                            break;
                        }
                        case 45: {
                            escapeSequence = "\u001b[2~";
                            break;
                        }
                        case 46: {
                            escapeSequence = "\u001b[3~";
                            break;
                        }
                    }
                    if (escapeSequence != null) {
                        for (int k = 0; k < keyEvent.repeatCount; ++k) {
                            sb.append((String)escapeSequence);
                        }
                    }
                }
            } else if (keyEvent.keyCode == 18 && keyEvent.uchar > '\u0000') {
                sb.append((char)keyEvent.uchar);
            }
            ++i;
        }
        return sb.toString().getBytes();
    }

    private int getConsoleOutputCodepage() {
        return Kernel32.GetConsoleOutputCP();
    }

    private int getWindowsTerminalWidth() {
        return WindowsSupport.getWindowsTerminalWidth();
    }

    private int getWindowsTerminalHeight() {
        return WindowsSupport.getWindowsTerminalHeight();
    }

    static /* synthetic */ byte[] access$000(WindowsTerminal x0) {
        return x0.readConsoleInput();
    }
}

