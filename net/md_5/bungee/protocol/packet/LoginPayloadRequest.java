/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.OverflowPacketException;

public class LoginPayloadRequest
extends DefinedPacket {
    private int id;
    private String channel;
    private byte[] data;

    @Override
    public void read(ByteBuf buf) {
        this.id = LoginPayloadRequest.readVarInt((ByteBuf)buf);
        this.channel = LoginPayloadRequest.readString((ByteBuf)buf);
        int len = buf.readableBytes();
        if (len > 1048576) {
            throw new OverflowPacketException((String)"Payload may not be larger than 1048576 bytes");
        }
        this.data = new byte[len];
        buf.readBytes((byte[])this.data);
    }

    @Override
    public void write(ByteBuf buf) {
        LoginPayloadRequest.writeVarInt((int)this.id, (ByteBuf)buf);
        LoginPayloadRequest.writeString((String)this.channel, (ByteBuf)buf);
        buf.writeBytes((byte[])this.data);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((LoginPayloadRequest)this);
    }

    public int getId() {
        return this.id;
    }

    public String getChannel() {
        return this.channel;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginPayloadRequest(id=" + this.getId() + ", channel=" + this.getChannel() + ", data=" + Arrays.toString((byte[])this.getData()) + ")";
    }

    public LoginPayloadRequest() {
    }

    public LoginPayloadRequest(int id, String channel, byte[] data) {
        this.id = id;
        this.channel = channel;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginPayloadRequest)) {
            return false;
        }
        LoginPayloadRequest other = (LoginPayloadRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        String this$channel = this.getChannel();
        String other$channel = other.getChannel();
        if (this$channel == null ? other$channel != null : !this$channel.equals((Object)other$channel)) {
            return false;
        }
        if (Arrays.equals((byte[])this.getData(), (byte[])other.getData())) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginPayloadRequest;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        String $channel = this.getChannel();
        result = result * 59 + ($channel == null ? 43 : $channel.hashCode());
        return result * 59 + Arrays.hashCode((byte[])this.getData());
    }
}

