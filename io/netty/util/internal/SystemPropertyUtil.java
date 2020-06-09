/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class SystemPropertyUtil {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SystemPropertyUtil.class);

    public static boolean contains(String key) {
        if (SystemPropertyUtil.get((String)key) == null) return false;
        return true;
    }

    public static String get(String key) {
        return SystemPropertyUtil.get((String)key, null);
    }

    public static String get(String key, String def) {
        if (key == null) {
            throw new NullPointerException((String)"key");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException((String)"key must not be empty.");
        }
        String value = null;
        try {
            value = System.getSecurityManager() == null ? System.getProperty((String)key) : AccessController.doPrivileged(new PrivilegedAction<String>((String)key){
                final /* synthetic */ String val$key;
                {
                    this.val$key = string;
                }

                public String run() {
                    return System.getProperty((String)this.val$key);
                }
            });
        }
        catch (SecurityException e) {
            logger.warn((String)"Unable to retrieve a system property '{}'; default values will be used.", (Object)key, (Object)e);
        }
        if (value != null) return value;
        return def;
    }

    public static boolean getBoolean(String key, boolean def) {
        String value = SystemPropertyUtil.get((String)key);
        if (value == null) {
            return def;
        }
        if ((value = value.trim().toLowerCase()).isEmpty()) {
            return def;
        }
        if ("true".equals((Object)value)) return true;
        if ("yes".equals((Object)value)) return true;
        if ("1".equals((Object)value)) {
            return true;
        }
        if ("false".equals((Object)value)) return false;
        if ("no".equals((Object)value)) return false;
        if ("0".equals((Object)value)) {
            return false;
        }
        logger.warn((String)"Unable to parse the boolean system property '{}':{} - using the default value: {}", (Object[])new Object[]{key, value, Boolean.valueOf((boolean)def)});
        return def;
    }

    public static int getInt(String key, int def) {
        String value = SystemPropertyUtil.get((String)key);
        if (value == null) {
            return def;
        }
        value = value.trim();
        try {
            return Integer.parseInt((String)value);
        }
        catch (Exception exception) {
            logger.warn((String)"Unable to parse the integer system property '{}':{} - using the default value: {}", (Object[])new Object[]{key, value, Integer.valueOf((int)def)});
            return def;
        }
    }

    public static long getLong(String key, long def) {
        String value = SystemPropertyUtil.get((String)key);
        if (value == null) {
            return def;
        }
        value = value.trim();
        try {
            return Long.parseLong((String)value);
        }
        catch (Exception exception) {
            logger.warn((String)"Unable to parse the long integer system property '{}':{} - using the default value: {}", (Object[])new Object[]{key, value, Long.valueOf((long)def)});
            return def;
        }
    }

    private SystemPropertyUtil() {
    }
}

