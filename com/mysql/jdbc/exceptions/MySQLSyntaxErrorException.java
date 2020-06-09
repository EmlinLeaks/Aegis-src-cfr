/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions;

import com.mysql.jdbc.exceptions.MySQLNonTransientException;

public class MySQLSyntaxErrorException
extends MySQLNonTransientException {
    static final long serialVersionUID = 6919059513432113764L;

    public MySQLSyntaxErrorException() {
    }

    public MySQLSyntaxErrorException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLSyntaxErrorException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLSyntaxErrorException(String reason) {
        super((String)reason);
    }
}

