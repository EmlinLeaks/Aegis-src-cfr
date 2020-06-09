/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.thirdparty.publicsuffix.PublicSuffixType;
import com.google.thirdparty.publicsuffix.TrieParser;

@GwtCompatible
@Beta
public final class PublicSuffixPatterns {
    public static final ImmutableMap<String, PublicSuffixType> EXACT = TrieParser.parseTrie((CharSequence)"longStr4[a&0&0trk9-]");
    public static final ImmutableMap<String, PublicSuffixType> UNDER = TrieParser.parseTrie((CharSequence)"d&b?uolc.etisotnegam,?e&k?noz.notirt,tatse.etupmoc,y??gp?h&k?s.mroftalp,?jf?k&c?f?rowten.secla,u.hcs??m&j?m?oc.&duolcbuhtig.&ipa,txe,?mme0,tne&tnocresuduolcbuhtig,yoj.snc,???nb?p&j.&a&mahokoy?yogan??ebok?i&adnes?kasawak??oroppas?uhsuykatik??n??r&b.mon?e??ten.cimonotpyrc,ug?w&k?z??zm??");
    public static final ImmutableMap<String, PublicSuffixType> EXCLUDED = TrieParser.parseTrie((CharSequence)"kc.www?pj.&a&mahokoy.ytic?yogan.ytic??ebok.ytic?i&adnes.ytic?kasawak.ytic??oroppas.ytic?uhsuykatik.ytic??zm.atadelet??");

    private PublicSuffixPatterns() {
    }
}

