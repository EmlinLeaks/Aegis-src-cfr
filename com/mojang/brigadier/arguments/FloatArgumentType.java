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

public class FloatArgumentType
implements ArgumentType<Float> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");
    private final float minimum;
    private final float maximum;

    private FloatArgumentType(float minimum, float maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static FloatArgumentType floatArg() {
        return FloatArgumentType.floatArg((float)-3.4028235E38f);
    }

    public static FloatArgumentType floatArg(float min) {
        return FloatArgumentType.floatArg((float)min, (float)Float.MAX_VALUE);
    }

    public static FloatArgumentType floatArg(float min, float max) {
        return new FloatArgumentType((float)min, (float)max);
    }

    public static float getFloat(CommandContext<?> context, String name) {
        return context.getArgument((String)name, Float.class).floatValue();
    }

    public float getMinimum() {
        return this.minimum;
    }

    public float getMaximum() {
        return this.maximum;
    }

    @Override
    public Float parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        float result = reader.readFloat();
        if (result < this.minimum) {
            reader.setCursor((int)start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooLow().createWithContext((ImmutableStringReader)reader, (Object)Float.valueOf((float)result), (Object)Float.valueOf((float)this.minimum));
        }
        if (!(result > this.maximum)) return Float.valueOf((float)result);
        reader.setCursor((int)start);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooHigh().createWithContext((ImmutableStringReader)reader, (Object)Float.valueOf((float)result), (Object)Float.valueOf((float)this.maximum));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FloatArgumentType)) {
            return false;
        }
        FloatArgumentType that = (FloatArgumentType)o;
        if (this.maximum != that.maximum) return false;
        if (this.minimum != that.minimum) return false;
        return true;
    }

    public int hashCode() {
        return (int)(31.0f * this.minimum + this.maximum);
    }

    public String toString() {
        if (this.minimum == -3.4028235E38f && this.maximum == Float.MAX_VALUE) {
            return "float()";
        }
        if (this.maximum != Float.MAX_VALUE) return "float(" + this.minimum + ", " + this.maximum + ")";
        return "float(" + this.minimum + ")";
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

