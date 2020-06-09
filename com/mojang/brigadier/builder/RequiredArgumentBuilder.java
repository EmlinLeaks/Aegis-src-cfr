/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

public class RequiredArgumentBuilder<S, T>
extends ArgumentBuilder<S, RequiredArgumentBuilder<S, T>> {
    private final String name;
    private final ArgumentType<T> type;
    private SuggestionProvider<S> suggestionsProvider = null;

    private RequiredArgumentBuilder(String name, ArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <S, T> RequiredArgumentBuilder<S, T> argument(String name, ArgumentType<T> type) {
        return new RequiredArgumentBuilder<S, T>((String)name, type);
    }

    public RequiredArgumentBuilder<S, T> suggests(SuggestionProvider<S> provider) {
        this.suggestionsProvider = provider;
        return this.getThis();
    }

    public SuggestionProvider<S> getSuggestionsProvider() {
        return this.suggestionsProvider;
    }

    @Override
    protected RequiredArgumentBuilder<S, T> getThis() {
        return this;
    }

    public ArgumentType<T> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public ArgumentCommandNode<S, T> build() {
        ArgumentCommandNode<S, T> result = new ArgumentCommandNode<S, T>((String)this.getName(), this.getType(), this.getCommand(), this.getRequirement(), this.getRedirect(), this.getRedirectModifier(), (boolean)this.isFork(), this.getSuggestionsProvider());
        Iterator<CommandNode<S>> iterator = this.getArguments().iterator();
        while (iterator.hasNext()) {
            CommandNode<S> argument = iterator.next();
            result.addChild(argument);
        }
        return result;
    }
}

