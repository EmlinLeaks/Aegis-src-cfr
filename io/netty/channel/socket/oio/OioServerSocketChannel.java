/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket.oio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.socket.oio.DefaultOioServerSocketChannelConfig;
import io.netty.channel.socket.oio.OioServerSocketChannelConfig;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Deprecated
public class OioServerSocketChannel
extends AbstractOioMessageChannel
implements ServerSocketChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioServerSocketChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)false, (int)1);
    final ServerSocket socket;
    final Lock shutdownLock = new ReentrantLock();
    private final OioServerSocketChannelConfig config;

    private static ServerSocket newServerSocket() {
        try {
            return new ServerSocket();
        }
        catch (IOException e) {
            throw new ChannelException((String)"failed to create a server socket", (Throwable)e);
        }
    }

    public OioServerSocketChannel() {
        this((ServerSocket)OioServerSocketChannel.newServerSocket());
    }

    public OioServerSocketChannel(ServerSocket socket) {
        super(null);
        if (socket == null) {
            throw new NullPointerException((String)"socket");
        }
        boolean success = false;
        try {
            socket.setSoTimeout((int)1000);
            success = true;
        }
        catch (IOException e) {
            throw new ChannelException((String)"Failed to set the server socket timeout.", (Throwable)e);
        }
        finally {
            block12 : {
                if (!success) {
                    try {
                        socket.close();
                    }
                    catch (IOException e) {
                        if (!logger.isWarnEnabled()) break block12;
                        logger.warn((String)"Failed to close a partially initialized socket.", (Throwable)e);
                    }
                }
            }
        }
        this.socket = socket;
        this.config = new DefaultOioServerSocketChannelConfig((OioServerSocketChannel)this, (ServerSocket)socket);
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public OioServerSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    public boolean isOpen() {
        if (this.socket.isClosed()) return false;
        return true;
    }

    @Override
    public boolean isActive() {
        if (!this.isOpen()) return false;
        if (!this.socket.isBound()) return false;
        return true;
    }

    @Override
    protected SocketAddress localAddress0() {
        return SocketUtils.localSocketAddress((ServerSocket)this.socket);
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.socket.bind((SocketAddress)localAddress, (int)this.config.getBacklog());
    }

    @Override
    protected void doClose() throws Exception {
        this.socket.close();
    }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        if (this.socket.isClosed()) {
            return -1;
        }
        try {
            Socket s = this.socket.accept();
            try {
                buf.add((Object)new OioSocketChannel((Channel)this, (Socket)s));
                return 1;
            }
            catch (Throwable t) {
                logger.warn((String)"Failed to create a new channel from an accepted socket.", (Throwable)t);
                try {
                    s.close();
                    return 0;
                }
                catch (Throwable t2) {
                    logger.warn((String)"Failed to close a socket.", (Throwable)t2);
                    return 0;
                }
            }
        }
        catch (SocketTimeoutException s) {
            // empty catch block
        }
        return 0;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object filterOutboundMessage(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    protected void setReadPending(boolean readPending) {
        super.setReadPending((boolean)readPending);
    }

    final void clearReadPending0() {
        super.clearReadPending();
    }
}

