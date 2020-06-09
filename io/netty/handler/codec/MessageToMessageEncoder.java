/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I>
extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;

    protected MessageToMessageEncoder() {
        this.matcher = TypeParameterMatcher.find((Object)this, MessageToMessageEncoder.class, (String)"I");
    }

    protected MessageToMessageEncoder(Class<? extends I> outboundMessageType) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.matcher.match((Object)msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        CodecOutputList out = null;
        try {
            if (this.acceptOutboundMessage((Object)msg)) {
                out = CodecOutputList.newInstance();
                Object cast = msg;
                try {
                    this.encode((ChannelHandlerContext)ctx, cast, (List<Object>)out);
                }
                finally {
                    ReferenceCountUtil.release((Object)cast);
                }
                if (!out.isEmpty()) return;
                out.recycle();
                out = null;
                throw new EncoderException((String)(StringUtil.simpleClassName((Object)this) + " must produce at least one message."));
            }
            ctx.write((Object)msg, (ChannelPromise)promise);
            return;
        }
        catch (EncoderException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new EncoderException((Throwable)t);
        }
        finally {
            if (out != null) {
                int sizeMinusOne = out.size() - 1;
                if (sizeMinusOne == 0) {
                    ctx.write((Object)out.getUnsafe((int)0), (ChannelPromise)promise);
                } else if (sizeMinusOne > 0) {
                    if (promise == ctx.voidPromise()) {
                        MessageToMessageEncoder.writeVoidPromise((ChannelHandlerContext)ctx, (CodecOutputList)out);
                    } else {
                        MessageToMessageEncoder.writePromiseCombiner((ChannelHandlerContext)ctx, (CodecOutputList)out, (ChannelPromise)promise);
                    }
                }
                out.recycle();
            }
        }
    }

    private static void writeVoidPromise(ChannelHandlerContext ctx, CodecOutputList out) {
        ChannelPromise voidPromise = ctx.voidPromise();
        int i = 0;
        while (i < out.size()) {
            ctx.write((Object)out.getUnsafe((int)i), (ChannelPromise)voidPromise);
            ++i;
        }
    }

    private static void writePromiseCombiner(ChannelHandlerContext ctx, CodecOutputList out, ChannelPromise promise) {
        PromiseCombiner combiner = new PromiseCombiner((EventExecutor)ctx.executor());
        int i = 0;
        do {
            if (i >= out.size()) {
                combiner.finish((Promise<Void>)promise);
                return;
            }
            combiner.add((Future)ctx.write((Object)out.getUnsafe((int)i)));
            ++i;
        } while (true);
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}

