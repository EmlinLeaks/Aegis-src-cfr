/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;

public class DynamicCommandExceptionType
implements CommandExceptionType {
    private final Function<Object, Message> function;

    public DynamicCommandExceptionType(Function<Object, Message> function) {
        this.function = function;
    }

    public CommandSyntaxException create(Object arg) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object)arg));
    }

    public CommandSyntaxException createWithContext(ImmutableStringReader reader, Object arg) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object)arg), (String)reader.getString(), (int)reader.getCursor());
    }
}

