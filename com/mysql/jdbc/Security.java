/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.AssertionFailedException;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
    private static final char PVERSION41_CHAR = '*';
    private static final int SHA1_HASH_SIZE = 20;
    private static int CACHING_SHA2_DIGEST_LENGTH = 32;

    private static int charVal(char c) {
        int n;
        if (c >= '0' && c <= '9') {
            n = c - 48;
            return n;
        }
        if (c >= 'A' && c <= 'Z') {
            n = c - 65 + 10;
            return n;
        }
        n = c - 97 + 10;
        return n;
    }

    static byte[] createKeyFromOldPassword(String passwd) throws NoSuchAlgorithmException {
        passwd = Security.makeScrambledPassword((String)passwd);
        int[] salt = Security.getSaltFromPassword((String)passwd);
        return Security.getBinaryPassword((int[])salt, (boolean)false);
    }

    static byte[] getBinaryPassword(int[] salt, boolean usingNewPasswords) throws NoSuchAlgorithmException {
        int val = 0;
        byte[] binaryPassword = new byte[20];
        if (usingNewPasswords) {
            int pos = 0;
            int i = 0;
            while (i < 4) {
                val = salt[i];
                for (int t = 3; t >= 0; val >>= 8, --t) {
                    binaryPassword[pos++] = (byte)(val & 255);
                }
                ++i;
            }
            return binaryPassword;
        }
        int offset = 0;
        int i = 0;
        do {
            if (i >= 2) {
                MessageDigest md = MessageDigest.getInstance((String)"SHA-1");
                md.update((byte[])binaryPassword, (int)0, (int)8);
                return md.digest();
            }
            val = salt[i];
            for (int t = 3; t >= 0; val >>= 8, --t) {
                binaryPassword[t + offset] = (byte)(val % 256);
            }
            offset += 4;
            ++i;
        } while (true);
    }

    private static int[] getSaltFromPassword(String password) {
        int[] result = new int[6];
        if (password == null) return result;
        if (password.length() == 0) {
            return result;
        }
        if (password.charAt((int)0) == '*') {
            String saltInHex = password.substring((int)1, (int)5);
            int val = 0;
            int i = 0;
            while (i < 4) {
                val = (val << 4) + Security.charVal((char)saltInHex.charAt((int)i));
                ++i;
            }
            return result;
        }
        int resultPos = 0;
        int pos = 0;
        int length = password.length();
        while (pos < length) {
            int val = 0;
            for (int i = 0; i < 8; ++i) {
                val = (val << 4) + Security.charVal((char)password.charAt((int)pos++));
            }
            result[resultPos++] = val;
        }
        return result;
    }

    private static String longToHex(long val) {
        String longHex = Long.toHexString((long)val);
        int length = longHex.length();
        if (length >= 8) return longHex.substring((int)0, (int)8);
        int padding = 8 - length;
        StringBuilder buf = new StringBuilder();
        int i = 0;
        do {
            if (i >= padding) {
                buf.append((String)longHex);
                return buf.toString();
            }
            buf.append((String)"0");
            ++i;
        } while (true);
    }

    static String makeScrambledPassword(String password) throws NoSuchAlgorithmException {
        long[] passwordHash = Util.hashPre41Password((String)password);
        StringBuilder scramble = new StringBuilder();
        scramble.append((String)Security.longToHex((long)passwordHash[0]));
        scramble.append((String)Security.longToHex((long)passwordHash[1]));
        return scramble.toString();
    }

    public static void xorString(byte[] from, byte[] to, byte[] scramble, int length) {
        int pos = 0;
        int scrambleLength = scramble.length;
        while (pos < length) {
            to[pos] = (byte)(from[pos] ^ scramble[pos % scrambleLength]);
            ++pos;
        }
    }

    static byte[] passwordHashStage1(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance((String)"SHA-1");
        StringBuilder cleansedPassword = new StringBuilder();
        int passwordLength = password.length();
        int i = 0;
        while (i < passwordLength) {
            char c = password.charAt((int)i);
            if (c != ' ' && c != '\t') {
                cleansedPassword.append((char)c);
            }
            ++i;
        }
        return md.digest((byte[])StringUtils.getBytes((String)cleansedPassword.toString()));
    }

    static byte[] passwordHashStage2(byte[] hashedPassword, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance((String)"SHA-1");
        md.update((byte[])salt, (int)0, (int)4);
        md.update((byte[])hashedPassword, (int)0, (int)20);
        return md.digest();
    }

    public static byte[] scramble411(String password, String seed, String passwordEncoding) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance((String)"SHA-1");
        byte[] passwordHashStage1 = md.digest((byte[])(passwordEncoding == null || passwordEncoding.length() == 0 ? StringUtils.getBytes((String)password) : StringUtils.getBytes((String)password, (String)passwordEncoding)));
        md.reset();
        byte[] passwordHashStage2 = md.digest((byte[])passwordHashStage1);
        md.reset();
        byte[] seedAsBytes = StringUtils.getBytes((String)seed, (String)"ASCII");
        md.update((byte[])seedAsBytes);
        md.update((byte[])passwordHashStage2);
        byte[] toBeXord = md.digest();
        int numToXor = toBeXord.length;
        int i = 0;
        while (i < numToXor) {
            toBeXord[i] = (byte)(toBeXord[i] ^ passwordHashStage1[i]);
            ++i;
        }
        return toBeXord;
    }

    public static byte[] scrambleCachingSha2(byte[] password, byte[] seed) throws DigestException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance((String)"SHA-256");
        }
        catch (NoSuchAlgorithmException ex) {
            throw new AssertionFailedException((Exception)ex);
        }
        byte[] dig1 = new byte[CACHING_SHA2_DIGEST_LENGTH];
        byte[] dig2 = new byte[CACHING_SHA2_DIGEST_LENGTH];
        byte[] scramble1 = new byte[CACHING_SHA2_DIGEST_LENGTH];
        md.update((byte[])password, (int)0, (int)password.length);
        md.digest((byte[])dig1, (int)0, (int)CACHING_SHA2_DIGEST_LENGTH);
        md.reset();
        md.update((byte[])dig1, (int)0, (int)dig1.length);
        md.digest((byte[])dig2, (int)0, (int)CACHING_SHA2_DIGEST_LENGTH);
        md.reset();
        md.update((byte[])dig2, (int)0, (int)dig1.length);
        md.update((byte[])seed, (int)0, (int)seed.length);
        md.digest((byte[])scramble1, (int)0, (int)CACHING_SHA2_DIGEST_LENGTH);
        byte[] mysqlScrambleBuff = new byte[CACHING_SHA2_DIGEST_LENGTH];
        Security.xorString((byte[])dig1, (byte[])mysqlScrambleBuff, (byte[])scramble1, (int)CACHING_SHA2_DIGEST_LENGTH);
        return mysqlScrambleBuff;
    }

    private Security() {
    }
}

