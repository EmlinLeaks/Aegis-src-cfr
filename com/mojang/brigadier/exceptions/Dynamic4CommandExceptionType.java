/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;

public class Dynamic4CommandExceptionType
implements CommandExceptionType {
    private final Function function;

    public Dynamic4CommandExceptionType(Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(Object a, Object b, Object c, Object d) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object)a, (Object)b, (Object)c, (Object)d));
    }

    public CommandSyntaxException createWithContext(ImmutableStringReader reader, Object a, Object b, Object c, Object d) {
        return new CommandSyntaxException((CommandExceptionType)this, (Message)this.function.apply((Object)a, (Object)b, (Object)c, (Object)d), (String)reader.getString(), (int)reader.getCursor());
    }
}

