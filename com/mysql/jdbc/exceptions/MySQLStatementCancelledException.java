/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions;

import com.mysql.jdbc.exceptions.MySQLNonTransientException;

public class MySQLStatementCancelledException
extends MySQLNonTransientException {
    static final long serialVersionUID = -8762717748377197378L;

    public MySQLStatementCancelledException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLStatementCancelledException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLStatementCancelledException(String reason) {
        super((String)reason);
    }

    public MySQLStatementCancelledException() {
        super((String)"Statement cancelled due to client request");
    }
}

