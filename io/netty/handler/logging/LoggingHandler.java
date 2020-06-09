/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.logging;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class LoggingHandler
extends ChannelDuplexHandler {
    private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;
    protected final InternalLogger logger;
    protected final InternalLogLevel internalLevel;
    private final LogLevel level;

    public LoggingHandler() {
        this((LogLevel)DEFAULT_LEVEL);
    }

    public LoggingHandler(LogLevel level) {
        if (level == null) {
            throw new NullPointerException((String)"level");
        }
        this.logger = InternalLoggerFactory.getInstance(this.getClass());
        this.level = level;
        this.internalLevel = level.toInternalLevel();
    }

    public LoggingHandler(Class<?> clazz) {
        this(clazz, (LogLevel)DEFAULT_LEVEL);
    }

    public LoggingHandler(Class<?> clazz, LogLevel level) {
        if (clazz == null) {
            throw new NullPointerException((String)"clazz");
        }
        if (level == null) {
            throw new NullPointerException((String)"level");
        }
        this.logger = InternalLoggerFactory.getInstance(clazz);
        this.level = level;
        this.internalLevel = level.toInternalLevel();
    }

    public LoggingHandler(String name) {
        this((String)name, (LogLevel)DEFAULT_LEVEL);
    }

    public LoggingHandler(String name, LogLevel level) {
        if (name == null) {
            throw new NullPointerException((String)"name");
        }
        if (level == null) {
            throw new NullPointerException((String)"level");
        }
        this.logger = InternalLoggerFactory.getInstance((String)name);
        this.level = level;
        this.internalLevel = level.toInternalLevel();
    }

    public LogLevel level() {
        return this.level;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"REGISTERED"));
        }
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"UNREGISTERED"));
        }
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"ACTIVE"));
        }
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"INACTIVE"));
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"EXCEPTION", (Object)cause), (Throwable)cause);
        }
        ctx.fireExceptionCaught((Throwable)cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"USER_EVENT", (Object)evt));
        }
        ctx.fireUserEventTriggered((Object)evt);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"BIND", (Object)localAddress));
        }
        ctx.bind((SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"CONNECT", (Object)remoteAddress, (Object)localAddress));
        }
        ctx.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"DISCONNECT"));
        }
        ctx.disconnect((ChannelPromise)promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"CLOSE"));
        }
        ctx.close((ChannelPromise)promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"DEREGISTER"));
        }
        ctx.deregister((ChannelPromise)promise);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"READ COMPLETE"));
        }
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"READ", (Object)msg));
        }
        ctx.fireChannelRead((Object)msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"WRITE", (Object)msg));
        }
        ctx.write((Object)msg, (ChannelPromise)promise);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"WRITABILITY CHANGED"));
        }
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled((InternalLogLevel)this.internalLevel)) {
            this.logger.log((InternalLogLevel)this.internalLevel, (String)this.format((ChannelHandlerContext)ctx, (String)"FLUSH"));
        }
        ctx.flush();
    }

    protected String format(ChannelHandlerContext ctx, String eventName) {
        String chStr = ctx.channel().toString();
        return new StringBuilder((int)(chStr.length() + 1 + eventName.length())).append((String)chStr).append((char)' ').append((String)eventName).toString();
    }

    protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {
        if (arg instanceof ByteBuf) {
            return LoggingHandler.formatByteBuf((ChannelHandlerContext)ctx, (String)eventName, (ByteBuf)((ByteBuf)arg));
        }
        if (!(arg instanceof ByteBufHolder)) return LoggingHandler.formatSimple((ChannelHandlerContext)ctx, (String)eventName, (Object)arg);
        return LoggingHandler.formatByteBufHolder((ChannelHandlerContext)ctx, (String)eventName, (ByteBufHolder)((ByteBufHolder)arg));
    }

    protected String format(ChannelHandlerContext ctx, String eventName, Object firstArg, Object secondArg) {
        if (secondArg == null) {
            return LoggingHandler.formatSimple((ChannelHandlerContext)ctx, (String)eventName, (Object)firstArg);
        }
        String chStr = ctx.channel().toString();
        String arg1Str = String.valueOf((Object)firstArg);
        String arg2Str = secondArg.toString();
        StringBuilder buf = new StringBuilder((int)(chStr.length() + 1 + eventName.length() + 2 + arg1Str.length() + 2 + arg2Str.length()));
        buf.append((String)chStr).append((char)' ').append((String)eventName).append((String)": ").append((String)arg1Str).append((String)", ").append((String)arg2Str);
        return buf.toString();
    }

    private static String formatByteBuf(ChannelHandlerContext ctx, String eventName, ByteBuf msg) {
        String chStr = ctx.channel().toString();
        int length = msg.readableBytes();
        if (length == 0) {
            StringBuilder buf = new StringBuilder((int)(chStr.length() + 1 + eventName.length() + 4));
            buf.append((String)chStr).append((char)' ').append((String)eventName).append((String)": 0B");
            return buf.toString();
        }
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder((int)(chStr.length() + 1 + eventName.length() + 2 + 10 + 1 + 2 + rows * 80));
        buf.append((String)chStr).append((char)' ').append((String)eventName).append((String)": ").append((int)length).append((char)'B').append((String)StringUtil.NEWLINE);
        ByteBufUtil.appendPrettyHexDump((StringBuilder)buf, (ByteBuf)msg);
        return buf.toString();
    }

    private static String formatByteBufHolder(ChannelHandlerContext ctx, String eventName, ByteBufHolder msg) {
        String chStr = ctx.channel().toString();
        String msgStr = msg.toString();
        ByteBuf content = msg.content();
        int length = content.readableBytes();
        if (length == 0) {
            StringBuilder buf = new StringBuilder((int)(chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 4));
            buf.append((String)chStr).append((char)' ').append((String)eventName).append((String)", ").append((String)msgStr).append((String)", 0B");
            return buf.toString();
        }
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder((int)(chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 2 + 10 + 1 + 2 + rows * 80));
        buf.append((String)chStr).append((char)' ').append((String)eventName).append((String)": ").append((String)msgStr).append((String)", ").append((int)length).append((char)'B').append((String)StringUtil.NEWLINE);
        ByteBufUtil.appendPrettyHexDump((StringBuilder)buf, (ByteBuf)content);
        return buf.toString();
    }

    private static String formatSimple(ChannelHandlerContext ctx, String eventName, Object msg) {
        String chStr = ctx.channel().toString();
        String msgStr = String.valueOf((Object)msg);
        StringBuilder buf = new StringBuilder((int)(chStr.length() + 1 + eventName.length() + 2 + msgStr.length()));
        return buf.append((String)chStr).append((char)' ').append((String)eventName).append((String)": ").append((String)msgStr).toString();
    }
}

