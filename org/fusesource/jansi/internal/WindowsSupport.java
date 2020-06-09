/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi.internal;

import java.io.IOException;
import org.fusesource.jansi.internal.Kernel32;

public class WindowsSupport {
    public static String getLastErrorMessage() {
        int errorCode = Kernel32.GetLastError();
        int bufferSize = 160;
        byte[] data = new byte[bufferSize];
        Kernel32.FormatMessageW((int)Kernel32.FORMAT_MESSAGE_FROM_SYSTEM, (long)0L, (int)errorCode, (int)0, (byte[])data, (int)bufferSize, null);
        return new String((byte[])data);
    }

    public static int readByte() {
        return Kernel32._getch();
    }

    public static int getConsoleMode() {
        long hConsole = Kernel32.GetStdHandle((int)Kernel32.STD_INPUT_HANDLE);
        if (hConsole == (long)Kernel32.INVALID_HANDLE_VALUE) {
            return -1;
        }
        int[] mode = new int[1];
        if (Kernel32.GetConsoleMode((long)hConsole, (int[])mode) != 0) return mode[0];
        return -1;
    }

    public static void setConsoleMode(int mode) {
        long hConsole = Kernel32.GetStdHandle((int)Kernel32.STD_INPUT_HANDLE);
        if (hConsole == (long)Kernel32.INVALID_HANDLE_VALUE) {
            return;
        }
        Kernel32.SetConsoleMode((long)hConsole, (int)mode);
    }

    public static int getWindowsTerminalWidth() {
        long outputHandle = Kernel32.GetStdHandle((int)Kernel32.STD_OUTPUT_HANDLE);
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
        Kernel32.GetConsoleScreenBufferInfo((long)outputHandle, (Kernel32.CONSOLE_SCREEN_BUFFER_INFO)info);
        return info.windowWidth();
    }

    public static int getWindowsTerminalHeight() {
        long outputHandle = Kernel32.GetStdHandle((int)Kernel32.STD_OUTPUT_HANDLE);
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
        Kernel32.GetConsoleScreenBufferInfo((long)outputHandle, (Kernel32.CONSOLE_SCREEN_BUFFER_INFO)info);
        return info.windowHeight();
    }

    public static int writeConsole(String msg) {
        int[] written;
        long hConsole = Kernel32.GetStdHandle((int)Kernel32.STD_OUTPUT_HANDLE);
        if (hConsole == (long)Kernel32.INVALID_HANDLE_VALUE) {
            return 0;
        }
        char[] chars = msg.toCharArray();
        if (Kernel32.WriteConsoleW((long)hConsole, (char[])chars, (int)chars.length, (int[])(written = new int[1]), (long)0L) == 0) return 0;
        return written[0];
    }

    public static Kernel32.INPUT_RECORD[] readConsoleInput(int count) throws IOException {
        long hConsole = Kernel32.GetStdHandle((int)Kernel32.STD_INPUT_HANDLE);
        if (hConsole != (long)Kernel32.INVALID_HANDLE_VALUE) return Kernel32.readConsoleKeyInput((long)hConsole, (int)count, (boolean)false);
        return null;
    }

    public static Kernel32.INPUT_RECORD[] peekConsoleInput(int count) throws IOException {
        long hConsole = Kernel32.GetStdHandle((int)Kernel32.STD_INPUT_HANDLE);
        if (hConsole != (long)Kernel32.INVALID_HANDLE_VALUE) return Kernel32.readConsoleKeyInput((long)hConsole, (int)count, (boolean)true);
        return null;
    }

    public static void flushConsoleInputBuffer() {
        long hConsole = Kernel32.GetStdHandle((int)Kernel32.STD_INPUT_HANDLE);
        if (hConsole == (long)Kernel32.INVALID_HANDLE_VALUE) {
            return;
        }
        Kernel32.FlushConsoleInputBuffer((long)hConsole);
    }
}

