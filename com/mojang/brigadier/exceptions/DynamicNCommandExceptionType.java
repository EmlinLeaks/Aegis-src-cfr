/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicNCommandExceptionType;

public class DynamicNCommandExceptionType
implements CommandExceptionType {
    private final Function function;

    public DynamicNCommandExceptionType(Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(Object a, Object ... args) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object[])args));
    }

    public CommandSyntaxException createWithContext(ImmutableStringReader reader, Object ... args) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object[])args), (String)reader.getString(), (int)reader.getCursor());
    }
}

