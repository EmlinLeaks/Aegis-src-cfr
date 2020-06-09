/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions.jdbc4;

import java.sql.SQLTransientException;

public class MySQLTransientException
extends SQLTransientException {
    static final long serialVersionUID = -1885878228558607563L;

    public MySQLTransientException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLTransientException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLTransientException(String reason) {
        super((String)reason);
    }

    public MySQLTransientException() {
    }
}

