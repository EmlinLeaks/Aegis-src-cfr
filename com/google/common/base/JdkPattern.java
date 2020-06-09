/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.CommonMatcher;
import com.google.common.base.CommonPattern;
import com.google.common.base.JdkPattern;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@GwtIncompatible
final class JdkPattern
extends CommonPattern
implements Serializable {
    private final Pattern pattern;
    private static final long serialVersionUID = 0L;

    JdkPattern(Pattern pattern) {
        this.pattern = Preconditions.checkNotNull(pattern);
    }

    @Override
    CommonMatcher matcher(CharSequence t) {
        return new JdkMatcher((Matcher)this.pattern.matcher((CharSequence)t));
    }

    @Override
    String pattern() {
        return this.pattern.pattern();
    }

    @Override
    int flags() {
        return this.pattern.flags();
    }

    @Override
    public String toString() {
        return this.pattern.toString();
    }

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof JdkPattern) return this.pattern.equals((Object)((JdkPattern)o).pattern);
        return false;
    }
}

