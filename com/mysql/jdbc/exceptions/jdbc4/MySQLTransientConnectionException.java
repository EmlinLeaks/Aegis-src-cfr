/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions.jdbc4;

import java.sql.SQLTransientConnectionException;

public class MySQLTransientConnectionException
extends SQLTransientConnectionException {
    static final long serialVersionUID = 8699144578759941201L;

    public MySQLTransientConnectionException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLTransientConnectionException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLTransientConnectionException(String reason) {
        super((String)reason);
    }

    public MySQLTransientConnectionException() {
    }
}

