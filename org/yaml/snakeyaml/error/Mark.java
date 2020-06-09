/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.error;

import java.io.Serializable;
import org.yaml.snakeyaml.scanner.Constant;

public final class Mark
implements Serializable {
    private String name;
    private int index;
    private int line;
    private int column;
    private int[] buffer;
    private int pointer;

    private static int[] toCodePoints(char[] str) {
        int[] codePoints = new int[Character.codePointCount((char[])str, (int)0, (int)str.length)];
        int i = 0;
        int c = 0;
        while (i < str.length) {
            int cp;
            codePoints[c] = cp = Character.codePointAt((char[])str, (int)i);
            i += Character.charCount((int)cp);
            ++c;
        }
        return codePoints;
    }

    public Mark(String name, int index, int line, int column, char[] str, int pointer) {
        this((String)name, (int)index, (int)line, (int)column, (int[])Mark.toCodePoints((char[])str), (int)pointer);
    }

    @Deprecated
    public Mark(String name, int index, int line, int column, String buffer, int pointer) {
        this((String)name, (int)index, (int)line, (int)column, (char[])buffer.toCharArray(), (int)pointer);
    }

    public Mark(String name, int index, int line, int column, int[] buffer, int pointer) {
        this.name = name;
        this.index = index;
        this.line = line;
        this.column = column;
        this.buffer = buffer;
        this.pointer = pointer;
    }

    private boolean isLineBreak(int c) {
        return Constant.NULL_OR_LINEBR.has((int)c);
    }

    public String get_snippet(int indent, int max_length) {
        int i;
        float half = (float)(max_length / 2 - 1);
        int start = this.pointer;
        String head = "";
        while (start > 0 && !this.isLineBreak((int)this.buffer[start - 1])) {
            if (!((float)(this.pointer - --start) > half)) continue;
            head = " ... ";
            start += 5;
            break;
        }
        String tail = "";
        int end = this.pointer;
        while (end < this.buffer.length && !this.isLineBreak((int)this.buffer[end])) {
            if (!((float)(++end - this.pointer) > half)) continue;
            tail = " ... ";
            end -= 5;
            break;
        }
        StringBuilder result = new StringBuilder();
        for (i = 0; i < indent; ++i) {
            result.append((String)" ");
        }
        result.append((String)head);
        for (i = start; i < end; ++i) {
            result.appendCodePoint((int)this.buffer[i]);
        }
        result.append((String)tail);
        result.append((String)"\n");
        i = 0;
        do {
            if (i >= indent + this.pointer - start + head.length()) {
                result.append((String)"^");
                return result.toString();
            }
            result.append((String)" ");
            ++i;
        } while (true);
    }

    public String get_snippet() {
        return this.get_snippet((int)4, (int)75);
    }

    public String toString() {
        String snippet = this.get_snippet();
        StringBuilder builder = new StringBuilder((String)" in ");
        builder.append((String)this.name);
        builder.append((String)", line ");
        builder.append((int)(this.line + 1));
        builder.append((String)", column ");
        builder.append((int)(this.column + 1));
        builder.append((String)":\n");
        builder.append((String)snippet);
        return builder.toString();
    }

    public String getName() {
        return this.name;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public int getIndex() {
        return this.index;
    }

    public int[] getBuffer() {
        return this.buffer;
    }

    public int getPointer() {
        return this.pointer;
    }
}

