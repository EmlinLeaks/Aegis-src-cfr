/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.io.LineBuffer;
import com.google.common.io.LineReader;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.util.LinkedList;
import java.util.Queue;

@Beta
@GwtIncompatible
public final class LineReader {
    private final Readable readable;
    private final Reader reader;
    private final CharBuffer cbuf = CharStreams.createBuffer();
    private final char[] buf = this.cbuf.array();
    private final Queue<String> lines = new LinkedList<String>();
    private final LineBuffer lineBuf = new LineBuffer((LineReader)this){
        final /* synthetic */ LineReader this$0;
        {
            this.this$0 = lineReader;
        }

        protected void handleLine(String line, String end) {
            LineReader.access$000((LineReader)this.this$0).add(line);
        }
    };

    public LineReader(Readable readable) {
        this.readable = Preconditions.checkNotNull(readable);
        this.reader = readable instanceof Reader ? (Reader)readable : null;
    }

    @CanIgnoreReturnValue
    public String readLine() throws IOException {
        while (this.lines.peek() == null) {
            int read;
            this.cbuf.clear();
            int n = read = this.reader != null ? this.reader.read((char[])this.buf, (int)0, (int)this.buf.length) : this.readable.read((CharBuffer)this.cbuf);
            if (read == -1) {
                this.lineBuf.finish();
                return this.lines.poll();
            }
            this.lineBuf.add((char[])this.buf, (int)0, (int)read);
        }
        return this.lines.poll();
    }

    static /* synthetic */ Queue access$000(LineReader x0) {
        return x0.lines;
    }
}

