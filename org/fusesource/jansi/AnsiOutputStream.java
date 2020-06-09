/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import org.fusesource.jansi.Ansi;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AnsiOutputStream
extends FilterOutputStream {
    public static final byte[] REST_CODE = AnsiOutputStream.resetCode();
    private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
    private byte[] buffer = new byte[100];
    private int pos = 0;
    private int startOfValue;
    private final ArrayList<Object> options = new ArrayList<E>();
    private static final int LOOKING_FOR_FIRST_ESC_CHAR = 0;
    private static final int LOOKING_FOR_SECOND_ESC_CHAR = 1;
    private static final int LOOKING_FOR_NEXT_ARG = 2;
    private static final int LOOKING_FOR_STR_ARG_END = 3;
    private static final int LOOKING_FOR_INT_ARG_END = 4;
    private static final int LOOKING_FOR_OSC_COMMAND = 5;
    private static final int LOOKING_FOR_OSC_COMMAND_END = 6;
    private static final int LOOKING_FOR_OSC_PARAM = 7;
    private static final int LOOKING_FOR_ST = 8;
    int state = 0;
    private static final int FIRST_ESC_CHAR = 27;
    private static final int SECOND_ESC_CHAR = 91;
    private static final int SECOND_OSC_CHAR = 93;
    private static final int BEL = 7;
    private static final int SECOND_ST_CHAR = 92;
    protected static final int ERASE_SCREEN_TO_END = 0;
    protected static final int ERASE_SCREEN_TO_BEGINING = 1;
    protected static final int ERASE_SCREEN = 2;
    protected static final int ERASE_LINE_TO_END = 0;
    protected static final int ERASE_LINE_TO_BEGINING = 1;
    protected static final int ERASE_LINE = 2;
    protected static final int ATTRIBUTE_INTENSITY_BOLD = 1;
    protected static final int ATTRIBUTE_INTENSITY_FAINT = 2;
    protected static final int ATTRIBUTE_ITALIC = 3;
    protected static final int ATTRIBUTE_UNDERLINE = 4;
    protected static final int ATTRIBUTE_BLINK_SLOW = 5;
    protected static final int ATTRIBUTE_BLINK_FAST = 6;
    protected static final int ATTRIBUTE_NEGATIVE_ON = 7;
    protected static final int ATTRIBUTE_CONCEAL_ON = 8;
    protected static final int ATTRIBUTE_UNDERLINE_DOUBLE = 21;
    protected static final int ATTRIBUTE_INTENSITY_NORMAL = 22;
    protected static final int ATTRIBUTE_UNDERLINE_OFF = 24;
    protected static final int ATTRIBUTE_BLINK_OFF = 25;
    protected static final int ATTRIBUTE_NEGATIVE_Off = 27;
    protected static final int ATTRIBUTE_CONCEAL_OFF = 28;
    protected static final int BLACK = 0;
    protected static final int RED = 1;
    protected static final int GREEN = 2;
    protected static final int YELLOW = 3;
    protected static final int BLUE = 4;
    protected static final int MAGENTA = 5;
    protected static final int CYAN = 6;
    protected static final int WHITE = 7;

    public AnsiOutputStream(OutputStream os) {
        super((OutputStream)os);
    }

    @Override
    public void write(int data) throws IOException {
        switch (this.state) {
            case 0: {
                if (data == 27) {
                    this.buffer[this.pos++] = (byte)data;
                    this.state = 1;
                    break;
                }
                this.out.write((int)data);
                break;
            }
            case 1: {
                this.buffer[this.pos++] = (byte)data;
                if (data == 91) {
                    this.state = 2;
                    break;
                }
                if (data == 93) {
                    this.state = 5;
                    break;
                }
                this.reset((boolean)false);
                break;
            }
            case 2: {
                this.buffer[this.pos++] = (byte)data;
                if (34 == data) {
                    this.startOfValue = this.pos - 1;
                    this.state = 3;
                    break;
                }
                if (48 <= data && data <= 57) {
                    this.startOfValue = this.pos - 1;
                    this.state = 4;
                    break;
                }
                if (59 == data) {
                    this.options.add(null);
                    break;
                }
                if (63 == data) {
                    this.options.add((Object)new Character((char)'?'));
                    break;
                }
                if (61 == data) {
                    this.options.add((Object)new Character((char)'='));
                    break;
                }
                this.reset((boolean)this.processEscapeCommand(this.options, (int)data));
                break;
            }
            case 4: {
                this.buffer[this.pos++] = (byte)data;
                if (48 <= data && data <= 57) break;
                String strValue = new String((byte[])this.buffer, (int)this.startOfValue, (int)(this.pos - 1 - this.startOfValue), (String)"UTF-8");
                Integer value = new Integer((String)strValue);
                this.options.add((Object)value);
                if (data == 59) {
                    this.state = 2;
                    break;
                }
                this.reset((boolean)this.processEscapeCommand(this.options, (int)data));
                break;
            }
            case 3: {
                this.buffer[this.pos++] = (byte)data;
                if (34 == data) break;
                String value = new String((byte[])this.buffer, (int)this.startOfValue, (int)(this.pos - 1 - this.startOfValue), (String)"UTF-8");
                this.options.add((Object)value);
                if (data == 59) {
                    this.state = 2;
                    break;
                }
                this.reset((boolean)this.processEscapeCommand(this.options, (int)data));
                break;
            }
            case 5: {
                this.buffer[this.pos++] = (byte)data;
                if (48 <= data && data <= 57) {
                    this.startOfValue = this.pos - 1;
                    this.state = 6;
                    break;
                }
                this.reset((boolean)false);
                break;
            }
            case 6: {
                this.buffer[this.pos++] = (byte)data;
                if (59 == data) {
                    String strValue = new String((byte[])this.buffer, (int)this.startOfValue, (int)(this.pos - 1 - this.startOfValue), (String)"UTF-8");
                    Integer value = new Integer((String)strValue);
                    this.options.add((Object)value);
                    this.startOfValue = this.pos;
                    this.state = 7;
                    break;
                }
                if (48 <= data && data <= 57) break;
                this.reset((boolean)false);
                break;
            }
            case 7: {
                this.buffer[this.pos++] = (byte)data;
                if (7 == data) {
                    String value = new String((byte[])this.buffer, (int)this.startOfValue, (int)(this.pos - 1 - this.startOfValue), (String)"UTF-8");
                    this.options.add((Object)value);
                    this.reset((boolean)this.processOperatingSystemCommand(this.options));
                    break;
                }
                if (27 != data) break;
                this.state = 8;
                break;
            }
            case 8: {
                this.buffer[this.pos++] = (byte)data;
                if (92 == data) {
                    String value = new String((byte[])this.buffer, (int)this.startOfValue, (int)(this.pos - 2 - this.startOfValue), (String)"UTF-8");
                    this.options.add((Object)value);
                    this.reset((boolean)this.processOperatingSystemCommand(this.options));
                    break;
                }
                this.state = 7;
            }
        }
        if (this.pos < this.buffer.length) return;
        this.reset((boolean)false);
    }

    private void reset(boolean skipBuffer) throws IOException {
        if (!skipBuffer) {
            this.out.write((byte[])this.buffer, (int)0, (int)this.pos);
        }
        this.pos = 0;
        this.startOfValue = 0;
        this.options.clear();
        this.state = 0;
    }

    private boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
        try {
            switch (command) {
                case 65: {
                    this.processCursorUp((int)this.optionInt(options, (int)0, (int)1));
                    return true;
                }
                case 66: {
                    this.processCursorDown((int)this.optionInt(options, (int)0, (int)1));
                    return true;
                }
                case 67: {
                    this.processCursorRight((int)this.optionInt(options, (int)0, (int)1));
                    return true;
                }
                case 68: {
                    this.processCursorLeft((int)this.optionInt(options, (int)0, (int)1));
                    return true;
                }
                case 69: {
                    this.processCursorDownLine((int)this.optionInt(options, (int)0, (int)1));
                    return true;
                }
                case 70: {
                    this.processCursorUpLine((int)this.optionInt(options, (int)0, (int)1));
                    return true;
                }
                case 71: {
                    this.processCursorToColumn((int)this.optionInt(options, (int)0));
                    return true;
                }
                case 72: 
                case 102: {
                    this.processCursorTo((int)this.optionInt(options, (int)0, (int)1), (int)this.optionInt(options, (int)1, (int)1));
                    return true;
                }
                case 74: {
                    this.processEraseScreen((int)this.optionInt(options, (int)0, (int)0));
                    return true;
                }
                case 75: {
                    this.processEraseLine((int)this.optionInt(options, (int)0, (int)0));
                    return true;
                }
                case 83: {
                    this.processScrollUp((int)this.optionInt(options, (int)0, (int)1));
                    return true;
                }
                case 84: {
                    this.processScrollDown((int)this.optionInt(options, (int)0, (int)1));
                    return true;
                }
                case 109: {
                    for (Object next : options) {
                        if (next == null || next.getClass() == Integer.class) continue;
                        throw new IllegalArgumentException();
                    }
                    int count = 0;
                    Iterator<Object> i$ = options.iterator();
                    block25 : do {
                        if (!i$.hasNext()) {
                            if (count != 0) return true;
                            this.processAttributeRest();
                            return true;
                        }
                        Object next = i$.next();
                        if (next == null) continue;
                        ++count;
                        int value = ((Integer)next).intValue();
                        if (30 <= value && value <= 37) {
                            this.processSetForegroundColor((int)(value - 30));
                            continue;
                        }
                        if (40 <= value && value <= 47) {
                            this.processSetBackgroundColor((int)(value - 40));
                            continue;
                        }
                        switch (value) {
                            case 39: {
                                this.processDefaultTextColor();
                                continue block25;
                            }
                            case 49: {
                                this.processDefaultBackgroundColor();
                                continue block25;
                            }
                            case 0: {
                                this.processAttributeRest();
                                continue block25;
                            }
                        }
                        this.processSetAttribute((int)value);
                    } while (true);
                }
                case 115: {
                    this.processSaveCursorPosition();
                    return true;
                }
                case 117: {
                    this.processRestoreCursorPosition();
                    return true;
                }
            }
            if (97 <= command && 122 <= command) {
                this.processUnknownExtension(options, (int)command);
                return true;
            }
            if (65 > command) return false;
            if (90 > command) return false;
            this.processUnknownExtension(options, (int)command);
            return true;
        }
        catch (IllegalArgumentException ignore) {
            return false;
        }
    }

    private boolean processOperatingSystemCommand(ArrayList<Object> options) throws IOException {
        int command = this.optionInt(options, (int)0);
        String label = (String)options.get((int)1);
        try {
            switch (command) {
                case 0: {
                    this.processChangeIconNameAndWindowTitle((String)label);
                    return true;
                }
                case 1: {
                    this.processChangeIconName((String)label);
                    return true;
                }
                case 2: {
                    this.processChangeWindowTitle((String)label);
                    return true;
                }
            }
            this.processUnknownOperatingSystemCommand((int)command, (String)label);
            return true;
        }
        catch (IllegalArgumentException ignore) {
            return false;
        }
    }

    protected void processRestoreCursorPosition() throws IOException {
    }

    protected void processSaveCursorPosition() throws IOException {
    }

    protected void processScrollDown(int optionInt) throws IOException {
    }

    protected void processScrollUp(int optionInt) throws IOException {
    }

    protected void processEraseScreen(int eraseOption) throws IOException {
    }

    protected void processEraseLine(int eraseOption) throws IOException {
    }

    protected void processSetAttribute(int attribute) throws IOException {
    }

    protected void processSetForegroundColor(int color) throws IOException {
    }

    protected void processSetBackgroundColor(int color) throws IOException {
    }

    protected void processDefaultTextColor() throws IOException {
    }

    protected void processDefaultBackgroundColor() throws IOException {
    }

    protected void processAttributeRest() throws IOException {
    }

    protected void processCursorTo(int row, int col) throws IOException {
    }

    protected void processCursorToColumn(int x) throws IOException {
    }

    protected void processCursorUpLine(int count) throws IOException {
    }

    protected void processCursorDownLine(int count) throws IOException {
        int i = 0;
        while (i < count) {
            this.out.write((int)10);
            ++i;
        }
    }

    protected void processCursorLeft(int count) throws IOException {
    }

    protected void processCursorRight(int count) throws IOException {
        int i = 0;
        while (i < count) {
            this.out.write((int)32);
            ++i;
        }
    }

    protected void processCursorDown(int count) throws IOException {
    }

    protected void processCursorUp(int count) throws IOException {
    }

    protected void processUnknownExtension(ArrayList<Object> options, int command) {
    }

    protected void processChangeIconNameAndWindowTitle(String label) {
        this.processChangeIconName((String)label);
        this.processChangeWindowTitle((String)label);
    }

    protected void processChangeIconName(String label) {
    }

    protected void processChangeWindowTitle(String label) {
    }

    protected void processUnknownOperatingSystemCommand(int command, String param) {
    }

    private int optionInt(ArrayList<Object> options, int index) {
        if (options.size() <= index) {
            throw new IllegalArgumentException();
        }
        Object value = options.get((int)index);
        if (value == null) {
            throw new IllegalArgumentException();
        }
        if (value.getClass().equals(Integer.class)) return ((Integer)value).intValue();
        throw new IllegalArgumentException();
    }

    private int optionInt(ArrayList<Object> options, int index, int defaultValue) {
        if (options.size() <= index) return defaultValue;
        Object value = options.get((int)index);
        if (value != null) return ((Integer)value).intValue();
        return defaultValue;
    }

    @Override
    public void close() throws IOException {
        this.write((byte[])REST_CODE);
        this.flush();
        super.close();
    }

    private static byte[] resetCode() {
        try {
            return new Ansi().reset().toString().getBytes((String)"UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

