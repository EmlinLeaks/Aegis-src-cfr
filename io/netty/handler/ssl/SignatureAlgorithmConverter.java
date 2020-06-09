/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SignatureAlgorithmConverter {
    private static final Pattern PATTERN = Pattern.compile((String)"(?:(^[a-zA-Z].+)With(.+)Encryption$)|(?:(^[a-zA-Z].+)(?:_with_|-with-|_pkcs1_|_pss_rsae_)(.+$))|(?:(^[a-zA-Z].+)_(.+$))");

    private SignatureAlgorithmConverter() {
    }

    static String toJavaName(String opensslName) {
        if (opensslName == null) {
            return null;
        }
        Matcher matcher = PATTERN.matcher((CharSequence)opensslName);
        if (!matcher.matches()) return null;
        String group1 = matcher.group((int)1);
        if (group1 != null) {
            return group1.toUpperCase((Locale)Locale.ROOT) + "with" + matcher.group((int)2).toUpperCase((Locale)Locale.ROOT);
        }
        if (matcher.group((int)3) != null) {
            return matcher.group((int)4).toUpperCase((Locale)Locale.ROOT) + "with" + matcher.group((int)3).toUpperCase((Locale)Locale.ROOT);
        }
        if (matcher.group((int)5) == null) return null;
        return matcher.group((int)6).toUpperCase((Locale)Locale.ROOT) + "with" + matcher.group((int)5).toUpperCase((Locale)Locale.ROOT);
    }
}

