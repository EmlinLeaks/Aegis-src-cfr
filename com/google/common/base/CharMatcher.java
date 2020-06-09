/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.CharMatcher;
import com.google.common.base.Platform;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.SmallCharMatcher;
import java.util.BitSet;

@GwtCompatible(emulated=true)
public abstract class CharMatcher
implements Predicate<Character> {
    @Deprecated
    public static final CharMatcher WHITESPACE = CharMatcher.whitespace();
    @Deprecated
    public static final CharMatcher BREAKING_WHITESPACE = CharMatcher.breakingWhitespace();
    @Deprecated
    public static final CharMatcher ASCII = CharMatcher.ascii();
    @Deprecated
    public static final CharMatcher DIGIT = CharMatcher.digit();
    @Deprecated
    public static final CharMatcher JAVA_DIGIT = CharMatcher.javaDigit();
    @Deprecated
    public static final CharMatcher JAVA_LETTER = CharMatcher.javaLetter();
    @Deprecated
    public static final CharMatcher JAVA_LETTER_OR_DIGIT = CharMatcher.javaLetterOrDigit();
    @Deprecated
    public static final CharMatcher JAVA_UPPER_CASE = CharMatcher.javaUpperCase();
    @Deprecated
    public static final CharMatcher JAVA_LOWER_CASE = CharMatcher.javaLowerCase();
    @Deprecated
    public static final CharMatcher JAVA_ISO_CONTROL = CharMatcher.javaIsoControl();
    @Deprecated
    public static final CharMatcher INVISIBLE = CharMatcher.invisible();
    @Deprecated
    public static final CharMatcher SINGLE_WIDTH = CharMatcher.singleWidth();
    @Deprecated
    public static final CharMatcher ANY = CharMatcher.any();
    @Deprecated
    public static final CharMatcher NONE = CharMatcher.none();
    private static final int DISTINCT_CHARS = 65536;

    public static CharMatcher any() {
        return Any.INSTANCE;
    }

    public static CharMatcher none() {
        return None.INSTANCE;
    }

    public static CharMatcher whitespace() {
        return Whitespace.INSTANCE;
    }

    public static CharMatcher breakingWhitespace() {
        return BreakingWhitespace.INSTANCE;
    }

    public static CharMatcher ascii() {
        return Ascii.INSTANCE;
    }

    public static CharMatcher digit() {
        return Digit.INSTANCE;
    }

    public static CharMatcher javaDigit() {
        return JavaDigit.INSTANCE;
    }

    public static CharMatcher javaLetter() {
        return JavaLetter.INSTANCE;
    }

    public static CharMatcher javaLetterOrDigit() {
        return JavaLetterOrDigit.INSTANCE;
    }

    public static CharMatcher javaUpperCase() {
        return JavaUpperCase.INSTANCE;
    }

    public static CharMatcher javaLowerCase() {
        return JavaLowerCase.INSTANCE;
    }

    public static CharMatcher javaIsoControl() {
        return JavaIsoControl.INSTANCE;
    }

    public static CharMatcher invisible() {
        return Invisible.INSTANCE;
    }

    public static CharMatcher singleWidth() {
        return SingleWidth.INSTANCE;
    }

    public static CharMatcher is(char match) {
        return new Is((char)match);
    }

    public static CharMatcher isNot(char match) {
        return new IsNot((char)match);
    }

    public static CharMatcher anyOf(CharSequence sequence) {
        switch (sequence.length()) {
            case 0: {
                return CharMatcher.none();
            }
            case 1: {
                return CharMatcher.is((char)sequence.charAt((int)0));
            }
            case 2: {
                return CharMatcher.isEither((char)sequence.charAt((int)0), (char)sequence.charAt((int)1));
            }
        }
        return new AnyOf((CharSequence)sequence);
    }

    public static CharMatcher noneOf(CharSequence sequence) {
        return CharMatcher.anyOf((CharSequence)sequence).negate();
    }

    public static CharMatcher inRange(char startInclusive, char endInclusive) {
        return new InRange((char)startInclusive, (char)endInclusive);
    }

    public static CharMatcher forPredicate(Predicate<? super Character> predicate) {
        CharMatcher charMatcher;
        if (predicate instanceof CharMatcher) {
            charMatcher = (CharMatcher)predicate;
            return charMatcher;
        }
        charMatcher = new ForPredicate(predicate);
        return charMatcher;
    }

    protected CharMatcher() {
    }

    public abstract boolean matches(char var1);

    public CharMatcher negate() {
        return new Negated((CharMatcher)this);
    }

    public CharMatcher and(CharMatcher other) {
        return new And((CharMatcher)this, (CharMatcher)other);
    }

    public CharMatcher or(CharMatcher other) {
        return new Or((CharMatcher)this, (CharMatcher)other);
    }

    public CharMatcher precomputed() {
        return Platform.precomputeCharMatcher((CharMatcher)this);
    }

    @GwtIncompatible
    CharMatcher precomputedInternal() {
        BitSet table = new BitSet();
        this.setBits((BitSet)table);
        int totalCharacters = table.cardinality();
        if (totalCharacters * 2 <= 65536) {
            return CharMatcher.precomputedPositive((int)totalCharacters, (BitSet)table, (String)this.toString());
        }
        table.flip((int)0, (int)65536);
        int negatedCharacters = 65536 - totalCharacters;
        String suffix = ".negate()";
        String description = this.toString();
        String negatedDescription = description.endsWith((String)suffix) ? description.substring((int)0, (int)(description.length() - suffix.length())) : description + suffix;
        return new NegatedFastMatcher((CharMatcher)this, (CharMatcher)CharMatcher.precomputedPositive((int)negatedCharacters, (BitSet)table, (String)negatedDescription), (String)description){
            final /* synthetic */ String val$description;
            final /* synthetic */ CharMatcher this$0;
            {
                this.this$0 = charMatcher;
                this.val$description = string;
                super((CharMatcher)x0);
            }

            public String toString() {
                return this.val$description;
            }
        };
    }

    @GwtIncompatible
    private static CharMatcher precomputedPositive(int totalCharacters, BitSet table, String description) {
        CharMatcher charMatcher;
        switch (totalCharacters) {
            case 0: {
                return CharMatcher.none();
            }
            case 1: {
                return CharMatcher.is((char)((char)table.nextSetBit((int)0)));
            }
            case 2: {
                char c1 = (char)table.nextSetBit((int)0);
                char c2 = (char)table.nextSetBit((int)(c1 + '\u0001'));
                return CharMatcher.isEither((char)c1, (char)c2);
            }
        }
        if (CharMatcher.isSmall((int)totalCharacters, (int)table.length())) {
            charMatcher = SmallCharMatcher.from((BitSet)table, (String)description);
            return charMatcher;
        }
        charMatcher = new BitSetMatcher((BitSet)table, (String)description, null);
        return charMatcher;
    }

    @GwtIncompatible
    private static boolean isSmall(int totalCharacters, int tableLength) {
        if (totalCharacters > 1023) return false;
        if (tableLength <= totalCharacters * 4 * 16) return false;
        return true;
    }

    @GwtIncompatible
    void setBits(BitSet table) {
        int c = 65535;
        while (c >= 0) {
            if (this.matches((char)((char)c))) {
                table.set((int)c);
            }
            --c;
        }
    }

    public boolean matchesAnyOf(CharSequence sequence) {
        if (this.matchesNoneOf((CharSequence)sequence)) return false;
        return true;
    }

    public boolean matchesAllOf(CharSequence sequence) {
        int i = sequence.length() - 1;
        while (i >= 0) {
            if (!this.matches((char)sequence.charAt((int)i))) {
                return false;
            }
            --i;
        }
        return true;
    }

    public boolean matchesNoneOf(CharSequence sequence) {
        if (this.indexIn((CharSequence)sequence) != -1) return false;
        return true;
    }

    public int indexIn(CharSequence sequence) {
        return this.indexIn((CharSequence)sequence, (int)0);
    }

    public int indexIn(CharSequence sequence, int start) {
        int length = sequence.length();
        Preconditions.checkPositionIndex((int)start, (int)length);
        int i = start;
        while (i < length) {
            if (this.matches((char)sequence.charAt((int)i))) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public int lastIndexIn(CharSequence sequence) {
        int i = sequence.length() - 1;
        while (i >= 0) {
            if (this.matches((char)sequence.charAt((int)i))) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public int countIn(CharSequence sequence) {
        int count = 0;
        int i = 0;
        while (i < sequence.length()) {
            if (this.matches((char)sequence.charAt((int)i))) {
                ++count;
            }
            ++i;
        }
        return count;
    }

    public String removeFrom(CharSequence sequence) {
        String string = sequence.toString();
        int pos = this.indexIn((CharSequence)string);
        if (pos == -1) {
            return string;
        }
        char[] chars = string.toCharArray();
        int spread = 1;
        block0 : do {
            ++pos;
            do {
                if (pos == chars.length) {
                    return new String((char[])chars, (int)0, (int)(pos - spread));
                }
                if (this.matches((char)chars[pos])) {
                    ++spread;
                    continue block0;
                }
                chars[pos - spread] = chars[pos];
                ++pos;
            } while (true);
            break;
        } while (true);
    }

    public String retainFrom(CharSequence sequence) {
        return this.negate().removeFrom((CharSequence)sequence);
    }

    public String replaceFrom(CharSequence sequence, char replacement) {
        String string = sequence.toString();
        int pos = this.indexIn((CharSequence)string);
        if (pos == -1) {
            return string;
        }
        char[] chars = string.toCharArray();
        chars[pos] = replacement;
        int i = pos + 1;
        while (i < chars.length) {
            if (this.matches((char)chars[i])) {
                chars[i] = replacement;
            }
            ++i;
        }
        return new String((char[])chars);
    }

    public String replaceFrom(CharSequence sequence, CharSequence replacement) {
        int replacementLen = replacement.length();
        if (replacementLen == 0) {
            return this.removeFrom((CharSequence)sequence);
        }
        if (replacementLen == 1) {
            return this.replaceFrom((CharSequence)sequence, (char)replacement.charAt((int)0));
        }
        String string = sequence.toString();
        int pos = this.indexIn((CharSequence)string);
        if (pos == -1) {
            return string;
        }
        int len = string.length();
        StringBuilder buf = new StringBuilder((int)(len * 3 / 2 + 16));
        int oldpos = 0;
        do {
            buf.append((CharSequence)string, (int)oldpos, (int)pos);
            buf.append((CharSequence)replacement);
            oldpos = pos + 1;
        } while ((pos = this.indexIn((CharSequence)string, (int)oldpos)) != -1);
        buf.append((CharSequence)string, (int)oldpos, (int)len);
        return buf.toString();
    }

    public String trimFrom(CharSequence sequence) {
        int first;
        int len = sequence.length();
        for (first = 0; first < len && this.matches((char)sequence.charAt((int)first)); ++first) {
        }
        int last = len - 1;
        while (last > first) {
            if (!this.matches((char)sequence.charAt((int)last))) {
                return sequence.subSequence((int)first, (int)(last + 1)).toString();
            }
            --last;
        }
        return sequence.subSequence((int)first, (int)(last + 1)).toString();
    }

    public String trimLeadingFrom(CharSequence sequence) {
        int len = sequence.length();
        int first = 0;
        while (first < len) {
            if (!this.matches((char)sequence.charAt((int)first))) {
                return sequence.subSequence((int)first, (int)len).toString();
            }
            ++first;
        }
        return "";
    }

    public String trimTrailingFrom(CharSequence sequence) {
        int len = sequence.length();
        int last = len - 1;
        while (last >= 0) {
            if (!this.matches((char)sequence.charAt((int)last))) {
                return sequence.subSequence((int)0, (int)(last + 1)).toString();
            }
            --last;
        }
        return "";
    }

    public String collapseFrom(CharSequence sequence, char replacement) {
        int i;
        int len;
        block3 : {
            len = sequence.length();
            i = 0;
            while (i < len) {
                char c = sequence.charAt((int)i);
                if (this.matches((char)c)) {
                    if (c != replacement || i != len - 1 && this.matches((char)sequence.charAt((int)(i + 1)))) break block3;
                    ++i;
                }
                ++i;
            }
            return sequence.toString();
        }
        StringBuilder builder = new StringBuilder((int)len).append((CharSequence)sequence, (int)0, (int)i).append((char)replacement);
        return this.finishCollapseFrom((CharSequence)sequence, (int)(i + 1), (int)len, (char)replacement, (StringBuilder)builder, (boolean)true);
    }

    public String trimAndCollapseFrom(CharSequence sequence, char replacement) {
        String string;
        int first;
        int len = sequence.length();
        int last = len - 1;
        for (first = 0; first < len && this.matches((char)sequence.charAt((int)first)); ++first) {
        }
        while (last > first && this.matches((char)sequence.charAt((int)last))) {
            --last;
        }
        if (first == 0 && last == len - 1) {
            string = this.collapseFrom((CharSequence)sequence, (char)replacement);
            return string;
        }
        string = this.finishCollapseFrom((CharSequence)sequence, (int)first, (int)(last + 1), (char)replacement, (StringBuilder)new StringBuilder((int)(last + 1 - first)), (boolean)false);
        return string;
    }

    private String finishCollapseFrom(CharSequence sequence, int start, int end, char replacement, StringBuilder builder, boolean inMatchingGroup) {
        int i = start;
        while (i < end) {
            char c = sequence.charAt((int)i);
            if (this.matches((char)c)) {
                if (!inMatchingGroup) {
                    builder.append((char)replacement);
                    inMatchingGroup = true;
                }
            } else {
                builder.append((char)c);
                inMatchingGroup = false;
            }
            ++i;
        }
        return builder.toString();
    }

    @Deprecated
    @Override
    public boolean apply(Character character) {
        return this.matches((char)character.charValue());
    }

    public String toString() {
        return super.toString();
    }

    private static String showCharacter(char c) {
        String hex = "0123456789ABCDEF";
        char[] tmp = new char[]{'\\', 'u', '\u0000', '\u0000', '\u0000', '\u0000'};
        int i = 0;
        while (i < 4) {
            tmp[5 - i] = hex.charAt((int)(c & 15));
            c = (char)(c >> 4);
            ++i;
        }
        return String.copyValueOf((char[])tmp);
    }

    private static IsEither isEither(char c1, char c2) {
        return new IsEither((char)c1, (char)c2);
    }

    static /* synthetic */ String access$100(char x0) {
        return CharMatcher.showCharacter((char)x0);
    }
}

