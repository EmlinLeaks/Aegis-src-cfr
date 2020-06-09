/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
enum PublicSuffixType {
    PRIVATE((char)':', (char)','),
    ICANN((char)'!', (char)'?');
    
    private final char innerNodeCode;
    private final char leafNodeCode;

    private PublicSuffixType(char innerNodeCode, char leafNodeCode) {
        this.innerNodeCode = innerNodeCode;
        this.leafNodeCode = leafNodeCode;
    }

    char getLeafNodeCode() {
        return this.leafNodeCode;
    }

    char getInnerNodeCode() {
        return this.innerNodeCode;
    }

    static PublicSuffixType fromCode(char code) {
        PublicSuffixType[] arr$ = PublicSuffixType.values();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            PublicSuffixType value = arr$[i$];
            if (value.getInnerNodeCode() == code) return value;
            if (value.getLeafNodeCode() == code) {
                return value;
            }
            ++i$;
        }
        throw new IllegalArgumentException((String)("No enum corresponding to given code: " + code));
    }

    static PublicSuffixType fromIsPrivate(boolean isPrivate) {
        PublicSuffixType publicSuffixType;
        if (isPrivate) {
            publicSuffixType = PRIVATE;
            return publicSuffixType;
        }
        publicSuffixType = ICANN;
        return publicSuffixType;
    }
}

