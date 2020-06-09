/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ConnectionPropertiesImpl;
import java.io.PrintStream;

public class DocsConnectionPropsHelper
extends ConnectionPropertiesImpl {
    static final long serialVersionUID = -1580779062220390294L;

    public static void main(String[] args) throws Exception {
        System.out.println((String)new DocsConnectionPropsHelper().exposeAsXml());
    }
}

