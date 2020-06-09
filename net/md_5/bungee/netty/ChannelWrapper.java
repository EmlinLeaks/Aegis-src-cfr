/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.compress.PacketCompressor;
import net.md_5.bungee.compress.PacketDecompressor;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Kick;

public class ChannelWrapper {
    private final Channel ch;
    private InetSocketAddress remoteAddress;
    private volatile boolean closed;
    private volatile boolean closing;

    public ChannelWrapper(ChannelHandlerContext ctx) {
        this.ch = ctx.channel();
        this.remoteAddress = (InetSocketAddress)this.ch.remoteAddress();
    }

    public void setProtocol(Protocol protocol) {
        this.ch.pipeline().get(MinecraftDecoder.class).setProtocol((Protocol)protocol);
        this.ch.pipeline().get(MinecraftEncoder.class).setProtocol((Protocol)protocol);
    }

    public void setVersion(int protocol) {
        this.ch.pipeline().get(MinecraftDecoder.class).setProtocolVersion((int)protocol);
        this.ch.pipeline().get(MinecraftEncoder.class).setProtocolVersion((int)protocol);
    }

    public void write(Object packet) {
        if (this.closed) return;
        if (packet instanceof PacketWrapper) {
            ((PacketWrapper)packet).setReleased((boolean)true);
            this.ch.writeAndFlush((Object)((PacketWrapper)packet).buf, (ChannelPromise)this.ch.voidPromise());
            return;
        }
        this.ch.writeAndFlush((Object)packet, (ChannelPromise)this.ch.voidPromise());
    }

    public void markClosed() {
        this.closing = true;
        this.closed = true;
    }

    public void close() {
        this.close(null);
    }

    public void close(Object packet) {
        if (this.closed) return;
        this.closing = true;
        this.closed = true;
        if (packet != null && this.ch.isActive()) {
            this.ch.writeAndFlush((Object)packet).addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, ChannelFutureListener.CLOSE);
            this.ch.eventLoop().schedule(this.ch::close, (long)250L, (TimeUnit)TimeUnit.MILLISECONDS);
            return;
        }
        this.ch.flush();
        this.ch.close();
    }

    public void delayedClose(Kick kick) {
        if (this.closing) return;
        this.closing = true;
        this.ch.eventLoop().schedule(() -> this.close((Object)kick), (long)250L, (TimeUnit)TimeUnit.MILLISECONDS);
    }

    public void addBefore(String baseName, String name, ChannelHandler handler) {
        Preconditions.checkState((boolean)this.ch.eventLoop().inEventLoop(), (Object)"cannot add handler outside of event loop");
        this.ch.pipeline().flush();
        this.ch.pipeline().addBefore((String)baseName, (String)name, (ChannelHandler)handler);
    }

    public Channel getHandle() {
        return this.ch;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        if (this.ch.pipeline().get(PacketCompressor.class) == null && compressionThreshold != -1) {
            this.addBefore((String)"packet-encoder", (String)"compress", (ChannelHandler)new PacketCompressor());
        }
        if (compressionThreshold != -1) {
            this.ch.pipeline().get(PacketCompressor.class).setThreshold((int)compressionThreshold);
        } else {
            this.ch.pipeline().remove((String)"compress");
        }
        if (this.ch.pipeline().get(PacketDecompressor.class) == null && compressionThreshold != -1) {
            this.addBefore((String)"packet-decoder", (String)"decompress", (ChannelHandler)new PacketDecompressor());
        }
        if (compressionThreshold != -1) return;
        this.ch.pipeline().remove((String)"decompress");
    }

    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean isClosing() {
        return this.closing;
    }
}

