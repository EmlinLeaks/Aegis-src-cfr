/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.StatusRequest;
import xyz.yooniks.aegis.AddressBlocker;
import xyz.yooniks.aegis.Aegis;

public class MinecraftDecoder
extends MessageToMessageDecoder<ByteBuf> {
    private static final AddressBlocker ADDRESS_BLOCKER = Aegis.getInstance().getAddressBlocker();
    private boolean supportsForge;
    private Protocol protocol;
    private final boolean server;
    private int protocolVersion;

    public MinecraftDecoder(Protocol protocol, boolean server, int protocolVersion) {
        this.protocol = protocol;
        this.server = server;
        this.protocolVersion = protocolVersion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        InetAddress address = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress();
        if (ADDRESS_BLOCKER.isBlocked((InetAddress)address)) {
            ctx.close();
            in.clear();
            return;
        }
        Protocol.DirectionData prot = this.server ? this.protocol.TO_SERVER : this.protocol.TO_CLIENT;
        ByteBuf slice = in.copy();
        if (in.readableBytes() == 0) {
            return;
        }
        try {
            int packetId;
            DefinedPacket packet;
            if (in.readerIndex() == in.writerIndex() || in.readerIndex() < 0 || in.writerIndex() < 1 || in.writerIndex() >= Integer.MAX_VALUE) {
                ctx.close();
                in.clear();
                System.out.println((String)("{CasualProtector} " + address.getHostAddress() + " invalid writerIndex"));
                return;
            }
            try {
                packetId = DefinedPacket.readVarInt((ByteBuf)in, (int)5, (ChannelHandlerContext)ctx, (InetAddress)address);
            }
            catch (Exception ex) {
                String message = ex.getMessage();
                if (message.length() > 30) {
                    message = message.substring((int)0, (int)29);
                }
                System.out.println((String)("{CasualProtector} " + address.getHostAddress() + " " + message));
                if (BungeeCord.getInstance().getServers().values().stream().noneMatch(server -> server.getAddress().getAddress().getHostAddress().equals((Object)address.getHostAddress())) && !BungeeCord.getInstance().getConfig().getBypassIps().contains((Object)address.getHostAddress())) {
                    ADDRESS_BLOCKER.block((InetAddress)address);
                    ctx.close();
                    in.clear();
                }
                if (slice == null) return;
                slice.release();
                return;
            }
            try {
                packet = prot.createPacket((int)packetId, (int)this.protocolVersion);
            }
            catch (Exception ex) {
                if (BungeeCord.getInstance().getServers().values().stream().noneMatch(server -> server.getAddress().getAddress().getHostAddress().equals((Object)address.getHostAddress())) && !BungeeCord.getInstance().getConfig().getBypassIps().contains((Object)address.getHostAddress())) {
                    ctx.close();
                    ADDRESS_BLOCKER.block((InetAddress)address);
                    in.clear();
                    String message = ex.getMessage();
                    if (message.length() > 30) {
                        message = message.substring((int)0, (int)29);
                    }
                    System.out.println((String)("{CasualProtector} " + address.getHostAddress() + " " + message));
                }
                if (slice == null) return;
                slice.release();
                return;
            }
            if (packet != null) {
                try {
                    packet.read((ByteBuf)in, (ProtocolConstants.Direction)prot.getDirection(), (int)this.protocolVersion);
                }
                catch (Exception ex) {
                    if (BungeeCord.getInstance().getServers().values().stream().noneMatch(server -> server.getAddress().getAddress().getHostAddress().equals((Object)address.getHostAddress())) && !BungeeCord.getInstance().getConfig().getBypassIps().contains((Object)address.getHostAddress())) {
                        ctx.close();
                        ADDRESS_BLOCKER.block((InetAddress)address);
                        in.clear();
                        String message = ex.getMessage();
                        if (message.length() > 30) {
                            message = message.substring((int)0, (int)29);
                        }
                        System.out.println((String)("{CasualProtector} Read packet error: " + address.getHostAddress() + " " + message));
                    }
                    if (slice == null) return;
                    slice.release();
                    return;
                }
                if (in.isReadable()) {
                    System.out.println((String)("{CasualProtector} " + address.getHostAddress() + " Did not read all bytes! v1 " + packet.getClass() + " " + packetId));
                    if (packet instanceof StatusRequest) return;
                    if (!BungeeCord.getInstance().getServers().values().stream().noneMatch(server -> server.getAddress().getAddress().getHostAddress().equals((Object)address.getHostAddress()))) return;
                    if (BungeeCord.getInstance().getConfig().getBypassIps().contains((Object)address.getHostAddress())) return;
                    ADDRESS_BLOCKER.block((InetAddress)address);
                    ctx.close();
                    System.out.println((String)("{CasualProtector} " + address.getHostAddress() + " Did not read all bytes! v2 " + packet.getClass() + " " + packetId));
                    return;
                }
            } else {
                in.skipBytes((int)in.readableBytes());
            }
            out.add((Object)new PacketWrapper((DefinedPacket)packet, (ByteBuf)slice));
            slice = null;
            return;
        }
        catch (Exception ex) {
            if (!BungeeCord.getInstance().getServers().values().stream().noneMatch(server -> server.getAddress().getAddress().getHostAddress().equals((Object)address.getHostAddress()))) return;
            if (BungeeCord.getInstance().getConfig().getBypassIps().contains((Object)address.getHostAddress())) return;
            ADDRESS_BLOCKER.block((InetAddress)address);
            ctx.close();
            System.out.println((String)("{CasualProtector} " + address.getHostAddress() + " error while decoding packet!"));
            return;
        }
        finally {
            if (slice != null) {
                slice.release();
            }
        }
    }

    public void setSupportsForge(boolean supportsForge) {
        this.supportsForge = supportsForge;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}

