/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;

@FunctionalInterface
public interface RedirectModifier<S> {
    public Collection<S> apply(CommandContext<S> var1) throws CommandSyntaxException;
}

