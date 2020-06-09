/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.util;

import com.mysql.jdbc.ConnectionPropertiesImpl;
import java.io.PrintStream;
import java.sql.SQLException;

public class PropertiesDocGenerator
extends ConnectionPropertiesImpl {
    static final long serialVersionUID = -4869689139143855383L;

    public static void main(String[] args) throws SQLException {
        System.out.println((String)new PropertiesDocGenerator().exposeAsXml());
    }
}

