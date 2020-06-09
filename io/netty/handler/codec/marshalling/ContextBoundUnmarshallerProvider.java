/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.jboss.marshalling.MarshallerFactory
 *  org.jboss.marshalling.MarshallingConfiguration
 *  org.jboss.marshalling.Unmarshaller
 */
package io.netty.handler.codec.marshalling;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class ContextBoundUnmarshallerProvider
extends DefaultUnmarshallerProvider {
    private static final AttributeKey<Unmarshaller> UNMARSHALLER = AttributeKey.valueOf(ContextBoundUnmarshallerProvider.class, (String)"UNMARSHALLER");

    public ContextBoundUnmarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
        super((MarshallerFactory)factory, (MarshallingConfiguration)config);
    }

    @Override
    public Unmarshaller getUnmarshaller(ChannelHandlerContext ctx) throws Exception {
        Attribute<Unmarshaller> attr = ctx.channel().attr(UNMARSHALLER);
        Unmarshaller unmarshaller = attr.get();
        if (unmarshaller != null) return unmarshaller;
        unmarshaller = super.getUnmarshaller((ChannelHandlerContext)ctx);
        attr.set((Unmarshaller)unmarshaller);
        return unmarshaller;
    }
}

