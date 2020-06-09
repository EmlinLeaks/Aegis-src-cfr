/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import jline.internal.Log;
import jline.internal.Preconditions;
import jline.internal.Urls;

public class Configuration {
    public static final String JLINE_CONFIGURATION = "jline.configuration";
    public static final String JLINE_RC = ".jline.rc";
    private static volatile Properties properties;

    private static Properties initProperties() {
        URL url = Configuration.determineUrl();
        Properties props = new Properties();
        try {
            Configuration.loadProperties((URL)url, (Properties)props);
            return props;
        }
        catch (IOException e) {
            Log.debug((Object[])new Object[]{"Unable to read configuration from: ", url, e});
        }
        return props;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadProperties(URL url, Properties props) throws IOException {
        Log.debug((Object[])new Object[]{"Loading properties from: ", url});
        InputStream input = url.openStream();
        try {
            props.load((InputStream)new BufferedInputStream((InputStream)input));
        }
        finally {
            try {
                input.close();
            }
            catch (IOException e) {}
        }
        if (!Log.DEBUG) return;
        Log.debug((Object[])new Object[]{"Loaded properties:"});
        Iterator<Map.Entry<K, V>> i$ = props.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            Log.debug((Object[])new Object[]{"  ", entry.getKey(), "=", entry.getValue()});
        }
    }

    private static URL determineUrl() {
        String tmp = System.getProperty((String)JLINE_CONFIGURATION);
        if (tmp != null) {
            return Urls.create((String)tmp);
        }
        File file = new File((File)Configuration.getUserHome(), (String)JLINE_RC);
        return Urls.create((File)file);
    }

    public static void reset() {
        Log.debug((Object[])new Object[]{"Resetting"});
        properties = null;
        Configuration.getProperties();
    }

    public static Properties getProperties() {
        if (properties != null) return properties;
        properties = Configuration.initProperties();
        return properties;
    }

    public static String getString(String name, String defaultValue) {
        Preconditions.checkNotNull(name);
        String value = System.getProperty((String)name);
        if (value != null) return value;
        value = Configuration.getProperties().getProperty((String)name);
        if (value != null) return value;
        return defaultValue;
    }

    public static String getString(String name) {
        return Configuration.getString((String)name, null);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        String value = Configuration.getString((String)name);
        if (value == null) {
            return defaultValue;
        }
        if (value.length() == 0) return true;
        if (value.equalsIgnoreCase((String)"1")) return true;
        if (value.equalsIgnoreCase((String)"on")) return true;
        if (value.equalsIgnoreCase((String)"true")) return true;
        return false;
    }

    public static int getInteger(String name, int defaultValue) {
        String str = Configuration.getString((String)name);
        if (str != null) return Integer.parseInt((String)str);
        return defaultValue;
    }

    public static long getLong(String name, long defaultValue) {
        String str = Configuration.getString((String)name);
        if (str != null) return Long.parseLong((String)str);
        return defaultValue;
    }

    public static String getLineSeparator() {
        return System.getProperty((String)"line.separator");
    }

    public static File getUserHome() {
        return new File((String)System.getProperty((String)"user.home"));
    }

    public static String getOsName() {
        return System.getProperty((String)"os.name").toLowerCase();
    }

    public static boolean isWindows() {
        return Configuration.getOsName().startsWith((String)"windows");
    }

    public static String getFileEncoding() {
        return System.getProperty((String)"file.encoding");
    }

    public static String getEncoding() {
        String envEncoding = Configuration.extractEncodingFromCtype((String)System.getenv((String)"LC_CTYPE"));
        if (envEncoding == null) return System.getProperty((String)"input.encoding", (String)Charset.defaultCharset().name());
        return envEncoding;
    }

    static String extractEncodingFromCtype(String ctype) {
        if (ctype == null) return null;
        if (ctype.indexOf((int)46) <= 0) return null;
        String encodingAndModifier = ctype.substring((int)(ctype.indexOf((int)46) + 1));
        if (encodingAndModifier.indexOf((int)64) <= 0) return encodingAndModifier;
        return encodingAndModifier.substring((int)0, (int)encodingAndModifier.indexOf((int)64));
    }
}

