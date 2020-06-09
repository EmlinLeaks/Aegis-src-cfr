/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions;

import com.mysql.jdbc.exceptions.MySQLNonTransientException;

public class MySQLQueryInterruptedException
extends MySQLNonTransientException {
    private static final long serialVersionUID = -8714521137662613517L;

    public MySQLQueryInterruptedException() {
    }

    public MySQLQueryInterruptedException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLQueryInterruptedException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLQueryInterruptedException(String reason) {
        super((String)reason);
    }
}

