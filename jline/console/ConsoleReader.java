/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import jline.Terminal;
import jline.TerminalFactory;
import jline.UnixTerminal;
import jline.console.ConsoleKeys;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import jline.console.KeyMap;
import jline.console.KillRing;
import jline.console.Operation;
import jline.console.UserInterruptException;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.Completer;
import jline.console.completer.CompletionHandler;
import jline.console.history.History;
import jline.console.history.MemoryHistory;
import jline.internal.Configuration;
import jline.internal.InputStreamReader;
import jline.internal.Log;
import jline.internal.NonBlockingInputStream;
import jline.internal.Nullable;
import jline.internal.Preconditions;
import jline.internal.Urls;
import org.fusesource.jansi.AnsiOutputStream;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConsoleReader {
    public static final String JLINE_NOBELL = "jline.nobell";
    public static final String JLINE_ESC_TIMEOUT = "jline.esc.timeout";
    public static final String JLINE_INPUTRC = "jline.inputrc";
    public static final String INPUT_RC = ".inputrc";
    public static final String DEFAULT_INPUT_RC = "/etc/inputrc";
    public static final char BACKSPACE = '\b';
    public static final char RESET_LINE = '\r';
    public static final char KEYBOARD_BELL = '\u0007';
    public static final char NULL_MASK = '\u0000';
    public static final int TAB_WIDTH = 4;
    private static final ResourceBundle resources = ResourceBundle.getBundle((String)CandidateListCompletionHandler.class.getName());
    private final Terminal terminal;
    private final Writer out;
    private final CursorBuffer buf = new CursorBuffer();
    private String prompt;
    private int promptLen;
    private boolean expandEvents = true;
    private boolean bellEnabled = !Configuration.getBoolean((String)"jline.nobell", (boolean)true);
    private boolean handleUserInterrupt = false;
    private Character mask;
    private Character echoCharacter;
    private StringBuffer searchTerm = null;
    private String previousSearchTerm = "";
    private int searchIndex = -1;
    private int parenBlinkTimeout = 500;
    private NonBlockingInputStream in;
    private long escapeTimeout;
    private Reader reader;
    private boolean isUnitTestInput;
    private char charSearchChar = '\u0000';
    private char charSearchLastInvokeChar = '\u0000';
    private char charSearchFirstInvokeChar = '\u0000';
    private String yankBuffer = "";
    private KillRing killRing = new KillRing();
    private String encoding;
    private boolean recording;
    private String macro = "";
    private String appName;
    private URL inputrcUrl;
    private ConsoleKeys consoleKeys;
    private String commentBegin = null;
    private boolean skipLF = false;
    private boolean copyPasteDetection = false;
    private State state = State.NORMAL;
    public static final String JLINE_COMPLETION_THRESHOLD = "jline.completion.threshold";
    private final List<Completer> completers = new LinkedList<Completer>();
    private CompletionHandler completionHandler = new CandidateListCompletionHandler();
    private int autoprintThreshold = Configuration.getInteger((String)"jline.completion.threshold", (int)100);
    private boolean paginationEnabled;
    private History history = new MemoryHistory();
    private boolean historyEnabled = true;
    public static final String CR = Configuration.getLineSeparator();
    private final Map<Character, ActionListener> triggeredActions = new HashMap<Character, ActionListener>();
    private Thread maskThread;

    public ConsoleReader() throws IOException {
        this(null, (InputStream)new FileInputStream((FileDescriptor)FileDescriptor.in), (OutputStream)System.out, null);
    }

    public ConsoleReader(InputStream in, OutputStream out) throws IOException {
        this(null, (InputStream)in, (OutputStream)out, null);
    }

    public ConsoleReader(InputStream in, OutputStream out, Terminal term) throws IOException {
        this(null, (InputStream)in, (OutputStream)out, (Terminal)term);
    }

    public ConsoleReader(@Nullable String appName, InputStream in, OutputStream out, @Nullable Terminal term) throws IOException {
        this((String)appName, (InputStream)in, (OutputStream)out, (Terminal)term, null);
    }

    public ConsoleReader(@Nullable String appName, InputStream in, OutputStream out, @Nullable Terminal term, @Nullable String encoding) throws IOException {
        this.appName = appName != null ? appName : "JLine";
        this.encoding = encoding != null ? encoding : Configuration.getEncoding();
        this.terminal = term != null ? term : TerminalFactory.get();
        String outEncoding = this.terminal.getOutputEncoding() != null ? this.terminal.getOutputEncoding() : this.encoding;
        this.out = new OutputStreamWriter((OutputStream)this.terminal.wrapOutIfNeeded((OutputStream)out), (String)outEncoding);
        this.setInput((InputStream)in);
        this.inputrcUrl = this.getInputRc();
        this.consoleKeys = new ConsoleKeys((String)this.appName, (URL)this.inputrcUrl);
    }

    private URL getInputRc() throws IOException {
        String path = Configuration.getString((String)JLINE_INPUTRC);
        if (path != null) return Urls.create((String)path);
        File f = new File((File)Configuration.getUserHome(), (String)INPUT_RC);
        if (f.exists()) return f.toURI().toURL();
        f = new File((String)DEFAULT_INPUT_RC);
        return f.toURI().toURL();
    }

    public KeyMap getKeys() {
        return this.consoleKeys.getKeys();
    }

    void setInput(InputStream in) throws IOException {
        boolean nonBlockingEnabled;
        this.escapeTimeout = Configuration.getLong((String)JLINE_ESC_TIMEOUT, (long)100L);
        this.isUnitTestInput = in instanceof ByteArrayInputStream;
        boolean bl = nonBlockingEnabled = this.escapeTimeout > 0L && this.terminal.isSupported() && in != null;
        if (this.in != null) {
            this.in.shutdown();
        }
        InputStream wrapped = this.terminal.wrapInIfNeeded((InputStream)in);
        this.in = new NonBlockingInputStream((InputStream)wrapped, (boolean)nonBlockingEnabled);
        this.reader = new InputStreamReader((InputStream)this.in, (String)this.encoding);
    }

    public void shutdown() {
        if (this.in == null) return;
        this.in.shutdown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalize() throws Throwable {
        try {
            this.shutdown();
            return;
        }
        finally {
            super.finalize();
        }
    }

    public InputStream getInput() {
        return this.in;
    }

    public Writer getOutput() {
        return this.out;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public CursorBuffer getCursorBuffer() {
        return this.buf;
    }

    public void setExpandEvents(boolean expand) {
        this.expandEvents = expand;
    }

    public boolean getExpandEvents() {
        return this.expandEvents;
    }

    public void setCopyPasteDetection(boolean onoff) {
        this.copyPasteDetection = onoff;
    }

    public boolean isCopyPasteDetectionEnabled() {
        return this.copyPasteDetection;
    }

    public void setBellEnabled(boolean enabled) {
        this.bellEnabled = enabled;
    }

    public boolean getBellEnabled() {
        return this.bellEnabled;
    }

    public void setHandleUserInterrupt(boolean enabled) {
        this.handleUserInterrupt = enabled;
    }

    public boolean getHandleUserInterrupt() {
        return this.handleUserInterrupt;
    }

    public void setCommentBegin(String commentBegin) {
        this.commentBegin = commentBegin;
    }

    public String getCommentBegin() {
        String str = this.commentBegin;
        if (str != null) return str;
        str = this.consoleKeys.getVariable((String)"comment-begin");
        if (str != null) return str;
        return "#";
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
        this.promptLen = prompt == null ? 0 : this.stripAnsi((String)this.lastLine((String)prompt)).length();
    }

    public String getPrompt() {
        return this.prompt;
    }

    public void setEchoCharacter(Character c) {
        this.echoCharacter = c;
    }

    public Character getEchoCharacter() {
        return this.echoCharacter;
    }

    protected final boolean resetLine() throws IOException {
        char c;
        if (this.buf.cursor == 0) {
            return false;
        }
        StringBuilder killed = new StringBuilder();
        while (this.buf.cursor > 0 && (c = this.buf.current()) != '\u0000') {
            killed.append((char)c);
            this.backspace();
        }
        String copy = killed.reverse().toString();
        this.killRing.addBackwards((String)copy);
        return true;
    }

    int getCursorPosition() {
        return this.promptLen + this.buf.cursor;
    }

    private String lastLine(String str) {
        if (str == null) {
            return "";
        }
        int last = str.lastIndexOf((String)"\n");
        if (last < 0) return str;
        return str.substring((int)(last + 1), (int)str.length());
    }

    private String stripAnsi(String str) {
        if (str == null) {
            return "";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AnsiOutputStream aos = new AnsiOutputStream((OutputStream)baos);
            aos.write((byte[])str.getBytes());
            aos.flush();
            return baos.toString();
        }
        catch (IOException e) {
            return str;
        }
    }

    public final boolean setCursorPosition(int position) throws IOException {
        if (position == this.buf.cursor) {
            return true;
        }
        if (this.moveCursor((int)(position - this.buf.cursor)) == 0) return false;
        return true;
    }

    private void setBuffer(String buffer) throws IOException {
        if (buffer.equals((Object)this.buf.buffer.toString())) {
            return;
        }
        int sameIndex = 0;
        int l1 = buffer.length();
        int l2 = this.buf.buffer.length();
        for (int i = 0; i < l1 && i < l2 && buffer.charAt((int)i) == this.buf.buffer.charAt((int)i); ++sameIndex, ++i) {
        }
        int diff = this.buf.cursor - sameIndex;
        if (diff < 0) {
            this.moveToEnd();
            diff = this.buf.buffer.length() - sameIndex;
        }
        this.backspace((int)diff);
        this.killLine();
        this.buf.buffer.setLength((int)sameIndex);
        this.putString((CharSequence)buffer.substring((int)sameIndex));
    }

    private void setBuffer(CharSequence buffer) throws IOException {
        this.setBuffer((String)String.valueOf((Object)buffer));
    }

    private void setBufferKeepPos(String buffer) throws IOException {
        int pos = this.buf.cursor;
        this.setBuffer((String)buffer);
        this.setCursorPosition((int)pos);
    }

    private void setBufferKeepPos(CharSequence buffer) throws IOException {
        this.setBufferKeepPos((String)String.valueOf((Object)buffer));
    }

    public final void drawLine() throws IOException {
        String prompt = this.getPrompt();
        if (prompt != null) {
            this.print((CharSequence)prompt);
        }
        this.print((CharSequence)this.buf.buffer.toString());
        if (this.buf.length() != this.buf.cursor) {
            this.back((int)(this.buf.length() - this.buf.cursor - 1));
        }
        this.drawBuffer();
    }

    public final void redrawLine() throws IOException {
        this.print((int)13);
        this.drawLine();
    }

    final String finishBuffer() throws IOException {
        String str;
        String historyLine = str = this.buf.buffer.toString();
        if (this.expandEvents) {
            try {
                str = this.expandEvents((String)str);
                historyLine = str.replace((CharSequence)"!", (CharSequence)"\\!");
                historyLine = historyLine.replaceAll((String)"^\\^", (String)"\\\\^");
            }
            catch (IllegalArgumentException e) {
                Log.error((Object[])new Object[]{"Could not expand event", e});
                this.beep();
                this.buf.clear();
                str = "";
            }
        }
        if (str.length() > 0) {
            if (this.mask == null && this.isHistoryEnabled()) {
                this.history.add((CharSequence)historyLine);
            } else {
                this.mask = null;
            }
        }
        this.history.moveToEnd();
        this.buf.buffer.setLength((int)0);
        this.buf.cursor = 0;
        return str;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    protected String expandEvents(String str) throws IOException {
        sb = new StringBuilder();
        i = 0;
        do {
            if (i >= str.length()) {
                result = sb.toString();
                if (str.equals((Object)result) != false) return result;
                this.print((CharSequence)result);
                this.println();
                this.flush();
                return result;
            }
            c = str.charAt((int)i);
            switch (c) {
                case '\\': {
                    if (i + 1 < str.length() && ((nextChar = str.charAt((int)(i + 1))) == '!' || nextChar == '^' && i == 0)) {
                        c = nextChar;
                        ++i;
                    }
                    sb.append((char)c);
                    break;
                }
                case '!': {
                    if (i + 1 < str.length()) {
                        c = str.charAt((int)(++i));
                        neg = false;
                        rep = null;
                        switch (c) {
                            case '!': {
                                if (this.history.size() == 0) {
                                    throw new IllegalArgumentException((String)"!!: event not found");
                                }
                                rep = this.history.get((int)(this.history.index() - 1)).toString();
                                ** break;
                            }
                            case '#': {
                                sb.append((String)sb.toString());
                                ** break;
                            }
                            case '?': {
                                i1 = str.indexOf((int)63, (int)(i + 1));
                                if (i1 < 0) {
                                    i1 = str.length();
                                }
                                sc = str.substring((int)(i + 1), (int)i1);
                                i = i1;
                                idx = this.searchBackwards((String)sc);
                                if (idx < 0) {
                                    throw new IllegalArgumentException((String)("!?" + sc + ": event not found"));
                                }
                                rep = this.history.get((int)idx).toString();
                                ** break;
                            }
                            case '$': {
                                if (this.history.size() == 0) {
                                    throw new IllegalArgumentException((String)"!$: event not found");
                                }
                                previous = this.history.get((int)(this.history.index() - 1)).toString().trim();
                                lastSpace = previous.lastIndexOf((int)32);
                                if (lastSpace != -1) {
                                    rep = previous.substring((int)(lastSpace + 1));
                                    ** break;
                                }
                                rep = previous;
                                ** break;
                            }
                            case '\t': 
                            case ' ': {
                                sb.append((char)'!');
                                sb.append((char)c);
                                ** break;
                            }
                            case '-': {
                                neg = true;
                            }
                            case '0': 
                            case '1': 
                            case '2': 
                            case '3': 
                            case '4': 
                            case '5': 
                            case '6': 
                            case '7': 
                            case '8': 
                            case '9': {
                                i1 = ++i;
                                while (i < str.length() && (c = str.charAt((int)i)) >= '0' && c <= '9') {
                                    ++i;
                                }
                                idx = 0;
                                try {
                                    idx = Integer.parseInt((String)str.substring((int)i1, (int)i));
                                }
                                catch (NumberFormatException e) {
                                    if (neg) {
                                        v0 = "!-";
                                        throw new IllegalArgumentException((String)(v0 + str.substring((int)i1, (int)i) + ": event not found"));
                                    }
                                    v0 = "!";
                                    throw new IllegalArgumentException((String)(v0 + str.substring((int)i1, (int)i) + ": event not found"));
                                }
                                if (neg) {
                                    if (idx > 0 && idx <= this.history.size()) {
                                        rep = this.history.get((int)(this.history.index() - idx)).toString();
                                        ** break;
                                    }
                                    if (neg) {
                                        v1 = "!-";
                                        throw new IllegalArgumentException((String)(v1 + str.substring((int)i1, (int)i) + ": event not found"));
                                    }
                                    v1 = "!";
                                    throw new IllegalArgumentException((String)(v1 + str.substring((int)i1, (int)i) + ": event not found"));
                                }
                                if (idx > this.history.index() - this.history.size() && idx <= this.history.index()) {
                                    rep = this.history.get((int)(idx - 1)).toString();
                                    ** break;
                                }
                                if (neg) {
                                    v2 = "!-";
                                    throw new IllegalArgumentException((String)(v2 + str.substring((int)i1, (int)i) + ": event not found"));
                                }
                                v2 = "!";
                                throw new IllegalArgumentException((String)(v2 + str.substring((int)i1, (int)i) + ": event not found"));
                            }
                        }
                        ss = str.substring((int)i);
                        i = str.length();
                        idx = this.searchBackwards((String)ss, (int)this.history.index(), (boolean)true);
                        if (idx < 0) {
                            throw new IllegalArgumentException((String)("!" + ss + ": event not found"));
                        }
                        rep = this.history.get((int)idx).toString();
lbl102: // 9 sources:
                        if (rep == null) break;
                        sb.append(rep);
                        break;
                    }
                    sb.append((char)c);
                    break;
                }
                case '^': {
                    if (i == 0) {
                        i1 = str.indexOf((int)94, (int)(i + 1));
                        i2 = str.indexOf((int)94, (int)(i1 + 1));
                        if (i2 < 0) {
                            i2 = str.length();
                        }
                        if (i1 > 0 && i2 > 0) {
                            s1 = str.substring((int)(i + 1), (int)i1);
                            s2 = str.substring((int)(i1 + 1), (int)i2);
                            s = this.history.get((int)(this.history.index() - 1)).toString().replace((CharSequence)s1, (CharSequence)s2);
                            sb.append((String)s);
                            i = i2 + 1;
                            break;
                        }
                    }
                    sb.append((char)c);
                    break;
                }
                default: {
                    sb.append((char)c);
                }
            }
            ++i;
        } while (true);
    }

    public final void putString(CharSequence str) throws IOException {
        this.buf.write((CharSequence)str);
        if (this.mask == null) {
            this.print((CharSequence)str);
        } else if (this.mask.charValue() != '\u0000') {
            this.print((char)this.mask.charValue(), (int)str.length());
        }
        this.drawBuffer();
    }

    private void drawBuffer(int clear) throws IOException {
        if (this.buf.cursor != this.buf.length() || clear != 0) {
            char[] chars = this.buf.buffer.substring((int)this.buf.cursor).toCharArray();
            if (this.mask != null) {
                Arrays.fill((char[])chars, (char)this.mask.charValue());
            }
            if (this.terminal.hasWeirdWrap()) {
                int width = this.terminal.getWidth();
                int pos = this.getCursorPosition();
                for (int i = 0; i < chars.length; ++i) {
                    this.print((int)chars[i]);
                    if ((pos + i + 1) % width != 0) continue;
                    this.print((int)32);
                    this.print((int)13);
                }
            } else {
                this.print((char[])chars);
            }
            this.clearAhead((int)clear, (int)chars.length);
            if (this.terminal.isAnsiSupported()) {
                if (chars.length > 0) {
                    this.back((int)chars.length);
                }
            } else {
                this.back((int)chars.length);
            }
        }
        if (!this.terminal.hasWeirdWrap()) return;
        int width = this.terminal.getWidth();
        if (this.getCursorPosition() <= 0) return;
        if (this.getCursorPosition() % width != 0) return;
        if (this.buf.cursor != this.buf.length()) return;
        if (clear != 0) return;
        this.print((int)32);
        this.print((int)13);
    }

    private void drawBuffer() throws IOException {
        this.drawBuffer((int)0);
    }

    private void clearAhead(int num, int delta) throws IOException {
        int i;
        if (num == 0) {
            return;
        }
        if (!this.terminal.isAnsiSupported()) {
            this.print((char)' ', (int)num);
            this.back((int)num);
            return;
        }
        int width = this.terminal.getWidth();
        int screenCursorCol = this.getCursorPosition() + delta;
        this.printAnsiSequence((String)"K");
        int curCol = screenCursorCol % width;
        int endCol = (screenCursorCol + num - 1) % width;
        int lines = num / width;
        if (endCol < curCol) {
            ++lines;
        }
        for (i = 0; i < lines; ++i) {
            this.printAnsiSequence((String)"B");
            this.printAnsiSequence((String)"2K");
        }
        i = 0;
        while (i < lines) {
            this.printAnsiSequence((String)"A");
            ++i;
        }
    }

    protected void back(int num) throws IOException {
        if (num == 0) {
            return;
        }
        if (!this.terminal.isAnsiSupported()) {
            this.print((char)'\b', (int)num);
            return;
        }
        int width = this.getTerminal().getWidth();
        int cursor = this.getCursorPosition();
        int realCursor = cursor + num;
        int realCol = realCursor % width;
        int newCol = cursor % width;
        int moveup = num / width;
        int delta = realCol - newCol;
        if (delta < 0) {
            ++moveup;
        }
        if (moveup > 0) {
            this.printAnsiSequence((String)(moveup + "A"));
        }
        this.printAnsiSequence((String)(1 + newCol + "G"));
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    private int backspaceAll() throws IOException {
        return this.backspace((int)Integer.MAX_VALUE);
    }

    private int backspace(int num) throws IOException {
        if (this.buf.cursor == 0) {
            return 0;
        }
        int count = 0;
        int termwidth = this.getTerminal().getWidth();
        int lines = this.getCursorPosition() / termwidth;
        count = this.moveCursor((int)(-1 * num)) * -1;
        this.buf.buffer.delete((int)this.buf.cursor, (int)(this.buf.cursor + count));
        if (this.getCursorPosition() / termwidth != lines && this.terminal.isAnsiSupported()) {
            this.printAnsiSequence((String)"K");
        }
        this.drawBuffer((int)count);
        return count;
    }

    public boolean backspace() throws IOException {
        if (this.backspace((int)1) != 1) return false;
        return true;
    }

    protected boolean moveToEnd() throws IOException {
        if (this.buf.cursor == this.buf.length()) {
            return true;
        }
        if (this.moveCursor((int)(this.buf.length() - this.buf.cursor)) <= 0) return false;
        return true;
    }

    private boolean deleteCurrentCharacter() throws IOException {
        if (this.buf.length() == 0) return false;
        if (this.buf.cursor == this.buf.length()) {
            return false;
        }
        this.buf.buffer.deleteCharAt((int)this.buf.cursor);
        this.drawBuffer((int)1);
        return true;
    }

    private Operation viDeleteChangeYankToRemap(Operation op) {
        switch (op) {
            case VI_EOF_MAYBE: 
            case ABORT: 
            case BACKWARD_CHAR: 
            case FORWARD_CHAR: 
            case END_OF_LINE: 
            case VI_MATCH: 
            case VI_BEGNNING_OF_LINE_OR_ARG_DIGIT: 
            case VI_ARG_DIGIT: 
            case VI_PREV_WORD: 
            case VI_END_WORD: 
            case VI_CHAR_SEARCH: 
            case VI_NEXT_WORD: 
            case VI_FIRST_PRINT: 
            case VI_GOTO_MARK: 
            case VI_COLUMN: 
            case VI_DELETE_TO: 
            case VI_YANK_TO: 
            case VI_CHANGE_TO: {
                return op;
            }
        }
        return Operation.VI_MOVEMENT_MODE;
    }

    private boolean viRubout(int count) throws IOException {
        boolean ok = true;
        int i = 0;
        while (ok) {
            if (i >= count) return ok;
            ok = this.backspace();
            ++i;
        }
        return ok;
    }

    private boolean viDelete(int count) throws IOException {
        boolean ok = true;
        int i = 0;
        while (ok) {
            if (i >= count) return ok;
            ok = this.deleteCurrentCharacter();
            ++i;
        }
        return ok;
    }

    private boolean viChangeCase(int count) throws IOException {
        boolean ok = true;
        int i = 0;
        while (ok) {
            if (i >= count) return ok;
            boolean bl = ok = this.buf.cursor < this.buf.buffer.length();
            if (ok) {
                char ch = this.buf.buffer.charAt((int)this.buf.cursor);
                if (Character.isUpperCase((char)ch)) {
                    ch = Character.toLowerCase((char)ch);
                } else if (Character.isLowerCase((char)ch)) {
                    ch = Character.toUpperCase((char)ch);
                }
                this.buf.buffer.setCharAt((int)this.buf.cursor, (char)ch);
                this.drawBuffer((int)1);
                this.moveCursor((int)1);
            }
            ++i;
        }
        return ok;
    }

    private boolean viChangeChar(int count, int c) throws IOException {
        if (c < 0) return true;
        if (c == 27) return true;
        if (c == 3) {
            return true;
        }
        boolean ok = true;
        int i = 0;
        while (ok) {
            if (i >= count) return ok;
            boolean bl = ok = this.buf.cursor < this.buf.buffer.length();
            if (ok) {
                this.buf.buffer.setCharAt((int)this.buf.cursor, (char)((char)c));
                this.drawBuffer((int)1);
                if (i < count - 1) {
                    this.moveCursor((int)1);
                }
            }
            ++i;
        }
        return ok;
    }

    private boolean viPreviousWord(int count) throws IOException {
        boolean ok = true;
        if (this.buf.cursor == 0) {
            return false;
        }
        int pos = this.buf.cursor - 1;
        for (int i = 0; pos > 0 && i < count; ++i) {
            while (pos > 0 && this.isWhitespace((char)this.buf.buffer.charAt((int)pos))) {
                --pos;
            }
            while (pos > 0 && !this.isDelimiter((char)this.buf.buffer.charAt((int)(pos - 1)))) {
                --pos;
            }
            if (pos <= 0 || i >= count - 1) continue;
            --pos;
        }
        this.setCursorPosition((int)pos);
        return ok;
    }

    private boolean viDeleteTo(int startPos, int endPos, boolean isChange) throws IOException {
        if (startPos == endPos) {
            return true;
        }
        if (endPos < startPos) {
            int tmp = endPos;
            endPos = startPos;
            startPos = tmp;
        }
        this.setCursorPosition((int)startPos);
        this.buf.cursor = startPos;
        this.buf.buffer.delete((int)startPos, (int)endPos);
        this.drawBuffer((int)(endPos - startPos));
        if (isChange) return true;
        if (startPos <= 0) return true;
        if (startPos != this.buf.length()) return true;
        this.moveCursor((int)-1);
        return true;
    }

    private boolean viYankTo(int startPos, int endPos) throws IOException {
        int cursorPos = startPos;
        if (endPos < startPos) {
            int tmp = endPos;
            endPos = startPos;
            startPos = tmp;
        }
        if (startPos == endPos) {
            this.yankBuffer = "";
            return true;
        }
        this.yankBuffer = this.buf.buffer.substring((int)startPos, (int)endPos);
        this.setCursorPosition((int)cursorPos);
        return true;
    }

    private boolean viPut(int count) throws IOException {
        if (this.yankBuffer.length() == 0) {
            return true;
        }
        if (this.buf.cursor < this.buf.buffer.length()) {
            this.moveCursor((int)1);
        }
        int i = 0;
        do {
            if (i >= count) {
                this.moveCursor((int)-1);
                return true;
            }
            this.putString((CharSequence)this.yankBuffer);
            ++i;
        } while (true);
    }

    private boolean viCharSearch(int count, int invokeChar, int ch) throws IOException {
        if (ch < 0) return false;
        if (invokeChar < 0) {
            return false;
        }
        char searchChar = (char)ch;
        if (invokeChar == 59 || invokeChar == 44) {
            if (this.charSearchChar == '\u0000') {
                return false;
            }
            if (this.charSearchLastInvokeChar == ';' || this.charSearchLastInvokeChar == ',') {
                if (this.charSearchLastInvokeChar != invokeChar) {
                    this.charSearchFirstInvokeChar = this.switchCase((char)this.charSearchFirstInvokeChar);
                }
            } else if (invokeChar == 44) {
                this.charSearchFirstInvokeChar = this.switchCase((char)this.charSearchFirstInvokeChar);
            }
            searchChar = this.charSearchChar;
        } else {
            this.charSearchChar = searchChar;
            this.charSearchFirstInvokeChar = (char)invokeChar;
        }
        this.charSearchLastInvokeChar = (char)invokeChar;
        boolean isForward = Character.isLowerCase((char)this.charSearchFirstInvokeChar);
        boolean stopBefore = Character.toLowerCase((char)this.charSearchFirstInvokeChar) == 't';
        boolean ok = false;
        if (isForward) {
            block0 : while (count-- > 0) {
                for (int pos = this.buf.cursor + 1; pos < this.buf.buffer.length(); ++pos) {
                    if (this.buf.buffer.charAt((int)pos) != searchChar) continue;
                    this.setCursorPosition((int)pos);
                    ok = true;
                    continue block0;
                }
            }
            if (!ok) return ok;
            if (stopBefore) {
                this.moveCursor((int)-1);
            }
            if (!this.isInViMoveOperationState()) return ok;
            this.moveCursor((int)1);
            return ok;
        }
        block2 : do {
            if (count-- <= 0) {
                if (!ok) return ok;
                if (!stopBefore) return ok;
                this.moveCursor((int)1);
                return ok;
            }
            int pos = this.buf.cursor - 1;
            do {
                if (pos < 0) continue block2;
                if (this.buf.buffer.charAt((int)pos) == searchChar) {
                    this.setCursorPosition((int)pos);
                    ok = true;
                    continue block2;
                }
                --pos;
            } while (true);
            break;
        } while (true);
    }

    private char switchCase(char ch) {
        if (!Character.isUpperCase((char)ch)) return Character.toUpperCase((char)ch);
        return Character.toLowerCase((char)ch);
    }

    private final boolean isInViMoveOperationState() {
        if (this.state == State.VI_CHANGE_TO) return true;
        if (this.state == State.VI_DELETE_TO) return true;
        if (this.state == State.VI_YANK_TO) return true;
        return false;
    }

    private boolean viNextWord(int count) throws IOException {
        int pos = this.buf.cursor;
        int end = this.buf.buffer.length();
        for (int i = 0; pos < end && i < count; ++i) {
            while (pos < end && !this.isDelimiter((char)this.buf.buffer.charAt((int)pos))) {
                ++pos;
            }
            if (i >= count - 1 && this.state == State.VI_CHANGE_TO) continue;
            while (pos < end && this.isDelimiter((char)this.buf.buffer.charAt((int)pos))) {
                ++pos;
            }
        }
        this.setCursorPosition((int)pos);
        return true;
    }

    private boolean viEndWord(int count) throws IOException {
        int pos = this.buf.cursor;
        int end = this.buf.buffer.length();
        for (int i = 0; pos < end && i < count; ++i) {
            if (pos < end - 1 && !this.isDelimiter((char)this.buf.buffer.charAt((int)pos)) && this.isDelimiter((char)this.buf.buffer.charAt((int)(pos + 1)))) {
                ++pos;
            }
            while (pos < end && this.isDelimiter((char)this.buf.buffer.charAt((int)pos))) {
                ++pos;
            }
            while (pos < end - 1 && !this.isDelimiter((char)this.buf.buffer.charAt((int)(pos + 1)))) {
                ++pos;
            }
        }
        this.setCursorPosition((int)pos);
        return true;
    }

    private boolean previousWord() throws IOException {
        while (this.isDelimiter((char)this.buf.current()) && this.moveCursor((int)-1) != 0) {
        }
        while (!this.isDelimiter((char)this.buf.current())) {
            if (this.moveCursor((int)-1) == 0) return true;
        }
        return true;
    }

    private boolean nextWord() throws IOException {
        while (this.isDelimiter((char)this.buf.nextChar()) && this.moveCursor((int)1) != 0) {
        }
        while (!this.isDelimiter((char)this.buf.nextChar())) {
            if (this.moveCursor((int)1) == 0) return true;
        }
        return true;
    }

    private boolean unixWordRubout(int count) throws IOException {
        boolean success = true;
        StringBuilder killed = new StringBuilder();
        while (count > 0) {
            char c;
            if (this.buf.cursor == 0) {
                success = false;
                break;
            }
            while (this.isWhitespace((char)this.buf.current()) && (c = this.buf.current()) != '\u0000') {
                killed.append((char)c);
                this.backspace();
            }
            while (!this.isWhitespace((char)this.buf.current()) && (c = this.buf.current()) != '\u0000') {
                killed.append((char)c);
                this.backspace();
            }
            --count;
        }
        String copy = killed.reverse().toString();
        this.killRing.addBackwards((String)copy);
        return success;
    }

    private String insertComment(boolean isViMode) throws IOException {
        String comment = this.getCommentBegin();
        this.setCursorPosition((int)0);
        this.putString((CharSequence)comment);
        if (!isViMode) return this.accept();
        this.consoleKeys.setKeyMap((String)"vi-insert");
        return this.accept();
    }

    private boolean insert(int count, CharSequence str) throws IOException {
        int i = 0;
        do {
            if (i >= count) {
                this.drawBuffer();
                return true;
            }
            this.buf.write((CharSequence)str);
            if (this.mask == null) {
                this.print((CharSequence)str);
            } else if (this.mask.charValue() != '\u0000') {
                this.print((char)this.mask.charValue(), (int)str.length());
            }
            ++i;
        } while (true);
    }

    private int viSearch(char searchChar) throws IOException {
        int start;
        int i;
        boolean isForward = searchChar == '/';
        CursorBuffer origBuffer = this.buf.copy();
        this.setCursorPosition((int)0);
        this.killLine();
        this.putString((CharSequence)Character.toString((char)searchChar));
        this.flush();
        boolean isAborted = false;
        boolean isComplete = false;
        int ch = -1;
        while (!isAborted && !isComplete && (ch = this.readCharacter()) != -1) {
            switch (ch) {
                case 27: {
                    isAborted = true;
                    break;
                }
                case 8: 
                case 127: {
                    this.backspace();
                    if (this.buf.cursor != 0) break;
                    isAborted = true;
                    break;
                }
                case 10: 
                case 13: {
                    isComplete = true;
                    break;
                }
                default: {
                    this.putString((CharSequence)Character.toString((char)((char)ch)));
                }
            }
            this.flush();
        }
        if (ch == -1 || isAborted) {
            this.setCursorPosition((int)0);
            this.killLine();
            this.putString((CharSequence)origBuffer.buffer);
            this.setCursorPosition((int)origBuffer.cursor);
            return -1;
        }
        String searchTerm = this.buf.buffer.substring((int)1);
        int idx = -1;
        int end = this.history.index();
        int n = start = end <= this.history.size() ? 0 : end - this.history.size();
        if (isForward) {
            for (i = start; i < end; ++i) {
                if (!this.history.get((int)i).toString().contains((CharSequence)searchTerm)) continue;
                idx = i;
                break;
            }
        } else {
            for (i = end - 1; i >= start; --i) {
                if (!this.history.get((int)i).toString().contains((CharSequence)searchTerm)) continue;
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            this.setCursorPosition((int)0);
            this.killLine();
            this.putString((CharSequence)origBuffer.buffer);
            this.setCursorPosition((int)0);
            return -1;
        }
        this.setCursorPosition((int)0);
        this.killLine();
        this.putString((CharSequence)this.history.get((int)idx));
        this.setCursorPosition((int)0);
        this.flush();
        isComplete = false;
        while (!isComplete) {
            ch = this.readCharacter();
            if (ch == -1) return ch;
            boolean forward = isForward;
            switch (ch) {
                case 80: 
                case 112: {
                    forward = !isForward;
                }
                case 78: 
                case 110: {
                    int i2;
                    boolean isMatch = false;
                    if (forward) {
                        for (i2 = idx + 1; !isMatch && i2 < end; ++i2) {
                            if (!this.history.get((int)i2).toString().contains((CharSequence)searchTerm)) continue;
                            idx = i2;
                            isMatch = true;
                        }
                    } else {
                        for (i2 = idx - 1; !isMatch && i2 >= start; --i2) {
                            if (!this.history.get((int)i2).toString().contains((CharSequence)searchTerm)) continue;
                            idx = i2;
                            isMatch = true;
                        }
                    }
                    if (!isMatch) break;
                    this.setCursorPosition((int)0);
                    this.killLine();
                    this.putString((CharSequence)this.history.get((int)idx));
                    this.setCursorPosition((int)0);
                    break;
                }
                default: {
                    isComplete = true;
                }
            }
            this.flush();
        }
        return ch;
    }

    public void setParenBlinkTimeout(int timeout) {
        this.parenBlinkTimeout = timeout;
    }

    private void insertClose(String s) throws IOException {
        this.putString((CharSequence)s);
        int closePosition = this.buf.cursor;
        this.moveCursor((int)-1);
        this.viMatch();
        if (this.in.isNonBlockingEnabled()) {
            this.in.peek((long)((long)this.parenBlinkTimeout));
        }
        this.setCursorPosition((int)closePosition);
    }

    private boolean viMatch() throws IOException {
        int pos = this.buf.cursor;
        if (pos == this.buf.length()) {
            return false;
        }
        int type = this.getBracketType((char)this.buf.buffer.charAt((int)pos));
        int move = type < 0 ? -1 : 1;
        int count = 1;
        if (type == 0) {
            return false;
        }
        while (count > 0) {
            if ((pos += move) < 0) return false;
            if (pos >= this.buf.buffer.length()) {
                return false;
            }
            int curType = this.getBracketType((char)this.buf.buffer.charAt((int)pos));
            if (curType == type) {
                ++count;
                continue;
            }
            if (curType != -type) continue;
            --count;
        }
        if (move > 0 && this.isInViMoveOperationState()) {
            ++pos;
        }
        this.setCursorPosition((int)pos);
        return true;
    }

    private int getBracketType(char ch) {
        switch (ch) {
            case '[': {
                return 1;
            }
            case ']': {
                return -1;
            }
            case '{': {
                return 2;
            }
            case '}': {
                return -2;
            }
            case '(': {
                return 3;
            }
            case ')': {
                return -3;
            }
        }
        return 0;
    }

    private boolean deletePreviousWord() throws IOException {
        char c;
        StringBuilder killed = new StringBuilder();
        while (this.isDelimiter((char)(c = this.buf.current())) && c != '\u0000') {
            killed.append((char)c);
            this.backspace();
        }
        while (!this.isDelimiter((char)(c = this.buf.current())) && c != '\u0000') {
            killed.append((char)c);
            this.backspace();
        }
        String copy = killed.reverse().toString();
        this.killRing.addBackwards((String)copy);
        return true;
    }

    private boolean deleteNextWord() throws IOException {
        char c;
        StringBuilder killed = new StringBuilder();
        while (this.isDelimiter((char)(c = this.buf.nextChar())) && c != '\u0000') {
            killed.append((char)c);
            this.delete();
        }
        while (!this.isDelimiter((char)(c = this.buf.nextChar())) && c != '\u0000') {
            killed.append((char)c);
            this.delete();
        }
        String copy = killed.toString();
        this.killRing.add((String)copy);
        return true;
    }

    private boolean capitalizeWord() throws IOException {
        char c;
        boolean first = true;
        int i = 1;
        while (this.buf.cursor + i - 1 < this.buf.length() && !this.isDelimiter((char)(c = this.buf.buffer.charAt((int)(this.buf.cursor + i - 1))))) {
            this.buf.buffer.setCharAt((int)(this.buf.cursor + i - 1), (char)(first ? Character.toUpperCase((char)c) : Character.toLowerCase((char)c)));
            first = false;
            ++i;
        }
        this.drawBuffer();
        this.moveCursor((int)(i - 1));
        return true;
    }

    private boolean upCaseWord() throws IOException {
        char c;
        int i = 1;
        while (this.buf.cursor + i - 1 < this.buf.length() && !this.isDelimiter((char)(c = this.buf.buffer.charAt((int)(this.buf.cursor + i - 1))))) {
            this.buf.buffer.setCharAt((int)(this.buf.cursor + i - 1), (char)Character.toUpperCase((char)c));
            ++i;
        }
        this.drawBuffer();
        this.moveCursor((int)(i - 1));
        return true;
    }

    private boolean downCaseWord() throws IOException {
        char c;
        int i = 1;
        while (this.buf.cursor + i - 1 < this.buf.length() && !this.isDelimiter((char)(c = this.buf.buffer.charAt((int)(this.buf.cursor + i - 1))))) {
            this.buf.buffer.setCharAt((int)(this.buf.cursor + i - 1), (char)Character.toLowerCase((char)c));
            ++i;
        }
        this.drawBuffer();
        this.moveCursor((int)(i - 1));
        return true;
    }

    private boolean transposeChars(int count) throws IOException {
        while (count > 0) {
            if (this.buf.cursor == 0) return false;
            if (this.buf.cursor == this.buf.buffer.length()) {
                return false;
            }
            int first = this.buf.cursor - 1;
            int second = this.buf.cursor;
            char tmp = this.buf.buffer.charAt((int)first);
            this.buf.buffer.setCharAt((int)first, (char)this.buf.buffer.charAt((int)second));
            this.buf.buffer.setCharAt((int)second, (char)tmp);
            this.moveInternal((int)-1);
            this.drawBuffer();
            this.moveInternal((int)2);
            --count;
        }
        return true;
    }

    public boolean isKeyMap(String name) {
        KeyMap map = this.consoleKeys.getKeys();
        KeyMap mapByName = this.consoleKeys.getKeyMaps().get((Object)name);
        if (mapByName == null) {
            return false;
        }
        if (map != mapByName) return false;
        return true;
    }

    public String accept() throws IOException {
        this.moveToEnd();
        this.println();
        this.flush();
        return this.finishBuffer();
    }

    private void abort() throws IOException {
        this.beep();
        this.buf.clear();
        this.println();
        this.redrawLine();
    }

    public int moveCursor(int num) throws IOException {
        int where = num;
        if (this.buf.cursor == 0 && where <= 0) {
            return 0;
        }
        if (this.buf.cursor == this.buf.buffer.length() && where >= 0) {
            return 0;
        }
        if (this.buf.cursor + where < 0) {
            where = -this.buf.cursor;
        } else if (this.buf.cursor + where > this.buf.buffer.length()) {
            where = this.buf.buffer.length() - this.buf.cursor;
        }
        this.moveInternal((int)where);
        return where;
    }

    private void moveInternal(int where) throws IOException {
        this.buf.cursor += where;
        if (this.terminal.isAnsiSupported()) {
            int oldLine;
            if (where < 0) {
                this.back((int)Math.abs((int)where));
                return;
            }
            int width = this.getTerminal().getWidth();
            int cursor = this.getCursorPosition();
            int newLine = cursor / width;
            if (newLine > (oldLine = (cursor - where) / width)) {
                this.printAnsiSequence((String)(newLine - oldLine + "B"));
            }
            this.printAnsiSequence((String)(1 + cursor % width + "G"));
            return;
        }
        if (where >= 0) {
            if (this.buf.cursor == 0) {
                return;
            }
            if (this.mask == null) {
                this.print((char[])this.buf.buffer.substring((int)(this.buf.cursor - where), (int)this.buf.cursor).toCharArray());
                return;
            }
            char c = this.mask.charValue();
            if (this.mask.charValue() == '\u0000') return;
            this.print((char)c, (int)Math.abs((int)where));
            return;
        }
        int len = 0;
        int i = this.buf.cursor;
        do {
            if (i >= this.buf.cursor - where) {
                char[] chars = new char[len];
                Arrays.fill((char[])chars, (char)'\b');
                this.out.write((char[])chars);
                return;
            }
            len = this.buf.buffer.charAt((int)i) == '\t' ? (len += 4) : ++len;
            ++i;
        } while (true);
    }

    public final boolean replace(int num, String replacement) {
        this.buf.buffer.replace((int)(this.buf.cursor - num), (int)this.buf.cursor, (String)replacement);
        try {
            this.moveCursor((int)(-num));
            this.drawBuffer((int)Math.max((int)0, (int)(num - replacement.length())));
            this.moveCursor((int)replacement.length());
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final int readCharacter() throws IOException {
        int c = this.reader.read();
        if (c < 0) return c;
        Log.trace((Object[])new Object[]{"Keystroke: ", Integer.valueOf((int)c)});
        if (!this.terminal.isSupported()) return c;
        this.clearEcho((int)c);
        return c;
    }

    private int clearEcho(int c) throws IOException {
        if (!this.terminal.isEchoEnabled()) {
            return 0;
        }
        int num = this.countEchoCharacters((int)c);
        this.back((int)num);
        this.drawBuffer((int)num);
        return num;
    }

    private int countEchoCharacters(int c) {
        if (c != 9) return this.getPrintableCharacters((int)c).length();
        int tabStop = 8;
        int position = this.getCursorPosition();
        return tabStop - position % tabStop;
    }

    private StringBuilder getPrintableCharacters(int ch) {
        StringBuilder sbuff = new StringBuilder();
        if (ch < 32) {
            sbuff.append((char)'^');
            sbuff.append((char)((char)(ch + 64)));
            return sbuff;
        }
        if (ch < 127) {
            sbuff.append((int)ch);
            return sbuff;
        }
        if (ch == 127) {
            sbuff.append((char)'^');
            sbuff.append((char)'?');
            return sbuff;
        }
        sbuff.append((char)'M');
        sbuff.append((char)'-');
        if (ch < 160) {
            sbuff.append((char)'^');
            sbuff.append((char)((char)(ch - 128 + 64)));
            return sbuff;
        }
        if (ch < 255) {
            sbuff.append((char)((char)(ch - 128)));
            return sbuff;
        }
        sbuff.append((char)'^');
        sbuff.append((char)'?');
        return sbuff;
    }

    public final int readCharacter(char ... allowed) throws IOException {
        char c;
        Arrays.sort((char[])allowed);
        while (Arrays.binarySearch((char[])allowed, (char)(c = (char)this.readCharacter())) < 0) {
        }
        return c;
    }

    public String readLine() throws IOException {
        return this.readLine((String)((String)null));
    }

    public String readLine(Character mask) throws IOException {
        return this.readLine(null, (Character)mask);
    }

    public String readLine(String prompt) throws IOException {
        return this.readLine((String)prompt, null);
    }

    public boolean setKeyMap(String name) {
        return this.consoleKeys.setKeyMap((String)name);
    }

    public String getKeyMap() {
        return this.consoleKeys.getKeys().getName();
    }

    public String readLine(String prompt, Character mask) throws IOException {
        int repeatCount = 0;
        this.mask = mask;
        if (prompt != null) {
            this.setPrompt((String)prompt);
        } else {
            prompt = this.getPrompt();
        }
        try {
            if (!this.terminal.isSupported()) {
                this.beforeReadLine((String)prompt, (Character)mask);
            }
            if (prompt != null && prompt.length() > 0) {
                this.out.write((String)prompt);
                this.out.flush();
            }
            if (!this.terminal.isSupported()) {
                String string = this.readLineSimple();
                return string;
            }
            if (this.handleUserInterrupt && this.terminal instanceof UnixTerminal) {
                ((UnixTerminal)this.terminal).disableInterruptCharacter();
            }
            String originalPrompt = this.prompt;
            this.state = State.NORMAL;
            boolean success = true;
            StringBuilder sb = new StringBuilder();
            Stack<Character> pushBackChar = new Stack<Character>();
            do {
                int c;
                int n = c = pushBackChar.isEmpty() ? this.readCharacter() : (int)((Character)pushBackChar.pop()).charValue();
                if (c == -1) {
                    String string = null;
                    return string;
                }
                sb.appendCodePoint((int)c);
                if (this.recording) {
                    this.macro = this.macro + new String((int[])new int[]{c}, (int)0, (int)1);
                }
                Object o = this.getKeys().getBound((CharSequence)sb);
                if (!this.recording && !(o instanceof KeyMap)) {
                    if (o != Operation.YANK_POP && o != Operation.YANK) {
                        this.killRing.resetLastYank();
                    }
                    if (o != Operation.KILL_LINE && o != Operation.KILL_WHOLE_LINE && o != Operation.BACKWARD_KILL_WORD && o != Operation.KILL_WORD && o != Operation.UNIX_LINE_DISCARD && o != Operation.UNIX_WORD_RUBOUT) {
                        this.killRing.resetLastKill();
                    }
                }
                if (o == Operation.DO_LOWERCASE_VERSION) {
                    sb.setLength((int)(sb.length() - 1));
                    sb.append((char)Character.toLowerCase((char)((char)c)));
                    o = this.getKeys().getBound((CharSequence)sb);
                }
                if (o instanceof KeyMap) {
                    if (c != 27 || !pushBackChar.isEmpty() || !this.in.isNonBlockingEnabled() || this.in.peek((long)this.escapeTimeout) != -2 || (o = ((KeyMap)o).getAnotherKey()) == null || o instanceof KeyMap) continue;
                    sb.setLength((int)0);
                }
                while (o == null && sb.length() > 0) {
                    c = (int)sb.charAt((int)(sb.length() - 1));
                    sb.setLength((int)(sb.length() - 1));
                    Object o2 = this.getKeys().getBound((CharSequence)sb);
                    if (!(o2 instanceof KeyMap) || (o = ((KeyMap)o2).getAnotherKey()) == null) continue;
                    pushBackChar.push(Character.valueOf((char)((char)c)));
                }
                if (o == null) continue;
                Log.trace((Object[])new Object[]{"Binding: ", o});
                if (o instanceof String) {
                    String macro = (String)o;
                    for (int i = 0; i < macro.length(); ++i) {
                        pushBackChar.push(Character.valueOf((char)macro.charAt((int)(macro.length() - 1 - i))));
                    }
                    sb.setLength((int)0);
                    continue;
                }
                if (o instanceof ActionListener) {
                    ((ActionListener)o).actionPerformed(null);
                    sb.setLength((int)0);
                    continue;
                }
                if (this.state == State.SEARCH || this.state == State.FORWARD_SEARCH) {
                    int cursorDest = -1;
                    switch ((Operation)((Object)o)) {
                        case ABORT: {
                            this.state = State.NORMAL;
                            this.buf.clear();
                            this.buf.buffer.append((StringBuffer)this.searchTerm);
                            break;
                        }
                        case REVERSE_SEARCH_HISTORY: {
                            this.state = State.SEARCH;
                            if (this.searchTerm.length() == 0) {
                                this.searchTerm.append((String)this.previousSearchTerm);
                            }
                            if (this.searchIndex <= 0) break;
                            this.searchIndex = this.searchBackwards((String)this.searchTerm.toString(), (int)this.searchIndex);
                            break;
                        }
                        case FORWARD_SEARCH_HISTORY: {
                            this.state = State.FORWARD_SEARCH;
                            if (this.searchTerm.length() == 0) {
                                this.searchTerm.append((String)this.previousSearchTerm);
                            }
                            if (this.searchIndex <= -1 || this.searchIndex >= this.history.size() - 1) break;
                            this.searchIndex = this.searchForwards((String)this.searchTerm.toString(), (int)this.searchIndex);
                            break;
                        }
                        case BACKWARD_DELETE_CHAR: {
                            if (this.searchTerm.length() <= 0) break;
                            this.searchTerm.deleteCharAt((int)(this.searchTerm.length() - 1));
                            if (this.state == State.SEARCH) {
                                this.searchIndex = this.searchBackwards((String)this.searchTerm.toString());
                                break;
                            }
                            this.searchIndex = this.searchForwards((String)this.searchTerm.toString());
                            break;
                        }
                        case SELF_INSERT: {
                            this.searchTerm.appendCodePoint((int)c);
                            if (this.state == State.SEARCH) {
                                this.searchIndex = this.searchBackwards((String)this.searchTerm.toString());
                                break;
                            }
                            this.searchIndex = this.searchForwards((String)this.searchTerm.toString());
                            break;
                        }
                        default: {
                            if (this.searchIndex != -1) {
                                this.history.moveTo((int)this.searchIndex);
                                cursorDest = this.history.current().toString().indexOf((String)this.searchTerm.toString());
                            }
                            this.state = State.NORMAL;
                        }
                    }
                    if (this.state == State.SEARCH || this.state == State.FORWARD_SEARCH) {
                        if (this.searchTerm.length() == 0) {
                            if (this.state == State.SEARCH) {
                                this.printSearchStatus((String)"", (String)"");
                            } else {
                                this.printForwardSearchStatus((String)"", (String)"");
                            }
                            this.searchIndex = -1;
                        } else if (this.searchIndex == -1) {
                            this.beep();
                            this.printSearchStatus((String)this.searchTerm.toString(), (String)"");
                        } else if (this.state == State.SEARCH) {
                            this.printSearchStatus((String)this.searchTerm.toString(), (String)this.history.get((int)this.searchIndex).toString());
                        } else {
                            this.printForwardSearchStatus((String)this.searchTerm.toString(), (String)this.history.get((int)this.searchIndex).toString());
                        }
                    } else {
                        this.restoreLine((String)originalPrompt, (int)cursorDest);
                    }
                }
                if (this.state != State.SEARCH && this.state != State.FORWARD_SEARCH) {
                    boolean isArgDigit = false;
                    int count = repeatCount == 0 ? 1 : repeatCount;
                    success = true;
                    if (o instanceof Operation) {
                        Operation op = (Operation)((Object)o);
                        int cursorStart = this.buf.cursor;
                        State origState = this.state;
                        if (this.state == State.VI_CHANGE_TO || this.state == State.VI_YANK_TO || this.state == State.VI_DELETE_TO) {
                            op = this.viDeleteChangeYankToRemap((Operation)op);
                        }
                        switch (2.$SwitchMap$jline$console$Operation[op.ordinal()]) {
                            case 23: {
                                boolean isTabLiteral = false;
                                if (this.copyPasteDetection && c == 9 && (!pushBackChar.isEmpty() || this.in.isNonBlockingEnabled() && this.in.peek((long)this.escapeTimeout) != -2)) {
                                    isTabLiteral = true;
                                }
                                if (!isTabLiteral) {
                                    success = this.complete();
                                    break;
                                }
                                this.putString((CharSequence)sb);
                                break;
                            }
                            case 24: {
                                this.printCompletionCandidates();
                                break;
                            }
                            case 25: {
                                success = this.setCursorPosition((int)0);
                                break;
                            }
                            case 26: {
                                success = this.yank();
                                break;
                            }
                            case 27: {
                                success = this.yankPop();
                                break;
                            }
                            case 28: {
                                success = this.killLine();
                                break;
                            }
                            case 29: {
                                success = this.setCursorPosition((int)0) && this.killLine();
                                break;
                            }
                            case 30: {
                                success = this.clearScreen();
                                this.redrawLine();
                                break;
                            }
                            case 31: {
                                this.buf.setOverTyping((boolean)(!this.buf.isOverTyping()));
                                break;
                            }
                            case 22: {
                                this.putString((CharSequence)sb);
                                break;
                            }
                            case 32: {
                                String string = this.accept();
                                return string;
                            }
                            case 2: {
                                if (this.searchTerm != null) break;
                                this.abort();
                                break;
                            }
                            case 33: {
                                if (!this.handleUserInterrupt) break;
                                this.println();
                                this.flush();
                                String partialLine = this.buf.buffer.toString();
                                this.buf.clear();
                                this.history.moveToEnd();
                                throw new UserInterruptException((String)partialLine);
                            }
                            case 34: {
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                                String partialLine = this.accept();
                                return partialLine;
                            }
                            case 35: {
                                success = this.previousWord();
                                break;
                            }
                            case 36: {
                                success = this.nextWord();
                                break;
                            }
                            case 37: {
                                success = this.moveHistory((boolean)false);
                                break;
                            }
                            case 38: {
                                success = this.moveHistory((boolean)false, (int)count) && this.setCursorPosition((int)0);
                                break;
                            }
                            case 39: {
                                success = this.moveHistory((boolean)true);
                                break;
                            }
                            case 40: {
                                success = this.moveHistory((boolean)true, (int)count) && this.setCursorPosition((int)0);
                                break;
                            }
                            case 21: {
                                success = this.backspace();
                                break;
                            }
                            case 41: {
                                if (this.buf.buffer.length() == 0) {
                                    String partialLine = null;
                                    return partialLine;
                                }
                                success = this.deleteCurrentCharacter();
                                break;
                            }
                            case 42: {
                                success = this.deleteCurrentCharacter();
                                break;
                            }
                            case 3: {
                                success = this.moveCursor((int)(-count)) != 0;
                                break;
                            }
                            case 4: {
                                success = this.moveCursor((int)count) != 0;
                                break;
                            }
                            case 43: {
                                success = this.resetLine();
                                break;
                            }
                            case 44: {
                                success = this.unixWordRubout((int)count);
                                break;
                            }
                            case 45: {
                                success = this.deletePreviousWord();
                                break;
                            }
                            case 46: {
                                success = this.deleteNextWord();
                                break;
                            }
                            case 47: {
                                success = this.history.moveToFirst();
                                if (!success) break;
                                this.setBuffer((CharSequence)this.history.current());
                                break;
                            }
                            case 48: {
                                success = this.history.moveToLast();
                                if (!success) break;
                                this.setBuffer((CharSequence)this.history.current());
                                break;
                            }
                            case 49: {
                                this.searchTerm = new StringBuffer((String)this.buf.upToCursor());
                                this.searchIndex = this.searchBackwards((String)this.searchTerm.toString(), (int)this.history.index(), (boolean)true);
                                if (this.searchIndex == -1) {
                                    this.beep();
                                    break;
                                }
                                success = this.history.moveTo((int)this.searchIndex);
                                if (!success) break;
                                this.setBufferKeepPos((CharSequence)this.history.current());
                                break;
                            }
                            case 50: {
                                this.searchTerm = new StringBuffer((String)this.buf.upToCursor());
                                int index = this.history.index() + 1;
                                if (index == this.history.size()) {
                                    this.history.moveToEnd();
                                    this.setBufferKeepPos((String)this.searchTerm.toString());
                                    break;
                                }
                                if (index >= this.history.size()) break;
                                this.searchIndex = this.searchForwards((String)this.searchTerm.toString(), (int)index, (boolean)true);
                                if (this.searchIndex == -1) {
                                    this.beep();
                                    break;
                                }
                                success = this.history.moveTo((int)this.searchIndex);
                                if (!success) break;
                                this.setBufferKeepPos((CharSequence)this.history.current());
                                break;
                            }
                            case 19: {
                                if (this.searchTerm != null) {
                                    this.previousSearchTerm = this.searchTerm.toString();
                                }
                                this.searchTerm = new StringBuffer((CharSequence)this.buf.buffer);
                                this.state = State.SEARCH;
                                if (this.searchTerm.length() > 0) {
                                    this.searchIndex = this.searchBackwards((String)this.searchTerm.toString());
                                    if (this.searchIndex == -1) {
                                        this.beep();
                                    }
                                    this.printSearchStatus((String)this.searchTerm.toString(), (String)(this.searchIndex > -1 ? this.history.get((int)this.searchIndex).toString() : ""));
                                    break;
                                }
                                this.searchIndex = -1;
                                this.printSearchStatus((String)"", (String)"");
                                break;
                            }
                            case 20: {
                                if (this.searchTerm != null) {
                                    this.previousSearchTerm = this.searchTerm.toString();
                                }
                                this.searchTerm = new StringBuffer((CharSequence)this.buf.buffer);
                                this.state = State.FORWARD_SEARCH;
                                if (this.searchTerm.length() > 0) {
                                    this.searchIndex = this.searchForwards((String)this.searchTerm.toString());
                                    if (this.searchIndex == -1) {
                                        this.beep();
                                    }
                                    this.printForwardSearchStatus((String)this.searchTerm.toString(), (String)(this.searchIndex > -1 ? this.history.get((int)this.searchIndex).toString() : ""));
                                    break;
                                }
                                this.searchIndex = -1;
                                this.printForwardSearchStatus((String)"", (String)"");
                                break;
                            }
                            case 51: {
                                success = this.capitalizeWord();
                                break;
                            }
                            case 52: {
                                success = this.upCaseWord();
                                break;
                            }
                            case 53: {
                                success = this.downCaseWord();
                                break;
                            }
                            case 5: {
                                success = this.moveToEnd();
                                break;
                            }
                            case 54: {
                                this.putString((CharSequence)"\t");
                                break;
                            }
                            case 55: {
                                this.consoleKeys.loadKeys((String)this.appName, (URL)this.inputrcUrl);
                                break;
                            }
                            case 56: {
                                this.recording = true;
                                break;
                            }
                            case 57: {
                                this.recording = false;
                                this.macro = this.macro.substring((int)0, (int)(this.macro.length() - sb.length()));
                                break;
                            }
                            case 58: {
                                for (int i = 0; i < this.macro.length(); ++i) {
                                    pushBackChar.push(Character.valueOf((char)this.macro.charAt((int)(this.macro.length() - 1 - i))));
                                }
                                sb.setLength((int)0);
                                break;
                            }
                            case 59: {
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                                break;
                            }
                            case 60: {
                                if (this.state == State.NORMAL) {
                                    this.moveCursor((int)-1);
                                }
                                this.consoleKeys.setKeyMap((String)"vi-move");
                                break;
                            }
                            case 61: {
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                                break;
                            }
                            case 62: {
                                this.moveCursor((int)1);
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                                break;
                            }
                            case 63: {
                                success = this.moveToEnd();
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                                break;
                            }
                            case 1: {
                                if (this.buf.buffer.length() == 0) {
                                    String i = null;
                                    return i;
                                }
                                String i = this.accept();
                                return i;
                            }
                            case 64: {
                                success = this.transposeChars((int)count);
                                break;
                            }
                            case 65: {
                                String i = this.insertComment((boolean)false);
                                return i;
                            }
                            case 66: {
                                this.insertClose((String)"}");
                                break;
                            }
                            case 67: {
                                this.insertClose((String)")");
                                break;
                            }
                            case 68: {
                                this.insertClose((String)"]");
                                break;
                            }
                            case 69: {
                                String i = this.insertComment((boolean)true);
                                return i;
                            }
                            case 6: {
                                success = this.viMatch();
                                break;
                            }
                            case 70: {
                                int lastChar = this.viSearch((char)sb.charAt((int)0));
                                if (lastChar == -1) break;
                                pushBackChar.push(Character.valueOf((char)((char)lastChar)));
                                break;
                            }
                            case 8: {
                                repeatCount = repeatCount * 10 + sb.charAt((int)0) - 48;
                                isArgDigit = true;
                                break;
                            }
                            case 7: {
                                if (repeatCount > 0) {
                                    repeatCount = repeatCount * 10 + sb.charAt((int)0) - 48;
                                    isArgDigit = true;
                                    break;
                                }
                                success = this.setCursorPosition((int)0);
                                break;
                            }
                            case 13: {
                                success = this.setCursorPosition((int)0) && this.viNextWord((int)1);
                                break;
                            }
                            case 9: {
                                success = this.viPreviousWord((int)count);
                                break;
                            }
                            case 12: {
                                success = this.viNextWord((int)count);
                                break;
                            }
                            case 10: {
                                success = this.viEndWord((int)count);
                                break;
                            }
                            case 71: {
                                success = this.setCursorPosition((int)0);
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                                break;
                            }
                            case 72: {
                                success = this.viRubout((int)count);
                                break;
                            }
                            case 73: {
                                success = this.viDelete((int)count);
                                break;
                            }
                            case 16: {
                                if (this.state == State.VI_DELETE_TO) {
                                    success = this.setCursorPosition((int)0) && this.killLine();
                                    this.state = origState = State.NORMAL;
                                    break;
                                }
                                this.state = State.VI_DELETE_TO;
                                break;
                            }
                            case 17: {
                                if (this.state == State.VI_YANK_TO) {
                                    this.yankBuffer = this.buf.buffer.toString();
                                    this.state = origState = State.NORMAL;
                                    break;
                                }
                                this.state = State.VI_YANK_TO;
                                break;
                            }
                            case 18: {
                                if (this.state == State.VI_CHANGE_TO) {
                                    success = this.setCursorPosition((int)0) && this.killLine();
                                    this.state = origState = State.NORMAL;
                                    this.consoleKeys.setKeyMap((String)"vi-insert");
                                    break;
                                }
                                this.state = State.VI_CHANGE_TO;
                                break;
                            }
                            case 74: {
                                success = this.setCursorPosition((int)0) && this.killLine();
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                                break;
                            }
                            case 75: {
                                success = this.viPut((int)count);
                                break;
                            }
                            case 11: {
                                int searchChar = c != 59 && c != 44 ? (pushBackChar.isEmpty() ? this.readCharacter() : (int)((Character)pushBackChar.pop()).charValue()) : 0;
                                success = this.viCharSearch((int)count, (int)c, (int)searchChar);
                                break;
                            }
                            case 76: {
                                success = this.viChangeCase((int)count);
                                break;
                            }
                            case 77: {
                                success = this.viChangeChar((int)count, (int)(pushBackChar.isEmpty() ? this.readCharacter() : (int)((Character)pushBackChar.pop()).charValue()));
                                break;
                            }
                            case 78: {
                                success = this.viDeleteTo((int)this.buf.cursor, (int)this.buf.buffer.length(), (boolean)false);
                                break;
                            }
                            case 79: {
                                success = this.viDeleteTo((int)this.buf.cursor, (int)this.buf.buffer.length(), (boolean)true);
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                                break;
                            }
                            case 80: {
                                this.consoleKeys.setKeyMap((String)"emacs");
                                break;
                            }
                        }
                        if (origState != State.NORMAL) {
                            if (origState == State.VI_DELETE_TO) {
                                success = this.viDeleteTo((int)cursorStart, (int)this.buf.cursor, (boolean)false);
                            } else if (origState == State.VI_CHANGE_TO) {
                                success = this.viDeleteTo((int)cursorStart, (int)this.buf.cursor, (boolean)true);
                                this.consoleKeys.setKeyMap((String)"vi-insert");
                            } else if (origState == State.VI_YANK_TO) {
                                success = this.viYankTo((int)cursorStart, (int)this.buf.cursor);
                            }
                            this.state = State.NORMAL;
                        }
                        if (this.state == State.NORMAL && !isArgDigit) {
                            repeatCount = 0;
                        }
                        if (this.state != State.SEARCH && this.state != State.FORWARD_SEARCH) {
                            this.previousSearchTerm = "";
                            this.searchTerm = null;
                            this.searchIndex = -1;
                        }
                    }
                }
                if (!success) {
                    this.beep();
                }
                sb.setLength((int)0);
                this.flush();
            } while (true);
        }
        finally {
            if (!this.terminal.isSupported()) {
                this.afterReadLine();
            }
            if (this.handleUserInterrupt && this.terminal instanceof UnixTerminal) {
                ((UnixTerminal)this.terminal).enableInterruptCharacter();
            }
        }
    }

    private String readLineSimple() throws IOException {
        int i;
        StringBuilder buff = new StringBuilder();
        if (this.skipLF) {
            this.skipLF = false;
            i = this.readCharacter();
            if (i == -1) return buff.toString();
            if (i == 13) {
                return buff.toString();
            }
            if (i != 10) {
                buff.append((char)((char)i));
            }
        }
        while ((i = this.readCharacter()) != -1 || buff.length() != 0) {
            if (i == -1) return buff.toString();
            if (i == 10) {
                return buff.toString();
            }
            if (i == 13) {
                this.skipLF = true;
                return buff.toString();
            }
            buff.append((char)((char)i));
        }
        return null;
    }

    public boolean addCompleter(Completer completer) {
        return this.completers.add((Completer)completer);
    }

    public boolean removeCompleter(Completer completer) {
        return this.completers.remove((Object)completer);
    }

    public Collection<Completer> getCompleters() {
        return Collections.unmodifiableList(this.completers);
    }

    public void setCompletionHandler(CompletionHandler handler) {
        this.completionHandler = Preconditions.checkNotNull(handler);
    }

    public CompletionHandler getCompletionHandler() {
        return this.completionHandler;
    }

    protected boolean complete() throws IOException {
        Completer comp;
        if (this.completers.size() == 0) {
            return false;
        }
        LinkedList<CharSequence> candidates = new LinkedList<CharSequence>();
        String bufstr = this.buf.buffer.toString();
        int cursor = this.buf.cursor;
        int position = -1;
        Iterator<Completer> i$ = this.completers.iterator();
        while (i$.hasNext() && (position = (comp = i$.next()).complete((String)bufstr, (int)cursor, candidates)) == -1) {
        }
        if (candidates.size() == 0) return false;
        if (!this.getCompletionHandler().complete((ConsoleReader)this, candidates, (int)position)) return false;
        return true;
    }

    protected void printCompletionCandidates() throws IOException {
        if (this.completers.size() == 0) {
            return;
        }
        LinkedList<CharSequence> candidates = new LinkedList<CharSequence>();
        String bufstr = this.buf.buffer.toString();
        int cursor = this.buf.cursor;
        for (Completer comp : this.completers) {
            if (comp.complete((String)bufstr, (int)cursor, candidates) != -1) break;
        }
        CandidateListCompletionHandler.printCandidates((ConsoleReader)this, candidates);
        this.drawLine();
    }

    public void setAutoprintThreshold(int threshold) {
        this.autoprintThreshold = threshold;
    }

    public int getAutoprintThreshold() {
        return this.autoprintThreshold;
    }

    public void setPaginationEnabled(boolean enabled) {
        this.paginationEnabled = enabled;
    }

    public boolean isPaginationEnabled() {
        return this.paginationEnabled;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public History getHistory() {
        return this.history;
    }

    public void setHistoryEnabled(boolean enabled) {
        this.historyEnabled = enabled;
    }

    public boolean isHistoryEnabled() {
        return this.historyEnabled;
    }

    private boolean moveHistory(boolean next, int count) throws IOException {
        boolean ok = true;
        int i = 0;
        while (i < count) {
            ok = this.moveHistory((boolean)next);
            if (!ok) return ok;
            ++i;
        }
        return ok;
    }

    private boolean moveHistory(boolean next) throws IOException {
        if (next && !this.history.next()) {
            return false;
        }
        if (!next && !this.history.previous()) {
            return false;
        }
        this.setBuffer((CharSequence)this.history.current());
        return true;
    }

    private void print(int c) throws IOException {
        if (c == 9) {
            char[] chars = new char[4];
            Arrays.fill((char[])chars, (char)' ');
            this.out.write((char[])chars);
            return;
        }
        this.out.write((int)c);
    }

    private void print(char ... buff) throws IOException {
        char[] chars;
        int len = 0;
        for (char c : buff) {
            if (c == '\t') {
                len += 4;
                continue;
            }
            ++len;
        }
        if (len == buff.length) {
            chars = buff;
        } else {
            chars = new char[len];
            int pos = 0;
            for (char c : buff) {
                if (c == '\t') {
                    Arrays.fill((char[])chars, (int)pos, (int)(pos + 4), (char)' ');
                    pos += 4;
                    continue;
                }
                chars[pos] = c;
                ++pos;
            }
        }
        this.out.write((char[])chars);
    }

    private void print(char c, int num) throws IOException {
        if (num == 1) {
            this.print((int)c);
            return;
        }
        char[] chars = new char[num];
        Arrays.fill((char[])chars, (char)c);
        this.print((char[])chars);
    }

    public final void print(CharSequence s) throws IOException {
        this.print((char[])Preconditions.checkNotNull(s).toString().toCharArray());
    }

    public final void println(CharSequence s) throws IOException {
        this.print((char[])Preconditions.checkNotNull(s).toString().toCharArray());
        this.println();
    }

    public final void println() throws IOException {
        this.print((CharSequence)CR);
    }

    public final boolean delete() throws IOException {
        if (this.buf.cursor == this.buf.buffer.length()) {
            return false;
        }
        this.buf.buffer.delete((int)this.buf.cursor, (int)(this.buf.cursor + 1));
        this.drawBuffer((int)1);
        return true;
    }

    public boolean killLine() throws IOException {
        int cp = this.buf.cursor;
        int len = this.buf.buffer.length();
        if (cp >= len) {
            return false;
        }
        int num = len - cp;
        this.clearAhead((int)num, (int)0);
        char[] killed = new char[num];
        this.buf.buffer.getChars((int)cp, (int)(cp + num), (char[])killed, (int)0);
        this.buf.buffer.delete((int)cp, (int)(cp + num));
        String copy = new String((char[])killed);
        this.killRing.add((String)copy);
        return true;
    }

    public boolean yank() throws IOException {
        String yanked = this.killRing.yank();
        if (yanked == null) {
            return false;
        }
        this.putString((CharSequence)yanked);
        return true;
    }

    public boolean yankPop() throws IOException {
        if (!this.killRing.lastYank()) {
            return false;
        }
        String current = this.killRing.yank();
        if (current == null) {
            return false;
        }
        this.backspace((int)current.length());
        String yanked = this.killRing.yankPop();
        if (yanked == null) {
            return false;
        }
        this.putString((CharSequence)yanked);
        return true;
    }

    public boolean clearScreen() throws IOException {
        if (!this.terminal.isAnsiSupported()) {
            return false;
        }
        this.printAnsiSequence((String)"2J");
        this.printAnsiSequence((String)"1;1H");
        return true;
    }

    public void beep() throws IOException {
        if (!this.bellEnabled) return;
        this.print((int)7);
        this.flush();
    }

    public boolean paste() throws IOException {
        Clipboard clipboard;
        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        catch (Exception e) {
            return false;
        }
        if (clipboard == null) {
            return false;
        }
        Transferable transferable = clipboard.getContents(null);
        if (transferable == null) {
            return false;
        }
        try {
            String value;
            Object content = transferable.getTransferData((DataFlavor)DataFlavor.plainTextFlavor);
            if (content == null) {
                try {
                    content = new DataFlavor().getReaderForText((Transferable)transferable);
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if (content == null) {
                return false;
            }
            if (content instanceof Reader) {
                String line;
                value = "";
                BufferedReader read = new BufferedReader((Reader)((Reader)content));
                while ((line = read.readLine()) != null) {
                    if (value.length() > 0) {
                        value = value + "\n";
                    }
                    value = value + line;
                }
            } else {
                value = content.toString();
            }
            if (value == null) {
                return true;
            }
            this.putString((CharSequence)value);
            return true;
        }
        catch (UnsupportedFlavorException e) {
            Log.error((Object[])new Object[]{"Paste failed: ", e});
            return false;
        }
    }

    public void addTriggeredAction(char c, ActionListener listener) {
        this.triggeredActions.put((Character)Character.valueOf((char)c), (ActionListener)listener);
    }

    public void printColumns(Collection<? extends CharSequence> items) throws IOException {
        if (items == null) return;
        if (items.isEmpty()) {
            return;
        }
        int width = this.getTerminal().getWidth();
        int height = this.getTerminal().getHeight();
        int maxWidth = 0;
        for (CharSequence item : items) {
            maxWidth = Math.max((int)maxWidth, (int)item.length());
        }
        Log.debug((Object[])new Object[]{"Max width: ", Integer.valueOf((int)(maxWidth += 3))});
        int showLines = this.isPaginationEnabled() ? height - 1 : Integer.MAX_VALUE;
        StringBuilder buff = new StringBuilder();
        for (CharSequence item : items) {
            if (buff.length() + maxWidth > width) {
                this.println((CharSequence)buff);
                buff.setLength((int)0);
                if (--showLines == 0) {
                    this.print((CharSequence)resources.getString((String)"DISPLAY_MORE"));
                    this.flush();
                    int c = this.readCharacter();
                    if (c == 13 || c == 10) {
                        showLines = 1;
                    } else if (c != 113) {
                        showLines = height - 1;
                    }
                    this.back((int)resources.getString((String)"DISPLAY_MORE").length());
                    if (c == 113) break;
                }
            }
            buff.append((String)item.toString());
            for (int i = 0; i < maxWidth - item.length(); ++i) {
                buff.append((char)' ');
            }
        }
        if (buff.length() <= 0) return;
        this.println((CharSequence)buff);
    }

    private void beforeReadLine(String prompt, Character mask) {
        if (mask == null) return;
        if (this.maskThread != null) return;
        String fullPrompt = "\r" + prompt + "                 " + "                 " + "                 " + "\r" + prompt;
        this.maskThread = new Thread((ConsoleReader)this, (String)fullPrompt){
            final /* synthetic */ String val$fullPrompt;
            final /* synthetic */ ConsoleReader this$0;
            {
                this.this$0 = consoleReader;
                this.val$fullPrompt = string;
            }

            public void run() {
                while (!1.interrupted()) {
                    try {
                        Writer out = this.this$0.getOutput();
                        out.write((String)this.val$fullPrompt);
                        out.flush();
                        1.sleep((long)3L);
                    }
                    catch (IOException e) {
                        return;
                    }
                    catch (java.lang.InterruptedException e) {
                        return;
                    }
                }
            }
        };
        this.maskThread.setPriority((int)10);
        this.maskThread.setDaemon((boolean)true);
        this.maskThread.start();
    }

    private void afterReadLine() {
        if (this.maskThread != null && this.maskThread.isAlive()) {
            this.maskThread.interrupt();
        }
        this.maskThread = null;
    }

    public void resetPromptLine(String prompt, String buffer, int cursorDest) throws IOException {
        this.moveToEnd();
        this.buf.buffer.append((String)this.prompt);
        int promptLength = 0;
        if (this.prompt != null) {
            promptLength = this.prompt.length();
        }
        this.buf.cursor += promptLength;
        this.setPrompt((String)"");
        this.backspaceAll();
        this.setPrompt((String)prompt);
        this.redrawLine();
        this.setBuffer((String)buffer);
        if (cursorDest < 0) {
            cursorDest = buffer.length();
        }
        this.setCursorPosition((int)cursorDest);
        this.flush();
    }

    public void printSearchStatus(String searchTerm, String match) throws IOException {
        this.printSearchStatus((String)searchTerm, (String)match, (String)"(reverse-i-search)`");
    }

    public void printForwardSearchStatus(String searchTerm, String match) throws IOException {
        this.printSearchStatus((String)searchTerm, (String)match, (String)"(i-search)`");
    }

    private void printSearchStatus(String searchTerm, String match, String searchLabel) throws IOException {
        String prompt = searchLabel + searchTerm + "': ";
        int cursorDest = match.indexOf((String)searchTerm);
        this.resetPromptLine((String)prompt, (String)match, (int)cursorDest);
    }

    public void restoreLine(String originalPrompt, int cursorDest) throws IOException {
        String prompt = this.lastLine((String)originalPrompt);
        String buffer = this.buf.buffer.toString();
        this.resetPromptLine((String)prompt, (String)buffer, (int)cursorDest);
    }

    public int searchBackwards(String searchTerm, int startIndex) {
        return this.searchBackwards((String)searchTerm, (int)startIndex, (boolean)false);
    }

    public int searchBackwards(String searchTerm) {
        return this.searchBackwards((String)searchTerm, (int)this.history.index());
    }

    public int searchBackwards(String searchTerm, int startIndex, boolean startsWith) {
        History.Entry e;
        ListIterator<History.Entry> it = this.history.entries((int)startIndex);
        do {
            if (!it.hasPrevious()) return -1;
            e = it.previous();
        } while (!(startsWith ? e.value().toString().startsWith((String)searchTerm) : e.value().toString().contains((CharSequence)searchTerm)));
        return e.index();
    }

    public int searchForwards(String searchTerm, int startIndex) {
        return this.searchForwards((String)searchTerm, (int)startIndex, (boolean)false);
    }

    public int searchForwards(String searchTerm) {
        return this.searchForwards((String)searchTerm, (int)this.history.index());
    }

    public int searchForwards(String searchTerm, int startIndex, boolean startsWith) {
        History.Entry e;
        if (startIndex >= this.history.size()) {
            startIndex = this.history.size() - 1;
        }
        ListIterator<History.Entry> it = this.history.entries((int)startIndex);
        if (this.searchIndex != -1 && it.hasNext()) {
            it.next();
        }
        do {
            if (!it.hasNext()) return -1;
            e = it.next();
        } while (!(startsWith ? e.value().toString().startsWith((String)searchTerm) : e.value().toString().contains((CharSequence)searchTerm)));
        return e.index();
    }

    private boolean isDelimiter(char c) {
        if (Character.isLetterOrDigit((char)c)) return false;
        return true;
    }

    private boolean isWhitespace(char c) {
        return Character.isWhitespace((char)c);
    }

    private void printAnsiSequence(String sequence) throws IOException {
        this.print((int)27);
        this.print((int)91);
        this.print((CharSequence)sequence);
        this.flush();
    }
}

