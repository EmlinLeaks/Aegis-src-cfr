/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class LiteralCommandNode<S>
extends CommandNode<S> {
    private final String literal;

    public LiteralCommandNode(String literal, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
        super(command, requirement, redirect, modifier, (boolean)forks);
        this.literal = literal;
    }

    public String getLiteral() {
        return this.literal;
    }

    @Override
    public String getName() {
        return this.literal;
    }

    @Override
    public void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        int start = reader.getCursor();
        int end = this.parse((StringReader)reader);
        if (end <= -1) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext((ImmutableStringReader)reader, (Object)this.literal);
        contextBuilder.withNode(this, (StringRange)StringRange.between((int)start, (int)end));
    }

    private int parse(StringReader reader) {
        int start = reader.getCursor();
        if (!reader.canRead((int)this.literal.length())) return -1;
        int end = start + this.literal.length();
        if (!reader.getString().substring((int)start, (int)end).equals((Object)this.literal)) return -1;
        reader.setCursor((int)end);
        if (!reader.canRead()) return end;
        if (reader.peek() == ' ') {
            return end;
        }
        reader.setCursor((int)start);
        return -1;
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!this.literal.toLowerCase().startsWith((String)builder.getRemaining().toLowerCase())) return Suggestions.empty();
        return builder.suggest((String)this.literal).buildFuture();
    }

    @Override
    public boolean isValidInput(String input) {
        if (this.parse((StringReader)new StringReader((String)input)) <= -1) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LiteralCommandNode)) {
            return false;
        }
        LiteralCommandNode that = (LiteralCommandNode)o;
        if (this.literal.equals((Object)that.literal)) return super.equals((Object)o);
        return false;
    }

    @Override
    public String getUsageText() {
        return this.literal;
    }

    @Override
    public int hashCode() {
        int result = this.literal.hashCode();
        return 31 * result + super.hashCode();
    }

    public LiteralArgumentBuilder<S> createBuilder() {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal((String)this.literal);
        builder.requires(this.getRequirement());
        builder.forward(this.getRedirect(), this.getRedirectModifier(), (boolean)this.isFork());
        if (this.getCommand() == null) return builder;
        builder.executes(this.getCommand());
        return builder;
    }

    @Override
    protected String getSortedKey() {
        return this.literal;
    }

    @Override
    public Collection<String> getExamples() {
        return Collections.singleton(this.literal);
    }

    public String toString() {
        return "<literal " + this.literal + ">";
    }
}

