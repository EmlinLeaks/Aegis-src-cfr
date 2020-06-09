/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.thirdparty.publicsuffix.PublicSuffixPatterns;
import com.google.thirdparty.publicsuffix.PublicSuffixType;
import java.util.List;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class InternetDomainName {
    private static final CharMatcher DOTS_MATCHER = CharMatcher.anyOf((CharSequence)".\u3002\uff0e\uff61");
    private static final Splitter DOT_SPLITTER = Splitter.on((char)'.');
    private static final Joiner DOT_JOINER = Joiner.on((char)'.');
    private static final int NO_PUBLIC_SUFFIX_FOUND = -1;
    private static final String DOT_REGEX = "\\.";
    private static final int MAX_PARTS = 127;
    private static final int MAX_LENGTH = 253;
    private static final int MAX_DOMAIN_PART_LENGTH = 63;
    private final String name;
    private final ImmutableList<String> parts;
    private final int publicSuffixIndex;
    private static final CharMatcher DASH_MATCHER = CharMatcher.anyOf((CharSequence)"-_");
    private static final CharMatcher PART_CHAR_MATCHER = CharMatcher.javaLetterOrDigit().or((CharMatcher)DASH_MATCHER);

    InternetDomainName(String name) {
        name = Ascii.toLowerCase((String)DOTS_MATCHER.replaceFrom((CharSequence)name, (char)'.'));
        if (name.endsWith((String)".")) {
            name = name.substring((int)0, (int)(name.length() - 1));
        }
        Preconditions.checkArgument((boolean)(name.length() <= 253), (String)"Domain name too long: '%s':", (Object)name);
        this.name = name;
        this.parts = ImmutableList.copyOf(DOT_SPLITTER.split((CharSequence)name));
        Preconditions.checkArgument((boolean)(this.parts.size() <= 127), (String)"Domain has too many parts: '%s'", (Object)name);
        Preconditions.checkArgument((boolean)InternetDomainName.validateSyntax(this.parts), (String)"Not a valid domain name: '%s'", (Object)name);
        this.publicSuffixIndex = this.findPublicSuffix();
    }

    private int findPublicSuffix() {
        int partsSize = this.parts.size();
        int i = 0;
        while (i < partsSize) {
            String ancestorName = DOT_JOINER.join(this.parts.subList((int)i, (int)partsSize));
            if (PublicSuffixPatterns.EXACT.containsKey((Object)ancestorName)) {
                return i;
            }
            if (PublicSuffixPatterns.EXCLUDED.containsKey((Object)ancestorName)) {
                return i + 1;
            }
            if (InternetDomainName.matchesWildcardPublicSuffix((String)ancestorName)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static InternetDomainName from(String domain) {
        return new InternetDomainName((String)Preconditions.checkNotNull(domain));
    }

    private static boolean validateSyntax(List<String> parts) {
        int lastIndex = parts.size() - 1;
        if (!InternetDomainName.validatePart((String)parts.get((int)lastIndex), (boolean)true)) {
            return false;
        }
        int i = 0;
        while (i < lastIndex) {
            String part = parts.get((int)i);
            if (!InternetDomainName.validatePart((String)part, (boolean)false)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private static boolean validatePart(String part, boolean isFinalPart) {
        if (part.length() < 1) return false;
        if (part.length() > 63) {
            return false;
        }
        String asciiChars = CharMatcher.ascii().retainFrom((CharSequence)part);
        if (!PART_CHAR_MATCHER.matchesAllOf((CharSequence)asciiChars)) {
            return false;
        }
        if (DASH_MATCHER.matches((char)part.charAt((int)0))) return false;
        if (DASH_MATCHER.matches((char)part.charAt((int)(part.length() - 1)))) {
            return false;
        }
        if (!isFinalPart) return true;
        if (!CharMatcher.digit().matches((char)part.charAt((int)0))) return true;
        return false;
    }

    public ImmutableList<String> parts() {
        return this.parts;
    }

    public boolean isPublicSuffix() {
        if (this.publicSuffixIndex != 0) return false;
        return true;
    }

    public boolean hasPublicSuffix() {
        if (this.publicSuffixIndex == -1) return false;
        return true;
    }

    public InternetDomainName publicSuffix() {
        if (!this.hasPublicSuffix()) return null;
        InternetDomainName internetDomainName = this.ancestor((int)this.publicSuffixIndex);
        return internetDomainName;
    }

    public boolean isUnderPublicSuffix() {
        if (this.publicSuffixIndex <= 0) return false;
        return true;
    }

    public boolean isTopPrivateDomain() {
        if (this.publicSuffixIndex != 1) return false;
        return true;
    }

    public InternetDomainName topPrivateDomain() {
        if (this.isTopPrivateDomain()) {
            return this;
        }
        Preconditions.checkState((boolean)this.isUnderPublicSuffix(), (String)"Not under a public suffix: %s", (Object)this.name);
        return this.ancestor((int)(this.publicSuffixIndex - 1));
    }

    public boolean hasParent() {
        if (this.parts.size() <= 1) return false;
        return true;
    }

    public InternetDomainName parent() {
        Preconditions.checkState((boolean)this.hasParent(), (String)"Domain '%s' has no parent", (Object)this.name);
        return this.ancestor((int)1);
    }

    private InternetDomainName ancestor(int levels) {
        return InternetDomainName.from((String)DOT_JOINER.join(this.parts.subList((int)levels, (int)this.parts.size())));
    }

    public InternetDomainName child(String leftParts) {
        return InternetDomainName.from((String)(Preconditions.checkNotNull(leftParts) + "." + this.name));
    }

    public static boolean isValid(String name) {
        try {
            InternetDomainName.from((String)name);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean matchesWildcardPublicSuffix(String domain) {
        String[] pieces = domain.split((String)DOT_REGEX, (int)2);
        if (pieces.length != 2) return false;
        if (!PublicSuffixPatterns.UNDER.containsKey((Object)pieces[1])) return false;
        return true;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof InternetDomainName)) return false;
        InternetDomainName that = (InternetDomainName)object;
        return this.name.equals((Object)that.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

