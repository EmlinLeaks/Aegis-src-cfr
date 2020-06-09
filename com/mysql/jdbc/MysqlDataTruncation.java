/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import java.sql.DataTruncation;

public class MysqlDataTruncation
extends DataTruncation {
    static final long serialVersionUID = 3263928195256986226L;
    private String message;
    private int vendorErrorCode;

    public MysqlDataTruncation(String message, int index, boolean parameter, boolean read, int dataSize, int transferSize, int vendorErrorCode) {
        super((int)index, (boolean)parameter, (boolean)read, (int)dataSize, (int)transferSize);
        this.message = message;
        this.vendorErrorCode = vendorErrorCode;
    }

    @Override
    public int getErrorCode() {
        return this.vendorErrorCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ": " + this.message;
    }
}

