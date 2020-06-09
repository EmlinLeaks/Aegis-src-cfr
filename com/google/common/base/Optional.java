/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Absent;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Present;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
public abstract class Optional<T>
implements Serializable {
    private static final long serialVersionUID = 0L;

    public static <T> Optional<T> absent() {
        return Absent.withType();
    }

    public static <T> Optional<T> of(T reference) {
        return new Present<T>(Preconditions.checkNotNull(reference));
    }

    public static <T> Optional<T> fromNullable(@Nullable T nullableReference) {
        Present<T> present;
        if (nullableReference == null) {
            present = Optional.absent();
            return present;
        }
        present = new Present<T>(nullableReference);
        return present;
    }

    Optional() {
    }

    public abstract boolean isPresent();

    public abstract T get();

    public abstract T or(T var1);

    public abstract Optional<T> or(Optional<? extends T> var1);

    @Beta
    public abstract T or(Supplier<? extends T> var1);

    @Nullable
    public abstract T orNull();

    public abstract Set<T> asSet();

    public abstract <V> Optional<V> transform(Function<? super T, V> var1);

    public abstract boolean equals(@Nullable Object var1);

    public abstract int hashCode();

    public abstract String toString();

    @Beta
    public static <T> Iterable<T> presentInstances(Iterable<? extends Optional<? extends T>> optionals) {
        Preconditions.checkNotNull(optionals);
        return new Iterable<T>(optionals){
            final /* synthetic */ Iterable val$optionals;
            {
                this.val$optionals = iterable;
            }

            public java.util.Iterator<T> iterator() {
                return new com.google.common.base.AbstractIterator<T>(this){
                    private final java.util.Iterator<? extends Optional<? extends T>> iterator;
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = var1_1;
                        this.iterator = Preconditions.checkNotNull(this.this$0.val$optionals.iterator());
                    }

                    protected T computeNext() {
                        Optional<T> optional;
                        do {
                            if (!this.iterator.hasNext()) return (T)this.endOfData();
                        } while (!(optional = this.iterator.next()).isPresent());
                        return (T)optional.get();
                    }
                };
            }
        };
    }
}

