/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class TabCompleteRequest
extends DefinedPacket {
    private int transactionId;
    private String cursor;
    private boolean assumeCommand;
    private boolean hasPositon;
    private long position;

    public TabCompleteRequest(int transactionId, String cursor) {
        this.transactionId = transactionId;
        this.cursor = cursor;
    }

    public TabCompleteRequest(String cursor, boolean assumeCommand, boolean hasPosition, long position) {
        this.cursor = cursor;
        this.assumeCommand = assumeCommand;
        this.hasPositon = hasPosition;
        this.position = position;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= 393) {
            this.transactionId = TabCompleteRequest.readVarInt((ByteBuf)buf);
        }
        this.cursor = TabCompleteRequest.readString((ByteBuf)buf);
        if (protocolVersion >= 393) return;
        if (protocolVersion >= 107) {
            this.assumeCommand = buf.readBoolean();
        }
        if (!(this.hasPositon = buf.readBoolean())) return;
        this.position = buf.readLong();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= 393) {
            TabCompleteRequest.writeVarInt((int)this.transactionId, (ByteBuf)buf);
        }
        TabCompleteRequest.writeString((String)this.cursor, (ByteBuf)buf);
        if (protocolVersion >= 393) return;
        if (protocolVersion >= 107) {
            buf.writeBoolean((boolean)this.assumeCommand);
        }
        buf.writeBoolean((boolean)this.hasPositon);
        if (!this.hasPositon) return;
        buf.writeLong((long)this.position);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((TabCompleteRequest)this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public String getCursor() {
        return this.cursor;
    }

    public boolean isAssumeCommand() {
        return this.assumeCommand;
    }

    public boolean isHasPositon() {
        return this.hasPositon;
    }

    public long getPosition() {
        return this.position;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public void setAssumeCommand(boolean assumeCommand) {
        this.assumeCommand = assumeCommand;
    }

    public void setHasPositon(boolean hasPositon) {
        this.hasPositon = hasPositon;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "TabCompleteRequest(transactionId=" + this.getTransactionId() + ", cursor=" + this.getCursor() + ", assumeCommand=" + this.isAssumeCommand() + ", hasPositon=" + this.isHasPositon() + ", position=" + this.getPosition() + ")";
    }

    public TabCompleteRequest() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TabCompleteRequest)) {
            return false;
        }
        TabCompleteRequest other = (TabCompleteRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getTransactionId() != other.getTransactionId()) {
            return false;
        }
        String this$cursor = this.getCursor();
        String other$cursor = other.getCursor();
        if (this$cursor == null ? other$cursor != null : !this$cursor.equals((Object)other$cursor)) {
            return false;
        }
        if (this.isAssumeCommand() != other.isAssumeCommand()) {
            return false;
        }
        if (this.isHasPositon() != other.isHasPositon()) {
            return false;
        }
        if (this.getPosition() == other.getPosition()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof TabCompleteRequest;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTransactionId();
        String $cursor = this.getCursor();
        result = result * 59 + ($cursor == null ? 43 : $cursor.hashCode());
        result = result * 59 + (this.isAssumeCommand() ? 79 : 97);
        result = result * 59 + (this.isHasPositon() ? 79 : 97);
        long $position = this.getPosition();
        return result * 59 + (int)($position >>> 32 ^ $position);
    }
}

