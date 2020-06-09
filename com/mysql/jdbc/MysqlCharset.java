/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

class MysqlCharset {
    public final String charsetName;
    public final int mblen;
    public final int priority;
    public final List<String> javaEncodingsUc = new ArrayList<String>();
    public int major = 4;
    public int minor = 1;
    public int subminor = 0;

    public MysqlCharset(String charsetName, int mblen, int priority, String[] javaEncodings) {
        this.charsetName = charsetName;
        this.mblen = mblen;
        this.priority = priority;
        for (int i = 0; i < javaEncodings.length; ++i) {
            String encoding = javaEncodings[i];
            try {
                Charset cs = Charset.forName((String)encoding);
                this.addEncodingMapping((String)cs.name());
                Set<String> als = cs.aliases();
                Iterator<String> ali = als.iterator();
                while (ali.hasNext()) {
                    this.addEncodingMapping((String)ali.next());
                }
                continue;
            }
            catch (Exception e) {
                if (mblen != 1) continue;
                this.addEncodingMapping((String)encoding);
            }
        }
        if (this.javaEncodingsUc.size() != 0) return;
        if (mblen > 1) {
            this.addEncodingMapping((String)"UTF-8");
            return;
        }
        this.addEncodingMapping((String)"Cp1252");
    }

    private void addEncodingMapping(String encoding) {
        String encodingUc = encoding.toUpperCase((Locale)Locale.ENGLISH);
        if (this.javaEncodingsUc.contains((Object)encodingUc)) return;
        this.javaEncodingsUc.add((String)encodingUc);
    }

    public MysqlCharset(String charsetName, int mblen, int priority, String[] javaEncodings, int major, int minor) {
        this((String)charsetName, (int)mblen, (int)priority, (String[])javaEncodings);
        this.major = major;
        this.minor = minor;
    }

    public MysqlCharset(String charsetName, int mblen, int priority, String[] javaEncodings, int major, int minor, int subminor) {
        this((String)charsetName, (int)mblen, (int)priority, (String[])javaEncodings);
        this.major = major;
        this.minor = minor;
        this.subminor = subminor;
    }

    public String toString() {
        StringBuilder asString = new StringBuilder();
        asString.append((String)"[");
        asString.append((String)"charsetName=");
        asString.append((String)this.charsetName);
        asString.append((String)",mblen=");
        asString.append((int)this.mblen);
        asString.append((String)"]");
        return asString.toString();
    }

    boolean isOkayForVersion(Connection conn) throws SQLException {
        return conn.versionMeetsMinimum((int)this.major, (int)this.minor, (int)this.subminor);
    }

    String getMatchingJavaEncoding(String javaEncoding) {
        if (javaEncoding == null) return this.javaEncodingsUc.get((int)0);
        if (!this.javaEncodingsUc.contains((Object)javaEncoding.toUpperCase((Locale)Locale.ENGLISH))) return this.javaEncodingsUc.get((int)0);
        return javaEncoding;
    }
}

