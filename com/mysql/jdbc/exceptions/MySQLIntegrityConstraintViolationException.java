/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions;

import com.mysql.jdbc.exceptions.MySQLNonTransientException;

public class MySQLIntegrityConstraintViolationException
extends MySQLNonTransientException {
    static final long serialVersionUID = -5528363270635808904L;

    public MySQLIntegrityConstraintViolationException() {
    }

    public MySQLIntegrityConstraintViolationException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLIntegrityConstraintViolationException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLIntegrityConstraintViolationException(String reason) {
        super((String)reason);
    }
}

