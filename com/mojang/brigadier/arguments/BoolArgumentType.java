/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BoolArgumentType
implements ArgumentType<Boolean> {
    private static final Collection<String> EXAMPLES = Arrays.asList("true", "false");

    private BoolArgumentType() {
    }

    public static BoolArgumentType bool() {
        return new BoolArgumentType();
    }

    public static boolean getBool(CommandContext<?> context, String name) {
        return context.getArgument((String)name, Boolean.class).booleanValue();
    }

    @Override
    public Boolean parse(StringReader reader) throws CommandSyntaxException {
        return Boolean.valueOf((boolean)reader.readBoolean());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if ("true".startsWith((String)builder.getRemaining().toLowerCase())) {
            builder.suggest((String)"true");
        }
        if (!"false".startsWith((String)builder.getRemaining().toLowerCase())) return builder.buildFuture();
        builder.suggest((String)"false");
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

