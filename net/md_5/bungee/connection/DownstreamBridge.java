/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.connection;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import java.io.DataInput;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Position;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.Commands;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.protocol.packet.Team;
import net.md_5.bungee.tab.TabList;

public class DownstreamBridge
extends PacketHandler {
    private final ProxyServer bungee;
    private final UserConnection con;
    private final ServerConnection server;

    @Override
    public void exception(Throwable t) throws Exception {
        if (this.server.isObsolete()) {
            return;
        }
        ServerInfo def = this.con.updateAndGetNextServer((ServerInfo)this.server.getInfo());
        if (def != null) {
            this.server.setObsolete((boolean)true);
            this.con.connectNow((ServerInfo)def, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.SERVER_DOWN_REDIRECT);
            this.con.sendMessage((String)this.bungee.getTranslation((String)"server_went_down", (Object[])new Object[0]));
            return;
        }
        this.con.disconnect((String)Util.exception((Throwable)t));
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        this.server.getInfo().removePlayer((ProxiedPlayer)this.con);
        if (this.bungee.getReconnectHandler() != null) {
            this.bungee.getReconnectHandler().setServer((ProxiedPlayer)this.con);
        }
        if (!this.server.isObsolete()) {
            this.con.disconnect((String)this.bungee.getTranslation((String)"lost_connection", (Object[])new Object[0]));
        }
        ServerDisconnectEvent serverDisconnectEvent = new ServerDisconnectEvent((ProxiedPlayer)this.con, (ServerInfo)this.server.getInfo());
        this.bungee.getPluginManager().callEvent(serverDisconnectEvent);
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        if (this.server.isObsolete()) return false;
        return true;
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        this.con.getEntityRewrite().rewriteClientbound((ByteBuf)packet.buf, (int)this.con.getServerEntityId(), (int)this.con.getClientEntityId(), (int)this.con.getPendingConnection().getVersion());
        this.con.sendPacket((PacketWrapper)packet);
    }

    @Override
    public void handle(KeepAlive alive) throws Exception {
        this.server.setSentPingId((long)alive.getRandomId());
        this.con.setSentPingTime((long)System.currentTimeMillis());
    }

    @Override
    public void handle(PlayerListItem playerList) throws Exception {
        this.con.getTabListHandler().onUpdate((PlayerListItem)TabList.rewrite((PlayerListItem)playerList));
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(ScoreboardObjective objective) throws Exception {
        Scoreboard serverScoreboard = this.con.getServerSentScoreboard();
        switch (objective.getAction()) {
            case 0: {
                serverScoreboard.addObjective((Objective)new Objective((String)objective.getName(), (String)objective.getValue(), (String)objective.getType().toString()));
                return;
            }
            case 1: {
                serverScoreboard.removeObjective((String)objective.getName());
                return;
            }
            case 2: {
                Objective oldObjective = serverScoreboard.getObjective((String)objective.getName());
                if (oldObjective == null) return;
                oldObjective.setValue((String)objective.getValue());
                oldObjective.setType((String)objective.getType().toString());
                return;
            }
        }
        throw new IllegalArgumentException((String)("Unknown objective action: " + objective.getAction()));
    }

    @Override
    public void handle(ScoreboardScore score) throws Exception {
        Scoreboard serverScoreboard = this.con.getServerSentScoreboard();
        switch (score.getAction()) {
            case 0: {
                Score s = new Score((String)score.getItemName(), (String)score.getScoreName(), (int)score.getValue());
                serverScoreboard.removeScore((String)score.getItemName());
                serverScoreboard.addScore((Score)s);
                return;
            }
            case 1: {
                serverScoreboard.removeScore((String)score.getItemName());
                return;
            }
        }
        throw new IllegalArgumentException((String)("Unknown scoreboard action: " + score.getAction()));
    }

    @Override
    public void handle(ScoreboardDisplay displayScoreboard) throws Exception {
        Scoreboard serverScoreboard = this.con.getServerSentScoreboard();
        serverScoreboard.setName((String)displayScoreboard.getName());
        serverScoreboard.setPosition((Position)Position.values()[displayScoreboard.getPosition()]);
    }

    @Override
    public void handle(Team team) throws Exception {
        net.md_5.bungee.api.score.Team t;
        Scoreboard serverScoreboard = this.con.getServerSentScoreboard();
        if (team.getMode() == 1) {
            serverScoreboard.removeTeam((String)team.getName());
            return;
        }
        if (team.getMode() == 0) {
            t = new net.md_5.bungee.api.score.Team((String)team.getName());
            serverScoreboard.addTeam((net.md_5.bungee.api.score.Team)t);
        } else {
            t = serverScoreboard.getTeam((String)team.getName());
        }
        if (t == null) return;
        if (team.getMode() == 0 || team.getMode() == 2) {
            t.setDisplayName((String)team.getDisplayName());
            t.setPrefix((String)team.getPrefix());
            t.setSuffix((String)team.getSuffix());
            t.setFriendlyFire((byte)team.getFriendlyFire());
            t.setNameTagVisibility((String)team.getNameTagVisibility());
            t.setCollisionRule((String)team.getCollisionRule());
            t.setColor((int)team.getColor());
        }
        if (team.getPlayers() == null) return;
        String[] arrstring = team.getPlayers();
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String s = arrstring[n2];
            if (team.getMode() == 0 || team.getMode() == 3) {
                t.addPlayer((String)s);
            } else if (team.getMode() == 4) {
                t.removePlayer((String)s);
            }
            ++n2;
        }
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        ServerInfo info;
        String channel;
        byte[] payload;
        ServerInfo server;
        byte[] data;
        short len;
        ServerInfo server2;
        Object target;
        ProxiedPlayer player;
        DataInput in = pluginMessage.getStream();
        PluginMessageEvent event = new PluginMessageEvent((Connection)this.server, (Connection)this.con, (String)pluginMessage.getTag(), (byte[])((byte[])pluginMessage.getData().clone()));
        if (this.bungee.getPluginManager().callEvent(event).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }
        if (pluginMessage.getTag().equals((Object)(this.con.getPendingConnection().getVersion() >= 393 ? "minecraft:brand" : "MC|Brand"))) {
            ByteBuf brand = Unpooled.wrappedBuffer((byte[])pluginMessage.getData());
            String serverBrand = DefinedPacket.readString((ByteBuf)brand);
            brand.release();
            Preconditions.checkState((boolean)(!serverBrand.contains((CharSequence)this.bungee.getName())), (Object)"Cannot connect proxy to itself!");
            brand = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString((String)(this.bungee.getName() + " (" + this.bungee.getVersion() + ") <- " + serverBrand), (ByteBuf)brand);
            pluginMessage.setData((byte[])DefinedPacket.toArray((ByteBuf)brand));
            brand.release();
            this.con.unsafe().sendPacket((DefinedPacket)pluginMessage);
            throw CancelSendSignal.INSTANCE;
        }
        if (!pluginMessage.getTag().equals((Object)"BungeeCord")) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        String subChannel = in.readUTF();
        if (subChannel.equals((Object)"ForwardToPlayer")) {
            target = this.bungee.getPlayer((String)in.readUTF());
            if (target != null) {
                channel = in.readUTF();
                len = in.readShort();
                data = new byte[len];
                in.readFully((byte[])data);
                out.writeUTF((String)channel);
                out.writeShort((int)data.length);
                out.write((byte[])data);
                payload = out.toByteArray();
                target.getServer().sendData((String)"BungeeCord", (byte[])payload);
            }
            out = null;
        }
        if (subChannel.equals((Object)"Forward")) {
            target = in.readUTF();
            channel = in.readUTF();
            len = in.readShort();
            data = new byte[len];
            in.readFully((byte[])data);
            out.writeUTF((String)channel);
            out.writeShort((int)data.length);
            out.write((byte[])data);
            payload = out.toByteArray();
            out = null;
            if (((String)target).equals((Object)"ALL")) {
                for (ServerInfo server3 : this.bungee.getServers().values()) {
                    if (server3 == this.server.getInfo()) continue;
                    server3.sendData((String)"BungeeCord", (byte[])payload);
                }
            } else if (((String)target).equals((Object)"ONLINE")) {
                for (ServerInfo server3 : this.bungee.getServers().values()) {
                    if (server3 == this.server.getInfo()) continue;
                    server3.sendData((String)"BungeeCord", (byte[])payload, (boolean)false);
                }
            } else {
                ServerInfo server4 = this.bungee.getServerInfo((String)target);
                if (server4 != null) {
                    server4.sendData((String)"BungeeCord", (byte[])payload);
                }
            }
        }
        if (subChannel.equals((Object)"Connect") && (server = this.bungee.getServerInfo((String)in.readUTF())) != null) {
            this.con.connect((ServerInfo)server, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.PLUGIN_MESSAGE);
        }
        if (subChannel.equals((Object)"ConnectOther") && (player = this.bungee.getPlayer((String)in.readUTF())) != null && (server2 = this.bungee.getServerInfo((String)in.readUTF())) != null) {
            player.connect((ServerInfo)server2);
        }
        if (subChannel.equals((Object)"IP")) {
            out.writeUTF((String)"IP");
            out.writeUTF((String)this.con.getAddress().getHostString());
            out.writeInt((int)this.con.getAddress().getPort());
        }
        if (subChannel.equals((Object)"PlayerCount")) {
            target = in.readUTF();
            out.writeUTF((String)"PlayerCount");
            if (((String)target).equals((Object)"ALL")) {
                out.writeUTF((String)"ALL");
                out.writeInt((int)this.bungee.getOnlineCount());
            } else {
                server2 = this.bungee.getServerInfo((String)target);
                if (server2 != null) {
                    out.writeUTF((String)server2.getName());
                    out.writeInt((int)server2.getPlayers().size());
                }
            }
        }
        if (subChannel.equals((Object)"PlayerList")) {
            target = in.readUTF();
            out.writeUTF((String)"PlayerList");
            if (((String)target).equals((Object)"ALL")) {
                out.writeUTF((String)"ALL");
                out.writeUTF((String)Util.csv(this.bungee.getPlayers()));
            } else {
                server2 = this.bungee.getServerInfo((String)target);
                if (server2 != null) {
                    out.writeUTF((String)server2.getName());
                    out.writeUTF((String)Util.csv(server2.getPlayers()));
                }
            }
        }
        if (subChannel.equals((Object)"GetServers")) {
            out.writeUTF((String)"GetServers");
            out.writeUTF((String)Util.csv(this.bungee.getServers().keySet()));
        }
        if (subChannel.equals((Object)"Message")) {
            target = in.readUTF();
            String message = in.readUTF();
            if (((String)target).equals((Object)"ALL")) {
                for (ProxiedPlayer player2 : this.bungee.getPlayers()) {
                    player2.sendMessage((String)message);
                }
            } else {
                ProxiedPlayer player3 = this.bungee.getPlayer((String)target);
                if (player3 != null) {
                    player3.sendMessage((String)message);
                }
            }
        }
        if (subChannel.equals((Object)"GetServer")) {
            out.writeUTF((String)"GetServer");
            out.writeUTF((String)this.server.getInfo().getName());
        }
        if (subChannel.equals((Object)"UUID")) {
            out.writeUTF((String)"UUID");
            out.writeUTF((String)this.con.getUUID());
        }
        if (subChannel.equals((Object)"UUIDOther") && (player = this.bungee.getPlayer((String)in.readUTF())) != null) {
            out.writeUTF((String)"UUIDOther");
            out.writeUTF((String)player.getName());
            out.writeUTF((String)player.getUUID());
        }
        if (subChannel.equals((Object)"ServerIP") && (info = this.bungee.getServerInfo((String)in.readUTF())) != null && !info.getAddress().isUnresolved()) {
            out.writeUTF((String)"ServerIP");
            out.writeUTF((String)info.getName());
            out.writeUTF((String)info.getAddress().getAddress().getHostAddress());
            out.writeShort((int)info.getAddress().getPort());
        }
        if (subChannel.equals((Object)"KickPlayer") && (player = this.bungee.getPlayer((String)in.readUTF())) != null) {
            String kickReason = in.readUTF();
            player.disconnect((BaseComponent)new TextComponent((String)kickReason));
        }
        if (out == null) throw CancelSendSignal.INSTANCE;
        byte[] b = out.toByteArray();
        if (b.length == 0) throw CancelSendSignal.INSTANCE;
        this.server.sendData((String)"BungeeCord", (byte[])b);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(Kick kick) throws Exception {
        ServerInfo def = this.con.updateAndGetNextServer((ServerInfo)this.server.getInfo());
        ServerKickEvent event = this.bungee.getPluginManager().callEvent(new ServerKickEvent((ProxiedPlayer)this.con, (ServerInfo)this.server.getInfo(), (BaseComponent[])ComponentSerializer.parse((String)kick.getMessage()), (ServerInfo)def, (ServerKickEvent.State)ServerKickEvent.State.CONNECTED));
        if (event.isCancelled() && event.getCancelServer() != null) {
            this.con.connectNow((ServerInfo)event.getCancelServer(), (ServerConnectEvent.Reason)ServerConnectEvent.Reason.KICK_REDIRECT);
        } else {
            this.con.disconnect0((BaseComponent[])event.getKickReasonComponent());
        }
        this.server.setObsolete((boolean)true);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(SetCompression setCompression) throws Exception {
        this.server.getCh().setCompressionThreshold((int)setCompression.getThreshold());
    }

    @Override
    public void handle(TabCompleteResponse tabCompleteResponse) throws Exception {
        List<String> commands = tabCompleteResponse.getCommands();
        if (commands == null) {
            commands = Lists.transform(tabCompleteResponse.getSuggestions().getList(), new Function<Suggestion, String>((DownstreamBridge)this){
                final /* synthetic */ DownstreamBridge this$0;
                {
                    this.this$0 = this$0;
                }

                public String apply(Suggestion input) {
                    return input.getText();
                }
            });
        }
        TabCompleteResponseEvent tabCompleteResponseEvent = new TabCompleteResponseEvent((Connection)this.server, (Connection)this.con, new ArrayList<String>(commands));
        if (this.bungee.getPluginManager().callEvent(tabCompleteResponseEvent).isCancelled()) throw CancelSendSignal.INSTANCE;
        if (!commands.equals(tabCompleteResponseEvent.getSuggestions())) {
            if (tabCompleteResponse.getCommands() != null) {
                tabCompleteResponse.setCommands(tabCompleteResponseEvent.getSuggestions());
            } else {
                StringRange range = tabCompleteResponse.getSuggestions().getRange();
                tabCompleteResponse.setSuggestions((Suggestions)new Suggestions((StringRange)range, Lists.transform(tabCompleteResponseEvent.getSuggestions(), new Function<String, Suggestion>((DownstreamBridge)this, (StringRange)range){
                    final /* synthetic */ StringRange val$range;
                    final /* synthetic */ DownstreamBridge this$0;
                    {
                        this.this$0 = this$0;
                        this.val$range = stringRange;
                    }

                    public Suggestion apply(String input) {
                        return new Suggestion((StringRange)this.val$range, (String)input);
                    }
                })));
            }
        }
        this.con.unsafe().sendPacket((DefinedPacket)tabCompleteResponse);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(BossBar bossBar) {
        switch (bossBar.getAction()) {
            case 0: {
                this.con.getSentBossBars().add((UUID)bossBar.getUuid());
                return;
            }
            case 1: {
                this.con.getSentBossBars().remove((Object)bossBar.getUuid());
            }
        }
    }

    @Override
    public void handle(Respawn respawn) {
        this.con.setDimension((int)respawn.getDimension());
    }

    @Override
    public void handle(Commands commands) throws Exception {
        boolean modified = false;
        Iterator<Map.Entry<String, Command>> iterator = this.bungee.getPluginManager().getCommands().iterator();
        do {
            if (!iterator.hasNext()) {
                if (!modified) return;
                this.con.unsafe().sendPacket((DefinedPacket)commands);
                throw CancelSendSignal.INSTANCE;
            }
            Map.Entry<String, Command> command = iterator.next();
            if (this.bungee.getDisabledCommands().contains((Object)command.getKey()) || commands.getRoot().getChild((String)command.getKey()) != null || !command.getValue().hasPermission((CommandSender)this.con)) continue;
            CommandNode dummy = ((LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)command.getKey()).then(RequiredArgumentBuilder.argument((String)"args", StringArgumentType.greedyString()).suggests(Commands.SuggestionRegistry.ASK_SERVER))).build();
            commands.getRoot().addChild(dummy);
            modified = true;
        } while (true);
    }

    @Override
    public String toString() {
        return "[" + this.con.getName() + "] <-> DownstreamBridge <-> [" + this.server.getInfo().getName() + "]";
    }

    public DownstreamBridge(ProxyServer bungee, UserConnection con, ServerConnection server) {
        this.bungee = bungee;
        this.con = con;
        this.server = server;
    }
}

