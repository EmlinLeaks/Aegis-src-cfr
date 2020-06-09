/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class LoginRequest
extends DefinedPacket {
    private String data;

    @Override
    public void read(ByteBuf buf) {
        this.data = LoginRequest.readString((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf) {
        LoginRequest.writeString((String)this.data, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((LoginRequest)this);
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginRequest(data=" + this.getData() + ")";
    }

    public LoginRequest() {
    }

    public LoginRequest(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginRequest)) {
            return false;
        }
        LoginRequest other = (LoginRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$data = this.getData();
        String other$data = other.getData();
        if (this$data == null) {
            if (other$data == null) return true;
            return false;
        }
        if (this$data.equals((Object)other$data)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginRequest;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $data = this.getData();
        return result * 59 + ($data == null ? 43 : $data.hashCode());
    }
}

