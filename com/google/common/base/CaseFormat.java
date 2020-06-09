/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;

@GwtCompatible
public enum CaseFormat {
    LOWER_HYPHEN((CharMatcher)CharMatcher.is((char)'-'), (String)"-"){

        String normalizeWord(String word) {
            return Ascii.toLowerCase((String)word);
        }

        String convert(CaseFormat format, String s) {
            if (format == LOWER_UNDERSCORE) {
                return s.replace((char)'-', (char)'_');
            }
            if (format != UPPER_UNDERSCORE) return super.convert((CaseFormat)format, (String)s);
            return Ascii.toUpperCase((String)s.replace((char)'-', (char)'_'));
        }
    }
    ,
    LOWER_UNDERSCORE((CharMatcher)CharMatcher.is((char)'_'), (String)"_"){

        String normalizeWord(String word) {
            return Ascii.toLowerCase((String)word);
        }

        String convert(CaseFormat format, String s) {
            if (format == LOWER_HYPHEN) {
                return s.replace((char)'_', (char)'-');
            }
            if (format != UPPER_UNDERSCORE) return super.convert((CaseFormat)format, (String)s);
            return Ascii.toUpperCase((String)s);
        }
    }
    ,
    LOWER_CAMEL((CharMatcher)CharMatcher.inRange((char)'A', (char)'Z'), (String)""){

        String normalizeWord(String word) {
            return CaseFormat.access$100((String)word);
        }
    }
    ,
    UPPER_CAMEL((CharMatcher)CharMatcher.inRange((char)'A', (char)'Z'), (String)""){

        String normalizeWord(String word) {
            return CaseFormat.access$100((String)word);
        }
    }
    ,
    UPPER_UNDERSCORE((CharMatcher)CharMatcher.is((char)'_'), (String)"_"){

        String normalizeWord(String word) {
            return Ascii.toUpperCase((String)word);
        }

        String convert(CaseFormat format, String s) {
            if (format == LOWER_HYPHEN) {
                return Ascii.toLowerCase((String)s.replace((char)'_', (char)'-'));
            }
            if (format != LOWER_UNDERSCORE) return super.convert((CaseFormat)format, (String)s);
            return Ascii.toLowerCase((String)s);
        }
    };
    
    private final CharMatcher wordBoundary;
    private final String wordSeparator;

    private CaseFormat(CharMatcher wordBoundary, String wordSeparator) {
        this.wordBoundary = wordBoundary;
        this.wordSeparator = wordSeparator;
    }

    public final String to(CaseFormat format, String str) {
        String string;
        Preconditions.checkNotNull(format);
        Preconditions.checkNotNull(str);
        if (format == this) {
            string = str;
            return string;
        }
        string = this.convert((CaseFormat)format, (String)str);
        return string;
    }

    String convert(CaseFormat format, String s) {
        String string;
        StringBuilder out = null;
        int i = 0;
        int j = -1;
        do {
            ++j;
            if ((j = this.wordBoundary.indexIn((CharSequence)s, (int)j)) == -1) break;
            if (i == 0) {
                out = new StringBuilder((int)(s.length() + 4 * this.wordSeparator.length()));
                out.append((String)format.normalizeFirstWord((String)s.substring((int)i, (int)j)));
            } else {
                out.append((String)format.normalizeWord((String)s.substring((int)i, (int)j)));
            }
            out.append((String)format.wordSeparator);
            i = j + this.wordSeparator.length();
        } while (true);
        if (i == 0) {
            string = format.normalizeFirstWord((String)s);
            return string;
        }
        string = out.append((String)format.normalizeWord((String)s.substring((int)i))).toString();
        return string;
    }

    public Converter<String, String> converterTo(CaseFormat targetFormat) {
        return new StringConverter((CaseFormat)this, (CaseFormat)targetFormat);
    }

    abstract String normalizeWord(String var1);

    private String normalizeFirstWord(String word) {
        String string;
        if (this == LOWER_CAMEL) {
            string = Ascii.toLowerCase((String)word);
            return string;
        }
        string = this.normalizeWord((String)word);
        return string;
    }

    private static String firstCharOnlyToUpper(String word) {
        String string;
        if (word.isEmpty()) {
            string = word;
            return string;
        }
        string = Ascii.toUpperCase((char)word.charAt((int)0)) + Ascii.toLowerCase((String)word.substring((int)1));
        return string;
    }

    static /* synthetic */ String access$100(String x0) {
        return CaseFormat.firstCharOnlyToUpper((String)x0);
    }
}

