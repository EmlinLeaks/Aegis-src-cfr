/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.md_5.bungee;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeSecurityManager;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.ConnectionThrottle;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.Metrics;
import net.md_5.bungee.PlayerInfoSerializer;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.ScoreComponentSerializer;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;
import net.md_5.bungee.command.CommandBungee;
import net.md_5.bungee.command.CommandEnd;
import net.md_5.bungee.command.CommandIP;
import net.md_5.bungee.command.CommandPerms;
import net.md_5.bungee.command.CommandReload;
import net.md_5.bungee.command.ConsoleCommandCompleter;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.log.BungeeLogger;
import net.md_5.bungee.log.LoggingOutputStream;
import net.md_5.bungee.module.ModuleManager;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.query.RemoteQuery;
import net.md_5.bungee.scheduler.BungeeScheduler;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.fusesource.jansi.AnsiConsole;

public class BungeeCord
extends ProxyServer {
    public volatile boolean isRunning;
    public final Configuration config = new Configuration();
    private ResourceBundle baseBundle;
    private ResourceBundle customBundle;
    public EventLoopGroup eventLoops;
    private final Timer saveThread = new Timer((String)"Reconnect Saver");
    private final Timer metricsThread = new Timer((String)"Metrics Thread");
    private final Collection<Channel> listeners = new HashSet<Channel>();
    private final Map<String, UserConnection> connections = new CaseInsensitiveMap<UserConnection>();
    private final Map<UUID, UserConnection> connectionsByOfflineUUID = new HashMap<UUID, UserConnection>();
    private final Map<UUID, UserConnection> connectionsByUUID = new HashMap<UUID, UserConnection>();
    private final ReadWriteLock connectionLock = new ReentrantReadWriteLock();
    public final PluginManager pluginManager;
    private ReconnectHandler reconnectHandler;
    private ConfigurationAdapter configurationAdapter = new YamlConfig();
    private final Collection<String> pluginChannels = new HashSet<String>();
    private final File pluginsFolder = new File((String)"plugins");
    private final BungeeScheduler scheduler = new BungeeScheduler();
    private final ConsoleReader consoleReader;
    private final Logger logger;
    public final Gson gson = new GsonBuilder().registerTypeAdapter(BaseComponent.class, (Object)new ComponentSerializer()).registerTypeAdapter(TextComponent.class, (Object)new TextComponentSerializer()).registerTypeAdapter(TranslatableComponent.class, (Object)new TranslatableComponentSerializer()).registerTypeAdapter(KeybindComponent.class, (Object)new KeybindComponentSerializer()).registerTypeAdapter(ScoreComponent.class, (Object)new ScoreComponentSerializer()).registerTypeAdapter(SelectorComponent.class, (Object)new SelectorComponentSerializer()).registerTypeAdapter(ServerPing.PlayerInfo.class, (Object)new PlayerInfoSerializer()).registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter()).create();
    private ConnectionThrottle connectionThrottle;
    private final ModuleManager moduleManager = new ModuleManager();

    public static BungeeCord getInstance() {
        return (BungeeCord)ProxyServer.getInstance();
    }

    @SuppressFBWarnings(value={"DM_DEFAULT_ENCODING"})
    public BungeeCord() throws IOException {
        this.registerChannel((String)"BungeeCord");
        Preconditions.checkState((boolean)(new File((String)".").getAbsolutePath().indexOf((int)33) == -1), (Object)"Cannot use BungeeCord in directory with ! in path.");
        System.setSecurityManager((SecurityManager)new BungeeSecurityManager());
        try {
            this.baseBundle = ResourceBundle.getBundle((String)"messages");
        }
        catch (MissingResourceException ex) {
            this.baseBundle = ResourceBundle.getBundle((String)"messages", (Locale)Locale.ENGLISH);
        }
        this.reloadMessages();
        System.setProperty((String)"library.jansi.version", (String)"BungeeCord");
        AnsiConsole.systemInstall();
        this.consoleReader = new ConsoleReader();
        this.consoleReader.setExpandEvents((boolean)false);
        this.consoleReader.addCompleter((Completer)new ConsoleCommandCompleter((ProxyServer)this));
        this.logger = new BungeeLogger((String)"BungeeCord", (String)"proxy.log", (ConsoleReader)this.consoleReader);
        System.setErr((PrintStream)new PrintStream((OutputStream)new LoggingOutputStream((Logger)this.logger, (Level)Level.SEVERE), (boolean)true));
        System.setOut((PrintStream)new PrintStream((OutputStream)new LoggingOutputStream((Logger)this.logger, (Level)Level.INFO), (boolean)true));
        this.pluginManager = new PluginManager((ProxyServer)this);
        this.getPluginManager().registerCommand(null, (Command)new CommandReload());
        this.getPluginManager().registerCommand(null, (Command)new CommandEnd());
        this.getPluginManager().registerCommand(null, (Command)new CommandIP());
        this.getPluginManager().registerCommand(null, (Command)new CommandBungee());
        this.getPluginManager().registerCommand(null, (Command)new CommandPerms());
        if (Boolean.getBoolean((String)"net.md_5.bungee.native.disable")) return;
        if (EncryptionUtil.nativeFactory.load()) {
            this.logger.info((String)"Using mbed TLS based native cipher.");
        } else {
            this.logger.info((String)"Using standard Java JCE cipher.");
        }
        if (CompressFactory.zlib.load()) {
            this.logger.info((String)"Using zlib based native compressor.");
            return;
        }
        this.logger.info((String)"Using standard Java compressor.");
    }

    @SuppressFBWarnings(value={"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
    public void start() throws Exception {
        System.setProperty((String)"io.netty.selectorAutoRebuildThreshold", (String)"0");
        if (System.getProperty((String)"io.netty.leakDetectionLevel") == null) {
            ResourceLeakDetector.setLevel((ResourceLeakDetector.Level)ResourceLeakDetector.Level.DISABLED);
        }
        this.eventLoops = PipelineUtils.newEventLoopGroup((int)0, (ThreadFactory)new ThreadFactoryBuilder().setNameFormat((String)"Netty IO Thread #%1$d").build());
        File moduleDirectory = new File((String)"modules");
        this.moduleManager.load((ProxyServer)this, (File)moduleDirectory);
        this.pluginManager.detectPlugins((File)moduleDirectory);
        this.pluginsFolder.mkdir();
        this.pluginManager.detectPlugins((File)this.pluginsFolder);
        this.pluginManager.loadPlugins();
        this.config.load();
        if (this.config.isForgeSupport()) {
            this.registerChannel((String)"FML");
            this.registerChannel((String)"FML|HS");
            this.registerChannel((String)"FORGE");
            this.getLogger().warning((String)"MinecraftForge support is currently unmaintained and may have unresolved issues. Please use at your own risk.");
        }
        this.isRunning = true;
        this.pluginManager.enablePlugins();
        if (this.config.getThrottle() > 0) {
            this.connectionThrottle = new ConnectionThrottle((int)this.config.getThrottle(), (int)this.config.getThrottleLimit());
        }
        this.startListeners();
        this.saveThread.scheduleAtFixedRate((TimerTask)new TimerTask((BungeeCord)this){
            final /* synthetic */ BungeeCord this$0;
            {
                this.this$0 = this$0;
            }

            public void run() {
                if (this.this$0.getReconnectHandler() == null) return;
                this.this$0.getReconnectHandler().save();
            }
        }, (long)0L, (long)TimeUnit.MINUTES.toMillis((long)5L));
        this.metricsThread.scheduleAtFixedRate((TimerTask)new Metrics(), (long)0L, (long)TimeUnit.MINUTES.toMillis((long)999999L));
    }

    public void startListeners() {
        Iterator<ListenerInfo> iterator = this.config.getListeners().iterator();
        while (iterator.hasNext()) {
            ListenerInfo info = iterator.next();
            if (info.isProxyProtocol()) {
                this.getLogger().log((Level)Level.WARNING, (String)"Using PROXY protocol for listener {0}, please ensure this listener is adequately firewalled.", (Object)info.getHost());
                if (this.connectionThrottle != null) {
                    this.connectionThrottle = null;
                    this.getLogger().log((Level)Level.WARNING, (String)"Since PROXY protocol is in use, internal connection throttle has been disabled.");
                }
            }
            ChannelFutureListener listener = new ChannelFutureListener((BungeeCord)this, (ListenerInfo)info){
                final /* synthetic */ ListenerInfo val$info;
                final /* synthetic */ BungeeCord this$0;
                {
                    this.this$0 = this$0;
                    this.val$info = listenerInfo;
                }

                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        BungeeCord.access$000((BungeeCord)this.this$0).add(future.channel());
                        this.this$0.getLogger().log((Level)Level.INFO, (String)"Listening on {0}", (Object)this.val$info.getHost());
                        return;
                    }
                    this.this$0.getLogger().log((Level)Level.WARNING, (String)("Could not bind to host " + this.val$info.getHost()), (Throwable)future.cause());
                }
            };
            ((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(PipelineUtils.getServerChannel())).option(ChannelOption.SO_REUSEADDR, Boolean.valueOf((boolean)true))).childAttr(PipelineUtils.LISTENER, info).childHandler(PipelineUtils.SERVER_CHILD).group((EventLoopGroup)this.eventLoops).localAddress((SocketAddress)info.getHost())).bind().addListener((GenericFutureListener<? extends Future<? super Void>>)listener);
            if (!info.isQueryEnabled()) continue;
            ChannelFutureListener bindListener = new ChannelFutureListener((BungeeCord)this, (ListenerInfo)info){
                final /* synthetic */ ListenerInfo val$info;
                final /* synthetic */ BungeeCord this$0;
                {
                    this.this$0 = this$0;
                    this.val$info = listenerInfo;
                }

                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        BungeeCord.access$000((BungeeCord)this.this$0).add(future.channel());
                        this.this$0.getLogger().log((Level)Level.INFO, (String)"Started query on {0}", (Object)future.channel().localAddress());
                        return;
                    }
                    this.this$0.getLogger().log((Level)Level.WARNING, (String)("Could not bind to host " + this.val$info.getHost()), (Throwable)future.cause());
                }
            };
            new RemoteQuery((ProxyServer)this, (ListenerInfo)info).start(PipelineUtils.getDatagramChannel(), (InetSocketAddress)new InetSocketAddress((InetAddress)info.getHost().getAddress(), (int)info.getQueryPort()), (EventLoopGroup)this.eventLoops, (ChannelFutureListener)bindListener);
        }
    }

    public void stopListeners() {
        Iterator<Channel> iterator = this.listeners.iterator();
        do {
            if (!iterator.hasNext()) {
                this.listeners.clear();
                return;
            }
            Channel listener = iterator.next();
            this.getLogger().log((Level)Level.INFO, (String)"Closing listener {0}", (Object)listener);
            try {
                listener.close().syncUninterruptibly();
            }
            catch (ChannelException ex) {
                this.getLogger().severe((String)"Could not close listen thread");
                continue;
            }
            break;
        } while (true);
    }

    @Override
    public void stop() {
        this.stop((String)this.getTranslation((String)"restart", (Object[])new Object[0]));
    }

    @Override
    public synchronized void stop(String reason) {
        if (!this.isRunning) {
            return;
        }
        this.isRunning = false;
        new Thread((BungeeCord)this, (String)"Shutdown Thread", (String)reason){
            final /* synthetic */ String val$reason;
            final /* synthetic */ BungeeCord this$0;
            {
                this.this$0 = this$0;
                this.val$reason = string;
                super((String)x0);
            }

            @SuppressFBWarnings(value={"DM_EXIT"})
            public void run() {
                this.this$0.stopListeners();
                this.this$0.getLogger().info((String)"Closing pending connections");
                BungeeCord.access$100((BungeeCord)this.this$0).readLock().lock();
                try {
                    this.this$0.getLogger().log((Level)Level.INFO, (String)"Disconnecting {0} connections", (Object)Integer.valueOf((int)BungeeCord.access$200((BungeeCord)this.this$0).size()));
                    for (UserConnection user : BungeeCord.access$200((BungeeCord)this.this$0).values()) {
                        user.disconnect((String)this.val$reason);
                    }
                }
                finally {
                    BungeeCord.access$100((BungeeCord)this.this$0).readLock().unlock();
                }
                try {
                    Thread.sleep((long)500L);
                }
                catch (java.lang.InterruptedException interruptedException) {
                    // empty catch block
                }
                if (BungeeCord.access$300((BungeeCord)this.this$0) != null) {
                    this.this$0.getLogger().info((String)"Saving reconnect locations");
                    BungeeCord.access$300((BungeeCord)this.this$0).save();
                    BungeeCord.access$300((BungeeCord)this.this$0).close();
                }
                BungeeCord.access$400((BungeeCord)this.this$0).cancel();
                BungeeCord.access$500((BungeeCord)this.this$0).cancel();
                this.this$0.getLogger().info((String)"Disabling plugins");
                for (Plugin plugin : com.google.common.collect.Lists.reverse(new java.util.ArrayList<Plugin>(this.this$0.pluginManager.getPlugins()))) {
                    try {
                        plugin.onDisable();
                        for (java.util.logging.Handler handler : plugin.getLogger().getHandlers()) {
                            handler.close();
                        }
                    }
                    catch (Throwable t) {
                        this.this$0.getLogger().log((Level)Level.SEVERE, (String)("Exception disabling plugin " + plugin.getDescription().getName()), (Throwable)t);
                    }
                    this.this$0.getScheduler().cancel((Plugin)plugin);
                    plugin.getExecutorService().shutdownNow();
                }
                this.this$0.getLogger().info((String)"Closing IO threads");
                this.this$0.eventLoops.shutdownGracefully();
                try {
                    this.this$0.eventLoops.awaitTermination((long)Long.MAX_VALUE, (TimeUnit)TimeUnit.NANOSECONDS);
                }
                catch (java.lang.InterruptedException interruptedException) {
                    // empty catch block
                }
                this.this$0.getLogger().info((String)"Thank you and goodbye");
                Iterator<Object> iterator = this.this$0.getLogger().getHandlers();
                int plugin = ((Iterator<Object>)iterator).length;
                int t = 0;
                do {
                    if (t >= plugin) {
                        System.exit((int)0);
                        return;
                    }
                    Iterator<Object> handler = iterator[t];
                    ((java.util.logging.Handler)((Object)handler)).close();
                    ++t;
                } while (true);
            }
        }.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcast(DefinedPacket packet) {
        this.connectionLock.readLock().lock();
        try {
            Iterator<UserConnection> iterator = this.connections.values().iterator();
            while (iterator.hasNext()) {
                UserConnection con = iterator.next();
                con.unsafe().sendPacket((DefinedPacket)packet);
            }
            return;
        }
        finally {
            this.connectionLock.readLock().unlock();
        }
    }

    @Override
    public String getName() {
        return "Aegis (CasualProtector)";
    }

    @Override
    public String getVersion() {
        if (BungeeCord.class.getPackage().getImplementationVersion() == null) {
            return "unknown";
        }
        String string = BungeeCord.class.getPackage().getImplementationVersion();
        return string;
    }

    public void reloadMessages() {
        File file = new File((String)"messages.properties");
        if (!file.isFile()) return;
        try {
            FileReader rd = new FileReader((File)file);
            Throwable throwable = null;
            try {
                this.customBundle = new PropertyResourceBundle((Reader)rd);
                return;
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (throwable != null) {
                    try {
                        rd.close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed((Throwable)throwable3);
                    }
                } else {
                    rd.close();
                }
            }
        }
        catch (IOException ex) {
            this.getLogger().log((Level)Level.SEVERE, (String)"Could not load custom messages.properties", (Throwable)ex);
        }
    }

    @Override
    public String getTranslation(String name, Object ... args) {
        String translation = "<translation '" + name + "' missing>";
        try {
            return MessageFormat.format((String)(this.customBundle != null && this.customBundle.containsKey((String)name) ? this.customBundle.getString((String)name) : this.baseBundle.getString((String)name)), (Object[])args);
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        return translation;
    }

    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        this.connectionLock.readLock().lock();
        try {
            Collection<ProxiedPlayer> collection = Collections.unmodifiableCollection(new HashSet<UserConnection>(this.connections.values()));
            return collection;
        }
        finally {
            this.connectionLock.readLock().unlock();
        }
    }

    @Override
    public int getOnlineCount() {
        return this.connections.size();
    }

    @Override
    public ProxiedPlayer getPlayer(String name) {
        this.connectionLock.readLock().lock();
        try {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)this.connections.get((Object)name);
            return proxiedPlayer;
        }
        finally {
            this.connectionLock.readLock().unlock();
        }
    }

    public UserConnection getPlayerByOfflineUUID(UUID name) {
        this.connectionLock.readLock().lock();
        try {
            UserConnection userConnection = this.connectionsByOfflineUUID.get((Object)name);
            return userConnection;
        }
        finally {
            this.connectionLock.readLock().unlock();
        }
    }

    @Override
    public ProxiedPlayer getPlayer(UUID uuid) {
        this.connectionLock.readLock().lock();
        try {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)this.connectionsByUUID.get((Object)uuid);
            return proxiedPlayer;
        }
        finally {
            this.connectionLock.readLock().unlock();
        }
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        return this.config.getServers();
    }

    @Override
    public ServerInfo getServerInfo(String name) {
        return this.getServers().get((Object)name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerChannel(String channel) {
        Collection<String> collection = this.pluginChannels;
        // MONITORENTER : collection
        this.pluginChannels.add((String)channel);
        // MONITOREXIT : collection
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterChannel(String channel) {
        Collection<String> collection = this.pluginChannels;
        // MONITORENTER : collection
        this.pluginChannels.remove((Object)channel);
        // MONITOREXIT : collection
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Collection<String> getChannels() {
        Collection<String> collection = this.pluginChannels;
        // MONITORENTER : collection
        // MONITOREXIT : collection
        return Collections.unmodifiableCollection(this.pluginChannels);
    }

    public PluginMessage registerChannels(int protocolVersion) {
        if (protocolVersion < 393) return new PluginMessage((String)"REGISTER", (byte[])Util.format(this.pluginChannels, (String)"\u0000").getBytes((Charset)Charsets.UTF_8), (boolean)false);
        return new PluginMessage((String)"minecraft:register", (byte[])Util.format(Iterables.transform(this.pluginChannels, PluginMessage.MODERNISE), (String)"\u0000").getBytes((Charset)Charsets.UTF_8), (boolean)false);
    }

    @Override
    public int getProtocolVersion() {
        return ProtocolConstants.SUPPORTED_VERSION_IDS.get((int)(ProtocolConstants.SUPPORTED_VERSION_IDS.size() - 1)).intValue();
    }

    @Override
    public String getGameVersion() {
        return ProtocolConstants.SUPPORTED_VERSIONS.get((int)0) + "-" + ProtocolConstants.SUPPORTED_VERSIONS.get((int)(ProtocolConstants.SUPPORTED_VERSIONS.size() - 1));
    }

    @Override
    public ServerInfo constructServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
        return new BungeeServerInfo((String)name, (InetSocketAddress)address, (String)motd, (boolean)restricted);
    }

    @Override
    public CommandSender getConsole() {
        return ConsoleCommandSender.getInstance();
    }

    @Override
    public void broadcast(String message) {
        this.broadcast((BaseComponent[])TextComponent.fromLegacyText((String)message));
    }

    @Override
    public void broadcast(BaseComponent ... message) {
        this.getConsole().sendMessage((String)BaseComponent.toLegacyText((BaseComponent[])message));
        this.broadcast((DefinedPacket)new Chat((String)ComponentSerializer.toString((BaseComponent[])message)));
    }

    @Override
    public void broadcast(BaseComponent message) {
        this.getConsole().sendMessage((String)message.toLegacyText());
        this.broadcast((DefinedPacket)new Chat((String)ComponentSerializer.toString((BaseComponent)message)));
    }

    public void addConnection(UserConnection con) {
        this.connectionLock.writeLock().lock();
        try {
            this.connections.put((String)con.getName(), (UserConnection)con);
            this.connectionsByUUID.put((UUID)con.getUniqueId(), (UserConnection)con);
            this.connectionsByOfflineUUID.put((UUID)con.getPendingConnection().getOfflineId(), (UserConnection)con);
            return;
        }
        finally {
            this.connectionLock.writeLock().unlock();
        }
    }

    public void removeConnection(UserConnection con) {
        this.connectionLock.writeLock().lock();
        try {
            if (this.connections.get((Object)con.getName()) != con) return;
            this.connections.remove((Object)con.getName());
            this.connectionsByUUID.remove((Object)con.getUniqueId());
            this.connectionsByOfflineUUID.remove((Object)con.getPendingConnection().getOfflineId());
            return;
        }
        finally {
            this.connectionLock.writeLock().unlock();
        }
    }

    @Override
    public Collection<String> getDisabledCommands() {
        return this.config.getDisabledCommands();
    }

    @Override
    public Collection<ProxiedPlayer> matchPlayer(String partialName) {
        Preconditions.checkNotNull(partialName, (Object)"partialName");
        ProxiedPlayer exactMatch = this.getPlayer((String)partialName);
        if (exactMatch == null) return Sets.newHashSet(Iterables.filter(this.getPlayers(), new Predicate<ProxiedPlayer>((BungeeCord)this, (String)partialName){
            final /* synthetic */ String val$partialName;
            final /* synthetic */ BungeeCord this$0;
            {
                this.this$0 = this$0;
                this.val$partialName = string;
            }

            public boolean apply(ProxiedPlayer input) {
                if (input == null) {
                    return false;
                }
                boolean bl = input.getName().toLowerCase((Locale)Locale.ROOT).startsWith((String)this.val$partialName.toLowerCase((Locale)Locale.ROOT));
                return bl;
            }
        }));
        return Collections.singleton(exactMatch);
    }

    @Override
    public Title createTitle() {
        return new BungeeTitle();
    }

    @Override
    public Configuration getConfig() {
        return this.config;
    }

    @Override
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override
    public ReconnectHandler getReconnectHandler() {
        return this.reconnectHandler;
    }

    @Override
    public void setReconnectHandler(ReconnectHandler reconnectHandler) {
        this.reconnectHandler = reconnectHandler;
    }

    @Override
    public ConfigurationAdapter getConfigurationAdapter() {
        return this.configurationAdapter;
    }

    @Override
    public void setConfigurationAdapter(ConfigurationAdapter configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override
    public File getPluginsFolder() {
        return this.pluginsFolder;
    }

    @Override
    public BungeeScheduler getScheduler() {
        return this.scheduler;
    }

    public ConsoleReader getConsoleReader() {
        return this.consoleReader;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    public ConnectionThrottle getConnectionThrottle() {
        return this.connectionThrottle;
    }

    static /* synthetic */ Collection access$000(BungeeCord x0) {
        return x0.listeners;
    }

    static /* synthetic */ ReadWriteLock access$100(BungeeCord x0) {
        return x0.connectionLock;
    }

    static /* synthetic */ Map access$200(BungeeCord x0) {
        return x0.connections;
    }

    static /* synthetic */ ReconnectHandler access$300(BungeeCord x0) {
        return x0.reconnectHandler;
    }

    static /* synthetic */ Timer access$400(BungeeCord x0) {
        return x0.saveThread;
    }

    static /* synthetic */ Timer access$500(BungeeCord x0) {
        return x0.metricsThread;
    }
}

