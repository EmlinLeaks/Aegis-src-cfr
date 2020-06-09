/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.IntegerSuggestion;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SuggestionsBuilder {
    private final String input;
    private final int start;
    private final String remaining;
    private final List<Suggestion> result = new ArrayList<Suggestion>();

    public SuggestionsBuilder(String input, int start) {
        this.input = input;
        this.start = start;
        this.remaining = input.substring((int)start);
    }

    public String getInput() {
        return this.input;
    }

    public int getStart() {
        return this.start;
    }

    public String getRemaining() {
        return this.remaining;
    }

    public Suggestions build() {
        return Suggestions.create((String)this.input, this.result);
    }

    public CompletableFuture<Suggestions> buildFuture() {
        return CompletableFuture.completedFuture(this.build());
    }

    public SuggestionsBuilder suggest(String text) {
        if (text.equals((Object)this.remaining)) {
            return this;
        }
        this.result.add((Suggestion)new Suggestion((StringRange)StringRange.between((int)this.start, (int)this.input.length()), (String)text));
        return this;
    }

    public SuggestionsBuilder suggest(String text, Message tooltip) {
        if (text.equals((Object)this.remaining)) {
            return this;
        }
        this.result.add((Suggestion)new Suggestion((StringRange)StringRange.between((int)this.start, (int)this.input.length()), (String)text, (Message)tooltip));
        return this;
    }

    public SuggestionsBuilder suggest(int value) {
        this.result.add((Suggestion)new IntegerSuggestion((StringRange)StringRange.between((int)this.start, (int)this.input.length()), (int)value));
        return this;
    }

    public SuggestionsBuilder suggest(int value, Message tooltip) {
        this.result.add((Suggestion)new IntegerSuggestion((StringRange)StringRange.between((int)this.start, (int)this.input.length()), (int)value, (Message)tooltip));
        return this;
    }

    public SuggestionsBuilder add(SuggestionsBuilder other) {
        this.result.addAll(other.result);
        return this;
    }

    public SuggestionsBuilder createOffset(int start) {
        return new SuggestionsBuilder((String)this.input, (int)start);
    }

    public SuggestionsBuilder restart() {
        return new SuggestionsBuilder((String)this.input, (int)this.start);
    }
}

