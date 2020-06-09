/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Chat
extends DefinedPacket {
    private String message;
    private byte position;

    public Chat(String message) {
        this((String)message, (byte)0);
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.message = Chat.readString((ByteBuf)buf);
        if (direction != ProtocolConstants.Direction.TO_CLIENT) return;
        this.position = buf.readByte();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        Chat.writeString((String)this.message, (ByteBuf)buf);
        if (direction != ProtocolConstants.Direction.TO_CLIENT) return;
        buf.writeByte((int)this.position);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((Chat)this);
    }

    public String getMessage() {
        return this.message;
    }

    public byte getPosition() {
        return this.position;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Chat(message=" + this.getMessage() + ", position=" + this.getPosition() + ")";
    }

    public Chat() {
    }

    public Chat(String message, byte position) {
        this.message = message;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Chat)) {
            return false;
        }
        Chat other = (Chat)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals((Object)other$message)) {
            return false;
        }
        if (this.getPosition() == other.getPosition()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Chat;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result * 59 + this.getPosition();
    }
}

