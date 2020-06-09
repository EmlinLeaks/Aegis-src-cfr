/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@GwtCompatible
public final class Preconditions {
    private Preconditions() {
    }

    public static void checkArgument(boolean expression) {
        if (expression) return;
        throw new IllegalArgumentException();
    }

    public static void checkArgument(boolean expression, @Nullable Object errorMessage) {
        if (expression) return;
        throw new IllegalArgumentException((String)String.valueOf((Object)errorMessage));
    }

    public static void checkArgument(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object ... errorMessageArgs) {
        if (expression) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])errorMessageArgs));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, char p1) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, int p1) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, long p1) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, char p1, char p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Character.valueOf((char)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, char p1, int p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Integer.valueOf((int)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, char p1, long p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Long.valueOf((long)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), p2}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, int p1, char p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Character.valueOf((char)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, int p1, int p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Integer.valueOf((int)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, int p1, long p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Long.valueOf((long)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), p2}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, long p1, char p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Character.valueOf((char)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, long p1, int p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Integer.valueOf((int)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, long p1, long p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Long.valueOf((long)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), p2}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Character.valueOf((char)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Integer.valueOf((int)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Long.valueOf((long)p2)}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2, p3}));
    }

    public static void checkArgument(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
        if (b) return;
        throw new IllegalArgumentException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2, p3, p4}));
    }

    public static void checkState(boolean expression) {
        if (expression) return;
        throw new IllegalStateException();
    }

    public static void checkState(boolean expression, @Nullable Object errorMessage) {
        if (expression) return;
        throw new IllegalStateException((String)String.valueOf((Object)errorMessage));
    }

    public static void checkState(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object ... errorMessageArgs) {
        if (expression) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])errorMessageArgs));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, char p1) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, int p1) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, long p1) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, char p1, char p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Character.valueOf((char)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, char p1, int p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Integer.valueOf((int)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, char p1, long p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Long.valueOf((long)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), p2}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, int p1, char p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Character.valueOf((char)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, int p1, int p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Integer.valueOf((int)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, int p1, long p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Long.valueOf((long)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), p2}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, long p1, char p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Character.valueOf((char)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, long p1, int p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Integer.valueOf((int)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, long p1, long p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Long.valueOf((long)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), p2}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Character.valueOf((char)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Integer.valueOf((int)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Long.valueOf((long)p2)}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2, p3}));
    }

    public static void checkState(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
        if (b) return;
        throw new IllegalStateException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2, p3, p4}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T reference) {
        if (reference != null) return (T)reference;
        throw new NullPointerException();
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {
        if (reference != null) return (T)reference;
        throw new NullPointerException((String)String.valueOf((Object)errorMessage));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T reference, @Nullable String errorMessageTemplate, @Nullable Object ... errorMessageArgs) {
        if (reference != null) return (T)reference;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])errorMessageArgs));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, char p1) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, int p1) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, long p1) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, char p1, char p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Character.valueOf((char)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, char p1, int p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Integer.valueOf((int)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, char p1, long p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), Long.valueOf((long)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Character.valueOf((char)p1), p2}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, int p1, char p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Character.valueOf((char)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, int p1, int p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Integer.valueOf((int)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, int p1, long p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), Long.valueOf((long)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Integer.valueOf((int)p1), p2}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, long p1, char p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Character.valueOf((char)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, long p1, int p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Integer.valueOf((int)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, long p1, long p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), Long.valueOf((long)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{Long.valueOf((long)p1), p2}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Character.valueOf((char)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Integer.valueOf((int)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, Long.valueOf((long)p2)}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2, p3}));
    }

    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
        if (obj != null) return (T)obj;
        throw new NullPointerException((String)Preconditions.format((String)errorMessageTemplate, (Object[])new Object[]{p1, p2, p3, p4}));
    }

    @CanIgnoreReturnValue
    public static int checkElementIndex(int index, int size) {
        return Preconditions.checkElementIndex((int)index, (int)size, (String)"index");
    }

    @CanIgnoreReturnValue
    public static int checkElementIndex(int index, int size, @Nullable String desc) {
        if (index < 0) throw new IndexOutOfBoundsException((String)Preconditions.badElementIndex((int)index, (int)size, (String)desc));
        if (index < size) return index;
        throw new IndexOutOfBoundsException((String)Preconditions.badElementIndex((int)index, (int)size, (String)desc));
    }

    private static String badElementIndex(int index, int size, String desc) {
        if (index < 0) {
            return Preconditions.format((String)"%s (%s) must not be negative", (Object[])new Object[]{desc, Integer.valueOf((int)index)});
        }
        if (size >= 0) return Preconditions.format((String)"%s (%s) must be less than size (%s)", (Object[])new Object[]{desc, Integer.valueOf((int)index), Integer.valueOf((int)size)});
        throw new IllegalArgumentException((String)("negative size: " + size));
    }

    @CanIgnoreReturnValue
    public static int checkPositionIndex(int index, int size) {
        return Preconditions.checkPositionIndex((int)index, (int)size, (String)"index");
    }

    @CanIgnoreReturnValue
    public static int checkPositionIndex(int index, int size, @Nullable String desc) {
        if (index < 0) throw new IndexOutOfBoundsException((String)Preconditions.badPositionIndex((int)index, (int)size, (String)desc));
        if (index <= size) return index;
        throw new IndexOutOfBoundsException((String)Preconditions.badPositionIndex((int)index, (int)size, (String)desc));
    }

    private static String badPositionIndex(int index, int size, String desc) {
        if (index < 0) {
            return Preconditions.format((String)"%s (%s) must not be negative", (Object[])new Object[]{desc, Integer.valueOf((int)index)});
        }
        if (size >= 0) return Preconditions.format((String)"%s (%s) must not be greater than size (%s)", (Object[])new Object[]{desc, Integer.valueOf((int)index), Integer.valueOf((int)size)});
        throw new IllegalArgumentException((String)("negative size: " + size));
    }

    public static void checkPositionIndexes(int start, int end, int size) {
        if (start < 0) throw new IndexOutOfBoundsException((String)Preconditions.badPositionIndexes((int)start, (int)end, (int)size));
        if (end < start) throw new IndexOutOfBoundsException((String)Preconditions.badPositionIndexes((int)start, (int)end, (int)size));
        if (end <= size) return;
        throw new IndexOutOfBoundsException((String)Preconditions.badPositionIndexes((int)start, (int)end, (int)size));
    }

    private static String badPositionIndexes(int start, int end, int size) {
        if (start < 0) return Preconditions.badPositionIndex((int)start, (int)size, (String)"start index");
        if (start > size) {
            return Preconditions.badPositionIndex((int)start, (int)size, (String)"start index");
        }
        if (end < 0) return Preconditions.badPositionIndex((int)end, (int)size, (String)"end index");
        if (end <= size) return Preconditions.format((String)"end index (%s) must not be less than start index (%s)", (Object[])new Object[]{Integer.valueOf((int)end), Integer.valueOf((int)start)});
        return Preconditions.badPositionIndex((int)end, (int)size, (String)"end index");
    }

    static String format(String template, @Nullable Object ... args) {
        int placeholderStart;
        template = String.valueOf((Object)template);
        StringBuilder builder = new StringBuilder((int)(template.length() + 16 * args.length));
        int templateStart = 0;
        int i = 0;
        while (i < args.length && (placeholderStart = template.indexOf((String)"%s", (int)templateStart)) != -1) {
            builder.append((CharSequence)template, (int)templateStart, (int)placeholderStart);
            builder.append((Object)args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append((CharSequence)template, (int)templateStart, (int)template.length());
        if (i >= args.length) return builder.toString();
        builder.append((String)" [");
        builder.append((Object)args[i++]);
        do {
            if (i >= args.length) {
                builder.append((char)']');
                return builder.toString();
            }
            builder.append((String)", ");
            builder.append((Object)args[i++]);
        } while (true);
    }
}

