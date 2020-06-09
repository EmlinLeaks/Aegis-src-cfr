/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;

public class StringArgumentType
implements ArgumentType<String> {
    private final StringType type;

    private StringArgumentType(StringType type) {
        this.type = type;
    }

    public static StringArgumentType word() {
        return new StringArgumentType((StringType)StringType.SINGLE_WORD);
    }

    public static StringArgumentType string() {
        return new StringArgumentType((StringType)StringType.QUOTABLE_PHRASE);
    }

    public static StringArgumentType greedyString() {
        return new StringArgumentType((StringType)StringType.GREEDY_PHRASE);
    }

    public static String getString(CommandContext<?> context, String name) {
        return context.getArgument((String)name, String.class);
    }

    public StringType getType() {
        return this.type;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        if (this.type == StringType.GREEDY_PHRASE) {
            String text = reader.getRemaining();
            reader.setCursor((int)reader.getTotalLength());
            return text;
        }
        if (this.type != StringType.SINGLE_WORD) return reader.readString();
        return reader.readUnquotedString();
    }

    public String toString() {
        return "string()";
    }

    @Override
    public Collection<String> getExamples() {
        return this.type.getExamples();
    }

    public static String escapeIfRequired(String input) {
        char[] arrc = input.toCharArray();
        int n = arrc.length;
        int n2 = 0;
        while (n2 < n) {
            char c = arrc[n2];
            if (!StringReader.isAllowedInUnquotedString((char)c)) {
                return StringArgumentType.escape((String)input);
            }
            ++n2;
        }
        return input;
    }

    private static String escape(String input) {
        StringBuilder result = new StringBuilder((String)"\"");
        int i = 0;
        do {
            if (i >= input.length()) {
                result.append((String)"\"");
                return result.toString();
            }
            char c = input.charAt((int)i);
            if (c == '\\' || c == '\"') {
                result.append((char)'\\');
            }
            result.append((char)c);
            ++i;
        } while (true);
    }
}

