/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.context;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandContextBuilder<S> {
    private final Map<String, ParsedArgument<S, ?>> arguments = new LinkedHashMap<String, ParsedArgument<S, ?>>();
    private final CommandNode<S> rootNode;
    private final List<ParsedCommandNode<S>> nodes = new ArrayList<ParsedCommandNode<S>>();
    private final CommandDispatcher<S> dispatcher;
    private S source;
    private Command<S> command;
    private CommandContextBuilder<S> child;
    private StringRange range;
    private RedirectModifier<S> modifier = null;
    private boolean forks;

    public CommandContextBuilder(CommandDispatcher<S> dispatcher, S source, CommandNode<S> rootNode, int start) {
        this.rootNode = rootNode;
        this.dispatcher = dispatcher;
        this.source = source;
        this.range = StringRange.at((int)start);
    }

    public CommandContextBuilder<S> withSource(S source) {
        this.source = source;
        return this;
    }

    public S getSource() {
        return (S)this.source;
    }

    public CommandNode<S> getRootNode() {
        return this.rootNode;
    }

    public CommandContextBuilder<S> withArgument(String name, ParsedArgument<S, ?> argument) {
        this.arguments.put((String)name, argument);
        return this;
    }

    public Map<String, ParsedArgument<S, ?>> getArguments() {
        return this.arguments;
    }

    public CommandContextBuilder<S> withCommand(Command<S> command) {
        this.command = command;
        return this;
    }

    public CommandContextBuilder<S> withNode(CommandNode<S> node, StringRange range) {
        this.nodes.add(new ParsedCommandNode<S>(node, (StringRange)range));
        this.range = StringRange.encompassing((StringRange)this.range, (StringRange)range);
        this.modifier = node.getRedirectModifier();
        this.forks = node.isFork();
        return this;
    }

    public CommandContextBuilder<S> copy() {
        CommandContextBuilder<S> copy = new CommandContextBuilder<S>(this.dispatcher, this.source, this.rootNode, (int)this.range.getStart());
        copy.command = this.command;
        copy.arguments.putAll(this.arguments);
        copy.nodes.addAll(this.nodes);
        copy.child = this.child;
        copy.range = this.range;
        copy.forks = this.forks;
        return copy;
    }

    public CommandContextBuilder<S> withChild(CommandContextBuilder<S> child) {
        this.child = child;
        return this;
    }

    public CommandContextBuilder<S> getChild() {
        return this.child;
    }

    public CommandContextBuilder<S> getLastChild() {
        CommandContextBuilder<S> result = this;
        while (result.getChild() != null) {
            result = result.getChild();
        }
        return result;
    }

    public Command<S> getCommand() {
        return this.command;
    }

    public List<ParsedCommandNode<S>> getNodes() {
        return this.nodes;
    }

    public CommandContext<S> build(String input) {
        CommandContext<S> commandContext;
        if (this.child == null) {
            commandContext = null;
            return new CommandContext<S>(this.source, (String)input, this.arguments, this.command, this.rootNode, this.nodes, (StringRange)this.range, commandContext, this.modifier, (boolean)this.forks);
        }
        commandContext = this.child.build((String)input);
        return new CommandContext<S>(this.source, (String)input, this.arguments, this.command, this.rootNode, this.nodes, (StringRange)this.range, commandContext, this.modifier, (boolean)this.forks);
    }

    public CommandDispatcher<S> getDispatcher() {
        return this.dispatcher;
    }

    public StringRange getRange() {
        return this.range;
    }

    public SuggestionContext<S> findSuggestionContext(int cursor) {
        if (this.range.getStart() > cursor) throw new IllegalStateException((String)"Can't find node before cursor");
        if (this.range.getEnd() < cursor) {
            if (this.child != null) {
                return this.child.findSuggestionContext((int)cursor);
            }
            if (this.nodes.isEmpty()) return new SuggestionContext<S>(this.rootNode, (int)this.range.getStart());
            ParsedCommandNode<S> last = this.nodes.get((int)(this.nodes.size() - 1));
            return new SuggestionContext<S>(last.getNode(), (int)(last.getRange().getEnd() + 1));
        }
        CommandNode<S> prev = this.rootNode;
        Iterator<ParsedCommandNode<S>> iterator = this.nodes.iterator();
        do {
            if (!iterator.hasNext()) {
                if (prev != null) return new SuggestionContext<S>(prev, (int)this.range.getStart());
                throw new IllegalStateException((String)"Can't find node before cursor");
            }
            ParsedCommandNode<S> node = iterator.next();
            StringRange nodeRange = node.getRange();
            if (nodeRange.getStart() <= cursor && cursor <= nodeRange.getEnd()) {
                return new SuggestionContext<S>(prev, (int)nodeRange.getStart());
            }
            prev = node.getNode();
        } while (true);
    }
}

