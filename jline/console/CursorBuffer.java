/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console;

import jline.internal.Preconditions;

public class CursorBuffer {
    private boolean overTyping = false;
    public int cursor = 0;
    public final StringBuilder buffer = new StringBuilder();

    public CursorBuffer copy() {
        CursorBuffer that = new CursorBuffer();
        that.overTyping = this.overTyping;
        that.cursor = this.cursor;
        that.buffer.append((String)this.toString());
        return that;
    }

    public boolean isOverTyping() {
        return this.overTyping;
    }

    public void setOverTyping(boolean b) {
        this.overTyping = b;
    }

    public int length() {
        return this.buffer.length();
    }

    public char nextChar() {
        if (this.cursor != this.buffer.length()) return this.buffer.charAt((int)this.cursor);
        return '\u0000';
    }

    public char current() {
        if (this.cursor > 0) return this.buffer.charAt((int)(this.cursor - 1));
        return '\u0000';
    }

    public void write(char c) {
        this.buffer.insert((int)this.cursor++, (char)c);
        if (!this.isOverTyping()) return;
        if (this.cursor >= this.buffer.length()) return;
        this.buffer.deleteCharAt((int)this.cursor);
    }

    public void write(CharSequence str) {
        Preconditions.checkNotNull(str);
        if (this.buffer.length() == 0) {
            this.buffer.append((CharSequence)str);
        } else {
            this.buffer.insert((int)this.cursor, (CharSequence)str);
        }
        this.cursor += str.length();
        if (!this.isOverTyping()) return;
        if (this.cursor >= this.buffer.length()) return;
        this.buffer.delete((int)this.cursor, (int)(this.cursor + str.length()));
    }

    public boolean clear() {
        if (this.buffer.length() == 0) {
            return false;
        }
        this.buffer.delete((int)0, (int)this.buffer.length());
        this.cursor = 0;
        return true;
    }

    public String upToCursor() {
        if (this.cursor > 0) return this.buffer.substring((int)0, (int)this.cursor);
        return "";
    }

    public String toString() {
        return this.buffer.toString();
    }
}

