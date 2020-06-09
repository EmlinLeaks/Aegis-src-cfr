/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;

@GwtIncompatible
abstract class LineBuffer {
    private StringBuilder line = new StringBuilder();
    private boolean sawReturn;

    LineBuffer() {
    }

    protected void add(char[] cbuf, int off, int len) throws IOException {
        int pos = off;
        if (this.sawReturn && len > 0 && this.finishLine((boolean)(cbuf[pos] == '\n'))) {
            ++pos;
        }
        int start = pos;
        int end = off + len;
        do {
            if (pos >= end) {
                this.line.append((char[])cbuf, (int)start, (int)(off + len - start));
                return;
            }
            switch (cbuf[pos]) {
                case '\r': {
                    this.line.append((char[])cbuf, (int)start, (int)(pos - start));
                    this.sawReturn = true;
                    if (pos + 1 < end && this.finishLine((boolean)(cbuf[pos + 1] == '\n'))) {
                        ++pos;
                    }
                    start = pos + 1;
                    break;
                }
                case '\n': {
                    this.line.append((char[])cbuf, (int)start, (int)(pos - start));
                    this.finishLine((boolean)true);
                    start = pos + 1;
                    break;
                }
            }
            ++pos;
        } while (true);
    }

    @CanIgnoreReturnValue
    private boolean finishLine(boolean sawNewline) throws IOException {
        String separator = this.sawReturn ? (sawNewline ? "\r\n" : "\r") : (sawNewline ? "\n" : "");
        this.handleLine((String)this.line.toString(), (String)separator);
        this.line = new StringBuilder();
        this.sawReturn = false;
        return sawNewline;
    }

    protected void finish() throws IOException {
        if (!this.sawReturn) {
            if (this.line.length() <= 0) return;
        }
        this.finishLine((boolean)false);
    }

    protected abstract void handleLine(String var1, String var2) throws IOException;
}

