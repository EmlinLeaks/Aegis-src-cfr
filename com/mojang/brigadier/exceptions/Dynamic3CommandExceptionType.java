/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;

public class Dynamic3CommandExceptionType
implements CommandExceptionType {
    private final Function function;

    public Dynamic3CommandExceptionType(Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(Object a, Object b, Object c) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object)a, (Object)b, (Object)c));
    }

    public CommandSyntaxException createWithContext(ImmutableStringReader reader, Object a, Object b, Object c) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object)a, (Object)b, (Object)c), (String)reader.getString(), (int)reader.getCursor());
    }
}

