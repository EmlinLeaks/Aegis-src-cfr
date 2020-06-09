/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.connection;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.ConnectionThrottle;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.UpstreamBridge;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.cipher.CipherDecoder;
import net.md_5.bungee.netty.cipher.CipherEncoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.util.BoundedArrayList;
import xyz.yooniks.aegis.AddressBlocker;
import xyz.yooniks.aegis.Aegis;

public class InitialHandler
extends PacketHandler
implements PendingConnection {
    private static final AddressBlocker ADDRESS_BLOCKER = Aegis.getInstance().getAddressBlocker();
    private final BungeeCord bungee;
    private ChannelWrapper ch;
    private final ListenerInfo listener;
    private Handshake handshake;
    private LoginRequest loginRequest;
    private EncryptionRequest request;
    private final List<PluginMessage> relayMessages = new BoundedArrayList<PluginMessage>((int)128);
    private State thisState = State.HANDSHAKE;
    private final Connection.Unsafe unsafe = new Connection.Unsafe((InitialHandler)this){
        final /* synthetic */ InitialHandler this$0;
        {
            this.this$0 = this$0;
        }

        public void sendPacket(DefinedPacket packet) {
            InitialHandler.access$000((InitialHandler)this.this$0).write((Object)packet);
        }
    };
    private boolean onlineMode;
    private InetSocketAddress virtualHost;
    private String name;
    private UUID uniqueId;
    private UUID offlineId;
    private LoginResult loginProfile;
    private boolean legacy;
    private String extraDataInHandshake;
    private static String lastNickname = "";
    private static int similarNicknamesCount = 0;
    private static long lastSimilarNicknameTime = 0L;

    @Override
    public boolean shouldHandle(PacketWrapper packet) {
        if (this.ch == null) {
            return true;
        }
        if (this.ch.isClosing()) return false;
        return true;
    }

    @Override
    public void connected(ChannelWrapper channel) {
        this.ch = channel;
    }

    @Override
    public void exception(Throwable t) {
        this.disconnect((String)((Object)((Object)ChatColor.RED) + Util.exception((Throwable)t)));
    }

    @Override
    public void handle(PacketWrapper packet) {
        if (packet != null) {
            if (packet.packet != null) return;
        }
        this.ch.close();
        if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
        InetAddress address = this.getAddress().getAddress();
        this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " sent null packet!"));
        ADDRESS_BLOCKER.block((InetAddress)address);
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (!PluginMessage.SHOULD_RELAY.apply((PluginMessage)pluginMessage)) return;
        this.relayMessages.add((PluginMessage)pluginMessage);
    }

    @Override
    public void handle(LegacyHandshake legacyHandshake) throws Exception {
        this.legacy = true;
        this.ch.close((Object)this.bungee.getTranslation((String)"outdated_client", (Object[])new Object[]{this.bungee.getGameVersion()}));
    }

    @Override
    public void handle(LegacyPing ping) throws Exception {
        this.legacy = true;
        boolean v1_5 = ping.isV1_5();
        ServerPing legacy = new ServerPing((ServerPing.Protocol)new ServerPing.Protocol((String)((Object)((Object)ChatColor.GOLD) + this.bungee.getName() + " " + "1.0.5 (1.8.x - 1.15.x)"), (int)this.bungee.getProtocolVersion()), (ServerPing.Players)new ServerPing.Players((int)this.listener.getMaxPlayers(), (int)this.bungee.getOnlineCount(), null), (BaseComponent)new TextComponent((BaseComponent[])TextComponent.fromLegacyText((String)this.listener.getMotd())), null);
        Callback<ProxyPingEvent> callback = (result, error) -> {
            if (this.ch.isClosed()) {
                return;
            }
            ServerPing legacy1 = result.getResponse();
            String kickMessage = v1_5 ? (Object)((Object)((Object)ChatColor.DARK_BLUE)) + "\u0000" + 127 + '\u0000' + legacy1.getVersion().getName() + '\u0000' + InitialHandler.getFirstLine((String)legacy1.getDescription()) + '\u0000' + legacy1.getPlayers().getOnline() + '\u0000' + legacy1.getPlayers().getMax() : ChatColor.stripColor((String)InitialHandler.getFirstLine((String)legacy1.getDescription())) + '\u00a7' + legacy1.getPlayers().getOnline() + '\u00a7' + legacy1.getPlayers().getMax();
            this.ch.close((Object)kickMessage);
        };
        this.bungee.getPluginManager().callEvent(new ProxyPingEvent((PendingConnection)this, (ServerPing)legacy, callback));
    }

    private static String getFirstLine(String str) {
        String string;
        int pos = str.indexOf((int)10);
        if (pos == -1) {
            string = str;
            return string;
        }
        string = str.substring((int)0, (int)pos);
        return string;
    }

    @Override
    public void handle(StatusRequest statusRequest) throws Exception {
        if (this.thisState != State.STATUS) {
            InetAddress address = this.getAddress().getAddress();
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " not expecting STATUS state!"));
            this.ch.close();
            return;
        }
        ServerInfo forced = AbstractReconnectHandler.getForcedHost((PendingConnection)this);
        String motd = forced != null ? forced.getMotd() : this.listener.getMotd();
        Callback<ServerPing> pingBack = (result, error) -> {
            if (error != null) {
                result = new ServerPing();
                result.setDescription((String)this.bungee.getTranslation((String)"ping_cannot_connect", (Object[])new Object[0]));
                this.bungee.getLogger().log((Level)Level.WARNING, (String)"Error pinging remote server", (Throwable)error);
            }
            Callback<ProxyPingEvent> callback = (pingResult, error1) -> {
                Gson gson = BungeeCord.getInstance().gson;
                this.unsafe.sendPacket((DefinedPacket)new StatusResponse((String)gson.toJson((Object)pingResult.getResponse())));
                if (this.bungee.getConnectionThrottle() == null) return;
                this.bungee.getConnectionThrottle().unthrottle((InetAddress)this.getAddress().getAddress());
            };
            this.bungee.getPluginManager().callEvent(new ProxyPingEvent((PendingConnection)this, (ServerPing)result, callback));
        };
        if (forced != null && this.listener.isPingPassthrough()) {
            ((BungeeServerInfo)forced).ping(pingBack, (int)this.handshake.getProtocolVersion());
        } else {
            int protocol = ProtocolConstants.SUPPORTED_VERSION_IDS.contains((Object)Integer.valueOf((int)this.handshake.getProtocolVersion())) ? this.handshake.getProtocolVersion() : this.bungee.getProtocolVersion();
            pingBack.done((ServerPing)new ServerPing((ServerPing.Protocol)new ServerPing.Protocol((String)((Object)((Object)ChatColor.GOLD) + this.bungee.getName() + " " + "1.0.5 (1.8.x - 1.15.x)"), (int)protocol), (ServerPing.Players)new ServerPing.Players((int)this.listener.getMaxPlayers(), (int)this.bungee.getOnlineCount(), null), (String)motd, (Favicon)BungeeCord.getInstance().config.getFaviconObject()), null);
        }
        this.thisState = State.PING;
    }

    @Override
    public void handle(PingPacket ping) throws Exception {
        if (this.thisState != State.PING) {
            InetAddress address = this.getAddress().getAddress();
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " not expecting PING state!"));
            this.ch.close();
            if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
            ADDRESS_BLOCKER.block((InetAddress)address);
            return;
        }
        this.unsafe.sendPacket((DefinedPacket)ping);
        this.disconnect((String)"");
    }

    @Override
    public void handle(Handshake handshake) throws Exception {
        if (this.thisState != State.HANDSHAKE) {
            InetAddress address = this.getAddress().getAddress();
            this.ch.close();
            if (this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) {
                ADDRESS_BLOCKER.block((InetAddress)this.getAddress().getAddress());
            }
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " not expecting HANDSHAKE state!"));
            return;
        }
        this.handshake = handshake;
        if (handshake.getRequestedProtocol() != 1 && handshake.getRequestedProtocol() != 2) {
            InetAddress address = this.getAddress().getAddress();
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " invalid protocol!"));
            this.ch.close();
            if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
            ADDRESS_BLOCKER.block((InetAddress)address);
            return;
        }
        this.ch.setVersion((int)handshake.getProtocolVersion());
        if (handshake.getHost().contains((CharSequence)"\u0000")) {
            String[] split = handshake.getHost().split((String)"\u0000", (int)2);
            handshake.setHost((String)split[0]);
            this.extraDataInHandshake = "\u0000" + split[1];
        }
        if (handshake.getHost().endsWith((String)".")) {
            handshake.setHost((String)handshake.getHost().substring((int)0, (int)(handshake.getHost().length() - 1)));
        }
        this.virtualHost = InetSocketAddress.createUnresolved((String)handshake.getHost(), (int)handshake.getPort());
        if (this.bungee.getConfig().isLogPings()) {
            this.bungee.getLogger().log((Level)Level.INFO, (String)"{0} has connected", (Object)this);
        }
        this.bungee.getPluginManager().callEvent(new PlayerHandshakeEvent((PendingConnection)this, (Handshake)handshake));
        switch (handshake.getRequestedProtocol()) {
            case 1: {
                this.thisState = State.STATUS;
                this.ch.setProtocol((Protocol)Protocol.STATUS);
                return;
            }
            case 2: {
                if (!this.bungee.getConfig().isLogPings()) {
                    this.bungee.getLogger().log((Level)Level.INFO, (String)"{0} has connected", (Object)this);
                }
                this.thisState = State.USERNAME;
                this.ch.setProtocol((Protocol)Protocol.LOGIN);
                if (ProtocolConstants.SUPPORTED_VERSION_IDS.contains((Object)Integer.valueOf((int)handshake.getProtocolVersion()))) return;
                if (handshake.getProtocolVersion() > this.bungee.getProtocolVersion()) {
                    this.disconnect((String)this.bungee.getTranslation((String)"outdated_server", (Object[])new Object[]{this.bungee.getGameVersion()}));
                    return;
                }
                this.disconnect((String)this.bungee.getTranslation((String)"outdated_client", (Object[])new Object[]{this.bungee.getGameVersion()}));
                return;
            }
        }
        InetAddress address = this.getAddress().getAddress();
        this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " invalid protocol!"));
        this.ch.close();
        if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
        ADDRESS_BLOCKER.block((InetAddress)address);
    }

    @Override
    public void handle(LoginRequest loginRequest) throws Exception {
        int nickLength;
        if (this.thisState != State.USERNAME) {
            InetAddress address = this.getAddress().getAddress();
            this.ch.close();
            if (this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) {
                ADDRESS_BLOCKER.block((InetAddress)address);
            }
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " not expecting USERNAME state"));
            return;
        }
        this.loginRequest = loginRequest;
        String lowerCaseName = this.getName().toLowerCase();
        if (this.getName().contains((CharSequence)".") || lowerCaseName.startsWith((String)"mcstorm") || lowerCaseName.startsWith((String)"mcspam")) {
            this.disconnect((String)this.bungee.getTranslation((String)"name_invalid", (Object[])new Object[0]));
            return;
        }
        if (this.getName().length() > 16) {
            this.disconnect((String)this.bungee.getTranslation((String)"name_too_long", (Object[])new Object[0]));
            return;
        }
        int limit = BungeeCord.getInstance().config.getPlayerLimit();
        if (limit > 0 && this.bungee.getOnlineCount() > limit) {
            this.disconnect((String)this.bungee.getTranslation((String)"proxy_full", (Object[])new Object[0]));
            return;
        }
        if (!this.onlineMode && (nickLength = lowerCaseName.length()) > 8 && !lastNickname.equals((Object)lowerCaseName)) {
            if (nickLength == lastNickname.length() && lastSimilarNicknameTime > System.currentTimeMillis() && similarNicknamesCount++ > this.bungee.config.getMaxSimilarNicknames()) {
                this.disconnect((BaseComponent)new TextComponent((String)this.bungee.getConfig().getMaxSimilarNicknamesMessage()));
                return;
            }
            similarNicknamesCount = 0;
            lastNickname = lowerCaseName;
            lastSimilarNicknameTime = System.currentTimeMillis() + 1000L * (long)this.bungee.getConfig().getMaxSimilarNicknamesTime();
        }
        if (!this.isOnlineMode() && this.bungee.getPlayer((UUID)this.getUniqueId()) != null) {
            this.disconnect((String)this.bungee.getTranslation((String)"already_connected_proxy", (Object[])new Object[0]));
            return;
        }
        Callback<PreLoginEvent> callback = (result, error) -> {
            if (result.isCancelled()) {
                this.disconnect((BaseComponent[])result.getCancelReasonComponents());
                return;
            }
            if (this.ch.isClosed()) {
                return;
            }
            if (this.onlineMode) {
                this.request = EncryptionUtil.encryptRequest();
                this.unsafe().sendPacket((DefinedPacket)this.request);
            } else {
                this.finish();
            }
            this.thisState = State.ENCRYPT;
        };
        this.bungee.getPluginManager().callEvent(new PreLoginEvent((PendingConnection)this, callback));
    }

    @Override
    public void handle(EncryptionResponse encryptResponse) throws Exception {
        SecretKey sharedKey;
        BungeeCipher encrypt;
        BungeeCipher decrypt;
        if (this.thisState != State.ENCRYPT) {
            InetAddress address = this.getAddress().getAddress();
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " not expecting ENCRYPT state!"));
            this.ch.close();
            if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
            ADDRESS_BLOCKER.block((InetAddress)address);
            return;
        }
        try {
            sharedKey = EncryptionUtil.getSecret((EncryptionResponse)encryptResponse, (EncryptionRequest)this.request);
        }
        catch (Exception ex) {
            InetAddress address = this.getAddress().getAddress();
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " SecretKey exception!"));
            this.ch.close();
            if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
            ADDRESS_BLOCKER.block((InetAddress)address);
            return;
        }
        try {
            decrypt = EncryptionUtil.getCipher((boolean)false, (SecretKey)sharedKey);
        }
        catch (Exception ex) {
            InetAddress address = this.getAddress().getAddress();
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " BungeeCipher decrypt exception!"));
            this.ch.close();
            if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
            ADDRESS_BLOCKER.block((InetAddress)address);
            return;
        }
        this.ch.addBefore((String)"frame-decoder", (String)"decrypt", (ChannelHandler)new CipherDecoder((BungeeCipher)decrypt));
        try {
            encrypt = EncryptionUtil.getCipher((boolean)true, (SecretKey)sharedKey);
        }
        catch (Exception ex) {
            InetAddress address = this.getAddress().getAddress();
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " BungeeCipher encrypt exception!"));
            this.ch.close();
            if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
            ADDRESS_BLOCKER.block((InetAddress)this.getAddress().getAddress());
            return;
        }
        try {
            this.ch.addBefore((String)"frame-prepender", (String)"encrypt", (ChannelHandler)new CipherEncoder((BungeeCipher)encrypt));
            String encName = URLEncoder.encode((String)this.getName(), (String)"UTF-8");
            MessageDigest sha = MessageDigest.getInstance((String)"SHA-1");
            for (byte[] bit : new byte[][]{this.request.getServerId().getBytes((String)"ISO_8859_1"), sharedKey.getEncoded(), EncryptionUtil.keys.getPublic().getEncoded()}) {
                sha.update((byte[])bit);
            }
            String encodedHash = URLEncoder.encode((String)new BigInteger((byte[])sha.digest()).toString((int)16), (String)"UTF-8");
            String preventProxy = BungeeCord.getInstance().config.isPreventProxyConnections() ? "&ip=" + URLEncoder.encode((String)this.getAddress().getAddress().getHostAddress(), (String)"UTF-8") : "";
            String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash + preventProxy;
            Callback<String> handler = (result, error) -> {
                if (error != null) {
                    this.disconnect((String)this.bungee.getTranslation((String)"mojang_fail", (Object[])new Object[0]));
                    this.bungee.getLogger().log((Level)Level.SEVERE, (String)("Error authenticating " + this.getName() + " with minecraft.net"), (Throwable)error);
                    return;
                }
                LoginResult obj = BungeeCord.getInstance().gson.fromJson((String)result, LoginResult.class);
                if (obj != null && obj.getId() != null) {
                    this.loginProfile = obj;
                    this.name = obj.getName();
                    this.uniqueId = Util.getUUID((String)obj.getId());
                    this.finish();
                    return;
                }
                this.disconnect((String)this.bungee.getTranslation((String)"offline_mode_player", (Object[])new Object[0]));
            };
            HttpClient.get((String)authURL, (EventLoop)this.ch.getHandle().eventLoop(), handler);
            return;
        }
        catch (Exception ex) {
            InetAddress address = this.getAddress().getAddress();
            this.bungee.getLogger().info((String)("{CasualProtector} " + address.getHostAddress() + " Encryption exception!"));
            this.ch.close();
            if (!this.bungee.getConfig().isBlockIpsWhenInvalidPacket()) return;
            ADDRESS_BLOCKER.block((InetAddress)address);
        }
    }

    private void finish() {
        ProxiedPlayer oldName;
        if (this.isOnlineMode()) {
            ProxiedPlayer oldID;
            oldName = this.bungee.getPlayer((String)this.getName());
            if (oldName != null) {
                oldName.disconnect((String)this.bungee.getTranslation((String)"already_connected_proxy", (Object[])new Object[0]));
            }
            if ((oldID = this.bungee.getPlayer((UUID)this.getUniqueId())) != null) {
                oldID.disconnect((String)this.bungee.getTranslation((String)"already_connected_proxy", (Object[])new Object[0]));
            }
        } else {
            oldName = this.bungee.getPlayer((String)this.getName());
            if (oldName != null) {
                this.disconnect((String)this.bungee.getTranslation((String)"already_connected_proxy", (Object[])new Object[0]));
                return;
            }
        }
        this.offlineId = UUID.nameUUIDFromBytes((byte[])("OfflinePlayer:" + this.getName()).getBytes((Charset)Charsets.UTF_8));
        if (this.uniqueId == null) {
            this.uniqueId = this.offlineId;
        }
        Callback<LoginEvent> complete = (result, error) -> {
            if (result.isCancelled()) {
                this.disconnect((BaseComponent[])result.getCancelReasonComponents());
                return;
            }
            if (this.ch.isClosed()) {
                return;
            }
            this.ch.getHandle().eventLoop().execute(() -> {
                if (this.ch.isClosing()) return;
                if (this.ch.isClosed()) return;
                UserConnection userCon = new UserConnection((ProxyServer)this.bungee, (ChannelWrapper)this.ch, (String)this.getName(), (InitialHandler)this);
                userCon.setCompressionThreshold((int)BungeeCord.getInstance().config.getCompressionThreshold());
                userCon.init();
                this.unsafe.sendPacket((DefinedPacket)new LoginSuccess((String)this.getUniqueId().toString(), (String)this.getName()));
                this.ch.setProtocol((Protocol)Protocol.GAME);
                this.ch.getHandle().pipeline().get(HandlerBoss.class).setHandler((PacketHandler)new UpstreamBridge((ProxyServer)this.bungee, (UserConnection)userCon));
                this.bungee.getPluginManager().callEvent(new PostLoginEvent((ProxiedPlayer)userCon));
                ServerInfo server = this.bungee.getReconnectHandler() != null ? this.bungee.getReconnectHandler().getServer((ProxiedPlayer)userCon) : AbstractReconnectHandler.getForcedHost((PendingConnection)this);
                if (server == null) {
                    server = this.bungee.getServerInfo((String)this.listener.getDefaultServer());
                }
                userCon.connect((ServerInfo)server, null, (boolean)true, (ServerConnectEvent.Reason)ServerConnectEvent.Reason.JOIN_PROXY);
                this.thisState = State.FINISHED;
            });
        };
        this.bungee.getPluginManager().callEvent(new LoginEvent((PendingConnection)this, complete));
    }

    @Override
    public void handle(LoginPayloadResponse response) throws Exception {
        this.disconnect((String)"Unexpected custom LoginPayloadResponse");
    }

    @Override
    public void disconnect(String reason) {
        this.disconnect((BaseComponent[])TextComponent.fromLegacyText((String)reason));
    }

    @Override
    public void disconnect(BaseComponent ... reason) {
        if (this.thisState != State.STATUS && this.thisState != State.PING && this.thisState != State.HANDSHAKE) {
            this.ch.delayedClose((Kick)new Kick((String)ComponentSerializer.toString((BaseComponent[])reason)));
            return;
        }
        this.ch.close();
    }

    @Override
    public void disconnect(BaseComponent reason) {
        this.disconnect((BaseComponent[])new BaseComponent[]{reason});
    }

    @Override
    public String getName() {
        String string;
        if (this.name != null) {
            string = this.name;
            return string;
        }
        if (this.loginRequest == null) {
            return null;
        }
        string = this.loginRequest.getData();
        return string;
    }

    @Override
    public int getVersion() {
        if (this.handshake == null) {
            return -1;
        }
        int n = this.handshake.getProtocolVersion();
        return n;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.ch.getRemoteAddress();
    }

    @Override
    public Connection.Unsafe unsafe() {
        return this.unsafe;
    }

    @Override
    public void setOnlineMode(boolean onlineMode) {
        Preconditions.checkState((boolean)(this.thisState == State.USERNAME), (Object)"Can only set online mode status whilst state is username");
        this.onlineMode = onlineMode;
    }

    @Override
    public void setUniqueId(UUID uuid) {
        Preconditions.checkState((boolean)(this.thisState == State.USERNAME), (Object)"Can only set uuid while state is username");
        Preconditions.checkState((boolean)(!this.onlineMode), (Object)"Can only set uuid when online mode is false");
        this.uniqueId = uuid;
    }

    @Override
    public String getUUID() {
        return this.uniqueId.toString().replace((CharSequence)"-", (CharSequence)"");
    }

    @Override
    public String toString() {
        String string;
        if (this.getName() != null) {
            string = this.getName();
            return "[" + string + "] <-> InitialHandler";
        }
        string = this.getAddress().getAddress().getHostAddress();
        return "[" + string + "] <-> InitialHandler";
    }

    @Override
    public boolean isConnected() {
        if (this.ch.isClosed()) return false;
        return true;
    }

    public InitialHandler(BungeeCord bungee, ListenerInfo listener) {
        this.onlineMode = BungeeCord.getInstance().config.isOnlineMode();
        this.extraDataInHandshake = "";
        this.bungee = bungee;
        this.listener = listener;
    }

    @Override
    public ListenerInfo getListener() {
        return this.listener;
    }

    public Handshake getHandshake() {
        return this.handshake;
    }

    public LoginRequest getLoginRequest() {
        return this.loginRequest;
    }

    public List<PluginMessage> getRelayMessages() {
        return this.relayMessages;
    }

    @Override
    public boolean isOnlineMode() {
        return this.onlineMode;
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.virtualHost;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public UUID getOfflineId() {
        return this.offlineId;
    }

    public LoginResult getLoginProfile() {
        return this.loginProfile;
    }

    @Override
    public boolean isLegacy() {
        return this.legacy;
    }

    public String getExtraDataInHandshake() {
        return this.extraDataInHandshake;
    }

    static /* synthetic */ ChannelWrapper access$000(InitialHandler x0) {
        return x0.ch;
    }
}

