/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier;

import com.mojang.brigadier.Message;

public class LiteralMessage
implements Message {
    private final String string;

    public LiteralMessage(String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return this.string;
    }

    public String toString() {
        return this.string;
    }
}

