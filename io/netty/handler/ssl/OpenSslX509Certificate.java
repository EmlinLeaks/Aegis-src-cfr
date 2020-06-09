/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.SuppressJava6Requirement;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

final class OpenSslX509Certificate
extends X509Certificate {
    private final byte[] bytes;
    private X509Certificate wrapped;

    OpenSslX509Certificate(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.unwrap().checkValidity();
    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        this.unwrap().checkValidity((Date)date);
    }

    @Override
    public X500Principal getIssuerX500Principal() {
        return this.unwrap().getIssuerX500Principal();
    }

    @Override
    public X500Principal getSubjectX500Principal() {
        return this.unwrap().getSubjectX500Principal();
    }

    @Override
    public List<String> getExtendedKeyUsage() throws CertificateParsingException {
        return this.unwrap().getExtendedKeyUsage();
    }

    @Override
    public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
        return this.unwrap().getSubjectAlternativeNames();
    }

    @Override
    public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
        return this.unwrap().getSubjectAlternativeNames();
    }

    @SuppressJava6Requirement(reason="Can only be called from Java8 as class is package-private")
    @Override
    public void verify(PublicKey key, Provider sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.unwrap().verify((PublicKey)key, (Provider)sigProvider);
    }

    @Override
    public int getVersion() {
        return this.unwrap().getVersion();
    }

    @Override
    public BigInteger getSerialNumber() {
        return this.unwrap().getSerialNumber();
    }

    @Override
    public Principal getIssuerDN() {
        return this.unwrap().getIssuerDN();
    }

    @Override
    public Principal getSubjectDN() {
        return this.unwrap().getSubjectDN();
    }

    @Override
    public Date getNotBefore() {
        return this.unwrap().getNotBefore();
    }

    @Override
    public Date getNotAfter() {
        return this.unwrap().getNotAfter();
    }

    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        return this.unwrap().getTBSCertificate();
    }

    @Override
    public byte[] getSignature() {
        return this.unwrap().getSignature();
    }

    @Override
    public String getSigAlgName() {
        return this.unwrap().getSigAlgName();
    }

    @Override
    public String getSigAlgOID() {
        return this.unwrap().getSigAlgOID();
    }

    @Override
    public byte[] getSigAlgParams() {
        return this.unwrap().getSigAlgParams();
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        return this.unwrap().getIssuerUniqueID();
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        return this.unwrap().getSubjectUniqueID();
    }

    @Override
    public boolean[] getKeyUsage() {
        return this.unwrap().getKeyUsage();
    }

    @Override
    public int getBasicConstraints() {
        return this.unwrap().getBasicConstraints();
    }

    @Override
    public byte[] getEncoded() {
        return (byte[])this.bytes.clone();
    }

    @Override
    public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.unwrap().verify((PublicKey)key);
    }

    @Override
    public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.unwrap().verify((PublicKey)key, (String)sigProvider);
    }

    @Override
    public String toString() {
        return this.unwrap().toString();
    }

    @Override
    public PublicKey getPublicKey() {
        return this.unwrap().getPublicKey();
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return this.unwrap().hasUnsupportedCriticalExtension();
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        return this.unwrap().getCriticalExtensionOIDs();
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        return this.unwrap().getNonCriticalExtensionOIDs();
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        return this.unwrap().getExtensionValue((String)oid);
    }

    private X509Certificate unwrap() {
        X509Certificate wrapped = this.wrapped;
        if (wrapped != null) return wrapped;
        try {
            this.wrapped = (X509Certificate)SslContext.X509_CERT_FACTORY.generateCertificate((InputStream)new ByteArrayInputStream((byte[])this.bytes));
            return this.wrapped;
        }
        catch (CertificateException e) {
            throw new IllegalStateException((Throwable)e);
        }
    }
}

