/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import java.sql.SQLException;

public interface Wrapper {
    public <T> T unwrap(Class<T> var1) throws SQLException;

    public boolean isWrapperFor(Class<?> var1) throws SQLException;
}

