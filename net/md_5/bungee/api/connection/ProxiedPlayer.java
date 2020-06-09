/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.connection;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;

public interface ProxiedPlayer
extends Connection,
CommandSender {
    public String getDisplayName();

    public void setDisplayName(String var1);

    public void sendMessage(ChatMessageType var1, BaseComponent ... var2);

    public void sendMessage(ChatMessageType var1, BaseComponent var2);

    public void connect(ServerInfo var1);

    public void connect(ServerInfo var1, ServerConnectEvent.Reason var2);

    public void connect(ServerInfo var1, Callback<Boolean> var2);

    public void connect(ServerInfo var1, Callback<Boolean> var2, ServerConnectEvent.Reason var3);

    public void connect(ServerConnectRequest var1);

    public Server getServer();

    public int getPing();

    public void sendData(String var1, byte[] var2);

    public PendingConnection getPendingConnection();

    public void chat(String var1);

    public ServerInfo getReconnectServer();

    public void setReconnectServer(ServerInfo var1);

    @Deprecated
    public String getUUID();

    public UUID getUniqueId();

    public Locale getLocale();

    public byte getViewDistance();

    public ChatMode getChatMode();

    public boolean hasChatColors();

    public SkinConfiguration getSkinParts();

    public MainHand getMainHand();

    public void setTabHeader(BaseComponent var1, BaseComponent var2);

    public void setTabHeader(BaseComponent[] var1, BaseComponent[] var2);

    public void resetTabHeader();

    public void sendTitle(Title var1);

    public boolean isForgeUser();

    public Map<String, String> getModList();

    public Scoreboard getScoreboard();
}

