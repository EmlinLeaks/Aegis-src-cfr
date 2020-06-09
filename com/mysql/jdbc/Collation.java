/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CharsetMapping;
import com.mysql.jdbc.MysqlCharset;
import java.util.Map;

class Collation {
    public final int index;
    public final String collationName;
    public final int priority;
    public final MysqlCharset mysqlCharset;

    public Collation(int index, String collationName, int priority, String charsetName) {
        this.index = index;
        this.collationName = collationName;
        this.priority = priority;
        this.mysqlCharset = CharsetMapping.CHARSET_NAME_TO_CHARSET.get((Object)charsetName);
    }

    public String toString() {
        StringBuilder asString = new StringBuilder();
        asString.append((String)"[");
        asString.append((String)"index=");
        asString.append((int)this.index);
        asString.append((String)",collationName=");
        asString.append((String)this.collationName);
        asString.append((String)",charsetName=");
        asString.append((String)this.mysqlCharset.charsetName);
        asString.append((String)",javaCharsetName=");
        asString.append((String)this.mysqlCharset.getMatchingJavaEncoding(null));
        asString.append((String)"]");
        return asString.toString();
    }
}

