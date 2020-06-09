/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.cert.X509CertificateHolder
 *  org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
 *  org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 *  org.bouncycastle.operator.ContentSigner
 *  org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

final class BouncyCastleSelfSignedCertGenerator {
    private static final Provider PROVIDER = new BouncyCastleProvider();

    static String[] generate(String fqdn, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter) throws Exception {
        PrivateKey key = keypair.getPrivate();
        X500Name owner = new X500Name((String)("CN=" + fqdn));
        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder((X500Name)owner, (BigInteger)new BigInteger((int)64, (Random)random), (Date)notBefore, (Date)notAfter, (X500Name)owner, (PublicKey)keypair.getPublic());
        ContentSigner signer = new JcaContentSignerBuilder((String)"SHA256WithRSAEncryption").build((PrivateKey)key);
        X509CertificateHolder certHolder = builder.build((ContentSigner)signer);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider((Provider)PROVIDER).getCertificate((X509CertificateHolder)certHolder);
        cert.verify((PublicKey)keypair.getPublic());
        return SelfSignedCertificate.newSelfSignedCertificate((String)fqdn, (PrivateKey)key, (X509Certificate)cert);
    }

    private BouncyCastleSelfSignedCertGenerator() {
    }
}

