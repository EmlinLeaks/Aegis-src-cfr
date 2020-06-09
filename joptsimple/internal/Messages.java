/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Messages {
    private Messages() {
        throw new UnsupportedOperationException();
    }

    public static String message(Locale locale, String bundleName, Class<?> type, String key, Object ... args) {
        ResourceBundle bundle = ResourceBundle.getBundle((String)bundleName, (Locale)locale);
        String template = bundle.getString((String)(type.getName() + '.' + key));
        MessageFormat format = new MessageFormat((String)template);
        format.setLocale((Locale)locale);
        return format.format((Object)args);
    }
}

