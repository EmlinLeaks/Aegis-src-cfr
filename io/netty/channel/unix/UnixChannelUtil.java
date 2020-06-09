/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.channel.unix.Limits;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class UnixChannelUtil {
    private UnixChannelUtil() {
    }

    public static boolean isBufferCopyNeededForWrite(ByteBuf byteBuf) {
        return UnixChannelUtil.isBufferCopyNeededForWrite((ByteBuf)byteBuf, (int)Limits.IOV_MAX);
    }

    static boolean isBufferCopyNeededForWrite(ByteBuf byteBuf, int iovMax) {
        if (byteBuf.hasMemoryAddress()) return false;
        if (!byteBuf.isDirect()) return true;
        if (byteBuf.nioBufferCount() <= iovMax) return false;
        return true;
    }

    public static InetSocketAddress computeRemoteAddr(InetSocketAddress remoteAddr, InetSocketAddress osRemoteAddr) {
        if (osRemoteAddr == null) return remoteAddr;
        if (PlatformDependent.javaVersion() < 7) return osRemoteAddr;
        try {
            return new InetSocketAddress((InetAddress)InetAddress.getByAddress((String)remoteAddr.getHostString(), (byte[])osRemoteAddr.getAddress().getAddress()), (int)osRemoteAddr.getPort());
        }
        catch (UnknownHostException unknownHostException) {
            // empty catch block
        }
        return osRemoteAddr;
    }
}

