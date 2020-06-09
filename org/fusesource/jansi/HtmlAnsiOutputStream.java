/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.fusesource.jansi.AnsiOutputStream;

public class HtmlAnsiOutputStream
extends AnsiOutputStream {
    private boolean concealOn = false;
    private static final String[] ANSI_COLOR_MAP = new String[]{"black", "red", "green", "yellow", "blue", "magenta", "cyan", "white"};
    private static final byte[] BYTES_QUOT = "&quot;".getBytes();
    private static final byte[] BYTES_AMP = "&amp;".getBytes();
    private static final byte[] BYTES_LT = "&lt;".getBytes();
    private static final byte[] BYTES_GT = "&gt;".getBytes();
    private List<String> closingAttributes = new ArrayList<String>();

    @Override
    public void close() throws IOException {
        this.closeAttributes();
        super.close();
    }

    public HtmlAnsiOutputStream(OutputStream os) {
        super((OutputStream)os);
    }

    private void write(String s) throws IOException {
        this.out.write((byte[])s.getBytes());
    }

    private void writeAttribute(String s) throws IOException {
        this.write((String)("<" + s + ">"));
        this.closingAttributes.add((int)0, (String)s.split((String)" ", (int)2)[0]);
    }

    private void closeAttributes() throws IOException {
        Iterator<String> i$ = this.closingAttributes.iterator();
        do {
            if (!i$.hasNext()) {
                this.closingAttributes.clear();
                return;
            }
            String attr = i$.next();
            this.write((String)("</" + attr + ">"));
        } while (true);
    }

    @Override
    public void write(int data) throws IOException {
        switch (data) {
            case 34: {
                this.out.write((byte[])BYTES_QUOT);
                return;
            }
            case 38: {
                this.out.write((byte[])BYTES_AMP);
                return;
            }
            case 60: {
                this.out.write((byte[])BYTES_LT);
                return;
            }
            case 62: {
                this.out.write((byte[])BYTES_GT);
                return;
            }
        }
        super.write((int)data);
    }

    public void writeLine(byte[] buf, int offset, int len) throws IOException {
        this.write((byte[])buf, (int)offset, (int)len);
        this.closeAttributes();
    }

    @Override
    protected void processSetAttribute(int attribute) throws IOException {
        switch (attribute) {
            case 8: {
                this.write((String)"\u001b[8m");
                this.concealOn = true;
                return;
            }
            case 1: {
                this.writeAttribute((String)"b");
                return;
            }
            case 22: {
                this.closeAttributes();
                return;
            }
            case 4: {
                this.writeAttribute((String)"u");
                return;
            }
            case 24: {
                this.closeAttributes();
                return;
            }
            case 7: {
                break;
            }
        }
    }

    @Override
    protected void processAttributeRest() throws IOException {
        if (this.concealOn) {
            this.write((String)"\u001b[0m");
            this.concealOn = false;
        }
        this.closeAttributes();
    }

    @Override
    protected void processSetForegroundColor(int color) throws IOException {
        this.writeAttribute((String)("span style=\"color: " + ANSI_COLOR_MAP[color] + ";\""));
    }

    @Override
    protected void processSetBackgroundColor(int color) throws IOException {
        this.writeAttribute((String)("span style=\"background-color: " + ANSI_COLOR_MAP[color] + ";\""));
    }
}

