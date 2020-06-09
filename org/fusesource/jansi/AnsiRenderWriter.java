/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import org.fusesource.jansi.AnsiRenderer;

public class AnsiRenderWriter
extends PrintWriter {
    public AnsiRenderWriter(OutputStream out) {
        super((OutputStream)out);
    }

    public AnsiRenderWriter(OutputStream out, boolean autoFlush) {
        super((OutputStream)out, (boolean)autoFlush);
    }

    public AnsiRenderWriter(Writer out) {
        super((Writer)out);
    }

    public AnsiRenderWriter(Writer out, boolean autoFlush) {
        super((Writer)out, (boolean)autoFlush);
    }

    @Override
    public void write(String s) {
        if (AnsiRenderer.test((String)s)) {
            super.write((String)AnsiRenderer.render((String)s));
            return;
        }
        super.write((String)s);
    }

    @Override
    public PrintWriter format(String format, Object ... args) {
        this.print((String)String.format((String)format, (Object[])args));
        return this;
    }

    @Override
    public PrintWriter format(Locale l, String format, Object ... args) {
        this.print((String)String.format((Locale)l, (String)format, (Object[])args));
        return this;
    }
}

