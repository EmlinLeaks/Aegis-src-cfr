/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetUtil {
    public static StringBuilder appendResultSetSlashGStyle(StringBuilder appendTo, ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int numFields = rsmd.getColumnCount();
        int maxWidth = 0;
        String[] fieldNames = new String[numFields];
        for (int i = 0; i < numFields; ++i) {
            fieldNames[i] = rsmd.getColumnLabel((int)(i + 1));
            if (fieldNames[i].length() <= maxWidth) continue;
            maxWidth = fieldNames[i].length();
        }
        int rowCount = 1;
        while (rs.next()) {
            appendTo.append((String)"*************************** ");
            appendTo.append((int)rowCount++);
            appendTo.append((String)". row ***************************\n");
            for (int i = 0; i < numFields; ++i) {
                int leftPad = maxWidth - fieldNames[i].length();
                for (int j = 0; j < leftPad; ++j) {
                    appendTo.append((String)" ");
                }
                appendTo.append((String)fieldNames[i]);
                appendTo.append((String)": ");
                String stringVal = rs.getString((int)(i + 1));
                if (stringVal != null) {
                    appendTo.append((String)stringVal);
                } else {
                    appendTo.append((String)"NULL");
                }
                appendTo.append((String)"\n");
            }
            appendTo.append((String)"\n");
        }
        return appendTo;
    }
}

