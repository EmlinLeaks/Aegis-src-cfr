/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.StringUtil;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class FingerprintTrustManagerFactory
extends SimpleTrustManagerFactory {
    private static final Pattern FINGERPRINT_PATTERN = Pattern.compile((String)"^[0-9a-fA-F:]+$");
    private static final Pattern FINGERPRINT_STRIP_PATTERN = Pattern.compile((String)":");
    private static final int SHA1_BYTE_LEN = 20;
    private static final int SHA1_HEX_LEN = 40;
    private static final FastThreadLocal<MessageDigest> tlmd = new FastThreadLocal<MessageDigest>(){

        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance((String)"SHA1");
            }
            catch (java.security.NoSuchAlgorithmException e) {
                throw new java.lang.Error((java.lang.Throwable)e);
            }
        }
    };
    private final TrustManager tm = new X509TrustManager((FingerprintTrustManagerFactory)this){
        final /* synthetic */ FingerprintTrustManagerFactory this$0;
        {
            this.this$0 = this$0;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String s) throws java.security.cert.CertificateException {
            this.checkTrusted((String)"client", (java.security.cert.X509Certificate[])chain);
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String s) throws java.security.cert.CertificateException {
            this.checkTrusted((String)"server", (java.security.cert.X509Certificate[])chain);
        }

        private void checkTrusted(String type, java.security.cert.X509Certificate[] chain) throws java.security.cert.CertificateException {
            java.security.cert.X509Certificate cert = chain[0];
            byte[] fingerprint = this.fingerprint((java.security.cert.X509Certificate)cert);
            boolean found = false;
            for (byte[] allowedFingerprint : FingerprintTrustManagerFactory.access$000((FingerprintTrustManagerFactory)this.this$0)) {
                if (!Arrays.equals((byte[])fingerprint, (byte[])allowedFingerprint)) continue;
                found = true;
                break;
            }
            if (found) return;
            throw new java.security.cert.CertificateException((String)(type + " certificate with unknown fingerprint: " + cert.getSubjectDN()));
        }

        private byte[] fingerprint(java.security.cert.X509Certificate cert) throws java.security.cert.CertificateEncodingException {
            MessageDigest md = (MessageDigest)FingerprintTrustManagerFactory.access$100().get();
            md.reset();
            return md.digest((byte[])cert.getEncoded());
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return io.netty.util.internal.EmptyArrays.EMPTY_X509_CERTIFICATES;
        }
    };
    private final byte[][] fingerprints;

    public FingerprintTrustManagerFactory(Iterable<String> fingerprints) {
        this((byte[][])FingerprintTrustManagerFactory.toFingerprintArray(fingerprints));
    }

    public FingerprintTrustManagerFactory(String ... fingerprints) {
        this((byte[][])FingerprintTrustManagerFactory.toFingerprintArray(Arrays.asList(fingerprints)));
    }

    public FingerprintTrustManagerFactory(byte[] ... fingerprints) {
        if (fingerprints == null) {
            throw new NullPointerException((String)"fingerprints");
        }
        ArrayList<Object> list = new ArrayList<Object>((int)fingerprints.length);
        for (byte[] f : fingerprints) {
            if (f == null) break;
            if (f.length != 20) {
                throw new IllegalArgumentException((String)("malformed fingerprint: " + ByteBufUtil.hexDump((ByteBuf)Unpooled.wrappedBuffer((byte[])f)) + " (expected: SHA1)"));
            }
            list.add(f.clone());
        }
        this.fingerprints = (byte[][])list.toArray(new byte[0][]);
    }

    private static byte[][] toFingerprintArray(Iterable<String> fingerprints) {
        if (fingerprints == null) {
            throw new NullPointerException((String)"fingerprints");
        }
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        Iterator<String> iterator = fingerprints.iterator();
        while (iterator.hasNext()) {
            String f = iterator.next();
            if (f == null) {
                return (byte[][])list.toArray(new byte[0][]);
            }
            if (!FINGERPRINT_PATTERN.matcher((CharSequence)f).matches()) {
                throw new IllegalArgumentException((String)("malformed fingerprint: " + f));
            }
            if ((f = FINGERPRINT_STRIP_PATTERN.matcher((CharSequence)f).replaceAll((String)"")).length() != 40) {
                throw new IllegalArgumentException((String)("malformed fingerprint: " + f + " (expected: SHA1)"));
            }
            list.add(StringUtil.decodeHexDump((CharSequence)f));
        }
        return (byte[][])list.toArray(new byte[0][]);
    }

    @Override
    protected void engineInit(KeyStore keyStore) throws Exception {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{this.tm};
    }

    static /* synthetic */ byte[][] access$000(FingerprintTrustManagerFactory x0) {
        return x0.fingerprints;
    }

    static /* synthetic */ FastThreadLocal access$100() {
        return tlmd;
    }
}

