/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.authentication;

import com.mysql.jdbc.AuthenticationPlugin;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.ExportControlled;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Security;
import com.mysql.jdbc.StringUtils;
import java.io.UnsupportedEncodingException;
import java.security.interfaces.RSAPublicKey;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Sha256PasswordPlugin
implements AuthenticationPlugin {
    public static String PLUGIN_NAME = "sha256_password";
    protected Connection connection;
    protected String password = null;
    protected String seed = null;
    protected boolean publicKeyRequested = false;
    protected String publicKeyString = null;

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        this.connection = conn;
        String pkURL = this.connection.getServerRSAPublicKeyFile();
        if (pkURL == null) return;
        this.publicKeyString = Sha256PasswordPlugin.readRSAKey((Connection)this.connection, (String)pkURL);
    }

    @Override
    public void destroy() {
        this.password = null;
        this.seed = null;
        this.publicKeyRequested = false;
    }

    @Override
    public String getProtocolPluginName() {
        return PLUGIN_NAME;
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
        if (this.password == null || this.password.length() == 0 || fromServer == null) {
            Buffer bresp = new Buffer((byte[])new byte[]{0});
            toServer.add((Buffer)bresp);
            return true;
        }
        if (((MySQLConnection)this.connection).getIO().isSSLEstablished()) {
            Buffer bresp;
            try {
                bresp = new Buffer((byte[])StringUtils.getBytes((String)this.password, (String)this.connection.getPasswordCharacterEncoding()));
            }
            catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException((String)Messages.getString((String)"Sha256PasswordPlugin.3", (Object[])new Object[]{this.connection.getPasswordCharacterEncoding()}), (String)"S1000", null);
            }
            bresp.setPosition((int)bresp.getBufLength());
            int oldBufLength = bresp.getBufLength();
            bresp.writeByte((byte)0);
            bresp.setBufLength((int)(oldBufLength + 1));
            bresp.setPosition((int)0);
            toServer.add((Buffer)bresp);
            return true;
        }
        if (this.connection.getServerRSAPublicKeyFile() != null) {
            this.seed = fromServer.readString();
            Buffer bresp = new Buffer((byte[])this.encryptPassword());
            toServer.add((Buffer)bresp);
            return true;
        }
        if (!this.connection.getAllowPublicKeyRetrieval()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Sha256PasswordPlugin.2"), (String)"08001", (ExceptionInterceptor)this.connection.getExceptionInterceptor());
        }
        if (this.publicKeyRequested && fromServer.getBufLength() > 20) {
            this.publicKeyString = fromServer.readString();
            Buffer bresp = new Buffer((byte[])this.encryptPassword());
            toServer.add((Buffer)bresp);
            this.publicKeyRequested = false;
            return true;
        }
        this.seed = fromServer.readString();
        Buffer bresp = new Buffer((byte[])new byte[]{1});
        toServer.add((Buffer)bresp);
        this.publicKeyRequested = true;
        return true;
    }

    protected byte[] encryptPassword() throws SQLException {
        return this.encryptPassword((String)"RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
    }

    protected byte[] encryptPassword(String transformation) throws SQLException {
        byte[] input = null;
        try {
            byte[] arrby;
            if (this.password != null) {
                arrby = StringUtils.getBytesNullTerminated((String)this.password, (String)this.connection.getPasswordCharacterEncoding());
            } else {
                byte[] arrby2 = new byte[1];
                arrby = arrby2;
                arrby2[0] = 0;
            }
            input = arrby;
        }
        catch (UnsupportedEncodingException e) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Sha256PasswordPlugin.3", (Object[])new Object[]{this.connection.getPasswordCharacterEncoding()}), (String)"S1000", null);
        }
        byte[] mysqlScrambleBuff = new byte[input.length];
        Security.xorString((byte[])input, (byte[])mysqlScrambleBuff, (byte[])this.seed.getBytes(), (int)input.length);
        return ExportControlled.encryptWithRSAPublicKey((byte[])mysqlScrambleBuff, (RSAPublicKey)ExportControlled.decodeRSAPublicKey((String)this.publicKeyString, (ExceptionInterceptor)this.connection.getExceptionInterceptor()), (String)transformation, (ExceptionInterceptor)this.connection.getExceptionInterceptor());
    }

    /*
     * Exception decompiling
     */
    private static String readRSAKey(Connection connection, String pkPath) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 2[TRYBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void reset() {
    }
}

