/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.ssl.util.BouncyCastleSelfSignedCertGenerator;
import io.netty.handler.ssl.util.OpenJdkSelfSignedCertGenerator;
import io.netty.handler.ssl.util.ThreadLocalInsecureRandom;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public final class SelfSignedCertificate {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);
    private static final Date DEFAULT_NOT_BEFORE = new Date((long)SystemPropertyUtil.getLong((String)"io.netty.selfSignedCertificate.defaultNotBefore", (long)(System.currentTimeMillis() - 31536000000L)));
    private static final Date DEFAULT_NOT_AFTER = new Date((long)SystemPropertyUtil.getLong((String)"io.netty.selfSignedCertificate.defaultNotAfter", (long)253402300799000L));
    private static final int DEFAULT_KEY_LENGTH_BITS = SystemPropertyUtil.getInt((String)"io.netty.handler.ssl.util.selfSignedKeyStrength", (int)2048);
    private final File certificate;
    private final File privateKey;
    private final X509Certificate cert;
    private final PrivateKey key;

    public SelfSignedCertificate() throws CertificateException {
        this((Date)DEFAULT_NOT_BEFORE, (Date)DEFAULT_NOT_AFTER);
    }

    public SelfSignedCertificate(Date notBefore, Date notAfter) throws CertificateException {
        this((String)"example.com", (Date)notBefore, (Date)notAfter);
    }

    public SelfSignedCertificate(String fqdn) throws CertificateException {
        this((String)fqdn, (Date)DEFAULT_NOT_BEFORE, (Date)DEFAULT_NOT_AFTER);
    }

    public SelfSignedCertificate(String fqdn, Date notBefore, Date notAfter) throws CertificateException {
        this((String)fqdn, (SecureRandom)ThreadLocalInsecureRandom.current(), (int)DEFAULT_KEY_LENGTH_BITS, (Date)notBefore, (Date)notAfter);
    }

    public SelfSignedCertificate(String fqdn, SecureRandom random, int bits) throws CertificateException {
        this((String)fqdn, (SecureRandom)random, (int)bits, (Date)DEFAULT_NOT_BEFORE, (Date)DEFAULT_NOT_AFTER);
    }

    public SelfSignedCertificate(String fqdn, SecureRandom random, int bits, Date notBefore, Date notAfter) throws CertificateException {
        String[] paths;
        KeyPair keypair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance((String)"RSA");
            keyGen.initialize((int)bits, (SecureRandom)random);
            keypair = keyGen.generateKeyPair();
        }
        catch (NoSuchAlgorithmException e) {
            throw new Error((Throwable)e);
        }
        try {
            paths = OpenJdkSelfSignedCertGenerator.generate((String)fqdn, (KeyPair)keypair, (SecureRandom)random, (Date)notBefore, (Date)notAfter);
        }
        catch (Throwable t) {
            logger.debug((String)"Failed to generate a self-signed X.509 certificate using sun.security.x509:", (Throwable)t);
            try {
                paths = BouncyCastleSelfSignedCertGenerator.generate((String)fqdn, (KeyPair)keypair, (SecureRandom)random, (Date)notBefore, (Date)notAfter);
            }
            catch (Throwable t2) {
                logger.debug((String)"Failed to generate a self-signed X.509 certificate using Bouncy Castle:", (Throwable)t2);
                throw new CertificateException((String)"No provider succeeded to generate a self-signed certificate. See debug log for the root cause.", (Throwable)t2);
            }
        }
        this.certificate = new File((String)paths[0]);
        this.privateKey = new File((String)paths[1]);
        this.key = keypair.getPrivate();
        FileInputStream certificateInput = null;
        try {
            certificateInput = new FileInputStream((File)this.certificate);
            this.cert = (X509Certificate)CertificateFactory.getInstance((String)"X509").generateCertificate((InputStream)certificateInput);
            return;
        }
        catch (Exception e) {
            throw new CertificateEncodingException((Throwable)e);
        }
        finally {
            block16 : {
                if (certificateInput != null) {
                    try {
                        certificateInput.close();
                    }
                    catch (IOException e) {
                        if (!logger.isWarnEnabled()) break block16;
                        logger.warn((String)("Failed to close a file: " + this.certificate), (Throwable)e);
                    }
                }
            }
        }
    }

    public File certificate() {
        return this.certificate;
    }

    public File privateKey() {
        return this.privateKey;
    }

    public X509Certificate cert() {
        return this.cert;
    }

    public PrivateKey key() {
        return this.key;
    }

    public void delete() {
        SelfSignedCertificate.safeDelete((File)this.certificate);
        SelfSignedCertificate.safeDelete((File)this.privateKey);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String[] newSelfSignedCertificate(String fqdn, PrivateKey key, X509Certificate cert) throws IOException, CertificateEncodingException {
        ByteBuf encodedBuf;
        String keyText;
        String certText;
        ByteBuf wrappedBuf = Unpooled.wrappedBuffer((byte[])key.getEncoded());
        try {
            encodedBuf = Base64.encode((ByteBuf)wrappedBuf, (boolean)true);
            try {
                keyText = "-----BEGIN PRIVATE KEY-----\n" + encodedBuf.toString((Charset)CharsetUtil.US_ASCII) + "\n-----END PRIVATE KEY-----\n";
            }
            finally {
                encodedBuf.release();
            }
        }
        finally {
            wrappedBuf.release();
        }
        File keyFile = File.createTempFile((String)("keyutil_" + fqdn + '_'), (String)".key");
        keyFile.deleteOnExit();
        FileOutputStream keyOut = new FileOutputStream((File)keyFile);
        try {
            ((OutputStream)keyOut).write((byte[])keyText.getBytes((Charset)CharsetUtil.US_ASCII));
            ((OutputStream)keyOut).close();
            keyOut = null;
        }
        finally {
            if (keyOut != null) {
                SelfSignedCertificate.safeClose((File)keyFile, (OutputStream)keyOut);
                SelfSignedCertificate.safeDelete((File)keyFile);
            }
        }
        wrappedBuf = Unpooled.wrappedBuffer((byte[])cert.getEncoded());
        try {
            encodedBuf = Base64.encode((ByteBuf)wrappedBuf, (boolean)true);
            try {
                certText = "-----BEGIN CERTIFICATE-----\n" + encodedBuf.toString((Charset)CharsetUtil.US_ASCII) + "\n-----END CERTIFICATE-----\n";
            }
            finally {
                encodedBuf.release();
            }
        }
        finally {
            wrappedBuf.release();
        }
        File certFile = File.createTempFile((String)("keyutil_" + fqdn + '_'), (String)".crt");
        certFile.deleteOnExit();
        FileOutputStream certOut = new FileOutputStream((File)certFile);
        try {
            ((OutputStream)certOut).write((byte[])certText.getBytes((Charset)CharsetUtil.US_ASCII));
            ((OutputStream)certOut).close();
            certOut = null;
            return new String[]{certFile.getPath(), keyFile.getPath()};
        }
        finally {
            if (certOut != null) {
                SelfSignedCertificate.safeClose((File)certFile, (OutputStream)certOut);
                SelfSignedCertificate.safeDelete((File)certFile);
                SelfSignedCertificate.safeDelete((File)keyFile);
            }
        }
    }

    private static void safeDelete(File certFile) {
        if (certFile.delete()) return;
        if (!logger.isWarnEnabled()) return;
        logger.warn((String)("Failed to delete a file: " + certFile));
    }

    private static void safeClose(File keyFile, OutputStream keyOut) {
        try {
            keyOut.close();
            return;
        }
        catch (IOException e) {
            if (!logger.isWarnEnabled()) return;
            logger.warn((String)("Failed to close a file: " + keyFile), (Throwable)e);
        }
    }
}

