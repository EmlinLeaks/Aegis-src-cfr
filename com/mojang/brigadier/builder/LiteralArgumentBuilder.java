/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

public class LiteralArgumentBuilder<S>
extends ArgumentBuilder<S, LiteralArgumentBuilder<S>> {
    private final String literal;

    protected LiteralArgumentBuilder(String literal) {
        this.literal = literal;
    }

    public static <S> LiteralArgumentBuilder<S> literal(String name) {
        return new LiteralArgumentBuilder<S>((String)name);
    }

    @Override
    protected LiteralArgumentBuilder<S> getThis() {
        return this;
    }

    public String getLiteral() {
        return this.literal;
    }

    @Override
    public LiteralCommandNode<S> build() {
        LiteralCommandNode<S> result = new LiteralCommandNode<S>((String)this.getLiteral(), this.getCommand(), this.getRequirement(), this.getRedirect(), this.getRedirectModifier(), (boolean)this.isFork());
        Iterator<CommandNode<S>> iterator = this.getArguments().iterator();
        while (iterator.hasNext()) {
            CommandNode<S> argument = iterator.next();
            result.addChild(argument);
        }
        return result;
    }
}

