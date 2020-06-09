/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.CommonPattern;
import com.google.common.base.Function;
import com.google.common.base.JdkPattern;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Predicates {
    private static final Joiner COMMA_JOINER = Joiner.on((char)',');

    private Predicates() {
    }

    @GwtCompatible(serializable=true)
    public static <T> Predicate<T> alwaysTrue() {
        return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
    }

    @GwtCompatible(serializable=true)
    public static <T> Predicate<T> alwaysFalse() {
        return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
    }

    @GwtCompatible(serializable=true)
    public static <T> Predicate<T> isNull() {
        return ObjectPredicate.IS_NULL.withNarrowedType();
    }

    @GwtCompatible(serializable=true)
    public static <T> Predicate<T> notNull() {
        return ObjectPredicate.NOT_NULL.withNarrowedType();
    }

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return new NotPredicate<T>(predicate);
    }

    public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> components) {
        return new AndPredicate<T>(Predicates.defensiveCopy(components), null);
    }

    public static <T> Predicate<T> and(Predicate<? super T> ... components) {
        return new AndPredicate<T>(Predicates.defensiveCopy(components), null);
    }

    public static <T> Predicate<T> and(Predicate<? super T> first, Predicate<? super T> second) {
        return new AndPredicate<T>(Predicates.asList(Preconditions.checkNotNull(first), Preconditions.checkNotNull(second)), null);
    }

    public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> components) {
        return new OrPredicate<T>(Predicates.defensiveCopy(components), null);
    }

    public static <T> Predicate<T> or(Predicate<? super T> ... components) {
        return new OrPredicate<T>(Predicates.defensiveCopy(components), null);
    }

    public static <T> Predicate<T> or(Predicate<? super T> first, Predicate<? super T> second) {
        return new OrPredicate<T>(Predicates.asList(Preconditions.checkNotNull(first), Preconditions.checkNotNull(second)), null);
    }

    public static <T> Predicate<T> equalTo(@Nullable T target) {
        IsEqualToPredicate<T> isEqualToPredicate;
        if (target == null) {
            isEqualToPredicate = Predicates.isNull();
            return isEqualToPredicate;
        }
        isEqualToPredicate = new IsEqualToPredicate<T>(target, null);
        return isEqualToPredicate;
    }

    @GwtIncompatible
    public static Predicate<Object> instanceOf(Class<?> clazz) {
        return new InstanceOfPredicate(clazz, null);
    }

    @Deprecated
    @GwtIncompatible
    @Beta
    public static Predicate<Class<?>> assignableFrom(Class<?> clazz) {
        return Predicates.subtypeOf(clazz);
    }

    @GwtIncompatible
    @Beta
    public static Predicate<Class<?>> subtypeOf(Class<?> clazz) {
        return new SubtypeOfPredicate(clazz, null);
    }

    public static <T> Predicate<T> in(Collection<? extends T> target) {
        return new InPredicate<T>(target, null);
    }

    public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function) {
        return new CompositionPredicate<A, B>(predicate, function, null);
    }

    @GwtIncompatible
    public static Predicate<CharSequence> containsPattern(String pattern) {
        return new ContainsPatternFromStringPredicate((String)pattern);
    }

    @GwtIncompatible(value="java.util.regex.Pattern")
    public static Predicate<CharSequence> contains(Pattern pattern) {
        return new ContainsPatternPredicate((CommonPattern)new JdkPattern((Pattern)pattern));
    }

    private static <T> List<Predicate<? super T>> asList(Predicate<? super T> first, Predicate<? super T> second) {
        return Arrays.asList(first, second);
    }

    private static <T> List<T> defensiveCopy(T ... array) {
        return Predicates.defensiveCopy(Arrays.asList(array));
    }

    static <T> List<T> defensiveCopy(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<T>();
        Iterator<T> i$ = iterable.iterator();
        while (i$.hasNext()) {
            T element = i$.next();
            list.add(Preconditions.checkNotNull(element));
        }
        return list;
    }

    static /* synthetic */ Joiner access$800() {
        return COMMA_JOINER;
    }
}

