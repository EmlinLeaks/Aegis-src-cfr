/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.proto.xmlrpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DigestAuthentication {
    private static Random random = new Random();

    public static String getChallengeHeader(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)new URL((String)url).openConnection();
        conn.setDoOutput((boolean)true);
        conn.getOutputStream().close();
        try {
            conn.getInputStream().close();
            return null;
        }
        catch (IOException ex) {
            if (401 == conn.getResponseCode()) {
                String hdr = conn.getHeaderField((String)"WWW-Authenticate");
                if (hdr == null) return null;
                if ("".equals((Object)hdr)) return null;
                return hdr;
            }
            if (400 != conn.getResponseCode()) throw ex;
            throw new IOException((String)"Fabric returns status 400. If authentication is disabled on the Fabric node, omit the `fabricUsername' and `fabricPassword' properties from your connection.");
        }
    }

    public static String calculateMD5RequestDigest(String uri, String username, String password, String realm, String nonce, String nc, String cnonce, String qop) {
        String reqA1 = username + ":" + realm + ":" + password;
        String reqA2 = "POST:" + uri;
        String hashA1 = DigestAuthentication.checksumMD5((String)reqA1);
        String hashA2 = DigestAuthentication.checksumMD5((String)reqA2);
        return DigestAuthentication.digestMD5((String)hashA1, (String)(nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + hashA2));
    }

    private static String checksumMD5(String data) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance((String)"MD5");
            return DigestAuthentication.hexEncode((byte[])md5.digest((byte[])data.getBytes()));
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException((String)"Unable to create MD5 instance", (Throwable)ex);
        }
    }

    private static String digestMD5(String secret, String data) {
        return DigestAuthentication.checksumMD5((String)(secret + ":" + data));
    }

    private static String hexEncode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < data.length) {
            sb.append((String)String.format((String)"%02x", (Object[])new Object[]{Byte.valueOf((byte)data[i])}));
            ++i;
        }
        return sb.toString();
    }

    public static String serializeDigestResponse(Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder((String)"Digest ");
        boolean prefixComma = false;
        Iterator<Map.Entry<String, String>> i$ = paramMap.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<String, String> entry = i$.next();
            if (!prefixComma) {
                prefixComma = true;
            } else {
                sb.append((String)", ");
            }
            sb.append((String)entry.getKey());
            sb.append((String)"=");
            sb.append((String)entry.getValue());
        }
        return sb.toString();
    }

    public static Map<String, String> parseDigestChallenge(String headerValue) {
        if (!headerValue.startsWith((String)"Digest ")) {
            throw new IllegalArgumentException((String)"Header is not a digest challenge");
        }
        String params = headerValue.substring((int)7);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String[] arr$ = params.split((String)",\\s*");
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String param = arr$[i$];
            String[] pieces = param.split((String)"=");
            paramMap.put((String)pieces[0], (String)pieces[1].replaceAll((String)"^\"(.*)\"$", (String)"$1"));
            ++i$;
        }
        return paramMap;
    }

    public static String generateCnonce(String nonce, String nc) {
        byte[] buf = new byte[8];
        random.nextBytes((byte[])buf);
        for (int i = 0; i < 8; ++i) {
            buf[i] = (byte)(32 + buf[i] % 95);
        }
        String combo = String.format((String)"%s:%s:%s:%s", (Object[])new Object[]{nonce, nc, new Date().toGMTString(), new String((byte[])buf)});
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance((String)"SHA-1");
            return DigestAuthentication.hexEncode((byte[])sha1.digest((byte[])combo.getBytes()));
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException((String)"Unable to create SHA-1 instance", (Throwable)ex);
        }
    }

    private static String quoteParam(String param) {
        if (param.contains((CharSequence)"\"")) throw new IllegalArgumentException((String)"Invalid character in parameter");
        if (!param.contains((CharSequence)"'")) return "\"" + param + "\"";
        throw new IllegalArgumentException((String)"Invalid character in parameter");
    }

    public static String generateAuthorizationHeader(Map<String, String> digestChallenge, String username, String password) {
        String nonce = digestChallenge.get((Object)"nonce");
        String nc = "00000001";
        String cnonce = DigestAuthentication.generateCnonce((String)nonce, (String)nc);
        String qop = "auth";
        String uri = "/RPC2";
        String realm = digestChallenge.get((Object)"realm");
        String opaque = digestChallenge.get((Object)"opaque");
        String requestDigest = DigestAuthentication.calculateMD5RequestDigest((String)uri, (String)username, (String)password, (String)realm, (String)nonce, (String)nc, (String)cnonce, (String)qop);
        HashMap<String, String> digestResponseMap = new HashMap<String, String>();
        digestResponseMap.put("algorithm", "MD5");
        digestResponseMap.put("username", DigestAuthentication.quoteParam((String)username));
        digestResponseMap.put("realm", DigestAuthentication.quoteParam((String)realm));
        digestResponseMap.put("nonce", DigestAuthentication.quoteParam((String)nonce));
        digestResponseMap.put("uri", DigestAuthentication.quoteParam((String)uri));
        digestResponseMap.put("qop", qop);
        digestResponseMap.put("nc", nc);
        digestResponseMap.put("cnonce", DigestAuthentication.quoteParam((String)cnonce));
        digestResponseMap.put("response", DigestAuthentication.quoteParam((String)requestDigest));
        digestResponseMap.put("opaque", DigestAuthentication.quoteParam((String)opaque));
        return DigestAuthentication.serializeDigestResponse(digestResponseMap);
    }
}

