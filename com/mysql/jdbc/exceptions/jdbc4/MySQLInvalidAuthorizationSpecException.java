/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions.jdbc4;

import java.sql.SQLInvalidAuthorizationSpecException;

public class MySQLInvalidAuthorizationSpecException
extends SQLInvalidAuthorizationSpecException {
    static final long serialVersionUID = 6878889837492500030L;

    public MySQLInvalidAuthorizationSpecException() {
    }

    public MySQLInvalidAuthorizationSpecException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLInvalidAuthorizationSpecException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLInvalidAuthorizationSpecException(String reason) {
        super((String)reason);
    }
}

