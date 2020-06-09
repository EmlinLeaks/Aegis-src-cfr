/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions;

import com.mysql.jdbc.exceptions.DeadlockTimeoutRollbackMarker;
import com.mysql.jdbc.exceptions.MySQLTransientException;

public class MySQLTransactionRollbackException
extends MySQLTransientException
implements DeadlockTimeoutRollbackMarker {
    static final long serialVersionUID = 6034999468737801730L;

    public MySQLTransactionRollbackException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLTransactionRollbackException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLTransactionRollbackException(String reason) {
        super((String)reason);
    }

    public MySQLTransactionRollbackException() {
    }
}

