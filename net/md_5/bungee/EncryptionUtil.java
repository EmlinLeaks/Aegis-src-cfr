/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.jni.cipher.JavaCipher;
import net.md_5.bungee.jni.cipher.NativeCipher;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;

public class EncryptionUtil {
    private static final Random random = new Random();
    public static final KeyPair keys;
    private static final SecretKey secret;
    public static final NativeCode<BungeeCipher> nativeFactory;

    public static EncryptionRequest encryptRequest() {
        String hash = Long.toString((long)random.nextLong(), (int)16);
        byte[] pubKey = keys.getPublic().getEncoded();
        byte[] verify = new byte[4];
        random.nextBytes((byte[])verify);
        return new EncryptionRequest((String)hash, (byte[])pubKey, (byte[])verify);
    }

    public static SecretKey getSecret(EncryptionResponse resp, EncryptionRequest request) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance((String)"RSA");
        cipher.init((int)2, (Key)keys.getPrivate());
        byte[] decrypted = cipher.doFinal((byte[])resp.getVerifyToken());
        if (!Arrays.equals((byte[])request.getVerifyToken(), (byte[])decrypted)) {
            throw new IllegalStateException((String)"Key pairs do not match!");
        }
        cipher.init((int)2, (Key)keys.getPrivate());
        return new SecretKeySpec((byte[])cipher.doFinal((byte[])resp.getSharedSecret()), (String)"AES");
    }

    public static BungeeCipher getCipher(boolean forEncryption, SecretKey shared) throws GeneralSecurityException {
        BungeeCipher cipher = nativeFactory.newInstance();
        cipher.init((boolean)forEncryption, (SecretKey)shared);
        return cipher;
    }

    public static PublicKey getPubkey(EncryptionRequest request) throws GeneralSecurityException {
        return KeyFactory.getInstance((String)"RSA").generatePublic((KeySpec)new X509EncodedKeySpec((byte[])request.getPublicKey()));
    }

    public static byte[] encrypt(Key key, byte[] b) throws GeneralSecurityException {
        Cipher hasher = Cipher.getInstance((String)"RSA");
        hasher.init((int)1, (Key)key);
        return hasher.doFinal((byte[])b);
    }

    public static SecretKey getSecret() {
        return secret;
    }

    static {
        secret = new SecretKeySpec((byte[])new byte[16], (String)"AES");
        nativeFactory = new NativeCode<NativeCipher>((String)"native-cipher", JavaCipher.class, NativeCipher.class);
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance((String)"RSA");
            generator.initialize((int)1024);
            keys = generator.generateKeyPair();
            return;
        }
        catch (NoSuchAlgorithmException ex) {
            throw new ExceptionInInitializerError((Throwable)ex);
        }
    }
}

