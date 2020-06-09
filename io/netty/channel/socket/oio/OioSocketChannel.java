/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.oio.OioByteStreamChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.oio.DefaultOioSocketChannelConfig;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannelConfig;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

@Deprecated
public class OioSocketChannel
extends OioByteStreamChannel
implements SocketChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSocketChannel.class);
    private final Socket socket;
    private final OioSocketChannelConfig config;

    public OioSocketChannel() {
        this((Socket)new Socket());
    }

    public OioSocketChannel(Socket socket) {
        this(null, (Socket)socket);
    }

    public OioSocketChannel(Channel parent, Socket socket) {
        super((Channel)parent);
        this.socket = socket;
        this.config = new DefaultOioSocketChannelConfig((OioSocketChannel)this, (Socket)socket);
        boolean success = false;
        try {
            if (socket.isConnected()) {
                this.activate((InputStream)socket.getInputStream(), (OutputStream)socket.getOutputStream());
            }
            socket.setSoTimeout((int)1000);
            success = true;
            return;
        }
        catch (Exception e) {
            throw new ChannelException((String)"failed to initialize a socket", (Throwable)e);
        }
        finally {
            if (!success) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    logger.warn((String)"Failed to close a socket.", (Throwable)e);
                }
            }
        }
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    @Override
    public OioSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        if (this.socket.isClosed()) return false;
        return true;
    }

    @Override
    public boolean isActive() {
        if (this.socket.isClosed()) return false;
        if (!this.socket.isConnected()) return false;
        return true;
    }

    @Override
    public boolean isOutputShutdown() {
        if (this.socket.isOutputShutdown()) return true;
        if (!this.isActive()) return true;
        return false;
    }

    @Override
    public boolean isInputShutdown() {
        if (this.socket.isInputShutdown()) return true;
        if (!this.isActive()) return true;
        return false;
    }

    @Override
    public boolean isShutdown() {
        if (this.socket.isInputShutdown()) {
            if (this.socket.isOutputShutdown()) return true;
        }
        if (!this.isActive()) return true;
        return false;
    }

    @Override
    protected final void doShutdownOutput() throws Exception {
        this.shutdownOutput0();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture shutdownInput() {
        return this.shutdownInput((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture shutdown() {
        return this.shutdown((ChannelPromise)this.newPromise());
    }

    @Override
    protected int doReadBytes(ByteBuf buf) throws Exception {
        if (this.socket.isClosed()) {
            return -1;
        }
        try {
            return super.doReadBytes((ByteBuf)buf);
        }
        catch (SocketTimeoutException ignored) {
            return 0;
        }
    }

    @Override
    public ChannelFuture shutdownOutput(ChannelPromise promise) {
        EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            this.shutdownOutput0((ChannelPromise)promise);
            return promise;
        }
        loop.execute((Runnable)new Runnable((OioSocketChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ OioSocketChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                OioSocketChannel.access$000((OioSocketChannel)this.this$0, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    private void shutdownOutput0(ChannelPromise promise) {
        try {
            this.shutdownOutput0();
            promise.setSuccess();
            return;
        }
        catch (Throwable t) {
            promise.setFailure((Throwable)t);
        }
    }

    private void shutdownOutput0() throws IOException {
        this.socket.shutdownOutput();
    }

    @Override
    public ChannelFuture shutdownInput(ChannelPromise promise) {
        EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            this.shutdownInput0((ChannelPromise)promise);
            return promise;
        }
        loop.execute((Runnable)new Runnable((OioSocketChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ OioSocketChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                OioSocketChannel.access$100((OioSocketChannel)this.this$0, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    private void shutdownInput0(ChannelPromise promise) {
        try {
            this.socket.shutdownInput();
            promise.setSuccess();
            return;
        }
        catch (Throwable t) {
            promise.setFailure((Throwable)t);
        }
    }

    @Override
    public ChannelFuture shutdown(ChannelPromise promise) {
        ChannelFuture shutdownOutputFuture = this.shutdownOutput();
        if (shutdownOutputFuture.isDone()) {
            this.shutdownOutputDone((ChannelFuture)shutdownOutputFuture, (ChannelPromise)promise);
            return promise;
        }
        shutdownOutputFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((OioSocketChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ OioSocketChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture shutdownOutputFuture) throws Exception {
                OioSocketChannel.access$200((OioSocketChannel)this.this$0, (ChannelFuture)shutdownOutputFuture, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    private void shutdownOutputDone(ChannelFuture shutdownOutputFuture, ChannelPromise promise) {
        ChannelFuture shutdownInputFuture = this.shutdownInput();
        if (shutdownInputFuture.isDone()) {
            OioSocketChannel.shutdownDone((ChannelFuture)shutdownOutputFuture, (ChannelFuture)shutdownInputFuture, (ChannelPromise)promise);
            return;
        }
        shutdownInputFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((OioSocketChannel)this, (ChannelFuture)shutdownOutputFuture, (ChannelPromise)promise){
            final /* synthetic */ ChannelFuture val$shutdownOutputFuture;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ OioSocketChannel this$0;
            {
                this.this$0 = this$0;
                this.val$shutdownOutputFuture = channelFuture;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture shutdownInputFuture) throws Exception {
                OioSocketChannel.access$300((ChannelFuture)this.val$shutdownOutputFuture, (ChannelFuture)shutdownInputFuture, (ChannelPromise)this.val$promise);
            }
        });
    }

    private static void shutdownDone(ChannelFuture shutdownOutputFuture, ChannelFuture shutdownInputFuture, ChannelPromise promise) {
        Throwable shutdownOutputCause = shutdownOutputFuture.cause();
        Throwable shutdownInputCause = shutdownInputFuture.cause();
        if (shutdownOutputCause != null) {
            if (shutdownInputCause != null) {
                logger.debug((String)"Exception suppressed because a previous exception occurred.", (Throwable)shutdownInputCause);
            }
            promise.setFailure((Throwable)shutdownOutputCause);
            return;
        }
        if (shutdownInputCause != null) {
            promise.setFailure((Throwable)shutdownInputCause);
            return;
        }
        promise.setSuccess();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.socket.getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.socket.getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        SocketUtils.bind((Socket)this.socket, (SocketAddress)localAddress);
    }

    @Override
    protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            SocketUtils.bind((Socket)this.socket, (SocketAddress)localAddress);
        }
        boolean success = false;
        try {
            SocketUtils.connect((Socket)this.socket, (SocketAddress)remoteAddress, (int)this.config().getConnectTimeoutMillis());
            this.activate((InputStream)this.socket.getInputStream(), (OutputStream)this.socket.getOutputStream());
            success = true;
            return;
        }
        catch (SocketTimeoutException e) {
            ConnectTimeoutException cause = new ConnectTimeoutException((String)("connection timed out: " + remoteAddress));
            cause.setStackTrace((StackTraceElement[])e.getStackTrace());
            throw cause;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.socket.close();
    }

    protected boolean checkInputShutdown() {
        if (!this.isInputShutdown()) return false;
        try {
            Thread.sleep((long)((long)this.config().getSoTimeout()));
            return true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return true;
    }

    @Deprecated
    @Override
    protected void setReadPending(boolean readPending) {
        super.setReadPending((boolean)readPending);
    }

    final void clearReadPending0() {
        this.clearReadPending();
    }

    static /* synthetic */ void access$000(OioSocketChannel x0, ChannelPromise x1) {
        x0.shutdownOutput0((ChannelPromise)x1);
    }

    static /* synthetic */ void access$100(OioSocketChannel x0, ChannelPromise x1) {
        x0.shutdownInput0((ChannelPromise)x1);
    }

    static /* synthetic */ void access$200(OioSocketChannel x0, ChannelFuture x1, ChannelPromise x2) {
        x0.shutdownOutputDone((ChannelFuture)x1, (ChannelPromise)x2);
    }

    static /* synthetic */ void access$300(ChannelFuture x0, ChannelFuture x1, ChannelPromise x2) {
        OioSocketChannel.shutdownDone((ChannelFuture)x0, (ChannelFuture)x1, (ChannelPromise)x2);
    }
}

