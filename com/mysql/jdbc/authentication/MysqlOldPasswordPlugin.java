/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.authentication;

import com.mysql.jdbc.AuthenticationPlugin;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MysqlOldPasswordPlugin
implements AuthenticationPlugin {
    private Connection connection;
    private String password = null;

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        this.connection = conn;
    }

    @Override
    public void destroy() {
        this.password = null;
    }

    @Override
    public String getProtocolPluginName() {
        return "mysql_old_password";
    }

    @Override
    public boolean requiresConfidentiality() {
        return false;
    }

    @Override
    public boolean isReusable() {
        return true;
    }

    @Override
    public void setAuthenticationParameters(String user, String password) {
        this.password = password;
    }

    @Override
    public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException {
        toServer.clear();
        Buffer bresp = null;
        String pwd = this.password;
        if (fromServer == null || pwd == null || pwd.length() == 0) {
            bresp = new Buffer((byte[])new byte[0]);
        } else {
            bresp = new Buffer((byte[])StringUtils.getBytes((String)Util.newCrypt((String)pwd, (String)fromServer.readString().substring((int)0, (int)8), (String)this.connection.getPasswordCharacterEncoding())));
            bresp.setPosition((int)bresp.getBufLength());
            int oldBufLength = bresp.getBufLength();
            bresp.writeByte((byte)0);
            bresp.setBufLength((int)(oldBufLength + 1));
            bresp.setPosition((int)0);
        }
        toServer.add((Buffer)bresp);
        return true;
    }

    @Override
    public void reset() {
    }
}

