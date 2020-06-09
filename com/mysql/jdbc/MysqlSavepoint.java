/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import java.rmi.server.UID;
import java.sql.SQLException;
import java.sql.Savepoint;

public class MysqlSavepoint
implements Savepoint {
    private String savepointName;
    private ExceptionInterceptor exceptionInterceptor;

    private static String getUniqueId() {
        String uidStr = new UID().toString();
        int uidLength = uidStr.length();
        StringBuilder safeString = new StringBuilder((int)(uidLength + 1));
        safeString.append((char)'_');
        int i = 0;
        while (i < uidLength) {
            char c = uidStr.charAt((int)i);
            if (Character.isLetter((char)c) || Character.isDigit((char)c)) {
                safeString.append((char)c);
            } else {
                safeString.append((char)'_');
            }
            ++i;
        }
        return safeString.toString();
    }

    MysqlSavepoint(ExceptionInterceptor exceptionInterceptor) throws SQLException {
        this((String)MysqlSavepoint.getUniqueId(), (ExceptionInterceptor)exceptionInterceptor);
    }

    MysqlSavepoint(String name, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (name == null) throw SQLError.createSQLException((String)"Savepoint name can not be NULL or empty", (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        if (name.length() == 0) {
            throw SQLError.createSQLException((String)"Savepoint name can not be NULL or empty", (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        this.savepointName = name;
        this.exceptionInterceptor = exceptionInterceptor;
    }

    @Override
    public int getSavepointId() throws SQLException {
        throw SQLError.createSQLException((String)"Only named savepoints are supported.", (String)"S1C00", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public String getSavepointName() throws SQLException {
        return this.savepointName;
    }
}

