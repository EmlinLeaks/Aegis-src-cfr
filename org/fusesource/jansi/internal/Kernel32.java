/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.fusesource.hawtjni.runtime.ArgFlag
 *  org.fusesource.hawtjni.runtime.FieldFlag
 *  org.fusesource.hawtjni.runtime.JniArg
 *  org.fusesource.hawtjni.runtime.JniClass
 *  org.fusesource.hawtjni.runtime.JniField
 *  org.fusesource.hawtjni.runtime.JniMethod
 *  org.fusesource.hawtjni.runtime.MethodFlag
 */
package org.fusesource.jansi.internal;

import java.io.IOException;
import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.FieldFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;
import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.PointerMath;
import org.fusesource.jansi.internal.Kernel32;

@JniClass(conditional="defined(_WIN32) || defined(_WIN64)")
public class Kernel32 {
    private static final Library LIBRARY = new Library((String)"jansi", Kernel32.class);
    @JniField(flags={FieldFlag.CONSTANT})
    public static short FOREGROUND_BLUE;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short FOREGROUND_GREEN;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short FOREGROUND_RED;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short FOREGROUND_INTENSITY;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short BACKGROUND_BLUE;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short BACKGROUND_GREEN;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short BACKGROUND_RED;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short BACKGROUND_INTENSITY;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short COMMON_LVB_LEADING_BYTE;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short COMMON_LVB_TRAILING_BYTE;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short COMMON_LVB_GRID_HORIZONTAL;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short COMMON_LVB_GRID_LVERTICAL;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short COMMON_LVB_GRID_RVERTICAL;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short COMMON_LVB_REVERSE_VIDEO;
    @JniField(flags={FieldFlag.CONSTANT})
    public static short COMMON_LVB_UNDERSCORE;
    @JniField(flags={FieldFlag.CONSTANT})
    public static int FORMAT_MESSAGE_FROM_SYSTEM;
    @JniField(flags={FieldFlag.CONSTANT})
    public static int STD_INPUT_HANDLE;
    @JniField(flags={FieldFlag.CONSTANT})
    public static int STD_OUTPUT_HANDLE;
    @JniField(flags={FieldFlag.CONSTANT})
    public static int STD_ERROR_HANDLE;
    @JniField(flags={FieldFlag.CONSTANT})
    public static int INVALID_HANDLE_VALUE;

    @JniMethod(flags={MethodFlag.CONSTANT_INITIALIZER})
    private static final native void init();

    @JniMethod(cast="void *")
    public static final native long malloc(@JniArg(cast="size_t") long var0);

    public static final native void free(@JniArg(cast="void *") long var0);

    public static final native int SetConsoleTextAttribute(@JniArg(cast="HANDLE") long var0, short var2);

    public static final native int CloseHandle(@JniArg(cast="HANDLE") long var0);

    public static final native int GetLastError();

    public static final native int FormatMessageW(int var0, @JniArg(cast="void *") long var1, int var3, int var4, @JniArg(cast="void *", flags={ArgFlag.NO_IN, ArgFlag.CRITICAL}) byte[] var5, int var6, @JniArg(cast="void *", flags={ArgFlag.NO_IN, ArgFlag.CRITICAL, ArgFlag.SENTINEL}) long[] var7);

    public static final native int GetConsoleScreenBufferInfo(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, CONSOLE_SCREEN_BUFFER_INFO var2);

    @JniMethod(cast="HANDLE", flags={MethodFlag.POINTER_RETURN})
    public static final native long GetStdHandle(int var0);

    public static final native int SetConsoleCursorPosition(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, @JniArg(flags={ArgFlag.BY_VALUE}) COORD var2);

    public static final native int FillConsoleOutputCharacterW(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, char var2, int var3, @JniArg(flags={ArgFlag.BY_VALUE}) COORD var4, int[] var5);

    public static final native int WriteConsoleW(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, char[] var2, int var3, int[] var4, @JniArg(cast="LPVOID", flags={ArgFlag.POINTER_ARG}) long var5);

    public static final native int GetConsoleMode(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, int[] var2);

    public static final native int SetConsoleMode(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, int var2);

    public static final native int _getch();

    public static final native int SetConsoleTitle(@JniArg(flags={ArgFlag.UNICODE}) String var0);

    public static final native int GetConsoleOutputCP();

    public static final native int SetConsoleOutputCP(int var0);

    private static final native int ReadConsoleInputW(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, @JniArg(cast="PINPUT_RECORD", flags={ArgFlag.POINTER_ARG}) long var2, int var4, int[] var5);

    private static final native int PeekConsoleInputW(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, @JniArg(cast="PINPUT_RECORD", flags={ArgFlag.POINTER_ARG}) long var2, int var4, int[] var5);

    public static final native int GetNumberOfConsoleInputEvents(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0, int[] var2);

    public static final native int FlushConsoleInputBuffer(@JniArg(cast="HANDLE", flags={ArgFlag.POINTER_ARG}) long var0);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static INPUT_RECORD[] readConsoleInputHelper(long handle, int count, boolean peek) throws IOException {
        int[] length = new int[1];
        long inputRecordPtr = 0L;
        try {
            int res;
            inputRecordPtr = Kernel32.malloc((long)((long)(INPUT_RECORD.SIZEOF * count)));
            if (inputRecordPtr == 0L) {
                throw new IOException((String)"cannot allocate memory with JNI");
            }
            int n = res = peek ? Kernel32.PeekConsoleInputW((long)handle, (long)inputRecordPtr, (int)count, (int[])length) : Kernel32.ReadConsoleInputW((long)handle, (long)inputRecordPtr, (int)count, (int[])length);
            if (res == 0) {
                throw new IOException((String)"ReadConsoleInputW failed");
            }
            if (length[0] <= 0) {
                INPUT_RECORD[] arriNPUT_RECORD = new INPUT_RECORD[]{};
                return arriNPUT_RECORD;
            }
            INPUT_RECORD[] records = new INPUT_RECORD[length[0]];
            for (int i = 0; i < records.length; ++i) {
                records[i] = new INPUT_RECORD();
                INPUT_RECORD.memmove((INPUT_RECORD)records[i], (long)PointerMath.add((long)inputRecordPtr, (long)((long)(i * INPUT_RECORD.SIZEOF))), (long)((long)INPUT_RECORD.SIZEOF));
            }
            INPUT_RECORD[] i = records;
            return i;
        }
        finally {
            if (inputRecordPtr != 0L) {
                Kernel32.free((long)inputRecordPtr);
            }
        }
    }

    public static INPUT_RECORD[] readConsoleKeyInput(long handle, int count, boolean peek) throws IOException {
        int keyEvtCount;
        INPUT_RECORD[] evts;
        do {
            evts = Kernel32.readConsoleInputHelper((long)handle, (int)count, (boolean)peek);
            keyEvtCount = 0;
            for (INPUT_RECORD evt : evts) {
                if (evt.eventType != INPUT_RECORD.KEY_EVENT) continue;
                ++keyEvtCount;
            }
        } while (keyEvtCount <= 0);
        INPUT_RECORD[] res = new INPUT_RECORD[keyEvtCount];
        int i = 0;
        INPUT_RECORD[] arr$ = evts;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            INPUT_RECORD evt = arr$[i$];
            if (evt.eventType == INPUT_RECORD.KEY_EVENT) {
                res[i++] = evt;
            }
            ++i$;
        }
        return res;
    }

    static /* synthetic */ Library access$000() {
        return LIBRARY;
    }

    static {
        LIBRARY.load();
        Kernel32.init();
    }
}

