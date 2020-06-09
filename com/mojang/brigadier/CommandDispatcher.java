/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandDispatcher<S> {
    public static final String ARGUMENT_SEPARATOR = " ";
    public static final char ARGUMENT_SEPARATOR_CHAR = ' ';
    private static final String USAGE_OPTIONAL_OPEN = "[";
    private static final String USAGE_OPTIONAL_CLOSE = "]";
    private static final String USAGE_REQUIRED_OPEN = "(";
    private static final String USAGE_REQUIRED_CLOSE = ")";
    private static final String USAGE_OR = "|";
    private final RootCommandNode<S> root;
    private final Predicate<CommandNode<S>> hasCommand = new Predicate<CommandNode<S>>((CommandDispatcher)this){
        final /* synthetic */ CommandDispatcher this$0;
        {
            this.this$0 = this$0;
        }

        public boolean test(CommandNode<S> input) {
            if (input == null) return false;
            if (input.getCommand() != null) return true;
            if (!input.getChildren().stream().anyMatch(CommandDispatcher.access$000((CommandDispatcher)this.this$0))) return false;
            return true;
        }
    };
    private ResultConsumer<S> consumer = (c, s, r) -> {};

    public CommandDispatcher(RootCommandNode<S> root) {
        this.root = root;
    }

    public CommandDispatcher() {
        this(new RootCommandNode<S>());
    }

    public LiteralCommandNode<S> register(LiteralArgumentBuilder<S> command) {
        CommandNode build = command.build();
        this.root.addChild(build);
        return build;
    }

    public void setConsumer(ResultConsumer<S> consumer) {
        this.consumer = consumer;
    }

    public int execute(String input, S source) throws CommandSyntaxException {
        return this.execute((StringReader)new StringReader((String)input), source);
    }

    public int execute(StringReader input, S source) throws CommandSyntaxException {
        ParseResults<S> parse = this.parse((StringReader)input, source);
        return this.execute(parse);
    }

    public int execute(ParseResults<S> parse) throws CommandSyntaxException {
        if (parse.getReader().canRead()) {
            if (parse.getExceptions().size() == 1) {
                throw parse.getExceptions().values().iterator().next();
            }
            if (!parse.getContext().getRange().isEmpty()) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext((ImmutableStringReader)parse.getReader());
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext((ImmutableStringReader)parse.getReader());
        }
        int result = 0;
        int successfulForks = 0;
        boolean forked = false;
        boolean foundCommand = false;
        String command = parse.getReader().getString();
        CommandContext<S> original = parse.getContext().build((String)command);
        List<CommandContext<S>> contexts = Collections.singletonList(original);
        ArrayList<CommandContext<S>> next = null;
        do {
            int size;
            if (contexts != null) {
                size = contexts.size();
            } else {
                int n;
                if (!foundCommand) {
                    this.consumer.onCommandComplete(original, (boolean)false, (int)0);
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext((ImmutableStringReader)parse.getReader());
                }
                if (forked) {
                    n = successfulForks;
                    return n;
                }
                n = result;
                return n;
            }
            for (int i = 0; i < size; ++i) {
                CommandContext<S> context = contexts.get((int)i);
                CommandContext<S> child = context.getChild();
                if (child != null) {
                    forked |= context.isForked();
                    if (!child.hasNodes()) continue;
                    foundCommand = true;
                    RedirectModifier<S> modifier = context.getRedirectModifier();
                    if (modifier == null) {
                        if (next == null) {
                            next = new ArrayList<CommandContext<S>>((int)1);
                        }
                        next.add(child.copyFor(context.getSource()));
                        continue;
                    }
                    try {
                        Collection<S> results = modifier.apply(context);
                        if (results.isEmpty()) continue;
                        if (next == null) {
                            next = new ArrayList<E>((int)results.size());
                        }
                        for (S source : results) {
                            next.add(child.copyFor(source));
                        }
                        continue;
                    }
                    catch (CommandSyntaxException ex) {
                        this.consumer.onCommandComplete(context, (boolean)false, (int)0);
                        if (forked) continue;
                        throw ex;
                    }
                }
                if (context.getCommand() == null) continue;
                foundCommand = true;
                try {
                    int value = context.getCommand().run(context);
                    result += value;
                    this.consumer.onCommandComplete(context, (boolean)true, (int)value);
                    ++successfulForks;
                    continue;
                }
                catch (CommandSyntaxException ex) {
                    this.consumer.onCommandComplete(context, (boolean)false, (int)0);
                    if (forked) continue;
                    throw ex;
                }
            }
            contexts = next;
            next = null;
        } while (true);
    }

    public ParseResults<S> parse(String command, S source) {
        return this.parse((StringReader)new StringReader((String)command), source);
    }

    public ParseResults<S> parse(StringReader command, S source) {
        CommandContextBuilder<S> context = new CommandContextBuilder<S>(this, source, this.root, (int)command.getCursor());
        return this.parseNodes(this.root, (StringReader)command, context);
    }

    private ParseResults<S> parseNodes(CommandNode<S> node, StringReader originalReader, CommandContextBuilder<S> contextSoFar) {
        Map<CommandNode<S>, CommandSyntaxException> map;
        S source = contextSoFar.getSource();
        LinkedHashMap<CommandNode<S>, CommandSyntaxException> errors = null;
        ArrayList<ParseResults<S>> potentials = null;
        int cursor = originalReader.getCursor();
        for (CommandNode<S> child : node.getRelevantNodes((StringReader)originalReader)) {
            if (!child.canUse(source)) continue;
            CommandContextBuilder<S> context = contextSoFar.copy();
            StringReader reader = new StringReader((StringReader)originalReader);
            try {
                try {
                    child.parse((StringReader)reader, context);
                }
                catch (RuntimeException ex) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext((ImmutableStringReader)reader, (Object)ex.getMessage());
                }
                if (reader.canRead() && reader.peek() != ' ') {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherExpectedArgumentSeparator().createWithContext((ImmutableStringReader)reader);
                }
            }
            catch (CommandSyntaxException ex) {
                if (errors == null) {
                    errors = new LinkedHashMap<CommandNode<S>, CommandSyntaxException>();
                }
                errors.put(child, ex);
                reader.setCursor((int)cursor);
                continue;
            }
            context.withCommand(child.getCommand());
            if (reader.canRead((int)(child.getRedirect() == null ? 2 : 1))) {
                reader.skip();
                if (child.getRedirect() != null) {
                    CommandContextBuilder<S> childContext = new CommandContextBuilder<S>(this, source, child.getRedirect(), (int)reader.getCursor());
                    ParseResults<S> parse = this.parseNodes(child.getRedirect(), (StringReader)reader, childContext);
                    context.withChild(parse.getContext());
                    return new ParseResults<S>(context, (ImmutableStringReader)parse.getReader(), parse.getExceptions());
                }
                ParseResults<S> parse = this.parseNodes(child, (StringReader)reader, context);
                if (potentials == null) {
                    potentials = new ArrayList<E>((int)1);
                }
                potentials.add(parse);
                continue;
            }
            if (potentials == null) {
                potentials = new ArrayList<ParseResults<S>>((int)1);
            }
            potentials.add(new ParseResults<S>(context, (ImmutableStringReader)reader, Collections.<CommandNode<S>, CommandSyntaxException>emptyMap()));
        }
        if (potentials != null) {
            if (potentials.size() <= 1) return (ParseResults)potentials.get((int)0);
            potentials.sort((a, b) -> {
                if (!a.getReader().canRead() && b.getReader().canRead()) {
                    return -1;
                }
                if (a.getReader().canRead() && !b.getReader().canRead()) {
                    return 1;
                }
                if (a.getExceptions().isEmpty() && !b.getExceptions().isEmpty()) {
                    return -1;
                }
                if (a.getExceptions().isEmpty()) return 0;
                if (!b.getExceptions().isEmpty()) return 0;
                return 1;
            });
            return (ParseResults)potentials.get((int)0);
        }
        if (errors == null) {
            map = Collections.emptyMap();
            return new ParseResults<S>(contextSoFar, (ImmutableStringReader)originalReader, map);
        }
        map = errors;
        return new ParseResults<S>(contextSoFar, (ImmutableStringReader)originalReader, map);
    }

    public String[] getAllUsage(CommandNode<S> node, S source, boolean restricted) {
        ArrayList<String> result = new ArrayList<String>();
        this.getAllUsage(node, source, result, (String)"", (boolean)restricted);
        return result.toArray(new String[result.size()]);
    }

    private void getAllUsage(CommandNode<S> node, S source, ArrayList<String> result, String prefix, boolean restricted) {
        if (restricted && !node.canUse(source)) {
            return;
        }
        if (node.getCommand() != null) {
            result.add((String)prefix);
        }
        if (node.getRedirect() != null) {
            String redirect = node.getRedirect() == this.root ? "..." : "-> " + node.getRedirect().getUsageText();
            result.add((String)(prefix.isEmpty() ? node.getUsageText() + ARGUMENT_SEPARATOR + redirect : prefix + ARGUMENT_SEPARATOR + redirect));
            return;
        }
        if (node.getChildren().isEmpty()) return;
        Iterator<CommandNode<S>> redirect = node.getChildren().iterator();
        while (redirect.hasNext()) {
            CommandNode<S> child = redirect.next();
            this.getAllUsage(child, source, result, (String)(prefix.isEmpty() ? child.getUsageText() : prefix + ARGUMENT_SEPARATOR + child.getUsageText()), (boolean)restricted);
        }
    }

    public Map<CommandNode<S>, String> getSmartUsage(CommandNode<S> node, S source) {
        LinkedHashMap<CommandNode<S>, String> result = new LinkedHashMap<CommandNode<S>, String>();
        boolean optional = node.getCommand() != null;
        Iterator<CommandNode<S>> iterator = node.getChildren().iterator();
        while (iterator.hasNext()) {
            CommandNode<S> child = iterator.next();
            String usage = this.getSmartUsage(child, source, (boolean)optional, (boolean)false);
            if (usage == null) continue;
            result.put(child, (String)usage);
        }
        return result;
    }

    private String getSmartUsage(CommandNode<S> node, S source, boolean optional, boolean deep) {
        Object usage;
        if (!node.canUse(source)) {
            return null;
        }
        String self = optional ? USAGE_OPTIONAL_OPEN + node.getUsageText() + USAGE_OPTIONAL_CLOSE : node.getUsageText();
        boolean childOptional = node.getCommand() != null;
        String open = childOptional ? USAGE_OPTIONAL_OPEN : USAGE_REQUIRED_OPEN;
        String close = childOptional ? USAGE_OPTIONAL_CLOSE : USAGE_REQUIRED_CLOSE;
        if (deep) return self;
        if (node.getRedirect() != null) {
            String redirect = node.getRedirect() == this.root ? "..." : "-> " + node.getRedirect().getUsageText();
            return self + ARGUMENT_SEPARATOR + redirect;
        }
        Collection children = (Collection)node.getChildren().stream().filter(c -> c.canUse(source)).collect(Collectors.<T>toList());
        if (children.size() == 1) {
            String usage2 = this.getSmartUsage((CommandNode)children.iterator().next(), source, (boolean)childOptional, (boolean)childOptional);
            if (usage2 == null) return self;
            return self + ARGUMENT_SEPARATOR + usage2;
        }
        if (children.size() <= 1) return self;
        LinkedHashSet<String> childUsage = new LinkedHashSet<String>();
        for (CommandNode child : children) {
            usage = this.getSmartUsage(child, source, (boolean)childOptional, (boolean)true);
            if (usage == null) continue;
            childUsage.add(usage);
        }
        if (childUsage.size() == 1) {
            String string;
            String usage3 = (String)childUsage.iterator().next();
            if (childOptional) {
                string = USAGE_OPTIONAL_OPEN + usage3 + USAGE_OPTIONAL_CLOSE;
                return self + ARGUMENT_SEPARATOR + string;
            }
            string = usage3;
            return self + ARGUMENT_SEPARATOR + string;
        }
        if (childUsage.size() <= 1) return self;
        StringBuilder builder = new StringBuilder((String)open);
        int count = 0;
        usage = children.iterator();
        do {
            if (!usage.hasNext()) {
                if (count <= 0) return self;
                builder.append((String)close);
                return self + ARGUMENT_SEPARATOR + builder.toString();
            }
            CommandNode child = (CommandNode)usage.next();
            if (count > 0) {
                builder.append((String)USAGE_OR);
            }
            builder.append((String)child.getUsageText());
            ++count;
        } while (true);
    }

    public CompletableFuture<Suggestions> getCompletionSuggestions(ParseResults<S> parse) {
        return this.getCompletionSuggestions(parse, (int)parse.getReader().getTotalLength());
    }

    public CompletableFuture<Suggestions> getCompletionSuggestions(ParseResults<S> parse, int cursor) {
        CommandContextBuilder<S> context = parse.getContext();
        SuggestionContext<S> nodeBeforeCursor = context.findSuggestionContext((int)cursor);
        CommandNode<S> parent = nodeBeforeCursor.parent;
        int start = Math.min((int)nodeBeforeCursor.startPos, (int)cursor);
        String fullInput = parse.getReader().getString();
        String truncatedInput = fullInput.substring((int)0, (int)cursor);
        CompletableFuture[] futures = new CompletableFuture[parent.getChildren().size()];
        int i = 0;
        Iterator<CommandNode<S>> iterator = parent.getChildren().iterator();
        do {
            if (!iterator.hasNext()) {
                CompletableFuture<Suggestions> result = new CompletableFuture<Suggestions>();
                CompletableFuture.allOf(futures).thenRun(() -> {
                    ArrayList<Suggestions> suggestions = new ArrayList<Suggestions>();
                    CompletableFuture[] arrcompletableFuture = futures;
                    int n = arrcompletableFuture.length;
                    int n2 = 0;
                    do {
                        if (n2 >= n) {
                            result.complete(Suggestions.merge((String)fullInput, suggestions));
                            return;
                        }
                        CompletableFuture future = arrcompletableFuture[n2];
                        suggestions.add(future.join());
                        ++n2;
                    } while (true);
                });
                return result;
            }
            CommandNode<S> node = iterator.next();
            CompletableFuture<Suggestions> future = Suggestions.empty();
            try {
                future = node.listSuggestions(context.build((String)truncatedInput), (SuggestionsBuilder)new SuggestionsBuilder((String)truncatedInput, (int)start));
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            futures[i++] = future;
        } while (true);
    }

    public RootCommandNode<S> getRoot() {
        return this.root;
    }

    public Collection<String> getPath(CommandNode<S> target) {
        List list;
        ArrayList<List<CommandNode<S>>> nodes = new ArrayList<List<CommandNode<S>>>();
        this.addPaths(this.root, nodes, new ArrayList<CommandNode<S>>());
        Iterator<E> iterator = nodes.iterator();
        do {
            if (!iterator.hasNext()) return Collections.emptyList();
        } while ((list = (List)iterator.next()).get((int)(list.size() - 1)) != target);
        ArrayList<String> result = new ArrayList<String>((int)list.size());
        Iterator<E> iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            CommandNode node = (CommandNode)iterator2.next();
            if (node == this.root) continue;
            result.add((String)node.getName());
        }
        return result;
    }

    public CommandNode<S> findNode(Collection<String> path) {
        String name;
        CommandNode node = this.root;
        Iterator<String> iterator = path.iterator();
        do {
            if (!iterator.hasNext()) return node;
        } while ((node = node.getChild((String)(name = iterator.next()))) != null);
        return null;
    }

    public void findAmbiguities(AmbiguityConsumer<S> consumer) {
        this.root.findAmbiguities(consumer);
    }

    private void addPaths(CommandNode<S> node, List<List<CommandNode<S>>> result, List<CommandNode<S>> parents) {
        ArrayList<CommandNode<S>> current = new ArrayList<CommandNode<S>>(parents);
        current.add(node);
        result.add(current);
        Iterator<CommandNode<S>> iterator = node.getChildren().iterator();
        while (iterator.hasNext()) {
            CommandNode<S> child = iterator.next();
            this.addPaths(child, result, current);
        }
    }

    static /* synthetic */ Predicate access$000(CommandDispatcher x0) {
        return x0.hasCommand;
    }
}

