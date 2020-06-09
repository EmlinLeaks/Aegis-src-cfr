/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions.jdbc4;

import java.sql.SQLIntegrityConstraintViolationException;

public class MySQLIntegrityConstraintViolationException
extends SQLIntegrityConstraintViolationException {
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

