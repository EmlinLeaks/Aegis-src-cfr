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

public class IntegerArgumentType
implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");
    private final int minimum;
    private final int maximum;

    private IntegerArgumentType(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static IntegerArgumentType integer() {
        return IntegerArgumentType.integer((int)Integer.MIN_VALUE);
    }

    public static IntegerArgumentType integer(int min) {
        return IntegerArgumentType.integer((int)min, (int)Integer.MAX_VALUE);
    }

    public static IntegerArgumentType integer(int min, int max) {
        return new IntegerArgumentType((int)min, (int)max);
    }

    public static int getInteger(CommandContext<?> context, String name) {
        return context.getArgument((String)name, Integer.TYPE).intValue();
    }

    public int getMinimum() {
        return this.minimum;
    }

    public int getMaximum() {
        return this.maximum;
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        int result = reader.readInt();
        if (result < this.minimum) {
            reader.setCursor((int)start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext((ImmutableStringReader)reader, (Object)Integer.valueOf((int)result), (Object)Integer.valueOf((int)this.minimum));
        }
        if (result <= this.maximum) return Integer.valueOf((int)result);
        reader.setCursor((int)start);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext((ImmutableStringReader)reader, (Object)Integer.valueOf((int)result), (Object)Integer.valueOf((int)this.maximum));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerArgumentType)) {
            return false;
        }
        IntegerArgumentType that = (IntegerArgumentType)o;
        if (this.maximum != that.maximum) return false;
        if (this.minimum != that.minimum) return false;
        return true;
    }

    public int hashCode() {
        return 31 * this.minimum + this.maximum;
    }

    public String toString() {
        if (this.minimum == Integer.MIN_VALUE && this.maximum == Integer.MAX_VALUE) {
            return "integer()";
        }
        if (this.maximum != Integer.MAX_VALUE) return "integer(" + this.minimum + ", " + this.maximum + ")";
        return "integer(" + this.minimum + ")";
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

