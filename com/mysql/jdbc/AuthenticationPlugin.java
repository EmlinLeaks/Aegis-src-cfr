/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.Extension;
import java.sql.SQLException;
import java.util.List;

public interface AuthenticationPlugin
extends Extension {
    public String getProtocolPluginName();

    public boolean requiresConfidentiality();

    public boolean isReusable();

    public void setAuthenticationParameters(String var1, String var2);

    public boolean nextAuthenticationStep(Buffer var1, List<Buffer> var2) throws SQLException;

    public void reset();
}

