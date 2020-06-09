/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc;

import com.mysql.fabric.xmlrpc.base.Fault;
import com.mysql.fabric.xmlrpc.base.MethodCall;
import com.mysql.fabric.xmlrpc.base.MethodResponse;
import com.mysql.fabric.xmlrpc.base.ResponseParser;
import com.mysql.fabric.xmlrpc.exceptions.MySQLFabricException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Client {
    private URL url;
    private Map<String, String> headers = new HashMap<String, String>();

    public Client(String url) throws MalformedURLException {
        this.url = new URL((String)url);
    }

    public void setHeader(String name, String value) {
        this.headers.put((String)name, (String)value);
    }

    public void clearHeader(String name) {
        this.headers.remove((Object)name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MethodResponse execute(MethodCall methodCall) throws IOException, ParserConfigurationException, SAXException, MySQLFabricException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)this.url.openConnection();
            connection.setRequestMethod((String)"POST");
            connection.setRequestProperty((String)"User-Agent", (String)"MySQL XML-RPC");
            connection.setRequestProperty((String)"Content-Type", (String)"text/xml");
            connection.setUseCaches((boolean)false);
            connection.setDoInput((boolean)true);
            connection.setDoOutput((boolean)true);
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                connection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
            }
            String out = methodCall.toString();
            OutputStream os = connection.getOutputStream();
            os.write((byte[])out.getBytes());
            os.flush();
            os.close();
            InputStream is = connection.getInputStream();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ResponseParser saxp = new ResponseParser();
            parser.parse((InputStream)is, (DefaultHandler)saxp);
            is.close();
            MethodResponse resp = saxp.getMethodResponse();
            if (resp.getFault() != null) {
                throw new MySQLFabricException((Fault)resp.getFault());
            }
            MethodResponse methodResponse = resp;
            Object var12_11 = null;
            if (connection == null) return methodResponse;
            connection.disconnect();
            return methodResponse;
        }
        catch (Throwable throwable) {
            Object var12_12 = null;
            if (connection == null) throw throwable;
            connection.disconnect();
            throw throwable;
        }
    }
}

