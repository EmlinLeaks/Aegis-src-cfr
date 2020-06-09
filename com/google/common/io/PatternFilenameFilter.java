/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class PatternFilenameFilter
implements FilenameFilter {
    private final Pattern pattern;

    public PatternFilenameFilter(String patternStr) {
        this((Pattern)Pattern.compile((String)patternStr));
    }

    public PatternFilenameFilter(Pattern pattern) {
        this.pattern = Preconditions.checkNotNull(pattern);
    }

    @Override
    public boolean accept(@Nullable File dir, String fileName) {
        return this.pattern.matcher((CharSequence)fileName).matches();
    }
}

