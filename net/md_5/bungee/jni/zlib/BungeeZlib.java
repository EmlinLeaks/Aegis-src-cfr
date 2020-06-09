/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.jni.zlib;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;

public interface BungeeZlib {
    public void init(boolean var1, int var2);

    public void free();

    public void process(ByteBuf var1, ByteBuf var2) throws DataFormatException;
}

