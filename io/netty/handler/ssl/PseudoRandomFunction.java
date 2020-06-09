/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class PseudoRandomFunction {
    private PseudoRandomFunction() {
    }

    static byte[] hash(byte[] secret, byte[] label, byte[] seed, int length, String algo) {
        if (length < 0) {
            throw new IllegalArgumentException((String)"You must provide a length greater than zero.");
        }
        try {
            byte[] data;
            Mac hmac = Mac.getInstance((String)algo);
            hmac.init((Key)new SecretKeySpec((byte[])secret, (String)algo));
            int iterations = (int)Math.ceil((double)((double)length / (double)hmac.getMacLength()));
            byte[] expansion = EmptyArrays.EMPTY_BYTES;
            byte[] A = data = PseudoRandomFunction.concat((byte[])label, (byte[])seed);
            int i = 0;
            while (i < iterations) {
                A = hmac.doFinal((byte[])A);
                expansion = PseudoRandomFunction.concat((byte[])expansion, (byte[])hmac.doFinal((byte[])PseudoRandomFunction.concat((byte[])A, (byte[])data)));
                ++i;
            }
            return Arrays.copyOf((byte[])expansion, (int)length);
        }
        catch (GeneralSecurityException e) {
            throw new IllegalArgumentException((String)("Could not find algo: " + algo), (Throwable)e);
        }
    }

    private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf((byte[])first, (int)(first.length + second.length));
        System.arraycopy((Object)second, (int)0, (Object)result, (int)first.length, (int)second.length);
        return result;
    }
}

