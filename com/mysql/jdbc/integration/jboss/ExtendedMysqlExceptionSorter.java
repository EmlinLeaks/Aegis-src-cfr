/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter
 */
package com.mysql.jdbc.integration.jboss;

import java.sql.SQLException;
import org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter;

public final class ExtendedMysqlExceptionSorter
extends MySQLExceptionSorter {
    static final long serialVersionUID = -2454582336945931069L;

    public boolean isExceptionFatal(SQLException ex) {
        String sqlState = ex.getSQLState();
        if (sqlState == null) return super.isExceptionFatal((SQLException)ex);
        if (!sqlState.startsWith((String)"08")) return super.isExceptionFatal((SQLException)ex);
        return true;
    }
}

