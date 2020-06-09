/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import java.util.Objects;

public class IntegerSuggestion
extends Suggestion {
    private int value;

    public IntegerSuggestion(StringRange range, int value) {
        this((StringRange)range, (int)value, null);
    }

    public IntegerSuggestion(StringRange range, int value, Message tooltip) {
        super((StringRange)range, (String)Integer.toString((int)value), (Message)tooltip);
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerSuggestion)) {
            return false;
        }
        IntegerSuggestion that = (IntegerSuggestion)o;
        if (this.value != that.value) return false;
        if (!super.equals((Object)o)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object[])new Object[]{Integer.valueOf((int)super.hashCode()), Integer.valueOf((int)this.value)});
    }

    @Override
    public String toString() {
        return "IntegerSuggestion{value=" + this.value + ", range=" + this.getRange() + ", text='" + this.getText() + '\'' + ", tooltip='" + this.getTooltip() + '\'' + '}';
    }

    @Override
    public int compareTo(Suggestion o) {
        if (!(o instanceof IntegerSuggestion)) return super.compareTo((Suggestion)o);
        return Integer.compare((int)this.value, (int)((IntegerSuggestion)o).value);
    }

    @Override
    public int compareToIgnoreCase(Suggestion b) {
        return this.compareTo((Suggestion)b);
    }
}

