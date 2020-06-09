/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.StandardLoadBalanceExceptionChecker;
import java.sql.SQLException;

public class NdbLoadBalanceExceptionChecker
extends StandardLoadBalanceExceptionChecker {
    @Override
    public boolean shouldExceptionTriggerFailover(SQLException ex) {
        if (super.shouldExceptionTriggerFailover((SQLException)ex)) return true;
        if (this.checkNdbException((SQLException)ex)) return true;
        return false;
    }

    private boolean checkNdbException(SQLException ex) {
        if (ex.getMessage().startsWith((String)"Lock wait timeout exceeded")) return true;
        if (!ex.getMessage().startsWith((String)"Got temporary error")) return false;
        if (!ex.getMessage().endsWith((String)"from NDB")) return false;
        return true;
    }
}

