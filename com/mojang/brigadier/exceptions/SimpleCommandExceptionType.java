/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class SimpleCommandExceptionType
implements CommandExceptionType {
    private final Message message;

    public SimpleCommandExceptionType(Message message) {
        this.message = message;
    }

    public CommandSyntaxException create() {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.message);
    }

    public CommandSyntaxException createWithContext(ImmutableStringReader reader) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.message, (String)reader.getString(), (int)reader.getCursor());
    }

    public String toString() {
        return this.message.getString();
    }
}

