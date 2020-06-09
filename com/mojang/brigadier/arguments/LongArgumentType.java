/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.arguments;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.Arrays;
import java.util.Collection;

public class LongArgumentType
implements ArgumentType<Long> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");
    private final long minimum;
    private final long maximum;

    private LongArgumentType(long minimum, long maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static LongArgumentType longArg() {
        return LongArgumentType.longArg((long)Long.MIN_VALUE);
    }

    public static LongArgumentType longArg(long min) {
        return LongArgumentType.longArg((long)min, (long)Long.MAX_VALUE);
    }

    public static LongArgumentType longArg(long min, long max) {
        return new LongArgumentType((long)min, (long)max);
    }

    public static long getLong(CommandContext<?> context, String name) {
        return context.getArgument((String)name, Long.TYPE).longValue();
    }

    public long getMinimum() {
        return this.minimum;
    }

    public long getMaximum() {
        return this.maximum;
    }

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        long result = reader.readLong();
        if (result < this.minimum) {
            reader.setCursor((int)start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooLow().createWithContext((ImmutableStringReader)reader, (Object)Long.valueOf((long)result), (Object)Long.valueOf((long)this.minimum));
        }
        if (result <= this.maximum) return Long.valueOf((long)result);
        reader.setCursor((int)start);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooHigh().createWithContext((ImmutableStringReader)reader, (Object)Long.valueOf((long)result), (Object)Long.valueOf((long)this.maximum));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LongArgumentType)) {
            return false;
        }
        LongArgumentType that = (LongArgumentType)o;
        if (this.maximum != that.maximum) return false;
        if (this.minimum != that.minimum) return false;
        return true;
    }

    public int hashCode() {
        return 31 * Long.hashCode((long)this.minimum) + Long.hashCode((long)this.maximum);
    }

    public String toString() {
        if (this.minimum == Long.MIN_VALUE && this.maximum == Long.MAX_VALUE) {
            return "longArg()";
        }
        if (this.maximum != Long.MAX_VALUE) return "longArg(" + this.minimum + ", " + this.maximum + ")";
        return "longArg(" + this.minimum + ")";
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

