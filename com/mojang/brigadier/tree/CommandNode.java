/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.tree;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CommandNode<S>
implements Comparable<CommandNode<S>> {
    private Map<String, CommandNode<S>> children = new LinkedHashMap<String, CommandNode<S>>();
    private Map<String, LiteralCommandNode<S>> literals = new LinkedHashMap<String, LiteralCommandNode<S>>();
    private Map<String, ArgumentCommandNode<S, ?>> arguments = new LinkedHashMap<String, ArgumentCommandNode<S, ?>>();
    private final Predicate<S> requirement;
    private final CommandNode<S> redirect;
    private final RedirectModifier<S> modifier;
    private final boolean forks;
    private Command<S> command;

    protected CommandNode(Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
        this.command = command;
        this.requirement = requirement;
        this.redirect = redirect;
        this.modifier = modifier;
        this.forks = forks;
    }

    public Command<S> getCommand() {
        return this.command;
    }

    public Collection<CommandNode<S>> getChildren() {
        return this.children.values();
    }

    public CommandNode<S> getChild(String name) {
        return this.children.get((Object)name);
    }

    public CommandNode<S> getRedirect() {
        return this.redirect;
    }

    public RedirectModifier<S> getRedirectModifier() {
        return this.modifier;
    }

    public boolean canUse(S source) {
        return this.requirement.test(source);
    }

    public void addChild(CommandNode<S> node) {
        if (node instanceof RootCommandNode) {
            throw new UnsupportedOperationException((String)"Cannot add a RootCommandNode as a child to any other CommandNode");
        }
        CommandNode<S> child = this.children.get((Object)node.getName());
        if (child != null) {
            if (node.getCommand() != null) {
                child.command = node.getCommand();
            }
            for (CommandNode<S> grandchild : node.getChildren()) {
                child.addChild(grandchild);
            }
        } else {
            this.children.put((String)node.getName(), node);
            if (node instanceof LiteralCommandNode) {
                this.literals.put((String)node.getName(), (LiteralCommandNode)node);
            } else if (node instanceof ArgumentCommandNode) {
                this.arguments.put((String)node.getName(), (ArgumentCommandNode)node);
            }
        }
        this.children = (Map)this.children.entrySet().stream().sorted(Map.Entry.<K, V>comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void findAmbiguities(AmbiguityConsumer<S> consumer) {
        HashSet<String> matches = new HashSet<String>();
        Iterator<CommandNode<S>> iterator = this.children.values().iterator();
        while (iterator.hasNext()) {
            CommandNode<S> child = iterator.next();
            for (CommandNode<S> sibling : this.children.values()) {
                if (child == sibling) continue;
                for (String input : child.getExamples()) {
                    if (!sibling.isValidInput((String)input)) continue;
                    matches.add((String)input);
                }
                if (matches.size() <= 0) continue;
                consumer.ambiguous(this, child, sibling, matches);
                matches = new HashSet<E>();
            }
            child.findAmbiguities(consumer);
        }
    }

    protected abstract boolean isValidInput(String var1);

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandNode)) {
            return false;
        }
        CommandNode that = (CommandNode)o;
        if (!this.children.equals(that.children)) {
            return false;
        }
        if (this.command != null) {
            if (this.command.equals(that.command)) return true;
            return false;
        }
        if (that.command == null) return true;
        return false;
    }

    public int hashCode() {
        int n;
        if (this.command != null) {
            n = this.command.hashCode();
            return 31 * this.children.hashCode() + n;
        }
        n = 0;
        return 31 * this.children.hashCode() + n;
    }

    public Predicate<S> getRequirement() {
        return this.requirement;
    }

    public abstract String getName();

    public abstract String getUsageText();

    public abstract void parse(StringReader var1, CommandContextBuilder<S> var2) throws CommandSyntaxException;

    public abstract CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) throws CommandSyntaxException;

    public abstract ArgumentBuilder<S, ?> createBuilder();

    protected abstract String getSortedKey();

    public Collection<? extends CommandNode<S>> getRelevantNodes(StringReader input) {
        if (this.literals.size() <= 0) return this.arguments.values();
        int cursor = input.getCursor();
        while (input.canRead() && input.peek() != ' ') {
            input.skip();
        }
        String text = input.getString().substring((int)cursor, (int)input.getCursor());
        input.setCursor((int)cursor);
        LiteralCommandNode<S> literal = this.literals.get((Object)text);
        if (literal == null) return this.arguments.values();
        return Collections.singleton(literal);
    }

    @Override
    public int compareTo(CommandNode<S> o) {
        if (this instanceof LiteralCommandNode == o instanceof LiteralCommandNode) {
            return this.getSortedKey().compareTo((String)o.getSortedKey());
        }
        if (!(o instanceof LiteralCommandNode)) return -1;
        return 1;
    }

    public boolean isFork() {
        return this.forks;
    }

    public abstract Collection<String> getExamples();
}

