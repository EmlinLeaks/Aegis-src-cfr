/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.ServerConnector;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.forge.ForgeClientHandler;
import net.md_5.bungee.forge.ForgeServerHandler;
import net.md_5.bungee.forge.ForgeUtils;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EntityStatus;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.Team;
import net.md_5.bungee.protocol.packet.ViewDistance;
import net.md_5.bungee.tab.TabList;
import net.md_5.bungee.util.QuietException;

public class ServerConnector
extends PacketHandler {
    private final ProxyServer bungee;
    private ChannelWrapper ch;
    private final UserConnection user;
    private final BungeeServerInfo target;
    private State thisState = State.LOGIN_SUCCESS;
    private ForgeServerHandler handshakeHandler;
    private boolean obsolete;

    @Override
    public void exception(Throwable t) {
        if (this.obsolete) {
            return;
        }
        String message = "Exception Connecting:" + Util.exception((Throwable)t);
        if (this.user.getServer() == null) {
            this.user.disconnect((String)message);
            return;
        }
        this.user.sendMessage((String)((Object)((Object)ChatColor.RED) + message));
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.ch = channel;
        this.handshakeHandler = new ForgeServerHandler((UserConnection)this.user, (ChannelWrapper)this.ch, (ServerInfo)this.target);
        Handshake originalHandshake = this.user.getPendingConnection().getHandshake();
        Handshake copiedHandshake = new Handshake((int)originalHandshake.getProtocolVersion(), (String)originalHandshake.getHost(), (int)originalHandshake.getPort(), (int)2);
        if (BungeeCord.getInstance().config.isIpForward()) {
            String newHost = copiedHandshake.getHost() + "\u0000" + this.user.getAddress().getHostString() + "\u0000" + this.user.getUUID();
            LoginResult profile = this.user.getPendingConnection().getLoginProfile();
            LoginResult.Property[] properties = new LoginResult.Property[]{};
            if (profile != null && profile.getProperties() != null && profile.getProperties().length > 0) {
                properties = profile.getProperties();
            }
            if (this.user.getForgeClientHandler().isFmlTokenInHandshake()) {
                LoginResult.Property[] newp = Arrays.copyOf(properties, (int)(properties.length + 2));
                newp[newp.length - 2] = new LoginResult.Property((String)"forgeClient", (String)"true", null);
                newp[newp.length - 1] = new LoginResult.Property((String)"extraData", (String)this.user.getExtraDataInHandshake().replaceAll((String)"\u0000", (String)"\u0001"), (String)"");
                properties = newp;
            }
            if (properties.length > 0) {
                newHost = newHost + "\u0000" + BungeeCord.getInstance().gson.toJson((Object)properties);
            }
            copiedHandshake.setHost((String)newHost);
        } else if (!this.user.getExtraDataInHandshake().isEmpty()) {
            copiedHandshake.setHost((String)(copiedHandshake.getHost() + this.user.getExtraDataInHandshake()));
        }
        channel.write((Object)copiedHandshake);
        channel.setProtocol((Protocol)Protocol.LOGIN);
        channel.write((Object)new LoginRequest((String)this.user.getName()));
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        this.user.getPendingConnects().remove((Object)this.target);
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        if (packet == null) {
            return;
        }
        if (packet.packet != null) return;
        if (this.ch == null) return;
        System.out.println((String)("{CasualProtector} " + this.ch.getRemoteAddress().getHostString() + " Unexpected packet received during server login process!"));
    }

    @Override
    public void handle(LoginSuccess loginSuccess) throws Exception {
        Preconditions.checkState((boolean)(this.thisState == State.LOGIN_SUCCESS), (Object)"Not expecting LOGIN_SUCCESS");
        this.ch.setProtocol((Protocol)Protocol.GAME);
        this.thisState = State.LOGIN;
        if (this.user.getServer() == null) throw CancelSendSignal.INSTANCE;
        if (!this.user.getForgeClientHandler().isHandshakeComplete()) throw CancelSendSignal.INSTANCE;
        if (!this.user.getServer().isForgeServer()) throw CancelSendSignal.INSTANCE;
        this.user.getForgeClientHandler().resetHandshake();
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(SetCompression setCompression) throws Exception {
        this.ch.setCompressionThreshold((int)setCompression.getThreshold());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handle(Login login) throws Exception {
        Preconditions.checkState((boolean)(this.thisState == State.LOGIN), (Object)"Not expecting LOGIN");
        ServerConnection server = new ServerConnection((ChannelWrapper)this.ch, (BungeeServerInfo)this.target);
        ServerConnectedEvent event = new ServerConnectedEvent((ProxiedPlayer)this.user, (Server)server);
        if (server.isForgeServer() && this.user.isForgeUser()) {
            ((MinecraftDecoder)server.getCh().getHandle().pipeline().get((String)"packet-decoder")).setSupportsForge((boolean)true);
            ((MinecraftDecoder)this.user.getCh().getHandle().pipeline().get((String)"packet-decoder")).setSupportsForge((boolean)true);
        }
        this.bungee.getPluginManager().callEvent(event);
        this.ch.write((Object)BungeeCord.getInstance().registerChannels((int)this.user.getPendingConnection().getVersion()));
        Queue<DefinedPacket> packetQueue = this.target.getPacketQueue();
        Queue<DefinedPacket> queue = packetQueue;
        // MONITORENTER : queue
        while (!packetQueue.isEmpty()) {
            this.ch.write((Object)packetQueue.poll());
        }
        // MONITOREXIT : queue
        for (PluginMessage message : this.user.getPendingConnection().getRelayMessages()) {
            this.ch.write((Object)message);
        }
        if (!this.user.isDisableEntityMetadataRewrite() && this.user.getSettings() != null) {
            this.ch.write((Object)this.user.getSettings());
        }
        if (this.user.getForgeClientHandler().getClientModList() == null && !this.user.getForgeClientHandler().isHandshakeComplete()) {
            this.user.getForgeClientHandler().setHandshakeComplete();
        }
        if (this.user.getServer() == null) {
            this.user.setClientEntityId((int)login.getEntityId());
            this.user.setServerEntityId((int)login.getEntityId());
            Login modLogin = new Login((int)login.getEntityId(), (short)login.getGameMode(), (int)((byte)login.getDimension()), (long)login.getHashedSeed(), (short)login.getDifficulty(), (short)((short)((byte)this.user.getPendingConnection().getListener().getTabListSize())), (String)login.getLevelType(), (int)login.getViewDistance(), (boolean)login.isReducedDebugInfo(), (boolean)login.isEnableRespawnScreen());
            this.user.unsafe().sendPacket((DefinedPacket)modLogin);
            ByteBuf brand = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString((String)(this.bungee.getName() + " (" + this.bungee.getVersion() + ")"), (ByteBuf)brand);
            this.user.unsafe().sendPacket((DefinedPacket)new PluginMessage((String)(this.user.getPendingConnection().getVersion() >= 393 ? "minecraft:brand" : "MC|Brand"), (ByteBuf)brand, (boolean)this.handshakeHandler.isServerForge()));
            brand.release();
            this.user.setDimension((int)login.getDimension());
        } else {
            this.user.getServer().setObsolete((boolean)true);
            this.user.getTabListHandler().onServerChange();
            Scoreboard serverScoreboard = this.user.getServerSentScoreboard();
            for (Objective objective : serverScoreboard.getObjectives()) {
                this.user.unsafe().sendPacket((DefinedPacket)new ScoreboardObjective((String)objective.getName(), (String)objective.getValue(), (ScoreboardObjective.HealthDisplay)ScoreboardObjective.HealthDisplay.fromString((String)objective.getType()), (byte)1));
            }
            for (Score score : serverScoreboard.getScores()) {
                this.user.unsafe().sendPacket((DefinedPacket)new ScoreboardScore((String)score.getItemName(), (byte)1, (String)score.getScoreName(), (int)score.getValue()));
            }
            for (net.md_5.bungee.api.score.Team team : serverScoreboard.getTeams()) {
                this.user.unsafe().sendPacket((DefinedPacket)new Team((String)team.getName()));
            }
            serverScoreboard.clear();
            for (UUID bossbar : this.user.getSentBossBars()) {
                this.user.unsafe().sendPacket((DefinedPacket)new BossBar((UUID)bossbar, (int)1));
            }
            this.user.getSentBossBars().clear();
            this.user.unsafe().sendPacket((DefinedPacket)new EntityStatus((int)this.user.getClientEntityId(), (byte)(login.isReducedDebugInfo() ? (byte)22 : 23)));
            this.user.setDimensionChange((boolean)true);
            if (!this.user.isDisableEntityMetadataRewrite() && login.getDimension() == this.user.getDimension()) {
                this.user.unsafe().sendPacket((DefinedPacket)new Respawn((int)(login.getDimension() >= 0 ? -1 : 0), (short)login.getDifficulty(), (long)login.getHashedSeed(), (short)login.getGameMode(), (String)login.getLevelType()));
            }
            this.user.setServerEntityId((int)login.getEntityId());
            if (this.user.isDisableEntityMetadataRewrite()) {
                this.user.setClientEntityId((int)login.getEntityId());
                if (this.user.getDimension() != login.getDimension()) {
                    this.user.unsafe().sendPacket((DefinedPacket)new Respawn((int)(this.user.getDimension() == 0 ? -1 : 0), (short)login.getDifficulty(), (long)login.getHashedSeed(), (short)login.getGameMode(), (String)login.getLevelType()));
                }
                Login modLogin = new Login((int)login.getEntityId(), (short)login.getGameMode(), (int)login.getDimension(), (long)login.getHashedSeed(), (short)login.getDifficulty(), (short)login.getMaxPlayers(), (String)login.getLevelType(), (int)login.getViewDistance(), (boolean)login.isReducedDebugInfo(), (boolean)login.isEnableRespawnScreen());
                this.user.unsafe().sendPacket((DefinedPacket)modLogin);
                if (this.user.getDimension() == login.getDimension()) {
                    this.user.unsafe().sendPacket((DefinedPacket)new Respawn((int)(this.user.getDimension() == 0 ? -1 : 0), (short)login.getDifficulty(), (long)login.getHashedSeed(), (short)login.getGameMode(), (String)login.getLevelType()));
                }
            }
            this.user.unsafe().sendPacket((DefinedPacket)new Respawn((int)login.getDimension(), (short)login.getDifficulty(), (long)login.getHashedSeed(), (short)login.getGameMode(), (String)login.getLevelType()));
            if (this.user.getPendingConnection().getVersion() >= 477) {
                this.user.unsafe().sendPacket((DefinedPacket)new ViewDistance((int)login.getViewDistance()));
            }
            this.user.setDimension((int)login.getDimension());
            this.user.getServer().disconnect((String)"Quitting");
        }
        if (!this.user.isActive()) {
            server.disconnect((String)"Quitting");
            this.bungee.getLogger().warning((String)"No client connected for pending server!");
            return;
        }
        this.target.addPlayer((ProxiedPlayer)this.user);
        this.user.getPendingConnects().remove((Object)this.target);
        this.user.setServerJoinQueue(null);
        this.user.setDimensionChange((boolean)false);
        this.user.setServer((ServerConnection)server);
        this.ch.getHandle().pipeline().get(HandlerBoss.class).setHandler((PacketHandler)new DownstreamBridge((ProxyServer)this.bungee, (UserConnection)this.user, (ServerConnection)server));
        this.bungee.getPluginManager().callEvent(new ServerSwitchEvent((ProxiedPlayer)this.user));
        this.thisState = State.FINISHED;
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(EncryptionRequest encryptionRequest) throws Exception {
        throw new QuietException((String)"Server is online mode!");
    }

    @Override
    public void handle(Kick kick) throws Exception {
        ServerInfo def = this.user.updateAndGetNextServer((ServerInfo)this.target);
        ServerKickEvent event = new ServerKickEvent((ProxiedPlayer)this.user, (ServerInfo)this.target, (BaseComponent[])ComponentSerializer.parse((String)kick.getMessage()), (ServerInfo)def, (ServerKickEvent.State)ServerKickEvent.State.CONNECTING);
        if (event.getKickReason().toLowerCase((Locale)Locale.ROOT).contains((CharSequence)"outdated") && def != null) {
            event.setCancelled((boolean)true);
        }
        this.bungee.getPluginManager().callEvent(event);
        if (event.isCancelled() && event.getCancelServer() != null) {
            this.obsolete = true;
            this.user.connect((ServerInfo)event.getCancelServer(), (ServerConnectEvent.Reason)ServerConnectEvent.Reason.KICK_REDIRECT);
            throw CancelSendSignal.INSTANCE;
        }
        String message = this.bungee.getTranslation((String)"connect_kick", (Object[])new Object[]{this.target.getName(), event.getKickReason()});
        if (this.user.isDimensionChange()) {
            this.user.disconnect((String)message);
            throw CancelSendSignal.INSTANCE;
        }
        this.user.sendMessage((String)message);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (BungeeCord.getInstance().config.isForgeSupport()) {
            if (pluginMessage.getTag().equals((Object)"REGISTER")) {
                Set<String> channels = ForgeUtils.readRegisteredChannels((PluginMessage)pluginMessage);
                boolean isForgeServer = false;
                for (String channel : channels) {
                    if (!channel.equals((Object)"FML|HS")) continue;
                    if (this.user.getServer() != null && this.user.getForgeClientHandler().isHandshakeComplete()) {
                        this.user.getForgeClientHandler().resetHandshake();
                    }
                    isForgeServer = true;
                    break;
                }
                if (isForgeServer && !this.handshakeHandler.isServerForge()) {
                    this.handshakeHandler.setServerAsForgeServer();
                    this.user.setForgeServerHandler((ForgeServerHandler)this.handshakeHandler);
                }
            }
            if (pluginMessage.getTag().equals((Object)"FML|HS") || pluginMessage.getTag().equals((Object)"FORGE")) {
                this.handshakeHandler.handle((PluginMessage)pluginMessage);
                throw CancelSendSignal.INSTANCE;
            }
        }
        this.user.unsafe().sendPacket((DefinedPacket)pluginMessage);
    }

    @Override
    public String toString() {
        return "[" + this.user.getName() + "|" + this.user.getAddress() + "] <-> ServerConnector [" + this.target.getName() + "]";
    }

    public ServerConnector(ProxyServer bungee, UserConnection user, BungeeServerInfo target) {
        this.bungee = bungee;
        this.user = user;
        this.target = target;
    }

    public ForgeServerHandler getHandshakeHandler() {
        return this.handshakeHandler;
    }
}

