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

public class DoubleArgumentType
implements ArgumentType<Double> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");
    private final double minimum;
    private final double maximum;

    private DoubleArgumentType(double minimum, double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static DoubleArgumentType doubleArg() {
        return DoubleArgumentType.doubleArg((double)-1.7976931348623157E308);
    }

    public static DoubleArgumentType doubleArg(double min) {
        return DoubleArgumentType.doubleArg((double)min, (double)Double.MAX_VALUE);
    }

    public static DoubleArgumentType doubleArg(double min, double max) {
        return new DoubleArgumentType((double)min, (double)max);
    }

    public static double getDouble(CommandContext<?> context, String name) {
        return context.getArgument((String)name, Double.class).doubleValue();
    }

    public double getMinimum() {
        return this.minimum;
    }

    public double getMaximum() {
        return this.maximum;
    }

    @Override
    public Double parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        double result = reader.readDouble();
        if (result < this.minimum) {
            reader.setCursor((int)start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooLow().createWithContext((ImmutableStringReader)reader, (Object)Double.valueOf((double)result), (Object)Double.valueOf((double)this.minimum));
        }
        if (!(result > this.maximum)) return Double.valueOf((double)result);
        reader.setCursor((int)start);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooHigh().createWithContext((ImmutableStringReader)reader, (Object)Double.valueOf((double)result), (Object)Double.valueOf((double)this.maximum));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoubleArgumentType)) {
            return false;
        }
        DoubleArgumentType that = (DoubleArgumentType)o;
        if (this.maximum != that.maximum) return false;
        if (this.minimum != that.minimum) return false;
        return true;
    }

    public int hashCode() {
        return (int)(31.0 * this.minimum + this.maximum);
    }

    public String toString() {
        if (this.minimum == -1.7976931348623157E308 && this.maximum == Double.MAX_VALUE) {
            return "double()";
        }
        if (this.maximum != Double.MAX_VALUE) return "double(" + this.minimum + ", " + this.maximum + ")";
        return "double(" + this.minimum + ")";
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

