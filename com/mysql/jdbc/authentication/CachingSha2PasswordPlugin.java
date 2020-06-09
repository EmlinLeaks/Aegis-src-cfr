/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.authentication;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Security;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.authentication.CachingSha2PasswordPlugin;
import com.mysql.jdbc.authentication.Sha256PasswordPlugin;
import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CachingSha2PasswordPlugin
extends Sha256PasswordPlugin {
    public static String PLUGIN_NAME = "caching_sha2_password";
    private AuthStage stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        super.init((Connection)conn, (Properties)props);
        this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
    }

    @Override
    public void destroy() {
        this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
        super.destroy();
    }

    @Override
    public String getProtocolPluginName() {
        return PLUGIN_NAME;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException {
        toServer.clear();
        if (this.password == null || this.password.length() == 0 || fromServer == null) {
            bresp = new Buffer((byte[])new byte[]{0});
            toServer.add((Buffer)bresp);
            return true;
        }
        if (this.stage == AuthStage.FAST_AUTH_SEND_SCRAMBLE) {
            this.seed = fromServer.readString();
            try {
                toServer.add((Buffer)new Buffer((byte[])Security.scrambleCachingSha2((byte[])StringUtils.getBytes((String)this.password, (String)this.connection.getPasswordCharacterEncoding()), (byte[])this.seed.getBytes())));
            }
            catch (DigestException e) {
                throw SQLError.createSQLException((String)e.getMessage(), (String)"S1000", (Throwable)e, null);
            }
            catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException((String)e.getMessage(), (String)"S1000", (Throwable)e, null);
            }
            this.stage = AuthStage.FAST_AUTH_READ_RESULT;
            return true;
        }
        if (this.stage == AuthStage.FAST_AUTH_READ_RESULT) {
            fastAuthResult = fromServer.getByteBuffer()[0];
            switch (fastAuthResult) {
                case 3: {
                    this.stage = AuthStage.FAST_AUTH_COMPLETE;
                    return true;
                }
                case 4: {
                    this.stage = AuthStage.FULL_AUTH;
                    ** break;
                }
            }
            throw SQLError.createSQLException((String)"Unknown server response after fast auth.", (String)"08001", (ExceptionInterceptor)this.connection.getExceptionInterceptor());
        }
lbl29: // 3 sources:
        if (((MySQLConnection)this.connection).getIO().isSSLEstablished()) {
            try {
                bresp = new Buffer((byte[])StringUtils.getBytes((String)this.password, (String)this.connection.getPasswordCharacterEncoding()));
            }
            catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException((String)Messages.getString((String)"Sha256PasswordPlugin.3", (Object[])new Object[]{this.connection.getPasswordCharacterEncoding()}), (String)"S1000", null);
            }
            bresp.setPosition((int)bresp.getBufLength());
            oldBufLength = bresp.getBufLength();
            bresp.writeByte((byte)0);
            bresp.setBufLength((int)(oldBufLength + 1));
            bresp.setPosition((int)0);
            toServer.add((Buffer)bresp);
            return true;
        }
        if (this.connection.getServerRSAPublicKeyFile() != null) {
            bresp = new Buffer((byte[])this.encryptPassword());
            toServer.add((Buffer)bresp);
            return true;
        }
        if (!this.connection.getAllowPublicKeyRetrieval()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Sha256PasswordPlugin.2"), (String)"08001", (ExceptionInterceptor)this.connection.getExceptionInterceptor());
        }
        if (this.publicKeyRequested && fromServer.getBufLength() > 20) {
            this.publicKeyString = fromServer.readString();
            bresp = new Buffer((byte[])this.encryptPassword());
            toServer.add((Buffer)bresp);
            this.publicKeyRequested = false;
            return true;
        }
        bresp = new Buffer((byte[])new byte[]{2});
        toServer.add((Buffer)bresp);
        this.publicKeyRequested = true;
        return true;
    }

    @Override
    protected byte[] encryptPassword() throws SQLException {
        if (!this.connection.versionMeetsMinimum((int)8, (int)0, (int)5)) return super.encryptPassword((String)"RSA/ECB/PKCS1Padding");
        return super.encryptPassword();
    }

    @Override
    public void reset() {
        this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
    }
}

