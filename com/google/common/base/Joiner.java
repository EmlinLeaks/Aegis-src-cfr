/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible
public class Joiner {
    private final String separator;

    public static Joiner on(String separator) {
        return new Joiner((String)separator);
    }

    public static Joiner on(char separator) {
        return new Joiner((String)String.valueOf((char)separator));
    }

    private Joiner(String separator) {
        this.separator = Preconditions.checkNotNull(separator);
    }

    private Joiner(Joiner prototype) {
        this.separator = prototype.separator;
    }

    @CanIgnoreReturnValue
    public <A extends Appendable> A appendTo(A appendable, Iterable<?> parts) throws IOException {
        return (A)this.appendTo(appendable, parts.iterator());
    }

    @CanIgnoreReturnValue
    public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts) throws IOException {
        Preconditions.checkNotNull(appendable);
        if (!parts.hasNext()) return (A)appendable;
        appendable.append((CharSequence)this.toString(parts.next()));
        while (parts.hasNext()) {
            appendable.append((CharSequence)this.separator);
            appendable.append((CharSequence)this.toString(parts.next()));
        }
        return (A)appendable;
    }

    @CanIgnoreReturnValue
    public final <A extends Appendable> A appendTo(A appendable, Object[] parts) throws IOException {
        return (A)this.appendTo(appendable, Arrays.asList(parts));
    }

    @CanIgnoreReturnValue
    public final <A extends Appendable> A appendTo(A appendable, @Nullable Object first, @Nullable Object second, Object ... rest) throws IOException {
        return (A)this.appendTo(appendable, Joiner.iterable((Object)first, (Object)second, (Object[])rest));
    }

    @CanIgnoreReturnValue
    public final StringBuilder appendTo(StringBuilder builder, Iterable<?> parts) {
        return this.appendTo((StringBuilder)builder, parts.iterator());
    }

    @CanIgnoreReturnValue
    public final StringBuilder appendTo(StringBuilder builder, Iterator<?> parts) {
        try {
            this.appendTo(builder, parts);
            return builder;
        }
        catch (IOException impossible) {
            throw new AssertionError((Object)impossible);
        }
    }

    @CanIgnoreReturnValue
    public final StringBuilder appendTo(StringBuilder builder, Object[] parts) {
        return this.appendTo((StringBuilder)builder, Arrays.asList(parts));
    }

    @CanIgnoreReturnValue
    public final StringBuilder appendTo(StringBuilder builder, @Nullable Object first, @Nullable Object second, Object ... rest) {
        return this.appendTo((StringBuilder)builder, Joiner.iterable((Object)first, (Object)second, (Object[])rest));
    }

    public final String join(Iterable<?> parts) {
        return this.join(parts.iterator());
    }

    public final String join(Iterator<?> parts) {
        return this.appendTo((StringBuilder)new StringBuilder(), parts).toString();
    }

    public final String join(Object[] parts) {
        return this.join(Arrays.asList(parts));
    }

    public final String join(@Nullable Object first, @Nullable Object second, Object ... rest) {
        return this.join(Joiner.iterable((Object)first, (Object)second, (Object[])rest));
    }

    public Joiner useForNull(String nullText) {
        Preconditions.checkNotNull(nullText);
        return new Joiner((Joiner)this, (Joiner)this, (String)nullText){
            final /* synthetic */ String val$nullText;
            final /* synthetic */ Joiner this$0;
            {
                this.this$0 = joiner;
                this.val$nullText = string;
                super((Joiner)x0);
            }

            CharSequence toString(@Nullable Object part) {
                CharSequence charSequence;
                if (part == null) {
                    charSequence = this.val$nullText;
                    return charSequence;
                }
                charSequence = this.this$0.toString((Object)part);
                return charSequence;
            }

            public Joiner useForNull(String nullText) {
                throw new java.lang.UnsupportedOperationException((String)"already specified useForNull");
            }

            public Joiner skipNulls() {
                throw new java.lang.UnsupportedOperationException((String)"already specified useForNull");
            }
        };
    }

    public Joiner skipNulls() {
        return new Joiner((Joiner)this, (Joiner)this){
            final /* synthetic */ Joiner this$0;
            {
                this.this$0 = joiner;
                super((Joiner)x0);
            }

            public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts) throws IOException {
                ? part;
                Preconditions.checkNotNull(appendable, (Object)"appendable");
                Preconditions.checkNotNull(parts, (Object)"parts");
                while (parts.hasNext()) {
                    part = parts.next();
                    if (part == null) continue;
                    appendable.append((CharSequence)this.this$0.toString(part));
                    break;
                }
                while (parts.hasNext()) {
                    part = parts.next();
                    if (part == null) continue;
                    appendable.append((CharSequence)Joiner.access$100((Joiner)this.this$0));
                    appendable.append((CharSequence)this.this$0.toString(part));
                }
                return (A)appendable;
            }

            public Joiner useForNull(String nullText) {
                throw new java.lang.UnsupportedOperationException((String)"already specified skipNulls");
            }

            public MapJoiner withKeyValueSeparator(String kvs) {
                throw new java.lang.UnsupportedOperationException((String)"can't use .skipNulls() with maps");
            }
        };
    }

    public MapJoiner withKeyValueSeparator(char keyValueSeparator) {
        return this.withKeyValueSeparator((String)String.valueOf((char)keyValueSeparator));
    }

    public MapJoiner withKeyValueSeparator(String keyValueSeparator) {
        return new MapJoiner((Joiner)this, (String)keyValueSeparator, null);
    }

    CharSequence toString(Object part) {
        CharSequence charSequence;
        Preconditions.checkNotNull(part);
        if (part instanceof CharSequence) {
            charSequence = (CharSequence)part;
            return charSequence;
        }
        charSequence = part.toString();
        return charSequence;
    }

    private static Iterable<Object> iterable(Object first, Object second, Object[] rest) {
        Preconditions.checkNotNull(rest);
        return new AbstractList<Object>((Object[])rest, (Object)first, (Object)second){
            final /* synthetic */ Object[] val$rest;
            final /* synthetic */ Object val$first;
            final /* synthetic */ Object val$second;
            {
                this.val$rest = arrobject;
                this.val$first = object;
                this.val$second = object2;
            }

            public int size() {
                return this.val$rest.length + 2;
            }

            public Object get(int index) {
                switch (index) {
                    case 0: {
                        return this.val$first;
                    }
                    case 1: {
                        return this.val$second;
                    }
                }
                return this.val$rest[index - 2];
            }
        };
    }

    static /* synthetic */ String access$100(Joiner x0) {
        return x0.separator;
    }
}

