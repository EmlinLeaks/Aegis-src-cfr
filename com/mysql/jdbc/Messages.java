/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "com.mysql.jdbc.LocalizedErrorMessages";
    private static final ResourceBundle RESOURCE_BUNDLE;

    public static String getString(String key) {
        if (RESOURCE_BUNDLE == null) {
            throw new RuntimeException((String)"Localized messages from resource bundle 'com.mysql.jdbc.LocalizedErrorMessages' not loaded during initialization of driver.");
        }
        try {
            if (key == null) {
                throw new IllegalArgumentException((String)"Message key can not be null");
            }
            String message = RESOURCE_BUNDLE.getString((String)key);
            if (message != null) return message;
            return "Missing error message for key '" + key + "'";
        }
        catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(String key, Object[] args) {
        return MessageFormat.format((String)Messages.getString((String)key), (Object[])args);
    }

    private Messages() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        ResourceBundle temp = null;
        try {
            try {
                temp = ResourceBundle.getBundle((String)BUNDLE_NAME, (Locale)Locale.getDefault(), (ClassLoader)Messages.class.getClassLoader());
            }
            catch (Throwable t) {
                try {
                    temp = ResourceBundle.getBundle((String)BUNDLE_NAME);
                }
                catch (Throwable t2) {
                    RuntimeException rt = new RuntimeException((String)("Can't load resource bundle due to underlying exception " + t.toString()));
                    rt.initCause((Throwable)t2);
                    throw rt;
                }
                Object var5_2 = null;
                RESOURCE_BUNDLE = temp;
                return;
            }
            Object var5_1 = null;
            RESOURCE_BUNDLE = temp;
            return;
        }
        catch (Throwable throwable) {
            Object var5_3 = null;
            RESOURCE_BUNDLE = temp;
            throw throwable;
        }
    }
}

