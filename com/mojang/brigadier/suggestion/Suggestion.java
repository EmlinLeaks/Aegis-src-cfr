/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import java.util.Objects;

public class Suggestion
implements Comparable<Suggestion> {
    private final StringRange range;
    private final String text;
    private final Message tooltip;

    public Suggestion(StringRange range, String text) {
        this((StringRange)range, (String)text, null);
    }

    public Suggestion(StringRange range, String text, Message tooltip) {
        this.range = range;
        this.text = text;
        this.tooltip = tooltip;
    }

    public StringRange getRange() {
        return this.range;
    }

    public String getText() {
        return this.text;
    }

    public Message getTooltip() {
        return this.tooltip;
    }

    public String apply(String input) {
        if (this.range.getStart() == 0 && this.range.getEnd() == input.length()) {
            return this.text;
        }
        StringBuilder result = new StringBuilder();
        if (this.range.getStart() > 0) {
            result.append((String)input.substring((int)0, (int)this.range.getStart()));
        }
        result.append((String)this.text);
        if (this.range.getEnd() >= input.length()) return result.toString();
        result.append((String)input.substring((int)this.range.getEnd()));
        return result.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Suggestion)) {
            return false;
        }
        Suggestion that = (Suggestion)o;
        if (!Objects.equals((Object)this.range, (Object)that.range)) return false;
        if (!Objects.equals((Object)this.text, (Object)that.text)) return false;
        if (!Objects.equals((Object)this.tooltip, (Object)that.tooltip)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.range, this.text, this.tooltip});
    }

    public String toString() {
        return "Suggestion{range=" + this.range + ", text='" + this.text + '\'' + ", tooltip='" + this.tooltip + '\'' + '}';
    }

    @Override
    public int compareTo(Suggestion o) {
        return this.text.compareTo((String)o.text);
    }

    public int compareToIgnoreCase(Suggestion b) {
        return this.text.compareToIgnoreCase((String)b.text);
    }

    public Suggestion expand(String command, StringRange range) {
        if (range.equals((Object)this.range)) {
            return this;
        }
        StringBuilder result = new StringBuilder();
        if (range.getStart() < this.range.getStart()) {
            result.append((String)command.substring((int)range.getStart(), (int)this.range.getStart()));
        }
        result.append((String)this.text);
        if (range.getEnd() <= this.range.getEnd()) return new Suggestion((StringRange)range, (String)result.toString(), (Message)this.tooltip);
        result.append((String)command.substring((int)this.range.getEnd(), (int)range.getEnd()));
        return new Suggestion((StringRange)range, (String)result.toString(), (Message)this.tooltip);
    }
}

