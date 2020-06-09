/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.CharMatcher;
import com.google.common.base.CommonPattern;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.PatternCompiler;
import com.google.common.base.Platform;
import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class Platform {
    private static final Logger logger = Logger.getLogger((String)Platform.class.getName());
    private static final PatternCompiler patternCompiler = Platform.loadPatternCompiler();

    private Platform() {
    }

    static long systemNanoTime() {
        return System.nanoTime();
    }

    static CharMatcher precomputeCharMatcher(CharMatcher matcher) {
        return matcher.precomputedInternal();
    }

    static <T extends Enum<T>> Optional<T> getEnumIfPresent(Class<T> enumClass, String value) {
        Optional<T> optional;
        WeakReference<Enum<?>> ref = Enums.getEnumConstants(enumClass).get((Object)value);
        if (ref == null) {
            optional = Optional.absent();
            return optional;
        }
        optional = Optional.of(enumClass.cast(ref.get()));
        return optional;
    }

    static String formatCompact4Digits(double value) {
        return String.format((Locale)Locale.ROOT, (String)"%.4g", (Object[])new Object[]{Double.valueOf((double)value)});
    }

    static boolean stringIsNullOrEmpty(@Nullable String string) {
        if (string == null) return true;
        if (string.isEmpty()) return true;
        return false;
    }

    static CommonPattern compilePattern(String pattern) {
        Preconditions.checkNotNull(pattern);
        return patternCompiler.compile((String)pattern);
    }

    static boolean usingJdkPatternCompiler() {
        return patternCompiler instanceof JdkPatternCompiler;
    }

    private static PatternCompiler loadPatternCompiler() {
        return new JdkPatternCompiler(null);
    }

    private static void logPatternCompilerError(ServiceConfigurationError e) {
        logger.log((Level)Level.WARNING, (String)"Error loading regex compiler, falling back to next option", (Throwable)e);
    }
}

