/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.scanner.Constant;

public class StreamReader {
    private String name = "'reader'";
    private final Reader stream;
    private int[] dataWindow = new int[0];
    private int dataLength = 0;
    private int pointer = 0;
    private boolean eof;
    private int index = 0;
    private int line = 0;
    private int column = 0;
    private char[] buffer;
    private static final int BUFFER_SIZE = 1025;

    public StreamReader(String stream) {
        this((Reader)new StringReader((String)stream));
        this.name = "'string'";
    }

    public StreamReader(Reader reader) {
        this.stream = reader;
        this.eof = false;
        this.buffer = new char[1025];
    }

    public static boolean isPrintable(String data) {
        int length = data.length();
        int offset = 0;
        while (offset < length) {
            int codePoint = data.codePointAt((int)offset);
            if (!StreamReader.isPrintable((int)codePoint)) {
                return false;
            }
            offset += Character.charCount((int)codePoint);
        }
        return true;
    }

    public static boolean isPrintable(int c) {
        if (c >= 32) {
            if (c <= 126) return true;
        }
        if (c == 9) return true;
        if (c == 10) return true;
        if (c == 13) return true;
        if (c == 133) return true;
        if (c >= 160) {
            if (c <= 55295) return true;
        }
        if (c >= 57344) {
            if (c <= 65533) return true;
        }
        if (c < 65536) return false;
        if (c > 1114111) return false;
        return true;
    }

    public Mark getMark() {
        return new Mark((String)this.name, (int)this.index, (int)this.line, (int)this.column, (int[])this.dataWindow, (int)this.pointer);
    }

    public void forward() {
        this.forward((int)1);
    }

    public void forward(int length) {
        int i = 0;
        while (i < length) {
            if (!this.ensureEnoughData()) return;
            int c = this.dataWindow[this.pointer++];
            ++this.index;
            if (Constant.LINEBR.has((int)c) || c == 13 && this.ensureEnoughData() && this.dataWindow[this.pointer] != 10) {
                ++this.line;
                this.column = 0;
            } else if (c != 65279) {
                ++this.column;
            }
            ++i;
        }
    }

    public int peek() {
        if (!this.ensureEnoughData()) return 0;
        int n = this.dataWindow[this.pointer];
        return n;
    }

    public int peek(int index) {
        if (!this.ensureEnoughData((int)index)) return 0;
        int n = this.dataWindow[this.pointer + index];
        return n;
    }

    public String prefix(int length) {
        if (length == 0) {
            return "";
        }
        if (!this.ensureEnoughData((int)length)) return new String((int[])this.dataWindow, (int)this.pointer, (int)Math.min((int)length, (int)(this.dataLength - this.pointer)));
        return new String((int[])this.dataWindow, (int)this.pointer, (int)length);
    }

    public String prefixForward(int length) {
        String prefix = this.prefix((int)length);
        this.pointer += length;
        this.index += length;
        this.column += length;
        return prefix;
    }

    private boolean ensureEnoughData() {
        return this.ensureEnoughData((int)0);
    }

    private boolean ensureEnoughData(int size) {
        if (!this.eof && this.pointer + size >= this.dataLength) {
            this.update();
        }
        if (this.pointer + size >= this.dataLength) return false;
        return true;
    }

    private void update() {
        try {
            int read = this.stream.read((char[])this.buffer, (int)0, (int)1024);
            if (read <= 0) {
                this.eof = true;
                return;
            }
            int cpIndex = this.dataLength - this.pointer;
            this.dataWindow = Arrays.copyOfRange((int[])this.dataWindow, (int)this.pointer, (int)(this.dataLength + read));
            if (Character.isHighSurrogate((char)this.buffer[read - 1])) {
                if (this.stream.read((char[])this.buffer, (int)read, (int)1) == -1) {
                    this.eof = true;
                } else {
                    ++read;
                }
            }
            int nonPrintable = 32;
            int i = 0;
            do {
                int codePoint;
                if (i >= read) {
                    this.dataLength = cpIndex;
                    this.pointer = 0;
                    if (nonPrintable == 32) return;
                    throw new ReaderException((String)this.name, (int)(cpIndex - 1), (int)nonPrintable, (String)"special characters are not allowed");
                }
                this.dataWindow[cpIndex] = codePoint = Character.codePointAt((char[])this.buffer, (int)i);
                if (StreamReader.isPrintable((int)codePoint)) {
                    i += Character.charCount((int)codePoint);
                } else {
                    nonPrintable = codePoint;
                    i = read;
                }
                ++cpIndex;
            } while (true);
        }
        catch (IOException ioe) {
            throw new YAMLException((Throwable)ioe);
        }
    }

    public int getColumn() {
        return this.column;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLine() {
        return this.line;
    }
}

