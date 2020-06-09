/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi;

import java.io.IOException;
import java.io.OutputStream;
import org.fusesource.jansi.AnsiOutputStream;
import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.WindowsSupport;

public final class WindowsAnsiOutputStream
extends AnsiOutputStream {
    private static final long console = Kernel32.GetStdHandle((int)Kernel32.STD_OUTPUT_HANDLE);
    private static final short FOREGROUND_BLACK = 0;
    private static final short FOREGROUND_YELLOW = (short)(Kernel32.FOREGROUND_RED | Kernel32.FOREGROUND_GREEN);
    private static final short FOREGROUND_MAGENTA = (short)(Kernel32.FOREGROUND_BLUE | Kernel32.FOREGROUND_RED);
    private static final short FOREGROUND_CYAN = (short)(Kernel32.FOREGROUND_BLUE | Kernel32.FOREGROUND_GREEN);
    private static final short FOREGROUND_WHITE = (short)(Kernel32.FOREGROUND_RED | Kernel32.FOREGROUND_GREEN | Kernel32.FOREGROUND_BLUE);
    private static final short BACKGROUND_BLACK = 0;
    private static final short BACKGROUND_YELLOW = (short)(Kernel32.BACKGROUND_RED | Kernel32.BACKGROUND_GREEN);
    private static final short BACKGROUND_MAGENTA = (short)(Kernel32.BACKGROUND_BLUE | Kernel32.BACKGROUND_RED);
    private static final short BACKGROUND_CYAN = (short)(Kernel32.BACKGROUND_BLUE | Kernel32.BACKGROUND_GREEN);
    private static final short BACKGROUND_WHITE = (short)(Kernel32.BACKGROUND_RED | Kernel32.BACKGROUND_GREEN | Kernel32.BACKGROUND_BLUE);
    private static final short[] ANSI_FOREGROUND_COLOR_MAP = new short[]{0, Kernel32.FOREGROUND_RED, Kernel32.FOREGROUND_GREEN, FOREGROUND_YELLOW, Kernel32.FOREGROUND_BLUE, FOREGROUND_MAGENTA, FOREGROUND_CYAN, FOREGROUND_WHITE};
    private static final short[] ANSI_BACKGROUND_COLOR_MAP = new short[]{0, Kernel32.BACKGROUND_RED, Kernel32.BACKGROUND_GREEN, BACKGROUND_YELLOW, Kernel32.BACKGROUND_BLUE, BACKGROUND_MAGENTA, BACKGROUND_CYAN, BACKGROUND_WHITE};
    private final Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
    private final short originalColors;
    private boolean negative;
    private short savedX = (short)-1;
    private short savedY = (short)-1;

    public WindowsAnsiOutputStream(OutputStream os) throws IOException {
        super((OutputStream)os);
        this.getConsoleInfo();
        this.originalColors = this.info.attributes;
    }

    private void getConsoleInfo() throws IOException {
        this.out.flush();
        if (Kernel32.GetConsoleScreenBufferInfo((long)console, (Kernel32.CONSOLE_SCREEN_BUFFER_INFO)this.info) == 0) {
            throw new IOException((String)("Could not get the screen info: " + WindowsSupport.getLastErrorMessage()));
        }
        if (!this.negative) return;
        this.info.attributes = this.invertAttributeColors((short)this.info.attributes);
    }

    private void applyAttribute() throws IOException {
        this.out.flush();
        short attributes = this.info.attributes;
        if (this.negative) {
            attributes = this.invertAttributeColors((short)attributes);
        }
        if (Kernel32.SetConsoleTextAttribute((long)console, (short)attributes) != 0) return;
        throw new IOException((String)WindowsSupport.getLastErrorMessage());
    }

    private short invertAttributeColors(short attibutes) {
        int fg = 15 & attibutes;
        int bg = 240 * attibutes;
        return (short)(attibutes & 65280 | (fg <<= 8) | (bg >>= 8));
    }

    private void applyCursorPosition() throws IOException {
        if (Kernel32.SetConsoleCursorPosition((long)console, (Kernel32.COORD)this.info.cursorPosition.copy()) != 0) return;
        throw new IOException((String)WindowsSupport.getLastErrorMessage());
    }

    @Override
    protected void processEraseScreen(int eraseOption) throws IOException {
        this.getConsoleInfo();
        int[] written = new int[1];
        switch (eraseOption) {
            case 2: {
                Kernel32.COORD topLeft = new Kernel32.COORD();
                topLeft.x = 0;
                topLeft.y = this.info.window.top;
                int screenLength = this.info.window.height() * this.info.size.x;
                Kernel32.FillConsoleOutputCharacterW((long)console, (char)' ', (int)screenLength, (Kernel32.COORD)topLeft, (int[])written);
                return;
            }
            case 1: {
                Kernel32.COORD topLeft2 = new Kernel32.COORD();
                topLeft2.x = 0;
                topLeft2.y = this.info.window.top;
                int lengthToCursor = (this.info.cursorPosition.y - this.info.window.top) * this.info.size.x + this.info.cursorPosition.x;
                Kernel32.FillConsoleOutputCharacterW((long)console, (char)' ', (int)lengthToCursor, (Kernel32.COORD)topLeft2, (int[])written);
                return;
            }
            case 0: {
                int lengthToEnd = (this.info.window.bottom - this.info.cursorPosition.y) * this.info.size.x + (this.info.size.x - this.info.cursorPosition.x);
                Kernel32.FillConsoleOutputCharacterW((long)console, (char)' ', (int)lengthToEnd, (Kernel32.COORD)this.info.cursorPosition.copy(), (int[])written);
            }
        }
    }

    @Override
    protected void processEraseLine(int eraseOption) throws IOException {
        this.getConsoleInfo();
        int[] written = new int[1];
        switch (eraseOption) {
            case 2: {
                Kernel32.COORD leftColCurrRow = this.info.cursorPosition.copy();
                leftColCurrRow.x = 0;
                Kernel32.FillConsoleOutputCharacterW((long)console, (char)' ', (int)this.info.size.x, (Kernel32.COORD)leftColCurrRow, (int[])written);
                return;
            }
            case 1: {
                Kernel32.COORD leftColCurrRow2 = this.info.cursorPosition.copy();
                leftColCurrRow2.x = 0;
                Kernel32.FillConsoleOutputCharacterW((long)console, (char)' ', (int)this.info.cursorPosition.x, (Kernel32.COORD)leftColCurrRow2, (int[])written);
                return;
            }
            case 0: {
                int lengthToLastCol = this.info.size.x - this.info.cursorPosition.x;
                Kernel32.FillConsoleOutputCharacterW((long)console, (char)' ', (int)lengthToLastCol, (Kernel32.COORD)this.info.cursorPosition.copy(), (int[])written);
            }
        }
    }

    @Override
    protected void processCursorLeft(int count) throws IOException {
        this.getConsoleInfo();
        this.info.cursorPosition.x = (short)Math.max((int)0, (int)(this.info.cursorPosition.x - count));
        this.applyCursorPosition();
    }

    @Override
    protected void processCursorRight(int count) throws IOException {
        this.getConsoleInfo();
        this.info.cursorPosition.x = (short)Math.min((int)this.info.window.width(), (int)(this.info.cursorPosition.x + count));
        this.applyCursorPosition();
    }

    @Override
    protected void processCursorDown(int count) throws IOException {
        this.getConsoleInfo();
        this.info.cursorPosition.y = (short)Math.min((int)this.info.size.y, (int)(this.info.cursorPosition.y + count));
        this.applyCursorPosition();
    }

    @Override
    protected void processCursorUp(int count) throws IOException {
        this.getConsoleInfo();
        this.info.cursorPosition.y = (short)Math.max((int)this.info.window.top, (int)(this.info.cursorPosition.y - count));
        this.applyCursorPosition();
    }

    @Override
    protected void processCursorTo(int row, int col) throws IOException {
        this.getConsoleInfo();
        this.info.cursorPosition.y = (short)Math.max((int)this.info.window.top, (int)Math.min((int)this.info.size.y, (int)(this.info.window.top + row - 1)));
        this.info.cursorPosition.x = (short)Math.max((int)0, (int)Math.min((int)this.info.window.width(), (int)(col - 1)));
        this.applyCursorPosition();
    }

    @Override
    protected void processCursorToColumn(int x) throws IOException {
        this.getConsoleInfo();
        this.info.cursorPosition.x = (short)Math.max((int)0, (int)Math.min((int)this.info.window.width(), (int)(x - 1)));
        this.applyCursorPosition();
    }

    @Override
    protected void processSetForegroundColor(int color) throws IOException {
        this.info.attributes = (short)(this.info.attributes & -8 | ANSI_FOREGROUND_COLOR_MAP[color]);
        this.applyAttribute();
    }

    @Override
    protected void processSetBackgroundColor(int color) throws IOException {
        this.info.attributes = (short)(this.info.attributes & -113 | ANSI_BACKGROUND_COLOR_MAP[color]);
        this.applyAttribute();
    }

    @Override
    protected void processAttributeRest() throws IOException {
        this.info.attributes = (short)(this.info.attributes & -256 | this.originalColors);
        this.negative = false;
        this.applyAttribute();
    }

    @Override
    protected void processSetAttribute(int attribute) throws IOException {
        switch (attribute) {
            case 1: {
                this.info.attributes = (short)(this.info.attributes | Kernel32.FOREGROUND_INTENSITY);
                this.applyAttribute();
                return;
            }
            case 22: {
                this.info.attributes = (short)(this.info.attributes & ~Kernel32.FOREGROUND_INTENSITY);
                this.applyAttribute();
                return;
            }
            case 4: {
                this.info.attributes = (short)(this.info.attributes | Kernel32.BACKGROUND_INTENSITY);
                this.applyAttribute();
                return;
            }
            case 24: {
                this.info.attributes = (short)(this.info.attributes & ~Kernel32.BACKGROUND_INTENSITY);
                this.applyAttribute();
                return;
            }
            case 7: {
                this.negative = true;
                this.applyAttribute();
                return;
            }
            case 27: {
                this.negative = false;
                this.applyAttribute();
            }
        }
    }

    @Override
    protected void processSaveCursorPosition() throws IOException {
        this.getConsoleInfo();
        this.savedX = this.info.cursorPosition.x;
        this.savedY = this.info.cursorPosition.y;
    }

    @Override
    protected void processRestoreCursorPosition() throws IOException {
        if (this.savedX == -1) return;
        if (this.savedY == -1) return;
        this.out.flush();
        this.info.cursorPosition.x = this.savedX;
        this.info.cursorPosition.y = this.savedY;
        this.applyCursorPosition();
    }

    @Override
    protected void processChangeWindowTitle(String label) {
        Kernel32.SetConsoleTitle((String)label);
    }
}

