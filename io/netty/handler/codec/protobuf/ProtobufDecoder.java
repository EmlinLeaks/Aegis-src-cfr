/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.ExtensionRegistry
 *  com.google.protobuf.ExtensionRegistryLite
 *  com.google.protobuf.MessageLite
 *  com.google.protobuf.MessageLite$Builder
 *  com.google.protobuf.Parser
 */
package io.netty.handler.codec.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.Method;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufDecoder
extends MessageToMessageDecoder<ByteBuf> {
    private static final boolean HAS_PARSER;
    private final MessageLite prototype;
    private final ExtensionRegistryLite extensionRegistry;

    public ProtobufDecoder(MessageLite prototype) {
        this((MessageLite)prototype, null);
    }

    public ProtobufDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
        this((MessageLite)prototype, (ExtensionRegistryLite)extensionRegistry);
    }

    public ProtobufDecoder(MessageLite prototype, ExtensionRegistryLite extensionRegistry) {
        if (prototype == null) {
            throw new NullPointerException((String)"prototype");
        }
        this.prototype = prototype.getDefaultInstanceForType();
        this.extensionRegistry = extensionRegistry;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] array;
        int offset;
        int length = msg.readableBytes();
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = ByteBufUtil.getBytes((ByteBuf)msg, (int)msg.readerIndex(), (int)length, (boolean)false);
            offset = 0;
        }
        if (this.extensionRegistry == null) {
            if (HAS_PARSER) {
                out.add((Object)this.prototype.getParserForType().parseFrom((byte[])array, (int)offset, (int)length));
                return;
            }
            out.add((Object)this.prototype.newBuilderForType().mergeFrom((byte[])array, (int)offset, (int)length).build());
            return;
        }
        if (HAS_PARSER) {
            out.add((Object)this.prototype.getParserForType().parseFrom((byte[])array, (int)offset, (int)length, (ExtensionRegistryLite)this.extensionRegistry));
            return;
        }
        out.add((Object)this.prototype.newBuilderForType().mergeFrom((byte[])array, (int)offset, (int)length, (ExtensionRegistryLite)this.extensionRegistry).build());
    }

    static {
        boolean hasParser = false;
        try {
            MessageLite.class.getDeclaredMethod((String)"getParserForType", new Class[0]);
            hasParser = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        HAS_PARSER = hasParser;
    }
}

