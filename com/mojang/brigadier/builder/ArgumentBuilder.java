/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public abstract class ArgumentBuilder<S, T extends ArgumentBuilder<S, T>> {
    private final RootCommandNode<S> arguments = new RootCommandNode<S>();
    private Command<S> command;
    private Predicate<S> requirement = s -> true;
    private CommandNode<S> target;
    private RedirectModifier<S> modifier = null;
    private boolean forks;

    protected abstract T getThis();

    public T then(ArgumentBuilder<S, ?> argument) {
        if (this.target != null) {
            throw new IllegalStateException((String)"Cannot add children to a redirected node");
        }
        this.arguments.addChild(argument.build());
        return (T)this.getThis();
    }

    public T then(CommandNode<S> argument) {
        if (this.target != null) {
            throw new IllegalStateException((String)"Cannot add children to a redirected node");
        }
        this.arguments.addChild(argument);
        return (T)this.getThis();
    }

    public Collection<CommandNode<S>> getArguments() {
        return this.arguments.getChildren();
    }

    public T executes(Command<S> command) {
        this.command = command;
        return (T)this.getThis();
    }

    public Command<S> getCommand() {
        return this.command;
    }

    public T requires(Predicate<S> requirement) {
        this.requirement = requirement;
        return (T)this.getThis();
    }

    public Predicate<S> getRequirement() {
        return this.requirement;
    }

    public T redirect(CommandNode<S> target) {
        return (T)this.forward(target, null, (boolean)false);
    }

    public T redirect(CommandNode<S> target, SingleRedirectModifier<S> modifier) {
        RedirectModifier<S> redirectModifier;
        if (modifier == null) {
            redirectModifier = null;
            return (T)((T)this.forward(target, redirectModifier, (boolean)false));
        }
        redirectModifier = o -> Collections.singleton(modifier.apply(o));
        return (T)this.forward(target, redirectModifier, (boolean)false);
    }

    public T fork(CommandNode<S> target, RedirectModifier<S> modifier) {
        return (T)this.forward(target, modifier, (boolean)true);
    }

    public T forward(CommandNode<S> target, RedirectModifier<S> modifier, boolean fork) {
        if (!this.arguments.getChildren().isEmpty()) {
            throw new IllegalStateException((String)"Cannot forward a node with children");
        }
        this.target = target;
        this.modifier = modifier;
        this.forks = fork;
        return (T)this.getThis();
    }

    public CommandNode<S> getRedirect() {
        return this.target;
    }

    public RedirectModifier<S> getRedirectModifier() {
        return this.modifier;
    }

    public boolean isFork() {
        return this.forks;
    }

    public abstract CommandNode<S> build();
}

