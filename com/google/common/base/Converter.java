/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Converter;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Converter<A, B>
implements Function<A, B> {
    private final boolean handleNullAutomatically;
    @LazyInit
    private transient Converter<B, A> reverse;

    protected Converter() {
        this((boolean)true);
    }

    Converter(boolean handleNullAutomatically) {
        this.handleNullAutomatically = handleNullAutomatically;
    }

    protected abstract B doForward(A var1);

    protected abstract A doBackward(B var1);

    @Nullable
    @CanIgnoreReturnValue
    public final B convert(@Nullable A a) {
        return (B)this.correctedDoForward(a);
    }

    @Nullable
    B correctedDoForward(@Nullable A a) {
        B b;
        if (!this.handleNullAutomatically) return (B)this.doForward(a);
        if (a == null) {
            b = null;
            return (B)((B)b);
        }
        b = (B)Preconditions.checkNotNull(this.doForward(a));
        return (B)b;
    }

    @Nullable
    A correctedDoBackward(@Nullable B b) {
        A a;
        if (!this.handleNullAutomatically) return (A)this.doBackward(b);
        if (b == null) {
            a = null;
            return (A)((A)a);
        }
        a = (A)Preconditions.checkNotNull(this.doBackward(b));
        return (A)a;
    }

    @CanIgnoreReturnValue
    public Iterable<B> convertAll(Iterable<? extends A> fromIterable) {
        Preconditions.checkNotNull(fromIterable, (Object)"fromIterable");
        return new Iterable<B>((Converter)this, fromIterable){
            final /* synthetic */ Iterable val$fromIterable;
            final /* synthetic */ Converter this$0;
            {
                this.this$0 = converter;
                this.val$fromIterable = iterable;
            }

            public java.util.Iterator<B> iterator() {
                return new java.util.Iterator<B>(this){
                    private final java.util.Iterator<? extends A> fromIterator;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                        this.fromIterator = this.this$1.val$fromIterable.iterator();
                    }

                    public boolean hasNext() {
                        return this.fromIterator.hasNext();
                    }

                    public B next() {
                        return (B)this.this$1.this$0.convert(this.fromIterator.next());
                    }

                    public void remove() {
                        this.fromIterator.remove();
                    }
                };
            }
        };
    }

    @CanIgnoreReturnValue
    public Converter<B, A> reverse() {
        Converter<B, A> converter;
        Converter<B, A> result = this.reverse;
        if (result == null) {
            converter = this.reverse = new ReverseConverter<A, B>(this);
            return converter;
        }
        converter = result;
        return converter;
    }

    public final <C> Converter<A, C> andThen(Converter<B, C> secondConverter) {
        return this.doAndThen(secondConverter);
    }

    <C> Converter<A, C> doAndThen(Converter<B, C> secondConverter) {
        return new ConverterComposition<A, B, C>(this, Preconditions.checkNotNull(secondConverter));
    }

    @Deprecated
    @Nullable
    @CanIgnoreReturnValue
    @Override
    public final B apply(@Nullable A a) {
        return (B)this.convert(a);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return super.equals((Object)object);
    }

    public static <A, B> Converter<A, B> from(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction) {
        return new FunctionBasedConverter<A, B>(forwardFunction, backwardFunction, null);
    }

    public static <T> Converter<T, T> identity() {
        return IdentityConverter.INSTANCE;
    }
}

