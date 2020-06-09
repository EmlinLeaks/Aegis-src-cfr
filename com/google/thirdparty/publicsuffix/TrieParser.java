/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.thirdparty.publicsuffix.PublicSuffixType;
import java.util.List;

@GwtCompatible
final class TrieParser {
    private static final Joiner PREFIX_JOINER = Joiner.on((String)"");

    TrieParser() {
    }

    static ImmutableMap<String, PublicSuffixType> parseTrie(CharSequence encoded) {
        ImmutableMap.Builder<String, PublicSuffixType> builder = ImmutableMap.builder();
        int encodedLen = encoded.length();
        int idx = 0;
        while (idx < encodedLen) {
            idx += TrieParser.doParseTrieToBuilder(Lists.<CharSequence>newLinkedList(), (CharSequence)encoded.subSequence((int)idx, (int)encodedLen), builder);
        }
        return builder.build();
    }

    private static int doParseTrieToBuilder(List<CharSequence> stack, CharSequence encoded, ImmutableMap.Builder<String, PublicSuffixType> builder) {
        int idx;
        String domain;
        int encodedLen = encoded.length();
        char c = '\u0000';
        for (idx = 0; idx < encodedLen && (c = encoded.charAt((int)idx)) != '&' && c != '?' && c != '!' && c != ':' && c != ','; ++idx) {
        }
        stack.add((int)0, (CharSequence)TrieParser.reverse((CharSequence)encoded.subSequence((int)0, (int)idx)));
        if ((c == '!' || c == '?' || c == ':' || c == ',') && (domain = PREFIX_JOINER.join(stack)).length() > 0) {
            builder.put((String)domain, (PublicSuffixType)PublicSuffixType.fromCode((char)c));
        }
        ++idx;
        if (c != '?' && c != ',') {
            while (idx < encodedLen) {
                if (encoded.charAt((int)(idx += TrieParser.doParseTrieToBuilder(stack, (CharSequence)encoded.subSequence((int)idx, (int)encodedLen), builder))) != '?' && encoded.charAt((int)idx) != ',') continue;
                ++idx;
                break;
            }
        }
        stack.remove((int)0);
        return idx;
    }

    private static CharSequence reverse(CharSequence s) {
        return new StringBuilder((CharSequence)s).reverse();
    }
}

