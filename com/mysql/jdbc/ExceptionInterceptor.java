/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Extension;
import java.sql.SQLException;

public interface ExceptionInterceptor
extends Extension {
    public SQLException interceptException(SQLException var1, Connection var2);
}

