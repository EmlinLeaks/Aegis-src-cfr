/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.external.com.google.gdata.util.common.base;

import org.yaml.snakeyaml.external.com.google.gdata.util.common.base.Escaper;
import org.yaml.snakeyaml.external.com.google.gdata.util.common.base.UnicodeEscaper;

public abstract class UnicodeEscaper
implements Escaper {
    private static final int DEST_PAD = 32;
    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>(){

        protected char[] initialValue() {
            return new char[1024];
        }
    };

    protected abstract char[] escape(int var1);

    protected int nextEscapeIndex(CharSequence csq, int start, int end) {
        int index = start;
        while (index < end) {
            int cp = UnicodeEscaper.codePointAt((CharSequence)csq, (int)index, (int)end);
            if (cp < 0) return index;
            if (this.escape((int)cp) != null) {
                return index;
            }
            index += Character.isSupplementaryCodePoint((int)cp) ? 2 : 1;
        }
        return index;
    }

    @Override
    public String escape(String string) {
        String string2;
        int end = string.length();
        int index = this.nextEscapeIndex((CharSequence)string, (int)0, (int)end);
        if (index == end) {
            string2 = string;
            return string2;
        }
        string2 = this.escapeSlow((String)string, (int)index);
        return string2;
    }

    protected final String escapeSlow(String s, int index) {
        int end = s.length();
        char[] dest = DEST_TL.get();
        int destIndex = 0;
        int unescapedChunkStart = 0;
        while (index < end) {
            int cp = UnicodeEscaper.codePointAt((CharSequence)s, (int)index, (int)end);
            if (cp < 0) {
                throw new IllegalArgumentException((String)"Trailing high surrogate at end of input");
            }
            char[] escaped = this.escape((int)cp);
            if (escaped != null) {
                int charsSkipped = index - unescapedChunkStart;
                int sizeNeeded = destIndex + charsSkipped + escaped.length;
                if (dest.length < sizeNeeded) {
                    int destLength = sizeNeeded + (end - index) + 32;
                    dest = UnicodeEscaper.growBuffer((char[])dest, (int)destIndex, (int)destLength);
                }
                if (charsSkipped > 0) {
                    s.getChars((int)unescapedChunkStart, (int)index, (char[])dest, (int)destIndex);
                    destIndex += charsSkipped;
                }
                if (escaped.length > 0) {
                    System.arraycopy((Object)escaped, (int)0, (Object)dest, (int)destIndex, (int)escaped.length);
                    destIndex += escaped.length;
                }
            }
            unescapedChunkStart = index + (Character.isSupplementaryCodePoint((int)cp) ? 2 : 1);
            index = this.nextEscapeIndex((CharSequence)s, (int)unescapedChunkStart, (int)end);
        }
        int charsSkipped = end - unescapedChunkStart;
        if (charsSkipped <= 0) return new String((char[])dest, (int)0, (int)destIndex);
        int endIndex = destIndex + charsSkipped;
        if (dest.length < endIndex) {
            dest = UnicodeEscaper.growBuffer((char[])dest, (int)destIndex, (int)endIndex);
        }
        s.getChars((int)unescapedChunkStart, (int)end, (char[])dest, (int)destIndex);
        destIndex = endIndex;
        return new String((char[])dest, (int)0, (int)destIndex);
    }

    @Override
    public Appendable escape(Appendable out) {
        if ($assertionsDisabled) return new Appendable((UnicodeEscaper)this, (Appendable)out){
            int pendingHighSurrogate;
            char[] decodedChars;
            final /* synthetic */ Appendable val$out;
            final /* synthetic */ UnicodeEscaper this$0;
            {
                this.this$0 = this$0;
                this.val$out = appendable;
                this.pendingHighSurrogate = -1;
                this.decodedChars = new char[2];
            }

            public Appendable append(CharSequence csq) throws java.io.IOException {
                return this.append((CharSequence)csq, (int)0, (int)csq.length());
            }

            public Appendable append(CharSequence csq, int start, int end) throws java.io.IOException {
                char[] escaped;
                int index = start;
                if (index >= end) return this;
                int unescapedChunkStart = index;
                if (this.pendingHighSurrogate != -1) {
                    char c;
                    if (!Character.isLowSurrogate((char)(c = csq.charAt((int)index++)))) {
                        throw new IllegalArgumentException((String)("Expected low surrogate character but got " + c));
                    }
                    escaped = this.this$0.escape((int)Character.toCodePoint((char)((char)this.pendingHighSurrogate), (char)c));
                    if (escaped != null) {
                        this.outputChars((char[])escaped, (int)escaped.length);
                        ++unescapedChunkStart;
                    } else {
                        this.val$out.append((char)((char)this.pendingHighSurrogate));
                    }
                    this.pendingHighSurrogate = -1;
                }
                do {
                    if ((index = this.this$0.nextEscapeIndex((CharSequence)csq, (int)index, (int)end)) > unescapedChunkStart) {
                        this.val$out.append((CharSequence)csq, (int)unescapedChunkStart, (int)index);
                    }
                    if (index == end) {
                        return this;
                    }
                    int cp = UnicodeEscaper.codePointAt((CharSequence)csq, (int)index, (int)end);
                    if (cp < 0) {
                        this.pendingHighSurrogate = -cp;
                        return this;
                    }
                    escaped = this.this$0.escape((int)cp);
                    if (escaped != null) {
                        this.outputChars((char[])escaped, (int)escaped.length);
                    } else {
                        int len = Character.toChars((int)cp, (char[])this.decodedChars, (int)0);
                        this.outputChars((char[])this.decodedChars, (int)len);
                    }
                    unescapedChunkStart = index += Character.isSupplementaryCodePoint((int)cp) ? 2 : 1;
                } while (true);
            }

            public Appendable append(char c) throws java.io.IOException {
                if (this.pendingHighSurrogate != -1) {
                    if (!Character.isLowSurrogate((char)c)) {
                        throw new IllegalArgumentException((String)("Expected low surrogate character but got '" + c + "' with value " + c));
                    }
                    char[] escaped = this.this$0.escape((int)Character.toCodePoint((char)((char)this.pendingHighSurrogate), (char)c));
                    if (escaped != null) {
                        this.outputChars((char[])escaped, (int)escaped.length);
                    } else {
                        this.val$out.append((char)((char)this.pendingHighSurrogate));
                        this.val$out.append((char)c);
                    }
                    this.pendingHighSurrogate = -1;
                    return this;
                }
                if (Character.isHighSurrogate((char)c)) {
                    this.pendingHighSurrogate = (int)c;
                    return this;
                }
                if (Character.isLowSurrogate((char)c)) {
                    throw new IllegalArgumentException((String)("Unexpected low surrogate character '" + c + "' with value " + c));
                }
                char[] escaped = this.this$0.escape((int)c);
                if (escaped != null) {
                    this.outputChars((char[])escaped, (int)escaped.length);
                    return this;
                }
                this.val$out.append((char)c);
                return this;
            }

            private void outputChars(char[] chars, int len) throws java.io.IOException {
                int n = 0;
                while (n < len) {
                    this.val$out.append((char)chars[n]);
                    ++n;
                }
            }
        };
        if (out != null) return new /* invalid duplicate definition of identical inner class */;
        throw new AssertionError();
    }

    protected static final int codePointAt(CharSequence seq, int index, int end) {
        char c1;
        if (index >= end) throw new IndexOutOfBoundsException((String)"Index exceeds specified range");
        if ((c1 = seq.charAt((int)index++)) < '\ud800') return c1;
        if (c1 > '\udfff') {
            return c1;
        }
        if (c1 > '\udbff') throw new IllegalArgumentException((String)("Unexpected low surrogate character '" + c1 + "' with value " + c1 + " at index " + (index - 1)));
        if (index == end) {
            return -c1;
        }
        char c2 = seq.charAt((int)index);
        if (!Character.isLowSurrogate((char)c2)) throw new IllegalArgumentException((String)("Expected low surrogate but got char '" + c2 + "' with value " + c2 + " at index " + index));
        return Character.toCodePoint((char)c1, (char)c2);
    }

    private static final char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index <= 0) return copy;
        System.arraycopy((Object)dest, (int)0, (Object)copy, (int)0, (int)index);
        return copy;
    }
}

