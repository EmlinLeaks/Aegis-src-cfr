/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiRenderer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Ansi {
    private static final char FIRST_ESC_CHAR = '\u001b';
    private static final char SECOND_ESC_CHAR = '[';
    public static final String DISABLE = Ansi.class.getName() + ".disable";
    private static Callable<Boolean> detector = new Callable<Boolean>(){

        public Boolean call() throws Exception {
            boolean bl;
            if (!Boolean.getBoolean((String)DISABLE)) {
                bl = true;
                return Boolean.valueOf((boolean)bl);
            }
            bl = false;
            return Boolean.valueOf((boolean)bl);
        }
    };
    private static final InheritableThreadLocal<Boolean> holder = new InheritableThreadLocal<Boolean>(){

        protected Boolean initialValue() {
            return Boolean.valueOf((boolean)Ansi.isDetected());
        }
    };
    private final StringBuilder builder;
    private final ArrayList<Integer> attributeOptions = new ArrayList<E>((int)5);

    public static void setDetector(Callable<Boolean> detector) {
        if (detector == null) {
            throw new IllegalArgumentException();
        }
        Ansi.detector = detector;
    }

    public static boolean isDetected() {
        try {
            return detector.call().booleanValue();
        }
        catch (Exception e) {
            return true;
        }
    }

    public static void setEnabled(boolean flag) {
        holder.set((Boolean)Boolean.valueOf((boolean)flag));
    }

    public static boolean isEnabled() {
        return ((Boolean)holder.get()).booleanValue();
    }

    public static Ansi ansi() {
        if (!Ansi.isEnabled()) return new NoAnsi(null);
        return new Ansi();
    }

    public Ansi() {
        this((StringBuilder)new StringBuilder());
    }

    public Ansi(Ansi parent) {
        this((StringBuilder)new StringBuilder((CharSequence)parent.builder));
        this.attributeOptions.addAll(parent.attributeOptions);
    }

    public Ansi(int size) {
        this((StringBuilder)new StringBuilder((int)size));
    }

    public Ansi(StringBuilder builder) {
        this.builder = builder;
    }

    public static Ansi ansi(StringBuilder builder) {
        return new Ansi((StringBuilder)builder);
    }

    public static Ansi ansi(int size) {
        return new Ansi((int)size);
    }

    public Ansi fg(Color color) {
        this.attributeOptions.add((Integer)Integer.valueOf((int)color.fg()));
        return this;
    }

    public Ansi bg(Color color) {
        this.attributeOptions.add((Integer)Integer.valueOf((int)color.bg()));
        return this;
    }

    public Ansi fgBright(Color color) {
        this.attributeOptions.add((Integer)Integer.valueOf((int)color.fgBright()));
        return this;
    }

    public Ansi bgBright(Color color) {
        this.attributeOptions.add((Integer)Integer.valueOf((int)color.bgBright()));
        return this;
    }

    public Ansi a(Attribute attribute) {
        this.attributeOptions.add((Integer)Integer.valueOf((int)attribute.value()));
        return this;
    }

    public Ansi cursor(int x, int y) {
        return this.appendEscapeSequence((char)'H', (Object[])new Object[]{Integer.valueOf((int)x), Integer.valueOf((int)y)});
    }

    public Ansi cursorUp(int y) {
        return this.appendEscapeSequence((char)'A', (int)y);
    }

    public Ansi cursorDown(int y) {
        return this.appendEscapeSequence((char)'B', (int)y);
    }

    public Ansi cursorRight(int x) {
        return this.appendEscapeSequence((char)'C', (int)x);
    }

    public Ansi cursorLeft(int x) {
        return this.appendEscapeSequence((char)'D', (int)x);
    }

    public Ansi eraseScreen() {
        return this.appendEscapeSequence((char)'J', (int)Erase.ALL.value());
    }

    public Ansi eraseScreen(Erase kind) {
        return this.appendEscapeSequence((char)'J', (int)kind.value());
    }

    public Ansi eraseLine() {
        return this.appendEscapeSequence((char)'K');
    }

    public Ansi eraseLine(Erase kind) {
        return this.appendEscapeSequence((char)'K', (int)kind.value());
    }

    public Ansi scrollUp(int rows) {
        return this.appendEscapeSequence((char)'S', (int)rows);
    }

    public Ansi scrollDown(int rows) {
        return this.appendEscapeSequence((char)'T', (int)rows);
    }

    public Ansi saveCursorPosition() {
        return this.appendEscapeSequence((char)'s');
    }

    public Ansi restorCursorPosition() {
        return this.appendEscapeSequence((char)'u');
    }

    public Ansi reset() {
        return this.a((Attribute)Attribute.RESET);
    }

    public Ansi bold() {
        return this.a((Attribute)Attribute.INTENSITY_BOLD);
    }

    public Ansi boldOff() {
        return this.a((Attribute)Attribute.INTENSITY_BOLD_OFF);
    }

    public Ansi a(String value) {
        this.flushAtttributes();
        this.builder.append((String)value);
        return this;
    }

    public Ansi a(boolean value) {
        this.flushAtttributes();
        this.builder.append((boolean)value);
        return this;
    }

    public Ansi a(char value) {
        this.flushAtttributes();
        this.builder.append((char)value);
        return this;
    }

    public Ansi a(char[] value, int offset, int len) {
        this.flushAtttributes();
        this.builder.append((char[])value, (int)offset, (int)len);
        return this;
    }

    public Ansi a(char[] value) {
        this.flushAtttributes();
        this.builder.append((char[])value);
        return this;
    }

    public Ansi a(CharSequence value, int start, int end) {
        this.flushAtttributes();
        this.builder.append((CharSequence)value, (int)start, (int)end);
        return this;
    }

    public Ansi a(CharSequence value) {
        this.flushAtttributes();
        this.builder.append((CharSequence)value);
        return this;
    }

    public Ansi a(double value) {
        this.flushAtttributes();
        this.builder.append((double)value);
        return this;
    }

    public Ansi a(float value) {
        this.flushAtttributes();
        this.builder.append((float)value);
        return this;
    }

    public Ansi a(int value) {
        this.flushAtttributes();
        this.builder.append((int)value);
        return this;
    }

    public Ansi a(long value) {
        this.flushAtttributes();
        this.builder.append((long)value);
        return this;
    }

    public Ansi a(Object value) {
        this.flushAtttributes();
        this.builder.append((Object)value);
        return this;
    }

    public Ansi a(StringBuffer value) {
        this.flushAtttributes();
        this.builder.append((StringBuffer)value);
        return this;
    }

    public Ansi newline() {
        this.flushAtttributes();
        this.builder.append((String)System.getProperty((String)"line.separator"));
        return this;
    }

    public Ansi format(String pattern, Object ... args) {
        this.flushAtttributes();
        this.builder.append((String)String.format((String)pattern, (Object[])args));
        return this;
    }

    public Ansi render(String text) {
        this.a((String)AnsiRenderer.render((String)text));
        return this;
    }

    public Ansi render(String text, Object ... args) {
        this.a((String)String.format((String)AnsiRenderer.render((String)text), (Object[])args));
        return this;
    }

    public String toString() {
        this.flushAtttributes();
        return this.builder.toString();
    }

    private Ansi appendEscapeSequence(char command) {
        this.flushAtttributes();
        this.builder.append((char)'\u001b');
        this.builder.append((char)'[');
        this.builder.append((char)command);
        return this;
    }

    private Ansi appendEscapeSequence(char command, int option) {
        this.flushAtttributes();
        this.builder.append((char)'\u001b');
        this.builder.append((char)'[');
        this.builder.append((int)option);
        this.builder.append((char)command);
        return this;
    }

    private Ansi appendEscapeSequence(char command, Object ... options) {
        this.flushAtttributes();
        return this._appendEscapeSequence((char)command, (Object[])options);
    }

    private void flushAtttributes() {
        if (this.attributeOptions.isEmpty()) {
            return;
        }
        if (this.attributeOptions.size() == 1 && this.attributeOptions.get((int)0).intValue() == 0) {
            this.builder.append((char)'\u001b');
            this.builder.append((char)'[');
            this.builder.append((char)'m');
        } else {
            this._appendEscapeSequence((char)'m', (Object[])this.attributeOptions.toArray());
        }
        this.attributeOptions.clear();
    }

    private Ansi _appendEscapeSequence(char command, Object ... options) {
        this.builder.append((char)'\u001b');
        this.builder.append((char)'[');
        int size = options.length;
        int i = 0;
        do {
            if (i >= size) {
                this.builder.append((char)command);
                return this;
            }
            if (i != 0) {
                this.builder.append((char)';');
            }
            if (options[i] != null) {
                this.builder.append((Object)options[i]);
            }
            ++i;
        } while (true);
    }
}

