/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface SingleRedirectModifier<S> {
    public S apply(CommandContext<S> var1) throws CommandSyntaxException;
}

