/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class BufUtil {
    public static String dump(ByteBuf buf, int maxLen) {
        return ByteBufUtil.hexDump((ByteBuf)buf, (int)0, (int)Math.min((int)buf.writerIndex(), (int)maxLen));
    }
}

