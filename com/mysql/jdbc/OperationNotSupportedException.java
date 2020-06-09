/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Messages;
import java.sql.SQLException;

class OperationNotSupportedException
extends SQLException {
    static final long serialVersionUID = 474918612056813430L;

    OperationNotSupportedException() {
        super((String)Messages.getString((String)"RowDataDynamic.3"), (String)"S1009");
    }
}

