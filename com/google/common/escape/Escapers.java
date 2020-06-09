/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.CharEscaper;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.google.common.escape.UnicodeEscaper;

@Beta
@GwtCompatible
public final class Escapers {
    private static final Escaper NULL_ESCAPER = new CharEscaper(){

        public String escape(String string) {
            return Preconditions.checkNotNull(string);
        }

        protected char[] escape(char c) {
            return null;
        }
    };

    private Escapers() {
    }

    public static Escaper nullEscaper() {
        return NULL_ESCAPER;
    }

    public static Builder builder() {
        return new Builder(null);
    }

    static UnicodeEscaper asUnicodeEscaper(Escaper escaper) {
        Preconditions.checkNotNull(escaper);
        if (escaper instanceof UnicodeEscaper) {
            return (UnicodeEscaper)escaper;
        }
        if (!(escaper instanceof CharEscaper)) throw new IllegalArgumentException((String)("Cannot create a UnicodeEscaper from: " + escaper.getClass().getName()));
        return Escapers.wrap((CharEscaper)((CharEscaper)escaper));
    }

    public static String computeReplacement(CharEscaper escaper, char c) {
        return Escapers.stringOrNull((char[])escaper.escape((char)c));
    }

    public static String computeReplacement(UnicodeEscaper escaper, int cp) {
        return Escapers.stringOrNull((char[])escaper.escape((int)cp));
    }

    private static String stringOrNull(char[] in) {
        if (in == null) {
            return null;
        }
        String string = new String((char[])in);
        return string;
    }

    private static UnicodeEscaper wrap(CharEscaper escaper) {
        return new UnicodeEscaper((CharEscaper)escaper){
            final /* synthetic */ CharEscaper val$escaper;
            {
                this.val$escaper = charEscaper;
            }

            protected char[] escape(int cp) {
                int n;
                if (cp < 65536) {
                    return this.val$escaper.escape((char)((char)cp));
                }
                char[] surrogateChars = new char[2];
                java.lang.Character.toChars((int)cp, (char[])surrogateChars, (int)0);
                char[] hiChars = this.val$escaper.escape((char)surrogateChars[0]);
                char[] loChars = this.val$escaper.escape((char)surrogateChars[1]);
                if (hiChars == null && loChars == null) {
                    return null;
                }
                int hiCount = hiChars != null ? hiChars.length : 1;
                int loCount = loChars != null ? loChars.length : 1;
                char[] output = new char[hiCount + loCount];
                if (hiChars != null) {
                    for (n = 0; n < hiChars.length; ++n) {
                        output[n] = hiChars[n];
                    }
                } else {
                    output[0] = surrogateChars[0];
                }
                if (loChars == null) {
                    output[hiCount] = surrogateChars[1];
                    return output;
                }
                n = 0;
                while (n < loChars.length) {
                    output[hiCount + n] = loChars[n];
                    ++n;
                }
                return output;
            }
        };
    }
}

