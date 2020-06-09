/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

public class EscapeTokenizer {
    private static final char CHR_ESCAPE = '\\';
    private static final char CHR_SGL_QUOTE = '\'';
    private static final char CHR_DBL_QUOTE = '\"';
    private static final char CHR_LF = '\n';
    private static final char CHR_CR = '\r';
    private static final char CHR_COMMENT = '-';
    private static final char CHR_BEGIN_TOKEN = '{';
    private static final char CHR_END_TOKEN = '}';
    private static final char CHR_VARIABLE = '@';
    private String source = null;
    private int sourceLength = 0;
    private int pos = 0;
    private boolean emittingEscapeCode = false;
    private boolean sawVariableUse = false;
    private int bracesLevel = 0;
    private boolean inQuotes = false;
    private char quoteChar = '\u0000';

    public EscapeTokenizer(String source) {
        this.source = source;
        this.sourceLength = source.length();
        this.pos = 0;
    }

    public synchronized boolean hasMoreTokens() {
        if (this.pos >= this.sourceLength) return false;
        return true;
    }

    /*
     * Unable to fully structure code
     */
    public synchronized String nextToken() {
        tokenBuf = new StringBuilder();
        backslashEscape = false;
        if (this.emittingEscapeCode) {
            tokenBuf.append((String)"{");
            this.emittingEscapeCode = false;
        }
        while (this.pos < this.sourceLength) {
            block19 : {
                block21 : {
                    block20 : {
                        block18 : {
                            c = this.source.charAt((int)this.pos);
                            if (c != '\\') break block18;
                            tokenBuf.append((char)c);
                            backslashEscape = backslashEscape == false;
                            break block19;
                        }
                        if (c != '\'' && c != '\"' || backslashEscape) break block20;
                        tokenBuf.append((char)c);
                        if (this.inQuotes) {
                            if (c == this.quoteChar) {
                                if (this.pos + 1 < this.sourceLength && this.source.charAt((int)(this.pos + 1)) == this.quoteChar) {
                                    tokenBuf.append((char)c);
                                    ++this.pos;
                                } else {
                                    this.inQuotes = false;
                                }
                            }
                        } else {
                            this.inQuotes = true;
                            this.quoteChar = c;
                        }
                        break block19;
                    }
                    if (c != '\n' && c != '\r') break block21;
                    tokenBuf.append((char)c);
                    backslashEscape = false;
                    break block19;
                }
                if (this.inQuotes || backslashEscape) ** GOTO lbl68
                if (c == '-') {
                    tokenBuf.append((char)c);
                    if (this.pos + 1 < this.sourceLength && this.source.charAt((int)(this.pos + 1)) == '-') {
                        while (++this.pos < this.sourceLength && c != '\n' && c != '\r') {
                            c = this.source.charAt((int)this.pos);
                            tokenBuf.append((char)c);
                        }
                        --this.pos;
                    }
                } else if (c == '{') {
                    ++this.bracesLevel;
                    if (this.bracesLevel == 1) {
                        this.emittingEscapeCode = true;
                        ++this.pos;
                        return tokenBuf.toString();
                    }
                    tokenBuf.append((char)c);
                } else if (c == '}') {
                    tokenBuf.append((char)c);
                    --this.bracesLevel;
                    if (this.bracesLevel == 0) {
                        ++this.pos;
                        return tokenBuf.toString();
                    }
                } else {
                    if (c == '@') {
                        this.sawVariableUse = true;
                    }
lbl68: // 4 sources:
                    tokenBuf.append((char)c);
                    backslashEscape = false;
                }
            }
            ++this.pos;
        }
        return tokenBuf.toString();
    }

    boolean sawVariableUse() {
        return this.sawVariableUse;
    }
}

