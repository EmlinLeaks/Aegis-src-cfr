/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.internal;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jline.internal.Configuration;
import jline.internal.Log;
import jline.internal.Preconditions;

public final class TerminalLineSettings {
    public static final String JLINE_STTY = "jline.stty";
    public static final String DEFAULT_STTY = "stty";
    public static final String JLINE_SH = "jline.sh";
    public static final String DEFAULT_SH = "sh";
    private String sttyCommand = Configuration.getString((String)"jline.stty", (String)"stty");
    private String shCommand = Configuration.getString((String)"jline.sh", (String)"sh");
    private String config = this.get((String)"-a");
    private String initialConfig = this.get((String)"-g").trim();
    private long configLastFetched = System.currentTimeMillis();

    public TerminalLineSettings() throws IOException, InterruptedException {
        Log.debug((Object[])new Object[]{"Config: ", this.config});
        if (this.config.length() != 0) return;
        throw new IOException((String)MessageFormat.format((String)"Unrecognized stty code: {0}", (Object[])new Object[]{this.config}));
    }

    public String getConfig() {
        return this.config;
    }

    public void restore() throws IOException, InterruptedException {
        this.set((String)this.initialConfig);
    }

    public String get(String args) throws IOException, InterruptedException {
        return this.stty((String)args);
    }

    public void set(String args) throws IOException, InterruptedException {
        this.stty((String)args);
    }

    public int getProperty(String name) {
        long currentTime;
        block4 : {
            Preconditions.checkNotNull(name);
            currentTime = System.currentTimeMillis();
            try {
                if (this.config == null || currentTime - this.configLastFetched > 1000L) {
                    this.config = this.get((String)"-a");
                }
            }
            catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                Log.debug((Object[])new Object[]{"Failed to query stty ", name, "\n", e});
                if (this.config != null) break block4;
                return -1;
            }
        }
        if (currentTime - this.configLastFetched <= 1000L) return TerminalLineSettings.getProperty((String)name, (String)this.config);
        this.configLastFetched = currentTime;
        return TerminalLineSettings.getProperty((String)name, (String)this.config);
    }

    protected static int getProperty(String name, String stty) {
        Pattern pattern = Pattern.compile((String)(name + "\\s+=\\s+(.*?)[;\\n\\r]"));
        Matcher matcher = pattern.matcher((CharSequence)stty);
        if (matcher.find()) return TerminalLineSettings.parseControlChar((String)matcher.group((int)1));
        pattern = Pattern.compile((String)(name + "\\s+([^;]*)[;\\n\\r]"));
        matcher = pattern.matcher((CharSequence)stty);
        if (matcher.find()) return TerminalLineSettings.parseControlChar((String)matcher.group((int)1));
        pattern = Pattern.compile((String)("(\\S*)\\s+" + name));
        matcher = pattern.matcher((CharSequence)stty);
        if (matcher.find()) return TerminalLineSettings.parseControlChar((String)matcher.group((int)1));
        return -1;
    }

    private static int parseControlChar(String str) {
        if ("<undef>".equals((Object)str)) {
            return -1;
        }
        if (str.charAt((int)0) == '0') {
            return Integer.parseInt((String)str, (int)8);
        }
        if (str.charAt((int)0) >= '1' && str.charAt((int)0) <= '9') {
            return Integer.parseInt((String)str, (int)10);
        }
        if (str.charAt((int)0) == '^') {
            if (str.charAt((int)1) != '?') return str.charAt((int)1) - 64;
            return 127;
        }
        if (str.charAt((int)0) != 'M') return str.charAt((int)0);
        if (str.charAt((int)1) != '-') return str.charAt((int)0);
        if (str.charAt((int)2) != '^') return str.charAt((int)2) + 128;
        if (str.charAt((int)3) != '?') return str.charAt((int)3) - 64 + 128;
        return 255;
    }

    private String stty(String args) throws IOException, InterruptedException {
        Preconditions.checkNotNull(args);
        return this.exec((String)String.format((String)"%s %s < /dev/tty", (Object[])new Object[]{this.sttyCommand, args}));
    }

    private String exec(String cmd) throws IOException, InterruptedException {
        Preconditions.checkNotNull(cmd);
        return this.exec((String[])new String[]{this.shCommand, "-c", cmd});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String exec(String ... cmd) throws IOException, InterruptedException {
        Preconditions.checkNotNull(cmd);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Log.trace((Object[])new Object[]{"Running: ", cmd});
        Process p = Runtime.getRuntime().exec((String[])cmd);
        InputStream in = null;
        InputStream err = null;
        OutputStream out = null;
        try {
            int c;
            in = p.getInputStream();
            while ((c = in.read()) != -1) {
                bout.write((int)c);
            }
            err = p.getErrorStream();
            while ((c = err.read()) != -1) {
                bout.write((int)c);
            }
            out = p.getOutputStream();
            p.waitFor();
        }
        catch (Throwable throwable) {
            TerminalLineSettings.close((Closeable[])new Closeable[]{in, out, err});
            throw throwable;
        }
        TerminalLineSettings.close((Closeable[])new Closeable[]{in, out, err});
        String result = bout.toString();
        Log.trace((Object[])new Object[]{"Result: ", result});
        return result;
    }

    private static void close(Closeable ... closeables) {
        Closeable[] arr$ = closeables;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Closeable c = arr$[i$];
            try {
                c.close();
            }
            catch (Exception e) {
                // empty catch block
            }
            ++i$;
        }
    }
}

