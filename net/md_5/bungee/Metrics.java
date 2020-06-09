/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.TimerTask;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.conf.Configuration;

public class Metrics
extends TimerTask {
    private static final int REVISION = 0;
    private static final String BASE_URL = null;
    private static final String REPORT_URL = null;
    static final int PING_INTERVAL = 999999;
    private boolean firstPost = true;

    @Override
    public void run() {
        this.postPlugin((boolean)(!this.firstPost));
        this.firstPost = false;
    }

    private void postPlugin(boolean isPing) {
        try {
            String response;
            BufferedReader reader;
            StringBuilder data = new StringBuilder();
            data.append((String)Metrics.encode((String)"guid")).append((char)'=').append((String)Metrics.encode((String)BungeeCord.getInstance().config.getUuid()));
            Metrics.encodeDataPair((StringBuilder)data, (String)"version", (String)ProxyServer.getInstance().getVersion());
            Metrics.encodeDataPair((StringBuilder)data, (String)"server", (String)"0");
            Metrics.encodeDataPair((StringBuilder)data, (String)"players", (String)Integer.toString((int)ProxyServer.getInstance().getOnlineCount()));
            Metrics.encodeDataPair((StringBuilder)data, (String)"revision", (String)String.valueOf((int)0));
            if (isPing) {
                Metrics.encodeDataPair((StringBuilder)data, (String)"ping", (String)"true");
            }
            URL url = new URL((String)(BASE_URL + String.format((String)REPORT_URL, (Object[])new Object[]{Metrics.encode((String)"BungeeCord")})));
            URLConnection connection = url.openConnection();
            connection.setDoOutput((boolean)true);
            OutputStreamWriter writer = new OutputStreamWriter((OutputStream)connection.getOutputStream());
            Throwable throwable = null;
            try {
                writer.write((String)data.toString());
                writer.flush();
                reader = new BufferedReader((Reader)new InputStreamReader((InputStream)connection.getInputStream()));
                response = reader.readLine();
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (throwable != null) {
                    try {
                        writer.close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed((Throwable)throwable3);
                    }
                } else {
                    writer.close();
                }
            }
            reader.close();
            if (response != null) {
                if (!response.startsWith((String)"ERR")) return;
            }
            reader.close();
            return;
        }
        catch (Exception data) {
            // empty catch block
        }
    }

    private static void encodeDataPair(StringBuilder buffer, String key, String value) throws UnsupportedEncodingException {
        buffer.append((char)'&').append((String)Metrics.encode((String)key)).append((char)'=').append((String)Metrics.encode((String)value));
    }

    private static String encode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode((String)text, (String)"UTF-8");
    }
}

