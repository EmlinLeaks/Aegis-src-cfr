/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.Iterator;
import java.util.List;
import joptsimple.IllegalOptionSpecificationException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ParserRules {
    static final char HYPHEN_CHAR = '-';
    static final String HYPHEN = String.valueOf((char)'-');
    static final String DOUBLE_HYPHEN = "--";
    static final String OPTION_TERMINATOR = "--";
    static final String RESERVED_FOR_EXTENSIONS = "W";

    private ParserRules() {
        throw new UnsupportedOperationException();
    }

    static boolean isShortOptionToken(String argument) {
        if (!argument.startsWith((String)HYPHEN)) return false;
        if (HYPHEN.equals((Object)argument)) return false;
        if (ParserRules.isLongOptionToken((String)argument)) return false;
        return true;
    }

    static boolean isLongOptionToken(String argument) {
        if (!argument.startsWith((String)"--")) return false;
        if (ParserRules.isOptionTerminator((String)argument)) return false;
        return true;
    }

    static boolean isOptionTerminator(String argument) {
        return "--".equals((Object)argument);
    }

    static void ensureLegalOption(String option) {
        if (option.startsWith((String)HYPHEN)) {
            throw new IllegalOptionSpecificationException((String)String.valueOf((Object)option));
        }
        int i = 0;
        while (i < option.length()) {
            ParserRules.ensureLegalOptionCharacter((char)option.charAt((int)i));
            ++i;
        }
    }

    static void ensureLegalOptions(List<String> options) {
        Iterator<String> i$ = options.iterator();
        while (i$.hasNext()) {
            String each = i$.next();
            ParserRules.ensureLegalOption((String)each);
        }
    }

    private static void ensureLegalOptionCharacter(char option) {
        if (Character.isLetterOrDigit((char)option)) return;
        if (ParserRules.isAllowedPunctuation((char)option)) return;
        throw new IllegalOptionSpecificationException((String)String.valueOf((char)option));
    }

    private static boolean isAllowedPunctuation(char option) {
        String allowedPunctuation = "?._-";
        if (allowedPunctuation.indexOf((int)option) == -1) return false;
        return true;
    }
}

