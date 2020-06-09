/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.timeout.ReadTimeoutException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.PingHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.OverflowPacketException;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.util.QuietException;

public class HandlerBoss
extends ChannelInboundHandlerAdapter {
    private ChannelWrapper channel;
    private PacketHandler handler;

    public void setHandler(PacketHandler handler) {
        Preconditions.checkArgument((boolean)(handler != null), (Object)"handler");
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.handler == null) return;
        this.channel = new ChannelWrapper((ChannelHandlerContext)ctx);
        this.handler.connected((ChannelWrapper)this.channel);
        if (this.handler instanceof InitialHandler) return;
        if (this.handler instanceof PingHandler) return;
        ProxyServer.getInstance().getLogger().log((Level)Level.INFO, (String)"{0} has connected", (Object)this.handler);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (this.handler == null) return;
        this.channel.markClosed();
        this.handler.disconnected((ChannelWrapper)this.channel);
        if (this.handler instanceof InitialHandler) return;
        if (this.handler instanceof PingHandler) return;
        ProxyServer.getInstance().getLogger().log((Level)Level.INFO, (String)"{0} has disconnected", (Object)this.handler);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (this.handler == null) return;
        this.handler.writabilityChanged((ChannelWrapper)this.channel);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage proxy = (HAProxyMessage)msg;
            InetSocketAddress newAddress = new InetSocketAddress((String)proxy.sourceAddress(), (int)proxy.sourcePort());
            ProxyServer.getInstance().getLogger().log((Level)Level.FINE, (String)"Set remote address via PROXY {0} -> {1}", (Object[])new Object[]{this.channel.getRemoteAddress(), newAddress});
            this.channel.setRemoteAddress((InetSocketAddress)newAddress);
            return;
        }
        if (this.handler == null) return;
        PacketWrapper packet = (PacketWrapper)msg;
        boolean sendPacket = this.handler.shouldHandle((PacketWrapper)packet);
        try {
            if (sendPacket && packet != null && packet.packet != null) {
                try {
                    packet.packet.handle((AbstractPacketHandler)this.handler);
                }
                catch (CancelSendSignal ex) {
                    sendPacket = false;
                }
            }
            if (!sendPacket) return;
            this.handler.handle((PacketWrapper)packet);
            return;
        }
        finally {
            if (packet != null) {
                packet.trySingleRelease();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        boolean logExceptions;
        if (!ctx.channel().isActive()) return;
        boolean bl = logExceptions = !(this.handler instanceof PingHandler);
        if (logExceptions) {
            if (cause instanceof ReadTimeoutException) {
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"{0} - read timed out", (Object)this.handler);
            } else if (cause instanceof DecoderException && cause.getCause() instanceof BadPacketException) {
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"{0} - bad packet ID, are mods in use!? {1}", (Object[])new Object[]{this.handler, cause.getCause().getMessage()});
            } else if (cause instanceof DecoderException && cause.getCause() instanceof OverflowPacketException) {
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"{0} - overflow in packet detected! {1}", (Object[])new Object[]{this.handler, cause.getCause().getMessage()});
            } else if (cause instanceof IOException || cause instanceof IllegalStateException && this.handler instanceof InitialHandler) {
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"{0} - {1}: {2}", (Object[])new Object[]{this.handler, cause.getClass().getSimpleName(), cause.getMessage()});
            } else if (cause instanceof QuietException) {
                ProxyServer.getInstance().getLogger().log((Level)Level.SEVERE, (String)"{0} - encountered exception: {1}", (Object[])new Object[]{this.handler, cause});
            } else {
                ProxyServer.getInstance().getLogger().log((Level)Level.SEVERE, (String)(this.handler + " - encountered exception"), (Throwable)cause);
            }
        }
        if (this.handler != null) {
            try {
                this.handler.exception((Throwable)cause);
            }
            catch (Exception ex) {
                ProxyServer.getInstance().getLogger().log((Level)Level.SEVERE, (String)(this.handler + " - exception processing exception"), (Throwable)ex);
            }
        }
        ctx.close();
    }
}

