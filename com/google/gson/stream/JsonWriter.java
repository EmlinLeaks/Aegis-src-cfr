/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class JsonWriter
implements Closeable,
Flushable {
    private static final String[] REPLACEMENT_CHARS = new String[128];
    private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
    private final Writer out;
    private int[] stack = new int[32];
    private int stackSize = 0;
    private String indent;
    private String separator;
    private boolean lenient;
    private boolean htmlSafe;
    private String deferredName;
    private boolean serializeNulls;

    public JsonWriter(Writer out) {
        this.push((int)6);
        this.separator = ":";
        this.serializeNulls = true;
        if (out == null) {
            throw new NullPointerException((String)"out == null");
        }
        this.out = out;
    }

    public final void setIndent(String indent) {
        if (indent.length() == 0) {
            this.indent = null;
            this.separator = ":";
            return;
        }
        this.indent = indent;
        this.separator = ": ";
    }

    public final void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public final void setHtmlSafe(boolean htmlSafe) {
        this.htmlSafe = htmlSafe;
    }

    public final boolean isHtmlSafe() {
        return this.htmlSafe;
    }

    public final void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    public final boolean getSerializeNulls() {
        return this.serializeNulls;
    }

    public JsonWriter beginArray() throws IOException {
        this.writeDeferredName();
        return this.open((int)1, (String)"[");
    }

    public JsonWriter endArray() throws IOException {
        return this.close((int)1, (int)2, (String)"]");
    }

    public JsonWriter beginObject() throws IOException {
        this.writeDeferredName();
        return this.open((int)3, (String)"{");
    }

    public JsonWriter endObject() throws IOException {
        return this.close((int)3, (int)5, (String)"}");
    }

    private JsonWriter open(int empty, String openBracket) throws IOException {
        this.beforeValue();
        this.push((int)empty);
        this.out.write((String)openBracket);
        return this;
    }

    private JsonWriter close(int empty, int nonempty, String closeBracket) throws IOException {
        int context = this.peek();
        if (context != nonempty && context != empty) {
            throw new IllegalStateException((String)"Nesting problem.");
        }
        if (this.deferredName != null) {
            throw new IllegalStateException((String)("Dangling name: " + this.deferredName));
        }
        --this.stackSize;
        if (context == nonempty) {
            this.newline();
        }
        this.out.write((String)closeBracket);
        return this;
    }

    private void push(int newTop) {
        if (this.stackSize == this.stack.length) {
            int[] newStack = new int[this.stackSize * 2];
            System.arraycopy((Object)this.stack, (int)0, (Object)newStack, (int)0, (int)this.stackSize);
            this.stack = newStack;
        }
        this.stack[this.stackSize++] = newTop;
    }

    private int peek() {
        if (this.stackSize != 0) return this.stack[this.stackSize - 1];
        throw new IllegalStateException((String)"JsonWriter is closed.");
    }

    private void replaceTop(int topOfStack) {
        this.stack[this.stackSize - 1] = topOfStack;
    }

    public JsonWriter name(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException((String)"name == null");
        }
        if (this.deferredName != null) {
            throw new IllegalStateException();
        }
        if (this.stackSize == 0) {
            throw new IllegalStateException((String)"JsonWriter is closed.");
        }
        this.deferredName = name;
        return this;
    }

    private void writeDeferredName() throws IOException {
        if (this.deferredName == null) return;
        this.beforeName();
        this.string((String)this.deferredName);
        this.deferredName = null;
    }

    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.string((String)value);
        return this;
    }

    public JsonWriter jsonValue(String value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.out.append((CharSequence)value);
        return this;
    }

    public JsonWriter nullValue() throws IOException {
        if (this.deferredName != null) {
            if (!this.serializeNulls) {
                this.deferredName = null;
                return this;
            }
            this.writeDeferredName();
        }
        this.beforeValue();
        this.out.write((String)"null");
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        this.writeDeferredName();
        this.beforeValue();
        this.out.write((String)(value ? "true" : "false"));
        return this;
    }

    public JsonWriter value(Boolean value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.out.write((String)(value.booleanValue() ? "true" : "false"));
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        if (Double.isNaN((double)value)) throw new IllegalArgumentException((String)("Numeric values must be finite, but was " + value));
        if (Double.isInfinite((double)value)) {
            throw new IllegalArgumentException((String)("Numeric values must be finite, but was " + value));
        }
        this.writeDeferredName();
        this.beforeValue();
        this.out.append((CharSequence)Double.toString((double)value));
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        this.writeDeferredName();
        this.beforeValue();
        this.out.write((String)Long.toString((long)value));
        return this;
    }

    public JsonWriter value(Number value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        String string = value.toString();
        if (!this.lenient) {
            if (string.equals((Object)"-Infinity")) throw new IllegalArgumentException((String)("Numeric values must be finite, but was " + value));
            if (string.equals((Object)"Infinity")) throw new IllegalArgumentException((String)("Numeric values must be finite, but was " + value));
            if (string.equals((Object)"NaN")) {
                throw new IllegalArgumentException((String)("Numeric values must be finite, but was " + value));
            }
        }
        this.beforeValue();
        this.out.append((CharSequence)string);
        return this;
    }

    @Override
    public void flush() throws IOException {
        if (this.stackSize == 0) {
            throw new IllegalStateException((String)"JsonWriter is closed.");
        }
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.out.close();
        int size = this.stackSize;
        if (size > 1) throw new IOException((String)"Incomplete document");
        if (size == 1 && this.stack[size - 1] != 7) {
            throw new IOException((String)"Incomplete document");
        }
        this.stackSize = 0;
    }

    private void string(String value) throws IOException {
        String[] replacements = this.htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS : REPLACEMENT_CHARS;
        this.out.write((String)"\"");
        int last = 0;
        int length = value.length();
        for (int i = 0; i < length; ++i) {
            String replacement;
            char c = value.charAt((int)i);
            if (c < '?') {
                replacement = replacements[c];
                if (replacement == null) {
                    continue;
                }
            } else if (c == '\u2028') {
                replacement = "\\u2028";
            } else {
                if (c != '\u2029') continue;
                replacement = "\\u2029";
            }
            if (last < i) {
                this.out.write((String)value, (int)last, (int)(i - last));
            }
            this.out.write((String)replacement);
            last = i + 1;
        }
        if (last < length) {
            this.out.write((String)value, (int)last, (int)(length - last));
        }
        this.out.write((String)"\"");
    }

    private void newline() throws IOException {
        if (this.indent == null) {
            return;
        }
        this.out.write((String)"\n");
        int i = 1;
        int size = this.stackSize;
        while (i < size) {
            this.out.write((String)this.indent);
            ++i;
        }
    }

    private void beforeName() throws IOException {
        int context = this.peek();
        if (context == 5) {
            this.out.write((int)44);
        } else if (context != 3) {
            throw new IllegalStateException((String)"Nesting problem.");
        }
        this.newline();
        this.replaceTop((int)4);
    }

    private void beforeValue() throws IOException {
        switch (this.peek()) {
            case 7: {
                if (!this.lenient) {
                    throw new IllegalStateException((String)"JSON must have only one top-level value.");
                }
            }
            case 6: {
                this.replaceTop((int)7);
                return;
            }
            case 1: {
                this.replaceTop((int)2);
                this.newline();
                return;
            }
            case 2: {
                this.out.append((char)',');
                this.newline();
                return;
            }
            case 4: {
                this.out.append((CharSequence)this.separator);
                this.replaceTop((int)5);
                return;
            }
        }
        throw new IllegalStateException((String)"Nesting problem.");
    }

    static {
        int i = 0;
        do {
            if (i > 31) {
                JsonWriter.REPLACEMENT_CHARS[34] = "\\\"";
                JsonWriter.REPLACEMENT_CHARS[92] = "\\\\";
                JsonWriter.REPLACEMENT_CHARS[9] = "\\t";
                JsonWriter.REPLACEMENT_CHARS[8] = "\\b";
                JsonWriter.REPLACEMENT_CHARS[10] = "\\n";
                JsonWriter.REPLACEMENT_CHARS[13] = "\\r";
                JsonWriter.REPLACEMENT_CHARS[12] = "\\f";
                HTML_SAFE_REPLACEMENT_CHARS = (String[])REPLACEMENT_CHARS.clone();
                JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[60] = "\\u003c";
                JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
                JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
                JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
                JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
                return;
            }
            JsonWriter.REPLACEMENT_CHARS[i] = String.format((String)"\\u%04x", (Object[])new Object[]{Integer.valueOf((int)i)});
            ++i;
        } while (true);
    }
}

