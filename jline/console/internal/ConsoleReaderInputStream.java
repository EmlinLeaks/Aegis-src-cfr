/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import jline.console.ConsoleReader;
import jline.console.internal.ConsoleReaderInputStream;

class ConsoleReaderInputStream
extends SequenceInputStream {
    private static InputStream systemIn = System.in;

    public static void setIn() throws IOException {
        ConsoleReaderInputStream.setIn((ConsoleReader)new ConsoleReader());
    }

    public static void setIn(ConsoleReader reader) {
        System.setIn((InputStream)new ConsoleReaderInputStream((ConsoleReader)reader));
    }

    public static void restoreIn() {
        System.setIn((InputStream)systemIn);
    }

    public ConsoleReaderInputStream(ConsoleReader reader) {
        super((Enumeration<? extends InputStream>)new ConsoleEnumeration((ConsoleReader)reader));
    }
}

