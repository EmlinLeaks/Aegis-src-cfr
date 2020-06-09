/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package net.md_5.bungee;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.PlayerSkinConfiguration;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.entitymap.EntityMap_Dummy;
import net.md_5.bungee.forge.ForgeClientHandler;
import net.md_5.bungee.forge.ForgeServerHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.tab.ServerUnique;
import net.md_5.bungee.tab.TabList;
import net.md_5.bungee.util.CaseInsensitiveSet;
import net.md_5.bungee.util.ChatComponentTransformer;

public final class UserConnection
implements ProxiedPlayer {
    @NonNull
    private final ProxyServer bungee;
    @NonNull
    private final ChannelWrapper ch;
    @NonNull
    private final String name;
    private final InitialHandler pendingConnection;
    private ServerConnection server;
    private int dimension;
    private boolean dimensionChange = true;
    private final Collection<ServerInfo> pendingConnects = new HashSet<ServerInfo>();
    private long sentPingTime;
    private int ping = 100;
    private ServerInfo reconnectServer;
    private TabList tabListHandler;
    private int gamemode;
    private int compressionThreshold = -1;
    private Queue<String> serverJoinQueue;
    private final Collection<String> groups = new CaseInsensitiveSet();
    private final Collection<String> permissions = new CaseInsensitiveSet();
    private int clientEntityId;
    private int serverEntityId;
    private ClientSettings settings;
    private final Scoreboard serverSentScoreboard = new Scoreboard();
    private final Collection<UUID> sentBossBars = new HashSet<UUID>();
    private final Multimap<Integer, Integer> potions = HashMultimap.create();
    private String displayName;
    private EntityMap entityRewrite;
    private Locale locale;
    private ForgeClientHandler forgeClientHandler;
    private ForgeServerHandler forgeServerHandler;
    private boolean disableEntityMetadaRewrite;
    private final Connection.Unsafe unsafe = new Connection.Unsafe((UserConnection)this){
        final /* synthetic */ UserConnection this$0;
        {
            this.this$0 = this$0;
        }

        public void sendPacket(DefinedPacket packet) {
            UserConnection.access$000((UserConnection)this.this$0).write((Object)packet);
        }
    };

    public void init() {
        this.disableEntityMetadaRewrite = false;
        this.entityRewrite = this.disableEntityMetadaRewrite ? EntityMap_Dummy.INSTANCE : EntityMap.getEntityMap((int)this.getPendingConnection().getVersion());
        this.displayName = this.name;
        this.tabListHandler = new ServerUnique((ProxiedPlayer)this);
        Collection<String> g = this.bungee.getConfigurationAdapter().getGroups((String)this.name);
        g.addAll(this.bungee.getConfigurationAdapter().getGroups((String)this.getUniqueId().toString()));
        Iterator<String> iterator = g.iterator();
        do {
            if (!iterator.hasNext()) {
                this.forgeClientHandler = new ForgeClientHandler((UserConnection)this);
                if (!this.getPendingConnection().getExtraDataInHandshake().contains((CharSequence)"\u0000FML\u0000")) return;
                this.forgeClientHandler.setFmlTokenInHandshake((boolean)true);
                return;
            }
            String s = iterator.next();
            this.addGroups((String[])new String[]{s});
        } while (true);
    }

    public void sendPacket(PacketWrapper packet) {
        this.ch.write((Object)packet);
    }

    @Deprecated
    public boolean isActive() {
        if (this.ch.isClosed()) return false;
        return true;
    }

    @Override
    public void setDisplayName(String name) {
        Preconditions.checkNotNull(name, (Object)"displayName");
        this.displayName = name;
    }

    @Override
    public void connect(ServerInfo target) {
        this.connect((ServerInfo)target, null, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.PLUGIN);
    }

    @Override
    public void connect(ServerInfo target, ServerConnectEvent.Reason reason) {
        this.connect((ServerInfo)target, null, (boolean)false, (ServerConnectEvent.Reason)reason);
    }

    @Override
    public void connect(ServerInfo target, Callback<Boolean> callback) {
        this.connect((ServerInfo)target, callback, (boolean)false, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.PLUGIN);
    }

    @Override
    public void connect(ServerInfo target, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
        this.connect((ServerInfo)target, callback, (boolean)false, (ServerConnectEvent.Reason)reason);
    }

    @Deprecated
    public void connectNow(ServerInfo target) {
        this.connectNow((ServerInfo)target, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.UNKNOWN);
    }

    public void connectNow(ServerInfo target, ServerConnectEvent.Reason reason) {
        this.dimensionChange = true;
        this.connect((ServerInfo)target, (ServerConnectEvent.Reason)reason);
    }

    public ServerInfo updateAndGetNextServer(ServerInfo currentTarget) {
        ServerInfo candidate;
        if (this.serverJoinQueue == null) {
            this.serverJoinQueue = new LinkedList<String>(this.getPendingConnection().getListener().getServerPriority());
        }
        ServerInfo next = null;
        do {
            if (this.serverJoinQueue.isEmpty()) return next;
        } while (Objects.equals((Object)currentTarget, (Object)(candidate = ProxyServer.getInstance().getServerInfo((String)this.serverJoinQueue.remove()))));
        return candidate;
    }

    public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry) {
        this.connect((ServerInfo)info, callback, (boolean)retry, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.PLUGIN);
    }

    public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry, ServerConnectEvent.Reason reason) {
        this.connect((ServerInfo)info, callback, (boolean)retry, (ServerConnectEvent.Reason)reason, (int)5000);
    }

    public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry, int timeout) {
        this.connect((ServerInfo)info, callback, (boolean)retry, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.PLUGIN, (int)timeout);
    }

    public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry, ServerConnectEvent.Reason reason, int timeout) {
        Preconditions.checkNotNull(info, (Object)"info");
        ServerConnectRequest.Builder builder = ServerConnectRequest.builder().retry((boolean)retry).reason((ServerConnectEvent.Reason)reason).target((ServerInfo)info);
        builder.connectTimeout((int)timeout);
        if (callback != null) {
            builder.callback((Callback<ServerConnectRequest.Result>)new Callback<ServerConnectRequest.Result>((UserConnection)this, callback){
                final /* synthetic */ Callback val$callback;
                final /* synthetic */ UserConnection this$0;
                {
                    this.this$0 = this$0;
                    this.val$callback = callback;
                }

                public void done(ServerConnectRequest.Result result, Throwable error) {
                    this.val$callback.done(result == ServerConnectRequest.Result.SUCCESS ? Boolean.TRUE : Boolean.FALSE, (Throwable)error);
                }
            });
        }
        this.connect((ServerConnectRequest)builder.build());
    }

    @Override
    public void connect(ServerConnectRequest request) {
        Preconditions.checkNotNull(request, (Object)"request");
        Callback<ServerConnectRequest.Result> callback = request.getCallback();
        ServerConnectEvent event = new ServerConnectEvent((ProxiedPlayer)this, (ServerInfo)request.getTarget(), (ServerConnectEvent.Reason)request.getReason());
        if (this.bungee.getPluginManager().callEvent(event).isCancelled()) {
            if (callback == null) return;
            callback.done((ServerConnectRequest.Result)ServerConnectRequest.Result.EVENT_CANCEL, null);
            return;
        }
        BungeeServerInfo target = (BungeeServerInfo)event.getTarget();
        if (this.getServer() != null && Objects.equals((Object)this.getServer().getInfo(), (Object)target)) {
            if (callback != null) {
                callback.done((ServerConnectRequest.Result)ServerConnectRequest.Result.ALREADY_CONNECTED, null);
            }
            this.sendMessage((String)this.bungee.getTranslation((String)"already_connected", (Object[])new Object[0]));
            return;
        }
        if (this.pendingConnects.contains((Object)target)) {
            if (callback != null) {
                callback.done((ServerConnectRequest.Result)ServerConnectRequest.Result.ALREADY_CONNECTING, null);
            }
            this.sendMessage((String)this.bungee.getTranslation((String)"already_connecting", (Object[])new Object[0]));
            return;
        }
        this.pendingConnects.add((ServerInfo)target);
        ChannelInitializer initializer = new ChannelInitializer((UserConnection)this, (BungeeServerInfo)target){
            final /* synthetic */ BungeeServerInfo val$target;
            final /* synthetic */ UserConnection this$0;
            {
                this.this$0 = this$0;
                this.val$target = bungeeServerInfo;
            }

            protected void initChannel(Channel ch) throws Exception {
                PipelineUtils.BASE.initChannel((Channel)ch);
                ch.pipeline().addAfter((String)"frame-decoder", (String)"packet-decoder", (ChannelHandler)new net.md_5.bungee.protocol.MinecraftDecoder((net.md_5.bungee.protocol.Protocol)net.md_5.bungee.protocol.Protocol.HANDSHAKE, (boolean)false, (int)this.this$0.getPendingConnection().getVersion()));
                ch.pipeline().addAfter((String)"frame-prepender", (String)"packet-encoder", (ChannelHandler)new net.md_5.bungee.protocol.MinecraftEncoder((net.md_5.bungee.protocol.Protocol)net.md_5.bungee.protocol.Protocol.HANDSHAKE, (boolean)false, (int)this.this$0.getPendingConnection().getVersion()));
                ch.pipeline().get(net.md_5.bungee.netty.HandlerBoss.class).setHandler((net.md_5.bungee.netty.PacketHandler)new net.md_5.bungee.ServerConnector((ProxyServer)UserConnection.access$100((UserConnection)this.this$0), (UserConnection)this.this$0, (BungeeServerInfo)this.val$target));
            }
        };
        ChannelFutureListener listener = future -> {
            if (callback != null) {
                callback.done(future.isSuccess() ? ServerConnectRequest.Result.SUCCESS : ServerConnectRequest.Result.FAIL, (Throwable)future.cause());
            }
            if (future.isSuccess()) return;
            future.channel().close();
            this.pendingConnects.remove((Object)target);
            ServerInfo def = this.updateAndGetNextServer((ServerInfo)target);
            if (request.isRetry() && def != null && (this.getServer() == null || def != this.getServer().getInfo())) {
                this.sendMessage((String)this.bungee.getTranslation((String)"fallback_lobby", (Object[])new Object[0]));
                this.connect((ServerInfo)def, null, (boolean)true, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.LOBBY_FALLBACK, (int)request.getConnectTimeout());
                return;
            }
            if (this.dimensionChange) {
                this.disconnect((String)this.bungee.getTranslation((String)"fallback_kick", (Object[])new Object[]{future.cause().getClass().getName()}));
                return;
            }
            this.sendMessage((String)this.bungee.getTranslation((String)"fallback_kick", (Object[])new Object[]{future.cause().getClass().getName()}));
        };
        Bootstrap b = ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(PipelineUtils.getChannel())).group((EventLoopGroup)this.ch.getHandle().eventLoop())).handler((ChannelHandler)initializer)).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf((int)request.getConnectTimeout()))).remoteAddress((SocketAddress)target.getAddress());
        if (this.getPendingConnection().getListener().isSetLocalAddress() && !PlatformDependent.isWindows()) {
            b.localAddress((String)this.getPendingConnection().getListener().getHost().getHostString(), (int)0);
        }
        b.connect().addListener((GenericFutureListener<? extends Future<? super Void>>)listener);
    }

    @Override
    public void disconnect(String reason) {
        this.disconnect0((BaseComponent[])TextComponent.fromLegacyText((String)reason));
    }

    @Override
    public void disconnect(BaseComponent ... reason) {
        this.disconnect0((BaseComponent[])reason);
    }

    @Override
    public void disconnect(BaseComponent reason) {
        this.disconnect0((BaseComponent[])new BaseComponent[]{reason});
    }

    public void disconnect0(BaseComponent ... reason) {
        if (this.ch.isClosing()) return;
        this.bungee.getLogger().log((Level)Level.INFO, (String)"[{0}] disconnected with: {1}", (Object[])new Object[]{this.getName(), BaseComponent.toLegacyText((BaseComponent[])reason)});
        this.ch.delayedClose((Kick)new Kick((String)ComponentSerializer.toString((BaseComponent[])reason)));
        if (this.server == null) return;
        this.server.setObsolete((boolean)true);
        this.server.disconnect((String)"Quitting");
    }

    @Override
    public void chat(String message) {
        Preconditions.checkState((boolean)(this.server != null), (Object)"Not connected to server");
        this.server.getCh().write((Object)new Chat((String)message));
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage((BaseComponent[])TextComponent.fromLegacyText((String)message));
    }

    @Override
    public void sendMessages(String ... messages) {
        String[] arrstring = messages;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String message = arrstring[n2];
            this.sendMessage((String)message);
            ++n2;
        }
    }

    @Override
    public void sendMessage(BaseComponent ... message) {
        this.sendMessage((ChatMessageType)ChatMessageType.SYSTEM, (BaseComponent[])message);
    }

    @Override
    public void sendMessage(BaseComponent message) {
        this.sendMessage((ChatMessageType)ChatMessageType.SYSTEM, (BaseComponent)message);
    }

    private void sendMessage(ChatMessageType position, String message) {
        this.unsafe().sendPacket((DefinedPacket)new Chat((String)message, (byte)((byte)position.ordinal())));
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent ... message) {
        message = ChatComponentTransformer.getInstance().transform((ProxiedPlayer)this, (BaseComponent[])message);
        if (position == ChatMessageType.ACTION_BAR) {
            this.sendMessage((ChatMessageType)position, (String)ComponentSerializer.toString((BaseComponent)new TextComponent((String)BaseComponent.toLegacyText((BaseComponent[])message))));
            return;
        }
        this.sendMessage((ChatMessageType)position, (String)ComponentSerializer.toString((BaseComponent[])message));
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent message) {
        message = ChatComponentTransformer.getInstance().transform((ProxiedPlayer)this, (BaseComponent[])new BaseComponent[]{message})[0];
        if (position == ChatMessageType.ACTION_BAR) {
            this.sendMessage((ChatMessageType)position, (String)ComponentSerializer.toString((BaseComponent)new TextComponent((String)BaseComponent.toLegacyText((BaseComponent[])new BaseComponent[]{message}))));
            return;
        }
        this.sendMessage((ChatMessageType)position, (String)ComponentSerializer.toString((BaseComponent)message));
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.unsafe().sendPacket((DefinedPacket)new PluginMessage((String)channel, (byte[])data, (boolean)this.forgeClientHandler.isForgeUser()));
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.ch.getRemoteAddress();
    }

    @Override
    public Collection<String> getGroups() {
        return Collections.unmodifiableCollection(this.groups);
    }

    @Override
    public void addGroups(String ... groups) {
        String[] arrstring = groups;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String group = arrstring[n2];
            this.groups.add((String)group);
            for (String permission : this.bungee.getConfigurationAdapter().getPermissions((String)group)) {
                this.setPermission((String)permission, (boolean)true);
            }
            ++n2;
        }
    }

    @Override
    public void removeGroups(String ... groups) {
        String[] arrstring = groups;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String group = arrstring[n2];
            this.groups.remove((Object)group);
            for (String permission : this.bungee.getConfigurationAdapter().getPermissions((String)group)) {
                this.setPermission((String)permission, (boolean)false);
            }
            ++n2;
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.bungee.getPluginManager().callEvent(new PermissionCheckEvent((CommandSender)this, (String)permission, (boolean)this.permissions.contains((Object)permission))).hasPermission();
    }

    @Override
    public void setPermission(String permission, boolean value) {
        if (value) {
            this.permissions.add((String)permission);
            return;
        }
        this.permissions.remove((Object)permission);
    }

    @Override
    public Collection<String> getPermissions() {
        return Collections.unmodifiableCollection(this.permissions);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public Connection.Unsafe unsafe() {
        return this.unsafe;
    }

    @Override
    public String getUUID() {
        return this.getPendingConnection().getUUID();
    }

    @Override
    public UUID getUniqueId() {
        return this.getPendingConnection().getUniqueId();
    }

    public void setSettings(ClientSettings settings) {
        this.settings = settings;
        this.locale = null;
    }

    @Override
    public Locale getLocale() {
        Locale locale;
        if (this.locale == null && this.settings != null) {
            locale = this.locale = Locale.forLanguageTag((String)this.settings.getLocale().replace((char)'_', (char)'-'));
            return locale;
        }
        locale = this.locale;
        return locale;
    }

    @Override
    public byte getViewDistance() {
        if (this.settings == null) return 10;
        byte by = this.settings.getViewDistance();
        return by;
    }

    @Override
    public ProxiedPlayer.ChatMode getChatMode() {
        if (this.settings == null) {
            return ProxiedPlayer.ChatMode.SHOWN;
        }
        switch (this.settings.getChatFlags()) {
            default: {
                return ProxiedPlayer.ChatMode.SHOWN;
            }
            case 1: {
                return ProxiedPlayer.ChatMode.COMMANDS_ONLY;
            }
            case 2: 
        }
        return ProxiedPlayer.ChatMode.HIDDEN;
    }

    @Override
    public boolean hasChatColors() {
        if (this.settings == null) return true;
        if (this.settings.isChatColours()) return true;
        return false;
    }

    @Override
    public SkinConfiguration getSkinParts() {
        SkinConfiguration skinConfiguration;
        if (this.settings != null) {
            skinConfiguration = new PlayerSkinConfiguration((byte)this.settings.getSkinParts());
            return skinConfiguration;
        }
        skinConfiguration = PlayerSkinConfiguration.SKIN_SHOW_ALL;
        return skinConfiguration;
    }

    @Override
    public ProxiedPlayer.MainHand getMainHand() {
        ProxiedPlayer.MainHand mainHand;
        if (this.settings != null && this.settings.getMainHand() != 1) {
            mainHand = ProxiedPlayer.MainHand.LEFT;
            return mainHand;
        }
        mainHand = ProxiedPlayer.MainHand.RIGHT;
        return mainHand;
    }

    @Override
    public boolean isForgeUser() {
        return this.forgeClientHandler.isForgeUser();
    }

    @Override
    public Map<String, String> getModList() {
        if (this.forgeClientHandler.getClientModList() != null) return ImmutableMap.copyOf(this.forgeClientHandler.getClientModList());
        return ImmutableMap.of();
    }

    @Override
    public void setTabHeader(BaseComponent header, BaseComponent footer) {
        header = ChatComponentTransformer.getInstance().transform((ProxiedPlayer)this, (BaseComponent[])new BaseComponent[]{header})[0];
        footer = ChatComponentTransformer.getInstance().transform((ProxiedPlayer)this, (BaseComponent[])new BaseComponent[]{footer})[0];
        this.unsafe().sendPacket((DefinedPacket)new PlayerListHeaderFooter((String)ComponentSerializer.toString((BaseComponent)header), (String)ComponentSerializer.toString((BaseComponent)footer)));
    }

    @Override
    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
        header = ChatComponentTransformer.getInstance().transform((ProxiedPlayer)this, (BaseComponent[])header);
        footer = ChatComponentTransformer.getInstance().transform((ProxiedPlayer)this, (BaseComponent[])footer);
        this.unsafe().sendPacket((DefinedPacket)new PlayerListHeaderFooter((String)ComponentSerializer.toString((BaseComponent[])header), (String)ComponentSerializer.toString((BaseComponent[])footer)));
    }

    @Override
    public void resetTabHeader() {
        this.setTabHeader((BaseComponent)((BaseComponent)null), null);
    }

    @Override
    public void sendTitle(Title title) {
        title.send((ProxiedPlayer)this);
    }

    public String getExtraDataInHandshake() {
        return this.getPendingConnection().getExtraDataInHandshake();
    }

    public void setCompressionThreshold(int compressionThreshold) {
        if (this.ch.isClosing()) return;
        if (this.compressionThreshold != -1) return;
        if (compressionThreshold < 0) return;
        this.compressionThreshold = compressionThreshold;
        this.unsafe.sendPacket((DefinedPacket)new SetCompression((int)compressionThreshold));
        this.ch.setCompressionThreshold((int)compressionThreshold);
    }

    @Override
    public boolean isConnected() {
        if (this.ch.isClosed()) return false;
        return true;
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.serverSentScoreboard;
    }

    public boolean isDisableEntityMetadataRewrite() {
        return this.disableEntityMetadaRewrite;
    }

    public UserConnection(@NonNull ProxyServer bungee, @NonNull ChannelWrapper ch, @NonNull String name, InitialHandler pendingConnection) {
        if (bungee == null) {
            throw new NullPointerException((String)"bungee is marked non-null but is null");
        }
        if (ch == null) {
            throw new NullPointerException((String)"ch is marked non-null but is null");
        }
        if (name == null) {
            throw new NullPointerException((String)"name is marked non-null but is null");
        }
        this.bungee = bungee;
        this.ch = ch;
        this.name = name;
        this.pendingConnection = pendingConnection;
    }

    @NonNull
    public ChannelWrapper getCh() {
        return this.ch;
    }

    @NonNull
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public InitialHandler getPendingConnection() {
        return this.pendingConnection;
    }

    @Override
    public ServerConnection getServer() {
        return this.server;
    }

    public void setServer(ServerConnection server) {
        this.server = server;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public boolean isDimensionChange() {
        return this.dimensionChange;
    }

    public void setDimensionChange(boolean dimensionChange) {
        this.dimensionChange = dimensionChange;
    }

    public Collection<ServerInfo> getPendingConnects() {
        return this.pendingConnects;
    }

    public long getSentPingTime() {
        return this.sentPingTime;
    }

    public void setSentPingTime(long sentPingTime) {
        this.sentPingTime = sentPingTime;
    }

    @Override
    public int getPing() {
        return this.ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    @Override
    public ServerInfo getReconnectServer() {
        return this.reconnectServer;
    }

    @Override
    public void setReconnectServer(ServerInfo reconnectServer) {
        this.reconnectServer = reconnectServer;
    }

    public TabList getTabListHandler() {
        return this.tabListHandler;
    }

    public int getGamemode() {
        return this.gamemode;
    }

    public void setGamemode(int gamemode) {
        this.gamemode = gamemode;
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public void setServerJoinQueue(Queue<String> serverJoinQueue) {
        this.serverJoinQueue = serverJoinQueue;
    }

    public int getClientEntityId() {
        return this.clientEntityId;
    }

    public void setClientEntityId(int clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public int getServerEntityId() {
        return this.serverEntityId;
    }

    public void setServerEntityId(int serverEntityId) {
        this.serverEntityId = serverEntityId;
    }

    public ClientSettings getSettings() {
        return this.settings;
    }

    public Scoreboard getServerSentScoreboard() {
        return this.serverSentScoreboard;
    }

    public Collection<UUID> getSentBossBars() {
        return this.sentBossBars;
    }

    public Multimap<Integer, Integer> getPotions() {
        return this.potions;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    public EntityMap getEntityRewrite() {
        return this.entityRewrite;
    }

    public ForgeClientHandler getForgeClientHandler() {
        return this.forgeClientHandler;
    }

    public void setForgeClientHandler(ForgeClientHandler forgeClientHandler) {
        this.forgeClientHandler = forgeClientHandler;
    }

    public ForgeServerHandler getForgeServerHandler() {
        return this.forgeServerHandler;
    }

    public void setForgeServerHandler(ForgeServerHandler forgeServerHandler) {
        this.forgeServerHandler = forgeServerHandler;
    }

    static /* synthetic */ ChannelWrapper access$000(UserConnection x0) {
        return x0.ch;
    }

    static /* synthetic */ ProxyServer access$100(UserConnection x0) {
        return x0.bungee;
    }
}

