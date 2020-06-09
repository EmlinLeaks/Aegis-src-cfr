/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.ExportControlled;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SocketFactory;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.util.Base64Decoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class ExportControlled {
    private static final String SQL_STATE_BAD_SSL_PARAMS = "08000";
    private static final String TLSv1 = "TLSv1";
    private static final String TLSv1_1 = "TLSv1.1";
    private static final String TLSv1_2 = "TLSv1.2";
    private static final String[] TLS_PROTOCOLS = new String[]{"TLSv1.2", "TLSv1.1", "TLSv1"};

    protected static boolean enabled() {
        return true;
    }

    protected static void transformSocketToSSLSocket(MysqlIO mysqlIO) throws SQLException {
        StandardSSLSocketFactory sslFact = new StandardSSLSocketFactory((SSLSocketFactory)ExportControlled.getSSLSocketFactoryDefaultOrConfigured((MysqlIO)mysqlIO), (SocketFactory)mysqlIO.socketFactory, (Socket)mysqlIO.mysqlConnection);
        try {
            mysqlIO.mysqlConnection = sslFact.connect((String)mysqlIO.host, (int)mysqlIO.port, null);
            String[] tryProtocols = null;
            String enabledTLSProtocols = mysqlIO.connection.getEnabledTLSProtocols();
            tryProtocols = enabledTLSProtocols != null && enabledTLSProtocols.length() > 0 ? enabledTLSProtocols.split((String)"\\s*,\\s*") : (mysqlIO.versionMeetsMinimum((int)8, (int)0, (int)4) || mysqlIO.versionMeetsMinimum((int)5, (int)6, (int)0) && Util.isEnterpriseEdition((String)mysqlIO.getServerVersion()) ? TLS_PROTOCOLS : new String[]{TLSv1_1, TLSv1});
            ArrayList<String> configuredProtocols = new ArrayList<String>(Arrays.asList(tryProtocols));
            List<String> jvmSupportedProtocols = Arrays.asList(((SSLSocket)mysqlIO.mysqlConnection).getSupportedProtocols());
            ArrayList<String> allowedProtocols = new ArrayList<String>();
            for (String protocol : TLS_PROTOCOLS) {
                if (!jvmSupportedProtocols.contains((Object)protocol) || !configuredProtocols.contains((Object)protocol)) continue;
                allowedProtocols.add(protocol);
            }
            ((SSLSocket)mysqlIO.mysqlConnection).setEnabledProtocols((String[])allowedProtocols.toArray(new String[0]));
            String enabledSSLCipherSuites = mysqlIO.connection.getEnabledSSLCipherSuites();
            boolean overrideCiphers = enabledSSLCipherSuites != null && enabledSSLCipherSuites.length() > 0;
            ArrayList<String> allowedCiphers = null;
            if (overrideCiphers) {
                allowedCiphers = new ArrayList<String>();
                List<String> availableCiphers = Arrays.asList(((SSLSocket)mysqlIO.mysqlConnection).getEnabledCipherSuites());
                for (String cipher : enabledSSLCipherSuites.split((String)"\\s*,\\s*")) {
                    if (!availableCiphers.contains((Object)cipher)) continue;
                    allowedCiphers.add(cipher);
                }
            } else {
                boolean disableDHAlgorithm = false;
                if (mysqlIO.versionMeetsMinimum((int)5, (int)5, (int)45) && !mysqlIO.versionMeetsMinimum((int)5, (int)6, (int)0) || mysqlIO.versionMeetsMinimum((int)5, (int)6, (int)26) && !mysqlIO.versionMeetsMinimum((int)5, (int)7, (int)0) || mysqlIO.versionMeetsMinimum((int)5, (int)7, (int)6)) {
                    if (Util.getJVMVersion() < 8) {
                        disableDHAlgorithm = true;
                    }
                } else if (Util.getJVMVersion() >= 8) {
                    disableDHAlgorithm = true;
                }
                if (disableDHAlgorithm) {
                    allowedCiphers = new ArrayList<E>();
                    for (String cipher : ((SSLSocket)mysqlIO.mysqlConnection).getEnabledCipherSuites()) {
                        if (disableDHAlgorithm && (cipher.indexOf((String)"_DHE_") > -1 || cipher.indexOf((String)"_DH_") > -1)) continue;
                        allowedCiphers.add(cipher);
                    }
                }
            }
            if (allowedCiphers != null) {
                ((SSLSocket)mysqlIO.mysqlConnection).setEnabledCipherSuites((String[])allowedCiphers.toArray(new String[0]));
            }
            ((SSLSocket)mysqlIO.mysqlConnection).startHandshake();
            mysqlIO.mysqlInput = mysqlIO.connection.getUseUnbufferedInput() ? mysqlIO.mysqlConnection.getInputStream() : new BufferedInputStream((InputStream)mysqlIO.mysqlConnection.getInputStream(), (int)16384);
            mysqlIO.mysqlOutput = new BufferedOutputStream((OutputStream)mysqlIO.mysqlConnection.getOutputStream(), (int)16384);
            mysqlIO.mysqlOutput.flush();
            mysqlIO.socketFactory = sslFact;
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)mysqlIO.connection, (long)mysqlIO.getLastPacketSentTimeMs(), (long)mysqlIO.getLastPacketReceivedTimeMs(), (Exception)ioEx, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
        }
    }

    private ExportControlled() {
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private static SSLSocketFactory getSSLSocketFactoryDefaultOrConfigured(MysqlIO mysqlIO) throws SQLException {
        block44 : {
            block43 : {
                clientCertificateKeyStoreUrl = mysqlIO.connection.getClientCertificateKeyStoreUrl();
                clientCertificateKeyStorePassword = mysqlIO.connection.getClientCertificateKeyStorePassword();
                clientCertificateKeyStoreType = mysqlIO.connection.getClientCertificateKeyStoreType();
                trustCertificateKeyStoreUrl = mysqlIO.connection.getTrustCertificateKeyStoreUrl();
                trustCertificateKeyStorePassword = mysqlIO.connection.getTrustCertificateKeyStorePassword();
                trustCertificateKeyStoreType = mysqlIO.connection.getTrustCertificateKeyStoreType();
                if (StringUtils.isNullOrEmpty((String)clientCertificateKeyStoreUrl)) {
                    clientCertificateKeyStoreUrl = System.getProperty((String)"javax.net.ssl.keyStore");
                    clientCertificateKeyStorePassword = System.getProperty((String)"javax.net.ssl.keyStorePassword");
                    clientCertificateKeyStoreType = System.getProperty((String)"javax.net.ssl.keyStoreType");
                    if (StringUtils.isNullOrEmpty((String)clientCertificateKeyStoreType)) {
                        clientCertificateKeyStoreType = "JKS";
                    }
                    if (!StringUtils.isNullOrEmpty((String)clientCertificateKeyStoreUrl)) {
                        try {
                            new URL((String)clientCertificateKeyStoreUrl);
                        }
                        catch (MalformedURLException e) {
                            clientCertificateKeyStoreUrl = "file:" + clientCertificateKeyStoreUrl;
                        }
                    }
                }
                if (StringUtils.isNullOrEmpty((String)trustCertificateKeyStoreUrl)) {
                    trustCertificateKeyStoreUrl = System.getProperty((String)"javax.net.ssl.trustStore");
                    trustCertificateKeyStorePassword = System.getProperty((String)"javax.net.ssl.trustStorePassword");
                    trustCertificateKeyStoreType = System.getProperty((String)"javax.net.ssl.trustStoreType");
                    if (StringUtils.isNullOrEmpty((String)trustCertificateKeyStoreType)) {
                        trustCertificateKeyStoreType = "JKS";
                    }
                    if (!StringUtils.isNullOrEmpty((String)trustCertificateKeyStoreUrl)) {
                        try {
                            new URL((String)trustCertificateKeyStoreUrl);
                        }
                        catch (MalformedURLException e) {
                            trustCertificateKeyStoreUrl = "file:" + trustCertificateKeyStoreUrl;
                        }
                    }
                }
                tmf = null;
                kmf = null;
                kms = null;
                tms = new ArrayList<TrustManager>();
                try {
                    tmf = TrustManagerFactory.getInstance((String)TrustManagerFactory.getDefaultAlgorithm());
                    kmf = KeyManagerFactory.getInstance((String)KeyManagerFactory.getDefaultAlgorithm());
                }
                catch (NoSuchAlgorithmException nsae) {
                    throw SQLError.createSQLException((String)"Default algorithm definitions for TrustManager and/or KeyManager are invalid.  Check java security properties file.", (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                }
                if (!StringUtils.isNullOrEmpty((String)clientCertificateKeyStoreUrl)) {
                    ksIS = null;
                    try {
                        block42 : {
                            if (StringUtils.isNullOrEmpty((String)clientCertificateKeyStoreType)) break block42;
                            clientKeyStore = KeyStore.getInstance((String)clientCertificateKeyStoreType);
                            ksURL = new URL((String)clientCertificateKeyStoreUrl);
                            password = clientCertificateKeyStorePassword == null ? new char[0] : clientCertificateKeyStorePassword.toCharArray();
                            ksIS = ksURL.openStream();
                            clientKeyStore.load((InputStream)ksIS, (char[])password);
                            kmf.init((KeyStore)clientKeyStore, (char[])password);
                            kms = kmf.getKeyManagers();
                        }
                        var16_33 = null;
                        if (ksIS == null) break block43;
                        try {
                            ksIS.close();
                        }
                        catch (IOException e) {}
                        catch (UnrecoverableKeyException uke) {
                            throw SQLError.createSQLException((String)"Could not recover keys from client keystore.  Check password?", (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                        }
                        catch (NoSuchAlgorithmException nsae) {
                            throw SQLError.createSQLException((String)("Unsupported keystore algorithm [" + nsae.getMessage() + "]"), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                        }
                        catch (KeyStoreException kse) {
                            throw SQLError.createSQLException((String)("Could not create KeyStore instance [" + kse.getMessage() + "]"), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                        }
                        catch (CertificateException nsae) {
                            throw SQLError.createSQLException((String)("Could not load client" + clientCertificateKeyStoreType + " keystore from " + clientCertificateKeyStoreUrl), (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                        }
                        catch (MalformedURLException mue) {
                            throw SQLError.createSQLException((String)(clientCertificateKeyStoreUrl + " does not appear to be a valid URL."), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                        }
                        catch (IOException ioe) {
                            sqlEx = SQLError.createSQLException((String)("Cannot open " + clientCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]"), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                            sqlEx.initCause((Throwable)ioe);
                            throw sqlEx;
                        }
                    }
                    catch (Throwable var15_39) {
                        var16_34 = null;
                        if (ksIS == null) throw var15_39;
                        ** try [egrp 5[TRYBLOCK] [11 : 636->644)] { 
lbl84: // 1 sources:
                        ksIS.close();
                        throw var15_39;
lbl86: // 1 sources:
                        catch (IOException e) {
                            // empty catch block
                        }
                        throw var15_39;
                    }
                }
            }
            trustStoreIS = null;
            try {
                trustKeyStore = null;
                if (!StringUtils.isNullOrEmpty((String)trustCertificateKeyStoreUrl) && !StringUtils.isNullOrEmpty((String)trustCertificateKeyStoreType)) {
                    trustStoreIS = new URL((String)trustCertificateKeyStoreUrl).openStream();
                    trustStorePassword = trustCertificateKeyStorePassword == null ? new char[0] : trustCertificateKeyStorePassword.toCharArray();
                    trustKeyStore = KeyStore.getInstance((String)trustCertificateKeyStoreType);
                    trustKeyStore.load((InputStream)trustStoreIS, (char[])trustStorePassword);
                }
                tmf.init(trustKeyStore);
                origTms = tmf.getTrustManagers();
                verifyServerCert = mysqlIO.connection.getVerifyServerCertificate();
                for (TrustManager tm : origTms) {
                    tms.add(tm instanceof X509TrustManager != false ? new X509TrustManagerWrapper((X509TrustManager)((X509TrustManager)tm), (boolean)verifyServerCert) : tm);
                }
                var20_42 = null;
                if (trustStoreIS == null) break block44;
                try {
                    trustStoreIS.close();
                }
                catch (IOException e) {}
                catch (MalformedURLException e) {
                    throw SQLError.createSQLException((String)(trustCertificateKeyStoreUrl + " does not appear to be a valid URL."), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                }
                catch (KeyStoreException e) {
                    throw SQLError.createSQLException((String)("Could not create KeyStore instance [" + e.getMessage() + "]"), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                }
                catch (NoSuchAlgorithmException e) {
                    throw SQLError.createSQLException((String)("Unsupported keystore algorithm [" + e.getMessage() + "]"), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                }
                catch (CertificateException e) {
                    throw SQLError.createSQLException((String)("Could not load trust" + trustCertificateKeyStoreType + " keystore from " + trustCertificateKeyStoreUrl), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                }
                catch (IOException e) {
                    sqlEx = SQLError.createSQLException((String)("Cannot open " + trustCertificateKeyStoreType + " [" + e.getMessage() + "]"), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
                    sqlEx.initCause((Throwable)e);
                    throw sqlEx;
                }
            }
            catch (Throwable var19_46) {
                var20_43 = null;
                if (trustStoreIS == null) throw var19_46;
                ** try [egrp 8[TRYBLOCK] [19 : 1067->1075)] { 
lbl131: // 1 sources:
                trustStoreIS.close();
                throw var19_46;
lbl133: // 1 sources:
                catch (IOException e) {
                    // empty catch block
                }
                throw var19_46;
            }
        }
        if (tms.size() == 0) {
            tms.add(new X509TrustManagerWrapper());
        }
        try {
            sslContext = SSLContext.getInstance((String)"TLS");
            sslContext.init((KeyManager[])kms, (TrustManager[])tms.toArray(new TrustManager[tms.size()]), null);
            return sslContext.getSocketFactory();
        }
        catch (NoSuchAlgorithmException nsae) {
            throw SQLError.createSQLException((String)"TLS is not a valid SSL protocol.", (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
        }
        catch (KeyManagementException kme) {
            throw SQLError.createSQLException((String)("KeyManagementException: " + kme.getMessage()), (String)"08000", (int)0, (boolean)false, (ExceptionInterceptor)mysqlIO.getExceptionInterceptor());
        }
    }

    public static boolean isSSLEstablished(Socket socket) {
        if (socket == null) {
            return false;
        }
        boolean bl = SSLSocket.class.isAssignableFrom(socket.getClass());
        return bl;
    }

    public static RSAPublicKey decodeRSAPublicKey(String key, ExceptionInterceptor interceptor) throws SQLException {
        try {
            if (key == null) {
                throw new SQLException((String)"key parameter is null");
            }
            int offset = key.indexOf((String)"\n") + 1;
            int len = key.indexOf((String)"-----END PUBLIC KEY-----") - offset;
            byte[] certificateData = Base64Decoder.decode((byte[])key.getBytes(), (int)offset, (int)len);
            X509EncodedKeySpec spec = new X509EncodedKeySpec((byte[])certificateData);
            KeyFactory kf = KeyFactory.getInstance((String)"RSA");
            return (RSAPublicKey)kf.generatePublic((KeySpec)spec);
        }
        catch (Exception ex) {
            throw SQLError.createSQLException((String)"Unable to decode public key", (String)"S1009", (Throwable)ex, (ExceptionInterceptor)interceptor);
        }
    }

    public static byte[] encryptWithRSAPublicKey(byte[] source, RSAPublicKey key, String transformation, ExceptionInterceptor interceptor) throws SQLException {
        try {
            Cipher cipher = Cipher.getInstance((String)transformation);
            cipher.init((int)1, (Key)key);
            return cipher.doFinal((byte[])source);
        }
        catch (Exception ex) {
            throw SQLError.createSQLException((String)ex.getMessage(), (String)"S1009", (Throwable)ex, (ExceptionInterceptor)interceptor);
        }
    }
}

