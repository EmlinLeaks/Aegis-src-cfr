/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions;

import com.mysql.jdbc.exceptions.MySQLTransientException;

public class MySQLTimeoutException
extends MySQLTransientException {
    static final long serialVersionUID = -789621240523230339L;

    public MySQLTimeoutException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLTimeoutException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLTimeoutException(String reason) {
        super((String)reason);
    }

    public MySQLTimeoutException() {
        super((String)"Statement cancelled due to timeout or client request");
    }
}

