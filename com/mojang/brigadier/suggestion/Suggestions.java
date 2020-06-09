/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Suggestions {
    private static final Suggestions EMPTY = new Suggestions((StringRange)StringRange.at((int)0), new ArrayList<Suggestion>());
    private final StringRange range;
    private final List<Suggestion> suggestions;

    public Suggestions(StringRange range, List<Suggestion> suggestions) {
        this.range = range;
        this.suggestions = suggestions;
    }

    public StringRange getRange() {
        return this.range;
    }

    public List<Suggestion> getList() {
        return this.suggestions;
    }

    public boolean isEmpty() {
        return this.suggestions.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Suggestions)) {
            return false;
        }
        Suggestions that = (Suggestions)o;
        if (!Objects.equals((Object)this.range, (Object)that.range)) return false;
        if (!Objects.equals(this.suggestions, that.suggestions)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.range, this.suggestions});
    }

    public String toString() {
        return "Suggestions{range=" + this.range + ", suggestions=" + this.suggestions + '}';
    }

    public static CompletableFuture<Suggestions> empty() {
        return CompletableFuture.completedFuture(EMPTY);
    }

    public static Suggestions merge(String command, Collection<Suggestions> input) {
        if (input.isEmpty()) {
            return EMPTY;
        }
        if (input.size() == 1) {
            return input.iterator().next();
        }
        HashSet<Suggestion> texts = new HashSet<Suggestion>();
        Iterator<Suggestions> iterator = input.iterator();
        while (iterator.hasNext()) {
            Suggestions suggestions = iterator.next();
            texts.addAll(suggestions.getList());
        }
        return Suggestions.create((String)command, texts);
    }

    public static Suggestions create(String command, Collection<Suggestion> suggestions) {
        if (suggestions.isEmpty()) {
            return EMPTY;
        }
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        for (Suggestion suggestion : suggestions) {
            start = Math.min((int)suggestion.getRange().getStart(), (int)start);
            end = Math.max((int)suggestion.getRange().getEnd(), (int)end);
        }
        StringRange range = new StringRange((int)start, (int)end);
        HashSet<Suggestion> texts = new HashSet<Suggestion>();
        Iterator<Suggestion> iterator = suggestions.iterator();
        do {
            if (!iterator.hasNext()) {
                ArrayList<Suggestion> sorted = new ArrayList<Suggestion>(texts);
                sorted.sort((a, b) -> a.compareToIgnoreCase((Suggestion)b));
                return new Suggestions((StringRange)range, sorted);
            }
            Suggestion suggestion = iterator.next();
            texts.add(suggestion.expand((String)command, (StringRange)range));
        } while (true);
    }
}

