/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.NoSuchElementException;
import joptsimple.AbstractOptionSpec;
import joptsimple.AlternativeLongOptionSpec;
import joptsimple.NoArgumentOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionalArgumentOptionSpec;
import joptsimple.ParserRules;
import joptsimple.RequiredArgumentOptionSpec;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class OptionSpecTokenizer {
    private static final char POSIXLY_CORRECT_MARKER = '+';
    private static final char HELP_MARKER = '*';
    private String specification;
    private int index;

    OptionSpecTokenizer(String specification) {
        if (specification == null) {
            throw new NullPointerException((String)"null option specification");
        }
        this.specification = specification;
    }

    boolean hasMore() {
        if (this.index >= this.specification.length()) return false;
        return true;
    }

    AbstractOptionSpec<?> next() {
        NoArgumentOptionSpec spec;
        if (!this.hasMore()) {
            throw new NoSuchElementException();
        }
        String optionCandidate = String.valueOf((char)this.specification.charAt((int)this.index));
        ++this.index;
        if ("W".equals((Object)optionCandidate) && (spec = this.handleReservedForExtensionsToken()) != null) {
            return spec;
        }
        ParserRules.ensureLegalOption((String)optionCandidate);
        if (!this.hasMore()) {
            return new NoArgumentOptionSpec((String)optionCandidate);
        }
        boolean forHelp = false;
        if (this.specification.charAt((int)this.index) == '*') {
            forHelp = true;
            ++this.index;
        }
        spec = this.hasMore() && this.specification.charAt((int)this.index) == ':' ? this.handleArgumentAcceptingOption((String)optionCandidate) : new NoArgumentOptionSpec((String)optionCandidate);
        if (!forHelp) return spec;
        spec.forHelp();
        return spec;
    }

    void configure(OptionParser parser) {
        this.adjustForPosixlyCorrect((OptionParser)parser);
        while (this.hasMore()) {
            parser.recognize(this.next());
        }
    }

    private void adjustForPosixlyCorrect(OptionParser parser) {
        if ('+' != this.specification.charAt((int)0)) return;
        parser.posixlyCorrect((boolean)true);
        this.specification = this.specification.substring((int)1);
    }

    private AbstractOptionSpec<?> handleReservedForExtensionsToken() {
        if (!this.hasMore()) {
            return new NoArgumentOptionSpec((String)"W");
        }
        if (this.specification.charAt((int)this.index) != ';') return null;
        ++this.index;
        return new AlternativeLongOptionSpec();
    }

    private AbstractOptionSpec<?> handleArgumentAcceptingOption(String candidate) {
        ++this.index;
        if (!this.hasMore()) return new RequiredArgumentOptionSpec<V>((String)candidate);
        if (this.specification.charAt((int)this.index) != ':') return new RequiredArgumentOptionSpec<V>((String)candidate);
        ++this.index;
        return new OptionalArgumentOptionSpec<V>((String)candidate);
    }
}

