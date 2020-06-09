/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.connection;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import java.io.DataInput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.forge.ForgeClientHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.tab.TabList;

public class UpstreamBridge
extends PacketHandler {
    private final ProxyServer bungee;
    private final UserConnection con;

    public UpstreamBridge(ProxyServer bungee, UserConnection con) {
        this.bungee = bungee;
        this.con = con;
        BungeeCord.getInstance().addConnection((UserConnection)con);
        con.getTabListHandler().onConnect();
        con.unsafe().sendPacket((DefinedPacket)BungeeCord.getInstance().registerChannels((int)con.getPendingConnection().getVersion()));
    }

    @Override
    public void exception(Throwable t) throws Exception {
        this.con.disconnect((String)Util.exception((Throwable)t));
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        PlayerDisconnectEvent event = new PlayerDisconnectEvent((ProxiedPlayer)this.con);
        this.bungee.getPluginManager().callEvent(event);
        this.con.getTabListHandler().onDisconnect();
        BungeeCord.getInstance().removeConnection((UserConnection)this.con);
        if (this.con.getServer() == null) return;
        PlayerListItem packet = new PlayerListItem();
        packet.setAction((PlayerListItem.Action)PlayerListItem.Action.REMOVE_PLAYER);
        PlayerListItem.Item item = new PlayerListItem.Item();
        item.setUuid((UUID)this.con.getUniqueId());
        packet.setItems((PlayerListItem.Item[])new PlayerListItem.Item[]{item});
        Iterator<ProxiedPlayer> iterator = this.con.getServer().getInfo().getPlayers().iterator();
        do {
            if (!iterator.hasNext()) {
                this.con.getServer().disconnect((String)"Quitting");
                return;
            }
            ProxiedPlayer player = iterator.next();
            player.unsafe().sendPacket((DefinedPacket)packet);
        } while (true);
    }

    @Override
    public void writabilityChanged(ChannelWrapper channel) throws Exception {
        if (this.con.getServer() == null) return;
        Channel server = this.con.getServer().getCh().getHandle();
        if (channel.getHandle().isWritable()) {
            server.config().setAutoRead((boolean)true);
            return;
        }
        server.config().setAutoRead((boolean)false);
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        if (this.con.getServer() != null) return true;
        if (packet.packet instanceof PluginMessage) return true;
        return false;
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        if (this.con.getServer() == null) return;
        this.con.getEntityRewrite().rewriteServerbound((ByteBuf)packet.buf, (int)this.con.getClientEntityId(), (int)this.con.getServerEntityId(), (int)this.con.getPendingConnection().getVersion());
        this.con.getServer().getCh().write((Object)packet);
    }

    @Override
    public void handle(KeepAlive alive) throws Exception {
        if (alive.getRandomId() != this.con.getServer().getSentPingId()) throw CancelSendSignal.INSTANCE;
        int newPing = (int)(System.currentTimeMillis() - this.con.getSentPingTime());
        this.con.getTabListHandler().onPingChange((int)newPing);
        this.con.setPing((int)newPing);
    }

    @Override
    public void handle(Chat chat) throws Exception {
        int maxLength = this.con.getPendingConnection().getVersion() >= 315 ? 256 : 100;
        Preconditions.checkArgument((boolean)(chat.getMessage().length() <= maxLength), (Object)"Chat message too long");
        ChatEvent chatEvent = new ChatEvent((Connection)this.con, (Connection)this.con.getServer(), (String)chat.getMessage());
        if (this.bungee.getPluginManager().callEvent(chatEvent).isCancelled()) throw CancelSendSignal.INSTANCE;
        chat.setMessage((String)chatEvent.getMessage());
        if (chatEvent.isCommand()) {
            if (this.bungee.getPluginManager().dispatchCommand((CommandSender)this.con, (String)chat.getMessage().substring((int)1))) throw CancelSendSignal.INSTANCE;
        }
        this.con.getServer().unsafe().sendPacket((DefinedPacket)chat);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(TabCompleteRequest tabComplete) throws Exception {
        ArrayList<String> suggestions = new ArrayList<String>();
        if (tabComplete.getCursor().startsWith((String)"/")) {
            this.bungee.getPluginManager().dispatchCommand((CommandSender)this.con, (String)tabComplete.getCursor().substring((int)1), suggestions);
        }
        TabCompleteEvent tabCompleteEvent = new TabCompleteEvent((Connection)this.con, (Connection)this.con.getServer(), (String)tabComplete.getCursor(), suggestions);
        this.bungee.getPluginManager().callEvent(tabCompleteEvent);
        if (tabCompleteEvent.isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }
        List<String> results = tabCompleteEvent.getSuggestions();
        if (results.isEmpty()) return;
        if (this.con.getPendingConnection().getVersion() < 393) {
            this.con.unsafe().sendPacket((DefinedPacket)new TabCompleteResponse(results));
            throw CancelSendSignal.INSTANCE;
        }
        int start = tabComplete.getCursor().lastIndexOf((int)32) + 1;
        int end = tabComplete.getCursor().length();
        StringRange range = StringRange.between((int)start, (int)end);
        LinkedList<Suggestion> brigadier = new LinkedList<Suggestion>();
        Iterator<String> iterator = results.iterator();
        do {
            if (!iterator.hasNext()) {
                this.con.unsafe().sendPacket((DefinedPacket)new TabCompleteResponse((int)tabComplete.getTransactionId(), (Suggestions)new Suggestions((StringRange)range, brigadier)));
                throw CancelSendSignal.INSTANCE;
            }
            String s = iterator.next();
            brigadier.add(new Suggestion((StringRange)range, (String)s));
        } while (true);
    }

    @Override
    public void handle(ClientSettings settings) throws Exception {
        this.con.setSettings((ClientSettings)settings);
        SettingsChangedEvent settingsEvent = new SettingsChangedEvent((ProxiedPlayer)this.con);
        this.bungee.getPluginManager().callEvent(settingsEvent);
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (pluginMessage.getTag().equals((Object)"BungeeCord")) {
            throw CancelSendSignal.INSTANCE;
        }
        if (BungeeCord.getInstance().config.isForgeSupport()) {
            if (pluginMessage.getTag().equals((Object)"FML") && pluginMessage.getStream().readUnsignedByte() == 1) {
                throw CancelSendSignal.INSTANCE;
            }
            if (pluginMessage.getTag().equals((Object)"FML|HS")) {
                this.con.getForgeClientHandler().handle((PluginMessage)pluginMessage);
                throw CancelSendSignal.INSTANCE;
            }
            if (this.con.getServer() != null && !this.con.getServer().isForgeServer() && pluginMessage.getData().length > 32767) {
                throw CancelSendSignal.INSTANCE;
            }
        }
        PluginMessageEvent event = new PluginMessageEvent((Connection)this.con, (Connection)this.con.getServer(), (String)pluginMessage.getTag(), (byte[])((byte[])pluginMessage.getData().clone()));
        if (this.bungee.getPluginManager().callEvent(event).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }
        if (!PluginMessage.SHOULD_RELAY.apply((PluginMessage)pluginMessage)) return;
        this.con.getPendingConnection().getRelayMessages().add((PluginMessage)pluginMessage);
    }

    @Override
    public String toString() {
        return "[" + this.con.getName() + "] -> UpstreamBridge";
    }
}

