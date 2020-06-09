/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.jni.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;

public interface BungeeCipher {
    public void init(boolean var1, SecretKey var2) throws GeneralSecurityException;

    public void free();

    public void cipher(ByteBuf var1, ByteBuf var2) throws GeneralSecurityException;

    public ByteBuf cipher(ChannelHandlerContext var1, ByteBuf var2) throws GeneralSecurityException;
}

