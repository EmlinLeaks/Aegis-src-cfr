/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions.jdbc4;

import java.sql.SQLDataException;

public class MySQLDataException
extends SQLDataException {
    static final long serialVersionUID = 4317904269797988676L;

    public MySQLDataException() {
    }

    public MySQLDataException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLDataException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLDataException(String reason) {
        super((String)reason);
    }
}

