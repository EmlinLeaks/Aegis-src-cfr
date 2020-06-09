/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.authentication;

import com.mysql.jdbc.AuthenticationPlugin;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Security;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MysqlNativePasswordPlugin
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
        return "mysql_native_password";
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
        try {
            toServer.clear();
            Buffer bresp = null;
            String pwd = this.password;
            bresp = fromServer == null || pwd == null || pwd.length() == 0 ? new Buffer((byte[])new byte[0]) : new Buffer((byte[])Security.scramble411((String)pwd, (String)fromServer.readString(), (String)this.connection.getPasswordCharacterEncoding()));
            toServer.add((Buffer)bresp);
            return true;
        }
        catch (NoSuchAlgorithmException nse) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.91") + Messages.getString((String)"MysqlIO.92")), (String)"S1000", null);
        }
        catch (UnsupportedEncodingException e) {
            throw SQLError.createSQLException((String)Messages.getString((String)"MysqlNativePasswordPlugin.1", (Object[])new Object[]{this.connection.getPasswordCharacterEncoding()}), (String)"S1000", null);
        }
    }

    @Override
    public void reset() {
    }
}

