/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RegexMatcher
implements ValueConverter<String> {
    private final Pattern pattern;

    public RegexMatcher(String pattern, int flags) {
        this.pattern = Pattern.compile((String)pattern, (int)flags);
    }

    public static ValueConverter<String> regex(String pattern) {
        return new RegexMatcher((String)pattern, (int)0);
    }

    @Override
    public String convert(String value) {
        if (this.pattern.matcher((CharSequence)value).matches()) return value;
        this.raiseValueConversionFailure((String)value);
        return value;
    }

    @Override
    public Class<String> valueType() {
        return String.class;
    }

    @Override
    public String valuePattern() {
        return this.pattern.pattern();
    }

    private void raiseValueConversionFailure(String value) {
        String message = Messages.message((Locale)Locale.getDefault(), (String)"joptsimple.ExceptionMessages", RegexMatcher.class, (String)"message", (Object[])new Object[]{value, this.pattern.pattern()});
        throw new ValueConversionException((String)message);
    }
}

