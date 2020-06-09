/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.OverflowPacketException;

public class LoginPayloadResponse
extends DefinedPacket {
    private int id;
    private byte[] data;

    @Override
    public void read(ByteBuf buf) {
        this.id = LoginPayloadResponse.readVarInt((ByteBuf)buf);
        if (!buf.readBoolean()) return;
        int len = buf.readableBytes();
        if (len > 1048576) {
            throw new OverflowPacketException((String)"Payload may not be larger than 1048576 bytes");
        }
        this.data = new byte[len];
        buf.readBytes((byte[])this.data);
    }

    @Override
    public void write(ByteBuf buf) {
        LoginPayloadResponse.writeVarInt((int)this.id, (ByteBuf)buf);
        if (this.data != null) {
            buf.writeBoolean((boolean)true);
            buf.writeBytes((byte[])this.data);
            return;
        }
        buf.writeBoolean((boolean)false);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((LoginPayloadResponse)this);
    }

    public int getId() {
        return this.id;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginPayloadResponse(id=" + this.getId() + ", data=" + Arrays.toString((byte[])this.getData()) + ")";
    }

    public LoginPayloadResponse() {
    }

    public LoginPayloadResponse(int id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginPayloadResponse)) {
            return false;
        }
        LoginPayloadResponse other = (LoginPayloadResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (Arrays.equals((byte[])this.getData(), (byte[])other.getData())) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginPayloadResponse;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        return result * 59 + Arrays.hashCode((byte[])this.getData());
    }
}

