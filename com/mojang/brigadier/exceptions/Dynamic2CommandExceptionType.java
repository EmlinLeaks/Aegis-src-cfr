/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;

public class Dynamic2CommandExceptionType
implements CommandExceptionType {
    private final Function function;

    public Dynamic2CommandExceptionType(Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(Object a, Object b) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object)a, (Object)b));
    }

    public CommandSyntaxException createWithContext(ImmutableStringReader reader, Object a, Object b) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object)a, (Object)b), (String)reader.getString(), (int)reader.getCursor());
    }
}

