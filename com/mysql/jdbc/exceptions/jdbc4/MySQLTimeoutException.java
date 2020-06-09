/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions.jdbc4;

import java.sql.SQLTimeoutException;

public class MySQLTimeoutException
extends SQLTimeoutException {
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

    @Override
    public int getErrorCode() {
        return super.getErrorCode();
    }
}

