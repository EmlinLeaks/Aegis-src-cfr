/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class LoginSuccess
extends DefinedPacket {
    private String uuid;
    private String username;

    @Override
    public void read(ByteBuf buf) {
        this.uuid = LoginSuccess.readString((ByteBuf)buf);
        this.username = LoginSuccess.readString((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf) {
        LoginSuccess.writeString((String)this.uuid, (ByteBuf)buf);
        LoginSuccess.writeString((String)this.username, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((LoginSuccess)this);
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "LoginSuccess(uuid=" + this.getUuid() + ", username=" + this.getUsername() + ")";
    }

    public LoginSuccess() {
    }

    public LoginSuccess(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginSuccess)) {
            return false;
        }
        LoginSuccess other = (LoginSuccess)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$uuid = this.getUuid();
        String other$uuid = other.getUuid();
        if (this$uuid == null ? other$uuid != null : !this$uuid.equals((Object)other$uuid)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null) {
            if (other$username == null) return true;
            return false;
        }
        if (this$username.equals((Object)other$username)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginSuccess;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $uuid = this.getUuid();
        result = result * 59 + ($uuid == null ? 43 : $uuid.hashCode());
        String $username = this.getUsername();
        return result * 59 + ($username == null ? 43 : $username.hashCode());
    }
}

