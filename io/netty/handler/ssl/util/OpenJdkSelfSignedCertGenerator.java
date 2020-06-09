/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.SuppressJava6Requirement;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

final class OpenJdkSelfSignedCertGenerator {
    @SuppressJava6Requirement(reason="Usage guarded by dependency check")
    static String[] generate(String fqdn, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter) throws Exception {
        PrivateKey key = keypair.getPrivate();
        X509CertInfo info = new X509CertInfo();
        X500Name owner = new X500Name((String)("CN=" + fqdn));
        info.set((String)"version", (Object)new CertificateVersion((int)2));
        info.set((String)"serialNumber", (Object)new CertificateSerialNumber((BigInteger)new BigInteger((int)64, (Random)random)));
        try {
            info.set((String)"subject", (Object)new CertificateSubjectName((X500Name)owner));
        }
        catch (CertificateException ignore) {
            info.set((String)"subject", (Object)owner);
        }
        try {
            info.set((String)"issuer", (Object)new CertificateIssuerName((X500Name)owner));
        }
        catch (CertificateException ignore) {
            info.set((String)"issuer", (Object)owner);
        }
        info.set((String)"validity", (Object)new CertificateValidity((Date)notBefore, (Date)notAfter));
        info.set((String)"key", (Object)new CertificateX509Key((PublicKey)keypair.getPublic()));
        info.set((String)"algorithmID", (Object)new CertificateAlgorithmId((AlgorithmId)new AlgorithmId((ObjectIdentifier)AlgorithmId.sha256WithRSAEncryption_oid)));
        X509CertImpl cert = new X509CertImpl((X509CertInfo)info);
        cert.sign((PrivateKey)key, (String)"SHA256withRSA");
        info.set((String)"algorithmID.algorithm", (Object)cert.get((String)"x509.algorithm"));
        cert = new X509CertImpl((X509CertInfo)info);
        cert.sign((PrivateKey)key, (String)"SHA256withRSA");
        cert.verify((PublicKey)keypair.getPublic());
        return SelfSignedCertificate.newSelfSignedCertificate((String)fqdn, (PrivateKey)key, (X509Certificate)cert);
    }

    private OpenJdkSelfSignedCertGenerator() {
    }
}

