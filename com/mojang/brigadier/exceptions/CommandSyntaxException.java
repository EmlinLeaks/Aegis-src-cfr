/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.BuiltInExceptions;
import com.mojang.brigadier.exceptions.CommandExceptionType;

public class CommandSyntaxException
extends Exception {
    public static final int CONTEXT_AMOUNT = 10;
    public static boolean ENABLE_COMMAND_STACK_TRACES = true;
    public static BuiltInExceptionProvider BUILT_IN_EXCEPTIONS = new BuiltInExceptions();
    private final CommandExceptionType type;
    private final Message message;
    private final String input;
    private final int cursor;

    public CommandSyntaxException(CommandExceptionType type, Message message) {
        super((String)message.getString(), null, (boolean)ENABLE_COMMAND_STACK_TRACES, (boolean)ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.message = message;
        this.input = null;
        this.cursor = -1;
    }

    public CommandSyntaxException(CommandExceptionType type, Message message, String input, int cursor) {
        super((String)message.getString(), null, (boolean)ENABLE_COMMAND_STACK_TRACES, (boolean)ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.message = message;
        this.input = input;
        this.cursor = cursor;
    }

    @Override
    public String getMessage() {
        String message = this.message.getString();
        String context = this.getContext();
        if (context == null) return message;
        return message + " at position " + this.cursor + ": " + context;
    }

    public Message getRawMessage() {
        return this.message;
    }

    public String getContext() {
        if (this.input == null) return null;
        if (this.cursor < 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int cursor = Math.min((int)this.input.length(), (int)this.cursor);
        if (cursor > 10) {
            builder.append((String)"...");
        }
        builder.append((String)this.input.substring((int)Math.max((int)0, (int)(cursor - 10)), (int)cursor));
        builder.append((String)"<--[HERE]");
        return builder.toString();
    }

    public CommandExceptionType getType() {
        return this.type;
    }

    public String getInput() {
        return this.input;
    }

    public int getCursor() {
        return this.cursor;
    }
}

