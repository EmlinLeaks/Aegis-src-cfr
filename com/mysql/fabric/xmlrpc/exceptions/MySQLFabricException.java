/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.exceptions;

import com.mysql.fabric.xmlrpc.base.Fault;
import com.mysql.fabric.xmlrpc.base.Member;
import com.mysql.fabric.xmlrpc.base.Struct;
import com.mysql.fabric.xmlrpc.base.Value;
import java.sql.SQLException;
import java.util.List;

public class MySQLFabricException
extends SQLException {
    static final long serialVersionUID = -8776763137552613517L;

    public MySQLFabricException() {
    }

    public MySQLFabricException(Fault fault) {
        super((String)((String)((Struct)fault.getValue().getValue()).getMember().get((int)1).getValue().getValue()), (String)"", (int)((Integer)((Struct)fault.getValue().getValue()).getMember().get((int)0).getValue().getValue()).intValue());
    }

    public MySQLFabricException(String reason, String SQLState, int vendorCode) {
        super((String)reason, (String)SQLState, (int)vendorCode);
    }

    public MySQLFabricException(String reason, String SQLState) {
        super((String)reason, (String)SQLState);
    }

    public MySQLFabricException(String reason) {
        super((String)reason);
    }
}

