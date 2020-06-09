/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CipherSuiteConverter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CipherSuiteConverter.class);
    private static final Pattern JAVA_CIPHERSUITE_PATTERN = Pattern.compile((String)"^(?:TLS|SSL)_((?:(?!_WITH_).)+)_WITH_(.*)_(.*)$");
    private static final Pattern OPENSSL_CIPHERSUITE_PATTERN = Pattern.compile((String)"^(?:((?:(?:EXP-)?(?:(?:DHE|EDH|ECDH|ECDHE|SRP|RSA)-(?:DSS|RSA|ECDSA|PSK)|(?:ADH|AECDH|KRB5|PSK|SRP)))|EXP)-)?(.*)-(.*)$");
    private static final Pattern JAVA_AES_CBC_PATTERN = Pattern.compile((String)"^(AES)_([0-9]+)_CBC$");
    private static final Pattern JAVA_AES_PATTERN = Pattern.compile((String)"^(AES)_([0-9]+)_(.*)$");
    private static final Pattern OPENSSL_AES_CBC_PATTERN = Pattern.compile((String)"^(AES)([0-9]+)$");
    private static final Pattern OPENSSL_AES_PATTERN = Pattern.compile((String)"^(AES)([0-9]+)-(.*)$");
    private static final ConcurrentMap<String, String> j2o = PlatformDependent.newConcurrentHashMap();
    private static final ConcurrentMap<String, Map<String, String>> o2j = PlatformDependent.newConcurrentHashMap();
    private static final Map<String, String> j2oTls13;
    private static final Map<String, Map<String, String>> o2jTls13;

    static void clearCache() {
        j2o.clear();
        o2j.clear();
    }

    static boolean isJ2OCached(String key, String value) {
        return value.equals(j2o.get((Object)key));
    }

    static boolean isO2JCached(String key, String protocol, String value) {
        Map p2j = (Map)o2j.get((Object)key);
        if (p2j != null) return value.equals(p2j.get((Object)protocol));
        return false;
    }

    static String toOpenSsl(String javaCipherSuite, boolean boringSSL) {
        String converted = (String)j2o.get((Object)javaCipherSuite);
        if (converted == null) return CipherSuiteConverter.cacheFromJava((String)javaCipherSuite, (boolean)boringSSL);
        return converted;
    }

    private static String cacheFromJava(String javaCipherSuite, boolean boringSSL) {
        String converted = j2oTls13.get((Object)javaCipherSuite);
        if (converted != null) {
            String string;
            if (boringSSL) {
                string = converted;
                return string;
            }
            string = javaCipherSuite;
            return string;
        }
        String openSslCipherSuite = CipherSuiteConverter.toOpenSslUncached((String)javaCipherSuite, (boolean)boringSSL);
        if (openSslCipherSuite == null) {
            return null;
        }
        j2o.putIfAbsent((String)javaCipherSuite, (String)openSslCipherSuite);
        String javaCipherSuiteSuffix = javaCipherSuite.substring((int)4);
        HashMap<String, String> p2j = new HashMap<String, String>((int)4);
        p2j.put("", javaCipherSuiteSuffix);
        p2j.put("SSL", "SSL_" + javaCipherSuiteSuffix);
        p2j.put("TLS", "TLS_" + javaCipherSuiteSuffix);
        o2j.put((String)openSslCipherSuite, p2j);
        logger.debug((String)"Cipher suite mapping: {} => {}", (Object)javaCipherSuite, (Object)openSslCipherSuite);
        return openSslCipherSuite;
    }

    static String toOpenSslUncached(String javaCipherSuite, boolean boringSSL) {
        String converted = j2oTls13.get((Object)javaCipherSuite);
        if (converted != null) {
            String string;
            if (boringSSL) {
                string = converted;
                return string;
            }
            string = javaCipherSuite;
            return string;
        }
        Matcher m = JAVA_CIPHERSUITE_PATTERN.matcher((CharSequence)javaCipherSuite);
        if (!m.matches()) {
            return null;
        }
        String handshakeAlgo = CipherSuiteConverter.toOpenSslHandshakeAlgo((String)m.group((int)1));
        String bulkCipher = CipherSuiteConverter.toOpenSslBulkCipher((String)m.group((int)2));
        String hmacAlgo = CipherSuiteConverter.toOpenSslHmacAlgo((String)m.group((int)3));
        if (handshakeAlgo.isEmpty()) {
            return bulkCipher + '-' + hmacAlgo;
        }
        if (!bulkCipher.contains((CharSequence)"CHACHA20")) return handshakeAlgo + '-' + bulkCipher + '-' + hmacAlgo;
        return handshakeAlgo + '-' + bulkCipher;
    }

    private static String toOpenSslHandshakeAlgo(String handshakeAlgo) {
        boolean export = handshakeAlgo.endsWith((String)"_EXPORT");
        if (export) {
            handshakeAlgo = handshakeAlgo.substring((int)0, (int)(handshakeAlgo.length() - 7));
        }
        if ("RSA".equals((Object)handshakeAlgo)) {
            handshakeAlgo = "";
        } else if (handshakeAlgo.endsWith((String)"_anon")) {
            handshakeAlgo = 'A' + handshakeAlgo.substring((int)0, (int)(handshakeAlgo.length() - 5));
        }
        if (!export) return handshakeAlgo.replace((char)'_', (char)'-');
        if (handshakeAlgo.isEmpty()) {
            handshakeAlgo = "EXP";
            return handshakeAlgo.replace((char)'_', (char)'-');
        }
        handshakeAlgo = "EXP-" + handshakeAlgo;
        return handshakeAlgo.replace((char)'_', (char)'-');
    }

    private static String toOpenSslBulkCipher(String bulkCipher) {
        if (bulkCipher.startsWith((String)"AES_")) {
            Matcher m = JAVA_AES_CBC_PATTERN.matcher((CharSequence)bulkCipher);
            if (m.matches()) {
                return m.replaceFirst((String)"$1$2");
            }
            m = JAVA_AES_PATTERN.matcher((CharSequence)bulkCipher);
            if (m.matches()) {
                return m.replaceFirst((String)"$1$2-$3");
            }
        }
        if ("3DES_EDE_CBC".equals((Object)bulkCipher)) {
            return "DES-CBC3";
        }
        if ("RC4_128".equals((Object)bulkCipher)) return "RC4";
        if ("RC4_40".equals((Object)bulkCipher)) {
            return "RC4";
        }
        if ("DES40_CBC".equals((Object)bulkCipher)) return "DES-CBC";
        if ("DES_CBC_40".equals((Object)bulkCipher)) {
            return "DES-CBC";
        }
        if (!"RC2_CBC_40".equals((Object)bulkCipher)) return bulkCipher.replace((char)'_', (char)'-');
        return "RC2-CBC";
    }

    private static String toOpenSslHmacAlgo(String hmacAlgo) {
        return hmacAlgo;
    }

    static String toJava(String openSslCipherSuite, String protocol) {
        Map<String, String> p2j = (Map<String, String>)o2j.get((Object)openSslCipherSuite);
        if (p2j == null && (p2j = CipherSuiteConverter.cacheFromOpenSsl((String)openSslCipherSuite)) == null) {
            return null;
        }
        String javaCipherSuite = p2j.get((Object)protocol);
        if (javaCipherSuite != null) return javaCipherSuite;
        String cipher = p2j.get((Object)"");
        if (cipher != null) return protocol + '_' + cipher;
        return null;
    }

    private static Map<String, String> cacheFromOpenSsl(String openSslCipherSuite) {
        Map<String, String> converted = o2jTls13.get((Object)openSslCipherSuite);
        if (converted != null) {
            return converted;
        }
        String javaCipherSuiteSuffix = CipherSuiteConverter.toJavaUncached0((String)openSslCipherSuite, (boolean)false);
        if (javaCipherSuiteSuffix == null) {
            return null;
        }
        String javaCipherSuiteSsl = "SSL_" + javaCipherSuiteSuffix;
        String javaCipherSuiteTls = "TLS_" + javaCipherSuiteSuffix;
        HashMap<String, String> p2j = new HashMap<String, String>((int)4);
        p2j.put("", javaCipherSuiteSuffix);
        p2j.put("SSL", javaCipherSuiteSsl);
        p2j.put("TLS", javaCipherSuiteTls);
        o2j.putIfAbsent((String)openSslCipherSuite, p2j);
        j2o.putIfAbsent((String)javaCipherSuiteTls, (String)openSslCipherSuite);
        j2o.putIfAbsent((String)javaCipherSuiteSsl, (String)openSslCipherSuite);
        logger.debug((String)"Cipher suite mapping: {} => {}", (Object)javaCipherSuiteTls, (Object)openSslCipherSuite);
        logger.debug((String)"Cipher suite mapping: {} => {}", (Object)javaCipherSuiteSsl, (Object)openSslCipherSuite);
        return p2j;
    }

    static String toJavaUncached(String openSslCipherSuite) {
        return CipherSuiteConverter.toJavaUncached0((String)openSslCipherSuite, (boolean)true);
    }

    private static String toJavaUncached0(String openSslCipherSuite, boolean checkTls13) {
        Map<String, String> converted;
        String string;
        boolean export;
        if (checkTls13 && (converted = o2jTls13.get((Object)openSslCipherSuite)) != null) {
            return converted.get((Object)"TLS");
        }
        Matcher m = OPENSSL_CIPHERSUITE_PATTERN.matcher((CharSequence)openSslCipherSuite);
        if (!m.matches()) {
            return null;
        }
        String handshakeAlgo = m.group((int)1);
        if (handshakeAlgo == null) {
            handshakeAlgo = "";
            export = false;
        } else if (handshakeAlgo.startsWith((String)"EXP-")) {
            handshakeAlgo = handshakeAlgo.substring((int)4);
            export = true;
        } else if ("EXP".equals((Object)handshakeAlgo)) {
            handshakeAlgo = "";
            export = true;
        } else {
            export = false;
        }
        handshakeAlgo = CipherSuiteConverter.toJavaHandshakeAlgo((String)handshakeAlgo, (boolean)export);
        String bulkCipher = CipherSuiteConverter.toJavaBulkCipher((String)m.group((int)2), (boolean)export);
        String hmacAlgo = CipherSuiteConverter.toJavaHmacAlgo((String)m.group((int)3));
        String javaCipherSuite = handshakeAlgo + "_WITH_" + bulkCipher + '_' + hmacAlgo;
        if (bulkCipher.contains((CharSequence)"CHACHA20")) {
            string = javaCipherSuite + "_SHA256";
            return string;
        }
        string = javaCipherSuite;
        return string;
    }

    private static String toJavaHandshakeAlgo(String handshakeAlgo, boolean export) {
        if (handshakeAlgo.isEmpty()) {
            handshakeAlgo = "RSA";
        } else if ("ADH".equals((Object)handshakeAlgo)) {
            handshakeAlgo = "DH_anon";
        } else if ("AECDH".equals((Object)handshakeAlgo)) {
            handshakeAlgo = "ECDH_anon";
        }
        handshakeAlgo = handshakeAlgo.replace((char)'-', (char)'_');
        if (!export) return handshakeAlgo;
        return handshakeAlgo + "_EXPORT";
    }

    private static String toJavaBulkCipher(String bulkCipher, boolean export) {
        if (bulkCipher.startsWith((String)"AES")) {
            Matcher m = OPENSSL_AES_CBC_PATTERN.matcher((CharSequence)bulkCipher);
            if (m.matches()) {
                return m.replaceFirst((String)"$1_$2_CBC");
            }
            m = OPENSSL_AES_PATTERN.matcher((CharSequence)bulkCipher);
            if (m.matches()) {
                return m.replaceFirst((String)"$1_$2_$3");
            }
        }
        if ("DES-CBC3".equals((Object)bulkCipher)) {
            return "3DES_EDE_CBC";
        }
        if ("RC4".equals((Object)bulkCipher)) {
            if (!export) return "RC4_128";
            return "RC4_40";
        }
        if ("DES-CBC".equals((Object)bulkCipher)) {
            if (!export) return "DES_CBC";
            return "DES_CBC_40";
        }
        if (!"RC2-CBC".equals((Object)bulkCipher)) return bulkCipher.replace((char)'-', (char)'_');
        if (!export) return "RC2_CBC";
        return "RC2_CBC_40";
    }

    private static String toJavaHmacAlgo(String hmacAlgo) {
        return hmacAlgo;
    }

    static void convertToCipherStrings(Iterable<String> cipherSuites, StringBuilder cipherBuilder, StringBuilder cipherTLSv13Builder, boolean boringSSL) {
        for (String c : cipherSuites) {
            if (c == null) break;
            String converted = CipherSuiteConverter.toOpenSsl((String)c, (boolean)boringSSL);
            if (converted == null) {
                converted = c;
            }
            if (!OpenSsl.isCipherSuiteAvailable((String)converted)) {
                throw new IllegalArgumentException((String)("unsupported cipher suite: " + c + '(' + converted + ')'));
            }
            if (SslUtils.isTLSv13Cipher((String)converted) || SslUtils.isTLSv13Cipher((String)c)) {
                cipherTLSv13Builder.append((String)converted);
                cipherTLSv13Builder.append((char)':');
                continue;
            }
            cipherBuilder.append((String)converted);
            cipherBuilder.append((char)':');
        }
        if (cipherBuilder.length() == 0 && cipherTLSv13Builder.length() == 0) {
            throw new IllegalArgumentException((String)"empty cipher suites");
        }
        if (cipherBuilder.length() > 0) {
            cipherBuilder.setLength((int)(cipherBuilder.length() - 1));
        }
        if (cipherTLSv13Builder.length() <= 0) return;
        cipherTLSv13Builder.setLength((int)(cipherTLSv13Builder.length() - 1));
    }

    private CipherSuiteConverter() {
    }

    static {
        HashMap<String, String> j2oTls13Map = new HashMap<String, String>();
        j2oTls13Map.put("TLS_AES_128_GCM_SHA256", "AEAD-AES128-GCM-SHA256");
        j2oTls13Map.put("TLS_AES_256_GCM_SHA384", "AEAD-AES256-GCM-SHA384");
        j2oTls13Map.put("TLS_CHACHA20_POLY1305_SHA256", "AEAD-CHACHA20-POLY1305-SHA256");
        j2oTls13 = Collections.unmodifiableMap(j2oTls13Map);
        HashMap<String, Map<String, String>> o2jTls13Map = new HashMap<String, Map<String, String>>();
        o2jTls13Map.put("TLS_AES_128_GCM_SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
        o2jTls13Map.put("TLS_AES_256_GCM_SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
        o2jTls13Map.put("TLS_CHACHA20_POLY1305_SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
        o2jTls13Map.put("AEAD-AES128-GCM-SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
        o2jTls13Map.put("AEAD-AES256-GCM-SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
        o2jTls13Map.put("AEAD-CHACHA20-POLY1305-SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
        o2jTls13 = Collections.unmodifiableMap(o2jTls13Map);
    }
}

