/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier;

public interface ImmutableStringReader {
    public String getString();

    public int getRemainingLength();

    public int getTotalLength();

    public int getCursor();

    public String getRead();

    public String getRemaining();

    public boolean canRead(int var1);

    public boolean canRead();

    public char peek();

    public char peek(int var1);
}

