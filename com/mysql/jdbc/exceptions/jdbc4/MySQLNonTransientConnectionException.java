/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions.jdbc4;

import java.sql.SQLNonTransientConnectionException;

public class MySQLNonTransientConnectionException
extends SQLNonTransientConnectionException {
    static final long serialVersionUID = -3050543822763367670L;

    public MySQLNonTransientConnectionException() {
    }

    public MySQLNonTransientConnectionException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLNonTransientConnectionException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLNonTransientConnectionException(String reason) {
        super((String)reason);
    }
}

