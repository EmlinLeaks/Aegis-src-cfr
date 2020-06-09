/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.util.ReferenceCounted;
import java.net.InetSocketAddress;

public final class DatagramPacket
extends DefaultAddressedEnvelope<ByteBuf, InetSocketAddress>
implements ByteBufHolder {
    public DatagramPacket(ByteBuf data, InetSocketAddress recipient) {
        super(data, recipient);
    }

    public DatagramPacket(ByteBuf data, InetSocketAddress recipient, InetSocketAddress sender) {
        super(data, recipient, sender);
    }

    @Override
    public DatagramPacket copy() {
        return this.replace((ByteBuf)((ByteBuf)this.content()).copy());
    }

    @Override
    public DatagramPacket duplicate() {
        return this.replace((ByteBuf)((ByteBuf)this.content()).duplicate());
    }

    @Override
    public DatagramPacket retainedDuplicate() {
        return this.replace((ByteBuf)((ByteBuf)this.content()).retainedDuplicate());
    }

    @Override
    public DatagramPacket replace(ByteBuf content) {
        return new DatagramPacket((ByteBuf)content, (InetSocketAddress)((InetSocketAddress)this.recipient()), (InetSocketAddress)((InetSocketAddress)this.sender()));
    }

    @Override
    public DatagramPacket retain() {
        super.retain();
        return this;
    }

    @Override
    public DatagramPacket retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public DatagramPacket touch() {
        super.touch();
        return this;
    }

    @Override
    public DatagramPacket touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }
}

