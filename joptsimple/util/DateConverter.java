/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DateConverter
implements ValueConverter<Date> {
    private final DateFormat formatter;

    public DateConverter(DateFormat formatter) {
        if (formatter == null) {
            throw new NullPointerException((String)"illegal null formatter");
        }
        this.formatter = formatter;
    }

    public static DateConverter datePattern(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat((String)pattern);
        formatter.setLenient((boolean)false);
        return new DateConverter((DateFormat)formatter);
    }

    @Override
    public Date convert(String value) {
        ParsePosition position = new ParsePosition((int)0);
        Date date = this.formatter.parse((String)value, (ParsePosition)position);
        if (position.getIndex() == value.length()) return date;
        throw new ValueConversionException((String)this.message((String)value));
    }

    @Override
    public Class<Date> valueType() {
        return Date.class;
    }

    @Override
    public String valuePattern() {
        if (!(this.formatter instanceof SimpleDateFormat)) return "";
        String string = ((SimpleDateFormat)this.formatter).toPattern();
        return string;
    }

    private String message(String value) {
        String key;
        Object[] arguments;
        if (this.formatter instanceof SimpleDateFormat) {
            key = "with.pattern.message";
            arguments = new Object[]{value, ((SimpleDateFormat)this.formatter).toPattern()};
            return Messages.message((Locale)Locale.getDefault(), (String)"joptsimple.ExceptionMessages", DateConverter.class, (String)key, (Object[])arguments);
        }
        key = "without.pattern.message";
        arguments = new Object[]{value};
        return Messages.message((Locale)Locale.getDefault(), (String)"joptsimple.ExceptionMessages", DateConverter.class, (String)key, (Object[])arguments);
    }
}

