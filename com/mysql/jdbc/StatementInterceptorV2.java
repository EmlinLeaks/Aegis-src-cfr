/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import java.sql.SQLException;
import java.util.Properties;

public interface StatementInterceptorV2
extends Extension {
    @Override
    public void init(Connection var1, Properties var2) throws SQLException;

    public ResultSetInternalMethods preProcess(String var1, Statement var2, Connection var3) throws SQLException;

    public boolean executeTopLevelOnly();

    @Override
    public void destroy();

    public ResultSetInternalMethods postProcess(String var1, Statement var2, ResultSetInternalMethods var3, Connection var4, int var5, boolean var6, boolean var7, SQLException var8) throws SQLException;
}

