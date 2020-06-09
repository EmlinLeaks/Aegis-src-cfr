/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.context;

import com.mojang.brigadier.ImmutableStringReader;
import java.util.Objects;

public class StringRange {
    private final int start;
    private final int end;

    public StringRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public static StringRange at(int pos) {
        return new StringRange((int)pos, (int)pos);
    }

    public static StringRange between(int start, int end) {
        return new StringRange((int)start, (int)end);
    }

    public static StringRange encompassing(StringRange a, StringRange b) {
        return new StringRange((int)Math.min((int)a.getStart(), (int)b.getStart()), (int)Math.max((int)a.getEnd(), (int)b.getEnd()));
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public String get(ImmutableStringReader reader) {
        return reader.getString().substring((int)this.start, (int)this.end);
    }

    public String get(String string) {
        return string.substring((int)this.start, (int)this.end);
    }

    public boolean isEmpty() {
        if (this.start != this.end) return false;
        return true;
    }

    public int getLength() {
        return this.end - this.start;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringRange)) {
            return false;
        }
        StringRange that = (StringRange)o;
        if (this.start != that.start) return false;
        if (this.end != that.end) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{Integer.valueOf((int)this.start), Integer.valueOf((int)this.end)});
    }

    public String toString() {
        return "StringRange{start=" + this.start + ", end=" + this.end + '}';
    }
}

