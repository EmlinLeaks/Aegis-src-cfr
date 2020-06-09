/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.context;

import com.mojang.brigadier.context.StringRange;
import java.util.Objects;

public class ParsedArgument<S, T> {
    private final StringRange range;
    private final T result;

    public ParsedArgument(int start, int end, T result) {
        this.range = StringRange.between((int)start, (int)end);
        this.result = result;
    }

    public StringRange getRange() {
        return this.range;
    }

    public T getResult() {
        return (T)this.result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParsedArgument)) {
            return false;
        }
        ParsedArgument that = (ParsedArgument)o;
        if (!Objects.equals((Object)this.range, (Object)that.range)) return false;
        if (!Objects.equals(this.result, that.result)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.range, this.result});
    }
}

